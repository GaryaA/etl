package ru.cubesolutions.etl.clickhousepusher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * Created by Garya on 09.02.2018.
 */
public class ConsumerListener extends DefaultConsumer {

    private final static Logger log = Logger.getLogger(ConsumerListener.class);

    private final static Map<Long, Event> eventsWithDeliveryTags = new ConcurrentHashMap<>();

    private static int counter = 0;

    private ObjectMapper mapper = new ObjectMapper();
    private ClickhouseSupport clickhouseSupport;
    private Lock lock;
    private DestConfig appConfig;

    public ConsumerListener(Channel channel, Lock lock, DestConfig appConfig) {
        super(channel);
        this.lock = lock;
        this.appConfig = appConfig;
        this.clickhouseSupport = new ClickhouseSupport(appConfig);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        String json = new String(body, "UTF-8");
        Event event = new Event(mapper.<Map<String, String>>readValue(json, new TypeReference<Map<String, String>>() {
        }));
        eventsWithDeliveryTags.put(envelope.getDeliveryTag(), event);

        ++counter;
        if (counter % this.appConfig.getFlushCount() == 0) {
            Counter.INSTANCE.nullify();
            flush();
            Counter.INSTANCE.nullify();
        }
    }

    @Override
    public void handleConsumeOk(String consumerTag) {
        super.handleConsumeOk(consumerTag);
        log.info("Queue listening started, wait...");
    }

    @Override
    public void handleCancelOk(String consumerTag) {
        try {
            flush();
        } catch (IOException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
        log.info("Queue listening stopped");
    }

    private void acknowledge() {
        try {
            acknowledge(4, 0);
        } catch (IOException e) {
            log.error("Internal error, can't acknowledge input events", e);
            throw new RuntimeException(e);
        }
    }

    private void acknowledge(int attempts, int currentAttempt) throws IOException {
        try {
            this.getChannel().basicAck(maxTag(eventsWithDeliveryTags.keySet()), true);
            log.info("Acknowledged");
        } catch (IOException e) {
            ++currentAttempt;
            log.error("Can't acknowledge events, try " + currentAttempt, e);
            if (currentAttempt > attempts) {
                throw e;
            }
            acknowledge(attempts, currentAttempt);
        }
    }

    private void negateAcknowledge() {
        try {
            negateAcknowledge(4, 0);
        } catch (IOException e) {
            log.error("Internal error, can't restore input events", e);
            throw new RuntimeException(e);
        }
    }

    private void negateAcknowledge(int attempts, int currentAttempt) throws IOException {
        try {
            log.info("Restoration: " + eventsWithDeliveryTags.size() + " messages");
            this.getChannel().basicNack(maxTag(eventsWithDeliveryTags.keySet()), true, true);
            log.info("Restored " + eventsWithDeliveryTags.size());
        } catch (IOException e) {
            ++currentAttempt;
            log.error("Can't restore input events, try " + currentAttempt, e);
            if (currentAttempt > attempts) {
                throw e;
            }
            negateAcknowledge(attempts, currentAttempt);
        }
    }

    private void flush() throws IOException {
        lock.lock();
        try {
            log.info(counter + " messages are consumed");
            if (!eventsWithDeliveryTags.isEmpty()) {
                long start = System.currentTimeMillis();
                clickhouseSupport.insertEvents(new ArrayList<>(eventsWithDeliveryTags.values()));
                log.info(counter + " events are inserted, " + (System.currentTimeMillis() - start) + "ms");
                acknowledge();
            } else {
                log.info("0 messages");
            }
        } catch (Exception e) {
            log.error("", e);
            negateAcknowledge();
        } finally {
            lock.unlock();
        }
        eventsWithDeliveryTags.clear();
    }

    private long maxTag(Set<Long> tags) {
        if (tags == null || tags.isEmpty()) {
            return -1;
        }
        long max = -1;
        for (Long tag : tags) {
            if (tag > max) {
                max = tag;
            }
        }
        return max;
    }


}

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * Created by Garya on 09.02.2018.
 */
public class ConsumerListener extends DefaultConsumer {

    private final static Logger log = Logger.getLogger(ConsumerListener.class);

    private final static Map<Long, Event> eventsWithDeliveryTags = new ConcurrentHashMap<>();

    private static int counter = 0;

    private Lock lock;
    private ObjectMapper mapper = new ObjectMapper();
    private ClickhouseSupport clickhouseSupport = new ClickhouseSupport();

    public ConsumerListener(Channel channel, Lock lock) {
        super(channel);
        this.lock = lock;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        String json = new String(body, "UTF-8");
        Event event = new Event(mapper.<Map<String, String>>readValue(json, new TypeReference<Map<String, String>>() {
        }));
        eventsWithDeliveryTags.put(envelope.getDeliveryTag(), event);

        ++counter;
        if (counter % AppConfig.getInstance().getFlushCount() == 0) {
            flush();
        }
    }

    @Override
    public void handleConsumeOk(String consumerTag) {
        super.handleConsumeOk(consumerTag);
        log.info("Queue listening started, wait...");
    }

    @Override
    public void handleCancelOk(String consumerTag) {
        super.handleCancelOk(consumerTag);
    }

    private synchronized void acknowledge() {
        try {
            acknowledge(4, 0);
        } catch (IOException e) {
            log.error("Internal error, can't acknowledge input events", e);
            throw new RuntimeException(e);
        }
    }

    private synchronized void acknowledge(int attempts, int currentAttempt) throws IOException {
        try {
            log.info("Acknowledgement: " + eventsWithDeliveryTags.size() + " messages");
            for (Long tag : eventsWithDeliveryTags.keySet()) {
                log.debug("tag to acknowledge: " + tag);
                this.getChannel().basicAck(tag, true);
                log.debug("success");
            }
            log.info("Acknowledged " + eventsWithDeliveryTags.size() + " messages");
            eventsWithDeliveryTags.clear();
        } catch (IOException e) {
            ++currentAttempt;
            log.error("Can't acknowledge input events, try " + currentAttempt, e);
            if (currentAttempt > attempts) {
                throw e;
            }
            acknowledge(attempts, currentAttempt);
        }
    }

    private synchronized void negateAcknowledge() {
        try {
            negateAcknowledge(4, 0);
        } catch (IOException e) {
            log.error("Internal error, can't restore input events", e);
            throw new RuntimeException(e);
        }
    }

    private synchronized void negateAcknowledge(int attempts, int currentAttempt) throws IOException {
        try {
            log.info("Restoration: " + eventsWithDeliveryTags.size() + " messages");
            for (Long tag : eventsWithDeliveryTags.keySet()) {
                this.getChannel().basicNack(tag, true, true);
            }
            log.info("Restored " + eventsWithDeliveryTags.size() + " messages");
            eventsWithDeliveryTags.clear();
        } catch (IOException e) {
            ++currentAttempt;
            log.error("Can't restore input events, try " + currentAttempt, e);
            if (currentAttempt > attempts) {
                throw e;
            }
            negateAcknowledge(attempts, currentAttempt);
        }
    }

    private synchronized void flush() {
        lock.lock();
        try {
            try {
                log.info(eventsWithDeliveryTags.size() + " messages are consumed");
                if (!eventsWithDeliveryTags.isEmpty()) {
                    clickhouseSupport.insertEvents(new ArrayList<>(eventsWithDeliveryTags.values()));
                    log.info(eventsWithDeliveryTags.size() + " events is inserted");
                    acknowledge();
                } else {
                    log.info("0 messages");
                }
            } catch (Exception e) {
                log.error("Can't write to clickhouse", e);
                negateAcknowledge();
            }
            log.info("Queue listening stopped");
        } finally {
            lock.unlock();
        }
    }

}

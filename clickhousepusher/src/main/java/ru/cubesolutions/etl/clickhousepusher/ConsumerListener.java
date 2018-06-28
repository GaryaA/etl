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

/**
 * Created by Garya on 09.02.2018.
 */
public class ConsumerListener extends DefaultConsumer {

    private final static Logger log = Logger.getLogger(ConsumerListener.class);

    private final static Map<Long, Event> eventsWithDeliveryTags = new ConcurrentHashMap<>();

    private static int counter = 0;

    private ObjectMapper mapper = new ObjectMapper();
    private ClickhouseSupport clickhouseSupport = new ClickhouseSupport();

    public ConsumerListener(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        super.handleDelivery(consumerTag, envelope, properties, body);
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
        log.info("Queue listening stopped");
    }

    private synchronized void flush() throws IOException {
        try {
            log.info(counter + " messages are consumed");
            if (!eventsWithDeliveryTags.isEmpty()) {
                long start = System.currentTimeMillis();
                clickhouseSupport.insertEvents(new ArrayList<>(eventsWithDeliveryTags.values()));
                log.info(counter + " events are inserted, " + (System.currentTimeMillis() - start) + "ms");
                eventsWithDeliveryTags.clear();
            } else {
                log.info("0 messages");
            }
        } catch (Exception e) {
            log.error("Can't write to clickhouse", e);
        }
    }

}

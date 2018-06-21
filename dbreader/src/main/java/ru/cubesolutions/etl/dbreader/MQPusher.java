package ru.cubesolutions.etl.dbreader;

import org.apache.log4j.Logger;
import ru.cubesolutions.rabbitmq.Producer;
import ru.cubesolutions.rabbitmq.RabbitConfig;

import java.io.IOException;

/**
 * Created by Garya on 21.06.2018.
 */
public class MQPusher {

    private final static Logger log = Logger.getLogger(MQPusher.class);

    private Producer producer;

    public MQPusher() {
    }

    public void initMqProducer() {
        RabbitConfig rabbitConfig = new RabbitConfig(Config.MQ_HOST, Config.MQ_PORT, Config.MQ_V_HOST, Config.MQ_USER, Config.MQ_PASSWORD);
        Producer producer;
        try {
            producer = new Producer(rabbitConfig);
            producer.queueDeclare(Config.QUEUE);
        } catch (IOException e) {
            log.error("Can't connect to rabbitmq", e);
            throw new RuntimeException("Can't connect to rabbitmq", e);
        }
        this.producer = producer;
    }

    public void closeMqProducer() {
        try {
            this.producer.close();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void push(byte[] bytes) {
        try {
            this.producer.sendMessage(bytes, "", Config.QUEUE);
        } catch (IOException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }
}

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
    private SrcConfig appConfig;

    public MQPusher(SrcConfig appConfig) {
        this.appConfig = appConfig;
    }

    public void initMqProducer() {
        RabbitConfig rabbitConfig = new RabbitConfig(this.appConfig.getMqHost(), this.appConfig.getMqPort(), this.appConfig.getMqVHost(), this.appConfig.getMqUser(), this.appConfig.getMqPassword());
        Producer producer;
        try {
            producer = new Producer(rabbitConfig);
            producer.queueDeclare(this.appConfig.getQueue());
            producer.queuePurge(this.appConfig.getQueue());
            log.info("queue " + this.appConfig.getQueue() + " is purged");
        } catch (IOException e) {
            log.error("Can't connect to rabbitmq", e);
            throw new RuntimeException("Can't connect to rabbitmq", e);
        }
        this.producer = producer;
        log.debug("mq producer is inited");
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
            this.producer.sendMessage(bytes, "", this.appConfig.getQueue());
        } catch (IOException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }
}

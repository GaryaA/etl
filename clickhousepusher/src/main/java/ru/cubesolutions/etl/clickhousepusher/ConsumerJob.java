package ru.cubesolutions.etl.clickhousepusher;

import org.apache.log4j.Logger;
import ru.cubesolutions.rabbitmq.RabbitConfig;

import java.io.IOException;

/**
 * Created by Garya on 10.02.2018.
 */
public class ConsumerJob implements Runnable {

    private final static Logger log = Logger.getLogger(ConsumerJob.class);

    public static ConsumerJob INSTANCE;

    static {
        init();
    }

    private synchronized static void init() {
        init(3, 0);
    }

    private synchronized static void init(int attempts, int currentAttempt) {
        try {
            INSTANCE = new ConsumerJob(new EndpointWrapper(new RabbitConfig(
                    AppConfig.getInstance().getMqHost(),
                    AppConfig.getInstance().getMqPort(),
                    AppConfig.getInstance().getMqVHost(),
                    AppConfig.getInstance().getMqUser(),
                    AppConfig.getInstance().getMqPassword()
            )));
        } catch (IOException e) {
            ++currentAttempt;
            log.error("Can't init rabbitmq endpoint by properties, try " + currentAttempt, e);
            if (currentAttempt > attempts) {
                throw new RuntimeException("Can't init rabbitmq endpoint by properties, try " + currentAttempt, e);
            }
            Utils.sleepInSeconds(20);
            init(attempts, currentAttempt);
        }
    }

    private EndpointWrapper endpoint;
    private ConsumerListener consumerListener;

    private ConsumerJob(EndpointWrapper endpoint) {
        this.endpoint = endpoint;
        this.consumerListener = new ConsumerListener(endpoint.getChannel());
    }

    public synchronized void start() {
        try {
            INSTANCE.getEndpoint().getChannel().basicConsume(AppConfig.getInstance().getQueue(), true, INSTANCE.getConsumerListener());
        } catch (Exception e) {
            try {
                init();
                INSTANCE.getEndpoint().getChannel().basicConsume(AppConfig.getInstance().getQueue(), true, INSTANCE.getConsumerListener());
            } catch (IOException ex) {
                log.error("Can't start consuming", ex);
            }
        }
    }

    public EndpointWrapper getEndpoint() {
        return endpoint;
    }

    public ConsumerListener getConsumerListener() {
        return consumerListener;
    }

    @Override
    public void run() {
        start();
    }
}

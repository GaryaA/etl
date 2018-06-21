package ru.cubesolutions.etl.clickhousepusher;

import org.apache.log4j.Logger;
import ru.cubesolutions.rabbitmq.RabbitConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Garya on 10.02.2018.
 */
public class ConsumerJob implements Runnable {

    private final static Logger log = Logger.getLogger(ConsumerJob.class);

    public static ConsumerJob INSTANCE;

    private Lock lock;

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
            )), new ReentrantLock());
            INSTANCE.getEndpoint().getChannel().basicQos(AppConfig.getInstance().getFlushCount());
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

    private ConsumerJob(EndpointWrapper endpoint, Lock lock) {
        this.endpoint = endpoint;
        this.lock = lock;
        this.consumerListener = new ConsumerListener(endpoint.getChannel(), lock);
    }

    public synchronized void start() {
        try {
            INSTANCE.getEndpoint().getChannel().basicConsume(AppConfig.getInstance().getQueue(), false, INSTANCE.getConsumerListener());
        } catch (Exception e) {
            try {
                init();
                INSTANCE.getEndpoint().getChannel().basicConsume(AppConfig.getInstance().getQueue(), false, INSTANCE.getConsumerListener());
            } catch (IOException ex) {
                log.error("Can't start consuming", ex);
            }
        }
    }

    public synchronized void stop() {
        try {
            INSTANCE.getEndpoint().getChannel().basicCancel(INSTANCE.getConsumerListener().getConsumerTag());
            TimeUnit.MILLISECONDS.sleep(200);
            lock.lock();
            try {
                close(INSTANCE.getEndpoint());
            } finally {
                lock.unlock();
            }
        } catch (Exception e) {
            log.error("Can't stop consuming", e);
        }
    }

    private synchronized static void close(EndpointWrapper endpoint) {
        try {
            close(endpoint, 3, 0);
        } catch (Exception e) {
            log.error("Can't close connection to rabbitmq", e);
        }
    }

    private synchronized static void close(EndpointWrapper endpoint, int attempts, int currentAttempt) throws Exception {
        try {
            if (endpoint.getChannel().isOpen()) {
                endpoint.getChannel().close();
            }
            if (endpoint.getConnection().isOpen()) {
                endpoint.getConnection().close();
            }
        } catch (Exception e) {
            ++currentAttempt;
            if (currentAttempt > attempts) {
                throw e;
            }
            log.warn("Can't close connection to rabbitmq, try " + currentAttempt + "...", e);
            Utils.sleepInSeconds(20);
            close(endpoint, attempts, currentAttempt);
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

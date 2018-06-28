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

    public final static ConsumerJob INSTANCE;

    static {
        try {
            INSTANCE = new ConsumerJob(new EndpointWrapper(new RabbitConfig(
                    AppConfig.getInstance().getMqHost(),
                    AppConfig.getInstance().getMqPort(),
                    AppConfig.getInstance().getMqVHost(),
                    AppConfig.getInstance().getMqUser(),
                    AppConfig.getInstance().getMqPassword()
            )));
            INSTANCE.getEndpoint().getChannel().basicQos(AppConfig.getInstance().getFlushCount() * 2, false);
        } catch (IOException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    private EndpointWrapper endpoint;
    private ConsumerListener consumerListener;
    private Lock lock;

    private ConsumerJob(EndpointWrapper endpoint) {
        this.endpoint = endpoint;
        this.lock = new ReentrantLock();
        this.consumerListener = new ConsumerListener(endpoint.getChannel(), lock);
    }

    public synchronized void start() {
        try {
            INSTANCE.getEndpoint().getChannel().basicConsume(AppConfig.getInstance().getQueue(), false, INSTANCE.getConsumerListener());
        } catch (Exception e) {
            try {
                INSTANCE.getEndpoint().getChannel().basicConsume(AppConfig.getInstance().getQueue(), false, INSTANCE.getConsumerListener());
            } catch (IOException ex) {
                log.error("Can't start consuming", ex);
            }
        }
    }

    public synchronized void stop() {
        try {
            INSTANCE.getEndpoint().getChannel().basicCancel(INSTANCE.getConsumerListener().getConsumerTag());
            TimeUnit.MILLISECONDS.sleep(300);
            close(INSTANCE.getEndpoint());
        } catch (Exception e) {
            log.error("Can't stop consuming", e);
        }
    }

    private synchronized void close(EndpointWrapper endpoint) {
        lock.lock();
        try {
            close(endpoint, 3, 0);
        } catch (Exception e) {
            log.error("Can't close connection to rabbitmq", e);
        } finally {
            lock.unlock();
        }
    }

    private synchronized void close(EndpointWrapper endpoint, int attempts, int currentAttempt) throws Exception {
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

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
public class ConsumerJob {

    private final static Logger log = Logger.getLogger(ConsumerJob.class);

    private DestConfig appConfig;
    private EndpointWrapper endpoint;
    private ConsumerListener consumerListener;
    private Lock lock;

    public ConsumerJob(DestConfig appConfig) {
        this.appConfig = appConfig;
        try {
            this.endpoint = new EndpointWrapper(new RabbitConfig(
                    appConfig.getMqHost(),
                    appConfig.getMqPort(),
                    appConfig.getMqVHost(),
                    appConfig.getMqUser(),
                    appConfig.getMqPassword()
            ));
            this.endpoint.getChannel().basicQos(appConfig.getFetchSize() * 2, false);
        } catch (IOException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
        this.lock = new ReentrantLock();
        this.consumerListener = new ConsumerListener(endpoint.getChannel(), lock, appConfig);
    }

    public synchronized void start() {
        try {
            endpoint.getChannel().basicConsume(this.appConfig.getQueue(), false, consumerListener);
        } catch (Exception e) {
            log.error("Can't start consuming", e);
        }
    }

    public synchronized void stop() {
        try {
            endpoint.getChannel().basicCancel(consumerListener.getConsumerTag());
            TimeUnit.SECONDS.sleep(3);
            close(endpoint);
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

}

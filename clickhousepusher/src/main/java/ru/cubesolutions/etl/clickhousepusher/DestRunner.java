package ru.cubesolutions.etl.clickhousepusher;

import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Garya on 09.04.2018.
 */
public class DestRunner {

    private final static Logger log = Logger.getLogger(DestRunner.class);
    private DestConfig appConfig;

    public DestRunner(DestConfig appConfig) {
        this.appConfig = appConfig;
    }

    public static void main(String[] args) {
        DestConfig appConfig = new DestConfig("clickhousepusher.properties");
        DestRunner runner = new DestRunner(appConfig);
        runner.run();
    }

    public void run() {
        ConsumerJob consumerJob = new ConsumerJob(appConfig);
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleWithFixedDelay(Counter.INSTANCE, 0, 1, TimeUnit.SECONDS);

        consumerJob.start();
        while (true) {
            if (Counter.INSTANCE.getC() >= appConfig.getTimeToStopInSeconds()) {
                consumerJob.stop();
                exec.shutdownNow();
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }
    }

}

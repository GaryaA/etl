package ru.cubesolutions.etl.clickhousepusher;

import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Garya on 09.04.2018.
 */
public class Run {

    private final static Logger log = Logger.getLogger(Run.class);

    public static void main(String[] args) {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleWithFixedDelay(Counter.INSTANCE, 0, 1, TimeUnit.SECONDS);
        ConsumerJob.INSTANCE.start();
        log.info("test");
        while (true) {
            if (Counter.INSTANCE.getC() >= AppConfig.getInstance().getTimeToStopInSeconds()) {
                ConsumerJob.INSTANCE.stop();
                break;
            }
        }
    }

}

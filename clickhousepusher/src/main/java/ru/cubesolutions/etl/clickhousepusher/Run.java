package ru.cubesolutions.etl.clickhousepusher;

import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ru.cubesolutions.etl.clickhousepusher.Utils.sleepInMilliseconds;

/**
 * Created by Garya on 09.04.2018.
 */
public class Run implements Runnable {

    private final static Logger log = Logger.getLogger(AppConfig.class);

    public static void main(String[] args) {
        Run run = new Run();
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(run, 0, AppConfig.getInstance().getTimeBetweenBatchesInMilliseconds(), TimeUnit.MILLISECONDS);
    }

    private void taskPushOneBatch() {
        ConsumerJob.INSTANCE.start();
        sleepInMilliseconds(AppConfig.getInstance().getTimeForOneBatchInMilliseconds());
        ConsumerJob.INSTANCE.stop();
    }

    @Override
    public void run() {
        taskPushOneBatch();
    }
}

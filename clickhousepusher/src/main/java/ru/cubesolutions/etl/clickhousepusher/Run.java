package ru.cubesolutions.etl.clickhousepusher;

import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ru.cubesolutions.etl.clickhousepusher.Utils.sleepInMilliseconds;
import static ru.cubesolutions.etl.clickhousepusher.Utils.stop;

/**
 * Created by Garya on 09.04.2018.
 */
public class Run {

    private final static Logger log = Logger.getLogger(AppConfig.class);

    public static void main(String[] args) {
        Run run = new Run();
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(run::taskPushOneBatch, 0, AppConfig.getInstance().getTimeBetweenBatchesInMilliseconds(), TimeUnit.MILLISECONDS);
    }

    private void taskPushOneBatch() {
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(ConsumerJob.INSTANCE::start);
        sleepInMilliseconds(AppConfig.getInstance().getTimeForOneBatchInMilliseconds());
        ConsumerJob.INSTANCE.stop();
        stop(es, 60);
    }
}

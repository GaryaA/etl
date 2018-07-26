package ru.cubesolutions.etl.controller;

import org.apache.log4j.Logger;
import org.quartz.*;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ru.cubesolutions.evam.utils.CommonUtils.getProps;

/**
 * Created by Garya on 26.07.2018.
 */
public class Rescheduler implements Runnable {

    private final static Logger log = Logger.getLogger(Rescheduler.class);

    private Scheduler scheduler;
    private TriggerKey triggerKey;

    public Rescheduler(Scheduler scheduler, TriggerKey currentKey) {
        this.scheduler = scheduler;
        this.triggerKey = currentKey;
    }

    public void start() {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleWithFixedDelay(this, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        Properties props = getProps("etl-controller.properties");
        try {
            String oldTrigger = this.triggerKey.getName();
            Trigger newTrigger = getCronTrigger(props);
            scheduler.rescheduleJob(TriggerKey.triggerKey(oldTrigger), newTrigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private CronTrigger getCronTrigger(Properties props) {
        this.triggerKey = TriggerKey.triggerKey(UUID.randomUUID().toString());
        return TriggerBuilder.newTrigger()
                .withIdentity(this.triggerKey)
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(props.getProperty("cron")))
                .build();
    }
}

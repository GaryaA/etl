package ru.cubesolutions.etl.controller;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.cubesolutions.etl.clickhousepusher.DestConfig;
import ru.cubesolutions.etl.clickhousepusher.DestRunner;
import ru.cubesolutions.etl.dbreader.SrcConfig;
import ru.cubesolutions.etl.dbreader.SrcRunner;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static ru.cubesolutions.etl.clickhousepusher.Utils.isNotNull;
import static ru.cubesolutions.etl.clickhousepusher.Utils.isNull;
import static ru.cubesolutions.evam.utils.CommonUtils.getProps;

/**
 * Created by Garya on 05.07.2018.
 */
public class Runner implements Job {

    private final static Logger log = Logger.getLogger(Runner.class);

    public static void main(String[] args) {
        Properties props = getProps("etl-controller.properties");
        if (args == null || args.length == 1) {
            log.info("type:");
            log.info("-src <file properties name of source> (optional)");
            log.info("-dest <file properties name of destination> (optional)");
            return;
        }
        String srcProps = null;
        String destProps = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-src")) {
                srcProps = args[i + 1];
            }
            if (args[i].equals("-dest")) {
                destProps = args[i + 1];
            }
        }

        if (Boolean.parseBoolean(props.getProperty("run-one-time"))) {
            Runner runner = new Runner();
            runner.run(srcProps, destProps);
        } else {
            try {
                JobDetail job = JobBuilder.newJob(Runner.class)
                        .withIdentity("Runner")
                        .usingJobData("srcProps", srcProps)
                        .usingJobData("destProps", destProps)
                        .build();

                CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                        .withIdentity("crontrigger")
                        .withSchedule(CronScheduleBuilder.cronSchedule(props.getProperty("cron")))
                        .build();

                SchedulerFactory schFactory = new StdSchedulerFactory();
                Scheduler sch = schFactory.getScheduler();
                sch.start();
                sch.scheduleJob(job, cronTrigger);

                Rescheduler rescheduler = new Rescheduler(sch, TriggerKey.triggerKey("crontrigger"));
                rescheduler.start();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }

    private void run(String srcProps, String destProps) {
        if (isNotNull(srcProps)) {
            SrcRunner srcRunner = new SrcRunner(new SrcConfig(srcProps));
            if (isNull(destProps)) {
                srcRunner.run();
            } else {
                Executor exec = Executors.newSingleThreadExecutor();
                exec.execute(srcRunner::run);
            }
        }
        if (isNotNull(destProps)) {
            DestRunner destRunner = new DestRunner(new DestConfig(destProps));
            destRunner.run();
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        run(
                (String) jobExecutionContext.getJobDetail().getJobDataMap().get("srcProps"),
                (String) jobExecutionContext.getJobDetail().getJobDataMap().get("destProps")
        );
    }
}

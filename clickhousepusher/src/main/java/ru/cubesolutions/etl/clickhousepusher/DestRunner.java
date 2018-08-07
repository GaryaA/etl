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
        ClickhouseSupport clickhouseSupport = new ClickhouseSupport(appConfig);

        String originalTableName = null;
        String tableForLoading = null;
        boolean pushToClickhouse = appConfig.getJdbcDriver().contains("clickhouse");
        if (pushToClickhouse) {
            originalTableName = appConfig.getTableMapHolder().getDbName() + "." + appConfig.getTableMapHolder().getTable().getTableName();
            tableForLoading = clickhouseSupport.createNewTable(appConfig.getTableMapHolder().getDbName(), appConfig.getTableMapHolder().getTable().getTableName());
            appConfig.getTableMapHolder().getTable().setTableName(tableForLoading.substring(tableForLoading.indexOf(".") + 1));
        }

        ConsumerJob consumerJob = new ConsumerJob(appConfig, clickhouseSupport);
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleWithFixedDelay(Counter.INSTANCE, 0, 1, TimeUnit.SECONDS);

        consumerJob.start();
        while (true) {
            if (Counter.INSTANCE.getC() >= appConfig.getTimeToStopInSeconds()) {
                consumerJob.stop();
                exec.shutdownNow();
                if (pushToClickhouse) {
                    clickhouseSupport.replaceTable(originalTableName, tableForLoading);
                }
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

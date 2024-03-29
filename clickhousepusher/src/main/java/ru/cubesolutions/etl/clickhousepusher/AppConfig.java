package ru.cubesolutions.etl.clickhousepusher;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Garya on 12.11.2017.
 */
public class AppConfig {

    private final static Logger log = Logger.getLogger(AppConfig.class);

    private String mqHost;
    private int mqPort;
    private String mqVHost;
    private String mqUser;
    private String mqPassword;
    private String queue;
    private int flushCount;
    private int timeForOneBatchInMilliseconds;
    private int timeBetweenBatchesInMilliseconds;

    private String jdbcDriver;
    private String jdbcUrl;
    private String jdbcUser;
    private String jdbcPassword;
    private TableMapHolder tableMapHolder;

    private final static AppConfig INSTANCE;

    private AppConfig(String mqHost, int mqPort, String mqVHost, String mqUser, String mqPassword, String queue, String jdbcDriver, String jdbcUrl, String jdbcUser, String jdbcPassword, int flushCount, int timeForOneBatchInMilliseconds, int timeBetweenBatchesInMilliseconds) {
        this.mqHost = mqHost;
        this.mqPort = mqPort;
        this.mqVHost = mqVHost;
        this.mqUser = mqUser;
        this.mqPassword = mqPassword;
        this.queue = queue;
        this.jdbcDriver = jdbcDriver;
        this.jdbcUrl = jdbcUrl;
        this.jdbcUser = jdbcUser;
        this.jdbcPassword = jdbcPassword;
        this.tableMapHolder = TableMapHolder.getInstance();
        this.flushCount = flushCount;
        this.timeForOneBatchInMilliseconds = timeForOneBatchInMilliseconds;
        this.timeBetweenBatchesInMilliseconds = timeBetweenBatchesInMilliseconds;
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    static {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream("clickhousepusher.properties")) {
            props.load(is);
            PropertyConfigurator.configure("log4j.properties");
            log.debug("clickhousepusher.properties is found");
        } catch (FileNotFoundException e) {
            try (InputStream input = AppConfig.class.getResourceAsStream("/clickhousepusher.properties")) {
                props.load(input);
            } catch (Throwable t) {
                log.error("File config clickhousepusher.properties not found", e);
                System.out.println("File config clickhousepusher.properties not found");
                throw new RuntimeException(t);
            }
        } catch (Throwable t) {
            log.error("File config clickhousepusher.properties not found", t);
            System.out.println("File config clickhousepusher.properties not found");
            throw new RuntimeException(t);
        }
        String jdbcDriver = props.getProperty("jdbc-driver");
        String jdbcUrl = props.getProperty("jdbc-url");
        String jdbcUser = props.getProperty("jdbc-user");
        String jdbcPassword = props.getProperty("jdbc-password");

        String mqHost = props.getProperty("mq-host");
        int mqPort = Integer.parseInt(props.getProperty("mq-port"));
        String mqVHost = props.getProperty("mq-v-host");
        String mqUser = props.getProperty("mq-user");
        String mqPassword = props.getProperty("mq-password");
        String queue = props.getProperty("queue");

        int flushCount = Integer.parseInt(props.getProperty("flush-count"));
        int timeForOneBatchInMilliseconds = Integer.parseInt(props.getProperty("time-for-one-batch-in-milliseconds"));
        int timeBetweenBatchesInMilliseconds = Integer.parseInt(props.getProperty("time-between-batches-in-milliseconds"));
        INSTANCE = new AppConfig(mqHost, mqPort, mqVHost, mqUser, mqPassword, queue, jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword, flushCount, timeForOneBatchInMilliseconds, timeBetweenBatchesInMilliseconds);
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public TableMapHolder getTableMapHolder() {
        return tableMapHolder;
    }

    public String getMqHost() {
        return mqHost;
    }

    public int getMqPort() {
        return mqPort;
    }

    public String getMqVHost() {
        return mqVHost;
    }

    public String getMqUser() {
        return mqUser;
    }

    public String getMqPassword() {
        return mqPassword;
    }

    public String getQueue() {
        return queue;
    }

    public int getFlushCount() {
        return flushCount;
    }

    public int getTimeForOneBatchInMilliseconds() {
        return timeForOneBatchInMilliseconds;
    }

    public int getTimeBetweenBatchesInMilliseconds() {
        return timeBetweenBatchesInMilliseconds;
    }
}

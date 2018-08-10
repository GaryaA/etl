package ru.cubesolutions.etl.dbreader;

import org.apache.log4j.Logger;

import java.util.Properties;

import static ru.cubesolutions.evam.utils.CommonUtils.getProps;
import static ru.cubesolutions.evam.utils.CommonUtils.isNullOrEmpty;

/**
 * Created by Garya on 12.11.2017.
 */
public class SrcConfig {

    private final static Logger log = Logger.getLogger(SrcConfig.class);

    private String jdbcDriver;
    private String jdbcUrl;
    private String jdbcUser;
    private String jdbcPassword;

    private int fetchSize;
    private String sql;
    private boolean readRowsWhichMoreThanFixIdOnly;
    private String initialSql;
    private Long idToStop;
    private String fieldIdName;
    private int delayBetweenTasksInSeconds;

    private String mqHost;
    private int mqPort;
    private String mqVHost;
    private String mqUser;
    private String mqPassword;
    private String queue;

    public SrcConfig(String fileNameProperties) {
        Properties props = getProps(fileNameProperties);
        jdbcDriver = props.getProperty("jdbc-driver");
        jdbcUrl = props.getProperty("jdbc-url");
        jdbcUser = props.getProperty("jdbc-user");
        jdbcPassword = props.getProperty("jdbc-password");

        fetchSize = Integer.parseInt(props.getProperty("fetch-size"));
        sql = props.getProperty("sql");
        readRowsWhichMoreThanFixIdOnly = Boolean.parseBoolean(props.getProperty("read-rows-which-more-than-fix-id-only"));
        initialSql = props.getProperty("initial-sql");
        idToStop = isNullOrEmpty(props.getProperty("id-to-stop")) ? null : Long.parseLong(props.getProperty("id-to-stop"));
        fieldIdName = props.getProperty("field-id-name");
        delayBetweenTasksInSeconds = Integer.parseInt(props.getProperty("delay-between-tasks-in-seconds"));

        mqHost = props.getProperty("mq-host");
        mqPort = Integer.parseInt(props.getProperty("mq-port"));
        mqVHost = props.getProperty("mq-v-host");
        mqUser = props.getProperty("mq-user");
        mqPassword = props.getProperty("mq-password");
        queue = props.getProperty("queue");
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

    public int getFetchSize() {
        return fetchSize;
    }

    public String getSql() {
        return sql;
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

    public boolean isReadRowsWhichMoreThanFixIdOnly() {
        return readRowsWhichMoreThanFixIdOnly;
    }

    public String getInitialSql() {
        return initialSql;
    }

    public Long getIdToStop() {
        return idToStop;
    }

    public String getFieldIdName() {
        return fieldIdName;
    }

    public int getDelayBetweenTasksInSeconds() {
        return delayBetweenTasksInSeconds;
    }
}

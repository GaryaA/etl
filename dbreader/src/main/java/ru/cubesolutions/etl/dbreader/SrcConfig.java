package ru.cubesolutions.etl.dbreader;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import static ru.cubesolutions.evam.utils.CommonUtils.getProps;

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
}

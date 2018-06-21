package ru.cubesolutions.etl.dbreader;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Garya on 12.11.2017.
 */
public class Config {

    private final static Logger log = Logger.getLogger(Config.class);

    public static String JDBC_DRIVER;
    public static String JDBC_URL;
    public static String JDBC_USER;
    public static String JDBC_PASSWORD;

    public static int STEP_VALUE;
    public static int TIME_BETWEEN_STEPS_IN_MILLISECONDS;
    public static String FIELD_ID_NAME;
    public static String INITIAL_ID_SQL;
    public static String SQL;

    public static String MQ_HOST;
    public static int MQ_PORT;
    public static String MQ_V_HOST;
    public static String MQ_USER;
    public static String MQ_PASSWORD;
    public static String QUEUE;

    public static Boolean LAST_ID_IN_MEMORY;


    static {
        init();
    }

    private static void init() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("dbreader.properties")) {
            props.load(input);
            PropertyConfigurator.configure("log4j.properties");
        } catch (FileNotFoundException e) {
            try (InputStream input = Config.class.getResourceAsStream("/dbreader.properties")) {
                props.load(input);
            } catch (Throwable t) {
                log.error("File config dbreader.properties not found", e);
                System.out.println("File config dbreader.properties not found");
                throw new RuntimeException(t);
            }
        } catch (Throwable t) {
            log.error("File config dbreader.properties not found", t);
            System.out.println("File config dbreader.properties not found");
            throw new RuntimeException(t);
        }
        JDBC_DRIVER = props.getProperty("jdbc-driver");
        JDBC_URL = props.getProperty("jdbc-url");
        JDBC_USER = props.getProperty("jdbc-user");
        JDBC_PASSWORD = props.getProperty("jdbc-password");

        STEP_VALUE = Integer.parseInt(props.getProperty("step-value"));
        TIME_BETWEEN_STEPS_IN_MILLISECONDS = Integer.parseInt(props.getProperty("time-between-steps-in-milliseconds"));
        FIELD_ID_NAME = props.getProperty("field-id-name");
        INITIAL_ID_SQL = props.getProperty("initial-id-sql");
        SQL = props.getProperty("sql");

        MQ_HOST = props.getProperty("mq-host");
        MQ_PORT = Integer.parseInt(props.getProperty("mq-port"));
        MQ_V_HOST = props.getProperty("mq-v-host");
        MQ_USER = props.getProperty("mq-user");
        MQ_PASSWORD = props.getProperty("mq-password");
        QUEUE = props.getProperty("queue");

        LAST_ID_IN_MEMORY = Boolean.parseBoolean(props.getProperty("last-id-in-memory"));
    }

}

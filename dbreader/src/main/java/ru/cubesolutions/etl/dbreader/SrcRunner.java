package ru.cubesolutions.etl.dbreader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Garya on 08.04.2018.
 */
public class SrcRunner {

    private final static Logger log = Logger.getLogger(SrcRunner.class);
    private SrcConfig appConfig;
    private DataSource dataSource;
    private MQPusher mqPusher;

    public SrcRunner(SrcConfig appConfig) {
        this.appConfig = appConfig;
        this.dataSource = new DataSource(appConfig);
        this.mqPusher = new MQPusher(this.appConfig);
    }

    public static void main(String[] args) throws IOException {
        SrcConfig appConfig = new SrcConfig("dbreader.properties");
        SrcRunner runner = new SrcRunner(appConfig);
        runner.run();
    }

    public void run() {
        if (appConfig.isReadRowsWhichMoreThanFixIdOnly()) {
            runWithLastIdMode();
        } else {
            runWithoutLastIdMode();
        }
    }

    private void runWithoutLastIdMode() {
        mqPusher.initMqProducer();
        readAndPush(appConfig.getSql());
        mqPusher.closeMqProducer();
    }

    private void runWithLastIdMode() {
        mqPusher.initMqProducer();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleWithFixedDelay(() -> {
            try {
                Long lastId = getLastId();
                String sql = String.format(appConfig.getSql(),
                        lastId,
                        appConfig.getIdToStop() == null ? Long.MAX_VALUE : appConfig.getIdToStop()
                );

                lastId = readAndPush(sql);
                if (lastId < 0) {
                    return;
                }
                log.info("last-id is " + lastId);
                try {
                    Files.write(Paths.get("last-id"), ("" + lastId).getBytes(Charset.forName("UTF-8")), StandardOpenOption.CREATE);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error(e);
                    throw new RuntimeException("Can't write last id", e);
                }

            } catch (Exception e) {
                log.error("", e);
                throw new RuntimeException(e);
            }
        }, 0, appConfig.getDelayBetweenTasksInSeconds(), TimeUnit.SECONDS);
//        mqPusher.closeMqProducer();
    }

    private Long readAndPush(String sql) {
        Long lastId = -1L;
        try {
            try (Connection connection = this.dataSource.getConnection()) {
                log.info("sql: " + sql);
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setFetchSize(this.appConfig.getFetchSize());

                ResultSet rs = ps.executeQuery();

                int mark = 0;
                ObjectMapper mapper = new ObjectMapper();
                while (rs.next()) {
                    ++mark;
                    Map<String, String> params = new HashMap<>();
                    ResultSetMetaData metaData = rs.getMetaData();
                    for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object obj = rs.getObject(columnName);
                        params.put(metaData.getColumnLabel(i), "" + obj);
                        if (columnName.equalsIgnoreCase(appConfig.getFieldIdName())) {
                            if (obj instanceof Number) {
                                lastId = ((Number) obj).longValue();
                            } else {
                                try {
                                    lastId = Long.parseLong(obj.toString());
                                } catch (NumberFormatException e) {
                                    log.error("Field id has no number format", e);
                                }
                            }

                        }
                    }
                    byte[] eventBytes = mapper.writer().writeValueAsBytes(params);
                    mqPusher.push(eventBytes);
                    if (mark % this.appConfig.getFetchSize() == 0) {
                        log.info(mark + " records are pushed to queue");
                    }
                }
                if (mark == 0) {
                    log.info("Can't find any records");
                } else {
                    log.info(mark + " records are pushed to queue");
                }
            }
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
        return lastId;
    }

    private Long getInitialId(String initialIdSql) {
        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(initialIdSql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                log.error("Can't find any records");
                throw new RuntimeException("Can't find any records");
            }
        } catch (SQLException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    private Long getLastId() {
        Long lastId;
        try {
            lastId = Long.parseLong(new String(Files.readAllBytes(Paths.get("last-id"))));
        } catch (NoSuchFileException e) {
            lastId = this.getInitialId(appConfig.getInitialSql());
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        log.info("Last id is " + lastId);
        return lastId;
    }

}

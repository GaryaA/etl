package ru.cubesolutions.etl.dbreader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

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
        mqPusher.initMqProducer();
        readAndPushAll();
        mqPusher.closeMqProducer();
    }

    private void readAndPushAll() {
        try {
            try (Connection connection = this.dataSource.getConnection()) {
                log.info("sql: " + this.appConfig.getSql());
                PreparedStatement ps = connection.prepareStatement(this.appConfig.getSql());
                ps.setFetchSize(this.appConfig.getFetchSize());

                ResultSet rs = ps.executeQuery();

                int mark = 0;
                ObjectMapper mapper = new ObjectMapper();
                while (rs.next()) {
                    ++mark;
                    Map<String, String> params = new HashMap<>();
                    ResultSetMetaData metaData = rs.getMetaData();
                    for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                        params.put(metaData.getColumnLabel(i), "" + rs.getObject(metaData.getColumnName(i)));
                    }
                    byte[] eventBytes = mapper.writer().writeValueAsBytes(params);
                    mqPusher.push(eventBytes);
                    if (mark % this.appConfig.getFetchSize() == 0) {
                        log.info(mark + " records are loaded");
                    }
                }
                if (mark == 0) {
                    log.info("Can't find any records");
                } else {
                    log.info(mark + " records are loaded");
                }
            }
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    private void readAndPushByRows() {

    }

}

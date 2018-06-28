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
public class Run {

    private final static Logger log = Logger.getLogger(Run.class);

    public static void main(String[] args) throws IOException {
        Run run = new Run();
        MQPusher mqPusher = new MQPusher();
        mqPusher.initMqProducer();
        run.push(mqPusher);
        mqPusher.closeMqProducer();
    }


    private void push(MQPusher mqPusher) {
        try {
            try (Connection connection = DataSource.getConnection()) {
                log.info("sql: " + Config.SQL);
                PreparedStatement ps = connection.prepareStatement(Config.SQL);
                ps.setFetchSize(Config.FETCH_SIZE);

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
                    if (mark % Config.FETCH_SIZE == 0) {
                        log.info(mark + " records are loaded");
                    }
                }
                if (mark == 0) {
                    log.info("Can't find any records");
                }
            }
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

}

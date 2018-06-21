package ru.cubesolutions.etl.dbreader;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Garya on 22.01.2018.
 */
public class DB {

    private final static Logger log = Logger.getLogger(DB.class);

    public static Long getInitialId(String initialIdSql) {
        try (Connection connection = DataSource.getConnection()) {
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

    public static List<Map<String, String>> getRecords(String sql, Long idStart, int step) {
        List<Map<String, String>> result = new ArrayList<>();
        try {
            try (Connection connection = DataSource.getConnection()) {
                log.debug("sql: " + String.format(sql, idStart, idStart + step));
                PreparedStatement ps = connection.prepareStatement(String.format(sql, idStart, idStart + step));
                long start = System.currentTimeMillis();
                ResultSet rs = ps.executeQuery();
                log.info("sql execution: " + (System.currentTimeMillis() - start) + "ms");

                int mark = 0;
                while (rs.next()) {
                    ++mark;
                    Map<String, String> params = new HashMap<>();

                    ResultSetMetaData metaData = rs.getMetaData();
                    for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                        params.put(metaData.getColumnLabel(i), "" + rs.getObject(metaData.getColumnName(i)));
                    }

                    result.add(params);
                }
                if (mark == 0) {
                    log.info("Can't find any records");
                    return result;
                }

            }
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
        return result;
    }

}

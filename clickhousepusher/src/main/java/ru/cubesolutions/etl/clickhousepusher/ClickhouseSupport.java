package ru.cubesolutions.etl.clickhousepusher;

import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static ru.cubesolutions.etl.clickhousepusher.Utils.*;


/**
 * Created by Garya on 15.11.2017.
 */
public class ClickhouseSupport {

    private final static Logger log = Logger.getLogger(ClickhouseSupport.class);

    private DestConfig appConfig;
    private DataSource dataSource;

    public ClickhouseSupport(DestConfig appConfig) {
        this.appConfig = appConfig;
        this.dataSource = new DataSource(appConfig);
    }

    public void insertEvents(List<Event> events) {
        if (isNullOrEmpty(events)) {
            return;
        }
        Set<String> paramNames = events.get(0).getParams().keySet();
        TableMapHolder tableMapHolder = appConfig.getTableMapHolder();
        try (Connection connection = dataSource.getConnection()) {
            StringBuilder sql = new StringBuilder("insert into "
                    + tableMapHolder.getDbName() + "." + tableMapHolder.getTable().getTableName()
                    + " (");
            for (String paramName : paramNames) {
                appendColumnName(sql, paramName);
            }
            removeLastSymbol(sql);
            sql.append(")values(");

            int i = 0;
            for (String paramName : paramNames) {
                if (tableMapHolder.existsEventParam(paramName)) {
                    appendQuestionMark(sql, paramName, ++i);
                }
            }
            removeLastSymbol(sql);
            sql.append(")");

            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql.toString());

            for (Event event : events) {
                i = 0;
                for (String paramName : paramNames) {
                    if (tableMapHolder.existsEventParam(paramName)) {
                        setValue(ps, paramName, event.getValue(paramName), ++i);
                    }
                }
                ps.addBatch();
            }

            ps.executeBatch();
            connection.commit();
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    public void truncateTable(String tableName) {
        checkTableOrColumnNameForSqlInjection(tableName);
        try (Connection connection = dataSource.getConnection()) {
            String sql = "truncate table " + tableName;
            connection.setAutoCommit(true);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeUpdate();
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    private void appendColumnName(StringBuilder sql, String eventParamName) {
        Column column = appConfig.getTableMapHolder().getColumnByParamName(eventParamName);
        if (isNotNull(column)) {
            sql.append(column.getName()).append(",");
        }
    }

    private void appendValue(StringBuilder sql, String eventParamName, String value) {
        Column column = appConfig.getTableMapHolder().getColumnByParamName(eventParamName);
        if (isNotNull(column)) {
            sql.append(value).append(",");
        }
    }

    private void appendQuestionMark(StringBuilder sql, String eventParamName, int index) {
        Column column = appConfig.getTableMapHolder().getColumnByParamName(eventParamName);
        if (isNotNull(column)) {
            sql.append("?").append(",");
        }
    }

    private void setValue(PreparedStatement ps, String eventParamName, String value, int index) throws SQLException {
        Column column = appConfig.getTableMapHolder().getColumnByParamName(eventParamName);
        if (isNull(column)) {
            return;
        }
        String columnType = column.getType().toLowerCase();
        if (columnType.contains("int")) {
            if (columnType.contains("8")) {
                ps.setByte(index, Byte.parseByte(value));
            } else if (columnType.contains("16")) {
                ps.setShort(index, Short.parseShort(value));
            } else if (columnType.contains("32")) {
                ps.setInt(index, Integer.parseInt(value));
            } else if (columnType.contains("64")) {
                ps.setLong(index, Long.parseLong(value));
            }
        } else if (columnType.equals("string")) {
            ps.setString(index, value);
        } else if (columnType.equals("datetime")) {
            Timestamp timestamp;
            try {
                timestamp = Timestamp.valueOf(LocalDateTime.parse(value, DateTimeFormatter.ofPattern(column.getValueFormat())));
            } catch (Exception e) {
                log.error("Can't parse timestamp for column: " + column + ", need to set correct column-value-format. Writing " + column.getName() + " as null");
                throw new RuntimeException(e);
            }
            ps.setTimestamp(index, timestamp);
        } else if (columnType.equals("date")) {
            Date date;
            try {
                date = Date.valueOf(LocalDate.parse(value, DateTimeFormatter.ofPattern(column.getValueFormat())));
            } catch (Exception e) {
                log.error("Can't parse date for column: " + column + ", need to set correct column-value-format. Writing " + column.getName() + " as null");
                throw new RuntimeException(e);
            }
            ps.setDate(index, date);
        } else if (columnType.contains("float")) {
            if (columnType.contains("32")) {
                ps.setFloat(index, Float.parseFloat(value));
            } else if (columnType.contains("64")) {
                ps.setDouble(index, Double.parseDouble(value));
            }
        }
    }

    private static void removeLastSymbol(StringBuilder sql) {
        sql.deleteCharAt(sql.length() - 1);
    }

}

package ru.cubesolutions.etl.clickhousepusher;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Garya on 15.11.2017.
 */
public class TableMapHolder {

    private final static Logger log = Logger.getLogger(TableMapHolder.class);

    private final static TableMapHolder INSTANCE;

    private String dbName;
    private Table table;
    private EventParam[] eventParams;

    private TableMapHolder(String dbName, Table table, EventParam[] eventParams) {
        this.dbName = dbName;
        this.table = table;
        this.eventParams = eventParams;
    }

    static {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream("./conf/clickhousepusher.properties")) {
            props.load(is);
        } catch (FileNotFoundException e) {
            try (InputStream input = AppConfig.class.getResourceAsStream("/clickhousepusher.properties")) {
                props.load(input);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        String dbName = props.getProperty("jdbc-url");
        dbName = dbName.substring(dbName.lastIndexOf("/") + 1);
        String tableName = props.getProperty("table-name");
        if (tableName != null) {
            tableName = tableName.trim();
        }
        checkTableOrColumnNameForSqlInjection(tableName);

        int nColumns = Integer.parseInt(props.getProperty("n-columns"));
        List<Column> columns = new ArrayList<>();
        for (int i = 0; i < nColumns; i++) {
            String iColumnName = props.getProperty("column-name-" + i);
            if (iColumnName != null) {
                iColumnName = iColumnName.trim();
            }
            checkTableOrColumnNameForSqlInjection(iColumnName);
            String iColumnType = props.getProperty("column-type-" + i);
            if (iColumnType == null || iColumnType.isEmpty()) {
                iColumnType = "String";
            } else {
                iColumnType = iColumnType.trim();
            }
            String iColumnValueFormat = props.getProperty("column-value-format-" + i);
            if (iColumnValueFormat != null) {
                iColumnValueFormat = iColumnValueFormat.trim();
            }
            Column iColumn = new Column(iColumnName, iColumnType, iColumnValueFormat);
            columns.add(iColumn);
        }
        Table table = new Table(tableName, nColumns, columns.toArray(new Column[]{}));

        List<EventParam> eventParams = new ArrayList<>();
        Set<String> propertyNames = props.stringPropertyNames();
        Iterator<String> it = propertyNames.iterator();
        while (it.hasNext()) {
            String propertyName = it.next();
            EventParam eventParam;
            if (propertyName.startsWith("event-param-name")) {
                String[] propertyLexemes = propertyName.split("-");
                eventParam = new EventParam(Integer.parseInt(propertyLexemes[propertyLexemes.length - 1]), props.getProperty(propertyName));
            } else {
                continue;
            }
            eventParams.add(eventParam);
        }
        INSTANCE = new TableMapHolder(dbName, table, eventParams.toArray(new EventParam[]{}));
    }

    private static void checkTableOrColumnNameForSqlInjection(String name) {
        Pattern p = Pattern.compile("\\w+");
        Matcher m = p.matcher(name);
        if (!m.matches()) {
            log.error("table name or column name must contains letters or digits only (or symbol _)");
            throw new RuntimeException("table name or column name must contains letters or digits only (or symbol _)");
        }
    }

    public static TableMapHolder getInstance() {
        return INSTANCE;
    }

    public boolean existsEventParam(String paramName) {
        if (paramName == null) {
            return false;
        }
        for (int i = 0; i < eventParams.length; i++) {
            if (paramName.trim().equalsIgnoreCase(eventParams[i].getName())) {
                return true;
            }
        }
        return false;
    }

    public Column getColumnByParamName(String paramName) {
        if (paramName == null) {
            return null;
        }
        for (int i = 0; i < eventParams.length; i++) {
            if (paramName.trim().equalsIgnoreCase(eventParams[i].getName())) {
                int columnIndex = eventParams[i].getColumnIndex();
                return table.getColumns()[columnIndex];
            }
        }
        return null;
    }

    public String getDbName() {
        return dbName;
    }

    public Table getTable() {
        return table;
    }

    public EventParam[] getEventParams() {
        return eventParams;
    }

}

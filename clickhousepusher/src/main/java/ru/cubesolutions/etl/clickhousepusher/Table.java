package ru.cubesolutions.etl.clickhousepusher;

/**
 * Created by Garya on 15.11.2017.
 */
public class Table {

    private String tableName;
    private int nColumns;
    private Column[] columns;

    public Table(String tableName, int nColumns, Column[] columns) {
        this.tableName = tableName;
        this.nColumns = nColumns;
        this.columns = columns;
    }

    public String getTableName() {
        return tableName;
    }

    public int getnColumns() {
        return nColumns;
    }

    public Column[] getColumns() {
        return columns;
    }
}

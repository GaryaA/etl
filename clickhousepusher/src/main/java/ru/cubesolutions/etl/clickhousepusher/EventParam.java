package ru.cubesolutions.etl.clickhousepusher;

/**
 * Created by Garya on 15.11.2017.
 */
public class EventParam {

    private int columnIndex;
    private String name;

    public EventParam(int columnIndex, String name) {
        this.columnIndex = columnIndex;
        this.name = name;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getName() {
        return name;
    }
}

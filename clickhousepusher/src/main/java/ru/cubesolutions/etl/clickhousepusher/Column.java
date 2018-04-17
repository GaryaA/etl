package ru.cubesolutions.etl.clickhousepusher;

/**
 * Created by Garya on 15.11.2017.
 */
public class Column {

    private String name;
    private String type;
    private String valueFormat;

    public Column(String name, String type) {
        this(name, type, null);
    }

    public Column(String name, String type, String valueFormat) {
        this.name = name;
        this.type = type;
        this.valueFormat = valueFormat;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValueFormat() {
        return valueFormat;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", valueFormat='" + valueFormat + '\'' +
                '}';
    }
}

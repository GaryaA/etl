package ru.cubesolutions.etl.clickhousepusher;

import java.util.Map;

/**
 * Created by Garya on 09.04.2018.
 */
public class Event {

    private Map<String, String> params;

    public Event(Map<String, String> params) {
        this.params = params;
    }

    public String getValue(String paramName) {
        return params.get(paramName);
    }

    public int paramsCount() {
        return params.size();
    }

    public Map<String, String> getParams() {
        return params;
    }
}

package ru.cubesolutions.etl.clickhousepusher;

import org.apache.log4j.Logger;

/**
 * Created by Garya on 09.04.2018.
 */
public class Run {

    private final static Logger log = Logger.getLogger(AppConfig.class);

    public static void main(String[] args) {
        ConsumerJob.INSTANCE.start();
    }

}

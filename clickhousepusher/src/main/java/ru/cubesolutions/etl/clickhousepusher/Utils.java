package ru.cubesolutions.etl.clickhousepusher;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Garya on 16.11.2017.
 */
public class Utils {

    private final static Logger log = Logger.getLogger(Utils.class);

    private Utils() {
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNullOrEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return c != null && !c.isEmpty();
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean isNotNull(Object o) {
        return o != null;
    }

    public static void sleepInSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            log.error("Internal error", e);
            throw new IllegalStateException(e);
        }
    }

    public static void sleepInMilliseconds(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            log.error("Internal error", e);
            throw new IllegalStateException(e);
        }
    }

    public static void stop(ExecutorService executor, int awaitTerminationInSeconds) {
        try {
            executor.shutdown();
            executor.awaitTermination(awaitTerminationInSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Internal error, termination task interrupted", e);
        } finally {
            if (!executor.isTerminated()) {
                log.warn("Internal error, killing non-finished tasks");
            }
            executor.shutdownNow();
        }
    }

    public static void checkTableOrColumnNameForSqlInjection(String name) {
        Pattern p = Pattern.compile("\\w+");
        Matcher m = p.matcher(name);
        if (!m.matches()) {
            log.error("table name or column name must contains letters or digits only (or symbol _)");
            throw new RuntimeException("table name or column name must contains letters or digits only (or symbol _)");
        }
    }

}

package ru.cubesolutions.etl.clickhousepusher;

/**
 * Created by Garya on 28.06.2018.
 */
public class Counter implements Runnable {

    public final static Counter INSTANCE = new Counter();

    private static long C = 0;

    private Counter() {
    }

    @Override
    public void run() {
        ++C;
    }

    public static long getC() {
        return C;
    }

    public static void nullify() {
        C = 0;
    }
}

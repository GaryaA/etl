package ru.cubesolutions.etl.clickhousepusher;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Garya on 28.06.2018.
 */
public class Counter implements Runnable {

    public final static Counter INSTANCE = new Counter();

    private AtomicInteger i = new AtomicInteger(0);

    private Counter() {
    }

    @Override
    public void run() {
        i.incrementAndGet();
    }

    public long getC() {
        return i.get();
    }

    public void nullify() {
        i.set(0);
    }
}

package com.taxisim.pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class WTFactory implements ThreadFactory {
    private final AtomicInteger id = new AtomicInteger(1);
    private final String prefix;

    public WTFactory(String prefix) { this.prefix = prefix; }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, prefix + "-" + id.getAndIncrement());
        t.setDaemon(false);
        return t;
    }
}

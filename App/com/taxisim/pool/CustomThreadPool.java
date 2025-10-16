package com.taxisim.pool;

import com.taxisim.logging.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CustomThreadPool {
    private final BlockingQueue<Runnable> queue;
    private final Thread[] workers;
    private final Logger log = Logger.getInstance();

    public CustomThreadPool(int threads, WTFactory factory) {
        this.queue = new LinkedBlockingQueue<>();
        this.workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            Thread t = factory.newThread(() -> {
                while (true) {
                    try {
                        Runnable r = queue.take();
                        try { r.run(); } catch (Throwable ex) { log.error("Task error"); }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            workers[i] = t;
            t.start();
        }
        log.info("Thread pool created with " + threads + " threads");
    }

    public void submit(Runnable task) {
        queue.offer(task);
    }

    public void shutdown() {
        for (Thread t : workers) t.interrupt();
        log.info("Thread pool shutdown called");
    }
}
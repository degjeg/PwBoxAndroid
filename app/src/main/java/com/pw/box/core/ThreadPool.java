package com.pw.box.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 * Created by danger on 16/9/4.
 */
public class ThreadPool {
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 10, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100000));

    public static ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    public static void executeQueued(Runnable runnable) {
        singleThreadPool.execute(runnable);
    }
}

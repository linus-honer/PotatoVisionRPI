package org.potato.core.util.task;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.potato.core.logging.Logger;

public class TimedTaskManager {
    public static final TimedTaskManager instance = new TimedTaskManager();

    public static TimedTaskManager getInstance() {
        return instance;
    }

    public static class ThreadMaker implements ThreadFactory {
        private static final ThreadFactory threadFactory = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = threadFactory.newThread(r);
            thread.setUncaughtExceptionHandler((t, e) -> logger.error("TimedTask aint caught bruh", e));
            return thread;
        }
    }

    private static final Logger logger = new Logger(TimedTaskManager.class, Logger.LogClassType.Master);

    private final ScheduledExecutorService timedTaskExecutorService = new ScheduledThreadPoolExecutor(2, new ThreadMaker());
    private final ConcurrentHashMap<String, Future<?>> activeTimedTasks = new ConcurrentHashMap<>();

    public void addTask(String identifier, Runnable runnable, long interval) {
        if(!activeTimedTasks.containsKey(identifier)) {
            var future = timedTaskExecutorService.scheduleAtFixedRate(runnable, 0, interval, TimeUnit.MILLISECONDS);
            activeTimedTasks.put(identifier, future);
        }
    }

    public void cancelTask(String identifier) {
        var future = activeTimedTasks.getOrDefault(identifier, null);
        if(future != null) {
            future.cancel(true);
            activeTimedTasks.remove(identifier);
        }
    }
}

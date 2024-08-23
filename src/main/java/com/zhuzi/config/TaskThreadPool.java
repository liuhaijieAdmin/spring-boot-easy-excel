package com.zhuzi.config;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 报表任务线程池（使用Spring封装的线程池）
 */
public class TaskThreadPool {

    /*
    * 并发比例
    * */
    public static final int concurrentRate = 3;

    /*
    * 核心线程数
    * */
    private static final int ASYNC_CORE_THREADS = 3, CONCURRENT_CORE_THREADS = ASYNC_CORE_THREADS * concurrentRate;

    /*
    * 最大线程数
    * */
    private static final int ASYNC_MAX_THREADS = ASYNC_CORE_THREADS + 1, CONCURRENT_MAX_THREADS = ASYNC_MAX_THREADS * concurrentRate;

    /*
    * 队列大小
    * */
    private static final int ASYNC_QUEUE_SIZE = 2000, CONCURRENT_QUEUE_SIZE = 20000;

    /*
    * 线程池的线程前缀
    * */
    public static final String ASYNC_THREAD_PREFIX = "excel-async-pool-", CONCURRENT_THREAD_PREFIX = "excel-concurrent-pool-";

    /*
    * 空闲线程的存活时间（单位秒），三分钟
    * */
    private static final int KEEP_ALIVE_SECONDS = 60 * 3;

    /*
    * 拒绝策略：如果队列、线程数已满，本次提交的任务返回给线程自己执行
    * */
    public static final ThreadPoolExecutor.AbortPolicy ASYNC_REJECTED_HANDLER =
            new ThreadPoolExecutor.AbortPolicy();
    public static final ThreadPoolExecutor.CallerRunsPolicy CONCURRENT_REJECTED_HANDLER =
                        new ThreadPoolExecutor.CallerRunsPolicy();
    /*
    * 异步线程池
    * */
    private volatile static ThreadPoolTaskExecutor asyncThreadPool, concurrentThreadPool;

    /*
    * DCL单例式懒加载：获取异步线程池
    * */
    public static ThreadPoolTaskExecutor getAsyncThreadPool() {
        if (asyncThreadPool == null) {
            synchronized (TaskThreadPool.class) {
                if (asyncThreadPool == null) {
                    asyncThreadPool = new ThreadPoolTaskExecutor();
                    asyncThreadPool.setCorePoolSize(ASYNC_CORE_THREADS);
                    asyncThreadPool.setMaxPoolSize(ASYNC_MAX_THREADS);
                    asyncThreadPool.setQueueCapacity(ASYNC_QUEUE_SIZE);
                    asyncThreadPool.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
                    asyncThreadPool.setThreadNamePrefix(ASYNC_THREAD_PREFIX);
                    asyncThreadPool.setWaitForTasksToCompleteOnShutdown(true);
                    asyncThreadPool.setRejectedExecutionHandler(ASYNC_REJECTED_HANDLER);
                    asyncThreadPool.initialize();
                    return asyncThreadPool;
                }
            }
        }
        return asyncThreadPool;
    }

    /*
     * DCL单例式懒加载：获取并发线程池
     * */
    public static ThreadPoolTaskExecutor getConcurrentThreadPool() {
        if (concurrentThreadPool == null) {
            synchronized (TaskThreadPool.class) {
                if (concurrentThreadPool == null) {
                    concurrentThreadPool = new ThreadPoolTaskExecutor();
                    concurrentThreadPool.setCorePoolSize(CONCURRENT_CORE_THREADS);
                    concurrentThreadPool.setMaxPoolSize(CONCURRENT_MAX_THREADS);
                    concurrentThreadPool.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
                    concurrentThreadPool.setQueueCapacity(CONCURRENT_QUEUE_SIZE);
                    concurrentThreadPool.setThreadNamePrefix(CONCURRENT_THREAD_PREFIX);
                    concurrentThreadPool.setWaitForTasksToCompleteOnShutdown(true);
                    concurrentThreadPool.setRejectedExecutionHandler(CONCURRENT_REJECTED_HANDLER);
                    concurrentThreadPool.initialize();
                    return concurrentThreadPool;
                }
            }
        }
        return concurrentThreadPool;
    }
}

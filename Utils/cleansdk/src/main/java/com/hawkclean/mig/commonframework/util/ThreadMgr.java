package com.hawkclean.mig.commonframework.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dongdong.huang
 * @Description: 线程统一管理类
 * @date 2016/8/29 17:27
 * @copyright TCL-MIG
 */
public class ThreadMgr {
    private static final String TAG = ThreadMgr.class.getSimpleName();
    volatile private static ThreadMgr threadMgr;
    private static Object mMgrLock = new Object();
    private ThreadPoolExecutor mLocalExecutor;
    private ThreadPoolExecutor mNetworkExecutor;

    /**
     * 执行本地任务
     * @param task
     */
    public static Future executeLocalTask(Runnable task){
        initMgrIfNeed();
        return threadMgr.runLocalTask(task);
    }

    public static Future executeLocalTask(Runnable task, long delay){
        initMgrIfNeed();
        return threadMgr.runLocalTask(task, delay);
    }

    /**
     * 执行网络任务
     * @param task
     */
    public static Future executeNetworkTask(Runnable task){
        initMgrIfNeed();
        return threadMgr.runNetworkTask(task);
    }

    public static Future executeNetworkTask(Runnable task, long delay){
        initMgrIfNeed();
        return threadMgr.runNetworkTask(task, delay);
    }

    public static Executor getNetworkExecutor(){
        initMgrIfNeed();
        return threadMgr.getNetExecutor();
    }

    public static Executor getLocalExecutor(){
        initMgrIfNeed();
        return threadMgr.getLocExecutor();
    }

    private Executor getNetExecutor(){
        return mNetworkExecutor;
    }

    private Executor getLocExecutor(){
        return mLocalExecutor;
    }

    public static void destroy(){
        if(threadMgr != null){
            threadMgr.close();
            threadMgr = null;
        }
    }

    private static void initMgrIfNeed(){
        if(threadMgr == null){
            synchronized (mMgrLock){
                if(threadMgr == null){
                    threadMgr = new ThreadMgr();
                }
            }
        }
    }

    private ThreadMgr(){
        initExecutors();
    }

    private void initExecutors(){
        mLocalExecutor = new ThreadPoolExecutor(3, 5, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadMgrFactory("thread-local"));
        mNetworkExecutor = new ThreadPoolExecutor(2, 5, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadMgrFactory("thread-network"));
    }

    private Future runLocalTask(Runnable runnable){
        return mLocalExecutor.submit(runnable);
    }

    private Future runLocalTask(Runnable runnable, long delay){
        return mLocalExecutor.submit(new DelayRunnable(runnable, delay));
    }

    private Future runNetworkTask(Runnable runnable){
        return mNetworkExecutor.submit(runnable);
    }

    private Future runNetworkTask(Runnable runnable, long delay){
        return mNetworkExecutor.submit(new DelayRunnable(runnable, delay));
    }

    private void close(){
        if(mLocalExecutor != null){
            mLocalExecutor.shutdown();
            mLocalExecutor.setCorePoolSize(0);
            mLocalExecutor = null;
        }

        if(mNetworkExecutor != null){
            mNetworkExecutor.shutdown();
            mNetworkExecutor.setCorePoolSize(0);
            mNetworkExecutor = null;
        }
    }

    class ThreadMgrFactory implements ThreadFactory{
        private String threadName;
        public ThreadMgrFactory(String name){
            threadName = name;
        }

        private final AtomicInteger mCount = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            Thread newThread = new Thread(r);
            newThread.setName(threadName + mCount.getAndIncrement());
            return newThread;
        }
    }

    /**
     * 延时执行runnable
     */
    class DelayRunnable implements Runnable{
        private Runnable delayTask;
        private long delayTime;
        public DelayRunnable(Runnable task, long delay){
            delayTask = task;
            delayTime = delay;
        }

        @Override
        public void run() {
            if(delayTime >= 0){
                try {
                    Thread.sleep(delayTime);
                } catch (InterruptedException e) {

                }
            }

            if(delayTask != null){
                delayTask.run();
            }
        }
    }
}

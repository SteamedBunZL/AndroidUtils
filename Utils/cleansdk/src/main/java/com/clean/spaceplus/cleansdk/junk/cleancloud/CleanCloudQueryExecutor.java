package com.clean.spaceplus.cleansdk.junk.cleancloud;

import android.annotation.SuppressLint;
import android.os.Build;

import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import space.network.cleancloud.CleanCloudDef;
import space.network.cleancloud.CleanCloudDef.WaitResultType;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/25 14:43
 * @copyright TCL-MIG
 */
public class CleanCloudQueryExecutor {
    private static final class KCMThreadFactory implements ThreadFactory {
        private final AtomicInteger mCount = new AtomicInteger(1);

        private final String mThreadName;

        public KCMThreadFactory(String mThreadName) {
            this.mThreadName = mThreadName;
        }

        public Thread newThread(Runnable r) {
            return new Thread(r, mThreadName + "#" + mCount.getAndIncrement());
        }
    }

    private static final class KCMRunnable implements Runnable {
        private final Runnable mTask;

        public KCMRunnable(Runnable mTask) {
            this.mTask = mTask;
        }

        @Override
        public void run() {
            if (Thread.currentThread().isInterrupted()) {// 恢复出厂设置
                Thread.currentThread().interrupt();
                return;
            }
            if (mTask != null) {
                mTask.run();
            }
        }
    }

    public static final int CALLBACK_RUNNER = 1;
    public static final int NETWORK_RUNNER  = 2;
    public static final int CALLBACK_RUNNER_2 = 3;

    private static int CORE_POOL_SIZE ;
    private static final int MAXIMUM_POOL_SIZE = 8;
    private static final int KEEP_ALIVE = 5;

    private final BlockingQueue<Future<?>> mPoolResultQueue = new LinkedBlockingQueue<>();

    /**
     * NQuery Thread pool
     */
    private ThreadPoolExecutor mNetWorkThreadHandler;
    private final BlockingQueue<Runnable> mPoolWorkQueue = new LinkedBlockingQueue<Runnable>(64);

    /**
     * UCallback Thread pool
     */
    private ThreadPoolExecutor mUserCallbackThreadHandler;
    private final BlockingQueue<Runnable> mCallbackPoolWorkQueue = new LinkedBlockingQueue<Runnable>();

    private volatile ThreadPoolExecutor mUserCallbackThreadHandler2 = null;
    private volatile BlockingQueue<Runnable> mCallbackPoolWorkQueue2 = null;


    @SuppressLint("NewApi")
    public CleanCloudQueryExecutor() {
        CORE_POOL_SIZE = Math.min(2 * Runtime.getRuntime().availableProcessors(), 4);
        mUserCallbackThreadHandler = new ThreadPoolExecutor(0, 1, 60L,
                TimeUnit.SECONDS, mCallbackPoolWorkQueue, new KCMThreadFactory(
                "UCallback")){
            protected void afterExecute(Runnable r, Throwable t) {
                CleanCloudQueryExecutor.this.afterExecute(r, t);
            }
        };

        mNetWorkThreadHandler = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                TimeUnit.SECONDS, mPoolWorkQueue, new KCMThreadFactory("NQuery"), new ThreadPoolExecutor.CallerRunsPolicy()){
            protected void afterExecute(Runnable r, Throwable t) {
                CleanCloudQueryExecutor.this.afterExecute(r, t);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mNetWorkThreadHandler.allowCoreThreadTimeOut(true);
        } else {
            mNetWorkThreadHandler.setCorePoolSize(1);
        }
    }

    private void initUserCallbackThreadHandler2() {
        if (mUserCallbackThreadHandler2 == null) {
            synchronized(this) {
                if (mUserCallbackThreadHandler2 == null) {
                    mCallbackPoolWorkQueue2 = new LinkedBlockingQueue<Runnable>();
                    mUserCallbackThreadHandler2 = new ThreadPoolExecutor(0, 1, 60L,
                            TimeUnit.SECONDS, mCallbackPoolWorkQueue2, new KCMThreadFactory(
                            "UCallback2")){
                        protected void afterExecute(Runnable r, Throwable t) {
                            CleanCloudQueryExecutor.this.afterExecute(r, t);
                        }
                    };
                }
            }
        }
    }
    private Future<?> submitTaskToCallbackRunner2(Runnable r) {
        initUserCallbackThreadHandler2();
        return mUserCallbackThreadHandler2.submit(new KCMRunnable(r));
    }

    public boolean post(int type, Runnable r){
        boolean result = false;
        Future<?> future = null;
        if (type == CALLBACK_RUNNER) {
            future = mUserCallbackThreadHandler.submit(new KCMRunnable(r));
        } else if (type == NETWORK_RUNNER) {
            future = mNetWorkThreadHandler.submit(new KCMRunnable(r));
        } else if (type == CALLBACK_RUNNER_2) {
            future = submitTaskToCallbackRunner2(r);
        }
        try {
            mPoolResultQueue.put(future);
            result = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public void quit() {
        discardAllQuery();
        safeQuit();
    }

    public void safeQuit() {
        mUserCallbackThreadHandler.setCorePoolSize(0);
        mNetWorkThreadHandler.setCorePoolSize(0);
        ThreadPoolExecutor executor2 = mUserCallbackThreadHandler2;
        if (executor2 != null) {
            executor2.setCorePoolSize(0);
        }
    }

    public void discardAllQuery() {
        synchronized(this) {
            discardAllHandle();
        }
    }

    public int waitForComplete(final long timeoutMillis, boolean discardQueryIfTimeout) {

        long lastTime = System.nanoTime();
        long nanosTimeout = TimeUnit.MILLISECONDS.toNanos(timeoutMillis);
        Exception exception = null;
        boolean timeout = false;
        while (true) {
            if (mPoolResultQueue == null) {
                return CleanCloudDef.WaitResultType.WAIT_FAILED;
            }
            if (mPoolResultQueue.isEmpty()) {
                break;
            }

            if (nanosTimeout <= 0) {
                timeout = true;
                break ;
            }

            try {
                Future<?> future = mPoolResultQueue.poll();
                if (future != null) {
                    try {
                        future.get(nanosTimeout, TimeUnit.NANOSECONDS);
                    } catch (TimeoutException e) {
                        timeout = true;
                        if (!discardQueryIfTimeout) {
                            mPoolResultQueue.put(future);
                        }
                        break;
                    }
                }
            } catch (InterruptedException e) {
                exception = e;
                e.printStackTrace();
                if (Thread.currentThread().isInterrupted()) {
                    Thread.currentThread().interrupt();
                }
            } catch (ExecutionException e) {
                exception = e;
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
            long now = System.nanoTime();
            nanosTimeout -= now - lastTime;
            lastTime = now;
        }

        if (discardQueryIfTimeout && timeout) {
            discardAllQuery();
        }

        if (timeout) {
            return WaitResultType.WAIT_TIMEOUT;
        }
        return exception == null ? WaitResultType.WAIT_SUCCESSED : WaitResultType.WAIT_COMPLETE;
    }

    public int waitForComplete(final long timeoutMillis, boolean discardQueryIfTimeout, CleanCloudDef.ScanTaskCtrl ctrl) {

        long lastTime = System.nanoTime();
        long nanosTimeout = TimeUnit.MILLISECONDS.toNanos(timeoutMillis);
        long nanosTimeoutOne = TimeUnit.MILLISECONDS.toNanos(701);
        Exception exception = null;
        boolean timeout = false;
        boolean cancelled = false;
        while (true) {
            if (mPoolResultQueue == null) {
                return WaitResultType.WAIT_FAILED;
            }
            if (mPoolResultQueue.isEmpty()) {
                break;
            }

            if ((null != ctrl && ctrl.checkStop())) {
                cancelled = true;
                break;
            }

            if (nanosTimeout <= 0) {
                timeout = true;
                break ;
            }

            try {
                Future<?> future = mPoolResultQueue.poll();
                if (future != null) {
                    boolean bBreak = false;
                    while (nanosTimeout > 0) {
                        try {
                            future.get(nanosTimeoutOne, TimeUnit.NANOSECONDS);
                        } catch (TimeoutException e) {
                            nanosTimeout -= nanosTimeoutOne;

                            if (nanosTimeout <= 0 || (null != ctrl && ctrl.checkStop())) {
                                timeout = true;
                                if (!discardQueryIfTimeout) {
                                    mPoolResultQueue.put(future);
                                }
                                bBreak = true;
                                break;
                            }

                            continue;
                        }

                        break;
                    }

                    if (bBreak) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                exception = e;
                e.printStackTrace();
                if (Thread.currentThread().isInterrupted()) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                exception = e;
                e.printStackTrace();
            }
            long now = System.nanoTime();
            nanosTimeout -= now - lastTime;
            lastTime = now;
        }

        if (discardQueryIfTimeout && timeout) {
            discardAllQuery();
        }

        if (timeout) {
            return CleanCloudDef.WaitResultType.WAIT_TIMEOUT;
        }
        if (cancelled) {
            return WaitResultType.WAIT_CANCEL;
        }
        return exception == null ? WaitResultType.WAIT_SUCCESSED : WaitResultType.WAIT_COMPLETE;
    }

    private void discardAllHandle() {
        if (mPoolResultQueue != null) {
            for (Future<?> f : mPoolResultQueue) {
                if (f != null) {
                    f.cancel(true);
                }
            }
            mPoolResultQueue.clear();
        }
        mNetWorkThreadHandler.purge();
        mUserCallbackThreadHandler.purge();
        ThreadPoolExecutor executor2 = mUserCallbackThreadHandler2;
        if (executor2 != null) {
            executor2.purge();
        }
    }
    protected void afterExecute(Runnable r, Throwable t) {
        if (r != null && r instanceof FutureTask<?>) {
            if (mPoolResultQueue != null) {
                mPoolResultQueue.remove(r);
            }
            try {
                Object o = ((FutureTask<?>) r).get(0, TimeUnit.MILLISECONDS);
            }catch (ExecutionException e) {
				e.printStackTrace();
                if(PublishVersionManager.isTest()){
                    throw new RuntimeException(e);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if ( t != null &&(t instanceof Exception) && !(t instanceof InterruptedException) && !(t instanceof CancellationException)) {
            if(PublishVersionManager.isTest()){
                throw new RuntimeException(t);
            }
        }
    }
}

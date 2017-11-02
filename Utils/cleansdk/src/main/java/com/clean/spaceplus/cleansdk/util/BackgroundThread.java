package com.clean.spaceplus.cleansdk.util;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * @author zengtao.kuang
 * @Description: 带Handler的后台线程
 * @date 2016/4/9 10:37
 * @copyright TCL-MIG
 */
public final class BackgroundThread extends HandlerThread {
    private static BackgroundThread sInstance;
    private static Handler sHandler;

    private BackgroundThread() {
        super("spaceplus.bg", android.os.Process.THREAD_PRIORITY_BACKGROUND);
    }

    private static void ensureThreadLocked() {
        if (sInstance == null) {
            sInstance = new BackgroundThread();
            sInstance.start();
            sHandler = new Handler(sInstance.getLooper());
        }
    }

    public static BackgroundThread get() {
        synchronized (BackgroundThread.class) {
            ensureThreadLocked();
            return sInstance;
        }
    }

    public static Handler getHandler() {
        synchronized (BackgroundThread.class) {
            ensureThreadLocked();
            return sHandler;
        }
    }

    public static void post(final Runnable runnable) {
        synchronized (BackgroundThread.class) {
            ensureThreadLocked();
            sHandler.post(runnable);
        }
    }
}


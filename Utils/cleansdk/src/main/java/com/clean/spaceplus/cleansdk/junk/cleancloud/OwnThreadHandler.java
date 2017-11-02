package com.clean.spaceplus.cleansdk.junk.cleancloud;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/18 19:42
 * @copyright TCL-MIG
 */
public class OwnThreadHandler {
    volatile private HandlerThread mHandlerThread;
    volatile private Handler mHandler;

    private String 	mName;

//	public void handleMessage(Message msg) {
//    }

    public Handler getHandler() {
        return mHandler;
    }

    public OwnThreadHandler(String name) {
        mName = name;
    }

    public void start() {
        init();
    }

    public void quit() {
        if (mHandlerThread != null) {
            synchronized(this) {
                if (mHandlerThread != null) {
                    mHandler.removeCallbacksAndMessages(null);
                    mHandlerThread.quit();
                    mHandler = null;
                    mHandlerThread = null;
                }
            }
        }
    }

/*	public void discardAllHandle() {
		Handler handler;
		synchronized(this) {
			handler = mHandler;
		}
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
	}*/

    public boolean post(Runnable r) {
        init();
        Handler handler;
        synchronized(this) {
            handler = mHandler;
        }
        return handler != null ? handler.post(r) : false;
    }

    public boolean postDelayed(Runnable r, long delayMillis) {
        init();
        Handler handler;
        synchronized(this) {
            handler = mHandler;
        }
        return handler != null ? handler.postDelayed(r, delayMillis) : false;
    }

    public boolean postAtFrontOfQueue(Runnable r) {
        init();
        Handler handler;
        synchronized(this) {
            handler = mHandler;
        }
        return handler != null ? handler.postAtFrontOfQueue(r) : false;
    }

    public void removeCallbacks(Runnable r) {
        Handler handler;
        synchronized(this) {
            handler = mHandler;
        }
        if (handler != null) {
            handler.removeCallbacks(r);
        }
    }

    private void init() {
        if (mHandler != null)
            return;

        synchronized(this) {
            if (null == mHandler) {
                HandlerThread thread = new HandlerThread(mName);
                thread.start();
                Handler handler = new Handler(thread.getLooper());
				/*
				Handler handler = new Handler(thread.getLooper()) {
					@Override
					public void handleMessage(Message msg) {
						OwnThreadHandler.this.handleMessage(msg);
				    }
				};
				*/
                mHandlerThread = thread;
                mHandler = handler;
            }
        }
    }
}

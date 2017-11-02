package com.clean.spaceplus.cleansdk.junk.cleancloud;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/18 19:42
 * @copyright TCL-MIG
 */
public class KSimpleGlobalTask {
    private static KSimpleGlobalTask sRef = null;
    public static synchronized KSimpleGlobalTask getInstance() {
        if (sRef == null) {
            sRef = new KSimpleGlobalTask();
        }
        return sRef;
    }

    OwnThreadHandler mOwnThreadHandler = new OwnThreadHandler("KSimpleGlobalTask");

    public boolean post(Runnable r) {
        return mOwnThreadHandler.post(r);
    }

    public boolean postDelayed(Runnable r, long delayMillis) { return mOwnThreadHandler.postDelayed(r, delayMillis); }

    public boolean postAtFrontOfQueue(Runnable r) { return mOwnThreadHandler.postAtFrontOfQueue(r); }

    public void removeCallbacks(Runnable r) {
        mOwnThreadHandler.removeCallbacks(r);
    }
}

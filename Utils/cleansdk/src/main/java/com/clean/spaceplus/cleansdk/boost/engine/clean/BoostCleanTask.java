package com.clean.spaceplus.cleansdk.boost.engine.clean;

import android.content.Context;

import com.clean.spaceplus.cleansdk.boost.engine.data.BoostResult;

/**
 * @author zengtao.kuang
 * @Description: boost清理抽象类
 * @date 2016/4/6 17:42
 * @copyright TCL-MIG
 */
public abstract class BoostCleanTask<T> {
    protected Context mContext;
    protected T mSetting;
    protected boolean mIsStop;

    /*
     * Initialize boost clean, create some objects for clean
     *
     * @param ctx, the Context from caller
     * @param settings, clean settings
     *
     */
    public BoostCleanTask(Context ctx, T setting) {
        mContext = ctx;
        mSetting = setting;
        mIsStop = false;
    }

    public abstract int getType();

    /*
     * Clean according BoostCleanSetting
     *
     * @param callback, callback the clean progress
     */
    public abstract void clean(ICleanTaskCallback callback);

    public abstract BoostResult cleanSync();

    public void stop() {
        mIsStop = true;
    }

    /*
     * Clean task callback interface
     */
    public interface ICleanTaskCallback {
        void onCleanStart();
        void onCleanProgress(Object data);
        void onCleanFinish(Object result);
    }
}

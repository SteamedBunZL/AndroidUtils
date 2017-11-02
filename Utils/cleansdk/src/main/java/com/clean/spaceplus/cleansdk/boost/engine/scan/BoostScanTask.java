package com.clean.spaceplus.cleansdk.boost.engine.scan;

import android.content.Context;

import com.clean.spaceplus.cleansdk.boost.engine.data.BoostResult;

/**
 * @author zengtao.kuang
 * @Description: boost扫描抽象类
 * @date 2016/4/5 20:39
 * @copyright TCL-MIG
 */
public abstract class BoostScanTask<T extends BaseScanSetting> {
    protected Context mContext;
    protected T mSetting;

    /*
     * Initialize boost scan, create some objects for scan
     *
     * @param ctx, the Context from caller
     * @param settings, scan settings
     *
     */
    public BoostScanTask(Context ctx, T setting) {
        mContext = ctx;
        mSetting = setting;
    }

    public boolean isUseDataManager() {
        return mSetting.isUseDataManager;
    }

    public abstract int getType();

    /*
     * Scan according BoostScanSetting
     *
     * @param callback, callback the scan progress and scan results
     */
    public abstract void scan(IScanTaskCallback callback);

    public abstract BoostResult scanSync();

    /*
     * Scan task callback interface
     */
    public interface IScanTaskCallback {
        void onScanStart();
        void onScanProgress(Object data);
        void onScanPreFinish(Object results);
        void onScanFinish(Object results);
    }
}


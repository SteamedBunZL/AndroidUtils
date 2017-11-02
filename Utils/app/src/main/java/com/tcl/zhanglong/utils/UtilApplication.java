package com.tcl.zhanglong.utils;

import android.support.multidex.MultiDexApplication;

import com.steve.commonlib.DebugLog;
import com.tcl.zhanglong.utils.Utils.AppUtil;

/**
 * Created by Steve on 16/9/7.
 */
public class UtilApplication extends MultiDexApplication{

    public static UtilApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        DebugLog.w("Process: %s", AppUtil.getCurProcessName(this));
    }
}

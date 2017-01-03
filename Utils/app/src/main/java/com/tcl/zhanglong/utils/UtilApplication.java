package com.tcl.zhanglong.utils;

import android.app.Application;

import com.tcl.zhanglong.utils.Utils.AppUtil;
import com.tcl.zhanglong.utils.Utils.DebugLog;

/**
 * Created by Steve on 16/9/7.
 */
public class UtilApplication extends Application{

    public static UtilApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        DebugLog.w("Process: %s", AppUtil.getCurProcessName(this));
    }
}

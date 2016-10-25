package com.tcl.zhanglong.utils;

import android.app.Application;

/**
 * Created by Steve on 16/9/7.
 */
public class UtilApplication extends Application{

    public static UtilApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }
}

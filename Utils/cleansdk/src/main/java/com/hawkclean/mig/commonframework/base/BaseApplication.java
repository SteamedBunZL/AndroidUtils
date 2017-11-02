package com.hawkclean.mig.commonframework.base;

import android.app.Application;
import android.content.Context;

/**
 * @author liangni
 * @Description:
 * @date 2016/3/2 17:01
 * @copyright TCL-MIG
 */
public class BaseApplication extends Application{
    protected static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

}

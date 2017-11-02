package com.clean.spaceplus.cleansdk.base.utils.root;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.hawkclean.framework.log.NLog;

/**
 * @author shunyou.huang
 * @Description:Root广播接收器
 * @date 2016/5/5 20:16
 * @copyright TCL-MIG
 */

public class RootReceiver extends BroadcastReceiver {

    private static final String TAG = RootReceiver.class.getSimpleName();

    public static final String ACTION_ROOT_RECEIVER = "com.spaceplus.root.RootReceiver.Action";
    public static final String STATE = "STATE";

    public RootReceiver() {
    }

    public void onReceive(Context var1, Intent var2) {
        if (var2.getAction().equals(ACTION_ROOT_RECEIVER)) {
            NLog.d(TAG, "aState = %d", var2.getIntExtra("STATE", 0));
        }

    }

    public static void doNotify(int var0) {
        Context var1 = SpaceApplication.getInstance().getContext();
        //int var2 = AppEnvironment.getEnv().toUiRootStateMonitor(var0);
        //AppEnvironment.getEnv().setIntValue("rtreceiver", var2);
        Intent var3;
        (var3 = new Intent(ACTION_ROOT_RECEIVER)).setPackage(var1.getPackageName());
        var3.putExtra("STATE", var0);
        var1.sendBroadcast(var3);
    }

    public static int getState() {
        //return AppEnvironment.getEnv().getIntValue("rtreceiver", 0);
        return 0;
    }
}

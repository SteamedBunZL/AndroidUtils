package com.tcl.zhanglong.utils.Utils;

import android.app.ActivityManager;
import android.content.Context;

import static com.tcl.zhanglong.utils.Utils.FileUtil.readFile;

/**
 * Created by Steve on 16/10/25.
 */

public class AppUtil {

    /**
     * 获取当前进程名(弱),可能获取到的进程名为空
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;

    }

    /**
     * 获取当前进程名(强)
     * @param pid
     * @return
     */
    public static String getProcessName(int pid) {
        String processName = null;
        try {
            if (processName == null) {
                processName = readFile("/proc/" + pid + "/cmdline", '\0');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processName;
    }
}

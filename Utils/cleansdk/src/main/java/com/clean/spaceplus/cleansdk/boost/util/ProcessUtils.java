package com.clean.spaceplus.cleansdk.boost.util;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcScanResult;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessInfo;
import com.clean.spaceplus.cleansdk.util.BackgroundThread;
import com.hawkclean.framework.log.NLog;

/**
 * @author zengtao.kuang
 * @Description: 杀进程的工具
 * @date 2016/4/6 18:44
 * @copyright TCL-MIG
 */
public class ProcessUtils {

    public static final String TAG = ProcessUtils.class.getSimpleName();
    private static ActivityManager sActivityManager = null;

    private static PackageManager sPackageManager = null;

    private ProcessUtils() {
    }

    /**后台杀进程*/
    public static void killBackground(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return ;
        }

        ActivityManager am = getAm();
        if (am != null) {
            am.killBackgroundProcesses(pkgName);
        }

        NLog.d("KillTask",  "Restart:" +  pkgName);
    }

    private static class ProcessKillerTask implements Runnable {

        private ProcessModel mModel;

        public ProcessKillerTask(ProcessModel m) {
            mModel = m;
        }

        @Override
        public void run() {
            if (mModel.getServComponentList() != null) {
                for (ComponentName component : mModel.getServComponentList()) {
                    Intent intent = new Intent();
                    intent.setComponent(component);
                    try {
                        SpaceApplication.getInstance().getContext().stopService(intent);
                    } catch (Exception e) {
                        NLog.printStackTrace(e);
                    }
                }
            }

            //系统标识的属性
            if (mModel.mType == ProcessModel.PROCESS_SYS ||
                    mModel.isAbnormal() ||
                    mModel.isInFlexibleWhiteListState() ||
                    mModel.getExtKillStrategy() == ProcScanResult.STRATEGY_KILL ||
                    mModel.getCleanStrategy() == ProcessInfo.PROC_STRATEGY_KILL) {
                NLog.d(TAG,  "KillBackground:" +  mModel.getPkgName() + " " + mModel.getTitle() +
                        " mOOM:" + mModel.getOOMADJ() + " mUID:" + mModel.getUid() +
                        " mem:" + mModel.getMemory() / 1024 +
                        " servces:" + mModel.getServicesCount());
                restartPackageUtils(getAm(), mModel.getPkgName());

                return;
            }

            //FIXME
            /*if (Build.VERSION.SDK_INT > 7 && SuExec.getInstance().checkRoot()) {
                ForceStopPkgQueue.getInstance().asyncForceStopPackage(mModel.getPkgName());
                NLog.d("KillTask", "ForceStop:" + mModel.getPkgName() +
                        " mOOM:" + mModel.getOOMADJ() + " mUID:" + mModel.getUid() +
                        " mem:" + mModel.mGetMemory() / 1024 +
                        " servces:" + mModel.getServicesCount());
            } else */{
                restartPackageUtils(getAm(), mModel.getPkgName());
                NLog.d(TAG, "KillBackground:" + mModel.getPkgName() +
                        " mOOM:" + mModel.getOOMADJ() + " mUID:" + mModel.getUid() +
                        " mem:" + mModel.getMemory() / 1024 +
                        " servces:" + mModel.getServicesCount());

                // it will kill process in background
                CleanProcessUtilBackground.getInstance().addProcessModel(mModel);
            }
        }
    }

    public static void killAsync(ProcessModel model) {
        BackgroundThread.getHandler().post(new ProcessKillerTask(model));
    }

    public static void killSystem(String packageName) {
        restartPackageUtils(getAm(), packageName);
    }

    private static void restartPackageUtils(ActivityManager am, String packageName) {
        try {
            am.restartPackage(packageName);
        } catch (SecurityException e) {
        } catch (Exception e) {
        }
    }

    private synchronized static ActivityManager getAm() {
        if (sActivityManager == null) {
            sActivityManager = (ActivityManager) SpaceApplication.getInstance().getContext().getSystemService(Service.ACTIVITY_SERVICE);
        }
        return sActivityManager;
    }

}

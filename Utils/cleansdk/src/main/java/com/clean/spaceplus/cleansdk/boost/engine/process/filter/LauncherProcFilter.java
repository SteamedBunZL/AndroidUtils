package com.clean.spaceplus.cleansdk.boost.engine.process.filter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.boost.util.ProcessOOMHelper;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zengtao.kuang
 * @Description: Launcher过滤
 * @date 2016/4/19 14:09
 * @copyright TCL-MIG
 */
public class LauncherProcFilter {

    private Set<String> mLaunchers = new HashSet<String>();

    public LauncherProcFilter(Context context) {
        mLaunchers = getLaucherPackageNames(context);
        //FIXME
        String pkgToExclude = "com.lbe.security";
        if (mLaunchers.contains(pkgToExclude)) {

            if (!pkgToExclude.equals(LauncherProcFilterHelper.getInstance().getCurrentLauncherName(false))) {
                mLaunchers.remove(pkgToExclude);
            }
        }
    }

    public boolean isLauncherPkg(String pkgName) {
        return mLaunchers.contains(pkgName);
    }

    /**
     *  手机中launchers的包名
     *  */
    public static Set<String> getLaucherPackageNames(Context context) {
        Set<String> launchers = new HashSet<String>();
        if (null == context) {
            return launchers;
        }

        PackageManager pm = context.getPackageManager();
        if (null == pm) {
            return launchers;
        }


        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> list =
                pm.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (null == list || list.isEmpty()) {
            return launchers;
        }

        for(ResolveInfo info : list){
            if(null != info.activityInfo && null != info.activityInfo.packageName){
                launchers.add(info.activityInfo.packageName);
            }
        }

        return launchers;
    }

    static class LauncherProcFilterHelper{
        public static final long REFRESH_TIME_INTERVAL = 1000 * 60 * 60 * 24;

        private int mMyVersion = 0;
        private long mLastFlushTime = 0;
        private Context mCtx = null;
        private String mTargetPkg = null;

        private static LauncherProcFilterHelper ms_inst = null;

        public static LauncherProcFilterHelper getInstance() {
            if (ms_inst == null) {
                ms_inst = new LauncherProcFilterHelper();

            }

            if (ms_inst.mCtx == null) {
                ms_inst.mCtx = SpaceApplication.getInstance().getContext().getApplicationContext();
            }

            return ms_inst;
        }

        /**
         * 获取当前launcher。先尝试获取默认launcher，成功就返回。否则获取整个launcher列表，获取其oom最低值认为是当前launcher。
         * 如果forceReflesh==false, 则内部会保证两次重新计算的时间间隔不小于REFRESH_TIME_INTERVAL。
         *
         * */
        public String getCurrentLauncherName(boolean forceReflesh) {

            checkData(forceReflesh);

            return mTargetPkg;
        }

        private void checkData(boolean forceReflesh) {
            if (forceReflesh
                    || (REFRESH_TIME_INTERVAL < System.currentTimeMillis()
                    - mLastFlushTime)) {
                doFlush();
            }
        }


        /**
         * 查询当前已锁定的launcher名
         * */
        public String getCurrentLockedLauncherPkg(Context context) {
            if (null == context) {
                return null;
            }

            PackageManager pm = context.getPackageManager();
            if (null == pm) {
                return null;
            }

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (null == resolveInfo || null == resolveInfo.activityInfo || null == resolveInfo.activityInfo.packageName) {
                return null;
            }

            ///< 多个launcher的情况下，没有设置默认的launcher，那么获取到的数据为android，故过滤掉
            if (resolveInfo.activityInfo.packageName.equals("android")) {
                return null;
            }

            return resolveInfo.activityInfo.packageName;
        }


        private void doFlush() {

            mLastFlushTime = System.currentTimeMillis();

            String defaultLaucher = getCurrentLockedLauncherPkg(mCtx);
            if (!TextUtils.isEmpty(defaultLaucher)) {
                mTargetPkg = defaultLaucher;
                return ;
            }

            mTargetPkg = ""; // 返回null的话鬼知道后续哪会空指针

            Set<String> allLauncher = getLaucherPackageNames(mCtx);

            int lastMinOOM = 15;
            int lastMinOOMProcIndex = -1;

            ActivityManager am = (ActivityManager) mCtx.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processList = null;
            try {
                processList = am.getRunningAppProcesses();
            } catch (Exception e) {
                // workaround: dumpkey = 1948482092
            }
            if (processList != null && processList.size() > 0) {
                for (int idx = 0; idx < processList.size(); ++idx) {
                    ActivityManager.RunningAppProcessInfo procInfo = processList.get(idx);

                    if (procInfo.pkgList != null && procInfo.pkgList.length > 0) {
                        String pkgName = procInfo.pkgList[0];

                        if (allLauncher.contains(pkgName)) {
                            int oom = ProcessOOMHelper.getProcessOOM(procInfo.pid);

                            if (oom == 6 || oom == 7) {
                                lastMinOOMProcIndex = idx;
                                break;
                            } else if (oom < lastMinOOM) {
                                lastMinOOMProcIndex = idx;
                                lastMinOOM = oom;
                            }
                        }
                    }
                }

                if (0 <= lastMinOOMProcIndex) {
                    mTargetPkg = processList.get(lastMinOOMProcIndex).pkgList[0];
                }
            }

            // clean
            allLauncher = null;
        }
    }
}

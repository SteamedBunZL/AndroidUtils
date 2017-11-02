package com.clean.spaceplus.cleansdk.junk.cleancloud;

import android.content.Context;
import android.content.SharedPreferences;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import space.network.util.KCleanCloudMiscHelper;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/25 16:52
 * @copyright TCL-MIG
 */
public class CleanCloudPref {
    private static CleanCloudPref sInstanse;
    public static synchronized CleanCloudPref getInstanse() {
        if (sInstanse == null) {
            sInstanse = new CleanCloudPref(SpaceApplication.getInstance().getContext());
        }
        return sInstanse;
    }

    private static final String PREFS_NAME     	= "cleancloud_pref";

    //设置缓存时效
    private static final String CACHE_LIFETIME 	= "cache_lifetime";

    //记录当前app版本，主要用于判断是否是新安装，是升级安装还是降级安装
    private static final String CURRENT_VERSION = "current_version";

    //上次上报路径收集的时间，主要用于一天只上报一次路径收集信息的功能
    private static final String REP_PATH_TIME   = "rep_path_time";

    //上次去误报时间
    private static final String LAST_DO_FALSE_TIME = "last_do_false_time";
    //private static final String ANDROID_ID 		= "android_id";

    private static final String CPU_RANDOM_QUERY_SEED = "cpu_query_seed";

    private SharedPreferences mSharedPreferences;
    private int mOldVersion;
    private int mCurrentVersion;
    private boolean mIsNewInstall;

    /////////////////////////////////////////////////
    public CleanCloudPref(Context context) {
        //mContext = context;
        mSharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        mIsNewInstall = checkisNewInstall();
    }

    public int getCacheLifetime() {
        return getIntValue(CACHE_LIFETIME, 7);
    }

    public long getLastReportPathTime() {
        return getLongValue(REP_PATH_TIME, 0);
    }

    public void setLastReportPathTime(long time) {
        setLongValue(REP_PATH_TIME, time);
    }

    public long getLastDoFalseTime() {
        return getLongValue(LAST_DO_FALSE_TIME, 0);
    }

    public void setLastDoFalseTime(long time) {
        setLongValue(LAST_DO_FALSE_TIME, time);
    }

    public int getCpuQueryRandomQuerySeed() {
        return getIntValue(CPU_RANDOM_QUERY_SEED, 0);
    }

    public void setCpuQueryRandomQuerySeed(int value) {
        setIntValue(CPU_RANDOM_QUERY_SEED, value);
    }

    private void setIntValue(String key, int value) {
        synchronized(this) {
            if (!mSharedPreferences.contains(key)) {
                setIntValueNoLock(key, value);
            } else {
                int oldValue = mSharedPreferences.getInt(key, 0);
                if (value != oldValue) {
                    setIntValueNoLock(key, value);
                }
            }
        }
    }

    private int getIntValue(String key, int defaultValue) {
        synchronized(this) {
            int value = mSharedPreferences.getInt(key, defaultValue);
            return value;
        }
    }

    private void setIntValueNoLock(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private void setLongValue(String key, long value) {
        synchronized(this) {
            if (!mSharedPreferences.contains(key)) {
                setLongValueNoLock(key, value);
            } else {
                long oldValue = mSharedPreferences.getLong(key, 0);
                if (value != oldValue) {
                    setLongValueNoLock(key, value);
                }
            }
        }
    }

    private long getLongValue(String key, long defaultValue) {
        synchronized(this) {
            long value = mSharedPreferences.getLong(key, defaultValue);
            return value;
        }
    }

    private void setLongValueNoLock(String key, long value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }


    private void _setCurrentVersion(int value) {
        synchronized(this) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(CURRENT_VERSION, value);
            editor.commit();
        }
    }

    private int _getCurrentVersion() {
        synchronized(this) {
            int value = mSharedPreferences.getInt(CURRENT_VERSION, 0);
            return value;
        }
    }

    private boolean checkisNewInstall() {
        boolean result = false;
        int version = KCleanCloudMiscHelper.getCurrentVersion(CleanCloudManager.getApplicationContext());
        mCurrentVersion = version;
        int prefVersion = _getCurrentVersion();
        if (version != prefVersion) {
            result = true;
            mOldVersion = prefVersion;
            _setCurrentVersion(version);
        }
        return result;
    }
}

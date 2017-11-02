package com.clean.spaceplus.cleansdk.junk.cleancloud.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.utils.system.PackageManagerWrapper;
import com.clean.spaceplus.cleansdk.util.Commons;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/7/19 15:28
 * @copyright TCL-MIG
 */

public class LocalStorage {

    private static LocalStorage sInstance = new LocalStorage();

    private static final int UNINSTALL_SYSTEM_ENABLE_APPS = 50;

    private static boolean sLoadedSo = false;

    private static boolean DEBUG_SQL = false;

    static final String TAG_SHORTCUT_CREATED_LIST = "ShortcutCreatedList";


    public static final String[] PROJECTION_STRINGS = new String[] {"value"};

    public static final long UNINSTALL_RECOMMEND_CLOUD_RECORD_EXPIRE_TIME = 1000L*60*60*24*14;
    public static final long COMMENT_RECORD_EXPIRE_TIME = 1000L*60*60*24*14;


    public ArrayList<PackageInfo> getUserAppinfos() {
        ArrayList<PackageInfo> result = new ArrayList<>();

        List<PackageInfo> xs = PackageManagerWrapper.getInstance().getPkgInfoList();
        if(xs == null) {
            return result;
        }

        for(PackageInfo x : xs) {
            if(Commons.isUserApp(x.applicationInfo)) {
                result.add(x);
            }
        }
        return result;
    }


    private LocalStorage() {

    }
    public static LocalStorage getInstance() {
        return sInstance;
    }


    public void saveUserAppDescription(String packageName,String tip) {
        SharedPreferences sp = SpaceApplication.getInstance().getContext().getSharedPreferences("user_app", Context.MODE_PRIVATE);
        sp.edit().putString(packageName, tip).commit();
    }

    public String getUserAppDescription(String packageName) {
        SharedPreferences sp = SpaceApplication.getInstance().getContext().getSharedPreferences("user_app", Context.MODE_PRIVATE);
        return sp.getString(packageName, "");
    }

    public void saveInstallAppName(String packageName,String appName) {
        SharedPreferences sp = SpaceApplication.getInstance().getContext().getSharedPreferences("install_app_name", Context.MODE_PRIVATE);
        sp.edit().putString(packageName, appName).commit();
    }

    public void removeUninstalledAppName(String packageName) {
        SharedPreferences sp = SpaceApplication.getInstance().getContext().getSharedPreferences("install_app_name", Context.MODE_PRIVATE);
        sp.edit().remove(packageName).commit();
    }
    public String getInstallAppName(String packageName) {
        SharedPreferences sp = SpaceApplication.getInstance().getContext().getSharedPreferences("install_app_name", Context.MODE_PRIVATE);
        return sp.getString(packageName, "");
    }
}

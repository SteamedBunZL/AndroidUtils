package com.tcl.zhanglong.utils.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * Package 相关工具类
 * Created by zhanglong on 16/8/30.
 */
public class PackageUtil {

    /**
     * 由apk路径获取packageName
     * @param context
     * @param apkPath
     * @return
     */
    public static String getPackageName(Context context, String apkPath){
        final PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, 0);
        return info.packageName;
    }

    /**
     * 由apk路径获取PackageInfo
     * @param context
     * @param apkPath
     * @return
     */
    public static PackageInfo getPackageInfo(Context context,String apkPath){
        final PackageManager pm = context.getPackageManager();
        return pm.getPackageArchiveInfo(apkPath,0);
    }

    /**
     * 判断一个APP是否已经安装，不要用getInstalled方法去判断，耗时
     * @param context
     * @param pkg
     * @return
     */
    public static boolean isAppInstalled(Context context,String pkg){
        boolean ret = true;
        if(TextUtils.isEmpty(pkg)){
            ret = false;
        }
        try {
            context.getPackageManager().getPackageGids(pkg);
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }


    /**
     * 通过pkg 获取pakgeinfo
     * @param context
     * @param pkg
     * @return
     */
    public static PackageInfo getPackageInfoByPkg(Context context,String pkg) {
        PackageInfo info = null;
        try {
            PackageManager pm = context.getPackageManager();
            info = pm.getPackageInfo(pkg,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }
}

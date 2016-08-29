package com.tcl.zhanglong.utilsapplication.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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
}

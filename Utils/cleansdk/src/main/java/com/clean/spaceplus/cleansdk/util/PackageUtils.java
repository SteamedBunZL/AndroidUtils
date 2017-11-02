package com.clean.spaceplus.cleansdk.util;

import android.app.ActivityManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.util.system.PhoneModelUtils;
import com.hawkclean.framework.log.NLog;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: 包管理工具
 * @date 2016/4/6 16:35
 * @copyright TCL-MIG
 */
public class PackageUtils {

    private static final int APPLICATIONINFO_FLAG_STOPPED = 1<<21;

    public static int FLAG_UNKNOWN = 0;
    public static int FLAG_STOPPED = 1;
    public static int FLAG_UNSTOPPED = 2;

    private static final String SCHEME = "package";
    // 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    // 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
    private static final String APP_PKG_NAME_22 = "pkg";
    // InstalledAppDetails所在包名
    public static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    // InstalledAppDetails类名
    public static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
    // 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
    // 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
    public static final String APP_SDK_23 = "android.settings.APPLICATION_DETAILS_SETTINGS";

    public static final int FLAG_ACTIVITY_CLEAR_TASK = 0X00008000;
    /**
     * 查询是否是停包状态
     * @return Commons.FLAG_UNKNOWN
     * Commons.FLAG_STOPPED
     * Commons.FLAG_UNSTOPPED
     * */
    public static int getPackageStopped(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        if(packageInfo == null)
            return FLAG_UNKNOWN;

        ApplicationInfo ai = packageInfo.applicationInfo;
        if (ai == null) {
            return FLAG_UNKNOWN;
        }

        if ((ai.flags & APPLICATIONINFO_FLAG_STOPPED) != 0) {
            return FLAG_STOPPED;
        } else {
            return FLAG_UNSTOPPED;
        }
    }

    public static boolean isHasPackage(Context c, String packageName) {
        if (null == c || null == packageName)
            return false;

        boolean bHas = true;
        try {
            c.getPackageManager().getPackageInfo(packageName, PackageManager.GET_GIDS);
        } catch (/* NameNotFoundException */Exception e) {
            // 抛出找不到的异常，说明该程序已经被卸载
            bHas = false;
        }
        return bHas;
    }

    /**
     * 获取当前动态壁纸的包名
     *
     * @param context
     *            the context
     * @return 动态壁纸的包名，或者""
     */
    public final static String getPackageNameOfCurrentLiveWallpaper(Context context) {
        try {
            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            if (wallpaperManager == null) {
                return "";
            }

            WallpaperInfo wi = wallpaperManager.getWallpaperInfo();
            if (wi == null) {
                return "";
            }

            String pkgname = wallpaperManager.getWallpaperInfo().getPackageName();
            return pkgname == null ? "" : pkgname;
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
        return "";
    }

    public static ApplicationInfo getApplicationInfo(Context context, String pkgName) {

        if (TextUtils.isEmpty(pkgName)) {
            return null;
        }

        try {
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
            if (pkgInfo != null) {
                ApplicationInfo appInfo = pkgInfo.applicationInfo;
                return appInfo;
            }
        } catch (Exception e) {
        }

        return null;
    }


    public static ApplicationInfo getAppApplication(Context context, String packageName) {
        ApplicationInfo info = null;
        if (context == null) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        try {
            info = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return info;
        } catch (Exception e) {
        }
        return info;
    }

    public synchronized static void getPkgSize(Context context, String pkgName, IPackageStatsObserver.Stub observer) {
        try {
            if (observer != null) {
                Method getPackageSizeInfo = context.getPackageManager().getClass()
                        .getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(context.getPackageManager(), pkgName, observer);
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
    }


    /**
     * Get application name(OR application label) by specify package name
     *
     * @param context
     *            the context
     * @param packageName
     *            package name
     * @return null or application name
     */
    public static String getAppNameByPackageName(Context context, String packageName) {
        String label = null;
        if (context == null) {
            return null;
        }

        ApplicationInfo info = getApplicationInfo(context, packageName);
        if(info != null)    {
            PackageManager pm = context.getPackageManager();
            label = pm.getApplicationLabel(info).toString();
        }
        else{
            label = packageName;
        }

        return label;
    }

    /**
     * 获取未安装的APK的Icon
     * @param apkPath APK包路径
     * @return Icon
     */
    public static Drawable getApkIconByApkPath(String apkPath) {
        PackageManager pm = SpaceApplication.getInstance().getContext().getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (Error e) {
                NLog.e("ApkIconLoader", e.toString());
            }
        }
        return null;
    }

    /**
     * 获取packageInfo
     * @param context
     * @param pkgName
     * @return
     */
    public static PackageInfo getPackageInfo(Context context, String pkgName){
        if(context == null || TextUtils.isEmpty(pkgName)){
            return null;
        }

        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }

        return packageInfo;
    }

    static public boolean showAppSystemDetail(Context context, String packageName) {
        if (context == null || packageName == null) {
            return false;
        }

        final int apiLevel = Build.VERSION.SDK_INT;
        int flags = Intent.FLAG_ACTIVITY_NEW_TASK
                |Intent.FLAG_ACTIVITY_NO_HISTORY
                |Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
        if (apiLevel >= 11) {
            flags |= FLAG_ACTIVITY_CLEAR_TASK; //  min is 11
        }

        Intent intent = new Intent();
        intent.setFlags(flags);

        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(APP_SDK_23);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (null == list || list.size() == 0) {
            return false;
        }

        return ComponentUtils.startActivity(context, intent);
    }

    /**
     * 给定包名是否有进程存在
     * */
    public static boolean isPackageAlive(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        String[] pkgs = null;
        for (ActivityManager.RunningAppProcessInfo item : processList) {
            if (item == null)
                continue;
            pkgs = item.pkgList;
            if (pkgs != null) {
                for (int i = 0; i < pkgs.length; ++i) {
                    if (pkgs[i].equalsIgnoreCase(pkgName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }



    public static int getVersionCode(Context context, String pkgName) {
        if (context == null || null == pkgName || pkgName.length() <= 0) {
            return -1;
        }

        PackageManager localPackageManager = context.getPackageManager();
        try {
            return localPackageManager.getPackageInfo(pkgName, 0).versionCode;
        } catch (/* NameNotFoundException */Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Intent showInstalledAppDetails(Context context, String packageName) {
        if (packageName == null)
            return null;
        final int apiLevel = Build.VERSION.SDK_INT;
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if(PhoneModelUtils.isEMUI() && PackageUtils.isHasPackage(SpaceApplication.getInstance().getContext(), "com.huawei.systemmanager")){
//            intent.setAction("Intent.ACTION_VIEW");
//            intent.setClassName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
//        }
        if (apiLevel >= 9) {
            // 如果是MIUIV6，还得判断下这个app是否存在。
            if (PhoneModelUtils.isSingleMiuiV6() && PackageUtils.isHasPackage(SpaceApplication.getInstance().getContext(), "com.miui.securitycenter")) {
                intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                intent.putExtra("extra_pkgname", packageName);
            } else {
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + packageName));
            }
        } else {
            final String appPkgName = (apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra(appPkgName, packageName);
        }
        return intent;
    }
}

package com.clean.spaceplus.cleansdk.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.io.InputStream;

/**
 * @author shunyou.huang
 * @Description: 通用实用类
 * @date 2016/5/25 10:45
 * @copyright TCL-MIG
 */
@SuppressLint("NewApi")
public class CommonUtils {

//    private static final String TAG = CommonUtils.class.getName();

    /**
     * 是否是测试环境
     *
     * @param context
     * @return
     */
    static final String DOMAIN_TEST_VALUE= "ISTEST_VALUE";
    static final String DOMAIN_TEST = "TEST";
    static final String DOMAIN_PRD = "PRD";

    public static boolean isTest() {
        if (getSDPropertyValue("test", "false").equalsIgnoreCase("true")) {
            return true;
        }
        String isTest = ContextUtils.getMetaData(SpaceApplication.getInstance().getContext(), "ISTEST");
        if (TextUtils.isEmpty(isTest)) {
            return false;
        }
        return isTest.equalsIgnoreCase(DOMAIN_TEST) || isTest.equalsIgnoreCase(DOMAIN_TEST_VALUE);
    }

    /**
     * 获取渠道号
     *
     * @return
     */
    public static String getChannelId() {
        return PublishVersionManager.getChannelId();
    }

    /**
     * 获取鹰眼sdk的appkey
     * @return
     */
    public static String getStatisticsKey(){
        return PublishVersionManager.getStatisticsKey();
    }

//    public static String getnetworkInfoName(Context mContext) {
//        ConnectivityManager connectionManager =
//                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
//        /** 无网络 */
//        if (networkInfo == null) {
//            return "";
//        }
//        return networkInfo.getTypeName();
//    }
//
//    /**
//     * Refresh view status
//     */
//    public static void setVisibleGone(View view, View... views) {
//        if (null != view && view.getVisibility() != View.VISIBLE)
//            view.setVisibility(View.VISIBLE);
//        setGone(views);
//
//    }

    public static void setGone(View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (null != view && view.getVisibility() != View.GONE)
                    view.setVisibility(View.GONE);
            }
        }
    }

    public static void setVisible(View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (null != view && view.getVisibility() != View.VISIBLE)
                    view.setVisibility(View.VISIBLE);
            }
        }
    }

//    public static void setEnable(View... views) {
//        if (views != null && views.length > 0) {
//            for (View view : views) {
//                if (view != null && !view.isEnabled()) {
//                    view.setEnabled(true);
//                }
//            }
//        }
//    }
//
//    public static void setDisable(View... views) {
//        if (views != null && views.length > 0) {
//            for (View view : views) {
//                if (view != null && view.isEnabled()) {
//                    view.setEnabled(false);
//                }
//            }
//        }
//    }
//
//    public static void setInvisible(View... views) {
//        if (views != null && views.length > 0) {
//            for (View view : views) {
//                if (null != view && view.getVisibility() != View.INVISIBLE)
//                    view.setVisibility(View.INVISIBLE);
//            }
//        }
//    }
//
//    // 获取系统版本
//    public static String getVersionRelease() {
//        return Build.VERSION.RELEASE;
//    }
//
//    // 获取系统版本
//    public static int getVersionSDKINT() {
//        return Build.VERSION.SDK_INT;
//    }

    @SuppressWarnings("deprecation")
    public static int getDisplayMetricsWidth(Context mContext) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }


    @SuppressWarnings("deprecation")
    public static int getDisplayMetricsHeight(Context mContext) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

//    //屏幕密度
//    public static float getDisplayMetricsDensity(Activity mContext) {
//        DisplayMetrics metric = new DisplayMetrics();
//        mContext.getWindowManager().getDefaultDisplay().getMetrics(metric);
//        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
//        return density;
//    }

    public static String getIMSI(Context context) {
        String result = "";
        try{
            TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            result =  telManager.getSubscriberId();

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return result;
    }

//    /**
//     * 获取打开应用的Intent
//     * @param resolveInfo
//     * @param packageName
//     * @return
//     */
//    public static Intent getOpenIntent(ResolveInfo resolveInfo, String packageName) {
//        ComponentName componet = new ComponentName(packageName, resolveInfo.activityInfo.name);
//        Intent i = new Intent();
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.setComponent(componet);
//        return i;
//    }
//
//    /**
//     * 获取安装应用的Intent
//     * @param mContext
//     * @param file
//     * @return
//     */
//    public static Intent getInstallIntent(Context mContext, File file) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//        return intent;
//    }
//
//    /**
//     * 是否为系统应用
//     *
//     * @param context
//     * @return
//     */
//    public static boolean isSystemApp(Context context) {
//        return ((context.getApplicationInfo()).flags & ApplicationInfo.FLAG_SYSTEM) > 0;
//    }
//
//
//    /**
//     * 系统级自动安装
//     *
//     * @param apkPath
//     * @return
//     */
//    public static String sysInstall(String apkPath) {
//        String[] args = {"pm", "install", "-r", apkPath};
//        String result = "";
//        ProcessBuilder processBuilder = new ProcessBuilder(args);
//        Process process = null;
//        InputStream errIs = null;
//        InputStream inIs = null;
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            int read = -1;
//            process = processBuilder.start();
//            errIs = process.getErrorStream();
//            while ((read = errIs.read()) != -1) {
//                baos.write(read);
//            }
//            baos.write("/n".getBytes("UTF-8"));
//            inIs = process.getInputStream();
//            while ((read = inIs.read()) != -1) {
//                baos.write(read);
//            }
//            byte[] data = baos.toByteArray();
//            result = new String(data, "UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (errIs != null) {
//                    errIs.close();
//                }
//                if (inIs != null) {
//                    inIs.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (process != null) {
//                process.destroy();
//            }
//        }
//        return result;
//    }
//
//    /**
//     * root手机的自动安装
//     *
//     * @param
//     * @return
//     */
//    public static boolean execRootCmdSilent(String apkPath) {
//        int result = -1;
//        DataOutputStream dos = null;
//        String cmd = "pm install -r " + apkPath;
//        try {
//            Process p = Runtime.getRuntime().exec("su");
//            dos = new DataOutputStream(p.getOutputStream());
//            dos.writeBytes(cmd + "\n");
//            dos.flush();
//            dos.writeBytes("exit\n");
//            dos.flush();
//            p.waitFor();
//            result = p.exitValue();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (dos != null) {
//                try {
//                    dos.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return result == 0;
//    }

//    /**
//     * 获取图片资源
//     *
//     * @param mContext
//     * @param name
//     * @return
//     */
//    public static int getResource(Context mContext, String name) {
//        ApplicationInfo appInfo = mContext.getApplicationInfo();
//        return mContext.getResources().getIdentifier(name, "drawable", appInfo.packageName);
//    }

//    /**
//     * 获取app信息默认图片资源
//     *
//     * @param mContext
//     * @param
//     * @return
//     */
//    public static int getDefaultAppInfoPicture(Context mContext) {
//        return getResource(mContext, "screen"); /* 暂时为这个图片 */
//    }
//
//    /**
//     * 获取app默认图片资源
//     *
//     * @param mContext
//     * @param
//     * @return
//     */
//    public static int getDefaultAppPicture(Context mContext) {
//        return getResource(mContext, "ic_list_app_default"); /* 暂时为这个图片 */
//    }
//
//    /**
//     * 获取banner big默认图片资源
//     *
//     * @param mContext
//     * @param
//     * @return
//     */
//    public static int getDefaultBigBannerPicture(Context mContext) {
//        return getResource(mContext, "banner_big_default"); /* 暂时为这个图片 */
//    }
//
//    /**
//     * 获取banner small默认图片资源
//     *
//     * @param mContext
//     * @param
//     * @return
//     */
//    public static int getDefaultSmallBannerPicture(Context mContext) {
//        return getResource(mContext, "banner_small_default"); /* 暂时为这个图片 */
//    }
//
//    /**
//     * 判断网络是否连接
//     *
//     * @param mContext
//     * @return
//     */
//    public static boolean isNetConnect(Context mContext) {
//        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        if (wifi != null && wifi.isConnected()) {
//            return true;
//        }
//        return gprs != null && gprs.isConnected();
//    }
//
//    /**
//     * 判断是否wifi网络
//     *
//     * @param mContext
//     * @return
//     */
//    public static boolean isWifiConnect(Context mContext) {
//        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        return wifi != null && wifi.isConnected();
//    }
//
//    /**
//     * 获取版本name
//     *
//     * @param context
//     * @return
//     */
//    public static String getVersionName(Context context) {
//        PackageManager manager = context.getPackageManager();
//        try {
//            return (manager.getPackageInfo(context.getPackageName(), 0).versionName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    /**
     * 获取版本code
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {

        }
        return verCode;
    }

//    /**
//     * make true current connect service is wifi
//     *
//     * @param mContext
//     * @return
//     */
//    public static boolean isWifi(Context mContext) {
//        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        return wifi.isConnected();
//    }
//
//    /**
//     * 指定位置颜色
//     *
//     * @param text
//     * @param
//     * @param
//     * @return
//     */
//    public static SpannableStringBuilder setStringPartColor(String text) {
//        SpannableStringBuilder style = new SpannableStringBuilder(text);
//        style.setSpan(new ForegroundColorSpan(0xFF5db224), text.lastIndexOf("->"), text.length(),
//                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//        return style;
//    }

//    private final static int kSystemRootStateUnknow = -1;
//    private final static int kSystemRootStateDisable = 0;
//    private final static int kSystemRootStateEnable = 1;
//    private static int systemRootState = kSystemRootStateUnknow;

//    public static boolean isRootSystem() {
//        if (systemRootState == kSystemRootStateEnable) {
//            return true;
//        } else if (systemRootState == kSystemRootStateDisable) {
//
//            return false;
//        }
//        File f = null;
//        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
//        try {
//            for (int i = 0; i < kSuSearchPaths.length; i++) {
//                f = new File(kSuSearchPaths[i] + "su");
//                if (f != null && f.exists()) {
//                    systemRootState = kSystemRootStateEnable;
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//            NLog.printStackTrace(e);
//        }
//        systemRootState = kSystemRootStateDisable;
//        return false;
//    }

//    /**
//     * convert dip to px
//     *
//     * @param
//     * @param
//     * @return
//     */
//    public static int convertDIP2PX(Context context, int dip) {
//        float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
//    }
//
//    public static float convertDIP2PX(Context context, float dip) {
//        float scale = context.getResources().getDisplayMetrics().density;
//        return (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
//    }

    /**
     * 获取当前程序包名的名字
     * @return
     */
    public static String getPackageName() {
        String packageNames = "";
        try {
            Context context = SpaceApplication.getInstance().getContext();
            if (context == null){
                return "";
            }
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            packageNames = info.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return packageNames;

    }

//    /**
//     * 获取顶部Activity
//     * @param context
//     * @return
//     */
//    @SuppressWarnings("deprecation")
//    public static String getTopActivity(Context context) {
//        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
//        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
//
//        try {
//            if (runningTaskInfos != null)
//                return (runningTaskInfos.get(0).topActivity).toString();
//            else
//                return "";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    /**
//     * 判断某个界面是否在前台
//     *
//     * @param context
//     * @param className 某个界面名称
//     */
//    @SuppressWarnings({"deprecation"})
//    public static boolean isForeground(Context context, String className) {
//        if (context == null || TextUtils.isEmpty(className)) {
//            return false;
//        }
//
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<RunningTaskInfo> list = am.getRunningTasks(1);
//        if (list != null && list.size() > 0) {
//            ComponentName cpn = list.get(0).topActivity;
//            NLog.i(TAG, "------ cpn.getClassName = %s",cpn.getClassName());
//            if (className.equals(cpn.getClassName())) {
//                NLog.i(TAG, "------ is foreground------");
//                return true;
//            }
//        }
//        NLog.i(TAG, "------ is not  foreground------");
//        return false;
//    }
//
//    /**
//     * 返回当前的应用是否处于前台显示状态
//     * @param context
//     * @param packageName
//     * @return
//     */
//    public static  boolean isTopActivity(Context context, String packageName) {
//        ActivityManager activityManager = (ActivityManager)context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//        List<RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
//        if(list.size() == 0) {
//            NLog.i(TAG, "------ size == 0, is not foreground ------");
//            return false;
//        }
//        for (RunningAppProcessInfo process : list) {
//            NLog.d(TAG, Integer.toString(process.importance));
//            NLog.d(TAG, process.processName);
//            if (process.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE
//                    && process.processName.equals(packageName)) {
//                NLog.i(TAG, "------ is foreground------");
//                return true;
//            }
//        }
//        NLog.i(TAG, "------ is not foreground------");
//        return false;
//    }
//
//    /**
//     * 判断一个程序是否显示在前端,根据测试此方法执行效率在11毫秒,无需担心此方法的执行效率
//     *
//     * @param context
//     * @param packageName
//     * @return true--->在前端,false--->不在前端
//     */
//    public static boolean isApplicationShowing(Context context, String packageName) {
//        boolean result = false;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
//        if (appProcesses != null) {
//            for (RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
//                if (runningAppProcessInfo.processName.equals(packageName)) {
//                    int status = runningAppProcessInfo.importance;
//                    if (status == RunningAppProcessInfo.IMPORTANCE_VISIBLE
//                            || status == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                        result = true;
//                    }
//                }
//            }
//        }
//        return result;
//    }
//
//    /**
//     * String 时间传入 转换出 yyyy/MM/dd
//     *
//     * @param time
//     * @return
//     */
//    public static String time2Date(String time) {
//        String dateString = "";
//        if (!TextUtils.isEmpty(time) && TextUtils.isDigitsOnly(time)) {
//            Date currentTime = new Date(Long.parseLong(time));
//            dateString = DateFormat.getDateTimeInstance().format(currentTime);
//        }
//        return dateString;
//    }

//    /**
//     * 自动适配保持底部水平
//     *
//     * @param context
//     * @param view
//     * @param minHeight
//     * @param maxHeight
//     * @param position
//     * @param marginBottom
//     */
//    public static void autoScrollToAlignBottom(Context context, ListView view, int minHeight, int maxHeight,
//                                               int position, int marginBottom) {
//        int height = (position - view.getFirstVisiblePosition()) * minHeight + maxHeight;
//        int distance = height + marginBottom - view.getHeight();
//
//        if (distance > 0) {
//            if (Build.VERSION.SDK_INT >= 11) {
//                view.smoothScrollToPositionFromTop(view.getFirstVisiblePosition(), -distance);
//            } else {
//                view.smoothScrollToPosition(position + 1);
//            }
//        }
//    }

//    /**
//     * 自动适配保持底部水平
//     *
//     * @param context
//     * @param view
//     * @param position
//     */
//    public static void autoScrollToAlignBottom(Context context, ListView view, int position) {
//        autoScrollToAlignBottom(context, view, convertDIP2PX(context, 81), convertDIP2PX(context, 157), position,
//                convertDIP2PX(context, 10));
//    }
//
//    /**
//     * 删除当前应用的桌面快捷方式
//     *
//     * @param cx
//     */
//    public static void delShortcut(Context cx) {
//        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
//
//        // 获取当前应用名称
//        String title = null;
//        try {
//            final PackageManager pm = cx.getPackageManager();
//            title =
//                    pm.getApplicationLabel(pm.getApplicationInfo(cx.getPackageName(), PackageManager.GET_META_DATA))
//                            .toString();
//        } catch (Exception e) {
//        }
//        // 快捷方式名称
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
//        Intent shortcutIntent = cx.getPackageManager().getLaunchIntentForPackage(cx.getPackageName());
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//        cx.sendBroadcast(shortcut);
//    }
//
//    /**
//     * 判断桌面是否已添加快捷方式
//     *
//     * @param
//     * @param
//     * @return
//     */
//    public static boolean hasShortcut(Context cx) {
//        boolean result = false;
//        // 获取当前应用名称
//        String title = null;
//        try {
//            final PackageManager pm = cx.getPackageManager();
//            title =
//                    pm.getApplicationLabel(pm.getApplicationInfo(cx.getPackageName(), PackageManager.GET_META_DATA))
//                            .toString();
//        } catch (Exception e) {
//        }
//
//        final String uriStr;
//        if (Build.VERSION.SDK_INT < 8) {
//            uriStr = "content://com.android.launcher.settings/favorites?notify=true";
//        } else {
//            uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
//        }
//        final Uri CONTENT_URI = Uri.parse(uriStr);
//        final Cursor c = cx.getContentResolver().query(CONTENT_URI, null, "title=?", new String[]{title}, null);
//        if (c != null && c.getCount() > 0) {
//            result = true;
//        }
//        /* System.out.println("判断" + result); */
//        return result;
//    }
//
//    /**
//     * 嵌套listView 解决冲突
//     *
//     * @param listView
//     */
//    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        int height = getListViewHeight(listView);
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = height;
//        listView.setLayoutParams(params);
//    }

//    /**
//     * 获取ListView的高度
//     * @param listView
//     * @return
//     */
//    public static int getListViewHeight(ListView listView) {
//        if (listView == null) {
//            return 0;
//        }
//
//        HeaderViewListAdapter appAdapter = (HeaderViewListAdapter) listView.getAdapter();
//        if (appAdapter == null) {
//            return 0;
//        }
//        int totalHeight = 0;
//        for (int i = 0; i < appAdapter.getCount(); i++) {
//            View listItem = appAdapter.getView(i, null, listView);
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//
//        return totalHeight + (listView.getDividerHeight() * (appAdapter.getCount() - 1));
//    }

    /*
     * public boolean isHome(Context context) { ActivityManager mActivityManager = (ActivityManager)
     * context .getSystemService(Context.ACTIVITY_SERVICE); List<RunningTaskInfo> rti =
     * mActivityManager.getRunningTasks(1); List<String> homePackageNames = getHomes(context);
     * return homePackageNames.contains(rti.get(0).topActivity .getPackageName()); }
     *
     * private List<String> getHomes(Context context) { List<String> names = new
     * ArrayList<String>(); PackageManager packageManager = context.getPackageManager(); // 属性
     * Intent intent = new Intent(Intent.ACTION_MAIN); intent.addCategory(Intent.CATEGORY_HOME);
     * List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities( intent,
     * PackageManager.MATCH_DEFAULT_ONLY); for (ResolveInfo ri : resolveInfo) {
     * names.add(ri.activityInfo.packageName); // System.out.println(ri.activityInfo.packageName); }
     * return names; }
     */

//    /**
//     * 获取imei
//     *
//     * @param context
//     * @return
//     */
//    public static String getDeviceIMei(Context context) {
//        TelephonyManager telephonyManager = (TelephonyManager)
//                context.getSystemService(Context.TELEPHONY_SERVICE);
//        return telephonyManager.getDeviceId();
//    }

//    /**
//     * 获取屏幕原始尺寸高度，不包括虚拟功能键高度
//     * @param cxt
//     * @return
//     */
//    @SuppressWarnings("deprecation")
//    public static int getActualDisplayHeight(Context cxt) {
//        return ((Activity) cxt).getWindowManager().getDefaultDisplay().getHeight();
//    }
//
//    /**
//     * 获取屏幕原始尺寸高度，包括虚拟功能键高度
//     * @param cxt
//     * @return
//     */
//    @SuppressWarnings({"unchecked", "rawtypes"})
//    public static int getWholeDisplayHeight(Context cxt) {
//        int height = 0;
//        Display display = ((Activity) cxt).getWindowManager().getDefaultDisplay();
//        DisplayMetrics dm = new DisplayMetrics();
//        Class c = null;
//        try {
//            c = Class.forName("android.view.Display");
//            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
//            method.invoke(display, dm);
//            height = dm.heightPixels;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return height;
//    }

//    /**
//     * 判断虚拟按键是否存在
//     * 并根据差值获取其高度
//     *
//     * @return
//     */
//    public static int getVirtualKeyHeight(Context cxt) {
//        int sHeightDelta = getWholeDisplayHeight(cxt) - getActualDisplayHeight(cxt);
//        return sHeightDelta > 0 ? sHeightDelta : 0; //标识无虚拟按键
//    }
//
//    /**
//     * 获取navigation bar的高度
//     *
//     * @param
//     * @return
//     */
//    public static int getNavigationBarHeight(Context cxt) {
//        Resources resources = cxt.getResources();
//        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
//        int height = resources.getDimensionPixelSize(resourceId);
//        return height;
//    }
//
//    /**
//     * 判断手机是否有navigation bar
//     *
//     * @param activity
//     * @return
//     */
//    @SuppressLint("NewApi")
//    public static boolean checkDeviceHasNavigationBar(Context activity) {
//        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
//        boolean hasMenuKey = ViewConfiguration.get(activity)
//                .hasPermanentMenuKey();
////        boolean hasBackKey = KeyCharacterMap
////                .deviceHasKey(KeyEvent.KEYCODE_BACK);
//        return !hasMenuKey;
//    }

//    /**
//     * 判断手机系统是否为国内的方法
//     *
//     * @return
//     */
//    public static boolean isChinaEnvironment(Context context) {
//        String defaultDomain = ContextUtils.getMetaData(context, "DOMAIN_VERSION");
//        if (!TextUtils.isEmpty(defaultDomain)) {
//            if (DOMAIN_CHINA.equalsIgnoreCase(defaultDomain)) {
//                return true;
//            }
//        }
//        return false;
//    }

//    static final String DOMAIN_GLOBAL = "GLOBAL";
//    static final String DOMAIN_CHINA = "CHINA";
//    static final String DOMAIN_NA = "NORTHAMERICA";
//    static final String DOMAIN_LA = "LATAM";
//    public static final String PKGNAME_GLOBAL = "com.tcl.usercare";
//    public static final String PKGNAME_CHINA = "com.tcl.usercare.china";
//    public static final String PKGNAME_NA = "com.tcl.usercare.na";
//    public static final String PKGNAME_LA = "com.tcl.usercare.la";
//
//    /**
//     * 打开系统浏览器
//     *
//     * @param url
//     * @param context
//     */
//    public static void openWebByURL(String url, Context context) {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        Uri content_url = Uri.parse(url);
//        intent.setData(content_url);
//        context.startActivity(intent);
//    }
//
//    /**
//     * 邮件发送
//     * @param url
//     * @param context
//     */
//    public static void sendEmail(String url, Context context) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + url));
//        intent.putExtra(Intent.EXTRA_SUBJECT, "");
//        intent.putExtra(Intent.EXTRA_BCC, "");
//        intent.putExtra(Intent.EXTRA_CC, "");
//        intent.putExtra(Intent.EXTRA_TEXT, "");
//        context.startActivity(intent);
//    }

//    /**
//     * 判断是否为拉美定制
//     *
//     * @return
//     */
//    public static boolean isLaAmericaSpec() {
//
//        if (isTest()) {
//            return false;
//        }
//        String defaultDomain = ContextUtils.getMetaData(SpaceApplication.getInstance().getContext(), "DOMAIN_VERSION");
//        if (TextUtils.isEmpty(defaultDomain)) {
//            return false;
//        }
//        String domain = defaultDomain;
//        return DOMAIN_LA.equalsIgnoreCase(domain);
//    }

//    /**
//     * 判断是否为中国区定制
//     * 不依托于手机设置后的语言环境
//     *
//     * @return
//     */
//
//    public static boolean isChinaSpec() {
//        if (isTest()) {
//            return false;
//        }
//        String defaultDomain = ContextUtils.getMetaData(SpaceApplication.getInstance().getContext(), "DOMAIN_VERSION");
//        if (TextUtils.isEmpty(defaultDomain)) {
//            return false;
//        }
//        String domain = defaultDomain;
//        return DOMAIN_CHINA.equalsIgnoreCase(domain);
//    }

//    /**
//     * 判断是否为中国区accontsdk登录环境
//     * 不依托于手机设置后的语言环境
//     *
//     * @return
//     */
//
//    public static boolean isChinaUserInfoSpec() {
//        if (isTest()) {
//            return false;
//        }
//        String defaultDomain = ContextUtils.getMetaData(SpaceApplication.getInstance().getContext(), "TCL_SDK_DOMAIN_VERSION");
//        if (TextUtils.isEmpty(defaultDomain)) {
//            return false;
//        }
//        String domain = defaultDomain;
//        return DOMAIN_CHINA.equalsIgnoreCase(domain);
//    }


//    /**
//     * 获取服务端地址，如果不配置走测试服务地址,基础服务器地址分为国外，国内以及测试
//     *
//     * @param
//     * @return
//     */
//    public static String getServerUrl() {
//        String defaultDomain = ContextUtils.getMetaData(SpaceApplication.getInstance().getContext(), "DOMAIN_VERSION");
////        if (isTest()) {
////            return SettingUtility.getStringSetting("base_url_test");
////        } else {
////            if (defaultDomain.equalsIgnoreCase(DOMAIN_GLOBAL)) {
////                return SettingUtility.getStringSetting("base_url");
////            }
////            if (defaultDomain.equalsIgnoreCase(DOMAIN_CHINA)) {
////                return SettingUtility.getStringSetting("base_url_china");
////            }
////            if (defaultDomain.equalsIgnoreCase(DOMAIN_NA)) {
////                return SettingUtility.getStringSetting("base_url");
////            }
////        }
////        return SettingUtility.getStringSetting("base_url");
//        return null;
//    }

//    /**
//     * 获取首次服务条款服务地址
//     *
//     * @param
//     * @return
//     */
//    public static String getFirstServiceTermUrl() {
//        String defaultDomain = ContextUtils.getMetaData(MyApplication.getInstance(), "DOMAIN_VERSION");
//        if (isTest()) {
//            return getSettingBaseUrl(SettingUtility.getSetting("getServiceTerm_test"));
//        } else {
//            if (defaultDomain.equalsIgnoreCase(DOMAIN_GLOBAL)) {
//                return getSettingBaseUrl(SettingUtility.getSetting("getServiceTerm"));
//            }
//            if (defaultDomain.equalsIgnoreCase(DOMAIN_CHINA)) {
//                return getSettingBaseUrl(SettingUtility.getSetting("getServiceTerm_china"));
//            }
//            if (defaultDomain.equalsIgnoreCase(DOMAIN_NA)) {
//                return getSettingBaseUrl(SettingUtility.getSetting("getServiceTerm"));
//            }
//        }
//        return getSettingBaseUrl(SettingUtility.getSetting("getServiceTerm"));
//    }
//
//    public static String getSettingBaseUrl(Setting setting) {
//        return setting.getExtras().get(Consts.Setting.BASE_URL).getValue().toString() + setting.getValue();
//    }



//    /**
//     * 获取版本升级服务端地址，如果不配置走测试服务地址,基础服务器地址分为国外，国内以及测试
//     *
//     * @param
//     * @return
//     */
//    public static Setting getUpdateVersionServerSetting() {
//
//        if (isTest()) {
//            return SettingUtility.getSetting("getVersions_test");
//        } else {
//            String defaultDomain = ContextUtils.getMetaData(MyApplication.getInstance(), "DOMAIN_VERSION");
//            if (TextUtils.isEmpty(defaultDomain)) {
//                return SettingUtility.getSetting("getVersions");
//            }
//            if (defaultDomain.equalsIgnoreCase(DOMAIN_GLOBAL)) {
//                return SettingUtility.getSetting("getVersions");
//            }
//            if (defaultDomain.equalsIgnoreCase(DOMAIN_CHINA)) {
//                return SettingUtility.getSetting("getVersions_china");
//            }
//            if (defaultDomain.equalsIgnoreCase(DOMAIN_NA)) {
//                return SettingUtility.getSetting("getVersions");
//            }
//        }
//        return SettingUtility.getSetting("getVersions");
//    }

//    public static String getPhoneOperatorType(Context context) {
//
//        String returnStr = null;
//        returnStr = getSDPropertyValue("cu_version", "");
//        if ("US".equalsIgnoreCase(returnStr)) {
//            return USA_OPERATOR;
//        } else if ("CA".equalsIgnoreCase(returnStr)) {
//            return CANADA_OPERATOR;
//        }
//
//        //---通过手机CU来判断
//        PhoneInfoUtils utils = new PhoneInfoUtils(context);
//        String cuStr = utils.getCUValue();
//        if (!TextUtils.isEmpty(cuStr)) {
//            String[] curefArr = cuStr.split("-");
//            if (curefArr != null && curefArr.length > 1 && !TextUtils.isEmpty(curefArr[1])) {
//                if (curefArr[1].length() >= 3) {
//                    String fiterStr = curefArr[1];
//                    String targetCountry = fiterStr.substring(fiterStr.length() - 3, fiterStr.length() - 1);
//                    if ("US".equalsIgnoreCase(targetCountry)) {
//                        return USA_OPERATOR;
//                    } else if ("CA".equalsIgnoreCase(targetCountry)) {
//                        return CANADA_OPERATOR;
//                    }
//                }
//            }
//        }
//        //---通过手机运行商来判断
//        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        String imsi = telManager.getSubscriberId();
//        if (imsi != null) {
//            if (imsi.startsWith(USA_MCC1) || imsi.startsWith(USA_MCC2)) {
//                // 美国手机运营商
//                return USA_OPERATOR;
//            } else if (imsi.startsWith(CANADA_MCC1)) {
//                // 加拿大运营商
//                return CANADA_OPERATOR;
//            }
//        }
//        //---通过手机语言来判断
//        String country = context.getResources().getConfiguration().locale.getCountry().toString();
//        if ("CA".equalsIgnoreCase(country)) {
//            return CANADA_OPERATOR;
//        } else if ("US".equalsIgnoreCase(country)) {
//            return USA_OPERATOR;
//        }
//        return USA_OPERATOR;
//    }


//    /**
//     * 高通芯片
//     * 通过反射获取CU,PTS,PTH,BSN参数
//     *
//     * @return
//     */
//
//    public static String getSpecTctParam(String spec) {
//        if (TextUtils.isEmpty(spec)) {
//            return null;
//        }
//        try {
//            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
//            Method getMethod = systemPropertiesClass.getMethod("get",
//                    String.class);
//            Object object = new Object();
//            Object obj = getMethod.invoke(object, spec);
//            return (obj == null ? null : (String) obj);
//        } catch (Exception e) {
////			NLog.e("android.os.SystemProperties", e.getMessage(), new Object[0]);
//            NLog.printStackTrace(e);
//            return null;
//        }
//    }
//
//    /**
//     * 处理多语言，如果有国家区分的话，需添加国家后缀
//     *
//     * @return String faq需要的语言后缀
//     */
//    public static String langAndCountry() {
//        String lang = SpaceApplication.getInstance().getContext().getResources().getConfiguration().locale.getLanguage().toString();
//        String country = SpaceApplication.getInstance().getContext().getResources().getConfiguration().locale.getCountry().toString();
//        if (country.equalsIgnoreCase("CN") || country.equalsIgnoreCase("KH") || country.equalsIgnoreCase("BR")
//                || country.equalsIgnoreCase("LA") || country.equalsIgnoreCase("CA") || country.equalsIgnoreCase("BD")
//                || country.equalsIgnoreCase("HK") || country.equalsIgnoreCase("MM")) {
//            return lang + "-r" + country;
//        } else {
//            return lang;
//        }
//    }
//
//    /**
//     * 获取imei号
//     *
//     * @return String imei号
//     */
//    public static String getImei() {
//        Context mContext = SpaceApplication.getInstance().getContext();
//        TelephonyManager telephonyManager = (TelephonyManager) mContext
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        return telephonyManager.getDeviceId();
//    }

    /**
     * 获取sd卡上配置文件
     *
     * @param value        取的值
     * @param defaultValue 默认值
     * @return String
     */
    public static String getSDPropertyValue(String value, String defaultValue) {
        InputStream is = null;
        String returnStr = defaultValue;
//        try {
//            String path = ACContext.getDirectoryPath(DirType.properties) + File.separator + PROPERTIES_NAME;
//            File file = new File(path);
//            if (file.exists()) {
//                is = new FileInputStream(file);
//                InputStreamReader reader = new InputStreamReader(is, "utf-8");
//                Properties properties = new Properties();
//                properties.load(reader);
//                returnStr = properties.getProperty(value, defaultValue);
//            }
//        } catch (Exception e) {
//            return defaultValue;
//        } finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                }
//            }
//        }
        return returnStr;
    }


//    /**
//     * 获取升级apk所在的文件路径
//     *
//     * @return
//     */
//    public static String getUpdateApkFilePath(String latestVersionName) {
//        return new StringBuilder(Environment.getExternalStorageDirectory()
//                .getAbsolutePath()).append(File.separator).append("UserCare")
//                .append(File.separator).append("app").append(File.separator)
//                .append("OneTouchUserCare_" + latestVersionName + ".apk")
//                .toString();
//    }

//    public static void runApp(String packageName, String className, Context context) {
//        PackageInfo pi;
//        try {
//            pi = GlobalContext.getInstance().getPackageManager().getPackageInfo(packageName, 0);
//            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
//            resolveIntent.setPackage(pi.packageName);
//            PackageManager pManager = GlobalContext.getInstance().getPackageManager();
//            List apps = pManager.queryIntentActivities(
//                    resolveIntent, 0);
//
//            ResolveInfo ri = (ResolveInfo) apps.iterator().next();
//            if (ri != null) {
//                packageName = ri.activityInfo.packageName;
//                Intent intent = new Intent();
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                ComponentName cn = new ComponentName(packageName, className);
//                intent.setComponent(cn);
//                context.startActivity(intent);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



    // 常用APK 包名
//    public static final String PACKAGE_NAME_SMART_SUITE = "com.tcl.smartsuite";
//    public static final String PACKAGE_NAME_DIALER = "com.android.dialer";
//    public static final String PACKAGE_NAME_CONTACTS = "com.android.contacts";
//    public static final String PACKAGE_NAME_EMAIL = "com.android.email"; // com.tct.email
//    public static final String PACKAGE_NAME_CAMERA = "com.android.gallery3d"; //com.android.camera.CameraLauncher
//
//    // 常用Activity类名
////    public static final String CLASS_NAME_CAMERA = "com.android.camera.CameraLauncher";

//    /**
//     * @param context
//     * @param packageName 包名
//     * @return 如果包名存在，返回包名对应的图标，否则，返回空
//     */
//    public static Drawable getApkIcon(Context context, String packageName) {
//        Drawable icon = null;
//        try {
//            icon = context.getPackageManager().getApplicationIcon(packageName);
//        } catch (Exception e) {
//            //Logger.d(TAG, "getApkIcon() --->>  packageName: " + packageName + "not found!");
//        }
//        return icon;
//    }
//
//    /**
//     * @param context
//     * @param packageName 包名
//     * @param className Activity类的名称
//     * @return 如果所指定的Activity存在，返回Activity对应的图标，否则，返回空
//     */
//    public static Drawable getActivityIcon(Context context, String packageName, String className) {
//        Drawable icon = null;
//
//        try {
//            icon = context.getPackageManager().getActivityIcon(new ComponentName(packageName, className));
//        } catch (Exception e) {
//            //Logger.d(TAG, "getActivityIcon() --->>  packageName: " + packageName + " ;className:" + className + "not found!");
//        }
//        return icon;
//    }
//
//
//    /**
//     * 判断当前系统语言是否为反向语言
//     * ar he ur fa
//     *
//     * @return
//     */
//    public static boolean checkSystemLanguageRtl() {
//        String language = null;
//        try {
//            language = Locale.getDefault().getLanguage();
//            if (TextUtils.isEmpty(language)) {
//                return false;
//            }
//            if ("ar".equalsIgnoreCase(language) || language.startsWith("ar")) { //阿拉伯语
//                return true;
//            } else if ("he".equalsIgnoreCase(language) || language.startsWith("he")) { //希伯来语
//                return true;
//            } else if ("fa".equalsIgnoreCase(language) || language.startsWith("fa")) { //波斯语
//                return true;
//            } else if ("ur".equalsIgnoreCase(language) || language.startsWith("ur")) {  //乌尔都语
//                return true;
//            }
//        } catch (Exception e) {
//            return false;
//        }
//        return false;
//    }
//
//    /**
//     * 获取手机机型信息
//     * 如果pixi android5手机获取model的api为反射ro.build.product方式
//     * @return
//     */
//    public static String getPhoneModel(){
//        // start::: 改方法是用于自测的
//        String model = CommonUtils.getSDRootPropertyValue("phone_model", "");
//        if (!TextUtils.isEmpty(model)) {
//            return model;
//        }
//        // :::end
//        return Build.MODEL;
//    }

//    /**
//     * 获取系统属性 键值对
//     *
//     * @return
//     */
    //public static String getSpecProductName() {
//        return MySystemProperties.getProductName();
//    }

//    /**
//     * 获取sd卡上 指定配置文件
//     *
//     * @param value        取的值
//     * @param defaultValue 默认值
//     * @return String
//     */
//    public static String getSDRootPropertyValue(String value, String defaultValue) {
//        InputStream is = null;
//        String returnStr = defaultValue;
//        try {
//            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//            path += File.separator + "tcl_phone_build_info.properties";
//            File file = new File(path);
//            if (file.exists()) {
//                is = new FileInputStream(file);
//                InputStreamReader reader = new InputStreamReader(is, "utf-8");
//                Properties properties = new Properties();
//                properties.load(reader);
//                returnStr = properties.getProperty(value, defaultValue);
//            }
//        } catch (Exception e) {
//            return defaultValue;
//        } finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (Exception e) {
//                }
//            }
//        }
//        return returnStr;
//    }
//
//    public static Boolean existHttpPath(String httpPath) {
//        try {
//            URL url = new URL(httpPath);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            /**
//             * public int getResponseCode()throws IOException
//             * 从 HTTP 响应消息获取状态码。
//             * 例如，就以下状态行来说：
//             * HTTP/1.0 200 OK
//             * HTTP/1.0 401 Unauthorized
//             * 将分别返回 200 和 401。
//             * 如果无法从响应中识别任何代码（即响应不是有效的 HTTP），则返回 -1。
//             *
//             * 返回 HTTP 状态码或 -1
//             */
//            int state = conn.getResponseCode();
//            return state == 200;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    /**
//     * 不区分key的大小写从Map中获取对应value
//     *
//     * @param str
//     * @param map
//     * @return
//     */
//    public static String getSpecKey4Value(String str, Map<String, String> map) {
//        if (TextUtils.isEmpty(str) || map == null) {
//            return null;
//        }
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            if (str.trim().equalsIgnoreCase(entry.getKey().trim())) {
//                return entry.getValue();
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 判断是否pixi系列的TMO定制机型
//     */
//    private final static String PIXI_TMO_MODEL1 = "5054N";
//    private final static String PIXI_TMO_MODEL2 = "5054W";
//    private final static String PIXI_TMO_PROJECT1 = "PIXI3-55_TMO";
//    private final static String PIXI_TMO_PROJECT2 = "PIXI3-55 TMO";

}


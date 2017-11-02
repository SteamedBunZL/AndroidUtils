package com.hawkclean.mig.commonframework.util;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;

import com.hawkclean.framework.log.NLog;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.util.Locale;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/7/11 13:38
 * @copyright TCL-MIG
 */
public class CommonUtil {

    public static final String SHARED_NAME = "phone_util";
    public static final String IMEI_SHARED_NAME = "imei";
    public static final String USER_ID_FILE_NAME = "userId";

    public static String getIMEI(Context context) {
        if (context == null)
            return "";
        SharedPreferences sp = context.getSharedPreferences(SHARED_NAME, Context.MODE_APPEND);
        String imei = sp.getString(IMEI_SHARED_NAME, "");
        if (!imei.equals("")){
            return imei;
        }
        //检查权限
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            return "";
        }

        final TelephonyManager tm = (TelephonyManager)context
                .getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        //存储imei号
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(IMEI_SHARED_NAME, imei);
        editor.apply();
        return imei;
    }

    public static String getLanguage(Context context){
       return Locale.getDefault().getLanguage();
    }

    public static String getConuntry(Context context){
        return Locale.getDefault().getCountry();
    }

    public static int getVersionCode(Context context) {
        ComponentName cn = new ComponentName(context, context.getClass());
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    cn.getPackageName(), 0);
            return info.versionCode;
        } catch (/*NameNotFoundException*/Exception e) {
            return -1;
        }
    }

    public static String getVersionName(Context context) {
        ComponentName cn = new ComponentName(context, context.getClass());
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    cn.getPackageName(), 0);
            return info.versionName;
        } catch (/*NameNotFoundException*/Exception e) {
            return "";
        }
    }

    public static String getVersionName() {
        return PublishVersionManager.getVersionName();
    }

    public static String getOsVersionCode(Context context){
        return String.valueOf(android.os.Build.VERSION.SDK_INT);
    }

    public static String getOsVersionName(Context context){
        return Build.VERSION.RELEASE;
    }


    public static String getChannelId() {
        return PublishVersionManager.getChannelId();
    }

    public static String getMetaData(Context context, String name) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        Object value = null;
        try {

            applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), packageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                value = applicationInfo.metaData.get(name);
            }

        } catch (Exception e) {
            NLog.printStackTrace(e);
            NLog.w("ContextUtils",
                    "Could not read the name(%s) in the manifest file.", name);
            return null;
        }

        return value == null ? null : value.toString();
    }

    public static String getScreenSize(Context context){
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        int width = size.x;
        int height = size.y;
        return String.valueOf(width) + "*" + String.valueOf(height);
    }

    public static String getNetworkType(Context context, String defaultType) {
        String networkType = "";
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null) {
                    int type = info.getType();
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        networkType = "wifi";
                    } else {
                        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        if (tm != null) {
                            switch (tm.getNetworkType()) {
                                case TelephonyManager.NETWORK_TYPE_1xRTT:
                                case TelephonyManager.NETWORK_TYPE_EDGE:
                                case TelephonyManager.NETWORK_TYPE_GPRS:
                                case TelephonyManager.NETWORK_TYPE_CDMA:
                                case TelephonyManager.NETWORK_TYPE_IDEN:
                                    networkType = "2g";
                                    break;
                                case TelephonyManager.NETWORK_TYPE_EHRPD:
                                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                                case TelephonyManager.NETWORK_TYPE_HSPAP:
                                case TelephonyManager.NETWORK_TYPE_HSPA:
                                case TelephonyManager.NETWORK_TYPE_UMTS:
                                    networkType = "3g";
                                    break;
                                case TelephonyManager.NETWORK_TYPE_HSDPA:
                                case TelephonyManager.NETWORK_TYPE_HSUPA:
                                    networkType = "3.5g";
                                    break;
                                case TelephonyManager.NETWORK_TYPE_LTE:
                                    networkType = "4g";
                                    break;
                                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        return TextUtils.isEmpty(networkType) ? defaultType : networkType;
    }

    public static String getPkgName() {
        Context context = SpaceApplication.getInstance().getContext();
        ComponentName cn = new ComponentName(context, context.getClass());
        return cn.getPackageName();
    }
}

package com.tcl.security.virusengine.utils;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import com.tcl.security.cloudengine.NetworkUtils;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * DeviceInfo 设备信息类
 *
 * 1.通过{@link DeviceInfo#get(Context)}方法获取信息集合{@link Map}
 * 2.通过{@link Map#get(Object)}方法 传入tag作为key获取所需的信息
 */
public class DeviceInfo {
    private static final int sdk = Build.VERSION.SDK_INT;
    private static String sdkStr = null;
    private static final String brand = Build.BRAND;
    private static final String model = Build.MODEL;
    private static String finger = Build.FINGERPRINT;
    private static String imei = null;
    private static String language = null;
    private static String country = null;
    private static int versionCode = 0;
    private static String versionCodeStr = null;
    private static String versionName = null;
    private static int networkType;
    private static String networkTypeStr = null;
    private static String androidId = null;
    private static int androidSdk = 0;
    private static String avengineName = null;
    private static String appName = null;

    private static final String UNKNOWN = "unknown";

    /**ANDROIDSDK*/
    public static final String sdkTag = "androidSdk";

    /**BRAND*/
    public static final String brandTag = "brand";

    /**MODLE*/
    public static final String modelTag = "model";

    public static final String fingerTag = "fingerPrint";

    /**IMEI*/
    public static final String imeiTag = "imei";

    /**LANGUAGE*/
    public static final String langTag = "language";

    /**COUNTRY*/
    public static final String countryTag = "country";

    /**VERSIONCODE*/
    public static final String versionCodeTag = "versionCode";

    /**VERSIONNAME*/
    public static final String versionNameTag = "versionName";

    /**NETWORK*/
    public static final String networkTag = "network";

    /**ANDROIDID*/
    public static final String androidIdTag = "androidId";

    /**AVENGINENAME*/
    public static final String avengineNameTag = "avengineName";

    /**APPNAME*/
    public static final String appNameTag = "appName";



    private static String getImei(Context c) {
//        TelephonyManager manager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
//        if (manager != null) {
//            String imei = null;
//            try {
//                imei = manager.getDeviceId();
//            } catch (Throwable e) {
//                if (sdk >= 23) {
//                    return "deny";
//                }
//            }
//            return TextUtils.isEmpty(imei) ? UNKNOWN : imei;
//        }
        return "deny";
    }

    private static int getVersionCode(Context c) {
        PackageInfo pkg = getPkgInfo(c);
        if (pkg != null) {
            return pkg.versionCode;
        }
        return 0;
    }

    private static String getVersionName(Context c) {
        PackageInfo pkg = getPkgInfo(c);
        if (pkg != null) {
            return pkg.versionName;
        }
        return UNKNOWN;
    }

    private static String getAppName(Context c){
        String appName = null;
        try {
            PackageManager pm = c.getApplicationContext().getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(c.getPackageName(),0);
            appName = (String) pm.getApplicationLabel(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appName;
    }

    private static PackageInfo getPkgInfo(Context c) {
        try {
            return c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getLang() {
        Locale local = Locale.getDefault();
        String language = local.getLanguage();
        if (language != null) {
            return language.toLowerCase();
        }
        return UNKNOWN;
    }

    private static String getCountry() {
        Locale local = Locale.getDefault();
        String country = local.getCountry();
        if (country != null) {
            return country.toLowerCase();
        }
        return UNKNOWN;
    }

    private static String getAndroidId(Context c) {
        String id = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (id == null) {
            id = UNKNOWN;
        }
        return id;
    }


    private static void getHelper(Context c) {
        if (avengineName ==null)
            avengineName = "McAfee";


        if (imei == null) {
            imei = getImei(c);
        }
        if (language == null) {
            language = getLang();
        }
        if (country == null) {
            country = getCountry();
        }
        if (versionCode == 0) {
            versionCode = getVersionCode(c);
        }
        if (versionName == null) {
            versionName = getVersionName(c);
        }
        if (androidId == null) {
            androidId = getAndroidId(c);
        }
        networkType = NetworkUtils.getConnectionType(c);
        if (networkTypeStr == null) {
            networkTypeStr = String.valueOf(networkType);
        }
        if (sdkStr == null) {
            sdkStr = String.valueOf(sdk);
        }
        if (versionCodeStr == null) {
            versionCodeStr = String.valueOf(versionCode);
        }

        if (appName == null){
            appName = getAppName(c);
        }
        VirusLog.d(" print %s",print());
    }

    public static Map<String, String> get(Context c) {
        getHelper(c);
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(brandTag, brand);
        map.put(modelTag, model);
        map.put(sdkTag, sdkStr);
        map.put(fingerTag, finger);
        map.put(imeiTag, imei);
        map.put(androidIdTag, androidId);
        map.put(langTag, language);
        map.put(countryTag, country);
        map.put(versionCodeTag, versionCodeStr);
        map.put(versionNameTag, versionName);
        map.put(networkTag, networkTypeStr);
        map.put(avengineNameTag,avengineName);
        map.put(appNameTag,appName);
        return map;
    }

    private static String print() {
        StringBuilder builder = new StringBuilder(512);
        builder.append("sdk:");
        builder.append(sdk);
        builder.append("|");
        builder.append("imei:");
        builder.append(imei);
        builder.append("|");
        builder.append("brand:");
        builder.append(brand);
        builder.append("|");
        builder.append("model:");
        builder.append(model);
        builder.append("|");
        builder.append("lang:");
        builder.append(language);
        builder.append("|");
        builder.append("country:");
        builder.append(country);
        builder.append("|");
        builder.append("version code:");
        builder.append(versionCode);
        builder.append("|");
        builder.append("version name:");
        builder.append(versionName);
        builder.append("|");
        builder.append("network:");
        builder.append(networkType);
        builder.append("|");
        builder.append("androidId:");
        builder.append(androidId);
        builder.append("|");
        builder.append("fingerPrint:");
        builder.append(finger);

        return builder.toString();
    }
}

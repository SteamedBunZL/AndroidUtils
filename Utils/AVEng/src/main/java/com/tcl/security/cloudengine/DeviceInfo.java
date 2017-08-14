package com.tcl.security.cloudengine;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Locale;

public class DeviceInfo {
    private static final String TAG = ProjectEnv.bDebug ? "DeviceInfo" : DeviceInfo.class.getSimpleName();
    private static final int sdk = Build.VERSION.SDK_INT;
    private static final String brand = Build.BRAND;
    private static final String model = Build.MODEL;
    private static String finger = Build.FINGERPRINT;
    private static String language;
    private static String country;
    private static int networkType;
    private static String androidId;

    private static final String UNKNOWN = "unknown";

    private static String sdkTag = "AndroidSDK";
    private static String brandTag = "Brand";
    private static String modelTag = "Model";
    private static String langTag = "Language";
    private static String countryTag = "Country";
    private static String networkTag = "Network";
    private static String androidIdTag = "AndroidID";


    private static String getImei(Context c) {
        TelephonyManager manager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            String imei = null;
            try {
                imei = manager.getDeviceId();
            } catch (Throwable e) {
                if (sdk >= 23) {
                    return "deny";
                }
            }
            return TextUtils.isEmpty(imei) ? UNKNOWN : imei;
        }
        return UNKNOWN;
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

    private static PackageInfo getPkgInfo(Context c) {
        try {
            PackageInfo pkg = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            return pkg;
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "pkg info:");
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getLang() {
        Locale local = Locale.getDefault();
        String language = local.getLanguage();
        if (language != null) {
            return language.toLowerCase();
        }
        return UNKNOWN;
    }

    public static String getCountry() {
        Locale local = Locale.getDefault();
        String country = local.getCountry();
        if (country != null) {
            return country.toLowerCase();
        }
        return UNKNOWN;
    }

    public static String getAndroidId(Context c) {
        String id = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (id == null) {
            id = UNKNOWN;
        }
        return id;
    }

    private static void getHelper(Context c) {
        language = getLang();
        country = getCountry();
        if (androidId == null) {
            androidId = getAndroidId(c);
        }
        networkType = NetworkUtils.getConnectionType(c);
    }

    public static void write(Context c, JsonWriter writer) throws IOException {
        getHelper(c);
        writer.name(brandTag).value(brand);
        writer.name(modelTag).value(model);
        writer.name(sdkTag).value(sdk);
        writer.name(androidIdTag).value(androidId);
        writer.name(langTag).value(language);
        writer.name(countryTag).value(country);
        writer.name(networkTag).value(networkType);
    }

    private static String print() {
        StringBuilder builder = new StringBuilder(512);
        builder.append("sdk:");
        builder.append(sdk);
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

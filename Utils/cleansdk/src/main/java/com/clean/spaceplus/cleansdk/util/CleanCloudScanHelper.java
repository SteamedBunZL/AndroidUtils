package com.clean.spaceplus.cleansdk.util;

import android.content.Context;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudManager;

import java.util.Locale;


/**
 * @author dongdong.huang
 * @Description: 扫描帮助类
 * @date 2016/4/23 14:49
 * @copyright TCL-MIG
 */
public class CleanCloudScanHelper {
    public static String getCurrentLanguage() {
        return getCurrentLanguage(CleanCloudManager.getApplicationContext());
    }

    public static String getCurrentLanguage(Context context) {
        if(context != null){
            Locale locale = context.getResources().getConfiguration().locale;
            String lang = locale.getLanguage();
            String country = locale.getCountry();

            if(!TextUtils.isEmpty(lang)){
                if(!TextUtils.isEmpty(country)){
                    lang += "-" + country;
                }

                return lang;
            }
        }

        return "en";
    }

    private static final int CLEAN_CLOUD_DETECTED_RESULT_UPLOAD_MAX_RATE = 10000;
    public static boolean isCleanCloudScanReport2Enable() {

        boolean result = true;
//        int rateCfg = CloudCfgDataWrapper.getCloudCfgIntValue(
//                CloudCfgKey.CLEAN_CLOUD_SWITCH,
//                CloudCfgKey.CLEAN_CLOUD_DETECTED_RESULT_UPLOAD_RATE,
//                CLEAN_CLOUD_DETECTED_RESULT_UPLOAD_MAX_RATE);
//
//        if (rateCfg < CLEAN_CLOUD_DETECTED_RESULT_UPLOAD_MAX_RATE) {
//            int currentRateCfg = (int)Commons.random() * CLEAN_CLOUD_DETECTED_RESULT_UPLOAD_MAX_RATE;
//            if (currentRateCfg <= rateCfg) {
//                result = false;
//            }
//        }
        return result;
    }
}

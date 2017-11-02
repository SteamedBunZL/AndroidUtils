package com.clean.spaceplus.cleansdk.util;

import android.content.Context;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.config.CommonConfigSharedPref;
import com.clean.spaceplus.cleansdk.util.bean.LanguageCountry;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.util.Locale;

/**
 * @author Jerry
 * @Description:
 * @date 2016/4/23 10:29
 * @copyright TCL-MIG
 */
public class LanguageUtil {

    public static String getCurrentLanguage() {
        return getCurrentLanguage(SpaceApplication.getInstance().getContext());
    }

    /**
     * 获取当前语言
     * @param context
     * @return
     */
    public static String getCurrentLanguage(Context context) {
        LanguageCountry language = CommonConfigSharedPref.getInstanse(context).getLanguageSelected(context);
        String lang = language.getLanguage();
        String country = language.getCountry();

        if(lang != null){
            lang = lang.toLowerCase(Locale.ENGLISH);
        }
        if(country != null){
            lang = (TextUtils.isEmpty(lang) ? "" : lang) + (TextUtils.isEmpty(country) ? "":("-"+country.toLowerCase(Locale.ENGLISH)));
        }

        if(TextUtils.isEmpty(lang)){
            lang = "en";
        }
        return lang;
    }

    public static String getMIGLanguageMark(Context context){
        LanguageCountry language = CommonConfigSharedPref.getInstanse(context).getLanguageSelected(context);
        String lang = language.getLanguage();
        String country = language.getCountry();

        if(lang != null){
            lang = lang.toLowerCase(Locale.ENGLISH);
        }
        if(country != null){
            lang = (TextUtils.isEmpty(lang) ? "" : lang) + (TextUtils.isEmpty(country) ? "":("-r"+country.toUpperCase(Locale.ENGLISH)));
        }

        if(TextUtils.isEmpty(lang)){
            lang = "en";
        }
        return lang;
    }

    public static String getLanguage(Context context){
        LanguageCountry language = CommonConfigSharedPref.getInstanse(context).getLanguageSelected(context);
        return language.getLanguage();
    }

    public static String getCountry(Context context){
        LanguageCountry language = CommonConfigSharedPref.getInstanse(context).getLanguageSelected(context);
        return language.getCountry();
    }
}

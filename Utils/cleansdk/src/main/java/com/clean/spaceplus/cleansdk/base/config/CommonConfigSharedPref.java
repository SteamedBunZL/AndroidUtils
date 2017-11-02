package com.clean.spaceplus.cleansdk.base.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.clean.spaceplus.cleansdk.util.SharePreferenceUtil;
import com.clean.spaceplus.cleansdk.util.bean.LanguageCountry;

import space.network.util.RuntimeCheck;

/**
 * @author Jerry
 * @Description:保存一些公共的配置信息在SharedPreference里面
 * @date 2016/4/23 10:57
 * @copyright TCL-MIG
 */
public class CommonConfigSharedPref {

    private static Context context = null;
    private SharedPreferences mshardPreferences = null;
    private String mstrSharedPreferenceName = null;
    private static final String LANGUAGE_SELECTED = "language_selected";
    private static final String COUNTRY_SELECTED = "country_selected";

    private CommonConfigSharedPref(Context context) {
        mstrSharedPreferenceName = new String(context.getPackageName() + "_preferences");
        mshardPreferences = context.getSharedPreferences(mstrSharedPreferenceName, Context.MODE_PRIVATE);
    }


    private static class InnerConfigManager {
        private static final CommonConfigSharedPref instanse = new CommonConfigSharedPref(context);
    }

    public static CommonConfigSharedPref getInstanse(Context context) {
        CommonConfigSharedPref.context = context.getApplicationContext(); // context
        CommonConfigSharedPref cm = InnerConfigManager.instanse;
        return cm;
    }

    public long getLibSoSize2(String soPath) {
        long soSize = getLongValue(soPath + "_2", 0);
        return soSize;
    }

    public long getLongValue(String key, long defValue) {
        if (RuntimeCheck.IsServiceProcess()) {
            return getSharedPreference().getLong(key, defValue);
        }
        else {
            //nilo provider今后开发，先去除
            return 0;
           // return ConfigProvider.getLongValue(key, defValue);
        }
    }

    public String getLibSoHeadMd5(String soPath) {
        return getStringValue(soPath + "_hm5", "");
    }

    public void setLibSoSize2(String soPath, long soSize) {
        setLongValue(soPath + "_2", soSize);
    }

    private static final String SO_VERSION = "SoVersion_new";
    public void setSoVersion(String version) {
        if (null == version || 0 == version.length()) {
            return;
        }
        setStringValue(SO_VERSION, version);
    }

    public String getSoVersion() {
        return getStringValue(SO_VERSION, "");
    }

    public void setLongValue(String key, long value) {
        if (RuntimeCheck.IsServiceProcess()) {
            SharedPreferences.Editor editor = getSharedPreference().edit();
            editor.putLong(key, value);
            SharePreferenceUtil.applyToEditor(editor);
        } else {
            //nilo  Provider延后开发 先去除
            // ConfigProvider.setLongValue(key, value);
        }
    }
    public void setLibSoHeadMd5(String soPath, String md5) {
        setStringValue(soPath + "_hm5", null != md5 ? md5 : "");
    }

    public void setStringValue(String key, String value) {
        if (RuntimeCheck.IsServiceProcess()) {
            SharedPreferences.Editor editor = getSharedPreference().edit();
            editor.putString(key, value);
            SharePreferenceUtil.applyToEditor(editor);
        } else {
            //nilo  Provider延后开发 先去除
            //ConfigProvider.setStringValue(key, value);
        }
    }
    /**
     * 获取保存在本地SharedPreference文件中的语言和国家信息
     * 如果有保存过 就用保存的，如果没有保存过，就按系统默认的语言
     * @param context
     * @return
     */
    public LanguageCountry getLanguageSelected(Context context) {
        String language = getStringValue(LANGUAGE_SELECTED, LanguageCountry.LANGUAGE_OPTION_DEFAULT);
        String country = getStringValue(COUNTRY_SELECTED, LanguageCountry.COUNTRY_OPTION_DEFAULT);
        if (language.equalsIgnoreCase(LanguageCountry.LANGUAGE_OPTION_DEFAULT)) {
            language = context.getResources().getConfiguration().locale.getLanguage();
        }
        if (country.equalsIgnoreCase(LanguageCountry.COUNTRY_OPTION_DEFAULT)) {
            country = context.getResources().getConfiguration().locale.getCountry();
        }
        return new LanguageCountry(language, country);
    }

    public String getStringValue(String key, String defValue) {
        return getSharedPreference().getString(key, defValue);
    }

    private SharedPreferences getSharedPreference() {
        return mshardPreferences;
    }
}

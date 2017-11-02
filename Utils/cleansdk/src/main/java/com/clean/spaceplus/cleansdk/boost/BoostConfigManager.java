package com.clean.spaceplus.cleansdk.boost;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;

/**
 * @author zengtao.kuang
 * @Description:加速相关的配置
 * @date 2016/4/6 20:37
 * @copyright TCL-MIG
 */
public class BoostConfigManager {

    private String mSharedPreferenceName = null;
    private SharedPreferences mShardPreferences = null;
    private static BoostConfigManager sInstanse;

    public synchronized static BoostConfigManager getInstanse(Context context) {
        if(sInstanse==null){
            sInstanse = new BoostConfigManager(context);
        }
        return sInstanse;
    }

    private BoostConfigManager(Context context) {
        mSharedPreferenceName = new String("boost_pref");
        mShardPreferences = SpaceApplication.getInstance().getContext().getSharedPreferences(mSharedPreferenceName, Context.MODE_PRIVATE);
    }

    public long getLongValue(String key, long defValue) {
        return getSharedPreference().getLong(key, defValue);
    }

    public void setLongValue(String key, long value) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putLong(key, value);
        applyToEditor(editor);
    }

    public int getIntValue(String key, int defValue) {
        return getSharedPreference().getInt(key, defValue);
    }

    public void setIntValue(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putInt(key, value);
        applyToEditor(editor);
    }

    public boolean getBooleanValue(String key, boolean defValue) {
        return getSharedPreference().getBoolean(key, defValue);
    }

    public void setBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putBoolean(key, value);
        applyToEditor(editor);
    }

    private SharedPreferences getSharedPreference() {
        return mShardPreferences;
    }

    @SuppressLint("NewApi")
    public static void applyToEditor(SharedPreferences.Editor editor) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public static void commitToEditor(SharedPreferences.Editor editor) {
        editor.commit();
    }
}

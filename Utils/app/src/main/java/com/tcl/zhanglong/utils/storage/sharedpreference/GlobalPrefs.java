package com.tcl.zhanglong.utils.storage.sharedpreference;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;

import com.tcl.zhanglong.utils.UtilApplication;

/**
 * 全局的首选项,可跨进程使用
 * Created by Steve on 16/10/25.
 */

public class GlobalPrefs {

    private static volatile  GlobalPrefs mPrefs;

    public static GlobalPrefs getInstance(){
        if (mPrefs==null){
            synchronized (GlobalPrefs.class){
                if (mPrefs==null)
                    mPrefs = new GlobalPrefs();
            }
        }
        return mPrefs;
    }

    private ContentResolver mContentResolver;

    private GlobalPrefs(){
        mContentResolver = UtilApplication.mApplication.getContentResolver();
    }

    private String queryProvider(String key){
        try {
            Uri uri = Uri.parse(GlobalConfigProvider.URI_CONFIG + "/" + key);
            return mContentResolver.getType(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean execUpdate(String key,String value){
        try {
            ContentValues cv = new ContentValues();
            cv.put(GlobalConfigProvider.CONTENT_KEY,key);
            cv.put(GlobalConfigProvider.CONTENT_VALUE,value);
            mContentResolver.insert(Uri.parse(GlobalConfigProvider.URI_CONFIG),cv);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateValue(String key,String value){
        if (!execUpdate(key,value)){
            //这里应该有策略,再试一次?
        }
    }

    public void putBooleanValue(String key,boolean value){
        updateValue(key,String.valueOf(value));
    }

    public boolean getBooleanValue(String key,boolean defalutValue){
        String res = queryProvider(key);
        if (TextUtils.isEmpty(res))
            return defalutValue;
        try {
            return Boolean.parseBoolean(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defalutValue;
    }
}

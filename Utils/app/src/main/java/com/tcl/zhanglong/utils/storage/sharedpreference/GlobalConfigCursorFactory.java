package com.tcl.zhanglong.utils.storage.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.ArrayMap;

/**
 * Created by Steve on 16/10/25.
 */

public class GlobalConfigCursorFactory {

    private static volatile GlobalConfigCursorFactory mGlobalConfigCursorFactory;

    public static GlobalConfigCursorFactory getInstance(Context context){
        if (mGlobalConfigCursorFactory==null){
            synchronized (GlobalConfigCursorFactory.class){
                if (mGlobalConfigCursorFactory==null){
                    mGlobalConfigCursorFactory = new GlobalConfigCursorFactory(context);
                }
            }
        }
        return mGlobalConfigCursorFactory;
    }

    private GlobalConfigCursorFactory(Context context){
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SharedPreferences mPref;

    private ArrayMap<String,String> mCache = new ArrayMap<>();


    synchronized GlobalConfigCursor queryData(String[] keys){
        for(int i = 0;i<keys.length;i++){
            String key = keys[i];
            if (!mCache.containsKey(key)){
                String value = mPref.getString(key,null);
                mCache.put(key,value);
            }
        }
        return new GlobalConfigCursor(keys,mCache);
    }


    synchronized void setData(String key,String value){
        mCache.put(key,value);
        SharedPreferences.Editor edit = mPref.edit();
        edit.putString(key,value);
        edit.commit();
    }

    synchronized String getData(String key){
        String value;
        if (mCache.containsKey(key))
            value = mCache.get(key);
        else{
            value = mPref.getString(key,null);
            mCache.put(key,value);
        }
        return value;
    }
}

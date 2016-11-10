package com.tcl.security.virusengine.cache.database.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.tcl.security.virusengine.cache.database.DatabaseCache;
import com.tcl.security.virusengine.cache.database.Provider;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steve on 2016/5/6.
 */
public class DatabaseCacheImpl implements DatabaseCache {

    private final Context mContext;

    private final String[] selections = new String[]{Provider.VirusCacheColumns.COLUMN_PACKAGENAME,
            Provider.VirusCacheColumns.COLUMN_APP_NAME,
            Provider.VirusCacheColumns.COLUMN_APPLICATION_VERSION,
            Provider.VirusCacheColumns.COLUMN_CACHE_KEY,
            Provider.VirusCacheColumns.COLUMN_SCAN_STATE,
            Provider.VirusCacheColumns.COLUMN_TTL,
            Provider.VirusCacheColumns.COLUMN_VIRUS_LIB_VERSION,
            Provider.VirusCacheColumns.COLUMN_VIRUS_NAME,
            Provider.VirusCacheColumns.COLUMN_FROM,
            Provider.VirusCacheColumns.COLUM_TYPE,
            Provider.VirusCacheColumns.COLUM_RISK_LEVEL,
            Provider.VirusCacheColumns.COLUM_TCL_HASH,
            Provider.VirusCacheColumns.COLUM_SUGGEST,
            Provider.VirusCacheColumns.COLUM_TCL_CLOUD_RESULT,
            Provider.VirusCacheColumns.COLUM_AVENGINE_CLOUD_RESULT,
            Provider.VirusCacheColumns.COLUM_CLOUD_CACHE_TIME
    };

    public DatabaseCacheImpl(Context context) {
        mContext = context;
    }


    @Override
    public synchronized CacheEntry get(String key) {
        Cursor cursor = null;
        CacheEntry entry = null;
        try {
            cursor = mContext.getContentResolver().query(Provider.VirusCacheColumns.CONTENT_URI, selections, Provider.VirusCacheColumns.COLUMN_CACHE_KEY + "=?", new String[]{key}, null);
            if (cursor != null && cursor.moveToFirst()) {
                entry = new CacheEntry();
                entry.packageName = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_PACKAGENAME));
                entry.appName = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_APP_NAME));
                entry.applicationVersion = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_APPLICATION_VERSION));
                entry.cacheKey = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_CACHE_KEY));
                entry.scanState = cursor.getInt(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_SCAN_STATE));
                entry.ttl = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_TTL));
                entry.virusLibVersion = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_VIRUS_LIB_VERSION));
                entry.virusName = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_VIRUS_NAME));
                entry.from = cursor.getInt(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_FROM));
                entry.type = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_TYPE));
                entry.risk_level = cursor.getInt(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_RISK_LEVEL));
                entry.tclHash = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_TCL_HASH));
                entry.suggest = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_SUGGEST));
                entry.tcl_cloud_result = cursor.getInt(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_TCL_CLOUD_RESULT));
                entry.avengine_cloud_result = cursor.getInt(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_AVENGINE_CLOUD_RESULT));
                entry.cloud_cache_time = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_CLOUD_CACHE_TIME));
//            VirusLog.w("query success CacheEntry %s",entry.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return entry;
    }

    @Override
    public synchronized CacheEntry put(String key, CacheEntry entry) {
        try {
            ContentValues values = buildContentValues(entry);
            //如果有key就更新，如果没key就插入
            int count = mContext.getContentResolver().update(Provider.VirusCacheColumns.CONTENT_URI, values, Provider.VirusCacheColumns.COLUMN_CACHE_KEY + "=?", new String[]{key});
            if (count > 0) {
    //            VirusLog.w("update count = %d",count);
            } else {
                Uri uri = mContext.getContentResolver().insert(Provider.VirusCacheColumns.CONTENT_URI, values);
                String lastPath = uri != null ? uri.getLastPathSegment() : null;
                if (TextUtils.isEmpty(lastPath)) {
                    VirusLog.w("insert failure");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entry;
    }

    @Override
    public CacheEntry remove(String key) {
        return null;
    }

    /**
     * Build insert or update ContentValues.
     *
     */
    private ContentValues buildContentValues(CacheEntry entry) {
        ContentValues values = new ContentValues();
        try {
            values.put(Provider.VirusCacheColumns.COLUMN_PACKAGENAME, entry.packageName);
            values.put(Provider.VirusCacheColumns.COLUMN_CACHE_KEY, entry.cacheKey);
            values.put(Provider.VirusCacheColumns.COLUMN_APP_NAME, entry.appName);
            values.put(Provider.VirusCacheColumns.COLUMN_APPLICATION_VERSION, entry.applicationVersion);
            values.put(Provider.VirusCacheColumns.COLUMN_VIRUS_LIB_VERSION, entry.virusLibVersion);
            values.put(Provider.VirusCacheColumns.COLUMN_SCAN_STATE, entry.scanState);
            values.put(Provider.VirusCacheColumns.COLUMN_TTL, entry.ttl);
            values.put(Provider.VirusCacheColumns.COLUMN_VIRUS_NAME, entry.virusName);
            values.put(Provider.VirusCacheColumns.COLUMN_FROM, entry.from);
            values.put(Provider.VirusCacheColumns.COLUM_TYPE,entry.type);
            values.put(Provider.VirusCacheColumns.COLUM_RISK_LEVEL,entry.risk_level);
            values.put(Provider.VirusCacheColumns.COLUM_TCL_HASH,entry.tclHash);
            values.put(Provider.VirusCacheColumns.COLUM_SUGGEST,entry.suggest);
            values.put(Provider.VirusCacheColumns.COLUM_TCL_CLOUD_RESULT,entry.tcl_cloud_result);
            values.put(Provider.VirusCacheColumns.COLUM_AVENGINE_CLOUD_RESULT,entry.avengine_cloud_result);
            values.put(Provider.VirusCacheColumns.COLUM_CLOUD_CACHE_TIME,entry.cloud_cache_time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    @Override
    public synchronized List<CacheEntry> getAll() {
        List<CacheEntry> list = new LinkedList<>();
        Cursor cursor = null;
        CacheEntry entry;
        try {
            cursor = mContext.getContentResolver().query(Provider.VirusCacheColumns.CONTENT_URI, selections, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                entry = new CacheEntry();
                entry.packageName = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_PACKAGENAME));
                entry.appName = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_APP_NAME));
                entry.applicationVersion = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_APPLICATION_VERSION));
                entry.cacheKey = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_CACHE_KEY));
                entry.scanState = cursor.getInt(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_SCAN_STATE));
                entry.ttl = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_TTL));
                entry.virusLibVersion = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_VIRUS_LIB_VERSION));
                entry.virusName = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_VIRUS_NAME));
                entry.from = cursor.getInt(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUMN_FROM));
                entry.type = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_TYPE));
                entry.risk_level = cursor.getInt(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_RISK_LEVEL));
                entry.tclHash = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_TCL_HASH));
                entry.suggest = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_SUGGEST));
                entry.tcl_cloud_result = cursor.getInt(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_TCL_CLOUD_RESULT));
                entry.avengine_cloud_result = cursor.getInt(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_AVENGINE_CLOUD_RESULT));
                entry.cloud_cache_time = cursor.getString(cursor.getColumnIndexOrThrow(Provider.VirusCacheColumns.COLUM_CLOUD_CACHE_TIME));
                list.add(entry);
            }
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }


}

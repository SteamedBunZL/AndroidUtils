package com.tcl.security.virusengine.cache.database.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tcl.security.virusengine.Constants;
import com.tcl.security.virusengine.cache.database.Provider;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 目前给UI进程使用，用于查询隐私中的数据库
 * Created by Steve on 16/9/2.
 */
public class PrivacyDatabaseHelper {

    private static final String[] selections = new String[]{
            Provider.PrivacyCacheColumns.COLUMN_TYPE,
            Provider.PrivacyCacheColumns.COLUMN_CONTENT,
            Provider.PrivacyCacheColumns.COLUMN_TITLE

    };

    /**
     * 查询数据库中的历史记录
     * @param context
     * @return
     */
    public static Map<String,String> queryHistory(Context context){
        Cursor cursor = null;
        Map<String,String> map = new HashMap<>();
        try{
            cursor = context.getContentResolver().query(Provider.PrivacyCacheColumns.CONTENT_URI,selections,Provider.PrivacyCacheColumns.COLUMN_TYPE + "=?",new String[]{String.valueOf(Constants.PRIVACY_TYPE_HISTORY)},null);
            String content;
            String title;
            while (cursor!=null&&cursor.moveToNext()){
                content = cursor.getString(cursor.getColumnIndexOrThrow(Provider.PrivacyCacheColumns.COLUMN_CONTENT));
                title = cursor.getString(cursor.getColumnIndexOrThrow(Provider.PrivacyCacheColumns.COLUMN_TITLE));
                map.put(title,content);
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (cursor!=null) {
                cursor.close();
            }
        }
        return map;
    }

    /**
     * 查询搜索记录
     * @param context
     * @return
     */
    public static List<String> querySearch(Context context){
        Cursor cursor = null;
        List<String> list = new ArrayList<>();
        try{
            cursor = context.getContentResolver().query(Provider.PrivacyCacheColumns.CONTENT_URI,selections,Provider.PrivacyCacheColumns.COLUMN_TYPE + "=?",new String[]{String.valueOf(Constants.PRIVACY_TYPE_SEARCH)},null);
            String content;
            while (cursor!=null&&cursor.moveToNext()){
                content = cursor.getString(cursor.getColumnIndexOrThrow(Provider.PrivacyCacheColumns.COLUMN_CONTENT));
                list.add(content);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor!=null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 查询剪切记录
     * @param context
     * @return
     */
    public static List<String> queryClip(Context context){
        Cursor cursor = null;
        List<String> list = new ArrayList<>();
        try{
            cursor = context.getContentResolver().query(Provider.PrivacyCacheColumns.CONTENT_URI,selections,Provider.PrivacyCacheColumns.COLUMN_TYPE + "=?",new String[]{String.valueOf(Constants.PRIVACY_TYPE_CLIP)},null);
            String content;
            while (cursor!=null&&cursor.moveToNext()){
                content = cursor.getString(cursor.getColumnIndexOrThrow(Provider.PrivacyCacheColumns.COLUMN_CONTENT));
                list.add(content);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor!=null) {
                cursor.close();
            }
        }
        return list;
    }


    /**
     * 插入
     * @param context
     * @param dataMap
     */
    public static void insertHistoryData(Context context,Map<String,String> dataMap){
        try {
            Iterator iterator = dataMap.entrySet().iterator();
            ContentValues values;
            while(iterator.hasNext()){
                Map.Entry<String,String> entry = (Map.Entry<String, String>) iterator.next();
                values = new ContentValues();
                values.put(Provider.PrivacyCacheColumns.COLUMN_TYPE,Constants.PRIVACY_TYPE_HISTORY);
                values.put(Provider.PrivacyCacheColumns.COLUMN_TITLE,entry.getKey());
                values.put(Provider.PrivacyCacheColumns.COLUMN_CONTENT,entry.getValue());
                context.getContentResolver().insert(Provider.PrivacyCacheColumns.CONTENT_URI,values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入搜索记录数据
     * @param context
     * @param dataList
     */
    public static void insertSearchData(Context context,List<String> dataList){
        try{
            ContentValues value;
            for(String data:dataList){
                value = new ContentValues();
                value.put(Provider.PrivacyCacheColumns.COLUMN_TYPE,Constants.PRIVACY_TYPE_SEARCH);
                value.put(Provider.PrivacyCacheColumns.COLUMN_CONTENT,data);
                context.getContentResolver().insert(Provider.PrivacyCacheColumns.CONTENT_URI,value);
            }
        }catch (Exception e){
            VirusLog.e(e.getCause(),"insertSearchData");
            e.printStackTrace();
        }


    }

    /**
     * 插入剪切板数据
     * @param context
     * @param dataList
     */
    public static void insertClipData(Context context,List<String> dataList){
        try {
            ContentValues value;
            for(String data:dataList){
                value = new ContentValues();
                value.put(Provider.PrivacyCacheColumns.COLUMN_TYPE,Constants.PRIVACY_TYPE_CLIP);
                value.put(Provider.PrivacyCacheColumns.COLUMN_CONTENT,data);
                value.put(Provider.PrivacyCacheColumns.COLUMN_TITLE,"");
                context.getContentResolver().insert(Provider.PrivacyCacheColumns.CONTENT_URI,value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除历史记录
     * @param context
     */
    public static void deleteHistoryData(Context context){
        try {
            context.getContentResolver().delete(Provider.PrivacyCacheColumns.CONTENT_URI,Provider.PrivacyCacheColumns.COLUMN_TYPE + "=?",new String[]{String.valueOf(Constants.PRIVACY_TYPE_HISTORY)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除搜索记录
     * @param context
     */
    public static void deleteSearchData(Context context){
        try {
            context.getContentResolver().delete(Provider.PrivacyCacheColumns.CONTENT_URI,Provider.PrivacyCacheColumns.COLUMN_TYPE + "=?",new String[]{String.valueOf(Constants.PRIVACY_TYPE_SEARCH)});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除剪切记录
     * @param context
     */
    public static void deleteClipData(Context context){
        try {
            context.getContentResolver().delete(Provider.PrivacyCacheColumns.CONTENT_URI,Provider.PrivacyCacheColumns.COLUMN_TYPE + "=?",new String[]{String.valueOf(Constants.PRIVACY_TYPE_CLIP)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

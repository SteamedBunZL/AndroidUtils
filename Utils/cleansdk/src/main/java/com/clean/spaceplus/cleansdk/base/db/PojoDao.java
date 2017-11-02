package com.clean.spaceplus.cleansdk.base.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.hawkclean.framework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author shunyou.huang
 * @Description:持久对象处理
 * @date 2016/4/22 14:23
 * @copyright TCL-MIG
 */

public class PojoDao<T> {
    private ContentResolver mContentResolver;
    protected String mBaseContentUri;
    private TableHelper<T> mTableHelper;

    //@SuppressWarnings("unchecked")
    public PojoDao(Context context, TableHelper<T> helper) {
        mContentResolver = context.getContentResolver();
		/*
		Class<T> clazz = (Class<T>) ((ParameterizedType) super.getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
		*/
        mTableHelper = helper;
        String uri  = mTableHelper.getProviderAuthority();
        mBaseContentUri = ContentResolver.SCHEME_CONTENT + "://" + uri + "/" + mTableHelper.getTableName();
    }

    protected Uri getContentUri(){
        return Uri.parse(mBaseContentUri);
    }

    public int insert(ContentValues values){
        Uri uri = mContentResolver.insert(getContentUri(), values);
        long id = ContentUris.parseId(uri);
        return (int) id;
    }

    public int insert(T t){
        ContentValues values = mTableHelper.getContentValues(t);
        return insert(values);
    }

    public int delete(String where, String[] selectionArgs){
        return mContentResolver.delete(getContentUri(), where, selectionArgs);
    }

    public  int update(T t, String where, String[] selectionArgs){
        ContentValues values = mTableHelper.getContentValues(t);
        return mContentResolver.update(getContentUri(), values, where, selectionArgs);
    }

    public int update(ContentValues values, String where, String[] selectionArgs){
        return mContentResolver.update(getContentUri(), values, where, selectionArgs);
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        return mContentResolver.query(getContentUri(), projection, selection, selectionArgs, sortOrder);
    }

    protected Collection<T> queryBulk(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor cursor = query(projection, selection, selectionArgs, sortOrder);
        if (cursor == null)
            return null;

        Collection<T> result = null;
        while(cursor.moveToNext()){
            T t = mTableHelper.parseCursor(cursor);
            if (t != null) {
                if (result == null)
                    result = new ArrayList<T>();

                result.add(t);
            }

        }
        cursor.close();
        return result;
    }

    protected T querySingle(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor cursor = query(projection, selection, selectionArgs, sortOrder);
        if (cursor == null)
            return null;

        T result = null;
        if(cursor.moveToNext()){
            result= mTableHelper.parseCursor(cursor);
        }
        cursor.close();
        return result;
    }

    protected int bulkInsert(ContentValues[] values){
        if (CollectionUtils.isEmpty(values))
            return 0;
        return mContentResolver.bulkInsert(getContentUri(), values);
    }

    protected int bulkInsert(List<T> datas){
        if (CollectionUtils.isEmpty(datas))
            return 0;

        int size = datas.size();
        ContentValues[] values = new ContentValues[size];
        for(int i = 0; i < size; i++)
        {
            values[i] = mTableHelper.getContentValues(datas.get(i));
        }

        return mContentResolver.bulkInsert(getContentUri(), values);
    }

    public static <K> String buildWhere(String key, K value){
        StringBuffer sb = new StringBuffer(key);
        sb.append("=").append(value).append("'");

        if (value instanceof String) {
            sb.append("'").append(value).append("'");
        }
        else
            sb.append(value);

        return sb.toString();
    }
}

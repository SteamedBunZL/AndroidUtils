package com.tcl.zhanglong.utils.storage.sharedpreference;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Steve on 16/10/25.
 */

public class GlobalConfigProvider extends ContentProvider{

    public static final String URI_CONFIG = "content://com.tcl.zhanglong.util.config";

    public static final String CONTENT_KEY = "config_key";

    public static final String CONTENT_VALUE = "config_value";

    private GlobalConfigCursorFactory mFactory;

    @Override
    public boolean onCreate() {
        mFactory = GlobalConfigCursorFactory.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        if (mFactory!=null)
            cursor = mFactory.queryData(projection);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        String key = uri.getLastPathSegment();
        String value = null;
        if (mFactory!=null)
            value = mFactory.getData(key);
        return value;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String key = (String) values.get(CONTENT_KEY);
        String value = (String) values.get(CONTENT_VALUE);
        if (mFactory!=null)
            mFactory.setData(key,value);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

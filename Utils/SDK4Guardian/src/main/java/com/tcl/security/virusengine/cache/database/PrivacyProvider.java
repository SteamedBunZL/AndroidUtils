package com.tcl.security.virusengine.cache.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by Steve on 16/9/2.
 */
public class PrivacyProvider extends ContentProvider{

    private static final HashMap<String,String> sprivacycachesProjectionMap;

    private static final int PRIVACYCACHES = 1;

    private static final int PRIVACYCACHES_ID = 2;

    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mOpenHelpter;


    @Override
    public boolean onCreate() {
        mOpenHelpter = DatabaseHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)){
            case PRIVACYCACHES:
                qb.setTables(Provider.PrivacyCacheColumns.TABLE_NAME);
                qb.setProjectionMap(sprivacycachesProjectionMap);
                break;
            case PRIVACYCACHES_ID:
                qb.setTables(Provider.PrivacyCacheColumns.TABLE_NAME);
                qb.setProjectionMap(sprivacycachesProjectionMap);
                qb.appendWhere(Provider.PrivacyCacheColumns.COLUMN_ID + "="+uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mOpenHelpter.getReadableDatabase();
        Cursor c = qb.query(db,projections,selection,selectionArgs,null,null,sortOrder);
        if (getContext().getContentResolver()!=null)
            c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)){
            case PRIVACYCACHES:
                return Provider.CONTENT_TYPE;
            case PRIVACYCACHES_ID:
                return Provider.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI "+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues!=null)
            values = new ContentValues(initialValues);
        else
            values = new ContentValues();

        String tableNames;
        String nullColumn = "";

        switch (sUriMatcher.match(uri)){
            case PRIVACYCACHES:
                tableNames = Provider.PrivacyCacheColumns.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI "+ uri);
        }

        SQLiteDatabase db = mOpenHelpter.getReadableDatabase();
        long rowId = db.insert(tableNames,nullColumn,values);

        if (rowId>0){
            Uri notifyUri = ContentUris.withAppendedId(uri,rowId);
            if (getContext().getContentResolver()!=null)
                getContext().getContentResolver().notifyChange(notifyUri,null);
            return notifyUri;
        }
        throw new SQLException("Failed to insert into " + uri);
    }



    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelpter.getReadableDatabase();
        int count;
        switch (sUriMatcher.match(uri)){
            case PRIVACYCACHES:
                count = db.delete(Provider.PrivacyCacheColumns.TABLE_NAME,selection,selectionArgs);
                break;
            case PRIVACYCACHES_ID:
                String privacycachedId = uri.getPathSegments().get(1);
                count = db.delete(Provider.PrivacyCacheColumns.TABLE_NAME, Provider.PrivacyCacheColumns.COLUMN_ID + "=" + privacycachedId
                        + (!TextUtils.isEmpty(selection)?" AND (" + selection + ')':""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI "+uri);
        }
        if (getContext().getContentResolver()!=null)
            getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelpter.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)){
            case PRIVACYCACHES:
                count = db.update(Provider.PrivacyCacheColumns.TABLE_NAME,values,selection,selectionArgs);
                break;
            case PRIVACYCACHES_ID:
                String notifyId = uri.getPathSegments().get(1);
                count = db.update(Provider.PrivacyCacheColumns.TABLE_NAME,values, Provider.PrivacyCacheColumns._ID + "=" + notifyId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI "+uri);
        }
        if (getContext().getContentResolver()!=null)
            getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }




    static{
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Provider.PrivacyCacheColumns.AUTHORITY,"privacycaches",PRIVACYCACHES);
        sUriMatcher.addURI(Provider.PrivacyCacheColumns.AUTHORITY,"privacycaches/#",PRIVACYCACHES_ID);

        sprivacycachesProjectionMap = new HashMap<>();
        sprivacycachesProjectionMap.put(Provider.PrivacyCacheColumns.COLUMN_ID, Provider.PrivacyCacheColumns.COLUMN_ID);
        sprivacycachesProjectionMap.put(Provider.PrivacyCacheColumns.COLUMN_TYPE, Provider.PrivacyCacheColumns.COLUMN_TYPE);
        sprivacycachesProjectionMap.put(Provider.PrivacyCacheColumns.COLUMN_CONTENT, Provider.PrivacyCacheColumns.COLUMN_CONTENT);
        sprivacycachesProjectionMap.put(Provider.PrivacyCacheColumns.COLUMN_TITLE, Provider.PrivacyCacheColumns.COLUMN_TITLE);
    }
}

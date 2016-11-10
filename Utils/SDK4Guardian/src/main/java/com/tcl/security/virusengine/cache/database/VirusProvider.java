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
 * Created by Steve on 2016/5/6.
 */
public class VirusProvider extends ContentProvider{

    private static final HashMap<String,String> sviruscachesProjectionMap;

    private static final int VIRUSCACHES = 1;

    private static final int VIRUSCACHES_ID = 2;

    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mOpenHelper;


    @Override
    public boolean onCreate() {
        mOpenHelper = DatabaseHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)){
            case VIRUSCACHES:
                qb.setTables(Provider.VirusCacheColumns.TABLE_NAME);
                qb.setProjectionMap(sviruscachesProjectionMap);
                break;
            case VIRUSCACHES_ID:
                qb.setTables(Provider.VirusCacheColumns.TABLE_NAME);
                qb.setProjectionMap(sviruscachesProjectionMap);
                qb.appendWhere(Provider.VirusCacheColumns.COLUMN_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        if (getContext().getContentResolver()!=null)
            c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)){
            case VIRUSCACHES:
                return Provider.CONTENT_TYPE;
            case VIRUSCACHES_ID:
                return Provider.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues!=null){
            values = new ContentValues(initialValues);
        }else{
            values = new ContentValues();
        }

        String tableName;
        String nullColumn = "";
        switch (sUriMatcher.match(uri)){
            case VIRUSCACHES:
                tableName = Provider.VirusCacheColumns.TABLE_NAME;
                // Make sure that the fields are all set
//                viruscachesFieldsSet(values);
                break;
            default:
                // Validate the requested uri
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        long rowId = db.insert(tableName,nullColumn,values);

        if (rowId>0){
            Uri notifyUri = ContentUris.withAppendedId(uri,rowId);
            if (getContext().getContentResolver()!=null)
                getContext().getContentResolver().notifyChange(notifyUri,null);
            return notifyUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        int count;
        switch (sUriMatcher.match(uri)){
            case VIRUSCACHES:
                count = db.delete(Provider.VirusCacheColumns.TABLE_NAME,selection,selectionArgs);
                break;

            case VIRUSCACHES_ID:
                String viruscacheId = uri.getPathSegments().get(1);
                count = db.delete(Provider.VirusCacheColumns.TABLE_NAME,Provider.VirusCacheColumns.COLUMN_ID + "=" + viruscacheId
                        + (!TextUtils.isEmpty(selection)?" AND (" + selection + ')':""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (getContext().getContentResolver()!=null)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case VIRUSCACHES:
                count = db.update(Provider.VirusCacheColumns.TABLE_NAME,values,selection,selectionArgs);
                break;

            case VIRUSCACHES_ID:
                String notifyId = uri.getPathSegments().get(1);
                count = db.update(Provider.VirusCacheColumns.TABLE_NAME,values,Provider.VirusCacheColumns._ID + "=" + notifyId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (getContext().getContentResolver()!=null)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    private void viruscachesFieldsSet(ContentValues values){

        if (!values.containsKey(Provider.VirusCacheColumns.COLUMN_PACKAGENAME)){
            values.put(Provider.VirusCacheColumns.COLUMN_PACKAGENAME,"");
        }

        if (!values.containsKey(Provider.VirusCacheColumns.COLUMN_APP_NAME)){
            values.put(Provider.VirusCacheColumns.COLUMN_APP_NAME,"");
        }

        if (!values.containsKey(Provider.VirusCacheColumns.COLUMN_APPLICATION_VERSION)){
            values.put(Provider.VirusCacheColumns.COLUMN_APPLICATION_VERSION,"");
        }

        if (!values.containsKey(Provider.VirusCacheColumns.COLUMN_CACHE_KEY)){
            values.put(Provider.VirusCacheColumns.COLUMN_CACHE_KEY,"");
        }

        if (!values.containsKey(Provider.VirusCacheColumns.COLUMN_SCAN_STATE)){
            values.put(Provider.VirusCacheColumns.COLUMN_SCAN_STATE,0);
        }

        if (!values.containsKey(Provider.VirusCacheColumns.COLUMN_VIRUS_LIB_VERSION)){
            values.put(Provider.VirusCacheColumns.COLUMN_VIRUS_LIB_VERSION,"");
        }

        if (!values.containsKey(Provider.VirusCacheColumns.COLUMN_TTL)){
            values.put(Provider.VirusCacheColumns.COLUMN_TTL,"");
        }

        if (!values.containsKey(Provider.VirusCacheColumns.COLUMN_VIRUS_NAME)){
            values.put(Provider.VirusCacheColumns.COLUMN_VIRUS_NAME,"");
        }

//        if (values.containsKey(Provider.VirusCacheColumns.COLUMN_FROM)==false){
//            values.put(Provider.VirusCacheColumns.COLUMN_FROM,0);
//        }

    }
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Provider.VirusCacheColumns.AUTHORITY,"viruscaches",VIRUSCACHES);
        sUriMatcher.addURI(Provider.VirusCacheColumns.AUTHORITY,"viruscaches/#",VIRUSCACHES_ID);

        sviruscachesProjectionMap = new HashMap<>();
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUMN_ID,Provider.VirusCacheColumns.COLUMN_ID);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUMN_CACHE_KEY,Provider.VirusCacheColumns.COLUMN_CACHE_KEY);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUMN_APP_NAME,Provider.VirusCacheColumns.COLUMN_APP_NAME);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUMN_APPLICATION_VERSION,Provider.VirusCacheColumns.COLUMN_APPLICATION_VERSION);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUMN_PACKAGENAME,Provider.VirusCacheColumns.COLUMN_PACKAGENAME);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUMN_SCAN_STATE,Provider.VirusCacheColumns.COLUMN_SCAN_STATE);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUMN_TTL,Provider.VirusCacheColumns.COLUMN_TTL);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUMN_VIRUS_LIB_VERSION,Provider.VirusCacheColumns.COLUMN_VIRUS_LIB_VERSION);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUMN_VIRUS_NAME,Provider.VirusCacheColumns.COLUMN_VIRUS_NAME);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUMN_FROM,Provider.VirusCacheColumns.COLUMN_FROM);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUM_TYPE,Provider.VirusCacheColumns.COLUM_TYPE);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUM_RISK_LEVEL,Provider.VirusCacheColumns.COLUM_RISK_LEVEL);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUM_TCL_HASH,Provider.VirusCacheColumns.COLUM_TCL_HASH);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUM_SUGGEST,Provider.VirusCacheColumns.COLUM_SUGGEST);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUM_TCL_CLOUD_RESULT,Provider.VirusCacheColumns.COLUM_TCL_CLOUD_RESULT);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUM_AVENGINE_CLOUD_RESULT,Provider.VirusCacheColumns.COLUM_AVENGINE_CLOUD_RESULT);
        sviruscachesProjectionMap.put(Provider.VirusCacheColumns.COLUM_CLOUD_CACHE_TIME, Provider.VirusCacheColumns.COLUM_CLOUD_CACHE_TIME);
    }

}

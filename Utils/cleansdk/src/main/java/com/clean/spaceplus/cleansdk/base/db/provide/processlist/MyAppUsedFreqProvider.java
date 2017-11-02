package com.clean.spaceplus.cleansdk.base.db.provide.processlist;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.clean.spaceplus.cleansdk.base.config.DBVersionConfigManager;
import com.clean.spaceplus.cleansdk.base.db.TableFactory;
import com.clean.spaceplus.cleansdk.base.db.app_used_freq.AppUsedInfoTable;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBFactory;
import com.clean.spaceplus.cleansdk.base.db.provide.BaseContentProvider;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/24 11:27
 * @copyright TCL-MIG
 */
public class MyAppUsedFreqProvider extends BaseContentProvider{

    public static final String AUTHORITIES = "com.clean.spaceplus.cleansdk.base.db.provide.processlist.MyAppUsedFreqProvider";


    private static final int MATCH_APPUSEDINFO = 1;

    static{
        MATCHER.addURI(AUTHORITIES, AppUsedInfoTable.TABLE_NAME, MATCH_APPUSEDINFO);
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c = null;
        switch (MATCHER.match(uri)){
            case MATCH_APPUSEDINFO:
                c = getDatabase().query(AppUsedInfoTable.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder,null);
                break;
        }
        return c;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = -1;
        switch (MATCHER.match(uri)){
            case MATCH_APPUSEDINFO:
                try{
                    id = getDatabase().replace(AppUsedInfoTable.TABLE_NAME,null,values);
                    getContext().getContentResolver().notifyChange(AppUsedInfoTable.URI,null);
                }catch (Exception e){

                }
                return ContentUris.withAppendedId(AppUsedInfoTable.URI,id);
        }

        return null;


    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (MATCHER.match(uri)){
            case MATCH_APPUSEDINFO:
                try{
                    count = getDatabase().update(AppUsedInfoTable.TABLE_NAME,values,selection,selectionArgs);
                    getContext().getContentResolver().notifyChange(AppUsedInfoTable.URI,null);
                }catch (Exception e){

                }
                return count;

        }

        return count;


    }

    @Override
    public String getDBName() {
        return DBVersionConfigManager.getInstance().getAppUsedFreqDBName();
    }

    @Override
    public int getDBVersion() {
        return DBVersionConfigManager.DEFAULT_DB_VERSION_NUM;
    }

    @Override
    public String getAuthorities() {
        return AUTHORITIES;
    }

    @Override
    public TableFactory getFactory() {
        //return AppUsedFreqFactory.createFactory(getContext());
        return BaseDBFactory.getTableFactory(getContext(),BaseDBFactory.TYPE_APP_USED_FREQ);
    }
}

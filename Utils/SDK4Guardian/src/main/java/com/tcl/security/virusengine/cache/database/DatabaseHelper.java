package com.tcl.security.virusengine.cache.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tcl.security.virusengine.utils.VirusLog;

/**
 * Created by Steve on 2016/5/6.
 */
class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "tcl_virus.db";

    private static final int DATABASE_VERSION = 4;

    private static volatile DatabaseHelper sInstance;

    public static DatabaseHelper getInstance(Context context){
        if (sInstance == null){
            synchronized (DatabaseHelper.class){
                if (sInstance==null){
                    sInstance = new DatabaseHelper(context) ;
                }
            }
        }
        return sInstance ;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        //Create virus_cache table
        db.execSQL(Provider.VirusCacheColumns.buildSQL().toString());

        //Create privacy_cache table
        db.execSQL(Provider.PrivacyCacheColumns.buildSQL().toString());
        //Create others tables

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Upgrade virus_cache
        VirusLog.d("DatabaseHelper  onUpgrade oldVersion %d , newVersion %d",oldVersion,newVersion);
        //数据库升级
        try {
            db.beginTransaction();

            db.execSQL("DROP TABLE IF EXISTS " + Provider.VirusCacheColumns.TABLE_NAME);

            db.execSQL(Provider.VirusCacheColumns.buildSQL().toString());

//            db.execSQL("DROP TABLE IF EXISTS " + Provider.PrivacyCacheColumns.TABLE_NAME);
//
//            db.execSQL(Provider.PrivacyCacheColumns.buildSQL().toString());

            db.setTransactionSuccessful();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}

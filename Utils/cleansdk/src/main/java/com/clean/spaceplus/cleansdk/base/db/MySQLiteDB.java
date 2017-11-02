package com.clean.spaceplus.cleansdk.base.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/25 19:07
 * @copyright TCL-MIG
 */
public interface MySQLiteDB {
    public static class MyDBData {
        public MyDbRefOpenHelper mDbOpenHelper;
        public SQLiteDatabase mDb;
    }

    public boolean initDb();

    public void unInitDb();

    public SQLiteDatabase getDatabase();

    public MyDBData getDatabaseData();

    public MyDBData getDatabaseAndAcquireReference();

    public void releaseReference(MyDBData data);

    public MyDbRefOpenHelper getOpenHelper(String dbName);

    public String getDefaultDbName();

    public String getDefaultDbFilePath();

    public String getBackupDbName();

    public String getBackupDbFilePath();
}

package com.clean.spaceplus.cleansdk.base.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/25 19:10
 * @copyright TCL-MIG
 */
public interface MyDbRefOpenHelper {
    public SQLiteDatabase myGetWritableDatabase();
    public SQLiteDatabase myGetReadableDatabase();
    public SQLiteDatabase getDatabase();
    public void myClose();
    public void acquireReference();
    public void releaseReference();
}

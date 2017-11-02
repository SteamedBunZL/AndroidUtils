package com.clean.spaceplus.cleansdk.base.db;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * @author shunyou.huang
 * @Description:
 * @date 2016/4/26 20:01
 * @copyright TCL-MIG
 */

public abstract class DatabaseProvider {

    /**
     * SQL数据查询
     *
     * @param sql
     * @param selectionArgs
     */
    public abstract Cursor rawQuery(String sql, String[] selectionArgs) ;

    /**
     * 数据插入
     *
     * @param table
     * @param nullColumnHack
     * @param values
     */
    public abstract long insert(String table, String nullColumnHack, ContentValues values);

    /**
     * 数据删除
     *
     * @param table
     * @param whereClause
     * @param whereArgs
     */
    public abstract int delete(String table, String whereClause, String[] whereArgs);

    /**
     * 数据更新
     *
     * @param
     * @param values
     * @param whereClause
     * @param whereArgs
     */
    public abstract int update(String table, ContentValues values, String whereClause, String[] whereArgs);


}

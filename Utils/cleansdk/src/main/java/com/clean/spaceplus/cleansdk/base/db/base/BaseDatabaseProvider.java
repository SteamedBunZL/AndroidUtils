package com.clean.spaceplus.cleansdk.base.db.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.clean.spaceplus.cleansdk.base.db.DatabaseProvider;
import com.clean.spaceplus.cleansdk.base.db.TDatabaseHelper;
import com.clean.spaceplus.cleansdk.base.db.TableFactory;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Analytics;
import com.hawkclean.framework.log.NLog;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/2 13:30
 * @copyright TCL-MIG
 */
public abstract class BaseDatabaseProvider extends DatabaseProvider {

    public TDatabaseHelper mHelper;

    public SQLiteDatabase getDatabase() {
        final TDatabaseHelper helper = mHelper;
        if (helper != null) {
            return helper.openDatabase();
        }
        return null;
    }

    /**
     * 之前的代码由于AtomicInteger处理的有问题会出现关闭不掉数据库的情况
     * 为了不大范围改变原有代码结构
     */
    public synchronized void closeDb() {
        long start = System.nanoTime();
        final TDatabaseHelper helper = mHelper;
        if (helper != null) {
            helper.closeMyDatabase();
        }
        Analytics.endDBUtil(start);
    }

    protected synchronized boolean onCreate(Context context, TableFactory factory) {
        if (factory == null) {
            return false;
        }
        if (mHelper == null) {
            mHelper = new TDatabaseHelper(context, factory);
            mHelper.openDatabase();
        }
        return true;
    }

    @Override
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        long start = System.nanoTime();
        final TDatabaseHelper helper = mHelper;
        SQLiteDatabase db = helper.openDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, selectionArgs);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } catch (Error e) {
        } finally {
            //helper.closeSafety();
        }
        Analytics.endDBUtil(start);
        return cursor;
    }

    @Override
    public long insert(String table, String nullColumnHack, ContentValues values) {
        long start = System.nanoTime();
        final TDatabaseHelper helper = mHelper;
        SQLiteDatabase db = helper.openDatabase();
        long id = -1;

        try {
            id = db.insert(table, nullColumnHack, values);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } catch (Error e) {
        } finally {
            helper.closeSafety();
        }
        Analytics.endDBUtil(start);
        return id;
    }

    @Override
    public int delete(String table, String whereClause, String[] whereArgs) {
        long start = System.nanoTime();
        final TDatabaseHelper helper = mHelper;
        SQLiteDatabase db = helper.openDatabase();
        int count = 0;

        try {
            count = db.delete(table, whereClause, whereArgs);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } catch (Error e) {
        } finally {
            helper.closeSafety();
        }
        Analytics.endDBUtil(start);
        return count;
    }

    @Override
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        long start = System.nanoTime();
        final TDatabaseHelper helper = mHelper;
        SQLiteDatabase db = helper.openDatabase();
        int count = 0;

        try {
            count = db.update(table, values, whereClause, whereArgs);
        } catch (Exception e) {
            NLog.printStackTrace(e);
            count = -1;
        } catch (Error e) {
        } finally {
            helper.closeSafety();
        }
        Analytics.endDBUtil(start);
        return count;
    }


    /**
     * 一次插入多条记录
     * 一定要注意:调用这里的时候,需要确保表已经创建好
     *
     * @param table
     * @param nullColumnHack
     * @param values
     */
    public long insert(String table, String nullColumnHack, ContentValues[] values) {
        long start = System.nanoTime();
        final TDatabaseHelper helper = mHelper;
        SQLiteDatabase db = helper.openDatabase();

        int result = 0;
        int numValues = values.length;
        try {
            db.beginTransaction();
            for (ContentValues value : values) {
                db.insert(table, nullColumnHack, value);
            }
            result = numValues;
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        } catch (Error e) {
            e.printStackTrace();
            result = -1;
        } finally {
            try {
                db.endTransaction();
            } catch (Exception e) {
                e.printStackTrace();
                result = -1;
            } catch (Error e) {
                e.printStackTrace();
                result = -1;
            }
        }
        Analytics.endDBUtil(start);
        return result;
    }
}

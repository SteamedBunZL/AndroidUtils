package com.clean.spaceplus.cleansdk.base.db;

import android.content.Context;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;

import com.hawkclean.framework.log.NLog;
import com.hawkclean.framework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author shunyou.huang
 * @Description:数据库管理器
 * @date 2016/4/22 14:05
 * @copyright TCL-MIG
 */

public class TDatabaseHelper extends SQLiteOpenHelper{

    public final static String TAG = TDatabaseHelper.class.getSimpleName();
    private final static int COUNTER_INITIAL_VALUE = 1;
    private TableHelper<?>[] mTableHelpers;
    private TableFactory mTableFactory;
    //private AtomicInteger atomicOpenCount = new AtomicInteger(0);
    private SQLiteDatabase mDatabase = null;

    public TDatabaseHelper(Context context, TableFactory factory) {
        super(context, factory.getDatabaseName(), null, factory.getDatabaseVersion(),
                (factory.getErrorHandler() != null) ? factory.getErrorHandler(): new DefaultDatabaseErrorHandler());
        NLog.d(TAG, "private TDatabaseHelper db version = %d, db name = %s", factory.getDatabaseVersion(), factory.getDatabaseName());
        this.mTableFactory = factory;
        this.mTableHelpers = factory.createAllTableHelpers();
    }

    public TableHelper<?> getTableHelper(Class<?> cls) {
        return mTableFactory.getTableHelper(cls);
    }

    public PojoDao<?> getDao(Class<?> cls) {
        return mTableFactory.getDao(cls);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(CollectionUtils.isEmpty(mTableHelpers)){
            return;
        }

        List<String> sqls = new ArrayList<>();

        for(TableHelper<?> tableHelper : mTableHelpers){
            Collection<String> s = tableHelper.getCreateTableSqls();
            if (!CollectionUtils.isEmpty(s))
                sqls.addAll(s);
        }

        batchExec(db, sqls);
    }

    void batchExec(SQLiteDatabase db, Collection<String> sqls) {
        if (CollectionUtils.isEmpty(sqls))
            return;

        db.beginTransaction();
        try {

            for (String sql: sqls) {
                if (!TextUtils.isEmpty(sql))
                    db.execSQL(sql);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        NLog.v(TAG, "onUpgrade oldVersion = %d, newVersion = %d, db name = %s", oldVersion, newVersion, db);
        if(CollectionUtils.isEmpty(mTableHelpers)){
            return;
        }

        List<String> sqls = new ArrayList<>();
        for(TableHelper<?> tableHelper : mTableHelpers){
            Collection<String> s = tableHelper.getUpdateTableSqls(oldVersion, newVersion);
            if (!CollectionUtils.isEmpty(s))
                sqls.addAll(s);
        }

        batchExec(db, sqls);
    }

    private void dropAll(SQLiteDatabase db) {
        if(CollectionUtils.isEmpty(mTableHelpers)){
            return;
        }

        List<String> sqls = new ArrayList<String>();
        for(TableHelper<?> tableHelper : mTableHelpers){
            Collection<String> s = tableHelper.getDropTableSqls();
            if (!CollectionUtils.isEmpty(s))
                sqls.addAll(s);
        }

        batchExec(db, sqls);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAll(db);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        //atomicOpenCount.set(COUNTER_INITIAL_VALUE);
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (mDatabase == null) {
            try{
                mDatabase = getWritableDatabase();
                if(Build.VERSION.SDK_INT >= 11 && mDatabase!=null){
                    mDatabase.enableWriteAheadLogging();
                }
            }catch (Exception e){

            }
        }
        return mDatabase;
    }

//    public SQLiteDatabase getReadableDatabase(){
//        if(mDatabase == null){
//            mDatabase = getReadableDatabase();
//            return mDatabase;
//        }
//        return mDatabase;
//    }

    /**
     * 统一不关闭数库库
     * 注释掉
     */
    public synchronized void closeSafety() {
//        final AtomicInteger counter = atomicOpenCount;
//        int count = counter.decrementAndGet();
//        if (mDatabase != null && count <= 0) {
//            NLog.e(TAG, "closeSafety db---->" + mDatabase);
//            close();
//            mDatabase = null;
//        }
    }



    public synchronized SQLiteDatabase openMyDatabase() {
        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
            return mDatabase;
        }
        return mDatabase;
    }

    /**
     * 之前的代码由于AtomicInteger处理的有问题会出现关闭不掉数据库的情况
     * 为了不大范围改变原有代码结构 不判断atomicOpenCount的值来关闭数据库
     */
    public synchronized void closeMyDatabase() {
       /* if (mDatabase != null && mDatabase.isOpen()) {
            NLog.e(TAG, "关闭数据库db---->"+mDatabase);
            close();
            mDatabase = null;
        }*/
    }
}

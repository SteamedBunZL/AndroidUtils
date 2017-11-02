//package com.clean.spaceplus.cleansdk.base.db;
//
//import android.content.ContentResolver;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.net.Uri;
//
///**
// * @author dongdong.huang
// * @Description: db wrapper
// * @date 2016/4/25 17:32
// * @copyright TCL-MIG
// */
//public class SmartDbWrapper {
//    protected Context mContext;
//
//    private boolean mUseProvider = false;
//    private volatile ContentResolver mContentResolver = null;
//    private boolean mInsertUseReplaceMode = true;
//    private DbUriUtil.TableNameToUriCache mTableNameToUri = null;
//
//    private MySQLiteDB mMyDb = null;
//
//    public SmartDbWrapper(Context context, Uri baseUri, MySQLiteDB myDb) {
//        mContext = context;
//        //setInsertUseReplaceMode(true);
//        setBaseUri(baseUri);
//        mMyDb = myDb;
//    }
//
//    public void setBaseUri(Uri baseUri) {
//        synchronized(this) {
//            if (baseUri != null) {
//                if (null == mTableNameToUri) {
//                    mTableNameToUri = new DbUriUtil.TableNameToUriCache();
//                } else {
//                    mTableNameToUri.clear();
//                }
//                mTableNameToUri.setBaseUri(baseUri);
//                mUseProvider = true;
//            } else {
//                if (mUseProvider) {
//                    mUseProvider = false;
//                }
//                if (mTableNameToUri != null) {
//                    mTableNameToUri.clear();
//                    mTableNameToUri = null;
//                }
//                if (mContentResolver != null) {
//                    mContentResolver = null;
//                }
//            }
//        }
//    }
//
//    private void readyContentResolver() {
//        if (null== mContentResolver) {
//            synchronized(this) {
//                if (null== mContentResolver) {
//                    mContentResolver = mContext.getContentResolver();
//                }
//            }
//        }
//    }
//
//    public SQLiteDatabase getDatabase() {
//        return mMyDb != null ? mMyDb.getDatabase() : null;
//    }
//
//    public Uri getUri(String table) {
//        return mTableNameToUri != null ? mTableNameToUri.getUri(table) : null;
//    }
//
//
//    public int bulkInsert(String table, ContentValues[] values) {
//        int result = 0;
//        if (mUseProvider) {
//            readyContentResolver();
//            Uri uri = getUri(table);
//            if (uri != null && mContentResolver != null) {
//                try {
//                    result = mContentResolver.bulkInsert(uri, values);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    result = -1;
//                }
//            }
//        } else {
//            MySQLiteDB.MyDBData data = null;
//            if (mMyDb != null) {
//                data = mMyDb.getDatabaseAndAcquireReference();
//            }
//            SQLiteDatabase db = getDatabase();
//            if (db != null) {
//                result = bulkInsertWithRealDb(db, table, values);
//            }
//            if (data != null) {
//                mMyDb.releaseReference(data);
//            }
//        }
//        return result;
//    }
//
//    private long _insertWithRealDb(SQLiteDatabase db, String table, ContentValues values) {
//        long id = -1;
//        if (mInsertUseReplaceMode) {
//            id = db.replace(table, null, values);
//        } else {
//            id = db.insert(table, null, values);
//        }
//        return id;
//    }
//
//    private int bulkInsertWithRealDb(SQLiteDatabase db, String table, ContentValues[] values) {
//        int result = 0;
//        int numValues = values.length;
//        try {
//            db.beginTransaction();
//            for ( ContentValues value : values ) {
//                _insertWithRealDb(db, table, value);
//            }
//            result = numValues;
//            db.setTransactionSuccessful();
//        }catch(Exception e) {
//            e.printStackTrace();
//            result = -1;
//        } catch (Error e) {
//            e.printStackTrace();
//            result = -1;
//        } finally {
//            try {
//                db.endTransaction();
//            } catch (Exception e) {
//                e.printStackTrace();
//                result = -1;
//            } catch (Error e) {
//                e.printStackTrace();
//                result = -1;
//            }
//        }
//        return result;
//    }
//}

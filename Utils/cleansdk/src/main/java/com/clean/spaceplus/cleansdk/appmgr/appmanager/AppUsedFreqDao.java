package com.clean.spaceplus.cleansdk.appmgr.appmanager;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.appmgr.service.AppUsedInfoRecord;
import com.clean.spaceplus.cleansdk.base.db.app_used_freq.AppUsedFreqProvider;
import com.clean.spaceplus.cleansdk.base.db.app_used_freq.AppUsedInfoTable;
import com.clean.spaceplus.cleansdk.base.db.provide.processlist.MyAppUsedFreqProvider;
import com.clean.spaceplus.cleansdk.util.DateUtils;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author wangtianbao
 * @Description: App使用频率dao
 * @date 2016/5/11 14:05
 * @copyright TCL-MIG
 */
public class AppUsedFreqDao {
    private static final String TAG = MyAppUsedFreqProvider.TAG;
    //private ProcessListProvider mProvider;
    private AppUsedFreqProvider mProvider;


    public AppUsedFreqDao() {
        //Provider = ProcessListProvider.getInstance(SpaceApplication.getInstance().getContext());
        mProvider = AppUsedFreqProvider.getInstance(SpaceApplication.getInstance().getContext());
    }

    /**
     * 获取跟今天相距7天的记录
     *
     * @return
     */
    public List<AppUsedInfoRecord> getLatestSevenDayRecords() {
       /* String sql = String.format("select * from %s where date(record_date) > date('now','-30 day') order by pkg_name;", AppUsedInfoTable.TABLE_NAME);
        Cursor cursor = null;
        List<AppUsedInfoRecord> records = new ArrayList<>();
        try {
            cursor = mProvider.rawQuery(sql, null);
            if (cursor == null) {
                return null;
            }
            while (cursor.moveToNext()) {
                records.add(parse(cursor));
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }finally {
            closeCursorSafely(cursor);
        }
        return records;*/
        return getLatestSevenDayRecordsByProvider();
    }

//    public List<AppUsedFreqInfo> getLastOpenTime(){
//        List<AppUsedFreqInfo> infos = new ArrayList<AppUsedFreqInfo>();
//        Cursor cursor = null;
//        try {
//            String[] projection = new String[]{AppUsedInfoTable.PKG_NAME, AppUsedInfoTable.LAST_OPEN_TIME};
//            cursor = SpaceApplication.getInstance().getContext().getContentResolver().query(AppUsedInfoTable.URI, projection, null, null, null);
//
//            if (cursor == null) {
//                return infos;
//            }
//
//            String pkgName = "";
//            AppUsedFreqInfo inf;
//            while(cursor.moveToNext()){
//                pkgName = cursor.getString(0);
//                if(!TextUtils.isEmpty(pkgName)){
//                    inf = new AppUsedFreqInfo(pkgName);
//                    inf.setLastOpenTime(cursor.getLong(1));
//                    infos.add(inf);
//                }
//            }
//        } catch (Exception e) {
//            NLog.printStackTrace(e);
//        }finally {
//            closeCursorSafely(cursor);
//        }
//
//        return infos;
//    }

    public List<AppUsedInfoRecord> getLatestSevenDayRecordsByProvider() {
        NLog.d(TAG, "getLatestSevenDayRecordsByProvider");
        String sql = String.format("select * from %s where date(record_date) > date('now','-30 day') order by pkg_name;", AppUsedInfoTable.TABLE_NAME);
        Cursor cursor = null;
        List<AppUsedInfoRecord> records = new ArrayList<>();
        try {
            //cursor = mProvider.rawQuery(sql, null);

            String sortOrder = AppUsedInfoTable.PKG_NAME;
            String selection = "date(record_date) > date('now','-30 day')";
            cursor = SpaceApplication.getInstance().getContext().getContentResolver().query(AppUsedInfoTable.URI,null,selection,null,sortOrder);

            if (cursor == null) {
                return null;
            }
            NLog.d(TAG, "getLatestSevenDayRecordsByProvider cursor count = %d", cursor.getCount());
            while (cursor.moveToNext()) {
                records.add(parse(cursor));
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }finally {
            closeCursorSafely(cursor);
        }
        return records;
    }



    AppUsedInfoRecord parse(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        int pkgNameIndex = cursor.getColumnIndex(AppUsedInfoTable.PKG_NAME);
        String pkgName = cursor.getString(pkgNameIndex);
        long lastOpenTime = cursor.getLong(cursor.getColumnIndex(AppUsedInfoTable.LAST_OPEN_TIME));
        String recordDate = cursor.getString(cursor.getColumnIndex(AppUsedInfoTable.RECORD_DATE));
        int totalOpenCount = cursor.getInt(cursor.getColumnIndex(AppUsedInfoTable.TOTAL_OPEN_COUNT));
        long totalOpenTime = cursor.getLong(cursor.getColumnIndex(AppUsedInfoTable.TOTAL_OPEN_TIME));
        int id = cursor.getInt(cursor.getColumnIndex(AppUsedInfoTable._ID));
        return AppUsedInfoRecord.createRecord(id, pkgName, lastOpenTime, recordDate, totalOpenCount, totalOpenTime);
    }

    /**
     * 获取今天的记录
     *
     * @return
     */
    public AppUsedInfoRecord getTodayRecord(String pkgName) {
        /*String sql = String.format("select * from %s where pkg_name='%s' and date(record_date)=date('now');", AppUsedInfoTable.TABLE_NAME, pkgName);
        Cursor cursor = null;
        AppUsedInfoRecord record = null;
        try {
            cursor = mProvider.rawQuery(sql, null);
            if (cursor == null || cursor.getCount() == 0) {
               closeCursorSafely(cursor);
                return null;
            }
            cursor.moveToNext();
            record = parse(cursor);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }finally {
            closeCursorSafely(cursor);
        }
        return record;*/
        return getTodayRecordByProvider(pkgName);
    }


    public AppUsedInfoRecord getTodayRecordByProvider(String pkgName) {
        String sql = String.format("select * from %s where pkg_name='%s' and date(record_date)=date('now');", AppUsedInfoTable.TABLE_NAME, pkgName);
        Cursor cursor = null;
        AppUsedInfoRecord record = null;
        try {
            //cursor = mProvider.rawQuery(sql, null);

            String selection = String.format("%s = '%s' and date(record_date)=date('now')", AppUsedInfoTable.PKG_NAME, pkgName);
            NLog.d(MyAppUsedFreqProvider.TAG, "getTodayRecordByProvider selection = %s", selection);
            cursor = SpaceApplication.getInstance().getContext().getContentResolver().query(AppUsedInfoTable.URI,null,selection,null,null);

            if (cursor == null || cursor.getCount() == 0) {
                closeCursorSafely(cursor);
                return null;
            }
            cursor.moveToNext();
            record = parse(cursor);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }finally {
            closeCursorSafely(cursor);
        }
        return record;
    }


    /**
     * 找出最早的记录，如果超过3天，则可以显示频率
     *
     * @return
     */
    public int getEldestRecordDaysToNow() {
//        String sql = String.format("select count(*) as num from %s where date(record_date) < date('now','-2 day');", AppUsedInfoTable.TABLE_NAME);
//        Cursor cursor = mProvider.rawQuery(sql,null);
//        if(cursor==null||cursor.getCount()==0){
//            return 0;
//        }
//        cursor.moveToNext();
//        int num=cursor.getInt(cursor.getColumnIndex("num"));
//        return num;

      /*  String str = String.format("select * from %s order by date(record_date) asc LIMIT 1 OFFSET 0;", AppUsedInfoTable.TABLE_NAME);
        Cursor cursor = null;
        int days;
        try {
            cursor = mProvider.rawQuery(str, null);
            if (cursor == null || cursor.getCount() == 0) {
                closeCursorSafely(cursor);
                return 0;
            }
            cursor.moveToNext();
            String date = cursor.getString(cursor.getColumnIndex(AppUsedInfoTable.RECORD_DATE));
            days = DateUtils.getDaysToNow(DateUtils.dbParse(date));
        } catch (Exception e) {
            NLog.printStackTrace(e);
            return 0;
        }finally {
            closeCursorSafely(cursor);
        }
        return days;*/
        return getEldestRecordDaysToNowByProvider();
    }



    public int getEldestRecordDaysToNowByProvider() {
        String str = String.format("select * from %s order by date(record_date) asc LIMIT 1 OFFSET 0;", AppUsedInfoTable.TABLE_NAME);
        Cursor cursor = null;
        int days;
        try {
            String sortOrder = "date(record_date) asc LIMIT 1 OFFSET 0";
            cursor = SpaceApplication.getInstance().getContext().getContentResolver().query(AppUsedInfoTable.URI,null,null,null,sortOrder);
            //cursor = mProvider.rawQuery(str, null);
            if (cursor == null || cursor.getCount() == 0) {
                closeCursorSafely(cursor);
                return 0;
            }
            NLog.d(MyAppUsedFreqProvider.TAG, "getEldestRecordDaysToNowByProvider cursor count = %d", cursor.getCount());
            cursor.moveToNext();
            String date = cursor.getString(cursor.getColumnIndex(AppUsedInfoTable.RECORD_DATE));
            days = DateUtils.getDaysToNow(DateUtils.dbParse(date));
        } catch (Exception e) {
            NLog.printStackTrace(e);
            return 0;
        }finally {
            closeCursorSafely(cursor);
        }
        return days;
    }

    private static void closeCursorSafely(Cursor cursor) {
        if (null != cursor) {
            try {
                cursor.close();
            } catch (Exception e) {
                NLog.printStackTrace(e);
            } catch (Error e) {
                NLog.printStackTrace(e);
            }
            cursor = null;
        }
    }

    public boolean saveRecords(Collection<AppUsedInfoRecord> records) {
       // return mProvider.saveRecords(records);
        return saveRecordsByProvider(records);
    }


    public boolean saveRecordsByProvider(Collection<AppUsedInfoRecord> records) {
        NLog.d(TAG, "saveRecordsByProvider ");
        ContentValues[] values = new ContentValues[]{};
        for (AppUsedInfoRecord record: records){
            ContentValues cv = new ContentValues();
            cv.put(AppUsedInfoTable.PKG_NAME,record.mAppUsedFreqInfo.getPackageName());
            cv.put(AppUsedInfoTable.LAST_OPEN_TIME,record.mAppUsedFreqInfo.getLastOpenTime());
            cv.put(AppUsedInfoTable.RECORD_DATE,record.mRecordDate);
            cv.put(AppUsedInfoTable.TOTAL_OPEN_COUNT,record.mAppUsedFreqInfo.getTotalOpenCount());
            cv.put(AppUsedInfoTable.TOTAL_OPEN_TIME,record.mAppUsedFreqInfo.getTotalOpenTime());
            if(record.mId>0){
                NLog.d(TAG, "saveRecordsByProvider 更新数据");
                //更新
                String selection= AppUsedInfoTable._ID +"=?";
                String[] selectionArgs = new String[]{String.valueOf(record.mId)};
                //int result=update(AppUsedInfoTable.TABLE_NAME,cv,where,new String[]{record.mId+""});
                int result= SpaceApplication.getInstance().getContext().getContentResolver().update(AppUsedInfoTable.URI,cv,selection, selectionArgs);
                NLog.d(TAG, "saveRecordsByProvider update result = %d", result);
            }else{
                //插入
                //long result=insert(AppUsedInfoTable.TABLE_NAME,null,cv);

                Uri uri = SpaceApplication.getInstance().getContext().getContentResolver().insert(AppUsedInfoTable.URI, cv);
                long result = ContentUris.parseId(uri);
                record.mId=result;
                NLog.d(TAG, "saveRecordsByProvider 插入数据 id = "+result);
            }
        }
        return true;



    }

    public long getLastLauchedTimeByProvider(String pkgName) {
        NLog.d(MyAppUsedFreqProvider.TAG, "getLastLauchedTimeByProvider");
        String sql = String.format("select %s from %s where pkg_name = '%s' "
                , AppUsedInfoTable.LAST_OPEN_TIME,
                AppUsedInfoTable.TABLE_NAME, pkgName);
        Cursor cursor = null;
        long lastOpenTime = -1;
        try {
           // cursor = mProvider.rawQuery(sql, null);

            String selection = String.format("%s = '%s'", AppUsedInfoTable.PKG_NAME, pkgName);
            String[] projection = new String[]{AppUsedInfoTable.LAST_OPEN_TIME};
            cursor = SpaceApplication.getInstance().getContext().getContentResolver().query(AppUsedInfoTable.URI,projection,selection,null,null);


            if (cursor == null) {
                return lastOpenTime;
            }
            NLog.d(MyAppUsedFreqProvider.TAG, "getLastLauchedTime cursor count = %d", cursor.getCount());
            if (cursor.moveToFirst()) {
                lastOpenTime = cursor.getLong(cursor.getColumnIndex(AppUsedInfoTable.LAST_OPEN_TIME));
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }finally {
            closeCursorSafely(cursor);
        }
        return lastOpenTime;
    }



    public long getLastLauchedTime(String pkgName) {
        /*String sql = String.format("select %s from %s where pkg_name = '%s' "
                , AppUsedInfoTable.LAST_OPEN_TIME,
                AppUsedInfoTable.TABLE_NAME, pkgName);
        Cursor cursor = null;
        long lastOpenTime = -1;
        try {
            cursor = mProvider.rawQuery(sql, null);

            if (cursor == null) {
                return lastOpenTime;
            }
            if (cursor.moveToFirst()) {
                lastOpenTime = cursor.getLong(cursor.getColumnIndex(AppUsedInfoTable.LAST_OPEN_TIME));
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }finally {
            closeCursorSafely(cursor);
        }
        return lastOpenTime;*/
        return getLastLauchedTimeByProvider(pkgName);
    }
}

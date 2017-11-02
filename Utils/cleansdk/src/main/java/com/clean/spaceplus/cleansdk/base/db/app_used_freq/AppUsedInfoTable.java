package com.clean.spaceplus.cleansdk.base.db.app_used_freq;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.clean.spaceplus.cleansdk.appmgr.service.AppUsedInfoRecord;
import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.base.db.provide.processlist.MyAppUsedFreqProvider;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author wangtianbao
 * @Description:
 * @date 2016/5/10 20:36
 * @copyright TCL-MIG
 */
public class AppUsedInfoTable implements TableHelper<AppUsedInfoRecord> {
    public static final String TAG = AppUsedInfoTable.class.getSimpleName();
    public final static String TABLE_NAME = "app_open_frequency";

    public static final String _ID="_id";
    public static final String PKG_NAME="pkg_name";
    public static final String LAST_OPEN_TIME="last_open_time";
    public static final String RECORD_DATE="record_date";
    public static final String TOTAL_OPEN_COUNT="total_open_count";
    public static final String TOTAL_OPEN_TIME="total_open_time";

    public static final Uri URI = Uri.parse("content://" + MyAppUsedFreqProvider.AUTHORITIES  + "/" + TABLE_NAME);


    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<>();

        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY AUTOINCREMENT, ", _ID);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", PKG_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER, ", LAST_OPEN_TIME);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", RECORD_DATE);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", TOTAL_OPEN_COUNT);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0) ); ", TOTAL_OPEN_TIME);
//        StringUtils.appendFormat(sb, "CREATE UNIQUE INDEX %s_idx ON %s(%s, %s); ", TABLE_NAME,TABLE_NAME,PKG_NAME,RECORD_DATE);

//        StringUtils.appendFormat(sb,"constraint pk_t2 primary key (%s,%s)",PKG_NAME,RECORD_DATE);
        NLog.i(TAG, "AppUsedInfoTable create table sqls " + sb.toString());
        sqls.add(sb.toString());
        return sqls;
    }

    @Override
    public Collection<String> getDropTableSqls() {
        List<String> sqls = new ArrayList<String>();
        sqls.add("DROP TABLE IF EXISTS " + TABLE_NAME);
        return sqls;
    }

    @Override
    public Collection<String> getUpdateTableSqls(int oldVersion, int newVersion) {
        return null;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(AppUsedInfoRecord usedInfoRecord) {
        ContentValues cv = new ContentValues();
        cv.put(PKG_NAME,usedInfoRecord.mAppUsedFreqInfo.getPackageName());
        cv.put(LAST_OPEN_TIME,usedInfoRecord.mAppUsedFreqInfo.getLastOpenTime());
        cv.put(RECORD_DATE,usedInfoRecord.mRecordDate);
        cv.put(TOTAL_OPEN_COUNT,usedInfoRecord.mAppUsedFreqInfo.getTotalOpenCount());
        cv.put(TOTAL_OPEN_TIME,usedInfoRecord.mAppUsedFreqInfo.getTotalOpenTime());
        return cv;
    }

    @Override
    public AppUsedInfoRecord parseCursor(Cursor cursor) {
        String pkgName=cursor.getString(cursor.getColumnIndex(PKG_NAME));
        long lastOpenTime=cursor.getLong(cursor.getColumnIndex(LAST_OPEN_TIME));
        String recordDate= cursor.getString(cursor.getColumnIndex(RECORD_DATE));
        int totalOpenCount=cursor.getInt(cursor.getColumnIndex(TOTAL_OPEN_COUNT));
        long totalOpenTime=cursor.getLong(cursor.getColumnIndex(TOTAL_OPEN_TIME));
        int id=cursor.getInt(cursor.getColumnIndex(_ID));
        return AppUsedInfoRecord.createRecord(id,pkgName,lastOpenTime,recordDate,totalOpenCount,totalOpenTime);
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }
}

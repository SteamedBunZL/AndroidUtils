package com.clean.spaceplus.cleansdk.base.db.process_list;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description:ProcessWhiteList表构造
 * @date 2016/5/3 11:16
 * @copyright TCL-MIG
 */
public class ProcessWhiteListTable implements TableHelper<ProcessWhiteList> {

    public static final String TAG = ProcessWhiteListTable.class.getSimpleName();
    public final static String TABLE_NAME = "process_white_list";
    public static final String MARK = "mark";
    public static final String PKG_NAME = "pkgname";
    public static final String TITLE = "title";

    @Override
    public ContentValues getContentValues(ProcessWhiteList processWhiteList) {
        ContentValues cv = new ContentValues();
        cv.put(MARK, processWhiteList.mMark);
        cv.put(PKG_NAME, processWhiteList.mPkgname);
        cv.put(TITLE, processWhiteList.mTitle);
        return cv;
    }

    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<String>();

        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] TEXT primary key, ", PKG_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", MARK);
        StringUtils.appendFormat(sb, "[%s] TEXT )", TITLE);
        NLog.i(TAG , " sqls " + sb.toString());
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
    public ProcessWhiteList parseCursor(Cursor cursor) {
        ProcessWhiteList info = new ProcessWhiteList();
        info.mMark = cursor.getInt(cursor.getColumnIndex(MARK));
        info.mPkgname = cursor.getString(cursor.getColumnIndex(PKG_NAME));
        info.mTitle = cursor.getString(cursor.getColumnIndex(TITLE));

        return info;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }
}

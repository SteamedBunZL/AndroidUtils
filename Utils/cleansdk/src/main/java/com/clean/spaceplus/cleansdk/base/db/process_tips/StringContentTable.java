package com.clean.spaceplus.cleansdk.base.db.process_tips;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description:
 * @date 2016/6/27 17:14
 * @copyright TCL-MIG
 */
public class StringContentTable implements TableHelper<LocalTip4Model> {

    public static final String TABLE_NAME = "string_content";
    public static final String RECNO = "RecNo";
    public static final String ID = "_id";
    public final String CONTENT = "content";

    @Override
    public ContentValues getContentValues(LocalTip4Model localTip4Model) {
        return null;
    }

    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER primary key autoincrement, ", RECNO);
        StringUtils.appendFormat(sb, "[%s] INTEGER, ", ID);
        StringUtils.appendFormat(sb, "[%s] TEXT )", CONTENT);
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
    public LocalTip4Model parseCursor(Cursor cursor) {
        return null;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }
}

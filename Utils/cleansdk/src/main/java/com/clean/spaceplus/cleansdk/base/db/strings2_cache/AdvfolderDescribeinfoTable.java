package com.clean.spaceplus.cleansdk.base.db.strings2_cache;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.string2_cache.AdvFolderDescribeInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/3 11:08
 * @copyright TCL-MIG
 */
public class AdvfolderDescribeinfoTable implements TableHelper<AdvFolderDescribeInfo> {
    private static final String TAG = AdvfolderDescribeinfoTable.class.getSimpleName();
    //广告描述表
    public final static String TABLE_NAME = "adv_desc";
    public final static String AUTO_INC_ID = "_id";
    public final static String ID = "id";
    public final static String LANG = "lang";
    public final static String VALUE = "desc";


    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<>();

        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY AUTOINCREMENT, ", _ID);
        StringUtils.appendFormat(sb, "[%s] INTEGER , ", ID);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", LANG);
        StringUtils.appendFormat(sb, "[%s] TEXT )", VALUE);
        sqls.add(sb.toString());
        return sqls;
    }

    public static String getCreateTableSqlString(){
        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY AUTOINCREMENT, ", _ID);
        StringUtils.appendFormat(sb, "[%s] INTEGER , ", ID);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", LANG);
        StringUtils.appendFormat(sb, "[%s] TEXT )", VALUE);
        return sb.toString();
    }
    public static String getDropTableSqlString(){
        return "drop table if exists "+TABLE_NAME;
    }




    @Override
    public Collection<String> getDropTableSqls() {
        return null;
    }

    @Override
    public Collection<String> getUpdateTableSqls(int oldVersion, int newVersion) {
        return null;
    }

    @Override
    public String getTableName() {
        return TAG;
    }

    @Override
    public ContentValues getContentValues(AdvFolderDescribeInfo advfolderDescribeinfo) {
        return null;
    }

    @Override
    public AdvFolderDescribeInfo parseCursor(Cursor cursor) {
        return null;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }
}

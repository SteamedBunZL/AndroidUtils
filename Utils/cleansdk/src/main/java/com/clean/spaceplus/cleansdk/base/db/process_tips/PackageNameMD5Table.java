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
 * @date 2016/6/27 17:08
 * @copyright TCL-MIG
 */
public class PackageNameMD5Table implements TableHelper<LocalTip3Model> {

    public static final String TABLE_NAME = "package_name_md5";
    public static final String RECNO = "RecNo";
    public static final String ID = "_id";
    public final String PACKAGE_NAME_MD5 = "package_name_md5";

    @Override
    public ContentValues getContentValues(LocalTip3Model localTip3Model) {
        return null;
    }

    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER primary key autoincrement, ", RECNO);
        StringUtils.appendFormat(sb, "[%s] INTEGER, ", ID);
        StringUtils.appendFormat(sb, "[%s] TEXT )", PACKAGE_NAME_MD5);
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
    public LocalTip3Model parseCursor(Cursor cursor) {
        return null;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }
}

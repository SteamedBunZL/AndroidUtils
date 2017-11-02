package com.clean.spaceplus.cleansdk.base.db.residual_dir_hf;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgquery_hf.RegDirQuery;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jerry
 * @Description:目录查询残留高频数据库表
 * @date 2016/5/12 14:04
 * @copyright TCL-MIG
 */
public class PkgQueryHfDirQuery2Table implements TableHelper<RegDirQuery> {

    private static final String TAG = PkgQueryHfDirQuery2Table.class.getSimpleName();

    //public final static String TABLE_NAME = "dirquery2";
    public final static String TABLE_NAME = "routeinquery2";

    public final static String _ID = "_id";
    //public final static String DIR = "dir";
    //public final static String DIR = "route"; 新库需要取dir2字段
    public final static String DIR = "dir2";
    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);

        StringUtils.appendFormat(sb, "[%s] integer default (0) PRIMARY KEY, ", _ID);
        StringUtils.appendFormat(sb, "[%s] BLOB, ", DIR);
        NLog.d(TAG , " create dirquery2 table sqls " + sb.toString());
        sqls.add(sb.toString());

        return sqls;
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
        return null;
    }

    @Override
    public ContentValues getContentValues(RegDirQuery regDirQuery) {
        return null;
    }

    @Override
    public RegDirQuery parseCursor(Cursor cursor) {
        return null;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }
}

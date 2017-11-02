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
public class GlobalsuffixconfigTable implements TableHelper<RegDirQuery> {

    private static final String TAG = GlobalsuffixconfigTable.class.getSimpleName();

    //public final static String TABLE_NAME = "globalsuffixconfig";
    public final static String TABLE_NAME = "postfix_config";

    //public final static String TYPEID = "typeid";
    public final static String TYPEID = "genre_id";
    //public final static String SUFFIX = "suffix";
    public final static String SUFFIX = "postfixdesc";
    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] integer primary key, ", TYPEID);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", SUFFIX);
        NLog.d(TAG , " create globalsuffixconfig table sqls " + sb.toString());
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

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
public class PkgQueryHfRegDirQueryTable implements TableHelper<RegDirQuery> {

    private static final String TAG = PkgQueryHfRegDirQueryTable.class.getSimpleName();

   // public final static String TABLE_NAME = "regdirquery";
   public final static String TABLE_NAME = "regexrouteinquery";

    //public final static String DIRID = "dirid";
    public final static String DIRID = "routeid";

    //public final static String REGDIR = "regdir";
    public final static String REGDIR = "regexroute";


    //public final static String REPKGS = "repkgs";
    public final static String REPKGS = "regexpackages";
    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);

        StringUtils.appendFormat(sb, "[%s] integer default (0), ", DIRID);
        StringUtils.appendFormat(sb, "[%s] text, ", REGDIR);
        StringUtils.appendFormat(sb, "[%s] text ) ", REPKGS);
        NLog.d(TAG , " create regdirquery table sqls " + sb.toString());
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

package com.clean.spaceplus.cleansdk.base.db.residual_pkg_cache;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgquery_hf.PkgQuery;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author shunyou.huang
 * @Description:包信息查询表
 * @date 2016/4/22 15:00
 * @copyright TCL-MIG
 */

public class ResidualPkgCachePkgQueryTable implements TableHelper<PkgQuery>{

    private static final String TAG = ResidualPkgCachePkgQueryTable.class.getSimpleName();

   // public final static String TABLE_NAME = "pkgquery";
    public final static String TABLE_NAME = "packageinquery";
   // public final static String PKG_ID = "pkgid";
    public final static String PKG_ID = "packageid";
   // public final static String PKG = "pkg";
    public final static String PKG = "package";
   // public final static String DIRS = "dirs";

    public final static String TIME = "time";

    public final static String DIRS = "routes";
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(PkgQuery t) {
        ContentValues cv = new ContentValues();
        cv.put(PKG_ID, t.pkgid);
        cv.put(PKG, t.pkg);
        cv.put(DIRS, t.dirs);
        return cv;
    }

    @Override
    public PkgQuery parseCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0){
            return null;
        }
        PkgQuery info = new PkgQuery();
        info.pkgid = cursor.getString(cursor.getColumnIndex(PKG_ID));
        info.pkg = cursor.getString(cursor.getColumnIndex(PKG));
        info.dirs = cursor.getString(cursor.getColumnIndex(DIRS));
        return info;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }

    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] integer default (0), ", PKG_ID);
        StringUtils.appendFormat(sb, "[%s] integer primary key, ", PKG);
        StringUtils.appendFormat(sb, "[%s] LONG DEFAULT (0), ", TIME);
        StringUtils.appendFormat(sb, "[%s] TEXT )", DIRS);
        NLog.i(TAG , " sqls " + sb.toString());
        sqls.add(sb.toString());
        return sqls;
    }



    @Override
    public Collection<String> getDropTableSqls() {
        List<String> sqls = new ArrayList<>();
        sqls.add("DROP TABLE IF EXISTS " + TABLE_NAME);
        return null;
    }



    @Override
    public Collection<String> getUpdateTableSqls(int oldVersion, int newVersion) {
        return null;
    }
}

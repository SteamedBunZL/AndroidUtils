package com.clean.spaceplus.cleansdk.base.db.pkgcache_hf;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.PkgQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author shunyou.huang
 * @Description:PkgQuery表构造器
 * @date 2016/4/22 15:00
 * @copyright TCL-MIG
 */

public class CacheHfPkgQueryTable implements TableHelper<PkgQuery>{

    private static final String TAG = CacheHfPkgQueryTable.class.getSimpleName();

    //public final static String TABLE_NAME = "pkgquery";
    public final static String TABLE_NAME = "packageinquery";
    //public final static String PKG_ID = "pkgid";
    public final static String PKG_ID = "packageid";
    //public final static String PKG = "pkg";
    public final static String PKG = "package";
    //public final static String SYS_FLAG = "sysflag";
    public final static String SYS_FLAG = "cachecleansign";
    //public final static String IS_INTEGRITY = "is_integrity";
    public final static String IS_INTEGRITY = "result_integrity";
   // public final static String DIRS = "dirs";
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
        cv.put(SYS_FLAG, t.sysflag);
        cv.put(IS_INTEGRITY, t.is_integrity);
        cv.put(DIRS, t.dirs);

        return cv;
    }

    @Override
    public PkgQuery parseCursor(Cursor cursor) {

        PkgQuery info = new PkgQuery();
        info.pkgid = cursor.getInt(cursor.getColumnIndex(PKG_ID));
        info.pkg = cursor.getInt(cursor.getColumnIndex(PKG));
        info.sysflag = cursor.getInt(cursor.getColumnIndex(SYS_FLAG));
        info.is_integrity = cursor.getInt(cursor.getColumnIndex(IS_INTEGRITY));
        info.dirs = cursor.getString(cursor.getColumnIndex(DIRS));

        return info;
    }

    @Override
    public String getProviderAuthority() {
        return null;
        //return PkgCacheProvider.AUTHORITY;
    }

    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<String>();

        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER , ", PKG_ID);
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY , ", PKG);
        StringUtils.appendFormat(sb, "[%s] INTEGER , ", SYS_FLAG);
        StringUtils.appendFormat(sb, "[%s] INTEGER , ", IS_INTEGRITY);
        StringUtils.appendFormat(sb, "[%s] TEXT )", DIRS);
        sqls.add(sb.toString());

        StringBuffer sbIndex = new StringBuffer();
        StringUtils.appendFormat(sbIndex, "CREATE INDEX IF NOT EXISTS INDEX_PKG ON %s(%s)", TABLE_NAME, PKG);
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
}

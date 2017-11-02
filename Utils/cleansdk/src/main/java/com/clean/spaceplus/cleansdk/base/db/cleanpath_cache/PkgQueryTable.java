package com.clean.spaceplus.cleansdk.base.db.cleanpath_cache;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.PkgQueryInfo;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author shunyou.huang
 * @Description:PkgQuery表构造
 * @date 2016/4/22 15:00
 * @copyright TCL-MIG
 */

public class PkgQueryTable implements TableHelper<PkgQueryInfo>{

    private static final String TAG = PkgQueryTable.class.getSimpleName();

    public final static String TABLE_NAME = "pkgquery";
    public final static String AUTO_INC_ID = "_id";
    public final static String PKG_ID = "pkgid";
    public final static String PKG = "pkg";
    public final static String TIME = "time";
    public final static String SRC = "src";
    public final static String DIRS = "dirs";
    public final static String RE_DIRS = "redirs";
    public final static String FILES = "files";
    public final static String RE_FILES = "refiles";
    public final static String SYS_FLAG = "sysflag";
    public final static String IS_INTEGRITY = "is_integrity";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(PkgQueryInfo t) {
        ContentValues cv = new ContentValues();
        cv.put(AUTO_INC_ID, t._id);
        cv.put(PKG_ID, t.pkgid);
        cv.put(PKG, t.pkg);
        cv.put(TIME, t.time);
        cv.put(SRC, t.src);
        cv.put(DIRS, t.dirs);
        cv.put(RE_DIRS, t.redirs);
        cv.put(FILES, t.files);
        cv.put(RE_FILES, t.refiles);
        cv.put(SYS_FLAG, t.sysflag);
        cv.put(IS_INTEGRITY, t.is_integrity);

        return cv;
    }

    @Override
    public PkgQueryInfo parseCursor(Cursor cursor) {

        PkgQueryInfo info = new PkgQueryInfo();
        info._id = cursor.getInt(cursor.getColumnIndex(_ID));
        info.pkgid = cursor.getInt(cursor.getColumnIndex(PKG_ID));
        info.pkg = cursor.getString(cursor.getColumnIndex(PKG));
        info.time = cursor.getInt(cursor.getColumnIndex(TIME));
        info.src = cursor.getInt(cursor.getColumnIndex(SRC));
        info.dirs = cursor.getString(cursor.getColumnIndex(DIRS));
        info.redirs = cursor.getString(cursor.getColumnIndex(RE_DIRS));
        info.files = cursor.getString(cursor.getColumnIndex(FILES));
        info.refiles = cursor.getString(cursor.getColumnIndex(RE_FILES));
        info.sysflag = cursor.getInt(cursor.getColumnIndex(SYS_FLAG));
        info.is_integrity = cursor.getInt(cursor.getColumnIndex(IS_INTEGRITY));

        return info;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }

    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<String>();

        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY AUTOINCREMENT, ", _ID);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", PKG_ID);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", PKG);
        StringUtils.appendFormat(sb, "[%s] LONG DEFAULT (0), ", TIME);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", SRC);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", DIRS);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", RE_DIRS);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", FILES);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", RE_FILES);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", SYS_FLAG);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (1))", IS_INTEGRITY);
        NLog.i(TAG , " sqls " + sb.toString());
        sqls.add(sb.toString());
        sqls.add(String.format("CREATE UNIQUE INDEX [pkgindex] ON [%s] ([%s])", TABLE_NAME, PKG));
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

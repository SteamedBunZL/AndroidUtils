package com.clean.spaceplus.cleansdk.base.db.pkgcache;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.Version;
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

public class VersionTable implements TableHelper<Version>{

    private static final String TAG = VersionTable.class.getSimpleName();

//    public int major;
//    public int minor;
//    public int build;
//    public int subcnt;

    public final static String TABLE_NAME = "version";

    public final static String MAJOR = "major";
    public final static String MINOR = "minor";
    public final static String BUILD = "build";
    public final static String SUBCNT = "subcnt";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(Version t) {

        ContentValues cv = new ContentValues();
        cv.put(MAJOR, t.major);
        cv.put(MINOR, t.minor);
        cv.put(BUILD, t.build);
        cv.put(SUBCNT, t.subcnt);

        return cv;
    }

    @Override
    public Version parseCursor(Cursor cursor) {

        Version info = new Version();
        info.major = cursor.getInt(cursor.getColumnIndex(MAJOR));
        info.minor = cursor.getInt(cursor.getColumnIndex(MINOR));
        info.build = cursor.getInt(cursor.getColumnIndex(BUILD));
        info.subcnt = cursor.getInt(cursor.getColumnIndex(SUBCNT));

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
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY, ", MAJOR);
        StringUtils.appendFormat(sb, "[%s] INTEGER, ", MINOR);
        StringUtils.appendFormat(sb, "[%s] INTEGER, ", BUILD);
        StringUtils.appendFormat(sb, "[%s] INTEGER )", SUBCNT);
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
}

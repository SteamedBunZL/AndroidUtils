package com.clean.spaceplus.cleansdk.base.db.residual_dir_cache;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.PkgQueryHfDirQueryTable;
import com.clean.spaceplus.cleansdk.main.bean.residual_cache.ResidualDirQuery;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jerry
 * @Description:目录查询残留缓存数据库表
 * @date 2016/5/12 14:04
 * @copyright TCL-MIG
 */
public class ResidualDirCacheDirQueryTable implements TableHelper<ResidualDirQuery> {

    private static final String TAG = ResidualDirCacheDirQueryTable.class.getSimpleName();

    public final static String TABLE_NAME = PkgQueryHfDirQueryTable.TABLE_NAME;

    public final static String _ID = "_id";
    //public final static String DIRID = "dirid";
    public final static String DIRID = PkgQueryHfDirQueryTable.DIRID;

    //public final static String DIR = "dir";
    public final static String DIR = PkgQueryHfDirQueryTable.DIR;

    //public final static String QUERYRESULT = "queryresult";
    public final static String QUERYRESULT = PkgQueryHfDirQueryTable.QUERYRESULT;

    //public final static String CLEANTYPE = "cleantype";
    public final static String CLEANTYPE = PkgQueryHfDirQueryTable.CLEANTYPE;

    //public final static String CONTENTTYPE = "contenttype";
    public final static String CONTENTTYPE = PkgQueryHfDirQueryTable.CONTENTTYPE;

    //public final static String CMTYPE = "cmtype";
    public final static String CMTYPE = PkgQueryHfDirQueryTable.CMTYPE;

    public final static String TIME = "time";

    //public final static String DIRS = "dirs";
    public final static String DIRS = PkgQueryHfDirQueryTable.DIRS;

    //public final static String PKGS = "pkgs";
    public final static String PKGS = PkgQueryHfDirQueryTable.PKGS;

    //public final static String REPKGS = "repkgs";
    public final static String REPKGS = PkgQueryHfDirQueryTable.REPKGS;

    //public final static String TEST = "test";
    public final static String TEST = PkgQueryHfDirQueryTable.TEST;

    //public final static String SUBDIRS = "subdirs";
    public final static String SUBDIRS = PkgQueryHfDirQueryTable.SUBDIRS;

    //public final static String CLEANTIME = "cleantime";
    public final static String CLEANTIME = PkgQueryHfDirQueryTable.CLEANTIME;

    //public final static String SUFFIXINFO = "suffixinfo";
    public final static String SUFFIXINFO = PkgQueryHfDirQueryTable.SUFFIXINFO;
    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY AUTOINCREMENT, ", _ID);
        StringUtils.appendFormat(sb, "[%s] integer default (0), ", DIRID);
        StringUtils.appendFormat(sb, "[%s] blob, ", DIR);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", QUERYRESULT);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CLEANTYPE);

        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CONTENTTYPE);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CMTYPE);
        StringUtils.appendFormat(sb, "[%s] LONG DEFAULT (0), ", TIME);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", DIRS);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", PKGS);

        StringUtils.appendFormat(sb, "[%s] TEXT, ", REPKGS);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", TEST);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", SUBDIRS);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CLEANTIME);
        StringUtils.appendFormat(sb, "[%s] TEXT )", SUFFIXINFO);
        NLog.d(TAG , "crate residual_dir_cache dirquery表 sqls: " + sb.toString());
        sqls.add(sb.toString());
        //sqls.add(String.format("CREATE UNIQUE INDEX [dirindex] ON [%s] ([%s])", TABLE_NAME, DIR));
       // sqls.add(String.format("CREATE INDEX [diridindex] ON [%s] ([%s])", TABLE_NAME, DIRID));
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

    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(ResidualDirQuery dirQuery) {
        ContentValues cv = new ContentValues();
        cv.put(_ID, dirQuery._id);
        cv.put(DIRID, dirQuery.dirid);
        cv.put(DIR, dirQuery.dir);
        cv.put(QUERYRESULT, dirQuery.queryresult);
        cv.put(CLEANTYPE, dirQuery.cleantime);

        cv.put(CONTENTTYPE, dirQuery.contenttype);
        cv.put(CMTYPE, dirQuery.cmtype);
        cv.put(TIME, dirQuery.time);
        cv.put(DIRS, dirQuery.dirs);
        cv.put(PKGS, dirQuery.pkgs);

        cv.put(REPKGS, dirQuery.repkgs);
        cv.put(TEST, dirQuery.test);
        cv.put(SUBDIRS, dirQuery.subdirs);
        cv.put(CLEANTIME, dirQuery.cleantime);
        cv.put(SUFFIXINFO, dirQuery.suffixinfo);
        return cv;
    }

    @Override
    public ResidualDirQuery parseCursor(Cursor cursor) {
        ResidualDirQuery info = new ResidualDirQuery();
        info._id = cursor.getInt(cursor.getColumnIndex(_ID));
        info.dirid = cursor.getInt(cursor.getColumnIndex(DIRID));
        info.dir = cursor.getBlob(cursor.getColumnIndex(DIR));
        info.queryresult = cursor.getInt(cursor.getColumnIndex(QUERYRESULT));
        info.cleantype = cursor.getInt(cursor.getColumnIndex(CLEANTYPE));

        info.contenttype = cursor.getInt(cursor.getColumnIndex(CONTENTTYPE));
        info.cmtype = cursor.getInt(cursor.getColumnIndex(CMTYPE));
        info.time = cursor.getLong(cursor.getColumnIndex(TIME));
        info.dirs = cursor.getString(cursor.getColumnIndex(DIRS));
        info.pkgs = cursor.getString(cursor.getColumnIndex(PKGS));


        info.repkgs = cursor.getString(cursor.getColumnIndex(REPKGS));
        info.test = cursor.getInt(cursor.getColumnIndex(TEST));
        info.subdirs = cursor.getString(cursor.getColumnIndex(SUBDIRS));
        info.cleantime = cursor.getInt(cursor.getColumnIndex(CLEANTIME));
        info.suffixinfo = cursor.getString(cursor.getColumnIndex(SUFFIXINFO));
        return info;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }


}

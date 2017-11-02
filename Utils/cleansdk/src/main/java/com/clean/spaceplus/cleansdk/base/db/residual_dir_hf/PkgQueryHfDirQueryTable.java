package com.clean.spaceplus.cleansdk.base.db.residual_dir_hf;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.residual_cache.ResidualDirQuery;
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
public class PkgQueryHfDirQueryTable implements TableHelper<ResidualDirQuery> {

    private static final String TAG = PkgQueryHfDirQueryTable.class.getSimpleName();

    //public final static String TABLE_NAME = "dirquery";
    public final static String TABLE_NAME = "routeinquery";

    //public final static String DIRID = "dirid";
    public final static String DIRID = "routeid";
    //public final static String DIR = "dir";
    //public final static String DIR = "route";
    public final static String DIR = "dir2";

    //public final static String QUERYRESULT = "queryresult";
    public final static String QUERYRESULT = "resulttype";

    //public final static String CLEANTYPE = "cleantype";
    public final static String CLEANTYPE = "cleartype";


    //public final static String CONTENTTYPE = "contenttype";
    public final static String CONTENTTYPE = "filetype";

    //public final static String CMTYPE = "cmtype";
    public final static String CMTYPE = "media_clean_type";

    //public final static String DIRS = "dirs";
    public final static String DIRS = "routes";

    //public final static String PKGS = "pkgs";
    public final static String PKGS = "packages";


    //public final static String REPKGS = "repkgs";
    public final static String REPKGS = "regpackages";


    //public final static String LANGNAMEALERT = "langnamealert";
    public final static String LANGNAMEALERT = "langdesc";


    //public final static String TEST = "test";
    public final static String TEST = "debug";



    //public final static String SUBDIRS = "subdirs";
    public final static String SUBDIRS = "subroutes";

    //public final static String CLEANTIME = "cleantime";
    public final static String CLEANTIME = "cleartime";

    //public final static String SUFFIXINFO = "suffixinfo";
    public final static String SUFFIXINFO = "postfix";

    //public final static String UNINCLEANTIME = "unincleantime";
    public final static String UNINCLEANTIME = "uninstallcleantime";


    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);

        StringUtils.appendFormat(sb, "[%s] integer default (0)  primary key, ", DIRID);
        StringUtils.appendFormat(sb, "[%s] blob, ", DIR);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", QUERYRESULT);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CLEANTYPE);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CONTENTTYPE);

        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CMTYPE);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", DIRS);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", PKGS);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", REPKGS);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", LANGNAMEALERT);

        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", TEST);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", SUBDIRS);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CLEANTIME);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", SUFFIXINFO);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0) )", UNINCLEANTIME);
        NLog.i(TAG , " sqls " + sb.toString());
        sqls.add(sb.toString());
        //sqls.add(String.format("CREATE  INDEX [diridindex] ON [%s] ([%s])", TABLE_NAME, DIR));

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
        cv.put(DIRID, dirQuery.dirid);
        cv.put(DIR, dirQuery.dir);
        cv.put(QUERYRESULT, dirQuery.queryresult);
        cv.put(CLEANTYPE, dirQuery.cleantime);
        cv.put(CONTENTTYPE, dirQuery.contenttype);

        cv.put(CMTYPE, dirQuery.cmtype);
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
        info.dirid = cursor.getInt(cursor.getColumnIndex(DIRID));
        info.dir = cursor.getBlob(cursor.getColumnIndex(DIR));
        info.queryresult = cursor.getInt(cursor.getColumnIndex(QUERYRESULT));
        info.cleantype = cursor.getInt(cursor.getColumnIndex(CLEANTYPE));
        info.contenttype = cursor.getInt(cursor.getColumnIndex(CONTENTTYPE));

        info.cmtype = cursor.getInt(cursor.getColumnIndex(CMTYPE));
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

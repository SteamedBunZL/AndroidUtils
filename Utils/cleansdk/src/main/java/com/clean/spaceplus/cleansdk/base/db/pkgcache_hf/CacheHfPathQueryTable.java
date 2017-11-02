package com.clean.spaceplus.cleansdk.base.db.pkgcache_hf;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.PathQuery;
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

public class CacheHfPathQueryTable implements TableHelper<PathQuery>{

    private static final String TAG = CacheHfPathQueryTable.class.getSimpleName();

   // public final static String TABLE_NAME = "pathquery";
   public final static String TABLE_NAME = "routeinquery";

    //public final static String PATH_ID = "pathid";
    public final static String PATH_ID = "routeid";

    //public final static String PATH_TYPE = "pathtype";
    public final static String PATH_TYPE = "routetype";

    //public final static String PATH = "path";
    public final static String PATH = "route";

    //public final static String CLEAN_TYPE = "cleantype";
    public final static String CLEAN_TYPE = "cleartype";

    //public final static String CLEAN_TIME = "cleantime";
    public final static String CLEAN_TIME = "cleartime";

    //public final static String CLEANOP = "cleanop";
    public final static String CLEANOP = "clearopertype";

    //public final static String CONTENT_TYPE = "contenttype";
    public final static String CONTENT_TYPE = "filetype";

    //public final static String IS_NEED_CHECK = "isneedcheck";
    public final static String IS_NEED_CHECK = "ischeckneed";

    //public final static String CMTYPE = "cmtype";
    public final static String CMTYPE = "media_clean_type";

    //public final static String PRIVACY_TYPE = "privacytype";
    public final static String PRIVACY_TYPE = "secrettype";

    //public final static String TEST = "test";
    public final static String TEST = "debug";

    public final static String LANG_NAME_DESC = "langnamedesc";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(PathQuery t) {
        ContentValues cv = new ContentValues();
        cv.put(PATH_ID, t.pathid);
        cv.put(PATH_TYPE, t.pathtype);
        cv.put(PATH, t.path);
        cv.put(CLEAN_TYPE, t.cleantype);
        cv.put(CLEAN_TIME, t.cleantime);
        cv.put(CLEANOP, t.cleanop);
        cv.put(CONTENT_TYPE, t.contenttype);
        cv.put(IS_NEED_CHECK, t.isneedcheck);
        cv.put(CMTYPE, t.cmtype);
        cv.put(PRIVACY_TYPE, t.privacytype);
        cv.put(TEST, t.test);
        cv.put(LANG_NAME_DESC, t.langnamedesc);

        return cv;
    }

    @Override
    public PathQuery parseCursor(Cursor cursor) {

        PathQuery info = new PathQuery();
        info.pathid = cursor.getInt(cursor.getColumnIndex(PATH_ID));
        info.pathtype = cursor.getInt(cursor.getColumnIndex(PATH_TYPE));
        info.path = cursor.getInt(cursor.getColumnIndex(PATH));
        info.cleantype = cursor.getInt(cursor.getColumnIndex(CLEAN_TYPE));
        info.cleantime = cursor.getInt(cursor.getColumnIndex(CLEAN_TIME));
        info.cleanop = cursor.getInt(cursor.getColumnIndex(CLEANOP));
        info.contenttype = cursor.getInt(cursor.getColumnIndex(CONTENT_TYPE));
        info.isneedcheck = cursor.getInt(cursor.getColumnIndex(IS_NEED_CHECK));
        info.cmtype = cursor.getInt(cursor.getColumnIndex(CMTYPE));
        info.privacytype = cursor.getInt(cursor.getColumnIndex(PRIVACY_TYPE));
        info.test = cursor.getInt(cursor.getColumnIndex(TEST));
        info.langnamedesc = cursor.getString(cursor.getColumnIndex(LANG_NAME_DESC));

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
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0) primary key, ", PATH_ID);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", PATH_TYPE);
        StringUtils.appendFormat(sb, "[%s] INTEGER, ", PATH);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CLEAN_TYPE);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CLEAN_TIME);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CLEANOP);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CONTENT_TYPE);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", IS_NEED_CHECK);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CMTYPE);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", PRIVACY_TYPE);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", TEST);
        StringUtils.appendFormat(sb, "[%s] TEXT )", LANG_NAME_DESC);
        NLog.i(TAG , " sqls " + sb.toString());
        sqls.add(sb.toString());

        StringBuffer sbIndex = new StringBuffer();
        StringUtils.appendFormat(sbIndex, "CREATE INDEX IF NOT EXISTS INDEX_ID_PATH ON %s(%s, %s)"
                , TABLE_NAME, PATH, PATH_ID);
        sqls.add(sbIndex.toString());

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

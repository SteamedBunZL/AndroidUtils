package com.clean.spaceplus.cleansdk.base.db.pkgcache;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.PathQuery;
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

public class CachePathQueryTable implements TableHelper<PathQuery>{

    private static final String TAG = CachePathQueryTable.class.getSimpleName();

    //public final static String TABLE_NAME = "pathquery";
    public final static String TABLE_NAME = "routeinquery";

    public final static String AUTO_INC_ID = "_id";

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

    //public final static String CMTYPE = "cmtype";
    public final static String CMTYPE = "media_clean_type";

    //public final static String PRIVACY_TYPE = "privacytype";
    public final static String PRIVACY_TYPE = "secrettype";

    //public final static String IS_NEED_CHECK = "isneedcheck";
    public final static String IS_NEED_CHECK = "ischeckneed";

    //public final static String SRC = "src";
    public final static String SRC = "source";

    //public final static String TEST = "test";
    public final static String TEST = "debug";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(PathQuery t) {
        ContentValues cv = new ContentValues();
        cv.put(AUTO_INC_ID, t._id);
        cv.put(PATH_ID, t.pathid);
        cv.put(PATH_TYPE, t.pathtype);
        cv.put(PATH, t.path);
        cv.put(CLEAN_TYPE, t.cleantype);
        cv.put(CLEAN_TIME, t.cleantime);
        cv.put(CLEANOP, t.cleanop);
        cv.put(CONTENT_TYPE, t.contenttype);
        cv.put(CMTYPE, t.cmtype);
        cv.put(PRIVACY_TYPE, t.privacytype);
        cv.put(IS_NEED_CHECK, t.isneedcheck);
        cv.put(SRC, t.src);
        cv.put(TEST, t.test);

        return cv;
    }

    @Override
    public PathQuery parseCursor(Cursor cursor) {

        PathQuery info = new PathQuery();
        info._id = cursor.getInt(cursor.getColumnIndex(_ID));
        info.pathid = cursor.getInt(cursor.getColumnIndex(PATH_ID));
        info.pathtype = cursor.getInt(cursor.getColumnIndex(PATH_TYPE));
        info.path = cursor.getString(cursor.getColumnIndex(PATH));
        info.cleantype = cursor.getInt(cursor.getColumnIndex(CLEAN_TYPE));
        info.cleantime = cursor.getInt(cursor.getColumnIndex(CLEAN_TIME));
        info.cleanop = cursor.getInt(cursor.getColumnIndex(CLEANOP));
        info.contenttype = cursor.getInt(cursor.getColumnIndex(CONTENT_TYPE));
        info.cmtype = cursor.getInt(cursor.getColumnIndex(CMTYPE));
        info.privacytype = cursor.getInt(cursor.getColumnIndex(PRIVACY_TYPE));
        info.isneedcheck = cursor.getInt(cursor.getColumnIndex(IS_NEED_CHECK));
        info.src = cursor.getInt(cursor.getColumnIndex(SRC));
        info.test = cursor.getInt(cursor.getColumnIndex(TEST));

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
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", PATH_ID);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", PATH_TYPE);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", PATH);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CLEAN_TYPE);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CLEAN_TIME);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CLEANOP);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CONTENT_TYPE);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", CMTYPE);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", PRIVACY_TYPE);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", IS_NEED_CHECK);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", SRC);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0))", TEST);
        NLog.i(TAG , " sqls " + sb.toString());
        sqls.add(sb.toString());
        sqls.add(String.format("CREATE UNIQUE INDEX [pathindex] ON [%s] ([%s])", TABLE_NAME, PATH_ID));
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

package com.clean.spaceplus.cleansdk.base.db.pkgcache;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.LangQuery;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author shunyou.huang
 * @Description:LangQuery表构造
 * @date 2016/4/22 15:00
 * @copyright TCL-MIG
 */

public class CacheLangQueryTable implements TableHelper<LangQuery>{

    private static final String TAG = CacheLangQueryTable.class.getSimpleName();

    //public final static String TABLE_NAME = "langquery";
    public final static String TABLE_NAME = "langdesc";

    public final static String AUTO_INC_ID = "_id";
    //public final static String PATH_ID = "pathid";
    public final static String PATH_ID = "routeid";

    public final static String LANG = "lang";

    //public final static String SRC = "src";
    public final static String SRC = "source";

    public final static String TIME = "time";



    //public final static String NAME = "name";
    public final static String NAME = "cachetype";

    //public final static String DESC = "desc";
    public final static String DESC = "cachedesc";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(LangQuery t) {
        ContentValues cv = new ContentValues();
        cv.put(AUTO_INC_ID, t._id);
        cv.put(PATH_ID, t.pathid);
        cv.put(LANG, String.valueOf(t.lang));
        cv.put(TIME, t.time);
        cv.put(SRC, t.src);
        cv.put(NAME, t.name);
        cv.put(DESC, t.desc);

        return cv;
    }

    @Override
    public LangQuery parseCursor(Cursor cursor) {

        LangQuery info = new LangQuery();
        info._id = cursor.getInt(cursor.getColumnIndex(_ID));
        info.pathid = cursor.getInt(cursor.getColumnIndex(PATH_ID));
        info.lang = cursor.getString(cursor.getColumnIndex(LANG)).charAt(0);
        info.time = cursor.getInt(cursor.getColumnIndex(TIME));
        info.src = cursor.getInt(cursor.getColumnIndex(SRC));
        info.name = cursor.getString(cursor.getColumnIndex(NAME));
        info.desc = cursor.getString(cursor.getColumnIndex(DESC));

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
        StringUtils.appendFormat(sb, "[%s] CHAR(8), ", LANG);
        StringUtils.appendFormat(sb, "[%s] LONG DEFAULT (0), ", TIME);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", SRC);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", NAME);
        StringUtils.appendFormat(sb, "[%s] TEXT)", DESC);
        NLog.i(TAG , " sqls " + sb.toString());
        sqls.add(sb.toString());
        sqls.add(String.format("CREATE UNIQUE INDEX [langindex] ON [%s] ([%s], [%s])", TABLE_NAME, PATH_ID, LANG));
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

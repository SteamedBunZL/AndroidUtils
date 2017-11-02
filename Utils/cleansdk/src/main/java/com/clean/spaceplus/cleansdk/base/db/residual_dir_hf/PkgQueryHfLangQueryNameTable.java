package com.clean.spaceplus.cleansdk.base.db.residual_dir_hf;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.LangQueryName;
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

public class PkgQueryHfLangQueryNameTable implements TableHelper<LangQueryName>{

    private static final String TAG = PkgQueryHfLangQueryNameTable.class.getSimpleName();
    //public final static String TABLE_NAME = "langqueryname";
    public final static String TABLE_NAME = "langnamedesc";
    public final static String AUTO_INC_ID = "_id";
    //public final static String NAME = "name";
    public final static String NAME = "desc";
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(LangQueryName t) {
        ContentValues cv = new ContentValues();
        cv.put(AUTO_INC_ID, t._id);
        cv.put(NAME, t.name);
        return cv;
    }

    @Override
    public LangQueryName parseCursor(Cursor cursor) {
        LangQueryName info = new LangQueryName();
        info._id = cursor.getInt(cursor.getColumnIndex(_ID));
        info.name = cursor.getString(cursor.getColumnIndex(NAME));
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
        StringUtils.appendFormat(sb, "[%s] TEXT )", NAME);
        NLog.i(TAG , " sqls " + sb.toString());
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
}

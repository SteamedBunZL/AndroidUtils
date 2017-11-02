package com.clean.spaceplus.cleansdk.base.db.pkgcache;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.DataVersions;
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

public class DataVersionTable implements TableHelper<DataVersions>{

    private static final String TAG = DataVersionTable.class.getSimpleName();

    public final static String TABLE_NAME = "data_versions";

    public final static String AUTO_INC_ID = "_id";
    public final static String NAME = "name";
    public final static String VERSION = "version";


    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(DataVersions t) {
        ContentValues cv = new ContentValues();
        cv.put(AUTO_INC_ID, t._id);
        cv.put(NAME, t.name);
        cv.put(VERSION, t.version);
        return cv;
    }

    @Override
    public DataVersions parseCursor(Cursor cursor) {
        DataVersions info = new DataVersions();
        info._id = cursor.getInt(cursor.getColumnIndex(_ID));
        info.name = cursor.getString(cursor.getColumnIndex(NAME));
        info.version = cursor.getString(cursor.getColumnIndex(VERSION));
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
        StringUtils.appendFormat(sb, "[%s] TEXT, ", NAME);
        StringUtils.appendFormat(sb, "[%s] TEXT)", VERSION);
        NLog.i(TAG , " sqls " + sb.toString());
        sqls.add(sb.toString());
        sqls.add(String.format("CREATE UNIQUE INDEX [data_verindex] ON [%s] ([%s])", TABLE_NAME, NAME));

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

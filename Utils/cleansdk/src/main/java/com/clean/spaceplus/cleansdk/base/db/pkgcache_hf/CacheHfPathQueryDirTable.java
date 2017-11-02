package com.clean.spaceplus.cleansdk.base.db.pkgcache_hf;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.PathQueryDir;
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

public class CacheHfPathQueryDirTable implements TableHelper<PathQueryDir>{

    private static final String TAG = CacheHfPathQueryDirTable.class.getSimpleName();

    public final static String TABLE_NAME = "pathquerydir";

    public final static String AUTO_INC_ID = "_id";
    public final static String DIR = "dir";


    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(PathQueryDir t) {
        ContentValues cv = new ContentValues();
        cv.put(AUTO_INC_ID, t._id);
        cv.put(DIR, t.dir);
        return cv;
    }

    @Override
    public PathQueryDir parseCursor(Cursor cursor) {
        PathQueryDir info = new PathQueryDir();
        info._id = cursor.getInt(cursor.getColumnIndex(_ID));
        info.dir = cursor.getString(cursor.getColumnIndex(DIR));
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
        StringUtils.appendFormat(sb, "[%s] TEXT )", DIR);
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

package com.clean.spaceplus.cleansdk.base.db.pkgcache_hf;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.PathQueryDirMd5;
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

public class CacheHfPathQueryDirMd5Table implements TableHelper<PathQueryDirMd5>{

    private static final String TAG = CacheHfPathQueryDirMd5Table.class.getSimpleName();

    public final static String TABLE_NAME = "pathquerydirmd5";

    public final static String AUTO_INC_ID = "_id";
    //public final static String DIR_MD5 = "dirmd5"; 新库需要取dirmd52字段
    public final static String DIR_MD5 = "dirmd52";


    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(PathQueryDirMd5 t) {
        ContentValues cv = new ContentValues();
        cv.put(AUTO_INC_ID, t._id);
        cv.put(DIR_MD5, t.dirmd5);
        return cv;
    }

    @Override
    public PathQueryDirMd5 parseCursor(Cursor cursor) {
        PathQueryDirMd5 info = new PathQueryDirMd5();
        info._id = cursor.getInt(cursor.getColumnIndex(_ID));
        info.dirmd5 = cursor.getString(cursor.getColumnIndex(DIR_MD5));
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
        StringUtils.appendFormat(sb, "[%s] TEXT )", DIR_MD5);
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

package com.clean.spaceplus.cleansdk.base.db.pkgcache_hf;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.SysCacheAlert;
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

public class SysCacheAlertTable implements TableHelper<SysCacheAlert>{

    private static final String TAG = SysCacheAlertTable.class.getSimpleName();

    public final static String TABLE_NAME = "syscachealert";
    public final static String PKG_ID = "pkgid";
    public final static String ALERT = "alert";


    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(SysCacheAlert t) {
        ContentValues cv = new ContentValues();
        cv.put(PKG_ID, t.pkgid);
        cv.put(ALERT, t.alert);
        return cv;
    }

    @Override
    public SysCacheAlert parseCursor(Cursor cursor) {
        SysCacheAlert info = new SysCacheAlert();
        info.pkgid = cursor.getInt(cursor.getColumnIndex(PKG_ID));
        info.alert = cursor.getString(cursor.getColumnIndex(ALERT));
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
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY, ", PKG_ID);
        StringUtils.appendFormat(sb, "[%s] TEXT )", ALERT);
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

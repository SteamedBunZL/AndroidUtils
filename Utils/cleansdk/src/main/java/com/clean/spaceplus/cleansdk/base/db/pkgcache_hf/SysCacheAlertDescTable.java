package com.clean.spaceplus.cleansdk.base.db.pkgcache_hf;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.SysCatcheAlertDesc;
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

public class SysCacheAlertDescTable implements TableHelper<SysCatcheAlertDesc>{

    private static final String TAG = SysCacheAlertDescTable.class.getSimpleName();

    public final static String TABLE_NAME = "syscachealertdesc";
    public final static String ID = "_id";
    public final static String ALERT = "alert";


    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(SysCatcheAlertDesc t) {
        ContentValues cv = new ContentValues();
        cv.put(ID, t._id);
        cv.put(ALERT, t.alert);
        return cv;
    }

    @Override
    public SysCatcheAlertDesc parseCursor(Cursor cursor) {
        SysCatcheAlertDesc info = new SysCatcheAlertDesc();
        info._id = cursor.getInt(cursor.getColumnIndex(ID));
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
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY AUTOINCREMENT, ", ID);
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

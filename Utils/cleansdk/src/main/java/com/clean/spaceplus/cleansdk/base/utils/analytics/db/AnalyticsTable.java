package com.clean.spaceplus.cleansdk.base.utils.analytics.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TDatabaseHelper;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/7/6 15:50
 * @copyright TCL-MIG
 */
public class AnalyticsTable<T extends Event> implements TableHelper<T> {

    private static final String TAG = AnalyticsTable.class.getSimpleName();

    public static final String TABLE_NAME = "analytics_info";
    public static final String _ID = "id";
    public static final String EVENT_INFO = "event_info";

    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY AUTOINCREMENT, ", _ID);
        StringUtils.appendFormat(sb, "[%s] TEXT)", EVENT_INFO);
        sqls.add(sb.toString());
        NLog.d(TAG, "crate AnalyticsTable sql = %s",sb.toString());
        return sqls;
    }

    @Override
    public Collection<String> getDropTableSqls() {
        return null;
    }

    @Override
    public Collection<String> getUpdateTableSqls(int oldVersion, int newVersion) {
        NLog.d(TDatabaseHelper.TAG, "AnalyticsTable getUpdateTableSqls oldVersion = "+oldVersion +", newVersion = "+newVersion);
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public ContentValues getContentValues(T t) {
        return null;
    }

    @Override
    public T parseCursor(Cursor cursor) {
        return null;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }
}

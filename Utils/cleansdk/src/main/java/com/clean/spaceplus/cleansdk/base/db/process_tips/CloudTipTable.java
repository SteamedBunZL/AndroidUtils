package com.clean.spaceplus.cleansdk.base.db.process_tips;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: ProcessTip表构造
 * @date 2016/6/27 13:49
 * @copyright TCL-MIG
 */
public class CloudTipTable implements TableHelper<CloudTipsModel> {


    public static final String TABLE_NAME = "process_cloud_tips";
    public static final String ID = "id";
    public static final String PACKAGE_NAME = "package_name_md5";
    public static final String LANUGAGE = "language";
    public static final String PROCESS_TIPS = "proc_tips";
    public static final String APK_TIPS = "app_tips";
    public static final String UPDATE_TIME = "update_time";

    @Override
    public ContentValues getContentValues(CloudTipsModel cloudTipsModel) {
        return null;
    }

    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER primary key autoincrement, ", ID);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", PACKAGE_NAME);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", LANUGAGE);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", PROCESS_TIPS);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", APK_TIPS);
        StringUtils.appendFormat(sb, "[%s] INTEGER )", UPDATE_TIME);
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

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public CloudTipsModel parseCursor(Cursor cursor) {
        return null;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }
}

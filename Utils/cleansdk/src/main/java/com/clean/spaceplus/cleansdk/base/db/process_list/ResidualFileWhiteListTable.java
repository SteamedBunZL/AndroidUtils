package com.clean.spaceplus.cleansdk.base.db.process_list;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description:ResidualFileWhiteList表构造
 * @date 2016/5/11 16:41
 * @copyright TCL-MIG
 */
public class ResidualFileWhiteListTable implements TableHelper<ResidualModel> {

    public static final String TAG = ResidualFileWhiteListTable.class.getSimpleName();
    public final static String TABLE_NAME = "ResidualFileWhiteList";
    public static final String ID = "id";
    public static final String PROCESS_NAME = "process_name";
    public static final String TITLE = "title";
    public static final String CHECKED="checked";
    @Override
    public ContentValues getContentValues(ResidualModel processModel) {
        int check=0;
        if(processModel.isChecked()){
            check=1;
        }
        ContentValues values = new ContentValues();
        values.put(PROCESS_NAME, processModel.getPkgName());
        values.put(TITLE, processModel.getTitle());
        values.put(CHECKED, check);
        return values;
    }

    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<String>();

        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY AUTOINCREMENT, ", ID);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", PROCESS_NAME);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", TITLE);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0))", CHECKED);
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

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ResidualModel parseCursor(Cursor cursor) {
        if(cursor == null || cursor.getCount() <= 0){
            return null;
        }

        ResidualModel model = new ResidualModel();

        int idx = cursor.getColumnIndex(ID);
        if(idx > -1){
            model.setId(cursor.getInt(idx));
        }

        idx = cursor.getColumnIndex(PROCESS_NAME);
        if(idx > -1){
            model.setPkgName(cursor.getString(idx));
        }

        idx = cursor.getColumnIndex(TITLE);
        if(idx > -1){
            model.setTitle(cursor.getString(idx));
        }

        idx = cursor.getColumnIndex(CHECKED);
        if(idx>-1){
            model.setId(cursor.getLong(idx));
        }

        model.mType = 1000;
        return model;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }
}

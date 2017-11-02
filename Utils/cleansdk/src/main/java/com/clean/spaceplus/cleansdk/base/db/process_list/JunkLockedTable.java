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
 * @Description: 锁定垃圾选项表构造
 * @date 2016/5/11 19:42
 * @copyright TCL-MIG
 */
public class JunkLockedTable implements TableHelper<JunkLockedModel> {

    public static final String TAG = JunkLockedTable.class.getSimpleName();
    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String FILE_PATH = "filepath";
    public static final String STATUS = "status";

    public static final String TABLE_NAME = "t_junk_locked";

    @Override
    public ContentValues getContentValues(JunkLockedModel model) {
        if (model == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(ID, model.getId());
        values.put(TYPE, model.getType());
        values.put(FILE_PATH, model.getFilePath());
        values.put(STATUS, model.getStatus());

        return values;
    }

    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<String>();

        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY, ", ID);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", TYPE);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", FILE_PATH);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0))", STATUS);
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
    public JunkLockedModel parseCursor(Cursor cursor) {
        JunkLockedModel junkLockedModel = null;
        if (cursor == null || cursor.getCount() <= 0) {
            return null;
        }
        junkLockedModel = new JunkLockedModel(
                JunkLockedModel.TYPE_ALL_SYS_CACHE);
        int index = cursor.getColumnIndex(ID);
        // 设置ID
        if (index > -1) {
            junkLockedModel.setId(cursor.getInt(index));
        }
        // 设置类型
        index = cursor.getColumnIndex(TYPE);
        if (index > -1) {
            junkLockedModel.setType(cursor.getInt(index));
        }
        // 设置状态
        index = cursor.getColumnIndex(STATUS);
        if (index > -1) {
            junkLockedModel.setStatus(cursor.getInt(index));
        }
        // 设置路径
        index = cursor.getColumnIndex(FILE_PATH);
        if (index > -1) {
            String filepath = cursor.getString(index);
            junkLockedModel.setFilePath(filepath);
        }
        return junkLockedModel;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }
}

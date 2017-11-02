package com.clean.spaceplus.cleansdk.base.db.process_list;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: APKParserCache表构造
 * @date 2016/5/12 10:27
 * @copyright TCL-MIG
 */
public class APKParserCacheTable implements TableHelper<APKModel> {

    public static final String TAG = APKParserCacheTable.class.getSimpleName();
    public static final String FILE_PATH = "filepath";
    public static final String SIZE = "size";
    public static final String LAST_MODIFIED = "lastmodified";
    public static final String PACKAGENAME = "packagename";
    public static final String VERSIONNAME = "versionname";
    public static final String VERSIONCODE = "versioncode";
    public static final String TITLE = "title";

    public static final String TABLE_NAME = "t_apk_parser_cache";

    @Override
    public ContentValues getContentValues(APKModel model) {
        if (model == null ) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(SIZE, model.getSize());
        values.put(LAST_MODIFIED, model.getModifyTime());
        values.put(FILE_PATH, model.getPath());
        values.put(PACKAGENAME, model.getPackageName());
        values.put(VERSIONNAME, model.getVersion());
        values.put(VERSIONCODE, model.getVersionCode());
        values.put(TITLE, model.getTitle());

        return values;
    }

    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", FILE_PATH);
        StringUtils.appendFormat(sb, "[%s] INTEGER, ", SIZE);
        StringUtils.appendFormat(sb, "[%s] INTEGER, ", LAST_MODIFIED);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", PACKAGENAME);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", VERSIONNAME);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", TITLE);
        StringUtils.appendFormat(sb, "[%s] TEXT) ", VERSIONCODE);
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
    public APKModel parseCursor(Cursor cursor) {
        APKModel apkModel = null;
        {
            String filePath = cursor.getString(cursor.getColumnIndex(FILE_PATH));
            if(TextUtils.isEmpty(filePath))
            {
                return null;
            }
            apkModel = new APKModel();
            apkModel.setPackageName(cursor.getString(cursor.getColumnIndex(PACKAGENAME)));//包名
            apkModel.setVersion(cursor.getString(cursor.getColumnIndex(VERSIONNAME)));//版本名
            apkModel.setVersionCode(cursor.getInt(cursor.getColumnIndex(VERSIONCODE)));//版本号
            apkModel.setPath(filePath);//路径
            apkModel.setSize(cursor.getLong(cursor.getColumnIndex(SIZE)));//大小
            apkModel.setModifyTime(cursor.getLong(cursor.getColumnIndex(LAST_MODIFIED)));//修改时间
            apkModel.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));//标题

        }
        return apkModel;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }
}

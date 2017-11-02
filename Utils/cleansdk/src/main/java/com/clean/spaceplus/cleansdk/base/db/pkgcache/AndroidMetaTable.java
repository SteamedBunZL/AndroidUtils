package com.clean.spaceplus.cleansdk.base.db.pkgcache;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.AndroidMetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author shunyou.huang
 * @Description:AndroidMeta表构造
 * @date 2016/4/22 15:00
 * @copyright TCL-MIG
 */

public class AndroidMetaTable implements TableHelper<AndroidMetadata>{

    private static final String TAG = AndroidMetaTable.class.getSimpleName();
    public final static String TABLE_NAME = "android_metadata";
    public final static String LOCALE = "locale";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(AndroidMetadata t) {

        ContentValues cv = new ContentValues();
        cv.put(LOCALE, t.locale);

        return cv;
    }

    @Override
    public AndroidMetadata parseCursor(Cursor cursor) {

        AndroidMetadata info = new AndroidMetadata();
        info.locale = cursor.getString(cursor.getColumnIndex(LOCALE));

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
        StringUtils.appendFormat(sb, "[%s] TEXT )", LOCALE);
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

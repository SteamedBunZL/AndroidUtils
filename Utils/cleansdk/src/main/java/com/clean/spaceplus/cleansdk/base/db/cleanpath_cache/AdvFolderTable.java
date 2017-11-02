package com.clean.spaceplus.cleansdk.base.db.cleanpath_cache;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.cleanpath_cache.AdvFolder;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/3 10:41
 * @copyright TCL-MIG
 */
public class AdvFolderTable implements TableHelper<AdvFolder> {
    private static final String TAG = AdvFolderTable.class.getSimpleName();


    //public final static String TABLE_NAME = "advfolder";
    //public final static String TABLE_NAME = "adv_clean_path";
    public final static String TABLE_NAME = "adv_path";
    public final static String AUTO_INC_ID = "_id";
    //public final static String PATH = "path";
    public final static String PATH = "md5";
    public final static String DESCRIBEINFO = "description";
    public final static String SRSID = "descid";
    public final static String DESC = "desc";


    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<>();

        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY AUTOINCREMENT, ", _ID);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", PATH);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", DESCRIBEINFO);
        StringUtils.appendFormat(sb, "[%s] INTEGER DEFAULT (0), ", SRSID);
        StringUtils.appendFormat(sb, "[%s] TEXT ) ", DESC);
        sqls.add(sb.toString());
        NLog.d(TAG, "crate AdvFolderTable sql = %s",sb.toString() );
        return sqls;
    }

    @Override
    public Collection<String> getDropTableSqls() {
        return null;
    }

    @Override
    public Collection<String> getUpdateTableSqls(int oldVersion, int newVersion) {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public ContentValues getContentValues(AdvFolder advFolder) {
        return null;
    }

    @Override
    public AdvFolder parseCursor(Cursor cursor) {
        return null;
    }

    @Override
    public String getProviderAuthority() {
        return null;
    }
}

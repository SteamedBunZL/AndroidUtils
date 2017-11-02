package com.clean.spaceplus.cleansdk.base.db.residual_dir_cache;

import android.content.ContentValues;
import android.database.Cursor;

import com.clean.spaceplus.cleansdk.base.db.StringUtils;
import com.clean.spaceplus.cleansdk.base.db.TableHelper;
import com.clean.spaceplus.cleansdk.main.bean.residual_cache.ResiducalLangQuery;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/14 14:29
 * @copyright TCL-MIG
 */
public class ResidualDirCacheLangQueryTable implements TableHelper<ResiducalLangQuery> {
    private static final String TAG = ResidualDirCacheDirQueryTable.class.getSimpleName();

    //public final static String TABLE_NAME = "langquery";
    public final static String TABLE_NAME = "dirlanginquery";
    public final static String _ID = "_id";
    //public final static String DIRID = "dirid";
    public final static String DIRID = "routeid";

    //public final static String LANG = "lang";
    public final static String LANG = "lan";

    //public final static String NAME = "name";
    public final static String NAME = "namedesc";

    //public final static String ALERT = "alert";
    public final static String ALERT = "alertdesc";
    public final static String DESC = "desc";


    @Override
    public Collection<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        StringUtils.appendFormat(sb, "CREATE TABLE if not exists [%s] (", TABLE_NAME);
        StringUtils.appendFormat(sb, "[%s] INTEGER PRIMARY KEY AUTOINCREMENT, ", _ID);
        StringUtils.appendFormat(sb, "[%s] integer default (0), ", DIRID);
        StringUtils.appendFormat(sb, "[%s] CHAR(8), ", LANG);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", NAME);
        StringUtils.appendFormat(sb, "[%s] TEXT, ", ALERT);
        StringUtils.appendFormat(sb, "[%s] TEXT) ", DESC);

        NLog.d(TAG , "crate residual_dir_cache langquery表 sqls: " + sb.toString());
        sqls.add(sb.toString());
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
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(ResiducalLangQuery residucalLangQuery) {
        return null;
    }

    @Override
    public ResiducalLangQuery parseCursor(Cursor cursor) {
        return null;
    }


    @Override
    public String getProviderAuthority() {
        return null;
    }
}

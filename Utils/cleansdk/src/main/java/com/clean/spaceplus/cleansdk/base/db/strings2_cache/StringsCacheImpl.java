package com.clean.spaceplus.cleansdk.base.db.strings2_cache;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.config.DBVersionConfigManager;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBImpl;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/28 13:24
 * @copyright TCL-MIG
 */
public class StringsCacheImpl extends BaseDBImpl {
    private final static int DB_VERSION = DBVersionConfigManager.DEFAULT_DB_VERSION_NUM;

    public StringsCacheImpl(Context mContext) {
        super(mContext);
    }

    @Override
    public String getDatabaseName() {
        return DBVersionConfigManager.getInstance().getDBName(DBVersionConfigManager.ADV_DESC_DB_TYPE);
    }

    @Override
    public int getDatabaseVersion() {
        return DB_VERSION;
    }
}

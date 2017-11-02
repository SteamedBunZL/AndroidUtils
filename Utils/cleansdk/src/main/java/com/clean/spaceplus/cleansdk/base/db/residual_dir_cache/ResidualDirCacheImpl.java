package com.clean.spaceplus.cleansdk.base.db.residual_dir_cache;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.config.DBVersionConfigManager;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBImpl;

/**
 * @author xiangxiang.liu
 * @Description:
 * @date 2016/4/22 14:40
 * @copyright TCL-MIG
 */
public class ResidualDirCacheImpl extends BaseDBImpl{
    private final static int DB_VERSION = DBVersionConfigManager.DEFAULT_DB_VERSION_NUM;

    public ResidualDirCacheImpl(Context mContext) {
        super(mContext);
    }


    @Override
    public String getDatabaseName() {
        return DBVersionConfigManager.getInstance().getDBName(DBVersionConfigManager.RESIDUAL_CACHE_DB_TYPE);
    }

    @Override
    public int getDatabaseVersion() {
        return DB_VERSION;
    }
}

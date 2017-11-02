package com.clean.spaceplus.cleansdk.base.db.pkgcache_hf;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.config.DBVersionConfigManager;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBImpl;

/**
 * @author shunyou.huang
 * @Description:PkgCache高频数据构造
 * @date 2016/4/22 14:50
 * @copyright TCL-MIG
 */

public class PkgCacheHfImpl extends BaseDBImpl{

    private final static int DB_VERSION = DBVersionConfigManager.DEFAULT_DB_VERSION_NUM;

    public PkgCacheHfImpl(Context mContext) {
        super(mContext);
    }


    @Override
    public String getDatabaseName() {
        return DBVersionConfigManager.getInstance().getDBName(DBVersionConfigManager.PKG_CACHE_HF_DB_TYPE);
    }

    @Override
    public int getDatabaseVersion() {
        return DB_VERSION;
    }
}

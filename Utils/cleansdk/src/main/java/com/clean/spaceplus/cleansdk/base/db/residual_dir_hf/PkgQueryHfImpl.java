package com.clean.spaceplus.cleansdk.base.db.residual_dir_hf;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.config.DBVersionConfigManager;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBImpl;

/**
 * @author shunyou.huang
 * @Description:PkgQuery构造器
 * @date 2016/4/22 14:50
 * @copyright TCL-MIG
 */

public class PkgQueryHfImpl extends BaseDBImpl{
    private final static int DB_VERSION = DBVersionConfigManager.DEFAULT_DB_VERSION_NUM;

    public PkgQueryHfImpl(Context mContext) {
        super(mContext);
    }


    @Override
    public String getDatabaseName() {
        return DBVersionConfigManager.getInstance().getDBName(DBVersionConfigManager.PKG_QUERY_HF_DB_TYPE);
    }

    @Override
    public int getDatabaseVersion() {
        return DB_VERSION;
    }
}

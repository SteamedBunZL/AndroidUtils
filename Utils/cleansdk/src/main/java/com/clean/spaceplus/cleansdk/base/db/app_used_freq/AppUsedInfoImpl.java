package com.clean.spaceplus.cleansdk.base.db.app_used_freq;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.config.DBVersionConfigManager;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBImpl;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/3 11:19
 * @copyright TCL-MIG
 */
public class AppUsedInfoImpl extends BaseDBImpl {
    private final static int DB_VERSION = DBVersionConfigManager.DEFAULT_DB_VERSION_NUM;

    public AppUsedInfoImpl(Context mContext) {
        super(mContext);
    }


    @Override
    public String getDatabaseName() {
        return DBVersionConfigManager.getInstance().getAppUsedFreqDBName();
    }

    @Override
    public int getDatabaseVersion() {
        return DB_VERSION;
    }
}

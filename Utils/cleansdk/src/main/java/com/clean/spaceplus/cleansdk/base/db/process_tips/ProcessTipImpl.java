package com.clean.spaceplus.cleansdk.base.db.process_tips;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.config.DBVersionConfigManager;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBImpl;

/**
 * @author zengtao.kuang
 * @Description:
 * @date 2016/6/27 14:49
 * @copyright TCL-MIG
 */
public class ProcessTipImpl extends BaseDBImpl{

    private final static int DB_VERSION = DBVersionConfigManager.DEFAULT_DB_VERSION_NUM;

    public ProcessTipImpl(Context mContext) {
        super(mContext);
    }

    @Override
    public String getDatabaseName() {
        return DBVersionConfigManager.getInstance().getProcessTipDBName();
    }

    @Override
    public int getDatabaseVersion() {
        return DB_VERSION;
    }
}

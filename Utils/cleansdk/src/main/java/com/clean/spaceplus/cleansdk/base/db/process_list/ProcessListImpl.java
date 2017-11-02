package com.clean.spaceplus.cleansdk.base.db.process_list;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.config.DBVersionConfigManager;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBImpl;

/**
 * @author zengtao.kuang
 * @Description:ProcessList数据构造
 * @date 2016/5/3 11:24
 * @copyright TCL-MIG
 */
public class ProcessListImpl extends BaseDBImpl{

    public final static String DB_NAME = "process_list.db";
    private final static int DB_VERSION = DBVersionConfigManager.DEFAULT_DB_VERSION_NUM;

    public ProcessListImpl(Context mContext) {
        super(mContext);
    }


    @Override
    public String getDatabaseName() {
        return DB_NAME;
    }

    @Override
    public int getDatabaseVersion() {
        return DB_VERSION;
    }
}

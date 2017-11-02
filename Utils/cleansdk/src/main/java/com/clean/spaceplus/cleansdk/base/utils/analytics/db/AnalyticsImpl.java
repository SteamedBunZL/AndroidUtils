package com.clean.spaceplus.cleansdk.base.utils.analytics.db;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.CommonDBData;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBImpl;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/7/6 14:55
 * @copyright TCL-MIG
 */
public class AnalyticsImpl extends BaseDBImpl {

    private static final String DB_NAME = CommonDBData.ANALYTICS_DB_NAME;
    private final static int DB_VERSION = 1;

    public AnalyticsImpl(Context mContext) {
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

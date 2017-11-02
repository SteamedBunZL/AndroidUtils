package com.clean.spaceplus.cleansdk.junk.engine;

import com.clean.spaceplus.cleansdk.base.db.MySQLiteDB;

import space.network.cleancloud.core.cache.KCacheDef;
import space.network.cleancloud.core.residual.KResidualDef;
import space.network.commondata.KCleanCloudEnv;
import space.network.commondata.KFalseData;
import space.network.util.KCleanCloudMiscHelper;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/6 17:17
 * @copyright TCL-MIG
 */
public class FalseFilterManagerImpl implements FalseFilterManager{
    FalseFilterManagerImpl() {
    }

    /**
     * 拉本地数据+写内存数据，每个模块初始化时要调用一次本方法
     *
     * @return
     */
    public FalseSignFilter getFalseDataByCategory(int key) {
        return null;
    }
}

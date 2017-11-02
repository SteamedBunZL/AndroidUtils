package com.clean.spaceplus.cleansdk.junk.cleancloud;

import space.network.commondata.KCleanCloudEnv;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/25 15:48
 * @copyright TCL-MIG
 */
public class CacheQueryStatistics extends QueryStatusStatistics{
    private CleanCloudGlue mCleanCloudGlue = null;
    public CacheQueryStatistics(CleanCloudGlue cleanCloudGlue, int type) {
        mCleanCloudGlue = cleanCloudGlue;
        if (cleanCloudGlue != null && cleanCloudGlue.isUseAbroadServer()) {
            if (type == KCleanCloudEnv.CloudQueryType.CACHE_PKG_QUERY) {
                m_mytype = KCleanCloudEnv.CloudQueryType.CACHE_PKG_QUERY_ABROAD;
            } else if (type == KCleanCloudEnv.CloudQueryType.CACHE_SHOW_QUERY) {
                m_mytype = KCleanCloudEnv.CloudQueryType.CACHE_SHOW_QUERY_ABROAD;
            }
        } else {
            m_mytype = type;
        }
    }

    public void setNetWorkType(int type) {
        m_network_type = type;
    }

    public void reportStatisticsToServer() {

    }
}

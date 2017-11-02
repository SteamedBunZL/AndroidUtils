package com.clean.spaceplus.cleansdk.junk.cleancloud.cache.cloud;

import com.clean.spaceplus.cleansdk.base.strategy.BaseStrategy;
import com.clean.spaceplus.cleansdk.base.strategy.NetStrategy;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudManager;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CloudQueryParamsUtil;
import com.hawkclean.framework.log.NLog;

import java.util.Collection;

import space.network.cleancloud.KCacheCloudQuery;

/**
 * @author Jerry
 * @Description:
 * @date 2016/7/4 15:39
 * @copyright TCL-MIG
 */
public class CachePkgNetQuery {
    public static final String TAG = CachePkgNetQuery.class.getSimpleName();

    public void queryByPkgName(final BaseStrategy bs){
        KCacheCloudQuery mCacheCloudQuery = CleanCloudManager.createCacheCloudQuery(true);
        Collection<KCacheCloudQuery.PkgQueryParam> queryDatas =  CloudQueryParamsUtil.getCacheQueryByPkgNameParams();
        if(queryDatas != null && queryDatas.size() > 0){
            mCacheCloudQuery.initialize(false);
            mCacheCloudQuery.queryByPkgName(queryDatas, new KCacheCloudQuery.PkgQueryCallback() {
                @Override
                public void onGetQueryId(int queryId) {
                    NLog.d(TAG, "onGetQueryId queryId = %d", queryId);
                }

                @Override
                public void onGetQueryResult(int queryId, Collection<KCacheCloudQuery.PkgQueryData> results, boolean queryComplete) {
                    NLog.d(TAG, "CachePkgNetQuery onGetQueryResult queryId = %d,queryComplete = %b",queryId, queryComplete);
                    if (queryComplete){
                        if (bs != null){
                            bs.setState(NetStrategy.StateValue.FINISH);
                        }
                    }
                }

                @Override
                public boolean checkStop() {
                    return false;
                }
            }, true, false);  // 修改by chaohao.zhou
        }
    }
}

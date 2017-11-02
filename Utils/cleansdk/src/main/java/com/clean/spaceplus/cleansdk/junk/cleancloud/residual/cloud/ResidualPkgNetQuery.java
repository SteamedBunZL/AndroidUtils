package com.clean.spaceplus.cleansdk.junk.cleancloud.residual.cloud;

import com.clean.spaceplus.cleansdk.base.strategy.BaseStrategy;
import com.clean.spaceplus.cleansdk.base.strategy.NetStrategy;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CloudQueryParamsUtil;
import com.clean.spaceplus.cleansdk.junk.cleancloud.cleanhelper.ResidualCloudQueryHolder;
import com.hawkclean.framework.log.NLog;

import java.util.Collection;

import space.network.cleancloud.KResidualCloudQuery;

/**
 * @author Jerry
 * @Description:
 * @date 2016/7/4 13:34
 * @copyright TCL-MIG
 */
public class ResidualPkgNetQuery {

    public static final String TAG = ResidualPkgNetQuery.class.getSimpleName();

    public void queryByPkgName(final BaseStrategy bs){
        KResidualCloudQuery ikResidualCloudQuery = ResidualCloudQueryHolder.createIKResidualCloudQuery(true);
        final Collection<String> pkgnames = CloudQueryParamsUtil.getResidualQueryByPkgNameParams();
        NLog.d(TAG, "ResidualPkgNetQuery queryByPkgName pkgnames = "+pkgnames);
        ikResidualCloudQuery.queryByPkgName(pkgnames, new KResidualCloudQuery.PkgQueryCallback() {
            @Override
            public void onGetQueryId(int queryId) {

            }

            @Override
            public void onGetQueryResult(int queryId, Collection<KResidualCloudQuery.PkgQueryData> results, boolean queryComplete) {
                NLog.d(TAG, "ResidualPkgNetQuery onGetQueryResult queryId = %d,queryComplete = %b",queryId, queryComplete);
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
        },false,false);
    }
}

//=============================================================================
/**
 * @file KResidualNetWorkPkgQuery.java
 * @brief
 */
//=============================================================================
package space.network.cleancloud.core.cache;


import android.content.Context;

import com.hawkclean.framework.log.NLog;

import java.util.Collection;

import space.network.cleancloud.KCacheCloudQuery;
import space.network.cleancloud.KNetWorkHelper;
import space.network.cleancloud.core.base.CleanCloudNetWorkBase;
import space.network.commondata.KPostConfigData;

public class KCacheNetWorkPkgQuery extends CleanCloudNetWorkBase<KCacheCloudQuery.PkgQueryData, KCacheCloudQuery.PkgQueryCallback> {


    public KCacheNetWorkPkgQuery(Context context,String[] urls) {
        super(context, urls);
        for (String url: urls){
            NLog.d(TAG, "KCacheNetWorkPkgQuery url = %s", url);
        }
    }

    @Override
    protected byte[] getPostData(KPostConfigData configData, Collection<KCacheCloudQuery.PkgQueryData> datas, KCacheCloudQuery.PkgQueryCallback callback) {
        return KCachePkgQueryDataEnDeCode.getPostData(
                datas,
                configData.mChannelId,
                configData.mVersion,
                configData.mLang,
                configData.mXaid,
                configData.mMCC,
                configData.mPostDataEnCodeKey);
    }

    @Override
    protected boolean decodeResultData(KPostConfigData configData, Collection<KCacheCloudQuery.PkgQueryData> datas, KNetWorkHelper.PostResult postResult) {
        return KCachePkgQueryDataEnDeCode.decodeAndsetResultToQueryData(
                postResult.mResponse,
                configData.mResponseDecodeKey,
                datas);
    }
}
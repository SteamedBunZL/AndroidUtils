//=============================================================================
/**
 * @file KResidualNetWorkQuery.java
 * @brief
 */
//=============================================================================
package space.network.cleancloud.core.residual.dir;

import android.content.Context;

import com.hawkclean.framework.log.NLog;

import java.util.Collection;

import space.network.cleancloud.KNetWorkHelper;
import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.core.base.CleanCloudNetWorkBase;
import space.network.commondata.KPostConfigData;

public class KResidualNetWorkDirQuery extends CleanCloudNetWorkBase<KResidualCloudQuery.DirQueryData, KResidualCloudQuery.DirQueryCallback> {

    public KResidualNetWorkDirQuery(Context context, String[] urls) {
        super(context, urls);
        for (String url: urls){
            NLog.d(TAG, "KResidualNetWorkDirQuery url = %s", url);
        }
    }

    @Override
    protected byte[] getPostData(KPostConfigData configData, Collection<KResidualCloudQuery.DirQueryData> datas, KResidualCloudQuery.DirQueryCallback callback) {
        return KDirQueryDataEnDeCode.getPostData(
                datas,
                configData.mChannelId,
                configData.mVersion,
                configData.mLang,
                configData.mXaid,
                configData.mMCC,
                configData.mPostDataEnCodeKey);
    }

    @Override
    protected boolean decodeResultData(KPostConfigData configData, Collection<KResidualCloudQuery.DirQueryData> datas, KNetWorkHelper.PostResult postResult) {
        return KDirQueryDataEnDeCode.decodeAndsetResultToQueryData(
                postResult.mResponse,
                configData.mResponseDecodeKey,
                datas);
    }
}
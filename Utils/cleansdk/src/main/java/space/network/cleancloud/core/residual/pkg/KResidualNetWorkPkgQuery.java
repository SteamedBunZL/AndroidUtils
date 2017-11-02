//=============================================================================
/**
 * @file KResidualNetWorkPkgQuery.java
 * @brief
 */
//=============================================================================
package space.network.cleancloud.core.residual.pkg;

import android.content.Context;

import com.hawkclean.framework.log.NLog;

import java.util.Collection;

import space.network.cleancloud.KNetWorkHelper;
import space.network.cleancloud.KResidualCloudQuery.PkgQueryCallback;
import space.network.cleancloud.KResidualCloudQuery.PkgQueryData;
import space.network.cleancloud.core.base.CleanCloudNetWorkBase;
import space.network.commondata.KPostConfigData;


public class KResidualNetWorkPkgQuery extends CleanCloudNetWorkBase<PkgQueryData, PkgQueryCallback> {

    public KResidualNetWorkPkgQuery(Context context, String[] urls) {
        super(context, urls);
        for (String url: urls){
            NLog.d(TAG, "KResidualNetWorkDirQuery url = %s", url);
        }
    }

    @Override
    protected byte[] getPostData(KPostConfigData configData, Collection<PkgQueryData> datas, PkgQueryCallback callback) {
        return KPkgQueryDataEnDeCode.getPostData(
                datas,
                configData.mChannelId,
                configData.mVersion,
                configData.mLang,
                configData.mUuid,
                configData.mPostDataEnCodeKey);
    }


    @Override
    protected boolean decodeResultData(KPostConfigData configData, Collection<PkgQueryData> datas, KNetWorkHelper.PostResult postResult) {
        return KPkgQueryDataEnDeCode.decodeAndsetResultToQueryData(
                postResult.mResponse,
                configData.mResponseDecodeKey,
                datas);
    }
}
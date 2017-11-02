package com.clean.spaceplus.cleansdk.junk.cleancloud;


import android.content.Context;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.junk.cleancloud.cache.cloud.CacheCloudQueryImpl;
import com.clean.spaceplus.cleansdk.junk.cleancloud.residual.cloud.ResidualCloudQueryImpl;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import space.network.cleancloud.KCacheCloudQuery;
import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.MultiTaskTimeCalculator;
import space.network.cleancloud.MultiTaskTimeCalculatorImpl;
import space.network.util.KCleanCloudMiscHelper;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/23 11:32
 * @copyright TCL-MIG
 */
public class CleanCloudFactory {
    private static volatile CleanCloudGlue sCleanCloudGlue = new CleanCloudGlue();

    public static KResidualCloudQuery createResidualCloudQuery(boolean needNetQuery) {
        Context context = SpaceApplication.getInstance().getContext();
        if (null == context) {
            if(PublishVersionManager.isTest()){
                throw new IllegalStateException(
                        "AppGlobalData.setApplicationContext()  needs to be called"
                                + "before KCleanCloudFactroy.createResidualCloudQuery()");
            }
        }
        ResidualCloudQueryImpl result = new ResidualCloudQueryImpl(context, sCleanCloudGlue,needNetQuery);
        return result;
    }




    public static KCacheCloudQuery createCacheCloudQuery(boolean netQuery) {
        Context context = CleanCloudManager.getApplicationContext();
        if (null == context) {
            if(PublishVersionManager.isTest()){
                throw new IllegalStateException(
                        "CleanCloudManager.setApplicationContext()  needs to be called"
                                + "before CleanCloudFactroy.createCacheCloudQuery()");
            }
        }

        CacheCloudQueryImpl result = new CacheCloudQueryImpl(context, sCleanCloudGlue, netQuery);
        result.setParams(KCleanCloudMiscHelper.GetUuid(context), KCleanCloudMiscHelper.getCurrentVersion(context));
        return result;
    }

    public static MultiTaskTimeCalculator createMultiTaskTimeCalculator() {
        return new MultiTaskTimeCalculatorImpl();
    }

}

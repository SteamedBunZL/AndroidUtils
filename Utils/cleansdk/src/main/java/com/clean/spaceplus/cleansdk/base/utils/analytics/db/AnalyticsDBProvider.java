package com.clean.spaceplus.cleansdk.base.utils.analytics.db;

import android.content.Context;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBFactory;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDatabaseProvider;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/7/6 16:12
 * @copyright TCL-MIG
 */
public class AnalyticsDBProvider extends BaseDatabaseProvider{
    private static final String TAG = AnalyticsDBProvider.class.getSimpleName();

    private static AnalyticsDBProvider analyticsDBProvider;

    private AnalyticsDBProvider(Context context){
        onCreate(context, BaseDBFactory.getTableFactory(context, BaseDBFactory.TYPE_ANALYTICS));
    }

    public static AnalyticsDBProvider getInstance(){
        if (analyticsDBProvider == null){
            analyticsDBProvider = new AnalyticsDBProvider(SpaceApplication.getInstance().getContext());
        }
        return analyticsDBProvider;
    }


}

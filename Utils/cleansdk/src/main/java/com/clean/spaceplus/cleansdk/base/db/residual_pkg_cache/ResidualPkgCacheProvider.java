package com.clean.spaceplus.cleansdk.base.db.residual_pkg_cache;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.base.BaseDBFactory;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDatabaseProvider;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/3 11:23
 * @copyright TCL-MIG
 */
public class ResidualPkgCacheProvider extends BaseDatabaseProvider {
    private static ResidualPkgCacheProvider sCleanPathCacheProvider = null;

    public static synchronized ResidualPkgCacheProvider getInstance() {
        if (sCleanPathCacheProvider == null) {
            sCleanPathCacheProvider = new ResidualPkgCacheProvider(SpaceApplication.getInstance().getContext());
        }
        return sCleanPathCacheProvider;
    }

    private ResidualPkgCacheProvider(Context context){
        //onCreate(context,ResidualPkgCacheFactory.createFactory(context));
        onCreate(context, BaseDBFactory.getTableFactory(context,BaseDBFactory.TYPE_RESIDUAL_PKG_CACHE));
    }
}

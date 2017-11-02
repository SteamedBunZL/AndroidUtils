package com.clean.spaceplus.cleansdk.base.db.residual_dir_cache;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.base.BaseDBFactory;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDatabaseProvider;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/12 14:04
 * @copyright TCL-MIG
 */
public class ResidualDirCacheProvider extends BaseDatabaseProvider{
    private final static String TAG = ResidualDirCacheProvider.class.getSimpleName();
    private static ResidualDirCacheProvider mResidualDirCacheProvider = null;


    public static synchronized ResidualDirCacheProvider getInstance() {
        if (mResidualDirCacheProvider == null) {
            mResidualDirCacheProvider = new ResidualDirCacheProvider(SpaceApplication.getInstance().getContext());
        }
        return mResidualDirCacheProvider;
    }


    public ResidualDirCacheProvider(Context context){
        //nCreate(context, ResidualDirCacheFactory.createFactory(context));
        onCreate(context, BaseDBFactory.getTableFactory(context, BaseDBFactory.TYPE_RESIDUAL_DIR_CACHE));
    }



}

package com.clean.spaceplus.cleansdk.base.db.pkgcache;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.base.BaseDBFactory;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDatabaseProvider;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

/**
 * @author shunyou.huang
 * @Description:PkgCache数据
 * @date 2016/4/23 15:08
 * @copyright TCL-MIG
 */

public class PkgCacheProvider extends BaseDatabaseProvider{
    private final static String TAG = PkgCacheProvider.class.getSimpleName();

    private static PkgCacheProvider sPkgCacheProvider = null;

    public static synchronized PkgCacheProvider getInstance() {
        if (sPkgCacheProvider == null) {
            sPkgCacheProvider = new PkgCacheProvider(SpaceApplication.getInstance().getContext());
        }
        return sPkgCacheProvider;
    }

    private PkgCacheProvider(Context context){
        //onCreate(context,PkgCacheFactory.createFactory(context));
        onCreate(context, BaseDBFactory.getTableFactory(context, BaseDBFactory.TYPE_PKG_CACHE));
    }



}

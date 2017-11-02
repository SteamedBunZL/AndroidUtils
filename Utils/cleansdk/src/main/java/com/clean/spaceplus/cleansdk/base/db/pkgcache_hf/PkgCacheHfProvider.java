package com.clean.spaceplus.cleansdk.base.db.pkgcache_hf;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.base.BaseDBFactory;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDatabaseProvider;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

/**
 * @author shunyou.huang
 * @Description:PkgCache数据提供
 * @date 2016/4/23 15:08
 * @copyright TCL-MIG
 */

public class PkgCacheHfProvider extends BaseDatabaseProvider {
    private final static String TAG = PkgCacheHfProvider.class.getSimpleName();

    private static PkgCacheHfProvider sPkgCacheHfProvider = null;

    public static synchronized PkgCacheHfProvider getInstance() {
        if (sPkgCacheHfProvider == null) {
            sPkgCacheHfProvider = new PkgCacheHfProvider(SpaceApplication.getInstance().getContext());
        }
        return sPkgCacheHfProvider;
    }

    private PkgCacheHfProvider(Context context){
        onCreate(context, BaseDBFactory.getTableFactory(context,BaseDBFactory.TYPE_PKG_CACHE_HF));
    }

}

package com.clean.spaceplus.cleansdk.base.db.strings2_cache;

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
public class Strings2CacheProvider extends BaseDatabaseProvider {
    private static Strings2CacheProvider sCleanPathCacheProvider = null;

    public static synchronized Strings2CacheProvider getInstance() {
        if (sCleanPathCacheProvider == null) {
            sCleanPathCacheProvider = new Strings2CacheProvider(SpaceApplication.getInstance().getContext());
        }
        return sCleanPathCacheProvider;
    }

    private Strings2CacheProvider(Context context){
        //onCreate(context, Strings2CacheFactory.createFactory(context));
        onCreate(context, BaseDBFactory.getTableFactory(context,BaseDBFactory.TYPE_STRING2CACHE));
    }

}

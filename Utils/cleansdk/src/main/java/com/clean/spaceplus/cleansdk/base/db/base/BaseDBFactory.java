package com.clean.spaceplus.cleansdk.base.db.base;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.TableFactory;
import com.clean.spaceplus.cleansdk.base.db.app_used_freq.AppUsedFreqDBTableGenerator;
import com.clean.spaceplus.cleansdk.base.db.app_used_freq.AppUsedInfoImpl;
import com.clean.spaceplus.cleansdk.base.db.cleanpath_cache.CleanPathCacheDBTableGenerator;
import com.clean.spaceplus.cleansdk.base.db.cleanpath_cache.CleanPathCacheImpl;
import com.clean.spaceplus.cleansdk.base.db.pkgcache.CacheDBTableGenerator;
import com.clean.spaceplus.cleansdk.base.db.pkgcache.PkgCacheImpl;
import com.clean.spaceplus.cleansdk.base.db.pkgcache_hf.CacheHfDBTableGenerator;
import com.clean.spaceplus.cleansdk.base.db.pkgcache_hf.PkgCacheHfImpl;
import com.clean.spaceplus.cleansdk.base.db.process_list.ProcessListDBTableGenerator;
import com.clean.spaceplus.cleansdk.base.db.process_list.ProcessListImpl;
import com.clean.spaceplus.cleansdk.base.db.process_tips.ProcessTipImpl;
import com.clean.spaceplus.cleansdk.base.db.process_tips.ProcessTipsDBTableGenerator;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_cache.ResidualDirCacheDBTableGenerator;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_cache.ResidualDirCacheImpl;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.PkgQueryHfDBTableGenerator;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.PkgQueryHfImpl;
import com.clean.spaceplus.cleansdk.base.db.residual_pkg_cache.ResidualPkgCacheDBTableGenerator;
import com.clean.spaceplus.cleansdk.base.db.residual_pkg_cache.ResidualPkgCacheImpl;
import com.clean.spaceplus.cleansdk.base.db.strings2_cache.String2CacheDBTableGenerator;
import com.clean.spaceplus.cleansdk.base.db.strings2_cache.StringsCacheImpl;
import com.clean.spaceplus.cleansdk.base.utils.analytics.db.AnalyticsDBTableGenerator;
import com.clean.spaceplus.cleansdk.base.utils.analytics.db.AnalyticsImpl;
import com.hawkclean.framework.log.NLog;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.util.Hashtable;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/28 13:30
 * @copyright TCL-MIG
 */
public  abstract class BaseDBFactory {
    public static final String TAG = BaseDBFactory.class.getSimpleName();
    public static final int TYPE_STRING2CACHE = 1;
    public static final int TYPE_CLEANPATH = 2;
    public static final int TYPE_RESIDUAL_DIR_HF = 3;
    public static final int TYPE_RESIDUAL_DIR_CACHE = 4;
    public static final int TYPE_RESIDUAL_PKG_CACHE = 5;
    public static final int TYPE_PKG_CACHE_HF = 6;
    public static final int TYPE_PKG_CACHE = 7;

    public static final int TYPE_PROCESS_LIST = 8;
    public static final int TYPE_PROCESS_TIPS = 9;

    public static final int TYPE_APP_USED_FREQ = 10;
    public static final int TYPE_ANALYTICS = 11;

    private static final Hashtable<String, TableFactory> mFactoryTable = new Hashtable<>();

    private static TableFactory createFactory(Context context, int type) {
        BaseDBImpl dbImpl = null;
        BaseDBTableGenerator generator = null;
        switch (type) {
            case TYPE_STRING2CACHE:
                dbImpl = new StringsCacheImpl(context);
                generator = new String2CacheDBTableGenerator();
                break;
            case TYPE_CLEANPATH:
                dbImpl = new CleanPathCacheImpl(context);
                generator = new CleanPathCacheDBTableGenerator();
                break;
            case TYPE_RESIDUAL_DIR_HF:
                dbImpl = new PkgQueryHfImpl(context);
                generator = new PkgQueryHfDBTableGenerator();
                break;
            case TYPE_RESIDUAL_DIR_CACHE:
                dbImpl = new ResidualDirCacheImpl(context);
                generator = new ResidualDirCacheDBTableGenerator();
                break;
            case TYPE_RESIDUAL_PKG_CACHE:
                dbImpl = new ResidualPkgCacheImpl(context);
                generator = new ResidualPkgCacheDBTableGenerator();
                break;
            case TYPE_PKG_CACHE_HF:
                dbImpl = new PkgCacheHfImpl(context);
                generator = new CacheHfDBTableGenerator();
                break;
            case TYPE_PKG_CACHE:
                dbImpl = new PkgCacheImpl(context);
                generator = new CacheDBTableGenerator();
                break;
            case TYPE_PROCESS_LIST:
                dbImpl = new ProcessListImpl(context);
                generator = new ProcessListDBTableGenerator();
                break;
            case TYPE_PROCESS_TIPS:
                dbImpl = new ProcessTipImpl(context);
                generator = new ProcessTipsDBTableGenerator();
                break;
            case TYPE_APP_USED_FREQ:
                dbImpl = new AppUsedInfoImpl(context);
                generator = new AppUsedFreqDBTableGenerator();
                break;
            case TYPE_ANALYTICS:
                dbImpl = new AnalyticsImpl(context);
                generator = new AnalyticsDBTableGenerator();
                break;
            default:
                if (PublishVersionManager.isTest()) {
                    throw new RuntimeException("请设置正确的Type");
                }
        }
        List<TableCodec<?>> tableCodecs = generator.generateTableBeans();
        NLog.d(TAG, "createFactory tableCodecs = "+tableCodecs);
        if (tableCodecs == null || tableCodecs.size() == 0){
            return dbImpl;
        }
        for (TableCodec<?> codec: tableCodecs) {
            NLog.d(TAG, "createFactory add table  = %s", codec.clazz.getSimpleName());
            dbImpl.addHelper(codec.clazz, codec.helper, codec.dao);
        }
        return dbImpl;
    }

    public synchronized static TableFactory getTableFactory(Context context, int type) {
        TableFactory factory = mFactoryTable.get(String.valueOf(type));
        NLog.d(TAG, "getTableFactory type = %d, factoryName = %s", type,factory);
        if (factory == null) {
            factory = createFactory(context, type);
            mFactoryTable.put(String.valueOf(type), factory);
        }
        return factory;
    }
}

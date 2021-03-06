package com.clean.spaceplus.cleansdk.base.db.pkgcache_hf;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.TableFactory;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.DataVersions;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.LangQueryDesc;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.LangQueryDescParam;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.LangQueryFormatDesc;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.LangQueryName;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.PathQuery;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.PathQueryDir;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.PathQueryDirMd5;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.PkgQuery;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.SysCacheAlert;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.SysCatcheAlertDesc;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache_hf.Version;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shunyou.huang
 * @Description:PkgCache高频数据工厂类
 * @date 2016/4/22 14:40
 * @copyright TCL-MIG
 */

public class PkgCacheHfFactory {

    private static List<TableCodec<?>> codecs;
    private static TableFactory sTableFactory;
    static {
        codecs = new ArrayList<>();
        codecs.add(new TableCodec<>(DataVersions.class, new DataVersionTable(), null));
        codecs.add(new TableCodec<>(LangQueryDesc.class, new CacheHfLangDescTable(), null));
        codecs.add(new TableCodec<>(LangQueryDescParam.class, new CacheHfLangDescPreferTable(), null));
        codecs.add(new TableCodec<>(LangQueryFormatDesc.class, new CacheHfLangPreferDescTable(), null));
        codecs.add(new TableCodec<>(LangQueryName.class, new CacheHfLangNameDescTable(), null));
        codecs.add(new TableCodec<>(PathQuery.class, new CacheHfPathQueryTable(), null));
        codecs.add(new TableCodec<>(PathQueryDir.class, new CacheHfPathQueryDirTable(), null));
        codecs.add(new TableCodec<>(PathQueryDirMd5.class, new CacheHfPathQueryDirMd5Table(), null));
        codecs.add(new TableCodec<>(PkgQuery.class, new CacheHfPkgQueryTable(), null));
        codecs.add(new TableCodec<>(SysCacheAlert.class, new SysCacheAlertTable(), null));
        codecs.add(new TableCodec<>(SysCatcheAlertDesc.class, new SysCacheAlertDescTable(), null));
        codecs.add(new TableCodec<>(Version.class, new VersionTable(), null));
    }

    public synchronized static TableFactory createFactory(Context context) {
        if (sTableFactory == null) {
            //PkgCacheHfImpl impl = new PkgCacheHfImpl(context);
            PkgCacheHfImpl impl = new PkgCacheHfImpl(context);
            for (TableCodec<?> codec: codecs) {
                impl.addHelper(codec.clazz, codec.helper, codec.dao);
            }

            sTableFactory = impl;
        }

        return sTableFactory;
    }
}

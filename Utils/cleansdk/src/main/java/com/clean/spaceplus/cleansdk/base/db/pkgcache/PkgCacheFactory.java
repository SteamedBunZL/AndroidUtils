package com.clean.spaceplus.cleansdk.base.db.pkgcache;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.TableFactory;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.AndroidMetadata;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.DataVersions;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.LangQuery;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.PathQuery;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.PkgQueryInfo;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.Version;

import java.util.ArrayList;
import java.util.List;


/**
 * @author shunyou.huang
 * @Description:PkgCache工厂类
 * @date 2016/4/22 14:40
 * @copyright TCL-MIG
 */

public class PkgCacheFactory {

    private static List<TableCodec<?>> codecs;
    private static TableFactory sTableFactory;
    static {
        codecs = new ArrayList<>();

        codecs.add(new TableCodec<>(PkgQueryInfo.class, new CachePkgQueryTable(), null));
        codecs.add(new TableCodec<>(LangQuery.class, new CacheLangQueryTable(), null));
        codecs.add(new TableCodec<>(PathQuery.class, new CachePathQueryTable(), null));
        codecs.add(new TableCodec<>(Version.class, new VersionTable(), null));
        codecs.add(new TableCodec<>(DataVersions.class, new DataVersionTable(), null));
        codecs.add(new TableCodec<>(AndroidMetadata.class, new AndroidMetaTable(), null));

    }

    public synchronized static TableFactory createFactory(Context context) {
        if (sTableFactory == null) {
            PkgCacheImpl impl = new PkgCacheImpl(context);
            for (TableCodec<?> codec: codecs) {
                impl.addHelper(codec.clazz, codec.helper, codec.dao);
            }

            sTableFactory = impl;
        }

        return sTableFactory;
    }
}

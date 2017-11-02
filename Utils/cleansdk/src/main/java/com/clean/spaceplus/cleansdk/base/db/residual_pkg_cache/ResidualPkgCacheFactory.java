package com.clean.spaceplus.cleansdk.base.db.residual_pkg_cache;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.TableFactory;
import com.clean.spaceplus.cleansdk.main.bean.pkgquery_hf.PkgQuery;
import com.clean.spaceplus.cleansdk.main.bean.residual_cache.ResidualDirQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/3 11:18
 * @copyright TCL-MIG
 */
public class ResidualPkgCacheFactory {
    private static List<TableCodec<?>> codecs;
    private static TableFactory sTableFactory;
    static {
        codecs = new ArrayList<>();
        codecs.add(new TableCodec<>(PkgQuery.class, new ResidualPkgCachePkgQueryTable(), null));
        codecs.add(new TableCodec<>(ResidualDirQuery.class, new ResidualPkgCacheDirQueryTable(), null));
    }

    public synchronized static TableFactory createFactory(Context context) {
        if (sTableFactory == null) {
            ResidualPkgCacheImpl impl = new ResidualPkgCacheImpl(context);
            for (TableCodec<?> codec: codecs) {
                impl.addHelper(codec.clazz, codec.helper, codec.dao);
            }

            sTableFactory = impl;
        }

        return sTableFactory;
    }
}

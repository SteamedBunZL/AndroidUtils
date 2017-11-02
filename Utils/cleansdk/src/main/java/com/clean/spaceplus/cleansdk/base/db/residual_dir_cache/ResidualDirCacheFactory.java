package com.clean.spaceplus.cleansdk.base.db.residual_dir_cache;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.TableFactory;
import com.clean.spaceplus.cleansdk.main.bean.residual_cache.ResidualDirQuery;
import com.clean.spaceplus.cleansdk.main.bean.residual_cache.ResiducalLangQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiangxiang.liu
 * @Description:残留工厂类
 * @date 2016/4/22 14:40
 * @copyright TCL-MIG
 */
public class ResidualDirCacheFactory {

    private static List<TableCodec<?>> codecs;
    private static TableFactory sTableFactory;
    static {
        codecs = new ArrayList<>();
        codecs.add(new TableCodec(ResidualDirQuery.class, new ResidualDirCacheDirQueryTable(), null));
        codecs.add(new TableCodec(ResiducalLangQuery.class, new ResidualDirCacheLangQueryTable(), null));

    }

    public synchronized static TableFactory createFactory(Context context) {
        if (sTableFactory == null) {
           // ResidualDirCacheImpl impl = new ResidualDirCacheImpl(context);
            ResidualDirCacheImpl impl = new ResidualDirCacheImpl(context);
            for (TableCodec<?> codec: codecs) {
                impl.addHelper(codec.clazz, codec.helper, codec.dao);
            }

            sTableFactory = impl;
        }

        return sTableFactory;
    }
}

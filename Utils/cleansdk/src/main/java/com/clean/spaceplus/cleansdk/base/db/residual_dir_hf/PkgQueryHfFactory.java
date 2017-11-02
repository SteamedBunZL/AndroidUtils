package com.clean.spaceplus.cleansdk.base.db.residual_dir_hf;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.TableFactory;
import com.clean.spaceplus.cleansdk.main.bean.pkgquery_hf.PkgQuery;
import com.clean.spaceplus.cleansdk.main.bean.pkgquery_hf.RegDirQuery;
import com.clean.spaceplus.cleansdk.main.bean.residual_cache.ResidualDirQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shunyou.huang
 * @Description:PkgQuery高频工厂类
 * @date 2016/4/22 14:40
 * @copyright TCL-MIG
 */

public class PkgQueryHfFactory {

    private static List<TableCodec<?>> codecs;
    private static TableFactory sTableFactory;
    static {
        codecs = new ArrayList<>();
        codecs.add(new TableCodec(ResidualDirQuery.class, new PkgQueryHfDirQueryTable(), null));
        codecs.add(new TableCodec(PkgQuery.class, new com.clean.spaceplus.cleansdk.base.db.residual_pkg_cache.ResidualPkgCachePkgQueryTable(), null));
        codecs.add(new TableCodec(RegDirQuery.class, new PkgQueryHfRegDirQueryTable(), null));

    }

    public synchronized static TableFactory createFactory(Context context) {
        if (sTableFactory == null) {
            PkgQueryHfImpl impl = new PkgQueryHfImpl(context);
            for (TableCodec<?> codec: codecs) {
                impl.addHelper(codec.clazz, codec.helper, codec.dao);
            }

            sTableFactory = impl;
        }

        return sTableFactory;
    }
}

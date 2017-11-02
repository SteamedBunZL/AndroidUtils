package com.clean.spaceplus.cleansdk.base.db.residual_pkg_cache;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBTableGenerator;
import com.clean.spaceplus.cleansdk.main.bean.pkgquery_hf.PkgQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/29 16:33
 * @copyright TCL-MIG
 */
public class ResidualPkgCacheDBTableGenerator implements BaseDBTableGenerator{
    @Override
    public List<TableCodec<?>> generateTableBeans() {
        List<TableCodec<?>> codecs = new ArrayList<>();
        codecs.add(new TableCodec<>(PkgQuery.class, new ResidualPkgCachePkgQueryTable(), null));
        return codecs;
    }
}

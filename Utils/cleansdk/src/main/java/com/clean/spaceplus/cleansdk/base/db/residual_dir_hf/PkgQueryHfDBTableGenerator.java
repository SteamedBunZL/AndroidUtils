package com.clean.spaceplus.cleansdk.base.db.residual_dir_hf;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBTableGenerator;
import com.clean.spaceplus.cleansdk.main.bean.pkgquery_hf.PkgQuery;
import com.clean.spaceplus.cleansdk.main.bean.pkgquery_hf.RegDirQuery;
import com.clean.spaceplus.cleansdk.main.bean.residual_cache.ResidualDirQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/29 16:33
 * @copyright TCL-MIG
 */
public class PkgQueryHfDBTableGenerator implements BaseDBTableGenerator{
    @Override
    public List<TableCodec<?>> generateTableBeans() {
        List<TableCodec<?>> codecs = new ArrayList<>();
        codecs.add(new TableCodec(ResidualDirQuery.class, new PkgQueryHfDirQueryTable(), null));
        codecs.add(new TableCodec(PkgQuery.class, new com.clean.spaceplus.cleansdk.base.db.residual_pkg_cache.ResidualPkgCachePkgQueryTable(), null));
        codecs.add(new TableCodec(RegDirQuery.class, new PkgQueryHfRegDirQueryTable(), null));
        return codecs;
    }
}

package com.clean.spaceplus.cleansdk.base.db.residual_dir_cache;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBTableGenerator;
import com.clean.spaceplus.cleansdk.main.bean.residual_cache.ResidualDirQuery;
import com.clean.spaceplus.cleansdk.main.bean.residual_cache.ResiducalLangQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/29 16:33
 * @copyright TCL-MIG
 */
public class ResidualDirCacheDBTableGenerator implements BaseDBTableGenerator {
    @Override
    public List<TableCodec<?>> generateTableBeans() {
        List<TableCodec<?>> codecs = new ArrayList<>();
        codecs.add(new TableCodec(ResidualDirQuery.class, new ResidualDirCacheDirQueryTable(), null));
        codecs.add(new TableCodec(ResiducalLangQuery.class, new ResidualDirCacheLangQueryTable(), null));
        return codecs;
    }
}

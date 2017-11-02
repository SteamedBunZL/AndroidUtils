package com.clean.spaceplus.cleansdk.base.db.pkgcache;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBTableGenerator;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.AndroidMetadata;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.LangQuery;
import com.clean.spaceplus.cleansdk.main.bean.pkgcache.PkgQueryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/29 16:33
 * @copyright TCL-MIG
 */
public class CacheDBTableGenerator implements BaseDBTableGenerator{
    @Override
    public List<TableCodec<?>> generateTableBeans() {
        List<TableCodec<?>> codecs = new ArrayList<>();
        //packageinquery
        codecs.add(new TableCodec<>(PkgQueryInfo.class, new CachePkgQueryTable(), null));
        //routeinquery
        codecs.add(new TableCodec<>(com.clean.spaceplus.cleansdk.main.bean.pkgcache.PathQuery.class, new CachePathQueryTable(), null));
        //langdesc
        codecs.add(new TableCodec<>(LangQuery.class, new CacheLangQueryTable(), null));



        codecs.add(new TableCodec<>(com.clean.spaceplus.cleansdk.main.bean.pkgcache.Version.class, new com.clean.spaceplus.cleansdk.base.db.pkgcache.VersionTable(), null));
        codecs.add(new TableCodec<>(com.clean.spaceplus.cleansdk.main.bean.pkgcache.DataVersions.class, new com.clean.spaceplus.cleansdk.base.db.pkgcache.DataVersionTable(), null));
        codecs.add(new TableCodec<>(AndroidMetadata.class, new AndroidMetaTable(), null));
        return codecs;
    }
}

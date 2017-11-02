package com.clean.spaceplus.cleansdk.base.db.cleanpath_cache;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBTableGenerator;
import com.clean.spaceplus.cleansdk.main.bean.cleanpath_cache.AdvFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/29 10:04
 * @copyright TCL-MIG
 */
public class CleanPathCacheDBTableGenerator implements BaseDBTableGenerator {
    @Override
    public List<TableCodec<?>> generateTableBeans() {
        List<TableCodec<?>> codecs = new ArrayList<>();
        codecs.add(new TableCodec<>(AdvFolder.class, new AdvFolderTable(), null));
        return codecs;
    }
}

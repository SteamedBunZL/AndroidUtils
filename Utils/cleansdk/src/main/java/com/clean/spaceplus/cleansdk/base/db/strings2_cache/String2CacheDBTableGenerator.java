package com.clean.spaceplus.cleansdk.base.db.strings2_cache;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBTableGenerator;
import com.clean.spaceplus.cleansdk.main.bean.string2_cache.AdvFolderDescribeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/29 10:04
 * @copyright TCL-MIG
 */
public class String2CacheDBTableGenerator implements BaseDBTableGenerator {
    @Override
    public List<TableCodec<?>> generateTableBeans() {
        List<TableCodec<?>> codecs = new ArrayList<>();
        codecs.add(new TableCodec<>(AdvFolderDescribeInfo.class, new AdvfolderDescribeinfoTable(), null));
        return codecs;
    }
}

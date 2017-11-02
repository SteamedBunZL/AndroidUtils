package com.clean.spaceplus.cleansdk.base.db.process_list;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBTableGenerator;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/29 10:04
 * @copyright TCL-MIG
 */
public class ProcessListDBTableGenerator implements BaseDBTableGenerator {
    @Override
    public List<TableCodec<?>> generateTableBeans() {
        List<TableCodec<?>> codecs = new ArrayList<>();
        codecs.add(new TableCodec<>(ProcessWhiteList.class, new ProcessWhiteListTable(), null));
        codecs.add(new TableCodec<>(ProcessModel.class,new JunkApkWhiteListTable(),null));
        codecs.add(new TableCodec<>(CacheProcessModel.class,new CacheWhiteListTable(),null));
        codecs.add(new TableCodec<>(ResidualModel.class,new ResidualFileWhiteListTable(),null));
        codecs.add(new TableCodec<>(JunkLockedModel.class,new JunkLockedTable(),null));
        codecs.add(new TableCodec<>(APKModel.class,new APKParserCacheTable(),null));
        return codecs;
    }
}

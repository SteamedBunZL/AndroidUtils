package com.clean.spaceplus.cleansdk.base.db.process_list;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.TableFactory;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description:ProcessList数据工厂类
 * @date 2016/5/3 11:23
 * @copyright TCL-MIG
 */
public class ProcessListFactory {

    private static List<TableCodec<?>> codecs;
    private static TableFactory sTableFactory;
    static {
        codecs = new ArrayList<>();
        codecs.add(new TableCodec<>(ProcessWhiteList.class, new ProcessWhiteListTable(), null));
        codecs.add(new TableCodec<>(ProcessModel.class,new JunkApkWhiteListTable(),null));
        codecs.add(new TableCodec<>(CacheProcessModel.class,new CacheWhiteListTable(),null));
        //codecs.add(new TableCodec<>(AppUsedInfoRecord.class,new AppUsedInfoTable(),null));
        codecs.add(new TableCodec<>(ResidualModel.class,new ResidualFileWhiteListTable(),null));
        codecs.add(new TableCodec<>(JunkLockedModel.class,new JunkLockedTable(),null));
        codecs.add(new TableCodec<>(APKModel.class,new APKParserCacheTable(),null));
    }

    public synchronized static TableFactory createFactory(Context context) {
        if (sTableFactory == null) {
            ProcessListImpl impl = new ProcessListImpl(context);
            for (TableCodec<?> codec: codecs) {
                impl.addHelper(codec.clazz, codec.helper, codec.dao);
            }

            sTableFactory = impl;
        }

        return sTableFactory;
    }


}

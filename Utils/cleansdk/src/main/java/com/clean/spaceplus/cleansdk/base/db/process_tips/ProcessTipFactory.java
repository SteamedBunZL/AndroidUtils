package com.clean.spaceplus.cleansdk.base.db.process_tips;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.TableFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description:
 * @date 2016/6/27 14:32
 * @copyright TCL-MIG
 */
public class ProcessTipFactory {
    private static List<TableCodec<?>> codecs;
    private static TableFactory sTableFactory;
    static {
        codecs = new ArrayList<>();
        codecs.add(new TableCodec<>(CloudTipsModel.class, new CloudTipTable(), null));
        codecs.add(new TableCodec<>(LocalTip1Model.class, new LocalLabelTable(), null));
        codecs.add(new TableCodec<>(LocalTip2Model.class, new LocalProcessTipsTable(), null));
        codecs.add(new TableCodec<>(LocalTip3Model.class, new PackageNameMD5Table(), null));
        codecs.add(new TableCodec<>(LocalTip4Model.class, new StringContentTable(), null));
    }

    public synchronized static TableFactory createFactory(Context context) {
        if (sTableFactory == null) {
            ProcessTipImpl impl = new ProcessTipImpl(context);
            for (TableCodec<?> codec: codecs) {
                impl.addHelper(codec.clazz, codec.helper, codec.dao);
            }

            sTableFactory = impl;
        }

        return sTableFactory;
    }
}

package com.clean.spaceplus.cleansdk.base.db.process_tips;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBTableGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/29 10:04
 * @copyright TCL-MIG
 */
public class ProcessTipsDBTableGenerator implements BaseDBTableGenerator {
    @Override
    public List<TableCodec<?>> generateTableBeans() {
        List<TableCodec<?>> codecs = new ArrayList<>();
        codecs.add(new TableCodec<>(CloudTipsModel.class, new CloudTipTable(), null));
        codecs.add(new TableCodec<>(LocalTip1Model.class, new LocalLabelTable(), null));
        codecs.add(new TableCodec<>(LocalTip2Model.class, new LocalProcessTipsTable(), null));
        codecs.add(new TableCodec<>(LocalTip3Model.class, new PackageNameMD5Table(), null));
        codecs.add(new TableCodec<>(LocalTip4Model.class, new StringContentTable(), null));
        return codecs;
    }
}

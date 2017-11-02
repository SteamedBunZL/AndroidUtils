package com.clean.spaceplus.cleansdk.base.utils.analytics.db;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDBTableGenerator;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/7/6 15:47
 * @copyright TCL-MIG
 */
public class AnalyticsDBTableGenerator implements BaseDBTableGenerator {
    @Override
    public List<TableCodec<?>> generateTableBeans() {
        List<TableCodec<?>> codecs = new ArrayList<>();
        codecs.add(new TableCodec<>(Event.class, new AnalyticsTable(), null));
        return codecs;
    }
}

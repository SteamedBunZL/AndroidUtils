package com.clean.spaceplus.cleansdk.base.db.app_used_freq;

import com.clean.spaceplus.cleansdk.appmgr.service.AppUsedInfoRecord;
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
public class AppUsedFreqDBTableGenerator implements BaseDBTableGenerator {
    @Override
    public List<TableCodec<?>> generateTableBeans() {
        List<TableCodec<?>> codecs = new ArrayList<>();
        codecs.add(new TableCodec<>(AppUsedInfoRecord.class, new AppUsedInfoTable(), null));
        return codecs;
    }
}

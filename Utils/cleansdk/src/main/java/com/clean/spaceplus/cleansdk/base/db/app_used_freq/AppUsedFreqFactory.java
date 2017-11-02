//package com.clean.spaceplus.cleansdk.base.db.app_used_freq;
//
//import android.content.Context;
//
//import com.clean.spaceplus.cleansdk.appmgr.service.AppUsedInfoRecord;
//import com.clean.spaceplus.cleansdk.base.db.TableCodec;
//import com.clean.spaceplus.cleansdk.base.db.TableFactory;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Jerry
// * @Description:
// * @date 2016/5/3 11:18
// * @copyright TCL-MIG
// */
//public class AppUsedFreqFactory {
//    private static List<TableCodec<?>> codecs;
//    private static TableFactory sTableFactory;
//    static {
//        codecs = new ArrayList<>();
//        codecs.add(new TableCodec<>(AppUsedInfoRecord.class, new AppUsedInfoTable(), null));
//    }
//
//    public synchronized static TableFactory createFactory(Context context) {
//        if (sTableFactory == null) {
//            AppUsedInfoImpl impl = new AppUsedInfoImpl(context);
//            for (TableCodec<?> codec: codecs) {
//                impl.addHelper(codec.clazz, codec.helper, codec.dao);
//            }
//
//            sTableFactory = impl;
//        }
//
//        return sTableFactory;
//    }
//}

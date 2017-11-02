package com.clean.spaceplus.cleansdk.base.db.process_list;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.base.BaseDBFactory;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDatabaseProvider;

/**
 * @author zengtao.kuang
 * @Description:ProcessList数据提供
 * @date 2016/5/3 11:04
 * @copyright TCL-MIG
 */
public class ProcessListProvider extends BaseDatabaseProvider {

    private static final String TAG = ProcessListProvider.class.getSimpleName();

    private static ProcessListProvider sListProvider;

    public static synchronized ProcessListProvider getInstance(Context context) {
        if (sListProvider == null) {
            sListProvider = new ProcessListProvider(context);
        }
        return sListProvider;
    }

    private ProcessListProvider(Context context){
        //onCreate(context,ProcessListFactory.createFactory(context));
        onCreate(context, BaseDBFactory.getTableFactory(context,BaseDBFactory.TYPE_PROCESS_LIST));
    }


}

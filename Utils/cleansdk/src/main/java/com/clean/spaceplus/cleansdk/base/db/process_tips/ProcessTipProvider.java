package com.clean.spaceplus.cleansdk.base.db.process_tips;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.db.base.BaseDBFactory;
import com.clean.spaceplus.cleansdk.base.db.base.BaseDatabaseProvider;

/**
 * @author zengtao.kuang
 * @Description:
 * @date 2016/6/27 14:56
 * @copyright TCL-MIG
 */
public class ProcessTipProvider extends BaseDatabaseProvider {

    private static final String TAG = ProcessTipProvider.class.getSimpleName();

    private static ProcessTipProvider sProcessTipProvider;

    public static synchronized ProcessTipProvider getInstance(Context context) {
        if (sProcessTipProvider == null) {
            sProcessTipProvider = new ProcessTipProvider(context);
        }
        return sProcessTipProvider;
    }

    private ProcessTipProvider(Context context){
        //onCreate(context,ProcessTipFactory.createFactory(context));
        onCreate(context, BaseDBFactory.getTableFactory(context,BaseDBFactory.TYPE_PROCESS_TIPS));
    }

}

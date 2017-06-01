package com.tcl.zhanglong.utils.binderpool;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * Created by Steve on 17/4/7.
 */

public class ScanService extends Service{

    private Binder mBinderPool = new BinderPool.BinderPoolImpl();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinderPool;
    }


}

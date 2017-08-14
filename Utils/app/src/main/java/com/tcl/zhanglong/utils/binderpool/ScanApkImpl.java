package com.tcl.zhanglong.utils.binderpool;

import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.steve.commonlib.DebugLog;
import com.tcl.zhanglong.binder.aidl.IScanApk;
import com.tcl.zhanglong.binder.aidl.IScanApkListener;

/**
 * Created by Steve on 17/4/7.
 */

public class ScanApkImpl extends IScanApk.Stub{

    private RemoteCallbackList<IScanApkListener> list = new RemoteCallbackList<>();


    @Override
    public void startScanApk() throws RemoteException {
        DebugLog.d("startScanApk");
    }

    @Override
    public void stopScanApk() throws RemoteException {
        DebugLog.d("stopScanApk");
    }

    @Override
    public void registerListener(IScanApkListener listener) throws RemoteException {
        list.register(listener);
    }

    @Override
    public void unregisterListener(IScanApkListener listener) throws RemoteException {
        list.unregister(listener);
    }



}

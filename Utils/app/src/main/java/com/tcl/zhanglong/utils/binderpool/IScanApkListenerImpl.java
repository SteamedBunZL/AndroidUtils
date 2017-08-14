package com.tcl.zhanglong.utils.binderpool;

import android.os.RemoteException;

import com.steve.commonlib.DebugLog;
import com.tcl.zhanglong.binder.aidl.IScanApkListener;
import com.tcl.zhanglong.binder.aidl.ScanInfo;

/**
 * Created by Steve on 17/4/10.
 */

public class IScanApkListenerImpl extends IScanApkListener.Stub{


    @Override
    public void onScanApk(ScanInfo info) throws RemoteException {
        DebugLog.d("onScanApk is call");
    }
}

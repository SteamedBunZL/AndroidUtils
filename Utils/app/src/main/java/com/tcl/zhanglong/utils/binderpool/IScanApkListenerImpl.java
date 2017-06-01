package com.tcl.zhanglong.utils.binderpool;

import android.os.RemoteException;

import com.tcl.zhanglong.binder.aidl.IScanApkListener;
import com.tcl.zhanglong.binder.aidl.ScanInfo;
import com.tcl.zhanglong.utils.Utils.DebugLog;

/**
 * Created by Steve on 17/4/10.
 */

public class IScanApkListenerImpl extends IScanApkListener.Stub{


    @Override
    public void onScanApk(ScanInfo info) throws RemoteException {
        DebugLog.d("onScanApk is call");
    }
}

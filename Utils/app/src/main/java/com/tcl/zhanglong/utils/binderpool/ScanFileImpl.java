package com.tcl.zhanglong.utils.binderpool;

import android.os.RemoteException;

import com.steve.commonlib.DebugLog;
import com.tcl.zhanglong.binder.aidl.IScanFile;

/**
 * Created by Steve on 17/4/7.
 */

public class ScanFileImpl extends IScanFile.Stub{


    @Override
    public void startScanFile() throws RemoteException {
        DebugLog.d("startScanFile");
    }

    @Override
    public void stopScanFile() throws RemoteException {
        DebugLog.d("stopScanFile");
    }


}

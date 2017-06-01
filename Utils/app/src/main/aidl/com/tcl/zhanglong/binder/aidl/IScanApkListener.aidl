// IScanApkListener.aidl
package com.tcl.zhanglong.binder.aidl;
import com.tcl.zhanglong.binder.aidl.ScanInfo;

// Declare any non-default types here with import statements

interface IScanApkListener {

    void onScanApk(in ScanInfo info);

}

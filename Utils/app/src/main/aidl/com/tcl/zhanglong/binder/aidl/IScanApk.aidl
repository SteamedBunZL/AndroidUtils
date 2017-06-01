// IScanApk.aidl
package com.tcl.zhanglong.binder.aidl;

import com.tcl.zhanglong.binder.aidl.IScanApkListener;
import com.tcl.zhanglong.binder.aidl.ScanInfo;

// Declare any non-default types here with import statements

interface IScanApk {

   void startScanApk();

   void stopScanApk();

   void registerListener(IScanApkListener listener);

   void unregisterListener(IScanApkListener listener);
}

package com.tcl.security.virusengine.func_interface;

import com.tcl.security.virusengine.entry.PackagedScanTask;

/**
 * Created by Steve on 2016/8/4.
 */
public interface McAfeeScanTimer {

    void startTiming(PackagedScanTask task, long scanTime, IMcAfeeScanListener listener);
}

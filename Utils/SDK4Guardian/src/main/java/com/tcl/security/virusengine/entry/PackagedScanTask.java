package com.tcl.security.virusengine.entry;

import com.intel.security.vsm.ScanTask;

/**
 * Created by Steve on 2016/8/4.
 */
public class PackagedScanTask {

    public ScanTask scanTask;

    public volatile boolean isTimeout = false;

    public volatile boolean isCompleted = false;

    public PackagedScanTask(ScanTask scanTask, boolean isTimeout, boolean isCompleted) {
        this.scanTask = scanTask;
        this.isTimeout = isTimeout;
        this.isCompleted = isCompleted;
    }
}

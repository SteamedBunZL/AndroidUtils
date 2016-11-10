package com.tcl.security.virusengine.func_interface;

import com.tcl.security.virusengine.VirusEngine;
import com.tcl.security.virusengine.entry.ScanInfo;

/**
 * Created by Steve on 2016/6/28.
 */
public interface IRealTimeScanListener {

    /**
     * Invoke when the {@link VirusEngine#isVirus(String, IRealTimeScanListener)} that the realtime(实时监控) is excuting.
     * @param info
     */
    void onScanRealTimeComplete(ScanInfo info);

//    void onScanRealTimeFail();
}

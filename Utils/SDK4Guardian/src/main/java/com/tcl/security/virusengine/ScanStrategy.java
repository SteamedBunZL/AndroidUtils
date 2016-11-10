package com.tcl.security.virusengine;

import com.tcl.security.virusengine.func_interface.IScanListener;

/**
 * Created by Steve on 16/8/30.
 */
public interface ScanStrategy {

    void startScan(IScanListener listener,Object... obj);

    void cancelScan();


}

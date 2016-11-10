package com.tcl.security.virusengine.func_interface;


import com.tcl.security.virusengine.entry.ScanInfo;

/**
 * Created by Steve on 16/10/31.
 */

public interface IFileScanListener {

    void onStarted();

    void onCompleted(int i);

    void onScanned(ScanInfo info);

    void onProgress(int progress);

}

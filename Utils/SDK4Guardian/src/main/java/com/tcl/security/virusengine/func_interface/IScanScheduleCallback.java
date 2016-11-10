package com.tcl.security.virusengine.func_interface;

/**
 * 因为各种扫描都是异步的，所以需要回调通知上层扫描进度
 * Created by Steve on 16/8/31.
 */
public interface IScanScheduleCallback {

    void onScanStart();

    void onScanFinished();

    void onScanCanceled();
}

package com.tcl.security.virusengine.func_interface;

import com.tcl.security.virusengine.VirusEngine;
import com.tcl.security.virusengine.entry.ScanInfo;

import java.util.List;

/**
 * The interface of the ScanListener that use between engine and client
 * Created by Steve on 2016/5/5.
 */
public interface IScanListener {



    /**
     * Scan start
     * @param numeric the number of the scan
     */
    void onScanStart(int numeric);

    /**
     * Invoke when the {@link VirusEngine#cancelQuickScan()} is excute.
     *
     */
    //void onScanCancel();

    /**
     * Invoke when the normal scan is finished
     *
     */
    void onScanFinish(List<ScanInfo> resutList);


    /**
     * When one entry is scan complete the method is invoked
     * @param current the real number of the current scan entry
     * @param info
     */
    void onScanOneComplete(int current, ScanInfo info);


    /**
     * Fake progress,max is 100
     * @param numeric the current fake progress the max is 100.
     */
    void onScanProgress(int numeric);







}

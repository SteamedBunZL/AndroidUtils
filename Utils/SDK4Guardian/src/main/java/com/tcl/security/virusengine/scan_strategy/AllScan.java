package com.tcl.security.virusengine.scan_strategy;

import android.content.pm.PackageManager;

import com.tcl.security.virusengine.ScanStrategy;
import com.tcl.security.virusengine.func_interface.IRealTimeScanListener;
import com.tcl.security.virusengine.func_interface.IScanListener;
import com.tcl.security.virusengine.func_interface.IScanScheduleCallback;
import com.tcl.security.virusengine.utils.VirusLog;

/**
 * Created by Steve on 16/8/30.
 */
public class AllScan extends BaseScan implements ScanStrategy {

    VirusQuickScan mVirusQuickScan;
    IScanListener mListener;

    public AllScan() {
        mVirusQuickScan = new VirusQuickScan();
        mVirusQuickScan.setScheduleCallback(new VirusScanScheduleImpl());
    }


    @Override
    public void startScan(IScanListener listener,Object... obj) {
        mListener = listener;
        scanVirus(obj);
    }

    @Override
    public void cancelScan() {
        mVirusQuickScan.cancelScan();
    }

    /**
     * 对apk安装进行实时监控
     *
     * @param packageName
     * @param engine
     * @param listener
     * @throws PackageManager.NameNotFoundException
     */
    public void isVirus(String packageName, String engine, IRealTimeScanListener listener) throws PackageManager.NameNotFoundException {
        if (mVirusQuickScan != null)
            mVirusQuickScan.isVirus(packageName, engine, listener);
    }


    /**
     * 病毒扫描调度接口
     */
    public static class VirusScanScheduleImpl implements IScanScheduleCallback {

        @Override
        public void onScanStart() {
            VirusLog.i("VirusScan");
        }


        @Override
        public void onScanFinished() {

        }

        @Override
        public void onScanCanceled() {

        }
    }

    /**
     * 隐私扫描调度接口
     */
    class PrivacyScanScheduleImpl implements IScanScheduleCallback {

        @Override
        public void onScanStart() {

        }


        @Override
        public void onScanFinished() {

        }

        @Override
        public void onScanCanceled() {

        }
    }

    /**
     * 扫描病毒
     */
    public void scanVirus(Object... obj) {
        if (mVirusQuickScan != null && mListener != null)
            mVirusQuickScan.startScan(mListener,obj);
    }

}
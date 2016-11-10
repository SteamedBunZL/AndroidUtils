package com.tcl.security.virusengine;

import com.tcl.security.virusengine.func_interface.ScanMessage;

/**
 * Created by Steve on 2016/5/27.
 */
public class VirusScanMessage implements ScanMessage {

    /** This Scan Time*/
    private long mThisScanTime;

    /** Last Scan Time*/
    private long mLastScanTime;

    /** lib version*/
    private String mLibVerion;

    /** mcafeeengine version*/
    private String mMcafeeEngineVersion;

    @Override
    public long getThisScanTime() {
        return mThisScanTime;
    }

    @Override
    public long getLastScanTime() {
        return mLastScanTime;
    }

    @Override
    public void setThisScanTime(long time) {
        mLastScanTime = mThisScanTime;
        mThisScanTime = time;
    }

    @Override
    public String getLibVersion() {
        return mLibVerion;
    }

    @Override
    public String getMcafeeEngineVersion() {
        return mMcafeeEngineVersion;
    }

    @Override
    public void setLibVersion(String version) {
        mLibVerion = version;
    }

    @Override
    public void setMcafeeEngineVersion(String version) {
        mMcafeeEngineVersion = version;
    }

}

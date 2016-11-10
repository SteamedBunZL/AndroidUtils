package com.tcl.security.virusengine.func_interface;

/**
 * Created by Steve on 2016/5/27.
 */
public interface ScanMessage {

    /**
     * Get this Scan time.
     * @return
     */
    long getThisScanTime();

    /**
     * Get last Scan time.
     * @return
     */
    long getLastScanTime();

    /**
     * Set this Scan time.
     * @param time
     */
    void setThisScanTime(long time);

    /**
     * Get version of lib.
     * @return
     */
    String getLibVersion();

    /**
     * Get version of McafeeEngine.
     * @return
     */
    String getMcafeeEngineVersion();

    /**
     * Set version of lib.
     * @param version
     */
    void setLibVersion(String version);

    /**
     * Set McafeeEngine version
     * @param version
     */
    void setMcafeeEngineVersion(String version);
}

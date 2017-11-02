package com.clean.spaceplus.cleansdk.junk.engine.bean;

/**
 * Created by bruceliu on 2016/9/1.
 */

public class JunkScanSetting {

    //扫描应用缓存开关
    private boolean mbScanSdCache;
    //扫描系统缓存开关
    private boolean mbScanSysCache;
    //扫描广告垃圾开关
    private boolean mbScanAdDirCache;
    //扫描无用安装包开关
    private boolean mbScanApkFile;
    //扫描卸载残留开关
    private boolean mbScanRubbish;
    //扫描内存缓存开关
    private boolean mbScanProcess;
    //是否启用缓存数据
    private boolean mbScanCacheEnable;

    public boolean isMbScanSdCache() {
        return mbScanSdCache;
    }

    public void setMbScanSdCache(boolean mbScanSdCache) {
        this.mbScanSdCache = mbScanSdCache;
    }

    public boolean isMbScanSysCache() {
        return mbScanSysCache;
    }

    public void setMbScanSysCache(boolean mbScanSysCache) {
        this.mbScanSysCache = mbScanSysCache;
    }

    public boolean isMbScanAdDirCache() {
        return mbScanAdDirCache;
    }

    public void setMbScanAdDirCache(boolean mbScanAdDirCache) {
        this.mbScanAdDirCache = mbScanAdDirCache;
    }

    public boolean isMbScanApkFile() {
        return mbScanApkFile;
    }

    public void setMbScanApkFile(boolean mbScanApkFile) {
        this.mbScanApkFile = mbScanApkFile;
    }

    public boolean isMbScanRubbish() {
        return mbScanRubbish;
    }

    public void setMbScanRubbish(boolean mbScanRubbish) {
        this.mbScanRubbish = mbScanRubbish;
    }

    public boolean isMbScanProcess() {
        return mbScanProcess;
    }

    public void setMbScanProcess(boolean mbScanProcess) {
        this.mbScanProcess = mbScanProcess;
    }

    //只是扫描内存缓存的情况
    public boolean isOnlyScanProcess(){
        return mbScanProcess && !mbScanSdCache && !mbScanSysCache && !mbScanAdDirCache
                && !mbScanApkFile && !mbScanRubbish;
    }

    public boolean isMbScanCacheEnable() {
        return mbScanCacheEnable;
    }

    public void setMbScanCacheEnable(boolean mbScanCacheEnable) {
        this.mbScanCacheEnable = mbScanCacheEnable;
    }
}

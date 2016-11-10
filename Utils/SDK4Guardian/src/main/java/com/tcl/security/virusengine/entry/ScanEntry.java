package com.tcl.security.virusengine.entry;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.tcl.security.virusengine.Constants;

/**
 * Created by Steve on 2016/5/3.
 */
public class ScanEntry implements Comparable<ScanEntry> {

    /**Scan pass cache*/
    public static final int PASS_CACHE = 0x40;

    /**Scan pass tcl cloud*/
    public static final int PASS_TCL_CLOUD = 0x60;

    /**Scan pass mcafee cloud*/
    private static final int PASS_MACFEE_CLOUD = 0x80;

    /**Scan pass mcafee local engine*/
    private static final int PASS_MACFEE_ENGINE = 0x100;

    /**Scan pass real time*/
    public static final int REAL_TIME = 0x120;

    @IntDef({PASS_CACHE, PASS_TCL_CLOUD,PASS_MACFEE_CLOUD,PASS_MACFEE_ENGINE, REAL_TIME})
    public @interface Priority {}

    public String packageName;

    public String appName;

    public String appVersion;

    public int appVersionCode;

    public boolean shouldCache = false;

    public long ttl;

    public long freshtime;

    public boolean isLocalScanned = true;

    @Priority public int priority = PASS_CACHE;

    public String cacheKey;

    public boolean isCloudScaned = false;

    //APK路径  2016.10.13 数据上报新增字段 临时缓存 不存数据库
    public String publicSourceDir;

    /**新增字段，定位到实时扫描是哪个virusEngine发起此次的扫描*/
    public String virusEngine;

    public long cacheTime = Long.valueOf(Constants.DEFAULT_SCAN_RESULT_CACHE_TIME);

    public boolean isExpired() {
        return (this.ttl + freshtime) < System.currentTimeMillis();
    }

    public void setLocalScanned(boolean localScanned) {
        isLocalScanned = localScanned;
    }

    public boolean isLocalScanned(){
        return isLocalScanned;
    }

    @Override
    public int compareTo(@NonNull ScanEntry other) {
        int left = this.priority;
        int right = other.priority;
        return right - left;
    }


}
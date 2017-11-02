package com.clean.spaceplus.cleansdk.junk.cleanmgr;

import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkScanSetting;

import java.util.List;

/**
 * @author wangtianbao
 * @Description: 封装扫描和清理逻辑
 * @date 2016/7/18 15:11
 * @copyright TCL-MIG
 */

public interface CleanManager {

    void startScan();

    void stopScan();

    void interruptScan();//退出activity

    void startClean();

    void endClean();

    void setCallback(CleanCallback callback);

    boolean isCleanning();

    void onDestroy();

    @Deprecated
    void removeDataItem(CacheInfo info);

    @Deprecated
    void deleteDataItem(List<JunkModel> list);

    void setScanSetting(JunkScanSetting value);
}

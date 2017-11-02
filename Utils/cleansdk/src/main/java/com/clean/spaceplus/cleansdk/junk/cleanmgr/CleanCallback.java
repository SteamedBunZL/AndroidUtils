package com.clean.spaceplus.cleansdk.junk.cleanmgr;

import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkModel;

import java.util.List;

/**
 * @author wangtianbao
 * @Description: 垃圾管理相关回调，作用于JunkActivity
 * @date 2016/7/18 15:12
 * @copyright TCL-MIG
 */

public interface CleanCallback {

    void onScanStart();

    void  onScanNewDir(String dir);

    void onScanEnd(boolean isStop, List<JunkModel> datas, long checkedSize, long totalSize, long memorySize);

    void onUpdateCheckedSize(long checkSize);

    void onCleanStart();

    void  onCleanEnd(long cleanedSize);

    void onScanProgress(int percent);

    void onItemScanFinish(int flag, long size);

    void onInCacheTime();//在缓存时间内

    void onNeedNotClean();//<100b不清理
}

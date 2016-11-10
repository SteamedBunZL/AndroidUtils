package com.tcl.security.virusengine;

import android.os.Process;

import com.tcl.security.virusengine.cache.Cache;
import com.tcl.security.virusengine.cache.CacheHandle;
import com.tcl.security.virusengine.engine.CloudFilterEngine;
import com.tcl.security.virusengine.entry.ScanEntry;
import com.tcl.security.virusengine.entry.ScanInfo;
import com.tcl.security.virusengine.func_interface.ScanMessage;
import com.tcl.security.virusengine.func_interface.ScanResultDelivery;
import com.tcl.security.virusengine.network.NetworkChange;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Steve on 2016/4/29.
 */
public class CacheScanDispatcher extends Thread {

    private final CacheHandle mCacheHandle;

    private final ScanResultDelivery mDelivery;

    private final BlockingQueue<ScanEntry> mCacheQueue;

    private final BlockingQueue<ScanEntry> mCloudScanQueue;

    private final CloudFilterEngine mCloudFilterEngine;

    private final ScanMessage mScanMessage;

    public static int mCurrentNetState = NetworkChange.NETWORK_AVAILABLE;

    private volatile boolean mQuit = false;

    public CacheScanDispatcher(BlockingQueue<ScanEntry> cacheQueue, BlockingQueue<ScanEntry> scanQueue, CloudFilterEngine cloudEngine, CacheHandle cacheHandle, ScanResultDelivery delivery, ScanMessage scanMessage) {
        mCacheQueue = cacheQueue;
        mCloudScanQueue = scanQueue;
        mCloudFilterEngine = cloudEngine;
        mCacheHandle = cacheHandle;
        mDelivery = delivery;
        mScanMessage = scanMessage;
    }

    public void quit() {
        interrupt();
        mQuit = true;
    }



    @Override
    public void run() {

        setName("CacheScanDispatcher");

        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        ScanEntry entry;
        Cache.CacheEntry cacheEntry;
        ScanInfo info;

        while (true) {
            try {
                entry = mCacheQueue.take();
            } catch (InterruptedException e) {
                if (mQuit)
                    return;

                continue;
            }

            try {
                //先去缓存中去查
                cacheEntry = mCacheHandle.performHandleCache(entry.cacheKey,entry.appVersion, VirusScanQueue.getInstance().getScanMessage().getLibVersion(),24*60*60*1000);

                //如果entry = null 那么把这个APP交由抛给引擎去进行查杀
                if (cacheEntry == null||entry.priority== ScanEntry.REAL_TIME) {
//                    VirusLog.e("CacheEntry hits the cache %s fail and throw it to ScanQueue",entry.packageName);
                    if(entry.priority!= ScanEntry.REAL_TIME)
                        entry.priority = ScanEntry.PASS_TCL_CLOUD;

                    //TODO 这里要判断是否取光，然后通知云引擎缓存队列为空
                    synchronized (mCacheQueue){
                        if (mCacheQueue.peek()==null){
                            mCloudFilterEngine.setCacheEmpty(true);
                        }
                    }
//                    VirusLog.w("====Cache %s add cloudQueue %d",entry.packageName,i);
                    mCloudScanQueue.add(entry);
                    continue;
                }
//                VirusLog.e("CacheScan hits the cache %s.",entry.packageName);
                //交给ScanResultDelivery进行处理和传递
                synchronized (mCacheQueue){
                    if (mCacheQueue.peek()==null){
                        mCloudFilterEngine.setCacheEmpty(true);
                        mCloudFilterEngine.cc();
                    }
                }
//                VirusLog.e("====Cache %s post ui %d",entry.packageName,j);
                info = parseEntryToScanInfo(cacheEntry);

                mDelivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_ENTRY,info,entry);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    private ScanInfo parseEntryToScanInfo(Cache.CacheEntry entry) {
        return new ScanInfo(Constants.ScanInfo.FILE_TYPE_APK,entry.packageName,entry.virusName,entry.virusDescription,entry.scanState,entry.appName,entry.type,entry.risk_level,entry.suggest);
    }





}

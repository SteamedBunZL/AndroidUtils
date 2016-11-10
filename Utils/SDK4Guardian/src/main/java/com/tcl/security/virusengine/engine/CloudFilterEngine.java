package com.tcl.security.virusengine.engine;

import android.content.Context;

import com.tcl.security.virusengine.cache.CacheHandle;
import com.tcl.security.virusengine.entry.CloudTask;
import com.tcl.security.virusengine.entry.ScanEntry;
import com.tcl.security.virusengine.func_interface.CloudScheduling;
import com.tcl.security.virusengine.func_interface.ScanMessage;
import com.tcl.security.virusengine.func_interface.ScanResultDelivery;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Steve on 2016/4/29.
 */
public class CloudFilterEngine {

    /**
     * Default batch number
     */
    private static final int DEFAULT_BATCH_NUMERIC = 16;

    /**
     * Upload queue
     */
    private final LinkedBlockingQueue<ScanEntry> mUploadQueue;

    private final BlockingQueue<ScanEntry> mCloudScanQueue;

    private volatile boolean mCacheEmpty = false;

    /**
     * 云查杀任务取消
     */
    private volatile boolean mCanceled = false;

    private final McafeeCloudEngine mMcafeeCloudEngine;

    public static CopyOnWriteArrayList<CloudTask> cloudTaskList = new CopyOnWriteArrayList<>();

    private final CloudScheduling mCloudScheduling;


    public CloudFilterEngine(Context context, int batchNumeric, PriorityBlockingQueue<ScanEntry> macfeeScanQueue, PriorityBlockingQueue<ScanEntry> cloudScanQueue, ScanResultDelivery delivery, CacheHandle cacheHandle, McafeeCloudEngine mcafeeCloudEngine, McafeeFilterEngine mcafeeFilterEngine, ScanMessage time, CloudScheduling scheduling) {
        this.mUploadQueue = new LinkedBlockingQueue<>(CloudFilterEngine.DEFAULT_BATCH_NUMERIC);
        this.mCloudScanQueue = cloudScanQueue;
        this.mMcafeeCloudEngine = mcafeeCloudEngine;
        this.mCloudScheduling = scheduling;
    }

    public CloudFilterEngine(Context context, PriorityBlockingQueue<ScanEntry> aviraScanQueue, PriorityBlockingQueue<ScanEntry> cloudScanQueue, ScanResultDelivery delivery, CacheHandle cacheHandle, McafeeCloudEngine mcafeeCloudEngine, McafeeFilterEngine mcafeeFilterEngine, ScanMessage time, CloudScheduling scheduling) {
        this(context,DEFAULT_BATCH_NUMERIC, aviraScanQueue, cloudScanQueue, delivery, cacheHandle, mcafeeCloudEngine, mcafeeFilterEngine, time,scheduling);
    }

    public void isVirus(ScanEntry entry) throws Exception {
        if (entry.priority == ScanEntry.REAL_TIME) {
            mCloudScheduling.uploading(entry);
            return;
        }

        if (mCanceled) {
            return;
        }
        logic(entry);
        if (mCacheEmpty && mCloudScanQueue.peek() == null) {
            mCacheEmpty = false;
            VirusLog.w("=== isVirus()");
            finish();
        }
    }

    public void cc() throws Exception {
        if (mCacheEmpty && mCloudScanQueue.peek() == null) {
            mCacheEmpty = false;
            VirusLog.w("=== cc()");
            finish();
        }
    }

    public synchronized void cancel() {
        mCanceled = true;
        ((CloudSchedulingImpl)mCloudScheduling).setCanceled(true);
        //网络任务取消
        mMcafeeCloudEngine.cancel();
    }

    public synchronized void prepare() {
        mCanceled = false;
        ((CloudSchedulingImpl)mCloudScheduling).setCanceled(false);
        mCacheEmpty = false;
        cloudTaskList.clear();
    }

    private void logic(ScanEntry entry) throws Exception {
        boolean result = mUploadQueue.offer(entry);
        if (!result) {
            LinkedList<ScanEntry> list = new LinkedList<>();
            mUploadQueue.drainTo(list);
            if (!mUploadQueue.offer(entry))
                throw new IllegalStateException("什么情况？");
            if (list.size() > 0) {
                mCloudScheduling.uploading(list);
                VirusLog.w("=== logic()");
            }
        }

    }

    private void finish() throws Exception {
        if (mUploadQueue.peek() != null) {
            LinkedList<ScanEntry> list = new LinkedList<>();
            mUploadQueue.drainTo(list);
            if (list.size() > 0) {
                VirusLog.w("=== finish()");
                mCloudScheduling.uploading(list);
            }
        }

    }


    public void setCacheEmpty(boolean isEmpty) {
        mCacheEmpty = isEmpty;
    }


}

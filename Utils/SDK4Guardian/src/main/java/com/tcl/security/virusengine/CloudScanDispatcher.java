package com.tcl.security.virusengine;

import android.os.Process;

import com.tcl.security.virusengine.engine.CloudFilterEngine;
import com.tcl.security.virusengine.entry.ScanEntry;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Steve on 2016/4/29.
 */
class CloudScanDispatcher extends Thread {

    public static final int CLOUD_STATE_TCL = 0;

    public static final int CLOUD_STATE_MACFEE = 1;

    private final BlockingQueue<ScanEntry> mCloudScanQueue;

    private final CloudFilterEngine mCloudFilterEngine;

    private volatile boolean mQuit = false;


    public CloudScanDispatcher(BlockingQueue<ScanEntry> cloudScanQueue, CloudFilterEngine cloudFilterEngine) {
        this.mCloudScanQueue = cloudScanQueue;
        this.mCloudFilterEngine = cloudFilterEngine;
    }


    public void quit() {
        interrupt();
        mQuit = true;
    }

    @Override
    public void run() {

        setName("CloudScanDispatcher");

        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        ScanEntry entry;

        while (true) {
            try {
                entry = mCloudScanQueue.take();
            } catch (InterruptedException e) {
                if (mQuit)
                    return;

                continue;
            }

            try {
                //TODO 云查杀逻辑
                mCloudFilterEngine.isVirus(entry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



}

package com.tcl.security.virusengine.engine;

import android.content.Context;

import com.intel.security.SecurityContext;
import com.intel.security.vsm.ScanObserver;
import com.intel.security.vsm.ScanStrategy;
import com.intel.security.vsm.ScanTask;
import com.intel.security.vsm.VirusScan;
import com.intel.security.vsm.content.ScanApplications;
import com.intel.security.vsm.content.ScanCombination;
import com.intel.security.vsm.content.ScanSource;
import com.tcl.security.virusengine.VirusScanQueue;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Steve on 2016/5/19.
 */
public class McafeeCloudEngine {

    private final Context mContext;

    private final ScanStrategy mStrategy;

    private final CopyOnWriteArrayList<ScanTask> mTaskList = new CopyOnWriteArrayList<>();

    public McafeeCloudEngine(Context context){
        mContext = context;
        mStrategy = new CloudStrategy();
    }

    public void scan(ScanObserver observer) throws  Exception{
        boolean isEngineInitialized = SecurityContext.isInitialized(mContext);

        //引擎未初始化
        if (!isEngineInitialized){
            throw new Exception("Macfee Engine is not initialized");
        }

        VirusScan virusScan = VirusScanQueue.getInstance().getVirusScan();

        if (virusScan==null){
            VirusLog.e("VirusScan未初始化？？？？？？？？？");
            virusScan = (VirusScan) SecurityContext.getService(mContext, SecurityContext.VIRUS_SCAN);
        }


        ScanTask scanTask = virusScan.scan(new ScanApplications(true), new ScanStrategy() {
            @Override
            public int getTechnology(ScanSource scanSource) {
                return TECHNOLOGY_CLOUD;
            }
        },observer);
        mTaskList.add(scanTask);
    }


    public ScanTask scan(final List<ScanSource> list, ScanObserver observer)throws Exception{

        boolean isEngineInitialized = SecurityContext.isInitialized(mContext);

        //引擎未初始化
        if (!isEngineInitialized){
            VirusLog.e("McAfee云引擎未初始化？？？？？？？？？");
            throw new Exception("Macfee Engine is not initialized");
        }

        VirusScan virusScan = VirusScanQueue.getInstance().getVirusScan();

        if (virusScan==null){
            VirusLog.e("VirusScan未初始化？？？？？？？？？");
            virusScan = (VirusScan) SecurityContext.getService(mContext, SecurityContext.VIRUS_SCAN);
        }

        ScanTask scanTask = virusScan.scan(new ScanCombination() {
            @Override
            public Collection<ScanSource> getSources() {
                return list;
            }
        },mStrategy,observer);
        mTaskList.add(scanTask);
        return scanTask;
    }

    private class CloudStrategy implements ScanStrategy {

        @Override
        public int getTechnology(ScanSource scanSource) {
            return TECHNOLOGY_CLOUD;
        }
    }

    public void cancel(){
        for(ScanTask task:mTaskList){
            if (task!=null)
                task.cancel();
        }
    }

}

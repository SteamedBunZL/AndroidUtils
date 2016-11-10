package com.tcl.security.virusengine.scan_strategy;

import android.content.Context;
import android.text.TextUtils;

import com.intel.security.SecurityContext;
import com.intel.security.vsm.ScanObserver;
import com.intel.security.vsm.ScanResult;
import com.intel.security.vsm.ScanState;
import com.intel.security.vsm.ScanTask;
import com.intel.security.vsm.Threat;
import com.intel.security.vsm.content.ScanCombination;
import com.intel.security.vsm.content.ScanPath;
import com.intel.security.vsm.content.ScanSource;
import com.tcl.security.virusengine.Constants;
import com.tcl.security.virusengine.VirusScanQueue;
import com.tcl.security.virusengine.engine.CloudProcessFlurryHelper;
import com.tcl.security.virusengine.entry.ScanInfo;
import com.tcl.security.virusengine.func_interface.IFileScanListener;
import com.tcl.security.virusengine.utils.FileUtil;
import com.tcl.security.virusengine.utils.RiskUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Steve on 16/10/31.
 */

public class VirusFileScan {

    private AtomicBoolean mDestory = new AtomicBoolean();

    private long start;

    private AtomicLong mVirusNum = new AtomicLong();

    private AtomicLong mScanNum = new AtomicLong();

    public VirusFileScan(){
        mDestory.set(false);
    }


    public ScanTask scanFile(final List<String> pathArray, IFileScanListener listener){

        Context context = VirusScanQueue.getInstance().getContext();

        boolean isEngineInitialized = SecurityContext.isInitialized(context);

        //引擎未初始化
        if (!isEngineInitialized) {
            if (listener!=null)
                listener.onCompleted(Constants.FileScan.RESULT_FAILED);
            return null;
        }

        com.intel.security.vsm.VirusScan virusScan = VirusScanQueue.getInstance().getVirusScan();

        if (virusScan == null) {
            virusScan = (com.intel.security.vsm.VirusScan) SecurityContext.getService(context, SecurityContext.VIRUS_SCAN);
        }

        if (pathArray==null||pathArray.isEmpty()){
            if (listener!=null)
                listener.onCompleted(Constants.FileScan.RESULT_FAILED);
            return null;
        }


        ScanTask scanTask = virusScan.scan(new ScanCombination() {
            @Override
            public Collection<ScanSource> getSources() {
                Collection<ScanSource> c = new LinkedHashSet<ScanSource>();
                for(String path:pathArray){
                    c.add(new ScanPath(path));
                }
                return c;
            }
        }, new FileScanObserver(listener));
        ProgressThread thread = new ProgressThread(scanTask,listener);
        thread.start();
        return scanTask;
    }



    class ProgressThread extends Thread{

        ScanTask scanTask;

        IFileScanListener listener;

        public ProgressThread(ScanTask scanTask,IFileScanListener listener){
            this.scanTask = scanTask;
            this.listener = listener;
        }
        @Override
        public void run() {
            setName("ProgressThread-" + scanTask.toString());

            while(scanTask.getState().getStatus()!= ScanState.STATUS_FAILED&&
                    scanTask.getState().getStatus()!=ScanState.STATUS_SUCCEEDED&&
                    scanTask.getState().getStatus()!=ScanState.STATUS_CANCELED&&
                    !mDestory.get()){
                if (listener!=null)
                    listener.onProgress((int) (scanTask.getState().getProgress()*100));

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    class FileScanObserver implements ScanObserver{

        IFileScanListener listener;

        FileScanObserver(IFileScanListener listener){
            this.listener = listener;
        }

        @Override
        public void onStarted() {
            start = System.currentTimeMillis();
            mVirusNum.set(0);
            mScanNum.set(0);
            listener.onStarted();
        }

        @Override
        public void onCompleted(int i) {
            mDestory.set(true);
            //成功时强制回调进度100
            if (i==Constants.FileScan.RESULT_SUCCEEDED){
                if (listener!=null)
                    listener.onProgress(100);
            }
            long end = System.currentTimeMillis();
            float time = (end - start)/1000.0f;
            listener.onCompleted(i);
            CloudProcessFlurryHelper.flurryMcafeeDeepScanTime(i,time);
            CloudProcessFlurryHelper.flurryMcafeeDeepScanVirusNum(mScanNum.get(),mVirusNum.get());
        }

        @Override
        public void onScanned(ScanSource scanSource, ScanResult scanResult) {
            int category = scanResult.getCategory();
            String virusName = null;
            String virusDescription = null;
            String riskType = "";
            int riskLevel = Constants.ScanInfo.NO_RISK;
            String suggest = "";
            switch (category){
                case ScanResult.CATEGORY_CLEAN:

                    break;
                case ScanResult.CATEGORY_RISKY:
                    if (scanResult.getThreat()!=null){
                        mVirusNum.incrementAndGet();
                        Threat threat = scanResult.getThreat();
                        virusName = threat.getName();
                        riskLevel = threat.getRiskLevel();
                        riskType = RiskUtils.threatTypeToString(threat.getType());
                        suggest = RiskUtils.getSuggest(VirusScanQueue.getInstance().getContext(),"");
                    }
                    break;
                case ScanResult.CATEGORY_UNSPECIFIED:
                    category = ScanResult.CATEGORY_CLEAN;
                    break;
            }
            ScanInfo info = new ScanInfo(Constants.ScanInfo.FILE_TYPE_NORMAL,null,virusName,virusDescription,category,scanSource.toString(),riskType,riskLevel,suggest);
            if (listener!=null)
                listener.onScanned(info);
            mScanNum.incrementAndGet();

        }
    }
}

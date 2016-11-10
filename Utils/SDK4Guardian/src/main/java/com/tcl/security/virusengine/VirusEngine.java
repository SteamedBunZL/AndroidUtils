package com.tcl.security.virusengine;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.intel.security.Properties;
import com.intel.security.SecurityContext;
import com.intel.security.vsm.ScanTask;
import com.intel.security.vsm.UpdateObserver;
import com.intel.security.vsm.UpdateTask;
import com.intel.security.vsm.VirusScan;
import com.tcl.security.virusengine.func_interface.IFileScanListener;
import com.tcl.security.virusengine.func_interface.IInitializationCallback;
import com.tcl.security.virusengine.func_interface.IRealTimeScanListener;
import com.tcl.security.virusengine.func_interface.IScanListener;
import com.tcl.security.virusengine.func_interface.IUpdateObserver;
import com.tcl.security.virusengine.scan_strategy.AllScan;
import com.tcl.security.virusengine.scan_strategy.VirusFileScan;
import com.tcl.security.virusengine.scan_strategy.VirusQuickScan;
import com.tcl.security.virusengine.utils.FileUtil;
import com.tcl.security.virusengine.utils.VirusLog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import tlogsdk.HiLogManager;


/**
 * The engine to manage entire control flow
 * Created by Steve on 2016/4/29.
 */
public class VirusEngine {

    private ScanStrategy mScanStrategy;

    private Context mContext;

    public static boolean engineForClean = false;

    private List<ScanTask> mFileTaskList = new ArrayList<>();


    private VirusEngine(Context context, IInitializationCallback callback) {
        //初始化VirusScanQueue
        VirusScanQueue.getInstance().intialize(context);
        VirusLog.d("init VirusEngine");
        mContext = context.getApplicationContext();
        //默认strategy是病毒扫描
        mScanStrategy = new VirusQuickScan();
        HiLogManager.initialize(context);
        initialize(callback);
    }

    /**
     * 设置扫描模式,如果不指定，默认是病毒扫描
     * @param strategy
     */
    public void setScanStrategy(ScanStrategy strategy){
        this.mScanStrategy = strategy;
    }

    private VirusEngine() {}

    /**
     * 获取VirusEngine
     *
     * @param context
     * @param callback
     * @return
     */
    public static VirusEngine open(Context context, IInitializationCallback callback) {
        return new VirusEngine(context, callback);
    }

    /**
     * 实时病毒查杀
     *
     * @param packagename
     */
    public synchronized void isVirus(String packagename, IRealTimeScanListener listener) {
        if (mScanStrategy != null){
            try {
                VirusLog.d("VirusEngine %s invoked",this.toString());
                if (mScanStrategy instanceof VirusQuickScan)
                    ((VirusQuickScan)mScanStrategy).isVirus(packagename,this.toString(),listener);
                else if(mScanStrategy instanceof AllScan)
                    ((AllScan)mScanStrategy).isVirus(packagename,this.toString(),listener);
                else
                    VirusLog.e("This scan strategy is not available for isVirus()");
            } catch (PackageManager.NameNotFoundException e) {
                throw new IllegalStateException("PackageName is invalid");
            }
        }
        else
            throw new IllegalStateException("VirusEngine must be construct.Please invoke open first.");
    }


    /**
     * 快速扫描
     */
    public synchronized void startQuickScan(List<String> ignoreList,IScanListener listener) {
        VirusLog.d("invoked");
        VirusScanQueue.getInstance().setQuerying(true);
        if (mScanStrategy != null)
            mScanStrategy.startScan(listener,ignoreList);
        else
            throw new IllegalStateException("VirusEngine must be construct.Please invoke open first.");
    }

    /**
     * 文件扫描
     * @param listener
     */
    public void startFileScan(List<String> pathArray,IFileScanListener listener){
        VirusLog.d("invoded");
        cancelFileScan();
        VirusFileScan fileScan = new VirusFileScan();
        ScanTask fileTask = fileScan.scanFile(pathArray,listener);
        mFileTaskList.add(fileTask);
    }

    /**
     * 取消文件扫描
     */
    public void cancelFileScan(){
        VirusLog.d("invoded");
        for(ScanTask task:mFileTaskList){
            if (task!=null)
                task.cancel();
        }
    }


    /**
     * 取消快速扫描
     */
    public synchronized void cancelQuickScan() {
        VirusLog.d("invoked");
        VirusScanQueue.getInstance().setQuerying(false);
        if (mScanStrategy != null)
            mScanStrategy.cancelScan();
        else
            throw new IllegalStateException("Can not cancel a null scantragtegy");
    }




    /**
     * Mcafee引擎初始化
     */
    private synchronized void initialize(final IInitializationCallback listener) {
        boolean isInitialized = SecurityContext.isInitialized(mContext);

        if (isInitialized) {
            VirusScan virusScan = (VirusScan) SecurityContext.getService(mContext, SecurityContext.VIRUS_SCAN);
            if (virusScan!=null){
                String mcs = virusScan.getProperties().getString(Properties.KEY_MCS_VERSION);
                String dat = virusScan.getProperties().getString(Properties.KEY_DAT_VERSION);
                if(listener!=null) {
                    listener.onInitiaSuccess(mcs + "." + dat);
                    return;
                }
            }
        }
        InputStream license = null;
        try {

            license = mContext.getResources().openRawResource(R.raw.license_isec);
            SecurityContext.initialize(mContext, license, new SecurityContext.InitializationCallback() {
                        @Override
                        public void onInitialized() {
                            VirusScan virusScan = (VirusScan) SecurityContext.getService(mContext, SecurityContext.VIRUS_SCAN);

                            virusScan.getProperties().setInt(Properties.KEY_CLOUD_SCAN_CONNECT_TIMEOUT,10000);
                            virusScan.getProperties().setInt(Properties.KEY_CLOUD_SCAN_READ_TIMEOUT,10000);

                            if (virusScan!=null){
                                //initailized the virusscan
                                VirusScanQueue.getInstance().setVirusScan(virusScan);
                                // Gets signature database version
                                String dat = virusScan.getProperties().getString(Properties.KEY_DAT_VERSION);
                                // Gets scan engine version
                                String mcs = virusScan.getProperties().getString(Properties.KEY_MCS_VERSION);

                                if (!TextUtils.isEmpty(dat)){
                                    VirusScanQueue.getInstance().getScanMessage().setLibVersion(dat);
                                }

                                if (!TextUtils.isEmpty(mcs)) {
                                    VirusScanQueue.getInstance().getScanMessage().setMcafeeEngineVersion(mcs);
                                }

                                if (listener!=null)
                                    listener.onInitiaSuccess(mcs + "." + dat);



                                VirusLog.e("===dat is %s, mcs is %s.",dat,mcs);

                            }
                        }
                    });
        } catch (Exception e) {
            VirusLog.printException(e,"McAfee引擎初始化");
            listener.onInitialFailed();
        } finally {
            FileUtil.cloaseQuietly(license);
        }
    }

    /**
     * 更新病毒库
     * @param observer
     */
    public synchronized void updateDat(final IUpdateObserver observer){

        VirusLog.e("==== updatDat");

        VirusScan virusScan = (VirusScan) SecurityContext.getService(mContext, SecurityContext.VIRUS_SCAN);

        if (virusScan==null){
            virusScan = (VirusScan) SecurityContext.getService(mContext, SecurityContext.VIRUS_SCAN);
        }


        final VirusScan finalVirusScan = virusScan;
        try {
            UpdateTask task = virusScan.update(new UpdateObserver() {
                @Override
                public void onStarted() {
                    if (observer!=null)
                        observer.onUpdateStart();
                }

                @Override
                public void onCompleted(int i) {

                    // Gets signature database version
                    String dat = finalVirusScan.getProperties().getString(Properties.KEY_DAT_VERSION);

                    if (!TextUtils.isEmpty(dat)){
                        VirusScanQueue.getInstance().getScanMessage().setLibVersion(dat);
                        VirusLog.w("onUpdate dat %s",dat);
                    }
                    // Gets scan engine version
                    String mcs = finalVirusScan.getProperties().getString(Properties.KEY_MCS_VERSION);

                    if (!TextUtils.isEmpty(mcs)){
                        VirusScanQueue.getInstance().getScanMessage().setMcafeeEngineVersion(mcs);
                        VirusLog.w("onUpdate mcs %s",dat);
                    }
                    if (observer!=null)
                        observer.onUpdateComplete(i,mcs + "."+dat);


                }
            });

            VirusScanQueue.getInstance().getUpdateList().add(task);
        } catch (Exception e) {
            VirusLog.printException(e,"更新引擎");
        }finally {

        }


    }



}

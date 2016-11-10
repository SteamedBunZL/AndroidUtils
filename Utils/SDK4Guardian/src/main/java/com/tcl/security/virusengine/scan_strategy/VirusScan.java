package com.tcl.security.virusengine.scan_strategy;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;

import com.intel.security.vsm.UpdateTask;
import com.tcl.security.virusengine.IgnoreDataReport;
import com.tcl.security.virusengine.ScanStrategy;
import com.tcl.security.virusengine.VirusScanQueue;
import com.tcl.security.virusengine.entry.ScanEntry;
import com.tcl.security.virusengine.func_interface.IRealTimeScanListener;
import com.tcl.security.virusengine.func_interface.IScanListener;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

/**
 * Created by Steve on 2016/5/1.
 */
public abstract class VirusScan extends BaseScan implements ScanStrategy {

    protected final VirusScanQueue mQueue;

    public VirusScan(){
        mQueue = VirusScanQueue.getInstance();
    }

    public boolean isVirusQuerying(){
        if (mQueue!=null){
            return mQueue.isVirusQuerying();
        } else{
            VirusLog.e("Queue is null.");
            return false;
        }
    }


    /**
     * 启动app查杀，外面同步处理，这里不需要同步？
     *
     * @param listener
     */
    public void startScan(IScanListener listener, Object... obj){
        if (mScheduleCallback!=null) {
            mScheduleCallback.onScanStart();
            VirusScanQueue.getInstance().setVirusScanScheduleCallback(mScheduleCallback);
        }
        List<UpdateTask> updateList = VirusScanQueue.getInstance().getUpdateList();
        if (updateList!=null&&!updateList.isEmpty()){
            for(UpdateTask task:updateList){
                if (task!=null){
                    VirusLog.e("==== updatetask %s",task.toString());
                    task.cancel();
                }
            }
            updateList.clear();
        }
        List<String> ignoreList = new ArrayList<>();
        if (obj!=null&&obj.length>0&&obj[0] instanceof List){
            ignoreList = (List<String>) obj[0];
        }
        FileScanThread scanThread = new FileScanThread(listener, ignoreList);
        scanThread.start();

    }



    public void cancelScan(){
        if (mQueue!=null){
            synchronized (mQueue){
                mQueue.cancel();
            }
        }
    }

    public void isVirus(String packageName, String engine,IRealTimeScanListener listener) throws PackageManager.NameNotFoundException {
        if (mQueue!=null) {
            synchronized (mQueue){
                mQueue.setRealTimeListener(engine,listener);
                PackageInfo info = VirusScanQueue.getInstance().getContext().getPackageManager().getPackageInfo(packageName,0);
                String appName = String.valueOf(info.applicationInfo.loadLabel(VirusScanQueue.getInstance().getContext().getPackageManager()));
                if (TextUtils.isEmpty(appName))
                    appName = packageName;
                String version = info.versionName;
                int versionCode = info.versionCode;
                ScanEntry entry = new ScanEntry();
                ApplicationInfo appInfo = info.applicationInfo;
                if (appInfo!=null)
                    entry.publicSourceDir = appInfo.publicSourceDir;
                entry = buildScanEntry(entry, ScanEntry.REAL_TIME,packageName,appName,version,engine,versionCode,entry.publicSourceDir);
                mQueue.add(entry);
            }
        }
    }

    protected abstract void addEntryToQueue(VirusScanQueue queue,List<String> ignoreList);

    protected abstract ScanEntry buildScanEntry(ScanEntry source, Object... obj);

    public class FileScanThread extends Thread{

        IScanListener sListener;

        List<String> ignoreList;



        public FileScanThread(IScanListener listener,List<String> igoreList){
            sListener = listener;
            this.ignoreList = igoreList;
        }
        @Override
        public void run() {
            setName("FileScanThread");
            try {
                if (mQueue!=null){
                    synchronized (mQueue){
                        mQueue.launch();
                        mQueue.setScanListener(sListener);
                        VirusLog.d("FileScanThread invoke");
                        //query ignorelist
                        addEntryToQueue(mQueue,ignoreList);
                    }
                }
            }catch (Exception e){
                VirusLog.e(e.getCause(),"FileScanThread");
                e.printStackTrace();
            }
        }
    }


}

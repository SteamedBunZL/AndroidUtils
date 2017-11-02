package com.clean.spaceplus.cleansdk.junk.engine.task;

import android.os.Environment;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.junk.engine.bean.RootCacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.base.utils.root.RootStateMonitor;
import com.clean.spaceplus.cleansdk.base.utils.root.SuExec;
import com.hawkclean.framework.log.NLog;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import space.network.cleancloud.KCacheCloudQuery;

/**
 * @author shunyou.huang
 * @Description:
 * @date 2016/5/5 19:04
 * @copyright TCL-MIG
 */

public class RootCacheScanTask extends ScanTask.BaseStub{

    private static final String TAG = RootCacheScanTask.class.getSimpleName();

    public static final int ROOT_CACHE_SCAN_START			= 1;
    public static final int ROOT_CACHE_SCAN_FINISH			= 2;	///< 扫描结束，若因超时结束，则arg1值为1，否则为0。
    //@DEAD(orz)@	public static final int ROOT_CACHE_SCAN_STATUS			= 3;
    public static final int ROOT_CACHE_SCAN_FOUND_ITEM		= 4;
    public static final int ROOT_CACHE_SCAN_PROGRESS_START	= 5;	///< 开始进度计算，arg1值为总步数
    //@DEAD(orz)@	public static final int ROOT_CACHE_SCAN_PROGRESS_STEP	= 6;	///< 进度计算，加一步

    private boolean mIsSdCacheScanFinish = false;
    private BlockingQueue<Integer> mRootStateBlockingQueue = new LinkedBlockingQueue<>();

    public interface RootCacheScanCallback{
        RootCacheInfo getRootCacheInfoItem();
        boolean isQueueEmpty();
    }

    private RootCacheScanCallback mRootCacheScanCallback;

    public void bindRootCacheScanCallback(RootCacheScanCallback rootCacheScanCallback){
        mRootCacheScanCallback = rootCacheScanCallback;
    }

    @Override
    public boolean scan(ScanTaskController ctrl) {

        NLog.i(TAG , " scan ");
        if (null != mCB) {
            mCB.callbackMessage(ROOT_CACHE_SCAN_START, 0, 0, null);
        }

        RootStateMonitor.getInst().register(rootStateObserver);

        try {
            int rootState = mRootStateBlockingQueue.take();

            if(rootState == RootStateMonitor.STATE_QUERYING){
                int state = mRootStateBlockingQueue.take();
                if(RootStateMonitor.isStateSuccess(state)){
                    NLog.i(TAG, " querying doScan ");
                    doScan(ctrl);
                }
            }else if(RootStateMonitor.isStateSuccess(rootState)){
                NLog.i(TAG, " isStateSuccess doScan ");
                doScan(ctrl);
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } finally {
            RootStateMonitor.getInst().unregister(rootStateObserver);
            if (null != mCB) {
                mCB.callbackMessage(ROOT_CACHE_SCAN_FINISH, 0, 0, null);
            }
        }

        return false;
    }

    private void doScan(ScanTaskController ctrl) {
        RootCacheInfo item = null;
        //LinkedList<CleanCloudResultReporter.ResultData> reportData = new LinkedList<CleanCloudResultReporter.ResultData>();
        while (!mIsSdCacheScanFinish || !mRootCacheScanCallback.isQueueEmpty()) {
            if (null != ctrl && ctrl.checkStop()) {
                break;
            }
            item = mRootCacheScanCallback.getRootCacheInfoItem();
            scanPkgPath(item,ctrl);
//            if (item.getSignId() != null && item.getScanType() >= 0 && !item.getSignId().trim().equalsIgnoreCase("0")) {
//                reportData.add(JunkCacheScanHelper.rootCacheInfoToResultData(item, IKCleanCloudResultReporter.FunctionType.ROOT_CACHE_SCAN));
//            }
        }

//        if (reportData != null && reportData.size() > 0) {
//            IKCleanCloudResultReporter cleanCloudReporer = KCleanCloudManager.createCacheResultReporter();
//            if (cleanCloudReporer != null) {
//                cleanCloudReporer.report(reportData);
//            }
//        }
    }

    @Override
    public String getTaskDesc() {
        return "RootCacheScanTask";
    }

    private void scanPkgPath(RootCacheInfo item, ScanTaskController ctrl){
        if (null != ctrl && ctrl.checkStop()) {
            return;
        }
        if(null == item)
            return;
        if(item.getPkgName().equals("end"))
            return;
        String path = item.getPath();
        if(item.getPathType() == KCacheCloudQuery.CachePathType.ROOT_DIR){
            String fullPath = SuExec.getInstance().convertRootCacheCleanCloudPath(Environment.getDataDirectory()+"/data/", path, item.getPkgName());
            if(!TextUtils.isEmpty(fullPath)){
                long size = SuExec.getInstance().getPathFileSize(fullPath);
                if(size > 0){
                    item.setPath(fullPath);
                    item.setSize(size);
                    item.setCheck(true);
                    item.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.ROOTCACHE);
                    if (null != mCB) {
                        mCB.callbackMessage(ROOT_CACHE_SCAN_FOUND_ITEM, 0, 0, item);
                    }
                }
            }
        }else if(item.getPathType() == KCacheCloudQuery.CachePathType.ROOT_DIR_REG){
            List<String> fullPathList = SuExec.getInstance().convertRootCacheCleanCloudPathREG(Environment.getDataDirectory()+"/data/", path, item.getPkgName());
            if(fullPathList != null && fullPathList.size() > 0){
                for(String p : fullPathList){
                    if(!TextUtils.isEmpty(p)){
                        long size = SuExec.getInstance().getPathFileSize(p);
                        if(size > 0){
                            item.setPath(p);
                            item.setSize(size);
                            item.setCheck(true);
                            item.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.ROOTCACHE);
                            if (null != mCB) {
                                mCB.callbackMessage(ROOT_CACHE_SCAN_FOUND_ITEM, 0, 0, item);
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 通知SD扫描完成
     * @param isFinish
     */
    public void notifySdCacheScanFinish(boolean isFinish) {
        try {
            mRootStateBlockingQueue.offer(RootStateMonitor.STATE_RESULT_DENY);
            mIsSdCacheScanFinish = isFinish;
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
    }

    RootStateMonitor.IOverallRootStateObserver rootStateObserver = new RootStateMonitor.IOverallRootStateObserver(){

        @Override
        public void onStateChange(int state) {
            try {
                mRootStateBlockingQueue.offer(state);
            } catch (Exception e) {
                NLog.printStackTrace(e);
            }
        }

    };
}

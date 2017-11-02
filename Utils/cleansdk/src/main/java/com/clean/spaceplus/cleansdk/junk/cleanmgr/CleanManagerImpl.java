package com.clean.spaceplus.cleansdk.junk.cleanmgr;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportConfigManage;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.bean.DataReportCleanBean;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Analytics;
import com.clean.spaceplus.cleansdk.base.utils.analytics.bean.CleanEvent;
import com.clean.spaceplus.cleansdk.base.utils.analytics.bean.StartEvent;
import com.clean.spaceplus.cleansdk.base.utils.system.PackageManagerWrapper;
import com.clean.spaceplus.cleansdk.base.utils.system.SystemCacheManager;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudResultReporter;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.ServiceConfigManager;
import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkGroupTitle;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkScanSetting;
import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFile;
import com.clean.spaceplus.cleansdk.junk.engine.bean.RootCacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.SDcardRubbishResult;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkEngine;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkEngineMsg;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkEngineWrapper;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkEngineWrapperMsg;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkEngineWrapperUpdateInfo;
import com.clean.spaceplus.cleansdk.setting.authorization.AuthorizationMgr;
import com.clean.spaceplus.cleansdk.setting.history.bean.HistoryAddInfoBean;
import com.clean.spaceplus.cleansdk.util.SDCardUtil;
import com.clean.spaceplus.cleansdk.util.SizeUtil;
import com.hawkclean.mig.commonframework.util.CommonUtil;
import com.hawkclean.mig.commonframework.util.ThreadMgr;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory.getDefaultDataReport;

/**
 * @author wangtianbao
 * @Description: 扫描和清理的管理类
 * @date 2016/7/18 15:12
 * @copyright TCL-MIG
 */

public class CleanManagerImpl implements CleanManager {
    private static final int TIME_BETWEEN_LAST_CLEAN_RELEASE = 10 * 60000;//最小10分钟之后可以再次点击清理
    private static final int TIME_BETWEEN_LAST_CLEAN_TEST = 10000;//最小10s之后可以再次点击清理
    private static final int MESSAGE_SHOW_END_DELAY = 0x1;
    private JunkEngineWrapper mStdJunkEngWrapper = JunkEngineWrapper.createNewEngine();
    private int mAdTmpLeftCompleted = 0;
    private int mSysCacheCompleted = 0;

    private JunkEngineWrapperUpdateInfo mScanSizeInfo = null;

    CleanCallback mCallback;
    private JunkScanSetting mJunkScanSetting;

    @Override
    public void setCallback(CleanCallback callback){
        this.mCallback=callback;
        mScanControltask= new JunkSizeControlTask(this);
    }
    List<JunkGroupTitle> mGroupData;
    private StringBuffer mTypeSb=new StringBuffer();
    public CleanManagerImpl(List<JunkGroupTitle> groupList){
        mGroupData=groupList;
    }

    private List<JunkModel> mJunkList;
    private static final long MAX_SIZE = 12l * 1024 * 1024 * 1024;  //最大12G


    public static long getMaxShowSize(long size) {
        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }
        return size;
    }

    private int mProgress;
    private int mPercent = 0;
    private boolean isCleaning = false;
    private static final String TAG=CleanManagerImpl.class.getSimpleName();
    private Handler mMsgHandler = new Handler() {


        long curTime;
        long lastFileNameTime = 0;
        boolean hasCleanedProcess = false;
        boolean hasCleanedSysCache = false;

        @Override
        public void handleMessage(Message msg) {
            long deleteSize = 0;
            switch (msg.what) {
                case JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_INFO:
                    if (!isPauseScan && !isCleaning) {
                        if(mCallback!=null) {
                            mCallback.onScanNewDir( (String) msg.obj);
                        }
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_PROCESS_SCAN: {
                        mCallback.onItemScanFinish(JunkGroupTitle.ITEM_MEMCACHE_FLAG, mScanSizeInfo.mProcessScanSize);
                        mPreCheckedSize += mScanSizeInfo.mProcessCheckedScanSize;
                        if(mScanSizeInfo.mProcessScanSize>0){
                            mTypeSb.append(",").append(DataReportCleanBean.SCAN_TYPE_PROCESS).append("-").append(SizeUtil.formatSizeSmallestMBUnit2(mScanSizeInfo.mProcessScanSize));
                        }
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_FINISH_SYS_SCAN:
                case JunkEngineMsg.MSG_HANDLER_FINISH_SYS_FIXED_SCAN:
                    //如果是没有SD卡的
                    long tmpSize=mStdJunkEngWrapper.getSystemCacheSize();
                    if(!SDCardUtil.isHaveSDCard()){
                        mCallback.onItemScanFinish(JunkGroupTitle.ITEM_SYSCACHE_FLAG, tmpSize);
                        //数据埋点，统计扫描系统缓存大小
                        reportCleanData(tmpSize,DataReportCleanBean.SCAN_TYPE_SYSCACHE);
                    }
                    else{
                        mSysCacheCompleted++;
                        if( mSysCacheCompleted >= 2) {
                            if(mCallback!=null) {
                                mCallback.onItemScanFinish(JunkGroupTitle.ITEM_SYSCACHE_FLAG, tmpSize);
                                //数据埋点，统计扫描系统缓存大小
                                reportCleanData(tmpSize,DataReportCleanBean.SCAN_TYPE_SYSCACHE);
                            }
                        }
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_SD_SCAN: {
                    if(mCallback!=null) {
                        long appSize=mStdJunkEngWrapper.getAppCacheSize();
                        mCallback.onItemScanFinish(JunkGroupTitle.ITEM_APPCACHE_FLAG, appSize);
                        //数据埋点，统计扫描应用缓存大小
                        reportCleanData(appSize,DataReportCleanBean.SCAN_TYPE_SDCACHE);
                    }
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_FINISH_LEFT_OVER_SCAN:
                    if(mCallback!=null) {
                        long leftSize=mStdJunkEngWrapper.getLeftSize();
                        mCallback.onItemScanFinish(JunkGroupTitle.ITEM_LEFTCACHE_FLAG, leftSize);
                        //数据埋点，统计卸载残留缓存大小
                        reportCleanData(leftSize,DataReportCleanBean.SCAN_TYPE_LEFTCACHE);
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_TMP_FILES_SCAN:
                case JunkEngineMsg.MSG_HANDLER_FINISH_ADV_SCAN: {
                    mAdTmpLeftCompleted++;
                    if ( mAdTmpLeftCompleted >= 2) {
                        if(mCallback!=null) {
                            long adleftSize=mStdJunkEngWrapper.getAdLeftTmpSize();
                            mCallback.onItemScanFinish(JunkGroupTitle.ITEM_ADCACHE_FLAG, adleftSize);
                            //数据埋点，统计广告垃圾缓存大小
                            reportCleanData(adleftSize,DataReportCleanBean.SCAN_TYPE_ADVCACHE);
                        }
                    }
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_FINISH_APK_SCAN: {
                    if(mCallback!=null) {
                        long apkSize=mStdJunkEngWrapper.getApkSize();
                        mCallback.onItemScanFinish(JunkGroupTitle.ITEM_APKCACHE_FLAG, apkSize);
                        //数据埋点，统计无用安装包缓存大小
                        reportCleanData(apkSize,DataReportCleanBean.SCAN_TYPE_APKCACHE);
                    }
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE: {
                    mPreCheckedSize += (Long) msg.obj;
//                    handlerUpdateSize(mPreCheckedSize,false);
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_ADD_PROGRESS: {
                    if (!isPauseScan && !isCleaning) {
                        mProgress += msg.arg1;

                        if (mProgress > msg.arg2) {
                            mProgress = msg.arg2;
                        }

                        int percent = (int) (mProgress * 100f / msg.arg2);
//                        mProgressBar.setPercent(percent);
                        if (percent > mPercent) {
                            mPercent = percent;
                        }
                        if(mCallback!=null) {
                            mCallback.onScanProgress(mPercent);
                        }
                    }
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_FINISH_CLEAN:
                    if (!isCleaning) {
                        return;
                    }
                    isCleaning = false;
                    finishClean();
                    if(mCallback!=null) {
                        mCallback.onCleanEnd(mPreCheckedSize);
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_REMOVE_DATA_ITEM:
                    String removingTitle = "";
                    if (msg.obj instanceof CacheInfo) {
                        CacheInfo cacheInfo = (CacheInfo) msg.obj;
                        if (!hasCleanedSysCache && (cacheInfo.getInfoType() == CacheInfo.INFOTYPE_SYSTEMCACHEITEM)) {
                            if (!mIsDeleteOneItem) {  //系统缓存变成一张卡片，只dismiss一次
                                hasCleanedSysCache = true;
                            }
                        } else if (cacheInfo.getInfoType() == CacheInfo.INFOTYPE_SYSFIXEDFIELITEM) {
                        } else {
                        }
                    } else if (msg.obj instanceof APKModel) {
                        removingTitle = ((APKModel) msg.obj).getFileName();
                    } else if (msg.obj instanceof SDcardRubbishResult) {
                        removingTitle = ((SDcardRubbishResult) msg.obj).getName();
                    } else if (msg.obj instanceof ProcessModel) {
                        removingTitle = ((ProcessModel) msg.obj).getTitle();
                        if (!hasCleanedProcess) { //内存变成一张卡片，所以dismiss一次
                            if (!mIsDeleteOneItem) {
                                hasCleanedProcess = true;
                            }
                        }
                    } else if (msg.obj instanceof RootCacheInfo) {
                        try {
                            RootCacheInfo rootCacheInfo = (RootCacheInfo) msg.obj;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    mCleanedSize = getMaxShowSize(mCleanedSize);
                    mCleanedSizeThisTime = getMaxShowSize(mCleanedSizeThisTime);
                    break;

                case JunkEngineMsg.MSG_HANDLER_SCAN_END:
                    mStandardState = JunkEngineMsg.MSG_HANDLER_SCAN_END;
                    if (!isPauseScan) {
//                        mCleanNow.setClickable(false); // 在处理过程中，不允许点击按钮
//                        onScanEnd(false);
                        //先获取数据
                        endScan(false);
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_STOP_WAIT_RESULT:
                    if (!isAllFinishScanExpectMemory()) { // 结果还没返回
                        mMsgHandler.sendEmptyMessageDelayed(JunkEngineMsg.MSG_HANDLER_STOP_WAIT_RESULT, 50);
                    } else {
                        mCallback.onItemScanFinish(JunkGroupTitle.ITEM_MEMCACHE_FLAG, getMemorySize()); // 停止内存扫描，因为内存扫描不受引擎控制。。
                        // 先获取数据
                        endScan(true);
                        PackageManager manager = SpaceApplication.getInstance().getContext().getPackageManager();
                        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                                manager.checkPermission( Manifest.permission.READ_EXTERNAL_STORAGE, CommonUtil.getPkgName()));
                    }
                    break;
            }
        }
    };

    private void handlerUpdateSize(long size, boolean isStop) {
        if (mStandardState == JunkEngineMsg.MSG_HANDLER_SCAN_END) {
            if(mCallback!=null) {
                mCallback.onUpdateCheckedSize(size);
            }
        } else {
            //限制展示的最大值
            size = CleanManagerImpl.getMaxShowSize(size);
            if(mCallback!=null) {
                mCallback.onUpdateCheckedSize(size);
            }
        }
    }


    /**
     * 去除不需要清理的选项
     */
    public boolean handleJunkForCleanEngine() {
        boolean bAllSelected = true;
        Iterator<JunkModel> iter = mJunkList.iterator();

        while (iter.hasNext()) {
            JunkModel junkModel = iter.next();
            if (junkModel.getType() == JunkModel.TYPE_CATEGORY || junkModel.getType() == JunkModel.TYPE_ADVANCED_JUNK) {
                iter.remove();
                continue;
            }

            int childSize = junkModel.getChildSize();
            if (junkModel.isGroupLockandRemoveUnCheckChild()) {
                bAllSelected = false;
                iter.remove();
            } else {
                if (childSize != junkModel.getChildSize()) {
                    bAllSelected = false;
                }
                junkModel.setHidden(false);
            }
        }
        return bAllSelected;
    }

    private long mPreCheckedSize = 0l;
    private long mTotalSizeShow = 0l;
    private static volatile long mLastCleanTime = 0L;


    private void finishClean(){
        //清理完4分钟后启动系统缓存预加载
        SystemCacheManager.postPreLoad(SystemCacheManager.WAIT_TIME);
        if(!isPauseScan && needSaveLastCleanTime() ){ // 不是手动暂停，并且需要保存时间条件满足
            saveCleanTime();
        }


        ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).setJunkStdSwitch2Bgclean(false);
        //报告清理记录
        long crtTime = System.currentTimeMillis() / 1000;
        if (crtTime - mLastReportTime < 5) {
            return;
        }
        mLastReportTime = crtTime;
        reportAppCleanData(CleanCloudResultReporter.getAppCleanRecord(), crtTime);
        //垃圾清理功能埋点，清理耗时和大小
        CleanEvent event=new CleanEvent("",DataReportCleanBean.ACTION_CLEAN,DataReportConfigManage.getInstance().getFirstScan(),
                Analytics.formatTimeSize(crtTime),SizeUtil.formatSizeSmallestMBUnit2(mPreCheckedSize),Analytics.formatTimeSize(mStdJunkEngWrapper.getTotalScanTime()),
                SizeUtil.formatSizeSmallestMBUnit2(mPreCheckedSize),"",SizeUtil.formatSizeSmallestMBUnit2(mTotalSizeShow));
        getDefaultDataReport().putEvent(event);
    }

    /**
     * 上报应用垃圾
     *
     * @param beanList  清理app列表
     * @param cleanTime 清理的时间
     */
    private void reportAppCleanData(List<HistoryAddInfoBean> beanList, Long cleanTime) {
        if (beanList == null || beanList.size() < 1) {
            return;
        }

        //AddHistoryHelper helper = AddHistoryHelper.getInstance();
        //helper.addHistory(beanList, cleanTime);
    }
    private long mLastReportTime = 0l;
    /**
     * 主要用于确定是否记录最近清理时间，避免频繁进入清理页面
     * 当 mTotalSizeShow - mPreCheckedSize <= 100 返回true
     * 有内存项的时候，当非内存项全选，内存项半选的情况下返回true
     * 没内存项的时候，非内存项全选，返回true
     * 其他返回false
     * @return 是否已经将所有垃圾清理了
     */
    private boolean needSaveLastCleanTime() {
        if (mTotalSizeShow - mPreCheckedSize <= 100) {
            return true;
        }
        boolean hasMemoryItem = false;
        boolean isMemoryItemHalfCheck = false;
        for (int i = 0; i < mGroupData.size(); i++) {
            JunkGroupTitle junkGroupTitle = mGroupData.get(i);
            if (junkGroupTitle.groupFlag != JunkGroupTitle.ITEM_MEMCACHE_FLAG) {
                if (!junkGroupTitle.isGroupChecked) {
                    return false;
                }
            } else {
                hasMemoryItem = true;
                isMemoryItemHalfCheck = junkGroupTitle.halfCheck;
            }
        }
        // 非内存项都勾选了才会走到这里
        return !hasMemoryItem || isMemoryItemHalfCheck; // 如果没有内存项 或者 如果是半选，即有勾选的情况下，直接返回true
    }
    /**
     * @return 是否所有的Item都已经扫描结束，暂时用于暂停时等待结果返回，（除了内存垃圾，内存垃圾不受引擎控制）
     */
    public boolean isAllFinishScanExpectMemory() {
        for (int i = 0; i < mGroupData.size(); i++) {
            if (mGroupData.get(i).groupFlag != JunkGroupTitle.ITEM_MEMCACHE_FLAG && mGroupData.get(i).stateType != JunkGroupTitle.TYPE_FINISH) {
                return false;
            }
        }
        return true;
    }

    private JunkEngine.JunkEventCommandInterface mJunkEvent = new JunkEngine.JunkEventCommandInterface() {

        private long preTime = 0l;

        @Override
        public void callbackMessage(int what, int arg1, int arg2, final Object obj) {
            switch (what) {
                case JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO:
                    if (null != obj) {
                        mScanSizeInfo = ((JunkEngineWrapperUpdateInfo) obj).copyValue(mScanSizeInfo);
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_UPDATE_COMING_SOON_SIZE:
                    mMsgHandler.sendMessage(mMsgHandler.obtainMessage(what, 0, 0, obj));
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_SCAN:
                    mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_SCAN_END);
                    break;
                case JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_INFO:
                    if(!isScanEnd) {
                        mScanControltask.mScanTextControl.add((String) obj);
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_CLEAN_STATUS_INFO:
                    if (isCleaning || mIsDeleteOneItem) {
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(what, 0, 0, obj));
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_PROCESS_SCAN: {
                    mMsgHandler.sendMessage(mMsgHandler.obtainMessage(what));
                    if(isOnlyScanProcess()){
                        //如果是只是扫内存，扫完后需要把引擎状态设置为活动状态,并且结束扫描
                        mStdJunkEngWrapper.setEngineStatus(JunkEngine.EM_ENGINE_STATUS.IDLE);
                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_SCAN_END);
                    }
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_FINISH_TMP_FILES_SCAN:
                case JunkEngineMsg.MSG_HANDLER_FINISH_ADV_SCAN:
                case JunkEngineMsg.MSG_HANDLER_FINISH_LEFT_OVER_SCAN: {
                    mMsgHandler.sendMessage(mMsgHandler.obtainMessage(what));
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_FINISH_SYS_SCAN:
                    mMsgHandler.sendMessage(mMsgHandler.obtainMessage(what));
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_SYS_FIXED_SCAN:
                    mMsgHandler.sendMessage(mMsgHandler.obtainMessage(what));
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_SD_SCAN:
//                    NLog.i(TAG,"MSG_HANDLER_FINISH_SD_SCAN");
                    mMsgHandler.sendMessage(mMsgHandler.obtainMessage(what));
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_APK_SCAN: {
                    mMsgHandler.sendMessage(mMsgHandler.obtainMessage(what));
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE:
                    mMsgHandler.sendMessage(mMsgHandler.obtainMessage(what, 0, 0, obj));
                    break;
                case JunkEngineMsg.MSG_HANDLER_ADD_PROGRESS: {
                    mMsgHandler.sendMessage(mMsgHandler.obtainMessage(what, arg1, arg2));
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_SD_CLEAN_FINISH://sd clean finish
//                    int appSize = mAppCleanNum.decrementAndGet();
//                    if (appSize == 0) {
//                        NLog.e(TAG, "sd clean finish");
//                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_RUB_CLEAN_FINISH://rubbish clean finish
                    break;
                case JunkEngineMsg.MSG_HANDLER_APK_CLEAN_FINISH://无用安装包
//                    int apkSize = mUselessApkCleanNum.decrementAndGet();
//                    if (apkSize == 0) {
//                        NLog.e(TAG, "无用安装包");
//                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_SYS_CLEAN_FINISH://system clean finish
//                    int sysSize = mSystemCleanNum.decrementAndGet();
//                    if (sysSize == 0) {
//                        NLog.e(TAG, "system clean finish");
//                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_ROOT_CACHE_CLEAN_FINISH://root clean finish
//                    int rootSize = mSystemCleanNum.decrementAndGet();
//                    if (rootSize == 0) {
//                        NLog.e(TAG, "root clean finish");
//                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_SYS_FIXED_CLEAN_FINISH://fixed finish
//                    int fixedSize = mSystemCleanNum.decrementAndGet();
//                    if (fixedSize == 0) {
//                        NLog.e(TAG, "fixed finish");
//                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_MEDIA_CLEAN_FINISH://media file
                    //暂不统计媒体文件
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_COMPRESS_SCRSHOTS://screenshots compress finish
                    //暂不统计截图压缩
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_CLEAN:
                    preTime = 0l;
                    mMsgHandler.sendMessage(mMsgHandler.obtainMessage(what));
                    break;
                case JunkEngineMsg.MSG_HANDLER_UPDATE_CLEAN_BUTTON: {
                    mMsgHandler.sendEmptyMessage(what);
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_REMOVE_DATA_ITEM:
                    long deleteSize = 0;
                    if (obj instanceof CacheInfo) {
                        CacheInfo cacheInfo = (CacheInfo) obj;
                        deleteSize = cacheInfo.getSize();
                    } else if (obj instanceof APKModel) {
                        //deleteSize = ((APKModel)pkgName).getSize();
                    } else if (obj instanceof SDcardRubbishResult) {
                        SDcardRubbishResult dcardRubbishResult = ((SDcardRubbishResult) obj);
                        deleteSize = dcardRubbishResult.getSize();
                    } else if (obj instanceof ProcessModel) {
                        deleteSize = ((ProcessModel) obj).getMemory();
                    } else if (obj instanceof RootCacheInfo) {
                        deleteSize = ((RootCacheInfo) obj).getSize();
                    } else if (obj instanceof MediaFile) {
                        deleteSize = ((MediaFile) obj).getSize();
                    }
                    mCleanedSize += deleteSize;
                    mCleanedSizeThisTime += deleteSize;
//                    NLog.i(TAG, "handler remove data item size:"+deleteSize);
//                    cleanSize(deleteSize);
                    if (mIsDeleteOneItem) {
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(what, 0, 0, obj));
                    } else if (preTime == 0 || System.currentTimeMillis() - preTime >= 50) {
                        preTime = System.currentTimeMillis();
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(what, 0, 0, obj));
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_CLEAN_FOR_CACHE:
                    mMsgHandler.sendEmptyMessage(what);
                    break;
            }
        }
    };

    JunkSizeControlTask mScanControltask ;

    @Override
    public void onDestroy() {
        mScanControltask.cancel(true);
    }

    /**
     * 扫描数据显示控制器
     */
    static class JunkSizeControlTask extends AsyncTask<Object, Object, Boolean> {

        private boolean isCancel = false;
        LinkedBlockingQueue<String> mAssistQueue = new LinkedBlockingQueue<>();
        ConcurrentLinkedQueue<String> mScanTextControl = new ConcurrentLinkedQueue<>();
//        WeakReference<CleanCallback> mWeakRef;
        WeakReference<CleanManagerImpl> mManagerRef;
        JunkSizeControlTask(CleanManagerImpl manager) {
//            mWeakRef = new WeakReference<>(callback);
            mManagerRef=new WeakReference<>(manager);
            List<PackageInfo> packages = PackageManagerWrapper.getInstance().getPkgInfoList();
            if (packages == null){
                return;
            }
            int length = packages.size();
            String name = "";
            for (int i = 0; i < length; i++) {
                name = packages.get(i).packageName;
                mAssistQueue.add(name);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isCancel = true;
            cancel(true);
            mScanTextControl.clear();
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            long consumerSize = 0L;
            long allSize = 0L;
            long tempSize = 0L;
            String scanFile = "";
            while (!isCancel) {
                //控制数字显示
                CleanManagerImpl cleanManager = mManagerRef.get();
                if (cleanManager == null||cleanManager.isScanEnd){
                    break;
                }
                allSize = cleanManager.mPreCheckedSize;
                tempSize = allSize - consumerSize;
//                NLog.d(TAG, "ControlTask ----> %d", tempSize);
                if (tempSize > 1024 * 1024 * 1024) {
                    consumerSize += tempSize * 0.02;
                } else if (tempSize > 1024 * 1024) {
                    consumerSize += tempSize * 0.1;
                } else {
                    consumerSize += tempSize * 0.25;
                }
                if (mScanTextControl != null &&
                        !mScanTextControl.isEmpty()) {
                    scanFile = mScanTextControl.poll();
                }else {
                    scanFile = mAssistQueue.poll();
                    if (scanFile != null) {
                        mAssistQueue.add(scanFile);
                    }
                }
                if (scanFile == null) {
                    scanFile = "";
                }

                publishProgress(consumerSize, scanFile);

                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    Thread.interrupted();
                    break;
                }
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            if (isCancel) {
                return;
            }
            CleanManagerImpl cleanManager = mManagerRef.get();
            if (cleanManager == null||cleanManager.isScanEnd){
                return;
            }
            cleanManager.handlerUpdateSize((Long) (values[0]), false);
//            NLog.d(TAG, "handlerControlTask ----> %s", values[1]);
            cleanManager.mMsgHandler.sendMessage(cleanManager.mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_INFO, 0, 0, (String) values[1]));
        }
    }


    /*
     * 此方法获得扫描垃圾的总大小
	 */
    public long getScanAllSize() {
        if (null == mScanSizeInfo) {
            return 0L;
        }

        return mScanSizeInfo.mScanSize;
    }

    public long getMemorySize() {
        return mScanSizeInfo == null ? 0 : mScanSizeInfo.mProcessScanSize;
    }

    /*
   *  此变量标示垃圾建议界面所处的状态，在 mMsgHander 内对其赋值，目的是外部（JunkStandardFragment JunkAdvancedFragment JunkManagerActivity）能够容易获取当前状态
   *  -1 ：进入界面初始状态
   *  MSG_HANDLER_START_SCAN  开始扫描
   *  MSG_HANDLER_SCAN_END  结束扫描
   *  MSG_HANDLER_REMOVE_SYSTEM_CACHE_ITEM 清理系统缓存
   *  MSG_HANDLER_REMOVE_APP_CACHE_ITEM
   *  MSG_HANDLER_REMOVE_SDRUBBISH_ITEM
   *  MSG_HANDLER_REMOVE_PROCESS_ITEM
   *  MSG_HANDLER_REMOVE_APK_ITEM
   *  MSG_HANDLER_REMOVE_DATA_ITEM
   *  MSG_HANDLER_FINISH_CLEAN 清理结束
   */
    private int mStandardState = -1;
    private long mCleanedSize;
    private volatile boolean isScanEnd=true;
    private void endScan(boolean manual){
        isScanEnd=true;
        mScanControltask.cancel(true);
        mJunkList = mStdJunkEngWrapper.getStdJunkModelList(true, -1, true);
        mStandardState = JunkEngineMsg.MSG_HANDLER_SCAN_END;
        if (mScanSizeInfo == null) {
            mPreCheckedSize = 0;
        } else {
            mPreCheckedSize = mScanSizeInfo.mCheckedScanSize;
        }
        mTotalSizeShow = mStdJunkEngWrapper.getTotalScanSize();
        long memorySize= getMemorySize();

        //少于100b,不清理
        if (mTotalSizeShow < 100) {
            if(mCallback!=null) {
                mCallback.onNeedNotClean();
            }
            return;
        }
        if(mCallback!=null) {
            mCallback.onScanEnd(manual, mJunkList, mPreCheckedSize, mTotalSizeShow, memorySize);
        }
        String type=mTypeSb.toString();
        if(type.length()>1){
            type=type.substring(1);
        }
        String action=DataReportCleanBean.ACTION_SCAN_FINISH;
        if(isPauseScan){
            action= DataReportCleanBean.ACTION_SCAN_STOP;
        }
        CleanEvent event=new CleanEvent("",action, DataReportConfigManage.getInstance().getFirstScan(),
                Analytics.formatTimeSize(mStdJunkEngWrapper.getTotalScanTime()), SizeUtil.formatSizeSmallestMBUnit2(mPreCheckedSize),SizeUtil.formatSizeSmallestMBUnit2(mTotalSizeShow),type);
        getDefaultDataReport().putEvent(event);
    }

    @Override
    public void interruptScan() {
        if (mStdJunkEngWrapper != null) {
            mStdJunkEngWrapper.notifyStop();
            mStdJunkEngWrapper.removeObserver(mJunkEvent);
        }
    }


    private long mCleanedSizeThisTime = 0;
    private boolean mIsDeleteOneItem = false;
    private boolean mbScanProcess;
    private boolean isPauseScan = false;
    private boolean hasStartScan = false; // add by chaohao.zhou 避免多次执行导致异常

    @Override
    public void startScan() {
        // 如果没有进行功能授权
        if (!AuthorizationMgr.getInstance().isAuthorized()) {
            endScan(false);
            return;
        }
        //扫描还没结束
        if(hasStartScan && !isScanEnd){
            return;
        }
        hasStartScan = true;
        isScanEnd = false;
        isPauseScan = false;
//        int timeToClean;
//        if (PublishVersionManager.isTest()) {
//            timeToClean = TIME_BETWEEN_LAST_CLEAN_TEST;
//        } else {
//            timeToClean = TIME_BETWEEN_LAST_CLEAN_RELEASE;
//        }
//        if (System.currentTimeMillis() - mLastCleanTime < timeToClean) {
//            if(mCallback!=null) {
//                NLog.e(TAG,"还在清理的时间内onInCacheTime");
//                mCallback.onInCacheTime();
//            }
//            return;
//        }
        mAdTmpLeftCompleted = 0;
        //根据设置中是否开启了扫描内存
        //mbScanProcess=LocalParamConfigManager.getInstance().isScanBoostSwitchOn();
        //mStdJunkEngWrapper.setCallerScanProcess(mbScanProcess);
        if(mStdJunkEngWrapper.getmObserverArray()!=null){
            mStdJunkEngWrapper.getmObserverArray().clear();
        }
        mStdJunkEngWrapper.setObserver(mJunkEvent);
        mStdJunkEngWrapper.startScan(JunkEngineWrapper.JUNK_WRAPPER_SCAN_TYPE_STD, true);
        //mScanControltask= new JunkSizeControlTask(this);
        mScanControltask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mMsgHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mCallback!=null&&!isScanEnd){
                    mCallback.onScanStart();
                }
            }
        },500);
        reportStart();
        //开始功能扫描码
        CleanEvent event=new CleanEvent("",DataReportCleanBean.ACTION_SCAN_START, DataReportConfigManage.getInstance().getFirstScan(),"","","","");
        getDefaultDataReport().putEvent(event);
    }

    private void reportStart(){
        Runnable reportTask = new Runnable() {
            @Override
            public void run() {
                try {
                    //数据上报，开始扫描上报一次活跃用户
                    StartEvent event=new StartEvent("0");
                    DataReportFactory.getDefaultDataReport().putEvent(event);
                }catch (Exception e){
                }
            }
        };
        ThreadMgr.executeLocalTask(reportTask);
    }

    @Override
    public void stopScan() {
        isPauseScan = true;
        mStdJunkEngWrapper.notifyStop();
        mMsgHandler.sendEmptyMessageDelayed(JunkEngineMsg.MSG_HANDLER_STOP_WAIT_RESULT, 50); // 延时100毫秒获取结果
        CleanEvent event=new CleanEvent("",DataReportCleanBean.ACTION_SCAN_STOP, DataReportConfigManage.getInstance().getFirstScan(),"","","","");
        getDefaultDataReport().putEvent(event);
    }

    public void saveCleanTime(){
        mLastCleanTime = System.currentTimeMillis();
    }

    @Override
    public void startClean() {
        //正在清理或没有选择要清理的垃圾或者正在扫描
        if (isCleaning && !isScanEnd) {
            return;
        }

        //少于100b,不清理
        if (mTotalSizeShow < 100) {
            if (!isPauseScan) { // 不是手动暂停的
                saveCleanTime();
            }
            if(mCallback!=null) {
                mCallback.onNeedNotClean();
            }
            return;
        }
        isCleaning = true;
        handleJunkForCleanEngine();

        List<JunkModel> list = new ArrayList<>();
        list.addAll(mJunkList);
        SystemCacheManager.cleanCheckCache(list);

        mStdJunkEngWrapper.setCleanItemList(list);
        mStdJunkEngWrapper.setCleanType(JunkEngineWrapper.JUNK_WRAPPER_SCAN_TYPE_STD);

        mStdJunkEngWrapper.startClean(false);
        mStdJunkEngWrapper.addObserver(mJunkEvent);
        if(mCallback!=null) {
            mCallback.onCleanStart();
        }
    }

    @Override
    public void endClean() {

    }

    @Override
    public boolean isCleanning() {
        return isCleaning;
    }

    @Deprecated
    @Override
    public void removeDataItem(CacheInfo info) {
//        if (mStdJunkEngWrapper != null) {
//            mStdJunkEngWrapper.removeDataItem(info);
//        }
    }

    @Deprecated
    @Override
    public void deleteDataItem(List<JunkModel> list) {
//        if (mStdJunkEngWrapper != null) {
//            mStdJunkEngWrapper.setCleanItemList(list);
//            mStdJunkEngWrapper
//                    .setCleanType(JunkEngineWrapper.JUNK_WRAPPER_SCAN_TYPE_STD);
//            mStdJunkEngWrapper.startClean(true);
//        }
    }

    @Override
    public void setScanSetting(JunkScanSetting value) {
        //支持设置是否扫描各垃圾清理项
        mJunkScanSetting=value;
        if(value != null){
            mStdJunkEngWrapper.mbScanSdCache=value.isMbScanSdCache();
            mStdJunkEngWrapper.mbScanSysCache=value.isMbScanSysCache();
            mStdJunkEngWrapper.mbScanAdDirCache=value.isMbScanAdDirCache();
            mStdJunkEngWrapper.mbScanApkFile=value.isMbScanApkFile();
            mStdJunkEngWrapper.mbScanRubbish=value.isMbScanRubbish();
            mStdJunkEngWrapper.mbCallerScanProcess=value.isMbScanProcess();
            mStdJunkEngWrapper.mbScanCacheEnable=value.isMbScanCacheEnable();
        }
    }

    private boolean isOnlyScanProcess(){
        if(mJunkScanSetting!=null){
            return mJunkScanSetting.isOnlyScanProcess();
        }
        return false;
    }

    //功能埋点，上报扫描到的每一项垃圾的大小情况
    private void reportCleanData(long tempSize,String type){
        if(tempSize>0){
            mTypeSb.append(",").append(type).append("-").append(SizeUtil.formatSizeSmallestMBUnit2(tempSize));
        }
    }
}

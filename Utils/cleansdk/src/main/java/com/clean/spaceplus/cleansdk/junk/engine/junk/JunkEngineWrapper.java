package com.clean.spaceplus.cleansdk.junk.engine.junk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;

import com.clean.spaceplus.cleansdk.R;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.clean.CleanRequest;
import com.clean.spaceplus.cleansdk.base.clean.CleanRequestImpl;
import com.clean.spaceplus.cleansdk.base.scan.ScanRequest;
import com.clean.spaceplus.cleansdk.base.scan.ScanRequestCallback;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Analytics;
import com.clean.spaceplus.cleansdk.base.utils.root.SuExec;
import com.clean.spaceplus.cleansdk.base.utils.system.PackageManagerWrapper;
import com.clean.spaceplus.cleansdk.boost.dao.ProcessWhiteListDAOHelper;
import com.clean.spaceplus.cleansdk.boost.engine.BoostEngine;
import com.clean.spaceplus.cleansdk.boost.engine.clean.BoostCleanEngine;
import com.clean.spaceplus.cleansdk.boost.engine.clean.BoostCleanSetting;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessCleanSetting;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessHelper;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessResult;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessScanSetting;
import com.clean.spaceplus.cleansdk.boost.engine.scan.BoostScanEngine;
import com.clean.spaceplus.cleansdk.boost.engine.scan.BoostScanSetting;
import com.clean.spaceplus.cleansdk.boost.engine.scan.BoostScanTask;
import com.clean.spaceplus.cleansdk.boost.util.ProcessWhiteListMarkHelper;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CloudCfgDataWrapper;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.CloudCfgKey;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.ServiceConfigManager;
import com.clean.spaceplus.cleansdk.junk.engine.ObjPoolMgr;
import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFile;
import com.clean.spaceplus.cleansdk.junk.engine.bean.RootCacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.SDcardRubbishResult;
import com.clean.spaceplus.cleansdk.junk.engine.bean.VideoOfflineResult;
import com.clean.spaceplus.cleansdk.util.ResUtil;
import com.clean.spaceplus.cleansdk.util.TimingUtil;
import com.hawkclean.framework.log.NLog;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import space.network.util.RuntimeCheck;

/**
 * @author liangni
 * @Description:调度引擎封装类库
 * @date 2016/4/27 20:00
 * @copyright TCL-MIG
 */
public class JunkEngineWrapper implements JunkEngine.EngineCallback, EngineConfig {
    private static String TAG = JunkEngineWrapper.class.getSimpleName();

    public static final int NOTIFICATION_APK = 1;
    public static final int NOTIFICATION_ADV = 2;

    public static final int JUNK_WRAPPER_SCAN_TYPE_STD = 1;
    public static final int JUNK_WRAPPER_SCAN_TYPE_ADV = 2;
    public static final String EXTRA_ADV_JUNKENGINE_INDEX = "extra_adv_junkengine_index";
    public static final String EXTRA_STD_JUNKENGINE_INDEX = "extra_std_junkengine_index";
    private static final int MIN_RECYCLE_FILE_SIZE = 1 * 1024 * 1024;
    private static final int RECYCLE_FILE_DEFAULT_SWTICH = 1;
    public static boolean smQuited = false;
    private JunkEngineImpl mJunkEngine = new JunkEngineImpl();
    private boolean mHaveCfgCleanEngine = false;
    private boolean mIsForegroundScan = false;
    public static final long DATA_CACHE_VALID_TIME_FOR_REOPEN = 60L * 1000L;
    public static final int ENGINE_WRAPPER_STATUS_NONE = 0;            ///< 无数据状态
    public static final int ENGINE_WRAPPER_STATUS_SCANNING = 1;        ///< 正在扫描
    public static final int ENGINE_WRAPPER_STATUS_FINISH_SCAN = 2;    ///< 扫描完毕
    public static final int ENGINE_WRAPPER_STATUS_CLEANING = 3;        ///< 正在清理
    public static final int ENGINE_WRAPPER_STATUS_FINISH_CLEAN = 4;    ///< 清理完毕
    private volatile long mValidTimeStart = 0L;    ///< mEngineStatus状态达成ENGINE_WRAPPER_STATUS_FINISH_SCAN或ENGINE_WRAPPER_STATUS_FINISH_CLEAN的时间。
    private volatile int mEngineStatus = ENGINE_WRAPPER_STATUS_NONE;
    public long junksize=0l;

    AdvScanRequestCallback advScanCB;
    APKFileScanRequestCallback mApkFileScanCB;
    TempFolderScanRequestCallback mTmpFolderScanCB = null;

    private Handler mObserverHandler = null;
    private JunkEngine.JunkEventCommandInterface mJunkEventObserver;
    private ArrayList<JunkEngine.JunkEventCommandInterface> mObserverArray = null;

    private SoftReference<ObjPoolMgr.KObjPool<JunkEngineWrapperUpdateInfo>> mObjPool = null;
    private AtomicInteger mProgressPos = new AtomicInteger(); ///< 进度百分比
    private AtomicInteger mProgressPosValue = new AtomicInteger(); ///< 进度位置
    private AtomicLong mTotalScanSize = new AtomicLong();
    private AtomicLong mTotalCheckedScanSize = new AtomicLong();
    private AtomicLong mTotalCleanSize = new AtomicLong();

    private AtomicLong mProcessScanSize = new AtomicLong();
    private AtomicLong mProcessCheckedScanSize = new AtomicLong();
    private AtomicLong mProcessCleanSize = new AtomicLong();

    private AtomicLong mSysCacheScanSize = new AtomicLong();
    private AtomicLong mSysCacheCheckedScanSize = new AtomicLong();
    private AtomicLong mSysCacheCleanSize = new AtomicLong();

    private List<BaseJunkBean> mSysCacheList = null;
    private List<BaseJunkBean> mSysFixedFileList = null;
    private List<BaseJunkBean> mRootCacheList = null;
    private List<BaseJunkBean> mSDCacheList = null;
    private List<BaseJunkBean> mAdvSDCacheList = null;
    private Map<String, List<CacheInfo>> mAllCacheInfoMap = new HashMap<>();
    private Map<String, List<RootCacheInfo>> mRootCacheInfoMap = new HashMap<>();
    private Map<String, List<CacheInfo>> mAllAdvCacheInfoMap = new HashMap<>();
    private List<BaseJunkBean> mVideoOfflineList = null;
    private List<BaseJunkBean> mScreenShotsCompressList = null;
    private List<ProcessModel> mProcessInfoList = null;

    private List<BaseJunkBean> mRubbishFileListForTempFiles = null;
    private List<BaseJunkBean> mAdvRubbishFileListForTempFiles = null;
    private List<BaseJunkBean> mRubbishFileListForAppLeftovers = null;
    private List<BaseJunkBean> mAdvRubbishFileListForAppLeftovers = null;
    private List<BaseJunkBean> mRubbishFileListForAdvFolders = null;
    private List<BaseJunkBean> mRubbishFileListForBigFiles = null;

    private List<BaseJunkBean> mUselessThumbnailList = null;
    private List<BaseJunkBean> mApkCleanItemInfos = null;
    private boolean mbScanProcess = false;
    public boolean mbCallerScanProcess = false;

    private boolean mbRecordAppCacheRcd = false;

    private int mCleanType = JUNK_WRAPPER_SCAN_TYPE_STD;
    /**
     * 根据进入垃圾界面的方式，设定扫描排序
     */
    private boolean mbNotifyFinishMsgSent = false;
    private boolean mbJunkActionFinished = false;
    private boolean mbProcessActionFinished = false;
    private boolean mbProcessActionStarted = false;
    private boolean mbOnlyVideoOfflineScan = false;
    private boolean mbOnlySdCacheScanResult = false;
    boolean mbIsMsgThreadQuit = false;
    private Object mMutexMsgSent = new Object();
    private Object mMutexForBGThread = new Object();
    private HandlerThread mBGThread = null;
    private Context mCtx = null;
    private boolean mbRptStdData;
    private int mScanType = 0; //扫描类型  standard或者advanced
    private int mJunkRecycleSwtich = 0;
    private int mJunkRecycleFileSize = 0;
    private volatile long mToBeCleanedSize = 0L;
    private int mForceTopType = 0;//1 apk 2 adv
    //控制是否扫描每一个垃圾项
    //扫描应用缓存开关
    public boolean mbScanSdCache;
    //扫描系统缓存开关
    public boolean mbScanSysCache;
    //扫描广告垃圾开关
    public boolean mbScanAdDirCache;
    public boolean mbScanUselessThumbnail;
    //扫描无用安装包开关
    public boolean mbScanApkFile;
    //扫描卸载残留开关
    public boolean mbScanRubbish;
    //扫描应用缓存是否启用缓存数据
    public boolean mbScanCacheEnable;

    private JunkEngineWrapper(boolean bRptStdData) {
        mbRptStdData = bRptStdData;
        initBGThread();
        mCtx = SpaceApplication.getInstance().getContext();
    }

    public void setCallerScanProcess(boolean flag) {
        mbCallerScanProcess = flag;
    }

    @Deprecated
    public static JunkEngineWrapper createNewEngine() {
        return new JunkEngineWrapper(true);
    }

    private void initBGThread() {
        if(mBGThread != null){
            return;
        }
        synchronized (mMutexForBGThread) {
            if(mBGThread != null){
                return;
            }

            mBGThread = new HandlerThread("JEWrapperMSG");
            mBGThread.start();
            mObserverHandler = new Handler(mBGThread.getLooper()) {
                @Override
                public boolean sendMessageAtTime (Message msg, long uptimeMillis) {
                    synchronized (mMutexForBGThread) {
                        if ( mbIsMsgThreadQuit ) {
                            return false;
                        }
                        return super.sendMessageAtTime( msg, uptimeMillis);
                    }
                }
                @Override
                public void handleMessage(Message msg) {
                    if (null != mObserverArray) {
                        for (JunkEngine.JunkEventCommandInterface o : mObserverArray) {
                            o.callbackMessage(msg.what, msg.arg1, msg.arg2, msg.obj);
                        }
                    }
                    if (JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO == msg.what && null != msg.obj) {
                        getObjPool().recycleObj((JunkEngineWrapperUpdateInfo) msg.obj);
                    }
                }
            };
            mbIsMsgThreadQuit = false;
        }

        mJunkEngine.initBackgroundThread();
    }


    class RubbishScanRequestCallback extends ScanRequestCallback {

        @Override
        public void onScanningItem(String strItemName) {
            super.onScanningItem(strItemName);
            if (null != mObserverArray) {
                //mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_INFO, 0, 0, strItemName));
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_RUBBLISH_INFO, 0, 0, strItemName));

            }
        }

        @Override
        public void onFoundItemSize(long nSize, boolean bChecked) {
            super.onFoundItemSize(nSize, bChecked);
            if (null != mObserverArray) {
//                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                mTotalScanSize.addAndGet(nSize);
                if (bChecked) {
                    mTotalCheckedScanSize.addAndGet(nSize);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                }
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                        JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                        getObjPool().obtainObj().updateValues(
                                mEngineStatus, mProgressPos.get(),
                                mTotalScanSize.get(),
                                mTotalCheckedScanSize.get(),
                                mTotalCleanSize.get(),
                                mProcessScanSize.get(),
                                mProcessCheckedScanSize.get(),
                                mProcessCleanSize.get(),
                                mSysCacheScanSize.get(),
                                mSysCacheCheckedScanSize.get(),
                                mSysCacheCleanSize.get())));
            }
        }

        @Override
        public void onScanBegin(JunkRequest request) {
            super.onScanBegin(request);
        }

        @Override
        public void onScanEnd(JunkRequest request, JunkResult result) {
            NLog.e(TAG,"Rubbish任务扫描完成: ---->");
            if (request.getRequestType() == JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV) {
                mAdvRubbishFileListForAppLeftovers = result.getDataList();
            } else {
                mRubbishFileListForAppLeftovers = result.getDataList();
                NLog.e(TAG,"Rubbish任务扫描完成: ---->残留数据为: "+ mRubbishFileListForAppLeftovers);
            }
            if (null != mObserverArray) {
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_LEFT_OVER_SCAN, 0, 0, null));
                if (mUseCache) {
                    List<BaseJunkBean> tempBaseJunkBean = request.getRequestType() == JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV ? mAdvRubbishFileListForAppLeftovers : mRubbishFileListForAppLeftovers;
                    calcCallbackStatus(tempBaseJunkBean);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, calcJunkInfoBaseSize(tempBaseJunkBean, CALC_SIZE_TYPE_CHECKED)));
                }
            }
        }
    }

    class UselessThumbnailScanRequestCallback extends ScanRequestCallback {

        @Override
        public void onScanningItem(String strItemName) {
            super.onScanningItem(strItemName);
            if (null != mObserverArray) {
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_INFO, 0, 0, strItemName));
            }
        }

        @Override
        public void onFoundItemSize(long nSize, boolean bChecked) {
            super.onFoundItemSize(nSize, bChecked);
            if (null != mObserverArray) {
//                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                mTotalScanSize.addAndGet(nSize);
                if (bChecked) {
                    mTotalCheckedScanSize.addAndGet(nSize);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                }
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                        JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                        getObjPool().obtainObj().updateValues(
                                mEngineStatus, mProgressPos.get(),
                                mTotalScanSize.get(),
                                mTotalCheckedScanSize.get(),
                                mTotalCleanSize.get(),
                                mProcessScanSize.get(),
                                mProcessCheckedScanSize.get(),
                                mProcessCleanSize.get(),
                                mSysCacheScanSize.get(),
                                mSysCacheCheckedScanSize.get(),
                                mSysCacheCleanSize.get())));
            }
        }

        @Override
        public void onScanBegin(JunkRequest request) {
            super.onScanBegin(request);
        }

        @Override
        public void onScanEnd(JunkRequest request, JunkResult result) {
            mUselessThumbnailList = result.getDataList();
            NLog.i(TAG,"UselessThumbnail %d",mUselessThumbnailList.size());
            if (null != mObserverArray) {
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_THUMBNAIL_SCAN, 0, 0, null));
                if (mUseCache) {
                    calcCallbackStatus(mUselessThumbnailList);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, calcJunkInfoBaseSize(mUselessThumbnailList, CALC_SIZE_TYPE_CHECKED)));
                }
            }
        }
    }

    class TempFolderScanRequestCallback extends ScanRequestCallback {

        @Override
        public void onScanningItem(String strItemName) {
            super.onScanningItem(strItemName);
            NLog.i(TAG,"TempFolderScanningItem");
            if (null != mObserverArray) {
                //mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_INFO, 0, 0, strItemName));
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_ADV_INFO, 0, 0, strItemName));
            }
        }

        @Override
        public void onFoundItemSize(long nSize, boolean bChecked) {
            super.onFoundItemSize(nSize, bChecked);
            NLog.i(TAG,"TempFolderonFoundItemSize");
            if (null != mObserverArray) {
//                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                mTotalScanSize.addAndGet(nSize);
                if (bChecked) {
                    mTotalCheckedScanSize.addAndGet(nSize);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                }
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                        JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                        getObjPool().obtainObj().updateValues(
                                mEngineStatus, mProgressPos.get(),
                                mTotalScanSize.get(),
                                mTotalCheckedScanSize.get(),
                                mTotalCleanSize.get(),
                                mProcessScanSize.get(),
                                mProcessCheckedScanSize.get(),
                                mProcessCleanSize.get(),
                                mSysCacheScanSize.get(),
                                mSysCacheCheckedScanSize.get(),
                                mSysCacheCleanSize.get())));
            }
        }

        @Override
        public void onScanBegin(JunkRequest request) {
            super.onScanBegin(request);
        }

        @Override
        public void onScanEnd(JunkRequest request, JunkResult result) {
            NLog.i(TAG,"TempFolderScanonScanEnd");
            if (request.getRequestType() == JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV) {
                mAdvRubbishFileListForTempFiles = result.getDataList();
            } else {
                mRubbishFileListForTempFiles = result.getDataList();
            }
            if (null != mObserverArray) {
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_TMP_FILES_SCAN, 0, 0, null));
                if (mUseCache) {
                    List<BaseJunkBean> tempBaseJunkBean = request.getRequestType() == JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV ? mAdvRubbishFileListForTempFiles : mRubbishFileListForTempFiles;
                    calcCallbackStatus(tempBaseJunkBean);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, calcJunkInfoBaseSize(tempBaseJunkBean, CALC_SIZE_TYPE_CHECKED)));
                }
            }
        }
    }

    class AdvScanRequestCallback extends ScanRequestCallback {
        public static final String TAG = "AdvScanRequestCallback";
        @Override
        public void onScanningItem(String strItemName) {
            super.onScanningItem(strItemName);
            NLog.d(TAG, "JunkEngineWrapper 正在扫描广告 strItemName = "+strItemName);
            if (null != mObserverArray) {
                //mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_INFO, 0, 0, strItemName));
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_ADV_INFO, 0, 0, strItemName));
            }
        }

        @Override
        public void onFoundItemSize(long nSize, boolean bChecked) {
            NLog.d(TAG, "JunkEngineWrapper AdvScanRequestCallback onFoundItemSize nSize = "+nSize +",bChecked = "+bChecked);
            super.onFoundItemSize(nSize, bChecked);
            if (null != mObserverArray) {
//                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                mTotalScanSize.addAndGet(nSize);
                if (bChecked) {
                    mTotalCheckedScanSize.addAndGet(nSize);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                }
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                        JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                        getObjPool().obtainObj().updateValues(
                                mEngineStatus, mProgressPos.get(),
                                mTotalScanSize.get(),
                                mTotalCheckedScanSize.get(),
                                mTotalCleanSize.get(),
                                mProcessScanSize.get(),
                                mProcessCheckedScanSize.get(),
                                mProcessCleanSize.get(),
                                mSysCacheScanSize.get(),
                                mSysCacheCheckedScanSize.get(),
                                mSysCacheCleanSize.get())));
            }
        }

        @Override
        public void onScanBegin(JunkRequest request) {
            super.onScanBegin(request);
        }

        @Override
        public void onScanEnd(JunkRequest request, JunkResult result) {
            mRubbishFileListForAdvFolders = result.getDataList();
            NLog.e(TAG, "JunkEngineWrapper 广告扫描完成  mRubbishFileListForAdvFolders = "+mRubbishFileListForAdvFolders);
            /*for ( BaseJunkBean junkInfoBase : mRubbishFileListForAppLeftovers ){
                NLog.e(TAG, "JunkEngineWrapper AdvScanRequestCallback onScanEnd junkInfoBase =  " + (SDcardRubbishResult)junkInfoBase);
            }*/
            if (null != mObserverArray) {
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_ADV_SCAN, 0, 0, null));
                if (mUseCache) {
                    calcCallbackStatus(mRubbishFileListForAdvFolders);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, calcJunkInfoBaseSize(mRubbishFileListForAdvFolders, CALC_SIZE_TYPE_CHECKED)));
                }
            }
        }
    }

    class APKFileScanRequestCallback extends ScanRequestCallback {

        @Override
        public void onScanningItem(String strItemName) {
            super.onScanningItem(strItemName);
            if (null != mObserverArray) {
                //mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_INFO, 0, 0, strItemName));
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_APKFILE_INFO, 0, 0, strItemName));
            }
        }

        @Override
        public void onFoundItemSize(long nSize, boolean bChecked) {
            super.onFoundItemSize(nSize, bChecked);
            if (null != mObserverArray) {
                if (nSize != -100) { // add by chaohao.zhou 由于安装包勾选状态改变
//                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                    mTotalScanSize.addAndGet(nSize);
                }
                if (bChecked) {
                    mTotalCheckedScanSize.addAndGet(nSize);
//                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                }
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                        JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                        getObjPool().obtainObj().updateValues(
                                mEngineStatus, mProgressPos.get(),
                                mTotalScanSize.get(),
                                mTotalCheckedScanSize.get(),
                                mTotalCleanSize.get(),
                                mProcessScanSize.get(),
                                mProcessCheckedScanSize.get(),
                                mProcessCleanSize.get(),
                                mSysCacheScanSize.get(),
                                mSysCacheCheckedScanSize.get(),
                                mSysCacheCleanSize.get())));
            }
        }

        @Override
        public void onScanBegin(JunkRequest request) {
            super.onScanBegin(request);
        }

        @Override
        public void onScanEnd(JunkRequest request, JunkResult result) {
            mApkCleanItemInfos = result.getDataList();
            if (null != mObserverArray) {
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_APK_SCAN, 0, 0, null));
                if (mUseCache) {
                    calcCallbackStatus(mApkCleanItemInfos);
                }
                // 现在无用安装包逻辑，只会在扫描结束的时候才会一次性将大小给到主页面 by chaohao.zhou
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, calcJunkInfoBaseSize(mApkCleanItemInfos, CALC_SIZE_TYPE_CHECKED)));
            }
        }
    }

    public ArrayList<JunkModel> convertToStdJunkModelList(boolean sort, int showAlterType, boolean isGetProcess) {
        return convertToStdJunkModelList(sort, showAlterType, false, isGetProcess, false);
    }

    public ArrayList<JunkModel> convertToStdJunkModelList(boolean sort, int showAlterType, boolean isGetProcess,boolean bForAdv) {
        return convertToStdJunkModelList(sort, showAlterType, bForAdv, isGetProcess, false);
    }

    public static Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> convertJunkModelToBaseJunkBeanMap(
            Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> junkInfoBaseMap, List<JunkModel> srcList) {

        RuntimeCheck.CheckMainUIThread();

        if (null == srcList || srcList.isEmpty()) {
            return junkInfoBaseMap;
        }

        if (null == junkInfoBaseMap) {
            junkInfoBaseMap = new HashMap<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>>();
        }

        for (JunkModel tmpModel : srcList) {
            junkInfoBaseMap = convertJunkModelToBaseJunkBeanMap(tmpModel, junkInfoBaseMap);
        }

        return junkInfoBaseMap;
    }

    public static Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> convertJunkModelToBaseJunkBeanMap(JunkModel tmpModel,
                                                                                                           Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> junkInfoBaseMap) {
        if (null == tmpModel) {
            return junkInfoBaseMap;
        }

        if (null == junkInfoBaseMap) {
            junkInfoBaseMap = new HashMap<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>>();
        }

        if (JunkModel.TYPE_SYSTEM_CACHE == tmpModel.getType()) {
            List<CacheInfo> cacheList = tmpModel.getChildList();

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE, tmpList);
            }
            tmpList.addAll(cacheList);
        } else if (JunkModel.TYPE_APP_CACHE == tmpModel.getType()) {
            List<CacheInfo> cacheList = tmpModel.getChildList();

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE, tmpList);
            }
            tmpList.addAll(cacheList);

        } else if (JunkModel.TYPE_APK_FILE == tmpModel.getType()) {
            APKModel tmpAPK = tmpModel.getApkModel();
            //junkInfoBaseList.add(tmpAPK);

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.APKFILE);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.APKFILE, tmpList);
            }
            tmpList.add(tmpAPK);

        } else if (JunkModel.TYPE_AD_FILE == tmpModel.getType()) {
            SDcardRubbishResult tmpRubbish = tmpModel.getSdcardRubbishResult();
            //junkInfoBaseList.add(tmpRubbish);

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.ADVERTISEMENT);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.ADVERTISEMENT, tmpList);
            }
            tmpList.add(tmpRubbish);

        } else if (JunkModel.TYPE_TEMP_FILE == tmpModel.getType()) {
            SDcardRubbishResult tmpRubbish = tmpModel.getSdcardRubbishResult();
            //junkInfoBaseList.add(tmpRubbish);

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER, tmpList);
            }
            tmpList.add(tmpRubbish);
        } else if (JunkModel.TYPE_APP_LEFT == tmpModel.getType()) {
            SDcardRubbishResult tmpRubbish = tmpModel.getSdcardRubbishResult();
            //junkInfoBaseList.add(tmpRubbish);

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER, tmpList);
            }
            tmpList.add(tmpRubbish);
        } else if(JunkModel.TYPE_SYS_FIXED_CACHE == tmpModel.getType()){
            List<CacheInfo> cacheList = tmpModel.getChildList();

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE, tmpList);
            }
            tmpList.addAll(cacheList);
        }else if(JunkModel.TYPE_VIDEO_OFF == tmpModel.getType()){
            /*VideoOfflineResult tmpVideo = tmpModel.getVideoOfflineResult();

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(EM_JUNK_DATA_TYPE.VIDEO_OFF);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(EM_JUNK_DATA_TYPE.VIDEO_OFF, tmpList);
            }
            tmpList.add(tmpVideo);*/
        } else if(JunkModel.TYPE_ROOT_CACHE == tmpModel.getType()){
            List<RootCacheInfo> rootCacheList = tmpModel.getRootChildList();
            if(rootCacheList != null){
                List<BaseJunkBean> tmpList=null;
                tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.ROOTCACHE);
                if (tmpList == null) {
                    tmpList = new ArrayList<BaseJunkBean>();
                    junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.ROOTCACHE, tmpList);
                }
                tmpList.addAll(rootCacheList);
            }else {
                List<CacheInfo> cacheList = tmpModel.getChildList();

                List<BaseJunkBean> tmpList=null;
                tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE);
                if (tmpList == null) {
                    tmpList = new ArrayList<BaseJunkBean>();
                    junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE, tmpList);
                }
                tmpList.addAll(cacheList);
            }
        } else if (JunkModel.TYPE_SCREEN_SHOTS_COMPRESS == tmpModel.getType()) {
            /*MediaFileList mfl = tmpModel.getMediaFileList();
            if (null != mfl) {
                ArrayList<MyMediaFile> screenShotsCompressList = mfl.getList();
                if (null != screenShotsCompressList) {
                    List<BaseJunkBean> tmpList = null;
                    tmpList = junkInfoBaseMap.get(EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS);
                    if (tmpList == null) {
                        tmpList = new ArrayList<BaseJunkBean>();
                        junkInfoBaseMap.put(EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS, tmpList);
                    }
                    tmpList.addAll(screenShotsCompressList);
                }
            }*/
        }

        return junkInfoBaseMap;
    }

    private long calcBaseJunkBeanSize(List<BaseJunkBean> list) {
        return calcJunkInfoBaseSize(list, CALC_SIZE_TYPE_ALL);
    }

    @Deprecated
    public void setObserver(JunkEngine.JunkEventCommandInterface observer) {
        addObserver(observer);
    }

    //for avoid multi-thread problem might cuase ClassCastException dumpKey : 609673537
    public synchronized void addObserver(final JunkEngine.JunkEventCommandInterface observer) {
        if (null == observer) {
            return;
        }
        mJunkEventObserver = observer;

        initBGThread();

        mObserverHandler.post(new Runnable() {
            @Override
            public void run() {
                if (null == mObserverArray) {
                    mObserverArray = new ArrayList<>();
                }

                if (!mObserverArray.add(observer)) {
                    return;
                }

                if (ENGINE_WRAPPER_STATUS_FINISH_SCAN == mEngineStatus
                        || ENGINE_WRAPPER_STATUS_FINISH_CLEAN == mEngineStatus) {
                    new Handler(mCtx.getMainLooper())
                            .post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mScanType == JUNK_WRAPPER_SCAN_TYPE_STD) {
                                        reCalcCheckedStdJunkModelSize(mbScanProcess && mbCallerScanProcess,
                                                mTotalScanSize, mTotalCheckedScanSize,
                                                mProcessScanSize, mProcessCheckedScanSize,
                                                mSysCacheScanSize, mSysCacheCheckedScanSize);
                                    }
                                    JunkEngineWrapperUpdateInfo info = getObjPool().obtainObj();
                                    observer.callbackMessage(
                                            JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                                            info.updateValues(
                                                    mEngineStatus, mProgressPos.get(),
                                                    mTotalScanSize.get(),
                                                    mTotalCheckedScanSize.get(),
                                                    mTotalCleanSize.get(),
                                                    mProcessScanSize.get(),
                                                    mProcessCheckedScanSize.get(),
                                                    mProcessCleanSize.get(),
                                                    mSysCacheScanSize.get(),
                                                    mSysCacheCheckedScanSize.get(),
                                                    mSysCacheCleanSize.get()));
                                    getObjPool().recycleObj(info);
                                }
                            });
                    return;
                }

                JunkEngineWrapperUpdateInfo info = getObjPool().obtainObj();
                observer.callbackMessage(
                        JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                        info.updateValues(
                                mEngineStatus, mProgressPos.get(),
                                mTotalScanSize.get(),
                                mTotalCheckedScanSize.get(),
                                mTotalCleanSize.get(),
                                mProcessScanSize.get(),
                                mProcessCheckedScanSize.get(),
                                mProcessCleanSize.get(),
                                mSysCacheScanSize.get(),
                                mSysCacheCheckedScanSize.get(),
                                mSysCacheCleanSize.get()));
                getObjPool().recycleObj(info);
            }
        });
    }

    public void removeObserver(final JunkEngine.JunkEventCommandInterface observer) {
        mObserverHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mObserverArray != null) {
                    mObserverArray.remove(observer);
                }

                if (null == mObserverArray || mObserverArray.isEmpty()) {
                    destroy();
                }
            }
        });
    }

    private void destroy() {
        NLog.d(TAG, "destroy");
        mJunkEngine.destroy();
        if(mBGThread !=null){
            synchronized (mMutexForBGThread) {
                if(mBGThread !=null) {
                    try {
                        mbIsMsgThreadQuit = true;
                        mBGThread.quit();
                    } catch (Exception e) {
                        NLog.printStackTrace(e);
                    }
                    mBGThread = null;
                }
            }
        }
    }

    class SdCacheScanRequestCallback extends ScanRequestCallback {

        @Override
        public void onScanningItem(String strItemName) {
            super.onScanningItem(strItemName);
            NLog.i(TAG,"SdCacheonScanningItem"+strItemName);
            if (null != mObserverArray) {
                //mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_INFO, 0, 0, strItemName));
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_SDCACHE_INFO, 0, 0, strItemName));
            }
        }

        @Override
        public void onFoundItemSize(long nSize, boolean bChecked) {
            super.onFoundItemSize(nSize, bChecked);
            NLog.i(TAG,"SdCacheScanonFoundItemSize"+nSize);
            if (null != mObserverArray) {
//                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                mTotalScanSize.addAndGet(nSize);
                if (bChecked) {
                    mTotalCheckedScanSize.addAndGet(nSize);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                }
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                        JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                        getObjPool().obtainObj().updateValues(
                                mEngineStatus, mProgressPos.get(),
                                mTotalScanSize.get(),
                                mTotalCheckedScanSize.get(),
                                mTotalCleanSize.get(),
                                mProcessScanSize.get(),
                                mProcessCheckedScanSize.get(),
                                mProcessCleanSize.get(),
                                mSysCacheScanSize.get(),
                                mSysCacheCheckedScanSize.get(),
                                mSysCacheCleanSize.get())));
            }
        }

        @Override
        public void onScanBegin(JunkRequest request) {
            super.onScanBegin(request);
        }

        @Override
        public void onScanEnd(JunkRequest request, JunkResult result) {
            if (request.getRequestType() == JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE_ADV) {
                mAdvSDCacheList = result.getDataList();
            } else {
                mSDCacheList = result.getDataList();
            }
            if (null != mObserverArray) {
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SD_SCAN, 0, 0, null));
                if (mUseCache) {
                    List<BaseJunkBean> tempBaseJunkBean = request.getRequestType() == JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE_ADV ? mAdvSDCacheList : mSDCacheList;
                    calcCallbackStatus(tempBaseJunkBean);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, calcJunkInfoBaseSize(tempBaseJunkBean, CALC_SIZE_TYPE_CHECKED)));
                }
            }
            NLog.d(TAG, "junk--engine--wrapper--result:"+(result!=null?result.toString():""));
        }
    }

    private void calcCallbackStatus(List<BaseJunkBean> dataList) {
        if (null == dataList || dataList.isEmpty()) {
            return;
        }

        int type = 0;    ///< 0表示未判断，-1表示其它类别，1表示进程类别，2表示系统缓存类别
        long size = 0L;
        long checkedSize = 0L;
        long itemSize = 0L;
        for (BaseJunkBean s : dataList) {
            if (null == s) {
                continue;
            }

            if (0 == type) {
                switch (s.getJunkDataType()) {
                    case PROCESS:
                        type = 1;
                        break;

                    case SYSCACHE:
                        type = 2;
                        break;

                    default:
                        type = -1;
                        break;
                }
            }

            itemSize = s.getSize();
            size += itemSize;

            if (s.isCheck()) {
                checkedSize += itemSize;
            }
        }

        if (1 == type) {
            mProcessScanSize.addAndGet(size);
            mProcessCheckedScanSize.addAndGet(checkedSize);
        } else if (2 == type) {
            mSysCacheScanSize.addAndGet(size);
            mSysCacheCheckedScanSize.addAndGet(checkedSize);
        }

        mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                getObjPool().obtainObj().updateValues(
                        mEngineStatus, mProgressPos.get(),
                        mTotalScanSize.addAndGet(size),
                        mTotalCheckedScanSize.addAndGet(checkedSize),
                        mTotalCleanSize.get(),
                        mProcessScanSize.get(),
                        mProcessCheckedScanSize.get(),
                        mProcessCleanSize.get(),
                        mSysCacheScanSize.get(),
                        mSysCacheCheckedScanSize.get(),
                        mSysCacheCleanSize.get())));
    }

    private ObjPoolMgr.KObjPool<JunkEngineWrapperUpdateInfo> getObjPool() {
        ObjPoolMgr.KObjPool<JunkEngineWrapperUpdateInfo> objPool = null;
        if (null != mObjPool) {
            objPool = mObjPool.get();
        }

        if (null == objPool) {
            objPool = ObjPoolMgr.getInstance().getObjPool(JunkEngineWrapperUpdateInfo.class, 32,
                    new ObjPoolMgr.IKPoolObjCreator<JunkEngineWrapperUpdateInfo>() {
                        @Override
                        public JunkEngineWrapperUpdateInfo create() {
                            return new JunkEngineWrapperUpdateInfo();
                        }
                    });
            mObjPool = new SoftReference<>(objPool);
        }

        return objPool;
    }

    @Override
    public void setCfgLong(int nCfgId, long lCfgValue) {
        NLog.i(TAG,"getCfgInt void"+nCfgId);
    }

    @Override
    public long getCfgLong(int nCfgId, long lDefValue) {
        NLog.i(TAG,"getCfgInt long"+nCfgId);

        switch (nCfgId) {
            case ENG_CFG_NAME_VALID_CACHE_DATA_TIME:
                if (smQuited) {
                    return DATA_CACHE_VALID_TIME_FOR_REOPEN;
                }
                break;

            default:
                break;
        }

        return lDefValue;
    }

    @Override
    public int getCfgInt(int nCfgId, int nDefault) {
        NLog.i(TAG,"getCfgInt"+nCfgId);
        int cfgValue;

        if (!mHaveCfgCleanEngine) {
            mJunkRecycleFileSize = CloudCfgDataWrapper.getCloudCfgIntValue(
                    CloudCfgKey.JUNK_CLEAN_FLAG_KEY,
                    CloudCfgKey.JUNK_STD_RECYCLE_SIZE,
                    MIN_RECYCLE_FILE_SIZE);

            mJunkRecycleSwtich = CloudCfgDataWrapper.getCloudCfgIntValue(
                    CloudCfgKey.JUNK_CLEAN_FLAG_KEY,
                    CloudCfgKey.JUNK_STD_RECYCLE_SWITCH,
                    RECYCLE_FILE_DEFAULT_SWTICH);
            mHaveCfgCleanEngine = true;
        }

        switch (nCfgId) {
            case ENG_CFG_ID_PIC_CLEAN_MODE:
                cfgValue = (mCleanType == JUNK_WRAPPER_SCAN_TYPE_STD ? 1 : 0);
                break;

            case ENG_CFG_ID_PIC_RECYCLE_SWTICH:
                cfgValue = mJunkRecycleSwtich;
                break;

            case ENG_CFG_ID_PIC_RECYCLE_SIZE:
                cfgValue = mJunkRecycleFileSize;
                break;

            default:
                cfgValue = -1;
        }

        return cfgValue;
    }

    @Override
    public String getCfgString(int nCfgId, String defaultStringValue) {
        NLog.i(TAG,"getRestCleanList"+nCfgId);
        String cfgString = "";
        switch (nCfgId) {
            case ENG_CFG_ID_PIC_CLEAN_FOLDER_EXT:
                //cfgString = UpdateManager.getInstance().getSdCardExternalPath();
                break;

            default:
                break;
        }

        return cfgString;
    }

    @Override
    public List<BaseJunkBean> getRestCleanList(int nCfgId) {
        NLog.i(TAG,"getRestCleanList"+nCfgId);
        if (nCfgId == ENG_CFG_ID_REST_CLEAN_ITEM_LIST) {
            return mApkCleanItemInfos;
        }

        return null;
    }

    @Override
    public void setCfgList(int nCfgId, List<String> list) {
        NLog.i(TAG,"setCfgList"+nCfgId);
        if (nCfgId == ENG_CFG_ID_SET_RECYCLE_ITEM_LIST) {
            mJunkEngine.setRecycleListFor2SdCard(list);
        }
    }

    @Override
    public void onSuccess() {
        NLog.i(TAG,"<---> onSuccess");
        mbJunkActionFinished = true;
        long nJunkSize=0L;
        long nUnCheckJunkSize=0L;
        long nProcessScanSize=0L;
        if(mJunkEngine.getEngineStatus() == JunkEngine.EM_ENGINE_STATUS.SCANNING) {

            nJunkSize = mProcessScanSize.get();
            nJunkSize = mTotalScanSize.get() - nJunkSize;

            nProcessScanSize = mProcessCheckedScanSize.get();
            nUnCheckJunkSize = mTotalCheckedScanSize.get() - nProcessScanSize;
            nUnCheckJunkSize = nJunkSize - nUnCheckJunkSize;

//			nJunkSize = getAllStorageJunkItemSize();
//          nUnCheckJunkSize = getAllUncheckedStorageJunkItemSize();
//          nProcessScanSize = calcProcessSize(mProcessInfoList);

//            recordAppCacheRcd();
            junksize=nJunkSize;
            if (mScanType == JUNK_WRAPPER_SCAN_TYPE_STD) {
                if (!mbOnlyVideoOfflineScan && !mbOnlySdCacheScanResult && !(nJunkSize == nUnCheckJunkSize)) {
                    JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_STD, nJunkSize);
                    JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_ZEUS,getZeusScanSize());
                    if (mbScanProcess && mbCallerScanProcess) {
                        JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_PROC, nProcessScanSize);
                    }
                    if (mRubbishFileListForAdvFolders != null && !mRubbishFileListForAdvFolders.isEmpty()){
                        JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_RUB_ADV,calcJunkInfoBaseSize(mRubbishFileListForAdvFolders));
                    }
                    if (mApkCleanItemInfos != null && !mApkCleanItemInfos.isEmpty()){
                        JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_APK,calcJunkInfoBaseSize(mApkCleanItemInfos));
                    }

                }
            } else if (JUNK_WRAPPER_SCAN_TYPE_ADV == mScanType) {
                JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_ADV, nJunkSize);
              //  JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_DOWNLOAD, DownloadManager.getSize(getDownloadItems()));
                //JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_BLUETOOTH, DownloadManager.getSize(getBluetoothItems()));
            }

        } else {
            //			nJunkSize = getAllUncheckedStorageJunkItemSize();
           /* if (null != mDataReporter) {
                mDataReporter.recordCacheCleanInfo();
            }
            if (null != mAdvDataReporter) {
                mAdvDataReporter.recordCacheCleanInfo();
            }

            if (JUNK_WRAPPER_SCAN_TYPE_STD == mCleanType) {
                AppSdCacheInfoMgr.getInstance().resetAllAppSdCacheSize();
                AppSdCacheInfoMgr.getInstance().commitAppSdCacheSize();
            }*/

            //更新后台扫描结果值，给结果页、首页头部等 其他入口使用
            if (mScanType == JUNK_WRAPPER_SCAN_TYPE_STD) {
                if (!mbOnlyVideoOfflineScan && !mbOnlySdCacheScanResult) {
                    JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_STD, 0L);
                    JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_ZEUS, 0L);
                }
                JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_RUB_ADV, 0L);
                JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_APK, 0L);
            } else if (JUNK_WRAPPER_SCAN_TYPE_ADV == mScanType) {
                JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_ADV, 0L);
            }
        }

        if (mScanType == JUNK_WRAPPER_SCAN_TYPE_STD) {
            JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_VIDEO, getVideoOfflineTotalSize());
            long mVideoOfflineSize = 0L;
            if (null != mVideoOfflineList && !mVideoOfflineList.isEmpty()) {
                mVideoOfflineSize = mVideoOfflineList.size();
            }
            JunkSizeMgr.getInstance().notifyJunkSize(JunkSizeMgr.JUNK_SIZE_MGR_TYPE_VIDEO_NUM, mVideoOfflineSize);
        }

        synchronized (mMutexMsgSent) {
            if (mbProcessActionStarted) {
                if (!mbNotifyFinishMsgSent && mbProcessActionFinished) {
                    sendFinishMsg(mJunkEngine.getEngineStatus());
                    mbNotifyFinishMsgSent = true;
                }
            } else {
                sendFinishMsg(mJunkEngine.getEngineStatus());
                mbNotifyFinishMsgSent = true;
            }
        }
    }

    public long getVideoOfflineTotalSize(){
        return calcJunkInfoBaseSize(mVideoOfflineList);
    }

    private long getZeusScanSize() {
        long size = calcJunkInfoBaseSize(mRootCacheList);
        if (null == mSDCacheList || mSDCacheList.isEmpty()) {
            return size;
        }

        CacheInfo cacheInfo = null;
        for (BaseJunkBean item : mSDCacheList) {
            if (null == item) {
                continue;
            }

            cacheInfo = (CacheInfo)item;
            if(cacheInfo.getFileType() != BaseJunkBean.FileType.File){
                continue;
            }

            size += cacheInfo.getSize();
        }

        return size;
    }

    private static final int CALC_SIZE_TYPE_ALL = 0;
    private static final int CALC_SIZE_TYPE_CHECKED = 1;
    private static final int CALC_SIZE_TYPE_UNCHECKED = 2;
    private long calcJunkInfoBaseSize(List<BaseJunkBean> list) {
        return calcJunkInfoBaseSize(list, CALC_SIZE_TYPE_ALL);
    }

    private long calcJunkInfoBaseSize(List<BaseJunkBean> list, int calcType) {

        if (null == list || list.isEmpty()) {
            return 0L;
        }

        long size = 0L;

        for (BaseJunkBean info : list) {
            if (null == info) {
                continue;
            }

            switch (calcType) {
                default:
                case CALC_SIZE_TYPE_ALL:
                    break;

                case CALC_SIZE_TYPE_UNCHECKED:
                    if (info.isCheck()) {
                        continue;
                    }
                    break;

                case CALC_SIZE_TYPE_CHECKED:
                    if (!info.isCheck()) {
                        continue;
                    }
                    break;
            }

            size += info.getSize();
        }

        return size;
    }

    private void sendFinishMsg(JunkEngine.EM_ENGINE_STATUS nEngineStatus) {
        if (null != mObserverArray) {
            mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                    JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                    getObjPool().obtainObj().updateValues(
                            mEngineStatus, mProgressPos.get(),
                            mTotalScanSize.get(),
                            mTotalCheckedScanSize.get(),
                            mTotalCleanSize.get(),
                            mProcessScanSize.get(),
                            mProcessCheckedScanSize.get(),
                            mProcessCleanSize.get(),
                            mSysCacheScanSize.get(),
                            mSysCacheCheckedScanSize.get(),
                            mSysCacheCleanSize.get())));

            //非扫描 即清理

            if (nEngineStatus == JunkEngine.EM_ENGINE_STATUS.SCANNING) {
                changeEngineStatus(JunkEngineWrapper.ENGINE_WRAPPER_STATUS_FINISH_SCAN);
                NLog.i(TAG,"<---> 1264 SendHandlerFinishScan");
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SCAN, 0, 0, null));
            } else {
                if (mJunkEngine != null) {
                    mJunkEngine.setAllJunkCleanSize((int)(mTotalCleanSize.get() / 1024));
                }
                changeEngineStatus(JunkEngineWrapper.ENGINE_WRAPPER_STATUS_FINISH_CLEAN);
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_CLEAN, 0, 0, null));
            }

            mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                    JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                    getObjPool().obtainObj().updateValues(
                            mEngineStatus, mProgressPos.get(),
                            mTotalScanSize.get(),
                            mTotalCheckedScanSize.get(),
                            mTotalCleanSize.get(),
                            mProcessScanSize.get(),
                            mProcessCheckedScanSize.get(),
                            mProcessCleanSize.get(),
                            mSysCacheScanSize.get(),
                            mSysCacheCheckedScanSize.get(),
                            mSysCacheCleanSize.get())));
        }
    }

    @Override
    public void onError(int errorCode) {
        NLog.i(TAG,"onError"+errorCode);
    }

    @Override
    public void onProgress(int nStep, int nMaxStep) {
        NLog.i(TAG,"onProgress"+nStep);
        if (null != mObserverArray) {
            mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_ADD_PROGRESS, nStep, nMaxStep, null));
            int progPosPercent = mProgressPosValue.addAndGet(nStep) * 100 / (0 != nMaxStep ? nMaxStep : 1);
            if (progPosPercent > 100) {
                progPosPercent = 100;
            }
            mProgressPos.set(progPosPercent);
        }
    }

    private void changeEngineStatus(int status) {
        mEngineStatus = status;
        if (ENGINE_WRAPPER_STATUS_FINISH_SCAN == status || ENGINE_WRAPPER_STATUS_FINISH_CLEAN == status) {
            mValidTimeStart = System.currentTimeMillis();
        }
    }

    public boolean startScan(int nScanType, boolean bReportScanTime ) {
        NLog.i(TAG,"startScan"+nScanType);

        if (mJunkEngine.getEngineStatus() != JunkEngine.EM_ENGINE_STATUS.IDLE) {
            return false;
        }
        resetEngineWrapper();
        TimingUtil.start(JunkEngineImpl.ENGINE_TIME);
        //初始化Engine时间统计
        Analytics analytics = Analytics.getInstance();
        analytics.reset();
        mJunkEngine.setIsForegroundScan(mIsForegroundScan);
        mScanType = nScanType;

        if (0 != (nScanType & JUNK_WRAPPER_SCAN_TYPE_STD)) {
            changeEngineStatus(JunkEngineWrapper.ENGINE_WRAPPER_STATUS_SCANNING);
            initStdScan();
            //控制是否扫描内存缓存
            if (mbCallerScanProcess) {
                mbProcessActionStarted = true;
                startScanProcessEngine();
            }
        }

        long nLimitSize = CloudCfgDataWrapper.getCloudCfgLongValue(
                CloudCfgKey.JUNK_SCAN_FLAG_KEY,
                CloudCfgKey.JUNK_CHK_SPARSEFILE_LIMIT_SIZE,
                0);
        // PathOperFunc.setChkSparseFileLimitSize( nLimitSize );
        mJunkEngine.setCallback(this);
        mJunkEngine.setEngineConfig(this);
        //mJunkEngine.setReportScanTime(bReportScanTime);
        mJunkEngine.startScan();
        return true;
    }

    SdCacheScanRequestCallback mSdCacheScanCB = null;
    RubbishScanRequestCallback mRubbishScanCB = new RubbishScanRequestCallback();
    private List<BaseJunkBean> mMyPhotoList = null;
    private List<BaseJunkBean> mMyAudioList = null;
    private List<BaseJunkBean> mCalcFolderList = null;
    private List<JunkModel> mCleanItemList = null;
    Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> mCleanMap = null;
    private JunkModel mOtherCategoryModel = null;
    private JunkModel mPhotoCategoryModel = null;
    private JunkModel mPhotoCategoryDetailModel = null;

    private void resetEngineWrapper() {
        mJunkEngine = new JunkEngineImpl();
        setDataManagerCacheDisable(!mbScanCacheEnable);
        SdCacheScanRequestCallback mSdCacheScanCB = null;
        AdvScanRequestCallback advScanCB;
        TempFolderScanRequestCallback mTmpFolderScanCB = null;
        RubbishScanRequestCallback mRubbishScanCB;
        mCleanType = JUNK_WRAPPER_SCAN_TYPE_STD;
        mCleanItemList = null;
        mSdCacheScanCB = null;
        mRubbishScanCB = new RubbishScanRequestCallback();


        mSysCacheList = null;
        mSysFixedFileList = null;
        mSDCacheList = null;
        mAdvSDCacheList = null;
        mVideoOfflineList = null;
        mScreenShotsCompressList = null;
        mAllCacheInfoMap = new HashMap<String, List<CacheInfo>>();
        mAllAdvCacheInfoMap = new HashMap<String, List<CacheInfo>>();

        mRubbishFileListForTempFiles = null;
        mAdvRubbishFileListForTempFiles = null;
        mRubbishFileListForAppLeftovers = null;
        mAdvRubbishFileListForAppLeftovers = null;
        mRubbishFileListForAdvFolders = null;
        mRubbishFileListForBigFiles = null;

        mUselessThumbnailList = null;
        mApkCleanItemInfos = null;
        mProcessInfoList = null;

        mMyPhotoList = null;
        mMyAudioList = null;
        mCalcFolderList = null;

        mCleanItemList = null;
        mbScanProcess=false;
        mCleanType = JUNK_WRAPPER_SCAN_TYPE_STD;

        mCleanMap = null;

        mOtherCategoryModel = null;
        mPhotoCategoryModel = null;
        mPhotoCategoryDetailModel = null;

        mValidTimeStart = 0L;
        mEngineStatus = ENGINE_WRAPPER_STATUS_NONE;
        mProgressPos.set(0);
        mProgressPosValue.set(0);
        mTotalCheckedScanSize.set(0L);
        mTotalScanSize.set(0L);
        mTotalCleanSize.set(0L);
        mProcessCheckedScanSize.set(0L);
        mProcessScanSize.set(0L);
        mProcessCleanSize.set(0L);
        mSysCacheCheckedScanSize.set(0L);
        mSysCacheScanSize.set(0L);
        mSysCacheCleanSize.set(0L);

        mbScanProcess = ServiceConfigManager.getInstanse(mCtx)
                .getScanMemorySwitch();

        mbNotifyFinishMsgSent = false;
        mbJunkActionFinished = false;
        mbProcessActionFinished = false;
        mbProcessActionStarted = false;
    }

    public long getSystemCacheSize() {
        return mSysCacheScanSize.get() + calcJunkInfoBaseSize(mSysFixedFileList);
    }

    public long getTotalScanSize() {
        long totalScanSize = mTotalScanSize.get();
        NLog.i(TAG, "totalScanSize %s ", totalScanSize);
        return (totalScanSize);
    }

    public long getTotalCheckScanSize(){
        long totalcheckScanSize = mTotalCheckedScanSize.get();
        return (totalcheckScanSize);
    }

    public long getAppCacheSize() {
        long appsize=calcJunkInfoBaseSize(mSDCacheList);
        return (appsize);
    }

    public long getApkSize() {
        long apksize=calcJunkInfoBaseSize(mApkCleanItemInfos);
        return (apksize);
    }

    public long getAdLeftTmpSize() {
        long advsize=calcJunkInfoBaseSize(mRubbishFileListForAdvFolders);
        long tmpsize=calcJunkInfoBaseSize(mRubbishFileListForTempFiles);
        long thumbnailSize = calcJunkInfoBaseSize(mUselessThumbnailList);
        return (advsize+tmpsize + thumbnailSize);
    }

    public long getLeftSize() {
        long leftsize=calcJunkInfoBaseSize(mRubbishFileListForAppLeftovers);
        NLog.i(TAG,"leftsize %s ",leftsize);
        return (leftsize);
    }

    class SysCacheScanRequestCallback extends ScanRequestCallback {
        private  final String TAG = SysCacheScanRequestCallback.class.getSimpleName();
        @Override
        public void onScanningItem(String strItemName) {
            super.onScanningItem(strItemName);
            NLog.i(TAG, " onScanningItem strItemName %s", strItemName);
            if (null != mObserverArray) {
                //mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_INFO, 0, 0, strItemName));
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_SYSCACHE_INFO, 0, 0, strItemName));
            }
        }

        @Override
        public void onFoundItemSize(long nSize, boolean bChecked) {
            super.onFoundItemSize(nSize, bChecked);
            NLog.i(TAG, " onFoundItemSize nsize %d" , nSize);
            if (null != mObserverArray) {
//                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));

                mTotalScanSize.addAndGet(nSize);
                mSysCacheScanSize.addAndGet(nSize);
                if (bChecked) {
                    mTotalCheckedScanSize.addAndGet(nSize);
                    mSysCacheCheckedScanSize.addAndGet(nSize);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                }

                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                        JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                        getObjPool().obtainObj().updateValues(
                                mEngineStatus, mProgressPos.get(),
                                mTotalScanSize.get(),
                                mTotalCheckedScanSize.get(),
                                mTotalCleanSize.get(),
                                mProcessScanSize.get(),
                                mProcessCheckedScanSize.get(),
                                mProcessCleanSize.get(),
                                mSysCacheScanSize.get(),
                                mSysCacheCheckedScanSize.get(),
                                mSysCacheCleanSize.get())));
            }
        }

        @Override
        public void onScanBegin(JunkRequest request) {
            super.onScanBegin(request);
        }

        @Override
        public void onScanEnd(JunkRequest request, JunkResult result) {
            mSysCacheList = result.getDataList();
            if (null != mObserverArray) {
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SYS_SCAN, 0, 0, null));
                if (mUseCache) {
                    calcCallbackStatus(mSysCacheList);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, calcJunkInfoBaseSize(mSysCacheList, CALC_SIZE_TYPE_CHECKED)));
                }
            }
        }
    }

    class SysFixedFileScanRequestCallback extends ScanRequestCallback {
        private final String TAG = SysFixedFileScanRequestCallback.class.getSimpleName();

        @Override
        public void onScanBegin(JunkRequest request) {
            super.onScanBegin(request);
        }

        @Override
        public void onScanEnd(JunkRequest request, JunkResult result) {
            mSysFixedFileList = result.getDataList();
            int size = 0;
            if (mSysFixedFileList != null){
                size = mSysFixedFileList.size();
            }
            NLog.i(TAG, " onScanEnd mSysFixedFileList %s  size %d", mSysFixedFileList.toString(), size);
            if(null != mObserverArray) {
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SYS_FIXED_SCAN, 0, 0, null));
                if (mUseCache) {
                    calcCallbackStatus(mSysFixedFileList);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, calcJunkInfoBaseSize(mSysFixedFileList, CALC_SIZE_TYPE_CHECKED)));
                }
            }
        }

        @Override
        public void onScanningItem(String strItemName) {
            super.onScanningItem(strItemName);
            NLog.i(TAG, "SysFixed strItemName %s", strItemName);
            if (null != mObserverArray) {
                //mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_INFO, 0, 0, strItemName));
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_SYSCACHE_INFO, 0, 0, strItemName));
            }
        }

        @Override
        public void onFoundItemSize(long nSize, boolean bChecked) {
            super.onFoundItemSize(nSize, bChecked);
            NLog.i(TAG, "SysFixed onFoundItemSize %d", nSize);
            if (null != mObserverArray) {
//                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                mTotalScanSize.addAndGet(nSize);
                if (bChecked) {
                    mTotalCheckedScanSize.addAndGet(nSize);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                }
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                        JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                        getObjPool().obtainObj().updateValues(
                                mEngineStatus, mProgressPos.get(),
                                mTotalScanSize.get(),
                                mTotalCheckedScanSize.get(),
                                mTotalCleanSize.get(),
                                mProcessScanSize.get(),
                                mProcessCheckedScanSize.get(),
                                mProcessCleanSize.get(),
                                mSysCacheScanSize.get(),
                                mSysCacheCheckedScanSize.get(),
                                mSysCacheCleanSize.get())));

            }
        }

    }

    class RootCacheScanRequestCallback extends ScanRequestCallback {

        @Override
        public void onScanningItem(String strItemName) {
            super.onScanningItem(strItemName);
        }

        @Override
        public void onFoundItemSize(long nSize, boolean bChecked) {
            super.onFoundItemSize(nSize, bChecked);
            if (null != mObserverArray) {
//                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                mTotalScanSize.addAndGet(nSize);
                if (bChecked) {
                    mTotalCheckedScanSize.addAndGet(nSize);
                    mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nSize));
                }
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                        JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                        getObjPool().obtainObj().updateValues(
                                mEngineStatus, mProgressPos.get(),
                                mTotalScanSize.get(),
                                mTotalCheckedScanSize.get(),
                                mTotalCleanSize.get(),
                                mProcessScanSize.get(),
                                mProcessCheckedScanSize.get(),
                                mProcessCleanSize.get(),
                                mSysCacheScanSize.get(),
                                mSysCacheCheckedScanSize.get(),
                                mSysCacheCleanSize.get())));
            }
        }

        @Override
        public void onScanEnd(JunkRequest request, JunkResult result) {
            mRootCacheList = result.getDataList();
            if (null != mObserverArray) {
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_ROOT_CACHE_SCAN_FINISH, 0, 0, null));
            }
        }

    }

    public void notifyStop() {
        if(!mJunkEngine.getStopFlag()){
            mJunkEngine.notifyStop();
        }
    }


    private void initStdScan() {

        if(mbScanSdCache){
            //sd卡
            if (null == mSdCacheScanCB) {
                mSdCacheScanCB = new SdCacheScanRequestCallback();
            }
            ScanRequest sdCacheScanRequest = new ScanRequest();
            sdCacheScanRequest.setRequestType(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE);
            sdCacheScanRequest.setScanCallback(mSdCacheScanCB);
            mJunkEngine.addScanRequest(sdCacheScanRequest);
        }

        if(mbScanSysCache){
            //系统
            SysCacheScanRequestCallback sysCacheScanCB = new SysCacheScanRequestCallback();
            ScanRequest sysCacheScanRequest = new ScanRequest();
            sysCacheScanRequest.setRequestType(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);
            sysCacheScanRequest.setScanCallback(sysCacheScanCB);
            mJunkEngine.addScanRequest(sysCacheScanRequest);

            //fixSystemCache
            SysFixedFileScanRequestCallback sysFixedFileScanCB = new SysFixedFileScanRequestCallback();
            ScanRequest sysFixedFileScanRequest = new ScanRequest();
            sysFixedFileScanRequest.setRequestType(JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE);
            sysFixedFileScanRequest.setScanCallback(sysFixedFileScanCB);
            mJunkEngine.addScanRequest(sysFixedFileScanRequest);

            //RootCache
//            RootCacheScanRequestCallback rootCacheScanCB = new RootCacheScanRequestCallback();
//            ScanRequest rootCacheScanRequest = new ScanRequest();
//            rootCacheScanRequest.setRequestType(JunkRequest.EM_JUNK_DATA_TYPE.ROOTCACHE);
//            rootCacheScanRequest.setScanCallback(rootCacheScanCB);
//            mJunkEngine.addScanRequest(rootCacheScanRequest);
        }

        if(mbScanUselessThumbnail){
            // 无用缩略图
            UselessThumbnailScanRequestCallback uselessThumbnailScanCB = new UselessThumbnailScanRequestCallback();
            ScanRequest uselessThumbnailScanRequest = new ScanRequest();
            uselessThumbnailScanRequest.setRequestType(JunkRequest.EM_JUNK_DATA_TYPE.USELESSTHUMBNAIL);
            uselessThumbnailScanRequest.setScanCallback(uselessThumbnailScanCB);
            mJunkEngine.addScanRequest(uselessThumbnailScanRequest);
        }

        if(mbScanRubbish){
            //卸载残留
            if (null == mRubbishScanCB) {
                mRubbishScanCB = new RubbishScanRequestCallback();
            }
            ScanRequest rubbishScanRequest = new ScanRequest();
            rubbishScanRequest.setRequestType(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER);
            rubbishScanRequest.setScanCallback(mRubbishScanCB);
            mJunkEngine.addScanRequest(rubbishScanRequest);
        }

        if(mbScanAdDirCache){
            //广告
            if(advScanCB == null){
                advScanCB = new AdvScanRequestCallback();
            }
            ScanRequest advScanRequest = new ScanRequest();
            advScanRequest.setRequestType(JunkRequest.EM_JUNK_DATA_TYPE.ADVERTISEMENT);
            advScanRequest.setScanCallback(advScanCB);
            mJunkEngine.addScanRequest(advScanRequest);

            //temp folder
            if (null == mTmpFolderScanCB) {
                mTmpFolderScanCB = new TempFolderScanRequestCallback();
            }
            ScanRequest tmpFolderScanRequest = new ScanRequest();
            tmpFolderScanRequest.setRequestType(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER);
            tmpFolderScanRequest.setScanCallback(mTmpFolderScanCB);
            mJunkEngine.addScanRequest(tmpFolderScanRequest);
        }

        if(mbScanApkFile){
            //apk file
            if (null == mApkFileScanCB) {
                mApkFileScanCB = new APKFileScanRequestCallback();
            }
            ScanRequest apkFileScanRequest = new ScanRequest();
            apkFileScanRequest.setRequestType(JunkRequest.EM_JUNK_DATA_TYPE.APKFILE);
            apkFileScanRequest.setScanCallback(mApkFileScanCB);
            mJunkEngine.addScanRequest(apkFileScanRequest);
        }

    }

    private void startScanProcessEngine() {
        mbProcessActionFinished = false;
        BoostScanSetting setting = new BoostScanSetting();
        setting.mTaskType = BoostEngine.BOOST_TASK_MEM;
        ProcessScanSetting procSetting = new ProcessScanSetting();
        procSetting.isUseDataManager = true;
        setting.mSettings.put(setting.mTaskType, procSetting);
        BoostScanEngine scanEngine = new BoostScanEngine(mCtx, setting);
        scanEngine.scan(new BoostScanEngine.ScanEngineCallback() {
            long currentProcessMemory = 0L;
            final long nFakeProcessMemory = 5 * 1024 * 1024L;  //for scan progress
            long nLastProgress = 0L;

            @Override
            public void onScanStart(int type) {
                TimingUtil.start(BoostScanTask.class.getName());
            }

            @Override
            public void onScanProgress(int type, Object data) {
                if (type == BoostEngine.BOOST_TASK_MEM) {
                    if (null != data && data instanceof ProcessModel) {
                        ProcessModel pm = ((ProcessModel) data);

                        if (null != mObserverArray) {
                            //mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_INFO, 0, 0, pm.getPkgName()));
                            mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_PROCESS_INFO, 0, 0, pm.getPkgName()));
                            if (!mbProcessActionFinished && currentProcessMemory < nFakeProcessMemory){
                                currentProcessMemory += nFakeProcessMemory / 5;
                                // TODO 目前不知道为什么要加上这个，但是现在造成了结果数值会大于扫描出来的垃圾数值
//                                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE, 0, 0, nFakeProcessMemory/5));
                            }
                        }

                        if (mbJunkActionFinished) {
                            long nNow = SystemClock.uptimeMillis();
                            if (mProgressPos.get() <= 55) {
                                onProgress(5, 100);
                            } else if (mProgressPos.get() < 90 &&
                                    nNow - nLastProgress > 2) {
                                nLastProgress = nNow;
                                onProgress(10, 100);
                            }
                        }
                    }
                }
            }

            @Override
            public void onScanPreFinish(int type, Object results) {

            }

            @Override
            public void onScanFinish(int type, Object results) {
                if (type == BoostEngine.BOOST_TASK_MEM) {
                    Analytics.endTask(BoostScanTask.class);
                    mbProcessActionFinished = true;

                    if (mbJunkActionFinished &&
                            mProgressPos.get() < 100) {
                        onProgress(100, 100);
                    }

                    //FIXME 第一版本不做，这个需要从云端拉取配置数据
                    boolean bCompetitorInstalled = false/*CompetitorStrategy.isCompetitorFeatureEnabled(CompetitorStrategy.FEATURE_ID_BOOST)*/;
                    if (results != null && results instanceof ProcessResult) {
                        ProcessResult procResult = (ProcessResult) results;
                        if (null == mProcessInfoList) {
                            mProcessInfoList = new ArrayList<ProcessModel>();
                        }
                        List<ProcessModel> data = procResult.getData();
                        for (ProcessModel pm : data) {
                            /*注释原有代码*/
//                            if (pm.isChecked()) {
//                                long nSize = pm.getMemory();
//                                mTotalScanSize.addAndGet(nSize);
//                                mProcessScanSize.addAndGet(nSize);
//
//                                mTotalCheckedScanSize.addAndGet(nSize);
//                                mProcessCheckedScanSize.addAndGet(nSize);
//                                mProcessInfoList.add(pm);
//                            } else if (!pm.mIsHide && bCompetitorInstalled) {
//                                long nSize = pm.getMemory();
//                                mTotalScanSize.addAndGet(nSize);
//                                mProcessScanSize.addAndGet(nSize);
//                                mProcessInfoList.add(pm);
//                            }
                            /*注释原有代码*/
                            NLog.d(TAG,pm.toString());
                            if(!pm.mIsHide && bCompetitorInstalled){
                                long nSize = pm.getMemory();
                                mTotalScanSize.addAndGet(nSize);
                                mProcessScanSize.addAndGet(nSize);
                                mProcessInfoList.add(pm);
                            } else if ("com.tct.gapp.middleman".equals(pm.getPkgName()) || "com.tcl.live".equals(pm.getPkgName())) {

                            } else{
                                //此处暂不做check的判断，只要扫描到了就统计，避免valid状态数据因为没有check而没有回传
                                //回传的数据全部设置为check状态，因为扫描完成默认为全部勾选
//                                pm.setChecked(true);

                                if (!pm.mIsHide&&pm.getMemory()>10.24) {

                                    int mark = ProcessWhiteListDAOHelper.getInstance().getProcessWhiteListIgnoreLevel(pm.getPkgName());
                                    if(pm.isHasLabel()
                                            ||(!pm.isHasLabel()&&!ProcessWhiteListMarkHelper.isInWhiteList(mark)&&ProcessWhiteListMarkHelper.isUserModified(mark))){
                                        long nSize = pm.getMemory();
                                        mTotalScanSize.addAndGet(nSize);
                                        mProcessScanSize.addAndGet(nSize);
                                        if (pm.isChecked()) {
                                            mTotalCheckedScanSize.addAndGet(nSize);
                                            mProcessCheckedScanSize.addAndGet(nSize);
                                        }
                                        mProcessInfoList.add(pm);
                                    }
                                }
                            }
                        }


                    }
                    if (null != mObserverArray) {
                        mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                                JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                                getObjPool().obtainObj().updateValues(
                                        mEngineStatus, mProgressPos.get(),
                                        mTotalScanSize.get(),
                                        mTotalCheckedScanSize.get(),
                                        mTotalCleanSize.get(),
                                        mProcessScanSize.get(),
                                        mProcessCheckedScanSize.get(),
                                        mProcessCleanSize.get(),
                                        mSysCacheScanSize.get(),
                                        mSysCacheCheckedScanSize.get(),
                                        mSysCacheCleanSize.get())));

                        mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_PROCESS_SCAN, 0, 0, null));
                    }

                    synchronized (mMutexMsgSent) {
                        if (!mbNotifyFinishMsgSent && mbJunkActionFinished) {
                            sendFinishMsg(JunkEngine.EM_ENGINE_STATUS.SCANNING);
                            mbNotifyFinishMsgSent = true;
                        }
                    }
                }

            }
        });
    }

    private void startCleanProcessEngine(List<ProcessModel> cleanList) {
        mbProcessActionFinished = false;
        BoostCleanSetting setting = new BoostCleanSetting();
        setting.mTaskType = BoostEngine.BOOST_TASK_MEM;

        if (null != cleanList && !cleanList.isEmpty()) {
            ProcessCleanSetting cleanSetting = new ProcessCleanSetting();
            cleanSetting.mCleanData = new ArrayList<ProcessModel>();
            cleanSetting.mCleanData.addAll(cleanList);
            setting.mSettings.put(BoostEngine.BOOST_TASK_MEM, cleanSetting);
        }

        BoostCleanEngine cleanEngine = new BoostCleanEngine(mCtx, setting);
        cleanEngine.clean(new BoostCleanEngine.CleanEngineCallback() {
            @Override
            public void onCleanStart(int type) {

            }

            @Override
            public void onCleanProgress(int type, Object data) {
                if (null == mObserverArray || null == data) {
                    return;
                }

                ProcessModel pm = (ProcessModel)data;
                if (null == pm) {
                    return;
                }

                long nSize = pm.getMemory();
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_REMOVE_DATA_ITEM_SIZE, 0, 0, nSize));
                long totalCleanSize = mTotalCleanSize.addAndGet(nSize);
                if (mToBeCleanedSize <= 0L) {
                    mToBeCleanedSize = totalCleanSize;
                    if (0 == totalCleanSize) {
                        mToBeCleanedSize = 1L;
                    }
                }
                mProgressPos.set((int)(totalCleanSize * 100L / mToBeCleanedSize));
                long temp = -1 * nSize;

                mProcessCleanSize.addAndGet(nSize);
                if (pm.isChecked()) {
                    mProcessCheckedScanSize.addAndGet(temp);
                    mTotalCheckedScanSize.addAndGet(temp);
                }
                mProcessScanSize.addAndGet(temp);
                mTotalScanSize.addAndGet(temp);

                mObserverHandler.sendMessage(
                        mObserverHandler.obtainMessage(
                                JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                                getObjPool().obtainObj().updateValues(
                                        mEngineStatus, mProgressPos.get(),
                                        mTotalScanSize.get(),
                                        mTotalCheckedScanSize.get(),
                                        totalCleanSize,
                                        mProcessScanSize.get(),
                                        mProcessCheckedScanSize.get(),
                                        mProcessCleanSize.get(),
                                        mSysCacheScanSize.get(),
                                        mSysCacheCheckedScanSize.get(),
                                        mSysCacheCleanSize.get())));

                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_REMOVE_DATA_ITEM, 0, 0, pm));
            }

            @Override
            public void onCleanFinish(int type, Object obj) {

                mbProcessActionFinished = true;
                synchronized (mMutexMsgSent) {
                    if (!mbNotifyFinishMsgSent && mbJunkActionFinished) {
                        sendFinishMsg(JunkEngine.EM_ENGINE_STATUS.CLEANING);
                        mbNotifyFinishMsgSent = true;
                    }
                }
            }
        });
    }

    /**
     * 开始清理
     * @param bDeleteOneItem
     * @return
     */
    public boolean startClean(boolean bDeleteOneItem) {
        if (mJunkEngine.getEngineStatus() != JunkEngine.EM_ENGINE_STATUS.IDLE) {
            return false;
        } else if (null == mCleanItemList || mCleanItemList.isEmpty()) {
            return false;
        }

        PrepareForCleanReport();

        mbProcessActionStarted = false;
        mbJunkActionFinished = false;
        mbNotifyFinishMsgSent = false;
        mProgressPos.set(0);
        mProgressPosValue.set(0);
        mToBeCleanedSize = 0L;
        if (mScanType == JUNK_WRAPPER_SCAN_TYPE_STD) {
            reCalcCheckedStdJunkModelSize(mbScanProcess && mbCallerScanProcess,
                    mTotalScanSize, mTotalCheckedScanSize,
                    mProcessScanSize, mProcessCheckedScanSize,
                    mSysCacheScanSize, mSysCacheCheckedScanSize);
        }

        changeEngineStatus(JunkEngineWrapper.ENGINE_WRAPPER_STATUS_CLEANING);

        ArrayList<ProcessModel> tmpProcessList = new ArrayList<ProcessModel>();
        getProcessCleanList(mCleanItemList, tmpProcessList);
        if (!tmpProcessList.isEmpty()) {
            mbProcessActionStarted = true;
            startCleanProcessEngine(tmpProcessList);
        }

        boolean bDoJunkClean = initClean(bDeleteOneItem);
        if (bDoJunkClean) {
            doCleanResultList();
            mJunkEngine.setEngineConfig(this);
            mJunkEngine.setCallback(this);
            mJunkEngine.setIsAdvancedClean(mCleanType == JUNK_WRAPPER_SCAN_TYPE_ADV);
            mJunkEngine.startClean();

        } else if (!mbProcessActionStarted && !bDoJunkClean) {
            changeEngineStatus(JunkEngineWrapper.ENGINE_WRAPPER_STATUS_NONE);
            return false;
        } else {
            mbJunkActionFinished = true;
        }

        return true;
    }

    public void setCleanType(int nCleanType) {
        mCleanType = nCleanType;
    }

    public void setCleanItemList(List<JunkModel> junkModelList) {
        mCleanItemList = junkModelList;
    }

    /**
     * 清除前获取手机存储状态
     * 依次获取系统清理前大小 第一张卡大小 第二张卡大小 一体机大小
     */
    private void PrepareForCleanReport() {

    }

    private void reCalcCheckedStdJunkModelSize(boolean bCalcProcessInTotal,
                                               AtomicLong totalScanSize, AtomicLong totalCheckedScanSize,
                                               AtomicLong processScanSize, AtomicLong processCheckedScanSize,
                                               AtomicLong sysCacheScanSize, AtomicLong sysCacheCheckedScanSize) {
        Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> tmpCleanMap =
                convertJunkModelToJunkInfoBaseMap(null, getStdJunkModelList(false, -1, true));
        calcSize(tmpCleanMap, bCalcProcessInTotal,
                totalScanSize, totalCheckedScanSize,
                processScanSize, processCheckedScanSize,
                sysCacheScanSize, sysCacheCheckedScanSize);
    }

    private void calcSize(Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> cleanMap,
                          boolean bCalcProcessInTotal,
                          AtomicLong totalScanSize, AtomicLong totalCheckedScanSize,
                          AtomicLong processScanSize, AtomicLong processCheckedScanSize,
                          AtomicLong sysCacheScanSize, AtomicLong sysCacheCheckedScanSize) {

        if ((null == cleanMap || cleanMap.isEmpty()) &&
                (null == mProcessInfoList || mProcessInfoList.isEmpty())) {
            totalScanSize.set(0L);
            totalCheckedScanSize.set(0L);
            processScanSize.set(0L);
            processCheckedScanSize.set(0L);
            sysCacheScanSize.set(0L);
            sysCacheCheckedScanSize.set(0L);

            return ;
        }

        long totalSize = 0L;
        long totalCheckedSize = 0L;
        long processSize = 0L;
        long processCheckedSize = 0L;
        long sysCacheSize = 0L;
        long sysCacheCheckedSize = 0L;

        int nType = 0; //2表示系统缓存。

        if(null != mProcessInfoList && !mProcessInfoList.isEmpty()){
            for ( ProcessModel procModel : mProcessInfoList ) {
                if (null == procModel) {
                    continue;
                }
                totalSize += procModel.getMemory();
                processSize += procModel.getMemory();
                if (procModel.isChecked()) {
                    totalCheckedSize += procModel.getMemory();
                    processCheckedSize += procModel.getMemory();
                }
            }
        }

        if (null != cleanMap) {
            for (Map.Entry<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> listEntry : cleanMap.entrySet()) {
                if (null == listEntry) {
                    continue;
                }

                List<BaseJunkBean> dataList = listEntry.getValue();
                if (null == dataList || dataList.isEmpty()) {
                    continue;
                }

                switch (listEntry.getKey()) {
                    case SYSCACHE:
                        nType = 2;
                        break;

                    default:
                        nType = 0;
                        break;
                }

                long tempSize = 0L;
                for (BaseJunkBean item : dataList) {
                    tempSize = item.getSize();
                    totalSize += tempSize;

                    switch (nType) {

                        case 2:
                            sysCacheSize += tempSize;
                            break;
                    }

                    if (item.isCheck()) {
                        totalCheckedSize += tempSize;
                        switch (nType) {

                            case 2:
                                sysCacheCheckedSize += tempSize;
                                break;
                        }
                    }
                }
            }
        }

        totalScanSize.set(totalSize);
        totalCheckedScanSize.set(totalCheckedSize);
        processScanSize.set(processSize);
        processCheckedScanSize.set(processCheckedSize);
        sysCacheScanSize.set(sysCacheSize);
        sysCacheCheckedScanSize.set(sysCacheCheckedSize);
    }

    public List<JunkModel> getStdJunkModelList(boolean bSort, int showAlterType, boolean isGetProcess) {
        return convertToStdJunkModelList(bSort, showAlterType, isGetProcess);
    }
    private ArrayList<JunkModel> convertToStdJunkModelList(boolean sort,
                                                           int showAlterType, boolean bForAdv, boolean isGetProcess, boolean queryChecked) {

        RuntimeCheck.CheckMainUIThread();
        ArrayList<JunkModel> junkModelList = new ArrayList<>();
        JunkStandardAdviceProvider adviceProvider = new JunkStandardAdviceProvider();
        HashMap<String, JunkModel> rootMap = new HashMap<>();
        ArrayList<JunkModel> rootCacheJunkModelList = new ArrayList<>();
        ArrayList<JunkModel> cacheJunkModelList = new ArrayList<>();
        JunkModel cacheCategoryModel = JunkModel.createCategoryModel(JunkModel.CATEGORY_TYPE_CACHE, 0);
        JunkModel fileCategoryModel = JunkModel.createCategoryModel(JunkModel.CATEGORY_TYPE_FILE, 0);
        fileCategoryModel.setCategoryHidden(true);
        //personal
        ArrayList<JunkModel> adv2stdJunkModelList = new ArrayList<JunkModel>();
        JunkModel personalCategoryModel = JunkModel.createCategoryModel(JunkModel.CATEGORY_TYPE_PERSONAL, 0);

        convertRootCacheListToRootCacheMap();
        convertSDCacheListToSDCacheMap();

        //RootCache
        if(null != mRootCacheList && !mRootCacheList.isEmpty()){
            ArrayList<JunkModel> tempList = new ArrayList<JunkModel>(mRootCacheInfoMap.size());
            Collection<List<RootCacheInfo>> allCache = mRootCacheInfoMap.values();
            for (List<RootCacheInfo> cacheList : allCache) {
                if (cacheList != null && cacheList.size() > 0) {
                    RootCacheInfo firstCacheInfo = cacheList.get(0);

                    JunkModel appModel = new JunkModel();
                    appModel.setType(JunkModel.TYPE_ROOT_CACHE);
                    appModel.setCategoryType(JunkModel.CATEGORY_TYPE_FILE);
                    appModel.setCategoryModel(fileCategoryModel);
                    appModel.setRootCacheInfo(firstCacheInfo);
                    appModel.setRootChildList(cacheList);
                    appModel.setHidden(true);
                    tempList.add(appModel);

                    rootMap.put(firstCacheInfo.getPkgName(), appModel);
                    if(sort){
                        Collections.sort(cacheList);
                    }
                }
            }
            rootCacheJunkModelList.addAll(tempList);
        }

        // Cache
        if (null != mSysCacheList && !mSysCacheList.isEmpty()) {
            CacheInfo sysAllInfo = new CacheInfo(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);
            sysAllInfo.setAppName(ResUtil.getString(R.string.junk_tag_system_cache));
            sysAllInfo.setInfoType(CacheInfo.INFOTYPE_SYSTEMCACHE);
            sysAllInfo.setAppCount(mSysCacheList.size());

            ArrayList<CacheInfo> myList = new ArrayList<CacheInfo>();
            for (BaseJunkBean info: mSysCacheList) {
                CacheInfo tmpCacheInfo = (CacheInfo)info;
                if (!queryChecked || tmpCacheInfo.isCheck()) {
                    myList.add(tmpCacheInfo);
                }
                sysAllInfo.setSize(sysAllInfo.getSize() + tmpCacheInfo.getSize());
                if (!info.isCheck()) {
                    sysAllInfo.setCheck(false);
                }
            }

            if (!queryChecked || !myList.isEmpty()) {
                JunkModel systemCacheModel = new JunkModel();
                systemCacheModel.setType(JunkModel.TYPE_SYSTEM_CACHE);
                systemCacheModel.setCategoryType(bForAdv ? JunkModel.CATEGORY_TYPE_STANDARD : JunkModel.CATEGORY_TYPE_CACHE);
                systemCacheModel.setCategoryModel(cacheCategoryModel);
                systemCacheModel.setCacheInfo(sysAllInfo);
                systemCacheModel.setChildList(myList);
                systemCacheModel.setHidden(bForAdv);
                //systemCacheModel.setBelongList(mAllCacheInfoList);
                cacheJunkModelList.add(systemCacheModel);
                if(sort){
                    Collections.sort(myList);
                }
            }
        }

        // Cache
        if (null != mSysFixedFileList && !mSysFixedFileList.isEmpty()) {
            CacheInfo sysAllInfo = new CacheInfo(JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE);
            sysAllInfo.setAppName(ResUtil.getString(R.string.junk_tag_system_fixed_cache));
            sysAllInfo.setInfoType(CacheInfo.INFOTYPE_SYSFIXEDFILE);
            sysAllInfo.setAppCount(mSysFixedFileList.size());

            ArrayList<CacheInfo> myList = new ArrayList<CacheInfo>();
            for (BaseJunkBean info: mSysFixedFileList) {
                CacheInfo tmpCacheInfo = (CacheInfo)info;
                if (!queryChecked || tmpCacheInfo.isCheck()) {
                    myList.add(tmpCacheInfo);
                }
                sysAllInfo.setSize(sysAllInfo.getSize() + tmpCacheInfo.getSize());
                if (!info.isCheck()) {
                    sysAllInfo.setCheck(false);
                }
            }

            if (!queryChecked || !myList.isEmpty()) {
                JunkModel systemCacheModel = new JunkModel();
                systemCacheModel.setType(JunkModel.TYPE_SYS_FIXED_CACHE);
                systemCacheModel.setCategoryType(JunkModel.CATEGORY_TYPE_CACHE);
                systemCacheModel.setCategoryModel(cacheCategoryModel);
                systemCacheModel.setCacheInfo(sysAllInfo);
                systemCacheModel.setChildList(myList);
                cacheJunkModelList.add(systemCacheModel);
                if(sort){
                    Collections.sort(myList);
                }
            }
        }

        // Cache
        if (null != mAllCacheInfoMap && !mAllCacheInfoMap.isEmpty()) {
            ArrayList<JunkModel> tempList = new ArrayList<JunkModel>(mAllCacheInfoMap.size());
            Collection<List<CacheInfo>> allCache = mAllCacheInfoMap.values();
            for (List<CacheInfo> cacheList : allCache) {
                if (cacheList != null && cacheList.size() > 0) {
                    if(!bForAdv){
                        JunkModel appModel = null;
                        ArrayList<CacheInfo> childList = new ArrayList<CacheInfo>();

                        JunkModel adv2stdModel = null;
                        ArrayList<CacheInfo> adv2stdModelChildList = new ArrayList<CacheInfo>();

                        Iterator<CacheInfo> iterator = cacheList.iterator();
                        if(iterator == null){
                            continue;
                        }
                        while(iterator.hasNext()) {
                            CacheInfo cacheInfo = iterator.next();
                            if (cacheInfo.isAdv2StdItem()) {
                                adv2stdModelChildList.add(cacheInfo);
                                if (adv2stdModel == null) {
                                    adv2stdModel = new JunkModel();
                                    adv2stdModel.setType(JunkModel.TYPE_APP_CACHE);
                                    adv2stdModel.setCategoryType(JunkModel.CATEGORY_TYPE_PERSONAL);
                                    adv2stdModel.setCategoryModel(personalCategoryModel);
                                    adv2stdModel.setHidden(bForAdv);
                                    adv2stdModel.setCacheInfo(cacheInfo);
                                    adv2stdModel.setChildList(adv2stdModelChildList);

                                    adv2stdJunkModelList.add(adv2stdModel);
                                }
                                iterator.remove();
                            }
                        }
                        if(sort){
                            Collections.sort(adv2stdModelChildList);
                        }
                    }

                    if(cacheList.size() == 0){
                        continue;
                    }

                    CacheInfo firstCacheInfo = cacheList.get(0);

                    JunkModel appModel = new JunkModel();
                    appModel.setType(JunkModel.TYPE_APP_CACHE);
                    appModel.setCategoryType(bForAdv ? JunkModel.CATEGORY_TYPE_STANDARD : JunkModel.CATEGORY_TYPE_CACHE);
                    appModel.setCategoryModel(cacheCategoryModel);
                    appModel.setCacheInfo(firstCacheInfo);
                    if (queryChecked) {
                        ArrayList<CacheInfo> checkedItemList = new ArrayList<CacheInfo>(cacheList.size());
                        for (CacheInfo item : cacheList) {
                            if (item.isCheck()) {
                                checkedItemList.add(item);
                            }
                        }

                        if (!checkedItemList.isEmpty()) {
                            appModel.setChildList(checkedItemList);
                            if(sort){
                                Collections.sort(checkedItemList);
                            }
                        }
                    } else {
                        appModel.setChildList(cacheList);
                        if(sort){
                            Collections.sort(cacheList);
                        }
                    }

                    appModel.setAdviceStr(adviceProvider.getAdviceStr(firstCacheInfo.getPackageName()));
                    appModel.setAdviceContentStr(adviceProvider.getAdviceStrContent(firstCacheInfo.getPackageName()));
                    appModel.setHidden(bForAdv);

                    List<CacheInfo> appList = appModel.getChildList();
                    if (null != appList && !appList.isEmpty()) {
                        tempList.add(appModel);
                    }
                }
            }
            if(sort){
                Collections.sort(tempList);
            }
            if (!tempList.isEmpty()) {
                cacheJunkModelList.addAll(tempList);
            }
        }

        if (!adv2stdJunkModelList.isEmpty() && !bForAdv) {
            if(sort){
                Collections.sort(adv2stdJunkModelList);
            }
            personalCategoryModel.setCategoryCount(adv2stdJunkModelList.size());
            junkModelList.add(personalCategoryModel);
            junkModelList.addAll(adv2stdJunkModelList);
        }


        if (!cacheJunkModelList.isEmpty() && !bForAdv) {
            cacheCategoryModel.setCategoryCount(cacheJunkModelList.size());
            junkModelList.add(cacheCategoryModel);
            junkModelList.addAll(cacheJunkModelList);
        }

        NLog.e(TAG, "TestActivity JunkEngineWrapper onScanEnd mRubbishFileListForAppLeftovers = "+mRubbishFileListForAppLeftovers);

        if (null != mRubbishFileListForAppLeftovers && !mRubbishFileListForAppLeftovers.isEmpty()) {
            JunkModel categoryModel = JunkModel.createCategoryModel(JunkModel.CATEGORY_TYPE_RESIDUAL,
                    mRubbishFileListForAppLeftovers.size());
            if (!bForAdv) {
                junkModelList.add(categoryModel);
            }
            ArrayList<JunkModel> tempList = new ArrayList<>(mRubbishFileListForAppLeftovers.size());
            for ( BaseJunkBean junkInfoBase : mRubbishFileListForAppLeftovers ) {
                SDcardRubbishResult sdDcardRubbishResult = (SDcardRubbishResult)junkInfoBase;

                if (queryChecked && !sdDcardRubbishResult.isCheck()) {
                    continue;
                }
           /*     if ( "unknow".equalsIgnoreCase(sdDcardRubbishResult.getApkName())){
                    sdDcardRubbishResult.setAppName("Kugou");
                    sdDcardRubbishResult.setApkName("Kugou");
                }

                if ("unknow".equalsIgnoreCase(sdDcardRubbishResult.getChineseName())){
                    sdDcardRubbishResult.setChineseName("Kugou");
                }*/

                JunkModel model = new JunkModel();
                model.setType(JunkModel.TYPE_APP_LEFT);
                model.setCategoryType(bForAdv ? JunkModel.CATEGORY_TYPE_STANDARD : JunkModel.CATEGORY_TYPE_RESIDUAL);
                model.setCategoryModel(categoryModel);
                model.setSdcardRubbishResult(sdDcardRubbishResult);
                model.setHidden(bForAdv);
                tempList.add(model);
            }
            if(sort){
                Collections.sort(tempList);
            }
            if (!tempList.isEmpty()) {
                junkModelList.addAll(tempList);
            }
        }

        JunkModel newCacheCategoryModel = JunkModel.createCategoryModel(JunkModel.CATEGORY_TYPE_CACHE_AD,0);

        ArrayList<JunkModel> newCacheJunkModelList = new ArrayList<>();

        // AD
        NLog.d(TAG, "convertToStdJunkModelList mRubbishFileListForAdvFolders = "+mRubbishFileListForAdvFolders);
        if (null != mRubbishFileListForAdvFolders && !mRubbishFileListForAdvFolders.isEmpty()) {
            ArrayList<JunkModel> tempList = new ArrayList<>(
                    mRubbishFileListForAdvFolders.size());
            for (BaseJunkBean junkInfoBase : mRubbishFileListForAdvFolders ) {
                SDcardRubbishResult sdDcardRubbishResult = (SDcardRubbishResult)junkInfoBase;

                if (queryChecked && !sdDcardRubbishResult.isCheck()) {
                    continue;
                }

                JunkModel model = new JunkModel();
                model.setType(JunkModel.TYPE_AD_FILE);
                model.setCategoryType(bForAdv ? JunkModel.CATEGORY_TYPE_STANDARD : JunkModel.CATEGORY_TYPE_CACHE_AD);
                model.setCategoryModel(newCacheCategoryModel);
                model.setSdcardRubbishResult(sdDcardRubbishResult);
                model.setHidden(bForAdv);
                tempList.add(model);
            }
            if (sort) {
                Collections.sort(tempList);
            }
            if (!tempList.isEmpty()) {
                newCacheJunkModelList.addAll(tempList);
            }
        }


        JunkModel thumbNailModel = null;
        // temp
        if (null != mRubbishFileListForTempFiles && !mRubbishFileListForTempFiles.isEmpty()) {

            String strTmp = mCtx.getResources().getString(R.string.junk_tag_RF_ImageThumbnails);

            ArrayList<JunkModel> tempList = new ArrayList<JunkModel>(
                    mRubbishFileListForTempFiles.size());
            for (BaseJunkBean junkInfoBase : mRubbishFileListForTempFiles ) {
                SDcardRubbishResult sdDcardRubbishResult = (SDcardRubbishResult)junkInfoBase;

                if (queryChecked && !sdDcardRubbishResult.isCheck()) {
                    continue;
                } else if (sdDcardRubbishResult.getApkName().equals(strTmp)) {

                    if (null == thumbNailModel) {
                        thumbNailModel = new JunkModel();
                    }
                    thumbNailModel.setType(JunkModel.TYPE_TEMP_FILE);
                    thumbNailModel.setCategoryType(JunkModel.CATEGORY_TYPE_OTHER);
                    thumbNailModel.setSdcardRubbishResult(sdDcardRubbishResult);

                    continue;
                }

                JunkModel model = new JunkModel();
                model.setType(JunkModel.TYPE_TEMP_FILE);
                model.setCategoryType(bForAdv ? JunkModel.CATEGORY_TYPE_STANDARD : JunkModel.CATEGORY_TYPE_CACHE_AD);
                model.setCategoryModel(newCacheCategoryModel);
                model.setSdcardRubbishResult(sdDcardRubbishResult);
                model.setHidden(bForAdv);
                tempList.add(model);
            }
            if (sort) {
                Collections.sort(tempList);
            }
            if (!tempList.isEmpty()) {
                newCacheJunkModelList.addAll(tempList);
            }
        }

        if (null != mUselessThumbnailList && !mUselessThumbnailList.isEmpty()) {
            ArrayList<JunkModel> tempList = new ArrayList<JunkModel>(mUselessThumbnailList.size());
            for ( BaseJunkBean junkInfoBase : mUselessThumbnailList ) {
                SDcardRubbishResult sdDcardRubbishResult = (SDcardRubbishResult)junkInfoBase;

                if (queryChecked && !sdDcardRubbishResult.isCheck()) {
                    continue;
                }

                JunkModel model = new JunkModel();
                model.setType(JunkModel.TYPE_TEMP_FILE);
                model.setCategoryType(bForAdv ? JunkModel.CATEGORY_TYPE_STANDARD : JunkModel.CATEGORY_TYPE_CACHE_AD);
                model.setCategoryModel(newCacheCategoryModel);
                model.setSdcardRubbishResult(sdDcardRubbishResult);
                model.setHidden(bForAdv);
                tempList.add(model);
            }
            if(sort){
                Collections.sort(tempList);
            }
            if (!tempList.isEmpty()) {
                newCacheJunkModelList.addAll(tempList);
            }
        }

        if(newCacheJunkModelList.size() > 0){
            if (!bForAdv) {
                if (mForceTopType == NOTIFICATION_ADV){
                    junkModelList.add(0,newCacheCategoryModel);
                    junkModelList.addAll(1,newCacheJunkModelList);
                }else{
                    junkModelList.add(newCacheCategoryModel);
                    junkModelList.addAll(newCacheJunkModelList);
                }
            }else{
                junkModelList.addAll(newCacheJunkModelList);
            }
        }

        //***********************以下顺序不要改变***************************
        if (null != mApkCleanItemInfos && !mApkCleanItemInfos.isEmpty()) {
            JunkModel categoryModel = JunkModel.createCategoryModel(JunkModel.CATEGORY_TYPE_APK,
                    mApkCleanItemInfos.size());
            if (!bForAdv && mForceTopType != NOTIFICATION_APK) {
                junkModelList.add(categoryModel);
            }
            ArrayList<JunkModel> tempList = new ArrayList<JunkModel>(mApkCleanItemInfos.size());
            for ( BaseJunkBean junkInfoBase : mApkCleanItemInfos ) {
                APKModel apkModel = (APKModel)junkInfoBase;

                if (queryChecked && !apkModel.isCheck()) {
                    continue;
                }

                JunkModel model = new JunkModel();
                model.setType(JunkModel.TYPE_APK_FILE);
                model.setCategoryType(bForAdv ? JunkModel.CATEGORY_TYPE_STANDARD : JunkModel.CATEGORY_TYPE_APK);
                model.setCategoryModel(categoryModel);
                model.setApkModel(apkModel);
                model.setHidden(bForAdv);
                tempList.add(model);
            }
            if(sort){
                Collections.sort(tempList);
            }
            if (!tempList.isEmpty()) {
                if (mForceTopType == NOTIFICATION_APK){
                    junkModelList.add(0,categoryModel);
                    junkModelList.addAll(1,tempList);
                }else{
                    junkModelList.addAll(tempList);
                }
            }
        }

//        if (null != mScreenShotsCompressList && !mScreenShotsCompressList.isEmpty() && !bForAdv) {
//            JunkModel categoryModel = JunkModel.createCategoryModel(JunkModel.CATEGORY_TYPE_SCREEN_SHOTS_COMPRESS,
//                    mScreenShotsCompressList.size());
//            MediaFileList mfl = new MediaFileList();
//            mfl.addAll(mScreenShotsCompressList);
//            categoryModel.setMediaFileList(mfl);
//            categoryModel.setChecked(true);
//            categoryModel.setType(JunkModel.TYPE_SCREEN_SHOTS_COMPRESS);
//            junkModelList.add(0, categoryModel);
//        }

        if ((!rootCacheJunkModelList.isEmpty()) && !bForAdv) {

            if(sort){
                Collections.sort(rootCacheJunkModelList);
            }
            fileCategoryModel.setCategoryCount(rootCacheJunkModelList.size());
            junkModelList.add(0,fileCategoryModel);
            junkModelList.addAll(1,rootCacheJunkModelList);
        }

//		if(!junkModelList.isEmpty() && showAlterType == JunkManagerActivity.SHOW_ALTER_SYSTEM && !bForAdv){
//			JunkModel alterSystemModel = JunkModel.createCategoryModel(JunkModel.CATEGORY_TYPE_ALTER_SYSTEM, 0);
//            junkModelList.add(0,alterSystemModel);
//        }

        if(null != mProcessInfoList && !mProcessInfoList.isEmpty() && !bForAdv && isGetProcess){
            JunkModel categoryModel = JunkModel.createCategoryModel(JunkModel.CATEGORY_TYPE_MEMORY_JUNK, mProcessInfoList.size());
            categoryModel.setCategoryHidden(true);
            junkModelList.add(categoryModel);
            ArrayList<JunkModel> tempList = new ArrayList<JunkModel>(mProcessInfoList.size());
            for ( ProcessModel procModel : mProcessInfoList ) {
                if (queryChecked && !procModel.isChecked()) {
                    continue;
                }

                JunkModel model = new JunkModel();
                model.setType(JunkModel.TYPE_PROCESS);
                model.setCategoryType(JunkModel.CATEGORY_TYPE_MEMORY_JUNK);
                model.setCategoryModel(categoryModel);
                model.setProcessModel(procModel);
                model.setChecked(procModel.isChecked());
                model.setHidden(true);
                model.setProcessChecked(procModel.isChecked());
                tempList.add(model);
            }
            if (sort) {
                Collections.sort(tempList);
            }
            if (!tempList.isEmpty()) {
                junkModelList.addAll(tempList);
            } else {
                junkModelList.remove(junkModelList.size() - 1);
            }
        }

        boolean needShowVideoCard = CloudCfgDataWrapper.getCloudCfgBooleanValue(
                CloudCfgKey.JUNK_SCAN_FLAG_KEY,
                CloudCfgKey.JUNK_SCAN_VIDEO_CARD_NEED_SHOW,
                true);

        boolean needShowVideoCardForRoot = false;//没装视频app，不显示卡片
        boolean checkRoot = SuExec.getInstance().checkRoot();

        if (needShowVideoCard && mVideoOfflineList != null && !junkModelList.isEmpty()
                && getVideoCardState() != JunkDataManager.VIDEO_ALL_CLEANED) {
            List<PackageInfo> packageInfos = PackageManagerWrapper.getInstance().getPkgInfoList();
            if (packageInfos != null) {
                int size = JunkOfflineService.mVideoOfflineScanPkg.length;
                for (PackageInfo pkg : packageInfos) {
                    String pkgName = pkg.packageName;
                    for (int i = 0; i < size; i++) {
                        if (i == 1 || i == 2) {//tudou/youku 只要有优酷土豆，都显示卡片
                            if (pkgName.contains(JunkOfflineService.mVideoOfflineScanPkg[i])) {
                                needShowVideoCardForRoot = true;
                                break;
                            }
                        } else if (checkRoot) {//有root权限 才显示卡片
                            if (pkgName.contains(JunkOfflineService.mVideoOfflineScanPkg[i])) {
                                needShowVideoCardForRoot = true;
                                break;
                            }
                        } else if(!checkRoot && i > 2)
                            break;
                    }
                }
            }
        }

        if(mVideoOfflineList != null && mVideoOfflineList.size() >0 && !junkModelList.isEmpty()
                && needShowVideoCardForRoot && needShowVideoCard
                && getVideoCardState() != JunkDataManager.VIDEO_ALL_CLEANED){//加云端开关
            JunkModel videoModel = JunkModel.createCategoryModel(JunkModel.CATEGORY_TYPE_VIDEO_OFF, 0);
            junkModelList.add(0, videoModel);
        }

        if(!junkModelList.isEmpty() && !bForAdv){
            JunkModel alterSystemModel = JunkModel.createCategoryModel(JunkModel.CATEGORY_TYPE_OTHER, 0);
            junkModelList.add(alterSystemModel);

            if (null != thumbNailModel) {
                junkModelList.add(thumbNailModel);
            }

            JunkModel model = new JunkModel();
            model.setType(JunkModel.TYPE_ADVANCED_JUNK);
            model.setCategoryType(JunkModel.CATEGORY_TYPE_OTHER);
            junkModelList.add(model);
        }

        return junkModelList;
    }

    public int getVideoCardState(){
        return mJunkEngine.getVideoCardState();
    }

    public void setForceTopType(int notifyType){
        mForceTopType = notifyType;
    }

    private void getProcessCleanList(List<JunkModel> srcList, ArrayList<ProcessModel> dstList) {
        if (null == srcList || srcList.isEmpty() ||
                null == dstList) {
            return;
        }

        boolean bDeleteOneItem = (srcList.size() == 1);

        Iterator<JunkModel> iter = srcList.iterator();
        while (iter.hasNext()) {
            JunkModel tmpModel = iter.next();
            if (JunkModel.TYPE_PROCESS == tmpModel.getType()) {
                ProcessModel tmpProcess = tmpModel.getProcessModel();
                if (null != tmpProcess) {
                    if (bDeleteOneItem || tmpModel.isProcessChecked()) {
                        dstList.add(tmpProcess);
                    }
                    iter.remove();
                }
            }
        }
        if(!dstList.isEmpty()){
            ProcessHelper.postCleanHandler(null);
        }

    }

    /**
     * 将JunkModel类型集合转换成清理需要的BaseJunkBean字典
     * @param junkInfoBaseMap 输出的map
     * @param srcList 被转换集合
     * @return
     */
    public static Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> convertJunkModelToJunkInfoBaseMap(
            Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> junkInfoBaseMap, List<JunkModel> srcList) {

        RuntimeCheck.CheckMainUIThread();

        if (null == srcList || srcList.isEmpty()) {
            return junkInfoBaseMap;
        }

        if (null == junkInfoBaseMap) {
            junkInfoBaseMap = new HashMap<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>>();
        }

        for (JunkModel tmpModel : srcList) {
            junkInfoBaseMap = convertJunkModelToJunkInfoBaseMap(tmpModel, junkInfoBaseMap);
        }

        return junkInfoBaseMap;
    }

    public static Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> convertJunkModelToJunkInfoBaseMap(JunkModel tmpModel,
                                                                                                           Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> junkInfoBaseMap) {
        if (null == tmpModel) {
            return junkInfoBaseMap;
        }

        if (null == junkInfoBaseMap) {
            junkInfoBaseMap = new HashMap<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>>();
        }

        if (JunkModel.TYPE_SYSTEM_CACHE == tmpModel.getType()) {
            List<CacheInfo> cacheList = tmpModel.getChildList();

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE, tmpList);
            }
            tmpList.addAll(cacheList);
        } else if (JunkModel.TYPE_APP_CACHE == tmpModel.getType()) {
            List<CacheInfo> cacheList = tmpModel.getChildList();

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE, tmpList);
            }
            tmpList.addAll(cacheList);

        } else if (JunkModel.TYPE_APK_FILE == tmpModel.getType()) {
            APKModel tmpAPK = tmpModel.getApkModel();
            //junkInfoBaseList.add(tmpAPK);

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.APKFILE);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.APKFILE, tmpList);
            }
            tmpList.add(tmpAPK);

        } else if (JunkModel.TYPE_AD_FILE == tmpModel.getType()) {
            SDcardRubbishResult tmpRubbish = tmpModel.getSdcardRubbishResult();
            //junkInfoBaseList.add(tmpRubbish);

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.ADVERTISEMENT);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.ADVERTISEMENT, tmpList);
            }
            tmpList.add(tmpRubbish);

        } else if (JunkModel.TYPE_TEMP_FILE == tmpModel.getType()) {
            SDcardRubbishResult tmpRubbish = tmpModel.getSdcardRubbishResult();
            //junkInfoBaseList.add(tmpRubbish);

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER, tmpList);
            }
            tmpList.add(tmpRubbish);
        } else if (JunkModel.TYPE_APP_LEFT == tmpModel.getType()) {
            SDcardRubbishResult tmpRubbish = tmpModel.getSdcardRubbishResult();
            //junkInfoBaseList.add(tmpRubbish);

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER, tmpList);
            }
            tmpList.add(tmpRubbish);
        } else if(JunkModel.TYPE_SYS_FIXED_CACHE == tmpModel.getType()){
            List<CacheInfo> cacheList = tmpModel.getChildList();

            List<BaseJunkBean> tmpList=null;
            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE);
            if (tmpList == null) {
                tmpList = new ArrayList<BaseJunkBean>();
                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE, tmpList);
            }
            tmpList.addAll(cacheList);
        }else if(JunkModel.TYPE_VIDEO_OFF == tmpModel.getType()){
//            VideoOfflineResult tmpVideo = tmpModel.getVideoOfflineResult();
//
//            List<BaseJunkBean> tmpList=null;
//            tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.VIDEO_OFF);
//            if (tmpList == null) {
//                tmpList = new ArrayList<BaseJunkBean>();
//                junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.VIDEO_OFF, tmpList);
//            }
//            tmpList.add(tmpVideo);
        } else if(JunkModel.TYPE_ROOT_CACHE == tmpModel.getType()){
            List<RootCacheInfo> rootCacheList = tmpModel.getRootChildList();
            if(rootCacheList != null){
                List<BaseJunkBean> tmpList=null;
                tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.ROOTCACHE);
                if (tmpList == null) {
                    tmpList = new ArrayList<BaseJunkBean>();
                    junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.ROOTCACHE, tmpList);
                }
                tmpList.addAll(rootCacheList);
            }else {
                List<CacheInfo> cacheList = tmpModel.getChildList();

                List<BaseJunkBean> tmpList=null;
                tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE);
                if (tmpList == null) {
                    tmpList = new ArrayList<BaseJunkBean>();
                    junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE, tmpList);
                }
                tmpList.addAll(cacheList);
            }
        } else if (JunkModel.TYPE_SCREEN_SHOTS_COMPRESS == tmpModel.getType()) {
//            MediaFileList mfl = tmpModel.getMediaFileList();
//            if (null != mfl) {
//                ArrayList<MediaFile> screenShotsCompressList = mfl.getList();
//                if (null != screenShotsCompressList) {
//                    List<BaseJunkBean> tmpList = null;
//                    tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS);
//                    if (tmpList == null) {
//                        tmpList = new ArrayList<BaseJunkBean>();
//                        junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS, tmpList);
//                    }
//                    tmpList.addAll(screenShotsCompressList);
//                }
//            }
        }

        return junkInfoBaseMap;
    }

    private void doCleanResultList() {
        if (null == mCleanMap) {
            return;
        }
        List<BaseJunkBean> tmpList=null;
        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.BIGFILE);
        if (null != tmpList && null != mRubbishFileListForBigFiles) {
            mRubbishFileListForBigFiles.removeAll(tmpList);
        }
        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.MYPHOTO);
        if (null != tmpList && null != mMyPhotoList) {
            mMyPhotoList.removeAll(tmpList);
        }
        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.MYAUDIO);
        if (null != tmpList && null != mMyAudioList) {
            mMyAudioList.removeAll(tmpList);
        }

        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE_ADV);
        if (null != tmpList && null != mAdvSDCacheList) {
            mAdvSDCacheList.removeAll(tmpList);
        }
        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV);
        if (null != tmpList && null != mAdvRubbishFileListForAppLeftovers) {
            mAdvRubbishFileListForAppLeftovers.removeAll(tmpList);
        }
        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV);
        if (null != tmpList && null != mAdvRubbishFileListForTempFiles) {
            mAdvRubbishFileListForTempFiles.removeAll(tmpList);
        }

        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);
        if (null != tmpList && null != mSysCacheList) {
            mSysCacheList.removeAll(tmpList);
        }

        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE);
        if (null != tmpList && null != mSysFixedFileList) {
            mSysFixedFileList.removeAll(tmpList);
        }

        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE);
        if (null != tmpList && null != mSDCacheList) {
            mSDCacheList.removeAll(tmpList);
        }

        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.ADVERTISEMENT);
        if (null != tmpList && null != mRubbishFileListForAdvFolders) {
            mRubbishFileListForAdvFolders.removeAll(tmpList);
        }

        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER);
        if (null != tmpList && null != mRubbishFileListForTempFiles) {
            mRubbishFileListForTempFiles.removeAll(tmpList);
        }

        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER);
        if (null != tmpList && null != mRubbishFileListForAppLeftovers) {
            mRubbishFileListForAppLeftovers.removeAll(tmpList);
        }

        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.USELESSTHUMBNAIL);
        if (null != tmpList && null != mUselessThumbnailList) {
            mUselessThumbnailList.removeAll(tmpList);
        }

        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.APKFILE);
        if (null != tmpList && null != mApkCleanItemInfos) {
            mApkCleanItemInfos.removeAll(tmpList);
        }
        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.ROOTCACHE);
        if (null != tmpList) {
            mRootCacheList.removeAll(tmpList);
        }
        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.VIDEO_OFF);
        if (null != tmpList && null != mVideoOfflineList) {
            mVideoOfflineList.removeAll(tmpList);
        }
        tmpList = mCleanMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS);
        if (null != tmpList && null != mScreenShotsCompressList) {
            mScreenShotsCompressList.removeAll(tmpList);
        }
    }

    private boolean initClean(boolean bDeleteOneItem) {
        CleanRequestCallback cleanCallBack = new CleanRequestCallback();
        Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> tmpCleanMap = null;

        if (mCleanType == JUNK_WRAPPER_SCAN_TYPE_ADV) {
            tmpCleanMap = convertJunkModelToJunkInfoBaseMapAdv(tmpCleanMap, mCleanItemList);
        } else {
            tmpCleanMap = convertJunkModelToJunkInfoBaseMap(tmpCleanMap, mCleanItemList);
        }

        if (null == tmpCleanMap || tmpCleanMap.isEmpty()) {
            return false;
        }

        mCleanMap = tmpCleanMap;

        mToBeCleanedSize = calcAllSize(tmpCleanMap);

        CleanRequest cleanRequest = new CleanRequestImpl(tmpCleanMap, cleanCallBack, bDeleteOneItem?CleanRequest.CLEAN_TYPE_DETAIL:CleanRequest.CLEAN_TYPE_ONETAP);
        return mJunkEngine.setCleanRequest(cleanRequest);
    }

    /**
     * 从引擎中移出该项，不去清理
     * @param item
     */
    public void removeDataItem(BaseJunkBean item){
        mJunkEngine.removeDataItem(item);
    }

    public void setEngineStatus(JunkEngine.EM_ENGINE_STATUS status){
        mJunkEngine.setmEngineStatus(status);
    }

    private long calcAllSize(Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> cleanMap) {
        if (null == cleanMap || cleanMap.isEmpty()) {
            return 0L;
        }

        long totalSize = 0L;
        for (Map.Entry<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> listEntry : cleanMap.entrySet()) {
            if (null == listEntry) {
                continue;
            }

            totalSize += calcJunkInfoBaseSize(listEntry.getValue());
        }

        return totalSize;
    }

    class CleanRequestCallback implements CleanRequest.ICleanCallback{

        @Override
        public void onCleanBegin(CleanRequest request) {

        }

        @Override
        public void onCleaningPath(String strItemName) {
            //回传路径
            if(mObserverArray != null){
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_CLEAN_STATUS_INFO, 0, 0, strItemName));
            }
        }

        @Override
        public void onCleanItem(BaseJunkBean obj) {

            if (null == mObserverArray || null == obj) {
                return;
            }

            long nSize = obj.getSize();
            mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_REMOVE_DATA_ITEM_SIZE, 0, 0, nSize));
            long totalCleanSize = mTotalCleanSize.addAndGet(nSize);
            if (mToBeCleanedSize <= 0L) {
                mToBeCleanedSize = totalCleanSize;
                if (0 == totalCleanSize) {
                    mToBeCleanedSize = 1L;
                }
            }
            mProgressPos.set((int)(totalCleanSize * 100L / mToBeCleanedSize));
            long temp = -1 * nSize;

            switch (obj.getJunkDataType()) {

                case SYSCACHE:
                    mSysCacheCleanSize.addAndGet(nSize);
                    if (obj.isCheck()) {
                        mSysCacheCheckedScanSize.addAndGet(temp);
                    }
                    mSysCacheScanSize.addAndGet(temp);
                    break;

                default:
                    break;
            }

            if (obj.isCheck()) {
                mTotalCheckedScanSize.addAndGet(temp);
            }
            mTotalScanSize.addAndGet(temp);
            if (obj instanceof VideoOfflineResult) {
                if (mTotalScanSize.get() < 0) {
                    mTotalScanSize.set(0);
                }
                if (mTotalCheckedScanSize.get() < 0) {
                    mTotalCheckedScanSize.set(0);
                }
            }
            mObserverHandler.sendMessage(mObserverHandler.obtainMessage(
                    JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO, 0, 0,
                    getObjPool().obtainObj().updateValues(
                            mEngineStatus, mProgressPos.get(),
                            mTotalScanSize.get(),
                            mTotalCheckedScanSize.get(),
                            totalCleanSize,
                            mProcessScanSize.get(),
                            mProcessCheckedScanSize.get(),
                            mProcessCleanSize.get(),
                            mSysCacheScanSize.get(),
                            mSysCacheCheckedScanSize.get(),
                            mSysCacheCleanSize.get())));

            mObserverHandler.sendMessage(mObserverHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_REMOVE_DATA_ITEM, 0, 0, obj));
        }

        @Override
        public void onSubCleanTaskFinish(int msg) {
            if (null != mObserverArray) {
                mObserverHandler.sendMessage(mObserverHandler.obtainMessage(msg));
            }
        }

        @Override
        public void onCleanItemSize(JunkRequest.EM_JUNK_DATA_TYPE type, long nSize) {

        }
    }

    public ArrayList<JunkEngine.JunkEventCommandInterface> getmObserverArray() {
        return mObserverArray;
    }

    private void convertRootCacheListToRootCacheMap() {
        mRootCacheInfoMap.clear();

        if (null == mRootCacheList) {
            return;
        }

        for (BaseJunkBean infoBase : mRootCacheList) {
            RootCacheInfo info = (RootCacheInfo)infoBase;
            if (mRootCacheInfoMap.containsKey(info.getPkgName())) {
                List<RootCacheInfo> listInfo = mRootCacheInfoMap.get(info.getPkgName());
                listInfo.add(info);
            } else {
                List<RootCacheInfo> listInfo = new ArrayList<RootCacheInfo>();
                listInfo.add(info);
                mRootCacheInfoMap.put(info.getPkgName(), listInfo);
            }
        }
    }

    private void convertSDCacheListToSDCacheMap() {
        mAllCacheInfoMap.clear();

        if (null == mSDCacheList) {
            return;
        }

        for (BaseJunkBean infoBase : mSDCacheList) {
            CacheInfo info = (CacheInfo)infoBase;
            if (mAllCacheInfoMap.containsKey(info.getPackageName())) {
                List<CacheInfo> listInfo = mAllCacheInfoMap.get(info.getPackageName());
                listInfo.add(info);
            } else {
                List<CacheInfo> listInfo = new ArrayList<CacheInfo>();
                listInfo.add(info);
                mAllCacheInfoMap.put(info.getPackageName(), listInfo);
            }
        }
    }

    public static Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> convertJunkModelToJunkInfoBaseMapAdv(
            Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> junkInfoBaseMap, List<JunkModel> srcList) {
        RuntimeCheck.CheckMainUIThread();

        if (null == srcList || srcList.isEmpty()) {
            return junkInfoBaseMap;
        }

        if (null == junkInfoBaseMap) {
            junkInfoBaseMap = new HashMap<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>>();
        }

        for (JunkModel tmpModel : srcList) {
            if (JunkModel.CATEGORY_TYPE_STANDARD == tmpModel.getCategoryType()) {
                junkInfoBaseMap = convertJunkModelToJunkInfoBaseMap(tmpModel, junkInfoBaseMap);
            } else if (JunkModel.TYPE_BIG_FILE == tmpModel.getType()) {
                List<BaseJunkBean> tmpList=null;
                tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.BIGFILE);
                if (tmpList == null) {
                    tmpList = new ArrayList<BaseJunkBean>();
                    junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.BIGFILE, tmpList);
                }
                if (null != tmpModel.getCacheInfo()) {
                    tmpList.add(tmpModel.getCacheInfo());
                } else if (null != tmpModel.getSdcardRubbishResult()) {
                    tmpList.add(tmpModel.getSdcardRubbishResult());
                }
            } else if (JunkModel.TYPE_PHOTO_GALLERY == tmpModel.getType() ||
                    JunkModel.TYPE_PHOTO_GALLERY_DETAIL == tmpModel.getType() || JunkModel.TYPE_SIMILAR_PHOTO == tmpModel.getType()) {
                List<BaseJunkBean> tmpList=null;
                tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.MYPHOTO);
                if (tmpList == null) {
                    tmpList = new ArrayList<BaseJunkBean>();
                    junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.MYPHOTO, tmpList);
                }
                ArrayList<MediaFile> delList = tmpModel.getMediaFileList().getCurDeleteList();
                if (delList != null) {
                    tmpList.addAll(delList);
                }
            } else if (JunkModel.TYPE_AUDIO_MANAGER == tmpModel.getType()) {
                List<BaseJunkBean> tmpList=null;
                tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.MYAUDIO);
                if (tmpList == null) {
                    tmpList = new ArrayList<BaseJunkBean>();
                    junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.MYAUDIO, tmpList);
                }
                tmpList.addAll(tmpModel.getMediaFileList().getCurDeleteList());
            } else if (JunkModel.TYPE_APP_CACHE == tmpModel.getType()) {
                List<CacheInfo> cacheList = tmpModel.getChildList();

                List<BaseJunkBean> tmpList=null;
                tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE_ADV);
                if (tmpList == null) {
                    tmpList = new ArrayList<BaseJunkBean>();
                    junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE_ADV, tmpList);
                }
                tmpList.addAll(cacheList);
            } else if (JunkModel.TYPE_APP_LEFT == tmpModel.getType()) {
                SDcardRubbishResult tmpRubbish = tmpModel.getSdcardRubbishResult();

                List<BaseJunkBean> tmpList=null;
                tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV);
                if (tmpList == null) {
                    tmpList = new ArrayList<BaseJunkBean>();
                    junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV, tmpList);
                }
                tmpList.add(tmpRubbish);
            } else if (JunkModel.TYPE_TEMP_FILE == tmpModel.getType()) {
                SDcardRubbishResult tmpRubbish = tmpModel.getSdcardRubbishResult();

                List<BaseJunkBean> tmpList=null;
                tmpList = junkInfoBaseMap.get(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV);
                if (tmpList == null) {
                    tmpList = new ArrayList<BaseJunkBean>();
                    junkInfoBaseMap.put(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV, tmpList);
                }
                tmpList.add(tmpRubbish);
            }
        }

        return junkInfoBaseMap;
    }

    private void setDataManagerCacheDisable(boolean value){
        if(mJunkEngine!=null){
            mJunkEngine.setDataManagerCacheDisable(value);
        }
    }

    public long getTotalScanTime(){
        if(mJunkEngine!=null){
            return mJunkEngine.getTotalScanTime();
        }
        return 0;
    }

    public long getTotalSize(){
        if(mJunkEngine!=null){
            return mJunkEngine.getTotalScanTime();
        }
        return 0;
    }

}

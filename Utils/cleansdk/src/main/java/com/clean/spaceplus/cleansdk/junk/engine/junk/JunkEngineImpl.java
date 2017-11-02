package com.clean.spaceplus.cleansdk.junk.engine.junk;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.BuildConfig;
import com.clean.spaceplus.cleansdk.R;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.clean.CleanRequest;
import com.clean.spaceplus.cleansdk.base.exception.SpacePlusFailedException;
import com.clean.spaceplus.cleansdk.base.scan.ExtraAndroidFileScanner;
import com.clean.spaceplus.cleansdk.base.scan.ScanCommonStatus;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskCallback;
import com.clean.spaceplus.cleansdk.base.scan.TaskExecutors;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Analytics;
import com.clean.spaceplus.cleansdk.base.utils.root.SuExec;
import com.clean.spaceplus.cleansdk.base.utils.system.PackageManagerWrapper;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudManager;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudResultReporter;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CloudCfgDataWrapper;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.CloudCfgKey;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.ServiceConfigManager;
import com.clean.spaceplus.cleansdk.junk.engine.EmergencyFalseSignFilter;
import com.clean.spaceplus.cleansdk.junk.engine.FileDeletedRecorder;
import com.clean.spaceplus.cleansdk.junk.engine.ProgressControl;
import com.clean.spaceplus.cleansdk.junk.engine.SpaceTaskTime;
import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkCleanItemInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFile;
import com.clean.spaceplus.cleansdk.junk.engine.bean.RootCacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.SDcardRubbishResult;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest.EM_JUNK_DATA_TYPE;
import com.clean.spaceplus.cleansdk.junk.engine.task.AdvFolderScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.ApkScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.BigFileScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.RootCacheScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.RubbishFileScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.SdCardCacheScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.SdPathCleanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.SdPathCleanTask.DelPathInfo;
import com.clean.spaceplus.cleansdk.junk.engine.task.SysCacheCleanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.SysCacheScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.SysFixedFileScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.ThumbnailScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.util.RubbishUtil;
import com.clean.spaceplus.cleansdk.junk.executor.SyncExecutors;
import com.clean.spaceplus.cleansdk.junk.executor.ThreadPoolExecutors;
import com.clean.spaceplus.cleansdk.util.EnableCacheListDir;
import com.clean.spaceplus.cleansdk.util.Env;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.clean.spaceplus.cleansdk.util.PackageUtils;
import com.clean.spaceplus.cleansdk.util.ResUtil;
import com.clean.spaceplus.cleansdk.util.SDCardUtil;
import com.clean.spaceplus.cleansdk.util.TimingUtil;
import com.clean.spaceplus.cleansdk.util.md5.MD5PathConvert;
import com.hawkclean.framework.log.NLog;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import space.network.cleancloud.MultiTaskTimeCalculator;

/**
 * @author liangni
 * @Description:垃圾扫描引擎接口实现类
 * @date 2016/4/22 10:28
 * @copyright TCL-MIG
 */
public class JunkEngineImpl implements JunkEngine {
    public final static String TAG = JunkEngineImpl.class.getSimpleName();
    private ArrayList<String> mRPList = new ArrayList<>();
    private EM_ENGINE_STATUS mEngineStatus = EM_ENGINE_STATUS.IDLE;
    private boolean isAdvancedClean = false;//是否是深度扫描
    //深度
    private final static String CLEAN_ADVANCED = "Clean_adv";
    //标准
    private final static String CLEAN_STANDARD = "Clean_std";
    //当前正在运行进程列表
    private ArrayList<String> mRunProcessList = new ArrayList<String>();
    public void setIsAdvancedClean(boolean bVal) {
        isAdvancedClean = bVal;
    }

    //是否是深度清理
    private boolean isAdvancedClean() {
        return isAdvancedClean;
    }

    //定义 清理传递数据类型
    private CleanRequest mCleanRequest;
    private boolean mIsCleanVideo = false;

    private EngineCallback mCB = null;
    private EngineConfig mCfg = null;
    private String sdDir;
    private int sdDirlength;
    private boolean isSystemInsufficient = false;
    private Map<String, Boolean> mVisibleFolderMap = new HashMap<String, Boolean>();
    private ArrayList<String> mRubFolderWhiteList = new ArrayList<String> ();
    private ArrayList<String> mRubFileWhiteList = new ArrayList<String> ();
    private int mProgBarTotal = 0;// 进度条总量
    private boolean mbDetailClean = false;
    private boolean mCompletedScan = true;
    private Context mCtxContext = null;
    private boolean mbCheckRoot = false;

    private int m_nCleanSize = 0;
    public static final long DATA_CACHE_VALID_TIME = 5L * 60L * 1000L;
    private Handler mMsgHandler = null;
    private boolean mbIsMsgThreadQuit = false;
    private List<String> mMSImageMediaIdList = null;
    private List<String> mMSImageThumbIdList = null;
    private StringBuffer mImgBuffer = new StringBuffer();
    private String mWhere = MediaStore.Images.Media.DATA + " = ?";
    private ArrayList<String> mImgs = new ArrayList<String>();
    private ArrayList<String> mVideoS = new ArrayList<String>();
    private StringBuffer mVideoBuffer = new StringBuffer();

    private String mSYSDelOpLog = "";
    private ArrayList<String> mSDCDelOpLog = new ArrayList<> ();
    private ArrayList<String> mRFDelOpLog = new ArrayList<> ();
    private ArrayList<String> mAPKDelOpLog = new ArrayList<> ();
    private long scanTime;

    public boolean setCleanRequest(CleanRequest cleanRequest) {
        if (getEngineStatus() != EM_ENGINE_STATUS.IDLE) {
            return false;
        }
        mCleanRequest = cleanRequest;
        return true;
    }


    public JunkEngineImpl() {
        super();
        initBackgroundThread();
    }

    public boolean getStopFlag() {
        return mStopFlag;
    }

    public void setAllJunkCleanSize(int nSize) {
        m_nCleanSize = nSize;
    }

    ProgressControl mSysScanTaskPC = new ProgressControl(new ProgressControlCallbackAgent(), 0);
    //初始化相关hander
    public void initBackgroundThread() {
        if (null != mJunkThread) {
            return;
        }

        synchronized (mMutexForBGThread) {
            if (null != mJunkThread) {
                return;
            }
            mJunkThread = new HandlerThread("JunkEngine_MSG");
            mJunkThread.start();
            mMsgHandler = new Handler(mJunkThread.getLooper()) {
                @Override
                public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
                    synchronized (mMutexForBGThread) {
                        if (mbIsMsgThreadQuit) {
                            return false;
                        }
                        return super.sendMessageAtTime(msg, uptimeMillis);
                    }
                }

                @Override
                public void handleMessage(Message msg) {
                    SdPathCleanTask.DelPathResult msgObj;
                    JunkCleanItemInfo info;
                    BaseJunkBean item;
                    CacheInfo cacheItem;
                    RootCacheInfo rootCacheItem;
                    SDcardRubbishResult rubItem;
                    APKModel apkModel;
                    MediaFile mediaFile;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    switch (msg.what) {

                        case JunkEngineMsg.MSG_HANDLER_FOUND_SIZE_ITEM:
                            mDataMgr.onFoundBigFileSize(msg.arg1, (BaseJunkBean) msg.obj);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FOUND_BIGFILE_ITEM:
                            if (!mDataMgr.onFoundExtendBigfileItem(msg.arg1, (BaseJunkBean) msg.obj))
                                return;
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FOUND_APK_ITEM:

                            if(mStopFlag){
                                return;
                            }
                            handlerAPKItem((APKModel)msg.obj);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_CHANGE_APK_ITEM:
                            if(mStopFlag){
                                return;
                            }
                            mDataMgr.onChangeItem((APKModel)msg.obj);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_SYS_CACHE_SCAN_STATUS:
                            mDataMgr.notifyCurrentScanItem(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE, (String) msg.obj);
                            break;

                        case JunkEngineMsg.MSG_HANDLER_SYS_CACHE_SCAN_PROGRESS_STEP:
                            mSysScanTaskPC.addStep();
                            break;

                        case JunkEngineMsg.MSG_HANDLER_SYS_CACHE_SCAN_PROGRESS_START:
                            mSysScanTaskPC.startControl(mProgBarTotal, PROG_BAR_SYS_CACHE, true);
                            mSysScanTaskPC.setStepNum(msg.arg1);
                            break;
                        //系统缓存扫描结束
                        case JunkEngineMsg.MSG_HANDLER_FINISH_SYS_SCAN:
                            mSysScanTaskPC.stopControl();
                            mDataMgr.onFinishSysCacheScan();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;

                        case JunkEngineMsg.MSG_HANDLER_FINISH_SYS_FIXED_SCAN:
                            mDataMgr.onFinishSysFixedCacheScan();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;

                        case JunkEngineMsg.MSG_HANDLER_ROOT_CACHE_SCAN_FINISH:
                            mDataMgr.onFinishRootCacheScan();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;

                        case JunkEngineMsg.MSG_HANDLER_FINISH_SD_SCAN:
                            mDataMgr.onFinishSdCacheScan();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FINISH_APK_SCAN:
                            mDataMgr.onFinishAPKScan();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FINISH_THUMBNAIL_SCAN:
                            mDataMgr.onFinishThumbnailScan();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FINISH_RUBBISH_SCAN:
                            mDataMgr.onFinishRubbishScan();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FINISH_TMP_FILES_SCAN:
                            mDataMgr.onFinishTmpFilesScan();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FINISH_LOG_FILES_SCAN:
                            mDataMgr.onFinishLogFilesScan();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FOUND_CACHE_ITEM:
                            if (mStopFlag) {
                                return;
                            }

                            NLog.i(TAG," cache item onFoundItem %s, size %s",((CacheInfo) msg.obj).getFilePath(), ((CacheInfo) msg.obj).getSize());
                            mDataMgr.onFoundItem((CacheInfo) msg.obj);
                            break;

                        case JunkEngineMsg.MSG_HANDLER_ROOT_CACHE_SCAN_FOUND_ITEM:
                            if(mStopFlag){
                                return;
                            }
                            mDataMgr.onFoundItem((RootCacheInfo)msg.obj);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FINISH_PHOTO_SCAN:
                            mDataMgr.onFinishPhotoScan();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FINISH_AUDIO_SCAN:
                            mDataMgr.onFinishAudioScan();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FINISH_CALC_FOLDER_SIZE:
                            mDataMgr.onFinishCalcFolderSize();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FINISH_BIG_FILE_SCAN:
                            mDataMgr.onFinishBigFileScan();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FOUND_RUBBISH_ITEM:
                            if(mStopFlag){
                                return;
                            }
                            mDataMgr.onFoundItem(msg.arg1, (SDcardRubbishResult)msg.obj);
                            break;
                         case JunkEngineMsg.MSG_HANDLER_UPDATE_RUBBISH_ITEM:
                            if(mStopFlag){
                                return;
                            }
                            mDataMgr.onUpdateItem(msg.arg1, (RubbishFileScanTask.UpdateChildrenData)msg.obj);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FINISH_SCAN:
                            //所有任务都扫描结束
                            String strTag = "Scan_std";
                            if (null != mCB) {
                                mCB.onSuccess();
                            }
                            //结束引擎统计
                            endAnalytics();
                            MD5PathConvert.getInstance().clearSubDirMap();
                            EnableCacheListDir.closeCache();
                            mEngineStatus = EM_ENGINE_STATUS.IDLE;
                            break;
                        case JunkEngineMsg.MSG_HANDLER_RST_CLEAN_LOG:
                            mSYSDelOpLog = "";
                            mSDCDelOpLog.clear();
                            mRFDelOpLog.clear();
                            mAPKDelOpLog.clear();
                            break;
                        case JunkEngineMsg.MSG_HANDLER_SYS_CLEAN_ITEM:

                            cacheItem = (CacheInfo) msg.obj;
                            removeDataItemCB(cacheItem);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_SYS_CLEAN_INFO:
                            cacheItem = (CacheInfo) msg.obj;
                            if (null != cacheItem) {
                                SysCacheScanTask.SysCacheOnCardInfo sysCacheInfo = cacheItem.getSysCacheOnCardInfo();
                                if (null != sysCacheInfo)  {
                                    StringBuilder  SYSDelopLogSB = new StringBuilder(String.valueOf(SystemClock.uptimeMillis())).append(" : ")
                                            .append(sysCacheInfo.nTotalSize).append(":")
                                            .append(sysCacheInfo.strPackageName);

                                    NLog.d(TAG, SYSDelopLogSB.toString());
                                }
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_SYS_CLEAN_FINISH:
                            mIsSysCacheCleaned  = (Boolean) msg.obj;
                            removeSubTaskItem(msg.what);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_SYS_FIXED_CLEAN_ITEM:
                            msgObj = ((SdPathCleanTask.DelPathResult) msg.obj);
                            info = (JunkCleanItemInfo)((DelPathInfo) msg.obj).mAttachInfo;
                            item = info.getJunkItem();
                            cacheItem = (CacheInfo)item;
                            removeDataItemCB(cacheItem);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_SYS_FIXED_CLEAN_FINISH:
                            removeSubTaskItem(msg.what);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_ROOT_CACHE_CLEAN_ITEM://root清理
                            info = (JunkCleanItemInfo)((SdPathCleanTask.DelPathInfo) msg.obj).mAttachInfo;
                            item = info.getJunkItem();
                            rootCacheItem = (RootCacheInfo)item;
                            removeDataItemCB(rootCacheItem);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_ROOT_CACHE_CLEAN_FINISH:
                            removeSubTaskItem(msg.what);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_SD_CLEAN_ITEM:
                            msgObj = ((SdPathCleanTask.DelPathResult) msg.obj);
                            info = (JunkCleanItemInfo)((SdPathCleanTask.DelPathInfo) msg.obj).mAttachInfo;
                            item = info.getJunkItem();
                            cacheItem = (CacheInfo)item;

                            if (null != item && !info.getIsSubItem()) {
                                removeDataItemCB(cacheItem);
                            }
                            String path = "";
                            if(msgObj!=null&&msgObj.mPathList!=null&&msgObj.mPathList.size()>0){
                                path = msgObj.mPathList.toString();
                            }
                            NLog.i(TAG, "sd--clean--item--path:"+path);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_SD_CLEAN_STATUS:
                            removeStatusCB(msg.obj);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_SD_CLEAN_INFO:
                            msgObj = ((SdPathCleanTask.DelPathResult) msg.obj);
                            info = (JunkCleanItemInfo)((SdPathCleanTask.DelPathInfo) msg.obj).mAttachInfo;
                            item = info.getJunkItem();
                            cacheItem = (CacheInfo)item;
                            if (item != null) {
                                StringBuilder DSDCopLogSB = new StringBuilder()
                                        .append(format.format(new Date(System.currentTimeMillis())))
                                        .append("-->").append("sdcache")
                                        .append("-->").append(msgObj.mFileDeletedList)
                                        .append("-->").append("app:").append(msgObj.mImageCount)
                                        .append(":").append(msgObj.mAudioCount)
                                        .append(":").append(msgObj.mVideoCount)
                                        .append(":").append(msgObj.mFileDeletedSize)
                                        .append(":").append(cacheItem.getCacheTableType())
                                        .append(":").append(cacheItem.getCacheId())
                                        .append(":").append(msgObj.mActualScanList);

                                NLog.i(TAG, DSDCopLogSB.toString());
                                mSDCDelOpLog.add(DSDCopLogSB.toString());
                                //保存清理记录
                                NLog.i(TAG, "clean info name:"+cacheItem.getRealAppName()+", pkg:"+cacheItem.getPackageName());
                                CleanCloudResultReporter.saveAppCleanRecord(cacheItem.getRealAppName(), cacheItem.getPackageName(), msgObj.mSize);
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_SD_CLEAN_FINISH:
                            removeSubTaskItem(msg.what);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_RUB_CLEAN_ITEM://rubbish 开始清理
                            NLog.d(TAG, "msg rub clean item");
                            msgObj = ((SdPathCleanTask.DelPathResult) msg.obj);
                            info = (JunkCleanItemInfo)((SdPathCleanTask.DelPathInfo) msg.obj).mAttachInfo;
                            item = info.getJunkItem();
                            rubItem = (SDcardRubbishResult)item;

                            if (null != item && !info.getIsSubItem()) {
                                removeDataItemCB(rubItem);
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_RUB_CLEAN_STATUS:
                            removeStatusCB(msg.obj);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_RUB_CLEAN_INFO://rubbish 清理完成
                            msgObj = ((SdPathCleanTask.DelPathResult) msg.obj);
                            info = (JunkCleanItemInfo)((SdPathCleanTask.DelPathInfo) msg.obj).mAttachInfo;
                            item = info.getJunkItem();
                            rubItem = (SDcardRubbishResult)item;

                            if (null != item) {
                                StringBuilder DRopLogSB;
                                if (ResUtil.getString(R.string.junk_tag_RF_EmptyFolders).equals(rubItem.getChineseName())) {
                                    // 空文件夹
                                    DRopLogSB = new StringBuilder()
                                            .append(format.format(new Date(System.currentTimeMillis())))
                                            .append("-->").append("rub-").append(item.getJunkDataType())
                                            .append("-").append(rubItem.getChineseName())
                                            .append("-->").append(rubItem.getPathList());
                                } else {
                                    DRopLogSB = new StringBuilder()
                                            .append(format.format(new Date(System.currentTimeMillis())))
                                            .append("-->").append("rub-").append(item.getJunkDataType())
                                            .append("-").append(rubItem.getChineseName())
                                            .append("-->").append(msgObj.mFileDeletedList)
                                            .append("-->").append("rub:").append(msgObj.mImageCount)
                                            .append(":").append(msgObj.mAudioCount)
                                            .append(":").append(msgObj.mVideoCount)
                                            .append(":").append(msgObj.mFileDeletedSize)
                                            .append(":").append(rubItem.getSignId())
                                            .append(":").append(msgObj.mActualScanList);
                                }
                                mRFDelOpLog.add(DRopLogSB.toString());
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_RUB_CLEAN_FINISH://rubbish clean finish
                            removeSubTaskItem(msg.what);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_APK_CLEAN_ITEM://apk清理开始
                            apkModel = (APKModel)((SdPathCleanTask.DelPathInfo) msg.obj).mAttachInfo;

                            if(apkModel != null){
                                removeDataItemCB(apkModel);
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_APK_CLEAN_STATUS:
                            removeStatusCB(msg.obj);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_APK_CLEAN_INFO://apk清理完成
                            msgObj = (SdPathCleanTask.DelPathResult) msg.obj;
                            apkModel = (APKModel) msgObj.mAttachInfo;

                            if(apkModel != null){
                                StringBuilder APKopLogSB = new StringBuilder()
                                        .append(format.format(new Date(System.currentTimeMillis())))
                                        .append("-->").append("apk")
                                        .append("-->").append(msgObj.mFileDeletedList);
                                mAPKDelOpLog.add(APKopLogSB.toString());
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_APK_CLEAN_FINISH://apk clean finish
                            removeSubTaskItem(msg.what);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FOUND_AUDIO_ITEM:
                        case JunkEngineMsg.MSG_HANDLER_FOUND_PHOTO_ITEM:
                        case JunkEngineMsg.MSG_HANDLER_FOUND_SCREENSHOTSCOMPRESS_ITEM:
                            if(mStopFlag){
                                return;
                            }
                            mDataMgr.onFoundItem((MediaFile)(msg.obj));
                            break;
                        case JunkEngineMsg.MSG_HANDLER_MEDIA_CLEAN_ITEM:
                            mediaFile = (MediaFile)((DelPathInfo) msg.obj).mAttachInfo;

                            if(mediaFile != null){
                                removeDataItemCB(mediaFile);
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_MEDIA_CLEAN_STATUS:
                            removeStatusCB(msg.obj);
                            break;
                        case JunkEngineMsg.MSG_HANDLER_MEDIA_CLEAN_INFO:
                            mediaFile = (MediaFile)((DelPathInfo) msg.obj).mAttachInfo;

                            if(mediaFile != null) {
                                StringBuilder  MFopLogSB = new StringBuilder(String.valueOf(SystemClock.uptimeMillis())).append(" : ")
                                        .append(mediaFile.getSize()).append(":")
                                        .append(mediaFile.getPath()).append(":");
                                NLog.d(TAG, "media--clean--info: "+MFopLogSB.toString());
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FINISH_CLEAN:
                            mCleanRequest = null;
                            if (mIsCleanVideo) {
                                mDataMgr.isVideoCardCleaned();
                                mIsCleanVideo = false;
                            }

                            if (null != mCB) {
                                mCB.onSuccess();
                            }
                            new FileDeletedRecorder().record(getDelLog());
                            mEngineStatus = EM_ENGINE_STATUS.IDLE;
                            //TODO : try to find a better time, maybe after UI
                            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_CLEAN_REPORT, 0, 0));

                            ///> 清理完成后进行照片回收
                            //new JunkFileRecycle().StartRecycleAfterClean();
                            //recyclePicsFrom2SdCard();
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FINISH_CLEAN_REPORT:
                            mSYSDelOpLog = "";
                            mSDCDelOpLog.clear();
                            mAPKDelOpLog.clear();
                            mRFDelOpLog.clear();
                        case JunkEngineMsg.MSG_HANDLER_REMOVE_DATA_ITEM:
                            if (null != msg.obj && null != mDataMgr) {
                                if (msg.obj instanceof BaseJunkBean) {
                                    mDataMgr.removeDataItem((BaseJunkBean)msg.obj, CleanRequest.CLEAN_TYPE_NONE);
                                }
                            }
                            break;
//
                        case JunkEngineMsg.MSG_HANDLER_UPDATE_DATA_ITEM:
                            if (null != msg.obj && null != mDataMgr) {
                                if (msg.obj instanceof BaseJunkBean) {
                                    mDataMgr.updateDataItem((BaseJunkBean)msg.obj);
                                }
                            }
                            break;

                        case JunkEngineMsg.MSG_HANDLER_FINISH_SCREENSHOTSCOMPRESSSCAN:
                            mDataMgr.onFinishScrShotsCompressScan();
                            if (mDataMgr.isFinishScan() && !mfinishScanMsgSended) {
                                sendScanFinishMsg();
                            }
                            break;
                        case JunkEngineMsg.MSG_HANDLER_FINISH_COMPRESS_SCRSHOTS:
                            if (null != msg.obj && msg.obj instanceof ArrayList<?>) {
                                ArrayList<MediaFile> dataList = (ArrayList<MediaFile>)msg.obj;

                                if (dataList != null) {
                                    for (MediaFile scrShotFile : dataList) {
                                        removeDataItemCB(scrShotFile);
                                    }
                                }
                            }
                            break;

                    }
                }
            };
            mbIsMsgThreadQuit = false;
        }
    }

    /**
     * @return 返回删除文件记录
     */
    private String getDelLog() {
        NLog.d(TAG, "getDelLog");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.append(mSYSDelOpLog);
        if (!TextUtils.isEmpty(mSYSDelOpLog)) {
            printWriter.append("\n");
        }
        for (String temp : mSDCDelOpLog) {
            printWriter.append(temp);
            printWriter.append("\n");
        }
        for (String temp : mRFDelOpLog) {
            printWriter.append(temp);
            printWriter.append("\n");
        }
        for (String temp : mAPKDelOpLog) {
            printWriter.append(temp);
            printWriter.append("\n");
        }
        return stringWriter.toString();
    }

    //发送扫描结束指令
    private void sendScanFinishMsg() {
        if (!mfinishScanMsgSended) {
            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SCAN, 0, 0));
            mfinishScanMsgSended = true;
        }
    }

    private void handlerAPKItem(APKModel info){
        try{
            if(sdDir==null|| sdDir.equals("")){
                sdDir =  FileUtils.addSlash(Environment.getExternalStorageDirectory().toString());//获取跟目录
                sdDirlength=sdDir.length();
            }

            if(info.getPath()!=null && info.getPath().length()>sdDirlength && sdDirlength!=0){
                String apkPath=info.getPath().substring(sdDirlength-1);
                int index = apkPath.lastIndexOf('/');
                if(index>0){
                    apkPath=apkPath.substring(0, index);
                    mDataMgr.getApkPathList().add(apkPath);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        mDataMgr.onFoundItem(info);
    }

    public static class ScanCommonStatusImpl implements ScanCommonStatus {
        private CommonStatus mStatus;
        private MultiTaskTimeCalculator mNetQueryTimeController = null;
        private boolean mIsFirstCleanedJunkStandard = false;
        private boolean mIsFirstCleanedJunkAdvanced = false;

        public ScanCommonStatusImpl(CommonStatus status) {
            mStatus = status;
        }

        @Override
        public boolean getIsForegroundScan() {
            return mStatus != null ? mStatus.getIsForegroundScan() : false;
        }

        @Override
        public MultiTaskTimeCalculator getNetQueryTimeController() {
            return mNetQueryTimeController;
        }

        @Override
        public boolean getIsFirstCleanedJunkStandard() {
            return mIsFirstCleanedJunkStandard;
        }

        @Override
        public boolean getIsFirstCleanedJunkAdvanced() {
            return mIsFirstCleanedJunkAdvanced;
        }

        public void setNetQueryTimeController(MultiTaskTimeCalculator netQueryTimeController) {
            mNetQueryTimeController = netQueryTimeController;
        }

        public void setIsFirstCleanedJunkStandard(boolean value) {
            mIsFirstCleanedJunkStandard = value;
        }

        public void setIsFirstCleanedJunkAdvanced(boolean value) {
            mIsFirstCleanedJunkAdvanced = value;
        }

    }

    private boolean isHaveSdCard = true;
    private Context mContext;
    private ContentResolver mCR = null;
    private PackageManager mPM = null;
    private List<PackageInfo> mPkgList = null;
    private boolean mStopFlag = false;
    private boolean mbIsAdvancedClean = false;
    //清理executors
    private TaskExecutors mTaskExecutorsForClean = null;
    //扫描executors
    private TaskExecutors mTaskExecutors = null;

    private boolean mfinishScanMsgSended = false;
    private JunkDataManager mDataMgr = new JunkDataManager();
    private static final int SCAN_TIME_OUT_MAX = Integer.MAX_VALUE;
    private CommonStatus mCommonStatus = new CommonStatus();
    private HandlerThread mJunkThread = null;

    private BlockingQueue<RootCacheInfo> rootCacheInfoQueue = new LinkedBlockingQueue<RootCacheInfo>();

    private Object mMutexForBGThread = new Object();
    private ArrayList<String> mEmptyFolderList = new ArrayList<String>();
    private ArrayList<String> mFolderWhiteList = new ArrayList<String>();
    private boolean mIsSysCacheCleaned = false;
    private ArrayList<String> mFileWhiteList = new ArrayList<String>();
    private long mAvailMemCB = 0;
    private long mCleanTimeoutCB = 0;
    private ArrayList<String> mOnCleanFeedbackListFolder = new ArrayList<String>();
    private ArrayList<String> mOnCleanFeedbackListFile = new ArrayList<String>();
    private Map<EM_JUNK_DATA_TYPE, JunkDataManager.JunkRequestData> mAvailableDataMap =
            new ConcurrentHashMap<EM_JUNK_DATA_TYPE, JunkDataManager.JunkRequestData>();
    private boolean mbFirstUse = false;
    private ScanTask rootCacheScanTask = null;
    private static final int PROG_BAR_PROGRESS = 600;
    private static final int PROG_BAR_SYS_CACHE = 1100;
    private static final int PROG_BAR_SD_CACHE = 3600;
    private static final int PROG_BAR_APP_LEFTOVER = 2600;
    private static final int PROG_BAR_TEMPFILE = 1900;
    private static final int PROG_BAR_ADV_FOLDER = 800;
    private static final int PROG_BAR_APK_FILE = 2000;
    private static final int PROG_BAR_TEMP_FILES = Build.VERSION.SDK_INT >= 11 ? 200 : 0;
    private static final int PROG_BAR_LOG_FILES = Build.VERSION.SDK_INT >= 11 ? 200 : 0;
    private static final int PROG_BAR_THUMBNAIL = 800;    // TODO
    private static final int PROG_BAR_BIG_FILE = Build.VERSION.SDK_INT >= 11 ? 200 : 2500;
    private static final int PROG_BAR_TEMPFOLDER = 1900;

    private static final int PROG_BAR_VIDEO_OFFLINE = 1000;

    private static final int PROG_BAR_PHOTO_IMAGE = 1000;
    private static final int PROG_BAR_AUDIO_FILE = 1000;

    private static final int PROG_BAR_SCREENSHOTSCOMPRESSSCAN = 300;

    //初始化扫描相关事情
    private void initScan() {
        isHaveSdCard = SDCardUtil.isHaveSDCard();
        //获取是否root
        mContext = SpaceApplication.getInstance().getContext().getApplicationContext();
        mCR = mContext.getApplicationContext().getContentResolver();
        mPM = mContext.getPackageManager();
        mPkgList = PackageManagerWrapper.getInstance().getPkgInfoList();
        mbCheckRoot = SuExec.getInstance().checkRoot();
    }

    public void setIsForegroundScan(boolean isForegroundScan) {
        mCommonStatus.setIsForegroundScan(isForegroundScan);
    }

    private ArrayList<String> mRecyleListFor2SdCard = new ArrayList<String>();

    public void setRecycleListFor2SdCard(List<String> fileList) {
        if (!fileList.isEmpty()) {
            mRecyleListFor2SdCard.addAll(fileList);
        }
    }

    public void addAvailableType(JunkRequest request) {
        JunkDataManager.JunkRequestData tmpData = new JunkDataManager.JunkRequestData();
        tmpData.mJunkRequest = request;
        tmpData.mScanResultList = new ArrayList<>();
        mAvailableDataMap.put(request.getRequestType(), tmpData);
    }

    //把SEMC LT22i这种机型的系统缓存改到深度清理中去，以防用户清理了系统缓存以后，相册不能用的情况。
    private static Boolean mFilterSysCacheInStandardScan = null;
    public static boolean filterSysCacheInStandardScan() {
        if (null == mFilterSysCacheInStandardScan) {
            PackageInfo pi = PackageUtils.getPackageInfo(SpaceApplication.getInstance().getContext(), "com.sonyericsson.album");
            if (null != pi) {
                mFilterSysCacheInStandardScan = (6553603 == pi.versionCode);
                NLog.i("SEALBUM", "SE album version = " + pi.versionCode);
            } else {
                mFilterSysCacheInStandardScan = false;
            }
        }

        if (null != mFilterSysCacheInStandardScan) {
            return mFilterSysCacheInStandardScan.booleanValue();
        }

        return false;
    }

    @Override
    public void startScan() {
        if (EM_ENGINE_STATUS.IDLE != mEngineStatus && PublishVersionManager.isTest()) {
            throw new SpacePlusFailedException("start at illegal status: " + mEngineStatus);
        }

        mEngineStatus = EM_ENGINE_STATUS.SCANNING;

        String strTag = CLEAN_STANDARD;
        if (isAdvancedClean()) {
            strTag = CLEAN_ADVANCED;
        }
        mMsgHandler.post(new Runnable() {
            @Override
            public void run() {
            initScan();
            MD5PathConvert.getInstance().clearSubDirMap();
            EnableCacheListDir.openCache();
            mTaskExecutors = Env.isMultiProc ? new SyncExecutors() : new ThreadPoolExecutors(2);
            mTaskExecutorsForClean = null;
            mfinishScanMsgSended = false;

            mDataMgr.reset();

            // 取得云端控制超时值。目前只针对系统缓存扫描和无用缩略图扫描使用此超时值，其它扫描依然不超时。
            int timeOut = CloudCfgDataWrapper.getCloudCfgIntValue(
                    CloudCfgKey.JUNK_SCAN_FLAG_KEY,
                    CloudCfgKey.JUNK_STD_SCAN_TIME_OUT,
                    SCAN_TIME_OUT_MAX);

            MultiTaskTimeCalculator netQueryTimeController = CleanCloudManager.createMultiTaskTimeCalculator();
            ScanCommonStatusImpl scanStatus = new ScanCommonStatusImpl(mCommonStatus);
            scanStatus.setNetQueryTimeController(netQueryTimeController);
            boolean isFirstCleanedJunkStandard = ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).isFirstCleanedJunkStandard();
            boolean isFirstCleanedJunkAdvanced = (ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).isHaveCleanedJunkAdvanced() ? false : true);
            scanStatus.setIsFirstCleanedJunkStandard(isFirstCleanedJunkStandard);
            scanStatus.setIsFirstCleanedJunkAdvanced(isFirstCleanedJunkAdvanced);

            boolean mIsRootCacheScan = CloudCfgDataWrapper.getCloudCfgBooleanValue(
            CloudCfgKey.JUNK_SCAN_FLAG_KEY,
            CloudCfgKey.JUNK_SCAN_ROOT_CACHE_SCAN,
            false) && (Build.VERSION.SDK_INT < 21);


            boolean bHaveCacheData = false;


            ScanTask sysCacheScanTask = null;
            ScanTask sysFixedFileScanTask = null;
            ScanTask sdCacheScanTask = null;
            ScanTask thumbnailScanTask = null;
            ScanTask rubbishScanTask = null;
            ScanTask rubbishScanTaskCachedRst = null;
            ScanTask sdCacheScanTaskCachedRst = null;
            ScanTask scrShotsCompressScanTask = null;
            ScanTask apkScanTask = null;

            if (mDataMgr.isAvailableType(EM_JUNK_DATA_TYPE.SYSCACHE)) {
                if (!filterSysCacheInStandardScan()) {
                    sysCacheScanTask = mDataMgr.getValidCachedDataScanTask(EM_JUNK_DATA_TYPE.SYSCACHE, getDataCacheValidTime(), null);
                    if (null == sysCacheScanTask) {
                        sysCacheScanTask = new SysCacheScanTask();
                        cfgSysCacheTask((SysCacheScanTask)sysCacheScanTask);
                        mProgBarTotal += PROG_BAR_SYS_CACHE;
                    } else {
                        bHaveCacheData = true;
                        sysCacheScanTask.bindCallbackObj(new ScanTaskCallback() {
                            @Override
                            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                                switch(what) {
                                    case JunkDataManager.CACHEDATASCANTASK_MSG_PATH:
                                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SYS_CACHE_SCAN_STATUS, obj));
                                        break;

                                    case JunkDataManager.CACHEDATASCANTASK_MSG_FINISH:
                                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SYS_SCAN);
                                        break;
                                }
                            }
                        });
                    }

                    mTaskExecutors.pushTask(sysCacheScanTask, timeOut);
                    mDataMgr.setTaskActive(JunkDataManager.TASK_TYPE_SYS_CACHE);
                }
            }

            if(mDataMgr.isAvailableType(EM_JUNK_DATA_TYPE.SYSFIXEDCACHE)){
                sysFixedFileScanTask = mDataMgr.getValidCachedDataScanTask(EM_JUNK_DATA_TYPE.SYSFIXEDCACHE, getDataCacheValidTime(), null);
                if(null == sysFixedFileScanTask){
                    //加入到apk扫描中
                    sysFixedFileScanTask = new SysFixedFileScanTask();
                    cfgSysFixedFileScanTask((SysFixedFileScanTask)sysFixedFileScanTask);
                } else {
                    bHaveCacheData = true;
                    sysFixedFileScanTask.bindCallbackObj(new ScanTaskCallback() {

                        @Override
                        public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                            switch(what) {
                                case JunkDataManager.CACHEDATASCANTASK_MSG_FINISH:
                                    mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SYS_FIXED_SCAN);
                                    break;
                            }
                        }
                    });
                    mTaskExecutors.pushTask(sysFixedFileScanTask, timeOut);
                mDataMgr.setTaskActive(JunkDataManager.TASK_TYPE_SYS_FIXED_CACHE);
                }
            }

            boolean didRootCacheScanTaskUseCacheData = false;
            boolean needQueryRootCacheData = false;

            if(mDataMgr.isAvailableType(EM_JUNK_DATA_TYPE.ROOTCACHE) && mIsRootCacheScan){
                needQueryRootCacheData = true;
                rootCacheScanTask = mDataMgr.getValidCachedDataScanTask(EM_JUNK_DATA_TYPE.ROOTCACHE, getDataCacheValidTime(), null);
                if(null == rootCacheScanTask){
                    didRootCacheScanTaskUseCacheData = false;
                    rootCacheScanTask = new RootCacheScanTask();
                    cfgRootCacheTask((RootCacheScanTask)rootCacheScanTask);
                }else{
                    didRootCacheScanTaskUseCacheData = true;
                    rootCacheScanTask.bindCallbackObj(new ScanTaskCallback() {

                        @Override
                        public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                            switch(what) {
                                case JunkDataManager.CACHEDATASCANTASK_MSG_FINISH:
                                    mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_ROOT_CACHE_SCAN_FINISH);
                                    break;
                            }
                        }
                    });
                }
            }
            //有SD卡的情况
            if (isHaveSdCard) {
                PackageInfo pkgInfoForOneCacheScan = null;
                boolean bIgnoreBigFile = false;
                bIgnoreBigFile = true;//先默认忽略大文件
                boolean bStdScan = false, bAdvScan = false;
                ScanTaskCallback bigFileCB = null;
                if (mDataMgr.isAvailableType(EM_JUNK_DATA_TYPE.SDCACHE)) {
                    String pkgName = mDataMgr.getPkgNameToScanCache();
                    if ((!TextUtils.isEmpty(pkgName)) && null != mPkgList) {
                        for (PackageInfo item : mPkgList) {
                            if (null == item) {
                                continue;
                            }

                            if (pkgName.equals(item.packageName)) {
                                pkgInfoForOneCacheScan = item;
                                break;
                            }
                        }
                    }
                    ScanTask tempTask = mDataMgr.getValidCachedDataScanTask(
                            EM_JUNK_DATA_TYPE.SDCACHE, getDataCacheValidTime(), null,
                            pkgInfoForOneCacheScan);
                    if (null == tempTask || (needQueryRootCacheData && (!didRootCacheScanTaskUseCacheData))) {
                        mProgBarTotal += PROG_BAR_SD_CACHE;
                        sdCacheScanTask = createSdCacheScanTask(scanStatus);
                        bStdScan = true;
                    } else {
                        bHaveCacheData = true;
                        sdCacheScanTaskCachedRst = tempTask;
                    }
                } else if(needQueryRootCacheData && (!didRootCacheScanTaskUseCacheData)){
                    sdCacheScanTask = createSdCacheScanTask(scanStatus);
                    bStdScan = true;
                }

                if (null != sdCacheScanTask) {
                    cfgSdCacheTask((SdCardCacheScanTask) sdCacheScanTask, bStdScan,
                            bAdvScan, bigFileCB, bIgnoreBigFile, pkgInfoForOneCacheScan);
                }

                if (mDataMgr.isAvailableType(EM_JUNK_DATA_TYPE.USELESSTHUMBNAIL)) {
                    thumbnailScanTask = mDataMgr.getValidCachedDataScanTask(EM_JUNK_DATA_TYPE.USELESSTHUMBNAIL, getDataCacheValidTime(), null);
                    if (null == thumbnailScanTask) {
                        mProgBarTotal += PROG_BAR_THUMBNAIL;
                        thumbnailScanTask = new ThumbnailScanTask();
                        cfgThumbnailScanTask((ThumbnailScanTask)thumbnailScanTask);
                    } else {
                        bHaveCacheData = true;
                        thumbnailScanTask.bindCallbackObj(new ScanTaskCallback() {
                            @Override
                            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                                switch(what) {
                                    case JunkDataManager.CACHEDATASCANTASK_MSG_PATH:
                                        mDataMgr.notifyCurrentScanItem(EM_JUNK_DATA_TYPE.USELESSTHUMBNAIL, (String)obj);
                                        break;

                                    case JunkDataManager.CACHEDATASCANTASK_MSG_FINISH:
                                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_THUMBNAIL_SCAN);
                                        break;
                                }
                            }
                        });
                    }
                }
                //卸载残留
                if (mDataMgr.isAvailableType(EM_JUNK_DATA_TYPE.APPLEFTOVER)) {
                    ScanTask tempTask = mDataMgr.getValidCachedDataScanTask(EM_JUNK_DATA_TYPE.APPLEFTOVER, getDataCacheValidTime(), rubbishScanTaskCachedRst);
                    if (null == tempTask) {
                        bStdScan = true;
                        if (null == rubbishScanTask) {
                            rubbishScanTask = createRubbishFileScanTask(scanStatus);
                            cfgRubbishFileTask((RubbishFileScanTask) rubbishScanTask, bigFileCB, bIgnoreBigFile);
                        }
                        ((RubbishFileScanTask) rubbishScanTask).setIsBackgroundScan(false);
                        mProgBarTotal += PROG_BAR_APP_LEFTOVER;
                    } else {
                        if (null == rubbishScanTaskCachedRst) {
                            rubbishScanTaskCachedRst = tempTask;
                        }
                    }
                }

                if (bStdScan || bAdvScan) {
                    if (rubbishScanTask != null){
                        int mask = ((RubbishFileScanTask) rubbishScanTask).getScanConfigMask();
                        if (bStdScan && bAdvScan) {
                            mask |= RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_DALVIK_CACHE | RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_QUERY_WITHOUT_ALERTINFO |
                                    RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO | RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS;
                        } else if (bStdScan) {
                            mask |= RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_DALVIK_CACHE | RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_QUERY_WITHOUT_ALERTINFO |
                                    RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_NOT_COUNT_REMAIN_TARGET_MEDIA_FILE_NUM;
                        } else if (bAdvScan) {
                            mask |= RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO | RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS;
                        }
                        ((RubbishFileScanTask) rubbishScanTask).setScanConfigMask(mask);
                    }
                }


                if (mDataMgr.isAvailableType(EM_JUNK_DATA_TYPE.ADVERTISEMENT)) {
                    ScanTask tempTask = mDataMgr.getValidCachedDataScanTask(EM_JUNK_DATA_TYPE.ADVERTISEMENT, getDataCacheValidTime(), rubbishScanTaskCachedRst);
                    if (null == tempTask) {
                        if (null == rubbishScanTask) {
                            rubbishScanTask = createRubbishFileScanTask(scanStatus);
                            cfgRubbishFileTask((RubbishFileScanTask) rubbishScanTask, bigFileCB, bIgnoreBigFile);
                        }
                        cfgRubbishFileTaskChangeMask((RubbishFileScanTask) rubbishScanTask, EM_JUNK_DATA_TYPE.ADVERTISEMENT);
                        mProgBarTotal += PROG_BAR_ADV_FOLDER;
                    } else {
                        bHaveCacheData = true;
                        if (null == rubbishScanTaskCachedRst) {
                            rubbishScanTaskCachedRst = tempTask;
                        }
                    }
                }

                bStdScan = false;
                bAdvScan = false;
                boolean bStdScanTempFolder = false;
                if (mDataMgr.isAvailableType(EM_JUNK_DATA_TYPE.TEMPFOLDER)) {
                    ScanTask tempTask = mDataMgr.getValidCachedDataScanTask(EM_JUNK_DATA_TYPE.TEMPFOLDER, getDataCacheValidTime(), rubbishScanTaskCachedRst);
                    if (null == tempTask) {
                        bStdScanTempFolder = true;
                        bStdScan = true;
                        if (null == rubbishScanTask) {
                            rubbishScanTask = createRubbishFileScanTask(scanStatus);
                            cfgRubbishFileTask((RubbishFileScanTask) rubbishScanTask, bigFileCB, bIgnoreBigFile);
                        }
                        mProgBarTotal += PROG_BAR_TEMPFOLDER;
                    } else {
                        if (null == rubbishScanTaskCachedRst) {
                            rubbishScanTaskCachedRst = tempTask;
                        }
                    }
                }

//屏蔽开始 目前没有用到ScreenShotsCompressScanTask，先屏蔽掉了。
//                if (mDataMgr.isAvailableType(EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS)) {
//                    scrShotsCompressScanTask = mDataMgr.getValidCachedDataScanTask(
//                            EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS, getDataCacheValidTime(), null);
//                    if (null == scrShotsCompressScanTask) {
//                        mProgBarTotal += PROG_BAR_SCREENSHOTSCOMPRESSSCAN;
//                        scrShotsCompressScanTask = new ScreenShotsCompressScanTask();
//                        cfgScreenShotsScanTask((ScreenShotsCompressScanTask) scrShotsCompressScanTask);
//                    } else {
//                        scrShotsCompressScanTask.bindCallbackObj(new ScanTaskCallback() {
//                            @Override
//                            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
//                                switch(what) {
//                                    case JunkDataManager.CACHEDATASCANTASK_MSG_PATH:
//                                        mDataMgr.notifyCurrentScanItem(EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS, (String)obj);
//                                        break;
//
//                                    case JunkDataManager.CACHEDATASCANTASK_MSG_FINISH:
//                                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SCREENSHOTSCOMPRESSSCAN);
//                                        break;
//                                }
//                            }
//                        });
//                    }
//                }
// 屏蔽结束 目前没有用到ScreenShotsCompressScanTask，先屏蔽掉了。

                boolean bscanSysFixedTask = sysFixedFileScanTask != null && (sysFixedFileScanTask instanceof SysFixedFileScanTask);
                boolean needScanApk = mDataMgr.isAvailableType(EM_JUNK_DATA_TYPE.APKFILE);
                if (needScanApk || bStdScanTempFolder || bscanSysFixedTask) {
                    ScanTask tempTask = mDataMgr.getValidCachedDataScanTask(EM_JUNK_DATA_TYPE.APKFILE, getDataCacheValidTime(), null);
                    if (null == tempTask || bStdScanTempFolder  ) {
                        mProgBarTotal += (PROG_BAR_APK_FILE + PROG_BAR_TEMP_FILES + PROG_BAR_LOG_FILES);
                        apkScanTask = new ApkScanTask();
                        if ( bscanSysFixedTask ) {
                            ((ApkScanTask)apkScanTask).setSysFixTask((SysFixedFileScanTask)sysFixedFileScanTask);
                        }
                        cfgApkScanTask((ApkScanTask)apkScanTask, bStdScanTempFolder);
                        ((ApkScanTask)apkScanTask).setScanSwitch(needScanApk && null == tempTask);
                        if (null != tempTask) {
                            bHaveCacheData = true;
                            ((ApkScanTask)apkScanTask).setApkCachedDataScanTask(tempTask);
                            tempTask = null;
                        }
                    } else {
                        bHaveCacheData = true;
                        tempTask.bindCallbackObj(new ScanTaskCallback() {
                            @Override
                            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                                switch(what) {
                                    case JunkDataManager.CACHEDATASCANTASK_MSG_PATH:
                                        mDataMgr.notifyCurrentScanItem(EM_JUNK_DATA_TYPE.APKFILE, (String)obj);
                                        break;

                                    case JunkDataManager.CACHEDATASCANTASK_MSG_FINISH:
                                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_APK_SCAN);
                                        break;
                                }
                            }
                        });
                        apkScanTask = tempTask;
                    }
                }

            }

        //执行任务
        if (null != sdCacheScanTask) {
            if (null != sdCacheScanTaskCachedRst) {
                ((SdCardCacheScanTask)sdCacheScanTask).setSDCachedDataScanTask(sdCacheScanTaskCachedRst);
            }

            mTaskExecutors.pushTask(sdCacheScanTask, SCAN_TIME_OUT_MAX);
            mDataMgr.setTaskActive(JunkDataManager.TASK_TYPE_SD_CACHE);
            NLog.i(TAG, "push--SdCardCacheScanTask0--start");
        } else {
            if (null != sdCacheScanTaskCachedRst) {
                sdCacheScanTaskCachedRst.bindCallbackObj(new ScanTaskCallback() {
                    @Override
                    public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                        switch (what) {
                            case JunkDataManager.CACHEDATASCANTASK_MSG_PATH:
                                mDataMgr.notifyCurrentScanItem(EM_JUNK_DATA_TYPE.SDCACHE, (String)obj);
                                break;

                            case JunkDataManager.CACHEDATASCANTASK_MSG_FINISH:
                                mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SD_SCAN);
                                break;
                        }
                    }
                });

                mTaskExecutors.pushTask(sdCacheScanTaskCachedRst, SCAN_TIME_OUT_MAX);
                mDataMgr.setTaskActive(JunkDataManager.TASK_TYPE_SD_CACHE);
            }
        }

        if (null != apkScanTask) {
            mTaskExecutors.pushTask(apkScanTask, SCAN_TIME_OUT_MAX);
            mDataMgr.setTaskActive(JunkDataManager.TASK_TYPE_APK);
        }

        if (null != rubbishScanTask) {
            if (null != rubbishScanTaskCachedRst) {
                ((RubbishFileScanTask)rubbishScanTask).setRubbishCachedDataScanTask(rubbishScanTaskCachedRst);
            }
            mTaskExecutors.pushTask(rubbishScanTask, SCAN_TIME_OUT_MAX);
            mDataMgr.setTaskActive(JunkDataManager.TASK_TYPE_RUBBISH);
        } else{
            if (null != rubbishScanTaskCachedRst) {
                rubbishScanTaskCachedRst.bindCallbackObj(new ScanTaskCallback() {
                    @Override
                    public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                        switch(what) {
                            case JunkDataManager.CACHEDATASCANTASK_MSG_FINISH:
                                mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_RUBBISH_SCAN);
                                break;
                        }
                    }
                });
                mTaskExecutors.pushTask(rubbishScanTaskCachedRst, SCAN_TIME_OUT_MAX);
                mDataMgr.setTaskActive(JunkDataManager.TASK_TYPE_RUBBISH);
            }
        }

        if (null != scrShotsCompressScanTask) {
            mTaskExecutors.pushTask(scrShotsCompressScanTask, SCAN_TIME_OUT_MAX);
            mDataMgr.setTaskActive(JunkDataManager.TASK_TYPE_SCRSHOTSCOMPRESS);
        }
        if (null != thumbnailScanTask) {
            mTaskExecutors.pushTask(thumbnailScanTask, SCAN_TIME_OUT_MAX);
            mDataMgr.setTaskActive(JunkDataManager.TASK_TYPE_THUMBNAIL);
        }
        NLog.i(TAG,"总进度:::%s",mProgBarTotal);
        //配置
        cfgScanTaskBus(mTaskExecutors);
        //开始扫描
        mTaskExecutors.startScan();
                if (mStopFlag) {
                    NLog.d(TAG, "-->:: mStopFlag true");
                    notifyStop();
                }
        }
        });
    }

    public int getTypeRemoveMask(EM_JUNK_DATA_TYPE type) {
        int rst = 0;
        switch (type) {
            case BIGFILE:
                rst = RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_NOT_QUERY_BIG_FILE_FROM;
                break;
            default:
                break;
        }

        return rst;
    }

    private void cfgRubbishFileTaskChangeMask(RubbishFileScanTask rubbishScanTask,
                                              EM_JUNK_DATA_TYPE type) {
        int mask = rubbishScanTask.getScanConfigMask();
        mask |= getTypeMask(type);
        int nRemoveMask = getTypeRemoveMask(type);
        if (0 != nRemoveMask) {
            mask &= ~nRemoveMask;
        }
        rubbishScanTask.setScanConfigMask(mask);
    }


    public int getTypeMask(EM_JUNK_DATA_TYPE type) {
        int rst = 0;
        switch (type) {
            case ADVERTISEMENT:
                rst = RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_ADV_FILE;
                break;

            case TEMPFOLDER:
                rst = (RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_STD_TEMP_FILE |
                        RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_TEMP_FILE);
                break;

            case APPLEFTOVER:
                rst = RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_REMAIN_INFO;
                break;

            case TEMPFOLDER_ADV:
                rst = RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_TEMP_FILE;
                break;

            case APPLEFTOVER_ADV:
                rst = RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_REMAIN_INFO;
                break;

            case BIGFILE:
                rst = RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_BIG_FILE;
                break;

            default:
                break;
        }

        return rst;
    }

    private void cfgSysCacheTask(SysCacheScanTask sysCacheScanTask) {

        if (null == sysCacheScanTask) {
            return;
        }

        sysCacheScanTask.setCaller(SpaceTaskTime.CM_TASK_TIME_USER_JUNKSTD);
        if (mbFirstUse) {
            sysCacheScanTask.setFirstScanFlag();
        }
        sysCacheScanTask.setPkgManager(mPM);
        sysCacheScanTask.setInstalledPkgList(mPkgList);

        //todo wufeng 开启锁定状态查询开关
        sysCacheScanTask.setScanConfigMask(sysCacheScanTask.getScanConfigMask() &
                (~(SysCacheScanTask.SYS_CACHE_SCAN_CFG_MASK_LOAD_LABEL |
                        SysCacheScanTask.SYS_CACHE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS)));
        sysCacheScanTask.bindCallbackObj(new ScanTaskCallback() {

            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {

                switch (what) {
                    case SysCacheScanTask.SYS_CACHE_SCAN_STATUS:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SYS_CACHE_SCAN_STATUS, obj));
                        break;

                    case SysCacheScanTask.SYS_CACHE_SCAN_FOUND_ITEM:
                        if(mStopFlag){
                            return;
                        }else{
                            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FOUND_CACHE_ITEM, obj));
                        }

                        break;

                    case SysCacheScanTask.SYS_CACHE_SCAN_FINISH:
                        Analytics.endTask(SysCacheScanTask.class);
                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SYS_SCAN);
                        break;

                    case SysCacheScanTask.SYS_CACHE_SCAN_PROGRESS_START:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SYS_CACHE_SCAN_PROGRESS_START, arg1, 0, null));
                        break;

                    case SysCacheScanTask.SYS_CACHE_SCAN_PROGRESS_STEP:
                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_SYS_CACHE_SCAN_PROGRESS_STEP);
                        break;
                }
            }

            private ProgressControl mPC = new ProgressControl(new ProgressControlCallbackAgent(), 0);
        });
    }

    //临时文件，属于广告垃圾
    private void cfgRubbishFileTask(RubbishFileScanTask rubbishScanTask, final ScanTaskCallback bigFileCallback, final boolean bIgnoreBigFile) {

        if (null == rubbishScanTask) {
            return;
        }
        int mask = RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_CALC_SIZE | RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_TEMP_FILE |
                RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE | RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_CALC_CHECKED_SIZE |
                RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_CALC_UNCHECKED_SIZE | RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_REMAIN_INFO |
                RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_DCIM_THUMBNAIL_FOLDER;
        rubbishScanTask.setScanConfigMask(mask);


        rubbishScanTask.bindCallbackObj(new ScanTaskCallback() {

            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {

                switch (what) {
                    case RubbishFileScanTask.SCAN_SDCARD_INFO:
                        EM_JUNK_DATA_TYPE notifyType = EM_JUNK_DATA_TYPE.ADVERTISEMENT;
                        if(arg1==1){
                            notifyType=EM_JUNK_DATA_TYPE.APPLEFTOVER;
                        }
//                        if (4 == arg1) {
//                            notifyType = EM_JUNK_DATA_TYPE.ADVERTISEMENT;
//                            NLog.d(TAG, "rubbisADD_CHILDREN_DATA_ITEM_TO_ADAPTERhScanTask SCAN_SDCARD_INFO 广告");
//                        } else if (5 == arg1) {
//                            notifyType = EM_JUNK_DATA_TYPE.TEMPFOLDER;
//                            NLog.d(TAG, "rubbishScanTask SCAN_SDCARD_INFO 临时文件");
//                        }
                        mDataMgr.notifyCurrentScanItem(notifyType, (String) obj);
                        break;

                    case RubbishFileScanTask.RUB_FILE_SCAN_COMING_SOON_SIZE:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_COMING_SOON_SIZE, obj));
                        break;

                    case RubbishFileScanTask.ADD_CHILDREN_DATA_ITEM_TO_ADAPTER: {
                        NLog.e(TAG, "rubbishScanTask 扫描到残留文件, 垃圾类型为 arg1 = " +arg1 + ", type = "+ RubbishUtil.getRubbishTypeString(arg1) + ", arg2 = "+ arg2 +", pkgName = "+obj);

                        if (mStopFlag) {
                            return;
                        }

                        SDcardRubbishResult sdcardRubbishResult = ((SDcardRubbishResult) obj);

                        if (sdcardRubbishResult.getScanType() == BaseJunkBean.SCAN_TYPE_ADVANCED
                                && SDcardRubbishResult.RF_APP_LEFTOVERS == arg1
                                && sdcardRubbishResult.getSize() >= ExtraAndroidFileScanner.SIZE_BIG_FILE_MIN) {
                            if (bigFileCallback != null) {
                                bigFileCallback.callbackMessage(ExtraAndroidFileScanner.ADD_CHILDREN_DATA_ITEM_TO_ADAPTER, arg1, arg2, obj);
                            } else if (!bIgnoreBigFile) {
                                mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FOUND_RUBBISH_ITEM, arg1, arg2, obj));
                            }
                        } else {
                            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FOUND_RUBBISH_ITEM, arg1, arg2, obj));
                        }

                        break;
                    }
                    case RubbishFileScanTask.UPDATE_CHILDREN_DATA_ITEM_TO_ADAPTER: {
                        //扫描空文件夹完成的时候会执行到这里
                        SDcardRubbishResult sdcardRubbishResult = ((RubbishFileScanTask.UpdateChildrenData) obj).newObj;
                        NLog.e(TAG,"RubbishFileScanTask UPDATE_CHILDREN_DATA_ITEM_TO_ADAPTER, pkgName = " +sdcardRubbishResult + ",  type = "+ RubbishUtil.getRubbishTypeString(arg1));
                        if (sdcardRubbishResult.getScanType() == BaseJunkBean.SCAN_TYPE_ADVANCED
                                && SDcardRubbishResult.RF_APP_LEFTOVERS == arg1
                                && sdcardRubbishResult.getSize() >= ExtraAndroidFileScanner.SIZE_BIG_FILE_MIN) {

                            if (bigFileCallback != null) {
                                bigFileCallback.callbackMessage(RubbishFileScanTask.UPDATE_CHILDREN_DATA_ITEM_TO_ADAPTER, arg1, arg2, sdcardRubbishResult);
                            } else if (!bIgnoreBigFile) {
                                mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RUBBISH_ITEM, arg1, arg2, obj));
                            }
                        } else {
                            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RUBBISH_ITEM, arg1, arg2, obj));
                        }
                        break;
                    }
                    case RubbishFileScanTask.RUB_FILE_SCAN_IGNORE_ITEM:
                        break;

                    case RubbishFileScanTask.SCAN_FINISH:
                        Analytics.endTask(RubbishFileScanTask.class);
                        NLog.e(TAG, "JunkEngineImpl rubbishScanTask 扫描完成, arg1 = "+arg1 + ", arg2 = "+ arg2 +", pkgName = "+obj);

                        if (bigFileCallback != null) {
                            bigFileCallback.callbackMessage(RubbishFileScanTask.RUB_FILE_SCAN_LEFT_OVER_FINISHED, arg1, arg2, obj);
                        }
                        mPCAppLeft.stopControl();
                        mPCTemp.stopControl();
                        mPCAdv.stopControl();
                        mPCBig.stopControl();
                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_RUBBISH_SCAN);
                        break;

                    case RubbishFileScanTask.RUB_FILE_SCAN_PROGRESS_START:

                        getProgressControl(arg1).startControl(mProgBarTotal, getProgressControlLength(arg1), true);
                        break;

                    case RubbishFileScanTask.RUB_FILE_SCAN_PROGRESS_STEP_NUM:
                        getProgressControl(arg1).setStepNum(arg2);
                        break;

                    case RubbishFileScanTask.RUB_FILE_SCAN_PROGRESS_ADD_STEP:
                        NLog.d(TAG, "rubbishScanTask RUB_FILE_SCAN_PROGRESS_ADD_STEP, arg1 = "+arg1 + ", arg2 = "+ arg2 +", pkgName = "+obj);
                        getProgressControl(arg1).addStep();
                        break;
                    case RubbishFileScanTask.RUB_FILE_SCAN_ADV_FINISHED:
                        Analytics.endTask(AdvFolderScanTask.class);
                        NLog.e(TAG, "rubbishScanTask 广告文件扫描完毕, 垃圾类型为 = "+ RubbishUtil.getRubbishTypeString(arg1) + ", arg2 = "+ arg2 +", pkgName = "+obj);

                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_ADV_SCAN, obj));
                        break;
                    case RubbishFileScanTask.RUB_FILE_SCAN_LEFT_OVER_FINISHED:
                        NLog.d(TAG, "rubbishScanTask RUB_FILE_SCAN_LEFT_OVER_FINISHED, arg1 = "+arg1 + ", arg2 = "+ arg2 +", pkgName = "+obj);

                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_LEFT_OVER_SCAN, obj));
                        break;
                }
            }

            private ProgressControl getProgressControl(int type) {
                switch (type) {
                    case SDcardRubbishResult.RF_APP_LEFTOVERS:
                        return mPCAppLeft;

                    case SDcardRubbishResult.RF_TEMPFILES:
                        return mPCTemp;

                    case SDcardRubbishResult.RF_ADV_FOLDERS:
                        return mPCAdv;

                    case SDcardRubbishResult.RF_BIG_FILES:
                        return mPCBig;
                }

                return null;
            }

            private int getProgressControlLength(int type) {
                switch (type) {
                    case SDcardRubbishResult.RF_APP_LEFTOVERS:
                        return PROG_BAR_APP_LEFTOVER;


                    case SDcardRubbishResult.RF_ADV_FOLDERS:
                        return PROG_BAR_ADV_FOLDER;

                    case SDcardRubbishResult.RF_BIG_FILES:
                }

                return 0;
            }

            private ProgressControl mPCAppLeft = new ProgressControl(new ProgressControlCallbackAgent(), 0);
            private ProgressControl mPCTemp = new ProgressControl(new ProgressControlCallbackAgent(), 0);
            private ProgressControl mPCAdv = new ProgressControl(new ProgressControlCallbackAgent(), 0);
            private ProgressControl mPCBig = new ProgressControl(new ProgressControlCallbackAgent(), 0);
        });
    }

    private void cfgSysFixedFileScanTask(SysFixedFileScanTask sysFixedFileScanTask){
        if (null == sysFixedFileScanTask) {
            return;
        }

        sysFixedFileScanTask.bindCallbackObj(new ScanTaskCallback() {

            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                switch(what){
                    case SysFixedFileScanTask.SYS_FIXED_FILE_SCAN_START:
                        break;
                    case SysFixedFileScanTask.SYS_FIXED_FILE_CACHE_SCAN_FINISH:
                        Analytics.endTask(SysFixedFileScanTask.class);
                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SYS_FIXED_SCAN);
                        break;
                    case SysFixedFileScanTask.SYS_FIXED_FILE_SCAN_FOUND_ITEM:
                        if(mStopFlag){
                            return;
                        }else{
                            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FOUND_CACHE_ITEM, obj));
                        }
                        break;
                }
            }
        });
    }

    private void cfgRootCacheTask(RootCacheScanTask rootCacheScanTask){
        if(null == rootCacheScanTask){
            return;
        }

        rootCacheScanTask.bindRootCacheScanCallback(new RootCacheScanTask.RootCacheScanCallback() {

            @Override
            public RootCacheInfo getRootCacheInfoItem() {
                try {
                    return rootCacheInfoQueue.take();
                } catch (Exception e) {
                    NLog.printStackTrace(e);
                }
                return null;
            }

            @Override
            public boolean isQueueEmpty() {
                return rootCacheInfoQueue.size() <= 0;
            }
        });

        rootCacheScanTask.bindCallbackObj(new ScanTaskCallback(){

            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                switch(what){
                    case RootCacheScanTask.ROOT_CACHE_SCAN_PROGRESS_START:
                        break;
                    case RootCacheScanTask.ROOT_CACHE_SCAN_FOUND_ITEM:
                        if(mStopFlag){
                            return;
                        }else{
                            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_ROOT_CACHE_SCAN_FOUND_ITEM, obj));
                        }
                        break;
                    case RootCacheScanTask.ROOT_CACHE_SCAN_FINISH:
                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_ROOT_CACHE_SCAN_FINISH);
                        break;
                }
            }

        });
    }

    private void cfgSdCacheTask(SdCardCacheScanTask sdCacheScanTask,
                                boolean bScanStd, boolean bScanAdv,
                                final ScanTaskCallback bigFileCallback, final boolean bIgnoreBigFile,
                                PackageInfo pkgInfoForOneCacheScan) {

        if (null == sdCacheScanTask) {
            return;
        }

        if (bScanStd && bScanAdv) {
            sdCacheScanTask.setCaller(SpaceTaskTime.CM_TASK_TIME_USER_JUNKADV);
        } else if (bScanStd) {
            sdCacheScanTask.setScanConfigMask(
                    ~(SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO |
                            SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS));
            sdCacheScanTask.setCaller(SpaceTaskTime.CM_TASK_TIME_USER_JUNKSTD);
        } else if (bScanAdv) {
            sdCacheScanTask.setScanConfigMask(
                    ~(SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_QUERY_WITHOUT_ALERTINFO |
                            SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_NOT_COUNT_TARGET_MEDIA_FILE_NUM));
            sdCacheScanTask.setCaller(SpaceTaskTime.CM_TASK_TIME_USER_JUNKADV);
        } else {
            return;
        }

        if (mbFirstUse) {
            sdCacheScanTask.setFirstScanFlag();
        }

        if (null == pkgInfoForOneCacheScan) {
            sdCacheScanTask.setInstalledPkgList(mPkgList);
        } else {
            ArrayList<PackageInfo> pkgList = new ArrayList<>(1);
            pkgList.add(pkgInfoForOneCacheScan);
            sdCacheScanTask.setInstalledPkgList(pkgList);
        }
        sdCacheScanTask.setRootCacheCallback(new SdCardCacheScanTask.RootCacheCallback() {

            @Override
            public void sdCacheScanFinish(boolean isFinish) {
                NLog.i(TAG,"sdCacheScanTask RootCache");
                if (rootCacheScanTask != null) {
                    if (rootCacheScanTask instanceof RootCacheScanTask) {
                        ((RootCacheScanTask) rootCacheScanTask).notifySdCacheScanFinish(isFinish);
                        RootCacheInfo rootCacheInfo = new RootCacheInfo();
                        rootCacheInfo.setPkgName("end");
                        try {
                            rootCacheInfoQueue.offer(rootCacheInfo);
                        } catch (Exception e) {
                            NLog.printStackTrace(e);
                        }
                    }
                }
            }

            @Override
            public void addPkgPathInfoItem(RootCacheInfo item) {
                try {
                    rootCacheInfoQueue.offer(item);
                } catch (Exception e) {
                    NLog.printStackTrace(e);
                }
            }
        });
        sdCacheScanTask.bindCallbackObj(new ScanTaskCallback() {

            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                NLog.i(TAG, "sdCacheScanTask callbackMessage" + what);
                switch (what) {
                    case SdCardCacheScanTask.SD_CACHE_SCAN_STATUS:
                        mDataMgr.notifyCurrentScanItem(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE, (String) obj);
                        break;

                    case SdCardCacheScanTask.SD_CACHE_SCAN_COMMING_SOON_SIZE:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_COMING_SOON_SIZE, obj));
                        break;

                    case SdCardCacheScanTask.SD_CACHE_SCAN_FOUND_ITEM:
                        if (mStopFlag) {
                            return;
                        }

                        CacheInfo info = ((CacheInfo) obj);
                        if (info.getInfoType() == CacheInfo.INFOTYPE_APPCACHE &&
                                info.getScanType() == BaseJunkBean.SCAN_TYPE_ADVANCED &&
                                info.getSize() >= ExtraAndroidFileScanner.SIZE_BIG_FILE_MIN) {
                            if (bigFileCallback != null) {
                                if (!BigFileScanTask.isFilterCombineRubbish(info)) {
                                    bigFileCallback.callbackMessage(ExtraAndroidFileScanner.ADD_CHILDREN_DATA_ITEM_TO_ADAPTER, arg1, arg2, obj);
                                } else if (!BigFileScanTask.isIgnoreItem(info.getFilePath())) {
                                    mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FOUND_CACHE_ITEM, obj));
                                }
                            } else if (!bIgnoreBigFile) {
                                mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FOUND_CACHE_ITEM, obj));
                            }
                        } else {
                            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FOUND_CACHE_ITEM, obj));
                        }

                        break;

                    case SdCardCacheScanTask.SD_CACHE_SCAN_IGNORE_ITEM:
//					mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FILTER_SD_CACHE, pkgName));
                        break;

                    case SdCardCacheScanTask.SD_CACHE_SCAN_FINISH:
                        Analytics.endTask(SdCardCacheScanTask.class);
                        if (bigFileCallback != null) {
                            NLog.d(TAG, "onScanEngineFinish");
                            bigFileCallback.callbackMessage(SdCardCacheScanTask.SD_CACHE_SCAN_FINISH, arg1, arg2, obj);
                        }
                        mPC.stopControl();
                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SD_SCAN);
                        NLog.i(TAG, "push--SdCardCacheScanTask0--end");
                        break;

                    case SdCardCacheScanTask.SD_CACHE_SCAN_PROGRESS_START:
                        mPC.startControl(mProgBarTotal, PROG_BAR_SD_CACHE, true);
                        mPC.setStepNum(arg1);
                        break;

                    case SdCardCacheScanTask.SD_CACHE_SCAN_PROGRESS_STEP:
                        mPC.addStep();
                        break;
                }
            }

            private ProgressControl mPC = new ProgressControl(new ProgressControlCallbackAgent(), 0);
        });
    }

    private void cfgThumbnailScanTask(ThumbnailScanTask thumbnailScanTask) {
        if (null == thumbnailScanTask) {
            return;
        }
        thumbnailScanTask.setScanConfigMask(~ThumbnailScanTask.THUMB_FILE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS);
//        thumbnailScanTask.setCaller(cm_task_time.CM_TASK_TIME_USER_JUNKSTD);
//        if (mbFirstUse) {
//            thumbnailScanTask.setFirstScanFlag();
//        }
        thumbnailScanTask.bindCallbackObj(new ScanTaskCallback() {
            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                NLog.i(TAG,"thumbnailScanTask %d",what);
                switch (what) {
                    case ThumbnailScanTask.SCAN_SDCARD_INFO:
                        mDataMgr.notifyCurrentScanItem(EM_JUNK_DATA_TYPE.USELESSTHUMBNAIL, (String)obj);
                        break;
                    case ThumbnailScanTask.ADD_CHILDREN_DATA_ITEM_TO_ADAPTER:
                        if(mStopFlag){
                            return;
                        } else{
                            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FOUND_RUBBISH_ITEM, arg1, arg2, obj));
                        }
                        break;
                    case ThumbnailScanTask.UPDATE_CHILDREN_DATA_ITEM_TO_ADAPTER:
                        if(mStopFlag){
                            return;
                        } else{
                            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_RUBBISH_ITEM, arg1, arg2, obj));
                        }
                        break;
                    case ThumbnailScanTask.THUMB_FILE_SCAN_IGNORE_ITEM:
//					    mMsgHander.sendMessage(mMsgHander.obtainMessage(JunkEngineMsg.MSG_HANDLER_FILTER_RUBBISH_ITEM, pkgName));
                        break;
                    case ThumbnailScanTask.SCAN_FINISH:
                        Analytics.endTask(ThumbnailScanTask.class);
                        mPC.stopControl();
                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_THUMBNAIL_SCAN);
                        break;
                }
            }

            private ProgressControl mPC = new ProgressControl(new ProgressControlCallbackAgent(), 0);
        });
    }

    private class ProgressControlCallbackAgent implements ScanTaskCallback {
        @Override
        public void callbackMessage(int what, int arg1, int arg2,
                                    Object obj) {
            if (null != mCB) {
                mCB.onProgress(arg1, arg2);
            }
        }
    }

    private void cfgScanTaskBus(TaskExecutors tb) {
        tb.setCallback(new TaskExecutors.ITaskBusCallback() {
            @Override
            public void changeTaskBusStatus(int oldStatus, int newStatus) {

            }

            @Override
            public void notifySkipScan(ScanTask task) {
                if (null == task) {
                    return;
                }
                if (task instanceof SdCardCacheScanTask) {
                    mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SD_SCAN);
                } else if (task instanceof RubbishFileScanTask) {
                    mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_RUBBISH_SCAN);
                } else if (task instanceof ThumbnailScanTask) {
                    mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_THUMBNAIL_SCAN);
                } else if (task instanceof SysCacheScanTask) {
                    mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SYS_SCAN);
                }else if (task instanceof ApkScanTask) {
                    mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_APK_SCAN);
                }
            }
        });
    }

    public static class CommonStatus {

        public boolean getIsForegroundScan() {
            return mIsForegroundScan;
        }

        public void setIsForegroundScan(boolean value) {
            mIsForegroundScan = value;
        }

        private volatile boolean mIsForegroundScan = false;
    }


    private ScanTask createRubbishFileScanTask(ScanCommonStatus scanStatus) {
        RubbishFileScanTask scanTask = new RubbishFileScanTask();
        if (scanStatus != null) {
            scanTask.setScanCommonStatus(scanStatus);
        }
        return scanTask;
    }


    private ScanTask createSdCacheScanTask(ScanCommonStatus scanStatus) {
        SdCardCacheScanTask scanTask = new SdCardCacheScanTask();
        if (scanStatus != null) {
            scanTask.setScanCommonStatus(scanStatus);
        }
        return scanTask;
    }

    private void getRunningProcess() {
        ActivityManager am = (ActivityManager) SpaceApplication.getInstance().getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        if (processList == null) {
            return;
        }
        for (ActivityManager.RunningAppProcessInfo rpi : processList) {
            mRunProcessList.add(rpi.processName);
        }
    }

    @Override
    public void startClean() {

        if (EM_ENGINE_STATUS.SCANNING == mEngineStatus ||
                EM_ENGINE_STATUS.CLEANING == mEngineStatus) {
            if(PublishVersionManager.isTest()){
                throw new SpacePlusFailedException("start at illegal status: " + mEngineStatus);
            }
        }
        String strTag = CLEAN_STANDARD;
        if (isAdvancedClean()) {
            strTag = CLEAN_ADVANCED;
        }
        if (mCleanRequest == null && PublishVersionManager.isTest()) {
            throw new NullPointerException();
        }
        Map<EM_JUNK_DATA_TYPE, List<BaseJunkBean>> mapCleanItem = mCleanRequest.getCleanJunkInfoList();
        if (null == mapCleanItem || mapCleanItem.isEmpty()) {
            NLog.d(TAG, strTag + "clean itemlist is empty");
        }

        mEngineStatus = EM_ENGINE_STATUS.CLEANING;
        //获取当前运行进程是为了防止删除正在运行的缓存
        getRunningProcess();

        mEngineStatus = EM_ENGINE_STATUS.CLEANING;
        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_RST_CLEAN_LOG, 0, 0, null));
        mEmptyFolderList.clear();
        mIsSysCacheCleaned = false;
        mAvailMemCB = 0;
        mCleanTimeoutCB = 0;
        setWhiteList();
        initOnCleanFileFeedbackList();

        mStopFlag = false;
        mTaskExecutors = null;
        mTaskExecutorsForClean = new TaskExecutors();

        //配置清理任务
        //rubbish 清理
        cfgRubbishPathTask(mapCleanItem);

        //apk清理
        cfgApkPathTask(mapCleanItem);

        //sd cache清理
        TreeSet<String> sysAndroidDataPathSet = new TreeSet<String>();
        HashMap<String, List<CacheInfo>> containSdCacheMap = new HashMap<String, List<CacheInfo>>();
        List<BaseJunkBean> itemList = mapCleanItem.get(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);
        if (null != itemList) {
            for (BaseJunkBean base : itemList) {
                if (base == null) {
                    continue;
                }
                CacheInfo cacheInfo = (CacheInfo) base;
                if (null != cacheInfo) {
                    SysCacheScanTask.SysCacheOnCardInfo sysCacheInfo = cacheInfo.getSysCacheOnCardInfo();
                    if (sysCacheInfo != null && sysCacheInfo.strAbsPathList != null){
                        sysAndroidDataPathSet.addAll(sysCacheInfo.strAbsPathList);
                    }
                }
            }
        }
        cfgSdCachePathTask(mapCleanItem,sysAndroidDataPathSet,containSdCacheMap);

        //root cache清理
        cfgRootCachePathTask(mapCleanItem);

        //system fixed cache
        cfgSysFixedCachePathTask(mapCleanItem);

        //media file
//        cfgMediaFilePathTask(mapCleanItem);

        //screenshots compress
//        cfgScreenShotsCompressTask(mapCleanItem);

        //系统cache清理
        // 因为系统缓存在清除的时候，会有一个清除前备份，清除完成后在异步线程回调中恢复的操作。
        // 所以，为了防止上面的删除流程中有删除此备份目录相关的操作，我们就要确保
        // 系统缓存的清理在最后执行。
        // 例如：SD缓存中要删除/Android/data/com.dropbox.android/cache目录，但是系
        // 统缓存在清理前会将此目录改名，如果系统缓存的清理操作没有回调结束，此目录就
        // 是不存在的，SD缓存这边就删除不掉。
        NLog.i(TAG, " cfgSysCacheTask ");
        cfgSysCacheTask(mapCleanItem, containSdCacheMap);

        //开始清理
        cfgTaskExcutor(mTaskExecutorsForClean);
        mTaskExecutorsForClean.startScan();
    }

    private void cfgTaskExcutor(TaskExecutors taskExecutors) {
        taskExecutors.setCallback(new TaskExecutors.ITaskBusCallback() {
            @Override
            public void changeTaskBusStatus(int oldStatus, int newStatus) {
                if (TaskExecutors.TASK_BUS_STATUS_FINISHED == newStatus) {
                    mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_CLEAN, 0, 0));
                }
            }

            @Override
            public void notifySkipScan(ScanTask task) {

            }
        });
    }

    public enum EnumCleanTask {
        PROC_STD, SYSCACHE_STD, SYSFIXEDCHE_STD, SDCACHE_STD, ROOTCACHE_STD,
        RUBBISH_STD, APK_STD, TOTAL_STD, SCAN_STD,
        SYSCACHE_ADVSTD, SDCACHE_ADVSTD, RUBBISH_ADVSTD, APK_ADVSTD,
        SDCACHE_ADV, RUBBISH_ADV,
        MEDIA_ADV,
        TOTAL_ADV, SCAN_ADV,
        SYSCACHE_STDI;
    }

    private void cfgSysCacheTask(Map<EM_JUNK_DATA_TYPE, List<BaseJunkBean>> mapCleanItem, HashMap<String, List<CacheInfo>> containSdCacheMap) {
        if (null == mapCleanItem || mapCleanItem.isEmpty() ||
                mapCleanItem.get(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE) == null) {
            return;
        }


        Queue<BaseJunkBean> sysCacheItemList = new LinkedList<>();
        List<BaseJunkBean> itemList = mapCleanItem.get(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);
        if (null != itemList) {
            NLog.d(TAG, "开始筛选系统缓存数据");
            removeUncheckedData(itemList);
            sysCacheItemList.addAll(itemList);
        }
        if (sysCacheItemList.isEmpty()) {
            return;
        }
        // 生成SysCache误删记录
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sysCacheSB = new StringBuilder();
        sysCacheSB.append(format.format(new Date(System.currentTimeMillis())))
                .append("-->").append("sdcache")
                .append("-->").append("[");
        for (BaseJunkBean baseJunkBean : sysCacheItemList) {
            CacheInfo cacheInfo = (CacheInfo) baseJunkBean;
            sysCacheSB.append(cacheInfo.getPackageName()).append("：size=").append(cacheInfo.getSize()).append(", ");
        }
        sysCacheSB.setLength(sysCacheSB.length() - 2); // 删除“， ”
        sysCacheSB.append("]");
        mSYSDelOpLog = sysCacheSB.toString();
        NLog.d(TAG, sysCacheSB.toString());

        SysCacheCleanTask sysCacheCleanTask = new SysCacheCleanTask(isAdvancedClean()? EnumCleanTask.SYSCACHE_ADVSTD.toString(): EnumCleanTask.SYSCACHE_STD.toString());
        sysCacheCleanTask.setWhiteList(mFileWhiteList, mFolderWhiteList);
        cfgSysCacheCleanTaskForList(sysCacheCleanTask, sysCacheItemList,containSdCacheMap);
        mTaskExecutorsForClean.pushTask(sysCacheCleanTask);
    }

    private void cfgSdCachePathTask(Map<EM_JUNK_DATA_TYPE, List<BaseJunkBean>> mapCleanItem, TreeSet<String> androidDataPathSet
            , HashMap<String, List<CacheInfo>> containSdCacheMap) {
        NLog.d(SysCacheScanTask.TAG, "cfgSdCachePathTask");
        if (null == mapCleanItem || mapCleanItem.isEmpty() ||
                (null == mapCleanItem.get(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE) &&
                        null == mapCleanItem.get(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE_ADV)) &&
                        null == mapCleanItem.get(JunkRequest.EM_JUNK_DATA_TYPE.BIGFILE)) {
            return;
        }

        Queue<BaseJunkBean> sdCacheItemList = new LinkedList<BaseJunkBean>();

        String strTaskName = EnumCleanTask.SDCACHE_STD.toString();
        List<BaseJunkBean> itemList = mapCleanItem.get(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE_ADV);
        if (null != itemList && !itemList.isEmpty()) {
            sdCacheItemList.addAll(itemList);
            strTaskName = EnumCleanTask.SDCACHE_ADV.toString();
        }

        itemList = mapCleanItem.get(JunkRequest.EM_JUNK_DATA_TYPE.BIGFILE);
        if (null != itemList && !itemList.isEmpty()) {
            for (BaseJunkBean info : itemList) {
                if (info instanceof CacheInfo) {
                    info.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.BIGFILE);
                    sdCacheItemList.add(info);
                }
            }
            strTaskName = EnumCleanTask.SDCACHE_ADV.toString();
        }

        itemList = mapCleanItem.get(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE);
        if (null != itemList && !itemList.isEmpty()) {
            NLog.d(SysCacheCleanTask.TAG, "开始筛选sd卡缓存数据");
            removeUncheckedData(itemList);
            if (androidDataPathSet == null || androidDataPathSet.isEmpty()) {
                sdCacheItemList.addAll(itemList);
            } else {
                for (BaseJunkBean info : itemList) {
                    if (info == null) {
                        continue;
                    }
                    if (info instanceof CacheInfo) {
                        CacheInfo cacheInfo = (CacheInfo) info;
                        String path = cacheInfo.getFilePath();
                        if (path == null) {
                            continue;
                        }
                        boolean isContain = false;

                        if (androidDataPathSet.contains(path)) {
                            isContain = true;
                        } else {
                            if (Build.VERSION.SDK_INT > 8) {
                                isContain = isContainPath(androidDataPathSet, path);
                            } else {
                                for (String parentPath : androidDataPathSet) {
                                    if (parentPath != null && path.startsWith(FileUtils.addSlash(parentPath))) {
                                        isContain = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (!isContain) {
                            sdCacheItemList.add(cacheInfo);
                        } else {
                            if (containSdCacheMap != null) {
                                List<CacheInfo> caches = containSdCacheMap.get(cacheInfo.getPackageName());
                                if (caches == null) {
                                    caches = new ArrayList<CacheInfo>();
                                    caches.add(cacheInfo);
                                    containSdCacheMap.put(cacheInfo.getPackageName(), caches);
                                } else {
                                    caches.add(cacheInfo);
                                }
                            }
                        }
                    }
                }

            }
        }

        if (sdCacheItemList.isEmpty()) {
            return;
        }

        SdPathCleanTask sdCacheCleanTask = new SdPathCleanTask(strTaskName, mVisibleFolderMap, true);
        sdCacheCleanTask.setWhiteList(mFileWhiteList, mFolderWhiteList);
        cfgSdCacheCleanTaskForList(sdCacheCleanTask, sdCacheItemList);
        mTaskExecutorsForClean.pushTask(sdCacheCleanTask);

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private Boolean isContainPath(TreeSet<String> set, String path) {
        String parentPath = set.lower(path);
        if (parentPath != null && path.startsWith(FileUtils.addSlash(parentPath))) {
            return true;
        }
        return false;
    }

    private void initOnCleanFileFeedbackList() {

        mOnCleanFeedbackListFolder.clear();
        mOnCleanFeedbackListFile.clear();
        //nilo 延后开发
        /*if (ConflictCommons.isCNVersion()) {
            mOnCleanFeedbackListFolder.add(File.separator + "data" + File.separator + "data" + File.separator);
        }*/
    }

    private void setWhiteList() {

        //Notice : Folder should end with "/"
        mFolderWhiteList.add(File.separator + MediaFile.NoMediaFileName);
        mFolderWhiteList.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + File.separator);

        mFileWhiteList.add(File.separator + MediaFile.NoMediaFileName);
        mFileWhiteList.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + File.separator);
    }

    @Override
    public void removeDataItem(BaseJunkBean item) {
        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_REMOVE_DATA_ITEM, item));
    }

    public void updateDataItem(BaseJunkBean item) {
        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_UPDATE_DATA_ITEM, item));
    }

    @Override
    public void notifyPause() {

    }

    @Override
    public void notifyResume() {

    }

    @Override
    public void notifyStop() {

        if (null != mDataMgr) {
            mDataMgr.notifyStop();
        }

        mStopFlag = true;
        if (null != mTaskExecutors) {
            mTaskExecutors.notifyStop();
        }
        if (null != mTaskExecutorsForClean) {
            mTaskExecutorsForClean.notifyStop();
        } else {
            if (null != mTaskExecutors && (!mDataMgr.isFinishedSysCacheScan())) {
                // 因为系统缓存是异步扫描的，所以没那么快能停下来，就不用等它停了。
                mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SYS_SCAN);
            }
        }
    }

    @Override
    public void addScanRequest(JunkRequest request) {
        if (null == request) {
            NLog.e(TAG," addScanRequest error");
            return;
        }

        if (getEngineStatus() != EM_ENGINE_STATUS.IDLE && PublishVersionManager.isTest()) {
            throw new SpacePlusFailedException("start at illegal status: " + mEngineStatus);
        }
        NLog.i(TAG, " dataManager addAvailableType ");
        mDataMgr.addAvailableType(request);
    }

    @Override
    public EM_ENGINE_STATUS getEngineStatus() {
        return mEngineStatus;
    }

    @Override
    public void setCallback(EngineCallback cb) {
        if (getEngineStatus() != EM_ENGINE_STATUS.IDLE) {
            if (BuildConfig.DEBUG && PublishVersionManager.isTest()) {
                throw new SpacePlusFailedException("setCallback at illegal status: " + mEngineStatus);
            } else {
                android.util.Log.e("JunkEng", "setCallback at illegal status: " + mEngineStatus);
                return;
            }
        }
        mCB = cb;
    }

    @Override
    public void setEngineConfig(EngineConfig cfg) {
        if (getEngineStatus() != EM_ENGINE_STATUS.IDLE) {
            if (BuildConfig.DEBUG && PublishVersionManager.isTest()) {
                throw new SpacePlusFailedException("setEngineConfig at illegal status: " + mEngineStatus);
            } else {
                android.util.Log.e("JunkEng", "setEngineConfig at illegal status: " + mEngineStatus);
                return;
            }
        }

        mCfg = cfg;
    }

    public int getVideoCardState() {
        return mDataMgr.getVideoCardState();
    }

    private long getDataCacheValidTime() {
        if (null != mCfg) {
            return mCfg.getCfgLong(EngineConfig.ENG_CFG_NAME_VALID_CACHE_DATA_TIME, DATA_CACHE_VALID_TIME);
        }

        return DATA_CACHE_VALID_TIME;
    }

    private void cfgSdCacheCleanTaskForList(final SdPathCleanTask sdCacheCleanTask, final Queue<BaseJunkBean> sdCacheItemList) {

        assert(null != sdCacheCleanTask && null != sdCacheItemList && (!sdCacheItemList.isEmpty()));

        final Queue<JunkCleanItemInfo> sdCacheCleanPath = new LinkedList<JunkCleanItemInfo>();
        JunkDataManager.getSdCardCacheListPathQueue(sdCacheItemList, sdCacheCleanPath);

        final EmergencyFalseSignFilter emFalseSignFilter = CleanCloudManager.createEmergencyFalseSignFilter(EmergencyFalseSignFilter.FilterType.CACHE_DIR);

        final int oldMask = sdCacheCleanTask.getCtrlMask();
        class SdCacheCleanCallback extends SdPathCleanTask.CleanDataSrcBase implements ScanTaskCallback {
            @Override
            public SdPathCleanTask.DelPathInfo getNextCleanPathInfo() {
                JunkCleanItemInfo info = null;
                BaseJunkBean item = null;
                CacheInfo cacheItem = null;
                while (true) {
                    info = sdCacheCleanPath.poll();
                    if (null == info) {
                        return null;
                    }

                    item = info.getJunkItem();
                    if (null != item) {
                        if (item instanceof CacheInfo) {
                            cacheItem = (CacheInfo)item;
                            if (!emFalseSignFilter.filter(cacheItem.getCacheId())) {
                                break;
                            }
                        }
                    }
                }

                int cleanFileFlag = info.getCleanFileFlag();

                boolean isRP = mRPList.contains(cacheItem.getPackageName());

                int mask = oldMask & (~SdPathCleanTask.CTRL_MASK_CALC_SIZE);
                if (cacheItem.getDeleteType() == 0  || isRP) {
                    mask = mask & (~(SdPathCleanTask.CTRL_MASK_CLEAN_FOLDER | SdPathCleanTask.CTRL_MASK_CLEAN_TOP_FOLDER));
                    mEmptyFolderList.add(cacheItem.getFilePath());
                }
                return new SdPathCleanTask.DelPathInfo(mask, info.getPathList(), info, cleanFileFlag, item.getFileType(), item.getSize());
            }

            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                switch (what) {
                    case SdPathCleanTask.CLEAN_ITEM:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SD_CLEAN_ITEM, arg1, arg2, obj));
                        break;
                    case SdPathCleanTask.CLEAN_STATUS:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SD_CLEAN_STATUS, arg1, arg2, obj));
                        break;
                    case SdPathCleanTask.CLEAN_INFO:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SD_CLEAN_INFO, arg1, arg2, obj));
                        break;
				case SdPathCleanTask.CLEAN_FINISH:
					mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SD_CLEAN_FINISH, arg1, arg2, obj));
					break;
                }
            }
        }

        SdCacheCleanCallback cb = new SdCacheCleanCallback();
        sdCacheCleanTask.setCtrlMask(
                sdCacheCleanTask.getCtrlMask() &
                        (~(SdPathCleanTask.CTRL_MASK_CLEAN_FOLDER |
                                SdPathCleanTask.CTRL_MASK_CALC_SIZE)));
        sdCacheCleanTask.bindCleanDataSrc(cb);
        sdCacheCleanTask.bindCallbackObj(cb);
        sdCacheCleanTask.setEngineConfigForCleanTasks(mCfg);
    }

    //回传清理项目
    private void removeDataItemCB(Object removedJunkObject) {
        if (null != removedJunkObject && null != mDataMgr) {
            if (removedJunkObject instanceof BaseJunkBean) {
                int nType = CleanRequest.CLEAN_TYPE_DETAIL;
                if (null != mCleanRequest) {
                    nType = mCleanRequest.getCleanType();
                }
                mDataMgr.removeDataItem((BaseJunkBean)removedJunkObject, nType);
            }
        }
        if(mCleanRequest !=null){
            CleanRequest.ICleanCallback callBack = mCleanRequest.getCleanCallback();
            if(callBack !=null){
                if (removedJunkObject instanceof BaseJunkBean) {
                    long nSize = ((BaseJunkBean)removedJunkObject).getSize();
                    callBack.onCleanItemSize(((BaseJunkBean)removedJunkObject).getJunkDataType(), nSize);
                    callBack.onCleanItem((BaseJunkBean)removedJunkObject);
                }
            }
        }
    }

    /**
     * 回传清理子任务完成状态
     * @param msg
     */
    private void removeSubTaskItem(int msg){
        CleanRequest.ICleanCallback callBack = mCleanRequest.getCleanCallback();
        if(callBack != null){
            callBack.onSubCleanTaskFinish(msg);
        }
    }

    /**
     * rubbish 清理task
     */
    private void cfgRubbishPathTask(Map<EM_JUNK_DATA_TYPE, List<BaseJunkBean>> mapCleanItem){
        if(mapCleanItem == null || mapCleanItem.isEmpty()){
            return;
        }

        String strTaskName = EnumCleanTask.RUBBISH_STD.toString();
        Queue<BaseJunkBean> rubbishItemList = new LinkedList<>();

        List<BaseJunkBean> itemList = mapCleanItem.get(EM_JUNK_DATA_TYPE.ADVERTISEMENT);
        if (null != itemList && !itemList.isEmpty()) {
            //add by xiangxiang.liu 2016/05/28 19:09 start
            //fix bug 清理垃圾时不勾选无用安装包后清理，清理完成后再次扫描垃圾，无法扫描到无用安装包
            NLog.d(AdvFolderScanTask.TAG, "开始筛选广告数据");
            removeUncheckedData(itemList);
            //add by xiangxiang.liu 2016/05/28 19:09 end
            rubbishItemList.addAll(itemList);

        }
        //临时文件
        itemList = mapCleanItem.get(EM_JUNK_DATA_TYPE.TEMPFOLDER);
        if (null != itemList && !itemList.isEmpty()) {
            //add by xiangxiang.liu 2016/05/28 19:09 start
            //fix bug 清理垃圾时不勾选无临时文件后清理，清理完成后再次扫描垃圾，无法扫描到临时文件
            NLog.d(AdvFolderScanTask.TAG, "开始筛选临时文件数据");
            removeUncheckedData(itemList);
            //add by xiangxiang.liu 2016/05/28 19:09 end
            rubbishItemList.addAll(itemList);
        }

        itemList = mapCleanItem.get(EM_JUNK_DATA_TYPE.BIGFILE);
        if (null != itemList && !itemList.isEmpty()) {
            for (BaseJunkBean info : itemList) {
                if (info instanceof SDcardRubbishResult) {
                    info.setJunkInfoType(EM_JUNK_DATA_TYPE.BIGFILE);
                    rubbishItemList.add(info);
                }
            }
        }
        //残留
        itemList = mapCleanItem.get(EM_JUNK_DATA_TYPE.APPLEFTOVER);
        if (null != itemList && !itemList.isEmpty()) {

            //add by xiangxiang.liu 2016/05/28 19:09 start
            //fix bug 清理垃圾时不勾选残留文件后清理，清理完成后再次扫描垃圾，无法扫描到残留文件
            NLog.d(AdvFolderScanTask.TAG, "开始筛选残留文件数据");
            removeUncheckedData(itemList);
            //add by xiangxiang.liu 2016/05/28 19:09 end

            rubbishItemList.addAll(itemList);
        }

        itemList = mapCleanItem.get(EM_JUNK_DATA_TYPE.USELESSTHUMBNAIL);
        if (null != itemList && !itemList.isEmpty()) {
            rubbishItemList.addAll(itemList);
        }

        itemList = mapCleanItem.get(EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV);
        if (null != itemList && !itemList.isEmpty()) {
            strTaskName = EnumCleanTask.RUBBISH_ADV.toString();
            rubbishItemList.addAll(itemList);
        }

        itemList = mapCleanItem.get(EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV);
        if (null != itemList && !itemList.isEmpty()) {
            strTaskName = EnumCleanTask.RUBBISH_ADV.toString();
            rubbishItemList.addAll(itemList);
        }

        if (rubbishItemList.isEmpty()) {
            return;
        }

        SdPathCleanTask rubbishCleanTask = new SdPathCleanTask(strTaskName, mVisibleFolderMap, true);
        setRubWhiteList();
        rubbishCleanTask.setWhiteList(mRubFileWhiteList, mRubFolderWhiteList);
        cfgRubbishCleanTaskForList(rubbishCleanTask, rubbishItemList);
        mTaskExecutorsForClean.pushTask(rubbishCleanTask);
    }

    /**
     * 清除未选中的数据
     * @param itemList
     */
    private void removeUncheckedData(List<BaseJunkBean> itemList){
        List<BaseJunkBean> removeList = new ArrayList<>();
        for (BaseJunkBean junkBean: itemList){
           /* if (junkBean instanceof SDcardRubbishResult){
                SDcardRubbishResult advResult = (SDcardRubbishResult) junkBean;
                if (!advResult.isCheck()){
                    removeList.add(junkBean);
                }
            }*/
            if (!junkBean.isCheck()){
                removeList.add(junkBean);
            }
        }
        if (removeList.size() > 0){
            itemList.removeAll(removeList);
        }
        for (BaseJunkBean junkBean: itemList){
            if (junkBean instanceof SDcardRubbishResult){
                SDcardRubbishResult advResult = (SDcardRubbishResult) junkBean;
                NLog.d(AdvFolderScanTask.TAG, "将要清理的垃圾: apkName = "+advResult.getApkName() +", chineseName = "+advResult.getChineseName()
                        +", isChecked = "+ advResult.isCheck()+", strDirPath = "+ advResult.getStrDirPath() +", pathList = "+advResult.getPathList() );
            }else if (junkBean instanceof CacheInfo){
                CacheInfo cacheInfo = (CacheInfo) junkBean;
                NLog.d(SysCacheCleanTask.TAG, "将要清理的缓存: apkName = "+cacheInfo.getPackageName()
                        +", isChecked = "+ cacheInfo.isCheck()+", strDirPath = "+ cacheInfo.getFilePath() +", size = "+cacheInfo.getSize());
            }else if (junkBean instanceof APKModel){
                APKModel advResult = (APKModel) junkBean;
                NLog.d(AdvFolderScanTask.TAG, "将要清理的无用apk: title = %s,isChecked = %b, path = %s, size = %d", advResult.getTitle(),advResult.isCheck(), advResult.getPath(), advResult.getSize());
            }
        }
    }





    private void setRubWhiteList() {
        mRubFolderWhiteList.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + File.separator);
        mRubFileWhiteList.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + File.separator);
    }

    private void cfgRubbishCleanTaskForList(final SdPathCleanTask rubbishCleanTask, final Queue<BaseJunkBean> rubbishItemList) {

        assert(null != rubbishCleanTask && null != rubbishItemList && (!rubbishItemList.isEmpty()));

        final Queue<JunkCleanItemInfo> rubbishCleanPath = new LinkedList<JunkCleanItemInfo>();
        JunkDataManager.getRubbishPath(rubbishCleanPath, rubbishItemList);

        final EmergencyFalseSignFilter emFalseSignFilter = CleanCloudManager.createEmergencyFalseSignFilter(EmergencyFalseSignFilter.FilterType.RESIDUAL_DIR);

        class RubbishCleanCallback extends SdPathCleanTask.CleanDataSrcBase implements ScanTaskCallback {

            @Override
            public SdPathCleanTask.DelPathInfo getNextCleanPathInfo() {
                JunkCleanItemInfo info = null;
                BaseJunkBean item = null;
                while (true) {
                    info = rubbishCleanPath.poll();
                    if (null == info) {
                        return null;
                    }

                    item = info.getJunkItem();
                    if (null != item) {
                        if (item instanceof SDcardRubbishResult) {
                            SDcardRubbishResult rubResult = (SDcardRubbishResult)item;
                            if (!emFalseSignFilter.filter(rubResult.getSignId())) {
                                break;
                            }
                        }
                    }
                }

                List<String> nowPath = info.getPathList();
                int mCleanFileFlag = info.getCleanFileFlag();

                if (item.getJunkType() == BaseJunkBean.JUNK_SD_RUBBISH &&
                        ((SDcardRubbishResult)item).getType() == SDcardRubbishResult.RF_TEMPFILES &&
                        ResUtil.getString(R.string.junk_tag_RF_ObsoleteImageThumbnails).equals(item.getName())) {

                    if (null == mMSImageMediaIdList) {
                        mMSImageMediaIdList = ((SDcardRubbishResult)item).getMSImageMediaIdList();
                    }
                    if (null == mMSImageThumbIdList) {
                        mMSImageThumbIdList = ((SDcardRubbishResult)item).getMSImageThumbIdList();
                    }
                }

                if (item.getJunkType() == BaseJunkBean.JUNK_SD_RUBBISH &&
                        ((SDcardRubbishResult)item).getType() == SDcardRubbishResult.RF_TEMPFILES &&
                        ResUtil.getString(R.string.junk_tag_RF_EmptyFolders).equals(item.getName())) {
                    return new SdPathCleanTask.DelPathInfo(rubbishCleanTask.getCtrlMask() &
                            (~(SdPathCleanTask.CTRL_MASK_CALC_SIZE | SdPathCleanTask.CTRL_MASK_CLEAN_FILES)),
                            nowPath, info, 0, item.getFileType(), item.getSize() );
                } else {
                    NLog.d(TAG, "开始清理 nowPath---->  = &s", nowPath);
                    return new SdPathCleanTask.DelPathInfo(nowPath, info, mCleanFileFlag, item.getFileType(), item.getSize());
                }
            }

            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                switch (what) {
                    case SdPathCleanTask.CLEAN_ITEM:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_RUB_CLEAN_ITEM, arg1, arg2, obj));
                        break;
                    case SdPathCleanTask.CLEAN_STATUS:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_RUB_CLEAN_STATUS, arg1, arg2, obj));
                        break;
                    case SdPathCleanTask.CLEAN_INFO:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_RUB_CLEAN_INFO, arg1, arg2, obj));
                        break;
				case SdPathCleanTask.CLEAN_FINISH:
					mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_RUB_CLEAN_FINISH, arg1, arg2, obj));
					break;
                }
            }
        }

        RubbishCleanCallback cb = new RubbishCleanCallback();
        rubbishCleanTask.bindCleanDataSrc(cb);
        rubbishCleanTask.bindCallbackObj(cb);
        rubbishCleanTask.setEngineConfigForCleanTasks(mCfg);
    }

    public static void getRubbishPath(Queue<JunkCleanItemInfo> rst,
                                      Collection<BaseJunkBean> rubbishList) {

        Iterator<BaseJunkBean> iter = rubbishList.iterator();
        if (null == iter) {
            return;
        }

        String path = null;
        SDcardRubbishResult item = null;
        int cleanFileFlag = 0;
        BaseJunkBean itemBase = null;
        Context mCtxContext = SpaceApplication.getInstance().getContext();
        while (iter.hasNext()) {
            itemBase = iter.next();
            if (null == itemBase) {
                continue;
            }

            item = (SDcardRubbishResult) itemBase;

            if (item.getPathList().isEmpty()) {
                path = item.getStrDirPath();
                cleanFileFlag = item.getCleanFileFlag();
                if (!TextUtils.isEmpty(path)) {
                    rst.offer(new JunkCleanItemInfo(path, cleanFileFlag, item));
                }
            } else {

                List<String> pathList = new ArrayList<String>();

                Iterator<SDcardRubbishResult.PathInfo> iterPath = item.getPathInfoList().iterator();
                if (null != iterPath) {
                    path = null;
                    boolean isFirstPath = true;
                    while (iterPath.hasNext()) {
                        SDcardRubbishResult.PathInfo pathInfoNext = iterPath.next();
                        path = pathInfoNext.getPath();
                        cleanFileFlag = pathInfoNext.getCleanFileFlag();
                        if (!TextUtils.isEmpty(path)) {

                            //Thumbnail
                            if (item.getJunkType() == BaseJunkBean.JUNK_SD_RUBBISH &&
                                    ((SDcardRubbishResult)item).getType() == SDcardRubbishResult.RF_TEMPFILES &&
                                    mCtxContext.getResources().getString(
                                            R.string.junk_tag_RF_ObsoleteImageThumbnails).equals(item.getName())) {
                                pathList.add(path);
                            }

                            //LogFile
                            else if (item.getJunkType() == BaseJunkBean.JUNK_SD_RUBBISH &&
                                    ((SDcardRubbishResult)item).getType() == SDcardRubbishResult.RF_TEMPFILES &&
                                    mCtxContext.getResources().getString(
                                            R.string.junk_tag_RF_LogFiles).equals(item.getName())) {
                                pathList.add(path);
                            } else {
                                JunkCleanItemInfo itemInfo = new JunkCleanItemInfo(path, cleanFileFlag, item);
                                if (isFirstPath) {
                                    isFirstPath = false;
                                } else {
                                    itemInfo.setIsSubItem(true);
                                }
                                rst.offer(itemInfo);
                            }
                        }
                    }

                    if (!pathList.isEmpty()) {
                        JunkCleanItemInfo itemInfo = new JunkCleanItemInfo(pathList, cleanFileFlag, item);
                        rst.offer(itemInfo);
                    }

                }
            }
        }
    }

    /**
     * apk path 配置task
     * @param mapCleanItem
     */
    private void cfgApkPathTask(Map<EM_JUNK_DATA_TYPE, List<BaseJunkBean>> mapCleanItem){
        if (null == mapCleanItem || mapCleanItem.isEmpty() ||
                null == mapCleanItem.get(EM_JUNK_DATA_TYPE.APKFILE)) {
            return;
        }

        Queue<BaseJunkBean> apkItemList = new LinkedList<BaseJunkBean>();
        List<BaseJunkBean> itemList = mapCleanItem.get(EM_JUNK_DATA_TYPE.APKFILE);
        if (null != itemList) {
            //add by xiangxiang.liu 2016/05/28 19:09 start
            removeUncheckedData(itemList);
            //add by xiangxiang.liu 2016/05/28 19:09 end
            apkItemList.addAll(itemList);
        }
        if (apkItemList.isEmpty()) {
            return;
        }

        SdPathCleanTask apkCleanTask = new SdPathCleanTask(isAdvancedClean()? EnumCleanTask.APK_ADVSTD.toString(): EnumCleanTask.APK_STD.toString(), mVisibleFolderMap, true);
        apkCleanTask.setWhiteList(mFileWhiteList, mFolderWhiteList);
        cfgAPKCleanTaskForList(apkCleanTask, apkItemList);
        mTaskExecutorsForClean.pushTask(apkCleanTask);
    }

    private void cfgAPKCleanTaskForList(final SdPathCleanTask apkCleanTask, final Queue<BaseJunkBean> apkItemList){
        assert(null != apkCleanTask && null != apkItemList && (!apkItemList.isEmpty()));

        class APKCleanCallback extends SdPathCleanTask.CleanDataSrcBase implements ScanTaskCallback {

            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                switch (what) {
                    case SdPathCleanTask.CLEAN_ITEM:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_APK_CLEAN_ITEM, arg1, arg2, obj));
                        break;
                    case SdPathCleanTask.CLEAN_STATUS:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_APK_CLEAN_STATUS, arg1, arg2, obj));
                        break;
                    case SdPathCleanTask.CLEAN_INFO:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_APK_CLEAN_INFO, arg1, arg2, obj));
                        break;
				case SdPathCleanTask.CLEAN_FINISH:
					mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_APK_CLEAN_FINISH, arg1, arg2, obj));
					break;
                }
            }

            @Override
            public SdPathCleanTask.DelPathInfo getNextCleanPathInfo() {
                APKModel apkModel = (APKModel)apkItemList.poll();

                if(apkModel==null)
                {
                    return null;
                }

                return new SdPathCleanTask.DelPathInfo(apkModel.getPath(), apkModel, 0, BaseJunkBean.FileType.File, apkModel.getSize());
            }
        }

        APKCleanCallback cb = new APKCleanCallback();
        apkCleanTask.bindCleanDataSrc(cb);
        apkCleanTask.bindCallbackObj(cb);
    }

    private void cfgRootCachePathTask(Map<EM_JUNK_DATA_TYPE, List<BaseJunkBean>> mapCleanItem){
        if (null == mapCleanItem || mapCleanItem.isEmpty() ||
                null == mapCleanItem.get(EM_JUNK_DATA_TYPE.ROOTCACHE)) {
            return;
        }

        Queue<BaseJunkBean> rootCacheItemList = new LinkedList<BaseJunkBean>();
        List<BaseJunkBean> itemList = mapCleanItem.get(EM_JUNK_DATA_TYPE.ROOTCACHE);
        rootCacheItemList.addAll(itemList);

        if (rootCacheItemList.isEmpty()) {
            return;
        }

        SdPathCleanTask rootCacheCleanTask = new SdPathCleanTask(EnumCleanTask.ROOTCACHE_STD.toString());
        cfgRootCacheCleanTaskForList(rootCacheCleanTask, rootCacheItemList);
        mTaskExecutorsForClean.pushTask(rootCacheCleanTask);
    }

    private void cfgRootCacheCleanTaskForList(final SdPathCleanTask rootCacheCleanTask, final Queue<BaseJunkBean> rootCacheItemList){

        assert(null != rootCacheCleanTask && null != rootCacheItemList && (!rootCacheItemList.isEmpty()));

        final Queue<JunkCleanItemInfo> rootCacheCleanPath = new LinkedList<JunkCleanItemInfo>();
        JunkDataManager.getRootCacheListPathQueue(rootCacheItemList, rootCacheCleanPath);

        class RootCacheCleanCallback extends SdPathCleanTask.CleanDataSrcBase implements ScanTaskCallback {

            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                switch (what) {
                    case SdPathCleanTask.CLEAN_ITEM:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_ROOT_CACHE_CLEAN_ITEM, arg1, arg2, obj));
                        break;
                    case SdPathCleanTask.CLEAN_FINISH:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_ROOT_CACHE_CLEAN_FINISH, arg1, arg2, obj));
                        break;
                }
            }

            @Override
            public SdPathCleanTask.DelPathInfo getNextCleanPathInfo() {
                JunkCleanItemInfo info = rootCacheCleanPath.poll();
                if (null == info) {
                    return null;
                }

                BaseJunkBean item = info.getJunkItem();
                RootCacheInfo cacheItem = ((RootCacheInfo)item);
                return new SdPathCleanTask.DelPathInfo(cacheItem.getPath(),info);
            }

        }

        RootCacheCleanCallback cb = new RootCacheCleanCallback();
        rootCacheCleanTask.bindCleanDataSrc(cb);
        rootCacheCleanTask.bindCallbackObj(cb);
    }

    private void cfgApkScanTask(ApkScanTask apkScanTask, boolean bScanTempFolder) {

        if (null == apkScanTask) {
            return;
        }

        apkScanTask.setScanSwitch(ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).getApkJunkScanSwitch());
        apkScanTask.setScanConfigMask( (bScanTempFolder ? (ApkScanTask.SCAN_CFG_SCAN_LOG_FILE | ApkScanTask.SCAN_CFG_SCAN_TMP_FILE) : 0)
                | ApkScanTask.SCAN_CFG_SCAN_N7PLARYER | ApkScanTask.SCAN_CFG_SCAN_POWERAMP
				/*| ApkScanTask.SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS */| ApkScanTask.SCAN_CFG_MASK_NOT_RETURN_IGNORE);
//        CleanerDataCenter.getInstance().setAPKPackagesData(null);
        apkScanTask.setApkScanFolderLevel(4);
        apkScanTask.setUseCompoundScan(true);
        apkScanTask.setAutoFilterBackup(true);
        apkScanTask.setFilterProbablyUserFolderFlag(true);
        apkScanTask.setShowAllApk(true);
        apkScanTask.setCheckUninstallApkModifyOuttime(1L * 24L * 60L * 60L * 1000L);	// 一天

        //todo wufeng 开启锁定状态查询开关
        apkScanTask.setCheckLocked(true);

        apkScanTask.bindCallbackObj(new ScanTaskCallback() {
            private ProgressControl mPC = new ProgressControl(new ProgressControlCallbackAgent(), 0);

            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                //wufeng
                switch (what) {
                    case ApkScanTask.HANDLER_ADD_PROGRESS://添加进度条
                        mPC.addStep();
                        break;
                    case ApkScanTask.HANDLER_APK_DIR_SHOW://显示扫描路径
                        mDataMgr.notifyCurrentScanItem(EM_JUNK_DATA_TYPE.APKFILE, (String)obj);
                        break;

                    case ApkScanTask.HANDLER_APK_SCAN_UPDATE:// 添加
                        if(mStopFlag){
                            return;
                        }else{
                            //handlerAPKItem((APKModel)pkgName);
                            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FOUND_APK_ITEM, obj));
                        }
                        break;
                    case ApkScanTask.HANDLER_SATRT_TASK:
                        mPC.startControl(mProgBarTotal, PROG_BAR_APK_FILE, true);
                        //mPC.setStepNum(arg1);
                        break;
                    case ApkScanTask.CB_TYPE_SCAN_FINISH: // 扫描结束
                        Analytics.endTask(ApkScanTask.class);
                        mPC.stopControl();
                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_APK_SCAN);
//					Log.e("FFFFFFFFFFF", "---------ApkScanTask.CB_TYPE_SCAN_FINISH---------");
                        break;
                    case ApkScanTask.HANDLER_FIND_MEDIASTORE_ITEM:
                        if(mStopFlag){
                            return;
                        }else{
                            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FOUND_RUBBISH_ITEM, arg1, arg2, obj));
                        }
                        break;
                    case ApkScanTask.HANDLER_UPDATE_MEDIASTORE_ITEM:
                        mMsgHandler.sendMessage(
                                mMsgHandler.obtainMessage(
                                        JunkEngineMsg.MSG_HANDLER_UPDATE_RUBBISH_ITEM,
                                        arg1, arg2,
                                        obj));
                        break;
                }
            }
        });
    }

//    private void cfgScreenShotsScanTask(ScreenShotsCompressScanTask task) {
//        task.bindCallbackObj(new ScanTaskCallback() {
//            private ProgressControl mPC = new ProgressControl(new ProgressControlCallbackAgent(), 0);
//
//            @Override
//            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
//                switch (what) {
//                    case ScreenShotsCompressScanTask.SCREEN_SHOTS_COMPRESS_SCAN_FOUND_ITEM:// 添加
//                        if (mStopFlag) {
//                            return;
//                        }
//                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FOUND_SCREENSHOTSCOMPRESS_ITEM, obj));
//                        break;
//                    case ScreenShotsCompressScanTask.SCREEN_SHOTS_COMPRESS_SCAN_START:
//                        mPC.startControl(mProgBarTotal, PROG_BAR_SCREENSHOTSCOMPRESSSCAN, true);
//                        mPC.setStepNum(PROG_BAR_SCREENSHOTSCOMPRESSSCAN);
//                        break;
//                    case ScreenShotsCompressScanTask.SCREEN_SHOTS_COMPRESS_SCAN_FINISH: // 扫描结束
//                        mPC.addStep();
//                        mPC.stopControl();
//                        mMsgHandler.sendEmptyMessage(JunkEngineMsg.MSG_HANDLER_FINISH_SCREENSHOTSCOMPRESSSCAN);
//                        break;
//                }
//            }
//        });
//    }
    private void cfgSysFixedCachePathTask(Map<EM_JUNK_DATA_TYPE, List<BaseJunkBean>> mapCleanItem) {
        if (null == mapCleanItem || mapCleanItem.isEmpty() ||
                null == mapCleanItem.get(EM_JUNK_DATA_TYPE.SYSFIXEDCACHE)) {
            return;
        }

        Queue<BaseJunkBean> sdCacheItemList = new LinkedList<BaseJunkBean>();
        List<BaseJunkBean> itemList = mapCleanItem.get(EM_JUNK_DATA_TYPE.SYSFIXEDCACHE);
        sdCacheItemList.addAll(itemList);

        if (sdCacheItemList.isEmpty()) {
            return;
        }

        SdPathCleanTask sysFixedCacheCleanTask = new SdPathCleanTask(EnumCleanTask.SYSFIXEDCHE_STD.toString());
        cfgSysFixedCacheCleanTaskForList(sysFixedCacheCleanTask, sdCacheItemList);
        sysFixedCacheCleanTask.setFeedbackList(mOnCleanFeedbackListFile, mOnCleanFeedbackListFolder);
        mTaskExecutorsForClean.pushTask(sysFixedCacheCleanTask);
    }

    private void cfgSysFixedCacheCleanTaskForList(final SdPathCleanTask sdCacheCleanTask, final Queue<BaseJunkBean> sdCacheItemList) {

        assert(null != sdCacheCleanTask && null != sdCacheItemList && (!sdCacheItemList.isEmpty()));

        final Queue<JunkCleanItemInfo> sdCacheCleanPath = new LinkedList<JunkCleanItemInfo>();
        JunkDataManager.getSdCardCacheListPathQueue(sdCacheItemList, sdCacheCleanPath);

        final int oldMask = sdCacheCleanTask.getCtrlMask();
        class SdCacheCleanCallback extends SdPathCleanTask.CleanDataSrcBase implements ScanTaskCallback {

            @Override
            public DelPathInfo getNextCleanPathInfo() {
                JunkCleanItemInfo info = sdCacheCleanPath.poll();
                if (null == info) {
                    return null;
                }

                BaseJunkBean item = info.getJunkItem();
                CacheInfo cacheItem = ((CacheInfo)item);
                int cleanFileFlag = info.getCleanFileFlag();

                boolean isRP = mRPList.contains(cacheItem.getPackageName());

                int mask = oldMask & (~SdPathCleanTask.CTRL_MASK_CALC_SIZE);
                if (cacheItem.getDeleteType() == 0  || isRP) {
                    mask = mask & (~(SdPathCleanTask.CTRL_MASK_CLEAN_FOLDER | SdPathCleanTask.CTRL_MASK_CLEAN_TOP_FOLDER));
                    mEmptyFolderList.add(cacheItem.getFilePath());
                }
                return new DelPathInfo(mask, info.getPathList(), info, cleanFileFlag, item.getFileType(), item.getSize());
            }

            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                switch (what) {
                    case SdPathCleanTask.CLEAN_ITEM:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SYS_FIXED_CLEAN_ITEM, arg1, arg2, obj));
                        break;
                    case SdPathCleanTask.CLEAN_FINISH:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SYS_FIXED_CLEAN_FINISH, arg1, arg2, obj));
                        break;
                }
            }
        }

        SdCacheCleanCallback cb = new SdCacheCleanCallback();
        sdCacheCleanTask.setCtrlMask(
                sdCacheCleanTask.getCtrlMask() &
                        (~(SdPathCleanTask.CTRL_MASK_CLEAN_FOLDER |
                                SdPathCleanTask.CTRL_MASK_CALC_SIZE)));
        sdCacheCleanTask.bindCleanDataSrc(cb);
        sdCacheCleanTask.bindCallbackObj(cb);
        sdCacheCleanTask.setEngineConfigForCleanTasks(mCfg);
    }

    private void cfgMediaFilePathTask(Map<EM_JUNK_DATA_TYPE, List<BaseJunkBean>> mapCleanItem) {
        if (null == mapCleanItem || mapCleanItem.isEmpty() ||
                (null == mapCleanItem.get(EM_JUNK_DATA_TYPE.MYPHOTO) && null == mapCleanItem.get(EM_JUNK_DATA_TYPE.MYAUDIO))) {
            return;
        }

        Queue<BaseJunkBean> mediaItemList = new LinkedList<BaseJunkBean>();
        List<BaseJunkBean> itemList = null;
        if (null != mapCleanItem.get(EM_JUNK_DATA_TYPE.MYPHOTO)) {
            itemList = mapCleanItem.get(EM_JUNK_DATA_TYPE.MYPHOTO);
        }
        if (null != mapCleanItem.get(EM_JUNK_DATA_TYPE.MYAUDIO)) {
            itemList = mapCleanItem.get(EM_JUNK_DATA_TYPE.MYAUDIO);
        }

        if (null != itemList) {
            mediaItemList.addAll(itemList);
        }
        if (mediaItemList.isEmpty()) {
            return;
        }

        SdPathCleanTask mediaCleanTask = new SdPathCleanTask(EnumCleanTask.MEDIA_ADV.toString(), mVisibleFolderMap, true);
        cfgMediaFileCleanTaskForList(mediaCleanTask, mediaItemList);
        mTaskExecutorsForClean.pushTask(mediaCleanTask);
    }

    private void cfgMediaFileCleanTaskForList(final SdPathCleanTask mediaCleanTask, final Queue<BaseJunkBean> mediaItemList) {

        assert(null != mediaCleanTask && null != mediaItemList && (!mediaItemList.isEmpty()));

        class MediaCleanCallback extends SdPathCleanTask.CleanDataSrcBase implements ScanTaskCallback {

            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                switch (what) {
                    case SdPathCleanTask.CLEAN_ITEM:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_MEDIA_CLEAN_ITEM, arg1, arg2, obj));
                        break;
                    case SdPathCleanTask.CLEAN_STATUS:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_MEDIA_CLEAN_STATUS, arg1, arg2, obj));
                        break;
                    case SdPathCleanTask.CLEAN_INFO:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_MEDIA_CLEAN_INFO, arg1, arg2, obj));
                        break;
                }
            }

            @Override
            public DelPathInfo getNextCleanPathInfo() {
                MediaFile mediaFile = (MediaFile)mediaItemList.poll();

                if(mediaFile==null)
                {
                    return null;
                }

                String path = mediaFile.getPath();
                int type = mediaFile.getMediaType();

                if (type == MediaFile.MEDIA_TYPE_IMAGE) {
                    if (mImgBuffer.length() != 0) {
                        mImgBuffer.append(" or ");
                    }
                    mImgBuffer.append(mWhere);
                    mImgs.add(path);
                } else if (type == MediaFile.MEDIA_TYPE_VIDEO) {
                    if (mVideoBuffer.length() != 0) {
                        mVideoBuffer.append(" or ");
                    }
                    mVideoBuffer.append(mWhere);
                    mVideoS.add(path);
                }

                return new DelPathInfo(mediaFile.getPath(), mediaFile, 0, BaseJunkBean.FileType.File, mediaFile.getSize());
            }
        }

        MediaCleanCallback cb = new MediaCleanCallback();
        mediaCleanTask.bindCleanDataSrc(cb);
        mediaCleanTask.bindCallbackObj(cb);
        mediaCleanTask.setEngineConfigForCleanTasks(mCfg);
    }

    //回传path
    private void removeStatusCB(Object removedPath) {
        if(mCleanRequest !=null){
            CleanRequest.ICleanCallback callBack= mCleanRequest.getCleanCallback();
            if(callBack !=null){
                callBack.onCleaningPath((String)removedPath);
            }
        }
    }

    private void recyclePicsFrom2SdCard() {
//        if (!mRecyleListFor2SdCard.isEmpty()) {
//            List<MyMediaFile> list = new ArrayList<MyMediaFile>();
//            for (final String filePath : mRecyleListFor2SdCard) {
//                MyMediaFile mediaFile = MediaFileGenerator.generateImage(filePath);
//                list.add(mediaFile);
//            }
//            boolean IsStorageInsufficent = SpaceCommonUtils.isSdcardInsufficeint();
//            PicRecycleCache.getInstance().RecyclePicsBySpaceState(null, list, !IsStorageInsufficent, PicRecycleCache.PIC_RECYLE_CALL_FROM_2SD);
//        }
    }

    private void cfgSysCacheCleanTaskForList(
            SysCacheCleanTask sysCacheCleanTask,
            final Queue<BaseJunkBean> sysCacheItemList, final HashMap<String, List<CacheInfo>> containSdCacheMap) {

        assert(null != sysCacheCleanTask && null != sysCacheItemList && (!sysCacheItemList.isEmpty()));
        NLog.d(SysCacheScanTask.TAG, "mbCheckRoot = "+mbCheckRoot +", sysCacheItemList = "+sysCacheItemList);

        if (mbCheckRoot) {
            sysCacheCleanTask.setCtrlMask(
                    sysCacheCleanTask.getCtrlMask() &
                            ~(SysCacheCleanTask.CTRL_MASK_CLEAN_ALL_WITHOUT_ROOT_PRIVACY));
        } else {
            sysCacheCleanTask.setPkgManager(mPM);
        }

        sysCacheCleanTask.setEngineConfigForCleanTasks(mCfg);
        if (null != mDataMgr) {
            sysCacheCleanTask.bindCleanDataSrc(new SysCacheCleanTask.ICleanDataSrc() {

                @Override
                public String getNextPackageName() {
                    CacheInfo item = getNextCacheInfo();
                    if (null == item) {
                        return null;
                    }
                    return item.getPackageName();
                }

                @Override
                public CacheInfo getNextCacheInfo() {
                    NLog.d(SysCacheCleanTask.TAG, "getNextCacheInfo ");
                    CacheInfo cacheInfo = (CacheInfo)sysCacheItemList.poll();
                    return cacheInfo;
                }
            });
        }

        sysCacheCleanTask.bindCallbackObj(new ScanTaskCallback() {
            @Override
            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
                switch (what) {
                    case SysCacheCleanTask.CLEAN_ITEM:
                        if(obj != null){
                            CacheInfo info = (CacheInfo) obj;
                            if(containSdCacheMap != null && !containSdCacheMap.isEmpty()){

                                if(containSdCacheMap.containsKey(info.getPackageName())){
                                    List<CacheInfo> cacheInfos = containSdCacheMap.get(info.getPackageName());
                                    if(cacheInfos != null ){
                                        for(CacheInfo cacheInfo:cacheInfos){
                                            if(cacheInfo == null){
                                                continue;
                                            }
                                            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SD_CLEAN_ITEM, 0, 0,
                                                    new SdPathCleanTask.DelPathResult(0, cacheInfo.getFilePath(), new JunkCleanItemInfo(cacheInfo.getFilePath(), cacheInfo), cacheInfo.getSize(), (int)cacheInfo.getCacheFolderNum(), (int)cacheInfo.getCacheFileNum(), 0, 0, 0 )));

                                            mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SD_CLEAN_INFO, 0, 0,
                                                    new SdPathCleanTask.DelPathResult(0, cacheInfo.getFilePath(), new JunkCleanItemInfo(cacheInfo.getFilePath(), cacheInfo), cacheInfo.getSize(), (int)cacheInfo.getCacheFolderNum(), (int)cacheInfo.getCacheFileNum(), 0, 0, 0 )));
                                        }
                                    }
                                }
                            }
                        }
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SYS_CLEAN_ITEM, arg1, arg2, obj));
                        break;
                    case SysCacheCleanTask.CLEAN_INFO:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SYS_CLEAN_INFO, arg1, arg2, obj));
                        break;
                    case SysCacheCleanTask.CLEAN_FINISH:
                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_SYS_CLEAN_FINISH, arg1, arg2, obj));
                        break;
                }
            }
        });
    }

//    private void cfgScreenShotsCompressTask(Map<EM_JUNK_DATA_TYPE, List<BaseJunkBean>> mapCleanItem) {
//        if (null == mapCleanItem || mapCleanItem.isEmpty()) {
//            return;
//        }
//
//        List<BaseJunkBean> itemList = mapCleanItem.get(EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS);
//        if (null == itemList || itemList.isEmpty()) {
//            return;
//        }
//
//        ScreenShotsCompressTask scrShotsCompressTask = new ScreenShotsCompressTask();
//        cfgScreenShotsCompressTaskDataSrc(scrShotsCompressTask, itemList);
//        mTaskExecutorsForClean.pushTask(scrShotsCompressTask);
//    }

//    private void cfgScreenShotsCompressTaskDataSrc(ScreenShotsCompressTask scrShotsCompressTask, final List<BaseJunkBean> itemList) {
//        if (null == scrShotsCompressTask || null == itemList) {
//            return;
//        }
//
//        class ScrShotsCompressCallback implements  ScreenShotsCompressTask.IScrShotsCompressDataSrc , ScanTaskCallback {
//            @Override
//            public void callbackMessage(int what, int arg1, int arg2, Object obj) {
//                switch (what) {
//                    case ScreenShotsCompressTask.COMPRESS_FINISH:
//                        mMsgHandler.sendMessage(mMsgHandler.obtainMessage(JunkEngineMsg.MSG_HANDLER_FINISH_COMPRESS_SCRSHOTS, arg1, arg2, obj));
//                        break;
//                }
//            }
//
//            @Override
//            public MediaFile getNextCompressFile() {
//                if (mIter.hasNext()) {
//                    return (MediaFile)(mIter.next());
//                }
//                return null;
//            }
//
//            Iterator<BaseJunkBean> mIter = itemList.iterator();
//        }
//
//        ScrShotsCompressCallback cb = new ScrShotsCompressCallback();
//        scrShotsCompressTask.bindCompressDataSrc(cb);
//        scrShotsCompressTask.bindCallbackObj(cb);
//    }

    public void destroy(){
        if(mJunkThread !=null){
            synchronized ( mMutexForBGThread ) {
                if(mJunkThread !=null) {
                    try {
                        mbIsMsgThreadQuit = true;
                        mJunkThread.quit();
                    } catch (Exception e) {
                    }

                    mJunkThread = null;
                }
            }
        }
    }

    public static final String ENGINE_TIME = "ScanEngine";


    public void setmEngineStatus(EM_ENGINE_STATUS mEngineStatus) {
        this.mEngineStatus = mEngineStatus;
    }

    public void setDataManagerCacheDisable(boolean value){
        if(mDataMgr!=null){
            mDataMgr.setDisableCache(value);
        }
    }

    void endAnalytics() {
        Analytics analytics = Analytics.getInstance();
        int length = Analytics.TASKLIST.length;
        long taskTime = 0L;
        long dbTime = 0L;
        for (int i = 0; i < length; i++) {
            taskTime = analytics.getTaskTime(i);
            dbTime = analytics.getDBTime(i);
        }
        scanTime = TimingUtil.end(ENGINE_TIME);
    }

    public long getTotalScanTime(){
        return scanTime;
    }
}

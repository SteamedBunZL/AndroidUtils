package com.clean.spaceplus.cleansdk.junk.engine.junk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.util.Pair;

import com.clean.spaceplus.cleansdk.R;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.scan.ExtraAndroidFileScanner;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.junk.engine.WhiteListsWrapper;
import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkCleanItemInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFile;
import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFileList;
import com.clean.spaceplus.cleansdk.junk.engine.bean.RootCacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.SDcardRubbishResult;
import com.clean.spaceplus.cleansdk.junk.engine.task.CalcSizeInfoTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.RubbishFileScanTask;
import com.clean.spaceplus.cleansdk.util.ResUtil;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liangni
 * @Description:垃圾清理数据管理类
 * @date 2016/4/26 20:06
 * @copyright TCL-MIG
 */
public class JunkDataManager {

    private final static String TAG=JunkDataManager.class.getSimpleName();
    private boolean mStopFlag = false;
    private boolean mbScanFinish = false;
    private boolean mCompletedScan = true;
    public static final int TASK_TYPE_SYS_CACHE = 1;
    public static final int TASK_TYPE_RUBBISH = 4;
    public static final int TASK_TYPE_APK = 8;
    //	public static final int TASK_TYPE_PROCESS = 16;
    public static final int TASK_TYPE_THUMBNAIL = 32;
    public static final int TASK_TYPE_TMP_FILE = 64;
    public static final int TASK_TYPE_LOG_FILE = 128;
    public static final int TASK_TYPE_SYS_FIXED_CACHE = 256;
    public static final int TASK_TYPE_PHOTO = 512;
    public static final int TASK_TYPE_AUDIO = 1024;
    public static final int TASK_TYPE_CALC_SIZE = 2048;
    public static final int TASK_TYPE_BIGFILE = 4096;
    public static final int TASK_TYPE_VIDEO_OFFLINE = 8192;
    public static final int TASK_TYPE_ROOT_CACHE = 16384;
    public static final int TASK_TYPE_SCRSHOTSCOMPRESS = 32768;
    public static final int TASK_TYPE_VIDEO = 65536;
    public static final int CACHEDATASCANTASK_MSG_FINISH = 0;
    public static final int CACHEDATASCANTASK_MSG_PATH = 1;

    private ArrayList<BaseJunkBean> mPhotoListAsBase = new ArrayList<>();
    private ArrayList<BaseJunkBean> mAudioListAsBase = new ArrayList<>();
    private ArrayList<BaseJunkBean> mCalcFolderList = new ArrayList<>();
    private ArrayList<BaseJunkBean> mMarkCleanItemList = new ArrayList<>();
    private ArrayList<BaseJunkBean> mVideoOfflineList = new ArrayList<>();
    private ArrayList<BaseJunkBean> mScreenShotsCompressList = new ArrayList<>();
    private ArrayList<MediaFile> mAudioList = new ArrayList<MediaFile>();

    private List<String> mApkPathList = new ArrayList<String>();

    /**
     * Engine分别设置 VIDEO_MANUL 和 全部清理状态
     * DataManager设置未发现状态
     */
    public static final int VIDEO_MANUL = 1;
    public static final int VIDEO_NOT_FOUND = 2;
    public static final int VIDEO_ALL_CLEANED = 3;
    private static int mVideoCardState = VIDEO_MANUL;

    public static class JunkRequestData {
        public ArrayList<BaseJunkBean> mScanResultList;
        public JunkRequest mJunkRequest;
    }

    private Map<JunkRequest.EM_JUNK_DATA_TYPE, JunkRequestData> mAvailableDataMap =
            new ConcurrentHashMap<>();

    public void reset() {
        mbScanFinish = false;
        mStopFlag = false;
        mCompletedScan = true;
    }

    public static class JunkCachedDataInfo {
        public ArrayList<BaseJunkBean> mScanResultList;
        public long mRecordTime;

        public JunkCachedDataInfo(ArrayList<BaseJunkBean> list) {
            mScanResultList = list;
            mRecordTime = System.currentTimeMillis();
        }
    }

    private boolean replaceItem(List<BaseJunkBean> rubbishFileList,
                                BaseJunkBean oldObj, BaseJunkBean newObj) {

        // SDcardRubbishResult temp = null;
        for (int i = 0; i < rubbishFileList.size(); ++i) {
            if (rubbishFileList.get(i) == oldObj) {
                rubbishFileList.set(i, newObj);
                return true;
            }
        }

        rubbishFileList.add(newObj);

        return true;
    }

    public boolean isFinishedSysCacheScan() {
        return (0 == (TASK_TYPE_SYS_CACHE & mActiveTaskMask));
    }

    public void updateDataItem(BaseJunkBean item) {
        if (null == item) {
            return;
        }

        JunkRequest.EM_JUNK_DATA_TYPE type = item.getJunkDataType();

        JunkRequestData tmpData = mAvailableDataMap.get(type);

        if (null == tmpData) {
            return;
        }

        List<BaseJunkBean> dataList = tmpData.mScanResultList;
        if (null == dataList) {
            return;
        }

        int idx = dataList.indexOf(item);
        if (idx != -1) {
            dataList.get(idx).setSize(item.getSize());
        }
    }

    public void onUpdateItem(int type, RubbishFileScanTask.UpdateChildrenData info) {
        if (null == info) {
            return;
        }

        if (info.oldObj == info.newObj) {
            return;
        }
        if (onFoundItemIsInvalid(info.newObj.getSize())) {
            return;
        }

        JunkRequest.EM_JUNK_DATA_TYPE junkType = JunkRequest.EM_JUNK_DATA_TYPE.UNKNOWN;
        switch (type) {
            case SDcardRubbishResult.RF_APP_LEFTOVERS:

                if (info.newObj.getScanType() == BaseJunkBean.SCAN_TYPE_STANDARD) {
                    junkType = JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER;
                    if (replaceItem(mRubbishFileListForAppLeftovers, info.oldObj,
                            info.newObj)) {
                    }
                } else {
                    junkType = JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV;
                    if (replaceItem(mRubbishFileListForAppLeftoversAdv, info.oldObj,
                            info.newObj)) {
                    }
                }
                break;
            case SDcardRubbishResult.RF_TEMPFILES: {
                List<BaseJunkBean> rubbishFileList = null;
                if (null != info.newObj.getStrDirPath()
                        && info.newObj.getStrDirPath()
                        .equals(WhiteListsWrapper.FUNCTION_FILTER_NAME_OBSOLETE_THUMBNAIL_SCAN)) {
                    junkType = JunkRequest.EM_JUNK_DATA_TYPE.USELESSTHUMBNAIL;
                    rubbishFileList = mUselessThumbnailList;
                } else {
                    if (info.newObj.getScanType() == BaseJunkBean.SCAN_TYPE_STANDARD) {
                        junkType = JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER;
                        rubbishFileList = mRubbishFileListForTempFiles;
                    } else {
                        junkType = JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV;
                        rubbishFileList = mRubbishFileListForTempFilesAdv;
                    }
                }
                if (replaceItem(rubbishFileList, info.oldObj,
                        info.newObj)) {
                    // todo, notify ui to refresh.
                }
                break;
            }
            case SDcardRubbishResult.RF_ADV_FOLDERS:
                junkType = JunkRequest.EM_JUNK_DATA_TYPE.ADVERTISEMENT;
                if (replaceItem(mRubbishFileListForAdvFolders, info.oldObj,
                        info.newObj)) {
                    // todo, notify ui to refresh.
                }
                break;
        }

        JunkRequestData tmpData = mAvailableDataMap.get(junkType);
        if (null == tmpData) {
            return;
        }
        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        if (null != cb) {
            cb.onFoundItemSize(info.newObj.getSize() - info.oldObj.getSize(), info.newObj.isCheck());
        }
    }

    public void notifyStop() {
        if (mbScanFinish) {
            return;
        }
        mStopFlag = true;
    }

    private int mActiveTaskMask = 0;
    public static final int TASK_TYPE_SD_CACHE = 2;
    private List<SDcardRubbishResult> mRubbishFileListForAppLeftoversToBigFiles = new ArrayList<SDcardRubbishResult>();
    private ArrayList<CacheInfo> mRubbishToBigFileList = new ArrayList<>();
    private ArrayList<BaseJunkBean> mRubbishFileListForBigFiles = new ArrayList<>();
    private ArrayList<BaseJunkBean> mSystemCacheList = new ArrayList<>();
    private ArrayList<BaseJunkBean> mSysFixedFileList = new ArrayList<>();
    private ArrayList<BaseJunkBean> mRootCacheList = new ArrayList<>();
    private static final String mSystemCacheName = "system_cache_clean_master_type";
    private static final String mSystemFixedCacheName = "system_fixed_cache_clean_master_type";

    public void setTaskActive(int type) {
        mActiveTaskMask |= type;
    }


    public ScanTask getValidCachedDataScanTask(JunkRequest.EM_JUNK_DATA_TYPE type, long timeDistance, ScanTask task) {
        return getValidCachedDataScanTask(type, timeDistance, task, null);
    }

    private class CacheDataScanTask extends ScanTask.BaseStub {

        private List<Pair<JunkRequest.EM_JUNK_DATA_TYPE, ArrayList<BaseJunkBean>>> mCachedRst =
                new ArrayList<Pair<JunkRequest.EM_JUNK_DATA_TYPE,ArrayList<BaseJunkBean>>>();
        private PackageInfo mPkgInfoForOneCacheScan = null;

        public CacheDataScanTask(JunkRequest.EM_JUNK_DATA_TYPE type, ArrayList<BaseJunkBean> scanResultList) {
            appendCacheRst(type, scanResultList);
        }

        public void setOneCacheScanPkgInfo(PackageInfo pkgInfoForOneCacheScan) {
            mPkgInfoForOneCacheScan = pkgInfoForOneCacheScan;
        }

        public void appendCacheRst(JunkRequest.EM_JUNK_DATA_TYPE type, ArrayList<BaseJunkBean> scanResultList) {
            assert(null != scanResultList);
            mCachedRst.add(Pair.create(type, scanResultList));
        }

        @Override
        public boolean scan(ScanTaskController ctrl) {

            for (Pair<JunkRequest.EM_JUNK_DATA_TYPE, ArrayList<BaseJunkBean>> item : mCachedRst) {
                JunkRequest.EM_JUNK_DATA_TYPE type = item.first;
                ArrayList<BaseJunkBean> scanResultList = item.second;

                JunkRequestData req = mAvailableDataMap.get(type);
                if (null != req && null != scanResultList) {
                    req.mScanResultList = scanResultList;

                    callbackPath(scanResultList);

                    switch (type) {
                        case SYSCACHE:
                            mSystemCacheList.ensureCapacity(scanResultList.size());
                            mSystemCacheList.addAll(scanResultList);
                            break;

                        case SYSFIXEDCACHE:
                            mSysFixedFileList.ensureCapacity(scanResultList.size());
                            mSysFixedFileList.addAll(scanResultList);
                            break;

                        case ROOTCACHE:
                            mRootCacheList.ensureCapacity(scanResultList.size());
                            mRootCacheList.addAll(scanResultList);
                            break;

                        case SDCACHE:
                            if (null == mPkgInfoForOneCacheScan) {
                                mSdCardCacheList.ensureCapacity(scanResultList.size());
                                mSdCardCacheList.addAll(scanResultList);
                            } else {
                                for (BaseJunkBean oneCacheItem : scanResultList) {
                                    if (null == oneCacheItem) {
                                        continue;
                                    }

                                    if (oneCacheItem instanceof CacheInfo) {
                                        String pkgName = ((CacheInfo)oneCacheItem).getPackageName();
                                        if (TextUtils.isEmpty(pkgName)) {
                                            continue;
                                        }

                                        if (!pkgName.equals(mPkgInfoForOneCacheScan.packageName)) {
                                            continue;
                                        }


                                        mSdCardCacheList.add(oneCacheItem);
                                    } else {
                                        continue;
                                    }
                                }
                            }
                            break;

                        case SDCACHE_ADV:
                            mAdvSdCardCacheList.ensureCapacity(scanResultList.size());
                            mAdvSdCardCacheList.addAll(scanResultList);
                            break;

                        case ADVERTISEMENT:
                            mRubbishFileListForAdvFolders.ensureCapacity(scanResultList.size());
                            mRubbishFileListForAdvFolders.addAll(scanResultList);
                            break;

                        case TEMPFOLDER:
                            mRubbishFileListForTempFiles.ensureCapacity(scanResultList.size());
                            mRubbishFileListForTempFiles.addAll(scanResultList);
                            break;

                        case TEMPFOLDER_ADV:
                            mRubbishFileListForTempFilesAdv.ensureCapacity(scanResultList.size());
                            mRubbishFileListForTempFilesAdv.addAll(scanResultList);
                            break;

                        case BIGFILE:
                            mRubbishFileListForBigFiles.ensureCapacity(scanResultList.size());
                            mRubbishFileListForBigFiles.addAll(scanResultList);
                            break;

                        case APPLEFTOVER:
                            mRubbishFileListForAppLeftovers.ensureCapacity(scanResultList.size());
                            mRubbishFileListForAppLeftovers.addAll(scanResultList);
                            break;

                        case APPLEFTOVER_ADV:
                            mRubbishFileListForAppLeftoversAdv.ensureCapacity(scanResultList.size());
                            mRubbishFileListForAppLeftoversAdv.addAll(scanResultList);
                            break;

                        case APKFILE:
                            mApkCleanItemInfos.ensureCapacity(scanResultList.size());
                            mApkCleanItemInfos.addAll(scanResultList);
                            break;

                        case USELESSTHUMBNAIL:
                            mUselessThumbnailList.ensureCapacity(scanResultList.size());
                            mUselessThumbnailList.addAll(scanResultList);
                            break;

                        case MYPHOTO:
                            mPhotoList.addAll(scanResultList);
                            break;

                        case MYAUDIO:
                            mAudioList.ensureCapacity(mAudioList.size() + scanResultList.size());
                            for (BaseJunkBean baseInfo : scanResultList) {
                                mAudioList.add(((MediaFile)(baseInfo)));
                            }

                            break;

                        case CALCFOLDER:
                            if (!scanResultList.isEmpty()) {
                                BaseJunkBean baseInfo = scanResultList.get(0);
                                CacheInfo calcSizeInfo = (CacheInfo)baseInfo;

                                mDownloads = calcSizeInfo.getCalcFolderInfo(JunkModel.TYPE_DOWNLOAD_GALLERY);
                                mBluetooths = calcSizeInfo.getCalcFolderInfo(JunkModel.TYPE_BLUETOOTH_GALLERY);
                            }
                            break;

                        case VIDEO_OFF:
                            mVideoOfflineList.ensureCapacity(scanResultList.size());
                            mVideoOfflineList.addAll(scanResultList);
                            break;

                        default:
                            break;
                    }
                }
            }

            if (null != mCB) {
                mCB.callbackMessage(CACHEDATASCANTASK_MSG_FINISH, 0, 0, null);
            }

            return false;
        }

        @Override
        public String getTaskDesc() {
            StringBuilder sb = new StringBuilder("CacheDataScanTask");
            for (Pair<JunkRequest.EM_JUNK_DATA_TYPE, ArrayList<BaseJunkBean>> item : mCachedRst) {
                sb.append('_').append(item.first);
            }
            return sb.toString();
        }

        private void callbackPath(ArrayList<BaseJunkBean> infoList) {
            if (null == infoList || infoList.isEmpty()) {
                return;
            }

            for (BaseJunkBean item : infoList) {
                callbackPath(item);
            }
        }

        private void callbackPath(BaseJunkBean info) {
            if (null == info || null == mCB) {
                return;
            }

            if (info instanceof CacheInfo) {
                String path = ((CacheInfo) info).getFilePath();
                if (!TextUtils.isEmpty(path)) {
                    mCB.callbackMessage(CACHEDATASCANTASK_MSG_PATH, 0, 0, path);
                } else {
                    mCB.callbackMessage(CACHEDATASCANTASK_MSG_PATH, 0, 0, ((CacheInfo) info).getPackageName());
                }
                return;
            }

            /*if (info instanceof CacheOfflineResult) {
                CacheOfflineResult cacheOfflineInfo = (CacheOfflineResult) info;
                for (String strFile : cacheOfflineInfo.getFilePathList()) {
                    mCB.callbackMessage(CACHEDATASCANTASK_MSG_PATH, 0, 0, strFile);
                }
                return;
            }*/

            if (info instanceof RootCacheInfo) {
                mCB.callbackMessage(CACHEDATASCANTASK_MSG_PATH, 0, 0, ((RootCacheInfo) info).getPath());
                return;
            }

            if (info instanceof SDcardRubbishResult) {
                List<String> pathList = ((SDcardRubbishResult) info).getPathList();
                if (null != pathList && !pathList.isEmpty()) {
                    for (String strFile : pathList) {
                        mCB.callbackMessage(CACHEDATASCANTASK_MSG_PATH, 0, 0, strFile);
                    }
                } else {
                    mCB.callbackMessage(CACHEDATASCANTASK_MSG_PATH, 0, 0, ((SDcardRubbishResult) info).getStrDirPath());
                }
                return;
            }

            /*if (info instanceof VideoOfflineResult) {
                mCB.callbackMessage(CACHEDATASCANTASK_MSG_PATH, 0, 0, ((VideoOfflineResult) info).getPath());
                return;
            }*/

            if (info instanceof APKModel) {
                mCB.callbackMessage(CACHEDATASCANTASK_MSG_PATH, 0, 0, ((APKModel) info).getPath());
                return;
            }

            if (info instanceof MediaFile) {
                mCB.callbackMessage(CACHEDATASCANTASK_MSG_PATH, 0, 0, ((MediaFile) info).getPath());
                return;
            }
        }
    }

    private Map<String, List<RootCacheInfo>> mRootCacheInfoMap = new HashMap<String, List<RootCacheInfo>>();
    private List<ArrayList<RootCacheInfo>> mRootCacheInfoList = new ArrayList<ArrayList<RootCacheInfo>>();
    private List<CalcSizeInfoTask.SizeUpdateInfo> mDownloads = new ArrayList<CalcSizeInfoTask.SizeUpdateInfo>();
    private List<CalcSizeInfoTask.SizeUpdateInfo> mBluetooths = new ArrayList<CalcSizeInfoTask.SizeUpdateInfo>();
    public void onFoundItem(RootCacheInfo info){
        if (null == info || onFoundItemIsInvalid(info.getSize())) {
            return;
        }

        List<RootCacheInfo> list = mRootCacheInfoMap.get(info.getPkgName());
        if (list != null) {
            list.add(info);
            mRootCacheList.add(info);
        } else {
            ArrayList<RootCacheInfo> llList = new ArrayList<RootCacheInfo>();
            mRootCacheInfoList.add(llList);
            llList.add(info);
            mRootCacheInfoMap.put(info.getPkgName(), llList);
            mRootCacheList.add(info);
        }

        JunkRequestData tmpData = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.ROOTCACHE);
        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        if (null != cb && info.isCheck()) {
            cb.onFoundItemSize(info.getSize(), info.isCheck());
        }
    }

    public void onChangeItem(APKModel apkModel) {
        if (apkModel == null){
            return;
        }
        if( onFoundItemIsInvalid(apkModel.getSize())){
            return;
        }
        JunkRequestData tmpData = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.APKFILE);

        if (null == tmpData) {
            return;
        }
        for (BaseJunkBean baseJunkBean : mApkCleanItemInfos) {
            APKModel tempModel = (APKModel) baseJunkBean;
            if (tempModel.getPath().equals(apkModel.getPath())) { // 如果是相同的路径，则证明找到
                JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
                if (null != cb) {
                    tempModel.setChecked(true);
                    cb.onFoundItemSize(-100, apkModel.isCheck());
                }
                break;
            }
        }
    }

    public void onFoundItem(APKModel apkModel) {
        if (apkModel == null){
            return;
        }
        if( onFoundItemIsInvalid(apkModel.getSize())){
            return;
        }

        mApkCleanItemInfos.add(apkModel);

        JunkRequestData tmpData = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.APKFILE);

        if (null == tmpData) {
            return;
        }

        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        if (null != cb) {
            cb.onFoundItemSize(apkModel.getSize(), apkModel.isCheck());
        }
    }

    public void onFinishBigFileScan() {
        finishScanTask(TASK_TYPE_BIGFILE);

        JunkRequestData tmpData = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.BIGFILE);

        mRubbishFileListForBigFiles.addAll(mRubbishToBigFileList);

        if (null == tmpData) {
            return;
        }

        if (tmpData.mScanResultList.isEmpty()) {
            tmpData.mScanResultList.ensureCapacity(mRubbishFileListForBigFiles.size());
            tmpData.mScanResultList.addAll(mRubbishFileListForBigFiles);
            updateCachedResultData(JunkRequest.EM_JUNK_DATA_TYPE.BIGFILE, tmpData.mScanResultList);
        }

        JunkResultImpl tmpResult = new JunkResultImpl();
        tmpResult.setDataList(mRubbishFileListForBigFiles);
        mRubbishFileListForBigFiles = new ArrayList<BaseJunkBean>();
        tmpResult.setDataType(JunkRequest.EM_JUNK_DATA_TYPE.BIGFILE);
        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        if (null != cb) {
            cb.onScanEnd(tmpData.mJunkRequest, tmpResult);
        }
    }


    public void onFinishCalcFolderSize() {
        finishScanTask(TASK_TYPE_CALC_SIZE);

        JunkRequestData tmpDataCalcFolder = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.CALCFOLDER);
        if (null != tmpDataCalcFolder) {

            CacheInfo calcSizeInfo = new CacheInfo(JunkRequest.EM_JUNK_DATA_TYPE.CALCFOLDER);
            if ( mDownloads != null && !mDownloads.isEmpty() ) {
                calcSizeInfo.setCalcFolderInfo(JunkModel.TYPE_DOWNLOAD_GALLERY, mDownloads);
            }
            if ( mBluetooths != null && !mBluetooths.isEmpty() ) {
                calcSizeInfo.setCalcFolderInfo(JunkModel.TYPE_BLUETOOTH_GALLERY, mBluetooths );
            }

            if (tmpDataCalcFolder.mScanResultList.isEmpty()) {
                tmpDataCalcFolder.mScanResultList.ensureCapacity(1);
                tmpDataCalcFolder.mScanResultList.add(calcSizeInfo);
                updateCachedResultData(JunkRequest.EM_JUNK_DATA_TYPE.CALCFOLDER, tmpDataCalcFolder.mScanResultList);
            }

            // invoke callback
            if (mCalcFolderList.isEmpty() && (!tmpDataCalcFolder.mScanResultList.isEmpty())) {
                mCalcFolderList.ensureCapacity(tmpDataCalcFolder.mScanResultList.size());
                mCalcFolderList.addAll(tmpDataCalcFolder.mScanResultList);
            }
            JunkResultImpl tmpResult = new JunkResultImpl();
            tmpResult.setDataList(mCalcFolderList);
            mCalcFolderList = new ArrayList<BaseJunkBean>();
            tmpResult.setDataType(JunkRequest.EM_JUNK_DATA_TYPE.CALCFOLDER);
            JunkRequest.RequestCallback cb = tmpDataCalcFolder.mJunkRequest.getScanCallback();
            if (null != cb) {
                cb.onScanEnd(tmpDataCalcFolder.mJunkRequest, tmpResult);
            }
        }
    }

    private boolean disableCache = true;
    public ScanTask getValidCachedDataScanTask(JunkRequest.EM_JUNK_DATA_TYPE type, long timeDistance, ScanTask task, PackageInfo pkgInfoForOneCacheScan) {
        if (disableCache){
            return null;
        }

        JunkCachedDataInfo cachedData = mCachedDataMap.get(type);
        if (null == cachedData) {
            return null;
        }

        long time = System.currentTimeMillis() - cachedData.mRecordTime;

        //新加代码：系统缓存预加载处理
        /*if (JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE.equals(type)) {
            //处理系统缓存失效的时候，提前预加载处理,提前1分钟处理，目前失效时间为5分钟失效；
            //当在第4分钟的时候就启用调度器进行系统缓存预加载处理
            final long ONE_MIN = 60 * 1000;//提前1分钟，4分钟开始预加载
            if (time < 0 || time >= (timeDistance - ONE_MIN) || null == cachedData.mScanResultList) {
                NLog.i(TAG, "系统缓存失效了 赶快进行预加载");
                StrategyExecutor.getInstance().exec();
            }
        }*/

        if (time < 0 || time >= timeDistance || null == cachedData.mScanResultList) {
            mCachedDataMap.remove(type);
            return null;
        }

        if (null == task) {
            CacheDataScanTask rst = new CacheDataScanTask(type, cachedData.mScanResultList);
            if (null != pkgInfoForOneCacheScan) {
                rst.setOneCacheScanPkgInfo(pkgInfoForOneCacheScan);
            }
            return rst;
        } else if (task instanceof CacheDataScanTask) {
            ((CacheDataScanTask)task).appendCacheRst(type, cachedData.mScanResultList);
            if (null != pkgInfoForOneCacheScan) {
                ((CacheDataScanTask)task).setOneCacheScanPkgInfo(pkgInfoForOneCacheScan);
            }
            return task;
        }

        return null;
    }

    public void onFinishAPKScan() {
        finishScanTask(TASK_TYPE_APK);

        JunkRequestData tmpData = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.APKFILE);
        if (null != tmpData) {
            if (tmpData.mScanResultList.isEmpty()) {
                tmpData.mScanResultList.ensureCapacity(mApkCleanItemInfos.size());
                tmpData.mScanResultList.addAll(mApkCleanItemInfos);
                updateCachedResultData(JunkRequest.EM_JUNK_DATA_TYPE.APKFILE, tmpData.mScanResultList);
            }

            // invoke callback
            if (mApkCleanItemInfos.isEmpty() && (!tmpData.mScanResultList.isEmpty())) {
                mApkCleanItemInfos.ensureCapacity(tmpData.mScanResultList.size());
                mApkCleanItemInfos.addAll(tmpData.mScanResultList);
            }
            JunkResultImpl tmpResult = new JunkResultImpl();
            tmpResult.setDataList(mApkCleanItemInfos);
            mApkCleanItemInfos = new ArrayList<BaseJunkBean>();
            tmpResult.setDataType(JunkRequest.EM_JUNK_DATA_TYPE.APKFILE);
            JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
            if (null != cb) {
                cb.onScanEnd(tmpData.mJunkRequest, tmpResult);
            }
        }

        if (isAvailableType(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER)) {
            if (0 == (mActiveTaskMask & TASK_TYPE_RUBBISH)) {	// 有部分数据是在apktask中扫描出来的
                finishTempFolderHandler(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER, mRubbishFileListForTempFiles);
            }
        }
    }

    private void finishTempFolderHandler(JunkRequest.EM_JUNK_DATA_TYPE type, ArrayList<BaseJunkBean> tmpFolderList) {
        if (!isAvailableType(type)) {
            return;
        }

        JunkRequestData tmpData = mAvailableDataMap.get(type);

        if (null == tmpData) {
            return;
        }

        if (tmpData.mScanResultList.isEmpty()) {
            tmpData.mScanResultList.ensureCapacity(tmpFolderList.size());
            tmpData.mScanResultList.addAll(tmpFolderList);
            updateCachedResultData(type, tmpData.mScanResultList);
        }

        // invoke callback
        if (tmpFolderList.isEmpty() && (!tmpData.mScanResultList.isEmpty())) {
            tmpFolderList.ensureCapacity(tmpData.mScanResultList.size());
            tmpFolderList.addAll(tmpData.mScanResultList);
        }
        JunkResultImpl tmpResult = new JunkResultImpl();
        tmpResult.setDataList(tmpFolderList);
        tmpFolderList = new ArrayList<BaseJunkBean>();
        tmpResult.setDataType(type);
        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        if (null != cb) {
            cb.onScanEnd(tmpData.mJunkRequest, tmpResult);
        }

    }

    public void onFinishThumbnailScan() {
        finishScanTask(TASK_TYPE_THUMBNAIL);
        JunkRequestData tmpDataUselessThumb = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.USELESSTHUMBNAIL);
        if (null != tmpDataUselessThumb) {
            if (tmpDataUselessThumb.mScanResultList.isEmpty()) {
                tmpDataUselessThumb.mScanResultList.ensureCapacity(mUselessThumbnailList.size());
                tmpDataUselessThumb.mScanResultList.addAll(mUselessThumbnailList);
                updateCachedResultData(JunkRequest.EM_JUNK_DATA_TYPE.USELESSTHUMBNAIL, tmpDataUselessThumb.mScanResultList);
            }

            // invoke callback
            if (mUselessThumbnailList.isEmpty() && (!tmpDataUselessThumb.mScanResultList.isEmpty())) {
                mUselessThumbnailList.ensureCapacity(tmpDataUselessThumb.mScanResultList.size());
                mUselessThumbnailList.addAll(tmpDataUselessThumb.mScanResultList);
            }
            JunkResultImpl tmpResult = new JunkResultImpl();
            tmpResult.setDataList(mUselessThumbnailList);
            mUselessThumbnailList = new ArrayList<BaseJunkBean>();
            tmpResult.setDataType(JunkRequest.EM_JUNK_DATA_TYPE.USELESSTHUMBNAIL);
            JunkRequest.RequestCallback cb = tmpDataUselessThumb.mJunkRequest.getScanCallback();
            if (null != cb) {
                cb.onScanEnd(tmpDataUselessThumb.mJunkRequest, tmpResult);
            }
        }
    }

    private void finishAppLeftOverHandler(JunkRequest.EM_JUNK_DATA_TYPE type, ArrayList<BaseJunkBean> appLeftOverList) {
        if (!isAvailableType(type)) {
            return;
        }

        JunkRequestData tmpData = mAvailableDataMap.get(type);

        if (null == tmpData) {
            return;
        }

        if (tmpData.mScanResultList.isEmpty()) {
            tmpData.mScanResultList.ensureCapacity(appLeftOverList.size());
            tmpData.mScanResultList.addAll(appLeftOverList);
            updateCachedResultData(type, tmpData.mScanResultList);
        }

        // invoke callback
        if (appLeftOverList.isEmpty() && (!tmpData.mScanResultList.isEmpty())) {
            appLeftOverList.ensureCapacity(tmpData.mScanResultList.size());
            appLeftOverList.addAll(tmpData.mScanResultList);
        }
        JunkResultImpl tmpResult = new JunkResultImpl();
        tmpResult.setDataList(appLeftOverList);
        appLeftOverList = new ArrayList<BaseJunkBean>();
        tmpResult.setDataType(type);
        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        NLog.d(TAG, "残留扫描结束: tmpResult = "+tmpResult.getDataList());
        if (null != cb) {
            cb.onScanEnd(tmpData.mJunkRequest, tmpResult);
        }

    }

    public void onFinishRubbishScan() {
        finishScanTask(TASK_TYPE_RUBBISH);

        finishAppLeftOverHandler(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER, mRubbishFileListForAppLeftovers);
        finishAppLeftOverHandler(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV, mRubbishFileListForAppLeftoversAdv);


        if (isAvailableType(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER)) {
            if (0 == (mActiveTaskMask & TASK_TYPE_APK)) {	// 有部分数据是在apktask中扫描出来的
                finishTempFolderHandler(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER, mRubbishFileListForTempFiles);
            }
        }

        if (isAvailableType(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV)) {
            finishTempFolderHandler(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV, mRubbishFileListForTempFilesAdv);
        }

        JunkRequestData tmpDataAdv = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.ADVERTISEMENT);
        if (null != tmpDataAdv) {
            if (tmpDataAdv.mScanResultList.isEmpty()) {
                tmpDataAdv.mScanResultList.ensureCapacity(mRubbishFileListForAdvFolders.size());
                tmpDataAdv.mScanResultList.addAll(mRubbishFileListForAdvFolders);
                updateCachedResultData(JunkRequest.EM_JUNK_DATA_TYPE.ADVERTISEMENT, tmpDataAdv.mScanResultList);
            }

            // invoke callback
            if (mRubbishFileListForAdvFolders.isEmpty() && (!tmpDataAdv.mScanResultList.isEmpty())) {
                mRubbishFileListForAdvFolders.ensureCapacity(tmpDataAdv.mScanResultList.size());
                mRubbishFileListForAdvFolders.addAll(tmpDataAdv.mScanResultList);
            }
            JunkResultImpl tmpResult = new JunkResultImpl();
            tmpResult.setDataList(mRubbishFileListForAdvFolders);
            mRubbishFileListForAdvFolders = new ArrayList<BaseJunkBean>();
            tmpResult.setDataType(JunkRequest.EM_JUNK_DATA_TYPE.ADVERTISEMENT);
            JunkRequest.RequestCallback cb = tmpDataAdv.mJunkRequest.getScanCallback();
            if (null != cb) {
                cb.onScanEnd(tmpDataAdv.mJunkRequest, tmpResult);
            }
        }
    }

    public void onFinishTmpFilesScan() {
        finishScanTask(TASK_TYPE_TMP_FILE);
    }

    public void onFinishLogFilesScan() {
        finishScanTask(TASK_TYPE_LOG_FILE);
    }

    public void onFinishPhotoScan() {

    }

    public void onFinishAudioScan() {
        finishScanTask(TASK_TYPE_AUDIO);

        JunkRequestData tmpDataMyAudio = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.MYAUDIO);
        if (null != tmpDataMyAudio) {
            for (MediaFile file : mAudioList) {
                mAudioListAsBase.add((BaseJunkBean)file);
            }

            if (tmpDataMyAudio.mScanResultList.isEmpty()) {
                tmpDataMyAudio.mScanResultList.ensureCapacity(mAudioListAsBase.size());
                tmpDataMyAudio.mScanResultList.addAll(mAudioListAsBase);
                updateCachedResultData(JunkRequest.EM_JUNK_DATA_TYPE.MYAUDIO, tmpDataMyAudio.mScanResultList);
            }

            // invoke callback
            if (mAudioListAsBase.isEmpty() && (!tmpDataMyAudio.mScanResultList.isEmpty())) {
                mAudioListAsBase.ensureCapacity(tmpDataMyAudio.mScanResultList.size());
                mAudioListAsBase.addAll(tmpDataMyAudio.mScanResultList);
            }
            JunkResultImpl tmpResult = new JunkResultImpl();
            tmpResult.setDataList(mAudioListAsBase);
            mAudioListAsBase = new ArrayList<BaseJunkBean>();
            tmpResult.setDataType(JunkRequest.EM_JUNK_DATA_TYPE.MYAUDIO);
            JunkRequest.RequestCallback cb = tmpDataMyAudio.mJunkRequest.getScanCallback();
            if (null != cb) {
                cb.onScanEnd(tmpDataMyAudio.mJunkRequest, tmpResult);
            }
        }
    }

    public void onFinishScrShotsCompressScan() {
        finishScanTask(TASK_TYPE_SCRSHOTSCOMPRESS);
        JunkRequestData tmpData = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS);
        if (null != tmpData) {
            if (tmpData.mScanResultList.isEmpty()) {
                tmpData.mScanResultList.ensureCapacity(mScreenShotsCompressList.size());
                tmpData.mScanResultList.addAll(mScreenShotsCompressList);
                updateCachedResultData(JunkRequest.EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS, tmpData.mScanResultList);
            }

            // invoke callback
            if (mScreenShotsCompressList.isEmpty() && (!tmpData.mScanResultList.isEmpty())) {
                mScreenShotsCompressList.ensureCapacity(tmpData.mScanResultList.size());
                mScreenShotsCompressList.addAll(tmpData.mScanResultList);
            }
            JunkResultImpl tmpResult = new JunkResultImpl();
            tmpResult.setDataList(mScreenShotsCompressList);
            mScreenShotsCompressList = new ArrayList<BaseJunkBean>();
            tmpResult.setDataType(JunkRequest.EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS);
            JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
            if (null != cb) {
                cb.onScanEnd(tmpData.mJunkRequest, tmpResult);
            }
        }
    }

    public void onFinishSysCacheScan() {
        finishScanTask(TASK_TYPE_SYS_CACHE);
        JunkRequestData tmpData = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);

        if (null == tmpData) {
            return;
        }

        if (tmpData.mScanResultList.isEmpty()) {
            tmpData.mScanResultList.ensureCapacity(mSystemCacheList.size());
            tmpData.mScanResultList.addAll(mSystemCacheList);
            updateCachedResultData(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE, tmpData.mScanResultList);
        }

        // invoke callback
        if (mSystemCacheList.isEmpty() && (!tmpData.mScanResultList.isEmpty())) {
            mSystemCacheList.ensureCapacity(tmpData.mScanResultList.size());
            mSystemCacheList.addAll(tmpData.mScanResultList);
        }
        JunkResultImpl tmpResult = new JunkResultImpl();
        tmpResult.setDataList(mSystemCacheList);
        mSystemCacheList = new ArrayList<BaseJunkBean>();
        tmpResult.setDataType(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);
        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        if (null != cb) {
            cb.onScanEnd(tmpData.mJunkRequest, tmpResult);
        }
    }

    public void onFinishSysFixedCacheScan(){
        finishScanTask(TASK_TYPE_SYS_FIXED_CACHE);
        JunkRequestData tmpData = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE);

        if (null == tmpData) {
            return;
        }

        if (tmpData.mScanResultList.isEmpty()) {
            tmpData.mScanResultList.ensureCapacity(mSysFixedFileList.size());
            tmpData.mScanResultList.addAll(mSysFixedFileList);
            updateCachedResultData(JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE, tmpData.mScanResultList);
        }

        // invoke callback
        if (mSysFixedFileList.isEmpty() && (!tmpData.mScanResultList.isEmpty())) {
            mSysFixedFileList.ensureCapacity(tmpData.mScanResultList.size());
            mSysFixedFileList.addAll(tmpData.mScanResultList);
        }
        JunkResultImpl tmpResult = new JunkResultImpl();
        tmpResult.setDataList(mSysFixedFileList);
        mSysFixedFileList = new ArrayList<BaseJunkBean>();
        tmpResult.setDataType(JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE);
        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        if (null != cb) {
            cb.onScanEnd(tmpData.mJunkRequest, tmpResult);
        }
    }

    public void onFinishRootCacheScan() {
        NLog.i(TAG, " onFinishRootCacheScan() ");
        finishScanTask(TASK_TYPE_ROOT_CACHE);
        JunkRequestData tmpData = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.ROOTCACHE);
        if (tmpData.mScanResultList.isEmpty()) {
            tmpData.mScanResultList.ensureCapacity(mRootCacheList.size());
            tmpData.mScanResultList.addAll(mRootCacheList);
            updateCachedResultData(JunkRequest.EM_JUNK_DATA_TYPE.ROOTCACHE, tmpData.mScanResultList);
        }

        // invoke callback
        if (mRootCacheList.isEmpty() && (!tmpData.mScanResultList.isEmpty())) {
            mRootCacheList.ensureCapacity(tmpData.mScanResultList.size());
            mRootCacheList.addAll(tmpData.mScanResultList);
        }
        JunkResultImpl tmpResult = new JunkResultImpl();
        tmpResult.setDataList(mRootCacheList);
        mRootCacheList = new ArrayList<>();
        tmpResult.setDataType(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE);
        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        if (null != cb) {
            cb.onScanEnd(tmpData.mJunkRequest, tmpResult);
        }
    }

    public void onFoundBigFileSize(int type, BaseJunkBean info) {
        if (null == info) {
            return;
        }
        if (onFoundItemIsInvalid(info.getSize())) {
            return;
        }

        JunkRequest.EM_JUNK_DATA_TYPE junkType = JunkRequest.EM_JUNK_DATA_TYPE.BIGFILE;

        JunkRequestData tmpData = mAvailableDataMap.get(junkType);
        if (null == tmpData) {
            return;
        } else if (null == tmpData.mJunkRequest) {
            return;
        }
        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        if (null != cb) {
            cb.onFoundItemSize(info.getSize(), info.isCheck());
        }
    }

    public boolean onFoundExtendBigfileItem(int type,BaseJunkBean info){
        if (null == info){
            return false;
        }

        if (onFoundItemIsInvalid(info.getSize())) {
            return false;
        }

        switch (type) {
            case ExtraAndroidFileScanner.RF_APP_LEFTOVERS:
                mRubbishFileListForBigFiles.add((SDcardRubbishResult) info);
                mRubbishFileListForAppLeftoversToBigFiles.add((SDcardRubbishResult) info);
                break;
            case ExtraAndroidFileScanner.RF_BIG_FILES:
                mRubbishFileListForBigFiles.add((SDcardRubbishResult) info);
                break;
            case ExtraAndroidFileScanner.RF_CACHE_INFO:
                mRubbishToBigFileList.add((CacheInfo) info);
                break;
            default:
                break;
        }

        return false;
    }

    /**
     * 判断item是否超过最大的磁盘大小
     * @param size
     * @return
     */
    private long maxStorage = 0l;
    public boolean onFoundItemIsInvalid(long size){

        if(maxStorage > 0  && size >= maxStorage){
            return true;
        }

        return false;
    }
    private Context mCtxContext;
    private List<ArrayList<CacheInfo>> mAllCacheInfoList = new ArrayList<ArrayList<CacheInfo>>();
    private ArrayList<BaseJunkBean> mRubbishFileListForAppLeftovers = new ArrayList<BaseJunkBean>();
    private ArrayList<BaseJunkBean> mRubbishFileListForAdvFolders = new ArrayList<BaseJunkBean>();
    private ArrayList<BaseJunkBean> mRubbishFileListForTempFilesAdv = new ArrayList<BaseJunkBean>();
    private ArrayList<BaseJunkBean> mRubbishFileListForAppLeftoversAdv = new ArrayList<BaseJunkBean>();
    private ArrayList<BaseJunkBean> mRubbishFileListForTempFiles = new ArrayList<BaseJunkBean>();
    private ArrayList<BaseJunkBean> mUselessThumbnailList = new ArrayList<BaseJunkBean>();
    private Map<String, List<CacheInfo>> mAllCacheInfoMap = new HashMap<String, List<CacheInfo>>();
    private ArrayList<BaseJunkBean> mApkCleanItemInfos = new ArrayList<BaseJunkBean>();
    private MediaFileList mPhotoList = new MediaFileList();

    public void onFoundItem(CacheInfo info) {
        if (null == info) {
            return;
        }

        if (onFoundItemIsInvalid(info.getSize())) {
            return;
        }

        JunkRequest.EM_JUNK_DATA_TYPE junkType = JunkRequest.EM_JUNK_DATA_TYPE.UNKNOWN;

        if (CacheInfo.INFOTYPE_SYSTEMCACHEITEM == info.getInfoType()) {
            junkType = JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE;
            mSystemCacheList.add(info);

            CacheInfo sysAllInfo = null;
            if ((!mAllCacheInfoMap.isEmpty())
                    && mAllCacheInfoMap.get(mSystemCacheName) != null) {
                sysAllInfo = mAllCacheInfoMap.get(mSystemCacheName).get(0);
            } else {
                sysAllInfo = new CacheInfo(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);
                ArrayList<CacheInfo> list = new ArrayList<CacheInfo>();
                mAllCacheInfoList.add(0, list);
                list.add(sysAllInfo);
                mAllCacheInfoMap.put(mSystemCacheName, list);

                sysAllInfo.setAppName(ResUtil.getString(R.string.junk_tag_system_cache));
                sysAllInfo.setInfoType(CacheInfo.INFOTYPE_SYSTEMCACHE);
                sysAllInfo.setCheck(true);
            }

            sysAllInfo.setSize(sysAllInfo.getSize() + info.getSize());
            sysAllInfo.setAppCount(mSystemCacheList.size());
            if (!info.isCheck() && sysAllInfo.isCheck()) {
                sysAllInfo.setCheck(false);
            }
        } else if (CacheInfo.INFOTYPE_APPCACHE == info.getInfoType()) {
            junkType = addInfo2Cache(info);
        } else if(CacheInfo.INFOTYPE_SYSFIXEDFIELITEM == info.getInfoType()){
            junkType = JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE;
            mSysFixedFileList.add(info);

            CacheInfo sysFixedAllInfo = null;
            if ((!mAllCacheInfoMap.isEmpty())
                    && mAllCacheInfoMap.get(mSystemFixedCacheName) != null) {
                sysFixedAllInfo = mAllCacheInfoMap.get(mSystemFixedCacheName).get(0);
            } else {
                sysFixedAllInfo = new CacheInfo(JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE);
                ArrayList<CacheInfo> list = new ArrayList<CacheInfo>();
                mAllCacheInfoList.add(list);
                list.add(sysFixedAllInfo);
                mAllCacheInfoMap.put(mSystemFixedCacheName, list);

                sysFixedAllInfo.setAppName(ResUtil.getString(R.string.junk_tag_system_fixed_cache));
                sysFixedAllInfo.setInfoType(CacheInfo.INFOTYPE_SYSFIXEDFILE);
                sysFixedAllInfo.setCheck(true);
            }

            sysFixedAllInfo.setSize(sysFixedAllInfo.getSize() + info.getSize());
            sysFixedAllInfo.setAppCount(mSysFixedFileList.size());
            if (!info.isCheck() && sysFixedAllInfo.isCheck()) {
                sysFixedAllInfo.setCheck(false);
            }
        }

        JunkRequestData tmpData = mAvailableDataMap.get(junkType);

        if (null == tmpData) {
            return;
        }

        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        if (null != cb) {
            cb.onFoundItemSize(info.getSize(), info.isCheck());
        }
    }

    public void onFoundItem(MediaFile mediaInfo) {
        if (mediaInfo == null){
            return;
        }

        JunkRequest.EM_JUNK_DATA_TYPE type = JunkRequest.EM_JUNK_DATA_TYPE.UNKNOWN;
        if (JunkRequest.EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS == mediaInfo.getJunkDataType()) {
            mScreenShotsCompressList.add(mediaInfo);
            type = JunkRequest.EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS;
        } else {
            switch (mediaInfo.getMediaType()) {
                case MediaFile.MEDIA_TYPE_IMAGE:
                case MediaFile.MEDIA_TYPE_VIDEO:
                    type = JunkRequest.EM_JUNK_DATA_TYPE.MYPHOTO;
                    mPhotoList.add(mediaInfo);
                    break;
                case MediaFile.MEDIA_TYPE_AUDIO:
                    type = JunkRequest.EM_JUNK_DATA_TYPE.MYAUDIO;
                    mAudioList.add(mediaInfo);
                    break;
                default:
                    break;
            }
        }

        if (JunkRequest.EM_JUNK_DATA_TYPE.UNKNOWN != type) {
            JunkRequestData tmpData = mAvailableDataMap.get(type);

            if (null == tmpData) {
                return;
            }

            JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
            if (null != cb) {
                cb.onFoundItemSize(mediaInfo.getSize(), mediaInfo.isCheck());
            }
        }
    }

    private JunkRequest.EM_JUNK_DATA_TYPE addInfo2Cache(CacheInfo info) {

        List<CacheInfo> list = mAllCacheInfoMap.get(info.getPackageName());
        if (list != null) {
            list.add(info);
        } else {
            ArrayList<CacheInfo> llList = new ArrayList<CacheInfo>();
            mAllCacheInfoList.add(llList);
            llList.add(info);
            mAllCacheInfoMap.put(info.getPackageName(), llList);
        }

        JunkRequest.EM_JUNK_DATA_TYPE junkType = JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE;
        if (info.getScanType() == BaseJunkBean.SCAN_TYPE_STANDARD) {
            mSdCardCacheList.add(info);
        } else if (info.getScanType() == BaseJunkBean.SCAN_TYPE_ADVANCED) {
            mAdvSdCardCacheList.add(info);
            junkType = JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE_ADV;
        }

        return junkType;
    }


    public void onFoundItem(int type, SDcardRubbishResult info) {
        if (null == info) {
            return;
        }
        if (onFoundItemIsInvalid(info.getSize())) {
            return;
        }

        JunkRequest.EM_JUNK_DATA_TYPE junkType = JunkRequest.EM_JUNK_DATA_TYPE.UNKNOWN;

        switch (type) {
            case SDcardRubbishResult.RF_APP_LEFTOVERS:
                if (info.getScanType() == BaseJunkBean.SCAN_TYPE_STANDARD) {
                    junkType = JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER;
                    mRubbishFileListForAppLeftovers.add(info);
                } else {
                    junkType = JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV;
                    mRubbishFileListForAppLeftoversAdv.add(info);
                }
                break;

            case SDcardRubbishResult.RF_TEMPFILES:
                if ((!info.isCheck()) && info.getScanType() == BaseJunkBean.SCAN_TYPE_STANDARD && info.getApkName().equals(
                        SpaceApplication.getInstance().getContext().getString(R.string.junk_tag_RF_ImageThumbnails))) {
                    junkType = JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER;
                    mRubbishFileListForTempFiles.add(info);
                } else {

                    if (null != info.getStrDirPath()
                            && info.getStrDirPath()
                            .equals(WhiteListsWrapper.FUNCTION_FILTER_NAME_OBSOLETE_THUMBNAIL_SCAN)) {
                        junkType = JunkRequest.EM_JUNK_DATA_TYPE.USELESSTHUMBNAIL;
                        mUselessThumbnailList.add(info);
                    } else {
                        if (info.getScanType() == BaseJunkBean.SCAN_TYPE_STANDARD) {
                            junkType = JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER;
                            mRubbishFileListForTempFiles.add(info);
                        } else {
                            junkType = JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV;
                            mRubbishFileListForTempFilesAdv.add(info);
                        }
                    }
                }
                break;
            case SDcardRubbishResult.RF_ADV_FOLDERS:
                junkType = JunkRequest.EM_JUNK_DATA_TYPE.ADVERTISEMENT;
                mRubbishFileListForAdvFolders.add(info);
                // todo, notify ui to refresh adv part
                break;
        }

        JunkRequestData tmpData = mAvailableDataMap.get(junkType);
        if (null == tmpData) {
            return;
        } else if (null == tmpData.mJunkRequest) {
            return;
        }
        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        if (null != cb) {
            cb.onFoundItemSize(info.getSize(), info.isCheck());
        }
    }

    public boolean isAvailableType(JunkRequest.EM_JUNK_DATA_TYPE type) {
        return mAvailableDataMap.containsKey(type);
    }

    public String getPkgNameToScanCache() {
        JunkRequestData tmpData = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE);
        if (null == tmpData || null == tmpData.mJunkRequest) {
            return null;
        }

        RequestConfig cfg = tmpData.mJunkRequest.getRequestConfig();
        if (null == cfg) {
            return null;
        }

        return cfg.getCfgString(RequestConfig.REQ_CFG_ID_SD_CACHE_PKG_NAME, null);
    }

    public void notifyCurrentScanItem(JunkRequest.EM_JUNK_DATA_TYPE junkType, String strName) {

        if (JunkRequest.EM_JUNK_DATA_TYPE.UNKNOWN == junkType ||
                TextUtils.isEmpty(strName) ||
                TextUtils.isEmpty(strName.trim())) {
            return;
        }

        JunkRequestData tmpData = mAvailableDataMap.get(junkType);
        if (null == tmpData) {
            return;
        } else if (null == tmpData.mJunkRequest) {
            return;
        }
        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        if (null != cb) {
            cb.onScanningItem(strName);
        }
    }

    public void onFinishSdCacheScan() {
        NLog.i(TAG, " onFinishSdCacheScan() ");
        finishScanTask(TASK_TYPE_SD_CACHE);

        finishSdCacheScanHandler(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE, isOnlyScanOneCache());

        finishSdCacheScanHandler(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE_ADV, false);
    }

    private boolean isOnlyScanOneCache() {
        JunkRequestData tmpData = mAvailableDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE);
        if (null == tmpData || null == tmpData.mJunkRequest) {
            return false;
        }

        RequestConfig cfg = tmpData.mJunkRequest.getRequestConfig();
        if (null == cfg) {
            return false;
        }

        return !TextUtils.isEmpty(cfg.getCfgString(RequestConfig.REQ_CFG_ID_SD_CACHE_PKG_NAME, null));
    }

    private ArrayList<BaseJunkBean> mSdCardCacheList = new ArrayList<BaseJunkBean>();
    private ArrayList<BaseJunkBean> mAdvSdCardCacheList = new ArrayList<BaseJunkBean>();

    private void finishSdCacheScanHandler(JunkRequest.EM_JUNK_DATA_TYPE type, boolean bSkipUpdateCache) {
        if (!isAvailableType(type)) {
            return;
        }

        assert(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE == type || JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE_ADV == type);
        ArrayList<BaseJunkBean> cacheList = (JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE == type ? mSdCardCacheList : mAdvSdCardCacheList);

        JunkRequestData tmpData = mAvailableDataMap.get(type);

        if (null == tmpData) {
            return;
        }

        if (!bSkipUpdateCache) {
            if (tmpData.mScanResultList.isEmpty()) {
                tmpData.mScanResultList.ensureCapacity(cacheList.size());
                tmpData.mScanResultList.addAll(cacheList);
                updateCachedResultData(type, tmpData.mScanResultList);
            }

            // invoke callback
            if (cacheList.isEmpty() && (!tmpData.mScanResultList.isEmpty())) {
                cacheList.ensureCapacity(tmpData.mScanResultList.size());
                cacheList.addAll(tmpData.mScanResultList);
            }
        }

        JunkResultImpl tmpResult = new JunkResultImpl();
        tmpResult.setDataList(cacheList);
        cacheList = new ArrayList<>();
        if (JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE == type) {
            mSdCardCacheList = cacheList;
        } else {
            mAdvSdCardCacheList = cacheList;
        }
        tmpResult.setDataType(type);
        JunkRequest.RequestCallback cb = tmpData.mJunkRequest.getScanCallback();
        if (null != cb) {
            cb.onScanEnd(tmpData.mJunkRequest, tmpResult);
        }

    }

    public boolean isFinishScan() {
        return mbScanFinish;
    }

    private static Map<JunkRequest.EM_JUNK_DATA_TYPE, JunkCachedDataInfo> mCachedDataMap =
            new ConcurrentHashMap<JunkRequest.EM_JUNK_DATA_TYPE, JunkCachedDataInfo>();

    private void updateCachedResultData(JunkRequest.EM_JUNK_DATA_TYPE type, ArrayList<BaseJunkBean> list) {
        if (mStopFlag) {
            return;
        }

        JunkCachedDataInfo cachedItem = mCachedDataMap.get(type);
        if (null != cachedItem) {
            // 有效缓存不更新
            NLog.i(TAG, " valid cache not to update");
            return;
        }
        NLog.i(TAG, " invalid cache to update");
        mCachedDataMap.put(type, new JunkCachedDataInfo(list));
    }

    public static class JunkResultImpl implements JunkResult {

        List<BaseJunkBean> mJunkList = null;
        JunkRequest.EM_JUNK_DATA_TYPE mJunkDataType = JunkRequest.EM_JUNK_DATA_TYPE.UNKNOWN;
        int mErrorCode = 0;

        @Override
        public List<BaseJunkBean> getDataList() {
            return mJunkList;
        }

        @Override
        public JunkRequest.EM_JUNK_DATA_TYPE getDataType() {
            return mJunkDataType;
        }

        @Override
        public int getErrorCode() {
            return mErrorCode;
        }

        public void setDataList(List<BaseJunkBean> list) {
            mJunkList = list;
        }

        public void setDataType(JunkRequest.EM_JUNK_DATA_TYPE type) {
            mJunkDataType = type;
        }

/*		public void setErrorCode(int nErrorCode) {
			mErrorCode = nErrorCode;
		}*/
    }

    private void finishScanTask(int taskType) {
        NLog.i(TAG, " finishScanTask()");
        mActiveTaskMask &= (~taskType);
        if (0 == mActiveTaskMask || mStopFlag) {
            if (mActiveTaskMask != 0 && mStopFlag) {
                mCompletedScan = false;
            }

            //nilo 延后开发
            /*if (null != mScanTimeRpt) {
                if (0 == mActiveTaskMask) {
                    mScanTimeRpt.end1();
                    mScanTimeRpt.report();
                    mScanTimeRpt = null;
                } else {
                    mScanTimeRpt.end0();
                }
            }*/

            mbScanFinish = true;
        }
    }

    public List<String> getApkPathList() {
        return mApkPathList;
    }

    public void addAvailableType(JunkRequest request) {
        JunkRequestData tmpData = new JunkRequestData();
        tmpData.mJunkRequest = request;
        tmpData.mScanResultList = new ArrayList<>();
        mAvailableDataMap.put(request.getRequestType(), tmpData);
    }

    public int getVideoCardState(){
        return mVideoCardState;
    }

    public static void getSdCardCacheListPathQueue(Collection<BaseJunkBean> cacheList,
                                                   Queue<JunkCleanItemInfo> q) {
        Iterator<BaseJunkBean> iter = cacheList.iterator();
        if (null == iter) {
            return;
        }

        String path = null;
        CacheInfo item = null;
        int cleanFileFlag = 0;
        List<String> cleanFileList = null;
        while (iter.hasNext()) {
            item = (CacheInfo)iter.next();
            if (null == item) {
                continue;
            }

            cleanFileFlag = item.getCleanFileFlag();
            cleanFileList = item.getCleanTimeFileList();

            //保证Filetype 一定是从 cleanTimeFileList 获取路径
            if ((null == cleanFileList || cleanFileList.isEmpty()) && (item.getFileType()!=CacheInfo.FileType.File)) {
                path = item.getFilePath();
                if (TextUtils.isEmpty(path)) {
                    continue;
                } else {
                    cleanFileList = new ArrayList<String>();
                    cleanFileList.add(path);
                }
            }

            if (null == cleanFileList) {
                continue;
            }
            q.offer(new JunkCleanItemInfo(cleanFileList, cleanFileFlag, item, item.getCleanTime()));
        }
    }

    public void removeDataItem(BaseJunkBean item, int nCleanType) {
        if (null == item) {
            return;
        }

        JunkRequest.EM_JUNK_DATA_TYPE type = item.getJunkDataType();

        JunkRequestData tmpData = mAvailableDataMap.get(type);
        if (null == tmpData)
            return;
        List<BaseJunkBean> dataList = tmpData.mScanResultList;
        if (null == dataList) {
            return;
        }

        int idx = dataList.indexOf(item);
        if (idx != -1) {
            BaseJunkBean oldItem = dataList.get(idx);
            oldItem.setCleanType(nCleanType);
            mMarkCleanItemList.add(oldItem);
        }
        dataList.remove(item);

        JunkCachedDataInfo cachedData = mCachedDataMap.get(type);
        if (null == cachedData) {
            return;
        }

        if (cachedData.mScanResultList != dataList) {
            // 缓存数据与当前修改数据不一致，则缓存失效。
            NLog.i(TAG, " invalid cache appear ");
            mCachedDataMap.remove(type);
        } else {
            cachedData.mRecordTime = System.currentTimeMillis();
            NLog.i(TAG, " removeDataItem  mRecordTime %d", cachedData.mRecordTime);
        }
    }

    public static void getRubbishPath(Queue<JunkCleanItemInfo> rst,
                                      Collection<BaseJunkBean> rubbishList) {
        NLog.i(TAG, " getRubbishPath ");

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

    public static void getRootCacheListPathQueue(Collection<BaseJunkBean> cacheList,
                                                 Queue<JunkCleanItemInfo> q) {
        NLog.i(TAG, " getRootCacheListPathQueue ");
        Iterator<BaseJunkBean> iter = cacheList.iterator();
        if (null == iter) {
            return;
        }

        String path = null;
        RootCacheInfo item = null;
        int cleanFileFlag = 0;
        while (iter.hasNext()) {
            item = (RootCacheInfo)iter.next();
            if (null == item) {
                continue;
            }

            cleanFileFlag = item.getCleanFileFlag();

            path = item.getPath();
            if (!TextUtils.isEmpty(path)) {
                q.offer(new JunkCleanItemInfo(path, cleanFileFlag, item));
            }
        }
    }

    /**
     * 清理结束engine判断 是否删除全部视频
     */
    public void isVideoCardCleaned(){
        if (mCachedDataMap != null
                && mCachedDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.VIDEO_OFF) != null
                && mCachedDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.VIDEO_OFF).mScanResultList != null){
            if (mCachedDataMap.get(JunkRequest.EM_JUNK_DATA_TYPE.VIDEO_OFF).mScanResultList.isEmpty())
                setVideoCardState(VIDEO_ALL_CLEANED);
        }
    }

    public void setVideoCardState(int state){
        mVideoCardState = state;
    }

    public boolean isDisableCache() {
        return disableCache;
    }

    public void setDisableCache(boolean disableCache) {
        this.disableCache = disableCache;
    }
}

package com.clean.spaceplus.cleansdk.junk.engine.task;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.db.DatabaseHelper;
import com.clean.spaceplus.cleansdk.base.db.process_list.CacheProcessModel;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudManager;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudResultReporter;
import com.clean.spaceplus.cleansdk.junk.engine.MediaFileCounter;
import com.clean.spaceplus.cleansdk.junk.engine.ProgressCtrl;
import com.clean.spaceplus.cleansdk.junk.engine.SdCardCacheFileScan;
import com.clean.spaceplus.cleansdk.junk.engine.SpaceTaskTime;
import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.RootCacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.junk.engine.util.CalculateFolderSizeUtil;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.clean.spaceplus.cleansdk.base.db.process_list.dao.CacheWhiteListDaoHelper;
import com.clean.spaceplus.cleansdk.base.db.process_list.dao.JunkLockedDAOHelper;
import com.clean.spaceplus.cleansdk.base.scan.ScanCommonStatus;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskControllerObserver;
import com.clean.spaceplus.cleansdk.base.scan.TaskControllerImpl;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.ServiceConfigManager;
import com.clean.spaceplus.cleansdk.junk.engine.PathListCallback;
import com.clean.spaceplus.cleansdk.junk.engine.bean.ParcelablePathInfo;
import com.clean.spaceplus.cleansdk.util.CleanCloudScanHelper;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import space.network.cleancloud.CleanCloudDef.ScanTaskCtrl;
import space.network.cleancloud.CleanCloudDef.WaitResultType;
import space.network.cleancloud.KCacheCloudQuery;
import space.network.cleancloud.KCacheCloudQuery.PkgQueryParam;
import space.network.cleancloud.KCacheCloudQuery.ResultSourceType;
import space.network.cleancloud.KCacheCloudQuery.ScanType;
import space.network.util.CleanTypeUtil;

/**
 * @author dongdong.huang
 * @Description: sd 卡缓存扫描
 * @date 2016/4/22 14:13
 * @copyright TCL-MIG
 */
public class SdCardCacheScanTask extends ScanTask.BaseStub {
    private static final String TAG = SdCardCacheScanTask.class.getSimpleName();
    public static final int SD_CACHE_SCAN_CFG_MASK_CALC_SIZE				= 0x00000001;	///< 是否计算size
    public static final int SD_CACHE_SCAN_CFG_MASK_CALC_CHECKED_SIZE		= 0x00000002;	///< 是否计算选中项size，若SD_CACHE_SCAN_CFG_MASK_CALC_SIZE开关处于关闭状态，本开关无效。
    public static final int SD_CACHE_SCAN_CFG_MASK_CALC_UNCHECKED_SIZE	= 0x00000004;	///< 是否计算未选中项size，若SD_CACHE_SCAN_CFG_MASK_CALC_SIZE开关处于关闭状态，本开关无效。
    private static final long DAY_IN_MS = (24*60*60*1000);
    private static final long DAY_IN_S = (24*60*60);

    CalculateFolderSizeUtil mCalcFolderSizeHelper = new CalculateFolderSizeUtil();
    private Context mContext;
    private boolean isSDCacheFileTypeEnable;
    private boolean isSDCacheFileTypeCustomEnable;
    private ScanTask mSDScanTaskCachedRst = null;

    private List<PackageInfo> mPkgList = null;
    private List<String> mCachePathList = new ArrayList<String>();
    private int mScanCfgMask = -1;
    private ScanCommonStatus mScanCommonStatus = null;
    private KCacheCloudQuery mCacheCloudQuery;
    private CacheCloudQueryCallback mCacheCloudQueryCallback;
    private final HashMap<String, PackageInfo> mCachePkgInfoData = new HashMap<String, PackageInfo>();

    private int mWhiteListMapSize=0;
    private ArrayMap<String, ProcessModel> mWhiteListMap = new ArrayMap<String, ProcessModel>();
    private Map<String, List<ParcelablePathInfo>> mAdv2StdFilterMap = null;
    private RootCacheCallback mRootCacheCallback;
    private boolean mNotTimeOut;
    private static boolean mIsSDCacheFileTypeCustomEnable = true;
    private SdCardCacheFileScan mSdCardCacheFileScan = new SdCardCacheFileScan();
    private final List<CacheInfo> mCacheInfo2Report = Collections.synchronizedList(new LinkedList<CacheInfo>());

    public static final int SD_CACHE_SCAN_FINISH			= 1;
    public static final int SD_CACHE_SCAN_STATUS			= 2;
    public static final int SD_CACHE_SCAN_FOUND_ITEM		= 3;
    public static final int SD_CACHE_SCAN_IGNORE_ITEM		= 4;
    public static final int SD_CACHE_SCAN_PROGRESS_START	= 5;
    public static final int SD_CACHE_SCAN_PROGRESS_STEP	= 6;
    public static final int SD_CACHE_SCAN_COMMING_SOON_SIZE = 7;

    //如果将该配置取非，那么白名单中的项也会被作为结果返回，可以通过BaseJunkBean的isIgnore()查询是否是ignore item
    public static final int SD_CACHE_SCAN_CFG_MASK_NOT_RETURN_IGNORE			= 0x00000008;
    public static final int SD_CACHE_SCAN_CFG_MASK_QUERY_WITHOUT_ALERTINFO	= 0x00000010;	///< 是否从数据库中查询不带有alertinfo的记录
    public static final int SD_CACHE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO		= 0x00000020;	///< 是否从数据库中查询带有alertinfo的记录
    public static final int SD_CACHE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS	= 0x00000040;  	//// 是否 检查扫描结果 用户设置的锁定状态    默认 不检查
    public static final int SD_CACHE_SCAN_CFG_MASK_NOT_ONLY_PRIVACY_QUERY		= 0x00000080;   ///< 是否查询隐私特定的id(置位则不查询)
    public static final int SD_CACHE_SCAN_CFG_MASK_NOT_COUNT_TARGET_MEDIA_FILE_NUM = 0x00000100;   ///< 是否计算目标文件夹下的媒体文件数\

    //云端缓存扫描最多等待
    private static final int MAX_CACHE_CLOUD_QUERY_WAIT_TIME = 5 * 60 * 1000;
    private boolean bIsAdv2StdItem = false;

    public interface RootCacheCallback{
         void addPkgPathInfoItem(RootCacheInfo item);
         void sdCacheScanFinish(boolean isFinish);
    }


    private int mScanType = -1;
    private byte mHaveNotCleaned = 2;

    public void setRootCacheCallback(RootCacheCallback rootCacheCallback){
        mRootCacheCallback = rootCacheCallback;
    }

    @Override
    public boolean scan(ScanTaskController ctrl) {
        mContext = SpaceApplication.getInstance().getContext();
        initFileCacheFilter();
        boolean result = startScan(ctrl);
        reportScan();
        return result;
    }

    /**
     * @param caller 取值定义为cm_task_time.CM_TASK_TIME_USER_*
     */
    private byte mCaller = SpaceTaskTime.CM_TASK_TIME_USER_UNKNOWN;
    private SpaceTaskTime mTimeRpt = new SpaceTaskTime();

    public void setCaller(byte caller) {
        //        mTimeRpt.user(caller);
        //         mJunkTaskSignInfoc.user(caller);
        mCaller = caller;
    }

    public void setFirstScanFlag() {
        //        mTimeRpt.first(true);
        //      mJunkTaskSignInfoc.first(true);
        mSdCardCacheFileScan.setFileFirstScanFlag(true);
    }

    @Override
    public String getTaskDesc() {
        return TAG;
    }

    /**
     * 设置已安装包名
     * @param pkgList
     */
    public void setInstalledPkgList(List<PackageInfo> pkgList) {
        final String PKG_NAME = SpaceApplication.getInstance().getContext().getPackageName();
        List<PackageInfo> installedList = new ArrayList<>();
        if(pkgList!=null){
            for(PackageInfo pkg : pkgList){
                if(pkg!=null){
                    if(!pkg.packageName.equals(PKG_NAME)){
                        installedList.add(pkg);
                    }
                }
            }
        }
        mPkgList = installedList;
    }

    /**
     * 开始扫描
     * @param taskController
     * @return
     */
    private boolean startScan(ScanTaskController taskController){
        NLog.i(TAG, "startScan");
        if (null != mSDScanTaskCachedRst) {
            mSDScanTaskCachedRst.scan(taskController);
        }

        if (null == mPkgList || mPkgList.isEmpty()) {
            if(mRootCacheCallback != null){
                mRootCacheCallback.sdCacheScanFinish(true);
            }
            if (null != mCB) {
                mCB.callbackMessage(SD_CACHE_SCAN_PROGRESS_START, 0, 0, null);
                mCB.callbackMessage(SD_CACHE_SCAN_FINISH, 0, 0, null);
            }
            return true;
        }

        // 扫描app在sd卡的缓存
        boolean successLoaded = true; //KcmutilSoLoader.doLoad(false);
        if (!successLoaded) {
            if(mRootCacheCallback != null){
                mRootCacheCallback.sdCacheScanFinish(true);
            }
            if (null != mCB) {
                mCB.callbackMessage(SD_CACHE_SCAN_PROGRESS_START, 0, 0, null);
                mCB.callbackMessage(SD_CACHE_SCAN_FINISH, 0, -1, null);
            }
            return true;
        }


        loadAllWhiteList();

        mScanType = KCacheCloudQuery.ScanType.DEFAULT;
        mHaveNotCleaned = CleanCloudResultReporter.IS_FIRST_TYPE.ALL_SCAN;
        if (0 == (mScanCfgMask & SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_NOT_ONLY_PRIVACY_QUERY)) {
            mScanType = ScanType.PRIVACY;
            mHaveNotCleaned = CleanCloudResultReporter.IS_FIRST_TYPE.PRIVACY_SCAN;
        } else if (0 == (mScanCfgMask & SD_CACHE_SCAN_CFG_MASK_QUERY_WITHOUT_ALERTINFO)) {
            mScanType = ScanType.CAREFUL;
            boolean isFristCleaned = (mScanCommonStatus != null) ? mScanCommonStatus.getIsFirstCleanedJunkAdvanced() : false;
            if (isFristCleaned) {
                mHaveNotCleaned = CleanCloudResultReporter.IS_FIRST_TYPE.ADV_FIRST_CLEANED;
            } else {
                mHaveNotCleaned = CleanCloudResultReporter.IS_FIRST_TYPE.ADV_NOT_FIRST_CLEANED;
            }
        } else if (0 == (mScanCfgMask & SD_CACHE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO)) {
            mScanType = ScanType.SUGGESTED_WITH_CLEANTIME;
            boolean isFristCleaned = (mScanCommonStatus != null) ? mScanCommonStatus.getIsFirstCleanedJunkStandard() : false;
            if (isFristCleaned) {
                mHaveNotCleaned = CleanCloudResultReporter.IS_FIRST_TYPE.STD_FIRST_CLEANED;
            } else {
                mHaveNotCleaned = CleanCloudResultReporter.IS_FIRST_TYPE.STD_NOT_FIRST_CLEANED;
            }
        }
        if (null != mCB) {
            mCB.callbackMessage(SD_CACHE_SCAN_PROGRESS_START, mPkgList.size(),
                    0, null);
        }

        queryJunk(taskController);
        return true;
    }

    public void setScanConfigMask(int mask) {
        mScanCfgMask = mask;
    }
    /**
     * 设置sd缓存过滤
     */
    private void initFileCacheFilter(){
//        isSDCacheFileTypeEnable = CloudCfgDataWrapper.getCloudCfgBooleanValue(
//                CloudCfgKey.JUNK_SCAN_FLAG_KEY,
//                CloudCfgKey.JUNK_SCAN_SDCACHE_FILETYPE590,
//                true);
//
//        isSDCacheFileTypeCustomEnable = CloudCfgDataWrapper.getCloudCfgBooleanValue(
//                CloudCfgKey.JUNK_SCAN_FLAG_KEY,
//                CloudCfgKey.JUNK_SCAN_SDCACHE_FILETYPE_CUSTOM590,
//                true);
//        mSdCardCacheFileScan.initFileCacheFilter();
    }

    /**
     * 上报扫描结果
     */
    private void reportScan(){

    }

    /**
     * 获取白名单信息
     */
    private void loadAllWhiteList(){
        mWhiteListMapSize = 0;
        mWhiteListMap.clear();
        List<CacheProcessModel> tmpWhiteList = CacheWhiteListDaoHelper.getInstance().queryAll();
        if (null != tmpWhiteList) {
            for (ProcessModel tmpModel : tmpWhiteList) {
                if (!TextUtils.isEmpty(tmpModel.getPkgName())) {
                    mWhiteListMap.put(tmpModel.getPkgName(), tmpModel);
                }
            }
            mWhiteListMapSize = mWhiteListMap.size();
        }
    }

    /**
     * 设置扫描状态
     * @param scanCommonStatus
     */
    public void setScanCommonStatus(ScanCommonStatus scanCommonStatus) {
        mScanCommonStatus = scanCommonStatus;
    }

    /**
     * 查询垃圾信息
     * @param taskController
     */
    private boolean queryJunk(ScanTaskController taskController){
        int observerIndex = -1;
        List<String> sdCardPathList = new ArrayList<>();
        sdCardPathList.add(Environment.getExternalStorageDirectory().getAbsolutePath());
        TaskControllerImpl localControl= null;
        mCacheInfo2Report.clear();

        if(null == mCacheCloudQuery){
            mCacheCloudQuery = CleanCloudManager.createCacheCloudQuery(false);
            final TaskControllerImpl localControlImpl = new TaskControllerImpl();
            localControl = localControlImpl;

            if(null != taskController){
                observerIndex = taskController.addObserver(new ScanTaskControllerObserver() {
                    @Override
                    public void stop() {
                        localControlImpl.notifyStop();
                    }

                    @Override
                    public void reset() {
                        localControlImpl.reset();
                    }

                    @Override
                    public void timeout() {
                        localControlImpl.notifyTimeOut();
                    }

                    @Override
                    public void pause(long millis) {
                        localControlImpl.notifyPause(millis);
                    }

                    @Override
                    public void resume() {
                        localControlImpl.resumePause();
                    }
                });
            }

            mCacheCloudQueryCallback = new CacheCloudQueryCallback(localControl);
        }

        mCacheCloudQuery.initialize(mHaveNotCleaned == 1);

        if(mScanCommonStatus != null){
            mCacheCloudQuery.setPkgNetQueryTimeController(mScanCommonStatus.getNetQueryTimeController());
        }

        String language = CleanCloudScanHelper.getCurrentLanguage();
        mCacheCloudQuery.setLanguage(language);
        String[] sdcardPaths = sdCardPathList.toArray(new String[sdCardPathList.size()]);
        mCacheCloudQuery.setSdCardRootPath(sdcardPaths);
        initAdvanced2StandardFilter();

        ArrayList<PkgQueryParam> queryDatas = new ArrayList<>(mPkgList.size());
        PkgQueryParam param = null;

        for(PackageInfo pkg : mPkgList){
            param = new PkgQueryParam();
            param.mCleanType = mScanType;
            param.mPkgName = pkg.packageName;
            queryDatas.add(param);
            mCachePkgInfoData.put(param.mPkgName, pkg);
        }

        if(queryDatas != null && queryDatas.size() > 0){
            mCacheCloudQuery.queryByPkgName(queryDatas, mCacheCloudQueryCallback, true, false);  // 修改by chaohao.zhou
        }

        completeResidualScan(taskController, localControl, queryDatas.size());

        if(taskController != null){
            taskController.removeObserver(observerIndex);
        }

        return true;
    }

    private void initAdvanced2StandardFilter(){

    }

    /**
     * 缓存查询回调类
     */
    public class CacheCloudQueryCallback implements KCacheCloudQuery.PkgQueryCallback {
        private ScanTaskController mCtrl;

        public CacheCloudQueryCallback(ScanTaskController ctrl){
            mCtrl = ctrl;
        }

        @Override
        public void onGetQueryId(int queryId) {

        }

        @Override
        public void onGetQueryResult(int queryId, Collection<KCacheCloudQuery.PkgQueryData> results, boolean queryComplete) {
            if(mFinish){
                return;
            }

            onGetCacheCloudQueryCallback(results, mCtrl);
        }

        @Override
        public boolean checkStop() {
            return mFinish || (mCtrl != null && mCtrl.checkStop());
        }

        private volatile boolean mFinish = false;
        public void finishQuery() {
            mFinish = true;
        }
    }

    /**
     *
     * @param ctrl
     * @param localCtrl
     * @param count
     */
    private void completeResidualScan(final ScanTaskController ctrl, TaskControllerImpl localCtrl, final int count) {
        if (mCacheCloudQuery != null) {
            int waitResult = mCacheCloudQuery.waitForComplete(
                    MAX_CACHE_CLOUD_QUERY_WAIT_TIME, true, null == ctrl ? null :
                            new ScanTaskCtrl() {

                                @Override
                                public boolean checkStop() {
                                    return ctrl.checkStop();
                                }

                            });

            mNotTimeOut = (waitResult == WaitResultType.WAIT_SUCCESSED);

            if (null != localCtrl) {
//                //此处停止会导致查询的数据无法回调回来
//                localCtrl.notifyStop();
            }

            if (null != mCacheCloudQueryCallback) {
                mCacheCloudQueryCallback.finishQuery();
            }
        }
        onScanEngineFinish(ctrl);
    }

    /**
     * 结束扫描
     * @param ctrl
     */
    private void onScanEngineFinish(final ScanTaskController ctrl) {
        mWhiteListMap.clear();

        if(mRootCacheCallback != null){
            mRootCacheCallback.sdCacheScanFinish(true);
        }
        if (null != mCB) {
            mCB.callbackMessage(
                    SD_CACHE_SCAN_FINISH,
                    (null != ctrl && ScanTaskController.TASK_CTRL_TIME_OUT == ctrl
                            .getStatus()) ? 1 : 0, 0, null);
        }
        mCacheCloudQuery.unInitialize();
        mAdv2StdFilterMap = null;
    }

    /**
     * 缓存查询回调返回
     * @param datas
     * @param ctrl
     */
    private void onGetCacheCloudQueryCallback(Collection<KCacheCloudQuery.PkgQueryData> datas, ScanTaskController ctrl){
        if(datas == null || datas.size() == 0 || (ctrl != null && ctrl.checkStop())){
            return;
        }

        boolean needCheck = true;
        PackageInfo pkg = null;
        for(KCacheCloudQuery.PkgQueryData data : datas){
            pkg = mCachePkgInfoData.get(data.mQueryParam.mPkgName);

            if(pkg == null){
                continue;
            }

            if(needCheck && data.mErrorCode != 0 && data.mResult.mQueryResult == KCacheCloudQuery.PkgResultType.UNKNOWN){
//                NLog.d(TAG, "sdcard scan result is unexpected from cloud and mark for rescan after wifi is enabled");
                Context context = CleanCloudManager.getApplicationContext();
                ServiceConfigManager.getInstanse(context).setNeedScanAfterWifiEnabled(true);
                needCheck = false;
            }

            onPkgCacheScanStart(data.mQueryParam.mPkgName);
            ArrayList<CacheInfo> resultList = new ArrayList<CacheInfo>();
            boolean isAddFileScanResult = false;

            if(mIsSDCacheFileTypeCustomEnable && ((ScanType.SUGGESTED_WITH_CLEANTIME == mScanType) || (ScanType.DEFAULT == mScanType))){
                isAddFileScanResult = mSdCardCacheFileScan.addFileScanResult(data, mCachePkgInfoData);
            }

            if(data.mErrorCode != 0 && !isAddFileScanResult){
                onPkgCacheScanEnd(data.mQueryParam.mPkgName, ctrl, resultList);
                continue;
            }

            if((data.mResult.mQueryResult != KCacheCloudQuery.PkgResultType.DIR_LIST
            || data.mResult.mPkgId == 0) && !isAddFileScanResult){
                onPkgCacheScanEnd(data.mQueryParam.mPkgName, ctrl, resultList);
                continue;
            }

            if(mRootCacheCallback != null){
                if(data.mResult.mSystemDataCleanItems != null && !data.mResult.mSystemDataCleanItems.isEmpty()){
                    for(KCacheCloudQuery.PkgQueryPathItem pkgQueryPathItem : data.mResult.mSystemDataCleanItems){
                        RootCacheInfo rootCacheInfo = new RootCacheInfo();
                        rootCacheInfo.setPkgName(data.mQueryParam.mPkgName);
                        rootCacheInfo.setPath(pkgQueryPathItem.mPathString);
                        rootCacheInfo.setCleanType(pkgQueryPathItem.mCleanType);
                        rootCacheInfo.setHaveNotCleaned(mHaveNotCleaned);
                        rootCacheInfo.setResultSource(data.mResultSource);
                        rootCacheInfo.setCleanOperation(pkgQueryPathItem.mCleanOperation);
                        rootCacheInfo.setPathType(pkgQueryPathItem.mPathType);
                        rootCacheInfo.setSignId(pkgQueryPathItem.mSignId);
                        mRootCacheCallback.addPkgPathInfoItem(rootCacheInfo);
                    }
                }
            }

            if(data.mResult.mPkgQueryPathItems == null || data.mResult.mPkgQueryPathItems.isEmpty()){
                onPkgCacheScanEnd(data.mQueryParam.mPkgName, ctrl, resultList);
                continue;
            }

            if(ctrl != null && ctrl.checkStop()){
                return;
            }

            CacheInfo cacheInfo = null;
            int tableType = getTableType(data);
            LinkedList<CacheInfo> resultInfos = new LinkedList<>();
            for(KCacheCloudQuery.PkgQueryPathItem item : data.mResult.mPkgQueryPathItems){
                if(ctrl != null && ctrl.checkStop()){
                    return;
                }

                if(!item.mIsPathStringExist){
                    continue;
                }

                if(item.mPathType == KCacheCloudQuery.CachePathType.DIR
                        || item.mPathType == KCacheCloudQuery.CachePathType.DIR_REG){
                    if(TextUtils.isEmpty(item.mPath)){
                        continue;
                    }
                }
                else{
                    if(!mIsSDCacheFileTypeCustomEnable || item.mFiles == null || item.mFiles.length == 0){
                        continue;
                    }
                }

                if(!isNeedScan(mScanType, item, mScanCfgMask)){
                    continue;
                }

                cacheInfo = createCacheInfo(item,
                        pkg,
                        data.mLanguage, tableType,
                        item.mCleanMediaFlag, data.mResultSource);
                resultInfos.add(cacheInfo);
            }

            resultList.addAll(resultInfos);
            onPkgCacheScanEnd(data.mQueryParam.mPkgName, ctrl, resultList);

            if(resultList != null && resultList.size() > 0){
                mCacheInfo2Report.addAll(resultList);
            }
        }
    }

    /**
     * 通知缓存开始查询事件，实际上已经查询完毕
     * @param pkgName
     */
    private void onPkgCacheScanStart(String pkgName){
        if (null != mCB) {
            mCB.callbackMessage(SD_CACHE_SCAN_STATUS, 0, 0,
                    pkgName);
        }
    }

    /**
     * pkg查询结束,逐个处理查询结果
     * @param pkgName
     * @param controller
     * @param resultList
     */
    private void onPkgCacheScanEnd(String pkgName, ScanTaskController controller, ArrayList<CacheInfo> resultList){
        resolveCacheScanResult(resultList, controller);
        onPkgItemCacheScanEnd();
    }

    /**
     * pkg查询，进度更新
     */
    private void onPkgItemCacheScanEnd() {
        if (null != mCB) {
            mCB.callbackMessage(SD_CACHE_SCAN_PROGRESS_STEP, 0, 0, null);
        }
    }

    /**
     * 获取表类型
     * @param data
     * @return
     */
    private int getTableType(KCacheCloudQuery.PkgQueryData data){
        int tableType = -1;
        switch (data.mResultSource){
            case ResultSourceType.HFREQ:
                tableType = DatabaseHelper.TABLE_ID_HF;
                break;
            case ResultSourceType.CACHE:
                tableType = DatabaseHelper.TABLE_ID_APP_CACHE;
                break;
            case ResultSourceType.CLOUD:
                tableType = DatabaseHelper.TABLE_ID_CLOUD;
                break;
            default:
                break;
        }

        return tableType;
    }

    /**
     * 是否需要扫描
     * @param scanType
     * @param item
     * @param mask
     * @return
     */
    private boolean isNeedScan(int scanType, KCacheCloudQuery.PkgQueryPathItem item, int mask) {
        if (scanType == ScanType.DEFAULT) {
            return true;
        }
        if (scanType == ScanType.PRIVACY
                && (item.mCleanType == ScanType.SUGGESTED || item.mCleanType == ScanType.CAREFUL)) {
            return true;
        }
        if (item.mCleanType == ScanType.SUGGESTED
                && (0 !=(mask & SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_QUERY_WITHOUT_ALERTINFO))) {
            return true;
        }
        if ((scanType == ScanType.SUGGESTED || scanType == ScanType.SUGGESTED_WITH_CLEANTIME)
                && item.isCustomCleanPath) {
            return true;
        }
        if (item.mCleanType == ScanType.CAREFUL
                && ((0 !=(mask & SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO))
                || (item.mCleanTime > 0 && item.mCleanTime != CleanTypeUtil.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE))) {
            return true;
        }
        return false;
    }

    /**
     * 处理pkg查询结果
     * @param scanResultList
     * @param ctrl
     */
    private void resolveCacheScanResult(final ArrayList<CacheInfo> scanResultList, final ScanTaskController ctrl) {
        JunkLockedDAOHelper junkLockedDao = JunkLockedDAOHelper.getInstance();
        String strAppName = null;
        // get app cache
        boolean checked = false;
        boolean calcSize = false;
        boolean bFoundNomediaName = false;
        long fileCompute[] = new long[3];
        final long fileComputeByCleanTime[] = new long[2];

        if (null != scanResultList) {
            for ( final CacheInfo info : scanResultList ) {
                //file level delete need to check the repeat filepath?
                if (info.getFileType() != CacheInfo.FileType.File) {
                    if (mCachePathList.contains(info.getFilePath())) {
                        continue;
                    }
                }

                final boolean bGotCleanTime = (mScanType == ScanType.SUGGESTED_WITH_CLEANTIME && info.getCleanTime() > 0 &&
                        info.getCleanTime() != CleanTypeUtil.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE);
                if ( !bGotCleanTime ) {
                    info.setCleanTime(0);
                }

                // 白名单
                String tmpKey = info.getPackageName() + ":"
                        + info.getFilePath();
                if (mWhiteListMapSize > 0
                        && null != mWhiteListMap.get(tmpKey)) {
                    if (null != mCB) {
                        // 此操作为了确保这个app的本地化资源得到下载。
                        strAppName = info.getAppName();
                        mCB.callbackMessage(SD_CACHE_SCAN_IGNORE_ITEM,
                                0, 0, info);
                    }
                    if ((mScanCfgMask & SD_CACHE_SCAN_CFG_MASK_NOT_RETURN_IGNORE) == 0) {
                        info.setIgnore(true);
                    } else {
                        continue;
                    }
                }

                info.setCheck(!info.isExistWaring()); // 去掉数据库查询
                if (info.isCheck()) {
                    info.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
                } else {
                    info.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
                    info.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE_ADV);
                }

                if ((mScanType == ScanType.SUGGESTED_WITH_CLEANTIME || mScanType == ScanType.SUGGESTED)
                        && info.isAdv2StdItem() && null != junkLockedDao) {
                    // 建议清理要查询记录的勾选状态
                    info.setCheck(!junkLockedDao.checkLocked(info.getCacheId(), false));
                } else if(0 == (SD_CACHE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS & mScanCfgMask)) {
                    //是否 检查锁定
                    //根据id 判断是否锁定
                    if(junkLockedDao!=null) {
                        if (info.getCacheId() != 0) {
                            info.setCheck(!(junkLockedDao.checkLocked(info.getCacheId(),info.isCheck())));
                        } else {
                            info.setCheck(!(junkLockedDao.checkLocked(info.getFilePath(), info.isCheck())));
                        }
                    }
                }
                bFoundNomediaName = false;
                calcSize = false;
                fileCompute[0] = 0;
                fileCompute[1] = 0;
                fileCompute[2] = 0;

                fileComputeByCleanTime[0] = 0;
                fileComputeByCleanTime[1] = 0;
                boolean isFileTypeFile = info.getFileType() == CacheInfo.FileType.File;
                //NLog.i("==sdCardCacheScanTask==",info.getFilePath()+":"+info.getCleanTime()*DAY_IN_MS+"isFileTypeFile:"+isFileTypeFile+",mScanCfgMask:"+mScanCfgMask);
                if (0 != (mScanCfgMask & SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_CALC_SIZE)) {

                    if ((0 != (mScanCfgMask & SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_CALC_CHECKED_SIZE) && checked)
                            || (0 != (mScanCfgMask & SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_CALC_UNCHECKED_SIZE) && !checked)) {

                        calcSize = true;

                        MediaFileCounter counter = null;
                        long[] mediaInfo = null;
                        if ((!isFileTypeFile) && 0 == (SD_CACHE_SCAN_CFG_MASK_NOT_COUNT_TARGET_MEDIA_FILE_NUM & mScanCfgMask)) {
                            mediaInfo = new long [3];
                            counter = new MediaFileCounter() {
                                @Override
                                public void onFile(String filePath,
                                                   long size, int atime, int mtime, int ctime) {
                                    super.onFile(filePath, size, atime, mtime, ctime);

                                    if (bGotCleanTime) {
                                        File tmpFile = new File(filePath);
                                        if (System.currentTimeMillis() - tmpFile.lastModified() > (info.getCleanTime()*DAY_IN_MS)) {
                                            //save total size
                                            fileComputeByCleanTime[0] += size;
                                            //add file count
                                            ++fileComputeByCleanTime[1];
                                            //save path
                                            info.appendCleanTimeFileList(filePath);
                                        }
                                    }

                                    if (size > 0L && null != mCB) {
                                        mCB.callbackMessage(
                                                SD_CACHE_SCAN_COMMING_SOON_SIZE,
                                                0, 0,
                                                Long.valueOf(size));
                                    }
                                }
                            };
                            counter.setMediaFileListMaxSize(-1);
                        }



                        PathOperFunc.CalcSizeCallback calcCallback = new PathOperFunc.CalcSizeCallback(
                                ctrl, 60L * 1000L, 32);
                        long calcTime = calcCallback.start();
                        if (null != counter) {
                            boolean [] msInfo = new boolean[2];
                            mCalcFolderSizeHelper.computeFileSize(
                                    info.getFilePath(), fileCompute,
                                    calcCallback, counter, mediaInfo, null, msInfo);
                        } else {

                            List<String> fileList = new ArrayList<String> ();
                            if (isFileTypeFile) {
                                fileList.addAll(info.getCleanTimeFileList());
                                if (bGotCleanTime) { //file level only clear filelist when timeline
                                    info.getCleanTimeFileList().clear();
                                }
                            } else {
                                fileList.add(info.getFilePath());
                            }

                            if (bGotCleanTime && (android.os.Build.VERSION.SDK_INT >= 11)) {
                                boolean isNeedReCompute = false;
                                if (isFileTypeFile) {
                                    isNeedReCompute = true;
                                } else {
                                    if (CalculateFolderSizeUtil.checkMediaStoreFileSDConstiancy( calcCallback, info.getFilePath(), CalculateFolderSizeUtil.MEDIA_STORE_CHECK_COUNT_LIMIT )
                                            && mediaStoreDataIsValid(info.getFilePath(),info.getCleanTime()) ) {
                                        isNeedReCompute = false;
                                        computeFileSizeByMediaStore(info.getFilePath(),
                                                info.getCleanTime(), fileComputeByCleanTime );
                                    }else {
                                        isNeedReCompute = true;
                                    }
                                }
                                if (isNeedReCompute) {
                                    //处理时间线问题，例如应用宝下载的安装包
                                    PathOperFunc.FilterFileCallback fileCallback=new PathOperFunc.FilterFileCallback(){
                                        @Override
                                        public boolean onFilterFile(File f) {
                                            if (bGotCleanTime) {
                                                //如果该文件在时间线以内则不去计算大小
                                                if (System.currentTimeMillis() - f.lastModified() < (info.getCleanTime()*DAY_IN_MS)) {
                                                    return true;
                                                }
                                            }
                                            return false;
                                        }
                                    };
                                    bFoundNomediaName = PathOperFunc.computePatchFileSize(
                                            fileList, true, bGotCleanTime,
                                            info.getCleanTime(), fileCompute,
                                            fileComputeByCleanTime, calcCallback,
                                            isFileTypeFile ? info.getCleanTimeFileList() : null,
                                            null != mCB ? new PathListCallback() {
                                                @Override
                                                public void onFile(int sizes) {
                                                    if (sizes > 0L) {
                                                        mCB.callbackMessage(
                                                                SD_CACHE_SCAN_COMMING_SOON_SIZE,
                                                                0, 0,
                                                                Long.valueOf(sizes));
                                                    }
                                                }
                                            } : null,fileCallback);
                                }
                            } else {
                                boolean [] msInfo = new boolean[2];
                                bFoundNomediaName = mCalcFolderSizeHelper.computePatchFileSize(
                                        fileList, info.getFileType(), true, bGotCleanTime,
                                        info.getCleanTime(), fileCompute,
                                        fileComputeByCleanTime, calcCallback,
                                        isFileTypeFile ? info.getCleanTimeFileList() : null,
                                        null != mCB ? new PathListCallback() {
                                            @Override
                                            public void onFile(int sizes) {
                                                if (sizes > 0L) {
                                                    mCB.callbackMessage(
                                                            SD_CACHE_SCAN_COMMING_SOON_SIZE,
                                                            0, 0,
                                                            Long.valueOf(sizes));
                                                }
                                            }
                                        } : null, mediaInfo, msInfo );

                            }
                        }

                        calcTime = SystemClock.uptimeMillis()
                                - calcTime;
                        if (calcCallback.isTimeOut()) {
                            //report time out
                        }

                        if (null != mCB) {
                            mCB.callbackMessage(
                                    SD_CACHE_SCAN_COMMING_SOON_SIZE, 0,
                                    0, null);
                        }

                        if (null != counter) {
                            info.setVideoNum(counter.getVideoNum());
                            if ( counter.getVideoNum() == 0 && mediaInfo != null ) info.setVideoNum((int)mediaInfo[0]);
                            info.setImageNum(counter.getImageNum());
                            if ( counter.getImageNum() == 0 && mediaInfo != null ) info.setImageNum((int)mediaInfo[1]);
                            info.setAudioNum(counter.getAudioNum());
                            if ( counter.getAudioNum() == 0 && mediaInfo != null ) info.setAudioNum((int)mediaInfo[2]);
                            info.addMediaList(counter.getMediaList());
                        }
                    }
                }

                boolean maybeTarget = false;
                if (!calcSize) {
                    // 因为我们对于cache的清理是只删文件，不删文件夹，
                    // 所以这里如果没有计算文件夹大小，则不能确定是否
                    // 要对外报出来。所以这里还要检查空文件夹来过滤一下。
                    boolean isEmptyFolder = PathOperFunc.isEmptyFolder(
                            info.getFilePath(), 3, new ProgressCtrl() {

                                @Override
                                public boolean isStop() {
                                    return ctrl.checkStop();
                                }
                            }, null, null);
                    if (!isEmptyFolder) {
                        maybeTarget = true;
                    }

                    if (maybeTarget) {
                        // 过滤掉只有一层，且只有0B的文件夹
                        File targetFolder = new File(info.getFilePath());
                        File[] sub = PathOperFunc
                                .listFiles(targetFolder.getPath());

                        if (null != sub) {
                            boolean needFilter = true;
                            for (File s : sub) {
                                if (s.isDirectory()) {
                                    needFilter = false;
                                    break;
                                }

                                if (s.length() > 0L) {
                                    needFilter = false;
                                    break;
                                }
                            }

                            if (needFilter) {
                                maybeTarget = false;
                            }
                        } else {
                            maybeTarget = false;
                        }
                    }
                }

                if (bGotCleanTime) {
                    if (!((bFoundNomediaName || isFileTypeFile) && fileComputeByCleanTime[0] <= 0L))  {
                        if ( fileComputeByCleanTime[1] > 0 )
                        {
                            if (calcSize) {
                                info.setSize(fileComputeByCleanTime[0]);
                            }
                            info.setCacheFileNum(fileComputeByCleanTime[1]);
                            info.setCacheFolderNum( 0 );
                            String strDesc = info.getAppName();
                            String strDaysAgo = "";
                            info.setAppName(strDesc+" "+strDaysAgo);

                            if (null != mCB) {
                                // 此操作为了确保这个app的本地化资源得到下载。
                                strAppName = info.getAppName();
                                mCachePathList.add(info.getFilePath());
                                mCB.callbackMessage(SD_CACHE_SCAN_FOUND_ITEM,
                                        0, 0, info);
                            }
                        }
                    }
                } else if ((fileCompute[0] > 0 && (fileCompute[1] > 1 || fileCompute[2] > 0))
                        || ((!calcSize) && maybeTarget)) {
                    if (calcSize) {
                        info.setSize(fileCompute[0]);
                    }

                    info.setCacheFileNum(fileCompute[2]);
                    info.setCacheFolderNum( fileCompute[1] );

                    if (null != mCB) {
                        // 此操作为了确保这个app的本地化资源得到下载。
                        strAppName = info.getAppName();
                        mCachePathList.add(info.getFilePath());
                        mCB.callbackMessage(SD_CACHE_SCAN_FOUND_ITEM,
                                0, 0, info);
                    }
                }
            }
        }
    }

    public boolean isAdv2StdItem() {
        return bIsAdv2StdItem;
    }

    public static boolean mediaStoreDataIsValid(String path, long time ) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        long cTime = currentTime/1000 - time * DAY_IN_S;
        String selection = "format!=12289 AND date_modified >= ? AND _data > ? AND _data < ? and title!='.nomedia'";
        path = FileUtils.addSlash(path);
        String path1 = FileUtils.replaceEndSlashBy0(path);
        Cursor cursor = null;
        final String[] projection = {"count(*)"};
        boolean bRet = false;
        try {
            Context context = SpaceApplication.getInstance().getContext();
            cursor = context.getContentResolver()
                    .query(MediaStore.Files.getContentUri("external"),
                            projection, selection, new String[]{String.valueOf(cTime), path, path1}, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long nCount =  cursor.getLong(0);
                    if ( nCount > 0 ) {
                        bRet = true;
                        break;
                    }
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        return bRet;
    }

    public static void computeFileSizeByMediaStore(String path, long time, long[] compute ) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        long cTime = currentTime/1000 - time * DAY_IN_S;
        String selection = "format!=12289 AND date_modified < ? AND _data > ? AND _data < ? and title!='.nomedia'";
        path = FileUtils.addSlash(path);
        String path1 = FileUtils.replaceEndSlashBy0(path);
        Cursor cursor = null;
        final String[] projection = {"sum("+MediaStore.Files.FileColumns.SIZE+")", "count(*)"};
        try {
            Context context = SpaceApplication.getInstance().getContext();
            cursor = context.getContentResolver()
                    .query(MediaStore.Files.getContentUri("external"),
                            projection, selection, new String[]{String.valueOf(cTime), path, path1}, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    compute[0] +=  cursor.getLong(0);
                    compute[1] += cursor.getLong(1) ;
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
    }

    private CacheInfo createCacheInfo(KCacheCloudQuery.PkgQueryPathItem item, PackageInfo packageInfo, String mLanguage,
                                      int tableType, int cleanFileFlag, int resultSource) {

        CacheInfo cacheInfo = new CacheInfo(JunkRequest.EM_JUNK_DATA_TYPE.SDCACHE);
        cacheInfo.setAdv2StdItemFlag(item.isCustomCleanPath);
        cacheInfo.setCleanFileFlag(cleanFileFlag);
        cacheInfo.setPackageInfo(packageInfo);
        cacheInfo.setAppName(item.mShowInfo != null ? item.mShowInfo.mName : "");
        cacheInfo.setCheck(
                mScanType == ScanType.SUGGESTED_WITH_CLEANTIME
                        || mScanType == ScanType.SUGGESTED
                        || item.mCleanType == ScanType.SUGGESTED);//1 建议清理 2 慎重清理
        cacheInfo.setSrsid(-1);
        String cachePath = null;
        boolean isFile = KCacheCloudQuery.CachePathType.FILE == item.mPathType || KCacheCloudQuery.CachePathType.FILE_REG == item.mPathType
                || KCacheCloudQuery.CachePathType.FILE_2 == item.mPathType|| KCacheCloudQuery.CachePathType.FILE_REG_2 == item.mPathType;
        if (isFile) {
            cacheInfo.setFileType(CacheInfo.FileType.File);
        }
        try {
            cachePath = FileUtils.get(new File(item.mPath)).getPath();// cachePath.getCanonicalFile();
        } catch (Exception e) {
            cachePath = item.mPath;
        }
        cacheInfo.setFilePath(cachePath);
        cacheInfo.setScanType(mScanType);
        cacheInfo.setHaveNotCleaned(mHaveNotCleaned);

        cacheInfo.setPrivacyType(item.mPrivacyType);
        cacheInfo.setInfoType(CacheInfo.INFOTYPE_APPCACHE);
        cacheInfo.setWarning((mScanType == ScanType.SUGGESTED_WITH_CLEANTIME
                || mScanType == ScanType.SUGGESTED
                || item.mCleanType == ScanType.SUGGESTED) ? "" : "" + item.mCleanType);//TODO privacyType >0? cleanType =3
        cacheInfo.setDescption(item.mShowInfo != null
                ? item.mShowInfo.mDescription
                : "");
        cacheInfo.setCacheTableTypeId(tableType, Integer.parseInt(item.mSignId));
        cacheInfo.setResultSource((byte) resultSource);
        cacheInfo.setNeedCheck(item.mNeedCheck);
        cacheInfo.setLanguage(mLanguage);
        cacheInfo.setDeleteType(item.mCleanOperation == 1 ? 1 : 0);//新旧字段要做转换
        cacheInfo.setContentType(item.mContentType);
        cacheInfo.setCleanTime(item.mCleanTime);
        cacheInfo.configIsCanAddToPersonalCleanPlan(item.mCleanTime);
        if (isFile) {
            for (int i = 0; i < item.mFiles.length; i++) {
                String file = item.mFiles[i];
                String fileCanPath;
                try {
                    fileCanPath = FileUtils.get(new File(file)).getPath(); //cachePath
                } catch (Exception e) {
                    fileCanPath = item.mFiles[i];
                }
                cacheInfo.appendCleanTimeFileList(fileCanPath);
            }
        }

        if (cacheInfo.isAdv2StdItem() && null != mAdv2StdFilterMap && !mAdv2StdFilterMap.isEmpty()) {
            List<ParcelablePathInfo> pathInfoList = mAdv2StdFilterMap.get(cacheInfo.getPackageName());
            if (null != pathInfoList && !pathInfoList.isEmpty()) {
                String path = cacheInfo.getFilePath();
                for (ParcelablePathInfo pathInfoItem : pathInfoList) {
                    if (null == pathInfoItem) {
                        continue;
                    }

                    if (!path.equals(pathInfoItem.path)) {
                        continue;
                    }

                    cacheInfo.setAdv2StdTime(pathInfoItem.time);
                    break;
                }
            }
        }

        return cacheInfo;
    }

    public void setSDCachedDataScanTask(ScanTask sdCacheScanTaskCachedRst) {
        mSDScanTaskCachedRst = sdCacheScanTaskCachedRst;
    }
}

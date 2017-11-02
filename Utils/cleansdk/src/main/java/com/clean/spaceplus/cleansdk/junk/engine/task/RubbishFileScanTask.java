package com.clean.spaceplus.cleansdk.junk.engine.task;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.R;
import com.clean.spaceplus.cleansdk.base.scan.IScanFilter;
import com.clean.spaceplus.cleansdk.base.scan.ScanCommonStatus;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask.BaseStub;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.base.utils.system.PackageManagerWrapper;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudManager;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CloudCfgDataWrapper;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.CloudCfgKey;
import com.clean.spaceplus.cleansdk.junk.engine.DBColumnFilterManager;
import com.clean.spaceplus.cleansdk.junk.engine.MediaFileCounter;
import com.clean.spaceplus.cleansdk.junk.engine.ProgressCtrl;
import com.clean.spaceplus.cleansdk.junk.engine.RubbishFileFilterImpl;
import com.clean.spaceplus.cleansdk.junk.engine.WhiteListsWrapper;
import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.bean.SDcardRubbishResult;
import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.junk.engine.util.CalcFolderSizeHelper;
import com.clean.spaceplus.cleansdk.junk.engine.util.NameFilter;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.clean.spaceplus.cleansdk.util.CleanCloudScanHelper;
import com.clean.spaceplus.cleansdk.util.EnableCacheListDir;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.clean.spaceplus.cleansdk.util.HeapSort;
import com.clean.spaceplus.cleansdk.util.ResUtil;
import com.clean.spaceplus.cleansdk.util.SDCardUtil;
import com.clean.spaceplus.cleansdk.util.StringUtils;
import com.clean.spaceplus.cleansdk.util.TimingUtil;
import com.clean.spaceplus.cleansdk.util.md5.MD5PackageNameConvert;
import com.hawkclean.framework.log.NLog;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.TreeSet;

import space.network.cleancloud.CleanCloudDef;
import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.KResidualCloudQuery.DirQueryData;

/**
 * @author Jerry
 * @Description:
 * @date 2016/4/30 11:02
 * @copyright TCL-MIG
 */
public class RubbishFileScanTask extends BaseStub implements IScanFilter {
    public static final String TAG = RubbishFileScanTask.class.getSimpleName();
    // 是否计算size
    public static final int RES_FILE_SCAN_CFG_MASK_CALC_SIZE = 0x00000001; // /<
    // 是否扫描空文件夹
    public static final int RES_FILE_SCAN_CFG_MASK_SCAN_EMPTY_FOLER = 0x00000002; // /<
    // 是否扫描大文件类别
    public static final int RES_FILE_SCAN_CFG_MASK_SCAN_BIG_FILE = 0x00000004; // /<
    // 是否扫描临时文件类别
    public static final int RES_FILE_SCAN_CFG_MASK_SCAN_TEMP_FILE = 0x00000008; // /<
    // 是否扫描广告文件类别
    public static final int RES_FILE_SCAN_CFG_MASK_SCAN_ADV_FILE = 0x00000010; // /<

    // RES_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE
    // 默认配置是不在ADD_CHILDREN_DATA_ITEM_TO_ADAPTER的消息回调中返回ignore item
    // 如果将该配置取非，那么白名单中的项也会被作为结果返回，可以通过JunkInfoBase的isIgnore()查询是否是ignore item
    public static final int RES_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE = 0x00000020;
    // 是否计算选中项size，若RES_FILE_SCAN_CFG_MASK_CALC_SIZE开关处于关闭状态，本开关无效。
    public static final int RES_FILE_SCAN_CFG_MASK_CALC_CHECKED_SIZE = 0x00000040; // /<
    // 是否计算未选中项size，若RES_FILE_SCAN_CFG_MASK_CALC_SIZE开关处于关闭状态，本开关无效。
    public static final int RES_FILE_SCAN_CFG_MASK_CALC_UNCHECKED_SIZE = 0x00000080; // /<
    // 是否从数据库中查询不带有alertinfo的记录
    public static final int RES_FILE_SCAN_CFG_MASK_QUERY_WITHOUT_ALERTINFO = 0x00000100; // /<
    // 是否从数据库中查询带有alertinfo的记录
    public static final int RES_FILE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO = 0x00000200; // /<
    // 是否扫描残留
    public static final int RES_FILE_SCAN_CFG_MASK_SCAN_REMAIN_INFO = 0x00000400; // /<
    // 是否扫描dalvik-cache
    public static final int RES_FILE_SCAN_CFG_MASK_SCAN_DALVIK_CACHE = 0x00000800; // /<

    public static final int RES_FILE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS	= 0x00001000;   //  是否 检查扫描结果 用户设置的锁定状态    默认 不检查

    public static final int RES_FILE_SCAN_CFG_MASK_NOT_COUNT_REMAIN_TARGET_MEDIA_FILE_NUM = 0x00002000; ///< 是否计算残留目标文件夹下的媒体文件数

    public static final int RES_FILE_SCAN_CFG_MASK_NOT_QUERY_BIG_FILE_FROM = 0x00004000; ///< 是否获取大文件来源

    //是否扫描建议清理的临时文件
    public static final int RES_FILE_SCAN_CFG_MASK_SCAN_STD_TEMP_FILE = 0x00008000;
    //是否扫描DCIM/.THUMBNAILS的临时文件 注意，该任务跟ThumbnailScanTask没有什么关系，这个是对整个文件内容进行扫描，而ThumbnailScanTask仅仅对无用缩略图进行扫描
    public static final int RES_FILE_SCAN_CFG_MASK_SCAN_DCIM_THUMBNAIL_FOLDER = 0x00010000;	///< 是否扫描DCIM/.THUMBNAILS的临时文件

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int SCAN_SDCARD_INFO = 0x0000001;
    public static final int SCAN_FINISH = 0x0000002; // /<
    // 扫描结束，若因超时结束，则arg1值为1，否则为0。
    public static final int RUB_FILE_SCAN_PROGRESS_START = 0x0000003; // /<
    // 开始进度计算，arg1值为类别
    public static final int RUB_FILE_SCAN_PROGRESS_STEP_NUM = 0x0000004; // /<
    // 进度计算，arg1值为类别，arg2为此类别的总步数
    public static final int RUB_FILE_SCAN_PROGRESS_ADD_STEP = 0x0000005; // /<
    // 进度计算，arg1类别加一步
    public static final int ADD_CHILDREN_DATA_ITEM_TO_ADAPTER = 0x0000006; // /<
    // 添加
    public static final int UPDATE_CHILDREN_DATA_ITEM_TO_ADAPTER = 0x0000007; // /<
    // 添加或更新
    public static final int RUB_FILE_SCAN_IGNORE_ITEM = 0x0000008;

    public static final int RUB_FILE_SCAN_TEMP_FILE_FINISHED = 0x0000009;	//temp file scan finished

    public static final int RUB_FILE_SCAN_LEFT_OVER_FINISHED = 0x000000A;

    public static final int RUB_FILE_SCAN_ADV_FINISHED 		= 0x00000B;

    public static final int RUB_FILE_SCAN_COMING_SOON_SIZE	= 0x00000C;

    private static final int MAX_RESIDUAL_CLOUD_QUERY_WAIT_TIME = 3 * 60 * 1000;//云端残留扫描最多等待3分钟
    public static final String CLEAN_CLOUD_RESIDUAL_ID_FILTER_NAME = "cc_r";


    private static final int MAXDEEPCONTROLTIMES = 4;

    /**
     * 屏蔽掉不扫描的【广告 临时文件等
     */
    private int mScanCfgMask = 0;
    private int mCleanCloudScanType = 0;
    private byte mHaveNotCleaned = 2;


    public static final int STANDARD_SCAN  = 1;//CleanCloudDetectReportor.STANDRAND_SCAN;
    public static final int ADVANVCED_SCAN = 2;//CleanCloudDetectReportor.ADVANVCED_SCAN;

    private final String mDefSdCardRootPath = StringUtils.toLowerCase(FileUtils.addSlash(
            Environment.getExternalStorageDirectory().getAbsolutePath()));
//    private String[] mSDCardCachePathArray = null;

    private HashMap<String, ProcessModel> mRFWhiteListMap = new HashMap<>();

    List<CloudQueryer> mCloudQueryers = null;
    private ScanCommonStatus mScanCommonStatus = null;
    private boolean mIsBackgroundScan = true;

    private volatile int mTotalProgressStep = 0;
    private static final String[] SCAN_SECOND_SDCARD_ANDROID_DATA_FILTER_LIST = {
            "com.sqage.wohucanglong"
    };
    RubbishFileFilterImpl mFilter = new RubbishFileFilterImpl();
    private List<SDcardRubbishResult> mListAppLeftovers = new ArrayList<>();

    private PackageCheckerForCloudQuery mPackageCheckerForCloudQuery;
    private MD5PackageNameConvert md5PackageNameConvert = new MD5PackageNameConvert();
    private ScanTask mRubbishScanTaskCachedRst = null;

    public RubbishFileScanTask() {
        mPackageCheckerForCloudQuery =  new PackageCheckerForCloudQuery(md5PackageNameConvert);
    }

    public void setScanConfigMask(int mask) {
        mScanCfgMask = mask;
    }

    public int getScanConfigMask() {
        return mScanCfgMask;
    }


    public void setScanCommonStatus(ScanCommonStatus scanCommonStatus) {
        mScanCommonStatus = scanCommonStatus;
    }

    public void setIsBackgroundScan(boolean value) {
        mIsBackgroundScan = value;
    }




    public static class UpdateChildrenData {
        public SDcardRubbishResult oldObj = null;
        public SDcardRubbishResult newObj = null;

        public UpdateChildrenData(SDcardRubbishResult oldObj, SDcardRubbishResult newObj) {
            this.oldObj = oldObj;
            this.newObj = newObj;
        }

        @Override
        public String toString() {
            return "UpdateChildrenData{" +
                    "oldObj=" + oldObj +
                    ", newObj=" + newObj +
                    '}';
        }
    }


    @Override
    public boolean scan(ScanTaskController ctrl) {
        NLog.d(TAG, "RubbishFileScanTask begin scan");
        // 当前SD卡可写可读
        boolean sdCardCanWrite = SDCardUtil.isHaveSDCard();
        boolean fileUtilLoadSuccess = true;
        try {

            if (null != mRubbishScanTaskCachedRst) {
                mRubbishScanTaskCachedRst.scan(ctrl);
            }

            // 注意，下面几步扫描顺序要确保scanBigFiles在最后，因为它要过滤掉前面的扫描结果。
            do {
                if (null != ctrl && ctrl.checkStop()) break;
                //残留扫描
                if (sdCardCanWrite && fileUtilLoadSuccess) {
                    getRubbishOnDoubleSDcard(ctrl);
                }
                if (null != ctrl && ctrl.checkStop()){
                    break;
                }

                //广告
               scanInternAdv(ctrl);

                if (null != ctrl && ctrl.checkStop()){
                    break;
                }

                //weixin临时文件
                scanInternStdTemp(ctrl);

                if (null != ctrl && ctrl.checkStop()) {
                    break;
                }

                //建议清理的扩展扫描，内部没有实现
                //scanInternDexExt(ctrl);
                if (null != ctrl && ctrl.checkStop()){
                    break;
                }

                //深度清理扩展，内部未实现
//                scanInternAdvDexExt(ctrl);

                if (null != ctrl && ctrl.checkStop()) break;
                //dalvik-cache 文件扫描,会提示root权限申请
//                scanInternAppLeftOver(ctrl);

                if (null != ctrl && ctrl.checkStop()) break;
                //相册缩略图、空文件夹
                scanInternTempFile(ctrl);

                if (null != ctrl && ctrl.checkStop()) break;
                if (sdCardCanWrite && fileUtilLoadSuccess) {
                    waitCompleteResidualScan(ctrl);
                }

            } while (false);//*???

        } finally {
            if (null != mCB) {//回调JunkAdvancedScan的RubbishFileScanTask定义时候的callback
                int arg1  = 0;
                if (null != ctrl && ScanTaskController.TASK_CTRL_TIME_OUT == ctrl.getStatus()){
                    arg1 = 1;
                }
                mCB.callbackMessage(SCAN_FINISH, arg1, 0, null);
            }

        }

        return true;
    }




    private void getRubbishOnDoubleSDcard(final ScanTaskController ctrl) {

        if (0 == (RES_FILE_SCAN_CFG_MASK_SCAN_REMAIN_INFO & mScanCfgMask)) {
            return;
        }

        //Log.i(TAG, "Residual Scan begin");
        File firstSdcardRootDir = Environment.getExternalStorageDirectory();
        File secondSdcardRootDir = getMountedThe2ndSdCardRootDir();

        if ( firstSdcardRootDir == null && secondSdcardRootDir == null ) {
            return;
        }
        mCloudQueryers = new ArrayList<>();
        mCloudQueryers.add( new CloudQueryer(firstSdcardRootDir.getAbsolutePath(), ctrl, true) );
        if ( secondSdcardRootDir != null ) {
            mCloudQueryers.add( new CloudQueryer(secondSdcardRootDir.getAbsolutePath(), ctrl, false) );
        }

        loadAllRFWhiteList();

        mTotalProgressStep = 0;

        ///////////////////////////////////////////////////////
        //残留云也是使用升级逻辑进行库释放，所以统一等待文件释放完成
        FileUtils.controlWait();
        String lang = CleanCloudScanHelper.getCurrentLanguage();
        int scanType = getScanType();
        int mask = mScanCfgMask;
        int result = KResidualCloudQuery.DirScanType.DIR_INVAILD_SCAN;
        if ((0 != (mask & RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_QUERY_WITHOUT_ALERTINFO))
                && (0 != (mask & RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO))) {
            result = KResidualCloudQuery.DirScanType.DIR_ALL_SCAN;
            NLog.d(TAG, "扫描所有");
        } else if (0 != (mask & RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_QUERY_WITHOUT_ALERTINFO)) {
            result = KResidualCloudQuery.DirScanType.DIR_STANDARD_SCAN;
            NLog.d(TAG, "建议扫描");
            boolean isFristCleaned = (mScanCommonStatus != null) ? mScanCommonStatus.getIsFirstCleanedJunkStandard() : false;
            if (isFristCleaned) {
            } else {
            }
        } else if (0 != (mask & RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO)) {
            result = KResidualCloudQuery.DirScanType.DIR_ADVANVCED_SCAN;
            NLog.d(TAG, "深度扫描");
            boolean isFristCleaned = (mScanCommonStatus != null) ? mScanCommonStatus.getIsFirstCleanedJunkAdvanced() : false;
        }
        mCleanCloudScanType = result;
        for ( CloudQueryer cloudQueryer : mCloudQueryers ) {
            File[] arrayOfFile = GetAllFolderOnSdcard(cloudQueryer.mSdcardPath);
            if (null == arrayOfFile) {
                continue;
            }

            if ( cloudQueryer.mbScanDefaultSdCard ) {
                scanALO(arrayOfFile, cloudQueryer.mSdcardPath, ctrl,  cloudQueryer.mbScanDefaultSdCard, cloudQueryer );
            }else {
                if (!CloudCfgDataWrapper.getCloudCfgBooleanValue(
                        CloudCfgKey.JUNK_SCAN_FLAG_KEY,
                        CloudCfgKey.JUNK_2ND_SD_ALO_RUBBISH3,
                        false)) {
                    continue;
                }
                scanALO(arrayOfFile, cloudQueryer.mSdcardPath, ctrl, cloudQueryer.mbScanDefaultSdCard, cloudQueryer );
                scanAndroidData(cloudQueryer.mSdcardPath, ctrl);
            }
        }
    }

    private int getScanType() {
        return (0 == (mScanCfgMask & RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO)) ?
                STANDARD_SCAN : ADVANVCED_SCAN;
    }

    private void scanAndroidData(String sdcardPath, final ScanTaskController ctrl) {
        // 扫描第二张卡Android/data目录
        File androidData = new File(sdcardPath, "Android/data");
        if (!androidData.isDirectory()) {
            return;
        }

        if (null != ctrl && ctrl.checkStop()) {
            return;
        }

        String name = ResUtil.getString(R.string.junk_tag_junk_android_data_2nd_card_left_overs);
        PathOperFunc.StringList subs = EnableCacheListDir.listDir(androidData.getPath());

        if (null == subs) {
            return;
        }

        try {

            List<PackageInfo> packageInfoList = PackageManagerWrapper.getInstance().getPkgInfoList();

            TreeSet<String> strAppNameSet = new TreeSet<> ();

            if (null != packageInfoList) {
                int allPkgSize = packageInfoList.size();
                for (int i = 0; i < allPkgSize; ++i) {
                    PackageInfo pi = packageInfoList.get(i);
                    if (null == pi) {
                        continue;
                    }
                    ApplicationInfo appInfo = pi.applicationInfo;
                    if (null == appInfo) {
                        continue;
                    }
                    strAppNameSet.add(appInfo.packageName);
                }
            }

            if (null != subs && !strAppNameSet.isEmpty()) {
                for (String str : subs) {
                    if (TextUtils.isEmpty(str) || str.length() < 3 || str.indexOf('.', 1) < 0) {
                        continue;
                    }

                    if (strAppNameSet.contains(str)) {
                        continue;
                    }

                    if (isInSecondSdCardAndroidDataFilter(str)) {
                        continue;
                    }

                    addResidualDetectResult(
                            androidData.getPath() + File.separator + str,
                            null,
                            0,
                            name,
                            name,   //targetPkgName
                            0,
                            false,
                            mListAppLeftovers,
                            ctrl,
                            false,
                            null,
                            (byte)-1,
                            0);

                }
            }
        } finally {
            subs.release();
            subs = null;
        }
    }

    CalcFolderSizeHelper mCalcFolderSizeHelper = new CalcFolderSizeHelper();

    private SDcardRubbishResult addResidualDetectResult(String filepath, String alertinfo,
                                                        int signId, String name, String targetPkgName, int cleanMediaFlag,
                                                        boolean isIgnoreItem, List<SDcardRubbishResult> listAppLeftovers,
                                                        final ScanTaskController ctrl, boolean scanDefaultSdCard,
                                                        List<String> filterSubDirList, byte resultSource, int cleanType) {
        SDcardRubbishResult info = new SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER);
        NLog.d(TAG, "addResidualDetectResult listAppLeftovers = "+listAppLeftovers);
        if (mFilter != null) {
            info.setRubbishCleanTime(mFilter.getRubbishCleanTime());
            info.setRubbishFilterData(mFilter.getFilterData());
        }
        info.setStrDirPath(filepath);
        info.set2ndSdCardRubbishFlag(!scanDefaultSdCard);
        info.addFilterSubFolderList(filterSubDirList);
        info.setCleanType(cleanType);
        boolean bChecked = true;
        if (!TextUtils.isEmpty(alertinfo)) {
            //bChecked = false;
        }
        info.setCheck(bChecked);
        info.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
        if (!info.isCheck()) {
            info.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
            info.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV);
        }
        info.setResultSource(resultSource);
        long fileCompute[] = new long[3];
        fileCompute[0] = 0;
        fileCompute[1] = 0;
        fileCompute[2] = 0;

        info.setCleanFileFlag(cleanMediaFlag);

        boolean bCalcSize = false;
        if (0 != (mScanCfgMask & RES_FILE_SCAN_CFG_MASK_CALC_SIZE)) {
            if ((0 != (RES_FILE_SCAN_CFG_MASK_CALC_CHECKED_SIZE & mScanCfgMask) && bChecked)
                    || (0 != (RES_FILE_SCAN_CFG_MASK_CALC_UNCHECKED_SIZE & mScanCfgMask) && !bChecked)) {
                bCalcSize = true;
                long[] mediaInfo = null;
                if (0 == (RES_FILE_SCAN_CFG_MASK_NOT_COUNT_REMAIN_TARGET_MEDIA_FILE_NUM & mScanCfgMask)) {
                    mediaInfo = new long [3];
                }
                MediaFileCounter counter = new MediaFileCounter();
                PathOperFunc.CalcSizeCallback calcCallback =
                        new PathOperFunc.CalcSizeCallback(ctrl, 60L * 1000L, 32);
                long calcTime = calcCallback.start();
                boolean [] msInfo = new boolean[2];

                ///> 深度清理中暂不使用时间线和后缀名检查机制
                if (counter != null && cleanType != KResidualCloudQuery.DirCleanType.CAREFUL && cleanType != KResidualCloudQuery.DirCleanType.CAREFUL_WITH_FILTER) {
                    counter.setRubbishFilterInterface(mFilter);
                }
                mCalcFolderSizeHelper.computeFileSize(filepath, fileCompute, calcCallback, counter, mediaInfo, filterSubDirList, msInfo);

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

        if (null != ctrl && ctrl.checkStop()) {
            return info;
        }

        info.setChineseName(name);
        info.setAlertInfo(alertinfo);

        if (fileCompute[0] > 0) {
            info.setSize(fileCompute[0]);
        } else if ((!scanDefaultSdCard) && Build.VERSION.SDK_INT >= 19) {
            // 规辟4.4上第二张卡只能删除文件，删不掉文件夹的情况。这样的空文件夹就不要检出了。
            return info;
        } else if (null != filterSubDirList && !filterSubDirList.isEmpty() &&
                bCalcSize && 0 == fileCompute[0] && 0 == fileCompute[2]) {
            return info;
        }

        info.setFoldersCount(fileCompute[1]);
        info.setFilesCount(fileCompute[2]);
        info.setApkName(name);
        info.setType(SDcardRubbishResult.RF_APP_LEFTOVERS);
        info.setSignId(signId);
        if (isIgnoreItem) {
            info.setIgnore(true);
        }

        info.setCheck(bChecked);

        checkLocked(info, info.getSignId());
        SDcardRubbishResult oldInfo = null;
        int idx = 0;
        boolean shouldAdding = true;
        synchronized(listAppLeftovers) {
            if (info.getScanType() == BaseJunkBean.SCAN_TYPE_STANDARD) {
                // apkName 相同的项合并
                for (idx = 0; idx < listAppLeftovers.size(); ++idx) {
                    SDcardRubbishResult tempInfo = listAppLeftovers.get(idx);
                    if (0 == info.getName().compareToIgnoreCase(tempInfo.getName())) {
                        if (!tempInfo.getPathList().contains(info.getStrDirPath())) {
                            if (tempInfo.getPathList().isEmpty()) {
                                // 初始化多项列表
                                info.addPathList(tempInfo.getStrDirPath(),tempInfo.getCleanFileFlag());
                            } else {
                                for (SDcardRubbishResult.PathInfo n : tempInfo.getPathInfoList()) {
                                    info.addPathInfo(n);
                                }
                            }
                            info.addPathList(info.getStrDirPath(),info.getCleanFileFlag());
                            info.setFilesCount(tempInfo.getFilesCount()
                                    + info.getFilesCount());
                            long tmpSize = tempInfo.getSize() + info.getSize();
                            if (tmpSize < 0) {
                                tmpSize = 0l;
                            }
                            info.setSize(tmpSize);
                            info.setFoldersCount(tempInfo.getFoldersCount()
                                    + info.getFoldersCount());
                            boolean bCheck = tempInfo.isCheck() && info.isCheck();
                            if (info.isCheck() && !bCheck) {
                                info.setAlertInfo(tempInfo.getAlertInfo());
                            }
                            info.setCheck(bCheck);
                        }
                        shouldAdding = false;
                        oldInfo = tempInfo;
                        break;
                    }
                }
            }
            if (shouldAdding) {
                listAppLeftovers.add(info);
            } else {
                listAppLeftovers.set(idx, info);
            }
        }

        if (shouldAdding) {
            if (null != mCB) {
                mCB.callbackMessage(ADD_CHILDREN_DATA_ITEM_TO_ADAPTER,
                        SDcardRubbishResult.RF_APP_LEFTOVERS, 0, info);
                NLog.e(TAG, "addResidualDetectResult 找到了残留文件 添加: info = " + info);
            }
        } else {
            if (null != mCB) {
                mCB.callbackMessage(UPDATE_CHILDREN_DATA_ITEM_TO_ADAPTER,
                        SDcardRubbishResult.RF_APP_LEFTOVERS, 0,
                        new UpdateChildrenData(oldInfo, info));
                NLog.e(TAG, "addResidualDetectResult 找到了残留文件 更新: info = " + info);
            }
        }

        return info;
    }



    boolean isInSecondSdCardAndroidDataFilter(String path) {
        boolean result = false;
        String lowerPath = StringUtils.toLowerCase(path);
        for (String filter : SCAN_SECOND_SDCARD_ANDROID_DATA_FILTER_LIST) {
            if (filter.compareTo(lowerPath) == 0) {
                result = true;
                break;
            }
        }
        return result;
    }


    /**
     *
     * @param arrayOfFile 目录数组，不需要再判断是否为一个目录
     * @param sdcardPath
     * @param ctrl
     * @param scanDefaultSdCard
     */
    private void scanALO(File[] arrayOfFile, String sdcardPath,
                         ScanTaskController ctrl,
                         boolean scanDefaultSdCard, CloudQueryer cloudQueryer ) {
        scanResidualByCloud(arrayOfFile, sdcardPath, ctrl, scanDefaultSdCard, cloudQueryer );
    }

    private void scanResidualByCloud(File[] arrayOfFile, String sdcardPath,
                                     final ScanTaskController ctrl, boolean scanDefaultSdCard, CloudQueryer cloudQueryer) {
        //Log.e(TAG, "scanResidualByCloud begin:");
        if (TextUtils.isEmpty(sdcardPath))
            return;
        int sdcardDirRootPos = sdcardPath.length() + 1;
        int size = arrayOfFile.length;

        synchronized(cloudQueryer.mResidualCloudResult) {
            cloudQueryer.mResidualCloudResult.clear();
        }

        mTotalProgressStep += size;

        if (mCB != null) {
            mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_STEP_NUM, SDcardRubbishResult.RF_APP_LEFTOVERS, mTotalProgressStep, null);
        }

        LinkedList<String> queryDirs = new LinkedList<>();
        for (int i = 0; i < size; ++i) {
            if (null != ctrl && ctrl.checkStop()) {
                return;
            }

            File currentFile = arrayOfFile[i];
            String dirPath = currentFile.getPath();
            String str = dirPath.substring(sdcardDirRootPos, dirPath.length());
            queryDirs.add(str);
        }

        if (!queryDirs.isEmpty() && !ctrl.checkStop()) {

            cloudQueryer.mResidualCloudQuery.queryByDirName(mCleanCloudScanType, queryDirs, cloudQueryer.mResidualCloudQueryCallback, true, false);
        }
    }


    private File[] GetAllFolderOnSdcard( String sdcardRootDir) {
        // 得到设备的SDcard目录
        File[] arrayOfFile = null;
        PathOperFunc.FilesAndFoldersStringList fileAndFolderList = EnableCacheListDir.listDir( sdcardRootDir );
        if (fileAndFolderList == null) {
            return null;
        }
        PathOperFunc.StringList folderList = fileAndFolderList.getFolderNameList();
        if ( folderList != null ) {
            arrayOfFile = new File[folderList.size()];
            for (int i = 0; i < folderList.size(); i++) {
                arrayOfFile[i] = new File(sdcardRootDir, folderList.get(i));
            }
            folderList.release();
            folderList = null;
        }
        fileAndFolderList.release();
        fileAndFolderList = null;
        return arrayOfFile;
    }




    private File getMountedThe2ndSdCardRootDir() {

        List<String> removalbeSdCardList = new StorageList().getMountedVolumePaths();
        if (null == removalbeSdCardList || removalbeSdCardList.isEmpty()) {
            return null;
        }

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return null;
        }

        File defaultSdCard = Environment.getExternalStorageDirectory();
        if (null == defaultSdCard) {
            return null;
        }

        String defaultSdCardPath = FileUtils.addSlash(defaultSdCard.getPath());
        for (String path : removalbeSdCardList) {
            path = FileUtils.addSlash(path);
            if (isTheSameCard(defaultSdCardPath, path)) {
                continue;
            }
            return new File(path);
        }
        return null;
    }


    private boolean isTheSameCard(String defaultSdCardPath, String path) {
        assert(!TextUtils.isEmpty(defaultSdCardPath));
        assert(!TextUtils.isEmpty(path));
        if (path.equals(defaultSdCardPath)) {
            return true;
        }

        String[] defSubNames = getDefSubNames(defaultSdCardPath);
        if (null == defSubNames || defSubNames.length == 0) {
            return true;
        }

        PathOperFunc.StringList filesList = EnableCacheListDir.listDir(path);
        if ( filesList == null ) {
            return true;
        }

        if ( filesList.size() == defSubNames.length ) {
            String[] rst = new String[filesList.size()];
            for (int i = 0; i < filesList.size(); ++i) {
                rst[i] = filesList.get(i);
            }
            if ( Arrays.equals(rst, defSubNames) ) {
                filesList.release();
                filesList = null;
                return true;
            }
        }
        filesList.release();
        filesList = null;
        return false;
    }

    private SoftReference<String[]> mDefSubNames = null;
    private String[] getDefSubNames(String defaultSdCardPath) {

        String[] rst = null;
        if (null != mDefSubNames) {
            rst = mDefSubNames.get();
        }

        if (null != rst) {
            return rst;
        }

        PathOperFunc.StringList defSubs = EnableCacheListDir.listDir(defaultSdCardPath);
        if (null == defSubs) {
            return null;
        }

        try {
            int size = defSubs.size();
            if (size == 0) {
                return null;
            }

            rst = new String[size];
            for (int i = 0; i < size; ++i) {
                rst[i] = defSubs.get(i);
            }
        } finally {
            defSubs.release();
        }

        mDefSubNames = new SoftReference<>(rst);
        return rst;
    }



    void waitCompleteResidualScan( final ScanTaskController ctrl ) {

        if (0 == (RES_FILE_SCAN_CFG_MASK_SCAN_REMAIN_INFO & mScanCfgMask)) {
            return;
        }

        if ( mCloudQueryers == null ) {
            return;
        }
        for (CloudQueryer cloudQueryer : mCloudQueryers) {
            cloudQueryer.waitForComplete(ctrl);
        }
        //Log.i(TAG, "Residual Scan end");
    }



    private void scanInternTempFile(ScanTaskController ctrl){
        int maskConfig = RES_FILE_SCAN_CFG_MASK_SCAN_TEMP_FILE & mScanCfgMask;
        NLog.d(TAG, "scanInternTempFile maskConfig = %d ",maskConfig);
        // 临时文件
        if (0 != (maskConfig)) {
            try {
                scanTempFile(ctrl);
            } finally {
            }
        }
    }


    private void scanTempFile(final ScanTaskController ctrl) {

        if (null != ctrl && ctrl.checkStop()) {
            return;
        }

        Context ctx = SpaceApplication.getInstance().getContext();
        List<SDcardRubbishResult> thumbnailList = new ArrayList<>();
        scanGallaryTempFile(ctrl,ctx,thumbnailList);
        scanAllEmptyFolders(ctrl,ctx,thumbnailList);

        if (null != mCB) {
            mCB.callbackMessage(RUB_FILE_SCAN_TEMP_FILE_FINISHED,
                    SDcardRubbishResult.RF_TEMPFILES, 0, null);
        }
    }

    private void scanGallaryTempFile(final ScanTaskController ctrl,Context ctx,List<SDcardRubbishResult> thumbnailList){
        Queue<TempFileTarget> targetQueue = initTempTarget(ctx);
        if (null == targetQueue) {
            return;
        }

        int scanStepNum = 50; //default set to a constant instead of calculating directories on SD card
        if (null != targetQueue) {
            scanStepNum += targetQueue.size();
        }

        if (null != mCB) {
            mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_START, SDcardRubbishResult.RF_TEMPFILES, 0, null);
            mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_STEP_NUM, SDcardRubbishResult.RF_TEMPFILES, scanStepNum * 4,
                    null);
        }

        if (null != targetQueue && !targetQueue.isEmpty()) {
            loadAllRFWhiteList();
            String targetFullPath = null;
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            TempFileTarget target = targetQueue.poll();

            do {
                if (null != ctrl && ctrl.checkStop()) {
                    break;
                }
                targetFullPath = sdcardPath + target.targetPath;
                if (null != mCB) {
                    mCB.callbackMessage(SCAN_SDCARD_INFO, 0, 0, target.targetPath);
                }
                boolean isIgnoreItem = false;
                File targetPathFile = new File(targetFullPath);
                if (targetPathFile.exists()) {

                    // 白名单
                    if (isFilter(targetFullPath)) {
                        isIgnoreItem = true;
                        if (null != mCB) {
                            mCB.callbackMessage(RUB_FILE_SCAN_IGNORE_ITEM, 0, 0, targetFullPath);
                        }
                        if ((mScanCfgMask & RES_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE) != 0) {
                            target = targetQueue.poll();
                            if (null != mCB) {
                                mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_ADD_STEP, SDcardRubbishResult.RF_TEMPFILES,
                                        0, null);
                            }
                            continue;
                        }
                    }

                    boolean calcSize = false;
                    long fileCompute[] = new long[3];
                    fileCompute[0] = 0;
                    fileCompute[1] = 0;
                    fileCompute[2] = 0;
                    if (0 != (RES_FILE_SCAN_CFG_MASK_CALC_SIZE & mScanCfgMask)) {
                        if ((0 != (RES_FILE_SCAN_CFG_MASK_CALC_CHECKED_SIZE & mScanCfgMask) && target.checked)
                                || (0 != (RES_FILE_SCAN_CFG_MASK_CALC_UNCHECKED_SIZE & mScanCfgMask) && !target.checked)) {
                            calcSize = true;
                            PathOperFunc.CalcSizeCallback calcCallback =
                                    new PathOperFunc.CalcSizeCallback(ctrl, 60L * 1000L, 32);
                            long calcTime = calcCallback.start();
                            PathOperFunc.computeFileSize(targetPathFile.getPath(), fileCompute, calcCallback);
                        }
                    }

                    if (null != ctrl && ctrl.checkStop()) {
                        break;
                    }

                    if (fileCompute[0] > 0 || (!calcSize)) {
                        SDcardRubbishResult info = new SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER);
                        info.setStrDirPath(targetFullPath);
                        info.setChineseName(ctx.getResources().getString(target.targetNameSrcId));
                        if (calcSize) {
                            info.setSize(fileCompute[0]);
                        }
                        info.setFoldersCount(fileCompute[1]);
                        info.setFilesCount(fileCompute[2]);
                        info.setApkName(ctx.getResources().getString(target.targetNameSrcId));
                        info.setCheck(target.checked);
                        info.setType(SDcardRubbishResult.RF_TEMPFILES);
                        info.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
                        if (!target.bStdJunk) {
                            info.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
                            info.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV);
                        }

                        //临时文件 根据目录判定
                        checkLocked(info,targetFullPath);

                        if (isIgnoreItem) {
                            info.setIgnore(true);
                        }
                        thumbnailList.add(info);


                        if (null != mCB) {
                            mCB.callbackMessage(ADD_CHILDREN_DATA_ITEM_TO_ADAPTER, SDcardRubbishResult.RF_TEMPFILES, 0,
                                    info);
                        }
                    }
                }

                target = targetQueue.poll();
                if (null != mCB) {
                    mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_ADD_STEP, SDcardRubbishResult.RF_TEMPFILES, 0, null);
                }
            } while (null != target);
        }
        targetQueue = null;
    }
    private void scanAllEmptyFolders(final ScanTaskController ctrl,Context ctx,List<SDcardRubbishResult> thumbnailList){
        if (true || 0 != (RES_FILE_SCAN_CFG_MASK_SCAN_EMPTY_FOLER & mScanCfgMask) ) {
            try {
                if (Build.VERSION.SDK_INT >= 11) {
                    scanEmptyFoldersByMediaStore(ctrl, ctx, thumbnailList);
                } else {
                    scanEmptyFolders(ctrl, ctx, thumbnailList);
                }
            } finally {
            }
        }
    }



//    private void scanInternAdvDexExt(ScanTaskController ctrl){
//        int maskConfig = RES_FILE_SCAN_CFG_MASK_SCAN_TEMP_FILE & mScanCfgMask;
//        NLog.d(TAG, "scanInternAdvDexExt maskConfig1 = %d", maskConfig);
//        if (0 == (maskConfig)) {
//            return;
//        }
//        maskConfig = RES_FILE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO & mScanCfgMask;
//        NLog.d(TAG, "scanInternAdvDexExt maskConfig2 = %d", maskConfig);
//        // 高级清理的扩展扫描 第一期不做
//        if (0 != (maskConfig)) {
//            try {
//                scanFileByExt(ctrl,SpaceApplication.getInstance().getContext(), false);
//            } finally {
//            }
//        }
//    }

//    private void scanInternDexExt(ScanTaskController ctrl){
//        int maskConfig = RES_FILE_SCAN_CFG_MASK_SCAN_STD_TEMP_FILE & mScanCfgMask;
//        NLog.d(TAG, "scanInternDexExt maskConfig1 = %d ",maskConfig);
//        if (0 == maskConfig) {
//            return;
//        }
//        maskConfig = RES_FILE_SCAN_CFG_MASK_QUERY_WITHOUT_ALERTINFO & mScanCfgMask;
//        NLog.d(TAG, "scanInternDexExt maskConfig2 = %d ",maskConfig);
//        // 建议清理的扩展扫描
//        if (0 != (maskConfig)) {
//            scanFileByExt(ctrl, SpaceApplication.getInstance().getContext(), true);
//        }
//    }

//    private void scanFileByExt(final ScanTaskController ctrl, Context ctx, final boolean bAdvice){
//        ClassFactory clsFty = (ClassFactory) DexLoaderUtil.createInstance(ctx, DexLoaderUtil.CLS_JUNK_FACTORY);
//        if (clsFty != null && clsFty.getVersion() >= ClassFactory.VERSION ) {
//            JunkScanner junkScanner = clsFty.createJunkScanner();
//            if ( junkScanner != null ){
//                int type = 0;
//                if (bAdvice){
//                    type = JunkScanner.TYPE_STARND;
//                }
//                else{
//                    type = JunkScanner.TYPE_ADVANCE;
//                }
//                junkScanner.startScan(ctx, type, new JunkScanCallback() {
//
//                    @Override
//                    public boolean onProgress(String str, int nProgress) {
//                        return ctrl.checkStop();
//                    }
//
//                    @Override
//                    public void onFoundItem(JunkExtItem item) {
//                        if (item != null){
//                            SDcardRubbishResult sdcardResult = RubbishExtConvert.convert(ctrl, item);
//                            if  (!isFilter(sdcardResult.getWhiteListKey()) ){
//                                if (null != mCB && sdcardResult != null ) {
//                                    sdcardResult.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
//                                    if (bAdvice)
//                                        checkLocked(sdcardResult, sdcardResult.getSignId());
//                                    else {
//                                        sdcardResult.setCheck(false);
//                                        sdcardResult.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
//                                        sdcardResult.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV);
//                                    }
//                                    mCB.callbackMessage(ADD_CHILDREN_DATA_ITEM_TO_ADAPTER, SDcardRubbishResult.RF_TEMPFILES, 0,
//                                            sdcardResult);
//                                }
//                            }
//                        }
//                    }
//                });
//            }
//        }
//    }

    private void scanInternStdTemp(ScanTaskController ctrl){
        // 建议清理的临时文件
        int maskConfig = RES_FILE_SCAN_CFG_MASK_SCAN_STD_TEMP_FILE & mScanCfgMask;
        if (0 != maskConfig) {
            NLog.d(TAG, "scanInternStdTemp");
            try {
                scanStdTempFiles(ctrl);
            } finally {
            }
        }
    }

    private void scanStdTempFiles(final ScanTaskController ctrl) {
        if (null != ctrl && ctrl.checkStop()) {
            return;
        }
        // 当前SD卡可写可读
        if (!SDCardUtil.isHaveSDCard()){
            return;
        }
        Context ctx = SpaceApplication.getInstance().getContext();
        List<SDcardRubbishResult> thumbnailList = new ArrayList<>();
        loadAllRFWhiteList();
        scanWeChatDownload(ctrl, ctx, thumbnailList);

    }

    private void scanWeChatDownload(ScanTaskController ctrl, Context ctx, List<SDcardRubbishResult> thumbnailList) {
        final File wechatDownload = new File(Environment.getExternalStorageDirectory(), "Tencent/MicroMsg/Download");
        if (!wechatDownload.exists()) {
            return;
        }

        // 服务器控制过滤，防止误删。
       /* if (DBColumnFilterManager.getInstance().isFilter(
                DBColumnFilterManager.EXPAND_FILTER_TABLE_NAME_STUB,
                DBColumnFilterManager.EXPAND_FILTER_ID_WECHAT_DOWNLOAD_SCAN)) {
            return;
        }*/

        boolean isIgnoreItem = false;
        if (isFilter(WhiteListsWrapper.FUNCTION_FILTER_NAME_WECHAT_DOWNLOAD_SCAN)) {
            isIgnoreItem = true;
            if (null != mCB) {
                mCB.callbackMessage(RUB_FILE_SCAN_IGNORE_ITEM, 0, 0,
                        WhiteListsWrapper.FUNCTION_FILTER_NAME_WECHAT_DOWNLOAD_SCAN);
            }
            if ((mScanCfgMask & RES_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE) != 0) {
                return;
            }
        }
        //从这可以看出不扫描目录，只是扫描Tencent/MicroMsg/Download目录下以com.tencent.xin.emoticon.开头的文件, add comment by xiangxiang.liu
        PathOperFunc.StringList targets = EnableCacheListDir.listDir(wechatDownload.getPath(), new NameFilter() {

            @Override
            public boolean accept(String parent, String sub, boolean bFolder) {
                NLog.d(TAG, "accept parent = %s, sub = %s, bFolder = %b",parent, sub, bFolder);
                if (TextUtils.isEmpty(sub)) {
                    return false;
                }
                if (StringUtils.toLowerCase(sub).startsWith("com.tencent.xin.emoticon.")) {
                    return !bFolder;
                }

                return false;
            }
        });

        long size = 0L;
        SDcardRubbishResult lostDirTarget = null;
        try {

            if (targets != null){
                NLog.d(TAG, "scanWeChatDownload targets.size() = %d", targets.size());
            }
            if (null == targets || targets.size() == 0) {
                return;
            }

            lostDirTarget = new SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER);
            lostDirTarget.setFilesCount(targets.size());
            lostDirTarget.setCheck(true);
            lostDirTarget.setType(SDcardRubbishResult.RF_TEMPFILES);
            if (isIgnoreItem) {
                lostDirTarget.setIgnore(true);
            }
            lostDirTarget.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
            if (!lostDirTarget.isCheck()) {
                lostDirTarget.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
                lostDirTarget.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV);
            }

            File nowFile = null;
            size = 0L;
            for (String sub : targets) {
                nowFile = new File(wechatDownload, sub);
                size += nowFile.length();
                lostDirTarget.addPathList(nowFile.getPath());

            }
        } finally {
            if (null != targets) {
                targets.release();
            }
        }
        lostDirTarget.setSize(size);
        lostDirTarget.setChineseName(ResUtil.getString(R.string.junk_tag_RF_WechatDownload));
        lostDirTarget.setApkName(ctx.getResources().getString(R.string.junk_tag_RF_WechatDownload));

        if (null != mCB) {
            mCB.callbackMessage(ADD_CHILDREN_DATA_ITEM_TO_ADAPTER, SDcardRubbishResult.RF_TEMPFILES, 0,
                    lostDirTarget);
        }

        thumbnailList.add(lostDirTarget);
    }
    private void scanInternAdv(ScanTaskController ctrl){
        // 广告
        int maskConfig = RES_FILE_SCAN_CFG_MASK_SCAN_ADV_FILE & mScanCfgMask;
        NLog.d(TAG, "scanInternAdv maskConfig = %d", maskConfig);
        if (0 != (maskConfig)) {
            scanAdvFolder(ctrl);
        }
    }

    private void scanAdvFolder(final ScanTaskController ctrl) {
        if (null != ctrl && ctrl.checkStop()) {
            return;
        }
        AdvFolderScanTask task = new AdvFolderScanTask(this);
        task.bindCallbackObj(mCB);
        if (0 == (RES_FILE_SCAN_CFG_MASK_CALC_SIZE & mScanCfgMask)) {
            // 不算大小
            task.setScanConfigMask((~AdvFolderScanTask.ADV_FOLDER_SCAN_CFG_MASK_CALC_SIZE) & task.getScanConfigMask());
        }
        if (0 == (RES_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE & mScanCfgMask)) {
            // 扫描结果中回调ignore item
            task.setScanConfigMask((~AdvFolderScanTask.ADV_FOLDER_SCAN_CFG_MASK_NOT_RETURN_IGNORE)
                    & task.getScanConfigMask());
        }
        if (0 == (RES_FILE_SCAN_CFG_MASK_CALC_CHECKED_SIZE & mScanCfgMask)) {
            // 不算大小
            task.setScanConfigMask((~AdvFolderScanTask.ADV_FOLDER_SCAN_CFG_MASK_CALC_CHECKED_SIZE)
                    & task.getScanConfigMask());
        }
        if (0 == (RES_FILE_SCAN_CFG_MASK_CALC_UNCHECKED_SIZE & mScanCfgMask)) {
            // 不算大小
            task.setScanConfigMask((~AdvFolderScanTask.ADV_FOLDER_SCAN_CFG_MASK_CALC_UNCHECKED_SIZE)
                    & task.getScanConfigMask());
        }
        if (0 == (RES_FILE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS & mScanCfgMask)) {
            // 不算大小
            task.setScanConfigMask((~AdvFolderScanTask.ADV_FOLDER_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS)
                    & task.getScanConfigMask());
        }
        TimingUtil.start(task.getClass().getName());
        task.scan(ctrl);
    }


//    private void scanInternAppLeftOver(ScanTaskController ctrl){
//        // app无用文件
//        // 残留文件
//        int maskConfig = RES_FILE_SCAN_CFG_MASK_SCAN_REMAIN_INFO & mScanCfgMask;
//        NLog.d(TAG, "scanInternAppLeftOver maskConfig = %d", maskConfig);
//        if ( 0 != (maskConfig) )
//        {
//            try {
//                scanAppLeftovers(ctrl);
//                if (null != mCB) {
//                    mCB.callbackMessage(RUB_FILE_SCAN_LEFT_OVER_FINISHED,
//                            SDcardRubbishResult.RF_APP_LEFTOVERS, 0, null);
//                }
//            } finally {
//            }
//        }
//    }
//    private void scanAppLeftovers(ScanTaskController ctrl) {
//
//        if (null != mCB) {
//            mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_START, SDcardRubbishResult.RF_APP_LEFTOVERS, 0, null);
//        }
//
//        if ( 0 != (mScanCfgMask & RES_FILE_SCAN_CFG_MASK_SCAN_DALVIK_CACHE)) {
//            scanDalvikCacheLeftovers(ctrl);
//        }
//
//    }



//    private void scanDalvikCacheLeftovers(ScanTaskController ctrl) {
//        boolean isIgnoreItem = false;
//        if (isFilter(WhiteListsWrapper.FUNCTION_FILTER_NAME_DALVIK_CACHE_LEFTOVERS_SCAN)) {
//            isIgnoreItem = true;
//            if (null != mCB) {
//                mCB.callbackMessage(RUB_FILE_SCAN_IGNORE_ITEM, 0, 0,
//                        WhiteListsWrapper.FUNCTION_FILTER_NAME_DALVIK_CACHE_LEFTOVERS_SCAN);
//            }
//            if ((mScanCfgMask & RES_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE) != 0) {
//                return;
//            }
//        }
//
//        if (!SuExec.getInstance().checkRoot()) {
//            return;
//        }
//
//        // 枚举出所有dalvik-cache文件
//        ArrayList<String> dalvikCacheList = SuExec.getInstance().GetDalvikDirFullPathFiles();
//        if (dalvikCacheList == null || dalvikCacheList.isEmpty()) {
//            return;
//        }
//
//        if (null != ctrl && ctrl.checkStop()) {
//            return;
//        }
//
//        if (null != mCB) {
//            mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_STEP_NUM, SDcardRubbishResult.RF_APP_LEFTOVERS,
//                    dalvikCacheList.size() * 3, null);
//        }
//
//        int idx = 0;
//        String path = null;
//        String name = null;
//        String cacheFilePath = null;
//        Iterator<String> iter = dalvikCacheList.iterator();
//        while (iter.hasNext()) {
//            cacheFilePath = iter.next();
//            if (TextUtils.isEmpty(cacheFilePath)) {
//                iter.remove();
//                continue;
//            }
//
//            if (null != ctrl && ctrl.checkStop()) {
//                return;
//            }
//
//            if (!SuExec.getInstance().isFile( cacheFilePath )) {	///< 排除掉文件夹和软链接
//                if (null != mCB) {
//                    mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_ADD_STEP, SDcardRubbishResult.RF_APP_LEFTOVERS, 0, null);
//                }
//                iter.remove();
//                continue;
//            }
//
//            int separatorIndex = cacheFilePath.lastIndexOf('/');
//            name = (separatorIndex < 0)? cacheFilePath : cacheFilePath.substring(separatorIndex + 1, cacheFilePath.length());
//            idx = name.lastIndexOf("@");
//            if (idx < 0) {
//                iter.remove();
//                if (null != mCB) {
//                    mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_ADD_STEP, SDcardRubbishResult.RF_APP_LEFTOVERS, 0, null);
//                }
//                continue;
//            }
//
//            path = name.substring(0, idx);
//            if (TextUtils.isEmpty(path)) {
//                iter.remove();
//                if (null != mCB) {
//                    mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_ADD_STEP, SDcardRubbishResult.RF_APP_LEFTOVERS, 0, null);
//                }
//                continue;
//            }
//
//            path = File.separatorChar + path.replace('@', File.separatorChar);
//            if (TextUtils.isEmpty(path)) {
//                iter.remove();
//                continue;
//            }
//
//            //不删 档案存在或是路径是到外置sdcard mnt/asec ，避免外置卡移除後清理，再插回来无法使用这些app。
//            //isFileExist返回true,导致查询的数据都被删除了
//            if (SuExec.getInstance().isFileExist(path) || path.startsWith("mnt/asec")) {
//                iter.remove();
//            }
//
//            if (null != mCB) {
//                mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_ADD_STEP, SDcardRubbishResult.RF_APP_LEFTOVERS, 0, null);
//            }
//        }
//
//        if (null != ctrl && ctrl.checkStop()) {
//            return;
//        }
//
//        if (dalvikCacheList.isEmpty()) {
//            return;
//        }
//
//        Context ctx = SpaceApplication.getInstance().getContext();
//        SDcardRubbishResult info = new SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER);
//        info.setFoldersCount(0);
//        info.setStrDirPath(null);
//        info.setChineseName(ResUtil.getString(R.string.junk_tag_RF_DalvikCacheLeftovers));
//        // info.setSize(0);
//        info.setFilesCount(dalvikCacheList.size());
//        info.setApkName(ResUtil.getString(R.string.junk_tag_RF_DalvikCacheLeftovers));
//        info.setCheck(true);
//        info.setType(SDcardRubbishResult.RF_APP_LEFTOVERS);
//        if (isIgnoreItem) {
//            info.setIgnore(true);
//        }
//        info.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
//        if (!info.isCheck()) {
//            info.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
//            info.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV);
//        }
//
//        checkLocked(info, info.getSignId());
//
//        if (null != mCB) {
//            mCB.callbackMessage(ADD_CHILDREN_DATA_ITEM_TO_ADAPTER, SDcardRubbishResult.RF_APP_LEFTOVERS, 0, info);
//        }
//    }


    private void checkLocked(SDcardRubbishResult info , String filePath)
    {

    }



    private void checkLocked(SDcardRubbishResult info , int id)
    {
    }

    private void loadAllRFWhiteList() {
        if (!mRFWhiteListMap.isEmpty()) {
            return;
        }
        mRFWhiteListMap.clear();
        List<ProcessModel> tmpWhiteList = WhiteListsWrapper.getRFWhiteList();
        if (null != tmpWhiteList) {
            for (ProcessModel tmpModel : tmpWhiteList) {
                if (!TextUtils.isEmpty(tmpModel.getPkgName())) {
                    mRFWhiteListMap.put(tmpModel.getPkgName(), tmpModel);
                }
            }
        }
    }



    @Override
    public String getTaskDesc() {
        return TAG;
    }

    @Override
    public boolean isFilter(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }

        //由于云端残留等查询获取的路径大小写可能和原始的路径大写不一致,所以对于路径，一律转成小写
        String strLowerName = name;
        if (name.contains("/")) {
            strLowerName = StringUtils.toLowerCase(name);
        }

        if(mRFWhiteListMap.size() > 0 &&  mRFWhiteListMap.get(strLowerName) != null) {
            return true;
        }
        return false;
    }



    private Queue<TempFileTarget> initTempTarget(Context ctx) {
        Queue<TempFileTarget> targetQueue = new LinkedList<>();
        int configMask1= RES_FILE_SCAN_CFG_MASK_SCAN_DCIM_THUMBNAIL_FOLDER & mScanCfgMask;
        if (0 != (configMask1)) {
            int configMask2= RES_FILE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO & mScanCfgMask;
            // 如果有警告的情况下，才会对缩略图文件进行深度扫描
            if (0 != (RES_FILE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO & mScanCfgMask)) {
                targetQueue.offer(new TempFileTarget("/DCIM/.thumbnails", R.string.junk_tag_RF_ImageThumbnails, false, false));
            } else { // 如果装了Du speed 就对缩略图进行建议扫描
                //scan only when Du Speed Booster exist on device for std scan
                /*if (CompetitorStrategy.isCompetitorFeatureEnabled(CompetitorStrategy.FEATURE_ID_RESIDUAL_THUMBNAIL)) {
                    targetQueue.offer(new TempFileTarget("/DCIM/.thumbnails", R.string.junk_tag_RF_ImageThumbnails, false, true));
                }*/
            }
        }
        return targetQueue;
    }


    private class TempFileTarget {
        public final String targetPath;
        public final int targetNameSrcId;
        public final boolean checked;
        public final boolean bStdJunk;

        TempFileTarget(String path, int id, boolean chk, boolean bStd) {
            targetPath = path;
            targetNameSrcId = id;
            checked = chk;
            bStdJunk = bStd;
        }
    }


    private void scanEmptyFolders(final ScanTaskController ctrl, Context ctx, List<SDcardRubbishResult> thumbnailList) {
        assert(null != ctx);
        assert(null != thumbnailList);

        boolean isIgnoreItem = false;
        String[] arrayOfFilePathString = getAllFoldersOnStorage();
        if (isFilter(WhiteListsWrapper.FUNCTION_FILTER_NAME_EMPTY_FOLDERS_SCAN)) {
            isIgnoreItem = true;
            if (null != mCB) {
                mCB.callbackMessage(RUB_FILE_SCAN_IGNORE_ITEM, 0, 0,
                        WhiteListsWrapper.FUNCTION_FILTER_NAME_EMPTY_FOLDERS_SCAN);
            }
            if ((mScanCfgMask & RES_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE) != 0) {
                return;
            }
        }

        if ((null != ctrl && ctrl.checkStop()) || null == arrayOfFilePathString
                || arrayOfFilePathString.length <= 0) {
            return;
        }

        boolean successLoaded = false; // SoLoader.doLoad(true);
        if (!successLoaded) {
            return;
        }

        final int EMPTY_FOLDER_SCAN_LEVEL = 3;

        long emptyFolderSize = 0L;

        EmptyFolderScanPathSet scanPathSet = new EmptyFolderScanPathSet();
        scanPathSet.setPathArray(arrayOfFilePathString);
        scanPathSet.setDeepControlTimes(MAXDEEPCONTROLTIMES);
        arrayOfFilePathString = null;

        LinkedList<String> subEmptyFolderList = new LinkedList<>();
        LinkedList<String> uncheckedSubFolderList = new LinkedList<>();
        SDcardRubbishResult emptyFolderTarget = new SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER);
        SDcardRubbishResult emptyFolderTarget_emtpy = null;
        {
            emptyFolderTarget.setStrDirPath(null);
            emptyFolderTarget.setChineseName(ResUtil.getString(R.string.junk_tag_RF_EmptyFolders));
            emptyFolderTarget.setFilesCount(0);
            emptyFolderTarget.setSize(0);
            emptyFolderTarget.setApkName(ResUtil.getString(R.string.junk_tag_RF_EmptyFolders));

            emptyFolderTarget.setCheck(true);
            emptyFolderTarget.setType(SDcardRubbishResult.RF_TEMPFILES);
            emptyFolderTarget.SetWhiteListKey(WhiteListsWrapper.FUNCTION_FILTER_NAME_EMPTY_FOLDERS_SCAN);
            emptyFolderTarget.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
            if (!emptyFolderTarget.isCheck()) {
                emptyFolderTarget.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
                emptyFolderTarget.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV);
            }
            if (isIgnoreItem) {
                emptyFolderTarget.setIgnore(true);
            }
            emptyFolderTarget_emtpy = new SDcardRubbishResult( emptyFolderTarget );
        }


        while (!scanPathSet.isEmpty()) {

            if (!uncheckedSubFolderList.isEmpty()) {
                scanPathSet.addPathList(uncheckedSubFolderList);
                uncheckedSubFolderList.clear();
            }

            if (null != mCB) {
                mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_STEP_NUM, SDcardRubbishResult.RF_TEMPFILES,
                        scanPathSet.size() * 2, null);
            }

            String sub = scanPathSet.getOneItem();
            if (null == sub) {
                break;
            }

            if (null != ctrl && ctrl.checkStop()) {
                break;
            }

            if (null != mCB) {
                mCB.callbackMessage(SCAN_SDCARD_INFO, 0, 0, getPathName(sub));
            }

            subEmptyFolderList.clear();
            PathOperFunc.isEmptyFolder(sub, EMPTY_FOLDER_SCAN_LEVEL, new ProgressCtrl() {
                @Override
                public boolean isStop() {
                    if (null != ctrl && ctrl.checkStop()) {
                        return true;
                    }

                    return false;
                }
            }, subEmptyFolderList, uncheckedSubFolderList);
            if (subEmptyFolderList.isEmpty()) {
                if (null != mCB) {
                    mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_ADD_STEP, SDcardRubbishResult.RF_TEMPFILES, 0,
                            null);
                }
                continue;
            }

            long fileCompute[] = new long[3];
            while (!subEmptyFolderList.isEmpty()) {
                if (null != ctrl && ctrl.checkStop()) {
                    break;
                }

                sub = subEmptyFolderList.poll();
                if (null == sub) {
                    break;
                }

                if (filterEmptyFolder(sub)) {
                    continue;
                }

                fileCompute[0] = 0L;
                fileCompute[1] = 0L;
                fileCompute[2] = 0L;
                if (0 != (RES_FILE_SCAN_CFG_MASK_CALC_SIZE & mScanCfgMask)) {
                    if (0 != (RES_FILE_SCAN_CFG_MASK_CALC_CHECKED_SIZE & mScanCfgMask)) {
                        PathOperFunc.CalcSizeCallback calcCallback =
                                new PathOperFunc.CalcSizeCallback(ctrl, 60L * 1000L, 32);
                        long calcTime = calcCallback.start();
                        PathOperFunc.computeRealSize(sub,
                                EMPTY_FOLDER_SCAN_LEVEL * MAXDEEPCONTROLTIMES,
                                calcCallback, fileCompute, null);
                    }
                }
                if (null != ctrl && ctrl.checkStop()) {
                    break;
                }

                if (0 == fileCompute[2]) {
                    emptyFolderSize += fileCompute[0];
                    emptyFolderTarget.addPathList(sub);
                    emptyFolderTarget.setFoldersCount(emptyFolderTarget.getFoldersCount()+1);
                    if (emptyFolderSize > 0) {
                        emptyFolderTarget.setSize(emptyFolderSize);
                    }
                }
            }

            if (null != ctrl && ctrl.checkStop()) {
                break;
            }

            if (null != mCB) {
                mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_ADD_STEP, SDcardRubbishResult.RF_TEMPFILES, 0, null);
            }
        }

        List<String> emptyFolder = emptyFolderTarget.getPathList();
        if (null != emptyFolder && !emptyFolder.isEmpty()) {
            thumbnailList.add(emptyFolderTarget);
            if (null != mCB) {
                mCB.callbackMessage(UPDATE_CHILDREN_DATA_ITEM_TO_ADAPTER, SDcardRubbishResult.RF_TEMPFILES,
                        0, new UpdateChildrenData(emptyFolderTarget_emtpy, emptyFolderTarget));
            }
        }
    }


    private String[] getAllFoldersOnStorage() {
        ArrayList<String> storagePathList = new StorageList().getMountedVolumePathsWithoutSubFolders();
        if (null == storagePathList) {
            return null;
        }

        if (storagePathList.isEmpty()) {
            return null;
        }

        ArrayList<String> rst = new ArrayList<>();
        for (int i = 0; i < storagePathList.size(); ++i) {

            String path = storagePathList.get(i);
            if (TextUtils.isEmpty(path)) {
                continue;
            }

            File sdRoot = new File(path);
            if (!sdRoot.exists() || !sdRoot.isDirectory()) {
                continue;
            }

            PathOperFunc.FilesAndFoldersStringList fileAndFoldersStringList = EnableCacheListDir.listDir(sdRoot.getPath());
            if ( fileAndFoldersStringList != null ) {
                PathOperFunc.StringList folderStringList = fileAndFoldersStringList.getFolderNameList();
                if ( folderStringList != null ) {
                    for (String folder : folderStringList) {
                        File file = new File( sdRoot, folder );
                        rst.add(file.getPath());
                    }
                    folderStringList.release();
                    folderStringList = null;
                }
                fileAndFoldersStringList.release();
                fileAndFoldersStringList = null;
            }
        }

        if (rst.isEmpty()) {
            return null;
        }

        return rst.toArray(new String[rst.size()]);
    }



    private void scanEmptyFoldersByMediaStore(final ScanTaskController ctrl, final Context ctx, List<SDcardRubbishResult> thumbnailList) {
        NLog.e(TAG, "scanEmptyFoldersByMediaStore start");
        assert(null != ctx);
        assert(null != thumbnailList);

        boolean isIgnoreItem = false;
        if (isFilter(WhiteListsWrapper.FUNCTION_FILTER_NAME_EMPTY_FOLDERS_SCAN)) {
            isIgnoreItem = true;
            if (null != mCB) {
                mCB.callbackMessage(RUB_FILE_SCAN_IGNORE_ITEM, 0, 0,
                        WhiteListsWrapper.FUNCTION_FILTER_NAME_EMPTY_FOLDERS_SCAN);
            }
            if ((mScanCfgMask & RES_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE) != 0) {
                return;
            }
        }

        if (null != ctrl && ctrl.checkStop()) {
            return;
        }

        class EmptyFolderCallbackImpl implements PathOperFunc.EmptyFolderCallback {
            boolean misIgnoreItem = false;
            //文件夹都是一般大小，所以一个内置外置各一次
            boolean mGetDefOneFolderSize = true;
            boolean mGetSecOneFolderSize = true;
            long mDefFolderSize = 0L;
            long mSecFolderSize = 0L;


            private SDcardRubbishResult mEmptyFolderTarget = new SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER);
            private SDcardRubbishResult mEmptyFolderTarget_empty = null;
            private long fileCompute[] = new long[3];

            public void setIgnore( boolean isIgnoreItem ) {
                misIgnoreItem = isIgnoreItem;
            }
            public EmptyFolderCallbackImpl() {
                mEmptyFolderTarget.setStrDirPath(null);
                mEmptyFolderTarget.setChineseName(ResUtil.getString(R.string.junk_tag_RF_EmptyFolders));
                mEmptyFolderTarget.setFilesCount(0);
                mEmptyFolderTarget.setApkName(ResUtil.getString(R.string.junk_tag_RF_EmptyFolders));
                mEmptyFolderTarget.setCheck(true);
                mEmptyFolderTarget.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
                if (!mEmptyFolderTarget.isCheck()) {
                    mEmptyFolderTarget.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
                    mEmptyFolderTarget.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.APPLEFTOVER_ADV);
                }
                if (misIgnoreItem) {
                    mEmptyFolderTarget.setIgnore(true);
                }
                mEmptyFolderTarget.setType(SDcardRubbishResult.RF_TEMPFILES);
                mEmptyFolderTarget.SetWhiteListKey(WhiteListsWrapper.FUNCTION_FILTER_NAME_EMPTY_FOLDERS_SCAN);
                mEmptyFolderTarget_empty = new SDcardRubbishResult(mEmptyFolderTarget);
            }

            public SDcardRubbishResult getEmptyFolderRubbish() {
                return mEmptyFolderTarget;
            }

            @Override
            public boolean onFilter(String path) {
                return filterEmptyFolder(path);
            }

            @Override
            public void onFoundEmptyFolder(String path, int num) {
                fileCompute[0] = 0L;
                fileCompute[1] = 0L;
                fileCompute[2] = 0L;
                if (num == 0) {
                    return;
                }
                boolean isDefSdcard = !TextUtils.isEmpty(mDefSdCardRootPath) && path.startsWith(mDefSdCardRootPath);
                if (isDefSdcard & mGetDefOneFolderSize || !isDefSdcard & mGetSecOneFolderSize) {
                    if (0 != (RES_FILE_SCAN_CFG_MASK_CALC_SIZE & mScanCfgMask)) {
                        if (0 != (RES_FILE_SCAN_CFG_MASK_CALC_CHECKED_SIZE & mScanCfgMask)) {
                            PathOperFunc.CalcSizeCallback calcCallback =
                                    new PathOperFunc.CalcSizeCallback(ctrl, 60L * 1000L, 32);
                            long calcTime = calcCallback.start();
                            PathOperFunc.computeRealSize(path, fileCompute, calcCallback);
                            if (calcCallback.isTimeOut()) {
                            }
                            if (num != 0) {
                                if (isDefSdcard) {
                                    mGetDefOneFolderSize = false;
                                    mDefFolderSize = (fileCompute[0]/num);
                                    //一些状况会获取到文件夹下面的剩馀空间假设空文件夹最多是4K
                                    if (mDefFolderSize > 4096) {
                                        mDefFolderSize = 4096;
                                    }
                                } else {
                                    mGetSecOneFolderSize = false;
                                    mSecFolderSize = fileCompute[0] / num;
                                    if (mSecFolderSize > 4096) {
                                        mSecFolderSize = 4096;
                                    }
                                }
                            }
                        }
                    }
                }

                long mFolderSize = 0L;
                if (isDefSdcard) {
                    mFolderSize = mDefFolderSize;
                } else {
                    mFolderSize = mSecFolderSize;
                }
                //we have verify this is an empty folder
                long addSize = num * mFolderSize;
                mEmptyFolderTarget.addPathList(path);
                mEmptyFolderTarget.setFoldersCount(mEmptyFolderTarget.getPathList().size());
                mEmptyFolderTarget.setSize(mEmptyFolderTarget.getSize() + addSize);


            }

            @Override
            public void onStatus(String path) {
                if (null != mCB) {
                    mCB.callbackMessage(SCAN_SDCARD_INFO, 0, 0, getPathName(path));
                }
            }

            @Override
            public void onStepNum(int n) {
                if (null != mCB) {
                    mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_STEP_NUM, SDcardRubbishResult.RF_TEMPFILES, n, null);
                }
            }

            @Override
            public void onAddStep() {
                if (null != mCB) {
                    mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_ADD_STEP, SDcardRubbishResult.RF_TEMPFILES, 0, null);
                }
            }

            public void onEnd() {
                if (null != mCB) {
                    mCB.callbackMessage(UPDATE_CHILDREN_DATA_ITEM_TO_ADAPTER,
                            SDcardRubbishResult.RF_TEMPFILES, 0,
                            new UpdateChildrenData(mEmptyFolderTarget_empty, mEmptyFolderTarget));
                    NLog.d(TAG, "------------>找到空文件夹: path = " + mEmptyFolderTarget.getPathList());
                }
            }
        }

        EmptyFolderCallbackImpl cb = new EmptyFolderCallbackImpl();
        cb.setIgnore(isIgnoreItem);
        PathOperFunc.getAllEmptyFolders(ctx, new ProgressCtrl() {
            @Override
            public boolean isStop() {
                return ctrl.checkStop();
            }
        }, cb);

        SDcardRubbishResult emptyFolderTarget = cb.getEmptyFolderRubbish();
        List<String> emptyFolder = emptyFolderTarget.getPathList();
        if (null != emptyFolder && !emptyFolder.isEmpty()) {
            thumbnailList.add(emptyFolderTarget);
            NLog.e(TAG, "scan empty foler finish");
            cb.onEnd();
        }
    }




    private boolean filterEmptyFolder(String path) {
        assert(!TextUtils.isEmpty(path));

        path = StringUtils.toLowerCase(path);
        String pathW = FileUtils.addSlash(path);

        boolean isDefSdcard = !TextUtils.isEmpty(mDefSdCardRootPath) && pathW.startsWith(mDefSdCardRootPath);

        if (Build.VERSION.SDK_INT >= 19 && !isDefSdcard) {
            // 4.4上第二张卡的文件夹删不掉，所以就不要检出了。
            return true;
        }

        /*if (WhiteInfoManager.getInstance().isExistInWhiteList(WhiteInfoManager.DB_WHITELIST_TYPE_EFOLDER, pathW, null)) {
            return true;
        }
        path = FileUtils.removeSlash(path);

        //TODO : why only default sdcard
        if (isDefSdcard) {
            if (isAssociateSdCachePath(path)) {
                return true;
            }
        }*/

        return false;
    }


    /**
     * 根据路径，获取文件名
     * @param path 绝对路径
     * @return 文件名（带后缀）
     */
    private String getPathName(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }

        int pos = path.lastIndexOf(File.separatorChar);
        if (pos < 0) {
            return path;
        }

        return path.substring(pos);
    }




    private class EmptyFolderScanPathSet {
        private boolean mTouchDeepControlTimes = false;
        private String[] mPathArray = null;
        private int mPathArrayValidRange = 0;
        private LinkedList<String> mPathList = null;
        private ArrayList<Integer> mDeepCtrl = null;
        private int mDeepControlTimes = -1;

        public void setPathArray(String[] pathArray) {
            mPathArray = pathArray;
            if (null != pathArray) {
                mPathArrayValidRange = pathArray.length;
            } else {
                mPathArrayValidRange = 0;
            }
        }

        public void addPathList(List<String> pathList) {
            if (null == pathList) {
                return;
            }
            if (null != mDeepCtrl) {
                if (mDeepCtrl.size() >= mDeepControlTimes) {
                    // 超过深度控制值，不允许添加。
                    mTouchDeepControlTimes = true;
                    return;
                }
            }

            if (null == mPathList) {
                mPathList = new LinkedList<String>();
            }

            if (!pathList.isEmpty()) {
                if (null != mDeepCtrl) {
                    mDeepCtrl.add(Integer.valueOf(pathList.size()));
                }

                mPathList.addAll(pathList);
            }
        }

        public String getOneItem() {

            if (null != mDeepCtrl) {
                while (!mDeepCtrl.isEmpty()) {
                    Integer size = mDeepCtrl.remove(mDeepCtrl.size() - 1);
                    int newSize = size.intValue() - 1;
                    if (newSize >= 0) {
                        mDeepCtrl.add(Integer.valueOf(newSize));
                        break;
                    }
                }
            }

            if (null != mPathList && !mPathList.isEmpty()) {
                return mPathList.removeLast();
            }

            if (null != mPathArray && mPathArrayValidRange > 0) {
                boolean rc = HeapSort.sort(mPathArray, mPathArrayValidRange, 1);

                if (rc) {
                    --mPathArrayValidRange;
                    String rst = mPathArray[mPathArrayValidRange];
                    mPathArray[mPathArrayValidRange] = null;

                    return rst;
                }
            }

            return null;
        }

        public int size() {
            if (null != mPathList && !mPathList.isEmpty()) {
                return mPathList.size() + mPathArrayValidRange;
            }

            return mPathArrayValidRange;
        }

        public boolean isEmpty() {
            if (mPathArrayValidRange > 0) {
                return false;
            }

            if (null == mPathList) {
                return true;
            }

            return mPathList.isEmpty();
        }

        public void setDeepControlTimes(int times) {
            mDeepControlTimes = times;
            if (mDeepControlTimes >= 0) {
                mDeepCtrl = new ArrayList<Integer>();
            } else {
                mDeepCtrl = null;
            }
        }
    }




    class CloudQueryer{
        public boolean mbScanDefaultSdCard = true;
        public String mSdcardPath;
        public KResidualCloudQuery mResidualCloudQuery = null;
        public ResidualCloudQueryCallback mResidualCloudQueryCallback = null;
        public TreeMap<String, KResidualCloudQuery.DirQueryData> mResidualCloudResult = new TreeMap<>(new StringNoCaseComparator());
        public final List<SDcardRubbishResult> mRubbishResult2Report = Collections.synchronizedList(new LinkedList<SDcardRubbishResult>());

        public ScanTaskController mCtrl;
        public CloudQueryer( String sdcardPath, final ScanTaskController ctrl, boolean scanDefaultSdCard) {
            mResidualCloudQuery = CleanCloudManager.createResidualCloudQuery(false);
            mResidualCloudQueryCallback =  new ResidualCloudQueryCallback(ctrl, sdcardPath, scanDefaultSdCard, this );
            mbScanDefaultSdCard = scanDefaultSdCard;
            mSdcardPath = sdcardPath;
            mCtrl = ctrl;
            String lang = CleanCloudScanHelper.getCurrentLanguage();
            mResidualCloudQuery.initialize();
            if (mScanCommonStatus != null) {
                mResidualCloudQuery.setDirNetQueryTimeController(mScanCommonStatus.getNetQueryTimeController());
            }
            mResidualCloudQuery.setPackageChecker(mPackageCheckerForCloudQuery);
            mResidualCloudQuery.setLanguage(lang);
            mResidualCloudQuery.setSdCardRootPath(sdcardPath);
        }

        public void waitForComplete(final ScanTaskController ctrl){
            if (mResidualCloudQuery != null) {

                int nWaitResult = mResidualCloudQuery.waitForComplete(
                        MAX_RESIDUAL_CLOUD_QUERY_WAIT_TIME, true, null == ctrl ? null :
                                new CleanCloudDef.ScanTaskCtrl() {
                                    @Override
                                    public boolean checkStop() {
                                        return ctrl.checkStop();
                                    }
                                });
                //Log.i(TAG, "Residual Scan waitForComplete" + nWaitResult);

                synchronized(mResidualCloudResult) {
                    processResidualCloudResult(mResidualCloudResult, mCtrl, mbScanDefaultSdCard, mRubbishResult2Report);
                    mResidualCloudResult.clear();
                }

                //mCleanCloudQueryStatistics.setQueryInnerStatistics(mResidualCloudQuery.getInnerStatistics());
                //mResidualCloudQuery.clearInnerStatistics();
                mResidualCloudQuery.unInitialize();//退出线程
                mResidualCloudQuery = null;
                mRubbishResult2Report.clear();
            }
        }
    }

    void processResidualCloudResult(TreeMap<String, KResidualCloudQuery.DirQueryData> results, final ScanTaskController ctrl, boolean scanDefaultSdCard, final List<SDcardRubbishResult> rubbishResultList) {
        Iterator<Map.Entry<String, KResidualCloudQuery.DirQueryData>> iter = results.entrySet().iterator();
        Map.Entry<String, KResidualCloudQuery.DirQueryData> entry = null;
        while (iter.hasNext()) {
            entry = iter.next();
            String filepath = entry.getKey();
            KResidualCloudQuery.DirQueryData result = entry.getValue();
            SDcardRubbishResult rubbishResult = processOneResidualCloudResult(filepath, result, ctrl, scanDefaultSdCard);
            if (rubbishResult != null && rubbishResultList!= null) {
                rubbishResultList.add(rubbishResult);
            }
        }
    }

    private SDcardRubbishResult processOneResidualCloudResult(String filepath, KResidualCloudQuery.DirQueryData result, final ScanTaskController ctrl, boolean scanDefaultSdCard) {
        NLog.d(TAG, "RubbishFileScanTask processOneResidualCloudResult filepath = %s", filepath);
        boolean isIgnoreItem = false;
        if (isFilter(filepath)) {
            isIgnoreItem = true;
            if (null != mCB) {
                mCB.callbackMessage(RUB_FILE_SCAN_IGNORE_ITEM, 0, 0, filepath);
            }

            if ((mScanCfgMask & RES_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE) != 0) {
                return null;
            }
        }

        if (null == result.mResult.mShowInfo){
            return null;
        }


        // 扫描结果中已经有了
        boolean IsDirExistInScanResult = IsDirExistInScanResult(filepath, mListAppLeftovers);
        NLog.d(TAG, "IsDirExistInScanResult = "+IsDirExistInScanResult);
        if (IsDirExistInScanResult)
            return null;


        // 服务器控制过滤，防止误删。
        if (DBColumnFilterManager.getInstance().isFilter(
                CLEAN_CLOUD_RESIDUAL_ID_FILTER_NAME,
                String.valueOf(result.mResult.mSignId))) {
            return null;
        }

        int signId = result.mResult.mSignId;
        String targetPkgName = "";
        if (result.mResult.mPkgsMD5HexString != null && !result.mResult.mPkgsMD5HexString.isEmpty()) {
            targetPkgName = result.mResult.mPkgsMD5HexString.iterator().next();
        } else if(result.mResult.mPackageRegexs != null && !result.mResult.mPackageRegexs.isEmpty()) {
            targetPkgName = result.mResult.mPackageRegexs.iterator().next();
        }

        String name = result.mResult.mShowInfo.mName;
        if (null == name) {
            name = "unknown";
        }
        int cleanMediaFlag = result.mResult.mCleanMediaFlag;

        String alertinfo = result.mResult.mShowInfo.mAlertInfo;

        List<String> filterDirList = null;
        if (null != result.mResult.mFilterSubDirs && !result.mResult.mFilterSubDirs.isEmpty()) {
            filterDirList = new ArrayList<>(result.mResult.mFilterSubDirs.size());
            for (KResidualCloudQuery.FilterDirData tmpData : result.mResult.mFilterSubDirs) {
                if (TextUtils.isEmpty(tmpData.mPath)) {
                    continue;
                }
                if (!tmpData.mPath.endsWith(File.separator)) {
                    filterDirList.add(tmpData.mPath + File.separator);
                } else {
                    filterDirList.add(tmpData.mPath);
                }
            }
        }

        mFilter.setRubbishCleanTime(result.mResult.mCleanTime);
        mFilter.setFilterData(result.mResult.mFileCheckerData);
        return addResidualDetectResult(
                filepath,
                alertinfo,
                signId,
                name,
                targetPkgName,
                cleanMediaFlag,
                isIgnoreItem,
                mListAppLeftovers,
                ctrl,
                scanDefaultSdCard,
                filterDirList,
                (byte) result.mResultSource,
                result.mResult.mCleanType);
    }
    private boolean IsDirExistInScanResult(String strDir, List<SDcardRubbishResult> resultList) {
        if (null == resultList)
            return false;

        synchronized(resultList) {
            if (resultList.isEmpty())
                return false;

            for (SDcardRubbishResult result : resultList) {
                if (result.getPathList().isEmpty()) {
                    if (strDir.equals(result.getStrDirPath())) {
                        return true;
                    }
                    if (isParentDir(result.getStrDirPath(), strDir)) {
                        return true;
                    }
                } else {
                    for (String path : result.getPathList()) {
                        if (null == path) {
                            continue;
                        }

                        if (strDir.equals(path)) {
                            return true;
                        }
                        if (isParentDir(path, strDir)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    // 判定删除结果列表中是否已经存在该目录的父目录 /
    private boolean isParentDir(String strParent, String strUnknown) {
        if (TextUtils.isEmpty(strParent)) {
            return false;
        }
        if (strUnknown.length() <= strParent.length()) {
            return false;
        }
        if ( !strUnknown.startsWith(strParent) ) {
            return false;
        }
//		if (path.charAt(path.length() - 1) != File.separatorChar) {
//			return path + File.separatorChar;
//		}
//

        if( strParent.charAt(strParent.length()-1) == File.separatorChar ){
            return true;
        }
        if ( strUnknown.charAt(strParent.length()) == File.separatorChar ) {
            return true;
        }
        return false;
    }


    private static class StringNoCaseComparator implements Comparator<String> {
        @Override
        public int compare(String left, String right) {
            return left.compareToIgnoreCase(right);
        }
    }



    private class ResidualCloudQueryCallback implements KResidualCloudQuery.DirQueryCallback {
        private final ScanTaskController mCtrl;
        private final String mSdcardPath;
        private final boolean mScanDefaultSdCard;
        private CloudQueryer mCloudQueryer;
        public ResidualCloudQueryCallback(final ScanTaskController ctrl, String sdcardPath, boolean scanDefaultSdCard, CloudQueryer cloudQueryer ) {
            mCtrl = ctrl;
            mSdcardPath = sdcardPath;
            mScanDefaultSdCard = scanDefaultSdCard;
            mCloudQueryer = cloudQueryer;
        }
        @Override
        public void onGetQueryDirs(int queryId, final Collection<String> dirs) {
            NLog.d(TAG, "ResidualCloudQueryCallback onGetQueryDirs dirs= "+dirs);
            onGetResidualCloudQueryDirs(dirs);
        }
        @Override
        public void onGetQueryId(int queryId) {
        }

        @Override
        public void onGetQueryResult(int queryId, Collection<KResidualCloudQuery.DirQueryData> results, boolean queryComplete) {
            if (results != null){
                NLog.e(TAG, "ResidualCloudQueryCallback onGetQueryResult size = "+results.size() +", queryComplete = "+queryComplete);
            }
            onGetResidualCloudQueryResult(results, mSdcardPath, mCtrl, mScanDefaultSdCard, queryComplete, mCloudQueryer);
        }

        @Override
        public boolean checkStop() {
            return (mCtrl != null ? mCtrl.checkStop() : false);
        }
    }
    private void onGetResidualCloudQueryDirs(final Collection<String> secondQueryDirs) {
        if (secondQueryDirs != null) {
            mTotalProgressStep += secondQueryDirs.size();
            if (mTotalProgressStep > 0 && mCB != null) {
                mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_STEP_NUM, SDcardRubbishResult.RF_APP_LEFTOVERS, mTotalProgressStep, null);
            }
        }
    }

    private void onGetResidualCloudQueryResult(
            Collection<KResidualCloudQuery.DirQueryData> results,
            String sdcardPath,
            final ScanTaskController ctrl,
            boolean scanDefaultSdCard,
            boolean queryComplete,
            CloudQueryer cloudQueryer ) {

        if (results.isEmpty())
            return;

        String filepath;
        for (DirQueryData result : results) {
            if (null != mCB) {
                mCB.callbackMessage(RUB_FILE_SCAN_PROGRESS_ADD_STEP, SDcardRubbishResult.RF_APP_LEFTOVERS, 0,
                        null);
                mCB.callbackMessage(SCAN_SDCARD_INFO, 1, 0, result.mDirName);
                NLog.d(TAG, "残留扫描: %s", result.mDirName);
            }

            if (result.mErrorCode != 0)
                continue;

            if (!result.mIsDetected)
                continue;

            StringBuilder builder = new StringBuilder(sdcardPath.length() + 1 + result.mDirName.length());
            builder.append(sdcardPath);
            builder.append(File.separator);
            builder.append(result.mDirName);
            filepath = builder.toString();

            synchronized(cloudQueryer.mResidualCloudResult) {
                cloudQueryer.mResidualCloudResult.put(filepath, result);
                NLog.e(TAG, "RubbishFileScanTask onGetResidualCloudQueryResult filepath = "+filepath);
            }
        }

        synchronized(cloudQueryer.mResidualCloudResult) {
            if (mFilter != null && cloudQueryer.mResidualCloudQuery != null && cloudQueryer.mResidualCloudQuery.getFileChecker() != null) {
                mFilter.setFilterInterface(cloudQueryer.mResidualCloudQuery.getFileChecker());
            }
            processResidualCloudResult(cloudQueryer.mResidualCloudResult, ctrl, scanDefaultSdCard, cloudQueryer.mRubbishResult2Report);
            cloudQueryer.mResidualCloudResult.clear();
        }
    }



    private static class PackageCheckerForCloudQuery implements KResidualCloudQuery.PackageChecker {
        private MD5PackageNameConvert mMd5PackageNameConvert;
        PackageCheckerForCloudQuery(MD5PackageNameConvert md5PackageNameConvert) {
            mMd5PackageNameConvert = md5PackageNameConvert;
        }
        @Override
        public Collection<String> getAllPackageNames() {
            return mMd5PackageNameConvert.getAllPackageNames();
        }
    }

    public void setRubbishCachedDataScanTask(ScanTask rubbishScanTaskCachedRst) {
        mRubbishScanTaskCachedRst = rubbishScanTaskCachedRst;
    }
}

package com.clean.spaceplus.cleansdk.junk.engine.task;

import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.clean.spaceplus.cleansdk.R;
import com.clean.spaceplus.cleansdk.base.bean.SpecialFolder;
import com.clean.spaceplus.cleansdk.base.db.process_list.dao.JunkLockedDAOHelper;
import com.clean.spaceplus.cleansdk.base.db.process_list.dao.ResidualFileWhiteListDAOHelper;
import com.clean.spaceplus.cleansdk.base.scan.IScanFilter;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudFactory;
import com.clean.spaceplus.cleansdk.junk.engine.ApkModelAssemblage;
import com.clean.spaceplus.cleansdk.junk.engine.DBColumnFilterManager;
import com.clean.spaceplus.cleansdk.junk.engine.PathScanCallback;
import com.clean.spaceplus.cleansdk.junk.engine.ProgressControl;
import com.clean.spaceplus.cleansdk.junk.engine.ProgressCtrl;
import com.clean.spaceplus.cleansdk.junk.engine.WhiteListsWrapper;
import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.GenericWhiteInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.SDcardRubbishResult;
import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;
import com.clean.spaceplus.cleansdk.junk.engine.fixedata.ApkTaskData;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.junk.engine.util.ApkBackupFilter;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.clean.spaceplus.cleansdk.util.ArraySet;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.clean.spaceplus.cleansdk.util.StringUtils;
import com.clean.spaceplus.cleansdk.util.TimingUtil;
import com.hawkclean.framework.log.NLog;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import space.network.cleancloud.KResidualCloudQuery;

/**
 * @author zengtao.kuang
 * @Description: ApkScanTask
 * @date 2016/5/10 19:43
 * @copyright TCL-MIG
 */
public class ApkScanTask extends ScanTask.BaseStub implements IScanFilter, ApkModelAssemblage {
    public static final int HANDLER_INVALID_SD_STORAGE = 1;
    @Deprecated
    public static final int HANDLER_APK_NOT_EXISTS = 2;
    public static final int HANDLER_APKMODELS_INIT_COMPLETED = 3;
    public static final int HANDLER_APK_DIR_SHOW = 4;
    public static final int HANDLER_APK_SCAN_UPDATE = 5;
//    public static final int HANDLER_APK_CHECKED_STATUS_UPDATE = 10;

    @Deprecated
    public static final int HANDLER_ADD_PROGRESS = 6;
    public static final int HANDLER_SATRT_TASK = 7;
    public static final int CB_TYPE_SCAN_FINISH = -1; // /<
    // 扫描结束，若因超时结束，则arg1值为1，否则为0。

    public static final int HANDLER_FIND_MEDIASTORE_ITEM = 8;
    public static final int HANDLER_UPDATE_MEDIASTORE_ITEM = 9;
    private static final String TAG = ApkScanTask.class.getSimpleName();

    public static ApkScanTask createDefault() {
        ApkScanTask task = new ApkScanTask();
        task.setApkScanFolderLevel(4);
        task.setUseCompoundScan(true);
        task.setAutoFilterBackup(true);
        task.setFilterProbablyUserFolderFlag(true);
        task.setShowAllApk(true);
        task.setCheckUninstallApkModifyOuttime(1L * 24L * 60L * 60L * 1000L); // 5.8.8 从三天改为一天
        // todo 开启锁定状态查询开关
        task.setCheckLocked(true);
        return task;
    }

    public static class ListViewDatas {
        public List<APKModel> installedApk;
        public List<APKModel> uninstalledApk;
    }

    public static final class TargetFolderParam {
        private String strPath;
        private int nScanLevel;
        private int nProgressBarCap;
        private int nFileLimit = DEF_FILE_LIMIT; //超过30个文件扫不到APK,LOG,TMP, 就不扫了。
        private int nFolderLimit = DEF_FOLDER_LIMIT; //过过15个文件夹是空的就不扫了。

        static private final int DEF_SCAN_LEVEL = 0;
        static private final int DEF_PROGRESS_CAP = 200;
        static private final int DEF_FILE_LIMIT = 30;
        static private final int DEF_FOLDER_LIMIT = 15;

        public TargetFolderParam(String strPath) {
            this.strPath = strPath;
            this.nScanLevel = DEF_SCAN_LEVEL;
            this.nProgressBarCap = DEF_PROGRESS_CAP;
        }

        public TargetFolderParam(String strPath, int nScanLevel, int nProgressBarCap) {
            this.strPath = strPath;
            this.nScanLevel = nScanLevel;
            this.nProgressBarCap = nProgressBarCap;
        }

        public TargetFolderParam(String strPath, int nScanLevel, int nProgressBarCap, int nFileLimit, int
                nFolderLimit) {
            this.strPath = strPath;
            this.nScanLevel = nScanLevel;
            this.nProgressBarCap = nProgressBarCap;
            this.nFileLimit = nFileLimit;
            this.nFolderLimit = nFolderLimit;
        }

    }


    private List<APKModel> installedAPKModels = null;
    private List<APKModel> notInstalledAPKModels = null;
    private Map<String, APKModel> apkModelMap = null;

    private ApkParseThread apkParseThread = null;

    private ScanTaskController mCtrl = null;

    private boolean mSoLoaded = false;

    private List<String> mApkScaned = new ArrayList<String>();

    private boolean mbFilterProbablyUserFolder = false;

    private long mCheckUninstallApkModifyOuttime = 0L;

    private boolean mbShowAllApk = false;

    private int mFolderScanLevel = 4;
    private boolean mbUseMediaSpeedUp = false; // 使用 mediastore 扫描
    private boolean mbAutoFilterBackup = false; // 过滤cm备份出的软件
    private boolean mbAutoAdvanvcedFilterBackup = false; // 过滤掉用户备份的软件， 即 只显示在
    // Download
    // 指定目录、隐藏目录，
    // 残留库缓存库（alertinfo中不含backup字样），广告库中的apk
    private boolean isCheckLocked = false;
    private static final int PROG_BAR_LIST_STORAGE_FILE = 6000;
    private static final int PROG_BAR_PARSER_FILE = 2000;
    private int mProgressBarTotal = 0; // 进度条总量

    //TODO wtb注释了这个东西，在apkParseThread里面已经有过滤，这里是多余的
//    private HashMap<String, Boolean> mApkHashMap; // 记录已经扫描到的apk路径 ，避免 复合扫描情况下
    // 同一apk包会返回2次问题
    private boolean mbUseCompoundScan = false; // 使用组合查询 文件夹遍历+mediastore 扫描
    // 当值为true时。。。mbUseMediaSpeedUp
    // 不生效

//    private ArraySet<String> mMediaStoreAPKPath = new ArraySet<String>();
//    private ArraySet<String> mScannedApkFolderPath = new ArraySet<String>();

    // Need Lock for APKParserThread using m_filterProbablyUserFolderMap to lock
//    private Object filteredProbablyUserFolderLock = new Object();//TODO wtb注释掉这个锁，解析线程现在只有一个
    private Set<String> m_filteredProbablyUserFolderSet = null;
    private Set<String> m_filteredResidualFolderSet = null;
    private Map<String, APKModel> m_filterProbablyUserFolderMap = null;
    private KResidualCloudQuery mIkResidualCloudQuery = null;
    private String m_strExternalDir;

    ////////////////////////////////////////////////////////////////////////////////////////////
    private Object m_filterSameApkLock = new Object();
    private Set<APKModel> m_filterSameApkSet = null;
    ///////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<TargetFolderParam> mTargetFolderParamList = new ArrayList<TargetFolderParam>();


    public static final int SCAN_CFG_SCAN_LOG_FILE = 0x00000001;
    public static final int SCAN_CFG_SCAN_TMP_FILE = 0x00000002;
    public static final int SCAN_CFG_SCAN_N7PLARYER = 0x00000004;
    public static final int SCAN_CFG_SCAN_POWERAMP = 0x00000008;
    public static final int SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS = 0x00000010;
    public static final int SCAN_CFG_MASK_NOT_RETURN_IGNORE = 0x00000020;

    private int mScanCfgMask = 0;

    public void setScanConfigMask(int mask) {
        mScanCfgMask = mask;
    }

    // 是否需要扫描APK标志，默认为true
    private boolean mScanSwitch = true;

    public void setScanSwitch(boolean bSwitch) {
        mScanSwitch = bSwitch;
    }

    /***
     * 使用组合查询 文件夹遍历+mediastore 扫描 当值为true时。。。mbUseMediaSpeedUp 不生效
     *
     * @param mbUseCompoundScan
     */
    public void setUseCompoundScan(boolean mbUseCompoundScan) {
        this.mbUseCompoundScan = mbUseCompoundScan;
    }

    public void setApkScanFolderLevel(int nLevel) {
        mFolderScanLevel = nLevel;
    }

    public void setCheckLocked(boolean isCheckLocked) {
        this.isCheckLocked = isCheckLocked;
    }

    // 是否不扫描目录中包含Backup字样的文件
    public void setAutoFilterBackup(boolean bAuto) {
        mbAutoFilterBackup = bAuto;
    }

    public void setShowAllApk(boolean bShowAllApk) {
        mbShowAllApk = bShowAllApk;
    }

    /**
     * 如果某个目录的安装包个数达到2个，且该目录不在残留库的目录列表中，则忽略该目录的APK，以下目录除外：download及其子目录、
     * bluetooth、tmp、android/data, 根目录
     */
    public void setFilterProbablyUserFolderFlag(boolean filterProbablyUserFolder) {
        mbFilterProbablyUserFolder = filterProbablyUserFolder;
    }

    /**
     * 未安装的apk包，最后修改时间在指定时间以前的全部钩选。
     */
    public void setCheckUninstallApkModifyOuttime(long outtime) {
        mCheckUninstallApkModifyOuttime = outtime;
    }

    private ScanTask mApkScanTaskCachedRst = null;

    public void setApkCachedDataScanTask(ScanTask task) {
        mApkScanTaskCachedRst = task;
    }

    //TODO wtb这个是单线程的
    @Override
    public boolean putOneApkModel(File apkFile, APKModel apkModel) {

        if (apkFile == null) return false;
        String path = apkFile.getAbsolutePath();
//
//        if (path != null) {
//            if (mApkHashMap == null) {
//                mApkHashMap = new HashMap<String, Boolean>();
//            }
//
//            Boolean b = mApkHashMap.get(StringUtils.toLowerCase(path));
//
//            if (b != null && b) {
//                return false;
//            } else {
//                mApkHashMap.put(StringUtils.toLowerCase(path), true);
//            }
//        }

        boolean bIsBackup = false;
        boolean bIsFont = false;

        if (apkModel != null) {
            if (getPathWithoutSDPath(path).startsWith("ifont/")) { // 爱字体目录
                bIsFont = true;
                if (!mbShowAllApk) {
                    return false;
                }
            } else { // 如果不是爱字体，才判断是否备份
                if (ApkBackupFilter.getInstance().filter(path)) {
                    bIsBackup = true;
                    // need to display on UI, skip return
                    if (!mbShowAllApk) {
                        return false;
                    }
                }
            }
        }

        if (null != apkModel) {
            if (bIsBackup) {
                apkModel.setDisplayType(1);
            }
            apkModel.setIsBackup(bIsBackup);
            if (bIsFont) {
                apkModel.setDisplayType(4);
            }
            if (apkModel.isInstalledByApkName()) {
                installedAPKModels.add(apkModel);
            } else {
                notInstalledAPKModels.add(apkModel);
            }
        } else {
            apkModel = new APKModel();
            apkModel.type = APKModel.APK_NOT_INSTALLED;
            apkModel.setBroken(true);
            apkModel.setInstalledByApkName(false);
            apkModel.setTitle(apkFile.getName().substring(0, apkFile.getName().lastIndexOf(".")));
            apkModel.setFileName(apkFile.getName());
            apkModel.setModifyTime(apkFile.lastModified());
            apkModel.setPath(apkFile.getAbsolutePath());
            apkModel.setSize(apkFile.length());
            apkModel.setIsBackup(bIsBackup);

            notInstalledAPKModels.add(apkModel);
        }
        checkAPKRule(apkModel);
        onFoundItem(apkModel);

        return true;
    }


    // Check Rule
    /*
        1：备份
        2：插件
        3：主题
        4：字体
        5：自建
        6：回收站
        7：更新包
        8：表情
        9：其他
     */
    private void checkAPKRule(APKModel apkModel) {
        // UnChecked APK in Backup Folder
        ///> 先判断apk文件是否存在于白名单中，若存在则将其检出等级降低为低级。
        if (apkModel.getDisplayType() == 4) {
            apkModel.setIsDisplay(true);
            apkModel.setChecked(false);
            return;
        }
        ///> 备份目录和用户自建目录需要判断是否存在相同的apk文件，若存在则只保留一个
        if (apkModel.isBackup() || apkModel.getDisplayType() == 1) {
            if (isExistInSameApkLib(apkModel)) {
                apkModel.setChecked(true);
            } else {
                apkModel.setChecked(false);
            }
            return;
        }

//        if (apkModel.getDisplayType() != 0) {  // 其他的APK都是为0，所以不会走进去
//            boolean needCheckNewDownload = true;
//            if (apkModel.getDisplayType() == 5) {
//                if (isExistInSameApkLib(apkModel)) {
//                    needCheckNewDownload = false;
//                    apkModel.setChecked(true);
//                } else {
//                    apkModel.setChecked(false);
//                }
//            }
//
//            if (apkModel.getDisplayType() == 9) {
//                needCheckNewDownload = false;
//            }
//
//            if (apkModel.isChecked() && needCheckNewDownload) {
//                checkApkUninstallAndNewDownload(apkModel);
//            }
//            return;
//        }

        // UnChecked APK if in User Folder
        if (mbFilterProbablyUserFolder) {
            filterProbablyUserFolder(apkModel);
        }
        // UnChecked APK if UnInstall and New DownLoad
        checkApkUninstallAndNewDownload(apkModel);
    }

    private void checkApkUninstallAndNewDownload(APKModel apkModel) {
        if (null != apkModel && apkModel.type == APKModel.APK_NOT_INSTALLED && mCheckUninstallApkModifyOuttime > 0L) {
            if (Math.abs(System.currentTimeMillis() - apkModel.getModifyTime()) <= mCheckUninstallApkModifyOuttime) {
                if (!apkModel.isInUserFilterFolder()) {
                    apkModel.setIsUninstalledNewDL(true);
                    apkModel.setChecked(false);
                } else {
                    apkModel.setIsUninstalledNewDL(false);
                }
            }
        }
    }

    private boolean isExistInSameApkLib(APKModel apkModel) {

        synchronized (m_filterSameApkLock) {
            if (m_filterSameApkSet == null) {
                m_filterSameApkSet = new ArraySet<APKModel>();
                m_filterSameApkSet.add(apkModel);
            } else {

                if (m_filterSameApkSet.size() <= 0) {
                    m_filterSameApkSet.add(apkModel);
                    return false;
                }
                String modelPackageName = null;
                String modelVersion = null;
                String modelTmpPackageName = null;
                String modelTmpVersion = null;
                Iterator iterator = m_filterSameApkSet.iterator();
                while (iterator.hasNext()) {
                    APKModel ModelTemp = (APKModel) iterator.next();
                    modelPackageName = apkModel.getPackageName();
                    modelVersion = apkModel.getVersion();
                    modelTmpPackageName = ModelTemp.getPackageName();
                    modelTmpVersion = ModelTemp.getVersion();
                    if (!TextUtils.isEmpty(modelPackageName) && !TextUtils.isEmpty(modelTmpPackageName) && !TextUtils
                            .isEmpty(modelVersion) && !TextUtils.isEmpty(modelTmpVersion)) {
                        if (modelTmpPackageName.equalsIgnoreCase(modelPackageName) && modelTmpVersion
                                .equalsIgnoreCase(modelVersion)) {
                            if (apkModel.isBackup() && !ModelTemp.isBackup()) {
                                ModelTemp.setChecked(true);
                                iterator.remove();
                                break;
                            }
                            return true;
                        }
                    }
                }
                m_filterSameApkSet.add(apkModel);
            }
        }
        return false;
    }

    /**
     * 是否在 download目录，bluetooth, tmp, android/data
     * 目录及根目录下的apk包以外，只要不在残留库中
     *
     * @return
     */
    private boolean isExistInCheckPathList(String apkPath, APKModel apkModel) {

        String apknoSdDir = apkPath;

        String strTemp = "";
        strTemp = getPathWithoutSDPath(apkPath);
        if (!TextUtils.isEmpty(strTemp)) {
            apknoSdDir = strTemp;
        }

        int dirPos = apknoSdDir.indexOf(File.separator);
//        if (dirPos < 0) {
//            Log.e("ApkCheck", "==> find apk file in sd root=" + apkPath);
//            if (isExistInSameApkLib(apkModel)) {
//                Log.e("ApkCheck", "==> apk file in SameApkLib=" + apkPath);
//                apkModel.setCurrentApkIsInUserFolder();
//            }
//            return true; // in SD Root
//        }
        if (dirPos < 0 || apknoSdDir.toLowerCase().startsWith("download/") || apknoSdDir.toLowerCase().startsWith
                ("bluetooth/") || apknoSdDir.toLowerCase().startsWith("tmp/") || apknoSdDir.toLowerCase().startsWith
                ("android/data/")) {
            return true;
        }
        String apkFirstDir = apknoSdDir.substring(0, dirPos);

        // Already querry residual folder
        return checkApkIsInResidualByFirstDir(apkFirstDir);
    }

//    private boolean checkApkIsInResidual(String strApkPath) {
//        String strApknoSdDir = "";
//        String strTemp = getPathWithoutSDPath(strApkPath);
//        if (!TextUtils.isEmpty(strTemp)) {
//            strApknoSdDir = strTemp;
//        }
//
//        int dirPos = strApknoSdDir.indexOf(File.separator);
//        if (dirPos < 0) {
//            return false;
//        }
//        String strApkFirstDir = strApknoSdDir.substring(0, dirPos);
//        return checkApkIsInResidualByFirstDir(strApkFirstDir);
//    }

    private String getPathWithoutSDPath(String strApkPath) {
        String strApknoSdDir = "";
        for (String sdPath : mExternalStoragePaths) {
            String sdPathLow = StringUtils.toLowerCase(sdPath);
            if (strApkPath.length() > sdPathLow.length() && strApkPath.startsWith(sdPathLow)) {
                strApknoSdDir = strApkPath.substring(sdPathLow.length() + 1, strApkPath.length());
            }
        }

        return strApknoSdDir;
    }

    private boolean checkApkIsInResidualByFirstDir(String strApkFirstDir) { // 是否在残留路径下面
        NLog.d(TAG, "checkApkIsInResidualByFirstDir strApkFirstDir = " + strApkFirstDir);
        if (TextUtils.isEmpty(strApkFirstDir)) {
            return false;
        }

        if (m_filteredResidualFolderSet.contains(strApkFirstDir)) {
            return true;
        }

        if (isOldSoftdetailDBDisabled()) {
            // 目前只有国内版开启了云端查询
            if (null == mIkResidualCloudQuery) {
                mIkResidualCloudQuery = CleanCloudFactory.createResidualCloudQuery(false);
                if (!mIkResidualCloudQuery.initialize()) {
                    mIkResidualCloudQuery = null;
                }
            }

            if (null != mIkResidualCloudQuery) {
                KResidualCloudQuery.DirQueryData[] dirRst = mIkResidualCloudQuery.localQueryDirAndSubDirInfo
                        (strApkFirstDir, false, null);
                if (null != dirRst && dirRst.length > 0) {
                    // 残留路径下的直接报
                    m_filteredResidualFolderSet.add(strApkFirstDir);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isOldSoftdetailDBDisabled() {
        return true;
    }

    /**
     * Download目录，Bluetooth, tmp, android/data
     * 目录及根目录下的apk包以外，只要不在残留库中，且个数达到2个，则过滤掉。
     * 在所有apk都扫完之後，最终在finishFilterProbablyUserFolder()函数中完成对留在map里面的apk判断。
     */
    private void filterProbablyUserFolder(APKModel apkModel) {

        APKModel oldApk = null;
        String pathTempStr = null;
        String nowPath = StringUtils.toLowerCase(apkModel.getPath());

//        synchronized (filteredProbablyUserFolderLock) {

            if (null == m_filterProbablyUserFolderMap) {
                m_filterProbablyUserFolderMap = new HashMap<>();
            }

            if (null == m_filteredProbablyUserFolderSet) {
                m_filteredProbablyUserFolderSet = new ArraySet<>();
            }

            if (null == m_filteredResidualFolderSet) {
                m_filteredResidualFolderSet = new ArraySet<>();
            }

            // check against already found user folders
            pathTempStr = nowPath.substring(0, nowPath.lastIndexOf(File.separatorChar));  // 获取父路径
            if (m_filteredProbablyUserFolderSet.contains(pathTempStr)) {  // 有2个APK的路径
                if (!isExistInSameApkLib(apkModel)) {
                    apkModel.setChecked(false);
                } else {
                    apkModel.setChecked(true);
                }
                apkModel.setCurrentApkIsInUserFolder();
                apkModel.setIsDisplay(mbShowAllApk);
                return;
            }

            if (isExistInCheckPathList(nowPath, apkModel)) {
                isExistInSameApkLib(apkModel);
                apkModel.setChecked(true);
                return;
            }

            oldApk = m_filterProbablyUserFolderMap.get(pathTempStr);
            if (null == oldApk) { // Map中没有
                // first apk found in folder, need to wait
                m_filterProbablyUserFolderMap.put(pathTempStr, apkModel);
//                String strTemp = getPathWithoutSDPath(apkModel.getPath());
//                if (strTemp.indexOf(File.separator) < 0) { // 在根目录下面
//                    if (isExistInSameApkLib(apkModel)) {
//                        Log.e("ApkCheck", "==> find apk file in sd root=" + apkModel.getPath());
//                        apkModel.setCurrentApkIsInUserFolder();
//                    }
//                }
                apkModel.setIsDisplay(false);
            } else {
                // found 2nd apk in folder, set path as user folder
                // display both apks

                // 统一个目录下面找到第二个APK了，将可能的转为真正的用户目录
                m_filterProbablyUserFolderMap.remove(pathTempStr);  // 移除可能的路径
                m_filteredProbablyUserFolderSet.add(pathTempStr);  //  转换成真正的路径

                if (isExistInSameApkLib(oldApk)) {
                    Log.e("ApkCheck", "==> old apk is in SameApkLib " + oldApk.getPath());
                    oldApk.setChecked(true);
                } else {
                    oldApk.setChecked(false);
                }

                if (isExistInSameApkLib(apkModel)) {
                    Log.e("ApkCheck", "==> new apk is in SameApkLib " + apkModel.getPath());
                    apkModel.setChecked(true);
                } else {
                    apkModel.setChecked(false);
                }

                apkModel.setCurrentApkIsInUserFolder();
                oldApk.setCurrentApkIsInUserFolder();
                checkApkUninstallAndNewDownload(oldApk);
                oldApk.setIsDisplay(mbShowAllApk);

                onFoundItem(oldApk);
            }
//        }
    }

    private void finishFilterProbablyUserFolder() {

        if (!mbFilterProbablyUserFolder) {
            return;
        }

        m_filteredProbablyUserFolderSet = null;
        m_filteredResidualFolderSet = null;

        if (null != m_filterProbablyUserFolderMap && !m_filterProbablyUserFolderMap.isEmpty()) {
            APKModel apk = null;
            for (Map.Entry<String, APKModel> item : m_filterProbablyUserFolderMap.entrySet()) {
                if (null == item) {
                    continue;
                }

                apk = item.getValue();
                if (null == apk) {
                    continue;
                }
                if (mbShowAllApk) {
                    apk.setChecked(true);
                    apk.setIsDisplay(true);
                    onFoundItem(apk);
                }
            }
        }

        m_filterProbablyUserFolderMap = null;
    }

    private void onFoundItem(APKModel apkModel) {
        if (!apkModel.isDisplay()) {
            return;
        }

        if (!mApkScaned.isEmpty()) {
            if (mApkScaned.contains(apkModel.getPath())) {
                return;
            }
        }

        if (isCheckLocked) {
            apkModel.setChecked(!JunkLockedDAOHelper.getInstance().checkLocked(apkModel.getPath(), apkModel.isChecked
                    ()));
        }

        // 修改by chaohao.zhou 基本上的完全修改了原来APK扫描勾选的逻辑
//        customSetChecked(apkModel);

        if (null != mCB) {
            mCB.callbackMessage(HANDLER_APK_SCAN_UPDATE, 0, 0, apkModel);
        }

        mApkScaned.add(apkModel.getPath());
    }

    /**
     * 自定义apkModel的set Check 方法
     * 现在逻辑，已安装：旧版本（包括当前版本）勾选
     * 新版本 勾选非最新版本包（会跟新下载和新版本比较）
     * 未安装：未安装 勾选非最新版本包 （会跟新下载和未安装比较）
     * 新下载：       不勾选
     *
     * @param apkModel apkModel
     */
//    private void customSetChecked(APKModel apkModel) {
//        // 不管是否已安装，如果是新下载，都不勾选 新下载：1天时间内下载
//        String packageName = apkModel.getPackageName();
//        APKModel tempModel;
//        if (apkModel.getVersion() == null || apkModel.getVersion().isEmpty()) {// 没有VersionCode 可以理解为损坏包
//            if (apkModel.isUninstalledNewDL()) {
//                apkModel.setChecked(false); // 新下载，保留
//            } else {
//                apkModel.setChecked(true);  // 非新下载，勾选
//            }
//        } else {
//            if (apkModel.isUninstalledNewDL()) {
//                apkModel.setChecked(false);
//                if (!apkModelMap.containsKey(packageName)) {
//                    apkModelMap.put(packageName, apkModel);
//                } else {
//                    tempModel = apkModelMap.get(packageName);
//                    if (compareApkModelVersion(apkModel, tempModel)) {  // 如果现在的大于之前添加的，替换掉
//                        apkModelMap.put(packageName, apkModel);
//                    }
//                }
//            } else {
//                if (apkModel.type == APKModel.APK_INSTALLED) {  // 已安装
//                    if (apkModel.getApkInstallStatus() == APKModel.APK_STATUS_NEW) { // 如果是新版本，勾选版本号小的
//                        if (!apkModelMap.containsKey(packageName)) { // 之前没有同样的包名
//                            apkModel.setChecked(false);
//                            apkModelMap.put(packageName, apkModel);
//                        } else { // 有同样的包名
//                            tempModel = apkModelMap.get(packageName);
//                            if (compareApkModelVersion(apkModel, tempModel)) {  // 如果现在的版本大于之前添加的，替换掉
//                                apkModelMap.put(packageName, apkModel);
//                                tempModel.setChecked(true); // 勾选旧版本的
//                                apkModel.setChecked(false); // 高版本不勾选
//                                if (null != mCB) {  // 由于tempModel 由未勾选状态转为勾选状态，所以需要改变勾选数值
//                                    mCB.callbackMessage(HANDLER_APK_CHECKED_STATUS_UPDATE, 0, 0, tempModel);
//                                }
//                            } else {  // 现在的版本小于之前添加的，直接勾选
//                                apkModel.setChecked(true);
//                            }
//                        }
//                    } else {
//                        apkModel.setChecked(true); // 如果是旧版本，勾选
//                    }
//                } else if (apkModel.type == APKModel.APK_NOT_INSTALLED) { // 未安装
//                    if (!apkModelMap.containsKey(packageName)) { // 之前没有同样的包名
//                        apkModel.setChecked(false);
//                        apkModelMap.put(packageName, apkModel);
//                    } else { // 有同样的包名
//                        tempModel = apkModelMap.get(packageName);
//                        if (compareApkModelVersion(apkModel, tempModel)) {  // 如果现在的版本大于之前添加的，替换掉
//                            apkModelMap.put(packageName, apkModel);
//                            tempModel.setChecked(true); // 勾选旧版本的
//                            apkModel.setChecked(false); // 高版本不勾选
//                            if (null != mCB) { // 由于tempModel 由未勾选状态转为勾选状态，所以需要改变勾选数值
//                                mCB.callbackMessage(HANDLER_APK_CHECKED_STATUS_UPDATE, 0, 0, tempModel);
//                            }
//                        } else {  // 现在的版本小于之前添加的，直接勾选
//                            apkModel.setChecked(true);
//                        }
//                    }
//                }
//            }
//        }
//        // 最后都要更新
//        if (null != mCB) {
//            mCB.callbackMessage(HANDLER_APK_SCAN_UPDATE, 0, 0, apkModel);
//        }
//        mApkScaned.add(apkModel.getPath());
//    }

    /**
     * @return 两个ApkModel 版本号的大小比较， preApkModel > nextApkModel ? true : false
     */
    private boolean compareApkModelVersion(APKModel preApkModel, APKModel nextApkModel) {
        if (preApkModel.getVersionCode() <= nextApkModel.getVersionCode()) {
            return false;
        } else if (preApkModel.getVersionCode() > nextApkModel.getVersionCode()) {
            return true;
        } else if (preApkModel.getVersion().compareToIgnoreCase(nextApkModel.getVersion()) == 0) {
            // 防止部分apk包不修改versionCode，只改versionName的情况。
            return false;
        } else if (preApkModel.getVersion().compareToIgnoreCase(nextApkModel.getVersion()) < 0) {
            // 防止部分apk包不修改versionCode，只改versionName的情况。
            return false;
        } else {
            // 防止部分apk包不修改versionCode，只改versionName的情况。
            return true;
        }
    }

    @Override
    public boolean isFilter(String name) {
        loadAllRFWhiteList();
        if (TextUtils.isEmpty(name)) {
            return false;
        }

        // 由于云端残留等查询获取的路径大小写可能和原始的路径大写不一致,所以对于路径，一律转成小写
        String strLowerName = name;
        if (name.contains("/")) {
            strLowerName = StringUtils.toLowerCase(name);
        }

        if (mRFWhiteListMapSize > 0 && null != mRFWhiteListMap.get(strLowerName)) {
            return true;
        }

        return false;
    }


    Context mContext = null;

    @Override
    public boolean scan(ScanTaskController ctrl) {

        mContext = SpaceApplication.getInstance().getContext().getApplicationContext();
        mCtrl = ctrl;
        Thread.currentThread().setName("ApkScanTask");
        long startTime= SystemClock.uptimeMillis();
        try {
            if (null != mApkScanTaskCachedRst) {
                mApkScanTaskCachedRst.scan(ctrl);
            }

            scanApk();
        } finally {

            if (apkParseThread != null && !apkParseThread.mApkParser.isUpdateBlock()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        apkParseThread.mApkParser.updateCache();
                    }
                }).start();
            }

            if (null != mCB) {
                mCB.callbackMessage(CB_TYPE_SCAN_FINISH, (null != ctrl && ScanTaskController.TASK_CTRL_TIME_OUT ==
                        ctrl.getStatus()) ? 1 : 0, 0, null);
            }
        }
        long endTime=SystemClock.uptimeMillis();
        return true;
    }

    private boolean initMediaStoreQueryApk() {
        if (mScanSwitch) {
            if (mbUseCompoundScan || (mbUseMediaSpeedUp && !mbUseCompoundScan)) {
                mbQueryApk = true;
                return true;
            }
        }
        mbQueryApk = false;
        return mbQueryApk;
    }

    private boolean initMediaStoreQueryTmp(Context c) {
        if ((mScanCfgMask & SCAN_CFG_SCAN_TMP_FILE) == 0) {
            mbQueryTmp = false;
            return false;
        }
        // 服务器控制过滤，防止误删。
        if (DBColumnFilterManager.getInstance().isFilter(DBColumnFilterManager.EXPAND_FILTER_TABLE_NAME_STUB,
                DBColumnFilterManager.EXPAND_FILTER_ID_TEMP_FILES_SCAN)) {
            mbQueryTmp = false;
            return false;
        }

        mbQueryTmp = !ResidualFileWhiteListDAOHelper.getInstance().isRFWhiteListItem(WhiteListsWrapper
                .FUNCTION_FILTER_NAME_TEMP_FILES_SCAN);
        if (mbQueryTmp) {
            mTmpInfo.setFileType(BaseJunkBean.FileType.File);
            mTmpInfo.setStrDirPath(null);
            mTmpInfo.setChineseName(c.getResources().getString(R.string.junk_tag_RF_TempFiles));
            mTmpInfo.setFoldersCount(0);
            mTmpInfo.setApkName(c.getResources().getString(R.string.junk_tag_RF_TempFiles));
            mTmpInfo.setCheck(true);
            mTmpInfo.setType(SDcardRubbishResult.RF_TEMPFILES);
            mTmpInfo.SetWhiteListKey(WhiteListsWrapper.FUNCTION_FILTER_NAME_TEMP_FILES_SCAN);
            mTmpInfo.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
            if (!mTmpInfo.isCheck()) {
                mTmpInfo.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
                mTmpInfo.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV);
            }

            mTmpInfoOld = new SDcardRubbishResult(mTmpInfo);
        }
        return mbQueryTmp;
    }

    private boolean initMediaStoreQueryLog(Context c) {
        if ((mScanCfgMask & SCAN_CFG_SCAN_LOG_FILE) == 0) {
            mbQueryLog = false;
            return false;
        }
        // 服务器控制过滤，防止误删。
        if (DBColumnFilterManager.getInstance().isFilter(DBColumnFilterManager.EXPAND_FILTER_TABLE_NAME_STUB,
                DBColumnFilterManager.EXPAND_FILTER_ID_LOG_FILES_SCAN2)) {
            mbQueryLog = false;
            return false;
        }

        mbQueryLog = !ResidualFileWhiteListDAOHelper.getInstance().isRFWhiteListItem(WhiteListsWrapper
                .FUNCTION_FILTER_NAME_LOG_FILES_SCAN);
        if (mbQueryLog) {
            mLogInfo.setFileType(BaseJunkBean.FileType.File);
            mLogInfo.setStrDirPath(null);
            mLogInfo.setChineseName(c.getResources().getString(R.string.junk_tag_RF_LogFiles));
            mLogInfo.setFoldersCount(0);
            mLogInfo.setApkName(c.getResources().getString(R.string.junk_tag_RF_LogFiles));
            mLogInfo.setCheck(true);
            mLogInfo.setType(SDcardRubbishResult.RF_TEMPFILES);
            mLogInfo.SetWhiteListKey(WhiteListsWrapper.FUNCTION_FILTER_NAME_LOG_FILES_SCAN);
            mLogInfo.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
            if (!mLogInfo.isCheck()) {
                mLogInfo.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
                mLogInfo.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER_ADV);
            }

            mLogInfoOld = new SDcardRubbishResult(mLogInfo);
        }
        return mbQueryLog;
    }

    private SDcardRubbishResult mTmpInfoOld = null;
    private SDcardRubbishResult mLogInfoOld = null;
    private SDcardRubbishResult mTmpInfo = new SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER);
    private SDcardRubbishResult mLogInfo = new SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE.TEMPFOLDER);
    private boolean mbQueryApk = false;
    private boolean mbQueryTmp = false;
    private boolean mbQueryLog = false;

    boolean checkFilter(String strFilterColumnIndex, String strWhiteListFilterName, AtomicBoolean isIgnoreItem) {
        if (strFilterColumnIndex != null) {
            // 服务器控制过滤，防止误删。
            if (DBColumnFilterManager.getInstance().isFilter(DBColumnFilterManager.EXPAND_FILTER_TABLE_NAME_STUB,
                    strFilterColumnIndex)) {
                return true;
            }
        }
        isIgnoreItem.set(false);
        if (isFilter(strWhiteListFilterName)) {
            isIgnoreItem.set(true);
            if ((mScanCfgMask & SCAN_CFG_MASK_NOT_RETURN_IGNORE) != 0) {
                return true;
            }
        }
        return false;
    }

    class SpecialFolderEx extends SpecialFolder {
        public boolean misIgnore = false;
        public List<String> mPaths;
        public int mDisplayStringId;
        public String mstrWhiteListFilterName;
        public SDcardRubbishResult mTargetResult;
        public boolean mbSysFixedItem = false;
        public boolean mbPushLogFile = false;
        public CacheInfo mTargetResult2;
    }

    void initRootPathSpecialFolders(List<SpecialFolderEx> specialFolders) {
        if (specialFolders == null) {
            return;
        }
        AtomicBoolean isIgnoreItem = new AtomicBoolean();
        if (!checkFilter(DBColumnFilterManager.EXPAND_FILTER_ID_LOST_DIR_FILE_SCAN, WhiteListsWrapper
                .FUNCTION_FILTER_NAME_LOST_DIR_FILE_SCAN, isIgnoreItem)) {
            SpecialFolderEx sFolder = new SpecialFolderEx();
            sFolder.mStrPath = "lost.dir";
            sFolder.misIgnore = isIgnoreItem.get();
            sFolder.mDisplayStringId = R.string.junk_tag_RF_LostDirFiles;
            sFolder.mstrWhiteListFilterName = WhiteListsWrapper.FUNCTION_FILTER_NAME_LOST_DIR_FILE_SCAN;
            sFolder.mStrRegex = "^[0-9][0-9]*$";
            specialFolders.add(sFolder);
        }
        if (!checkFilter(DBColumnFilterManager.EXPAND_FILTER_ID_TAOBAO_LOG_FILE_SCAN, WhiteListsWrapper
                .FUNCTION_FILTER_NAME_TAOBAO_LOG_FILE_SCAN, isIgnoreItem)) {
            SpecialFolderEx sFolder = new SpecialFolderEx();
            sFolder.mStrPath = "";
            sFolder.misIgnore = isIgnoreItem.get();
            sFolder.mDisplayStringId = R.string.junk_tag_RF_TaobaoLogFiles;
            sFolder.mstrWhiteListFilterName = WhiteListsWrapper.FUNCTION_FILTER_NAME_TAOBAO_LOG_FILE_SCAN;
            sFolder.mEndsWithArr = new ArrayList<String>();
            sFolder.mEndsWithArr.add(".lck");
            sFolder.mStartsWithArr = new ArrayList<String>();
            sFolder.mStartsWithArr.add("com.taobao.taobao_");
            specialFolders.add(sFolder);
        }
        if (!checkFilter(DBColumnFilterManager.EXPAND_FILTER_ID_LIBS_DIR_FILE_SCAN, WhiteListsWrapper
                .FUNCTION_FILTER_NAME_LIBS_DIR_FILE_SCAN, isIgnoreItem)) {
            SpecialFolderEx sFolder = new SpecialFolderEx();
            sFolder.mStrPath = "libs";
            sFolder.misIgnore = isIgnoreItem.get();
            sFolder.mDisplayStringId = R.string.junk_tag_RF_LibsDirFiles;
            sFolder.mstrWhiteListFilterName = WhiteListsWrapper.FUNCTION_FILTER_NAME_LIBS_DIR_FILE_SCAN;
            sFolder.mEndsWithArr = new ArrayList<String>();
            sFolder.mEndsWithArr.add(".db");
            specialFolders.add(sFolder);
        }
        if (!checkFilter(DBColumnFilterManager.EXPAND_FILTER_ID_MFCACHE_DIR_FILE_SCAN, WhiteListsWrapper
                .FUNCTION_FILTER_NAME_MFCACHE_DIR_FILE_SCAN, isIgnoreItem)) {
            SpecialFolderEx sFolder = new SpecialFolderEx();
            sFolder.mStrPath = "mfcache";
            sFolder.misIgnore = isIgnoreItem.get();
            sFolder.mDisplayStringId = R.string.junk_tag_RF_MFCacheDirFiles;
            sFolder.mstrWhiteListFilterName = WhiteListsWrapper.FUNCTION_FILTER_NAME_MFCACHE_DIR_FILE_SCAN;
            sFolder.mEndsWithArr = new ArrayList<String>();
            sFolder.mEndsWithArr.add(".cache");
            specialFolders.add(sFolder);
        }


        // 视频信息
        /*索尼手机演示视频：
        展示归类：建议清理-->缓存 （同广告）
		itemname中文：索尼手机演示用视频
		itemname英文：Sony demo videos
		itemname繁体：索尼手機演示用視頻

		规则：
		/demovideo、/.demovideo 目录下以下文件：
		demo_video_*_*.mp4*/
        SpecialFolderEx sFolder = new SpecialFolderEx();
        sFolder.mPaths = new ArrayList<String>();
        sFolder.mPaths.add("demovideo");
        sFolder.mPaths.add(".demovideo");
        sFolder.mDisplayStringId = R.string.junk_tag_RF_DemoVideo_Sony;
        sFolder.mStrRegex = "^demo_video_.*\\.mp4$";
        specialFolders.add(sFolder);
		/*/video 目录下以下文件: 100M以内
		Xperia HD Landscapes.mp4
		xperia_hd_landscapes.mp4
		gt5_concept_movie_xperia.mp4
		xperia_hd_landscapes_wvga.mp4
		Xperia HD Landscapes_1.mp4
		Sony Xperia Z2 Commercial_HD.mp4
		Sony Xperia Z1 - Official Promo Trailer (Full HD 1080p)_HD.mp4*/
        sFolder = new SpecialFolderEx();
        sFolder.mStrPath = "video";
        sFolder.mDisplayStringId = R.string.junk_tag_RF_DemoVideo_Sony;
        sFolder.mFullsMatchArr = new ArrayList<String>();
        sFolder.mFullsMatchArr.add("xperia hd landscapes.mp4");
        sFolder.mFullsMatchArr.add("gt5_concept_movie_xperia.mp4");
        sFolder.mFullsMatchArr.add("xperia_hd_landscapes_wvga.mp4");
        sFolder.mFullsMatchArr.add("xperia hd landscapes_1.mp4");
        sFolder.mFullsMatchArr.add("sony xperia z2 commercial_hd.mp4");
        sFolder.mFullsMatchArr.add("sony xperia z1 - official promo trailer (full hd 1080p)_hd.mp4");
        specialFolders.add(sFolder);
		/*三星手机演示视频：
		展示归类：建议清理-->缓存 （同广告）
		itemname中文：三星手机演示用视频
		itemname英文：Samsung demo videos
		itemname繁体：三星手機演示用視頻
		规则：
			/Samsung/Video、/Samsung/Movie 目录下以下文件: : 200M以内
			Helicopter.mkv
			Helicopter.mp4
			Wonders_of_Nature.mp4 100M以内
			Wonders of Nature.mp4
			Moments_of_Everyday_Life.mp4
			Art of Flight.mp4
			Wonders_of_Nature_800x480_3MB_0315.mp4
			Sound_Visual.mp4
		 */
        sFolder = new SpecialFolderEx();
        sFolder.mPaths = new ArrayList<String>();
        sFolder.mPaths.add("samsung/video");
        sFolder.mPaths.add("samsung/movie");
        sFolder.mDisplayStringId = R.string.junk_tag_RF_DemoVideo_Samsung;
        sFolder.mFullsMatchArr = new ArrayList<String>();
        sFolder.mFullsMatchArr.add("helicopter.mkv");
        sFolder.mFullsMatchArr.add("helicopter.mp4");
        sFolder.mFullsMatchArr.add("wonders_of_nature.mp4");
        sFolder.mFullsMatchArr.add("moments_of_everyday_life.mp4");
        sFolder.mFullsMatchArr.add("art of flight.mp4");
        sFolder.mFullsMatchArr.add("wonders_of_nature_800x480_3mb_0315.mp4");
        sFolder.mFullsMatchArr.add("sound_visual.mp4");
        specialFolders.add(sFolder);
		/*MIUI演示视频：100M以内
		展示归类：建议清理-->缓存 （同广告）
		itemname中文：小米手机品牌展示视频
		itemname英文：Xiaomi demo videos
		itemname繁体：小米手機品牌展示視頻
		详情中文：这是小米手机做品牌宣传用的视频
		详情英文：These are Xiaomi's demo videos.
		详情繁体：這是小米手機做品牌宣傳用的視頻
		规则：
		/MIUI/Gallery/DemoVideo 目录下以下文件：
		XiaomiPhone.mp4
		MIUI_V5.mp4*/
        sFolder = new SpecialFolderEx();
        sFolder.mStrPath = "miui/gallery/demovideo";
        sFolder.mDisplayStringId = R.string.junk_tag_RF_DemoVideo_MIUI;
        sFolder.mFullsMatchArr = new ArrayList<String>();
        sFolder.mFullsMatchArr.add("xiaomiphone.mp4");
        sFolder.mFullsMatchArr.add("miui_v5.mp4");
        specialFolders.add(sFolder);
		/*LG演示视频：
		展示归类：建议清理-->缓存 （同广告）
		itemname中文：LG手机演示用视频
		itemname英文：LG demo videos
		itemname繁体：三星手機演示用視頻

		规则：
		/Preload/LG、/Preload 目录下以下文件：100M以内
		01_Life_Is_Good.flac
		02_Heart_of_Jungle.flac
		03_Air_on_the_G_String.flac
		04_Arirang.flac
		Life_Is_Good.flac*/
        sFolder = new SpecialFolderEx();
        sFolder.mPaths = new ArrayList<String>();
        sFolder.mPaths.add("preload");
        sFolder.mPaths.add("preload/lg");
        sFolder.mDisplayStringId = R.string.junk_tag_RF_DemoVideo_LG;
        sFolder.mFullsMatchArr = new ArrayList<String>();
        sFolder.mFullsMatchArr.add("01_life_is_good.flac");
        sFolder.mFullsMatchArr.add("02_heart_of_jungle.flac");
        sFolder.mFullsMatchArr.add("03_air_on_the_g_string.flac");
        sFolder.mFullsMatchArr.add("04_arirang.flac");
        sFolder.mFullsMatchArr.add("life_is_good.flac");
        specialFolders.add(sFolder);

        if (mSysFixedFileScanTask != null) {
            long nTimeLine = mSysFixedFileScanTask.getTimeLine() / 1000; // 单位 s

            sFolder = new SpecialFolderEx();
            sFolder.mStrPath = "";
            sFolder.mDisplayStringId = R.string.junk_tag_system_fixed_cache_item_sd_pushLog_title;
            sFolder.mFullsMatchArr = new ArrayList<String>();
            sFolder.mFullsMatchArr.add("pushlog.txt");
            sFolder.mbSysFixedItem = true;
            specialFolders.add(sFolder);

            sFolder = new SpecialFolderEx();
            sFolder.mStrPath = "lost+found";
            sFolder.mDisplayStringId = R.string.junk_tag_system_fixed_cache_item_sd_lostfound_title;
            sFolder.mTimeLine = nTimeLine;
            sFolder.mCalSparseSize = true;
            sFolder.mbSysFixedItem = true;
            specialFolders.add(sFolder);
        }

    }

    void setRootPathSpecialFolders(String strCardRootPath, ScanTargetFolderDir rootDir, List<SpecialFolderEx>
            specialFolders) {
        if (specialFolders.isEmpty()) {
            return;
        }

        rootDir.specialFolders = new ArrayList<SpecialFolder>();

        for (final SpecialFolderEx specialFolderEx : specialFolders) {
            if (specialFolderEx.mPaths == null) {
                specialFolderEx.mPaths = new ArrayList<String>();
                specialFolderEx.mPaths.add(specialFolderEx.mStrPath);
            }
            specialFolderEx.mCallback = new PathScanCallback() {
                @Override
                public void onFile(String filePath, long size, int nType, long createTime, long modifyTime, long
                        accessTime, long mode) {
                    if (specialFolderEx.mbSysFixedItem) {
                        onFile_SYSFIXEDCACHE(filePath, size, nType);
                    } else {
                        onFile_TEMPFOLDER(filePath, size, nType);
                    }
                }

                private void onFile_TEMPFOLDER(String filePath, long size, int nType) {
                    synchronized (specialFolderEx) {
                        if (specialFolderEx.mTargetResult == null) {
                            specialFolderEx.mTargetResult = new SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE
                                    .TEMPFOLDER);
                            specialFolderEx.mTargetResult.setCheck(true);
                            specialFolderEx.mTargetResult.setType(SDcardRubbishResult.RF_TEMPFILES);
                            specialFolderEx.mTargetResult.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
                            if (!specialFolderEx.mTargetResult.isCheck()) {
                                specialFolderEx.mTargetResult.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
                                specialFolderEx.mTargetResult.setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE
                                        .TEMPFOLDER_ADV);
                            }
                            if (specialFolderEx.misIgnore) {
                                specialFolderEx.mTargetResult.setIgnore(true);
                            }
                            String resString = mContext.getResources().getString(specialFolderEx.mDisplayStringId);
                            specialFolderEx.mTargetResult.setChineseName(resString);
                            specialFolderEx.mTargetResult.setApkName(resString);
                            specialFolderEx.mTargetResult.setStrDirPath(specialFolderEx.mstrWhiteListFilterName);
                            specialFolderEx.mTargetResult.SetWhiteListKey(specialFolderEx.mstrWhiteListFilterName);
                        }
                    }
                    synchronized (specialFolderEx.mTargetResult) {
                        specialFolderEx.mTargetResult.addPathList(filePath);
                        specialFolderEx.mTargetResult.setSize(specialFolderEx.mTargetResult.getSize() + size);
                        specialFolderEx.mTargetResult.setFilesCount(specialFolderEx.mTargetResult.getFilesCount() + 1);
                    }
                }

                private void onFile_SYSFIXEDCACHE(String filePath, long size, int nType) {
                    synchronized (specialFolderEx) {
                        if (specialFolderEx.mTargetResult2 == null) {
                            specialFolderEx.mTargetResult2 = new CacheInfo(JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE);
                            specialFolderEx.mTargetResult2.setCheck(true);
                            specialFolderEx.mTargetResult2.setInfoType(CacheInfo.INFOTYPE_SYSFIXEDFIELITEM);
                            String resString = mContext.getResources().getString(specialFolderEx.mDisplayStringId);
                            specialFolderEx.mTargetResult2.setAppName(resString);
                            File file = new File(filePath);
                            specialFolderEx.mTargetResult2.setFilePath(file.getParent());
                        }
                    }
                    synchronized (specialFolderEx.mTargetResult2) {
                        specialFolderEx.mTargetResult2.setSize(specialFolderEx.mTargetResult2.getSize() + size);
                        specialFolderEx.mTargetResult2.appendCleanTimeFileList(filePath);
                    }
                }
            };

            for (String strPath : specialFolderEx.mPaths) {
                SpecialFolder sFolder = new SpecialFolder();
                sFolder.mStrPath = strCardRootPath + strPath;
                sFolder.mCallback = specialFolderEx.mCallback;
                sFolder.mTimeLine = specialFolderEx.mTimeLine;
                sFolder.mStrRegex = specialFolderEx.mStrRegex;
                sFolder.mFullsMatchArr = specialFolderEx.mFullsMatchArr;
                sFolder.mStartsWithArr = specialFolderEx.mStartsWithArr;
                sFolder.mEndsWithArr = specialFolderEx.mEndsWithArr;
                sFolder.mContainsArr = specialFolderEx.mContainsArr;
                sFolder.mNotContainsArr = specialFolderEx.mNotContainsArr;
                rootDir.specialFolders.add(sFolder);
            }
        }
    }

    void scanApkByEnumDiskOneCard(String strCardRootPath, int nScanTypes, PathScanCallback callback,
                                  List<SpecialFolderEx> specialFolders) {
        Set<ScanTargetFolderDir> listDirs = new TreeSet<ScanTargetFolderDir>(new Comparator<ScanTargetFolderDir>() {
            @Override
            public int compare(ScanTargetFolderDir lhs, ScanTargetFolderDir rhs) {
                return lhs.targetPath.compareToIgnoreCase(rhs.targetPath);
            }
        });
        ScanTargetFolderDir rootDir = new ScanTargetFolderDir();
        strCardRootPath = FileUtils.addSlash(strCardRootPath);
        rootDir.targetPath = strCardRootPath;
        rootDir.maxScanLevel = mFolderScanLevel;
        rootDir.ignoreDirs = new ArrayList<>();
        rootDir.ignoreDirs.add(strCardRootPath +"DCIM"); //"DCIM"
        rootDir.ignoreDirs.add(strCardRootPath +"Android/data"); //"Android/data"
        rootDir.ignoreDirs.add(strCardRootPath +"tencent"); // tencent
        rootDir.ignoreDirs.add(strCardRootPath + "download"); // download
        rootDir.ignoreDirs.add(strCardRootPath + "bluetooth"); //bluetooth
        rootDir.ignoreDirs.add(strCardRootPath + "games/com.mojang/minecraftworlds"); // games/com.mojang/minecraftworlds
        rootDir.ignoreDirs.add(strCardRootPath + "baidu/searchbox/books"); //
        rootDir.ignoreDirs.add(strCardRootPath +"baidu/flyflow/novel"); //
        rootDir.ignoreDirs.add(strCardRootPath +"cloudagent/cache/dropbox"); //
        rootDir.ignoreDirs.add(strCardRootPath + "tapatalk4/cache/longterm"); //
        rootDir.ignoreDirs.add(strCardRootPath + "cloudagent/cache/root"); //

        rootDir.progBarCapacity = PROG_BAR_LIST_STORAGE_FILE / mExternalStoragePaths.size();
        setRootPathSpecialFolders(strCardRootPath, rootDir, specialFolders);
        listDirs.add(rootDir);
        for (TargetFolderParam param : mTargetFolderParamList) {
            String targetPath = strCardRootPath + param.strPath;
            ScanTargetFolderDir target = new ScanTargetFolderDir();
            target.targetPath = targetPath;
            target.maxScanLevel = param.nScanLevel;
            target.progBarCapacity = param.nProgressBarCap;
            target.foundFileLimit = param.nFileLimit;
            target.foundFolderLimit = param.nFolderLimit;
            listDirs.add(target);
        }

        ProgressCtrl progCtrl = null;
        if (null != mCtrl) {
            progCtrl = new ProgressCtrl() {
                @Override
                public boolean isStop() {
                    return mCtrl.checkStop();
                }
            };
        }

        for (ScanTargetFolderDir target : listDirs) {
            if (null != mCtrl && mCtrl.checkStop()) {
                break;
            }
            ProgressControl progressControl = null;
            if (mCB != null) {
                progressControl = new ProgressControl(mCB, HANDLER_ADD_PROGRESS);
                progressControl.startControl(mProgressBarTotal, target.progBarCapacity, true);
                progressControl.setStepNum(2);
                progressControl.addStep();
            }

            PathOperFunc.pathScan(target.targetPath, progCtrl, target.maxScanLevel, target.foundFileLimit, target
                    .foundFolderLimit, target.ignoreDirs, target.specialFolders, nScanTypes, callback);
            if (mCB != null) {
                progressControl.addStep();
                progressControl.stopControl();
            }
        }
    }

    AtomicLong mFindApkCount = new AtomicLong();

    private void scanApkByEnumDisk() {

        if (!mSoLoaded) {
            mSoLoaded = false;//SoLoader.doLoad(false);
        }
        mFindApkCount.set(0);
        Context context = SpaceApplication.getInstance().getContext();
        initMediaStoreQueryApk();
//        initMediaStoreQueryTmp(context);
//        initMediaStoreQueryLog(context);

        int _nScanTypes = 0;
        if (mbQueryApk) {
            _nScanTypes |= PathScanCallback.TYPE_APK;
        }

        if (mbQueryTmp) {
            _nScanTypes |= PathScanCallback.TYPE_TMP;
        }
        if (mbQueryLog) {
            _nScanTypes |= PathScanCallback.TYPE_LOG;
        }


        final List<SpecialFolderEx> specialFolders = new ArrayList<SpecialFolderEx>();
        if (mbQueryTmp || mbQueryLog) {
            loadAllRFWhiteList();
            initRootPathSpecialFolders(specialFolders);
        }

        final int nScanTypes = _nScanTypes;
        final PathScanCallback callback = new PathScanCallback() {

            @Override
            public void onFile(String filePath, long size, int nType, long createTime, long modifyTime, long
                    accessTime, long mode) {
                ///> filt apks which in white file list
//                NLog.d("onFile", "filePath" + filePath);
                GenericWhiteInfo info = new GenericWhiteInfo();
                if (isExistInWhiteList(nType, filePath, info) && nType != PathScanCallback.TYPE_APK) {
//					Log.e("david", "isExistInWhiteList=true");
                    return;
                }

                switch (nType) {
                    case PathScanCallback.TYPE_APK:

                        mFindApkCount.incrementAndGet();
                        File file = new File(filePath);
                        if (mCB != null) {
                            mCB.callbackMessage(HANDLER_APK_DIR_SHOW, 0, 0, file.getParent());
                        }
                        apkParseThread.putOneApkFile(file, info);
                        break;
                    case PathScanCallback.TYPE_LOG:
                        // Log.d("apkstest", "log:"+filePath);
                        synchronized (mLogInfo) {
                            mLogInfo.addPathList(filePath);
                            mLogInfo.setFilesCount(mLogInfo.getFilesCount() + 1);
                            mLogInfo.setSize(mLogInfo.getSize() + size);
                        }
                        break;
                    case PathScanCallback.TYPE_TMP:
                        // Log.d("apkstest", "tmp:"+filePath);
                        synchronized (mTmpInfo) {
                            mTmpInfo.addPathList(filePath);
                            mTmpInfo.setFilesCount(mTmpInfo.getFilesCount() + 1);
                            mTmpInfo.setSize(mTmpInfo.getSize() + size);
                        }
                        break;
                    case PathScanCallback.TYPE_BIG_10M:
                        break;
                    default:
                        break;
                }
            }
        };

        long nStartTime = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(mExternalStoragePaths.size());
        for (final String rootString : mExternalStoragePaths) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    scanApkByEnumDiskOneCard(rootString, nScanTypes, callback, specialFolders);
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            apkParseThread.allApkOffered();//所有的apk都提供给解析线程了
        }
        if (mCB != null) {
            if (mLogInfo.getFilesCount() > 0) {
                mCB.callbackMessage(HANDLER_UPDATE_MEDIASTORE_ITEM, SDcardRubbishResult.RF_TEMPFILES, 0, new
                        RubbishFileScanTask.UpdateChildrenData(mLogInfoOld, mLogInfo));
            }
            if (mTmpInfo.getFilesCount() > 0) {
                mCB.callbackMessage(HANDLER_UPDATE_MEDIASTORE_ITEM, SDcardRubbishResult.RF_TEMPFILES, 0, new
                        RubbishFileScanTask.UpdateChildrenData(mTmpInfoOld, mTmpInfo));
            }
            for (SpecialFolderEx specialFolderEx : specialFolders) {
                if (specialFolderEx.mTargetResult != null) {
                    mCB.callbackMessage(HANDLER_FIND_MEDIASTORE_ITEM, SDcardRubbishResult.RF_TEMPFILES, 0,
                            specialFolderEx.mTargetResult);
                }
                if (specialFolderEx.mTargetResult2 != null && mSysFixedFileScanTask != null) {
                    mSysFixedFileScanTask.callbackMessage(SysFixedFileScanTask.SYS_FIXED_FILE_SCAN_FOUND_ITEM, 0, 0,
                            specialFolderEx.mTargetResult2);
                }
            }
        }
    }

    private int mRFWhiteListMapSize = 0;
    private ArrayMap<String, ProcessModel> mRFWhiteListMap = new ArrayMap<String, ProcessModel>();

    private boolean mbLoadAllRFWhiteList = false;

    private void loadAllRFWhiteList() {
        if (mbLoadAllRFWhiteList) {
            return;
        }
        mbLoadAllRFWhiteList = true;
        if (!mRFWhiteListMap.isEmpty()) {
            return;
        }

        mRFWhiteListMapSize = 0;
        mRFWhiteListMap.clear();
        List<ProcessModel> tmpWhiteList = WhiteListsWrapper.getRFWhiteList();
        if (null != tmpWhiteList) {
            for (ProcessModel tmpModel : tmpWhiteList) {
                if (!TextUtils.isEmpty(tmpModel.getPkgName())) {
                    mRFWhiteListMap.put(tmpModel.getPkgName(), tmpModel);
                }
            }
            mRFWhiteListMapSize = mRFWhiteListMap.size();
        }

    }

    SysFixedFileScanTask mSysFixedFileScanTask = null;

    public void setSysFixTask(SysFixedFileScanTask task) {
        mSysFixedFileScanTask = task;
    }

    ArrayList<String> mExternalStoragePaths = null;

    private void scanApk() {
        if (null != mCB) {
            mCB.callbackMessage(HANDLER_SATRT_TASK, 0, 0, null);
        }

        //FIXME BY Davis
//        WhiteInfoManager.getInstance().initGenericWhiteList();
        m_strExternalDir = Environment.getExternalStorageDirectory().getAbsolutePath();

        // 外部设备路径
        mExternalStoragePaths = (new StorageList()).getMountedVolumePaths();
        if (mExternalStoragePaths == null || mExternalStoragePaths.isEmpty()) {
            if (null != mCB) {
                mCB.callbackMessage(HANDLER_INVALID_SD_STORAGE, 0, 0, null);
            }
            return;
        }

        initFixedPath();
        mProgressBarTotal = PROG_BAR_LIST_STORAGE_FILE + PROG_BAR_PARSER_FILE;
        if (null != mTargetFolderParamList) {
            int nSize = 0;
            for (TargetFolderParam param : mTargetFolderParamList) {
                nSize += param.nProgressBarCap;
            }
            mProgressBarTotal += nSize * mExternalStoragePaths.size();
        }

        installedAPKModels = new ArrayList<>();
        notInstalledAPKModels = new ArrayList<>();
        apkModelMap = new HashMap<>();

        apkParseThread = new ApkParseThread(this,mCtrl,mCB);
        apkParseThread.start();
        try {
            scanApkByEnumDisk();
        } finally {

        }

        if (mSysFixedFileScanTask != null) {
            TimingUtil.start(SysFixedFileScanTask.class.getName());
            mSysFixedFileScanTask.scan(mCtrl);
        }

//        mMediaStoreAPKPath.clear();
//        mScannedApkFolderPath.clear();

//        apkParseThread.notifyStartWaitForFinish();
        try {
            apkParseThread.join();
        } catch (Exception e) {
        }

        finishFilterProbablyUserFolder();
        cleanUpResource();

        if ((installedAPKModels.size() + notInstalledAPKModels.size()) <= 0) {
            if (null != mCB) {
                mCB.callbackMessage(HANDLER_APK_NOT_EXISTS, installedAPKModels.size(), notInstalledAPKModels.size(),
                        null);
            }
            return;
        }

        try {
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            if (null != installedAPKModels && installedAPKModels.size() > 0) Collections.sort(installedAPKModels);

            if (null != notInstalledAPKModels && notInstalledAPKModels.size() > 0)
                Collections.sort(notInstalledAPKModels);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }

        ListViewDatas data = new ListViewDatas();
        data.installedApk = installedAPKModels;
        data.uninstalledApk = notInstalledAPKModels;
        if (null != mCB) {
            mCB.callbackMessage(HANDLER_APKMODELS_INIT_COMPLETED, 0, 0, data);
        }
    }

    /*
     * 初始化固定目录
     */
    private void initFixedPath() {

        TargetFolderParam[] datas= ApkTaskData.getFixedDatas();
        mTargetFolderParamList.addAll(Arrays.asList(datas));
        //FIXME BY Davis
//        List<String> list = CICManager.getInstance().getTmpLogBigFolders(4);
//        if (list != null && list.size() > 0) {
//            for (String path : list) {
//                mTargetFolderParamList.add(new TargetFolderParam(path));
//            }
//        }
    }

    private void cleanUpResource() {

        if (null != mIkResidualCloudQuery) {
            mIkResidualCloudQuery.unInitialize();
            mIkResidualCloudQuery = null;
        }

        // if (null != mCur) {
        // mCur.close();
        // mCur = null;
        // }
        // if (null != mAloDb) {
        // DbSoftDetailManager.getIns().closeDatabase(mAloDb);
        // mAloDb = null;
        // }
    }


    protected class ScanTargetFolderDir {
        public String targetPath = null;
        public int maxScanLevel = 0;
        public List<String> ignoreDirs;//TODO wtb过滤hashSet比List高效
        public List<SpecialFolder> specialFolders;
        public int progBarCapacity = 0;
        public int foundFileLimit = TargetFolderParam.DEF_FILE_LIMIT;
        public int foundFolderLimit = TargetFolderParam.DEF_FOLDER_LIMIT;
    }

    @Override
    public String getTaskDesc() {
        return "ApkScanTask";
    }

    boolean isExistInWhiteList(int nType, String strFilePath, GenericWhiteInfo info) {
        //FIXME BY Davis
//        int nValType = WhiteInfoManager.DB_WHITELIST_TYPE_UNKNOWN;
//        switch (nType) {
//            case PathScanCallback.TYPE_APK:
//                nValType = WhiteInfoManager.DB_WHITELIST_TYPE_APK;
//                break;
//            case PathScanCallback.TYPE_LOG:
//                nValType = WhiteInfoManager.DB_WHITELIST_TYPE_LOG;
//                break;
//            case PathScanCallback.TYPE_TMP:
//                nValType = WhiteInfoManager.DB_WHITELIST_TYPE_TMP;
//                break;
//            default:
//                return false;
//        }
//
//        return WhiteInfoManager.getInstance().isExistInWhiteList(nValType, strFilePath, info);
        return false;

    }
}

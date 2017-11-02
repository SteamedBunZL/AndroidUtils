package com.clean.spaceplus.cleansdk.junk.engine.task;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.db.LocalStringDbUtil;
import com.clean.spaceplus.cleansdk.base.db.cleanpath_cache.CleanPathCacheProvider;
import com.clean.spaceplus.cleansdk.base.db.strings2_cache.Strings2CacheProvider;
import com.clean.spaceplus.cleansdk.base.scan.IScanFilter;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.junk.engine.ProgressCtrl;
import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.bean.SDcardRubbishResult;
import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.junk.engine.util.NameFilter;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.clean.spaceplus.cleansdk.main.bean.cleanpath_cache.AdvFolder;
import com.clean.spaceplus.cleansdk.util.EnableCacheListDir;
import com.clean.spaceplus.cleansdk.util.StringUtils;
import com.clean.spaceplus.cleansdk.util.md5.MD5PathConvert;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author Jerry
 * @Description:
 * @date 2016/4/30 10:32
 * @copyright TCL-MIG
 */
public class AdvFolderScanTask extends ScanTask.BaseStub implements IScanFilter {
    public static final String TAG = AdvFolderScanTask.class.getSimpleName();

    public static final int ADV_FOLDER_SCAN_CFG_MASK_CALC_SIZE		= 0x00000001;	///< 是否计算size
    //ADV_FOLDER_SCAN_CFG_MASK_NOT_RETURN_IGNORE 默认配置是不在ADD_CHILDREN_DATA_ITEM_TO_ADAPTER的消息回调中返回ignore item
    //如果将该配置取非，那么白名单中的项也会被作为结果返回，可以通过JunkInfoBase的isIgnore()查询是否是ignore item
    public static final int ADV_FOLDER_SCAN_CFG_MASK_NOT_RETURN_IGNORE		= 0x00000002;
    public static final int ADV_FOLDER_SCAN_CFG_MASK_CALC_CHECKED_SIZE		= 0x00000004;	///< 是否计算选中项size，若ADV_FOLDER_SCAN_CFG_MASK_CALC_SIZE开关处于关闭状态，本开关无效。
    public static final int ADV_FOLDER_SCAN_CFG_MASK_CALC_UNCHECKED_SIZE		= 0x00000008;	///< 是否计算未选中项size，若ADV_FOLDER_SCAN_CFG_MASK_CALC_SIZE开关处于关闭状态，本开关无效。
    public static final int ADV_FOLDER_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS	= 0x00000010;   //  是否 检查扫描结果 用户设置的锁定状态    默认 不检查

    private int mWhiteListMapSize=0;
    private HashMap<String, ProcessModel> mWhiteListMap = new HashMap<>();

    private Queue<TargetFolder> mTagetFolers = null;

    private RubbishFileScanTask mFilterAgent = null;
    private MD5PathConvert md5PathConvert = MD5PathConvert.getInstance();
    private int mScanCfgMask = -1;
    //key:advFolder_describeinfo表中的id字段  value:advFolder_describeinfo表中的value字段
    private HashMap<String, LocalStringDbUtil.LangStr> mLocalStringMap = null;

    private Strings2CacheProvider mString2CacheProvider;

    @Override
    public boolean isFilter(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        //白名单过滤
        if(mWhiteListMapSize > 0 && null != mWhiteListMap.get(name)) {
            return true;
        }
        return false;
    }


    private class TargetFolder {
        public String folderName = null;
        public String describe = null;
        public int srsid = 0;
        public int signId = 0;

        public TargetFolder(String name, String des, int id, int sId) {
            folderName = name;
            describe = des;
            srsid = id;
            signId = sId;
        }

        @Override
        public String toString() {
            return "TargetFolder{" +
                    "folderName='" + folderName + '\'' +
                    ", describe='" + describe + '\'' +
                    ", srsid=" + srsid +
                    ", signId=" + signId +
                    '}';
        }
    }

    public AdvFolderScanTask(RubbishFileScanTask filterAgent) {
        mFilterAgent = filterAgent;
        mString2CacheProvider = Strings2CacheProvider.getInstance();
    }
    public AdvFolderScanTask() {

    }
    @Override
    public boolean scan(ScanTaskController ctrl) {
        return doScan(ctrl);
    }



    public boolean doScan(ScanTaskController ctrl) {
        boolean rc = initScanFolderList(ctrl);
        if (!rc) {
            return rc;
        }

        if (null != ctrl && ctrl.checkStop()) {
            return true;
        }

        boolean isChecklocked = false;
        /*JunkLockedDaoImp junkLockedDao = null ;

        if(0 == (ADV_FOLDER_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS & mScanCfgMask))
        {
            isChecklocked = true;
            junkLockedDao  = DaoFactory.getJunkLockedDao(BaseApplication.getInstance());
        }*/

        if (null != mCB) {
            mCB.callbackMessage(RubbishFileScanTask.RUB_FILE_SCAN_PROGRESS_START,
                    SDcardRubbishResult.RF_ADV_FOLDERS, 0, null);
        }

        //load whitelist to memory
        loadAllWhiteList();

        if (null != mLocalStringMap) {
            mLocalStringMap.clear();
            mLocalStringMap = null;
        }
        long startTime = System.currentTimeMillis();
        //load resource string to memory
        mLocalStringMap = LocalStringDbUtil.getInstance().getAllLocalStringFromDB(mString2CacheProvider);
        NLog.d(TAG, "AdvFolderScanTask doScan load string res cost time = "+ (System.currentTimeMillis() - startTime));
        List<SDcardRubbishResult> targetList = new ArrayList<>();
        //测试先注释掉这个
        //scanVungleAdvsFolders(targetList, ctrl/*, junkLockedDao*/);

        if (null != ctrl && ctrl.checkStop()) {
            return true;
        }

        if (null == mTagetFolers || mTagetFolers.isEmpty()) {
            if(mCB != null){
                mCB.callbackMessage(RubbishFileScanTask.RUB_FILE_SCAN_ADV_FINISHED,
                        SDcardRubbishResult.RF_ADV_FOLDERS, 0, null);
            }
            return true;
        }

        if (null != mCB) {
            mCB.callbackMessage(RubbishFileScanTask.RUB_FILE_SCAN_PROGRESS_STEP_NUM,
                    SDcardRubbishResult.RF_ADV_FOLDERS, mTagetFolers.size(), null);
        }

        File targetFolder = null;

        ArrayList<String> mExternalStoragePaths = null;
        mExternalStoragePaths = (new StorageList())
                .getMountedVolumePaths();
        if (mExternalStoragePaths == null || mExternalStoragePaths.isEmpty()) {
            mExternalStoragePaths = new ArrayList<>();
            mExternalStoragePaths.add(Environment.getExternalStorageDirectory().getPath());
        }

        String firstSdcardRootDir = Environment.getExternalStorageDirectory().getPath();
        NLog.d(TAG, "AdvFolderScanTask doScan firstSdcardRootDir = "+firstSdcardRootDir);
        for (TargetFolder target = mTagetFolers.poll(); null != target; target = mTagetFolers.poll()) {
            for(String rootPath: mExternalStoragePaths ) {
                if (null != ctrl && ctrl.checkStop()) {
                    break;
                }
                if (null == target.folderName) {
                    if (null != mCB) {
                        mCB.callbackMessage(RubbishFileScanTask.RUB_FILE_SCAN_PROGRESS_ADD_STEP,
                                SDcardRubbishResult.RF_ADV_FOLDERS, 0, null);
                    }
                    continue;
                }
                targetFolder = new File(rootPath + target.folderName);
                if ( !targetFolder.exists() ) {
                    continue;
                }
                if (null != mCB) {
                    mCB.callbackMessage(RubbishFileScanTask.SCAN_SDCARD_INFO, 0, 0, target.folderName);
                }
                boolean isIgnore = false;
                //过滤
                if (isFilter(targetFolder.getPath())) {
                    isIgnore = true;
                    if ((mScanCfgMask & ADV_FOLDER_SCAN_CFG_MASK_NOT_RETURN_IGNORE) != 0) {
                        if (null != mCB) {
                            mCB.callbackMessage(RubbishFileScanTask.RUB_FILE_SCAN_PROGRESS_ADD_STEP,
                                    SDcardRubbishResult.RF_ADV_FOLDERS, 0, null);
                        }
                        continue;
                    }
                }

                boolean checked = true;

                long fileCompute[] = new long[3];
                fileCompute[0] = 0;
                fileCompute[1] = 0;
                fileCompute[2] = 0;
                if (0 != (mScanCfgMask & ADV_FOLDER_SCAN_CFG_MASK_CALC_SIZE)) {
                    if ((0 != (mScanCfgMask & ADV_FOLDER_SCAN_CFG_MASK_CALC_CHECKED_SIZE) && checked) ||
                            (0 != (mScanCfgMask & ADV_FOLDER_SCAN_CFG_MASK_CALC_UNCHECKED_SIZE) && !checked)){
                        PathOperFunc.computeFileSize(targetFolder.getPath(), fileCompute, new ProgressCtrlImpl(ctrl));
                    }

                }

                if (null != ctrl && ctrl.checkStop()) {
                    break;
                }

                if (null != mLocalStringMap) {
                    LocalStringDbUtil.LangStr myLang = mLocalStringMap.get(Integer.valueOf(target.srsid).toString());
                    if (null != myLang) {
                        if (!TextUtils.isEmpty(myLang.primaryStr)) {
                            target.describe = myLang.primaryStr;
                        } else if (!TextUtils.isEmpty(myLang.secondaryStr)) {
                            target.describe = myLang.secondaryStr;
                        }
                    }
                }

                if (fileCompute[0] <= 0 && Build.VERSION.SDK_INT >= 19 && !(targetFolder.getAbsolutePath().startsWith(firstSdcardRootDir))) {
                    // 规辟4.4上第二张卡只能删除文件，删不掉文件夹的情况。这样的空文件夹就不要检出了。
                    continue;
                }
                SDcardRubbishResult info = new SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE.ADVERTISEMENT);
                info.setStrDirPath(targetFolder.getPath());
                //info.setFileType(FileType.Dir);
                info.setChineseName(target.describe);
                info.setSize(fileCompute[0]);
                info.setFoldersCount(fileCompute[1]);
                info.setFilesCount(fileCompute[2]);
                info.setApkName(target.describe);
                info.setCheck(checked);
                //signId是advFloder表中的_id字段的值
                info.setSignId(target.signId);
                info.setType(SDcardRubbishResult.RF_ADV_FOLDERS);
                if (isIgnore) {
                    info.setIgnore(true);
                }

                info.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
                if (!checked) {
                    info.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
                }
               /* if (isChecklocked) {
                    //判定是否锁定
                    if (junkLockedDao != null) {
                        info.setCheck(!junkLockedDao.checkLocked(target.signId, checked));
                    }
                }*/

                targetList.add(info);

                if (null != mCB) {
                    mCB.callbackMessage(
                            RubbishFileScanTask.ADD_CHILDREN_DATA_ITEM_TO_ADAPTER,
                            SDcardRubbishResult.RF_ADV_FOLDERS,
                            0,
                            info);
                    NLog.d(TAG, "找到广告文件: apk name = "+ info.getApkName() +", chinesName = "+info.getChineseName()
                            +", path = "+ info.getStrDirPath());
                    mCB.callbackMessage(RubbishFileScanTask.RUB_FILE_SCAN_PROGRESS_ADD_STEP,
                            SDcardRubbishResult.RF_ADV_FOLDERS, 0, null);
                }
            }
        }
        if(mCB != null){
            mCB.callbackMessage(RubbishFileScanTask.RUB_FILE_SCAN_ADV_FINISHED,
                    SDcardRubbishResult.RF_ADV_FOLDERS, 0, null);
        }
        return true;
    }





    private boolean initScanFolderList(ScanTaskController ctrl) {
        if (null == mTagetFolers) {
            mTagetFolers = new LinkedList<>();
        }
        mTagetFolers.clear();

        CleanPathCacheProvider provider = CleanPathCacheProvider.getInstance();
        List<AdvFolder> results = provider.findAllAdvCleanPath();

        if (results == null || results.size() == 0){
            return false;
        }
        NLog.d(TAG, "initScanFolderList results = "+ results.size());
        for (AdvFolder advFolder: results){
            //用数据库的path路径跟sd卡列表中的文件的md5值去碰撞 如果碰撞成功了 会返回明文的路径 否则返回null
            //advFolder.path = "eb03c8519dee26c39fdb2da5b817bf2f+cdc0602216cddaa58dfe1678d189ea52";
            String path = md5PathConvert.getFilePathByMd5(advFolder.path);

            if (path != null) {
                NLog.d(TAG, "initScanFolderList advFolder.path = "+ advFolder.path +", convert Path = "+ path);
                TargetFolder target = new TargetFolder(path,advFolder.describeinfo,advFolder.srsid,advFolder._id);
                mTagetFolers.offer(target);
            }else {
                //NLog.d(TAG, "initScanFolderList advFolder.path = "+ advFolder.path +", convert Path = null");
            }
        }
        NLog.d(TAG, "initScanFolderList mTagetFolers = "+mTagetFolers);
        //for test
        /*for (AdvFolder advFolder: results){
            //用数据库的path路径跟sd卡列表中的文件的md5值去碰撞 如果碰撞成功了 会返回明文的路径 否则返回null
            if (advFolder.describeinfo.toLowerCase().indexOf("tencent") > 0){
                String path = md5PathConvert.getFilePathByMd5(advFolder.path);
                if (path != null) {
                    TargetFolder target = new TargetFolder(path,advFolder.desc,advFolder.srsid,advFolder._id);
                    mTagetFolers.offer(target);
                }
            }
        }*/
        return true;
    }

    private void loadAllWhiteList() {
        mWhiteListMapSize = 0;
        mWhiteListMap.clear();
        List<ProcessModel> tmpWhiteList = null;
        //todo 用顺友数据库
        //tmpWhiteList = WhiteListsWrapper.getCacheWhiteList()
        if (null != tmpWhiteList) {
            for (ProcessModel tmpModel : tmpWhiteList) {
                if (null != tmpModel.getPkgName()) {
                    mWhiteListMap.put(tmpModel.getPkgName(), tmpModel);
                }
            }
            mWhiteListMapSize = mWhiteListMap.size();
        }

    }



    private void scanVungleAdvsFolders(
            List<SDcardRubbishResult> targetList,
            ScanTaskController ctrl/*,
            JunkLockedDaoImp junkLockedDao*/) {
        assert(null != targetList);
        assert(null != ctrl);
        File folder = new File(Environment.getExternalStorageDirectory(), "android/data");
        if (!folder.exists()) {
            return;
        }

        if (!folder.isDirectory()) {
            return;
        }

        if (null != mCB) {
            mCB.callbackMessage(RubbishFileScanTask.SCAN_SDCARD_INFO, 0, 0, "android/data");
        }

        PathOperFunc.StringList subFolders = EnableCacheListDir.listDir(folder.getPath(), new NameFilter() {

            @Override
            public boolean accept(String parent, String sub, boolean bFolder) {
                return (StringUtils.toLowerCase(sub).startsWith("com."));
            }
        });

        if (null == subFolders) {
            return;
        }

        try {
            if (0 == subFolders.size()) {
                return;
            }

            if (null != mCB) {
                mCB.callbackMessage(RubbishFileScanTask.RUB_FILE_SCAN_PROGRESS_STEP_NUM,
                        SDcardRubbishResult.RF_ADV_FOLDERS, subFolders.size() * 4, null);
            }

            if (null != ctrl && ctrl.checkStop()) {
                return;
            }

            String describe = "Vungle\'s ads";
            if (null != mLocalStringMap) {
                LocalStringDbUtil.LangStr myLang = mLocalStringMap.get("13");
                if (null != myLang) {
                    if (!TextUtils.isEmpty(myLang.primaryStr)) {
                        describe = myLang.primaryStr;
                    } else if (!TextUtils.isEmpty(myLang.secondaryStr)) {
                        describe = myLang.secondaryStr;
                    }
                }
            }

            boolean isLocked = false;

            /*if (null != junkLockedDao) {
                //TODO 用顺友提供的数据库接口
                //isLocked = junkLockedDao.checkLocked(13, true);
            }*/

            File subFolderItem = null;
            for (String sub : subFolders) {
                if (null != ctrl && ctrl.checkStop()) {
                    break;
                }

                if (null != mCB) {
                    mCB.callbackMessage(RubbishFileScanTask.SCAN_SDCARD_INFO, 0, 0, sub);
                    mCB.callbackMessage(RubbishFileScanTask.RUB_FILE_SCAN_PROGRESS_ADD_STEP,
                            SDcardRubbishResult.RF_ADV_FOLDERS, 0, null);
                }

                subFolderItem = new File(folder, sub + "/.vunglecachedir");
                if (!subFolderItem.exists()) {
                    continue;
                }

                if (!subFolderItem.isDirectory()) {
                    continue;
                }

                boolean isIgnore = false;
                //过滤
                if(isFilter(subFolderItem.getPath())){
                    isIgnore = true;
                    if((mScanCfgMask & ADV_FOLDER_SCAN_CFG_MASK_NOT_RETURN_IGNORE) != 0){
                        if (null != mCB) {
                            mCB.callbackMessage(RubbishFileScanTask.RUB_FILE_SCAN_PROGRESS_ADD_STEP,
                                    SDcardRubbishResult.RF_ADV_FOLDERS, 0, null);
                        }
                        continue;
                    }
                }

                boolean checked = true;

                long fileCompute[] = new long[3];
                fileCompute[0] = 0;
                fileCompute[1] = 0;
                fileCompute[2] = 0;
                if (0 != (mScanCfgMask & ADV_FOLDER_SCAN_CFG_MASK_CALC_SIZE)) {
                    if ((0 != (mScanCfgMask & ADV_FOLDER_SCAN_CFG_MASK_CALC_CHECKED_SIZE) && checked) ||
                            (0 != (mScanCfgMask & ADV_FOLDER_SCAN_CFG_MASK_CALC_UNCHECKED_SIZE) && !checked))
                        PathOperFunc.computeFileSize(subFolderItem.getPath(), fileCompute, new ProgressCtrlImpl(ctrl));
                }

                if (null != ctrl && ctrl.checkStop()) {
                    break;
                }

                SDcardRubbishResult info = new SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE.ADVERTISEMENT);
                info.setStrDirPath(subFolderItem.getPath());
                info.setChineseName(describe);
                info.setSize(fileCompute[0]);
                info.setFoldersCount(fileCompute[1]);
                info.setFilesCount(fileCompute[2]);
                info.setApkName(describe);
                info.setCheck(checked);
                info.setSignId(13);
                info.setType(SDcardRubbishResult.RF_ADV_FOLDERS);
                if(isIgnore){
                    info.setIgnore(true);
                }
                info.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
                if (!checked) {
                    info.setScanType(BaseJunkBean.SCAN_TYPE_ADVANCED);
                }

                //判定是否锁定
              /*  if(junkLockedDao!=null){
                    info.setCheck(!isLocked);
                }*/

                targetList.add(info);

                if (null != mCB) {
                    mCB.callbackMessage(
                            RubbishFileScanTask.ADD_CHILDREN_DATA_ITEM_TO_ADAPTER,
                            SDcardRubbishResult.RF_ADV_FOLDERS,
                            0,
                            info);
                }
            }
        } finally {
            subFolders.release();
        }
    }

    private class ProgressCtrlImpl implements ProgressCtrl {
        private ScanTaskController mCtrl = null;

        public ProgressCtrlImpl(ScanTaskController ctrl) {
            mCtrl = ctrl;
        }

        @Override
        public boolean isStop() {
            if (null != mCtrl) {
                return mCtrl.checkStop();
            }

            return false;
        }
    }


    public void setScanConfigMask(int mask) {
        mScanCfgMask = mask;
    }

    public int getScanConfigMask() {
        return mScanCfgMask;
    }


    @Override
    public String getTaskDesc() {
        return TAG;
    }

}

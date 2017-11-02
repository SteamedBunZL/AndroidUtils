package com.clean.spaceplus.cleansdk.junk.engine.task;

import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.base.utils.root.SuExec;
import com.clean.spaceplus.cleansdk.base.utils.system.SystemCacheManager;
import com.clean.spaceplus.cleansdk.junk.engine.DelCallback;
import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.util.Commons;
import com.clean.spaceplus.cleansdk.util.Env;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.clean.spaceplus.cleansdk.util.PackageUtils;
import com.clean.spaceplus.cleansdk.util.StringUtils;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/10 19:49
 * @copyright TCL-MIG
 */
public class SysCacheCleanTask extends ScanTask.BaseStub{
    public static final String TAG = SysCacheCleanTask.class.getSimpleName();
    public static final int CLEAN_FINISH			= 1;
    public static final int CLEAN_ITEM 				= 2;	///< arg1取值定义见CLEAN_ITEM_ARG1_*
    public static final int CLEAN_INFO   			= 4;

    public static final int CLEAN_ITEM_ARG1_CLEAN_SD_CARD_FOLDER = 1;		///< 表示只清理了sdcard上的cache
    public static final int CLEAN_ITEM_ARG1_CLEAN_ALL_FOLDER = 2;			///< 表示系统存储和sd卡上的都清理了

    public static final int CTRL_MASK_CLEAN_ALL_WITHOUT_ROOT_PRIVACY	= 0x00000001;	///< 是否清除所有系统缓存。不需要root权限
    private static String defaultSdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private String mTaskName = null;
    private Context mCtx;
    private File myDir;
    private List<String> mFolderWhiteList = new ArrayList<String>();
    private List<String> mFileWhiteList = new ArrayList<String> ();

    public SysCacheCleanTask() {
        mTaskName = "SysCacheCleanTask";
    }

    public SysCacheCleanTask(String taskName) {
        mTaskName = taskName;
    }

    public static interface ICleanDataSrc {
        public String getNextPackageName();
        public CacheInfo getNextCacheInfo();
    }

    public void bindCleanDataSrc(ICleanDataSrc srcCallback) {
        mDataMgr = srcCallback;
    }

    public int getCtrlMask() {
        return mCtrlMask;
    }

    public void setCtrlMask(int ctrlMask) {
        mCtrlMask = ctrlMask;
        NLog.i(TAG, "set ctrl mask : "+mCtrlMask);
    }

    public void setPkgManager(PackageManager pm) {
        mPM = pm;
    }

    public void setWhiteList (List<String> fileWhiteList, List<String> folderWhiteList) {
        mFileWhiteList = fileWhiteList;
        mFolderWhiteList = folderWhiteList;
    }

    @Override
    public boolean scan(ScanTaskController ctrl) {
        NLog.d(TAG, "sys_cache_clean");
        mCtx = SpaceApplication.getInstance().getContext();
        try {
            myDir = new File(Env.getExternalStorageDirectoryx());
        } catch (Exception e) {
            myDir = null;
            NLog.printStackTrace(e);
        }
        boolean isCleanSysCache = false;

        try {
            if (0 != (mCtrlMask & CTRL_MASK_CLEAN_ALL_WITHOUT_ROOT_PRIVACY)) {
                NLog.i(TAG, "CLEAN_ALL_WITHOUT_ROOT_PRIVACY");
                NLog.d(TAG, "只清理SD卡上的系统缓存");

                //只清除sd卡上的
                if (null == mPM) {
                    return true;
                }

                if (null != mDataMgr) {
                    CacheInfo tmpCacheInfo = null;

                    while (true) {
                        tmpCacheInfo = mDataMgr.getNextCacheInfo();

                        if (null == tmpCacheInfo) {
                            break;
                        }

                        if (null != ctrl && ctrl.checkStop()) {
                            break;
                        }

                        if (null != mCB) {
                            mCB.callbackMessage(CLEAN_ITEM, CLEAN_ITEM_ARG1_CLEAN_SD_CARD_FOLDER, 0, tmpCacheInfo);
                        }

                        SysCacheScanTask.SysCacheOnCardInfo tmpSysCacheOnCardInfo = tmpCacheInfo.getSysCacheOnCardInfo();

                        if (null == tmpSysCacheOnCardInfo) {
                            continue;
                        }

                        if (null != tmpSysCacheOnCardInfo.strAbsPathList) {
                            for (String path : tmpSysCacheOnCardInfo.strAbsPathList) {
                                removePackageCacheInAndroidData(path, tmpSysCacheOnCardInfo.strPackageName);
                                NLog.d(TAG, "开始清理sd卡上的系统缓存: pkgName = %s, filePath = %s, total size = %s",tmpSysCacheOnCardInfo.strPackageName,path, tmpSysCacheOnCardInfo.nTotalSize);

                            }
                        }

                        if (null != mCB) {
                            mCB.callbackMessage(CLEAN_INFO, 0, 0,  tmpCacheInfo);
                        }
                    }
                }

                // Move recorder syscache size 到所有的清理任务结束
                isCleanSysCache = true;

//                {
//                    // 先重命名sd卡的cache，防止被删掉
//                    String packageName = null;
//                    List<PackageInfo> pkgs = PackageManagerWrapper.getInstance().getPkgInfoList();
//                    if ( pkgs != null && pkgs.size() != 0 ){
//                        for ( PackageInfo pkgInfo : pkgs){
//                            beforeCleanPkgSamsung(pkgInfo.packageName);
//                        }
//                    }
//                }

                try {
                    Method deleteCachefile = mPM.getClass().getMethod(
                            "freeStorageAndNotify", Long.TYPE,
                            IPackageDataObserver.class);
                    Long localLong = Long.valueOf(getEnvironmentSize() - 1L);
                    deleteCachefile.invoke(mPM, localLong, new IPackageDataObserver.Stub() {

                        @Override
                        public void onRemoveCompleted(String packageName,
                                                      boolean succeeded) throws RemoteException {
//                            afterCleanAllPkgCache();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                NLog.i(TAG, "CLEAN_WITH_ROOT_PRIVACY");
                //清除系统存储和sd卡上的
                if (null == mDataMgr) {
                    return true;
                }

                CacheInfo tmpCacheInfo = null;
                while (true) {
                    tmpCacheInfo = mDataMgr.getNextCacheInfo();

                    if (null == tmpCacheInfo) {
                        break;
                    }

                    if (null != ctrl && ctrl.checkStop()) {
                        break;
                    }

                    if (null != mCB) {
                        NLog.i(TAG, tmpCacheInfo.getAppName() + "----: " + tmpCacheInfo.mPkgName);
                        mCB.callbackMessage(CLEAN_ITEM, CLEAN_ITEM_ARG1_CLEAN_ALL_FOLDER, 0, tmpCacheInfo);
                    }

                    //SysCacheScanTask.SysCacheOnCardInfo tmpSysCacheOnCardInfo = tmpCacheInfo.getSysCacheOnCardInfo();
                    String packageName = tmpCacheInfo.mPkgName;
                    NLog.i(TAG,"<---> packageName %s getPackageName %s", packageName, tmpCacheInfo.getPackageName());
                    if (!TextUtils.isEmpty(packageName)) {
                        PackageInfo pkgInfo = PackageUtils.getPackageInfo(SpaceApplication.getInstance().getContext().getApplicationContext(),
                                packageName);
                        tmpCacheInfo.setPackageInfo(pkgInfo);
                        List<String> strPathList = SystemCacheManager.newInstance().getAndroidDataPath(tmpCacheInfo.mPkgName);
                        for (String path : strPathList) {
                            NLog.i(TAG, " 开始清理android/data系统缓存 path %s", path);
                            SuExec.removeJunkFiles(path);
                        }
                        StringBuilder sb = new StringBuilder("data/data/").append(packageName).append("/cache/");
                        //FileUtils.directDeleteSubFoldersAndFiles(new File(sb.toString()), null);
                        NLog.d(TAG, "开始清理data/data系统缓存: pkgName = %s, filePath = %s", packageName, sb.toString());
                        SuExec.removeJunkFiles(sb.toString());
                    }
//                    final boolean bBackup;
                    /*if (null != tmpSysCacheOnCardInfo) {
                        if (null != tmpSysCacheOnCardInfo.strAbsPathList) {
                            for (String path : tmpSysCacheOnCardInfo.strAbsPathList) {
                                //removePackageCacheInAndroidData(path, tmpSysCacheOnCardInfo.strPackageName);
                                //NLog.i(TAG," remove android data path %s", path);
                               //NLog.d(TAG, "开始清理sd卡Android/data系统缓存: pkgName = %s, filePath = %s, size = %s, isChecked = %b ",tmpSysCacheOnCardInfo.strPackageName, path,tmpSysCacheOnCardInfo.nTotalSize, tmpCacheInfo.isCheck() );
                               // SuExec.removeJunkFiles(path);
                            }
                        }
//                        bBackup = false;
                    }*/
//                    else {
//                        bBackup = true;
//                        beforeCleanPkgSamsung(packageName);
//                    }

//                    if(bBackup){
//                        afterCleanPkgCache(packageName);
//                    }

//                    SuExec.getInstance().deleteApplicationCacheFiles(packageName,
//                            new IDelCacheObserver(){
//
//                                @Override
//                                public IBinder asBinder() {
//                                    // TODO Auto-generated method stub
//                                    return null;
//                                }
//
//                                @Override
//                                public void onRemoveCompleted(List<String> pathList)
//                                        throws RemoteException {
//                                    if(bBackup.booleanValue() == true ){
//                                        afterCleanPkgCache(packageName);
//                                    }
//                                }
//
//                            });

                    if (null != mCB) {
                        mCB.callbackMessage(CLEAN_INFO, 0, 0,  tmpCacheInfo);
                    }
                }
            }
        } finally {
            if (null != mCB) {
                mCB.callbackMessage(CLEAN_FINISH, 0, 0, isCleanSysCache);
            }
        }

        return true;
    }

    private void removePackageCacheInAndroidData(String strPkgCachePath, String pkgName) {
        if (TextUtils.isEmpty(strPkgCachePath)) {
            return;
        }

        List<String> pkgFolder = new ArrayList<String>();
        pkgFolder.add(strPkgCachePath);

//		Commons.directDeleteSubFoldersAndFiles(pkgFolder, null);
        DelCallbackImpl delCallback = new DelCallbackImpl();
        delCallback.setEnableFlags(DelCallback.DISABLE_WRITE_LOG, true);
        delCallback.setEnableFlags(DelCallback.ENABLE_AFTER_DELETE, false);
        delCallback.setFolderWhiteList(mFolderWhiteList);
        delCallback.setFileWhiteList(mFileWhiteList);

        delCallback.setDelFlags(DelCallback.DELETE_ONLY_FILE,true);

        boolean success = FileUtils.deleteFileOrFolder(pkgFolder, delCallback );
        if (!success) {
            File file = new File(strPkgCachePath);
            FileUtils.directDeleteSubFoldersAndFiles(file, null);
        }

    }


    private class DelCallbackImpl implements DelCallback {
        private int mFolderCount = 0;
        private int mFileCount = 0;
        private int mEnableFlags = 0;
        private int mFileTimeLimit = 0;
        private int mDelFlags = 0;

        private List<String> mFolderWhiteList = new ArrayList<String> ();
        private List<String> mFileWhiteList = new ArrayList<String> ();
        private List<String> mOnCleanFeedbackListFolder = new ArrayList<String> ();
        private List<String> mOnCleanFeedbackListFile = new ArrayList<String> ();

        public void setEnableFlags(int enableType, boolean enableValue) {
            if (enableValue) {
                mEnableFlags |= (enableType);
            }
            else {
                mEnableFlags &= ~(enableType);
            }
        }
        @Override
        public int getEnableFlags() {
            return mEnableFlags;
        }

        public void setDelFlags(int enableType, boolean enableValue) {
            if (enableValue) {
                mDelFlags |= (enableType);
            }
            else {
                mDelFlags &= ~(enableType);
            }
        }
        @Override
        public int getDelFlags() {
            return mDelFlags;
        }

        @Override
        public int getDelFileTimeLimit() {
            return mFileTimeLimit;
        }

        public void setFileWhiteList (List<String> whiteList) {
            mFileWhiteList.addAll(whiteList);
        }
        @Override
        public List<String> getFileWhiteList() {
            return mFileWhiteList;
        }

        public void setFolderWhiteList (List<String> whiteList) {
            mFolderWhiteList.addAll(whiteList);
        }
        @Override
        public List<String> getFolderWhiteList() {
            return mFolderWhiteList;
        }

        @Override
        public List<String> getFeedbackFolderList() {
            return mOnCleanFeedbackListFolder;
        }

        @Override
        public List<String> getFeedbackFileList() {
            return mOnCleanFeedbackListFile;
        }

        @Override
        public void onDeleteFile(String strFileName, long type) {
            if (type != 0L) {
                File file = new File(strFileName);
                String dir = file.getParent().replace(defaultSdCardPath, "");
                String fileName;
                if (Commons.isCNVersion()) {
                    fileName = file.getName();
                } else {
                    int idx = file.getName().lastIndexOf(".");
                    if (idx != -1 ) {
                        fileName = file.getName().substring(idx);
                    } else {
                        fileName = "";
                    }
                }
                String info = new StringBuilder("deletedetail=").append(dir)
                        .append("&name=").append(fileName)
                        .append("&t=").append(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE.ordinal())
                        .append("&sign=").append(Integer.toString(0))
                        .toString();
                NLog.d("DeletePhoto",info);
            }
        }

        @Override
        public void afterDel(int folderCount, int fileCount, int imageCount, int videoCount, int audioCount) {
            mFolderCount = folderCount;
            mFileCount = fileCount;
        }

        @Override
        public void onFeedbackFile(String strFilePath, String strFileName, long size) {

        }
        @Override
        public void onError(String strPath, boolean bRmDir, boolean bRoot,
                            int nErrorCode) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onFilter(String filePath, long fileModifyTime) {
            return true;
        }
    }



    private boolean checkNeedbackUp(){
        return (Build.VERSION.SDK_INT >= 17) ||
                (0 == (mCtrlMask & CTRL_MASK_CLEAN_ALL_WITHOUT_ROOT_PRIVACY));
    }

    private ArrayList<String> mPkgListBackup = new ArrayList<String>();
    // 先把 /sdcard/Android/data/xxxxx/cache目录重新命名一下
//    private void beforeCleanPkgSamsung(String pkgName){
//        if (!checkNeedbackUp())
//            return ;
//
//
////		TODO(YJ) 这里的备份操作，如果在4.4以上系统，需要覆盖到所有外插卡。 而且此处对于内置模拟卡中多用户的情况处理可能会有权限问题。
//        if ( myDir == null )
//            return;
//        File pkgSdFile = new File( myDir.getParent().replace(mCtx.getPackageName(), pkgName) );
//
//        File pkgCacheOld 	= new File(pkgSdFile, "cache");
//        File pkgCacheBackUp = new File(pkgSdFile, ".cache_sp_backup");
//
//        if (pkgCacheOld.exists() && pkgCacheOld.isDirectory() ){
//            if ( pkgCacheBackUp.exists() ){
//                FileUtils.deleteAllFile(pkgCacheBackUp);
//            }
//            if ( pkgCacheOld.renameTo(pkgCacheBackUp) ){
//                mPkgListBackup.add(pkgName);
//                NLog.d("beforeCleanPkgSamsung", pkgName + ": 1");
//            }
//            else{
//                NLog.d("beforeCleanPkgSamsung", pkgName + ": 0");
//            }
//        }
//    }

//    private void afterCleanAllPkgCache(){
//        if (!checkNeedbackUp())
//            return;
//        if ( myDir == null )
//            return;
//        for ( String pkgName : mPkgListBackup ){
//            File pkgSdFile = new File( myDir.getParent().replace(mCtx.getPackageName(), pkgName) );
//
//            File pkgCacheOld 	= new File(pkgSdFile, "cache");
//            File pkgCacheBackUp = new File(pkgSdFile, ".cache_sp_backup");
//
//            if (pkgCacheBackUp.exists() && pkgCacheBackUp.isDirectory() ){
//                if ( pkgCacheOld.exists() ){
//                    FileUtils.deleteAllFile(pkgCacheOld);
//                }
//                if ( pkgCacheBackUp.renameTo(pkgCacheOld) ){
//                    NLog.d("afterCleanAllPkgCache", pkgName + ": 1");
//                }
//                else{
//                    NLog.d("afterCleanAllPkgCache", pkgName + ": 0");
//                }
//            }
//        }
//    }

//    private void afterCleanPkgCache(String pkgName){
//        if (!checkNeedbackUp())
//            return;
//        if (myDir == null)
//            return;
//        File pkgSdFile = new File(myDir.getParent().replace(mCtx.getPackageName(), pkgName));
//
//        File pkgCacheOld = new File(pkgSdFile, "cache");
//        File pkgCacheBackUp = new File(pkgSdFile, ".cache_sp_backup");
//
//        if (pkgCacheBackUp.exists() && pkgCacheBackUp.isDirectory()) {
//            if (pkgCacheOld.exists()) {
//                FileUtils.deleteAllFile(pkgCacheOld);
//            }
//            if (pkgCacheBackUp.renameTo(pkgCacheOld)) {
//                NLog.d("afterCleanAllPkgCache", pkgName + ": 1");
//            } else {
//                NLog.d("afterCleanAllPkgCache", pkgName + ": 0");
//            }
//        }
//    }

    private static long getEnvironmentSize() {
        File localFile = Environment.getDataDirectory();
        long l1;
        if (localFile == null) {
            l1 = 0L;
        }
        while (true) {
            String str = localFile.getPath();
            StatFs localStatFs = new StatFs(str);
            long l2 = localStatFs.getBlockSize();
            l1 = localStatFs.getBlockCount() * l2;
            return l1;
        }
    }

    private PackageManager mPM = null;
    private int mCtrlMask = -1;
    private ICleanDataSrc mDataMgr = null;
    @Override
    public String getTaskDesc() {
        return StringUtils.toLowerCase(mTaskName) + "CleanTask";
    }
}

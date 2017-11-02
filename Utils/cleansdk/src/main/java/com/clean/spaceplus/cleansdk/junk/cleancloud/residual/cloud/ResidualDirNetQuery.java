package com.clean.spaceplus.cleansdk.junk.cleancloud.residual.cloud;

import android.os.Environment;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.base.strategy.BaseStrategy;
import com.clean.spaceplus.cleansdk.base.strategy.NetStrategy;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudManager;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CloudCfgDataWrapper;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.CloudCfgKey;
import com.clean.spaceplus.cleansdk.junk.engine.bean.SDcardRubbishResult;
import com.clean.spaceplus.cleansdk.util.CleanCloudScanHelper;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.clean.spaceplus.cleansdk.util.SDCardUtil;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import space.network.cleancloud.KResidualCloudQuery;

/**
 * @author Jerry
 * @Description:
 * @date 2016/7/1 17:19
 * @copyright TCL-MIG
 */
public class ResidualDirNetQuery {
    public static final String TAG = ResidualDirNetQuery.class.getSimpleName();
    List<CloudQueryer> mCloudQueryers = null;
    private int mCleanCloudScanType = 0;
    boolean needNetQuery = true;
    BaseStrategy bs;
    public void getRubbishOnDoubleSDcard(final ScanTaskController ctrl,BaseStrategy bs) {
        //Log.i(TAG, "Residual Scan begin");
        this.bs = bs;
        File firstSdcardRootDir = Environment.getExternalStorageDirectory();
        File secondSdcardRootDir = SDCardUtil.getMountedThe2ndSdCardRootDir();

        if ( firstSdcardRootDir == null && secondSdcardRootDir == null ) {
            return;
        }
        mCloudQueryers = new ArrayList<>();
        mCloudQueryers.add( new CloudQueryer(firstSdcardRootDir.getAbsolutePath(), ctrl, true) );
        if ( secondSdcardRootDir != null ) {
            mCloudQueryers.add( new CloudQueryer(secondSdcardRootDir.getAbsolutePath(), ctrl, false) );
        }



        ///////////////////////////////////////////////////////
        //残留云也是使用升级逻辑进行库释放，所以统一等待文件释放完成
        FileUtils.controlWait();
        String lang = CleanCloudScanHelper.getCurrentLanguage();
        int result = KResidualCloudQuery.DirScanType.DIR_INVAILD_SCAN;
        result = KResidualCloudQuery.DirScanType.DIR_STANDARD_SCAN;
        mCleanCloudScanType = result;
        for ( CloudQueryer cloudQueryer : mCloudQueryers ) {
            File[] arrayOfFile = SDCardUtil.GetAllFolderOnSdcard(cloudQueryer.mSdcardPath);
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
                //scanAndroidData(cloudQueryer.mSdcardPath, ctrl);
            }
        }
    }



    class CloudQueryer{
        public boolean mbScanDefaultSdCard = true;
        public String mSdcardPath;
        public KResidualCloudQuery mResidualCloudQuery = null;
        public MyResidualCloudQueryCallback mResidualCloudQueryCallback = null;
        public TreeMap<String, KResidualCloudQuery.DirQueryData> mResidualCloudResult = new TreeMap<>(new StringNoCaseComparator());
        public final List<SDcardRubbishResult> mRubbishResult2Report = Collections.synchronizedList(new LinkedList<SDcardRubbishResult>());

        public ScanTaskController mCtrl;
        public CloudQueryer( String sdcardPath, final ScanTaskController ctrl, boolean scanDefaultSdCard) {
            mResidualCloudQuery = CleanCloudManager.createResidualCloudQuery(needNetQuery);
            mResidualCloudQueryCallback =  new MyResidualCloudQueryCallback(ctrl, sdcardPath, scanDefaultSdCard, this );
            mbScanDefaultSdCard = scanDefaultSdCard;
            mSdcardPath = sdcardPath;
            mCtrl = ctrl;
            String lang = CleanCloudScanHelper.getCurrentLanguage();
            mResidualCloudQuery.initialize();

            //mResidualCloudQuery.setPackageChecker(mPackageCheckerForCloudQuery);
            mResidualCloudQuery.setLanguage(lang);
            mResidualCloudQuery.setSdCardRootPath(sdcardPath);
        }

        public void waitForComplete(final ScanTaskController ctrl){

        }
    }


    private class MyResidualCloudQueryCallback implements KResidualCloudQuery.DirQueryCallback {
        private final ScanTaskController mCtrl;
        private final String mSdcardPath;
        private final boolean mScanDefaultSdCard;
        private CloudQueryer mCloudQueryer;
        public MyResidualCloudQueryCallback(final ScanTaskController ctrl, String sdcardPath, boolean scanDefaultSdCard, CloudQueryer cloudQueryer ) {
            mCtrl = ctrl;
            mSdcardPath = sdcardPath;
            mScanDefaultSdCard = scanDefaultSdCard;
            mCloudQueryer = cloudQueryer;
        }
        @Override
        public void onGetQueryDirs(int queryId, final Collection<String> dirs) {
            NLog.d(TAG, "ResidualDirNetQuery MyResidualCloudQueryCallback onGetQueryDirs needNetQuery = " +needNetQuery +", dirs= "+dirs);
            if (!needNetQuery){
                NLog.d(TAG, "ResidualDirNetQuery MyResidualCloudQueryCallback onGetQueryDirs 正常模式 需要回调 ");

            }else {
                NLog.d(TAG, "ResidualDirNetQuery MyResidualCloudQueryCallback onGetQueryDirs 非正常模式,不需要回调 ");
            }
        }
        @Override
        public void onGetQueryId(int queryId) {
        }

        @Override
        public void onGetQueryResult(int queryId, Collection<KResidualCloudQuery.DirQueryData> results, boolean queryComplete) {
            if (results != null){
                NLog.d(TAG, "ResidualDirNetQuery onGetQueryResult size = "+results.size() +", queryComplete = "+queryComplete);
            }
            NLog.d(TAG, "ResidualDirNetQuery MyResidualCloudQueryCallback onGetQueryResult needNetQuery = %b, queryComplete = %b", needNetQuery, queryComplete);
            if (queryComplete){
                if (bs != null){
                    bs.setState(NetStrategy.StateValue.FINISH);
                }
            }
            if (!needNetQuery){
                NLog.d(TAG, "ResidualDirNetQuery MyResidualCloudQueryCallback onGetQueryDirs 正常模式 需要回调 ");
            }else {
                NLog.d(TAG, "RubbishFileScanTask MyResidualCloudQueryCallback onGetQueryResult 非正常模式,不需要回调 ");
            }

        }

        @Override
        public boolean checkStop() {
            return (mCtrl != null ? mCtrl.checkStop() : false);
        }
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

    private static class StringNoCaseComparator implements Comparator<String> {
        @Override
        public int compare(String left, String right) {
            return left.compareToIgnoreCase(right);
        }
    }
}

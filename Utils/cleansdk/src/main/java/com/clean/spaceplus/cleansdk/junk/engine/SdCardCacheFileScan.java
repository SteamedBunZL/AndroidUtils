package com.clean.spaceplus.cleansdk.junk.engine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.SystemClock;

import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudManager;

import java.util.HashMap;
import java.util.LinkedList;

import space.network.cleancloud.KCacheCloudQuery;
import space.network.util.compress.Base64;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/25 10:59
 * @copyright TCL-MIG
 */
public class SdCardCacheFileScan {
//    private int mDBColumnFilterST = Integer.valueOf(DBColumnFilterManager.EXPAND_FILTER_ID_CACHE_FILE_LEVEL0);
//    private int mDBColumnFilterSP = Integer.valueOf(DBColumnFilterManager.EXPAND_FILTER_ID_CACHE_FILE_LEVEL11);
//    private boolean[] mFileLevelScanPkgFilter = new boolean[mDBColumnFilterSP - mDBColumnFilterST + 1];
    private String defaultSdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private Context mCtx;
    boolean mFirstScanFlag = false;
    long mFileLevelScanTime = 0;
    int mFileLevelScanNum = 0;

    private static String[] mFileLevelScanPkg = {
            new String(Base64.decode("Y29tLmF1dG9uYXZpLnhtZ2QubmF2aWdhdG9y")), //com.autonavi.xmgd.navigator
            new String(Base64.decode("Y29tLmF1dG9uYXZpLnhtZ2QubmF2aWdhdG9yLmtleWJvYXJk")), //com.autonavi.xmgd.navigator.keyboard
            new String(Base64.decode("Y29tLnRlbmNlbnQubW0=")), //com.tencent.mm
            new String(Base64.decode("Y29tLm1pdWkudXNlcmJvb2s=")), //com.miui.userbook
            new String(Base64.decode("Y29tLmFuZHJvaWQudXBkYXRlcg==")), //com.android.updater
            new String(Base64.decode("Y29tLmt1Z291LmFuZHJvaWQ=")),//com.kugou.android
            new String(Base64.decode("Y29tLm1pdWkucGxheWVy")),//com.miui.player
            new String(Base64.decode("dlN0dWRpby5BbmRyb2lkLkNhbWVyYTM2MA==")),//vStudio.Android.Camera360
            new String(Base64.decode("Y29tLmFuZHJvaWQuZmFjZWxvY2s=")), //com.android.facelock
            new String(Base64.decode("a3IuY28udGljdG9jcGx1cw==")), //kr.co.tictocplus
    };

    boolean mbReport = false;

    public void initFileCacheFilter() {

        mCtx = CleanCloudManager.getApplicationContext();

        // 服务器控制过滤，防止误删。
//        for (int i = mDBColumnFilterST; i <= mDBColumnFilterSP; i++) {
//            if (DBColumnFilterManager.getInstance().isFilter(
//                    DBColumnFilterManager.EXPAND_FILTER_TABLE_NAME_STUB,
//                    String.valueOf(i))) {
//                mFileLevelScanPkgFilter[i - mDBColumnFilterST] = false;
//            } else {
//                mFileLevelScanPkgFilter[i - mDBColumnFilterST] = true;
//            }
//        }
    }

    public void setFileFirstScanFlag(boolean firstScanFlag) {
        mFirstScanFlag = firstScanFlag;
    }


    /**
     * 保存扫描结果
     * @param pkgData
     * @param mCachePkgInfoData
     * @return
     */
    public boolean addFileScanResult(KCacheCloudQuery.PkgQueryData pkgData, HashMap<String, PackageInfo> mCachePkgInfoData) {
        mbReport = true;
        long startTime = SystemClock.uptimeMillis();
        boolean bRet = addFileScanResultInternal( pkgData, mCachePkgInfoData );
        mFileLevelScanTime += (SystemClock.uptimeMillis() - startTime);
        return bRet;
    }

    private boolean addFileScanResultInternal(KCacheCloudQuery.PkgQueryData pkgData, HashMap<String, PackageInfo> mCachePkgInfoData) {
        String pkgName = pkgData.mQueryParam.mPkgName;
        String path = "";
        if (null == pkgData.mResult.mPkgQueryPathItems) {
            pkgData.mResult.mPkgQueryPathItems = new LinkedList<KCacheCloudQuery.PkgQueryPathItem>();
        }

//        if (mFileLevelScanPkgFilter[0] && (pkgName.equals(mFileLevelScanPkg[0])
//                || pkgName.equals(mFileLevelScanPkg[1]))) {
//            PackageInfo packageInfo = mCachePkgInfoData.get(pkgName);
//            if (packageInfo == null) {
//                return false;
//            }
//            return getAutonaviFileScan(pkgData, packageInfo);
//        } else if (pkgName.equals(mFileLevelScanPkg[2])) {
//            if (!mFileLevelScanPkgFilter[1] && !mFileLevelScanPkgFilter[7] && !mFileLevelScanPkgFilter[8] && !mFileLevelScanPkgFilter[9] ) {
//                return false;
//            }
//            boolean isAddFileLevel = false;
//            path = new String(Base64.decode("dGVuY2VudC9taWNyb21zZw==")); //tencent/micromsg;
//            String scanDirPath = defaultSdCardPath + File.separator + path;
//            String patternStr = "/[0-9a-z]{32}";
//            ArrayList<String> scanDirPathList = findDirRegList(scanDirPath, patternStr);
//            if (scanDirPathList.isEmpty()) {
//                return false;
//            }
////            if (mFileLevelScanPkgFilter[1] && getTencentVideoFileScan(pkgData, scanDirPath, scanDirPathList)) { //video
////                isAddFileLevel = true;
////            }
//            if (mFileLevelScanPkgFilter[7] && getTencentAvatarFileScan(pkgData, scanDirPath, scanDirPathList)) {
//                isAddFileLevel = true;
//            }
//            if (mFileLevelScanPkgFilter[8] && getTencentEmojiFileScan(pkgData, scanDirPath, scanDirPathList)) {
//                isAddFileLevel = true;
//            }
//            if (mFileLevelScanPkgFilter[9] && getTencentMicroMsgImageFileScan(pkgData, scanDirPath, scanDirPathList)) {
//                isAddFileLevel = true;
//            }
//            return isAddFileLevel;
//        } else if (mFileLevelScanPkgFilter[2] && pkgName.equals(mFileLevelScanPkg[3])) {
//            return getMiuiUserbookFileScan(pkgData);
//        } else if (mFileLevelScanPkgFilter[3] && pkgName.equals(mFileLevelScanPkg[4])) {
//            return getMiuiRomFileScan(pkgData);
//        } else if (mFileLevelScanPkgFilter[4] && pkgName.equals(mFileLevelScanPkg[5])) {
//            return getKugouFileScan(pkgData);
////        } else if (mFileLevelScanPkgFilter[5] && pkgName.equals(mFileLevelScanPkg[6])) {
////            return getMiuiMusicFileScan(pkgData);
//        } else if (mFileLevelScanPkgFilter[6] && pkgName.equals(mFileLevelScanPkg[7])) {
//            return getCamera360FileScan(pkgData);
//        } else if (mFileLevelScanPkgFilter[10] && pkgName.equals(mFileLevelScanPkg[8])) {
//            return getSamsungFaceLockFileScan(pkgData);
//        } else if (mFileLevelScanPkgFilter[11] && pkgName.equals(mFileLevelScanPkg[9])) {
//            return getTictocplusFileScan(pkgData);
//        }
        return false;
    }
}

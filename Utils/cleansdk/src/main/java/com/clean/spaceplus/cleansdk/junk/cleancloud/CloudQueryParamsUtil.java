package com.clean.spaceplus.cleansdk.junk.cleancloud;

import android.content.pm.PackageInfo;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.utils.system.PackageManagerWrapper;
import com.clean.spaceplus.cleansdk.junk.cleancloud.cache.cloud.CachePkgNetQuery;
import com.clean.spaceplus.cleansdk.junk.engine.task.SdCardCacheScanTask;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import space.network.cleancloud.KCacheCloudQuery;

/**
 * @author Jerry
 * @Description:
 * @date 2016/7/4 11:20
 * @copyright TCL-MIG
 */
public class CloudQueryParamsUtil {

    /**
     * 按包查询残留残留
     * @return
     */
    public static Collection<String>  getResidualQueryByPkgNameParams(){
        List<PackageInfo> allPkgList = PackageManagerWrapper.getInstance().getPkgInfoList();
        List<String> queryPkgList = filterInstalledPkgList(allPkgList);
        return queryPkgList;
    }


    /**
     * 过滤应用本身的包名
     * @param pkgList
     */
    public static List<String>  filterInstalledPkgList(List<PackageInfo> pkgList) {
        final String PKG_NAME = SpaceApplication.getInstance().getContext().getPackageName();
        List<String> installedList = new ArrayList<>();
        if(pkgList!=null){
            for(PackageInfo pkg : pkgList){
                if(pkg!=null){
                    if(!pkg.packageName.equals(PKG_NAME)){
                        installedList.add(pkg.packageName);
                    }
                }
            }
        }
        return installedList;
    }






    private static int getScanType(int mScanCfgMask){
        int mScanType = KCacheCloudQuery.ScanType.DEFAULT;
        if (0 == (mScanCfgMask & SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_NOT_ONLY_PRIVACY_QUERY)) {
            mScanType = KCacheCloudQuery.ScanType.PRIVACY;
        } else if (0 == (mScanCfgMask & SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_QUERY_WITHOUT_ALERTINFO)) {
            mScanType = KCacheCloudQuery.ScanType.CAREFUL;
        } else if (0 == (mScanCfgMask & SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO)) {
            mScanType = KCacheCloudQuery.ScanType.SUGGESTED_WITH_CLEANTIME;
        }
        return mScanType;
    }

    public static List<PackageInfo>  filteInstalledPkgList(List<PackageInfo> pkgList) {
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
        return installedList;
    }



    public static Collection<KCacheCloudQuery.PkgQueryParam>  getCacheQueryByPkgNameParams(){
        List<PackageInfo> allPkgList = PackageManagerWrapper.getInstance().getPkgInfoList();
        List<PackageInfo> queryPkgList = filteInstalledPkgList(allPkgList);

        KCacheCloudQuery.PkgQueryParam param = null;
        ArrayList<KCacheCloudQuery.PkgQueryParam> queryDatas = new ArrayList<>(queryPkgList.size());
        int scanCfgMask = ~(SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_QUERY_WITH_ALERTINFO |
                SdCardCacheScanTask.SD_CACHE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS);
        int scanType = getScanType(scanCfgMask);
        NLog.d(CachePkgNetQuery.TAG, "scanType = %d", scanType);
        for(PackageInfo pkg : queryPkgList){
            param = new KCacheCloudQuery.PkgQueryParam();
            param.mCleanType = scanType;
            param.mPkgName = pkg.packageName;
            queryDatas.add(param);
        }
        return queryDatas;
    }
}

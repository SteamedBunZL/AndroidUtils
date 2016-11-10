package com.tcl.security.virusengine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.tcl.security.cloudengine.CloudEngine;
import com.tcl.security.cloudengine.CloudRequest;
import com.tcl.security.virusengine.cache.Cache;
import com.tcl.security.virusengine.func_interface.DataReport;
import com.tcl.security.virusengine.modle.ApkModle;
import com.tcl.security.virusengine.modle.McafeeModle;
import com.tcl.security.virusengine.utils.DescriptionUtil;
import com.tcl.security.virusengine.utils.HashUtil;
import com.tcl.security.virusengine.utils.VirusLog;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 数据上报实现类
 * Created by Steve on 2016/7/13.
 */
public class DefaultDataReport extends DataReport {

    public DefaultDataReport(){
        super();
    }

    /**
     * 获取ApkModles
     * @param
     * @return
     */
    @Override
    protected ArrayList<ApkModle> getApkModles(Object... obj) {
        ArrayList<ApkModle> apkList = new ArrayList<>();
        try {
            CopyOnWriteArrayList<Cache.CacheEntry> list = (CopyOnWriteArrayList<Cache.CacheEntry>) obj[0];
            //去除McAfee云扫失败的entry,不上报
            for(Cache.CacheEntry entry:list){
                try {
                    if (entry.avengine_cloud_result== Constants.CLOUD_RESULT_ERROR) {
                        list.remove(entry);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
            if (list.isEmpty()) {
                return apkList;
            }
            Context mContext = VirusScanQueue.getInstance().getContext();

            CloudEngine engine = CloudEngine.getInstance(mContext);
            ApkModle apkModle;
            McafeeModle mcafeeModle;
            for(Cache.CacheEntry entry:list){
                try {
                    apkModle = new ApkModle();
                    VirusLog.w("================= from %d",entry.avengine_cloud_result);
                    apkModle.PackageName = entry.packageName;
                    apkModle.VersionCode = entry.versionCode;
                    apkModle.VersionName = entry.applicationVersion;
                    apkModle.CertName = HashUtil.getSigName(mContext,entry.packageName);
                    apkModle.CertMD5 = HashUtil.getSigHash(mContext,entry.packageName); // 不为空
                    if (TextUtils.isEmpty(entry.tclHash)){
                        CloudRequest.MetaInfo metaInfo =  CloudRequest.getMetaInfoForPkg(mContext,entry.packageName);
                        if (engine!=null&&metaInfo!=null)
                            apkModle.TCLHash = metaInfo.apkHash;
                    }else{
                        apkModle.TCLHash = entry.tclHash; // 不为空
                    }
                    if (TextUtils.isEmpty(entry.publicSourceDir)){
                        PackageInfo info = mContext.getPackageManager().getPackageInfo(entry.packageName,PackageManager.GET_SIGNATURES);
                        entry.publicSourceDir = info.applicationInfo.publicSourceDir;
                    }
                    apkModle.Size = new File(entry.publicSourceDir).length();
                    VirusLog.w("===ZL AppName : %s,PackageName : %s,Size %d,TCLHash %s.",entry.appName,entry.packageName,apkModle.Size,apkModle.TCLHash);
                    mcafeeModle = new McafeeModle();
                    mcafeeModle.Category = entry.scanState;
                    if (entry.avengine_cloud_result== Constants.CLOUD_RESULT_CLEAN||entry.avengine_cloud_result== Constants.CLOUD_RESULT_RISK)
                        mcafeeModle.ResultFrom = Constants.DATA_REPORT_RESULT_FROM_CLOUD;
                    else
                        mcafeeModle.ResultFrom = Constants.DATA_REPORT_RESULT_FROM_LOCAL;
                    if (entry.typeInt == Constants.ScanInfo.DEFAULT_VIRUS_TYPE){
                        mcafeeModle.Risk = Constants.ScanInfo.DEFAULT_VIRUS_TYPE;
                        mcafeeModle.Type = Constants.ScanInfo.DEFAULT_VIRUS_TYPE;
                    }else{
                        mcafeeModle.Risk = entry.risk_level;
                        mcafeeModle.Type = entry.typeInt;
                    }
                    mcafeeModle.VirusName = entry.virusName;
                    mcafeeModle.VirusDesc = (ArrayList<Integer>) DescriptionUtil.converseIdStrArrayToList(entry.description_ids);
                    apkModle.McAfee = mcafeeModle;
                    apkModle.Unknown = true;
                    apkList.add(apkModle);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apkList;
    }







}

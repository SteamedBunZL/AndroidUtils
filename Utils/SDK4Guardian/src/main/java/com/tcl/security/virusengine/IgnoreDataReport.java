package com.tcl.security.virusengine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.tcl.security.cloudengine.CloudEngine;
import com.tcl.security.cloudengine.CloudRequest;
import com.tcl.security.virusengine.func_interface.DataReport;
import com.tcl.security.virusengine.modle.ApkModle;
import com.tcl.security.virusengine.utils.HashUtil;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 忽略事件 上报
 * Created by Steve on 16/9/26.
 */

public class IgnoreDataReport extends DataReport {


    public IgnoreDataReport() {
        super();
    }

    @Override
    protected ArrayList<ApkModle> getApkModles(Object... obj) {
        Context context = VirusScanQueue.getInstance().getContext();
        ArrayList<ApkModle> apkList = new ArrayList<>();
        try {
            List<String> packageList = (List<String>) obj[0];
            CloudEngine engine = CloudEngine.getInstance(context);
            ApkModle apkModle;
            PackageManager pm = context.getPackageManager();
            PackageInfo info;
            for(String packageName:packageList){
                apkModle = new ApkModle();
                apkModle.PackageName = packageName;
                info = pm.getPackageInfo(packageName,0);
                apkModle.VersionCode = info.versionCode;
                apkModle.VersionName =info.versionName;
                apkModle.CertName = HashUtil.getSigName(context,packageName);
                apkModle.CertMD5 = HashUtil.getSigHash(context,packageName);
                CloudRequest.MetaInfo metaInfo =  CloudRequest.getMetaInfoForPkg(context,packageName);
                if (engine!=null&&metaInfo!=null){
                    apkModle.TCLHash = metaInfo.apkHash;
                    VirusLog.e("TCL hash %s",metaInfo.apkHash);
                }
                apkModle.Unknown = false;
                apkModle.Ignored = true;
                apkList.add(apkModle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apkList;
    }
}

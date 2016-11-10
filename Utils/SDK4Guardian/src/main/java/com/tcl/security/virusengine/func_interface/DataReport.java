package com.tcl.security.virusengine.func_interface;

import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.tcl.security.virusengine.VirusScanQueue;
import com.tcl.security.virusengine.modle.ASModle;
import com.tcl.security.virusengine.modle.AVEngineModle;
import com.tcl.security.virusengine.modle.ApkModle;
import com.tcl.security.virusengine.modle.DeviceModle;
import com.tcl.security.virusengine.modle.HiSecuritySDKModle;
import com.tcl.security.virusengine.modle.UploadModle;
import com.tcl.security.virusengine.network.BaseNetwork;
import com.tcl.security.virusengine.network.RequestCallback;
import com.tcl.security.virusengine.utils.ApkInfoUtil;
import com.tcl.security.virusengine.utils.DataReportUtil;
import com.tcl.security.virusengine.utils.DateUtil;
import com.tcl.security.virusengine.utils.JSON;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Steve on 2016/5/16.
 */
public abstract class DataReport {

    private static final long SIZE_APK_UPLOAD = 20*1024*1024;

    public static ExecutorService POOL = Executors.newFixedThreadPool(3);

    public DataReport(){
    }

    public void reportData(Object... obj){
        POOL.execute(new UploadTask(obj));
    }

    private UploadModle getUploadData(Object... obj){
        return getUploadModle(obj);
    }

    private UploadModle getUploadModle(Object... obj){
        Map<String, String> deviceMap = DataReportUtil.getDeviceMap(VirusScanQueue.getInstance().getContext());
        ArrayList<ApkModle> apkList = getApkModles(obj);
        ASModle asModle = DataReportUtil.getAsModle(deviceMap);
        AVEngineModle avEngineModle = DataReportUtil.getAvEngineModle(deviceMap);
        DeviceModle deviceModle = DataReportUtil.getDeviceModle(deviceMap);
        HiSecuritySDKModle sdkModle = DataReportUtil.getHiSecuritySDK();
        return getUploadModle(apkList,asModle,avEngineModle,deviceModle,sdkModle);
    }

    private UploadModle getUploadModle(ArrayList<ApkModle> apkList, ASModle asModle, AVEngineModle avEngineModle, DeviceModle deviceModle, HiSecuritySDKModle sdkModle) {
        UploadModle uploadModle = new UploadModle();
        uploadModle.APKs = apkList;
        uploadModle.AS = asModle;
        uploadModle.AVEngine = avEngineModle;
        uploadModle.Device = deviceModle;
        uploadModle.ReportDateTime = DateUtil.getUTCStringDate();
        uploadModle.HiSecuritySDK = sdkModle;
        VirusLog.d("UTC 时间: %s", DateUtil.getUTCStringDate());
        return uploadModle;
    }

    protected abstract ArrayList<ApkModle> getApkModles(Object... obj);

    class UploadTask implements Runnable{

        final Object[] obj;

        UploadTask(Object... obj){
            this.obj = obj;
        }

        @Override
        public void run() {
            final UploadModle uploadModle = getUploadData(obj);
            if (!allowUpload(uploadModle))
                return;
            String json = JSON.toJson(uploadModle);
            VirusLog.w("===ZL report mcafee json %s", json);
            BaseNetwork.getInstance().invokeUpload("reportData", json, new RequestCallback() {
                @Override
                public void onFail(int code, String message) {
                    VirusLog.w("upload mcafee code====%d,message====%s", code, message);
                }

                @Override
                public void onSuccess(Object obj) {
                    VirusLog.w("report mcafee successfully");
                    uploadMd5Task(uploadModle);
                }
            });
        }

            private void uploadMd5Task(UploadModle uploadModle){
                UploadModle md5UploadModle = getMd5UploadModle(uploadModle);
                if (md5UploadModle==null||!allowUpload(md5UploadModle))
                    return;
                String json = JSON.toJson(md5UploadModle);
                VirusLog.w("===ZL report md5 json %s",json);
                BaseNetwork.getInstance().invokeUpload("reportMD5", json, new RequestCallback() {
                    @Override
                    public void onSuccess(Object obj) {
                        VirusLog.w("report md5 data successfully");
                    }

                    @Override
                    public void onFail(int code, String message) {
                        VirusLog.w("upload md5 code====%d,message====%s",code,message);
                    }
                });

            }

            private boolean allowUpload(UploadModle uploadModle){
                ArrayList<ApkModle> APKS = uploadModle.APKs;
                if (APKS==null||APKS.isEmpty())
                    return false;
                return true;
            }

        private UploadModle getMd5UploadModle(UploadModle uploadModle){
            ArrayList<ApkModle> APKS = uploadModle.APKs;
            if (APKS.isEmpty())
                return null;
            ArrayList<ApkModle> MD5APKS = new ArrayList<>();
            ApkModle md5ApkModle =null;
            PackageManager pm = VirusScanQueue.getInstance().getContext().getPackageManager();
            String path = null;
            for(ApkModle apkModle:APKS){
                try {
                    if (apkModle.Size<=SIZE_APK_UPLOAD){
                        md5ApkModle = new ApkModle();
                        md5ApkModle.Size = apkModle.Size;
                        md5ApkModle.TCLHash = apkModle.TCLHash;
                        md5ApkModle.CertMD5 = apkModle.CertMD5;
                        md5ApkModle.CertName = apkModle.CertName;
                        md5ApkModle.Deleted = apkModle.Deleted;
                        md5ApkModle.Ignored = apkModle.Ignored;
                        md5ApkModle.McAfee = apkModle.McAfee;
                        md5ApkModle.PackageName = apkModle.PackageName;
                        md5ApkModle.Unknown = apkModle.Unknown;
                        md5ApkModle.VersionCode = apkModle.VersionCode;
                        md5ApkModle.VersionName = apkModle.VersionName;
                        path = pm.getApplicationInfo(apkModle.PackageName,0).publicSourceDir;
                        md5ApkModle.MD5 = ApkInfoUtil.obtainApkMD5(path);
                        if (TextUtils.isEmpty(md5ApkModle.MD5))
                            continue;
                        MD5APKS.add(md5ApkModle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (MD5APKS.isEmpty())
                return null;
            UploadModle newUploadModle = new UploadModle();
            newUploadModle.APKs = MD5APKS;
            newUploadModle.AS = uploadModle.AS;
            newUploadModle.AVEngine = uploadModle.AVEngine;
            newUploadModle.Device = uploadModle.Device;
            newUploadModle.ReportDateTime = uploadModle.ReportDateTime;
            newUploadModle.HiSecuritySDK = uploadModle.HiSecuritySDK;
            return newUploadModle;
        }


    }






}

package com.tcl.security.virusengine.network.network_task;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.security.cloudengine.CloudEngine;
import com.tcl.security.cloudengine.CloudResponse;
import com.tcl.security.cloudengine.RttProfile;
import com.tcl.security.virusengine.engine.CloudProcessFlurryHelper;
import com.tcl.security.virusengine.modle.ASModle;
import com.tcl.security.virusengine.modle.AVEngineModle;
import com.tcl.security.virusengine.modle.ApkModle;
import com.tcl.security.virusengine.modle.DeviceModle;
import com.tcl.security.virusengine.modle.HiSecuritySDKModle;
import com.tcl.security.virusengine.modle.UploadModle;
import com.tcl.security.virusengine.network.RequestCallback;
import com.tcl.security.virusengine.utils.DataReportUtil;
import com.tcl.security.virusengine.utils.DateUtil;
import com.tcl.security.virusengine.utils.JSON;
import com.tcl.security.virusengine.utils.NetworkUtils;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.start;
import static com.tcl.security.virusengine.utils.NetworkUtils.getConnectionType;

/**
 * Created by Steve on 2016/8/18.
 */
public class DoQuery implements Runnable {

    private String url;

    private List<String> packages;

    private Map<String, String> params;

    private RequestCallback callback;

    private Context context;

    public DoQuery(Context context, String url, List<String> packages, Map<String, String> params, RequestCallback callback) {
        this.url = url;
        this.packages = packages;
        this.params = params;
        this.callback = callback;
        this.context = context;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        List<CloudResponse> responses = null;
        CloudEngine engine = CloudEngine.getInstance(context);
        params = new HashMap<>();
        params.put("scanType","quick");
        UploadModle uploadModle = getUploadModle();
        String json = JSON.toJson(uploadModle);
        if (!TextUtils.isEmpty(json))
            params.put("clientInfo",json);
        setQueryTimeoutStrategy(engine);
        final int num = packages.size();
        boolean cloudResult = false;
        try {
            if (engine != null)
                responses = engine.queryPkgs(packages, url, params);
        } catch (Exception e) {
            cloudResult = false;
            VirusLog.printException(e,"Tcl Cloud");
        }finally {
            doFinally(num,engine,start,responses,cloudResult);

        }
    }


    private UploadModle getUploadModle(){
        Map<String, String> deviceMap = DataReportUtil.getDeviceMap(context);
        ASModle asModle = DataReportUtil.getAsModle(deviceMap);
        AVEngineModle avEngineModle = DataReportUtil.getAvEngineModle(deviceMap);
        DeviceModle deviceModle = DataReportUtil.getDeviceModle(deviceMap);
        HiSecuritySDKModle sdkModle = DataReportUtil.getHiSecuritySDK();
        return getUploadModle(null,asModle,avEngineModle,deviceModle,sdkModle);
    }

    private UploadModle getUploadModle(ArrayList<ApkModle> apkList, ASModle asModle, AVEngineModle avEngineModle, DeviceModle deviceModle,HiSecuritySDKModle sdkModle) {
        UploadModle uploadModle = new UploadModle();
        uploadModle.APKs = apkList;
        uploadModle.AS = asModle;
        uploadModle.AVEngine = avEngineModle;
        uploadModle.Device = deviceModle;
        uploadModle.ReportDateTime = DateUtil.getUTCStringDate();
        uploadModle.HiSecuritySDK = sdkModle;
        VirusLog.d("UTC 时间: %s",DateUtil.getUTCStringDate());
        return uploadModle;
    }

    /**
     * 设置云扫超时策略
     *
     * @param engine
     */
    private void setQueryTimeoutStrategy(CloudEngine engine) {
        int type = getConnectionType(context);
        if (engine != null && type != NetworkUtils.TYPE_DISCONNECT) {//有网络
            if (type == NetworkUtils.TYPE_WIFI) {
                engine.setTimeout(10000);
            } else {
                engine.setTimeout(20000);
            }
        }
    }

    private void doFinally(int num,CloudEngine engine,long start,List<CloudResponse> responses,boolean cloudResult){
        long end = System.currentTimeMillis();
        float real_time = (end - start) / 1000.0f;

        if (responses == null) {//说明云查失败了
            cloudResult = false;
            //engine为空的情况
            if (engine==null){
                if (callback!=null)
                    callback.onFail(-1,"Engine为空");
                return;
            }
            CloudEngine.Error error = engine.getError();
//            if (error != null)
//                VirusLog.e("Tcl Cloud Request Fail. Code: %d, Message: %s", error.code, error.msg);
            if (callback != null)
                callback.onFail(error==null?-1:error.code,error.msg);
            VirusLog.e("Tcl Cloud Request Fail");
        } else {//说明云查成功了
            cloudResult = true;
            if (callback != null)
                callback.onSuccess(responses);
        }
        Log.e("VirusLog","This time sdk invoked time:" + real_time + ",and result is " + cloudResult);
        uploadTclMessage(engine,real_time,num,cloudResult);
    }

    /**
     * 上报TCL云扫真实时间
     * @param engine
     */
    private void uploadTclResponseTime(CloudEngine engine){
        if (engine==null)
            return;
        List<RttProfile> list = engine.getRtt();
        if (list != null && !list.isEmpty()) {
            RttProfile rtt = list.get(0);
            if (rtt != null) {
                float real_time = rtt.rtt / 1000.0f;
                CloudProcessFlurryHelper.fluryTclCloudScanResponseTime(real_time);
            }
            VirusLog.e("==========================RTT %s", list.toString());
        }
    }

    /**
     * 上报TCL云完整时间
     * @param realTime
     * @param result
     */
    private void uploadTclRealTime(float realTime,boolean result){
        CloudProcessFlurryHelper.fluryTclCloudScanRealTime(realTime,result);
    }

    /**
     * 上报TCL云查数目
     * @param num
     */
    private void uploadTclScanSize(int num){
        CloudProcessFlurryHelper.fluryTclCloudScanNum(packages.size());
    }

    /**
     * 上报TCL云信息
     * @param engine
     * @param realTime
     * @param num
     * @param result
     */
    private void uploadTclMessage(CloudEngine engine,float realTime,int num,boolean result){
        uploadTclScanSize(num);
        uploadTclResponseTime(engine);
        uploadTclRealTime(realTime,result);
    }

}
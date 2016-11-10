package com.tcl.security.virusengine.engine;

import com.intel.security.vsm.ScanTask;
import com.intel.security.vsm.content.ScanSource;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.HashMap;
import java.util.List;

import tlogsdk.HiLogManager;

import static tlogsdk.HiLogManager.sendEvent;

/**
 * 用于云扫描上传flurry统计Helper类
 * Created by Steve on 2016/8/12.
 */
public class CloudProcessFlurryHelper {

    /**
     * McAfee flury云扫描完成数据上报
     * @param sourceList
     * @param start
     * @param taskList
     */
    public static void fluryMcAfeeCloudDataReport(List<ScanSource> sourceList, long start, List<ScanTask> taskList) {
        //上报个数
        HiLogManager.sendEvent("cloud_mcafee_time","cloud_mcafee_time_num",sourceList.size());
        //处理mcafee云返回异常/未知的，使用mcafee本地引擎继续查杀
        long end = System.currentTimeMillis();
        float time = (end-start)/1000.0f;
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("cloud_mcafee_time_real",Float.toString(time));
        sendEvent("cloud_mcafee_time",params);
        HashMap<String,String> params_response = new HashMap<String, String>();
        if (!taskList.isEmpty()){
            ScanTask task = taskList.get(0);
            float time_response = task.getState().getElapsedTime()/1000.0f;
            VirusLog.e("==== cloud_time_response %f",time_response);
            params_response.put("cloud_mcafee_time_response",Float.toString(time_response));
            sendEvent("cloud_mcafee_time",params_response);
        }
        VirusLog.w("本次Mcafee云扫用时 %f s",time);
    }

    /**
     * Tcl云扫 扫描APP数目上报
     * @param num
     */
    public static void fluryTclCloudScanNum(int num){
        HiLogManager.sendEvent("cloud_tcl_time", "cloud_tcl_time_num", num);
    }

    /**
     * Tcl云扫 扫描纯网络时间上报
     * @param real_time
     */
    public static void fluryTclCloudScanResponseTime(float real_time){
        HashMap<String, String> params = new HashMap<>();
        params.put("cloud_tcl_time_response", Float.toString(real_time));
        sendEvent("cloud_tcl_time", params);
    }

    /**
     * Tcl云扫 扫描全部用时上报
     * @param real_time
     */
    public static void fluryTclCloudScanRealTime(float real_time,boolean result){
        HashMap<String, String> params = new HashMap<>();
        params.put("cloud_tcl_time_real", Float.toString(real_time));
        params.put("cloud_tcl_time_result",Boolean.toString(result));
        sendEvent("cloud_tcl_time", params);
    }

    /**
     * TCL 深扫是否超时
     * @param timeout
     */
            public static void fluryTclDeepScanTimeout(boolean timeout,long apkNum,float scanTime){
        HashMap<String,String> params = new HashMap<>();
        params.put("cloud_tcl_deep_timeout",Boolean.toString(timeout));
        params.put("cloud_tcl_deep_timeout_appk_num",Long.toString(apkNum));
        params.put("cloud_tcl_deep_timeout_scan_time",Float.toString(scanTime));
        sendEvent("cloud_tcl_deep",params);
    }

    /**
     * TCL 深扫扫描数量
     * @param unapkNum
     * @param apkNum
     */
    public static void fluryTclDeepScanNum(long unapkNum,long apkNum){
        HashMap<String,String> params = new HashMap<>();
        params.put("cloud_tcl_deep_num_unapk",Long.toString(unapkNum));
        params.put("cloud_tcl_deep_num_apk",Long.toString(apkNum));
        sendEvent("cloud_tcl_deep_num",params);
    }


    /**
     * Mcafee深扫时间
     * @param time
     */
    public static void flurryMcafeeDeepScanTime(int result,float time){
        HashMap<String,String> params = new HashMap<>();
        params.put("mcafee_deep_scan_result",Integer.toString(result));
        params.put("mcafee_deep_scan_time",Float.toString(time));
        sendEvent("mcafee_deep_scan",params);
    }

    /**
     * Mcafee深扫检出数
     *
     */
    public static void flurryMcafeeDeepScanVirusNum(long scanNum,long virusNum){
        HashMap<String,String> params = new HashMap<>();
        params.put("mcafee_deep_scan_scan_num",Long.toString(scanNum));
        params.put("mcafee_deep_scan_virus_num",Long.toString(virusNum));
        sendEvent("mcafee_deep_scan",params);
    }






}

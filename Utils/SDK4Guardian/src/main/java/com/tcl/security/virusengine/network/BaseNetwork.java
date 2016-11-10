package com.tcl.security.virusengine.network;

import android.content.Context;

import com.tcl.security.cloudengine.CloudEngine;
import com.tcl.security.virusengine.VirusScanQueue;
import com.tcl.security.virusengine.network.network_task.DoDeepScanQuery;
import com.tcl.security.virusengine.network.network_task.DoQuery;
import com.tcl.security.virusengine.network.network_task.DoUpload;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Steve on 2016/4/29.
 */
public class BaseNetwork implements Network {

    private BaseNetwork() {
    }

    private static BaseNetwork instance = new BaseNetwork();

    private ExecutorService pool = Executors.newFixedThreadPool(4);

    private Context context;

    public static BaseNetwork getInstance(){
        return instance;
    }


    @Override
    public void init(Context context, CloudEngine.CallbackInit initCallback) {
        this.context = context;
        CloudEngine.init(context, initCallback);
    }

    @Override
    public void invokeQuery(String key, List<String> packages, Map<String, String> params, RequestCallback callback) {
        final URLData urlData = UrlConfigManager.findURL(context.getApplicationContext(), context.getPackageName(), key);
        if (urlData != null && urlData.getUrl() != null) {
            VirusLog.d("invoked and url %s", urlData);
            pool.execute(new DoQuery(context.getApplicationContext(), urlData.getUrl(), packages, params, callback));
        }
    }

    @Override
    public NetTask invokeDeepScanQuery(List<String> paths, Map<String, String> params, NettaskCallback callback) {
        NetTask netTask = new NetTask();
        final URLData urlData = UrlConfigManager.findURL(context.getApplicationContext(), context.getPackageName(), "queryPackage");
        if (urlData != null && urlData.getUrl() != null) {
            VirusLog.d("invoked and url %s", urlData);
            pool.execute(new DoDeepScanQuery(context.getApplicationContext(), netTask, urlData.getUrl(), paths, params, callback));
            return netTask;
        }
        return null;
    }

    @Override
    public void invokeUpload(String key, String json, RequestCallback callback) {
        final URLData urlData = UrlConfigManager.findURL(context.getApplicationContext(), context.getPackageName(), key);
        if (urlData != null && urlData.getUrl() != null) {
            VirusLog.d("invoked and url %s", urlData);
            VirusScanQueue.getInstance().getThreadPool().execute(new DoUpload(context, urlData.getUrl(), json, callback));
        }
    }


}

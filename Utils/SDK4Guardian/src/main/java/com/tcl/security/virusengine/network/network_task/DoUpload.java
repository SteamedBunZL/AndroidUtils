package com.tcl.security.virusengine.network.network_task;

import android.content.Context;

import com.tcl.security.cloudengine.CloudEngine;
import com.tcl.security.cloudengine.CloudResponse;
import com.tcl.security.virusengine.network.NetworkHelper;
import com.tcl.security.virusengine.network.RequestCallback;

/**
 * Created by Steve on 2016/8/18.
 */
public class DoUpload implements Runnable{

    private Context context;

    private String url;

    private String json;

    private RequestCallback callback;

    public DoUpload(Context context, String url, String json, RequestCallback callback){
        this.context = context;
        this.url = url;
        this.json = json;
        this.callback = callback;
    }


    @Override
    public void run() {
        CloudEngine engine = CloudEngine.getInstance(context);
        if (engine!=null){
            CloudResponse.ResponseStream responseStream = engine.visit(url,json.getBytes(),null);
            if (responseStream==null){
                CloudEngine.Error error = engine.getError();
                callback.onFail(error==null?-1:error.code,error==null?null:error.msg);
                return;
            }

            String response = NetworkHelper.getResponseString(responseStream);

            callback.onSuccess(response);

        }
    }
}

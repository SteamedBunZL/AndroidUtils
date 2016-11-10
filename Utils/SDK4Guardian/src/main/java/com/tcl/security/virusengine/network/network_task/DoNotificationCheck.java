package com.tcl.security.virusengine.network.network_task;

import android.content.Context;

import com.tcl.security.cloudengine.CloudEngine;
import com.tcl.security.cloudengine.CloudResponse;
import com.tcl.security.virusengine.network.NetworkHelper;
import com.tcl.security.virusengine.network.RequestCallback;

/**
 * Created by lenovo on 2016/9/6.
 */

public class DoNotificationCheck implements Runnable {

    private Context context;

    private String url;

    private RequestCallback callback;

    public DoNotificationCheck(Context context, String url, RequestCallback callback) {
        this.context = context;
        this.url = url;
        this.callback = callback;
    }

    @Override
    public void run() {
        CloudEngine engine = CloudEngine.getInstance(context);
        if (engine != null) {
            CloudResponse.ResponseStream responseStream = engine.visit(url, null, null, "GET");
            if (responseStream == null) {
                CloudEngine.Error error = engine.getError();
                callback.onFail(error == null ? -1 : error.code, error == null ? null : error.msg);
                return;
            }

            String response = NetworkHelper.getResponseString(responseStream);

            callback.onSuccess(response);

        }
    }
}

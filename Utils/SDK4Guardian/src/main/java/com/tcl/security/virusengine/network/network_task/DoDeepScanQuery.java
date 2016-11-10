package com.tcl.security.virusengine.network.network_task;

import android.content.Context;

import com.tcl.security.cloudengine.CloudEngine;
import com.tcl.security.cloudengine.CloudResponse;
import com.tcl.security.cloudengine.RttProfile;
import com.tcl.security.virusengine.network.NetTask;
import com.tcl.security.virusengine.network.NettaskCallback;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.List;
import java.util.Map;

/**
 * Created by Steve on 16/10/17.
 */

public class DoDeepScanQuery implements Runnable{


    private String url;

    private List<String> packages;

    private Map<String, String> params;

    private NettaskCallback callback;

    private Context context;

    private NetTask task;



    public DoDeepScanQuery(Context context, NetTask task, String url, List<String> packages, Map<String, String> params, NettaskCallback callback) {
        this.url = url;
        this.packages = packages;
        this.params = params;
        this.callback = callback;
        this.context = context;
        this.task = task;
    }
    @Override
    public void run() {
        long start = System.currentTimeMillis();
        List<CloudResponse> responses = null;
        CloudEngine engine = CloudEngine.getInstance(context);


        try {
            if (engine != null) {
                VirusLog.e("+++++++++++++++++++++++++++++++++++++");

                responses = engine.queryFiles(packages, url, params);

                //这里统计时间
                List<RttProfile> list = engine.getRtt();
                if (list != null && !list.isEmpty()) {
                    RttProfile rtt = list.get(0);
                    if (rtt != null) {
                        float real_time = rtt.rtt / 1000.0f;
                    }
                    VirusLog.w("RTT %s", list.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            long end = System.currentTimeMillis();
            float real_time = (end - start) / 1000.0f;
            VirusLog.e("This time sdk invoked time: %f", (end - start) / 1000.0f);
            if (responses == null || responses.size() == 0) {
                if (engine==null)
                    return;
                CloudEngine.Error error = engine.getError();
                if (error != null)
                    VirusLog.e("Tcl Cloud Request Fail. Code: %d, Message: %s", error.code, error.msg);
                if (callback != null) {
                    task.isCompleted = true;
                    callback.onFail(task, error == null ? -1 : error.code, error.msg);
                }
            } else {
                if (callback != null){
                    task.isCompleted = true;
                    callback.onSuccess(task,responses);
                }
            }
        }
    }
}

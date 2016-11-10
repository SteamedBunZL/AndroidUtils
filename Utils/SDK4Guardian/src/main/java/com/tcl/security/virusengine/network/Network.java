package com.tcl.security.virusengine.network;

import android.content.Context;

import com.tcl.security.cloudengine.CloudEngine;

import java.util.List;
import java.util.Map;

/**
 * 依托于cloudengine.jar上的封装
 * Created by Steve on 2016/4/29.
 */
public interface Network {

    void init(Context context, CloudEngine.CallbackInit initCallback);

    /**
     * 基于cloudengine.jar 访问tcl云
     * @param key
     * @param packages
     * @param params
     * @param callback
     */
    void invokeQuery(String key, List<String> packages, Map<String, String> params, RequestCallback callback);

    NetTask invokeDeepScanQuery(List<String> paths, Map<String, String> params, NettaskCallback callback);


    void invokeUpload(String key, String json, RequestCallback callback);

}

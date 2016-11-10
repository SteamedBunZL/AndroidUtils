package com.tcl.security.virusengine.network;

/**
 * Created by Steve on 2016/6/22.
 */
public interface RequestCallback {

    void onSuccess(Object obj);

    void onFail(int code, String message);

}

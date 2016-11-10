package com.tcl.security.virusengine.network;

/**
 * Created by Steve on 16/10/18.
 */

public interface NettaskCallback {

    void onSuccess(NetTask task, Object obj);

    void onFail(NetTask task, int code, String message);
}

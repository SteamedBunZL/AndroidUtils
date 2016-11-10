package com.tcl.security.virusengine.func_interface;

/**
 * Created by Steve on 2016/6/28.
 */
public interface IInitializationCallback {

    /**
     * When the method is invoked.It means that the engine is initial success.
     */
    void onInitiaSuccess(String dat);

    void onInitialFailed();

}

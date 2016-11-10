package com.tcl.security.virusengine.func_interface;

/**
 * Created by Steve on 2016/6/28.
 */
public interface IUpdateObserver {

    void onUpdateStart();

    void onUpdateComplete(int i, String dat);

}

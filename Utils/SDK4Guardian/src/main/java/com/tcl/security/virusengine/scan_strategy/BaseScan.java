package com.tcl.security.virusengine.scan_strategy;

import com.tcl.security.virusengine.func_interface.IScanScheduleCallback;

/**
 * Created by Steve on 16/8/31.
 */
public class BaseScan {

    protected IScanScheduleCallback mScheduleCallback;

    protected void setScheduleCallback(IScanScheduleCallback scheduleCallback){
        this.mScheduleCallback = scheduleCallback;
    }
}

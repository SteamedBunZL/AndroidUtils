package com.tcl.security.virusengine.entry;

import com.intel.security.vsm.content.ScanSource;

/**
 * 待扫描实体
 * Created by Steve on 2016/7/14.
 */
public class ScanEntity {

    public ScanSource scanSource;

    public int tcl_cloud_result;

    public int avenine_cloud_result;

    public ScanEntity(ScanSource scanSource, int tcl_cloud_result, int avenine_cloud_result){
        this.scanSource = scanSource;
        this.tcl_cloud_result = tcl_cloud_result;
        this.avenine_cloud_result = avenine_cloud_result;
    }


}

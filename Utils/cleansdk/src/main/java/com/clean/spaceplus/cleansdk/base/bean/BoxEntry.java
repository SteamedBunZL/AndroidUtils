package com.clean.spaceplus.cleansdk.base.bean;

import java.io.File;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/23 16:52
 * @copyright TCL-MIG
 */
public class BoxEntry {
    public BoxEntry(File mountPoint, File target, String packageName) {
        this.mountPoint = mountPoint;
        this.target = target;
        this.packageName = packageName;
    }
    public File mountPoint = null;
    public File target = null;
    public String packageName = null;


    @Override
    public String toString() {
        return String.format("(:CMBOX_ENTRY :mount-point %s :target %s :packageName %s)", mountPoint, target, packageName);
    }
}

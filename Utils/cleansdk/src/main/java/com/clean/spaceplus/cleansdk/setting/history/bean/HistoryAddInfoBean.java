package com.clean.spaceplus.cleansdk.setting.history.bean;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.util.PackageUtils;

import java.io.Serializable;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/5/14 11:16
 * @copyright TCL-MIG
 */
public class HistoryAddInfoBean implements Serializable, Comparable<HistoryAddInfoBean> {
    public String cleanName; //应用名
    public String packageName; //应用包名
    public double cleanSize; //清理大小
    public String icon = ""; //清理应用icon
    transient public long mCleanByteSize;  //清理大小，单位：b

    public HistoryAddInfoBean(){

    }

    public HistoryAddInfoBean(String cleanName, String packname, double cleanSize){
        this.packageName = packname;
        this.cleanSize = cleanSize;
        this.cleanName = cleanName;
    }

    public HistoryAddInfoBean(String packname, double cleanSize){
        this.packageName = packname;
        this.cleanSize = cleanSize;
        this.cleanName = PackageUtils.getAppNameByPackageName(SpaceApplication.getInstance().getContext(), packname);
    }

    @Override
    public String toString() {
        return "HistoryAddInfoBean{" +
                "cleanName='" + cleanName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", cleanSize=" + cleanSize +
                ", icon='" + icon + '\'' +
                '}';
    }

    @Override
    public int compareTo(HistoryAddInfoBean another) {
        if (another == null){
            return 1;
        }

        if(cleanSize < another.cleanSize){
            return -1;
        }
        else if(cleanSize == another.cleanSize){
            return 0;
        }
        else{
            return 1;
        }
    }

    public HistoryAddInfoBean add(HistoryAddInfoBean other){
        if (this.packageName.equals(other.packageName)){
            this.cleanSize += other.cleanSize;
        }
        return this;
    }
}

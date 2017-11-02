package com.clean.spaceplus.cleansdk.base.utils.analytics.bean;

import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.bean.DataReportCleanBean;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author zeming_liu
 * @Description: 垃圾清理功能事件
 * @date 2016/9/20.
 * @copyright TCL-MIG
 */
public class CleanAllEvent  extends Event {
    @Expose
    @SerializedName(DataReprotStringKey.EVENTNAME)
    public String mName;
    @Expose
    @SerializedName(DataReprotStringKey.SCANTYPE)
    public String mScanType;
    @Expose
    @SerializedName(DataReprotStringKey.SIZE)
    public String mSize;
    @Expose
    @SerializedName(DataReprotStringKey.PKG)
    public String mPkg;
    @Expose
    @SerializedName(DataReprotStringKey.PATH)
    public String mPath;
    @Expose
    @SerializedName(DataReprotStringKey.SCANTIME)
    public String mScantime;

    public CleanAllEvent(String scantype ,String size, String pkg, String path,String scantime){
        mName= DataReportCleanBean.EVENT_SPACE_CLEAN_ALL;
        mScanType = scantype;
        mSize=size;
        mPkg=pkg;
        mPath=path;
        mScantime=scantime;
    }

    @Override
    public String toJson() {
        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
    }
}

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
public class CleanEvent extends Event{

    @Expose
    @SerializedName(DataReprotStringKey.EVENTNAME)
    public String mName;
    @Expose
    @SerializedName(DataReprotStringKey.ENTRY)
    public String mEntry;
    @Expose
    @SerializedName(DataReprotStringKey.ACTION)
    public String mAction;
    @Expose
    @SerializedName(DataReprotStringKey.FIRST)
    public String mFirst;
    @Expose
    @SerializedName(DataReprotStringKey.SCANTIME)
    public String mScantime;
    @Expose
    @SerializedName(DataReprotStringKey.TOTAL)
    public String mTotal;
    @Expose
    @SerializedName(DataReprotStringKey.SCANTYPE)
    public String mScanType;
    @Expose
    @SerializedName(DataReprotStringKey.SUGGEST)
    public String mSuggest;
    @Expose
    @SerializedName(DataReprotStringKey.CLEANTIME)
    public String mCleanTime;
    @Expose
    @SerializedName(DataReprotStringKey.CLEANSITE)
    public String mCleanSize;

    public CleanEvent(String entry,String action,String first,String cleantime, String cleansize,String scantime,String suggest,String scantype,String total){
        mName= DataReportCleanBean.EVENT_SPACE_CLEAN;
        mEntry=entry;
        mAction=action;
        mScantime = scantime;
        mFirst=first;
        mTotal=total;
        mScanType=scantype;
        mSuggest=suggest;
        mCleanTime=cleantime;
        mCleanSize=cleansize;
    }

    public CleanEvent(String entry,String action,String first,String scantime, String suggest,String total, String scantype){
        mName= DataReportCleanBean.EVENT_SPACE_CLEAN;
        mEntry=entry;
        mAction=action;
        mScantime = scantime;
        mFirst=first;
        mTotal=total;
        mScanType=scantype;
        mSuggest=suggest;
        mCleanTime="";
        mCleanSize="";
    }

    @Override
    public String toJson() {
        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
    }
}

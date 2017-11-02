package com.clean.spaceplus.cleansdk.base.utils.analytics.bean;

import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.bean.DataReportPublicBean;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;
import com.clean.spaceplus.cleansdk.util.PhoneUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author zeming_liu
 * @Description: 后台报活事件
 * @date 2016/9/21.
 * @copyright TCL-MIG
 */
public class ActivityEvent extends Event {

    @Expose
    @SerializedName(DataReprotStringKey.EVENTNAME)
    public String mName;
    @Expose
    @SerializedName(DataReprotStringKey.DPI)
    public String mDpi;
    @Expose
    @SerializedName(DataReprotStringKey.IMSI)
    public String mImsi;
    @Expose
    @SerializedName(DataReprotStringKey.RAM)
    public String mRam;
    @Expose
    @SerializedName(DataReprotStringKey.SDCARD)
    public String mSdcard;
    @Expose
    @SerializedName(DataReprotStringKey.SDCARDIN)
    public String mSdcardin;
    @Expose
    @SerializedName(DataReprotStringKey.SDCARDOUT)
    public String mSdcardout;
    @Expose
    @SerializedName(DataReprotStringKey.BULK)
    public String mBulk;
    @Expose
    @SerializedName(DataReprotStringKey.COMPETITOR)
    public String mCompetitor;
    @Expose
    @SerializedName(DataReprotStringKey.NET)
    public String mNet;
    @Expose
    @SerializedName(DataReprotStringKey.ROOT)
    public String mRoot;
    @Expose
    @SerializedName(DataReprotStringKey.CPU)
    public String mCpu;
    @Expose
    @SerializedName(DataReprotStringKey.DATABASE)
    public String mDatabase;

    public ActivityEvent(String dpi,String imsi,String ram,String sdcard,String sdcardin,String sdcardout,
                         String bulk,String competitor,String net,String root,String database){
        mName= DataReportPublicBean.EVENT_SPACE_ACTIVITY;
        mCpu= PhoneUtil.getCpuType();
        mDpi= dpi;
        mImsi=imsi;
        mRam= ram;
        mSdcard=sdcard;
        mSdcardin=sdcardin;
        mSdcardout=sdcardout;
        mBulk=bulk;
        mCompetitor=competitor;
        mNet=net;
        mRoot=root;
        mDatabase=database;
    }

    @Override
    public String toJson() {

        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
    }
}

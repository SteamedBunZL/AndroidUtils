//package com.clean.spaceplus.cleansdk.base.utils.analytics.bean;
//
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.bean.DataReportPublicBean;
//import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;
//import com.google.gson.annotations.Expose;
//import com.google.gson.annotations.SerializedName;
//
///**
// * @author zeming_liu
// * @Description: 应用卸载事件
// * @date 2016/9/22.
// * @copyright TCL-MIG
// */
//public class UninstallEvent extends Event {
//
//    @Expose
//    @SerializedName(DataReprotStringKey.EVENTNAME)
//    public String mName;
//    @Expose
//    @SerializedName(DataReprotStringKey.COMPET)
//    public String mCompet;
//    @Expose
//    @SerializedName(DataReprotStringKey.APPNAME)
//    public String mAppname;
//    @Expose
//    @SerializedName(DataReprotStringKey.SITE)
//    public String mSite;
//    @Expose
//    @SerializedName(DataReprotStringKey.RESIDUALPOPTIME)
//    public String mResidualpoptime;
//    @Expose
//    @SerializedName(DataReprotStringKey.ACTION)
//    public String mAction;
//    @Expose
//    @SerializedName(DataReprotStringKey.TYPE)
//    public String mType;
//
//    public UninstallEvent(String compet,String appname,String site,String residualpoptime,String action,String type){
//        mName= DataReportPublicBean.EVENT_SPACE_UNINSTALL;
//        mCompet=compet;
//        mAppname=appname;
//        mSite=site;
//        mResidualpoptime=residualpoptime;
//        mAction=action;
//        mType=type;
//    }
//
//    @Override
//    public String toJson() {
//        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
//    }
//}

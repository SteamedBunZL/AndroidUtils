//package com.clean.spaceplus.cleansdk.base.utils.analytics.bean;
//
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.bean.DataReportPageBean;
//import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;
//import com.google.gson.annotations.Expose;
//import com.google.gson.annotations.SerializedName;
//
///**
// * @author zeming_liu
// * @Description: 页面停留时间事件
// * @date 2016/9/21.
// * @copyright TCL-MIG
// */
//public class PageTimeEvent extends Event {
//
//    @Expose
//    @SerializedName(DataReprotStringKey.EVENTNAME)
//    public String mName;
//    @Expose
//    @SerializedName(DataReprotStringKey.ENTRY)
//    public String mEntry;
//    @Expose
//    @SerializedName(DataReprotStringKey.PAGE)
//    public String mPage;
//    @Expose
//    @SerializedName(DataReprotStringKey.STAYTIME)
//    public String mStaytime;
//
//    public PageTimeEvent(String entry ,String page, String staytime){
//        mName= DataReportPageBean.EVENT_SPACE_TIME;
//        mEntry = entry;
//        mPage=page;
//        mStaytime=staytime;
//    }
//
//    @Override
//    public String toJson() {
//        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
//    }
//}

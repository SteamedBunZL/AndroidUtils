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
// * @Description: 通知事件
// * @date 2016/9/22.
// * @copyright TCL-MIG
// */
//public class NotificationEvent extends Event {
//
//    @Expose
//    @SerializedName(DataReprotStringKey.EVENTNAME)
//    public String mName;
//    @Expose
//    @SerializedName(DataReprotStringKey.ACTION)
//    public String mAction;
//    @Expose
//    @SerializedName(DataReprotStringKey.TYPE)
//    public String mType;
//    @Expose
//    @SerializedName(DataReprotStringKey.CONTENT)
//    public String mContent;
//
//    public NotificationEvent(String action,String type,String content){
//        mName= DataReportPublicBean.EVENT_SPACE_NOTIFICATION;
//        mAction=action;
//        mType=type;
//        mContent=content;
//    }
//
//    @Override
//    public String toJson() {
//        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
//    }
//}

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
// * @author Jerry
// * @Description:
// * @date 2016/9/23 17:35
// * @copyright TCL-MIG
// */
//
//public class ShortCutEvent extends Event{
//    @Expose
//    @SerializedName(DataReprotStringKey.EVENTNAME)
//    public String mName;
//    @Expose
//    @SerializedName(DataReprotStringKey.PKGNAME)
//    public String pkgname;
//    @Expose
//    @SerializedName(DataReprotStringKey.TYPE)
//    public String type;
//
//    public ShortCutEvent(String pkgname, String type) {
//        mName= DataReportPublicBean.EVENT_SPACE_ICON;
//        this.pkgname = pkgname;
//        this.type = type;
//    }
//
//    @Override
//    public String toJson() {
//        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
//    }
//}

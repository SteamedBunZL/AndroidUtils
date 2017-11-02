//package com.clean.spaceplus.cleansdk.base.utils.analytics.bean;
//
//import android.content.pm.PackageInfo;
//
//import SpaceApplication;
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportConfigManage;
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.bean.DataReportPageBean;
//import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;
//import com.google.gson.annotations.Expose;
//import com.google.gson.annotations.SerializedName;
//
///**
// * @author zeming_liu
// * @Description: 页面和动作事件
// * @date 2016/9/20.
// * @copyright TCL-MIG
// */
//public class PageEvent extends Event {
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
//    @SerializedName(DataReprotStringKey.CONTENT)
//    public String mContent;
//    @Expose
//    @SerializedName(DataReprotStringKey.CONTENT1)
//    public String mContent1;
//    @Expose
//    @SerializedName(DataReprotStringKey.ACTION)
//    public String mAction;
//    @Expose
//    @SerializedName(DataReprotStringKey.USERTYPE)
//    public String mUsertype;
//
//    public PageEvent(String entry ,String page, String content, String action){
//        mName= DataReportPageBean.EVENT_SPACE_NAME;
//        mEntry = entry;
//        mPage=page;
//        mContent=content;
//        mAction=action;
//        mContent1="";
//        setPageTime();
//    }
//
//    public PageEvent(String entry ,String page, String content, String action, String content1){
//        mName= DataReportPageBean.EVENT_SPACE_NAME;
//        mEntry = entry;
//        mPage=page;
//        mContent=content;
//        mAction=action;
//        mContent1=content1;
//        setPageTime();
//    }
//
//    private void setPageTime(){
//        try{
//            PackageInfo info=SpaceApplication.getInstance().getContext().getPackageManager().getPackageInfo(SpaceApplication.getInstance().getContext().getPackageName(),0);
//            long time=info.firstInstallTime;
//            if(System.currentTimeMillis()-time>=DataReportConfigManage.SPACE_DATAREPORT_ACTIVITY_PAGE_CYCLE){
//                mUsertype=DataReportPageBean.USERTYPE_OLD;
//            }
//            else{
//                mUsertype=DataReportPageBean.USERTYPE_NEW;
//            }
//        }catch (Exception e){
//            //mUsertype=DataReportPageBean.USERTYPE_OLD;
//        }
//
//    }
//
//    @Override
//    public String toJson() {
//        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
//    }
//}

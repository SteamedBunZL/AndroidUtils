//package com.clean.spaceplus.cleansdk.base.utils.analytics;
//
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;
//import com.google.gson.annotations.Expose;
//import com.google.gson.annotations.SerializedName;
//
///**
// * @author haiyang.tan
// * @Description:
// * @date 2016/7/5 15:19
// * @copyright TCL-MIG
// */
//public class ButtonEvent extends Event {
//
//    @Expose(deserialize = false,serialize = false)
//    public static final String NAME = "eventn";
//
//    @Expose(deserialize = false,serialize = false)
//    public static final String TIME = "startt";
//
//    @Expose(deserialize = false,serialize = false)
//    public static final String VALUE = "eventv";
//
//    @Expose(deserialize = false,serialize = false)
//    public static final String EXTRA = "i";
//
//    @Expose
//    @SerializedName(DataReprotStringKey.EVENTNAME)
//    public String mEvenname;
//    @Expose
//    @SerializedName(NAME)
//    public String mName;
//    @Expose
//    @SerializedName(VALUE)
//    public String mValue;
//    @Expose
//    @SerializedName(TIME)
//    public String mTime;
//    @Expose
//    @SerializedName(EXTRA)
//    public Object mExtra;
//
//    public ButtonEvent(String name, String value, String time, Object extra){
//        setEventName();
//        mName = name;
//        mValue = value;
//        mExtra = extra;
//        mTime = time;
//    }
//
//    public ButtonEvent(String name, String value, Object extra){
//        setEventName();
//        mName = name;
//        mValue = value;
//        mExtra = extra;
//        mTime = String.valueOf(System.currentTimeMillis());
//    }
//
//    public ButtonEvent(String name, String value){
//        setEventName();
//        mName = name;
//        mValue = value;
//        mExtra = "";
//        mTime = String.valueOf(System.currentTimeMillis());
//    }
//
//    public ButtonEvent(String name){
//        setEventName();
//        mName = name;
//        mValue = "";
//        mExtra = "";
//        mTime = String.valueOf(System.currentTimeMillis());
//    }
//
//    private void setEventName(){
//        mEvenname="oldEvent";
//    }
//
//    @Override
//    public String toJson() {
//        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
//    }
//
//}

//package com.clean.spaceplus.cleansdk.base.utils.analytics.bean;
//
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;
//import com.clean.spaceplus.cleansdk.base.utils.DataReport.bean.DataReportBoostBean;
//import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;
//import com.google.gson.annotations.Expose;
//import com.google.gson.annotations.SerializedName;
//
///**
// * @author zeming_liu
// * @Description: 内存加速功能事件
// * @date 2016/9/20.
// * @copyright TCL-MIG
// */
//public class BoostEvent extends Event {
//
//    @Expose
//    @SerializedName(DataReprotStringKey.EVENTNAME)
//    public String mName = DataReportBoostBean.EVENT_SPACE_NAME;
//    @Expose
//    @SerializedName(DataReprotStringKey.ENTRY)
//    public String mEntry;
//    @Expose
//    @SerializedName(DataReprotStringKey.ACTION)
//    public String mAction;
//    @Expose
//    @SerializedName(DataReprotStringKey.SCANTIME)
//    public String mScantime;
//    @Expose
//    @SerializedName(DataReprotStringKey.SUGGEST)
//    public String mSuggestSize;
//
//    @Expose
//    @SerializedName(DataReprotStringKey.TOTAL)
//    public String mTotalScanSize;
//
//
//    /**
//     * 清理耗时
//     */
//    @Expose
//    @SerializedName(DataReprotStringKey.CLEANTIME)
//    public String mCleanTime;
//
//    /**
//     * 清理内存大小
//     */
//    @Expose
//    @SerializedName(DataReprotStringKey.CLEANSITE)
//    public String mCleanSize;
//
//    /**
//     * 提速比例
//     */
//    @Expose
//    @SerializedName(DataReprotStringKey.RATIO)
//    public String mRatio;
//
//
//    /**
//     * 是否开启超级加速 是=1 否=2
//     */
//    @Expose
//    @SerializedName(DataReprotStringKey.SUPERBOOST)
//    public String mSuperboost;
//
//
//    public BoostEvent() {
//    }
//
//    /**
//     * 扫描时需要传递的参数
//     * @param entry
//     * @param action
//     * @param scantime
//     * @param suggestSize
//     * @param totalSize
//     */
//    public BoostEvent(String entry,String action,String scantime, String suggestSize,String totalSize, String superBoost){
//        mName = DataReportBoostBean.EVENT_SPACE_NAME;
//        mEntry = entry;
//        mAction = action;
//        mScantime = scantime;
//        mSuggestSize =suggestSize;
//        mTotalScanSize = totalSize;
//        mCleanTime = "";
//        mCleanSize = "";
//        mRatio = "";
//        mSuperboost = superBoost;
//    }
//
//    /**
//     * 清理完成时上报的构造函数
//     * @param entry
//     * @param action
//     */
//    public BoostEvent(String entry,String action,String scanTime, String suggestSize, String totalSize,String cleanTime, String cleanSize, String ratio, String superBoost){
//        mName = DataReportBoostBean.EVENT_SPACE_NAME;
//        mEntry = entry;
//        mAction = action;
//        mScantime = scanTime;
//        mSuggestSize = suggestSize;
//        mTotalScanSize = totalSize;
//        mCleanTime = cleanTime;
//        mCleanSize = cleanSize;
//        mRatio = ratio;
//        mSuperboost = superBoost;
//    }
//
//
//    @Override
//    public String toJson() {
//        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
//    }
//
//    @Override
//    public String toString() {
//        return "BoostEvent{" +
//                "mName='" + mName + '\'' +
//                ", mEntry='" + mEntry + '\'' +
//                ", mAction='" + mAction + '\'' +
//                ", mScantime='" + mScantime + '\'' +
//                ", mSuggestSize='" + mSuggestSize + '\'' +
//                ", mTotalScanSize='" + mTotalScanSize + '\'' +
//                ", mCleanTime='" + mCleanTime + '\'' +
//                ", mCleanSize='" + mCleanSize + '\'' +
//                ", mRatio='" + mRatio + '\'' +
//                ", mSuperboost='" + mSuperboost + '\'' +
//                "} " + super.toString();
//    }
//}

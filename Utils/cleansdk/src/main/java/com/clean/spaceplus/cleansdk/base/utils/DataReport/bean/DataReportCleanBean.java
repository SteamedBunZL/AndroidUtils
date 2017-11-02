package com.clean.spaceplus.cleansdk.base.utils.DataReport.bean;

import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;

/**
 * @author zeming_liu
 * @Description: 垃圾清理数据上报bean
 * @date 2016/9/14.
 * @copyright TCL-MIG
 */
public class DataReportCleanBean extends DataReportBaseBean{

    public static final String EVENT_SPACE_CLEAN="space_sdk_clean";
    public static final String EVENT_SPACE_CLEAN_ALL="space_sdk_clean_all";
    //清理入口选项
    public static final String ENTRY_TYPE_HOME_CIRCLE="1";
    public static final String ENTRY_TYPE_HOME_TEXT="2";
    public static final String ENTRY_TYPE_NOTIFICATION_BAR="3";
    public static final String ENTRY_TYPE_DIALOG_LEFT="4";
    //动作选项
    public static final String ACTION_SCAN_START="1";
    public static final String ACTION_SCAN_FINISH="2";
    public static final String ACTION_SCAN_STOP="3";
    public static final String ACTION_SCAN_BACK="4";
    public static final String ACTION_SCAN_HOME="5";
    public static final String ACTION_CLEAN="6";

    //是否首次
    public static final String SCAN_FIRST="1";

    //具体垃圾清理明细项
    public static final String SCAN_TYPE_SYSCACHE="1";
    public static final String SCAN_TYPE_SDCACHE="2";
    public static final String SCAN_TYPE_LEFTCACHE="3";
    public static final String SCAN_TYPE_ADVCACHE="4";
    public static final String SCAN_TYPE_APKCACHE="5";
    public static final String SCAN_TYPE_TEMPFILE="6";
    public static final String SCAN_TYPE_PROCESS="7";


    public DataReportCleanBean(String entry,String action,String first,String scantime,String total,String scantype) {
        setEntry(entry);
        setAction(action);
        setFirst(first);
        setScantime(scantime);
        setTotal(total);
        setScantype(scantype);
    }

    //垃圾清理入口标识
    public void setEntry(String entry) {
        put(DataReprotStringKey.ENTRY,entry);
    }

    public void setAction(String action) {
        put(DataReprotStringKey.ACTION,action);
    }

    public void setFirst(String first) {
        put(DataReprotStringKey.FIRST,first);
    }

    public void setScantime(String scantime) {
        put(DataReprotStringKey.SCANTIME,scantime);
    }

    public void setSuggest(String suggest) {
        put(DataReprotStringKey.SUGGEST,suggest);
    }

    public void setTotal(String total) {
        put(DataReprotStringKey.TOTAL,total);
    }

    public void setScantype(String scantype) {
        put(DataReprotStringKey.SCANTYPE,scantype);
    }

    public void setCleantype(String cleantype) {
        put(DataReprotStringKey.CLEANTYPE,cleantype);
    }

    public void setCleantime(String cleantime) {
        put(DataReprotStringKey.CLEANTIME,cleantime);
    }

    public void setCleansite(String cleansite) {
        put(DataReprotStringKey.CLEANSITE,cleansite);
    }

    public void setStaytime(String staytime) {
        put(DataReprotStringKey.STAYTIME,staytime);
    }
}

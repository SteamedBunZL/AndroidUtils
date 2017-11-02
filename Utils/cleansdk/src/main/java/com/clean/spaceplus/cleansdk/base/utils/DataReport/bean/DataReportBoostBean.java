package com.clean.spaceplus.cleansdk.base.utils.DataReport.bean;

/**
 * @author zeming_liu
 * @Description: 内存加速数据上报bean
 * @date 2016/9/21.
 * @copyright TCL-MIG
 */
public class DataReportBoostBean extends DataReportBaseBean{
    public static final String EVENT_SPACE_NAME="space_sdk_boost";
    //入口选项
    public static final String ENTRY_TYPE_HOME_CIRCLE="1";
    public static final String ENTRY_TYPE_HOME_TEXT="2";
    public static final String ENTRY_TYPE_NOTIFICATION_BAR="3";
    //动作选项
    public static final String ACTION_SCAN_START="1";
    public static final String ACTION_SCAN_FINISH="2";
    public static final String ACTION_SCAN_BACK="4";
    public static final String ACTION_SCAN_HOME="5";
    public static final String ACTION_SPEED="7";
    public static final String ACTION_SUPER_SPEED="8";
    public static final String ACTION_SUPER_SPEED_HOME="9";

    //是否开启超级加速
    public static final String SUPERBOOST_ON="1";
    public static final String SUPERBOOST_OFF="2";


    //是否首次
    public static final String SCAN_FIRST="1";
}

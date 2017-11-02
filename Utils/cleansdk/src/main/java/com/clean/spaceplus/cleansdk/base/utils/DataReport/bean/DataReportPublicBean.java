package com.clean.spaceplus.cleansdk.base.utils.DataReport.bean;

/**
 * @author zeming_liu
 * @Description: 公共数据上报bean
 * @date 2016/9/21.
 * @copyright TCL-MIG
 */
public class DataReportPublicBean {

    //公共事件，安装
    public static final String EVENT_SPACE_INSTALL="space_sdk_install";
    //公共事件，后台报活
    public static final String EVENT_SPACE_ACTIVITY="space_sdk_activity";
    //公共事件，活跃用户
    public static final String EVENT_SPACE_START="space_sdk_start";
    //公共事件，应用卸载表
    public static final String EVENT_SPACE_UNINSTALL="space_sdk_uninstall";
    //公共事件，通知栏
    public static final String EVENT_SPACE_NOTIFICATION="spac_sdk_notification";
    //公共事件，用户手机里的app
    public static final String EVENT_SPACE_APP="space_sdk_app";
    //公共事件，首次启动，创建桌面图标
    public static final String EVENT_SPACE_ICON="space_sdk_icon";

    //安装选项
    public static final String INSTALL_OVER="1";
    public static final String INSTALL_NEW="2";

    //活跃用户启动选项
    public static  final String OPEN_MODEL_ICON="1";
    public static  final String OPEN_MODEL_NOTIFY="2";
    public static  final String OPEN_MODEL_HOME="3";
    public static  final String OPEN_MODEL_OTHER="4";

    //是否通过space+卸载
    public static final String COMPET_YES="1";
    public static final String COMPET_NO="2";
    //卸载动作,应用卸载
    public static final String UNINSTALL_ACTION_APP="1";
    public static final String UNINSTALL_ACTION_Dialog="2";
    public static final String UNINSTALL_ACTION_CLICK="3";
    //是否快捷卸载应用
    public static final String UNISTALL_QUICK_NO="1";
    public static final String UNISTALL_QUICK_YES="2";

    //是否外置SD卡
    public static final String SDCARD_NO="0";
    public static final String SDCARD_YES="1";
    //手机是否Root
    public static final String ROOT_YES="1";
    public static final String ROOT_NO="2";

    //是否是系统应用
    public static final String APP_SYSTEM="1";
    public static final String APP_USER="2";

}

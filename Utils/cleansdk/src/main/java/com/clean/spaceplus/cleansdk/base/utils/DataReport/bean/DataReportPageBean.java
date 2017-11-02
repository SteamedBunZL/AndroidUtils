package com.clean.spaceplus.cleansdk.base.utils.DataReport.bean;

import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;

/**
 * @author zeming_liu
 * @Description: 页面行为数据上报bean
 * @date 2016/9/14.
 * @copyright TCL-MIG
 */
public class DataReportPageBean  extends DataReportBaseBean{

    public static final String EVENT_SPACE_NAME="space_sdk_page";
    public static final String EVENT_SPACE_TIME="space_sdk_pagetime";
    //清理入口选项
    public static final String ENTRY_TYPE_ICON_CLICK="1";
    public static final String ENTRY_TYPE_NOTIFY_CLICK="2";
    public static final String ENTRY_TYPE_HOME_CLICK="3";

    //动作选项
    public static final String ACTION_PV="1";
    public static final String ACTION_CLICK="2";
    public static final String ACTION_DIALOG="3";
    public static final String ACTION_DIALOG_YES="4";
    public static final String ACTION_DIALOG_NO="5";
    public static final String ACTION_TOAST="6";

    //用户类型,新装和非新装用户
    public static final String USERTYPE_NEW="1";
    public static final String USERTYPE_OLD="2";

    //所有的页面ID，统一管理
    //主界面，首次space+引导页
    public static final String PAGE_MAIN_GUIDE="1001";
    //主界面，space+主页
    public static final String PAGE_MAIN_HOME="1002";
    //主界面，GP好评弹窗
    public static final String PAGE_MAIN_GPRATE="1003";
    //主界面，space+启动页
    public static final String PAGE_MAIN_START="1004";

    //垃圾清理页面，垃圾扫描过程页
    public static final String PAGE_JUNK_SCAN="2001";
    //垃圾清理页面，中途停止扫描的扫描结果页
    public static final String PAGE_JUNK_SCANSTOP="2002";
    //垃圾清理页面，垃圾扫描完成页
    public static final String PAGE_JUNK_SCANFINISH="2003";
    //垃圾清理页面，垃圾清理完成页
    public static final String PAGE_JUNK_CLEANFINISH="2004";
    //垃圾清理页面，垃圾清理详情弹框
    public static final String PAGE_JUNK_SCANINFO="2005";

    //内存加速，扫描内存页
    public static final String PAGE_BOOST_SCAN="3001";
    //内存加速，扫描内存完成页
    public static final String PAGE_BOOST_SCANFINISH="3002";
    //内存加速，清理内存完成页
    public static final String PAGE_BOOST_CLEANFINISH="3003";
    //内存加速，白名单列表页
    public static final String PAGE_BOOST_WHITELIST="3004";
    //内存加速，加入白名单页
    public static final String PAGE_BOOST_JOIN="3005";
    //内存加速，更多功能
    public static final String PAGE_BOOST_MORE="3006";

    //应用管理,应用大小
    public static final String PAGE_APPMGR_APPSIZE="4001";
    //应用管理,最近未使用
    public static final String PAGE_APPMGR_UNUSED ="4002";
    //应用管理,频率
    public static final String PAGE_APPMGR_FREQUENCY="4003";
    //应用管理,日期
    public static final String PAGE_APPMGR_DATE="4004";
    //应用管理,最近未使用-设置
    public static final String PAGE_APPMGR_UNUSEDSETTING ="4005";
    //应用管理,频率-设置
    public static final String PAGE_APPMGR_FRESETTING="4006";
    //应用管理，卸载提示弹窗
    public static final String PAGE_APPMGR_UIS_TIPS_DLG = "4011";
    //应用管理，卸载完成弹窗
    public static final String PAGE_APPMGR_UIS_FINISH_DLG = "4012";
    //应用管理，快捷卸载提示条
    public static final String PAGE_APPMGR_QUICK_UIS_TIPS = "4013";
    //应用管理，前往设置跳转失败toast
    public static final String PAGE_APPMGR_SETTING_FAIL = "4014";

    //其他页面，侧边栏操作
    public static final String PAGE_OTHER_SIDE="5001";
    //其他页面，清理记录
    public static final String PAGE_OTHER_CLEANRECORD="5002";
    //其他页面，检查升级
    public static final String PAGE_OTHER_UPDATE="5003";
    //其他页面，设置
    public static final String PAGE_OTHER_SETTING="5004";
    //其他页面，用户反馈页
    public static final String PAGE_OTHER_FEEDBACE="5005";
    //其他页面，垃圾清理白名单
    public static final String PAGE_OTHER_WHITELIST="5006";
    //其他页面，安装包清理弹窗
    public static final String PAGE_OTHER_CLEARUNUSELESSAPK="5007";
    //其他页面，关于
    public static final String PAGE_OTHER_ABOUT = "5008";
    //其他页面，卸载残留清理弹窗
    public static final String PAGE_OTHER_CLEARUNINSTALREMAIN="5009";

    public DataReportPageBean(String entry,String page,String content){
        setEntry(entry);
        setPage(page);
        setContent(content);
    }

    //垃圾清理入口标识
    public void setEntry(String entry) {
        put(DataReprotStringKey.ENTRY,entry);
    }

    //当前页面
    public void setPage(String page){
        put(DataReprotStringKey.PAGE,page);
    }

    //动作对象
    public void setContent(String content){
        put(DataReprotStringKey.CONTENT,content);
    }

    //对象补充
    public void setContent1(String content){
        put(DataReprotStringKey.CONTENT1,content);
    }

    public void setUsertype(String usertype){
        put(DataReprotStringKey.USERTYPE,usertype);
    }

    public void setAction(String action) {
        put(DataReprotStringKey.ACTION,action);
    }
}

package com.clean.spaceplus.cleansdk.junk.engine.bean;

/**
 * @author zeming_liu
 * @Description: 垃圾清理一级扫描菜单
 * @date 2016/8/5.
 * @copyright TCL-MIG
 */
public class GroupJunkInfo {
    public static final String REPORT_GROUP_TITLE_APP_CACHE = "App cache";
    public static final String REPORT_GROUP_TITLE_APP_RESIDUAL ="App Residual";
    public static final String REPORT_GROUP_TITLE_AD_JUNK= "AD Junk";
    public static final String REPORT_GROUP_TITLE_OBSOLETE_APKS ="Obsolete apks";
    public static final String REPORT_GROUP_TITLE_SYSTEM_CACHE = "System cache";
    public static final String REPORT_GROUP_TITLE_MEMORY_CACHE = "Memory Cache";
    public static final String REPORT_GROUP_TITLE_MORE_SPACE = "more space";


    private String groupTitle;
    private int groupIcon;
    private int groupFlag;
    private String reportGroupTitle="";

    public GroupJunkInfo(String groupTitle,int groupIcon,int groupFlag){
        this.groupTitle=groupTitle;
        this.groupIcon=groupIcon;
        this.groupFlag=groupFlag;
    }

    public GroupJunkInfo(String groupTitle,String reportGroupTitle,int groupIcon,int groupFlag){
        this.groupTitle=groupTitle;
        this.groupIcon=groupIcon;
        this.groupFlag=groupFlag;
        this.reportGroupTitle=reportGroupTitle;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public int getGroupIcon() {
        return groupIcon;
    }

    public int getGroupFlag() {
        return groupFlag;
    }

    public String getReportGroupTitle() {
        return reportGroupTitle;
    }
}

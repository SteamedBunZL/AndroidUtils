package com.clean.spaceplus.cleansdk.base.strategy;

import android.content.Context;
import android.content.SharedPreferences;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.util.SharePreferenceUtil;

/**
 * @author zeming_liu
 * @Description:服务策略控制配置信息
 * @date 2016/9/7.
 * @copyright TCL-MIG
 */
public class SecularConfigManage {

    public static final String CLEANSDK_SECULAR_CONFIG_SP_NAME = "CleanSdk_secular_config_sp_name";

    //记录上传一二级目录的时间
    private static final String CLEANSDK_REPORT_DIR_TIME = "CleanSdk_Report_Dir_time";
    //需要上传一二级目录的周期时间
    public static final long CLEANSDK_SEND_DIR_TIME=7*24*3600*1000;

    //记录上传用户包名的时间
    private static final String CLEANSDK_REPORT_APPNAME_TIME = "CleanSdk_Report_Appname_time";
    //需要上传用户包名的周期时间
    public static final long CLEANSDK_SEND_APPNAME_TIME=7*24*3600*1000;

    private static volatile SecularConfigManage secularConfigManager;
    private SharedPreferences sharedPreference;

    public static SecularConfigManage getInstance(){
        if (secularConfigManager == null){
            synchronized (SecularConfigManage.class){
                if (secularConfigManager == null){
                    secularConfigManager = new SecularConfigManage();
                }
            }
        }
        return secularConfigManager;
    }

    public SharedPreferences getSharedPreference(){
        if (sharedPreference == null){
            sharedPreference = SpaceApplication.getInstance().getContext().getSharedPreferences(CLEANSDK_SECULAR_CONFIG_SP_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreference;
    }

    /**
     * 获取上一次上传一二级目录的时间
     * @return
     */
    public long getLastReportDirLongTime(){
        return getSharedPreference().getLong(CLEANSDK_REPORT_DIR_TIME, 0);
    }

    /**
     * 保存最后一次上传一二级目录的时间
     * @param time
     */
    public void setLastReportDirLongTime(long time) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putLong(CLEANSDK_REPORT_DIR_TIME, time);
        SharePreferenceUtil.applyToEditor(editor);
    }

    /**
     * 获取上一次上传包名的时间
     * @return
     */
    public long getLastReportAppNameLongTime(){
        return getSharedPreference().getLong(CLEANSDK_REPORT_APPNAME_TIME, 0);
    }

    /**
     * 保存最后一次上传包名的时间
     * @param time
     */
    public void setLastReportAppNameLongTime(long time) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putLong(CLEANSDK_REPORT_APPNAME_TIME, time);
        SharePreferenceUtil.applyToEditor(editor);
    }

}

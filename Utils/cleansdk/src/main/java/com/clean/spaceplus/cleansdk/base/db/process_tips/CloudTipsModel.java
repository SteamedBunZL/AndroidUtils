package com.clean.spaceplus.cleansdk.base.db.process_tips;

/**
 * @author zengtao.kuang
 * @Description:
 * @date 2016/6/27 19:43
 * @copyright TCL-MIG
 */
public class CloudTipsModel {
    public String mPkgNameMd5;
    public String mLang;
    public String mUpdateTime;


    private String processTips;
    private String apkTips;

    /**进程描述*/
    public String getProcessTips() {
        return processTips;
    }
    /**进程描述*/
    public void setProcessTips(String processTips) {
        this.processTips = processTips;
    }

    /**应用描述*/
    public String getApkTips() {
        return apkTips;
    }
    /**应用描述*/
    public void setApkTips(String apkTips) {
        this.apkTips = apkTips;
    }
}

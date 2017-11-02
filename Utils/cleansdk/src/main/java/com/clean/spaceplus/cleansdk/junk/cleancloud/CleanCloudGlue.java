package com.clean.spaceplus.cleansdk.junk.cleancloud;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/23 13:57
 * @copyright TCL-MIG
 */
public class CleanCloudGlue {
    public boolean isReportEnable() {
        return false;
    }
    /*
     * 数据上报接口
     */
    public void reportData(String tableName, String data) {
    }

    /*
     * 是否使用海外服务器
     */
    public boolean isUseAbroadServer() {
        return false;
    }

    /*
     * 获取外部存储目录
     */
    public String getExternalStorageDirectory() {
        return null;
    }

    /*
     * 获取provider的authorities
     */
    public String getDBProviderAuthorities() {
        return null;
    }

    /*
     * 云端过滤接口
     */
    public boolean isInCloudFilter(String tableName, String id) {
        return false;
    }

    /*
     * 获取当前的语言设置
     */
    public String getCurrentLanguage() {
        return "en";
    }

    /*
     * 获取库数据存放的目录，如果返回空串那么将使用默认目录
     */
    public String getDataDirectory() {
        return "";
    }

    /*
     * 等待库释放完成
     */
    public void waitDataPrepare() {
    }

    /*
     * 是否允许联网的总控制
     */
    public boolean isAllowAccessNetwork() {
        return true;
    }

    /*
     * 是否禁用本地高频库
     */
    public boolean isDisableLocalHighFreqDb() { return false; }

    /*
    * 是否禁用云端的缓存库
    */
    public boolean isDisableCloudCacheDb() { return false; }
}

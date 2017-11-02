package com.clean.spaceplus.cleansdk.junk.engine;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/28 19:38
 * @copyright TCL-MIG
 */
public class SpaceTaskTime {

    public static final byte CM_TASK_TIME_USER_UNKNOWN = 0;	///< 未知
    public static final byte CM_TASK_TIME_USER_JUNKSTD = 1;	///< 建议清理
    public static final byte CM_TASK_TIME_USER_JUNKADV = 2;	///< 深度清理
    public static final byte CM_TASK_TIME_USER_PC_CALL = 3;	///< 手机助手PC端调用
    public static final byte CM_TASK_TIME_USER_ONEKEY  = 4;	///< 一键清理
    public static final byte CM_TASK_TIME_USER_SYS_STORAGEINSUFFICIENT  = 5;	///< 系统空间不足
    public static final byte CM_TASK_TIME_USER_SPACE_MANAGER = 6;   ///< 空间管理
    public static final byte CM_TASK_TIME_STYPE_UNKNOWN  = 0;	///< 未知
    public static final byte CM_TASK_TIME_STYPE_SYSCACHE = 1;	///< 系统缓存
    public static final byte CM_TASK_TIME_STYPE_SDCACHE  = 2;	///< SD卡缓存
    public static final byte CM_TASK_TIME_STYPE_RUB_ADV  = 3;	///< 广告文件夹
    public static final byte CM_TASK_TIME_STYPE_RUB_ALO  = 4;	///< 残留文件夹
    public static final byte CM_TASK_TIME_STYPE_RUB_TEMP_TYPE = 5;	///< 临时类别(包含空文件夹)
    public static final byte CM_TASK_TIME_STYPE_RUB_EMPTY = 6;	///< 空文件夹
    public static final byte CM_TASK_TIME_STYPE_RUB_BIG  = 7;	///< 大文件
    public static final byte CM_TASK_TIME_STYPE_APK  = 8;		///< apk安装包
    public static final byte CM_TASK_TIME_STYPE_PHOTO  = 9;		///< 相册扫描
    public static final byte CM_TASK_TIME_STYPE_CALC_OTHER_SIZE = 10;	///< 其他目录大小计算，目前只有下载目录和蓝牙目录
    public static final byte CM_TASK_TIME_STYPE_TEMP_FILE  = 11;		///< 临时文件
    public static final byte CM_TASK_TIME_STYPE_LOG_FILE  = 12;			///< 日志文件
    public static final byte CM_TASK_TIME_STYPE_STD_TEMP_TYPE  = 13;		///< 建议清理的临时文件类别(LOST.DIR，淘宝日志)
    public static final byte CM_TASK_TIME_STYPE_OBSOLETE_THUMB  = 14;		///< 无用的缩列图
    public static final byte CM_TASK_TIME_STYPE_DEX_EXT  = 15;		///< 扩展引擎扫描
    public static final byte CM_TASK_TIME_STYPE_DEX_EXT_ADV  = 16;		///< 扩展引擎扫描扫描深度
    public static final byte CM_TASK_TIME_STYPE_AUDIO  = 17;		///< 音频扫描
    public static final byte CM_TASK_TIME_STYPE_RUB_TOTAL = 18;///< 残留扫描task总耗时
    public static final byte CM_TASK_TIME_STYPE_RUB_SD1_CLOUD = 19;///< 残留云端扫描第一张sd卡
    public static final byte CM_TASK_TIME_STYPE_RUB_SD2_CLOUD = 20;///< 残留云端扫描第二张sd卡
    public static final byte CM_TASK_TIME_STYPE_DOCS  = 21;        //docs文件
    public static final byte CM_TASK_TIME_STYPE_SDCACHE_FILE_CUST  = 22;   //系统缓存 文件级别 客户端
    public static final byte CM_TASK_TIME_STYPE_CACHE_CLOUD_FILE  = 23;        //云端缓存文件扫描
    public static final byte CM_TASK_TIME_STYPE_CACHE_CLOUD_FILE_2  = 25;        //云端缓存文件扫描2
    public static final byte CM_TASK_TIME_STYPE_VIDEO_OFFLINE  = 24;        //后台扫描video
    public static final byte CM_TASK_TIME_STYPE_BG_MEDIASTORE_SCAN = 26;
    public static final byte CM_TASK_TIME_STYPE_SCREENSHOTSCOMPRESSSCANTASK  = 27;	///< 截屏压缩扫描
    public static final byte CM_TASK_TIME_STYPE_VIDEO  = 28;		///< 视频扫描

    public static final byte CM_TASK_TIME_ETYPE_NORMAL  = 0;	///< 正常结束
    public static final byte CM_TASK_TIME_ETYPE_CANCEL  = 1;	///< 取消
    public static final byte CM_TASK_TIME_ETYPE_TIMEOUT  = 2;	///< 超时

    /**
     * 扫描来源
     */
    public SpaceTaskTime user(int user) {
        set("user", user);
        return this;
    }

    protected void set(String key, int value) {
        //data.put(key, value);

    }

}

package com.clean.spaceplus.cleansdk.junk.engine;

import com.clean.spaceplus.cleansdk.base.utils.FilterUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/5 15:53
 * @copyright TCL-MIG
 */
public class DBColumnFilterManager {
    public static final String EXPAND_FILTER_TABLE_NAME_STUB = "expand_filter_table_name_stub";

    public static final String EXPAND_FILTER_ID_N7PLARYER_REMAIN		= "1";
    public static final String EXPAND_FILTER_ID_POWERAMP_REMAIN		    = "2";
    public static final String EXPAND_FILTER_ID_TEMP_FILES_SCAN		    = "3";
    public static final String EXPAND_FILTER_ID_LOG_FILES_SCAN		    = "4";	// 从5.8.7.394开始不再使用
    public static final String EXPAND_FILTER_ID_LOST_DIR_FILE_SCAN	    = "5"; // 5.8.9以后关闭，由特征控制
    public static final String EXPAND_FILTER_ID_TAOBAO_LOG_FILE_SCAN	= "6";
    public static final String EXPAND_FILTER_ID_FACE_DIR_FILE_SCAN	    = "7";
    public static final String EXPAND_FILTER_ID_LIBS_DIR_FILE_SCAN	    = "8";
    public static final String EXPAND_FILTER_ID_MFCACHE_DIR_FILE_SCAN	= "9";
    public static final String EXPAND_FILTER_ID_WECHAT_DOWNLOAD_SCAN	= "10";
    public static final String EXPAND_FILTER_ID_LOG_FILES_SCAN2		    = "11";	// 从5.8.7.394开始使用
    public static final String EXPAND_FILTER_ID_CACHE_FILE_LEVEL0	    = "12";	// 从5.8.9 三星
    public static final String EXPAND_FILTER_ID_CACHE_FILE_LEVEL1	    = "13";	// 从5.8.9 腾讯
    public static final String EXPAND_FILTER_ID_CACHE_FILE_LEVEL2	    = "14";	// 从5.8.9 小米用户手册
    public static final String EXPAND_FILTER_ID_CACHE_FILE_LEVEL3	    = "15";	// 从5.8.9 小米升级rom包
    public static final String EXPAND_FILTER_ID_CACHE_FILE_LEVEL4	    = "16";	// 从5.8.9 酷狗音乐
    public static final String EXPAND_FILTER_ID_CACHE_FILE_LEVEL5	    = "17";	// 从5.8.9 小米音乐
    public static final String EXPAND_FILTER_ID_CACHE_FILE_LEVEL6	    = "18";	// 从5.8.9 Camera360
    public static final String EXPAND_FILTER_ID_CACHE_FILE_LEVEL7	    = "19";	// 从5.8.9 tencent avatar
    public static final String EXPAND_FILTER_ID_CACHE_FILE_LEVEL8	    = "20";	// 从5.8.9 tencent emoji
    public static final String EXPAND_FILTER_ID_CACHE_FILE_LEVEL9	    = "21";	// tencent mircomsg image
    public static final String EXPAND_FILTER_ID_CACHE_FILE_LEVEL10	    = "22";	// SAMSUNG face lock
    public static final String EXPAND_FILTER_ID_CACHE_FILE_LEVEL11	    = "23";	// tictocplus

    public static final String EXPAND_FILTER_ID_OFFLINE0	= "50";	// video offline 搜狐
    public static final String EXPAND_FILTER_ID_OFFLINE1	= "51";	// video offline 土豆
    public static final String EXPAND_FILTER_ID_OFFLINE2	= "52";	// video offline 优酷
    public static final String EXPAND_FILTER_ID_OFFLINE3	= "53";	// video  offline 腾讯
    public static final String EXPAND_FILTER_ID_OFFLINE4	= "54";	// vidleo offline 猎豹
    public static final String EXPAND_FILTER_ID_OFFLINE5	= "55";	// vidleo offline qq浏览器
    public static final String EXPAND_FILTER_ID_OFFLINE6	= "56";	// vidleo offline 暴风影音
    public static final String EXPAND_FILTER_ID_OFFLINE7	= "57";	// vidleo offline 爱奇艺
    public static final String EXPAND_FILTER_ID_OFFLINE8	= "58";	// vidleo offline 360影视大全
    public static final String EXPAND_FILTER_ID_OFFLINE9	= "59";	// vidleo offline pptv网络电视

    private static DBColumnFilterManager instance;
    private List<DBColumn> mDBColumnList = new ArrayList<DBColumn>();
    Object lock = new byte[0];

    public static synchronized DBColumnFilterManager getInstance() {
        if (instance == null) {
            instance = new DBColumnFilterManager();
            instance.init();
        }
        return instance;
    }

    public void init() {
        synchronized (lock) {
            mDBColumnList.clear();
            mDBColumnList.addAll(FilterUtil.readDBColumnFilterList());
        }
    }

    /**
     * 是否过滤掉次行数据
     */
    public boolean isFilter(String tableName, String columnIndex) {
        synchronized (lock) {
            for (DBColumn column : mDBColumnList) {
                if (column.equals(tableName, columnIndex)) {
                    return true;
                }
            }
        }
        return false;
    }
}

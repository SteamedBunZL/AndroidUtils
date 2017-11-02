package com.clean.spaceplus.cleansdk.junk.engine;

import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;

import java.util.List;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/29 15:48
 * @copyright TCL-MIG
 */
public class WhiteListsWrapper {

    // / 用于功能点过滤的名字定义
    public static final String FUNCTION_FILTER_NAME_EMPTY_FOLDERS_SCAN = "{BA72720A-60FE-4f0a-9AD2-450C91930AD4}";
    public static final String FUNCTION_FILTER_NAME_DALVIK_CACHE_LEFTOVERS_SCAN = "{0C59CEB8-510A-4c50-A2C0-1A795D6B8BE4}";
    public static final String FUNCTION_FILTER_NAME_POWERAMP_REMAIN_SCAN = "{BA83EA88-BF1F-48b5-8656-B6FBAF82506F}";
    public static final String FUNCTION_FILTER_NAME_N7PLARYER_REMAIN_SCAN = "{57F35E72-B1DD-41a3-887B-1EA3FB86211F}";
    public static final String FUNCTION_FILTER_NAME_LOG_FILES_SCAN = "{C607E6A1-DA0C-4061-BDB6-4602E9E8F756}";
    public static final String FUNCTION_FILTER_NAME_TEMP_FILES_SCAN = "{19CCD878-8A1F-42a5-A495-40DEA4E2A550}";
    public static final String FUNCTION_FILTER_NAME_LOST_DIR_FILE_SCAN = "{4038EEB3-9C4E-49ae-9AD6-7218EF3BC5F9}";
    public static final String FUNCTION_FILTER_NAME_TAOBAO_LOG_FILE_SCAN = "{BF49F24C-8CFD-4958-9725-5F935F38AEEA}";
    public static final String FUNCTION_FILTER_NAME_OBSOLETE_THUMBNAIL_SCAN = "{BBC68FE2-151C-4a94-AD5C-DC37F6A5C852}";
    public static final String FUNCTION_FILTER_NAME_FACE_DIR_FILE_SCAN = "{C2B116CA-FF67-4922-98CF-F36F7160F866}";
    public static final String FUNCTION_FILTER_NAME_LIBS_DIR_FILE_SCAN = "{7263B05C-CA12-47a8-A7C0-DC6A555759F1}";
    public static final String FUNCTION_FILTER_NAME_MFCACHE_DIR_FILE_SCAN = "{F4D5F653-E659-4808-94F4-315A43794461}";
    public static final String FUNCTION_FILTER_NAME_WECHAT_DOWNLOAD_SCAN = "{65D854A3-E23E-4d5c-B1D8-D0C27C503039}";

    private static Object mCacheWhiteListLock = new Object();

    public static List<ProcessModel> getRFWhiteList() {

        //nilo
        /*synchronized (mRFWhiteListLock) {
            return getRFWhiteListDAO().getAllData();
        }*/
        return  null;
    }

    public static boolean isCacheWhiteListItem(String title) {
        synchronized (mCacheWhiteListLock) {
            return false;
        }
    }
}

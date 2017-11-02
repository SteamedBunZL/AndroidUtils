package com.clean.spaceplus.cleansdk.base.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.util.SharePreferenceUtil;
import com.hawkclean.framework.log.NLog;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

/**
 * @author Jerry
 * @Description:
 * @date 2016/7/26 18:14
 * @copyright TCL-MIG
 */

public class DBVersionConfigManager {
    public static final String TAG = DBVersionConfigManager.class.getSimpleName();
    private static volatile DBVersionConfigManager dbConfigManager;
    private static final String DB_VERSION_SP_NAME = "DB_VERSION_SP_NAME";

    /**
     * 默认DB的Version，暂时统一都为10   by:chaohao.zhou
     */
    public static final int DEFAULT_DB_VERSION_NUM = 11;



    // 内存白名单
    public static final int JUNK_PROCESS_DB_TYPE = 2;
    private static final String JUNK_PROCESS_DEFAULT_DB_NAME = "junkprocess_en_1.0.2.filter";
    private static final String JUNK_PROCESS_DEFAULT_DB_VERSION = "1.0.2";
    private static final String JUNK_PROCESS_DB_NAME_SP_KEY = "junk_process_db_name";
    private static final String JUNK_PROCESS_DB_VERSION_SP_NAME = "junk_process_db_version";

    /**
     * 缓存高频库数据库
     * 1.  cache_hf_en_1.0.1.db  2016/08/12 使用qw提供的数据库，只能扫描出QQ和微信，但在08/19的版本上面使用了该库
     * 2.  cache_hf_en_1.0.2.db  2016/08/17 使用qw提供的数据，破解超过90%的数据，在08/22的时候改用该库
     * 3.  cache_hf_en_1.0.3.db  2016/08/19 添加雪璐在9月9号和9月13号提供的需要增加的数据
     */
    public static final int PKG_CACHE_HF_DB_TYPE = 3;
    private static final String PKG_CACHE_HF_DEFAULT_DB_NAME = "cache_hf_en20170424091130.db";
    private static final String PKG_CACHE_HF_DEFAULT_DB_VERSION = "20170424091130";
    private static final String PKG_CACHE_HF_DB_NAME_SP_KEY = "pkg_cache_hf_db_name";
    private static final String PKG_CACHE_HF_DB_VERSION_SP_NAME = "pkg_cache_hf_db_version";

    // 缓存数据库
    public static final int PKG_CACHE_DB_TYPE = 4;
    private static final String PKG_CACHE_DEFAULT_DB_NAME = "cache_1.0.1.db";
    private static final String PKG_CACHE_DEFAULT_DB_VERSION = "1.0.1";
    private static final String PKG_CACHE_DB_NAME_SP_KEY = "pkg_cache_db_name";
    private static final String PKG_CACHE_DB_VERSION_SP_NAME = "pkg_cache_db_version";

    /**
     * 残留高频库数据库
     * 1.  query_hf_en_1.0.1.db 2016/07/21解决appcenter的apps,cache,log这3个目录被删除的bug
     * 2.  query_hf_en_1.0.2.db  2016/08/24 使用全威提供的部分数据
     * 3.  query_hf_en_1.0.3.db  2016/08/24 使用全威提供的破解超过90%的数据
     */
    public static final int PKG_QUERY_HF_DB_TYPE = 5;
    private static final String PKG_QUERY_HF_DEFAULT_DB_NAME = "residual_dir_hf_en_20170401064643.db";
    private static final String PKG_QUERY_HF_DEFAULT_DB_VERSION = "20170401064643";
    private static final String PKG_QUERY_HF_DB_NAME_SP_KEY = "pkg_query_hf_db_name";
    private static final String PKG_QUERY_HF_DB_VERSION_SP_NAME = "pkg_query_hf_db_version";

    // 残留缓存库
    public static final int RESIDUAL_CACHE_DB_TYPE = 6;
    private static final String RESIDUAL_CACHE_DEFAULT_DB_NAME = "leftover_cache_1.0.1.db";
    private static final String RESIDUAL_CACHE_DEFAULT_DB_VERSION = "1.0.1";
    private static final String RESIDUAL_CACHE_DB_NAME_SP_KEY = "residual_cache_db_name";
    private static final String RESIDUAL_CACHE_DB_VERSION_SP_NAME = "residual_cache_db_version";

    /**
     * 广告描述
     * advdesc_cache_1.0.0.db:CM的广告库
     * advdesc_cache_1.0.1:雪璐8月26号提供的广告数据
     * adv_desc_1.0.2.db:雪璐9月9号提供的广告数据
     */
    public static final int ADV_DESC_DB_TYPE = 7;
    private static final String ADV_DESC_DEFAULT_DB_NAME = "adv_desc_1.0.6.db";
    private static final String ADV_DESC_DEFAULT_DB_VERSION = "1.0.6";
    private static final String ADV_DESC_DB_NAME_SP_KEY = "adv_desc_db_name";
    private static final String ADV_DESC_DB_VERSION_SP_NAME = "adv_desc_db_version";


    /**
     * 广告路径
     * junkpath_cache_1.0.0.db:CM的广告库
     * junkpath_cache_1.0.1.db:雪璐8月26号提供的广告数据
     * adv_path_1.0.2.db:雪璐9月9号提供的广告数据(为了方便理解 数据库名称改为adv_path开头)
     */
    public static final int ADV_PATH_DB_TYPE = 1;
    private static final String ADV_PATH_DEFAULT_DB_NAME = "adv_path_1.0.6.db";
    private static final String ADV_PATH_DEFAULT_DB_VERSION = "1.0.6";
    private static final String ADV_PATH_DB_NAME_SP_KEY = "adv_path_db_name";
    private static final String ADV_PATH_DB_VERSION_SP_NAME = "adv_path_db_version";



    // 残留路径缓存库
    public static final int RESIDUAL_PKG_CACHE_DB_TYPE = 100; // 云端是没有这个ID 的，临时取一个
    private static final String RESIDUAL_PKG_CACHE_DEFAULT_DB_NAME = "leftover_pkg_cache_1.0.1.db";
    private static final String RESIDUAL_PKG_CACHE_DEFAULT_DB_VERSION = "1.0.1";
    private static final String RESIDUAL_PKG_CACHE_DB_NAME_SP_KEY = "residual_pkg_cache_db_name";
    private static final String RESIDUAL_PKG_CACHE_DB_VERSION_SP_NAME = "residual_pkg_cache_db_version";

    // ---------------------- 其他库 ---------------------//
    // 进程提示库
    public static final String PROCESS_TIP_DB_NAME = "process_tips.db";

    //使用频率库
    public static  final String APP_OPEN_FREQ_DB_NAME = "app_open_freq_db_name_1.0.0.db";

    //统计分析库
    public static final String ANALYTICS_DB_NAME = "analytics_name.db";

    private SharedPreferences sharedPreference;

    public SharedPreferences getSharedPreference() {
        if (sharedPreference == null){
            sharedPreference = SpaceApplication.getInstance().getContext().getSharedPreferences(DB_VERSION_SP_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreference;
    }


    public static DBVersionConfigManager getInstance(){
        if (dbConfigManager == null){
            synchronized (DBVersionConfigManager.class){
                if (dbConfigManager == null){
                    dbConfigManager = new DBVersionConfigManager();
                }
            }
        }
        return dbConfigManager;
    }

    private String getSpKeyOfDbName(int dbType) {
        switch (dbType) {
            case ADV_PATH_DB_TYPE:
                return ADV_PATH_DB_NAME_SP_KEY;
            case JUNK_PROCESS_DB_TYPE:
                return JUNK_PROCESS_DB_NAME_SP_KEY;
            case PKG_CACHE_HF_DB_TYPE:
                return PKG_CACHE_HF_DB_NAME_SP_KEY;
            case PKG_CACHE_DB_TYPE:
                return PKG_CACHE_DB_NAME_SP_KEY;
            case PKG_QUERY_HF_DB_TYPE:
                return PKG_QUERY_HF_DB_NAME_SP_KEY;
            case RESIDUAL_CACHE_DB_TYPE:
                return RESIDUAL_CACHE_DB_NAME_SP_KEY;
            case ADV_DESC_DB_TYPE:
                return ADV_DESC_DB_NAME_SP_KEY;
            case RESIDUAL_PKG_CACHE_DB_TYPE:
                return RESIDUAL_PKG_CACHE_DB_NAME_SP_KEY;
            default:
                return "";
        }
    }

    private String getSpNameOfDbVersion(int dbType) {
        switch (dbType) {
            case ADV_PATH_DB_TYPE:
                return ADV_PATH_DB_VERSION_SP_NAME;
            case JUNK_PROCESS_DB_TYPE:
                return JUNK_PROCESS_DB_VERSION_SP_NAME;
            case PKG_CACHE_HF_DB_TYPE:
                return PKG_CACHE_HF_DB_VERSION_SP_NAME;
            case PKG_CACHE_DB_TYPE:
                return PKG_CACHE_DB_VERSION_SP_NAME;
            case PKG_QUERY_HF_DB_TYPE:
                return PKG_QUERY_HF_DB_VERSION_SP_NAME;
            case RESIDUAL_CACHE_DB_TYPE:
                return RESIDUAL_CACHE_DB_VERSION_SP_NAME;
            case ADV_DESC_DB_TYPE:
                return ADV_DESC_DB_VERSION_SP_NAME;
            case RESIDUAL_PKG_CACHE_DB_TYPE:
                return RESIDUAL_PKG_CACHE_DB_VERSION_SP_NAME;
            default:
                return "";
        }
    }

    private String getDefaultDBName(int dbType) {
        switch (dbType) {
            case ADV_PATH_DB_TYPE:
                return ADV_PATH_DEFAULT_DB_NAME;
            case JUNK_PROCESS_DB_TYPE:
                return JUNK_PROCESS_DEFAULT_DB_NAME;
            case PKG_CACHE_HF_DB_TYPE:
                return PKG_CACHE_HF_DEFAULT_DB_NAME;
            case PKG_CACHE_DB_TYPE:
                return PKG_CACHE_DEFAULT_DB_NAME;
            case PKG_QUERY_HF_DB_TYPE:
                return PKG_QUERY_HF_DEFAULT_DB_NAME;
            case RESIDUAL_CACHE_DB_TYPE:
                return RESIDUAL_CACHE_DEFAULT_DB_NAME;
            case ADV_DESC_DB_TYPE:
                return ADV_DESC_DEFAULT_DB_NAME;
            case RESIDUAL_PKG_CACHE_DB_TYPE:
                return RESIDUAL_PKG_CACHE_DEFAULT_DB_NAME;
            default:
                return "";
        }
    }

    private String getDefaultDBVersion(int dbType) {
        switch (dbType) {
            case ADV_PATH_DB_TYPE:
                return ADV_PATH_DEFAULT_DB_VERSION;
            case JUNK_PROCESS_DB_TYPE:
                return JUNK_PROCESS_DEFAULT_DB_VERSION;
            case PKG_CACHE_HF_DB_TYPE:
                return PKG_CACHE_HF_DEFAULT_DB_VERSION;
            case PKG_CACHE_DB_TYPE:
                return PKG_CACHE_DEFAULT_DB_VERSION;
            case PKG_QUERY_HF_DB_TYPE:
                return PKG_QUERY_HF_DEFAULT_DB_VERSION;
            case RESIDUAL_CACHE_DB_TYPE:
                return RESIDUAL_CACHE_DEFAULT_DB_VERSION;
            case ADV_DESC_DB_TYPE:
                return ADV_DESC_DEFAULT_DB_VERSION;
            case RESIDUAL_PKG_CACHE_DB_TYPE:
                return RESIDUAL_PKG_CACHE_DEFAULT_DB_VERSION;
            default:
                return "";
        }
    }

    public String getDBName(int dbType) {
        String spNameOfDbName = getSpKeyOfDbName(dbType);
        String defaultName = getDefaultDBName(dbType);
        SharedPreferences preferences = getSharedPreference();
        if (PublishVersionManager.isTest()) {
            NLog.i(TAG, "getDBName " + preferences.getString(spNameOfDbName, defaultName));
        }
        return preferences.getString(spNameOfDbName, defaultName);
    }

    public void setDBName(int dbType, String dbName) {
        String spName = getSpKeyOfDbName(dbType);
        setStringValue(spName, dbName);
    }

    public String getDBVersion(int dbType) {
        String spNameOfDbVersion = getSpNameOfDbVersion(dbType);
        String defaultVersion = getDefaultDBVersion(dbType);
        SharedPreferences preferences = getSharedPreference();
        return preferences.getString(spNameOfDbVersion, defaultVersion);
    }

    public void setDBVersion(int dbType, String dbVersion) {
        String spName = getSpNameOfDbVersion(dbType);
        setStringValue(spName, dbVersion);
    }

    /**
     * @return 每添加一个库，该方法都要进行修改
     */
    public String getCurrentAllDBVersion() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 7; i++) {
            sb.append(i).append(":").append(getDBVersion(i)).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    // ------------------------- 其他数据库 ------------------------------//

    /**
     * @return 返回应用使用频率数据库
     */
    public String getAppUsedFreqDBName() {
        return APP_OPEN_FREQ_DB_NAME;
    }

    /**
     * @return 返回统计数据库
     */
    public String getAnalytisDBName() {
        return ANALYTICS_DB_NAME;
    }

    /**
     * @return 返回进程提示数据库
     */
    public String getProcessTipDBName() {
        return PROCESS_TIP_DB_NAME;
    }

    public void setStringValue(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putString(key, value);
        SharePreferenceUtil.applyToEditor(editor);
    }
}



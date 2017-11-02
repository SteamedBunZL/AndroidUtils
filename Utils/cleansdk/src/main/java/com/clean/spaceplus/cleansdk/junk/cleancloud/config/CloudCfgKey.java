package com.clean.spaceplus.cleansdk.junk.cleancloud.config;


/*
 * @Mark 为了避免KEY重复了，cloud config的key全部放这里
 */
public class CloudCfgKey {
	
	//main key
	public static final String CLOUD_SWITCH_KEY = "switch";	///对应服务器的switch文件
	public static final String CLOUD_JUNK_SETTINGS_KEY = "junk_settings";	///对应服务器的junk文件
	public static final String CLOUD_JUNK_KEY = "junk_notify"; ///对应服务器的junk文件
	public static final String CLOUD_JUNK_RECOMMEND = "junk_recommend"; ///对应服务器的junk文件
	public static final String CLOUD_PROMOTION_KEY = "promotion_duba"; ///对应服务器的promotion文件,目前作为安全模块的公共配置
	public static final String CLOUD_SECURITY_CHECK_RISKWARE_KEY = "checkout_riskware_hacktool"; ///对应服务器的promotion文件,是否检查riskware和hacktool
	public static final String CLOUD_AUTOSTART_MANAGER_KEY = "auto_start_manager"; ///对应服务器的promotion文件,自启动管理
	public static final String CLOUD_ANR_MONITOR_KEY = "anr_monitor_switch"; ///对应服务器的promotion文件,app anr监控
    public static final String CLOUD_BOOST_KEY = "boost_cfg";
    public static final String CLOUD_BOOST_OLD_USER_ACTIVE_KEY = "boost_cfg_old_user_active";
	public static final String CLOUD_GP_RATE = "gp_rate"; ///对应服务器的junk文件
	public static final String CLOUD_JUNK_STRING_KEY = "string_info";
	public static final String CLOUD_PROCESS_RATING_THRESHOLD_KEY = "process_rating_threshold";//内存清理结果评论引导阀值开关
	public static final String CLOUD_FESTIVAL_SWITCH = "festival_switch";
	public static final String CLOUD_NOTIFICATION_SWITCH = "notification_switch";
	public static final String CLOUD_APP_MANAGER = "app_mgr";
    public static final String CLOUD_PRIVACY_SENSITIVE_DETECT = "privacy_sensitive";
    public static final String CLOUD_SCREEN_SAVER_KEY = "screen_saver";
    public static final String CLOUD_LOGIN = "login";
    public static final String CLOUD_PICKS = "picks";

    // 开屏温度通知栏
    public static final String CLOUD_ON_SCREEN_NOTIFICATION_INTERVAL_KEY = "boost_noti_interval";
    public static final String CLOUD_ON_SCREEN_NOTIFICATION_RECENT_KEY = "boost_noti_recent";
    public static final String CLOUD_ON_SCREEN_NOTIFICATION_CPU_KEY = "boost_noti_cpu";
    public static final String CLOUD_ON_SCREEN_NOTIFICATION_PROCESS_KEY = "boost_noti_process";

	public static final String CLOUD_PROCESS_KEY = "process_settings";
	public static final String CLOUD_CPU_KEY = "cpu_setting";
	public static final String CLOUD_APP_WEATHER = "app_weather";
	public static final String CLOUD_CPU_ABNORMAL_SCENE= "cloud_cpu_abnormal_scene";//cpu异常场景云端化
	public static final String CLOUD_CPU_TEMP = "cpu_temperature";
    public static final String CLOUD_MAIN_ACT_CARD = "main_card";//主界面云端卡片主key配置
    public static final String CLOUD_MAIN_ACT_DRAWER = "main_drawer";//主界面侧边栏主key配置
    public static final String CLOUD_MAIN_ACT_RING = "main_ring";//主界面tips主key配置
	
	public static final String CLOUD_APP_WEATHER_CLOSE_COUNTRY_LIST = "weather_close_countru_list";
	
	public static final String CLOUD_NLTP_KEY = "nltp";
	public static final String CLOUD_NLTP_MAIN_SWITCH = "switch"; 		// 主开关，0为关闭，1为开启功能
	public static final String CLOUD_NLTP_THRESHOLD_PERCENT = "threshold";	// 0-100
	public static final String CLOUD_NLTP_HOURS_BEFORE = "hours_before";	// 小时
	public static final String CLOUD_NLTP_MIN_PKG_MEM_KB	= "min_pkg_mem_kb";	// 进程内存 kb
	public static final String CLOUD_NLTP_SCAN_TIME_INTERVAL_MIN = "scan_time_interval_min";// 扫描时间间隔 分钟
	
	public static final String CLOUD_PERMANENT_NOTIFICATION_KEY = "permanent_notification";
    public static final String CLOUD_PERMANENT_NOTIY_BAIDU_SEARCH_SHOW = "cloud_permanent_notiy_baidu_search_show";
    public static final String CLOUD_PERMANENT_NOTIY_BAIDU_SEARCH_RANDOM = "cloud_permanent_notiy_baidu_search_random";


    public static final String TIME_WALL_SE_DATA = "time_wall_se_data";
	
	//游戏盒子检测项(系统缓存垃圾及cpu)阀值开关
	public static final String CLOUD_GAMEBOX_CHECK_THRESHOLD_KEY = "gamebox_check_cpu_threshold";
	
	public static final String TASK_NOTIFY_MSG  = "task_notify_msg";
	public static final String CONFIG_APPMANAGER = "appmgr_msg";
	public static final String TASK_TEXT = "task_text_msg";

	public static final String LOCAL_DB_SWITCH="local_db_switch";

    public static final String CLEAN_CLOUD_SWITCH = "cleancloud_switch";
    public static final String CLEAN_CLOUD_RESIDUAL_REGULAR_SWITCH="residual_regular_switch";
    public static final String CLEAN_CLOUD_RESIDUAL_LOCAL_RULE_SWITCH="residual_local_rule_switch";
    public static final String CLEAN_CLOUD_RESIDUAL_ANDROID_DATA_RULE_SHOW_SWITCH="residual_local_rule_android_data_show_switch";
    //残留本地规则展示开关,包括android/data 和 android/obb 目录的检出规则
    public static final String CLEAN_CLOUD_RESIDUAL_ANDROID_DIR_RULE_SHOW_PROBABILITY="residual_local_rule_android_dir_show_probability";
    public static final String CLEAN_CLOUD_CACHE_FILE_SWITCH = "cache_file_switch";
    public static final String CLEAN_CLOUD_CACHE_DIR_EMERGENCY_FALSE_SIGN = "cache_dir_emergency_false_sign";
    public static final String CLEAN_CLOUD_RESIDUAL_DIR_EMERGENCY_FALSE_SIGN = "residual_dir_emergency_false_sign";
    public static final String CLEAN_CLOUD_DETECTED_RESULT_UPLOAD_RATE = "detected_result_upload_rate";
    public static final String CLEAN_CLOUD_CACHE_REG_SIGN_TIME_THRESHOLD = "clean_cloud_cache_reg_sign_time_threshold";
  

	public static final String CLOUD_PMW_KEY = "pmw";
	public static final String CLOUD_PMW_MAIN_SWITCH = "switch";

	public static final String APP_MARKET = "app_market";
	
	// 应用漏洞升级apk下载main key
	public static final String APP_HIGH_RISK = "app_vul_package";

    // float service 云端 main key
    public static final String CLOUD_FLOAT_SERVICE = "float_service";
    
    // junk stand result page
    public static final String CLOUD_JUNK_STANDARD = "junk_standard";

    // cfg相关
    public static final String CLOUD_CFG_INFO = "cfg_info";
    
    // 活动
    public static final String CLOUD_ACTIVITY = "activity";

    public static final String DU_SETTING = "du";

    public static final String NOTIFICATION_SETTING = "notification";
    public static final String INTEREST_SETTING = "interest";

    /*-------------autoroot tips begin-----------------*/
    //自动root弹窗相关
    public static final String CLOUD_KEY_MAIN_ROOT_TIP = "AuthView_textShow";
    /*-------------autoroot tips end-------------------*/

	/*------------------- 相似图片 begin -------------------*/
	public static final String CLOUD_SIMILAR_PIC = "similar_pic";
	/*------------------- 相似图片 end -------------------*/

	/*------------------- 图片压缩 begin -------------------*/
	public static final String CLOUD_PHOTO_COMPRESS = "photo_compress";
	/*------------------- 图片压缩 end -------------------*/

    /**
     * 安全浏览器监控
     */
    public static final String CLOUD_SECURITY_NOTIFY = "security";

    //sub key
    /*-------------autoroot tips begin-----------------*/
    //是否显示小手提示
    public static final String CLOUD_KEY_SUB_ROOT_TIP_SMALL_HAND = "root_tip_small_hand";
    //是否跳转到其他猜测授权应用
    public static final String CLOUD_KEY_SUB_ROOT_OTHER_AUTH = "root_other_auth";
    public static final String CLOUD_KEY_SUB_ROOT_START_ROOT_KEEPER = "root_start_root_keeper";
    /*-------------autoroot tips end-------------------*/

    /*-------------inject tips start-------------------*/
    public static final String CLOUD_KEY_SUB_ROOT_SD_MON = "root_sd_mon";
    /*-------------inject tips end---------------------*/
	public static final String THE_FUNCTIONALITY_SWITCH_STRING = "key_background_killing_switch";
	public static final String SWITCH_TASK_NOTIFY_ALL = "key_task_switch_notify_all";
	public static final String SWITCH_TASK_NOTIFY_1 = "key_task_switch_notify_1";
	public static final String SWITCH_TASK_NOTIFY_2 = "key_task_switch_notify_2";
	public static final String CLOUD_KEY = "key_background_killing_time";
	public static final String LABEL_NAME_CACHE_SWITCH = "label_name_cache_switch";
	public static final String STRING_VERSION = "string_version";	//云端文字每次更新都会更新该版本号，目前仅对应CloudCfgManager的getCloudStringOfLocalLanguage使用
	public static final String GP_RATE_US = "gp_rate_us";
	public static final String GP_RATE_US_VERSION = "gp_rate_us_";
	
	public static final String PROCESS_RATING_ROOT_SWITCH = "process_rating_root_switch";//云端非root开启评分引导
	public static final String PROCESS_RATING_SWITCH = "process_rating_switch";//云端引导是否显示总开关
	public static final String PROCESS_RATING_SWITCH_40 = "process_rating_switch_40";//云端引导是否在小于4.0版本中显示总开关
	public static final String PROCESS_CLEAN_SIZE = "process_clean_size_int";//清理内存大小阀值
	public static final String PROCESS_CLEAN_COUNT = "process_clean_count_int";//清理内存数量阀值
	public static final String PROCESS_CLEAN_PERCENT = "process_clean_percent_int";//清理内存百分比阀值
	public static final String PROCESS_CLEAN_PERCENT_90 = "process_clean_percent_int_90";//清理内存百分比阀值(起始内存大于90%)
	public static final String PPOCESS_ABNORMAL_RATING_SWITCH = "process_abnormal_rating_switch";//异常界面评分引导开关
	public static final String PPOCESS_CPU_HIGH = "process_cpu_high";
	public static final String PPOCESS_PID_HIGH = "process_pid_high";
	public static final String PPOCESS_PID_AVG = "process_pid_avg";

	public static final String PROCESS_SHOW_BACK_1TAP_TOAST = "process_show_back_1tap_toast";//主界面退出是否弹出1tap添加提示Toast

    public static final String ABNORMAL_DETECTION_MCC = "abnormal_detection_mcc";

	// PROCESS 异常检测通知栏 KEY
    public static final String ABNORMAL_DETECTION_NOTIFY_KEY = "abnormal_detection_notify_key";   // main key
    public static final String ABNORMAL_DETECTION_NOTIFY_DELAY_HOUR_NORMAL = "abnormal_detection_notify_delay_hour_normal";   // 普通型 通知栏时间间隔，默认6小时
    public static final String ABNORMAL_DETECTION_NOTIFY_DELAY_HOUR_DANGER = "abnormal_detection_notify_delay_hour_danger";   // 严重型 通知栏时间间隔，默认3小时
    public static final String ABNORMAL_DETECTION_NOTIFY_DELAY_HOUR_EXTEND = "abnormal_detection_notify_delay_hour_extend";   // 是否需要延长通知间隔时间
    public static final String ABNORMAL_DETECTION_NOTIFY_SCENE_UNLOCK = "abnormal_detection_notify_scene_unlock";   // 场景开关：解锁
    public static final String ABNORMAL_DETECTION_NOTIFY_SCENE_PHONE = "abnormal_detection_notify_scene_phone";   // 场景开关：接听电话
    public static final String ABNORMAL_DETECTION_NOTIFY_SCENE_FOREGROUND = "abnormal_detection_notify_scene_foreground";   // 场景开关：前台应用
    public static final String ABNORMAL_DETECTION_NOTIFY_FREQSTART_CPU_THRESHOLD = "abnormal_detection_notify_freqstart_cpu_threshold";   // CPU 预设阀值
    public static final String ABNORMAL_DETECTION_NOTIFY_FREQSTART_KILLBACKGROUND_THRESHOLD = "abnormal_detection_notify_freqstart_killbackground_threshold";   // 频繁重启采用 KillBackground 处理阀值
    public static final String ABNORAML_DETECTION_NOTIFY_FREQSTART_WHITE_LIST = "abnormal_detection_notify_freqstart_white_list";   // 频繁重启白名单
    public static final String ABNORAML_DETECTION_NOTIFY_CPU_ABNORMAL = "abnormal_detection_notify_cpu_abnormal";//CPU异常通知栏云端开关

    // 异常排行
    public static final String ABNORMAL_RANKING_STATISTICS_PERIOD_DAY = "abnormal_ranking_statistics_period_day";    // 监控周期(单位：天)，本地默认为 7
    public static final String ABNORMAL_RANKING_NOTIFY_TIME_HOUR      = "abnormal_ranking_notify_time_hour";         // 弹出的本地时间(24小时制)，小时
    public static final String ABNORMAL_RANKING_NOTIFY_TIME_MINUTE    = "abnormal_ranking_notify_time_minute";       // 弹出的本地时间(24小时制)，分钟
    public static final String ABNORMAL_RANKING_TOTAL_THRESHOLD       = "abnormal_ranking_total_threshold";          // 总重启数通知阀值
    public static final String ABNORMAL_RANKING_MCC                   = "abnormal_ranking_mcc";                      // MCC
    public static final String ABNORMAL_RANKING_MCC_RATE              = "abnormal_ranking_mcc_rate";                 // MCC 概率 (0,100]

    //云端内存默认阀值
    public static final String MEMORY_NOTIFY_DEFAULT_SIZE = "memory_notify_default_size";

	public static final String GAME_BOOST_SWITCH = "game_boost_switch";
	public static final String GAME_BOOST_SWITCH_TEXT_A = "game_boost_switch_text_a";//进程界面游戏加速Toast引导开关文案A
	public static final String GAME_BOOST_SWITCH_TEXT_B = "game_boost_switch_text_b";//进程界面游戏加速Toast引导开关文案B
	
	public static final String GAME_BOOST_SHOW_GUIDE_DIALOG = "game_boost_show_guide_dialog";//游戏已加速的引导dialog开关
	public static final String GAME_BOOST_SHOW_GUIDE_TIPS 	= "game_boost_show_guide_tips";//游戏未加速的引导开关
	public static final String GAME_BOOST_TOAST_SHOW        = "game_boost_toast_show";//进出游戏toast提示开关
	public static final String GAMEBOX_UNUSE_NOTIFY_MCC = "gamebox_unuse_notify_mcc";//新生成游戏盒子48小时以上未使用通知栏MCC
	public static final String GAMEBOX_UNUSE_NOTIFY_SHOW	= "gamebox_unuse_notify_show";//新生成游戏盒子48小时以上未使用通知栏
	
	public static final String PROCESS_ADVANCE_RECOMMEND = "process_advance_recommend";//进程结果页推荐内存深度清理

	public static final String DB_RESIDUAL_SWITCH="db_residual_switch";
	public static final String DB_CACHE_SWITCH= "db_cache_switch";

		
	/**内存异常云开关通用section*/
	public static final String MEMORY_EXCEPTION_SWITCH = "memory_exception_switch";
	/**是否异常要用户可感知，本地默认值为true，要关闭云端赋值false*/
	public static final String MEMORY_EXCEPTION_SENSIBLE = "sensible";
	
	public static final String MemoryControlerEx = "mce1";
	public static final String MemoryControlerExMap = "mce2";
	
	
	public static final String KEY_APPMGR_SYSMOVE_NOTIFICATION = "";

    // float tips 内存阈值
    public static final String CLOUD_FLOAT_TIPS_MEMORY = "float_tips_memory";
    // float tips 垃圾阈值
    public static final String CLOUD_FLOAT_TIPS_JUNK = "float_tips_junk";

    public static final String CLOUD_FLOAT_FLASHLIGHT_PLAN = "float_flashlight_plan";

    public static final String CLOUD_FLOAT_FLASHLIGHT_OUSER_SWITCH = "float_flashlight_ouser_switch";

    public static final String CLOUD_FLOAT_DIALOG_TITLE_NEWS_SWITCH = "cloud_float_dialog_title_news_switch";
    public static final String CLOUD_FLOAT_CIRCLE_SIZE_SWITCH = "cloud_float_circle_size_switch";

    public static final String CLOUD_FLOAT_THRESHOLDFLOG_SIZE_SWITCH = "cloud_float_thresholdflog_size_switch"; //流量统计弹出阀值
    public static final String CLOUD_FLOAT_THRESHOLDFLOG_ONCE_SIZE_SWITCH = "cloud_float_thresholdflog_once_size_switch"; //流量统计弹出阀值
    public static final String CLOUD_FLOG_SHOW_INTERVAL_TIME_SWITCH = "cloud_flog_show_interval_time_switch";  //流量统计弹出时间间隔
    public static final String CLOUD_FLOG_SHOW_TOTALCOUNT_ONEDAY_SWITCH = "cloud_flog_show_totalcount_oneday_switch";//流量统计每天弹出总次数
    public static final String CLOUD_FLOG_SHOW_COUNT_ONEAPP_ONEDAY_SWITCH = "cloud_flog_show_count_oneapp_oneday_switch";//流量统计单个app每天弹出总次数
    public static final String CLOUD_FLOG_RECORD_CLOUD_SWITCH = "cloud_flog_record_cloud_switch";//流量统计开关
    public static final String CLOUD_FLOG_USE_CLOUD_SWITCH = "cloud_flog_use_cloud_switch";//流量使用开关

    // float 推荐新闻
    public static final String CLOUD_FLOAT_NEWS_ONE_DAY_MAX = "float_news_1day_max_receive"; // 一天最多收几条
    public static final String CLOUD_FLOAT_NEWS_SEV_DAY_MAX = "float_news_7day_max_receive";   // 一周最多收几条

    // float 流速云端默认开关
    public static final String CLOUD_FLOAT_FLOW_SPEED_SWITCH_DEFAULT_VAL = "float_flow_speed_switch_default_val";   // 流速开关云端默认值

	/** 流量过滤app，在过滤列表中的app不显示流量提醒弹窗 */
	public static final String CLOUD_FLOAT_FLOW_APP_FILTER = "cloud_float_flow_app_filter";

    // junk stand 常态分享
    public static final String CLOUD_JUNK_STAND_SHARE = "junk_stand_share";
    // junk stand 勋章分享
    public static final String CLOUD_JUNK_MEDAL_SHARE = "junk_medal_share";
	
    // cfg版本
    public static final String CLOUD_CFG_VERSION = "cfg_version";
    
    // 举办活动的渠道
    public static final String CLOUD_ACTIVITY_CHANNEL = "activity_channel";
    public static final String CLOUD_ACTIVITY_LOTTERY_CHECKING_INTERVAL = "activity_lottery_checking_interval";
    
	// 安装监控云端key
	public static final String INSTALL_MONITOR_CLOUD_ENABLE = "install_monitor_enable";	//是否允许安装监控

	// 安全 FB 推荐相关
	public static final String SECURITY_TIMEWALL_FB_RECOMMEND           = "security_timewall_fb_recommend";             // 是否允许推荐
	public static final String SECURITY_TIMEWALL_FB_RECOMMEND_TOP       = "security_timewall_fb_recommend_top";         // 是否允许置顶
	public static final String SECURITY_TIMEWALL_FB_RECOMMEND_POS       = "security_timewall_fb_recommend_pos";         // 非置顶卡片推荐位置
	public static final String SECURITY_TIMEWALL_FB_RECOMMEND_NEW_APPLY = "security_timewall_fb_recommend_new_apply";   // 新用户推荐广告策略生效
	public static final String SECURITY_TIMEWALL_BOOST_SCAN           = "security_timewall_boost_scan";             // 是否需要提升速度

    //安全播报 提示订阅对话框 文案
    public static final String SECURITY_BLOG_RSS_DIALOG_CONTENT = "security_blog_rss_dialog_content";
    public static final String SECURITY_BLOG_RSS_DIALOG_CONTENT_VERSION = "security_blog_rss_dialog_content_version";


    /**
     * 安全浏览器监控，黄色网站提示信息
     */
    public static final String SECURITY_NOTIFY_PORN_TITLE_EN = "security_notify_porn_title_en";
    public static final String SECURITY_NOTIFY_PORN_CONTENT_EN = "security_notify_porn_content_en";
    public static final String SECURITY_NOTIFY_PORN_SWITCH = "security_notify_porn_switch";
    public static final String SECURITY_NOTIFY_PORN_ABTEST_INDEX = "security_notify_porn_abtest_index";

    // app启动行为监控上报KEY
	public static final String APP_LAUNCH_CLOUD_ENABLE = "appl_enable";	//是否允许APP行为监控上报
	
	/**盒子推荐位是否直接跳转gp，1为跳，0不跳，默认值0*/
	public static final String BOX_RECOMMEND_DIRECT_GP = "box_recommend_direct_gp";
	public static final String BOX_SINGLE_RECOMMEND_DIRECT_GP = "box_single_recommend_direct_gp";
	public static final String AV_TEST_SWITCH = "av_test_state";
	public static final String BOX_RECOMMEND_LIST_DIRECT_GP = "box_recommend_list_direct_gp";
	
	/** 5.1以前版本的运营开关:(mcc)&(language)&(开关) 和 (mcc)&(开关) */
	public static final String APP_MARKET_NEW_PICKS = "app_market_new_pick";
	/** 是否显示APP MARKET的搜索按钮 :(mcc)&(language)和 (mcc)*/
	public static final String APP_MARKET_SEARCH_POLICY = "app_market_search_policy";
	public static final String APP_MARKET_NEW_SEARCH_POLICY = "app_market_new_search_policy";
	
	public static final String GAME_BOX_BUSSINESS_MCC = "game_box_bussiness_mcc";
	
	/** APP MARKET运营开关:(mcc)&(language)和 (mcc)*/
	public static final String APP_MARKET_OPERATORING_POLICY = "app_market_operatoring_policy";	///<现在废弃，5.3以前的版本生效
	public static final String APP_MARKET_OPERATORING_POLICY_5_3 = "app_market_operatoring_policy_5_3"; ///<5.3版本开始的新策略
	/** APP MARKET 总开关：0为关，1为开*/
	public static final String APP_MARKET_SWITCH = "app_market_switch";
	
	public static final String PROCESS_RECOMMEND_VALUE = "process_recommend_value";
	public static final String FLOAT_AND_NOTIFICATION_RECOMMEND_COUNT = "float_and_notification_recommend_count";//进程界面推荐悬浮窗和通知栏的总次数

	/**云端控制进程清理结果页是否推荐悬浮窗和通知栏 默认为开启*/
	public static final String PROCESS_RECOMMEND_FLOAT_NOTIFICATION_SWITCH = "process_recommend_float_notification_switch";
	/**云端控制垃圾建议清理结果页是否推荐悬浮窗和通知栏 默认为开启*/
	public static final String JUNK_STANDARD_RECOMMEND_FLOAT_NOTIFICATION_SWITCH = "junk_standard_recommend_float_notification_switch";
	/**云端控制垃圾深度清理结果页是否推荐悬浮窗和通知栏 默认为开启*/
	public static final String JUNK_ADVANCED_RECOMMEND_FLOAT_NOTIFICATION_SWITCH = "junk_advanced_recommend_float_notification_switch";
	
	/**进程界面推荐悬浮窗云端文案**/
	public static final String PROCESS_RECOMMENT_FLOAT_TEXT = "process_recommend_float_text";
	/**进程界面通知栏浮窗云端文案**/
	public static final String PROCESS_RECOMMENT_NOTIFICATION_TEXT = "process_recommend_notification_text";
	/**进程CPU异常云端控制显示时间间隔*/
	public static final String PROCESS_CPU_NOTIFICATION_SHOW_TIME = "process_cpu_notification_show_time";	//不同软件时间间隔
	public static final String PROCESS_CPU_NOTIFICATION_SHOW_TIME_GREATER_THRESHOLD = "process_cpu_notification_show_time_greater_threshold";	//相同软件大于CPU阀值时间间隔
	public static final String PROCESS_CPU_NOTIFICATION_SHOW_TIME_LESS_THRESHOLD = "process_cpu_notification_show_time_less_threshold";	//相同软件小于等于CPU阀值时间间隔
	
	/**进程CPU异常云端控制显示总开关*/
	public static final String PROCESS_CPU_NOTIFICATION_SWITCH = "process_cpu_notification_switch";//总开关
	public static final String PROCESS_CPU_NOTIFICATION_SWITCH_WARMING = "process_cpu_notification_switch_warming";//手机温度快速上升开关
	public static final String PROCESS_CPU_NOTIFICATION_SWITCH_RUNNING = "process_cpu_notification_switch_running";//手机运行速度变慢了开关
	public static final String PROCESS_CPU_NOTIFICATION_SWITCH_LAGGING = "process_cpu_notification_switch_lagging";//手机卡的走不动了
	public static final String PROCESS_CPU_NOTIFICATION_SWITCH_STANDBY = "process_cpu_notification_switch_standby";//本次锁屏期间耗电异常
	
	/**异常检测通知栏标题云端文案中英繁*/
	public static final String ABNORMAL_NOTIFY_DANGER_DEFAULT = "abnormal_notify_danger_default";
	public static final String ABNORMAL_NOTIFY_DANGER_UNLOCK = "abnormal_notify_danger_unlock";
	public static final String ABNORMAL_NOTIFY_DANGER_PHONE = "abnormal_notify_danger_phone";
	public static final String ABNORMAL_NOTIFY_DANGER_FOREGROUND = "abnormal_notify_danger_foreground";
	public static final String ABNORMAL_NOTIFY_DANGER_CPU_TEMP = "abnormal_notify_danger_cpu_temp";
	public static final String ABNORMAL_NOTIFY_NORMAL_CPU = "abnormal_notify_normal_cpu";
    public static final String ABNORMAL_TOAST_CPU = "abnormal_toast_cpu";

    public static final String ABNORMAL_NOTIFY_NORMAL_FREQSTART_TITLE = "abnormal_notify_normal_freqstart_title_";
    public static final String ABNORMAL_NOTIFY_DANGER_FREQSTART_TITLE = "abnormal_notify_danger_freqstart_title_";

    public static final String ABNORMAL_NOTIFY_NORMAL_CPU_TITLE = "abnormal_notify_normal_cpu_title_";
    public static final String ABNORMAL_NOTIFY_DANGER_CPU_TITLE = "abnormal_notify_danger_cpu_title_";
    public static final String ABNORMAL_RANKING_TOP_MAX_COUNT  = "abnormal_ranking_top_max_count";
	
	public static final String ABNORMAL_NOTIFY_FREQSTART_NORMAL_TITLE = "abnormal_notify_freqstart_normal_title";
	public static final String ABNORMAL_NOTIFY_FREQSTART_NORMAL_TITLE_APPNAME = "abnormal_notify_freqstart_normal_title_appname";
	
	public static final String ABNORMAL_NOTIFY_FREQSTART_DANGER_TITLE_DEFAULT = "abnormal_notify_freqstart_danger_title_default";
	public static final String ABNORMAL_NOTIFY_FREQSTART_DANGER_TITLE_UNLOCK = "abnormal_notify_freqstart_danger_title_unlock";
	public static final String ABNORMAL_NOTIFY_FREQSTART_DANGER_TITLE_PHONE = "abnormal_notify_freqstart_danger_title_phone";
	public static final String ABNORMAL_NOTIFY_FREQSTART_DANGER_TITLE_FOREGROUND = "abnormal_notify_freqstart_danger_title_foreground";
    public static final String ABNORMAL_TOAST_FREQSTART_ONE_APP = "abnormal_toast_freqstart_one";
    public static final String ABNORMAL_TOAST_FREQSTART_MULTI_APP = "abnormal_toast_freqstart_multi";

    public static final String ABNORMAL_TOAST_SHOW_TIME_S = "abnormal_toast_show_time_s";
    public static final String ABNORMAL_LOW_MEMORY_TOAST_DESC = "low_mem_toast_desc";
    public static final String ABNORMAL_LOW_MEMORY_TOAST_SHOW_RATE = "low_mem_toast_show_rate";
    public static final String ABNORMAL_TOAST_SHOW_RATE = "abnormal_toast_show_rate";

    public static final String ABNORMAL_LOW_MEMORY_NOTIFICATION_SHOW_PERIOD = "low_mem_notification_show_period";
    public static final String ABNORMAL_LOW_MEMORY_NOTIFICATION_NEW_TEXT_SHOW_PERIOD = "low_mem_notification_new_text_show_period";
	
	/**进程CPU异常云端文案中英繁*/
	public static final String PROCESS_CPU_NOTIFICATION_LAGGING_TEXT = "cpu_lagging_text";
	public static final String PROCESS_CPU_NOTIFICATION_WARMING_TEXT = "cpu_warming_text";
	public static final String PROCESS_CPU_NOTIFICATION_BATTERY_TEXT = "cpu_battery_text";
	public static final String PROCESS_CPU_NOTIFICATION_RUNNING_TEXT = "cpu_running_text";
	public static final String PROCESS_CPU_NOTIFICATION_STANDBY_TEXT = "cpu_standby_text";
	
	//cpu异常默认场景云端化
	public static final String CPU_ITEM_INFO_BATTERY = "cpu_item_info_battery";//耗电
	public static final String CPU_ITEM_INFO_OVERHEAT = "cpu_item_info_overheat";//温度
	public static final String CPU_TEMP_ABNORMAL_EXTEND_PREFIX = "cpu_temp_abnormal_";
	// 主动打开游戏盒子CPU异常温度阀值
	public static final String GAMEBOX_CPU_TEMP_ABNORMAL_EXTEND_PREFIX = "game_cpu_temp_abnormal_";
	// v5.8.2 游戏过程CPU温度异常阀值
	public static final String GAME_PROCESS_CPU_TEMP_ABNORMAL_EXTEND_PREFIX = "game_process_cpu_temp_abnormal_";

    // 游戏盒子process 弹框
    public static final String GAME_BOX_PROCESS_DIAOLOG = "game_pro_dialog";

    // 控制是否开启box动画
    public static final String GAME_BOX_GAME_START_ANIMATION = "game_box_start_animation_r1";

    // 是否有弹框
    public static final String GAME_BOX_FIX_ICON_DIALOG = "game_box_fix_icon_dialog";

    //cpu异常显示文案的标准>50% <50%
	public static final String PROCESS_CPU_SHOW_TEXT_STANDARD  = "cpu_show_text_standard";
    public static final String PROCESS_CPU_SHOW_TEMP_THRESHOLD = "process_cpu_show_temp_threshold";
    //CPU toast显示阀值
	public static final String PROCESS_CPU_TOAST_THRESHOLD = "process_cpu_toast_threshold";
	//CPU toast显示的场景ID
	public static final String PROCESS_CPU_TOAST_ENV_ID = "process_cpu_toast_env_id";
	
	/**CPU异常结果页item开关*/
	public static final String PROCESS_CPU_RESULT_SWITCH = "cpu_item_switch";
//	public static final String PROCESS_CPU_RESULT_SWITCH_PREFIX = "cpu_item_switch_";
	/*584版本以后：CPU结果页卡片控制开关*/
	public static final String PROCESS_CPU_RESULT_NEW_SWITCH_PREFIX = "cpu_item_new_switch_";

	
	public static final String PROCESS_CPU_INGORE_TIME = "cpu_ingore_time";
	
	
	/**建议清理界面推荐悬浮窗云端文案**/
	public static final String JUNK_STANDARD_RECOMMEND_FLOAT_TEXT = "junk_standard_recommend_float_text";
	/**建议清理界面通知栏浮窗云端文案**/
	public static final String JUNK_STANDARD_RECOMMEND_NOTIFICATION_TEXT = "junk_standard_recommend_notification_text";
	
	/**深度清理界面推荐悬浮窗云端文案**/
	public static final String JUNK_ADVANCED_RECOMMEND_FLOAT_TEXT = "junk_advanced_recommend_float_text";
	/**深度清理通知栏浮窗云端文案**/
	public static final String JUNK_ADVANCED_RECOMMEND_NOTIFICATION_TEXT = "junk_advanced_recommend_notification_text";
	
	/** 常驻通知栏子key */
	public static final String PERMANENT_NOTIFICATION_STYLE = "permanent_notification_style";
	
	
	public static final String GAMEBOX_CYCLE_SHOW = "gamebox_cycle_show";
	
	/** 游戏盒子检测CPU开关 默认开启 */
	public static final String GAMEBOX_CHECK_CPU = "gamebox_check_cpu";
	public static final String GAMEBOX_CHECK_MEM = "gamebox_check_mem";
	public static final String GAMEBOX_CHECK_JUNK = "gamebox_check_junk";
    public static final String GAMEBOX_CHECK_AUTOSTART = "gamebox_check_autostart";

    // 游戏盒子process页面dialog弹出国家控制
    public static final String GAMEBOX_PROCESS_DIALOG = "gamebox_pro_dia";
    public static final String GAMEBOX_SUPPORT_ALL_COUNTRY = "game_support_all_country";
    public static final String GAMEBOX_PROCESS_COUNTRY ="gamebox_support_country";
    public static final String GAMEBOX_FORCE_STYLE ="gamebox_force_style";

	/**cpu过高阀值 */
	public static final String GAMEBOX_CHECK_CPU_PERCENT_INT = "gamebox_check_cpu_percent";
	/** 缓存垃圾最大最小阀值(MB)*/
	public static final String GAMEBOX_SYS_CACHE_MIN_INT = "gamebox_sys_cache_min";
	public static final String GAMEBOX_SYS_CACHE_MAX_INT = "gamebox_sys_cache_max";
	public static final String APP_MGR_BATTERYDOCTOR_PERCENT = "app_mgr_bd";
	
	/** 内部广告推CMB，最小内存限制 */
	public static final String APP_MGR_MIN_PHONE_MEMORY = "app_mgr_min_phone_memory";
	
	/** 软管广告总开关 */
	public static final String APP_MGR_SHOULD_SHOW_AD = "app_mgr_should_show_ad";
	
	/** 软管pick和facebook 广告是否置顶 */
//    public static final String JUNK_STANDARD_PICK_POSITION = "junk_standard_pick_position";
//    public static final String JUNK_STANDARD_FACEBOOK_POSITION = "junk_standard_facebook_position";
//    public static final String JUNK_STANDARD_GDT_POSITION = "junk_standard_gdt_position";
//    public static final String JUNK_ADVANCE_PICK_POSITION = "junk_advance_pick_position";
//    public static final String JUNK_ADVANCE_FACEBOOK_POSITION = "junk_advance_facebook_position";
//    public static final String JUNK_ADVANCE_GDT_POSITION = "junk_advance_gdt_position";
//    public static final String PROCESS_STANDARD_PICK_POSITION = "process_standard_pick_position";
//    public static final String PROCESS_STANDARD_FACEBOOK_POSITION = "process_standard_facebook_position";
//    public static final String PROCESS_STANDARD_GDT_POSITION = "process_standard_gdt_position";
//    public static final String CPU_NORMAL_PICK_POSITION = "cpu_normal_pick_position";
//    public static final String CPU_NORMAL_FACEBOOK_POSITION = "cpu_normal_facebook_position";
//    public static final String CPU_NORMAL_GDT_POSITION = "cpu_normal_gdt_position";
//    public static final String CPU_ABNORMAL_PICK_POSITION = "cpu_abnormal_pick_position";
//    public static final String CPU_ABNORMAL_FACEBOOK_POSITION = "cpu_abnormal_facebook_position";
//    public static final String CPU_ABNORMAL_GDT_POSITION = "cpu_abnormal_gdt_position";
//    public static final String MAIN_PICK_POSITION = "main_pick_position";
//    public static final String MAIN_FACEBOOK_POSITION = "main_facebook_position";
//    public static final String MAIN_GDT_POSITION = "main_gdt_position";
    public static final String PRELOAD_AD_SWITCHER = "preload_ad_switcher";
    public static final String PRELOAD_AD = "preload_ad";
    public static final String PRELOAD_FB_SWITCHER = "preload_fb_switcher";
    public static final String PRELOAD_FB = "preload_fb_3g_switcher";
    public static final String FB_SHOW_TYPE = "facebook_show_type";

    /** pick商业广告内部内容显示时间间隔 */
    public static final String APP_MGR_PICK_INTERVAL = "app_mgr_pick_interval";
    /** 商业广告忽略间隔 */
    public static final String APP_MGR_IGNORE_INTERVAL = "app_mgr_ignore_interval";
	/** 安全页推CMB优先级顺序 */
    public static final String APP_MGR_SECURITY_CMB_PRIORITY = "app_mgr_security_cmb_priority";

	public static final String APP_MGR_RECOMMEND_G_U = "app_mgr_recommend_g_u";

	/** 控制电池医生是否显示的最小和最大电量值 */
	public static final String APP_MGR_BATTERYDOCTOR_LEVEL_MIN = "app_mgr_bd_level_min";
	public static final String APP_MGR_BATTERYDOCTOR_LEVEL_MAX = "app_mgr_bd_level_max";

	/** 猎豹浏览器详情页文案1 */
	public static final String APP_MGR_BROWSER_DETAILS_FAKE = "app_mgr_browser_details_fake";
	/** 猎豹浏览器详情页文案2 */
	public static final String APP_MGR_BROWSER_DETAILS_REAL = "app_mgr_browser_details_real";

	/** 控制通知栏推猎豹垃圾的最小值 */
	public static final String APP_MGR_BROWSER_JUNK_SIZE_MIN = "app_mgr_browser_junk_size_min";

    /** 控制top app打开显示清理垃圾 */
    public static final String APP_MGR_TOP_APP_JUNK = "app_mgr_top_app_junk";

    public static final String APP_MGR_ONE_TOP_APP_SHOW_MORE_THAN_ONCE = "app_mgr_one_top_app_show_more_than_once";

	/** 竞品浏览器内存最小值 */
	public static final String APP_MGR_BROWSER_MEMORY_SIZE_MIN = "app_mgr_browser_memory_size_min";

	/** 悬浮窗推猎豹黑名单 */
	public static final String APP_MGR_BROWSER_FLOAT_BLACK = "app_mgr_browser_float_black";

	/** 悬浮窗推猎豹白名单 */
	public static final String APP_MGR_BROWSER_FLOAT_WHITE = "app_mgr_browser_float_white";

	/** 猎豹浏览器推荐title 国际 */
	public static final String APP_MGR_BROWSER_AD_RECOMMEND_TITLE = "app_mgr_browser_ad_recommend_title";
	/** 猎豹浏览器推荐desc 国际*/
	public static final String APP_MGR_BROWSER_AD_RECOMMEND_DESC = "app_mgr_browser_ad_recommend_desc";

	/** 猎豹浏览器同类浏览器推荐 title 国内*/
	public static final String APP_MGR_BROWSER_AD_RECOMMEND_BROWSER_TITLE_CN = "app_mgr_browser_ad_recommend_browser_title_cn";
	/** 猎豹浏览器同类浏览器推荐desc 国内*/
	public static final String APP_MGR_BROWSER_AD_RECOMMEND_BROWSER_DESC_CN = "app_mgr_browser_ad_recommend_browser_desc_cn";

	/** 猎豹浏览器同类视频推荐title 国内*/
	public static final String APP_MGR_BROWSER_AD_RECOMMEND_VIDEO_TITLE_CN = "app_mgr_browser_ad_recommend_video_title_cn";
	/** 猎豹浏览器同类视频推荐desc 国内*/
	public static final String APP_MGR_BROWSER_AD_RECOMMEND_VIDEO_DESC_CN = "app_mgr_browser_ad_recommend_video_desc_cn";

	/*app推广*/
	public static final String APP_RECOMMEND_DATA_KEY = "app_recommend_data_key";
	public static final String APP_RECOMMEND_DATA_DATA = "app_recommend_data_data";

	/// 垃圾清理扫描相关的开关定义
	public static final String JUNK_SCAN_FLAG_KEY = "junk_scan_flag_key";

    /**592建议清理中的照片回收*/
    public static final String JUNK_CLEAN_FLAG_KEY = "junk_clean_flag_key";
    public static final String JUNK_STD_RECYCLE_SIZE = "junk_std_recycle_size";
    public static final String JUNK_STD_RECYCLE_SWITCH = "junk_std_recycle_switch";

    /**595建议清理中转入后台清理*/
    public static final String JUNK_STD_IS_CLEAN_TIME_LIMIT_EFFECT = "junk_std_is_clean_time_limit_effect";
    public static final String JUNK_STD_MAX_CLEAN_TIME = "junk_std_max_clean_time";

    /* 文件级别删除  开关和缓存客户端开关控制 bool型 */
    public static final String JUNK_SCAN_SDCACHE_FILETYPE = "junk_scan_sdcache_filetype";
    public static final String JUNK_SCAN_SDCACHE_FILETYPE_CUSTOM = "junk_scan_sdcache_filetype_custom"; //5.8.8
    public static final String JUNK_SCAN_SDCACHE_FILETYPE_CUSTOM589 = "junk_scan_sdcache_filetype_custom589"; //5.8.9
    public static final String JUNK_SCAN_SDCACHE_FILETYPE590 = "junk_scan_sdcache_filetype590";
    public static final String JUNK_SCAN_SDCACHE_FILETYPE_CUSTOM590 = "junk_scan_sdcache_filetype_custom590"; //5.8.9

    /*後台扫描sdcard文件加入*/
    public static final String JUNK_SCAN_BG_MEDIA_STORE_SCAN_EN = "junk_scan_bg_media_store_scan_en";
    public static final String JUNK_SCAN_BG_MEDIA_STORE_SCAN_TIME = "junk_scan_bg_media_store_scan_time";
    public static final String JUNK_SCAN_BG_PHOTO_COLLECT = "junk_scan_bg_photo_collect";

    /*建议清理扫描出的垃圾最大为12G 开关控制*/
    public static final String JUNK_STD_SCAN_MAX_SIZE = "junk_std_scan_max_size";

    /*Root Cache 开关控制*/
    public static final String JUNK_SCAN_ROOT_CACHE_SCAN = "junk_scan_root_cache_scan";

	/// 第二张卡残留扫描开关
	public static final String JUNK_2ND_SD_ALO_RUBBISH = "junk_2nd_sd_alo_rubbish";	//< 用于关闭最初发布的版本功能
	public static final String JUNK_2ND_SD_ALO_RUBBISH2 = "junk_2nd_sd_alo_rubbish2";
	public static final String JUNK_2ND_SD_ALO_RUBBISH3 = "junk_2nd_sd_alo_rubbish3";

	/// 第二张卡cache扫描开关
	public static final String JUNK_2ND_SD_CACHE = "junk_2nd_sd_cache";

    /* 从5.9.0 控制临时文件使用MediaStore扫描的开关 bool型 TRUE为开启，false为关闭*/
    public static final String JUNK_TEMPS_SCAN_ABTEST	    = "junk_temps_scan_abtest";	//

    /* 从5.9.0 检查文件是否为稀疏文件的临界值。long型 0为关闭检查。单位：字节*/
    public static final String JUNK_CHK_SPARSEFILE_LIMIT_SIZE	    = "junk_chk_sparsefile_limit_size";

	/* 垃圾清理扫描无用缩列图开关 bool型 */
	public static final String JUNK_SCAN_OBSOLETE_THUMBNAIL_FLAG = "junk_scan_obsolete_thumbnail_flag";

	// 建议清理扫描超时值(单位ms) int型
	public static final String JUNK_STD_SCAN_TIME_OUT = "junk_std_scan_time_out";

    // 5.9.5添加，建议清理扫描是否扫描截屏图片压缩
    public static final String JUNK_STD_SCAN_SCREEN_SHOTS_COMPRESS = "junk_std_scan_screen_shots_compress";

    // 自定义深度转建议缓存功能开关
    public static final String JUNK_ADV2STD_SWITCH = "junk_adv2std_switch";

	// 是否三星4.0以上机型的预装扫描都放开，本地默认值是true。要关闭的话云端设置false。
	public static final String PROCESS_ADVANCE_BOOST_ALL_SS = "pabas";

    //垃圾服务扫描控制
    public static final String JUNK_SCAN_SERVICE_FLAG_KEY = "junk_scan_service_flag_key";

    //锁屏後多少时间开始扫描
    public static final String JUNK_SCAN_SCREENOFF_SCHEDULE_TIME = "junk_scan_screenoff_schedule_time";

    public static final String JUNK_SCAN_DOWNLOAD_MANAGER_NOTIFY_TIME = "junk_scan_download_manager_notify_time";

    //照片最小size
    public static final String JUNK_SCAN_PHOTO_COMPRESS_MIN_SIZE = "junk_scan_photo_compress_min_size";
    //截图最小size
    public static final String JUNK_SCAN_SCREENSHOT_COMPRESS_MIN_SIZE = "junk_scan_screenshot_compress_min_size";
    //是否搜集压坏的图
    public static final String JUNK_SCAN_COMPRESS_DAMAGE_COLLECT_SWITCH = "junk_scan_compress_damage_collect_switch";
    //top应用弹框的样式
    public static final String JUNK_TOP_DIALOG_LAYOUT_TYPE = "junk_top_dialog_layout_type";
    //top弹框时间间隔
    public static final String JUNK_TOP_DIALOG_INTERVAL_TIME = "junk_top_dialog_interval_time";
    //top弹框最小size限制
    public static final String JUNK_TOP_DIALOG_MIN_SIZE = "junk_top_dialog_min_size";
    //top弹框增量size限制
    public static final String JUNK_TOP_DIALOG_INCREMENT_SIZE = "junk_top_dialog_increment_size";

    //Video Offline 扫描结果 expired time
    public static final String JUNK_SCAN_VIDEO_RESULT_EXPIRED_TIME = "junk_scan_video_result_expired_time";

    //Video Offline 扫描结果 未处理 expired time
    public static final String JUNK_SCAN_VIDEO_RESULT_NOT_OPER_EXPIRED_TIME = "junk_scan_video_result_not_oper_expired_time";

    //Video Offline 扫描弹窗大小
    public static final String JUNK_SCAN_VIDEO_RESULT_POP_SIZE = "junk_scan_video_result_pop_size";

    public static final String JUNK_SCAN_VIDEO_CARD_NEED_SHOW = "junk_scan_video_card_need_show";

    //competitor package feature list. JSON String
    public static final String JUNK_SCAN_COMPETITOR_PKG_FEAT_LIST = "junk_competitor_pkg_feat_list";

	/*  软管批量推荐开关: bool型, 是个功能开关 */
	public static final String N_MARKET_UBR_MAXIUM = "n_market_ubr_maxium";

	/* 游戏咨询/攻略页广告卡片位置:  bool型, 为true置腚, 否则沉底 */
	public static final String B_MARKET_GAMEINFO_AD1_TOP = "b_market_gameinfo_ad1_top";

	/**游戏盒子推荐游戏滑动两次过期开关*/
	public static final String GAMGEBOX_RECOMMEND_DATA_EXPIRE = "gamebox_recommend_data_expire";

	//卸载页面推荐游戏盒子的云端  子key
	public static final String APP_MGR_RECOMMEND_GAME_BOOST_TITLE = "app_mgr_recommend_game_boost_title";
	public static final String APP_MGR_RECOMMEND_GAME_BOOST_DESC = "app_mgr_recommend_game_boost_desc";

    /**************** 游戏盒子相关的云端开关**********************/
	/*游戏过程中单个APP CPU阀值*/
	public static final String PROCESS_IN_GAME_CPU_HIGH = "process_in_game_cpu_high";
	/*游戏过程中系统CPU阈值*/
	public static final String GAME_PROCESS_SYSTEM_CPU_HIGH = "game_process_system_cpu_high";
	/*游戏过程中内存不足阀值*/
	public static final String MEM_LOW_IN_GAME_R1 = "mem_low_in_game_r1";
    //游戏过程电量阀值，大于该值才有可能显示省电被动弹泡
    public static final String POWER_LOW_IN_GAME = "power_low_in_game";
    /*退出游戏提示省电模式开启次数阀值*/
    public static final String EXIT_GAME_POWER_SAVE_COUNT_R1 = "exit_game_power_save_r1";
    // 一天之内游戏场景异常被动弹框最多的弹出次数，默认是1次
    public static final String EXIT_GAME_PROMPT_LIMIT_IN_ONE_DAY_R1 = "exit_game_prompt_limit_in_one_day_r1";
    // 游戏退出场景，用户忽略处理次数的阈值X, 连续X次不操作，间隔N天再检出
    public static final String EXIT_GAME_PROBLEM_UNHANDLE_THRESHOLD_R3 = "exit_game_problem_unhandle_threshold_r3";
    // 游戏退出场景，用户忽略处理的间隔天数N，连续X次不操作，间隔N天再检出
    public static final String EXIT_GAME_UNHANDLE_DAY_INTERVAL = "exit_game_unhandle_day_interval";
    // 两次被动泡弹出的最少相隔天数，默认为0天，即每天都可以弹
    public static final String EXIT_GAME_PROMPT_DAY_INTERVAL = "exit_game_prompt_day_interval";
    //上报安装应用列表
    public static final String GAMEBOX_REPORT_INSTALL_APP_LIST = "gamebox_report_install_app_list";

    // 游戏盒子内存场景被动泡游戏时长阈值，单位：分钟
    public static final String MEM_SCENE_PLAY_TIME_THRESHOLD = "mem_scene_play_time_threhold";
    // 游戏盒子省电模式场景被动泡游戏时长阈值，单位：分钟
    public static final String POWER_SCENE_PLAY_TIME_THRESHOLD = "power_scene_play_time_threhold";
    // 游戏盒子游戏时长场景被动泡游戏时长阈值，单位：分钟
    public static final String GENERAL_SCENE_PLAY_TIME_THRESHOLD_MAX = "general_scene_play_time_threhold_max";
    // 游戏盒子游戏时长场景被动泡游戏时长阈值，单位：分钟
    public static final String GENERAL_SCENE_PLAY_TIME_THRESHOLD_MIN= "general_scene_play_time_threhold_min";


    // 被动场景检出时间间隔，单位小时， 默认2
    public static final String GAME_PROBLEM_SCENE_CHECK_INTERVAL = "game_problem_scene_check_interval";

    //盒子内存不足引导弹泡至少玩游戏时间 ,594改r1
    public static final String GAME_PROBLEM_MEM_GUIDE_PLAY_GAME_TIME_R1 = "game_problem_mem_guide_play_game_time_r1";

    //盒子问题弹泡是否需要跟cm通知栏弹泡做避规
    public static final String GAMEBOX_PROBLEM_DIALOG_AVOID_CM_NOTIFI = "gamebox_problem_dialog_avoid_cm_notifi";
    /**退出游戏时悬浮窗提示使用游戏盒子开关*/
    public static final String SHOW_USE_GAMEBOX_TIPS_BY_FLOAT_DIALOG = "show_use_gamebox_tips_by_float_dialog";
    public static final String SHOW_USE_GAMEBOX_TIPS_BY_FLOAT_DIALOG_MCC = "show_use_gamebox_tips_by_float_dialog_mcc";

    //每天提示最大次数，默认1次
    public static final String POP_MAX_PER_DAY_KEY = "game_tip_max_per_day";
    //一个包名被推荐提示最大的次数，默认3次
    public static final String POP_MAX_SHOW_TIMES_KEY = "game_tip_max_show_times";
    //同一款游戏提示间隔时间，默认72小时（单位s）
    public static final String POP_SHOW_INTERNAL_KEY = "game_tip_show_internal";
    //单条记录数据库过期时间，默认8小时（单位s）
    public static final String INVALID_DATA_INTERNAL_KEY = "game_tip_internal_time";
    // 快捷通知栏游戏盒子入口展示的策略-新用户游戏个数阈值
    public static final String GAME_COUNT_THRESHOLD_FOR_NEW_USERS = "game_count_threshold_for_new_users";
    // 快捷通知栏游戏盒子入口展示的策略-老用户游戏个数阈值
    public static final String GAME_COUNT_THRESHOLD_FOR_OLD_USERS = "game_count_threshold_for_old_users";
    // 快捷通知栏对游戏盒子的拉活方案的总频次（每天），默认次数：1
    public static final String MAX_NUM_PER_DAY_GAMEBOX_GUIDE_AT_PERMANENT_NOTIFICATION = "max_num_per_day_gamebox_guide_at_permanent_notification";
    // 切换开关，支持在“从非盒子启动游戏且有悬浮窗”、“从非盒子启动游戏且开启了快捷通知栏且有游戏盒子图标”之间切换，默认值：1 导向悬浮窗， 其它值（不等于1）：导向快捷通知栏
    public static final String SHOW_GAMEBOX_GUIDE_AT_PERMANENT_NOTIFICATION_OR_FLOAT_WINDOW = "show_gamebox_guide_at_permanent_notification_or_float_window";
    // 通过游戏场景引导开启快捷通知栏+盒子入口通知的开启的mcc国家。 值为“all”时:全部国家都打开，简化的云端设置
    public static final String OPEN_PERMANENT_NOTIFICATION_AND_GAMEBOX_GUIDE_MCC = "open_permanent_notification_and_gamebox_guide_mcc";
    // 通过游戏场景引导开启快捷通知栏+盒子入口通知的弹出最大次数，默认值是1
    public static final String MAX_NUM_OF_OPEN_PERMANENT_NOTIFICATION_AND_GAMEBOX_GUIDE = "max_num_of_open_permanent_notification_and_gamebox_guide";
    // 游戏用户的最小游戏个数，默认值为3 —— 用于已开启快捷通知栏的游戏用户，引导开启游戏盒子入口的“重度游戏用户”判断
    public static final String MIN_GAME_COUNT_OF_GAME_USER = "min_game_count_of_game_user";
    // 活跃的游戏用户：至今往前7天内有x天启动游戏,默认值3天  —— 用于已开启快捷通知栏的游戏用户，引导开启游戏盒子入口的“重度游戏用户”判断
    public static final String PLAY_GAME_COUNT_IN_A_WEEK = "play_game_count_in_a_week";
    // 游戏用户的最小游戏个数，默认值为6  —— 用于未开启快捷通知栏的游戏用户，引导开启快捷通知栏+盒子入口
    public static final String MIN_GAME_COUNT_OF_GAME_USER_OF_OPEN_PERMANENT_NOTIFICATION_AND_GAMEBOX_GUIDE = "min_game_count_of_game_user_of_open_permanent_notification_and_gamebox_guide";
    // 活跃的游戏用户：至今往前7天内有x天启动游戏,默认值3天  —— 用于未开启快捷通知栏的游戏用户，引导开启快捷通知栏+盒子入口
    public static final String PLAY_GAME_COUNT_IN_A_WEEK_OF_OPEN_PERMANENT_NOTIFICATION_AND_GAMEBOX_GUIDE = "play_game_count_in_a_week_of_open_permanent_notification_and_gamebox_guide";
    // 游戏盒子:检测到新游安装后,控制显示悬浮窗与否   0 disable, 1 enable
    public static final String GAMEBOX_SHOW_NOTIFICATION_DIALOG_AFTER_INSTALL_GAME = "gamebox_show_notification_dialog_after_install_game";
    // 游戏盒子:检测到新游安装后,控制盒子中图标是否显示红点  0 disable, 1 enable
    public static final String GAMEBOX_SHOW_RED_POINT_AFTER_INSTALL_GAME = "gamebox_show_red_point_after_install_game";
    // 游戏盒子:控制游戏盒子下拉引导,新用户首次是否弹出  0=disable, 1=enable default=0
    public static final String GAMEBOX_JUMP_ON_NEW_USER = "gamebox_jump_on_new_user";
    // 游戏盒子:控制游戏盒子下拉引导的弹出序列 0=disable, 1=enable default=2,4
    public static final String GAMEBOX_JUMP_ON_SEQUENCE="gamebox_jump_on_sequence";
    // 游戏仍在后台运行的通知栏是否展示
    public static final String GAME_CLEAN_NOTIFICATION_ENALBED = "game_clean_notification_enabled";

    /**------------------Game Boost V5.9.3支持拉活/轻游戏推荐------------------*/
    // 桌面下沉浮层：连续N次不点击次数
    public static final String FLOAT_WINDOW_NOT_CLICK_NUMBER = "float_window_not_click_number";
    /**------------------Game Boost V5.9.3支持拉活/轻游戏推荐------------------*/

    // CM首页头部卡片推荐游戏盒子的游戏个数限制
    public static final String GAME_COUNT_LIMIT_FOR_RECOMMEND_IN_CM_HEAD_CARD = "game_count_limit_for_recommend_in_cm_head_card";

    // 禁用与游戏盒子sdk的规避逻辑的CM版本号，小于等于这个版本号的CM，规避逻辑不生效，回复原状
    public static final String DISABLE_GAMEBOX_SDK_EVASION_VERSION = "disable_gamebox_sdk_evasion_version";

    //请求间隔时间，默认8小时（单位s）
    public static final String GAME_BOX_REQUEST_INTERNAL_KEY = "game_box_request_internal";

    // 游戏盒子的html卡片跳转到webview时，使用的浏览器类型。 1、CM的webview窗口   2、系统中的浏览器。  本地默认为1
    public static final String GAMEBOX_HTML_WEBVIEW_OPEN_TYPE = "gamebox_html_webview_open_type";
    // 被动泡入口的内容分发，是否新用户区分，1、区分；2、不区分
    public static final String NEW_USER_IS_DIFFERENT = "new_user_is_different";
    /**
     * 被动泡入口的内容分发，“商业流程+产品”和“产品”两个分流的概率控制。 客户端根据android id对100求余得到本地概率值。 云端设置值：0至100
     * 说明：百分百的概率，拆分给“商业流程+产品”和“产品”，后台配置的概率是针对“商业流程+产品”的，剩余的100-“商业流程+产品”的概率 = “产品”的概率
     */
    public static final String PROBABILITY_OF_BUSINESS_AND_PRODUCT = "probability_of_business_and_product";
    //盒子单用户的内容分发配置，针对弹出控制，单用户X天show Y次卡片，配置方式："X||Y"，用||隔开天数和次数，例如"1天1次"的配置为：1||1
    public static final String GAME_BOX_SINGLE_USER_APP_PUSH_X_DAY_SHOW_Y_TIME = "game_box_single_user_app_push_x_day_show_y_time";
    //盒子单用户的内容分发配置，针对内容频控，支持配置同一个包名如果连续展现X次后，需间隔Y小时再展现，配置方式："X||Y"，用||隔开连续展现次数和间隔小时，例如"连续展现10次后需间隔24小时再展现"的配置为：10||24
    public static final String GAME_BOX_SINGLE_USER_APP_PUSH_X_TIME_SHOW_Y_HOUR_INTERVAL = "game_box_single_user_app_push_show_x_time_y_hour_interval";
    //盒子html5栏目的内容频控，支持配置同一个包名如果连续展现X次后，需间隔Y小时再展现，配置方式："X||Y"，用||隔开连续展现次数和间隔小时，例如"连续展现3次后需间隔24小时再展现"的配置为：3||24
    public static final String GAME_BOX_HTML5_X_TIME_SHOW_Y_HOUR_INTERVAL = "game_box_html5_x_time_show_y_hour_interval";
    //是否在桌面启动游戏时预先加载单用户的内容分发数据， 1、开启桌面启动游戏时的数据预加载，同时关闭打开被动泡时的数据预加载;  0、关闭桌面启动游戏时的数据预加载，同时打开被动泡时的数据预加载, 本地默认为1
    public static final String PRE_LOAD_APPS_AT_LAUNCHER_GAME_START = "pre_load_apps_at_launcher_game_start";

    // gameboard下拉超过8卡片，控制创建图标, 0 创建  1 不要创建
    public static final String GAME_BOARD_DONT_CREATE_SHORTCUT_AFTER_SCROLL_8 = "gameboard_dont_create_shortcut_after_scroll_8";

    /**云端控制webview的js接口开关 0为开启, 1为关闭, 默认为0 */
    public static final String GAMEBOX_JS_INTERNAL_KEY = "gamebox_js_key";

    // 游戏内存数据上报间隔
    public static final String GAME_MEM_REPORT_INTERVAL = "game_mem_report_interval";

    // 游戏内存数据上报开关
    public static final String GAME_MEM_REPORT_SWITCH = "game_mem_report_switch";

    // 游戏盒子被动场景，场景匹配配置串
    public static final String GAME_PROBLEM_SCENE_MATCHER_CONFIG_R1 = "game_problem_scene_matcher_config_r1";

    //内容小豹子总开关
    public static final String GAMEBOX_CONTENTCHEETAH_SWITCH = "gamebox_contentcheetah_switch";
    //内容小豹子概率开关
    public static final String GAMEBOX_CONTENTCHEETAH_PROBABILITY = "gamebox_contentcheetah_probability";
    //内容小豹子游戏数量最小值
    public static final String GAMEBOX_CONTENTCHEETAH_GAME_MIN = "gamebox_contentcheetah_game_less";
    //内容小豹子没有下载天数最大值
    public static final String GAMEBOX_CONTENTCHEETAH_NODOWNLOAD_MAX = "gamebox_contentcheetah_nodownload_max";
    //内容小豹子第一阶段延迟
    public static final String GAMEBOX_CONTENTCHEETAH_DELAY_1ST = "gamebox_contentcheetah_delay_1st";
    //内容小豹子第二阶段延迟
    public static final String GAMEBOX_CONTENTCHEETAH_DELAY_2ND = "gamebox_contentcheetah_delay_2nd";
    //升级豹子的文案
    public static final String GAMEBOX_CHEETAH_GP_UPDATE = "gamebox_cheetah_gp_update";
    //被动豹子的文案
    public static final String GAMEBOX_CHEETAH_EXIT = "gamebox_cheetah_exit";
    //内容小豹子更新类型
    public static final String GAME_CHEETAH_UPDATE_TYPE_ENABLE = "game_cheetah_update_type_enable";
    //内容小豹子内容matcher云端配置
    public static final String GAME_CHEETAH_MATCHER_CONFIG = "game_cheetah_matcher_config";
    //内容小豹子一天显示最大数量
    public static final String GAME_CHEETAH_SHOW_ONEDAY_MAX = "game_cheetah_show_oneday_max";
    //被动框火箭结束后结果卡片出的第一段文案
    public static final String GAMEBOX_CHEETAH_EXIT_RESULT_ONE_LINE = "gamebox_cheetah_exit_result_one_line";
    //被动框火箭结束后结果卡片出的第2段文案
    public static final String GAMEBOX_CHEETAH_EXIT_RESULT_TWO_LINE = "gamebox_cheetah_exit_result_two_line";
    //被动火箭动画完成之后的卡片的文案 文案格式一定强烈要求为: "描述||左按钮||右按钮"的格式
    public static final String GAMEBOX_EXIT_ROCKET_CARD_TEXT = "gamebox_exit_rocket_card_text";
    //节日卡片是否本地加载了显示
    public static final String GAMEBOX_FESTIVAL_ISLOAD = "gamebox_festival_isload";

    /**
     * Gamebox读取picks超时时间
     */
    public static final String GAME_BOX_PICKS_TIME_OUT = "game_box_picks_time_out";

    /**
     * 禁止游戏盒子使用picks预加载功能
     */
    public static final String GAME_BOX_PICKS_DISABLE_PRELOAD = "game_box_picks_disable_preload";

    /** 新增内存的被动场景进入盒子，弹出内容小豹引导的AB test开关。云端配置，A出小豹：1; B不出小豹：2 */
    public static final String GAME_BOX_CONTENT_CHEETAH_AB = "content_cheetah_ab";

    /** 1.针对指定桌面&创建失败场景，引导开启常 1.驻通知栏 2.悬浮窗。*/
    public static final String GAME_BOX_OPEN_ENTRANCE_AB = "game_box_open_entrance_ab";

    /** 对装了游戏但没游戏行为的用户引导拉活云端时间_国内_开始 */
    public static final String GAME_BOX_PULL_TIME_DOMESTIC_START = "game_box_pull_time_domestic_start";
    /** 对装了游戏但没游戏行为的用户引导拉活云端时间_国内_结束 */
    public static final String GAME_BOX_PULL_TIME_DOMESTIC_END = "game_box_pull_time_domestic_end";

    /** 对装了游戏但没游戏行为的用户引导拉活云端时间_国外_开始 */
    public static final String GAME_BOX_PULL_TIME_FOREIGN_START = "game_box_pull_time_foreign_start";
    /** 对装了游戏但没游戏行为的用户引导拉活云端时间_国外_结束 */
    public static final String GAME_BOX_PULL_TIME_FOREIGN_END = "game_box_pull_time_foreign_end";
    /** 获取已安装游戏大于多少个游戏,(对装了游戏但没游戏行为的用户引导)*/
    public static final String GAME_BOX_PULL_GAME_COUNT = "game_box_pull_game_count";
    /**安装CM超过2天*/
    public static final String GAME_BOX_CM_INSTALL_DAY = "game_box_cm_install_day";
    /**云端控制拉活自己的浮层的次数*/
    public static final String GAME_BOX_PROMT_NUMBER = "game_box_promt_number";

    /** 新用户，本地游戏大于四行时(屏幕内picks列表内容不可见)，是否显示内容小豹开关。1：打开，0：关闭 */
    public static final String GAME_BOX_NEW_USER_CONTENT_CHEETAH_SHOW = "new_user_content_cheetah_show";

    //两次盒子打开触发内存优化的时间间隔
    public static final String GAME_BOX_OPEN_OPTIMIZE_MEMORY_INTERNAL = "game_box_open_optimize_mem_internal";
    //盒子里面两次点击加速球释放内存的时间间隔
    public static final String GAME_BOX_BOOST_BALL_OPTIMIZE_MEMORY_INTERNAL = "game_box_boost_ball_optimize_mem_internal";
    //盒子内存变化比率
    public static final String GAME_BOX_MEMORY_CHANGE_RATE = "game_box_memory_change_rate";
    //盒子两次点击头部且都触发优化动作的时间间隔
    public static final String GAME_BOX_MEMORY_TWICE_CLICK_BOOST_BALL_INTERNAL = "game_box_mem_twice_click_boost_ball_internal";

    // 为修复4.0以下系统盒子内图标消失的问题，将使用ipc通信存取游戏数据库，不再使用ContentProvider的方式，从590开始，默认值为1
    public static final String GAME_DATA_QUERY_USE_IPC = "game_data_query_use_ipc";

    // GameBoard是否显示红点提示
    public static final String GAME_BOARD_SHOW_RED_DOT = "game_board_show_red_dot_2";

    // 游戏加速比最小值和最大值，在此区间随机
    public static final String GAME_BOOST_PERCENT_MIN_VALUE = "game_boost_percent_min";
    public static final String GAME_BOOST_PERCENT_MAX_VALUE = "game_boost_percent_max";

    // 控制游戏盒子被动泡是否只在桌面上弹出
    public static final String GAME_PROBLEM_ONLY_POPUP_ON_LAUNCHER = "game_problem_only_popup_on_launcher";

    /* 需要被动火箭显示的卡片进行AB分类 1.正常界面的卡片  2.大卡*/
    public static final String GAME_BOX_EXIT_ROCKET_CARD_AB = "game_box_exit_rocket_card_ab";
    /**超时处理,如果请求超过了n秒,就不显示节日卡片 格式 1秒 = 1000   */
    public static final String GAME_BOX_LOAD_AD_DATA = "game_box_load_ad_data";


    // 快捷方式卡片展现
    public static final String GAME_BOARD_SHORTCUT_SHOW = "gameboard_shortcut_show"; //DEFAULT: true; NO_SHOW:false
    public static final String GAME_BOARD_FIRST_SHOW_POS = "gameboard_shortcut_show_pos"; //DEFAULT: 4;
    public static final String GAME_BOARD_SHOW_RANGE = "gameboard_show_range"; //DEFAULT: 6;
    public static final String GAME_BOARD_SPEC_POS_SPAWN_FLOAT = "gameboard_spec_pos_spawn_float"; //DEFAULT: 4;
    public static final String GAME_BOARD_SPAWN_FLOAT_HZ = "gameboard_spawn_float_hz"; //DEFAULT: 1 NO_FLOAT: -1
    public static final String GAME_BOARD_MAX_SHOW_COUNT = "gameboard_max_show_count"; //DEFAULT: 1

    // Branding卡片的展现
    public static final String GAME_BOARD_BRANDING_SHOW = "gameboard_branding_show"; // DEFAULT: true; NOT_SHOW:false
    public static final String GAME_BOARD_BRANDING_FIRST_SHOW_POS = "gameboard_branding_first_show"; // DEFAULT: 6
    public static final String GAME_BOARD_BRANDING_SHOW_RANGE = "gameboard_branding_show_range"; // DEFAULT: 9
    public static final String GAME_BOARD_BRANDING_MAX_SHOW_COUNT = "gameboard_branding_max_show_count"; // DEFAULT: 3

    // Branding卡片的文案配置
    public static final String GAME_BOARD_BRANDING_MAIN_TITLE_TXT = "gameboard_branding_main_title_txt";
    public static final String GAME_BOARD_BRANDING_SUB_MAIN_TITLE_TXT = "gameboard_branding_sub_main_title_txt";
    public static final String GAME_BOARD_BRANDING_SUB_MAIN_TITLE_DESC_TXT = "gameboard_branding_sub_main_title_desc_txt";
    public static final String GAME_BOARD_BRANDING_SUB_MAIN_DESC_TXT = "gameboard_branding_sub_main_desc_txt";
    public static final String GAME_BOARD_BRANDING_SUB_DESC_TXT0 = "gameboard_branding_sub_desc_txt0";
    public static final String GAME_BOARD_BRANDING_SUB_DESC_TXT1 = "gameboard_branding_sub_desc_txt1";
    public static final String GAME_BOARD_BRANDING_SUB_DESC_TXT2 = "gameboard_branding_sub_desc_txt2";

    // 基因中Branding卡片的展现
    public static final String GAME_BOARD_GENE_BRANDING_SHOW = "gameboard_gene_branding_show"; // DEFAULT: true; NOT_SHOW:false
    public static final String GAME_BOARD_GENE_BRANDING_FIRST_SHOW_POS = "gameboard_gene_branding_first_show"; // DEFAULT: 4
    public static final String GAME_BOARD_GENE_BRANDING_SHOW_RANGE = "gameboard_gene_branding_show_range"; // DEFAULT: 9
    public static final String GAME_BOARD_GENE_BRANDING_MAX_SHOW_COUNT = "gameboard_gene_branding_max_show_count"; // DEFAULT: 3

    // gameboard的dailyRookie APP view 调去web版还是native版
    public static final String GAME_BOARD_DAILY_ROOKIE_JUMP = "gameboard_daily_rookie_jump_v1";

    /**
     * 本地包名与Board映射（默认隔3天与服务器映射一次)
     */
    public static final String GAME_BOARD_MAPPING_BOARD_INTERVAL = "gameboard_mapping_board_interval";

    /**************** 游戏盒子相关的云端开关**********************/

	public static final String JUNK_SHOW_NOTIFY_FOR_NOT_TURN_TO_JUNK = "junk_show_notify_for_not_turn_to_junk";

	//570版本控制开关--用来控制570版本，千万别删除
	/**全量开放CPU功能*/
	public static final String CPU_OPEN_ALL = "cpu_open_all";
	/**全量关闭CPU功能*/
	public static final String CPU_CLOSE_ALL = "cpu_close_all";
	/**CPU功能开发的MCC列表*/
	public static final String CPU_OPEN_MCC_LIST = "cpu_open_mcc_list";

	//580版本控制开关
	/**全量开放CPU功能*/
	public static final String CPU_OPEN_ALL_NEW = "cpu_open_all_new";
	/**全量关闭CPU功能*/
	public static final String CPU_CLOSE_ALL_NEW = "cpu_close_all_new";
	/**CPU功能开发的MCC列表*/
	public static final String CPU_OPEN_MCC_LIST_NEW = "cpu_open_mcc_list_new";

    // 电池开关主动场景
    public static final String IS_AUTO_CLOSE_POWER_MODE_UI = "is_auto_close_power_mode_ui";
    // 电池开关被动场景
    public static final String IS_AUTO_CLOSE_POWER_MODE_BG = "is_auto_close_power_mode_bg";
    // 电池开关授权开关
    public static final String IS_NEED_SHOW_POWER_SWITCH = "is_need_show_power_switch";

	// 查询是否游戏的云端概率控制，取值范围[0, 10000]
	public static final String IS_GAME_CHECK_CLOUD_PROBABILITY = "is_game_check_cloud_probability";

    // 内存结果页推荐游戏盒子-上一次启动盒子距今的时间间隔，单位：小时
    public static final String LAST_START_GAMEBOX_TIME_INTERVAL = "last_start_gamebox_time_interval";
    // 内存结果页推荐游戏盒子-上一次退出游戏距今的时间间隔，单位：分钟
    public static final String LAST_EXIT_GAME_TIME_INTERVAL = "last_exit_game_interval";

    // 新用户长时间未开启加速拉活通知栏是否展示
    public static final String NEW_USER_NOT_OPEN_BOOST_NOTIFICATION_ENABLED = "new_user_not_open_boost_notification_enabled";
    // 新用户长时间未开启加速拉活通知栏，控制第X天检出
    public static final String NEW_USER_NOT_OPEN_BOOST_CHECK_DAY_INTERVAL = "new_user_not_open_boost_check_day_interval";
    // 新用户长时间未开启加速拉活通知栏,符合场景后X秒后弹出
    public static final String NEW_USER_NOT_OPEN_BOOST_NOTIFICATION_SHOW_DELAY = "new_user_not_open_boost_notification_show_delay";


    /**
     * 用于被动泡场景的推App的数据来源概率控制
     * 设置方式：把用户通过随机数选择0-99共100份， 通过设置两个在0-99之间的值（分别为push_app_probability_range_of_utag
     * 和push_app_probability_range_of_cloud_cfg），来划分出三个区值，来达到分流控制的效果。具体区间划分如下：
     * 0 至 push_app_probability_range_of_utag 是优先推荐uTag商业高质量用户的Ad；
     * push_app_probability_range_of_utag 至 push_app_probability_range_of_cloud_cfg 是优先显示云端配置的推荐App
     * push_app_probability_range_of_cloud_cfg 至 100 是走旧的被动弹泡逻辑
     *
     * 例如:当
     * push_app_probability_range_of_utag = 30；
     * push_app_probability_range_of_cloud_cfg = 70；
     * 优先推荐uTag的app概率划分为：30 - 0 = 30
     * 优先推荐云端配置的app概率划分为：70 - 30 = 40
     * 默认走旧的被动弹泡概率划分为：100 - 70 = 30
     *
     * 注意：（1）push_app_probability_range_of_utag和push_app_probability_range_of_cloud_cfg必须是在0-99之间；
     * （2）push_app_probability_range_of_utag必须“小于等于”push_app_probability_range_of_cloud_cfg
     */
    public static final String PUSH_APP_PROBABILITY_RANGE_OF_UTAG = "push_app_probability_range_of_utag";
    public static final String PUSH_APP_PROBABILITY_RANGE_OF_CLOUD_CFG = "push_app_probability_range_of_cloud_cfg";

    //5.8版信息页面每页数据条数
    public static final String MAX_NUM_PER_GAME_INFO_PAGE = "max_card_num_per_info_page";
    //5.8版信息流保存数据库记录条数
    public static final String MAX_NUM_SAVE_IN_DB = "max_save_card_in_db";

    public static final String B_ENABLE_NAVIGATION_CARD = "B_ENABLE_NAVIGATION_CARD";

    public static final String PHOTOGRID_AD_MIN_FILE_COUNT = "pg_ad_min_count";

    //多款浏览器显示通知栏的总数（国内）
    public static final String MULTI_BROWSER_SHOW_NOTIFICATION_NUM_FOR_ZH = "multi_browser_show_notification_num_for_zh";
    //多款浏览器显示通知栏的总数（国际）
    public static final String MULTI_BROWSER_SHOW_NOTIFICATION_NUM_FOR_INTERNAL = "multi_browser_show_notification_num_for_internal";
    //闲置应用通知栏在一周内显示次数
    public static final String UNUSED_SHOW_NOTIFICATION_NUM_IN_ONE_WEEK = "unused_show_notification_num_in_one_week";
    //软件搬家通知栏在一周内显示次数
    public static final String MOVE_SHOW_NOTIFICATION_NUM_IN_ONE_WEEK = "move_show_notification_num_in_one_week";

    //软件卸载界面是否显示最近3天安装应用的卡片
    public static final String MOVE_SHOW_NEW_APPS_CARD_INSTALL_IN_THREE_DAYS = "uninstall_new_apps_install_in_three_days";

    //优先上报GAID开关
    public static final String PICKS_REPORT_ONLY_GAID = "picks_report_only_gaid";

    //闲置应用通知栏在一周内显示次数
    public static final String UNUSED_SHOW_DIALOG_NUM_IN_ONE_WEEK = "unused_show_dialog_num_in_one_week";
    //软件搬家通知栏在一周内显示次数
    public static final String MULTI_SHOW_DIALOG_NUM_IN_ONE_WEEK = "multi_show_dialog_num_in_one_week";

    //软件搬家通知栏阙值
    public static final String MOVE_SHOW_NOTIFICATION_MOVE_NUM = "move_show_notification_move_num";

    //信息流页面广告个数云端配置
    public static final String INFO_PAGE_AD_NUM = "info_page_ad_num";

    // ipkg 上报开关
    public static final String IPKG_REPORT = "IPKG_REPORT";
    public static final String ORION_ACT_RPT = "ORION_ACT_RPT";

    // appstart 上报开关
    public static final String APPSTART_RPT = "APPSTART_RPT";

    //CPU常驻是否使用内存数据
    public static final String CPU_NORMAL_USE_PROCESS = "cpu_normal_use_process";
    //CPU温度正常有内存数据的低温度阀值
    public static final String CPU_NORMAL_PROCESS_MIDDLE_TEMP = "cpu_normal_process_middle_temp";
    //CPU温度正常有内存数据的高温度阀值
    public static final String CPU_NORMAL_PROCESS_HIGH_TEMP = "cpu_normal_process_high_temp";

    // 微博分享卡片开关
    public static final String WEIBO_SHARE_CARD = "weibo_share_card";

    //国内picks 3g是否拉取数据
    public static final String CN_PICKS_3G_LOAD_DATA = "cn_picks_3g_load_data";


    //DU针对性策略
    public static final String DU_POLICY_SWITCH_STRING = "du_policy_switch";
    public static final String B_RP_AD_PRELOAD = "B_RP_AD_PRELOAD";
    public static final String DU_CM_FIRST_SWITCH = "switch_cm_first";

    /* 垃圾清理上报系统无用档开关 bool型 */
	public static final String JUNK_SCAN_SYSFIXEDFILE_FEEDBACK_ONLY_FLAG = "junk_scan_sysfixedfile_feedback_only_flag";
	
	public static final String AUTOSTART_ABNORMAL_FREQ = "autostart_abnormal_freq";
	public static final String AUTOSTART_HIGH_FREQ = "autostart_high_freq";
    public static final String AUTOSTART_CPU_THRESHOLD = "autostart_cpu_threshold";

    /*CPU降温文案AB test开关*/
    public static final String CPU_COOL_TITLE_PLAN = "cpu_cool_title_plan";
    
    /** 结果页底部bt是否显示 */
    public static final String RESUL_PAGE_ISSHOW_BT = "resul_page_isshow_bt";

    //主界面退出以后弹哪个功能的通知栏的策略
    public static final String PRE_SHOW_NOTIFICATION_POLICY = "pre_show_notification_policy";
    public static final String PRE_SHOW_JUNK_TAB_FINISHED_STATE = "pre_show_junk_finished";
    public static final String MAIN_BELL_STAY_TIME = "main_bell_stay_time";
    public static final String PRE_SHOW_NOTIFICATION_BLACKMCC = "pre_show_notification_blackmcc";
    public static final String PRE_SHOW_JUNK_BLACKMCC = "pre_show_junk_blackmcc";
    public static final String PRE_SHOW_FUNCTION_RING = "pre_show_function_ring";//功能流转的ABtest
    public static final String PRE_SHOW_FUNCTION_RING_DEFAULT = "pre_show_function_ring_default";//功能流转默认显示配置
    public static final String PRE_SHOW_WEATHER_BLACKMCC = "pre_show_weather_blackmcc";
    public static final String PRE_SHOW_NOTIFICATION_SIMILAR_PIC_INTERVAL = "pre_show_notification_similar_pic_interval";
    public static final String PRE_SHOW_NEWRING_WHITEMCC = "pre_show_newring_whitemcc";//云端控制新铃铛
    public static final String PRE_SHOW_NEWRING_SHOW_PROBABILITY = "pre_show_newring_show_probability";//云端控制新铃铛显示概率


    //主界面头部卡片的展示间隔
    public static final String PRE_SHOW_PROBLEM_VIRUS_INTERNVAL = "pre_show_problem_virus_interval";
    public static final String PRE_SHOW_PROBLEM_SPACE_INTERNVAL = "pre_show_problem_space_interval";
    public static final String PRE_SHOW_PROBLEM_CPULAGGING_INTERNVAL = "pre_show_problem_cpulagging_interval";
    public static final String PRE_SHOW_PROBLEM_MEMORY_INTERNVAL = "pre_show_problem_memory_interval";
    public static final String PRE_SHOW_PROBLEM_JUNK_INTERNVAL = "pre_show_problem_junk_interval";
    public static final String PRE_SHOW_PROBLEM_TEMPCPU_INTERNVAL = "pre_show_problem_tempcpu_interval";
    public static final String PRE_SHOW_PROBLEM_HISTORY_INTERVAL = "pre_show_problem_history_interval";
    public static final String PRE_SHOW_PROBLEM_FREQSTART_INTERVAL = "pre_show_problem_freqstart_interval";
    public static final String PRE_SHOW_PROBLEM_MEMORY_PERCENT = "pre_show_problem_memory_percent";
    public static final String PRE_SHOW_PROBLEM_JUNK_SIZE = "pre_show_problem_junk_size";
    public static final String PRE_SHOW_ANY_PROBLEM_INTERVAL = "pre_show_any_problem_interval";
    public static final String PRE_SHOW_APP_CATEGORY_INTERVAL = "pre_show_app_category_interval";
    public static final String PRE_SHOW_APP_CATEGORY_MIN_PKG_SIZE = "pre_show_appcategory_min_pkg_size";
    public static final String PRE_SHOW_APP_CATEGORY_BLACK_MCC = "pre_show_app_category_blackmcc";
    public static final String PRE_SHOW_RING_JUNK_MIN_SIZE = "pre_show_ring_junk_minsize";
    public static final String PRE_SHOW_PROBLEM_AUTOSTART_INTERNVAL = "pre_show_problem_autostart_interval";
    public static final String PRE_SHOW_PROBLEM_VIDEO_JUNK_INTERNVAL = "pre_show_problem_video_junk_interval";
    public static final String PRE_SHOW_PROBLEM_GAME_SLOW_INTERNVAL = "pre_show_problem_game_slow_interval";
    public static final String PRE_SHOW_PROBLEM_BATTERY_DOCTER_INTERNVAL = "pre_show_problem_battery_docter_interval";
    public static final String PRE_SHOW_PROBLEM_SECURITYBROCAST_INTERNVAL = "pre_show_problem_securitybrocast_internval";
    public static final String PRE_SHOW_PROBLEM_APP_LOCK_INTERVAL = "pre_show_problem_app_lock_interval";
    public static final String PRE_SHOW_APP_CATEGORY_IS_SHOW = "pre_show_app_category_is_show";
    public static final String PRE_SHOW_RING_FUNCTION_RECOMMEND = "pre_show_ring_function_recommend";//功能引导云端开关
    public static final String PRE_SHOW_RING_NOTIFICATION = "pre_show_ring_notification";//拉活通知栏云端开关
    public static final String PRE_SHOW_RING_RECOMMEND_INTERNVAL = "pre_show_ring_recommend_interval";
    public static final String PRE_SHOW_PROBLEM_APPLOCK_TYPE_SHOW = "pre_show_problem_applock_type_show";//applock卡片展示策略,1为单个，2为多个
    public static final String PRE_SHOW_PROBLEM_APPLOCK_SHOW_PROBABILITY = "pre_show_problem_applock_show_probability";//APP LOCK卡片出现的概率控制
    public static final String PRE_SHOW_PROBLEM_BATTERY_DOCTOR_COUNT = "pre_show_problem_battery_doctor_count";
    public static final String PRE_SHOW_PROBLEM_APPLOCK_MAX_SHOW_TIME = "pre_show_problem_applock_max_show_time";
    public static final String PRE_SHOW_PROBLEM_RECOMMEND_OPEN = "pre_show_problem_recommend_open";//推荐类型卡片开关。
    public static final String PRE_SHOW_JUNK_CARD_IS_SHOW = "pre_show_junk_card_is_show";//垃圾卡片云端开关

    //首页功能未使用时长配置
    public static final String PRE_SHOW_RING_JUNK_FUNCTION_INTERNVAL = "pre_ring_show_junk_function_interval";
    public static final String PRE_SHOW_RING_MEMORY_FUNCTION_INTERNVAL = "pre_ring_show_memory_function_interval";
    public static final String PRE_SHOW_RING_SECURITYBROCAST_FUNCTION_INTERNVAL = "pre_ring_show_securitybrocast_function_interval";
    public static final String PRE_SHOW_RING_APPMGR_FUNCTION_INTERNVAL = "pre_ring_show_appmgr_function_interval";
    public static final String PRE_SHOW_RING_SIMILAR_PIC_FUNCTION_INTERNVAL = "pre_ring_show_similar_pic_function_interval";
    public static final String PRE_SHOW_RING_GAME_SLOW_FUNCTION_INTERNVAL = "pre_ring_show_game_slow_function_interval";
    public static final String PRE_SHOW_RING_PHONOTRIM_FUNCTION_INTERNVAL = "pre_show_ring_phonotrim_function_internval";

    public static final String PRE_SHOW_NEW_MESSAGE_SHOW = "pre_show_new_message_show";//首页消息红点是否显示

    public static final String B_INFOC_SSID = "B_INFOC_SSID";

    //game guess memory report switch
    public static final String GAME_GUESS_MEM_SWITCH = "game_guess_mem_report_switch";


	 /* 双11活动 */
    public static final String S_IMAGE_1110 = "S_IMAGE_1110";
    public static final String S_IMAGE_1111 = "S_IMAGE_1111";
    public static final String S_URL_1111 = "S_URL_1111";
    public static final String S_IMAGE_1110_RP = "S_IMAGE_1110_RP";
    public static final String S_IMAGE_1111_RP = "S_IMAGE_1111_RP";

    //内部推荐相互规避
    public static final String APP_MGR_RCMD_ONEDAY_LIMIT ="app_mgr_oneday_normal_key";
    public static final String APP_MGR_ONEDAY_AD_KEY="app_mgr_oneday_ad_key";

    //在picks是否显示facebook广告
    public static final String PICKS_SHOW_FACEBOOK_AD = "is_show_facebook_ad";

    //增加云端开关控制杀进程的调用方法
    public static final String GAME_BOX_KILL_METHOD = "game_box_kill_process_method_invoke_switch";

    //过多长时间，用户可以点击悬浮层外面的区域关闭显示框,单位s
    public static final String FLOAT_WINDOW_DELAY_CAN_TOUCH_OUT_CLOSE = "float_window_delay_can touch_out_close";
    //延时关闭显示窗口,整个窗口显示事件将会加上FLOAT_WINDOW_DELAY_CAN_TOUCH_OUT_CLOSE时间,单位s
    public static final String FLOAT_WINDOW_DELAY_AUTO_CLOSE = "float_window_delay_auto_close";
    //关闭浮层回桌面弹出的场景
    public static final String FLOAT_WINDOW_SWITCH = "float_window_switch";
    public static final String ACC_SERVICE_CONFIGUE = "acc_service_configue";
    public static final String ACC_SERVICE_MCC = "acc_service_mcc";
    public static final String ACC_SERVICE_URL    = "acc_service_url";
    public static final String ACC_SERVICE_VERCODE = "acc_service_vercode";
    public static final String ACC_SERVICE_RATE = "acc_service_rate";
    public static final String ACC_UNSUPPORTED_BRAND = "acc_unsupported_brand"; ///< 以 ; 隔开，CRC("none") 表示全支持，crc("all") 表示全不支持

	/*------------------- 相似图片 begin -------------------*/
	public static final String SIMILAR_PIC_RATE_SWITCH = "rate_switch";
	public static final String SIMILAR_PIC_BURST_INTERVAL = "burst_interval";
	public static final String SIMILAR_PIC_BURST_NUM = "burst_num";
	public static final String SIMILAR_PIC_BURST_NOTIFICATION_SHOW_INTERVAL = "burst_notif_interval";

	public static final String SIMILAR_PIC_NOTIFICATION_TITLE = "similar_notif_title";
	public static final String SIMILAR_PIC_NOTIFICATION_CONTENT = "similar_notif_content";
	public static final String SIMILAR_PIC_BURST_NOTIFICATION_TITLE = "burst_notif_title";
	public static final String SIMILAR_PIC_BURST_NOTIFICATION_CONTENT = "burst_notif_content";
	/*------------------- 相似图片 end -------------------*/

	/*------------------- 图片压缩 begin -------------------*/
	public static final String PHOTO_COMPRESS_SWITCH = "function_switch";
	public static final String PHOTO_COMPRESS_NOTIFY_SWITCH = "notify_switch";
	public static final String PHOTO_COMPRESS_NOTIFY_PHOTO_THRESHOLD = "notify_photo_threshold";
	/*------------------- 图片压缩 end -------------------*/


    public static final String NOTIFICATION_MAXIUM_COUNT = "notify_maxium_count";
    public static final String NOTIFICATION_MAXIUM_RANGE_HOUR = "notify_maxium_range_hour";
    public static final String NOTIFICATION_MAXIUM_COUNT_MCC = "notify_maxium_count_mcc";
    public static final String NOTIFICATION_MAXIUM_COUNT_CHANNEL = "notify_maxium_count_channel";

    public static final String NOTIFICATION_PRIORITY_HIGH = "priority_high";
    public static final String NOTIFICATION_PRIORITY_MIDDLE = "priority_middle";
    public static final String NOTIFICATION_PRIORITY_LOW = "priority_low";
    public static final String NOTIFICATION_SWITCH_BY_ID = "notification_id_";
    public static final String NOTIFICATION_SWITCH_BY_CATEGORY = "notification_category_";
    public static final String NOTIFICATION_LOCKER_SHOW = "locker_show_id";
    public static final String NOTIFICATION_HEADER_SHOW = "header_show_id";
    public static final String NOTIFICATION_HEADER_DURATION = "header_duration_";

    /*---------start of memory boost notify cloud title*/
    public static final String MEM_LOW_NOTIFY_CONTENT = "mem_low_notify_content";
    /*---------end of memory boost notify cloud title*/

    // 結果頁導航策略
    public static final String N_REMOTE_NAV_POLICY = "N_REMOTE_NAV_POLICY";

    //结果页软管盒子开关
    public static final String R_RESULT_CATEGORT = "R_RESULT_CATEGORT";
    //卡慢结果页软管盒子开关
    public static final String R_RESULT_AB_CATEGORT = "R_RESULT_AB_CATEGORT";

    //卸载页软管盒子开关
    public static final String R_UNINSTALL_CATEGORT = "R_UNINSTALL_CATEGORT";

    //结果页头部样式概率
    public static final String R_HEADER_RANDOM = "R_HEADER_RANDOM";

    //结果页头部样式
    public static final String R_HEADER_TYPE = "R_HEADER_TYPE";

    //结果页箭头重复次数
    public static final String R_INTRO_COUNT = "R_INTRO_COUNT";

    //结果页商业广告EXT开关 594
    public static final String R_RESULT_BUSSINESS_AD_EXT = "R_RESULT_BUSSINESS_AD_EXT_SWITCH";

    //结果页新用户上拉引导
    public static final String R_RESULT_SCROLL_UP_TIP = "R_RESULT_SCROLL_UP_TIP";

    //结果页评论云端开关
    public static final String R_RESULT_COMMENT = "R_RESULT_COMMENT";

    //结果页详情页评论云端开关
    public static final String R_RESULT_DETAIL_COMMENT = "R_RESULT_DETAIL_COMMENT";

    //数量和来源 展示开关
    public static final String R_WIZARD_TITLE = "R_WIZARD_TITLE";

    //结果页首次加载更多猎豹知道配置
    public static final String R_WIZARD_PAGE = "R_WIZARD_PAGE";

    /* 结果页新闻初始加载数量 */
    public static final String N_RP_NEWS_INIT_COUNT = "N_RP_NEWS_INIT_COUNT";

    /* 猎豹知道是否拉取分页数据 */
    public static final String R_WIZARD_MORE = "R_WIZARD_MORE";

    /*结果页头部提示*/
    public static final String R_RESULT_EFFECT_POINT = "R_RESULT_EFFECT_POINT";
    /*结果页头部效果语*/
    public static final String R_RESULT_EFFECT_GREET = "R_RESULT_EFFECT_GREET";
    /*结果页头部图片*/
    public static final String R_RESULT_EFFECT_ICON = "R_RESULT_EFFECT_ICON";

    //onetap cloud settings
    public static final String CLOUD_ONETAP_KEY = "onetap_settings";
    public static final String CLOUD_ONETAP_MAX_RECOMMEND_TIMES = "onetap_max_recommend_times"; //1tap一天内最大推荐次数
    public static final String CLOUD_ONETAP_RECOMMEND_INTERNAL_MIN = "onetap_recommend_internal"; //1tap两次推荐次数(分钟为单位)
    public static final String CLOUD_ONETAP_CLEAN_MAX_TIME = "onetap_clean_max_time"; //1tap清理内存最大时间，用于是否展示推荐
    public static final String CLOUD_ONETAP_ANIM_TIME = "onetap_anim_time"; //1tap动画时间配置项
    public static final String CLOUD_ONETAP_RECOMMEND_SORT = "onetap_recommend_sort";  //1tap推荐顺序
    public static final String CLOUD_ONETAP_RECOMMEND_JUNK_SIZE = "onetap_recommend_junk_size";//1tap推荐垃圾阀值
    public static final String CLOUD_ONETAP_RECOMMEND_JUNK_TEXT = "onetap_recommend_junk_text";//1tap推荐垃圾云端文案
    public static final String CLOUD_ONETAP_RECOMMEND_MARKET_INTERVAL = "onetap_recommend_market_interval";//1tap商业推荐时间间隔

    /**-----云端竞品相关配置信息-----**/
    public static final String INTEREST_PHONE_SWITCH = "interest_phone_switch";
    public static final String INTEREST_PHONE_MCC = "interest_phone_mcc";
    public static final String INTEREST_PHONE_PKG_SWITCH = "interest_phone_";

    //盒子通知安装更新通知栏提示弹出样式开关
    public static final String CLOUD_GAME_BOX_NOTIFICATION_STYLE = "gamebox_notification_style";
    public static final String CLOUD_GAME_BOX_IGNORE_SECURE = "gamebox_ignore_secure";

    /**垃圾通知栏文案
     * 主key：CLOUD_JUNK_KEY*/
    //subkey
    public static final String JUNK_NOTIFY_LONG_TIME_UNUSED_TICKER = "junk_notify_long_time_unused_ticker";  //7天未使用 tiker文案1
    public static final String JUNK_NOTIFY_LONG_TIME_UNUSED_TITLE = "junk_notify_long_time_unused_title";   //7天未使用 title文案1
    public static final String JUNK_NOTIFY_LONG_TIME_UNUSED_CONTENT = "junk_notify_long_time_unused_content";//7天未使用 content文案1
    public static final String JUNK_NOTIFY_LONG_TIME_UNUSED_TICKER2 = "junk_notify_long_time_unused_ticker2";//7天未使用 tiker文案2
    public static final String JUNK_NOTIFY_LONG_TIME_UNUSED_TITLE2 = "junk_notify_long_time_unused_title2";//7天未使用 title文案2
    public static final String JUNK_NOTIFY_LONG_TIME_UNUSED_CONTENT2 = "junk_notify_long_time_unused_content2";//7天未使用 content文案2
    public static final String JUNK_NOTIFY_LONG_TIME_UNUSED_TICKER3 = "junk_notify_long_time_unused_ticker3";//7天未使用 tiker文案3
    public static final String JUNK_NOTIFY_LONG_TIME_UNUSED_TITLE3 = "junk_notify_long_time_unused_title3";//7天未使用 title文案3
    public static final String JUNK_NOTIFY_LONG_TIME_UNUSED_CONTENT3 = "junk_notify_long_time_unused_content3";//7天未使用 content文案3

    public static final String JUNK_NOTIFY_TICKER1 = "junk_notify_ticker1";//文案1/2公用ticker
    public static final String JUNK_NOTIFY_TITLE_R3 = "junk_notify_title_r3";//3天100M title文案1
    public static final String JUNK_NOTIFY_TITLE_R2 = "junk_notify_title_r2";//3天100M title文案2
    public static final String JUNK_NOTIFY_CONTENT_R3 = "junk_notify_content_r3";//3天100M 文案1
    public static final String JUNK_NOTIFY_CONTENT_R2 = "junk_notify_content_r2";//3天100M 文案2
    public static final String JUNK_NOTIFY_ADV_TICKER = "junk_notify_adv_ticker";//3天100M广告 ticker文案3
    public static final String JUNK_NOTIFY_ADV_TITLE = "junk_notify_adv_title";//3天100M广告 title文案3
    public static final String JUNK_NOTIFY_ADV_CONTENT = "junk_notify_adv_content";//3天100M广告 content文案3
    public static final String JUNK_NOTIFY_VIDEO_REMIND_TITLE = "junk_notify_video_remind_title";//視頻3天增量200M通知title文案
    public static final String JUNK_NOTIFY_VIDEO_REMIND_CONTENT = "junk_notify_video_remind_content";//視頻3天增量200M通知content文案

    public static final String JUNK_NOTIFY_FIRST_TICKER = "junk_notify_first_ticker";//首次通知欄
    public static final String JUNK_NOTIFY_FIRST_TITLE = "junk_notify_first_title";
    public static final String JUNK_NOTIFY_FIRST_CONTENT = "junk_notify_first_content";

    /**
     * 垃圾通知栏开关
     * 主key：CLOUD_JUNK_KEY */
    //subkey
    //设置垃圾通知栏1天50M的time和size以及 弹出时机为点击时间用户比例
    public static final String JUNK_NOTIFY_CACHE_CLICK_TIME_RATE = "junk_notify_cache_click_time_rate";
    public static final String JUNK_NOTIFY_CACHE_TIME = "JunkReminderOriTime";
    public static final String JUNK_NOTIFY_CACHE_SIZE = "JunkReminderOriSize";
    //设置里面的垃圾通知栏是否生效开关
    public static final String JUNK_NOTIFY_IS_SETTING_EFFECT = "junk_notify_is_setting_effect";
    //增量通知（3天100M）是否增加总时间条件开关
    public static final String JUNK_NOTIFY_CACHE_TIME_CONCERNED = "junk_notify_cache_time_concerned";
    //增量通知（3天100M） 总时间开关关闭的情况下，第二次弹的时间(点)
    public static final String JUNK_NOTIFY_CACHE_SECOND_PUSH_TIME = "junk_notify_cache_second_push_time";
    //12小时开关
    public static final String JUNK_NOTIFY_CACHE_IS_TWELVE_HOUR_EFFECT = "junk_notify_cache_is_twelve_hour_effect";
    //置顶开关
    public static final String JUNK_NOTIFY_CACHE_IS_TOP_EFFECT = "junk_notify_cache_is_top_effect";
    //adv通知栏开关
    public static final String JUNK_NOTIFY_CACHE_IS_ADV_EFFECT = "junk_notify_cache_is_adv_effect";
    //apk通知栏开关
    public static final String JUNK_NOTIFY_CACHE_IS_APK_EFFECT = "junk_notify_cache_is_apk_effect";
    //通知栏覆盖策略开关
    public static final String JUNK_NOTIFY_CACHE_IS_COVER_EFFECT = "junk_notify_cache_is_cover_effect";
    //通知栏三天没处理策略开关
    public static final String JUNK_NOTIFY_CACHE_IS_CANCELL_EFFECT = "junk_notify_cache_is_cancel_effect";
    //通知栏右侧是否使用button开关
    public static final String JUNK_NOTIFY_CACHE_IS_RIGHT_BUTTON = "junk_notify_cache_is_right_button";

    //七天未使用 未点击进垃圾界面 一天弹一次开关
    public static final String JUNK_NOTIFY_LONGTIME_IS_ONEDAY_PUSH = "junk_notify_longtime_is_oneday_push";
    //盒子释放内存杀进程的白名单
    public static final String GAME_MEM_PROCESS_WHITE_LIST = "game_mem_process_white_list";
    //被动弹泡延时时长
    public static final String GAME_EXIT_DIALOG_DELAY_SHOW = "game_exit_dialog_delay_show";
    //加速球转圈加速动画最大时长
    public static final String BOOST_BALL_ANIMATION_DURATION = "game_ball_animation_duration";
    //是否殺掉最後玩得遊戲進程
    public static final String CLOUD_GAME_KILL_LAST = "game_kill_last";
    //用户常玩得游戏定义的时间单位h
    public static final String USER_LIKE_GAME_TIME = "user_like_game_time";
    //用户常玩的游戏定义的次数
    public static final String USER_LIKE_GAME_COUNT = "user_like_game_count";
    //切换浮层旧样式
    public static final String GAME_SWITCH_OLD_FLOAT_WINDOW_STYLE = "game_switch_old_float_style";
    //是否开启浮层的动画
    public static final String GAME_ENABLE_FLOAT_WINDOW_ANIMATION = "game_enable_float_animation";

    /** CM Family 游戏推广卡片配置信息 **/
    public static final String CM_FAMILY_KEY = "cm_family_key";
    public static final String CM_FAMILY_GAME_CONTENT = "cm_family_game_content";
    public static final String CM_FAMILY_GAME_BUTTON = "cm_family_game_button";

    public static final String MAIN_ACT_CARD_MEM_TITLE = "main_act_card_mem_title";
    public static final String MAIN_ACT_CARD_MEM_SUMMARY = "main_card_mem_summary";
    public static final String MAIN_ACT_CARD_MEM_BUTTON = "main_card_mem_button";

    public static final String MAIN_ACT_CARD_SPACE_SYSTEM_TITLE = "main_act_card_space_system_title";
    public static final String MAIN_ACT_CARD_SPACE_SYSTEM_SUMMARY ="main_act_card_space_system_summary";
    public static final String MAIN_ACT_CARD_SPACE_SYSTEM_BUTTON ="main_act_card_space_system_button";

    public static final String MAIN_ACT_CARD_SPACE_OTHERS_TITLE = "main_act_card_space_others_title";
    public static final String MAIN_ACT_CARD_SPACE_OTHERS_SUMMARY ="main_act_card_space_others_summary";
    public static final String MAIN_ACT_CARD_SPACE_OTHERS_BUTTON ="main_act_card_space_others_button";

    public static final String MAIN_ACT_CARD_MUCH_JUNK_TITLE = "main_act_card_much_junk_title";
//    public static final String MAIN_ACT_CARD_MUCH_JUNK_SUMMARY = "main_act_card_much_junk_summary";
    public static final String MAIN_ACT_CARD_MUCH_JUNK_BUTTON_CLEAR = "main_act_card_much_junk_button_clear";
    public static final String MAIN_ACT_CARD_MUCH_JUNK_BUTTON_IGNORE = "main_act_card_much_junk_button_ignore";

    public static final String MAIN_ACT_CARD_HIGH_TEMP_ABOVE_TITLE = "main_act_card_high_temp_above_title";
    public static final String MAIN_ACT_CARD_HIGH_TEMP_ABOVE_SUMMARY = "main_act_card_high_temp_above_summary";
    public static final String MAIN_ACT_CARD_HIGH_TEMP_ABOVE_BUTTON  = "main_act_card_high_temp_above_button";

    public static final String MAIN_ACT_CARD_HIGH_TEMP_BELOW_TITLE = "main_act_card_high_temp_below_title";
    public static final String MAIN_ACT_CARD_HIGH_TEMP_BELOW_SUMMARY = "main_act_card_high_temp_below_summary";
    public static final String MAIN_ACT_CARD_HIGH_TEMP_BELOW_BUTTON = "main_act_card_high_temp_below_button";

    public static final String MAIN_ACT_CARD_WEB_JUNK_TITLE = "main_act_card_web_junk_title";
    public static final String MAIN_ACT_CARD_WEB_JUNK_SUMMARY = "main_act_card_web_junk_summary";
    public static final String MAIN_ACT_CARD_WEB_JUNK_BUTTON = "main_act_card_web_junk_button";

    public static final String MAIN_ACT_CARD_FREQUENCE_RESTART_TITLE = "main_act_card_frequence_restart_title";
    public static final String MAIN_ACT_CARD_FREQUENCE_RESTART_SUMMARY = "main_act_card_frequence_restart_summary";
    public static final String MAIN_ACT_CARD_FREQUENCE_RESTART_BUTTON = "main_act_card_frequence_restart_button";

    public static final String MAIN_ACT_CARD_BATTERY_DOCTOR_SUMMARY = "main_card_battery_doctor_summary";
    public static final String MAIN_ACT_CARD_BATTERY_DOCTOR_TITLE = "main_card_battery_doctor_title";
    public static final String MAIN_ACT_CARD_BATTERY_DOCTOR_BUTTON = "main_card_battery_doctor_button";

    public static final String MAIN_ACT_CARD_GAME_BOX_TITLE = "main_act_card_game_box_title";
    public static final String MAIN_ACT_CARD_GAME_BOX_SUMMARY = "main_act_card_game_box_summary";
    public static final String MAIN_ACT_CARD_GAME_BOX_BUTTON = "main_act_card_game_box_button";

    public static final String MAIN_ACT_CARD_BROADCAST_TITLE = "main_act_card_broadcast_title";
    public static final String MAIN_ACT_CARD_BROADCAST_SUMMARY = "main_act_card_broadcast_summary";
    public static final String MAIN_ACT_CARD_BROADCAST_BUTTON = "main_act_card_broadcast_button";

    public static final String MAIN_ACT_CARD_APPLOCK_TITLE = "main_act_card_applock_title";
    public static final String MAIN_ACT_CARD_APPLOCK_SUMMARY = "main_act_card_applock_summary";
    public static final String MAIN_ACT_CARD_APPLOCK_BUTTON = "main_act_card_applock_button";

    public static final String MAIN_ACT_CARD_APPLOCK_APPS_TITLE = "main_act_card_applock_apps_title";
    public static final String MAIN_ACT_CARD_APPLOCK_APPS_SUMMARY = "main_act_card_applock_apps_summary";
    public static final String MAIN_ACT_CARD_APPLOCK_APPS_BUTTON = "main_act_card_applock_apps_button";

    public static final String MAIN_ACT_DRAWER_REGISTER_1 = "main_act_drawer_register_info1";
    public static final String MAIN_ACT_DRAWER_REGISTER_2 = "main_act_drawer_register_info2";
    public static final String MAIN_ACT_DRAWER_REGISTER_3 = "main_act_drawer_register_info3";

    public static final String MAIN_ACT_DRAWER_ALERT_MESSAGE_INTERVAL = "main_act_drawer_alert_message_interval";

    public static final String MAIN_ACT_RING_GAME_BOX = "main_act_ring_game_box";
    public static final String MAIN_ACT_RING_FUNCTION_JUNK = "main_act_ring_function_junk";
    public static final String MAIN_ACT_RING_FUNCTION_BOOST = "main_act_ring_function_boost";
    public static final String MAIN_ACT_RING_FUNCTION_APPRARELYUSED = "main_act_ring_function_apprarelyused";
    public static final String MAIN_ACT_RING_FUNCTION_APPMGR = "main_act_ring_function_appmgr";
    public static final String MAIN_ACT_RING_FUNCTION_SECURITY = "main_act_ring_function_security";
    public static final String MAIN_ACT_RING_FUNCTION_DUPLICATE_PHOTO = "main_act_ring_function_duplicate_photo";

    /* 节日运营相关 */
    public static final String SECTION_FESTIVAL = "festival";

    public static final String S_SPLASH_BG = "N_SPLASH_BG";         // 闪屏北京
    public static final String S_SPLASH_ICON = "S_SPLASH_ICON";     // 闪屏图标
    public static final String S_SPLASH_URL = "S_SPLASH_URL";       // 连接
    public static final String S_BELL_ICON = "S_BELL_ICON";         // 铃铛图片
    public static final String S_BELL_TEXT = "S_BELL_TEXT";         // 铃铛卡内容
    public static final String S_BELL_BUTTON_TEXT = "S_BELL_BUTTON_TEXT"; // 铃铛卡按钮文字
    public static final String S_BELL_FLAKE = "S_BELL_FLAKE"; // 漂浮物

    public static final String N_START_TIME = "N_START_TIME" ; // 活动开始时间 YYYYmmdd:hh
    public static final String N_STOP_TIME = "N_STOP_TIME";   // 活动结束时间 YYYYmmdd:hh
    public static final String S_BELL = "S_BELL"; // 铃铛图标
    public static final String N_ID = "N_ID"; // 活动ID

    public static final String CONTACT_BACKUP_RECOMMEND_CARD_TYPE = "contact_backup_recommend_card_type";
    public static final String CONTACT_BACKUP_ENABLE_PROTECTION_BTN = "contact_backup_enable_protection_btn";

    /** 充电屏保通知栏 main key:screen_saver **/
    public static final String SCREEN_SAVER_NOTIFICATION_MCC = "screen_saver_noti_mcc";
    public static final String SCREEN_SAVER_NOTIFICATION_PERCENT = "screen_saver_noti_percent";
    public static final String SCREEN_SAVER_NOTIFICATION_INTERVAL = "screen_saver_noti_interval";//通知栏弹出间隔基数，默认为3天
    public static final String SCREEN_SAVER_NOTIFICATION_INTERVAL_COUNT = "screen_saver_noti_interval_count";//通知栏弹出总次数，默认为5
    public static final String SCREEN_SAVER_NOTIFICATION_INTERVAL_VERSION = "screen_saver_noti_interval_version";//通知栏弹出间隔版本，如果发现版本不一致，则本地记录reset一下
    public static final String SCREEN_SAVER_NOTIFICATION_SWITCH = "screen_saver_noti_switch";

    /**充电屏保充电只展示一次云端配置 main key：screen_saver*/
    public static final String SCREEN_SAVER_ONCE_MCC = "screen_saver_once_mcc";
    public static final String SCREEN_SAVER_ONCE_PERCENT = "screen_saver_once_percent";
    public static final String SCREEN_SAVER_ONCE_USE_IN_NEW = "screen_saver_once_use_new_r595";
    public static final String SCREEN_SAVER_ONCE_SWITCH = "screen_saver_switch_r595";

    ///<新屏保展现控制
    public static final String NEW_SCREEN_SAVER_OLD_USER_PERCENT = "new_screen_saver_old_user_percent";
    public static final String NEW_SCREEN_SAVER_OLD_USER_MCC = "new_screen_saver_old_user_mcc";
    public static final String NEW_SCREEN_SAVER_NEW_USER_PERCENT = "new_screen_saver_new_user_percent";
    public static final String NEW_SCREEN_SAVER_NEW_USER_MCC = "new_screen_saver_new_user_mcc";

    ///< 老屏保展现控制
    public static final String OLD_SCREEN_SAVER_OLD_USER_PERCENT = "old_screen_saver_old_user_percent";
    public static final String OLD_SCREEN_SAVER_OLD_USER_MCC = "old_screen_saver_old_user_mcc";
    public static final String OLD_SCREEN_SAVER_NEW_USER_PERCENT = "old_screen_saver_new_user_percent";
    public static final String OLD_SCREEN_SAVER_NEW_USER_MCC = "old_screen_saver_new_user_mcc";

    ///< 屏保，拔电是否消失
    public static final String NEW_SCREEN_SAVER_DISCONN_DISAPPEAR = "new_screen_saver_disconn_disappear";

    //充电屏保默认值云端配置
    public static final String SCREEN_SAVER_DEFAULT_SWITCH = "screen_saver_default_switch";
    public static final String SCREEN_SAVER_DEFAULT_MCC = "screen_saver_default_mcc";
    public static final String SCREEN_SAVER_DEFAULT_PERCENT = "screen_saver_default_percent";
    public static final String SCREEN_SAVER_GUIDE_TOAST_SHOW = "screen_saver_guide_show";//是否展示充电屏保Header
    public static final String SCREEN_SAVER_GUIDE_TOAST_DURATION = "screen_saver_guide_duration";//充电屏保Header展示时间，默认为8S
    public static final String SCREEN_SAVER_GUIDE_TOAST_INTERVAL = "screen_saver_guide_interval";//充电屏保Header展示间隔，默认为一次

    // 充电屏保广告预加载开关
    public static final String SCREEN_SAVER_BUSSINESS_AD_PRELOAD = "screen_saver_bussiness_ad_preload";

    /** 充电屏保结果页 **/
    public static final String SCREEN_SAVER_RESULT_PAGE_SWITCH = "screen_saver_rp_switch";
    public static final String SCREEN_SAVER_RESULT_PAGE_MCC = "screen_saver_rp_mcc";

    //充电屏保facebook广告和cm广告是否显示广告标签
    public static final String SCREEN_SAVER_SHOW_FB_AD_TAG = "screen_saver_show_fb_ad_tag";
    public static final String SCREEN_SAVER_SHOW_CM_AD_TAG = "screen_saver_show_cm_ad_tag";
    //充电屏保是否显示小豹子
    public static final String SCREEN_SAVER_SHOW_CHEETAH = "screen_saver_show_cheetah";
    //充电屏保广告显示风格, 从电池医生代码中引入
    public static final String SCREEN_SAVER_WEBVIEW_AD_STYLE = "screen_saver_webview_ad_style";

    public static final String SCREEN_SAVER_RESULT_PAGE_PERCENT = "screen_saver_rp_percent";

    //隐藏设置界面开关
    public static final String SCREEN_SAVER_RESULT_HIDE_SETTING = "screen_saver_hide_setting";

    /** 判断是否有密码锁的开关 **/
    public static final String SCREEN_SAVER_LOCK_SWITCH = "screen_saver_lock_switch";

    /* 登录相关的 */
    public static final String LOGIN_FRESH_TOKEN_USE_3G = "login_fresh_token_use_3g";

    /* applock 功能相关 */
    public static final String APP_LOCK = "app_lock";
    public static final String APPLOCK_SWITCHER = "applock_switcher_final";
    public static final String APPLOCK_AD_SWITCHER = "applock_ad_switcher";
    public static final String APPLOCK_SWITCHER_MCC = "applock_switcher_mcc";
    public static final String APPLOCK_BANNER_BUSINESS_AD_PRELOAD = "applock_banner_business_ad_preload";
    public static final String APP_LOCK_NOTIFICATION_INTERVAL_DAYS = "app_lock_noti_interval_days";//通知栏弹出间隔基数，默认为3天
    public static final String APP_LOCK_NOTIFICATION_TOTAL_COUNT = "app_lock_noti_total_count";//通知栏弹出总次数，默认为3
    public static final String APP_LOCK_NOTIFICATION_COUNT_RESET = "app_lock_noti_count_reset";//通知栏弹出总次数重置，默认为false
    public static final String APP_LOCK_NOTIFICATION_SWITCH = "app_lock_noti_switch";

    public static final String APP_BOX_BG = "app_box_bg";
    public static final String APP_BOX_BOOST_HEADER = "app_box_boost_header";
    public static final String LIEBAO_MCC = "liebao_mcc";
    public static final String APP_BOX_UNINSTALL_RECOMMEND = "app_box_uninstall_recomend";
    public static final String APP_CATEGOTY_TIP = "app_category_tip";
    /* 可爱小豹卡片相关 */
    public static final String SECTION_LOVELY_CHEETAH = "lovely_cheetah";
    public static final String LOVELY_CHEETAH_REMIND_STRING = "lovely_cheetah_remind_string";
    public static final String LOVELY_CHEETAH_VERSION = "lovely_cheetah_version";

    public static final String APP_FOLDER_MONITOR_SWITCH = "a_f_mon_switch";    ///< CLOUD_JUNK_SETTINGS_KEY之下的开关，是否上报cm_app_sddb_mon_d， cm_app_sd_mon_d， cm_app_sddb_mon， cm_app_sd_mon数据。

    public static final String SOCIAL_KEY = "social";
    public static final String SOCIAL_COMMENT_CACHE = "social_comment_cache";
    public static final String SOCIAL_NOTIFY_MAX_COUNT_PER_DAY = "social_notify_max_count_per_day";

    /**系统预装应用是否需要在预装初期阻止部分通知栏弹出，默认为开*/
    public static final String PREINSTALL_CLOSE_CHANNEL = "preinstall_close_channel";
    /***系统预装应用需要在预装多大时间范围内阻止部分通知栏弹出，默认为7天，单位为天**/
    public static final String PREINSTALL_AVOID_TIME = "preinstall_avoid_time";
    public static final String PREINSTALL_AVOID_TIME_BY_ID = "preinstall_avoid_time_";
    /** 多语言动态加载 **/

    public static final String CLOUD_MULTI_LANG = "multi_lang";
    public static final String MULTI_LANG_MAIN_SWITCH = "main_switch";
    public static final String MULTI_LANG_APK_URL= "mutil_apk_url_";
    public static final String MULTI_LANG_DOWNLOAD_WITHOUTWIFI = "download_without_wifi";

    // 备份页直接为facebook登录的概率
    public static final String PHOTO_TRIM_FACEBOOK_LOGIN_DIRECT_PROBABILITY = "photo_trim_facebook_login_direct_probability";

    // 结果页是否在WIFI环境下加载新闻
    public static final String B_ENABLE_NEWS_PRELOAD = "B_ENABLE_NEWS_PRELOAD";

    //是否显示主页applock按钮概率
    public static final String MAIN_SHOW_APPLOCK_RATE = "main_show_applock_rate";
}

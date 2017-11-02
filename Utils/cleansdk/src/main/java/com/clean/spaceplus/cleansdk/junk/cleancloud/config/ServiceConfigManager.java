package com.clean.spaceplus.cleansdk.junk.cleancloud.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.util.LanguageSelectionHelp;
import com.clean.spaceplus.cleansdk.util.SharePreferenceUtil;
import com.clean.spaceplus.cleansdk.util.bean.LanguageCountry;

import java.util.ArrayList;
import java.util.Calendar;

import space.network.util.RuntimeCheck;

/**
 * @author dongdong.huang
 * @Description: 服务配置管理
 * @date 2016/4/23 17:23
 * @copyright TCL-MIG
 */
public class ServiceConfigManager {
    public static final String SYSTEM_RISK_KEY_APPPRIVACY = ":system-risk/appprivacy";
    public static final String SYSTEM_RISK_KEY_SYS_VULNERABILITY = ":system-risk/sysvulnerability";


    private static Context context = null;
    private static final long HALF_DAY = 1000L * 60 * 60 * 12;
    private static final long FOURTEEN_DAY = 1000L * 60 * 60 * 24 * 14;
//    private static final String CLEAN_SMS_GROUP_DIALOG_ALERT = "clean_sms_dialog_alert_set_key";
//    private static final String CLEAN_CALL_LOG_GROUP_DIALOG_ALERT = "clean_call_log_dialog_alert_set_key";
//    private static final String UPDATE_DATE_RECORD = "UpdateDateRecord";
//    private static final String UPDATE_LIB_DATE_RECORD = "UpdateLibDateRecord";
//    private static final String UPDATE_SHOW_DATE_RECORD = "UpdateShowDateRecord";
//    private static final String NOTIFY_UNUSE_LONGTIME = "notify_unuse_longtime";
//    private static final String NOTIFY_VIDEO_CLEAN_TIME = "notify_video_clean_time";
//    private static final String NOTIFY_VIDEO_CLEAN_NOT_OPER_TIME = "notify_video_clean_not_oper_time";
//    private static final String NOTIFY_DOWNLOAD_MANAGER_LONGTIME = "notify_download_manager_longtime";
//    private static final String NOTIFY_DOWNLOAD_MANAGER_PRESIZE = "notify_download_manager_presize";
//    private static final String NOTIFY_DOWNLOAD_MANAGER_BLUETOOTH_PRESIZE = "notify_download_manager_bluetooth_presize";
//    private static final String ISFIRSTSTSHOWPROCESSCLEANTIP = "isFirstShowProcessCleanTip";
//    private static final String ISFIRSTJUNKPUSH = "isFirstJunkPush";
//    private static final String ISFIRSTINSTALLSHORTCUT = "isFirstInstallShortCut4.0";
//    private static final String ISHAVECLEANEDJUNKSTANDARD = "isHaveCleanedJunkStandard";
//    private static final String ISCLEANEDJUNKSTANDARDRE = "isCleanedJunkStandardReport";
//    private static final String ISHAVESCANEDJUNKSTANDARD = "isHaveScanedJunkStandard";
//    private static final String ISHAVECLEANEDJUNKADVANCED = "isHaveCleanedJunkAdvanced";
//    private static final String IS_HAVE_CLEANED_JUNK = "is_have_clean_junk";
//    private static final String IS_FIRST_CLEANED_JUNK_STANDARD = "is_first_cleaned_junk_standard";
//    private static final String LAST_SHOW_150M_DIALOG_TIME = "last_show_150m_dialog_time";
//    private static final String ISNEEDSHOWPROCSYSTEMTIP = "isNeedShowProcSystemTip";
//
//    private static final String ISFIRSTMAINUIEXIT = "isFirstMainUIExit";
//    private static final String SO_VERSION = "SoVersion_new";
    private static final String DAYTIME_OF_TODAY_CLEANED_SIZE = "DayTimeOfTodayCleanedSize";
    private static final String TODAY_CLEANED_SIZE = "TodayCleanedSize";
    private static final String TOTAL_CLEANED_SIZE = "TotalCleanedSize";
    private static final String MAX_CLEANED_SIZE = "MaxCleanedSize";
//    private static final String JUNKNOTIFYLEVEL = "JunkNotifyLevel";
//    private static final String JUNKNOTIFYTIME = "JunkNotifyTime";
//    private static final String LAST_BUG_FEED_COUNT = "LastBugFeedCount";
//    private static final String LAST_BUG_FEED_TIME = "LastBugFeedTime";
//    private static final String MEM_NOTIFY_MIN_PERCENTAGE = "MemNotifyMinPercentage";
//    private static final String MEM_NOTIFY_DEFAULT_SIZE = "MemNotifyDefaultPercentage";
//    private static final String SYSTEM_CACHE_SIZE_RECOMMEND = "SystemCacheSizeRecommend";
//    private static final String RECOMMEND_TASK_CLEAN_TIME = "RecommendTaskCleanTime";
//    private static final String INSUFFICIENT_STORAGE_NOTIFY_TIME = "InsufficientStorageNotifyTime";
//    private static final String DU_INSUFFICIENT_STORAGE_NOTIFY = "duInsufficientStorageNotify";
//    private static final String PRIORITY_INSUFFICIENT_STORAGE_NOTIFY = "priorityInsufficientStorageNotify";
//    private static final String STD_JUNK_RADOM_TIP_SHOW_TIME = "StdJunkRadomTipShowTime";
//    private static final String STD_JUNK_SCENE_TIP_SHOW_TIME = "StdJunkSceneTipShowTime";
//    private static final String MEDIA_STORE_SCAN_FINISH_TIME = "MediaStoreScanFinishTime";
//    private static final String SEND_INTENT_TO_ALARM_BG_SCAN = "SendIntentToAlarmBgScan";
//    private static final String SEND_INTENT_TO_ALARM_BG_SCAN_FIRST_JUNK = "SendIntentToAlarmBgScanFirstJunk";
//    private static final String SEND_INTENT_TO_ALARM_BG_SCAN_MEDIASTORE = "SendIntentToAlarmBgScanMediastore";
//    private static final String SEND_INTENT_TO_ALARM_BG_SCAN_WIFI_CONN = "SendIntentToAlarmBgScanWifiConn";
//    private static final String FUNC_RECOM_SCAN_FINISH_TIME = "FuncRecomScanFinishTime";
//    private static final String ONE_TAP_SHOW_RECOM_TIME = "OneTapShowRecomTime";
//    private static final String TODAY_ONE_TAP_SHOW_RECOM_TIMES = "TodayOneTapShowRecomTimes";
//    private static final String APK_CLEAN_REMINDER_PUSH = "ApkCleanReminderPush";
//    private static final String STD_JUNK_CONSECUTIVE_IGNORE_TIMES = "StdJunkConsecutiveIgnoreTimes";
//    private static final String CHECK_PUSH_INTERVAL_TIME = "CheckPushIntervalTime";
//    private static final String CACHE_SCAN_INTERVAL_TIME = "CacheScanIntervalTime";
//    private static final String ISALLOWEDREPORTINFO = "isAllowedReportInfo";
//    private static final String ISFIRSTSHOWRATEUSDIALOG = "is_first_show_rate_us_dialog";
//    private static final String LAST_NOTIFICATION_SHOW_TIME = "lastNotification_showtime";
//    private static final String IS_LED_LIGHT_NEW_USER_FLAG = "is_led_light_new_user_flag";
//    private static final String IS_FIRST_SHOW_150M_RATE_US_DIALOG = "is_first_show_150m_rate_us_dialog";
//    private static final String IS_FIRST_SHOW_1_5G_RATE_US_DIALOG = "is_first_show_1_5g_rate_us_dialog";
//    private static final String HAS_RATED_US = "has_rated_us";
//
//    private static final String LASTSHOWLIKEUSDLGTIME = "lastshowlikeusdlgtime";
//    private static final String ISKILLPROCESS_SCREENOFF = "killprocess_screenoff";
//    private static final String ISKILLPROCESS_AUTO_TOAST = "killprocess_screenoff_toast";
//    private static final String ISMEMUSEDREMINDER = "mem_used_reminder";
//    private static final String IS_MOVING_REMINDER = "moving_reminder";
//    private static final String FREERAM_SCREENOFF = "freeram_srceenoff";
//    private static final String CLEANCACHE_SWITCH = "clean_cache_switch";
//    private static final String USEDSPACE_SWITCH = "used_space_switch";
    private static final String APKJUNKSCAN_SWITCH = "apk_junk_scan_switch";
    private static final String LANGUAGE_SELECTED = "language_selected";
    private static final String COUNTRY_SELECTED = "country_selected";
//    private static final String APPVERSIONCODE = "AppVersionCode";
//    private static final String LASTREPORTSTORAGEUSAGEINFOTIME = "LastReportStorageUsageInfoTime";
//    private static final String ISFIRSTREPORTOBBFOLDERINFO = "isFirstReportObbFolderInfo";
//    private static final String ISFIRSTREPORTSDFOLDERSINFO = "isFirstReportSdFoldersInfo";
//    private static final String IS_FIRST_TOUCHER_CLICK = "isFirstToucherClick";
//    private static final String APPCHANNELID = "appChannelId";
//    private static final String APPCHANNELID2 = "appChannelId2";
//    private static final String CLEANAPPDATAEXAMPLE = "CleanAppDataExample";
//    private static final String CAMPAIGNTRACKINGTIME = "CampaignTrackingTime";
//    private static final String CAMPAIGNTRACKINGSOURCE = "CampaignTrackingSource";
//    private static final String CRASHFEEDBACKINTERVAL = "CrashFeedbackInterval";
//    private static final String INFOCREPORTPRIVATEDATAAVAILABLE = "InfocReportAvailable";
//    private static final String SHOWRATEUSTIME = "ShowRateUsTime";
//    private static final String INJECT_MONITOR_ERROR_TIME = "inject_monitor_error_time";
//    private static final String CLOUD_CFG_VERSION = "cloud_cfg_version";
//    private static final String CLOUD_UPDATE_TIME = "cloud_update_time";
//
//    private static final String SHOW_APPLY_FOR_ROOT_DIALOG = "show_apply_for_root_dialog";
//    private static final String AUTO_GET_ROOT_PERMISSION = "auto_get_root_permission";
//    private static final String FLOAT_WINDOW_ENABLE = "float_window_enable";
//    private static final String FLOAT_WINDOW_MANUAL_SETTING = "float_window_manual";
//    private static final String CLEANMASTER_ID = "cmidcmidcmid";
//    private static final String FLOAT_WINDOW_MAGIC_SWEEP_ENABLE = "float_window_magic_sweep_enable";
//    private static final String FLOAT_WINDOW_MAGIC_SWEEP_SOUNDS_ENABLE = "float_window_magic_sweep_sounds_enable";
//    private static final String FLOAT_WINDOW_ONLY_IN_LAUNCHER = "float_window_only_in_launcher";
//    private static final String FLOAT_WINDOW_NEW = "float_window_new";
//    private static final String FLOAT_WINDOW_FLOG = "float_window_flog";
//    private static final String FLOAT_WINDOW_FLOG_ONCESHOW = "float_window_flog_onceshow";
//    private static final String FLOAT_WINDOW_FLOG_ONCESHOW_TIME = "float_window_flog_onceshow_time";
//    private static final String FLOAT_WINDOW_FLOG_ONCESHOW_CLICK = "float_window_flog_onceshow_click";
//    private static final String FLOAT_WINDOW_FLOG_NOREMIND = "float_window_flog_noremind";
//    private static final String FLOAT_WINDOW_ENHANCE_WIFI_ACCURATE = "float_window_enhance_wifi_accurate";
//    private static final String FLOAT_WINDOW_TIPS_SHOWING = "float_window_tips_showing";
//    private static final String FLOAT_WINDOW_WORLD_CUP_ENABLE = "float_window_world_cup_enable";
//    private static final String FLOAT_WINDOW_LINK_ENABLE = "float_window_link_enable";
//    private static final String FLOAT_WINDOW_FLOW_ENABLE = "float_window_flow_enable";
//    private static final String KILL_PERMANENT_SELF = "kill_permanet_self";
//    private static final String FLOAT_LAST_REPORT_ACTIVE_TIME = "float_last_report_active_time";
//    private static final String LAST_BATCH_REPORT_TIME = "last_batch_report_time";
//
//    private static final String FLOAT_WINDOW_WEATHER_ENABLE = "float_window_weather_enable";
//    private static final String FLOAT_WINDOW_WEATHER_CAN_READ_CLOUD_SWITCH = "float_window_weather_can_read_cloud_switch";
//    private static final String WEATHER_LOCATION_CAN_READ_CLOUD_SWITCH = "weatherLocation_can_read_cloud_switch";
//    private static final String FLOAT_WINDOW_WEATHER_MORNING_ENABLE = "float_window_weather_morning_enable";
//    private static final String FLOAT_WINDOW_WEATHER_TEMPERATURE_INDEX = "float_window_weather_temperature_centigrade";
//    private static final String FLOAT_WINDOW_WEATHER_WIND_SPEED_INDEX = "float_window_weather_wind_speed_km";
//    private static final String FLOAT_WIFI_FIRST_REPORT_TIME = "float_wifi_first_report_time";
//    private static final String LAST_REFRESH_APP_WATCH_TIME = "LastRefreshAppWatchTime";
//    private static final String LAST_CLEAN_JUNK_PROCESS_TIME = "LastCleanJunkProcessTime";
//
//    private static final String BATTERY_DOCTOR_FLOAT_DAY_TIME = "battery_doctor_float_day_time";
//    private static final String BATTERY_DOCTOR_FLOAT_HOUR_TIME = "battery_doctor_float_hour_time";
//    private static final String BATTERY_DOCTOR_FLOAT_SCAN_RESULT = "battery_doctor_float_scan_result";
//    private static final String BATTERY_DOCTOR_FLOAT_SCAN_TIMES = "battery_doctor_float_scan_times";
//
//    private static final String BATTERY_DOCTOR_NOTIFYCATION_CLICK_TIME = "bd_notify_click_time";
//    private static final String BATTERY_DRAINING_SCAN_TIME = "battery_draining_scan_time";
//    private static final String BATTERY_DOCTOR_GOTO_GP_TIME = "battery_doctor_goto_gp_time";
//    private static final String BATTERY_DOCTOR_AD_NORMAL_SHOW_NUM = "battery_doctor_ad_normal_show_num";
//    private static final String REPORTACTIVEPREFIX = "ra_";
    private static final String FILEVERSIONPREFIX = "fv_";
//    private static final String INFOC_PUB_DATA_SEC_PREFIX = "ifcpds_";
//    private static final String NOWVERSIONFIRSTSTARTTIMEPREFIX = "nvfst_";
//    private static final String FIRSTSCANPREFIX = "fstscan_";
//    private static final String FIRSTCLEANPREFIX = "fstclean_";
//    private static final String MIUI_ROOT_FILTER_PREFIX = "mrfp_";
//    private static final String DELETE_FAILED_HASH_HEAD = "dfhh_";
//    private static final String IS_FIRST_LAUNCH_ABOUT_ACTIVITY = "isFirstLauncheAboutActivity";
//    private static final String IS_FIRST_LAUNCH_PROCESS_ACTIVITY = "isFirstLauncheProcessActivity";
//
//    private static final String LAST_FLOAT_Y_POSITION = "LAST_FLOAT_Y_POSITION";
//    private static final String LAST_FLOAT_X_POSITION = "LAST_FLOAT_X_POSITION";
//    private static final String LAST_MOVE_INSTALL_APP = "LAST_MOVE_INSTALL_APP";
//    private static final String MOVE_INSTALL_TIMES = "MOVE_INSTALL_TIMES";
//    private static final String SAFE_CLEAN_TIP_SHOW_TIMES = "SAFE_CLEAN_TIP_SHOW_TIMES";
//
//    private static final String BATTERYDOCTOR_AD_IGNORE_CHARGE = "batterydoctor_ad_ignore_charge";
//    private static final String BATTERYDOCTOR_AD_IGNORE_DRAIN = "batterydoctor_ad_ignore_drain";
//    private static final String GAMEBOOST_RECOMMEND_AD_IGNORE = "gameboost_recommend_ad_ignore";
//    private static final String ANTI_THEFT_CMSE_AD_IGNORE = "anti_theft_cmse_ad_ignore";
//    private static final String PHOTO_GRID_AD_IGNORE = "photo_grid_ad_ignore";
//    private static final String RECOMMEND_CMB_NOTIFICATION_IGNORE = "recommend_cmb_notificaiton_ignore"; // 通知栏通过垃圾处理推cmb
//    private static final String RECOMMEND_CMB_FLOAT_IGNORE = "cmb_float_ignore"; // 通知栏通过垃圾处理推cmb
//    private static final String RECOMMEND_CMB_DETAIL_IGNORE = "recommend_cmb_detail_ignore";// 卸载页面结果页安全页面推cmb的忽略
//    private static final String RECOMMEND_CMB_SERCURITY_BASIC_IGNORE = "recommend_cmb_security_basic_ignore";
//    private static final String RECOMMEND_CMB_SERCURITY_MALICOUS_IGNORE = "cmb_sc_mal_ig";
//    private static final String RECOMMEND_CMB_FLOAT_LAST_TIME = "cmb_float_last_time"; // 悬浮窗推cmb弹tip最后一次时间
//    private static final String RECOMMEND_CMB_BROWSER_CLOSE_CHECK = "recommend_cmb_browser_close_check";
//    private static final String RECOMMEND_CMB_UNINSTALL_IGNORE = "recommend_cmb_uninstall_ignore";
//
//    private static final String RECOMMEND_CMBACKUP_BASIC_IGNORE = "recommend_cmbckup_basic_ignore";
//    private static final String RECOMMEND_CMFAMILY_BASIC_IGNORE = "recommend_cmfamily_basic_ignore";
//    private static final String FEEDBACK_CONTACT_FOR_CN = "FEEDBACK_CONTACT_FOR_CN";
//    private static final String FEEDBACK_CONTACT_FOR_EMAIL = "FEEDBACK_CONTACT_FOR_EMAIL";
    private static final String FILTER_LIST_VERSION = "filter_list_version";
//    private static final String LOCATION_LATITUDE_FROM_SERVER = "location_latitude_from_server";
//    private static final String LOCATION_LATITUDE_3G_FROM_SERVER = "location_latitude_3G_from_server";
//
//    private static final String LOCATION_LONGITUDE_FROM_SERVER = "location_longitude_from_server";
//    private static final String LOCATION_LONGITUDE_3G_FROM_SERVER = "location_longitude_3G_from_server";
//    private static final String LOCATION_LATITUDE_FROM_CLIENT = "location_latitude_from_client";
//    private static final String LOCATION_LONGITUDE_FROM_CLIENT = "location_longitude_from_client";
//
//    private static final String LOCATION_WIFI_MAC = "location_wifi_mac";
//
//    private static final String LOCATION_CITY_CODE = "location_city_code";
//    private static final String LOCATION_CITY_3G_CODE = "location_city_3g_code";
//
//    private static final String LAST_LOCATION_UPDATE_TIME = "last_location_update_time";
//
//    private static final String LAST_GET_CLIENTIP_UPDATE_TIME = "last_get_clientip_update_time";
//
//    private static final String LAST_WEATHER_UPDATE_TIME = "last_weather_update_time";
//
//    private static final String CLIENT_ID = "client_id";
//
//    private static final String LAST_FILTER_VERSION_REPORT_TIME = "last_filter_version_report_time";
//
//    private static final String CM_VERSION_DELETE_OLDER_DB = "cm_version_delete_older_db";
//    private static final String FIRST_USE_JUNK_STANDARD = "first_use_junk_standard";
//    private static final String FIRST_USE_JUNK_ADVANCED = "first_use_junk_advanced";
//    //	private static final String JUNK_CLEAN_HISTORY_DATA = "junk_clean_history_data";
//    private static final String CM_FIRST_INSTALL_TIME = "cm_first_install_time";
//    private static final String LAST_USE_JUNK_TIME = "last_use_junk_time";
//    private static final String TURNED_INTO_JUNK = "turn_into_junk";
//    private static final String CM_LAST_UPDATE_USER_APPS_DESCRIPTION = "cm_last_update_user_apps_description";
    //	private static final String CM_SHOW_NOTIFICATION_FOR_APP_FREQUENCE = "cm_show_notification_for_app_frequence_";
//	private static final String CM_LAST_RESIDUAL_TO_APP = "cm_last_residual_to_app";
//	private static final String CM_FLOAT_WINDOW_FIRST_SHOW = "cm_float_window_first_show";
    private static final String CM_IS_FIRST_IN_APPMANAGER = "cm_is_first_in_appmanager";
    //	private static final String CM_IS_FIRST_SHOW_FREQUENCE_NOTIFICATION = "cm_is_first_show_frequence_notification";
//    private static final String CM_LAST_UN_UNINSTALL_APP_FROM_RECOMMAND_ACTIVITY = "cm_last_un_uninstall_app_from_recommand_activity";
//    private static final String KEY_NOT_SHOW_APK_DELETE_CONFRIM_DIALOG = "not_show_apk_delete_confrim_dialog";
//    private static final String CM_APP_NO_USE_NOTIFY = "cm_app_no_use_notify";
//    private static final String CM_NEXT_RECOMMAND_GAME_UNINSTALL_TIME = "cm_next_recommand_game_uninstall_time";
//    private static final String CM_NEXT_RECOMMAND_GAME_UNINSTALL_DIALOG = "cm_next_recommand_game_uninstall_dialog";
//    private static final String APPMANAGER_LAST_OPEN_TIME = "APPMANAGER_LAST_OPEN_TIME";
//    private static final String APPMANAGER_HAS_REDDOT = "APPMANAGER_HAS_REDDOT";
//    private static final String JUNK_PROCESS_DEFAULT_CHECKED = "JUNK_PROCESS_DEFAULT_CHECKED";
//    //	private static final String APPMANAGER_LAST_UNUSED_REDDOT = "APPMANAGER_LAST_UNUSED_REDDOT";
//    private static final String MARKET_SHOW_AT_FIRST_MARK = "MARKET_SHOW_AT_FIRST_MARK";
//
//    private static final String FESTIVAL_REQUEST = "festival_request";
//
//    private static final String NOTIFICATION_SWITCH_PREFIX = "notification_switch_";
//    //	private static final String NOTIFICATION_MESSAGE_BOX_STATUS = "notification_message_box_status";
//    // 1tap推荐 上一次清理时间
//    private static final String ONE_TAP_RECOM_LAST_CLEAN_TIME = "one_tap_recom_last_clean_time";
//    private static final String ONE_TAP_RECOM_LAST_CLICK_TYPE = "one_tap_recom_last_clicK_type";
//
//    private static final String CM_ONETAP_MAKER = "cm_onetap_maker";
//    public static final int CM_ONETAP_MAKER_PASSIVE = 1; // 被动
//    public static final int CM_ONETAP_MAKER_INITIATIVE = 2; // 主动
//    public static final int CM_ONETAP_MAKER_EXISTED = 3; // 已经存在
//
//    private static final String CM_APP_MANAGER_SHORTCUT_MAKER = "cm_app_manager_shortcut_maker";
//    public static final int CM_APP_MANAGER_SHORTCUT_MAKER_PASSIVE = 1; // 被动
//    public static final int CM_APP_MANAGER_SHORTCUT_MAKER_INITIATIVE = 2; // 主动
//
//    //	private static final String CM_APP_MANAGER_SHORTCUT_TIPED = "cm_app_manager_shortcut_tiped"; // 创建快捷方式是否提示过
//    private static final String CM_APP_MANAGER_OPER = "cm_app_manager_oper"; // 在软件管理里进行过下载
//    // 升级或卸载操作
//
//    public static final String CM_HAVE_NEW_APK_STRING = "cm_have_new_apk_by_auto_update_in_service";
//
//    public static final String CM_FIXLAUNCHER_PREFIX = "fixlauncher_";
//
//    private static final String LOCATION_USE_AUTO = "location_use_auto";
//
//    // CM内存消耗与cpu占用上报时间保存
//    private static final String CM_MEMORY_AND_CPU_REPORT_TIME = "last_mem_cpu_report_time";
//
//    private static final String CM_CRASH_SO_FAILED_REPORTED = "crash_so_reported";
//
//    private static final String CM_SERVICE_FIRST_STARTED = "1983";
//    private static final String FILTER_LAST_UPDATE_TIME = "filter_last_update_time";
//
//    private static final String POLL_GET_VERSIONS_API_TIME = "poll_get_versions_api_time";
//
//    private static final String ALERT_APP_NEXT_SHOW_TIME = "alert_app_next_show_time_";
//
//    private static final String CM_DOWNLOAD_ZIP_FILE_HAVE_WIFI_TASK_WAITING = "zip_file_have_wifi_task_waiting";
//
//    private static final String CM_ZIP_FILE_VERSION = "cm_zip_file_version";
//
//    private static final String CM_PUSH_NOTIFICATION_DATA_VERSION = "cm_push_notification_item_version";
//
//    private static final String CM_INTERNAL_PUSH_DATA_VERSION = "cm_internal_push_data_version";
//
//    private static final String RECENT_CRASH_TIME_ONE = "recent_crash_time_one";
//    private static final String RECENT_CRASH_TIME_TWO = "recent_crash_time_two";
//    private static final String RECENT_CRASH_TIME_THREE = "recent_crash_time_three";
//
//    private static final String FREEZE_FIRST_HAS_NO_COULD_STOPPED = "freeze_first_has_no_could_stopped";
//    private static final String FREEZE_FIRST_USE_APP_FUNCTION = "freeze_first_use_app_function";
//    private static final String REPORT_NON_MARKET_FLAG = "reported_non_market_flag";
//
//    private static final String NEW_APP_TO_UNINSTALL_FLAG = "new_app_to_uninstall_flag";
//    private static final String NEW_APP_TO_UNINSTALL_FLAG_LASTTIME = "new_app_to_uninstall_flag_last_time";
//
//    private static final String LAST_USER_PLAY_GAME = "last_user_play_game"; //保存用户最后玩得游戏
//    // 游戏盒子快捷方式
//    private static final String GAMEBOX_SHORTCUT_ADD = "gamebox_shortcut_added";
//    private static final String GAMEBOX_SHORTCUT_FIXED_BY_BOX = "gamebox_shortcut_fixed_by_box";
//    private static final String GAMEBOX_SHORTCUT_FIXED_BY_GAME_MANAGE = "gamebox_shortcut_fixed_by_game_manage";
//    private static final String GAME_BOOST_DIALOG_SHOW = "game_boost_dialog_show";
//    private static final String GAME_BOOST_DIALOG_SHOW_COUNT = "game_boost_dialog_show_count";
//    private static final String GAME_BOOST_DIALOG_SHOW_TIME = "game_boost_dialog_show_time";
//    private static final String GAME_BOOST_GUIDE__TIPS_SHOW_COUNT = "game_boost_guide_tips_show_count";
//    private static final String GAME_BOOST_GUIDE_TIPS_SHOW_TIME = "game_boost_guide_tips_show_time";
//    private static final String GAME_BOOST_NEED_LOAD_UNBOOST_GAME = "game_boost_load_unboost_game";
//    private static final String FIRST_USE_PHOTO_GRID = "first_use_photo_grid";
//    private static final String GAMEBOX_ANIMATION_SHOW = "gamebox_animation_show";
//    private static final String GAMEBOX_GUIDE_DIALOG_IS_SHOWN = "gamebox_guide_dialog_is_shown";
//    private static final String GAMEBOX_ENTER_TIME = "gamebox_enter_time";
//    private static final String GAMEBOX_ENTER_TIME_HISTORY = "gamebox_enter_time_history";
//    private static final String GAMEBOX_ENTER_TIME_MILLIS = "gamebox_enter_time_millis";
//    private static final String GAMEBOX_NEED_FIX_ICON = "gamebox_need_fix_icon";
//    private static final String GAMEBOX_UPDATE_TIME = "gamebox_update_time";
//    private static final String GAMEBOX_IS_SHOW_FIX_ICON = "gamebox_is_show_fix_icon";
//
//    //推送游戏包展示次数  格式 packageName + "||" + times
//    private static final String GAMEBOX_PUSH_GAME_PACKAGE_SHOW_TIMES = "gamebox_push_game_package_show_times";
//
//    private static final String GAMEBOX_PUSH_SINGLE_GAME_SHOW_TIME = "gamebox_push_single_game_show_time";
//    private static final String GAMEBOX_SHORTCUT_CREATE_TIME_AT_5_6 = "gamebox_shortcut_create_time_at_5_6";
//    private static final String GAMEBOX_CM_GAME_MATCH_REPORTED = "gamebox_cm_game_match_reported";
//    //  游戏盒子清理process大小
//    private static final String GAMEBOX_CLEAN_PROCESS ="gamebox_clean_process_size";
//
//    // 游戏盒子开启加速source 与 cm_game_boost source来源相同（之前的key命名有问题）
//    private static final String GAMEBOX_OPEN_BOOST_SOURCE = "gamebox_shortcut_create_source";
//    // 游戏盒子快捷方式创建source
//    private static final String GAMEBOX_SHORTCUT_CREATE_SOURCE = "gb_shortcut_create_source";
//    // 游戏盒子创建时间
//    private static final String GAMEBOX_SHORTCUT_CREATE_TIME = "gamebox_shortcut_create_time";
//    // 创建游戏盒子48小时内未使用通知栏提示
//    private static final String GAMEBOX_UNUSE_NOTIFY_SHOW = "gamebox_unuse_notify_show";
//
//    // 用户上次玩的游戏
//    private static final String GAMEBOX_LAST_PLAY_GAME = "gamebox_last_play_game";
//    // 游戏盒子process dialgo
//    private static final String GAME_BOX_PROCESS_DIALOG = "game_box_pr_d";
//
//    //针对指定桌面&创建失败场景，引导开启常驻通知栏\悬浮窗
//    private static final String GAME_BOX_FAILURE_SHOW_DIALOG = "game_box_failure_show_dialog";
//    private static final String GAME_BOX_IS_ACCORD_LAUNCHER = "game_box_is_accord_launcher";
//    private static final String GAME_BOX_FAILURE_SHOW_DIALOG_TIME = "game_box_failure_show_dialog_times";
//    private static final String GAME_BOX_FAILURE_SHOW_DIALOG_COUNT = "game_box_failure_show_dialog_count";
//
//    // 上一次悬浮窗游戏盒子红点显示时间
//    private static final String LAST_RED_POINT_BY_GAMEBOX_SHOW_TIME = "last_red_point_by_gamebox_show_time";
//    // 有新游戏进入游戏盒子
//    private static final String NEW_GAME_FLAG_FOR_GAMEBOX = "new_game_flag_for_gamebox";
//    private static final String NEW_MSG_GAME_FROM_GAMEBOX_NOTIFY = "new_msg_game_from_gamebox_notify";
//    private static final String NEW_MSG_GAME_FROM_GAMEBOX_NOTIFY_SHOW_COUNT = "new_msg_game_from_gamebox_notify_count";
//
//    // 游戏盒子杀进程最后上报时间
//    private static final String LAST_GAMEBOX_AFTER_KILL_REPORT_TIME = "last_gamebox_after_kill_report_time";
//
//    private static final String PLAY_GAME_CPU_REPORT_COUNT = "play_game_cpu_report_count";
//    private static final String PLAY_GAME_CPU_REPORT_TIME = "play_game_cpu_report_time";
//
//    // 游戏盒子打开检测特定机型省电模式开关
//    private static final String LAST_GAMEBOX_POWER_SAVE_SWITCH_REPORT_TIME = "last_gamebox_power_save_switch_report_time";
//
//    // 图标修复逻辑是否运行服务启动时自动执行
//    private static final String ALLOW_FIX_SHORTCUT_ICON_AT_SERVICE_START = "a_f_s_i_a_s_s";
//
//    // 游戏推荐卸载规则包名
//    private static final String CM_RECOMMAND_GAME_UNINSTALL_TIMES = "cm_recommand_game_uninstall_times";
//
//    // 上一次退出游戏的时间
//    public static final String LAST_EXIT_GAME_TIME = "last_exit_game_time";
//
//    // 第一次扫描完全的游戏的数量
////	private static final String FIRST_SCAN_ALL_GAME_COUNT = "first_scan_all_game_count";
//    // 用户手机的游戏数量（非实时），首次扫描或每天回扫时会更新，用于游戏盒子与快捷通知栏对接
//    private static final String USER_GAME_COUNT = "user_game_count";
//    // 重置内存页面引导游戏加速泡泡
//    private static final String RESET_PM_GAME_TOAST = "reset_pm_game_toast";
//
//    private static final String FIRST_ALL_GAME_HAS_SCANED = "first_all_game_has_scaned";
//    private static final String FIRST_ALL_GAME_SCAN_FINISHED = "first_all_game_scan_finished";
//    private static final String PM_RECOMMEND_GAMEBOX_DIALOG_SHOW = "pm_recommend_game_dialog_show";
//    private static final String PM_RECOMMEND_WHITE_GAMEBOX_DIALOG_SHOW = "pm_recommend_white_game_dialog_show";
//    // 上一次扫描游戏时的CM版本号
//    private static final String LAST_SCAN_GAME_OUR_VERSION_CODE = "last_scan_game_our_version_code";
//
//    // 上一次插电扫描游戏时间
//    private static final String LAST_SCAN_GAMES_IN_POWER_CONNNECTED = "last_scan_games_in_power_connected";
//    // 第一次进入内存页面
//    private static final String FIRST_ENTER_PROCESS_MANAGER_ACTIVITY = "first_enter_process_manager_activity";
//    // 是否有新的游戏活动消息
//    private static final String HAS_GAME_NEW_MSG_FOR_GAMEBOX = "has_game_new_msg_for_gamebox";
//    // 上一次上报游戏盒子位置时间
//    private static final String LAST_REPORT_GAMEBOX_POSITION_TIME = "last_report_gamebox_position_time";
//
//    private static final String GAME_PLAY_TIME_REDUCE_TIMES = "game_play_time_reduce_times";
//    // 上一次退出游戏显示问题类型和时间
//    private static final String LAST_EXIT_GAME_SHOW_PROBLEM = "last_exit_game_show_problem";
//    // 退出游戏未处理问题次数
//    private static final String EXIT_GAME_UNHANDLE_COUNT_R1 = "exit_game_unhandle_count_r1";
//    // 记录上次未处理的时间
//    private static final String EXIT_GAME_UNHANDLE_DAY = "exit_game_unhandle_day";
//    // 退出游戏省电模式提示次数
//    private static final String EXIT_GAME_POWER_SAVE_COUNT = "exit_game_power_save_count";
//    // 游戏退出场景，今天已弹出的问题弹框次数
//    private static final String EXIT_GAME_PROMPT_COUNT_IN_TODAY = "exit_game_prompt_count_in_today";
//    // 游戏退出场景上一次的点击时间
//    private static final String EXIT_GAME_CLICK_LAST_TIME = "exit_game_click_last_time";
//    // 上一次悬浮窗显示TIPS使用盒子时间
//    private static final String LAST_SHOW_USE_GAMEBOX_BY_FLOAT_DIALOG = "last_show_use_gamebox_by_float_dialog";
//    // 悬浮窗显示TIPS使用盒子次数
//    private static final String SHOW_USE_GAMEBOX_BY_FLOAT_DIALOG_COUNT = "show_use_gamebox_by_float_dialog_count";
//    // 新安装一款游戏悬浮窗显示TIPS使用盒子时间
//    private static final String NEW_GAME_SHOW_TIPS_BY_FLOAT_DIALOG = "new_game_show_tips_by_float_dialog";
//    // 快捷通知栏对游戏盒子的拉活：新安装一款游戏时，快捷通知栏显示TIPS使用盒子时间
//    private static final String NEW_GAME_SHOW_TIPS_BY_PERMANENT_NOTIFICATION = "new_game_show_tips_by_permanent_notification";
//    // 快捷通知栏对游戏盒子的拉活时间
//    private static final String GAMEBOX_GUIDE_AT_PERMANENT_NOTIFICATION_TIME = "gamebox_guide_at_permanent_notification_time";
//    // 快捷通知栏对游戏盒子的拉活次数：当天内
//    private static final String GAMEBOX_GUIDE_COUNT_PER_DAY_AT_PERMANENT_NOTIFICATION = "gamebox_guide_count_per_day_at_permanent_notification";
//    // 记录是否游戏用户
//    private static final String IS_GAME_USER = "is_game_user";
//    // 通过游戏场景引导开启快捷通知栏+盒子入口的次数
//    private static final String OPEN_PERMANENT_NOTIFICATION_AND_GAMEBOX_GUIDE_COUNT = "open_permanent_notification_and_gamebox_guide_count";
//    // 不符合“引导开启快捷通知栏&游戏盒子入口”条件的上报时间
//    private static final String OPEN_PERMANENT_NOTIFICATION_AND_GAMEBOX_GUIDE_FAIL_REPORT_TIME = "open_permanent_notification_and_gamebox_guide_fail_report_time";
//    // 7天内玩游戏的天数
//    private static final String PLAY_GAME_COUNT_IN_A_WEEK = "play_game_count_in_a_week";
//    // 是否符合游戏用户条件原因
//    private static final String REASON_OF_IS_GAME_USER_OR_NOT = "reason_of_is_game_user_or_not";
//    // 本地缓存的游戏加速比
//    private static final String GAME_BOOST_PERCENT = "game_boost_percent";
//    // 本地缓存的游戏加速比最小值，用于与云端比较，判断是否需要使用云端值
//    private static final String GAME_BOOST_PERCENT_MIN = "game_boost_percent_min";
//    // 本地缓存的游戏加速比最大值，用于与云端比较，判断是否需要使用云端值
//    private static final String GAME_BOOST_PERCENT_MAX = "game_boost_percent_max";
//
//    // 用户的游戏时间记录，当天内只记录第一次
//    private static final String LAST_PLAY_GAME_TIMES ="last_play_game_times";
//    // 用户的玩游戏列表
//    private static final String LAST_PLAY_GAME_LIST ="last_play_game_list";
//    // 是否弹出自动进入省电模式提示框
//    private static final String SHOW_APPLY_FOR_AUTO_ENTER_POWER_SAVE_MODE_DIALOG = "show_apply_for_auto_enter_power_save_mode_dialog";
//    // 是否自动进入省电模式
//    private static final String AUTO_ENTER_POWER_SAVE_MODE_PERMISSION = "auto_enter_power_save_mode_permission";
//    // 是否强制展示省电模式被动弹框
//    private static final String FORCE_SHOW_GAME_PROBLEM_POWER_SAVING_ENABLED = "force_show_game_problem_power_saving_enabled";
//    // 是否首次强制省电模式被动弹框
//    private static final String FIRST_FORCE_SHOW_GAME_PROBLEM_POWER_SAVING = "first_force_show_game_problem_power_saving";
//    // 是否已显示新用户长期未开启加速拉活通知栏
//    private static final String NEW_USER_NOT_OPEN_BOOST_NOTIFICATION_SHOWED = "new_user_not_open_boost_notification_showed";
//
//    /**------------------Game Boost V5.9.3支持拉活/轻游戏推荐------------------*/
//    //桌面下沉浮层连续不点击次数记录
//    public static final String COUNT_OF_NOT_CLICK_FLOAT_WINDOW_SHOW = "count_of_not_click_float_window_show";
//    //弹泡连续不点击次数记录
//    public static final String COUNT_OF_NOT_CLICK_GAME_EXIT_DIALOG = "count_of_not_click_game_exit_dialog";
//    //动作列表轮询索引
//    public static final String GAMEBOX_ACTION_LIST_ROLL_POLL_INDEX = "gamebox_action_list_roll_poll_index";
//    //动作列表上一次轮询索引
//    public static final String GAMEBOX_ACTION_LIST_LAST_ROLL_POLL_INDEX = "gamebox_action_list_last_roll_poll_index";
//    //动作列表上一次轮询子key
//    public static final String GAMEBOX_ACTION_LIST_LAST_ROLL_POLL_SUBKEY = "gamebox_action_list_last_roll_poll_subkey";
//    /**------------------Game Boost V5.9.3支持拉活/轻游戏推荐------------------*/
//
//    //保存服务端当次的送出策略，用于客户端下次请求时回传
//    private static final String GAME_BOX_OT_RC ="game_box_ot_rc";
//    //用户第一次打开游戏盒子的时间
//    public static final String FIRST_OPEN_GAME_BOX_TIME = "first_open_game_box_time";
//    //被动入口，预加载推送内容的广告数据的时间
//    public static final String EXIT_GAME_PUSH_APP_PRE_LOAD_TIME = "exit_game_push_app_pre_load_time";
//    //被动入口，预加载推送内容没有数据的原因
//    public static final String EXIT_GAME_PUSH_APP_NO_DATA_REASON = "exit_game_push_app_no_data_reason";
//    //节日运营，预加载推送内容的广告数据的时间
//    public static final String FESTIVAL_PUSH_APP_PRE_LOAD_TIME = "festival_push_app_pre_load_time";
//    //节日运营，预加载推送内容没有数据的原因
//    public static final String FESTIVAL_PUSH_APP_NO_DATA_REASON = "festival_push_app_no_data_reason";
//    //频次控制逻辑：“单用户X天show Y次卡片”逻辑的时间记录
//    public static final String GAME_EXIT_PUSH_APP_CARD_FREQUENCY_CONTROL_TIME = "game_exit_push_app_card_freq_ctrl_time";
//    //频次控制逻辑：“单用户X天show Y次卡片”逻辑的次数记录
//    public static final String COUNT_OF_GAME_EXIT_PUSH_APP_CARD_SHOW_FREQUENCY_CONTROL = "count_of_game_exit_push_app_card_show_freq_ctrl";
//    //上一次预先加载节日运营内容推荐的时间
//    public static final String LAST_FESTIVAL_PUSH_APP_PRE_LOAD_TIME = "last_festival_push_app_pre_load_time";
//    //节日运营的内容推荐频次控制逻辑：“X天show Y次”逻辑的时间记录
//    public static final String FESTIVAL_PUSH_APP_CARD_FREQ_CTRL_TIME = "festival_push_app_card_freq_ctrl_time";
//    //节日运营的内容推荐频次控制逻辑：“X天show Y次”逻辑的次数记录
//    public static final String COUNT_OF_FESTIVAL_PUSH_APP_CARD_SHOW_FREQ_CTRL = "count_of_festival_push_app_card_show_freq_ctrl";
//
//    /** ***************** 安全隐私 */
//    // 漏洞修复
//    private static final String IGNORE_PROMOTION_DUBA = "ignore_promotion_duba";
//
//    // /< 安全扫描, 启发式扫描开关(目前只作用于sdcard scan)
//    private static final String SECURITY_SCAN_HEURISTIC_ENABLE = "cm_security_scan_heuristic_enable";
//    private static final String SECURITY_SCAN_AUTO_HEURISTIC_ENABLE = "cm_security_scan_auto_heuristic_enable";
//
//    // /< 安天数据版本
//    private static final String SECURITY_ANTIY_DATA_VERSION = "cm_security_antiy_data_version";
//
//    private static final String SECURITY_LAST_SD_SCAN_TIME = "cm_security_last_sd_scan_time";
//
//    private static final String SECURITY_FIRST_ENTER_TIMEWALL_TIME = "cm_security_first_enter_timewall_time";
//
//    // 安装监控
//    private static final String SECURITY_INSTALL_MONITOR_ENABLE = "cm_security_install_monitor_enable";
//    private static final String SECURITY_INSTALL_MONITOR_SWITCH_BY_USER = "cm_security_install_monitor_switch_by_user";
//
//    // 安全网址浏览
//    private static final String SECURITY_SAFE_BROWSING_ENABLE = "cm_security_safe_browsing_enable";
//    // 安全网址浏览提示
//    private static final String SECURITY_SAFE_BROWSING_TIPS = "cm_security_safe_browsing_tips";
//    // 和其他安全软件重复弹窗提醒
//    private static final String SECURITY_IGNORE_SYS_PROTECTION_DLG1 = "cm_security_sys_protection_dlg1";
//    private static final String SECURITY_IGNORE_SYS_PROTECTION_DLG2 = "cm_security_sys_protection_dlg2";
//
//    private static final String SECURITY_NEED_FULL_SCAN = "security_need_full_scan";
//
//    private static final String SECURITY_TIMEWALL_EVENTS_TYPE = "security_timewall_events_type";
//    private static final String SECURITY_TIMEWALL_EVENTS_AUTOCLEAR_CONFIRM = "security_timewall_events_autoclear_confirm";
//
//    // 常驻通知栏开关
//    private static final String PERMANENT_NOTIF_SWITCH = "permanent_notif_switch";
//    private static final String PERMANENT_NOTIF_REPORT_TIME = "permanent_notif_report_time";
//    private static final String PERMANENT_NOTIF_COMMON_APP_REPORT_TIME = "permanent_notif_common_app_report_time";
//    //	private static final String PERMANENT_NOTIF_USER_FLAG = "permanent_notif_user_flag";
//    private static final String PERMANENT_NOTIF_STYLE = "permanent_notif_style";
//    private static final String PERMANENT_NOTIF_MANUAL_CHANGE_STYLE = "permanent_notif_manual_change_style";
//    private static final String PERMANENT_NOTIF_DIALOG_TIPS_STATUS = "permanent_notif_dialog_tips_status";
//    private static final String PERMANENT_NOTIF_FIRST_SHOW_MORE = "permanent_notif_first_show_more";
//    private static final String PERMANENT_NOTIF_FEATURE_LIST = "permanent_notif_feature_list";
//    private static final String PERMANENT_NOTIF_FEATURE_LIST_V2 = "permanent_notif_feature_list_v2";
//    private static final String PERMANENT_NOTIF_SELECTED_FEATURE_LIST = "permanent_notif_selected_feature_list";
//    private static final String PERMANENT_NOTIF_DIALOG_FEATURE_LIST = "permanent_notif_dialog_feature_list";
//    private static final String PERMANENT_NOTIF_FEATURE_TYPE = "permanent_notif_feature_type";
//    private static final String PERMANENT_NOTIF_FEATURE_SWITCH = "permanent_notif_feature_switch";
//    private static final String PERMANENT_NOTIF_FEATURE_FUNCTION = "permanent_notif_feature_function";
//    private static final String PERMANENT_NOTIF_FEATURE_COMMON_APP = "permanent_notif_feature_common_app";
//    //	private static final String PERMANENT_NOTIF_GAME_BOOST_SHOW = "permanent_notif_game_boost_show";
//    private static final String PERMANENT_NOTIF_FEATURE_MULTILINE_STATUS = "permanent_notif_feature_multiline_status";
//    //	private static final String PERMANENT_NOTIF_FEATURE_CHECKED_STATUS_NUM = "permanent_notif_feature_checked_status_num";
////	private static final String PERMANENT_NOTIF_SETTINGS_FIRST_GUIDE = "permanent_notif_settings_first_guide";
//    private static final String PERMANENT_NOTIF_PROMPT_DATA = "permanent_notif_prompt_data";
//    private static final String PERMANENT_NOTIFICATION_CLICK_COUNT = "permanent_notification_click_count";
//
//    // 悬浮窗Bottom
//    private static final String PERMANENT_FLOATBOTTOM_SWITCH = "permanent_floatbottom_switch";
//    private static final String PERMANENT_FLOAT_BOTTOM_TYPE = "permanent_float_bottom_type";
//    private static final String PERMANENT_FLOATGRIDVIEW_SWITCH = "permanent_floatgridview_switch";
//    private static final String PERMANENT_FLOAT_GRIDVIEW_TYPE = "permanent_float_gridview_type";
//
//    private static final String CURRENT_HINT_FLAG = "current_hint_flag";
//    //	private static final String IS_IN_LAUNCHER_FLAG = "is_in_launcher_flag";
////	private static final String CURRENT_ORIENTATION_FLAG = "current_orientation_flag";
//    private static final String CURRENT_BATTERY_PERCENTAGE = "current_battery_percentage";

    // 数据库升级的问题, 是否要全量
    private static final String DB_UPDATE_NEED_FULL_STRING = "db_update_need_full_string";
    // db 使用的开始时间，从什么时候开始用db的
//    private static final String DB_START_USE_TIME = "db_start_use_time_string";
//
//    // 永久忽略
//    private static final long NEVER_SHOW = -1;
//
//    private static final String IGNORE_TIME = "ignore_time";

    // Cover第一次使用
//	private static final String COVER_FIRST_USAGE = "cm_cover_first_usage";
//
//	// 隐私清理引导遮罩层
//	private static final String SECURITY_PRIVACY_CLEANING_GUIDE = "security_privacy_cleaning_guide";

    // 是否为第一次进行安全扫描
//    private static final String SECURITY_FIRST_SCAN = "security_first_scan";
//
//    // 首页未点击进入安全模块计数（用于高优先级安全播报红点展示计数）
//    private static final String SECURITY_UNCLICK_COUNT = "security_unclick_count";
//
//    // 最新一条高优先级安全播报新闻ID
//    private static final String SECURITY_HIGH_PRIORITY_EVENT_ID = "security_high_priority_event_id";
//
//    // 最新一条已阅读过的高优先级安全播报新闻ID
//    private static final String SECURITY_LAST_READ_HIGH_PRIORITY_EVENT_ID = "security_last_read_high_priority_event_id";
//
//    // 最新一条已展示过的高优先级安全播报新闻ID
//    private static final String SECURITY_LAST_SHOW_HIGH_PRIORITY_EVENT_ID = "security_last_show_high_priority_event_id";
//
//    private static final String SECURITY_INSTALL_FLOAT_WIN_SHOWCOUNT = "security_install_float_win_showcount";
//
//    // 软管後台扫描记录APK大小
//    public static final String APP_APK_SIZE = "app_apk_size";
//
//    // 软管後台扫描记录APK个数
//    public static final String APP_APK_NUM = "app_apk_num";

    // 建议清理项大小记录
    public static final String CM_STD_JUNK_SIZE = "cm_std_junk_size";

    // 深度清理大小记录
    public static final String CM_ADV_JUNK_SIZE = "cm_adv_junk_size";

    // 火眼扫描大小记录
    public static final String CM_ZEUS_CLEAN_JUNK_SIZE = "cm_zeus_clean_junk_size";

    // 视频扫描大小记录
    public static final String CM_VIDEO_CLEAN_JUNK_SIZE = "cm_video_clean_junk_size";

    // 视频扫描个数记录
    public static final String CM_VIDEO_CLEAN_JUNK_NUM = "cm_video_clean_junk_num";
    // 深度清理是否加入图片回收站记录
    public static final String CM_IS_PIC_RECYCLE_IN_ADV= "cm_is_pic_recycle_in_adv";

    // 建议清理内存值大小记录
    public static final String CM_PROC_JUNK_SIZE = "cm_proc_junk_size";
    // 建议清理广告大小记录
    public static final String CM_RUB_ADV_JUNK_SIZE = "cm_rub_adv_junk_size";
    // 建议清理APK大小记录
    public static final String CM_APK_JUNK_SIZE = "cm_apk_junk_size";
    // 深度下载管理大小记录
    public static final String CM_DOWNLOAD_JUNK_SIZE = "cm_download_junk_size";
    // 深度蓝牙大小记录
    public static final String CM_BLUETOOTH_JUNK_SIZE = "cm_bluetooth_junk_size";
    // 安全模块：恶意软件包名列表
//    private static final String SECURITY_MALWARE_PKG_LIST = "security_malware_pkg_list";
//    // 安全模块：恶意软件包名列表(已通知)
//    private static final String SECURITY_MALWARE_NOTIFY_PKG_LIST = "security_malware_notify_pkg_list";
//    // 安全模块：恶意软件包名列表通知状态
//    private static final String SECURITY_MALWARE_PKG_LIST_LAST_NOTIFY_STATUS = "security_malware_pkg_list_last_notify_status";
//    // wifi监控白名单
//    private static final String SECURITY_WIFI_PROTECT_WHITELIST = "security_wifi_protect_whitelist";
//
//    // 安全处理恶意数量
//    private static final String SECURITY_DEALED_MALWARE_NUM = "security_dealed_malware_num";
//
//    // 新用户安全播报下载失败次数
//    private static final String SECURIYT_EVENT_DOWNLOAD_FAIL_COUNT = "security_event_download_fail_count";
//
//    // 安全播报订阅开关
//    private static final String SECURIYT_BLOG_RSS_SWITCH = "security_blog_rss_switch";
//
//    // WIFI 扫描订阅开关
//    private static final String SECURIYT_WIFI_SCAN_SWITCH = "securiyt_wifi_scan_switch";
//
//    // 高隐私风险APP 提供给AppLock使用
//    private static final String SECURIYT_PRIVACYAPP_LIST = "security_privacyapp_list";
//
//    private static final String SECURIYT_OPENGP_FOR_CMLAUNCHER= "security_opengp_for_cmlauncher";
//    /** ************** 安全隐私 end */
//
//    private static final String CURRENT_APPVERSIONCODE = "AppVerCode_current";
//    private static final String PREVIOUS_APPVERSIONCODE = "AppVerCode_previous";
////	private static final String INSTALLED_APPVERSION_CODE_LIST = "AppVerCode_list";
//
//    private static final String PREVIOUS_INSTED_LOWER42 = "AppVerCode_insted_lower_42";
//    // 上一次从launcher启动游戏上报时间
//    private static final String LAST＿GAME_LAUNCH_REPORT_TIME = "last_game_launch_report_time";
//    private static final String LAST_GAME_LAUNCH_REPORT_PKG = "last_game_launch_report_pkg";
//
//    private static final String GAME_PRESCAN_STATE = "gameprescan_state";
//
//    private static final String GAMEBOX_FIRST_OPEN = "gamebox_first_open";
//
//    // 用户是否手动关闭了游戏加速
//    private static final String GAMEBOX_CLOSED_BOOSTED_MANUALLY = "gamebox_closed_boosted_manually";
//    //标记特殊事件第一次显示时间
//    private static final String GAMEBOX_SPECIAL_STAGE_SHOW_FIRST_TIME = "gamebox_special_event_show_first_time";
//    //标记特殊场景轮询索引
//    private static final String GAMEBOX_SPECIAL_STAGE_INDEX = "gamebox_special_stage_index";
//    //标记特殊场景上一次轮询索引
//    private static final String GAMEBOX_SPECIAL_STAGE_LAST_INDEX = "gamebox_special_stage_last_index";
//    //标记普通场景轮询索引
//    private static final String GAMEBOX_NORAML_STAGE_INDEX = "gamebox_noraml_stage_index";
//    //标记普通场景上一次轮询索引
//    private static final String GAMEBOX_NORAML_STAGE_LAST_INDEX = "gamebox_noraml_stage_last_index";
//    //内容场景上一次的包名集合
//    private static final String GAMEBOX_CONTENT_LAST_PKNAMES = "gamebox_content_last_pknames";
//    //标记是否处于特殊场景中
//    private static final String GAMEBOX_STAGE_IS_INSPECIAL = "gamebox_stage_is_inspecial";
//    //标记特殊场景上次id列表
//    private static final String GAMEBOX_SPECIAL_STAGE_LAST_IDS = "gamebox_special_stage_last_ids";
//    //标记普通场景上次id列表
//    private static final String GAMEBOX_NORMAL_STAGE_LAST_IDS = "gamebox_normal_stage_last_ids";
//    //上次上报安装app的时间
//    private static final String GAMEBOX_INSTALL_APP_REPORT_LAST_TIME = "gamebox_install_game_report_last_time";
//    //上次上报安装app的列表
//    private static final String GAMEBOX_INSTALL_APP_REPORT_LAST_LIST = "gamebox_install_app_report_last_list";
//
//    // 开启cm次数
//    private static final String START_CM_TIMES = "start_cm_times";

    // 是否有密码锁
//	private static final String LOCK_PATTERN_STATE = "lock_pattern_state";

    private static final String RUBBISH_BIG_FILTER_TYPE_MASK = "rubbish_big_filter_type_mask";

    private static final String RUBBISH_SCAN_BIG_FILE = "rubbish_scan_big_file";
//    private static final String STAND_CLEAN_FINSH_FIRST_TO_ADVANCE = "stand_clean_finish_first_to_advance";
//    private static final String CM_OTHER_MANAGER_DOWNLOAD_ISFIRST = "isFirstReportJunkAdvDownload";
//    private static final String CM_OTHER_MANAGER_BULETOOTH_ISFIRST = "isFirstReportJunkAdvBuletooth";
//    private static final String CM_OTHER_MANAGER_APKMANAGER_ISFIRST = "isFirstReportJunkAdvApkManager";
//
//    private static final String APP_VERSION_CODE_FOR_UPDATE_DATA = "app_version_code_for_update_data";
//    private static final String CM_APPMGR_PICKS_POSITION = "cm_appmgr_picks_position";
//
//    public static final String CM_MASTER_MCC = "cm_default_mcc_for_report";
//    public static final String JUNK_STANDARD_IS_UNCHECKED = "junk_standard_is_unchecked";
//
//    public static final String ABNORMAL_RATE_US = "abnormal_rate_us";
//
//    public static final String DB_INSTALL_SD_MARK = "db_install_sd_mark";
//    public static final String DB_INSTALL_SD_PATH = "db_install_sd_path";
//
//    public static final String COLLECTED_ROMAPP_INFO2 = "freeze_app2";
//
//    private static final String ALLOW_ACCESS_NETWORK = "allow_access_network";
//    private static final String ALLOW_ACCESS_NETWORK_DONT_INFORM = "allow_access_network_dont_inform";
//    private static final String ALLOW_ACCESS_LOCATION_DONT_INFORM = "allow_access_location_dont_inform";
//
//    private static final String NEW_APP_UN_CATEGORY= "new_app_un_category";
//    private static final String FIRST_CLICK_ONE_TAP= "first_click_one_tap";

    public static final String JUNK_IS_NEED_SCAN_AFTER_WIFI_ENABLED = "junk_tag_need_scan_after_wifi_enabled";
//    public static final String JUNK_IS_NEED_SCAN_LAST_SCAN_TIME = "junk_tag_need_scan_last_scan_time";
//    public static final String JUNK_IS_NEED_REPORT_RECYCLE_INFO = "junk_tag_need_report_recycle_info";
//    public static final String JUNK_IS_NEED_REPORT_RECYCLE_TIME = "junk_tag_need_report_recycle_time";
//
//    private String mstrSharedPreferenceName = null;
    private SharedPreferences mshardPreferences = null;

    /** location **/
//    private static final String LOCATION_CITY_NAME = "location_city_name";
//    private static final String LOCATION_CITY_NAME_BY3G = "location_city_name_by3G";
//    private static final String LOCATION_COUNTRY_CODE = "location_country_code";
//    private static final String LOCATION_IS_UPDATING = "location_is_updating";
//    private static final String COVER_WEATHER_SETTING_DIALOG_SHOWED = "cover_weather_setting_dialog_showed";
//    //	private static final String WEATHER_FIRST_UPDATE_ING = "weather_first_update_ing";
////	private static final String WEATHER_TEMPERATURE_UNITS = "weather_temperature_units";
////	private static final String WEATHER_SHOW_WEATHER = "weather_show_weather";
////	private static final String WEATHER_FLOAT_IS_CLICK = "weather_flaot_is_click";
////	private static final String WEATHER_FLOAT_CLICK_DATE = "weather_float_click_date";
//    private static final String WEATHER_FLOAT_TIPS_TODAY_SHOW_COUNT = "weather_float_tips_today_is_show";
//    private static final String WEATHER_FLOAT_TIPS_TOMORROW_SHOW_COUNT = "weather_float_tips_tomorrow_is_show";
//    private static final String WEATHER_FLOAT_TIPS_TODAY_SHOW_DATE = "weather_float_tips_today_show_date";
//    private static final String WEATHER_FLOAT_TIPS_TOMORROW_SHOW_DATE = "weather_float_tips_tomorrow_show_date";
//
//    private static final String WEATHER_FLOAT_TODAY_IS_CLICK = "weather_float_today_is_click";
//    private static final String WEATHER_FLOAT_TODAY_CLICK_DATE = "weather_float_today_click_date";
//    private static final String WEATHER_FLOAT_TOMORROW_IS_CLICK = "weather_float_tomorrow_is_click";
//    private static final String WEATHER_FLOAT_TOMORROW_CLICK_DATE = "weather_float_tomorrow_click_date";
//
//    private static final String FLOAT_TIPS_HINT_JSON = "float_tips_hint_json";
//
//    private static final String FIRST_CLICK_FLOAT_RECOMMEND= "first_click_float_recommend";
//
//    private static final String LOCATION_AUTO_FAILED = "location_auto_failed";
//    private static final String INFOC_SSL_EXCEPTION = "cm_infoc_ssl_exception";
//
//    /** 标记用户是否进入过内存深度加速界面 */
//    private static final String PROCESS_ADVANCE_BOOST_ENTER = "process_advance_boost_enter";
//    /** 标记内存结果页推荐深度清理的时间 */
////	private static final String PROCESS_ADVANCE_BOOST_RECOMMEND_TIME = "process_advance_boost_recommend_time";
//    /** 记录是否显示过已停用列表泡泡 */
//    private static final String PROCESS_ADVANCE_DISABLE_TIPS_SHOW = "process_advance_disable_tips_show";
////	private static final String PROCESS_ADVANCE_SHOW_FIVE_RATING_COUNT = "process_advance_show_five_rating_count";
////	/** 记录上一次最后使用的内存默认推荐项 */
////	private static final String PROCESS_RECOMMEND_DEFAULT_TYPE_LAST = "process_recommend_default_type_last";
//
//    /** 记录上一次最后进入安全隐私的时间 */
//    private static final String RESULT_PAGE_FOR_SECURITYANDPRIVACY_LASTTIME = "result_page_for_securityandprivacy_lasttime";
//
//    /** 记录上一次最后进入软件管理时间*/
//    private static final String RESULT_PAGE_FOR_APPMANAGER_LASTTIME = "result_page_for_appmanager_lasttime";
//
//    private static final String RESULT_PAGE_GAME_LIST = "result_page_game_list";//结果页缓存游戏列表
//
//    private static final String WIZARD_UPDATE = "WIZARD_UPDATE";//猎豹知道数据跟新
//
//    /** 重复照片次数 */
//    private static final String RESULT_PAGE_REPEAT_PICTURE = "result_page_repeat_picture";
//    private static final String RESULT_PAGE_REPEAT_PICTURE_COUNT = "result_page_repeat_picture_count";
//
//    /** 卡慢卡片次数 */
//    private static final String RESULT_PAGE_SLOW_COUNT = "result_page_slow_count";
//    /** 卡慢卡片时间 */
//    private static final String RESULT_PAGE_SLOW_TIME = "result_page_slow_time";
//
//    public static final String RESULT_PAGE_CAROUSEL_SPLIT=",";
//
//    private static final String RESULT_PAGE_PICTURE_FIRST = "result_page_picture_first";//重复照片首次
//
//    private static final String RESULT_PAGE_APPS_CACHE = "result_page_apps_cache";//缓存app size
//
//    private static final String JUNK_RESULT_PAGE_ADVANCE_JUNK_CLEAN_TIME_AVOID = "junk_result_page_advance_junk_clean_time_avoid";
//
//    private static final String JUNK_RESULT_PAGE_NOTIFICATION_COUNT_AVOID = "junk_result_page_notification_count_avoid";
//    private static final String JUNK_RESULT_PAGE_FLOAT_WINDOW_COUNT_AVOID = "junk_result_page_float_window_count_avoid";
////	private static final String JUNK_RESULT_PAGE_UPDATE_CM_COUNT_AVOID = "junk_result_page_update_cm_count_avoid";
//
//    private static final String PROCESS_RECOMMEND_ADVANCE_BOOST_DEALALL = "process_recommend_advance_boost_deal_all";
//
//    private static final String PROCESS_RATING_US_SHOW = "process_rating_us_show";
//
//    private static final String PROCESS_CPU_ALERT_ICON_MAIN_IS_CLICK = "process_icon_main_is_click";
//    private static final String PROCESS_CPU_ALERT_ICON_MAIN_SHOW_TIME = "process_icon_main_show_time";
//    private static final String PROCESS_TEMP_ALERT_ICON_MAIN_IS_CLICK = "process_temp_main_is_click";
//    private static final String PROCESS_TEMP_ALERT_ICON_MAIN_SHOW_TIME = "process_temp_main_show_time";
//
//    private static final String PROCESS_CPU_NOTIFICATION_SHOW_TIME = "process_cpu_abnormal_notificaion_show_time";
//    private static final String PROCESS_CPU_REMINDER = "process_cpu_reminder";
//    private static final String PROCESS_FREQSTART_REMINDER = "process_freqstart_reminder";
//
//    private static final String CPU_NORMAL_LAST_ALL_CLEANED_TIME = "cpu_normal_last_all_cleaned_time";
//    private static final String CPU_NORMAL_LAST_CLEAN_TEMP = "cpu_normal_last_clean_temp";
//    private static final String CPU_ABNORMAL_TOAST_SHOW = "cpu_abnormal_toast_show";
//    private static final String CPU_NORMAL_IS_CLEAN_PROCESS = "cpu_normal_is_clean_process";
//
//    private static final String PROBE_CRASH_DB_COPY_RPTED = "probe_crash_db_copy_rpted";
//
////	private static final String PROCESS_RESULT_RECOMMEND_NOTIFICATION_TOP_COUNT = "process_rr_notification_top_count";
////	private static final String PROCESS_RESULT_RECOMMEND_FLOAT_TOP_COUNT = "process_rr_float_top_count";
//
//    private static final String CPU_RATING_US_COUNT = "cpu_rating_us_count";
//    private static final String CPU_RATING_US_LAST_TIME = "cpu_rating_us_last_time";
//    private static final String CPU_NORMAL_RATING_US_CLICK = "cpu_rating_us_click";
//
//    //异常结果页分享卡片点击标记
//    private static final String ABNORMAL_RESULT_SHARE_CLICK = "abnormal_result_share_click";
//
//    private static final String IS_FLASHLIGHT_OPEN = "is_flashlight_open";
//    private static final String ASSETS_CFG_FLAG = "assets_cfg_flag";
//    private static final String ASSETS_CFG_FLAG_OEM = "assets_cfg_flag_oem";
//
//    private static final String BROWSER_DEFAULT_PACKAGENAME = "browser_default_packagename";
//    private static final String BROWSER_DEFAULT_ACTIVITY = "browser_default_activity";
//
//    private static final String FORBIDDEN_NOTIFY_UPDATE_FLAG = "forbidden_notify_update_flag";
//    private static final String JUNK_ADV_LAST_SHOW_BATTERY = "junk_adv_last_show_battery";
//
//    //首次出现个性化清理时，提示
//    private static final String JUNK_STD_FIRST_PERSONAL = "junk_std_first_personal";
//
//    private static final String PROCESS_RUNNING_APP_COUNT = "process_running_app_count";
//    //	private static final String CPU_NORMAL_ENTER_FLAG = "cpu_normal_enter_flag";
////	private static final String CPU_NORMAL_RESULT_IS_FAQ_TOP = "cpu_normal_result_faq_top";//记录当前CPU正常结果页是否是FAQ置顶
//    private static final String INTERNALAPP_LAST_SHOW_AD_TIME = "internalapp_last_show_ad_time";
//
//    //	private static final String CPU_NORMAL_FIRST_COOL_RESULT = "cpu_norma_first_cool_result";
//    private static final String CPU_NORMAL_LAST_CHECK_TIME = "cpu_normal_last_check_time";
//    private static final String CPU_RESULT_FIRST_NORMAL_TEMP = "cpu_result_first_normal_temp";
//    private static final String CPU_RESULT_FIRST_NO_TEMP = "cpu_result_first_no_temp";
//
//    private static final String EMAIL_KEY = "email_key";
//    private static final String TIME_GAMEBOX_PAUSE = "time_gamebox_pause";
//    private static final String LASTCHECKEDVERSION = "ipc_last_checked_version";
//
//
//    private static final String AUTOSTART_ADD_WHITE_APP_LIST    = "autostart_add_white_app_list";
//    private static final String AUTOSTART_REMOVE_WHITE_APP_LIST = "autostart_remove_white_app_list";
//    private static final String AUTOSTART_SETTING_REMINDER = "autostart_setting_reminder";
//    private static final String AUTOSTART_CAN_DISABLE_APP_COUNT = "autostart_can_disable_app_count";
//    private static final String AUTOSTART_CAN_DISABLE_PKG = "autostart_can_disable_pkg";
//    private static final String AUTOSTART_WLIB_UNDO_VERSION = "autostart_wlib_undo_version";
//
//    private static final String SETTING_ON_SCREEN_TEMPERATURE_NOTIFICATION_ENABLE = "on_screen_temperature_notification_cloud";
//    private static final String SETTING_ON_SCREEN_TEMPERATURE_NOTIFICATION_DEFAULT = "on_screen_temperature_notification_default";
//    private static final String LAST_TIME_ON_SCREEN_TEMPERATURE_NOTIFICATION = "last_time_on_screen_temperature_notification";
//
//    /**CPU温度，用户内存主界面和通过内存主界面进入CPU界面的温度显示*/
//    private static final String CPU_TEMPERATURE_TEMP = "cpu_temperature_temp";
//    private static final String CPU_TEMPERATURE_TEMP_BASE = "cpu_temperature_temp_base";
//    private static final String CPU_TEMPERATURE_TEMP_TIME = "cpu_temperature_temp_time";
//
//    // 是否要展示游戏盒子图标创建成功的通知对话框
//    private static final String SHOULD_SHOW_GAME_BOX_CREATE_SUCCESS_TIP = "G1";
//
//    //记录昨天使用建议清理的一键清理次数 大小
//    private static final String JUNK_STD_YESTERDAY_CLEAN_COUNT = "junk_std_yesterday_clean_count";
//    private static final String JUNK_STD_TODAY_CLEAN_COUNT = "junk_std_today_clean_count";//最后一次使用的累计次数
//    private static final String JUNK_STD_YESTERDAY_CLEAN_SIZE = "junk_std_yesterday_clean_size";
//    private static final String JUNK_STD_TODAY_CLEAN_SIZE = "junk_std_today_clean_size";//最后一次使用的累计大小
//    private static final String JUNK_STD_LAST_CLEAN_TIME = "junk_std_last_clean_time";//最后一次使用的时间



    private static class InnerConfigManager {
        private static final ServiceConfigManager instanse = new ServiceConfigManager(context);
    }

//    private static final String USER_CLOSE_WEATHER = "user_close_weather";
//    private static final String USER_CLICK_LOCATION = "user_click_location";
//
//    private static final String USER_CLOSE_LINKS = "user_close_links";
//    private static final String USER_CLOSE_FLOW = "user_close_flow";
//    private static final String CERT_VERIFIED = "cert_verified";
//
//    /** 系统空间不足时上报用户手机上的APP信息 */
////	private static final String IS_FIRST_REPORT_JUNK_SYSSTOR_LESS = "isFirstReportJunkSysstorLess";
//    // private final SharedPreferences mSharedPreferences;
//
//    // report app cpu info
//    private static final String LAST_REPORT_APPCPU_TIME = "last_report_appcpu_time";
//
//    // report app cpu env time
//    private static final String LAST_REPORT_ENVCPU_TIME = "last_report_envcpu_time";
//
//    // report app cpu env info
//    private static final String ENV_REPORT_APPCPU_COUNT = "env_report_appcpu_count";
//
//    // 上报新产生的文件，时间限制
////	private static final String JUNK_NEW_FILE_REPORT_TIME = "junk_new_file_report_time";
//
//    // 是否不再提示省电模式问题
//    private static final String NOT_SHOW_BATTERY_SWITCH_TIPS = "not_show_battery_switch_tips";
//    // 用户是第一次浏览信息流内容页面
//    private static final String USER_FIRST_TIME_BROWSE_GAME_INFO_CONTENT = "user_first_time_browse_game_info_content";
//
//    // 相册管理，找重复照片tip
//    private static final String SHOW_FIND_DUPLICATE_PHOTO_TIP = "show_find_duplicate_photo_tip";
//
//    private static final String PROCESS_LAST_NOTIFICATION_TEXT = "process_last_notification_text";
//
//    private static final String LAST_SHOW_NOTIFY_PKGNAME = "last_pkgName";
//
//    // 是否始终展示闪屏页SplashingActivity
//    private static final String ALWAYS_SHOW_SPLASH = "always_show_splash";
//
//    /*---记录用户操作过CPU异常----*/
//    private static final String IS_CPU_ABNORMAL_OP = "is_cpu_abnormal_op";
//
//    // 新闻红点是否显示过
//    private static final String NEWS_REDDOT_SHOWED = "news_reddot_showed";
//
//    // 上次通知栏提示升级apk的日期
////    private static final String UPDATE_NOTIFY_LAST_TIME = "update_notify_last_time";
//    // 上次主界面提示升级apk的日期
//    private static final String UPDATE_DIALOG_LAST_TIME = "update_dialog_last_time";
//
//    private static final String PERMANENT_NOTIFICATION_BRIGHTNESS_UTILIZATION = "permanent_notification_brightness_utilization";
//    private static final String PERMANENT_NOTIFICATION_WIFI_UTILIZATION = "permanent_notification_wifi_utilization";
//    private static final String PERMANENT_NOTIFICATION_DATA_UTILIZATION = "permanent_notification_data_utilization";
//    private static final String PERMANENT_NOTIFICATION_VOLUME_UTILIZATION = "permanent_notification_volume_utilization";
//    private static final String PERMANENT_NOTIFICATION_SCREEN_TIMEOUT_UTILIZATION = "permanent_notification_screen_timeout_utilization";
//    private static final String PERMANENT_NOTIFICATION_ROTATE_UTILIZATION = "permanent_notification_rotate_utilization";
//    private static final String PERMANENT_NOTIFICATION_HAS_OPENED = "permanent_notification_has_opened";
//    private static final String PERMANENT_NOTIFICATION_MONITOR_COMPLETELY = "permanent_notification_monitor_completely";
//    private static final String PERMANENT_NOTIFICATION_MONITOR_COMPLETELY2 = "permanent_notification_monitor_completely2";
//    private static final String PERMANENT_NOTIFICATION_MONITOR_RUN_COUNT = "permanent_notification_monitor_run_count";
//    private static final String PERMANENT_NOTIFICATION_MONITOR_RUN_COUNT2 = "permanent_notification_monitor_run_count2";
//    private static final String PERMANENT_NOTIFICATION_MONITOR_CANCEL_COUNT = "permanent_notification_monitor_cancel_count";
//    private static final String PERMANENT_NOTIFICATION_MONITOR_START_TIME = "permanent_notification_monitor_start_time";
//    //	private static final String PERMANENT_NOTIFICATION_MONITOR_FIRST_DONE = "permanent_notification_monitor_first_done";
//    private static final String PERMANENT_NOTIFICATION_CLEAN_FONT_IMAGE = "permanent_notification_clean_font_image";
//
//    private static final String UNLOCKSCREE_DISPLAY_NOTIF_TYPE = "unlockscree_display_notif_type";
//    private static final String TOTAL_SCREE_OFF = "total_scree_off";
//
//    private static final String KEY_NOTIFICATION_CLOSED_PREFIX = "notification_closed_";
//    private static final String KEY_NOTIFICATION_CHANGE_TEXT_PREFIX = "notification_change_text_";
//    private static final String KEY_NOTIFICATION_TEXT_PREFIX = "notification_text_";
//    private static final String KEY_NOTIFICATION_SWITCH_VALUE_PREFIX = "notification_switch_value_";
//    private static final String KEY_NOTIFICATION_CONFIG_VALID = "notification_config_valid";
//
//    private static final String CPU_EVENT_MONITOR_TIME = "cpu_event_monitor_time";

    private static final String SETTING_JUNK_SCAN_MEMORY_SWITCH = "setting_junk_scan_memory_switch";
    private static final String INSTALL_JUNK_DELETE_FILE = "install_junk_delete_file";

//    private static final String FLOAT_DIALOG_TAB_LAST_NAME = "float_dialog_tab_last_name";
//
//    private static final String IS_NEW_DAY_SAVE_TIME = "is_new_day_save_time";
//
//    private static final String KEY_ALLOW_POSITIONING = "key_allow_positioning";//允许定位的key
//
//    private static final String CM_UNINSTALL_HAS_UNUSED_APP_FOR_MAIN_TAB = "cm_uninstall_has_unused_app_for_main_tab";
//    private static final String CM_UNINSTALL_MAIN_APP_SHOW_REDDOT = "CM_UNINSTALL_MAIN_APP_SHOW_REDDOT";
//    private static final String APPMGR_IS_FIRST_OPEN_PICKS = "AppMgrisFirstOpenPicks";
//    // 盒子退出或游戏退出后，是否需要帮用户恢复省电模式
//    private static final String AUTO_CLOSE_SAVER_MODE = "auto_close_saver_mode";
//
//    private static final String ABNORMAL_NOTIFY_FREQSTART_IGNORE_LIST = "abnormal_notify_freqstart_ignore_list";
//    private static final String ABNORMAL_NOTIFY_CPU_IGNORE_LIST = "abnormal_notify_cpu_ignore_list";
//
//    // PROCESS 异常检测通知栏
//    private static final String ABNORMAL_DETECTION_NOTIFY_LAST_TIME = "abnormal_detection_notify_last_time";
//    private static final String ABNORMAL_DETECTION_NOTIFY_DELAY_MILLIS_NORMAL_EXT = "abnormal_detection_notify_delay_millis_normal_ext";
//    private static final String ABNORMAL_DETECTION_NOTIFY_DELAY_MILLIS_DANGER_EXT = "abnormal_detection_notify_delay_millis_danger_ext";
//    private static final String ABNORMAL_DETECTION_NOTIFY_DELAY_MILLIS_FREQSTART_EXT = "abnormal_detection_notify_delay_millis_freqstart_ext";
//    private static final String ABNORMAL_DETECTION_NOTIFY_FLAG = "abnormal_detection_notify_flag";
//    private static final String ABNORMAL_DETECTION_NOTIFY_FREQSTART_FLAG = "abnormal_detection_notify_freqstart_flag";
//    private static final String ABNORMAL_DETECTION_NOTIFY_UNCLICK_COUNT = "abnormal_detection_notify_unclick_count";
//    private static final String ABNORMAL_DETECTION_NOTIFY_FREQSTART_UNCLICK_COUNT = "abnormal_detection_notify_freqstart_unclick_count";
//    private static final String CPU_ABNORMAL_HIGH_TEMP = "cpu_abnormal_high_temp";
//    private static final String CPU_ABNORMAL_FOREGROUND_PKG = "cpu_abnormal_high_temp_pkg";
//    private static final String LAST_CPU_ABNORMAL_NOTIFYID = "last_cpu_abnormal_notifyid";
//    private static final String LAST_CPU_ABNORMAL_LOCK_PKG = "last_cpu_abnormal_lock_pkg";
//    private static final String LAST_CPU_ABNORMAL_NOTIFY_INDEX = "last_cpu_abnormal_notify_idx";
//    private static final String LAST_ABNORMAL_FREQSTART_NOTIFICATION_INDEX = "abnormal_freqstart_notification_index";
//
//    private static final String ABNORMAL_FREQSTART_REPORT_TIME = "abnormal_freqstart_report_time";
//    private static final String ABNORMAL_FREQSTART_REPORT_COUNT = "abnormal_freqstart_report_count";
//
//    private static final String ABNORMAL_RANKING_NOTIFY_LAST_TIME = "abnormal_ranking_notify_last_time";
//    private static final String ABNORMAL_RANKING_NOTIFY_LAST_TYPE = "abnormal_ranking_notify_last_type";

    // 微博分享卡片
    private static final String WEIBO_SHARE_SHOW_TIMES = "weibo_share_show_times";
    private static final String WEIBO_SHARE_SHOW_DATE = "weibo_share_show_date";
    private static final String WEIBO_SHARE_SHOW_GIF = "weibo_share_show_gif";

//	private static final String CM_WIZARD_UPDATE_TIME = "cm_wizard_update_time";


    //DU特殊需求
//    private static final String PKG_JUNK_NOTIFY = "PKG_JUNK_NOTIFY";
//    private static final String PKG_MEM_NOTIFY = "PKG_MEM_NOTIFY";
//    private static final String PKG_AFTER_INSTALLED = "after_install_";
//    private static final String PKG_OPEN_COUNT = "pkg_open_count";
//    private static final String PKG_OPEN_NAME = "pkg_open_name";
//    private static final String PKG_FIRST_OPEN_TIME = "pkg_first_open_time";
//    private static final String PKG_FIRST_NOTIFY_CLICKED = "pkg_first_notify_clicked_";
//    private static final String DU_CPU_TEMP_CHECKED = "du_cpu_temp_checked";
//    private static final String START_PERMERNT_TIME = "START_PERMERNT_TIME";
//    private static final String PKG_MEM_NOTIFY_TRIGGERED = "pkg_mem_notify_triggered";
//    private static final String PKG_NOTIFY_LAST_TIME = "pkg_notify_last_time";
//    private static final String PKG_OPEN_PERMANT_NOTIFY = "pkg_open_permant_notify";
//
//    private static final String GAME_BOX_OPT_TIME ="game_box_opt_time_";
//
//    private static final String GAME_BOX_SCAN_TIME = "game_box_scan_time";
//
//    private static final String GAME_BOX_NEED_SHOW_JIAN = "game_box_need_show_jian";
//
//    private static final String IS_FIRST_SCAN_SIMILAR_PHOTO = "is_first_scan_similar_photo";//是否首扫重复照片
//
//    private static final String IS_FIRST_SCAN_SPACE_MANAGER= "is_first_scan_space_manager";//是否首扫空间管理
//
//    private static final String HAS_SHOW_SIMILAR_PHOTO_RATE= "has_show_similar_photo_rate";//是否显示相似图片评分
//
//    private static final String SIMILAR_PHOTO_BURST_COUNT = "similar_photo_burst_count";// 连拍数量
//    private static final String SIMILAR_PHOTO_BURST_NOTIFICATION_LAST_TIME = "similar_photo_burst_notification_last_time";// 连拍通知栏最后一次显示时间
//
//    private static final String SHOW_SCAN_SIMILAR_PHOTO_ON_SCREEN_ON_SIZE = "show_scan_similar_photo_on_screen_on_size";
//
//    private static final String SIMILAR_PHOTO_NOTIFICATION_SWITCH = "similar_photo_notification_switch";
//
//    /**
//     * 相似照片通知栏显示次数
//     */
//    private static final String SIMILAR_PHOTO_NOTIFICATION_SHOW_COUNT = "similar_photo_notification_show_count";
//
//    private static final String LAST_TIME_SCAN_SIMILAR_PHOTO_ON_SCREEN_OFF_POWER_ON = "last_time_scan_similar_photo_on_screen_off_power_on";
//
//
//    private static final String LAST_UPLOAD_ANR_TIME = "last_upload_anr_time";
//
//
//    //不常用app通知栏 展示的app集合
//    private static final String NOTIFY_UNUSED_APP_PKGS = "notify_unused_app_pkgs";
//
//    //top app 打开垃圾清理
//    private static final String TOP_APP_OPEN_JUNK = "top_app_open_junk";
//
//    // 猎豹知道更新标识
//    private static final String WIZARD_FLAG = "cm_wizard_flag";
//
//    //盒子创建游戏盒子样式
//    private static final String GAME_BOX_CREATE_STYLE = "game_box_style";
//
//    private static final String TOP_APP_SCAN_SIZE_PREFIX = "tass_";
//    private static final String TOP_APP_LAST_SIZE_PREFIX = "tass_ls_";
//    private static final String TOP_APP_SHOW_COUNT_PREFIX = "tass_sc_";
//    private static final String TOP_APP_SHOW_LAST_TIME = "tass_show_last_time";
//
//    private static final String SHOW_TIME_DIALOG_CLEAN_SIMILAR_PHOTO_ON_GALLERY = "show_time_dialog_clean_similar_photo_on_gallery";
//
//    private static final String LAST_TIME_SCAN_SIMILAR_PHOTO_ON_GALLERY = "last_time_clean_similar_photo_on_gallery";
//
//    private static final String RESIDUAL_SIZE_CLEAN_SIMILAR_PHOTO_ON_GALLERY = "residual_size_clean_similar_photo_on_gallery";
//
//    private static final String LAST_TIME_SCAN_SIMILAR_PHOTO_ON_CAMERA= "last_time_clean_similar_photo_on_camera";
//
//    private static final String SHOW_TIME_DIALOG_CLEAN_SIMILAR_PHOTO_ON_CAMERA = "show_time_dialog_clean_similar_photo_on_camera";
//
//    private static final String LAST_NOFITICATION_SYS_TIME  = "last_notification_sys_time";
//    private static final String LAST_ENTER_MAINACT_SYS_TIME = "last_enter_mainact_sys_time";
//    private static final String AUTOSTART_NOTIFY_NEW_PKGS   = "ats_notify_new_pkgs";
//
////    private static final String SCAN_SIMILAR_PHOTO_ON_SCREEN_OFF_SIZE = "scan_similar_photo_on_screen_off_size";
//
//    //low memory notification content plan
//    private static final String LAST_LOW_MEM_NOTIFY_PLAN = "last_low_mem_notify_plan";
//
//    // 新游戏安装通知栏是否有被点击（游戏盒子）
//    private static final String GAME_INSTALLED_NOTIFICATION_HANDLED = "game_installed_notification_handled";
//    // 新游戏安装通知栏的弹出时间（游戏盒子）
//    private static final String GAME_INSTALLED_NOTIFICATION_SHOW_TIME = "game_installed_notification_show_time";
//    // 最后一个安装的游戏
//    private static final String LAST_INSTALL_GAME_PKG = "last_install_game_pkg";
//
//    private static final String HAS_SHOW_GAME_PASSIVE_SHOW_CLEANER_DIALOG = "has_show_game_passive_show_cleaner_dialog";
//
//    // 上一次上报用户安装游戏数量的时间点
//    private static final String LAST_REPORT_GAME_NUM_TIME = "last_report_game_num_time";
//
//    /**通知栏总控根据ID规避不弹出的时间段信息**/
//    private static final String NOTIFICATION_ID_TIME_AVOID = "notify_id_";
//    /**通知栏总控根据Category规避不弹出的时间段信息**/
//    private static final String NOTIFICATION_CATEGORY_TIME_AVOID = "notify_category_";
//
//    private static final String SECURITY_IS_ENTER_MAIN_PAGE = "security_is_enter_main_page";
//    private static final String SECURITY_NOTIFY_LAST_SHOW_SE_ID = "security_notify_last_show_se_id";
//    private static final String SECURITY_MAIN_LAST_SHOW_SE_ID = "security_main_last_show_se_id";
//    private static final String SECURITY_LAST_SE_DATA_DOWNLOAD_TIME = "security_se_data_download_time";
//    private static final String SECURITY_SCHEDULE_SE_ID = "security_schedule_se_id";
//    private static final String SECURITY_HIDE_FB_RECOMMEND_SE_ID = "security_hide_fb_recommend_se_id";
//    private static final String SECURITY_FIRST_SE_ID = "security_first_se_id";
//    private static final String SECURITY_FIRST_SE_TIME = "security_first_se_time";
//    private static final String SECURITY_ISNEED_SCANLEAKAPK = "security_isneed_scanleakApk";
//
////    private static final String LAST_SHOW_SIMILAR_ACTIVITY_TIME = "last_show_similar_activity_time";
//
//    private static final String RESIDUAL_SIZE_SIMILAR_ACTIVITY = "residual_size_similar_activity";
//
//    // 用户是第一次浏览Gamebaord内容页面
//    private static final String USER_FIRST_TIME_BROWSE_GAMEBOARD_INFO_CONTENT = "user_first_time_browse_gameboard_info_content";
//
//    /**
//     * GameBoard映射时间（隔三天与服务器映射一次）
//     */
//    private static final String GAME_BOARD_MAPPING_BOARD_TIME = "game_board_mapping_board_time";
//
//    private static final String GAME_BOARD_GUIDE_DIALOG_IS_SHOWN = "game_board_guide_dialog_is_shown";
//    private static final String GAME_BOARD_USED = "game_board_used";
//
//
//    private static final String FLOAT_SETTING_TIP_COUNT = "float_setting_tip_count";
//    private static final String FLOAT_SETTING_TIP_LAST_TIME = "float_setting_tip_last_time";
//
//    // TOP_APP_SCAN_SIZE_PREFIX前缀记录的包名列表，用;隔开
//    private static final String TOP_APP_RCD_PKG_NAME = "top_app_rcd_pkg_name";
//    private static final String JUNK_FILE_CLEAN_SPEED_TIME = "junk_file_clean_speed_time";
//    private static final String JUNK_FILE_CLEAN_SPEED_FILENUM = "junk_file_clean_speed_filenum";
//
//    //1tap rcommend
//    private static final String ONE_TAP_RECOMMEND_GAMEBOOST = "one_tap_recommend_gameboost";
//    private static final String ONE_TAP_RECOMMEND_CPU = "one_tap_recommend_cpu";
//    private static final String ONE_TAP_RECOMMEND_AUTOSTART = "one_tap_recommend_autostart";
//    private static final String ONE_TAP_RECOMMEND_JUNK_STANDARD = "one_tap_recommend_junk_standard";
//    private static final String ONE_TAP_RECOMMEND_MARKET= "one_tap_recommend_market";
//
//    private static final String ONE_TAP_CREATE_ON_MAIN = "one_tap_create_on_main";
//    private static final String ONE_TAP_CREATE_TOAST_SHOW = "one_tap_create_toast_show";    //1tap create toast flag
//
//    private static final String NEW_ONE_TAP_IS_CREATED = "new_one_tap_is_created";
//    //是否在旧的1tap显示了替换动画
//    private static final String ONE_TAP_IS_SHOW_REPLACE = "one_tap_is_show_replace";
//
//    private static final String BOOST_LAST_STAT_START_SYS_TIME = "boost_last_stat_start_sys_time";
//    private static final String BOOST_LAST_STAT_CLEAN_TIMES = "boost_last_stat_clean_times";
//    private static final String BOOST_CURRENT_STAT_START_SYS_TIME = "boost_cur_stat_start_sys_time";
//    private static final String BOOST_CURRENT_STAT_CLEAN_TIMES = "boost_cur_stat_clean_times";

    // operation team requested enable test case
    private static final String OPERATION_TEAM_TEST_FLAG    = "operation_team_test_flag_id";
    // 首次安装的版本号及启动时间
//    private static final String FIRST_INSTALL_VERSION_AND_START_TIME = "first_install_version_and_start_time";
//
//    private static final String IS_SCREEN_UNLOCK = "screen_unlock";
//
//    //登录注册的
//    private static final String LOGIN_SID_INIT = "com.cleanmaster.LOGIN_SID_INIT";
    private static final String LOGIN_BASIC_STATE = "com.cleanmaster.LOGIN_BASIC_STATE";
//    private static final String LOGIN_CM_STATE = "com.cleanmaster.LOGIN_CM_STATE";
//    private static final String LOGIN_CMB_STATE = "com.cleanmaster.LOGIN_CMB_STATE";
    private static final String LOGIN_DATA = "com.cleanmaster.LOGIN_DATA";
//    private static final String CLEAN_USER_INFO = "com.cleanmaster.CLEAN_USER_INFO";
//    private static final String LOGIN_USER_INFO = "com.cleanmaster.LOGIN_USER_INFO";
//    private static final String LOGIN_LOGIN_CM_CAPTURE_CODE_URL = "com.cleanmaster.LOGIN_LOGIN_CM_CAPTURE_CODE_URL";
//    private static final String LOGIN_REGIST_CM_CAPTURE_CODE_URL = "com.cleanmaster.LOGIN_REGIST_CM_CAPTURE_CODE_URL";
//    private static final String LOGIN_FACEBOOK_CM_CAPTURE_CODE_URL = "com.cleanmaster.LOGIN_FACEBOOK_CM_CAPTURE_CODE_URL";
//    private static final String LOGIN_GOOGLE_CM_CAPTURE_CODE_URL = "com.cleanmaster.LOGIN_GOOGLE_CM_CAPTURE_CODE_URL";
//    private static final String LOGIN_CMB_USER_INFO = "com.cleanmaster.LOGIN_CMB_USER_INFO";
//    private static final String LOGIN_LAST_ADDRESS = "com.cleanmaster.LOGIN_LAST_ADDRESS";
//    private static final String LOGIN_LAST_UPLOAD_DATA_TIME = "com.cleanmaster.LOGIN_LAST_UPLOAD_DATA_TIME";
//    private static final String LOGIN_OPENID = "com.cleanmaster.LOGIN_OPENID";
//    private static final String LOGIN_UPLOAD_FAILED_DATA = "com.cleanmaster.LOGIN_UPLOAD_FAILED_DATA";
//    private static final String LOGIN_BY_GOOGLE_ACCOUNT = "com.cleanmaster.LOGIN_BY_GOOGLE_ACCOUNT";
//    private static final String LOGIN_LAST_TIME_FRESH_TOKEN_FOR_GOOGLE = "com.cleanmaster.LOGIN_LAST_TIME_FRESH_TOKEN_FOR_GOOGLE";
//    private static final String LOGIN_GOOGLE_ACCOUNT_DATA = "com.cleanmaster.LOGIN_GOOLE_ACCOUNT_DATA";
//    private static final String LAST_LOGIN_USER_INFO = "com.cleanmaster.LAST_LOGIN_USER_INFO";
//
//    //CMS Recommend
//    private static final String CMS_RECOMMEND_CONTACT_BACKUP_IGNORED = "cms_recommend_contact_backup_ignored";
//    private static final String CMS_RECOMMEND_CONTACT_BACKUP_NOTI_SHOW_COUNT = "cms_recommend_contact_backup_noti_show_count";
//    private static final String CMS_RECOMMEND_CONTACT_BACKUP_NOTI_SHOW_TIME = "cms_recommend_contact_backup_noti_show_time";
//    private static final String CMS_RECOMMEND_CONTACT_BACKUP_SCAN_SHOW_COUNT = "cms_recommend_contact_backup_scan_show_count";
//    private static final String CMS_RECOMMEND_CONTACT_BACKUP_SCAN_SHOW_TIME = "cms_recommend_contact_backup_scan_show_time";
//
//    //充电屏保
//    private static final String CHARGE_SCREEN_SWITCH = "charge_screen_switch";
//    private static final String CHARGE_SCREEN_STATE_CHANGED_ON = "charge_screen_state_on";
//    private static final String CHARGE_SCREEN_STATE_CHANGED_OFF = "charge_screen_state_off";
//    private static final String CHARGE_SCREEN_SWITCH_CLOSED_TIME = "charge_screen_switch_closed_time";
//    private static final String CHARGE_SCREEN_LAST_NOTIFICATION_TIME = "charge_screen_last_notification_time";
//    private static final String CHARGE_SCREEN_LAST_NOTIFICATION_COUNT = "charge_screen_last_notification_count";
//    private static final String CHARGE_SCREEN_GUIDE_CLOSED_FOR_OFF = "charge_screen_guide_closed_for_off";
//    private static final String CHARGE_SCREEN_SWITCH_SETTED = "charge_screen_switched_setted";
//    private static final String CHARGE_SCREEN_GUIDE_TOAST_SHOW = "charge_screen_toast_show";
//    private static final String CHARGE_SCREEN_GUIDE_INTERNAL_VERSION = "charge_screen_internal_version";
//    private static final String SCREEN_SAVER_IS_USE_NEW = "screen_saver_is_use_new";
//    private static final String SCREEN_SAVER_IS_USE_NEW_CLOUD = "screen_saver_is_use_new_cloud";
//
//    //APPLOCK
//    private static final String CHARGE_APPLOCK_LAST_NOTIFICATION_TIME = "charge_applock_last_notification_time";
//    private static final String CHARGE_APPLOCK_LAST_NOTIFICATION_COUNT = "charge_applock_last_notification_count";
//    private static final String CHARGE_APPLOCK_LAST_NOTIFICATION_RESET_FLAG = "charge_applock_last_notification_reset_flag";
//    private static final String APP_LOCK_CLICK_ENABLED = "app_lock_click_enabled";
//    private static final String APP_LOCK_OPEN_LOLIPOP_USAGE_PERMISSION = "app_lock_open_lolipop_usage_permission";
//
//    /** 最近的一次桌面Toast弹出的时间，所有的桌面Toast弹出时间最好都做记录，
//     * 并且弹出前根据时间判断是否还要再次显示，否则桌面会出现多个Toast提示*/
//    private static final String DESKTOP_TOAST_SHOW_TIME = "desktop_toast_show_time";
//
//
//    private static final String NOTIFY_MANAGER_NOTIFYSTARTTIME = "notify_manager_notifystarttime";
//    private static final String NOTIFY_MANAGER_NOTIFYCOUNT = "notify_manager_notifycount";
//
//    //facebook
//    public static final String FACEBOOK_IGNORE = "facebook_ignore";
//
//    // 图片压缩begin
//    private static final String PHOTO_COMPRESS_BG_SCAN_NUM = "photo_compress_bg_scan_num";
//    private static final String PHOTO_COMPRESS_LAST_UNHANDLE_NOTIFY_TIME = "photo_compress_last_unhandle_notify_time";
//    private static final String PHOTO_COMPRESS_AVERAGE_SAVED_SIZE = "photo_compress_average_saved_size";
    private static final String PHOTO_COMPRESS_AVERAGE_SAVED_RATE = "photo_compress_average_saved_rate";
    private static final String PHOTO_COMPRESS_COLLECT_DAMAGE_PHOTO= "photo_compress_collect_damage_photo";
    private static final String PHOTO_COMPRESS_AVERAGE_RATE= "photo_compress_average_rate";
    private static final String PHOTO_COMPRESS_HISTORY_SIZE = "photo_compress_history_size";
    // 图片压缩end

    //记录上次扫描相似照片的时间
//    private static final String LAST_SCREEN_OFF_SIMILAR_PHOTO_SCAN_TIME = "last_screen_off_similar_photo_scan_time";
//
//    private static final String ANTIY_LIB_DOWNLOADING = "antiy_lib_downloading";
//    private static final String ANTIY_LIB_DOWNLOAD_TIME = "antiy_lib_download_time";
//
//    //内存阀值通知栏，防止通知栏打扰
//    private static final String  MEMORY_USED_NOTIFICATION_CLICK_COUNT = "memory_used_notification_click_count";
//    private static final String  MEMORY_USED_NOTIFICATION_UNCLICK_COUNT = "memory_used_notification_unclick_count";
//    private static final String  MEMORY_USED_NOTIFICATION_POPUP_PERIOD = "memory_used_notification_popup_period";
//    private static final String  MEMORY_USED_NOTIFICATION_POPUP_TIME = "memory_used_notification_popup_time";
//    private static final String  MEMORY_USED_NOTIFICATION_NEW_TEXT_SHOW_COUNT = "memory_used_notification_new_text_show_count";
//
//    //消息提醒设置
//    private static final String MSG_SWITCH_GAME_BOOST_NOTIFY = "msg_switch_game_boost_notify";//游戏加速提醒开关
//    private static final String MSG_SWITCH_HOT_NEWS_NOTIFY = "msg_switch_hot_news_notify";//热点新闻通知开关
//
//    private static final String FLOAT_FLOW_SETTED = "float_flow_setted";//热点新闻通知开关
//
//    //登录时记录登录的方式的KEY
//    public static final String LOGIN_OPTION = "login_option";
//
//    //记录多语言包配置下载上报时间
//    private static final String CM_RESOURCE_LAST_REPROT_TIME = "cm_resource_last_report_time";
//
//    // 社区
//    private static final String SOCIAL_NOTIFY_LAST_MILLIS = "social_notify_last_millis";
//    private static final String SOCIAL_NOTIFY_COUNT       = "social_notify_count";
//    private static final String SOCIAL_ENABLE_NEW_REING   = "social_enable_new_ring";
//
//    /**设置中的新消息提醒**/
//    private static final String IS_NEW_MESSAGE_ENABLE = "is_new_message_enable";

    //标识建议界面是否在进行后台清理
    private static final String JUNK_STD_SWITCH_INTO_BG_CLEAN = "junk_std_switch_into_bg_clean";

    private ServiceConfigManager(Context context) {
//        if (RuntimeCheck.IsServiceProcess()) {
//            mstrSharedPreferenceName = new String(context.getPackageName() + "_preferences");
//            mshardPreferences = context.getApplicationContext().getSharedPreferences(mstrSharedPreferenceName, Context.MODE_PRIVATE);
//        }
		/*
		 * mSharedPreferences =
		 * PreferenceManager.getDefaultSharedPreferences(context);
		 * mSharedPreferences.registerOnSharedPreferenceChangeListener(new
		 * OnSharedPreferenceChangeListener() {
		 *
		 * @Override public void onSharedPreferenceChanged(SharedPreferences
		 * sharedPreferences, String key) { } });
		 */
    }

    public static ServiceConfigManager getInstanse(Context context) {
        ServiceConfigManager.context = context.getApplicationContext(); // context
        ServiceConfigManager cm = InnerConfigManager.instanse;
        return cm;
    }

    public boolean getScanMemorySwitch(){
        return getBooleanValue(SETTING_JUNK_SCAN_MEMORY_SWITCH,true);
    }

    // opeation team use only, disable high frequent db if flag set to be 1.
    public void setOperationTeamTestEnable(int flag){
        setIntValue(OPERATION_TEAM_TEST_FLAG, flag);
    }

    public void setApkJunkScan(boolean bSwitch) {
        setBooleanValue(APKJUNKSCAN_SWITCH, bSwitch);
    }

    public boolean getApkJunkScanSwitch() {
        return true/*getBooleanValue(APKJUNKSCAN_SWITCH, true)*/;
    }

    public int getOperationTeamTestEnable(){
        return getIntValue(OPERATION_TEAM_TEST_FLAG, 0);
    }

    // 增加微博卡片显示次数
    public void addWeiboShareShowTimes() {
        int nShowTimes = getIntValue(WEIBO_SHARE_SHOW_TIMES, 0);
        nShowTimes ++;
        setIntValue(WEIBO_SHARE_SHOW_TIMES, nShowTimes);
    }

    // 设置微博卡片显示满
    public void setWeiboShareShowTimesFull() {
        int nShowTimes = getIntValue(WEIBO_SHARE_SHOW_TIMES, 0);
        if(nShowTimes < 3){
            setIntValue(WEIBO_SHARE_SHOW_TIMES, 3);
        }
    }

    // 获取微博分享是否是Gif
    public int isWeiboShareGif(){
        return getIntValue(WEIBO_SHARE_SHOW_GIF, -1);
    }

    // 设置微博卡片当天已显示过
    public void setWeiboShareGif(int gif) {
        setIntValue(WEIBO_SHARE_SHOW_GIF, gif);
    }

    // 判断微博卡片显示是否超过3次
    public boolean isWeiboShareShowtimesLimits(){
        int nShowTimes = getIntValue(WEIBO_SHARE_SHOW_TIMES, 0);
        if(nShowTimes >= 3){
            return true;
        }
        return false;
    }

    // 设置微博卡片当天已显示过
    public void setWeiboShareShowDate() {
        setLongValue(WEIBO_SHARE_SHOW_DATE, System.currentTimeMillis());
    }

    // 判断微博卡片当天是否已显示过
    public boolean isWeiboShareShowDateLimits(){
        boolean bRet = false;
        long lastTime = getLongValue(WEIBO_SHARE_SHOW_DATE, 0);
        if( 0 != lastTime ){
            long nowTime = System.currentTimeMillis();
            if ((nowTime - lastTime) <= 24 * 60 * 60 * 1000L) {
                return true;
            }
        }
        return bRet;
    }

    /**
     * 是否显示 卸载删除apk弹窗
     */
    public boolean canShowDeleteApkFileDialog() {
        long now = System.currentTimeMillis();
        long last = getLongValue(INSTALL_JUNK_DELETE_FILE, 0);
        if (now - last > 1000 * 60 * 60 * 24 * 3) {//暂定3天
            return true;
        } else {
            return false;
        }
    }

    public void setShowDeleteApkFileDialogTime(long time){
        setLongValue(INSTALL_JUNK_DELETE_FILE,time);
    }

    public void setLoginData(String data){
        setStringValue(LOGIN_DATA,data);
    }

    public String getLoginData(){
        return getStringValue(LOGIN_DATA,"");
    }

    public void setLoginBasicState(int isLogined){
        setIntValue(LOGIN_BASIC_STATE,isLogined);
    }

    public void setNeedScanAfterWifiEnabled(boolean needScan) {
        setBooleanValue(JUNK_IS_NEED_SCAN_AFTER_WIFI_ENABLED, needScan);
    }

    private int getIntValue(String key, int defValue){
        return 0;
    }

    private void setIntValue(String key, int value){

    }

    public long getLongValue(String key, long defValue) {
        if (RuntimeCheck.IsServiceProcess()) {
            return getSharedPreference().getLong(key, defValue);
        } else {
          //  return ConfigProvider.getLongValue(key, defValue);
        }
        return  1L;
    }
    private String getStringValue(String key, String value){
        return value;
    }

    private void setStringValue(String key, String value){

    }

    private void setBooleanValue(String key, boolean value){

    }

    private boolean getBooleanValue(String key, boolean defValue){
        return false;
    }

    // 记录是否清理过 standard junk  只要有清junkItem就会设置
    public Boolean isFirstCleanedJunkStandard() {
        return  false;
        //  nilo 延后开发
        //return getBooleanValue(IS_FIRST_CLEANED_JUNK_STANDARD, VersionReplaceUtils.IsPreVerionNull());
    }

    // 是否有清理过advanced junk
    public boolean isHaveCleanedJunkAdvanced() {
        //nilo 延后开发
        return  false;
        //return getBooleanValue(ISHAVECLEANEDJUNKADVANCED, false);
    }

    public String getFileVersion(String fileName) {
        return getStringValue(FILEVERSIONPREFIX + fileName, "");
    }

    public void setDbUpdaetIsNeedFull(boolean isNeed) {
        setBooleanValue(DB_UPDATE_NEED_FULL_STRING, isNeed);
    }

    /**
     * 此处的类别为ExtraAndroidFileScanner.EF_TYPE_*
     */
    public boolean isFilterBigFileType(int type) {
        int filterMask = getRubbishBigFilterTypeMask();
        return (0 != ((1 << type) & filterMask));
    }

    public int getRubbishBigFilterTypeMask() {
        return getIntValue(RUBBISH_BIG_FILTER_TYPE_MASK, 0);
    }

    public boolean getScanBigFileFlag() {
        return getBooleanValue(RUBBISH_SCAN_BIG_FILE, true);
    }

    public void setLongValue(String key, long value) {
        if (RuntimeCheck.IsServiceProcess()) {
            SharedPreferences.Editor editor = getSharedPreference().edit();
            editor.putLong(key, value);
            SharePreferenceUtil.applyToEditor(editor);
        } else {
            //ConfigProvider.setLongValue(key, value);
        }
    }

    private SharedPreferences getSharedPreference() {
        RuntimeCheck.CheckServiceProcess();
        return mshardPreferences;
    }

    public void removeFilterListVersion() {
        saveFilterListVersion("");
    }

    public void saveFilterListVersion(String version) {
        setStringValue(FILTER_LIST_VERSION, version);
    }

    public int getPhotoCompressAverageRate() {
        return getIntValue(PHOTO_COMPRESS_AVERAGE_RATE, 0);
    }

    public void setJunkStdSwitch2Bgclean(boolean isBgclean){
        setBooleanValue(JUNK_STD_SWITCH_INTO_BG_CLEAN,isBgclean);
    }

    public void setPhotoCompressAverageRate(int rate) {
        setIntValue(PHOTO_COMPRESS_AVERAGE_RATE, rate);
    }

    public void addPhotoCompressHistorySize(long size) {
        setLongValue(PHOTO_COMPRESS_HISTORY_SIZE, getPhotoCompressHistorySize() + size);
    }

    public long getPhotoCompressHistorySize() {
        return getLongValue(PHOTO_COMPRESS_HISTORY_SIZE, 0L);
    }

    public int getPhotoCompressAverageSavedRate(String path) {
        String rateStr = getStringValue(PHOTO_COMPRESS_AVERAGE_SAVED_RATE,"");
        int index = rateStr.indexOf(path);
        int rate = 0;
        if(index > -1){
            rateStr = rateStr.substring(path.length() + 1);
            try{
                rate = Integer.parseInt(rateStr);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return rate;
    }

    public void setPhotoCompressAverageSavedRate(String path,int rate) {
        setStringValue(PHOTO_COMPRESS_AVERAGE_SAVED_RATE, path + "_" + rate);
    }

    public boolean getPhotoCompressCollectDamage() {
        return getBooleanValue(PHOTO_COMPRESS_COLLECT_DAMAGE_PHOTO, false);
    }

    public void setPhotoCompressCollectDamage() {
        setBooleanValue(PHOTO_COMPRESS_COLLECT_DAMAGE_PHOTO, true);
    }

    /**
     * 注意：不要直接调用本方法，请通过CleanedInfo类来使用。
     */
    public long getTodayCleanedSize() {
        int todayHash = getTodayHash();
        if (getIntValue(DAYTIME_OF_TODAY_CLEANED_SIZE, 0) != todayHash) {
            setTodayCleanedSize(0);
            return 0;
        }

        return getLongValue(TODAY_CLEANED_SIZE, 0);
    }

    private void setTodayCleanedSize(long size) {
        int todayHash = getTodayHash();
        setIntValue(DAYTIME_OF_TODAY_CLEANED_SIZE, todayHash);
        setLongValue(TODAY_CLEANED_SIZE, size);
    }

    private int getTodayHash() {
        Calendar cal = Calendar.getInstance();
        int hash = cal.get(Calendar.YEAR);
        hash <<= 2;
        hash += cal.get(Calendar.MONTH);
        hash <<= 2;
        hash += cal.get(Calendar.DATE);
        return hash;
    }

    /**
     * 注意：不要直接调用本方法，请通过CleanedInfo类来使用。
     */
    public long getTotalCleanedSize() {
        long size = getLongValue(TOTAL_CLEANED_SIZE, 0);
        return size;
    }

    public long getMaxCleanedSize() {
        long size = getLongValue(MAX_CLEANED_SIZE, 0);
        if(size == 0){
            size = getTodayCleanedSize();
        }
        return size;
    }

    /**
     * 注意：不要直接调用本方法，请通过CleanedInfo类来使用。
     */
    public void addCleanedSize(long size) {
        long todaySize = getTodayCleanedSize() + size;
        long totalSize = getTotalCleanedSize() + size;

        long maxSize = getMaxCleanedSize();
        if(maxSize == 0 || todaySize > maxSize){
            setLongValue(MAX_CLEANED_SIZE,todaySize);
        }

        int todayHash = getTodayHash();
        setIntValue(DAYTIME_OF_TODAY_CLEANED_SIZE, todayHash);
        setLongValue(TODAY_CLEANED_SIZE, todaySize);
        setLongValue(TOTAL_CLEANED_SIZE, totalSize);
    }

    public LanguageCountry getLanguageSelected(Context context) {
        String language = getStringValue(LANGUAGE_SELECTED, LanguageCountry.LANGUAGE_OPTION_DEFAULT);
        String country = getStringValue(COUNTRY_SELECTED, LanguageCountry.COUNTRY_OPTION_DEFAULT);
        if (language.equalsIgnoreCase(LanguageCountry.LANGUAGE_OPTION_DEFAULT)) {
            language = context.getResources().getConfiguration().locale.getLanguage();
        }
        if (country.equalsIgnoreCase(LanguageCountry.COUNTRY_OPTION_DEFAULT)) {
            country = context.getResources().getConfiguration().locale.getCountry();
        }
        return new LanguageCountry(language, country);
    }

    // 增加了对本地资源的国家匹配验证，比如本地只有不带国家代号的fr语言，那么就返回国家代号为空的LanguageCountry
    public LanguageCountry getLanguageSelectedEx(Context context) {
        String language = getStringValue(LANGUAGE_SELECTED, LanguageCountry.LANGUAGE_OPTION_DEFAULT);
        String country = getStringValue(COUNTRY_SELECTED, LanguageCountry.COUNTRY_OPTION_DEFAULT);
        if (language.equalsIgnoreCase(LanguageCountry.LANGUAGE_OPTION_DEFAULT)) {
            language = context.getResources().getConfiguration().locale.getLanguage();
        }
        if (country.equalsIgnoreCase(LanguageCountry.COUNTRY_OPTION_DEFAULT)) {
            country = context.getResources().getConfiguration().locale.getCountry();
        }
        // 如果我们资源里没有匹配的语言和国家，那就把国家置空，只匹配语言
        if (!LanguageSelectionHelp.getInstance().queryLanguageWithCountry(language, country)) {
            country = "";
        }
        return new LanguageCountry(language, country);
    }

    public static interface OnLanguageCountryChangeListener {
        public void onLanguageChanged(LanguageCountry languageCountry);
    }


    private final ArrayList<OnLanguageCountryChangeListener> mLanguageCountryChangeListeners = new ArrayList<OnLanguageCountryChangeListener>(2);

    public void registerLanguageCountryChangeListener(OnLanguageCountryChangeListener listener) {
        synchronized (mLanguageCountryChangeListeners) {
            mLanguageCountryChangeListeners.add(listener);
        }
    }

    public void setLanguageSelected(LanguageCountry languageCountry) {
        setStringValue(LANGUAGE_SELECTED, languageCountry.getLanguage());
        setStringValue(COUNTRY_SELECTED, languageCountry.getCountry());
        synchronized (mLanguageCountryChangeListeners) {
            for (OnLanguageCountryChangeListener listener : mLanguageCountryChangeListeners) {
                listener.onLanguageChanged(languageCountry);
            }
        }
    }




    public boolean isShowUninstallMultiNotify(int totalShow,String key) {
        String showV = getStringValue(":key_cm_multi_"+key, "");
        int showCount = 0;
        long firstTime = 0L;
        if (!TextUtils.isEmpty(showV)) {
            showCount = Integer.parseInt(showV.split(";")[0]);
            firstTime = Long.parseLong(showV.split(";")[1]);
        }
        if(firstTime > 0 && System.currentTimeMillis() - firstTime > FOURTEEN_DAY){
            showCount = 0;
            setStringValue(":key_cm_multi_"+key, "0;0");
        }
        return showCount < totalShow;
    }
}

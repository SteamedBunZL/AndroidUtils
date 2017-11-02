package com.clean.spaceplus.cleansdk.junk.engine.junk;

public class JunkEngineMsg {
	
	// TODO 这里的ID定义需要完善注释，同时需要把和JunkEngine无关却被JunkEngineWrapper使用的定义移到JunkEngineWrapperMsg中去。
	
	// 注意，这里定义的ID值域是[0, 1000000000)，不要超过范围定义，以免造成冲突。
    // 注意，请按顺序定义，把新定义的消息写在最下面，要让最下面那个消息的数值保持最大，这样才不会造成重复定义数值的错误。

	//扫描所有类型垃圾每一项时候返回的名称
    public static final int MSG_HANDLER_SCAN_STATUS_INFO 		= 0;
	public static final int MSG_HANDLER_FOUND_PROCESS_ITEM		= 1;
	public static final int MSG_HANDLER_FINISH_STORAGE_JUNK_SCAN =2;
	public static final int MSG_HANDLER_FINISH_PROCESS_SCAN		= 3;
	public static final int MSG_HANDLER_FINISH_SYS_SCAN			= 4;
	public static final int MSG_HANDLER_FINISH_SD_SCAN			= 5;
	public static final int MSG_HANDLER_FINISH_RUBBISH_SCAN		= 6;
	public static final int MSG_HANDLER_FOUND_CACHE_ITEM			= 7;
	public static final int MSG_HANDLER_FOUND_RUBBISH_ITEM		= 8;
	public static final int MSG_HANDLER_UPDATE_RUBBISH_ITEM		= 9;
	public static final int MSG_HANDLER_ADD_PROGRESS				= 10;
	public static final int MSG_HANDLER_FOUND_PROCESS_ITEM_UNCHECKED		= 11;
	public static final int MSG_HANDLER_FINISH_TMP_FILES_SCAN = 12;
	public static final int MSG_HANDLER_FINISH_CLEAN				= 13;
	public static final int MSG_HANDLER_UPDATE_CLEAN_BUTTON		= 14;
	public static final int MSG_HANDLER_SCAN_STATUS_FINAL_INFO 	= 15;
	public static final int MSG_HANDLER_REMOVE_DATA_ITEM 		= 16;
	public static final int MSG_HANDLER_REMOVE_DATA_ITEM_IGNORE = 17;
	public static final int MSG_HANDLER_FINISH_CLEAN_IGNORE		= 18;
	public static final int MSG_HANDLER_FINISH_LOG_FILES_SCAN   = 19;
	public static final int MSG_HANDLER_CLEAN_REPORT            = 20;
	public static final int MSG_HANDLER_FINISH_SYS_FIXED_SCAN   = 21;
	public static final int MSG_HANDLER_REMOVE_DATA_ITEM_SIZE	= 22;
	
	
	public static final int MSG_HANDLER_REMOVE_CACHE_ITEM		= 23;
	public static final int MSG_HANDLER_FOUND_APK_ITEM = 24;
	public static final int MSG_HANDLER_FINISH_APK_SCAN =25;
	
	public static final int MSG_HANDLER_FINISH_ADV_SCAN =26;
	public static final int MSG_HANDLER_FINISH_LEFT_OVER_SCAN =27;
	
	public static final int MSG_HANDLER_FINISH_SCAN =28;
	
	public static final int MSG_HANDLER_FINISH_CLEAN_FOR_CACHE =29;
	public static final int MSG_HANDLER_ADD_CLEAN_PROGRESS_FOR_SYSTEMCACHE =30;
	
	public static final int MSG_HANDLER_UPDATE_COMING_SOON_SIZE =31;
	
	public static final int MSG_HANDLER_FINISH_THUMBNAIL_SCAN = 32;
	public static final int MSG_HANDLER_FOUND_THUMBNAIL_ITEM = 33;
	public static final int MSG_HANDLER_UPDATE_THUMBNAIL_ITEM = 34;
	public static final int MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE = 35;
	
	
	public static final int MSG_HANDLER_START_SCAN = 39;
	public static final int MSG_HANDLER_UPDATE_SIZE = 40;
//	public static final int MSG_HANDLER_UPDATE_ALPHA = 27;
	public static final int MSG_HANDLER_EXPAND_GROUP = 41;
	public static final int MSG_HANDLER_SCAN_END = 42;
	public static final int MSG_HANDLER_SCAN_END_NONE = 43;//扫描结束 没有扫描项，直接跳转结果界面
	public static final int MSG_HANDLER_UPDATE_COLOR = 44;
	public static final int MSG_HANDLER_REMOVE_SYSTEM_CACHE_ITEM = 45;
	public static final int MSG_HANDLER_REMOVE_APP_CACHE_ITEM = 46;
	public static final int MSG_HANDLER_REMOVE_SDRUBBISH_ITEM = 47;
	public static final int MSG_HANDLER_REMOVE_APK_ITEM = 48;
	public static final int MSG_HANDLER_DISMISS_ITEM = 49;
	public static final int MSG_HANDLER_UPDATE_ADV_RECOM_SIZE = 50;
	public static final int MSG_HANDLER_TURN_OFF_SWITCH_BTN_SHOW_SIZE = 51;
	public static final int MSG_HANDLER_REMOVE_PROCESS_ITEM = 52;
	public static final int MSG_HANDLER_ON_FLOATGUIDE_USER_LEAVE = 53;
	public static final int MSG_HANDLER_FINISH_PHOTO_SIZE = 54;
	public static final int MSG_HANDLER_FINISH_AUDIO_SIZE = 55;
	public static final int MSG_HANDLER_FINISH_DOWNLOAD_SIZE = 56;
	public static final int MSG_HANDLER_UPDATE_DATA_ITEM = 57;

    public static final int MSG_HANDLER_ROOT_CACHE_SCAN_FOUND_ITEM = 58;
    public static final int MSG_HANDLER_ROOT_CACHE_SCAN_FINISH = 59;

	public static final int MSG_HANDLER_SYS_CACHE_SCAN_STATUS = 120;
	public static final int MSG_HANDLER_SYS_CACHE_SCAN_PROGRESS_STEP = 121;
	public static final int MSG_HANDLER_SYS_CACHE_SCAN_PROGRESS_START = 122;
	//新加的扫描建议在150到200之间
	//扫描应用缓存垃圾返回的内容
	public static final int MSG_HANDLER_SCAN_STATUS_SDCACHE_INFO 		= 150;
	public static final int MSG_HANDLER_SCAN_STATUS_RUBBLISH_INFO 		= 151;
	public static final int MSG_HANDLER_SCAN_STATUS_ADV_INFO 		= 152;
	public static final int MSG_HANDLER_SCAN_STATUS_SYSCACHE_INFO 		= 153;
	public static final int MSG_HANDLER_SCAN_STATUS_APKFILE_INFO 		= 154;
	public static final int MSG_HANDLER_SCAN_STATUS_PROCESS_INFO 		= 155;
	public static final int MSG_HANDLER_SCAN_STATUS_SDCACHE_CACHE_INFO 		= 160;

	//清理Msg====================================================================================================================
	public static final int MSG_HANDLER_CLEAN_STATUS_INFO = 81;
	public static final int MSG_HANDLER_RST_CLEAN_LOG = 82;

	public static final int MSG_HANDLER_PROC_CLEAN_FINISH = 60;
	public static final int MSG_HANDLER_PROC_CLEAN_ITEM   = 61;
	public static final int MSG_HANDLER_SYS_CLEAN_FINISH  = 62;
	public static final int MSG_HANDLER_SYS_CLEAN_ITEM    = 63;
	public static final int MSG_HANDLER_SYS_CLEAN_INFO    =   80;
	public static final int MSG_HANDLER_SD_CLEAN_FINISH   = 64;
	public static final int MSG_HANDLER_SD_CLEAN_ITEM     = 65;
	public static final int MSG_HANDLER_SD_CLEAN_STATUS   = 66;
	public static final int MSG_HANDLER_SD_CLEAN_INFO     = 67;
	public static final int MSG_HANDLER_RUB_CLEAN_FINISH  = 68;
	public static final int MSG_HANDLER_RUB_CLEAN_ITEM    = 69;
	public static final int MSG_HANDLER_RUB_CLEAN_STATUS  = 70;
	public static final int MSG_HANDLER_RUB_CLEAN_INFO    = 71;
	public static final int MSG_HANDLER_APK_CLEAN_FINISH  = 72;
	public static final int MSG_HANDLER_APK_CLEAN_ITEM    = 73;
	public static final int MSG_HANDLER_APK_CLEAN_STATUS  = 74;
	public static final int MSG_HANDLER_APK_CLEAN_INFO    = 75;
	public static final int MSG_HANDLER_MEDIA_CLEAN_FINISH= 76;
	public static final int MSG_HANDLER_MEDIA_CLEAN_ITEM  = 77;
	public static final int MSG_HANDLER_MEDIA_CLEAN_STATUS= 78;
	public static final int MSG_HANDLER_MEDIA_CLEAN_INFO  = 79;
	public static final int MSG_HANDLER_VIDEO_CLEAN_ITEM    = 83;
	public static final int MSG_HANDLER_VIDEO_CLEAN_STATUS  = 84;
	public static final int MSG_HANDLER_VIDEO_CLEAN_INFO    = 85;

	public static final int MSG_HANDLER_FINISH_CLEAN_REPORT = 90;

	public static final int MSG_HANDLER_SD_CLEAN_RECYCLE  = 91;
	public static final int MSG_HANDLER_RUB_CLEAN_RECYCLE = 92;
	
	public static final int MSG_HANDLER_SYS_FIXED_CLEAN_FINISH  = 93;
	public static final int MSG_HANDLER_SYS_FIXED_CLEAN_ITEM    = 94;
    public static final int MSG_HANDLER_ROOT_CACHE_CLEAN_FINISH  = 96;
    public static final int MSG_HANDLER_ROOT_CACHE_CLEAN_ITEM    = 97;

	public static final int MSG_HANDLER_UPDATE_ALL_APP_SIZE = 95;//扫描app大小
	
	//=========================================================
	public static final int MSG_HANDLER_FINISH_BIG_FILE_SCAN = 100;
	public static final int MSG_HANDLER_FINISH_PHOTO_SCAN = 101;
	public static final int MSG_HANDLER_FOUND_PHOTO_ITEM = 102;
	public static final int MSG_HANDLER_FOUND_DOWNLOAD_ITEM = 103;
	public static final int MSG_HANDLER_FOUND_BLUETOOTH_ITEM = 104;
	public static final int MSG_HANDLER_FINISH_CALC_FOLDER_SIZE = 105;
	public static final int MSG_HANDLER_FINISH_AUDIO_SCAN = 106;
	public static final int MSG_HANDLER_FOUND_AUDIO_ITEM = 107;
	public static final int MSG_HANDLER_REMOVE_BIG_FILE_TYPE = 108;
	public static final int MSG_HANDLER_TO_RESULT=109;
	public static final int MSG_HANDLER_FINISH_CALC_FOLDER_SCAN=110;
	public static final int MSG_HANDLER_FINISH_MY_AUDIO_SCAN=111;
	public static final int MSG_HANDLER_FINISH_MY_PHOTO_SCAN=112;
	public static final int MSG_HANDLER_FOUND_BIGFILE_ITEM = 113;
	public static final int MSG_HANDLER_FOUND_SIZE_ITEM = 114;
    public static final int MSG_HANDLER_FOUND_VIDEO_OFFLINE_ITEM = 115;
    public static final int MSG_HANDLER_FINISH_VIDEO_OFFLINE_SCAN = 116;
    
    public static final int MSG_HANDLER_SHOW_CLEANSLOW_TOAST = 117;
    public static final int MSG_HANDLER_HIDE_CLEANSLOW_TOAST = 118;

    public static final int MSG_HANDLER_FINISH_SCREENSHOTSCOMPRESSSCAN = 119;
    public static final int MSG_HANDLER_FOUND_SCREENSHOTSCOMPRESS_ITEM = 123;
    public static final int MSG_HANDLER_FINISH_COMPRESS_SCRSHOTS = 124;
	public static final int MSG_HANDLER_CHANGE_APK_ITEM = 125; // add by chaohao.zhou 修改APK item的数值
	public static final int MSG_HANDLER_STOP_WAIT_RESULT = 126; // add by chaohao.zhou 扫描过程中点击暂停需等待结果
}

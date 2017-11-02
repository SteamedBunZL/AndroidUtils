package com.clean.spaceplus.cleansdk.junk.engine;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/5 13:36
 * @copyright TCL-MIG
 */
public interface DataTypeInterface {
    // 类别，cache/residual/apk/temp/big
    public static final int TYPE_CATEGORY = 0;
    public static final int TYPE_SYSTEM_CACHE = 1;
    public static final int TYPE_APP_CACHE = 2;
    public static final int TYPE_APP_LEFT = 3;
    public static final int TYPE_TEMP_FILE = 4;
    public static final int TYPE_BIG_FILE = 5;
    public static final int TYPE_AD_FILE = 6;
    public static final int TYPE_APK_FILE = 7;
    public static final int TYPE_PHOTO_GALLERY = 8;
    public static final int TYPE_DOWNLOAD_GALLERY = 9;
    public static final int TYPE_BLUETOOTH_GALLERY = 10;
    public static final int TYPE_APKMANAGER_GALLERY = 11;
    public static final int TYPE_PROCESS = 12;
    public static final int TYPE_ADVANCED_JUNK = 13;
    public static final int TYPE_AUDIO_MANAGER = 14;
    public static final int TYPE_PHOTO_GALLERY_DETAIL = 15;
    public static final int TYPE_PHOTO_GRID_AD = 16;
    public static final int TYPE_SYS_FIXED_CACHE = 17;
    public static final int TYPE_ROOT_CACHE = 18;
    public static final int TYPE_SIMILAR_PHOTO = 19;
    public static final int TYPE_VIDEO_OFF = 20;
    public static final int TYPE_DOWNLOAD_MANAGER = 30;
    public static final int TYPE_AD_NORMAL = 31;
    public static final int TYPE_PHOTO_COMPRESS_MANAGER = 32;
    public static final int TYPE_SCREEN_SHOTS_COMPRESS = 33;
    public static final int CATEGORY_TYPE_CACHE = 1;
    public static final int CATEGORY_TYPE_RESIDUAL = 2;
    public static final int CATEGORY_TYPE_APK = 3;
    public static final int CATEGORY_TYPE_TEMP = 4;
    public static final int CATEGORY_TYPE_BIG = 5;
    public static final int CATEGORY_TYPE_STANDARD = 6;
    public static final int CATEGORY_TYPE_OTHER = 7;
    public static final int CATEGORY_TYPE_BIGFILE = 8;
    public static final int CATEGORY_TYPE_ROOT_CACHE = 13;
    public static final int CATEGORY_TYPE_VIDEO_OFF = 12;
    public static final int CATEGORY_TYPE_PERSONAL = 15;

    public static final int CATEGORY_TYPE_STORAGE_JUNK = 8;
    public static final int CATEGORY_TYPE_MEMORY_JUNK = 9;
    public static final int CATEGORY_TYPE_FILE = 14;
    // 三种警告类型，卡片显示
    public static final int CATEGORY_TYPE_ALTER_SYSTEM = 10;
    public static final int CATEGORY_TYPE_ALTER_PROCESS = 11;
    public static final int CATEGORY_TYPE_ALTER_SDCARD= 12;
    // 商业推广
    public static final int CATEGORY_TYPE_AD_BATTERY = 20;
    public static final int CATEGORY_TYPE_SIMILAR_PHOTO = 21;
    public static final int CATEGORY_TYPE_PHOTOTRIM = 22;

    public static final int CATEGORY_TYPE_CACHE_AD = 23;
    // 截屏压缩
    public static final int CATEGORY_TYPE_SCREEN_SHOTS_COMPRESS = 24;

    // -- 新的type放到这里
    public static final int MODEL_APP_CACHE = 1;
    public static final int MODEL_RARELY_APP = 2;
    public static final int MODEL_SIMILAR_PHOTO = 3;
    public static final int MODEL_MOVE_APP = 4;
    public static final int MODEL_APK_FILE = 5;
    public static final int MODEL_JUNK_STANDARD = 6;
    public static final int MODEL_BIG_FILE = 7;
    public static final int MODEL_PHOTO_TRIM = 8;
    public static final int MODEL_PHOTO_MANAGER = 9;
    public static final int MODEL_DOWNLOAD_MANAGER = 10;

    public static final int MODEL_PIC_MANAGER = 11;
    public static final int MODEL_VIDEO_MANAGER = 12;
    public static final int MODEL_AUDIO_MANAGER = 13;

    public static final int MODEL_COUNT = 13;
}

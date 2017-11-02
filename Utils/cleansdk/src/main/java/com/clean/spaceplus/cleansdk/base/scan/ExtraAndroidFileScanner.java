package com.clean.spaceplus.cleansdk.base.scan;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/28 20:00
 * @copyright TCL-MIG
 */
public class ExtraAndroidFileScanner {
    public static final int RES_FILE_SCAN_CFG_MASK_SCAN_BIG_FILE = 0x00000004; // 是否扫描大文件类别
    public static final int RUB_FILE_SCAN_PROGRESS_START = 0x0000003;
    // 进度计算，arg1值为类别
    public static final int RUB_FILE_SCAN_PROGRESS_STEP_NUM = 0x0000004;
    // 进度计算，arg1值为类别，arg2为此类别的总步数
    public static final int RUB_FILE_SCAN_PROGRESS_ADD_STEP = 0x0000005;
    // 进度计算，arg1类别加一步
    public static final int ADD_CHILDREN_DATA_ITEM_TO_ADAPTER = 0x0000006;

    public static final int ADD_BIGFILE_DATA_ITEM_TO_ADAPTER = 0x0000010;
    public static final int ADD_CACHEINFO_DATA_ITEM_TO_ADAPTER = 0x0000020;
    public static final int ADD_LEFTOVER_DATA_ITEM_TO_ADAPTER = 0x0000030;
    public static final int ADD_SIZE_DATA_ITEM_TO_ADAPTER = 0x0000040;
    public static final int SIZE_BIG_FILE_MIN = 10*1024*1024;

    //public final static int EF_TYPE_OTHER = 0;
    public final static int EF_TYPE_ARCHIVE = 1;
    public final static int EF_TYPE_AUDIO = 2;
    public final static int EF_TYPE_PICTURE = 3;
    public final static int EF_TYPE_VIDEO = 4;
    public final static int EF_TYPE_BOOK = 5;
    public final static int EF_TYPE_GPK = 6;//拇指玩游戏安装包
    public final static int EF_TYPE_APK = 7;
    public final static int EF_TYPE_GAMEDATA = 8;
    public final static int EF_TYPE_MERGE_VIDEO = 9;  //Merged video file
    public final static int EF_TYPE_MERGE_FILE = 11;  //Merged file
    public final static int EF_TYPE_BAIDU_MAP =12;    //Baidu navi map file
    public final static int EF_TYPE_OTHER = 10;

    public static final int RF_APP_LEFTOVERS = 1;
    public static final int RF_CACHE_INFO = 2;
    public static final int RF_BIG_FILES = 3;

    /**
     * @param caller
     */
    public void setCaller(byte caller) {
        //nilo 上报开发
        /*mTimeRpt.user(caller);
        mCaller = caller;*/
    }

    public void setFirstScanFlag() {
        //nilo 上报开发
        /*mTimeRpt.first(true);
        mFirstScan = true;*/
    }

    public void scanInternBigFile(ScanTaskCallback mergeCallback,ScanTaskCallback cb, ScanTaskController ctrl, int scanMask){
        //nilo 延后开发
        // 大文件
        /*if (0 != (RES_FILE_SCAN_CFG_MASK_SCAN_BIG_FILE & mScanCfgMask) && KcmutilSoLoader.doLoad(true)) {
            OpLog.x("RFST", "big S.");
            mTimeRpt.start(cm_task_time.CM_TASK_TIME_STYPE_RUB_BIG, ctrl);
            mCB = cb;
            mergeIScanTaskCallback = mergeCallback;
            mCtrl = ctrl;
            mScanCfgMask = scanMask;
            mQueryFromName =//default:false 是否查询bigfile的from...
                    (0 == (mScanCfgMask & RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_NOT_QUERY_BIG_FILE_FROM));
            mExternalStoragePaths = (new StorageList()).getMountedVolumePaths();//外存卡路径列表（可能多个）
            scanExtraAndroidFile(cb,ctrl,scanMask);//重点
            OpLog.x("RFST", "big E.");
        }*/
    }
}

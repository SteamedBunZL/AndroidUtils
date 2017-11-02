package com.clean.spaceplus.cleansdk.junk.engine;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.util.FileUtils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/6 15:18
 * @copyright TCL-MIG
 */
public class EmergencyFalseSignManager {
    private static  final int NWEWORK_REFRESH_INTERVAL_TIME = 30 * 60 ;//半个小时联网更新一次
    private static  final int NWEWORK_RETRY_INTERVAL_TIME   = 2 * 60 ;//联网如果失败间隔2分钟重试一次
    private static  final int LOAD_FROM_FILE_REFRESH_INTERVAL_TIME = 10 * 60;//10分钟从磁盘加载更新一次

    private static final String SAVE_DATA_TMP_FILE_PREFIX = "tmpfalse_";

    private static final int FILTER_COUNT = 3;
    public static final String[] EMERGENCY_FALSE_SAVE_FILE_NAME = {
            "",
            "e_false_cache",
            "e_false_residual"
    };

    private static EmergencyFalseSignManager sRef = null;

    public static synchronized EmergencyFalseSignManager getInstance() {
        if (sRef == null) {
            sRef = new EmergencyFalseSignManager();
        }
        return sRef;
    }

    static int getFilterIndex(int type) {
        int index = 0;
        switch (type) {
            case EmergencyFalseSignFilter.FilterType.CACHE_DIR :
                index = 1;
                break;
            case EmergencyFalseSignFilter.FilterType.RESIDUAL_DIR :
                index = 2;
                break;
            default :
                break;
        }
        return index;
    }

    static boolean isNeedRefresh(int oldTime, int currentTime, int maxIntervalTime) {
        boolean result = true;
        if (oldTime > 0 && currentTime >= oldTime) {
            int diff = currentTime - oldTime;
            if (diff < maxIntervalTime) {
                result = false;
            }
        }
        return result;
    }

    public static class EmergencyFalseSignFilterImpl implements EmergencyFalseSignFilter {
        private int mType;
        private volatile FalseSignFile.SaveData mData = null;
        private volatile int[] mFalseIds = null;
        private volatile int mUpdateTime;

        public FalseSignFile.SaveData getData() {
            return mData;
        }

        EmergencyFalseSignFilterImpl(int type, FalseSignFile.SaveData data) {
            mType = type;
            updateData(data);
        }

        public int getType() {
            return mType;
        }

        public void updateData(FalseSignFile.SaveData data) {
            mUpdateTime = (int)(SystemClock.uptimeMillis()/1000);
            mData = data;
            if (data != null
                    && data.mFalseData != null
                    && data.mFalseData.mFalseIds != null ) {
                mFalseIds = data.mFalseData.mFalseIds;
            }
        }

        public boolean isNeedReloadData() {
            if (null == mData) {
                return true;
            }
            int currentTime = (int)(SystemClock.uptimeMillis()/1000);
            boolean result = isNeedRefresh(mUpdateTime, currentTime, LOAD_FROM_FILE_REFRESH_INTERVAL_TIME);
            return result;
        }

        public boolean isNeedReDownloadData() {
            boolean result = true;
            if (mData != null) {
                //这个时间是保存到磁盘的时间,所以用系统时间
                int currentTime = (int)(System.currentTimeMillis()/1000);
                result = isNeedRefresh(mData.mSaveTime, currentTime, NWEWORK_REFRESH_INTERVAL_TIME);
            }
            return result;
        }

        @Override
        public boolean filter(int id) {
            int[] falseIds = mFalseIds;
            return falseIds != null && falseIds.length > 0 && Arrays.binarySearch(falseIds, id) >= 0;
        }
    }


    private String mSaveDataPath = "";
    //用来记录联网查询的时间,对象同时用于加锁
    private AtomicInteger[] mDownLoadTimeAndLock = new AtomicInteger[FILTER_COUNT];
    private SoftReference<EmergencyFalseSignFilterImpl>[] mSignFilters = new SoftReference[FILTER_COUNT];

    private EmergencyFalseSignManager() {
        for (int i = 0; i < FILTER_COUNT; ++i) {
            mDownLoadTimeAndLock[i] = new AtomicInteger(0);
        }
    }

    public EmergencyFalseSignFilter createEmergencyFalseSignFilter(final int type) {
        EmergencyFalseSignFilter result = getFalseSignFilter(type);
        return result;
    }

    private EmergencyFalseSignFilter getFalseSignFilter(int type) {
        int index = getFilterIndex(type);
        if (0 == index) {
            return  null;
        }

        EmergencyFalseSignFilterImpl filter = null;
        SoftReference<EmergencyFalseSignFilterImpl> ref;
        boolean isNeedReloadData = true;
        synchronized (mDownLoadTimeAndLock[index]) {
            ref = mSignFilters[index];
            if (ref != null) {
                filter = ref.get();
                if (filter != null && !filter.isNeedReloadData()) {
                    isNeedReloadData = false;
                }
            }
            if (isNeedReloadData) {
                filter = createFalseSignFilterFromFile(type);
            }
        }
        return filter;
    }

    private String getSaveDataTmpFile(int type) {
        int index = getFilterIndex(type);
        if (0 == index) {
            return  null;
        }

        long time = System.currentTimeMillis();
        String dir = getSaveDataDirPath();
        String fullPath = dir
                + File.separator
                + SAVE_DATA_TMP_FILE_PREFIX
                + EMERGENCY_FALSE_SAVE_FILE_NAME[index]
                + "_"
                + String.valueOf(time);

        return fullPath;
    }

    private String getSaveDataFile(int type) {
        int index = getFilterIndex(type);
        if (0 == index) {
            return  null;
        }
        String dir = getSaveDataDirPath();
        String fullPath = dir
                + File.separator
                + EMERGENCY_FALSE_SAVE_FILE_NAME[index];

        return fullPath;
    }

    private String getSaveDataBakFile(int type) {
        String path = getSaveDataFile(type);
        String fullPath = null;
        if (path != null) {
            fullPath = path + ".bak";
        }
        return fullPath;
    }

    private String getSaveDataDirPath() {
        synchronized (this) {
            if (TextUtils.isEmpty(mSaveDataPath)) {
                Context context = SpaceApplication.getInstance().getContext();
                String path = FileUtils.getFilePathInFilesDir(context, "cleancloud/false");
                if (!TextUtils.isEmpty(path)) {
                    mSaveDataPath = path;
                }
            }
            return mSaveDataPath;
        }
    }

    EmergencyFalseSignFilterImpl createFalseSignFilterFromFile(int type) {
        FalseSignFile.SaveData data = getSignData(type);
        EmergencyFalseSignFilterImpl filter = createFalseSignFilter(type, data);
        return filter;
    }

    public FalseSignFile.SaveData getSignData(int type) {
        FalseSignFile.SaveData result = null;
        String filePath = getSaveDataFile(type);
        result = FalseSignFile.load(filePath);
        if (null == result) {
            filePath = getSaveDataBakFile(type);
            result = FalseSignFile.load(filePath);
        }
        return result;
    }

    EmergencyFalseSignFilterImpl createFalseSignFilter(int type, FalseSignFile.SaveData data) {
        EmergencyFalseSignFilterImpl filter = new EmergencyFalseSignFilterImpl(type, data);
        int index = getFilterIndex(type);
        mSignFilters[index] = new SoftReference<EmergencyFalseSignFilterImpl>(filter);
        return filter;
    }
}

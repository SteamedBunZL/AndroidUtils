package com.clean.spaceplus.cleansdk.junk.engine.junk;

import com.clean.spaceplus.cleansdk.junk.cleancloud.config.ServiceConfigManager;
import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/5 13:54
 * @copyright TCL-MIG
 */
public class PicRecycleCache {
    public static final String RECYCLE_PATH_SUFFIX = "PicRecycle";
    public static final String RECOVERY_PATH_SUFFIX = "PicRecovery";
    public static final long RECYCLE_SCAN_CACHE_TIME = 1000 * 60 * 5;
    public static final int PIC_MOVE_DELETE_MODE = 1;
    public static final int PIC_MOVE_RESTOR_MODE = 2;
    public static final int PIC_SEARCH_FOLDER_LAYER = 5;
    public static final int MAX_REPORTED_COUNT = 50;
    private static final int TASK_WAIT_TIME_OUT = 1000 * 60 * 10;
    public static final long LOCAL_SHOW_DURATION = 7 * 24 * 60 * 60 * 1000;
    public static final long CLOUD_SHOW_DURATION = 30 * 24 * 60 * 60 * 1000L;
    public static final int PIC_RECYLE_CALL_FROM_RECYCLETMP = 1;  //> 回收操作由转移RecycleTmp文件夹触发
    public static final int PIC_RECYLE_CALL_FROM_FRAGMENT = 2;    //> 回收操作由界面触发
    public static final int PIC_RECYLE_CALL_FROM_2SD = 3;         //> 回收操作由4.4及以上第二张卡拷贝文件触发
    //public static final long CLOUD_SHOW_DURATION = LOCAL_SHOW_DURATION;

    private final Object mCacheLock = new Object();
    private CallBack mCallBack;
    private HashMap<String, MediaFile> mMediaMap = new HashMap<String, MediaFile>();
    private final Queue<RecycleDataInfo> mRecycleDataList = new LinkedList<RecycleDataInfo>();
    private long mScanSize = 0;
    private boolean mCacheValid = false;
    private long mCacheTime = 0;
    private boolean mFirstRecycle = true;
    ServiceConfigManager mConfigManager = null;
    private String mRecyclePath;
    private String mRecoveryPath;
    private String mStorageRootPath;
    private boolean mDeleteDirect = false;
    private boolean mHaveCheckOldVer = false;
    private volatile static PicRecycleCache sInstance = null;

    private Comparator<MediaFile> mScanComparator = new Comparator<MediaFile>() {
        @Override
        public int compare(MediaFile mediaFile, MediaFile mediaFile2) {
            return (int)(mediaFile.lastMoved() - mediaFile2.lastMoved());
        }
    };

    public static PicRecycleCache getInstance() {
        if (sInstance == null) {
            synchronized (PicRecycleCache.class) {
                if (sInstance == null) {
                    sInstance = new PicRecycleCache();
                }
            }
        }

        return sInstance;
    }

    private PicRecycleCache() {

    }

    public class RecycleDataInfo {
        public int mCallFrom = 0;
        public CallBack mCallBack = null;
        public List<MediaFile> mList = new ArrayList<MediaFile>();
    }

    public static class CallBack {
        /**
         * 回收结束
         *
         * @param size 回收大小
         */
        public void onRecycleFinished(long size) {
        }

        /**
         * 扫描结束
         *
         * @param list 扫描出来的照片
         * @param size 扫描大小
         */
        public void onScanFinished(List<MediaFile> list, long size) {
        }

        /**
         * 清理结束
         *
         * @param size       删除大小
         * @param deleteList 删除的照片
         */
        public void onCleanFinished(long size, List<MediaFile> deleteList) {
        }

        /**
         * 恢复结束
         *
         * @param size        恢复大小
         * @param recoverList 恢复的照片
         */
        public void onRecoverFinished(long size, List<MediaFile> recoverList) {
        }

        /**
         * 一个文件开始备份
         * @param id 文件ID
         */
///<DEAD CODE>///         public void onUploadOneBegin(long id){
//
//        }

        /**
         * 一个文件备份结束
         * @param id  文件ID
         * @param code 备份结果
         */
///<DEAD CODE>///         public void onUploadOneEnd(long id,int code){
//
//        }
        /**
         * 一个文件备份进度
         * @param id 文件ID
         * @param currSize 已经备份大小
         * @param totalSize 文件总大小
         */
///<DEAD CODE>///         public void onUploadOneProgress(long id,long currSize,long totalSize){
//
//        }
///<DEAD CODE>///         public void onDownloadProgress(){
//
//        }

    }

    public void setCallBack(CallBack cb) {
        mCallBack = cb;
    }

    public void unRegisterCallBack() {
        mCallBack = null;
    }

    /**
     * 缓存是否有效，五分钟缓存
     */
    public boolean isCacheValid() {
        if (System.currentTimeMillis() - mCacheTime > RECYCLE_SCAN_CACHE_TIME) {
            invalidateCache();
            return false;
        } else {
            return mCacheValid;
        }
    }
    /**
     * 设置缓存无效
     */
    public void invalidateCache() {

        synchronized (mCacheLock) {
            mMediaMap.clear();
            mCacheValid = false;
            mScanSize = 0;
            mCacheTime = 0;
        }
    }
    public String GetRecyclePath() {
        return mRecyclePath;
    }
    public String GetRecoveryPath() {
        return mRecoveryPath;
    }

    private int UpdateCache(List<MediaFile> list, boolean bRemoveFromCache) {
        int nSize = 0;
        synchronized (mCacheLock) {
            if (isCacheValid()) {
                for (MediaFile mediaFile : list) {
                    if (bRemoveFromCache) {
                        mMediaMap.remove(mediaFile.getPath());
                        mScanSize -= mediaFile.getSize();
                    } else {
                        mMediaMap.put(mediaFile.getPath(), mediaFile);
                        mScanSize += mediaFile.getSize();
                    }

                    nSize += mediaFile.getSize();
                }

                mCacheValid = true;
                mCacheTime = System.currentTimeMillis();
            }
        }

        return nSize;
    }


    /**
     * 开始扫描
     * @param includeExpired 是否需要包括过期的照片
     */
    public void startScan(final boolean includeExpired) {
    }

    /**
     * 从任务队列中获取任务对象
     * @return
     */
    private RecycleDataInfo PopRecycleTask() {
        synchronized (mRecycleDataList) {
            if (mRecycleDataList.isEmpty()) {
                try {
                    mRecycleDataList.wait(TASK_WAIT_TIME_OUT);
                    if (mRecycleDataList.isEmpty()) {
                        mFirstRecycle = true;
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //notify();
            RecycleDataInfo info = new RecycleDataInfo();
            RecycleDataInfo infoList = mRecycleDataList.poll();
            info.mCallBack = infoList.mCallBack;
            info.mCallFrom = infoList.mCallFrom;
            info.mList = infoList.mList;

            return info;
        }
    }
}

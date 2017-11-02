package com.clean.spaceplus.cleansdk.junk.engine.junk;

import com.clean.spaceplus.cleansdk.BuildConfig;
import com.clean.spaceplus.cleansdk.junk.engine.ObjPoolMgr;
import com.hawkclean.framework.log.NLog;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/27 21:02
 * @copyright TCL-MIG
 */
public class JunkEngineWrapperUpdateInfo extends ObjPoolMgr.KPoolObj {
    private final static String TAG=JunkEngineWrapperUpdateInfo.class.getSimpleName();
    public JunkEngineWrapperUpdateInfo() {
        reset();
    }

    @Override
    public void reset() {
        mEngineWrapperStatus = JunkEngineWrapper.ENGINE_WRAPPER_STATUS_NONE;
        mProgressPosition = 0;
        mScanSize = 0L;
        mCheckedScanSize = 0L;
        mCleanSize = 0L;
        mProcessScanSize = 0L;
        mProcessCheckedScanSize = 0L;
        mProcessCleanSize = 0L;
        mSysCacheScanSize = 0L;
        mSysCacheCheckedScanSize = 0L;
        mSysCacheCleanSize = 0L;
    }

    public JunkEngineWrapperUpdateInfo updateValues(int engineWrapperStatus, int progressPosition,
                                                    long scanSize, long checkedScanSize, long cleanSize,
                                                    long processScanSize, long processCheckedScanSize, long processCleanSize,
                                                    long sysCacheScanSize, long sysCacheCheckedScanSize, long sysCacheCleanSize) {
        mEngineWrapperStatus = engineWrapperStatus;
        mProgressPosition = progressPosition;
        mScanSize = scanSize;
        mCheckedScanSize = checkedScanSize;
        mCleanSize = cleanSize;
        mProcessScanSize = processScanSize;
        mProcessCheckedScanSize = processCheckedScanSize;
        mProcessCleanSize = processCleanSize;
        mSysCacheScanSize = sysCacheScanSize;
        mSysCacheCheckedScanSize = sysCacheCheckedScanSize;
        mSysCacheCleanSize = sysCacheCleanSize;

        if (JunkEngineWrapper.ENGINE_WRAPPER_STATUS_FINISH_SCAN == engineWrapperStatus
                || JunkEngineWrapper.ENGINE_WRAPPER_STATUS_FINISH_CLEAN == engineWrapperStatus) {
            mProgressPosition = 100;
        }

        if (mCheckedScanSize > mScanSize) {
            if (BuildConfig.DEBUG) {
//                throw new RuntimeException(String.format("mCheckedScanSize(%d) > mScanSize(%d)", mCheckedScanSize, mScanSize));
                NLog.w(TAG, "mCheckedScanSize:"+mCheckedScanSize+", mScanSize:"+mScanSize);
            }
            mCheckedScanSize = mScanSize;
        }
        if (mScanSize < 0L) {
            if (BuildConfig.DEBUG) {
//                throw new RuntimeException(String.format("mScanSize(%d) < 0L", mScanSize));
                NLog.w(TAG, "mScanSize(%d) < 0L", mScanSize);
            }
            mScanSize = 0L;
        }
        if (mCheckedScanSize < 0L) {
            if (BuildConfig.DEBUG) {
//                throw new RuntimeException(String.format("mCheckedScanSize(%d) < 0L", mCheckedScanSize));
                NLog.w(TAG, "mCheckedScanSize(%d) < 0L", mCheckedScanSize);
            }

            mCheckedScanSize = 0L;
        }
        if (mCleanSize < 0L) {
//            if (BuildConfig.DEBUG) {
//                throw new RuntimeException(String.format("mCleanSize(%d) < 0L", mCleanSize));
//            }
            mCleanSize = 0L;
        }

        if (mProcessCheckedScanSize > mProcessScanSize) {
//            if (BuildConfig.DEBUG) {
//                throw new RuntimeException(String.format("mProcessCheckedScanSize(%d) > mProcessScanSize(%d)", mProcessCheckedScanSize, mProcessScanSize));
//            }
            mProcessCheckedScanSize = mProcessScanSize;
        }
        if (mProcessScanSize < 0L) {
//            if (BuildConfig.DEBUG) {
//                throw new RuntimeException(String.format("mProcessScanSize(%d) < 0L", mProcessScanSize));
//            }
            mProcessScanSize = 0L;
        }
        if (mProcessCheckedScanSize < 0L) {
//            if (BuildConfig.DEBUG) {
//                throw new RuntimeException(String.format("mProcessCheckedScanSize(%d) < 0L", mProcessCheckedScanSize));
//            }
            mProcessCheckedScanSize = 0L;
        }
        if (mProcessCleanSize < 0L) {
//            if (BuildConfig.DEBUG) {
//                throw new RuntimeException(String.format("mProcessCleanSize(%d) < 0L", mProcessCleanSize));
//            }
            mProcessCleanSize = 0L;
        }

        if (mSysCacheCheckedScanSize > mSysCacheScanSize) {
//            if (BuildConfig.DEBUG) {
//                throw new RuntimeException(String.format("mSysCacheCheckedScanSize(%d) > mSysCacheScanSize(%d)", mSysCacheCheckedScanSize, mSysCacheScanSize));
//            }
            mSysCacheCheckedScanSize = mSysCacheScanSize;
        }
        if (mSysCacheScanSize < 0L) {
//            if (BuildConfig.DEBUG) {
//                throw new RuntimeException(String.format("mSysCacheScanSize(%d) < 0L", mSysCacheScanSize));
//            }
            mSysCacheScanSize = 0L;
        }
        if (mSysCacheCheckedScanSize < 0L) {
//            if (BuildConfig.DEBUG) {
//                throw new RuntimeException(String.format("mSysCacheCheckedScanSize(%d) < 0L", mSysCacheCheckedScanSize));
//            }
            mSysCacheCheckedScanSize = 0L;
        }
        if (mSysCacheCleanSize < 0L) {
//            if (BuildConfig.DEBUG) {
//                throw new RuntimeException(String.format("mSysCacheCleanSize(%d) < 0L", mSysCacheCleanSize));
//            }
            mSysCacheCleanSize = 0L;
        }

        NLog.d(TAG,
                    "status: %d, prog: %d, scan size: %,d, checked scan size: %,d, clean size: %,d, "
                            + "process scan size: %,d, checked process scan size: %,d, clean process size: %,d, "
                            + "syscache scan size: %,d, checked syscache scan size: %,d, clean syscache size: %,d.",
                    mEngineWrapperStatus, mProgressPosition,
                    mScanSize, mCheckedScanSize, mCleanSize,
                    mProcessScanSize, mProcessCheckedScanSize, mProcessCleanSize,
                    mSysCacheScanSize, mSysCacheCheckedScanSize, mSysCacheCleanSize);

        return this;
    }

    /**
     * 复制或新建一个JunkEngineWrapperUpdateInfo对象，将最新的数据复制进去。
     *
     * @param copyTo 如果传入为null，则新建一个复制对象并返回；如果不为null，则直接更新此对象并返回。
     * @return 更新后的对象
     */

    public JunkEngineWrapperUpdateInfo copyValue(JunkEngineWrapperUpdateInfo copyTo) {
        if (null == copyTo) {
            copyTo = new JunkEngineWrapperUpdateInfo();
        }

        return copyTo.updateValues(mEngineWrapperStatus, mProgressPosition, mScanSize, mCheckedScanSize,
                mCleanSize, mProcessScanSize, mProcessCheckedScanSize, mProcessCleanSize,
                mSysCacheScanSize, mSysCacheCheckedScanSize, mSysCacheCleanSize);
    }
    public int mEngineWrapperStatus;    ///< 引擎状态，取值为JunkEngineWrapper.ENGINE_WRAPPER_STATUS_*
    public int mProgressPosition;        ///< 进度条参考值，[0, 100]
    public long mScanSize;                ///< 扫描总大小或清理后剩余大小
    public long mCheckedScanSize;        ///< 已勾选的扫描项大小
    public long mCleanSize;                ///< 清理总大小
    public long mProcessScanSize;        ///< 进程扫描大小或清理后剩余大小
    public long mProcessCheckedScanSize;///< 进程已勾选的扫描项大小
    public long mProcessCleanSize;        ///< 进程清理大小
    public long mSysCacheScanSize;        ///< 系统缓存扫描大小或清理后剩余大小
    public long mSysCacheCheckedScanSize;///< 系统缓存已勾选的扫描项大小
    public long mSysCacheCleanSize;        ///< 系统缓存清理大小


    @Override
    public String toString() {
        return "JunkEngineWrapperUpdateInfo{" +
                "mEngineWrapperStatus=" + mEngineWrapperStatus +
                ", mProgressPosition=" + mProgressPosition +
                ", mScanSize=" + mScanSize +
                ", mCheckedScanSize=" + mCheckedScanSize +
                ", mCleanSize=" + mCleanSize +
                ", mProcessScanSize=" + mProcessScanSize +
                ", mProcessCheckedScanSize=" + mProcessCheckedScanSize +
                ", mProcessCleanSize=" + mProcessCleanSize +
                ", mSysCacheScanSize=" + mSysCacheScanSize +
                ", mSysCacheCheckedScanSize=" + mSysCacheCheckedScanSize +
                ", mSysCacheCleanSize=" + mSysCacheCleanSize +
                "} " + super.toString();
    }
}

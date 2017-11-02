package com.clean.spaceplus.cleansdk.junk.engine.junk;

import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.BuildConfig;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.utils.monitor.MonitorManager;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.ServiceConfigManager;
import com.clean.spaceplus.cleansdk.junk.engine.aidl.JunkService;
import com.hawkclean.framework.log.NLog;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import space.network.util.RuntimeCheck;

/**
 * @author liangni
 * @Description:
 * @date 2016/5/3 17:02
 * @copyright TCL-MIG
 */
public class JunkSizeMgr {

    private final static String TAG=JunkSizeMgr.class.getSimpleName();
    private static JunkSizeMgr mInst = new JunkSizeMgr();

    private JunkSizeMgr() {
    }

    public static JunkSizeMgr getInstance() {
        return mInst;
    }


    public static final int JUNK_SIZE_MGR_TYPE_NONE = 0;
    public static final int JUNK_SIZE_MGR_TYPE_STD = 1;
    public static final int JUNK_SIZE_MGR_TYPE_ADV = 2;
    public static final int JUNK_SIZE_MGR_TYPE_ZEUS = 3;
    public static final int JUNK_SIZE_MGR_TYPE_VIDEO = 4;
    public static final int JUNK_SIZE_MGR_TYPE_VIDEO_NUM = 5;
    public static final int JUNK_SIZE_MGR_TYPE_PIC_RECYCLE = 6;
    public static final int JUNK_SIZE_MGR_TYPE_PROC = 7;
    public static final int JUNK_SIZE_MGR_TYPE_RUB_ADV = 8;
    public static final int JUNK_SIZE_MGR_TYPE_APK = 9;
    public static final int JUNK_SIZE_MGR_TYPE_DOWNLOAD = 10;
    public static final int JUNK_SIZE_MGR_TYPE_BLUETOOTH = 11;
    /**
     * 查询指定类别清理大小。
     * @param type 查询类别，取值为JUNK_SIZE_MGR_TYPE_*
     * @return -1L表示失败或无数据；非负数表示成功查询到的大小。
     */
    public long queryJunkSize(int type) {

        if (RuntimeCheck.IsServiceProcess()) {
            DataMgr dm = getDataMgr(type);
            if (null != dm) {
                return dm.queryJunkSize();
            }
        } else {
            try {
                return getJunkService().queryJunkSize(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return -1L;
    }

    /**
     * 从记录的指定类别清理项中减去指定的清理大小，并在减去后完成notifyJunkSize()操作。
     * @param type 操作类别，取值为JUNK_SIZE_MGR_TYPE_*
     * @param size 清理大小值
     * @return 减去后再调用queryJunkSize()所能查询到的值
     */
    public long substractJunkSize(int type, long size) {

        if (RuntimeCheck.IsServiceProcess()) {
            DataMgr dm = getDataMgr(type);
            if (null != dm) {
                long rst = dm.substractJunkSize(size);
                if (JUNK_SIZE_MGR_TYPE_STD == type) {
                    MonitorManager.getInstance().triggerMonitor(MonitorManager.TYPE_STD_JUNK_SIZE, null, Long.valueOf(rst));
                }
                return rst;
            }
        } else {
            try {
                return getJunkService().substractJunkSize(type, size);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return -1L;
    }

    /**
     * 通知更新清理项大小。
     * @param type 操作类别，取值为JUNK_SIZE_MGR_TYPE_*
     * @param size
     */
    public void notifyJunkSize(int type, long size) {

        if (RuntimeCheck.IsServiceProcess()) {
            DataMgr dm = getDataMgr(type);
            if (null != dm) {
                long rst = dm.notifyJunkSize(size);
                if (JUNK_SIZE_MGR_TYPE_STD == type) {
                    MonitorManager.getInstance().triggerMonitor(MonitorManager.TYPE_STD_JUNK_SIZE, null, Long.valueOf(rst));
                }
            }
        } else {
           /* try {
                getJunkService().notifyJunkSize(type, size);
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
        }
    }

    private JunkService getJunkService() {
        if (null == mJunkService) {
         //   mJunkService = (JunkService) ServiceManager.getInstance().getService(ServiceDefine.JUNK_SERVICE);
        }

        return mJunkService;
    }

    private DataMgr getDataMgr(int type) {

        RuntimeCheck.CheckServiceProcess();

        switch (type) {
            case JUNK_SIZE_MGR_TYPE_STD:
                if (null == mStdDataMgr) {
                    synchronized (this) {
                        if (null == mStdDataMgr) {
                            mStdDataMgr = new DataMgr(type);
                        }
                    }
                }
                return mStdDataMgr;

            case JUNK_SIZE_MGR_TYPE_ADV:
                if (null == mAdvDataMgr) {
                    synchronized (this) {
                        if (null == mAdvDataMgr) {
                            mAdvDataMgr = new DataMgr(type);
                        }
                    }
                }
                return mAdvDataMgr;

            case JUNK_SIZE_MGR_TYPE_ZEUS:
                if (null == mZeusDataMgr) {
                    synchronized (this) {
                        if (null == mZeusDataMgr) {
                            mZeusDataMgr = new DataMgr(type);
                        }
                    }
                }
                return mZeusDataMgr;

            case JUNK_SIZE_MGR_TYPE_VIDEO:
                if (null == mVideoDataMgr) {
                    synchronized (this) {
                        if (null == mVideoDataMgr) {
                            mVideoDataMgr = new DataMgr(type);
                        }
                    }
                }
                return mVideoDataMgr;

            case JUNK_SIZE_MGR_TYPE_VIDEO_NUM:
                if (null == mVideoNumDataMgr) {
                    synchronized (this) {
                        if (null == mVideoNumDataMgr) {
                            mVideoNumDataMgr = new DataMgr(type);
                        }
                    }
                }
                return mVideoNumDataMgr;
            case JUNK_SIZE_MGR_TYPE_PIC_RECYCLE:
                if (null == mPicRecycleDataMgr) {
                    synchronized (this) {
                        if (null == mPicRecycleDataMgr) {
                            mPicRecycleDataMgr = new DataMgr(type);
                        }
                    }
                }
                return mPicRecycleDataMgr;
            case JUNK_SIZE_MGR_TYPE_PROC:
                if (null == mProcDataMgr) {
                    synchronized (this) {
                        if (null == mProcDataMgr) {
                            mProcDataMgr = new DataMgr(type);
                        }
                    }
                }
                return mProcDataMgr;
            case JUNK_SIZE_MGR_TYPE_RUB_ADV:
                if (null == mRubAdvDataMgr) {
                    synchronized (this) {
                        if (null == mRubAdvDataMgr) {
                            mRubAdvDataMgr = new DataMgr(type);
                        }
                    }
                }
                return mRubAdvDataMgr;
            case JUNK_SIZE_MGR_TYPE_APK:
                if (null == mApkDataMgr) {
                    synchronized (this) {
                        if (null == mApkDataMgr) {
                            mApkDataMgr = new DataMgr(type);
                        }
                    }
                }
                return mApkDataMgr;
            case JUNK_SIZE_MGR_TYPE_DOWNLOAD:
                if (null == mDownloadDataMgr) {
                    synchronized (this) {
                        if (null == mDownloadDataMgr) {
                            mDownloadDataMgr = new DataMgr(type);
                        }
                    }
                }
                return mDownloadDataMgr;
            case JUNK_SIZE_MGR_TYPE_BLUETOOTH:
                if (null == mBluetoothDataMgr) {
                    synchronized (this) {
                        if (null == mBluetoothDataMgr) {
                            mBluetoothDataMgr = new DataMgr(type);
                        }
                    }
                }
                return mBluetoothDataMgr;
            case JUNK_SIZE_MGR_TYPE_NONE:
            default:
                if (BuildConfig.DEBUG && PublishVersionManager.isTest()) {
                    throw new IllegalArgumentException();
                } else {
                    return null;
                }
        }
    }

    private JunkService mJunkService = null;

    private DataMgr mStdDataMgr = null;
    private DataMgr mAdvDataMgr = null;
    private DataMgr mZeusDataMgr = null;
    private DataMgr mVideoDataMgr = null;
    private DataMgr mVideoNumDataMgr = null;
    private DataMgr mPicRecycleDataMgr = null;
    private DataMgr mProcDataMgr = null;
    private DataMgr mRubAdvDataMgr = null;
    private DataMgr mApkDataMgr = null;
    private DataMgr mDownloadDataMgr = null;
    private DataMgr mBluetoothDataMgr = null;

    private static class DataMgr {

        public DataMgr(int type) {
            mType = type;

            switch (type) {
                case JUNK_SIZE_MGR_TYPE_STD:
                    mTypeCfgName = ServiceConfigManager.CM_STD_JUNK_SIZE;
                    break;

                case JUNK_SIZE_MGR_TYPE_ADV:
                    mTypeCfgName = ServiceConfigManager.CM_ADV_JUNK_SIZE;
                    break;

                case JUNK_SIZE_MGR_TYPE_ZEUS:
                    mTypeCfgName = ServiceConfigManager.CM_ZEUS_CLEAN_JUNK_SIZE;
                    break;

                case JUNK_SIZE_MGR_TYPE_VIDEO:
                    mTypeCfgName = ServiceConfigManager.CM_VIDEO_CLEAN_JUNK_SIZE;
                    break;

                case JUNK_SIZE_MGR_TYPE_VIDEO_NUM:
                    mTypeCfgName = ServiceConfigManager.CM_VIDEO_CLEAN_JUNK_NUM;
                    break;
                case JUNK_SIZE_MGR_TYPE_PIC_RECYCLE:
                    mTypeCfgName = ServiceConfigManager.CM_IS_PIC_RECYCLE_IN_ADV;
                    break;
                case JUNK_SIZE_MGR_TYPE_PROC:
                    mTypeCfgName = ServiceConfigManager.CM_PROC_JUNK_SIZE;
                    break;
                case JUNK_SIZE_MGR_TYPE_RUB_ADV:
                    mTypeCfgName = ServiceConfigManager.CM_RUB_ADV_JUNK_SIZE;
                    break;
                case JUNK_SIZE_MGR_TYPE_APK:
                    mTypeCfgName = ServiceConfigManager.CM_APK_JUNK_SIZE;
                    break;
                case JUNK_SIZE_MGR_TYPE_DOWNLOAD:
                    mTypeCfgName = ServiceConfigManager.CM_DOWNLOAD_JUNK_SIZE;
                    break;
                case JUNK_SIZE_MGR_TYPE_BLUETOOTH:
                    mTypeCfgName = ServiceConfigManager.CM_BLUETOOTH_JUNK_SIZE;
                    break;
                case JUNK_SIZE_MGR_TYPE_NONE:
                default:
                    if (BuildConfig.DEBUG && PublishVersionManager.isTest()) {
                        throw new IllegalArgumentException();
                    } else {
                        mTypeCfgName = null;
                        break;
                    }
            }
        }

        public long queryJunkSize() {

            RuntimeCheck.CheckServiceProcess();

            long rst = 0;
            synchronized(mMutex) {
                if (mSize < -1 && (!TextUtils.isEmpty(mTypeCfgName))) {
                    mSize = ServiceConfigManager.getInstanse(
                            SpaceApplication.getInstance().getContext())
                            .getLongValue(mTypeCfgName, -1L);
                }
                rst = mSize;
            }

            NLog.d("JSM", "Q: type(%d) %d", mType, rst);
            return rst;
        }

        public long substractJunkSize(long size) {

            RuntimeCheck.CheckServiceProcess();

            long rst = 0;
            synchronized(mMutex) {
                if (mSize < -1) {
                    mSize = queryJunkSize();
                }

                if (mSize < 0) {
                    NLog.d("JSM", "S: type(%d) %d", mType, mSize);
                    return mSize;
                }

                setJunkSize(mSize - size);
                rst = mSize;
            }

            NLog.d("JSM", "S: type(%d) %d", mType, rst);
            return rst;
        }

        public long notifyJunkSize(long size) {

            RuntimeCheck.CheckServiceProcess();

            synchronized(mMutex) {
                setJunkSize(size);
                size = mSize;
            }

            NLog.d("JSM", "N: type(%d) %d", mType, size);

            return size;
        }

        private void setJunkSize(long size) {

            if (size < 0) {
                size = -1;
            }

            synchronized(mMutex) {
                if (!TextUtils.isEmpty(mTypeCfgName)) {
                    ServiceConfigManager.getInstanse(
                           SpaceApplication.getInstance().getContext())
                            .setLongValue(mTypeCfgName, size);
                }
                mSize = size;
            }
        }

        private Object mMutex = new Object();
        private long mSize = -2;
        private final int mType;
        private final String mTypeCfgName;
    }
}

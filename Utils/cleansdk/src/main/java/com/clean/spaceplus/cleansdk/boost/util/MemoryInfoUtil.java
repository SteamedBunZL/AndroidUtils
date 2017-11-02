package com.clean.spaceplus.cleansdk.boost.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.boost.BoostConfigManager;
import com.clean.spaceplus.cleansdk.boost.engine.data.IPhoneMemoryInfo;
import com.clean.spaceplus.cleansdk.boost.engine.data.MemoryChangeParam;
import com.clean.spaceplus.cleansdk.boost.engine.data.PhoneMemoryInfo;
import com.hawkclean.framework.log.NLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author zengtao.kuang
 * @Description: 内存信息工具 ProcessInfoHelperImpl
 * @date 2016/4/6 14:30
 * @copyright TCL-MIG
 */
public class MemoryInfoUtil {
    public static final int INVALID_UID = -1;
    public static final String UI_PID = "ui_pid";

    static final long UI_PROCESS_MEMORY_QUERY_INTERVAL = 1000 * 5;

    static final long MAX_UI_MEMORY_HAD = 1024l * 1024l * 1024l; // 内存不会超过1G吧？？
    static final long MAX_TOTAL_MEMORY = 1024l * 1024l * 1024l * 100l; // 100G了，我x，不可能比这还高吧？？


    private static MemoryInfoUtil sInst = new MemoryInfoUtil();


    /**
     * 在service中调用
     * */
    public static MemoryInfoUtil getInst() {
        return sInst;
    }

    private MemoryInfoUtil() {
        mMemory = new PhoneMemoryInfo(getAvailableMemoryByte(),
                getTotalMemoryByte());
    }

    /**
     * 在service中调用
     * */
    public IPhoneMemoryInfo getProcMemoryInfo() {

        synchronized (sInst) {
            if (mMemory != null&&!isMemoryDataInCache()) {
                mMemory.flush(getAvailableMemoryByte(), getTotalMemoryByte());

                //如果内存初始化的时候信息就已经出现异常，则上报该异常问题
                if(mMemory.mTotalMemoryByte < 0 || mMemory.mAvailableMemoryByteReal < 0 ||
                        (mMemory.mTotalMemoryByte <= mMemory.mAvailableMemoryByteReal)
                        || mMemory.mUsedMemoryPercent <= 0 || mMemory.mUsedMemoryPercent >= 100){
                    //FIXME
//                    BackgroundThread.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            cm_memory_negative memReport = new cm_memory_negative();
//                            memReport.mTotalShow = mMemory.mTotalMemoryByte;
//                            memReport.mAvailMemReal = mMemory.mAvailableMemoryByteReal;
//                            memReport.mAvailMemCached = mMemory.mAvailableMemoryByte;
//                            memReport.mPercent  = mMemory.mUsedMemoryPercent;
//                            memReport.mSource = cm_memory_negative.FLUSH;
//                            memReport.initAndReport();
//                        }
//                    });
                }
            }
        }

        return mMemory;
    }

    /**
     * 在service中调用
     * */
    public int setMemoryChange(MemoryChangeParam param) {
        if (param == null) {
            return 0;
        }

        if (param.getOperation() == MemoryChangeParam.OP_ON_MEMORY_BOOST_NORMAL
                || param.getOperation() == MemoryChangeParam.OP_ON_MEMORY_BOOST_LONG) {
            mGetUIProcessMemoryTimeStamp = 0;
        }

        synchronized (sInst) {
            flush(getAvailableMemoryByte(), getTotalMemoryByte(),
                    param.getOperation(), param.getCachedMemory());
        }
        return 0;
    }

    private void flush(long availableReal, long total, int operation, long cachedAvailable) {

        switch (operation) {

            case MemoryChangeParam.OP_ON_MEMORY_BOOST_NORMAL:
                mMemory.mState = IPhoneMemoryInfo.STATE_CACHED_DATA;
                mMemory.mAvailableMemoryByte = cachedAvailable;
                mTimeStamp = System.currentTimeMillis();

                mTIMEOUT = IPhoneMemoryInfo.TIMEOUT_SHORT;
                break;

            case MemoryChangeParam.OP_ON_MEMORY_BOOST_LONG:
                mMemory.mState = IPhoneMemoryInfo.STATE_CACHED_DATA;
                mMemory.mAvailableMemoryByte = cachedAvailable;
                mTimeStamp = System.currentTimeMillis();

                mTIMEOUT = IPhoneMemoryInfo.TIMEOUT_LONG;
                break;

            case MemoryChangeParam.OP_ON_CLEAN_CACHE_NORMAL:
                mMemory.mState = IPhoneMemoryInfo.STATE_REAL_DATA;
                mMemory.mAvailableMemoryByte = availableReal;

                mTimeStamp = 0;
                mTIMEOUT = IPhoneMemoryInfo.TIMEOUT_SHORT;
                break;

            case MemoryChangeParam.OP_ON_CLEAN_CACHE_LONG:

                if (mTIMEOUT == IPhoneMemoryInfo.TIMEOUT_LONG) {
                    mTIMEOUT = IPhoneMemoryInfo.TIMEOUT_SHORT;
                }

                if (isMemoryDataInCache()) {
                    mMemory.mState = IPhoneMemoryInfo.STATE_CACHED_DATA;
                    //mMemory.mAvailableMemoryByte = cachedAvailable;
                    // 不动
                } else {
                    mMemory.mState = IPhoneMemoryInfo.STATE_REAL_DATA;
                    mMemory.mAvailableMemoryByte = availableReal;
                    mTimeStamp = 0;
                }

                break;
        }


        mMemory.mTotalMemoryByte = total;
        mMemory.mAvailableMemoryByteReal = availableReal;

		/* 1、如果total memory为0，则认为当前内存计算出问题了，直接返回默认值
		 * 2、如果total memory比available memory小，也认为内存值计算出问题了，也返回默认值*/
        if (0 < mMemory.mTotalMemoryByte && (mMemory.mTotalMemoryByte > mMemory.mAvailableMemoryByte)) {
            mMemory.mUsedMemoryPercent = (int)(((mMemory.mTotalMemoryByte - mMemory.mAvailableMemoryByte) * 100f) / mMemory.mTotalMemoryByte);
        } else {
            mMemory.mUsedMemoryPercent = IPhoneMemoryInfo.DEFAULT_USED_MEMORY_PERCENT; // default

            //如果内存值出问题，则当前缓存信息认为已经无效，需要重新加载内存信息
            mMemory.mState = IPhoneMemoryInfo.STATE_REAL_DATA;
            mTimeStamp = 0;
        }

        if(mMemory.mUsedMemoryPercent <= 0){
            NLog.d("MemoryFlush", "available:"+mMemory.mAvailableMemoryByte+";totalSize:"+mMemory.mTotalMemoryByte+";percentage:"+mMemory.mUsedMemoryPercent+";operation:"+operation);
        }
    }



    /**
     * 当前内存数据是否还是缓存
     * */
    private boolean isMemoryDataInCache() {
        boolean cache = mTimeStamp > 0
                && (System.currentTimeMillis() - mTimeStamp < mTIMEOUT);
        mMemory.mIsCache = cache;
        return cache;
    }

    private long getTotalMemoryByte() {
        if (sTotalMemByte > 1) {
            return sTotalMemByte;
        }

        sTotalMemByte = getTotalMemoryByteDirect();
        return sTotalMemByte;
    }


    /**
     * 要处理ui进程内存问题
     * */
    private long getAvailableMemoryByte() {
        return getAvailableMemoryByteDirect() + getUIProcessMemoryByte();
    }


    private long getUIProcessMemoryByte() {

        if (isNeedFlushUIProcessMemory()) {

            mGetUIProcessMemoryTimeStamp = System.currentTimeMillis();

            int pid = getValidUIPid();
            if (pid == INVALID_UID) {
                mUIProcessMemory = 0;
                return 0;
            }

            mUIProcessMemory = getProcessMemory(pid);
            if (mUIProcessMemory <= 0 || MAX_UI_MEMORY_HAD < mUIProcessMemory) {
                mUIProcessMemory = 0; // 不科学，估计取值有问题
            }
        }

        return mUIProcessMemory;
    }

    private long getProcessMemory(int pid) {
        Context ctx = SpaceApplication.getInstance().getContext().getApplicationContext();
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        ArrayList<Integer> pids = new ArrayList<Integer>();
        pids.add(pid);

        return getProcessMemory(am, pids);
    }

    /**
     * 返回有效的ui进程pid，无效返回小于0
     * */
    private int getValidUIPid() {
        Context ctx = SpaceApplication.getInstance().getContext().getApplicationContext();
        int pid = BoostConfigManager.getInstanse(ctx).getIntValue(UI_PID, INVALID_UID);
        if (pid == INVALID_UID) {
            return INVALID_UID;
        }

        File f = new File("/proc/" + pid);
        if (!f.exists()) {
            return INVALID_UID;
        }

        return pid;
    }

    /**
     * ui有两种状态，在和没在。
     * ui在启动时候就记录pid到pref，这里先查询该pid是否存在，如果不存在，则标识没ui进程。
     * 如果存在，查值，为加速，两次查询间有一个最新时间间隔UI_PROCESS_MEMORY_QUERY_INTERVAL
     *
     * */
    private boolean isNeedFlushUIProcessMemory() {
        return UI_PROCESS_MEMORY_QUERY_INTERVAL < System.currentTimeMillis() - mGetUIProcessMemoryTimeStamp;
    }

    private long mGetUIProcessMemoryTimeStamp = 0;
    private long mUIProcessMemory = 0;

    private long mTimeStamp = 0;
    private int mTIMEOUT = IPhoneMemoryInfo.TIMEOUT_SHORT;
    private PhoneMemoryInfo mMemory = null;


    // -----------------------

    /**
     * 获得可用内存大小，不再调用AMS，单位是字节
     *
     * @note 不要直接调用这个函数，应该调用 ProcessInfoHelper 系列函数
     * @return
     */
    public static long getAvailableMemoryByteDirect() {
        MemInfoReader reader = new MemInfoReader();
        reader.readMemInfo();

        long freed = (reader.getFreeSize() + reader.getCachedSize());

        // total赋值校验
        {
            long total = reader.getTotalSize();

            if (0 < total && sTotalMemByte < total
                    && total < MAX_TOTAL_MEMORY) {
                sTotalMemByte = reader.getTotalSize();
            }
        }

        if (freed <= 0 || sTotalMemByte <= freed) {
            //freed = (long)(totalbyte * 0.05f);

            long repaired = 0L;
            Context context = SpaceApplication.getInstance().getContext().getApplicationContext();
            ActivityManager.MemoryInfo mem = getMemoryInfo(context);
            if (mem != null && 0 < mem.availMem && mem.availMem < sTotalMemByte) {
                repaired = mem.availMem;
            } else {
                repaired = (long)(sTotalMemByte * 0.15f);
            }

            NLog.d("getmem", freed + ", " + repaired + ", " + sTotalMemByte);
            freed = repaired;
        }

        return freed;
    }

    /**
     * 单位是byte。</br>
     * TotalMem可能返回0，会引起Bug，所以先修改为返回1，防止除法为0异常
     * @note 不要直接调用这个函数，应该调用 ProcessInfoHelper 系列函数
     * */

    public static long getTotalMemoryByteDirect() {

        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String strResult;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            strResult = str2.substring(str2.indexOf(":") + 1, str2.indexOf("kB")).trim();
            initial_memory = Integer.valueOf(strResult);
            localBufferedReader.close();
            long result = initial_memory * 1024l; // 注意单位！！！！！

            if (0 < result && sTotalMemByte < result && result < MAX_TOTAL_MEMORY) {
                sTotalMemByte = result;
            }

            return result;
        } catch (Exception e) {
            if(e != null){
                NLog.d("ProcessMemory", "MemoryInfo-->getTotalMem:"+e.toString());
            }
        }

        if (sTotalMemByte < 0) {
            return 1;
        } else {
            return sTotalMemByte;
        }
    }

    /**
     * 初始化记录UI进程pid，由主进程启动时刻调用
     *
     * */
    public static void recordPid() {
        int pid = android.os.Process.myPid();
        BoostConfigManager.getInstanse(SpaceApplication.getInstance().getContext().
                getApplicationContext()).setIntValue(MemoryInfoUtil.UI_PID, pid);
    }

    private static long sTotalMemByte = -1;

    /**
     * 单位是byte
     * */
    private static ActivityManager.MemoryInfo getMemoryInfo(Context context) {
        if (context == null) {
            return null;
        }
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        try {
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.getMemoryInfo(memoryInfo);
        } catch (Exception e) {
            return memoryInfo;
        }

        return memoryInfo;
    }
    //把 所有 关联的PID 的内存值加起来
    static long getProcessMemory(ActivityManager am, ArrayList<Integer> pids) {

        if (pids == null || pids.size() == 0) {
            return 0;
        }

        int pidCount = pids.size();
        int[] ipids = new int[pidCount];
        long memory = 0;

        for (int i = 0; i < pidCount; i++) {
            ipids[i] = pids.get(i);
        }

        try {
            Debug.MemoryInfo[] memoryInfoArray = getMemoryInfosByPids(am, ipids);
            if (memoryInfoArray != null && memoryInfoArray.length > 0) {
                for ( Debug.MemoryInfo mInfo : memoryInfoArray ) {
                    memory += getTotalPssMemory(mInfo);
                }
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
        return memory * 1024;
    }


    /**
     * 反射获得占用内存大小
     */
    private static int getTotalPssMemory(Debug.MemoryInfo mi) {
        try {
            if (sMethodGetTotalPss == null) {
                sMethodGetTotalPss = mi.getClass().getMethod("getTotalPss");
            }
            return (Integer) sMethodGetTotalPss.invoke(mi);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
        return 0;
    }

    private static Method sMethodGetTotalPss = null;

    /**
     * 反射获得MemoryInfo
     */
    private static Debug.MemoryInfo[] getMemoryInfosByPids(ActivityManager am, int[] pids) {

        try {
            if (sMethodGetProcessMemoryInfo == null) {
                sMethodGetProcessMemoryInfo = ActivityManager.class.getMethod("getProcessMemoryInfo", int[].class);
            }
            return (Debug.MemoryInfo[]) sMethodGetProcessMemoryInfo.invoke(am, pids);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }

        return null;
    }

    private static Method sMethodGetProcessMemoryInfo = null;
}

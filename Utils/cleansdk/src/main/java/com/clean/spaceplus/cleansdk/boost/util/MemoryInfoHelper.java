package com.clean.spaceplus.cleansdk.boost.util;

import android.app.ActivityManager;
import android.os.Debug;

import com.clean.spaceplus.cleansdk.boost.engine.BoostEngine;
import com.clean.spaceplus.cleansdk.boost.engine.data.BoostDataManager;
import com.clean.spaceplus.cleansdk.boost.engine.data.IPhoneMemoryInfo;
import com.clean.spaceplus.cleansdk.boost.engine.data.MemoryInfo;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessResult;
import com.hawkclean.framework.log.NLog;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author zengtao.kuang
 * @Description: 内存信息辅助类 ProcessInfoHelper
 * @date 2016/4/6 14:23
 * @copyright TCL-MIG
 */
public class MemoryInfoHelper {

    private static long sTotalMemByte = -1;
    private static Method sMethodGetTotalPss = null;
    private static Method sMethodGetProcessMemoryInfo = null;

    public static int getMemoryUsagePercent() {
        IPhoneMemoryInfo pmi = getPhoneMemoryInfo();
        if (pmi.getState() == IPhoneMemoryInfo.STATE_REAL_DATA) {
//            ProcessNeedHideAllCheckedHelper.getInst().reset();
        }

        ProcessResult result = (ProcessResult) BoostDataManager.getInstance()
                .getResult(BoostEngine.BOOST_TASK_MEM);
        if (result != null) {
            MemoryInfo mem = MemoryInfo.newInstance(result.mTotalAvailMem);
            return mem.getPercent();
        } else {
            return pmi.getUsedMemoryPercentage();
        }
    }

    /**获取当前内存剩余值， 单位byte*/
    public static long getAvailableMemoryByte() {
        IPhoneMemoryInfo phoneMemoryInfo = getPhoneMemoryInfo();
        if(phoneMemoryInfo==null){
            return 0;
        }else {
            return phoneMemoryInfo.getAvailableMemoryByte();
        }
    }
    /**
     * 一次性获取内存信息，包括总内存值，已用内存值，内存占比，数据是否超时。
     * @note 内部实现保证该函数返回非null
     * */
    public static IPhoneMemoryInfo getPhoneMemoryInfo() {

        return MemoryInfoUtil.getInst().getProcMemoryInfo();
    }

    /**获取当前总内存值，单位byte*/
    public static long getTotalMemoryByte() {

        if (sTotalMemByte > 1) {
            return sTotalMemByte;
        }

        sTotalMemByte = getPhoneMemoryInfo().getTotalMemoryByte();
        return sTotalMemByte;
    }

    /**获取当前总内存值，单位byte*/
    public static long getTotalMemoryByteFast() {

        if (sTotalMemByte > 1) {
            return sTotalMemByte;
        }

        sTotalMemByte = MemoryInfoUtil.getTotalMemoryByteDirect();
        return sTotalMemByte;
    }

    /**
     * 把 所有 关联的PID 的内存值加起来，单位是byte
     *
     * */
    public static long getProcessMemory(ActivityManager am, ArrayList<Integer> pids) {

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

}

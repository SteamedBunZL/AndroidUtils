package com.clean.spaceplus.cleansdk.boost.engine.data;

import android.os.Parcelable;

/**
 * @author zengtao.kuang
 * @Description: 手机内存信息接口
 * @date 2016/4/6 14:27
 * @copyright TCL-MIG
 */
public interface IPhoneMemoryInfo extends Parcelable {

    /**
     * 当前数据是真实内存值
     * */
    int STATE_REAL_DATA = 1;

    /**
     * 处于进程清理后的内存不增时间段内
     * */
    int STATE_CACHED_DATA = 2;


    int TIMEOUT_SHORT = 1000 * 20;
    int TIMEOUT_LONG = 1000 * 80;


    int DEFAULT_USED_MEMORY_PERCENT = 85;


    /**
     * 获取当前数据状态
     * @return IPhoneMemoryInfo.STATE_REAL_DATA</br>
     * IPhoneMemoryInfo.STATE_CACHED_DATA
     * */
    int getState();

    /**
     * 获取可用内存，单位byte
     * */
    long getAvailableMemoryByte();

    /**
     * 获取总内存，单位byte
     * */
    long getTotalMemoryByte();


    /**
     * 获取内存已用内存占比
     * */
    int getUsedMemoryPercentage();

    /**
     * 判断当前是否处于缓存期间内
     * @return
     */
    boolean isInCache();

}


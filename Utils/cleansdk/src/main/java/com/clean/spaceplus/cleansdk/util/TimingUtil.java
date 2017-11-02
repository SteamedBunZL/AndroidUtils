package com.clean.spaceplus.cleansdk.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author haiyang.tan
 * @Description: 记录耗时 不适用于高重复的任务的计时
 * @date 2016/6/15 13:11
 * @copyright TCL-MIG
 */
public class TimingUtil {
    private static final String TAG = TimingUtil.class.getName();

    private static List<Long> mTimeList = Collections.synchronizedList(new ArrayList<Long>());

    private static List<String> mTagList = Collections.synchronizedList(new ArrayList<String>());

    private static final Object mLock = new Object();

    public TimingUtil() {
    }

    /**
     * 重置所有记录
     */
    public static void reset() {
        mTagList.clear();
        mTimeList.clear();
    }

    /**
     * 开始一个记录
     * @param tag 记录的名字
     */
    public static void start(String tag) {
        synchronized (mLock) {
            long now = System.nanoTime();
            int index = mTagList.indexOf(tag);
            if (index == -1) {
                mTagList.add(tag);
                mTimeList.add(now);
            } else {
                mTimeList.set(index, now);
            }
        }
    }

    /**
     * 获取一个记录
     * @param tag 记录的名字
     * @return 时间 以毫秒为单位
     */
    public static long get(String tag){
        synchronized (mLock) {
            long now = System.nanoTime();
            int index = mTagList.indexOf(tag);
            if (index == -1) {
                return -1L;
            }
            long time = mTimeList.get(index);
            return now - time;
        }
    }

    /**
     * 结束一个记录
     * @param tag 记录的名字
     * @return 时间 以毫秒为单位
     */
    public static long end(String tag){
        synchronized (mLock) {
            long now = System.nanoTime();
            int index = mTagList.indexOf(tag);
            if (index == -1) {
                return -1L;
            }
            long time = mTimeList.get(index);
            mTimeList.remove(index);
            mTagList.remove(index);
            return now - time;
        }
    }

}

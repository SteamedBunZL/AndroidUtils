//=============================================================================
/**
 * @file MultiTaskTimeCalculator.java
 * @brief 用来累计并发任务的执行时间,并判断当前的执行时间是否超出阈值
 * @author qiuruifeng <qiuruifeng@ijinshan.com>
 * @date 2015-03-10
 */
//=============================================================================
package space.network.cleancloud;

import android.os.SystemClock;

import java.util.concurrent.atomic.AtomicInteger;

public class MultiTaskTimeCalculatorImpl implements MultiTaskTimeCalculator {

    private static AtomicInteger msIdSeed = new AtomicInteger(1);

    private long mPendingStartTime;
    private long mLastEndTime;

    private volatile long   mTotalTime;
    private volatile int    mPendingTaskId = 0;

    private long mThreshold;

    @Override
    public boolean setTimeDurationThreshold(long threshold) {
        boolean result = false;
        if (threshold > 0) {
            mThreshold = threshold;
            result = true;
        }
        return result;
    }

    @Override
    public TimeData taskStart() {
        TimeData result = new TimeData();
        synchronized (this) {
            int taskId = msIdSeed.getAndIncrement();
            result.mStartTime = SystemClock.uptimeMillis();
            result.mTaskId    = taskId;

            if (0 == mPendingStartTime) {
                mPendingStartTime = result.mStartTime;
                mPendingTaskId = taskId;
            }
        }
        //long total = getTaskTimeDuration();
        //Log.e("xxxx", "MultiTaskTimeCalculator PendingTaskId:" + mPendingTaskId + " total:" + total + " taskId:"+ result.mTaskId + " start:" + result.mStartTime);
        return result;
    }

    @Override
    public long taskEnd(TimeData startData) {
        if (null == startData) {
            return 0;
        }

        long incrementTime = 0;
        long duration = 0;
        synchronized (this) {
            startData.mEndTime = SystemClock.uptimeMillis();
            duration = startData.mEndTime - startData.mStartTime;

            if (startData.mTaskId == mPendingTaskId) {
                incrementTime = duration;
                mPendingStartTime = 0;
                mPendingTaskId = 0;
            } else {
                if (mPendingStartTime == 0) {
                    if (startData.mEndTime > mLastEndTime) {
                        incrementTime = startData.mEndTime - mLastEndTime;
                    }
                }
            }
            mLastEndTime = startData.mEndTime;
            if (incrementTime > 0) {
                mTotalTime += incrementTime;
            }
        }
        //long total = getTaskTimeDuration();
        //Log.e("xxxx", "MultiTaskTimeCalculator PendingTaskId:" + mPendingTaskId + " total:" + total + " taskId:"+ startData.mTaskId + " start:" + startData.mStartTime + " end:"+startData.mEndTime + " duration:" + duration);
        return duration;
    }

    @Override
    public boolean isDurationOverThreshold() {
        boolean result = isDurationOverThreshold(mThreshold);
        //Log.e("xxxx", "MultiTaskTimeCalculator isDurationOverThreshold:" + result);
        return result;
    }

    private boolean isDurationOverThreshold(long threshold) {
        //安全措施,避免未初始化阈值就使用导致无法正常扫描
        if (0 == threshold) {
            return false;
        }

        boolean result;
        if (mTotalTime > threshold) {
            result = true;
        } else {
           long time = getTaskTimeDuration();
            result = (time > threshold);
        }
        return result;
    }

    @Override
    public long getTaskTimeDuration() {
        long result;
        synchronized (this) {
            result = mTotalTime;
            if (mPendingStartTime != 0) {
                long currentTime = SystemClock.uptimeMillis();
                if (currentTime > mPendingStartTime) {
                    long diff = currentTime - mPendingStartTime;
                    result += diff;
                }
            }
        }
        return result;
    }

    @Override
    public void resetStatus() {
        synchronized (this) {
            mPendingStartTime = 0;
            mPendingTaskId = 0;
            mLastEndTime = 0;
            mTotalTime = 0;
        }
    }
}
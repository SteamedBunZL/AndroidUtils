package com.clean.spaceplus.cleansdk.junk.cleancloud;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/18 19:37
 * @copyright TCL-MIG
 */
public class DelMaintainTask implements Runnable {

    public  interface Processor {
        boolean doWork();

        boolean scheduleTask(Runnable task, long delayTime);

        long getMaxIdelTime();

        long getLastBusyTime();

        long getPredictionWorkingTime();
    }

    Processor mProcessor;

    public DelMaintainTask(Processor processor) {
        mProcessor = processor;
    }

    public void scheduleTask() {
        scheduleTask(System.currentTimeMillis());
    }

    public void scheduleTask(long currentTime) {

        // 先预测当前任务完成时间，然后加上可以允许的空闲时间,如果预测准确,那么下次检查的时候就可以正常执行
        long scheduleTime = mProcessor.getLastBusyTime()
                + mProcessor.getPredictionWorkingTime()
                + mProcessor.getMaxIdelTime();
        long delaytime;
        if (scheduleTime > currentTime) {
            delaytime = scheduleTime - currentTime;
        } else {
            // 肯定是哪里写错才会到这里，或者是调试的时候执行，导致运行到这个逻辑的时候太慢了
            delaytime = mProcessor.getMaxIdelTime();
        }
        mProcessor.scheduleTask(this, delaytime);
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        long lastBusyTime = mProcessor.getLastBusyTime();
        long diff = 0;
        if (currentTime >= lastBusyTime) {
            diff = currentTime - lastBusyTime;
            if (diff > mProcessor.getMaxIdelTime()) {
                if (!mProcessor.doWork()) {
                    // 失败了,重头来吧
                    mProcessor.scheduleTask(this, mProcessor.getMaxIdelTime());
                }
            } else {
                scheduleTask(currentTime);
            }
        } else {
            // 时间溢出？? 就干活吧。
            mProcessor.doWork();
        }
    }
}
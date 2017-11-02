package com.clean.spaceplus.cleansdk.junk.executor;

import android.os.Process;
import android.os.SystemClock;

import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskControllerObserver;
import com.clean.spaceplus.cleansdk.base.scan.TaskControllerImpl;
import com.clean.spaceplus.cleansdk.base.scan.TaskExecutors;
import com.clean.spaceplus.cleansdk.util.TimingUtil;
import com.hawkclean.framework.log.NLog;

import java.util.Queue;

/**
 * @author liangni
 * @Description:同时提供并发及超时特性
 * @date 2016/4/26 19:08
 * @copyright TCL-MIG
 */
public class SyncExecutors extends TaskExecutors {

    private final static String TAG=SyncExecutors.class.getSimpleName();
    @Override
    protected String getThreadName() {
        return "syn-executors-thread";
    }

    @Override
    protected void scanTaskList(ScanTaskListParams params) {

        if (null == params) {
            return;
        }

        Queue<TaskInfo> taskQueue = params.getTaskQueue();
        if (null == taskQueue || taskQueue.isEmpty()) {
            return;
        }

        for (TaskInfo taskInfo = taskQueue.poll(); null != taskInfo; taskInfo = taskQueue.poll()) {
            if (null == taskInfo.mTask) {
                continue;
            }

            if (mTaskCtrl.checkStop()) {
                ITaskBusCallback cb = getTaskBusCallback();
                if (null != cb) {
                    cb.notifySkipScan(taskInfo.mTask);
                }
                break;
            }

            NLog.i(TAG, "scanTask--taskTime:"+taskInfo.mTaskTime+",mEssentialTask:"+taskInfo.mEssentialTask);
            if (taskInfo.mTaskTime <= 0) {
                syncRunTask(taskInfo);
            } else if (taskInfo.mEssentialTask) {
                params.setLeftTime(asyncRunEssentialTask(taskInfo, params.getLeftTime()));
            } else {
                concurrentRunTask(taskInfo);
            }
        }
    }

    private void syncRunTask(TaskInfo taskInfo) {
        long tid = Thread.currentThread().getId();
        String name = taskInfo.mTask.getTaskDesc();
        TimingUtil.start(taskInfo.mTask.getClass().getName());
        NLog.i(TAG, new StringBuilder("(").append(tid).append(")start: ").append(name).toString());
        taskInfo.mTask.scan(mTaskCtrl);
        NLog.i(TAG, new StringBuilder("(").append(tid).append(")end: ").append(name).toString());
    }

    /**
     * @param taskInfo
     * @return 返回剩余的时间
     */
    private long asyncRunEssentialTask(TaskInfo taskInfo, long timeLeft) {
        final TaskControllerImpl taskCtrl = new TaskControllerImpl();
        final ScanTask task = taskInfo.mTask;
        Thread workingThread = new Thread() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                long tid = Thread.currentThread().getId();
                TimingUtil.start(task.getClass().getName());
                String name = task.getTaskDesc();
                NLog.i(TAG, new StringBuilder("(").append(tid).append(")(A)start: ").append(name).toString());
                task.scan(taskCtrl);
                NLog.i(TAG, new StringBuilder("(").append(tid).append(")(A)end: ").append(name).toString());
            }
        };
        int obsIndex = mTaskCtrl.addObserver(new ScanTaskControllerObserver() {

            @Override
            public void stop() {
                taskCtrl.notifyStop();
            }

            @Override
            public void reset() {
                taskCtrl.reset();
            }

            @Override
            public void timeout() {
                taskCtrl.notifyTimeOut();
            }

            @Override
            public void pause(long millis) {
                taskCtrl.notifyPause(millis);
            }

            @Override
            public void resume() {
                taskCtrl.resumePause();
            }
        });

        long time = 0L;

        workingThread.setName(task.getTaskDesc());
        workingThread.start();

        try {
            time = SystemClock.uptimeMillis();
            workingThread.join(taskInfo.mTaskTime + timeLeft);
            time = (SystemClock.uptimeMillis() - time);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            timeLeft = taskInfo.mTaskTime + timeLeft - time;

            if (timeLeft <= 0L) {
                // 超时。
                taskCtrl.notifyTimeOut();
                timeLeft = 0L;
                NLog.i(TAG, new StringBuilder("(").append(
                        Thread.currentThread().getId()).append(")(A)timeout: ")
                        .append(task.getTaskDesc()).toString());
            }
            if (obsIndex >= 0) {
                mTaskCtrl.removeObserver(obsIndex);
            }
        }

        return timeLeft;
    }

    private void concurrentRunTask(final TaskInfo taskInfo) {
        NLog.i(TAG, "concurrentRunTask--%s", taskInfo.mTask.getTaskDesc());
        class TaskCtrlTimeoutImpl extends TaskControllerImpl {
            private long mStartTime = 0L;
            private boolean mTimeOut = false;

            public void recordStartTime() {
                mStartTime = SystemClock.uptimeMillis();
            }

            @Override
            public boolean checkStop() {
                if ((!mTimeOut) && (SystemClock.uptimeMillis() - mStartTime) >= taskInfo.mTaskTime) {
                    mTimeOut = true;
                    notifyTimeOut();
                    NLog.i(TAG, new StringBuilder("(").append(Thread.currentThread().getId())
                            .append(")(A)timeout: ").append(taskInfo.mTask.getTaskDesc()).toString());
                }

                return super.checkStop();
            }
        }

        final TaskCtrlTimeoutImpl taskCtrl = new TaskCtrlTimeoutImpl();

        final int obsIndex = mTaskCtrl.addObserver(new ScanTaskControllerObserver() {

            @Override
            public void stop() {
                taskCtrl.notifyStop();
            }

            @Override
            public void reset() {
                taskCtrl.reset();
            }

            @Override
            public void timeout() {
                taskCtrl.notifyTimeOut();
            }

            @Override
            public void pause(long millis) {
                taskCtrl.notifyPause(millis);
            }

            @Override
            public void resume() {
                taskCtrl.resumePause();
            }
        });

        Thread workingThread = new Thread() {
            @Override
            public void run() {
                try {
                    long tid = Thread.currentThread().getId();
                    String name = taskInfo.mTask.getTaskDesc();
                    TimingUtil.start(taskInfo.mTask.getClass().getName());
                    NLog.i(TAG, new StringBuilder("(").append(tid).append(")(A)start: ").append(name).toString());
                    taskInfo.mTask.scan(taskCtrl);
                    NLog.i(TAG, new StringBuilder("(").append(tid).append(")(A)end: ").append(name).toString());
                } finally {
                    mTaskCtrl.removeObserver(obsIndex);
                }
            }
        };

        workingThread.setName(taskInfo.mTask.getTaskDesc());
        taskCtrl.recordStartTime();
        workingThread.start();
    }
}

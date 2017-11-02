package com.clean.spaceplus.cleansdk.junk.executor;

import android.os.SystemClock;

import com.clean.spaceplus.cleansdk.base.scan.ScanTaskControllerObserver;
import com.clean.spaceplus.cleansdk.base.scan.TaskControllerImpl;
import com.clean.spaceplus.cleansdk.base.scan.TaskExecutors;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/26 19:44
 * @copyright TCL-MIG
 */
public class ThreadPoolExecutors extends TaskExecutors {

    private final  static String TAG=ThreadPoolExecutors.class.getSimpleName();
    private TaskInfo firstTaskInfo;
    private int mOtherTaskTimeDelay;
    private int mThreadNum;

    public ThreadPoolExecutors() {
        super();
        int cpuNums = Runtime.getRuntime().availableProcessors();
        mThreadNum = cpuNums == 1 ? 4 : cpuNums * 2;
        mOtherTaskTimeDelay = 0;
        firstTaskInfo = null;
    }

    public ThreadPoolExecutors(int nThreadNum) {
        super();
        mThreadNum = nThreadNum;
        mOtherTaskTimeDelay = 0;
        firstTaskInfo = null;
    }

    @Override
    protected String getThreadName() {
        return "tpool-taskbus-thread";
    }

    // startScan之前调用
/*	public void setThreadNumber(int nNum) {
		mThreadNum = nNum;
	}*/

    // 第一个任务
/*	public boolean pushFirstTask(IScanTask task, int nTaskTimeout,
			int otherTaskTimeDelay) {
		if (null == task || nTaskTimeout < 0 || otherTaskTimeDelay < 0) {
			return false;
		}

		synchronized (this) {
			if (null != mTaskThread) {
				return false;
			}
			firstTaskInfo = new TaskInfo(task, nTaskTimeout);
			mOtherTaskTimeDelay = otherTaskTimeDelay;
		}
		return true;
	}*/

    private static class TaskCtrlTimeoutImpl extends TaskControllerImpl {
        private long mStartTime = 0L;
        private boolean mTimeOut = false;
        private ExecTaskInfo mExecTaskInfo;

        public TaskCtrlTimeoutImpl(ExecTaskInfo execTaskInfo) {
            mExecTaskInfo = execTaskInfo;
        }

        public void recordStartTime() {
            mStartTime = SystemClock.uptimeMillis();
        }

        @Override
        public boolean checkStop() {
            if (mStartTime == 0) {
                return super.checkStop();
            }
            if (mExecTaskInfo.taskInfo.mTaskTime <= 0) {
                return super.checkStop();
            }
            if ((!mTimeOut)
                    && (SystemClock.uptimeMillis() - mStartTime) >= mExecTaskInfo.taskInfo.mTaskTime) {
                mTimeOut = true;
                notifyTimeOut();
                NLog.i("TB",
                        new StringBuilder("(")
                                .append(Thread.currentThread().getId())
                                .append(")(A)timeout: ")
                                .append(mExecTaskInfo.taskInfo.mTask
                                        .getTaskDesc()).toString());
            }

            return super.checkStop();
        }
    }

    private class MyTaskCallable implements Callable<Void> {
        private ExecTaskInfo mExecTaskInfo;

        public MyTaskCallable(ExecTaskInfo execTaskInfo) {
            mExecTaskInfo = execTaskInfo;
        }

        @Override
        public Void call() {
            long tid = Thread.currentThread().getId();
            String name = mExecTaskInfo.taskInfo.mTask.getTaskDesc();
            NLog.i("TB",
                    new StringBuilder("(").append(tid).append(")(A)start: ")
                            .append(name).toString());
            mExecTaskInfo.taskCtrl.recordStartTime();
            mExecTaskInfo.taskInfo.mTask.scan(mExecTaskInfo.taskCtrl);
            NLog.i("TB", new StringBuilder("(").append(tid)
                    .append(")(A)end: ").append(name).toString());
            finishedScan.countDown();
            return (Void) null;
        }
    }

    private static class MyThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("ThreadPoolTaskBus.Thread");
            return thread;
        }
    }

    private CountDownLatch finishedScan;
    private ExecutorService taskExecs;

    @Override
    protected void scanTaskList(ScanTaskListParams params) {

        if (null == params) {
            return;
        }

        Queue<TaskInfo> taskQueue = params.getTaskQueue();
        if (null == taskQueue || taskQueue.isEmpty()) {
            return;
        }

        int countSize = firstTaskInfo != null ? taskQueue.size() + 1
                : taskQueue.size();
        finishedScan = new CountDownLatch(countSize);
        int currentSize = 0;
        ArrayList<ExecTaskInfo> execTaskInfoLists = new ArrayList<ExecTaskInfo>();
        int nThreads = mThreadNum;
        if (nThreads <= 0 || nThreads > countSize ) {
            nThreads = countSize;
        }
        taskExecs = Executors.newFixedThreadPool(
                nThreads, new MyThreadFactory());
        if (firstTaskInfo != null) {
            currentSize++;
            ExecTaskInfo firstExecTaskInfo = new ExecTaskInfo(
                    firstTaskInfo);
            execTaskInfoLists.add(firstExecTaskInfo);
            // 其他任务延迟执行
            if (mOtherTaskTimeDelay > 0) {
                firstExecTaskInfo.waitTask(mOtherTaskTimeDelay);
            }
        }
        ITaskBusCallback cb = getTaskBusCallback();
        for (TaskInfo taskInfo = taskQueue.poll(); null != taskInfo; taskInfo = taskQueue
                .poll()) {
            if (null == taskInfo.mTask) {
                continue;
            }
            if (mTaskCtrl.checkStop()) {
                if (null != cb) {
                    cb.notifySkipScan(taskInfo.mTask);
                }
                break;
            }

            ExecTaskInfo execTaskInfo = new ExecTaskInfo(taskInfo);
            currentSize++;
            execTaskInfoLists.add(execTaskInfo);
            if (taskInfo.mEssentialTask) {
                execTaskInfo.waitTask(0);
            }
        }
        for (int i = currentSize; i < countSize; i++) {
            finishedScan.countDown();
        }
        if (finishedScan.getCount() != 0) {
            while (true) {
                try {
                    finishedScan.await(200, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    break;
                }
                if (finishedScan.getCount() == 0) {
                    break;
                }
                for (ExecTaskInfo execTaskInfo : execTaskInfoLists) {
                    execTaskInfo.checkStop();
                }
            }
        }
    }

    private class ExecTaskInfo {
        TaskInfo taskInfo;
        Future<Void> future;
        TaskCtrlTimeoutImpl taskCtrl;
        int obsIndex;

        public void waitTask(int nTimeout) {
            int nUnitTime = 200;
            if (nTimeout <= 0) {
                while (true) {
                    if (checkStop()) {
                        mTaskCtrl.removeObserver(obsIndex);
                        return;
                    }
                    try {
                        future.get(nUnitTime, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                    } catch (ExecutionException e) {
                    } catch (TimeoutException e) {
                    }
                }
            } else {
                for (int nCurrTime = 0; nCurrTime < nTimeout; nCurrTime += nUnitTime) {
                    if (nCurrTime + nUnitTime > nTimeout) {
                        nUnitTime = nTimeout - nCurrTime;
                    }
                    if (checkStop()) {
                        mTaskCtrl.removeObserver(obsIndex);
                        return;
                    }
                    try {
                        future.get(nUnitTime, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                    } catch (ExecutionException e) {
                    } catch (Exception e) {
                    }
                }
            }
        }

        public boolean checkStop() {
            if (future.isCancelled() || future.isDone()) {
                return true;
            }
            return taskCtrl.checkStop();
        }

        public ExecTaskInfo(TaskInfo taskInfo) {
            this.taskInfo = taskInfo;
            init();
        }

        private void init() {
            taskCtrl = new TaskCtrlTimeoutImpl(this);
            obsIndex = mTaskCtrl.addObserver(new ScanTaskControllerObserver() {
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
            future = taskExecs.submit(new MyTaskCallable(this));
        }
    }
}

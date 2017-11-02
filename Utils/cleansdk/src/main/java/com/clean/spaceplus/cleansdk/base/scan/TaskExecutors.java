package com.clean.spaceplus.cleansdk.base.scan;

import android.os.Process;
import android.os.SystemClock;

import com.hawkclean.framework.log.NLog;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * @author liangni
 * @Description:任务调度器
 * @date 2016/4/22 14:53
 * @copyright TCL-MIG
 */
public class TaskExecutors {

    private final static String TAG=TaskExecutors.class.getSimpleName();
    public static final int TASK_BUS_WAIT_FINISHED = 0;  ///< 正常结束
    public static final int TASK_BUS_WAIT_TIME_UP = 1;   ///< 等待超时

    public static final int TASK_BUS_STATUS_READY = 0;	///< 准备状态
    public static final int TASK_BUS_STATUS_WORKING = 1;	///< 工作状态
    public static final int TASK_BUS_STATUS_FINISHED = 2; ///< 完成状态

    public  interface ITaskBusCallback {
        /**
         * TaskBus状态变化时回调
         * @param oldStatus		旧状态
         * @param newStatus		新状态
         */
         void changeTaskBusStatus(int oldStatus, int newStatus);

        /**
         * 当某个Task因取消或超时而得不到调用它的scan()函数时，用本回调通知。
         * @param task
         */
         void notifySkipScan(ScanTask task);
    }

    private Object mMutexForMembers = new Object();

    private int mTaskBusStatus = TASK_BUS_STATUS_READY;

    protected ScanTaskControllerImpl mTaskCtrl = new ScanTaskControllerImpl();

    private volatile Thread mTaskThread = null;
    private Semaphore mWorkingThreadSemaphore = new Semaphore(1);
    private volatile boolean mbOnEndingThread = false;  ///< sync by mWorkingThreadSemaphore.

    private Queue<TaskInfo> mTaskQueue = new LinkedList<>();

    private ITaskBusCallback mCallback = null;
    private boolean hasTaskPushed = false;


    protected static class TaskInfo {
        public ScanTask  mTask = null;	///< 扫描任务
        public int        mTaskTime = 0;	///< 扫描任务时长约束(单位ms)，非负数，若为0表示无时长约束。
        public boolean   mEssentialTask = false;	///< 是否必要任务。当提交异步任务时，此任务若为异步任务，则必须先等它完成后，才能启动后面的异步任务。

        public TaskInfo(ScanTask task, int time) {
            mTask = task;
            mTaskTime = time;
            mEssentialTask = false;
        }

        public TaskInfo(ScanTask task, int time, boolean essentialTask) {
            mTask = task;
            mTaskTime = time;
            mEssentialTask = essentialTask;
        }
    }


    /**
     * 设定TaskBus回调对象
     * @param cb	回调对象
     */
    public void setCallback(ITaskBusCallback cb) {
        synchronized (mMutexForMembers) {
            mCallback = cb;
        }
    }

    /**
     * 添加扫描任务(无优先级关系，按添加的先后顺序进行扫描)，startScan()之后不可再调用本函数。
     * @param task 扫描任务
     * @return 成功返回true，失败返回false。
     */
    public boolean pushTask(ScanTask task) {
        if (null == task) {
            return false;
        }

        synchronized (mMutexForMembers) {
            if (null != mTaskThread) {
                return false;
            }
            hasTaskPushed = true;
            mTaskQueue.offer(new TaskInfo(task, 0));
        }

        checkAndStartWorking();

        return true;
    }

    public boolean hasTaskPushed() {
        synchronized (mMutexForMembers) {
            return hasTaskPushed;
        }
    }

    /**
     * 添加扫描任务(无优先级关系，按添加的先后顺序进行扫描)，startScan()之后不可再调用本函数。
     * @param task 扫描任务
     * @param time 扫描任务时长约束(单位ms)，非负数，若为0表示无时长约束。
     * @return 成功返回true，失败返回false。
     */
    public boolean pushTask(ScanTask task, int time) {
        if (null == task || time < 0) {
            return false;
        }

        synchronized (mMutexForMembers) {
            if (null != mTaskThread) {
                return false;
            }
            hasTaskPushed = true;
            mTaskQueue.offer(new TaskInfo(task, time));
        }
        NLog.i(TAG,"::mgr:::checkAndStartWorking::::");
        checkAndStartWorking();

        return true;
    }

    /**
     * 添加扫描任务(无优先级关系，按添加的先后顺序进行扫描)，startScan()之后不可再调用本函数。
     * @param task 扫描任务
     * @param time 扫描任务时长约束(单位ms)，非负数，若为0表示无时长约束。
     * @param essentialTask 必要任务，在此任务完成或超时前，不启动后续添加的任务。
     * @return 成功返回true，失败返回false。
     */
    public boolean pushTask(ScanTask task, int time, boolean essentialTask) {
        if (null == task || time < 0) {
            return false;
        }

        synchronized (mMutexForMembers) {
            if (null != mTaskThread) {
                return false;
            }
            hasTaskPushed = true;
            mTaskQueue.offer(new TaskInfo(task, time, essentialTask));
        }

        checkAndStartWorking();

        return true;
    }

    /**
     * 取出旧的任务队列，并且同时创建一个新的任务队列。
     * @return 旧的任务队列，当没有任务时，返回为null。
     */
    private Queue<TaskInfo> obtainTaskQueue() {

        Queue<TaskInfo> outQueue = null;
        synchronized (mMutexForMembers) {
            if (!mTaskQueue.isEmpty()) {
                outQueue = mTaskQueue;
                mTaskQueue = new LinkedList<>();
            }
        }

        return outQueue;
    }

    protected ITaskBusCallback getTaskBusCallback() {
        synchronized (mMutexForMembers) {
            return mCallback;
        }
    }

    private boolean hasBeenStarted() {
        return getTaskBusStatus() != TASK_BUS_STATUS_READY;
    }

    protected  interface ScanTaskListParams {

         Queue<TaskInfo> getTaskQueue();

         void setLeftTime(long leftTime);
         long getLeftTime();
    }

    protected String getThreadName() {
        return "task-executor-thread";
    }

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

            if (taskInfo.mTaskTime <= 0) {
                String name = taskInfo.mTask.getTaskDesc();
                NLog.i(TAG, new StringBuilder("(").append(hashCode()).append(")start: ").append(name).append(" Time : ").append(SystemClock.uptimeMillis()).toString());
                taskInfo.mTask.scan(mTaskCtrl);
                NLog.i(TAG, new StringBuilder("(").append(hashCode()).append(")end: ").append(name).append(" Time : ").append(SystemClock.uptimeMillis()).toString());
            } else {
                final ScanTaskControllerImpl taskCtrl = new ScanTaskControllerImpl();
                final ScanTask task = taskInfo.mTask;
                Thread workingThread = new Thread() {
                    @Override
                    public void run() {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        String name = task.getTaskDesc();

                        NLog.i(TAG, new StringBuilder("(").append(hashCode()).append(")(A)start: ").append(name).append(" Time : ").append(SystemClock.uptimeMillis()).toString());
                        task.scan(taskCtrl);

                        NLog.i(TAG, new StringBuilder("(").append(hashCode()).append(")(A)end: ").append(name).append(" Time : ").append(SystemClock.uptimeMillis()).toString());
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

                workingThread.start();

                long time = 0L;
                try {
                    time = SystemClock.uptimeMillis();
                    workingThread.join(taskInfo.mTaskTime + params.getLeftTime());
                    time = (SystemClock.uptimeMillis() - time);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    params.setLeftTime(taskInfo.mTaskTime + params.getLeftTime() - time);

                    if (params.getLeftTime() <= 0L) {
                        // 超时。
                        taskCtrl.notifyTimeOut();
                        params.setLeftTime(0L);
                        NLog.i(TAG, new StringBuilder("(").append(hashCode()).append(")(A)timeout: ").append(task.getTaskDesc()).toString());
                    }
                    if (obsIndex >= 0) {
                        mTaskCtrl.removeObserver(obsIndex);
                    }
                }
            }
        }
    }

    private static class ScanTaskListParamsImpl implements ScanTaskListParams {

        private long mTimeLeft = 0L;
        private Queue<TaskInfo> mTaskQueue = null;

        public void setTaskQueue(Queue<TaskInfo> taskQueue) {
            mTaskQueue = taskQueue;
        }

        @Override
        public Queue<TaskInfo> getTaskQueue() {
            return mTaskQueue;
        }

        @Override
        public void setLeftTime(long leftTime) {
            mTimeLeft = leftTime;
        }

        @Override
        public long getLeftTime() {
            return mTimeLeft;
        }
    }

    private void checkAndStartWorking() {

        // 注意，不要在mMutexForMembers锁范围内调用本函数。

        try {
            try {
                mWorkingThreadSemaphore.acquire();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!mbOnEndingThread) {
                return;
            }

            if (null != mTaskThread) {
                try {
                    mTaskThread.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mTaskThread = null;
            }

            startScan();
            mbOnEndingThread = false;

        } finally {
            mWorkingThreadSemaphore.release();
        }
    }

    /**
     * 开始异步扫描，本函数执行后，不能再使用pushTask()添加扫描任务。
     * @return 成功返回true，失败返回false。
     */
    public boolean startScan() {
        synchronized (mMutexForMembers) {
            if (null != mTaskThread) {
                return false;
            }
            NLog.i(TAG,":::mgr:::checkAndStartWorking::startScan::::");
            if (!hasBeenStarted()) {
                mTaskCtrl.reset();
            }

            mTaskThread = new Thread() {
                @Override
                public void run() {
                    ITaskBusCallback cb = getTaskBusCallback();
                    Queue<TaskInfo> taskQueue = null;
                    try {
                        try {
                            mWorkingThreadSemaphore.acquire();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mbOnEndingThread = false;

                        ScanTaskListParamsImpl params = new ScanTaskListParamsImpl();

                        for (taskQueue = obtainTaskQueue(); null != taskQueue; taskQueue = obtainTaskQueue()) {

                            mWorkingThreadSemaphore.release();

                            try {

                                params.setTaskQueue(taskQueue);

                                if (!mTaskCtrl.checkStop()) {
                                    scanTaskList(params);
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }finally {
                                if (null != taskQueue && !taskQueue.isEmpty()) {
                                    for (TaskInfo taskInfo = taskQueue.poll(); null != taskInfo; taskInfo = taskQueue.poll()) {
                                        if (null == taskInfo.mTask) {
                                            continue;
                                        }
                                        if (null != cb) {
                                            cb.notifySkipScan(taskInfo.mTask);
                                        }
                                    }
                                }

                                try {
                                    mWorkingThreadSemaphore.acquire();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } finally {
                        mbOnEndingThread = true;
                        mWorkingThreadSemaphore.release();

                        changeTaskBusStatus(TASK_BUS_STATUS_FINISHED, cb);
//                      LocalStringDBUtil.getInstance().closeAllDB();
                    }
                }
            };

            changeTaskBusStatus(TASK_BUS_STATUS_WORKING, getTaskBusCallback());
            mTaskThread.setName(getThreadName());
            mTaskThread.start();
        }

        return true;
    }

    /**
     * 通知正在进行的异步扫描流程停止。
     * @return 成功返回true，失败返回false。
     */
    public boolean notifyStop() {
        synchronized (mMutexForMembers) {
            if (null == mTaskThread) {
                return false;
            }
        }

        mTaskCtrl.notifyStop();

        return true;
    }

    /**
     * 等待异步扫描流程结束
     * @param maxWaitTime 最长等待时间(单位ms)；若为0，表示永远等待；不能为负数。
     * @return 成功结束返回TASK_BUS_WAIT_FINISHED，
     *         等待超时返回TASK_BUS_WAIT_TIME_UP(极端情况下，有可能把成功结束的情况判为超时)，
     *         其他值为失败错误码。
     */
/*	public int waitForFinish(long maxWaitTime) {
		if (maxWaitTime < 0L) {
			return -1;
		}

		Thread taskThread = null;
		synchronized (mMutexForMembers) {
			if (null == mTaskThread) {
				return -2;
			}

			taskThread = mTaskThread;
		}

		long time = 0L;
		try {
			time = SystemClock.uptimeMillis();
			taskThread.join(maxWaitTime);
			time = (SystemClock.uptimeMillis() - time);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return -3;
		}

		time = maxWaitTime - time;

		if (maxWaitTime > 0L && time <= 0L) {
			// 超时。
			return TASK_BUS_WAIT_TIME_UP;
		}

		return TASK_BUS_WAIT_FINISHED;
	}*/

    /**
     * 取得TaskBus当前状态
     * @return TaskBus当前状态(TASK_BUS_STATUS_*)
     */
    public int getTaskBusStatus() {
        synchronized (mMutexForMembers) {
            return mTaskBusStatus;
        }
    }

    private void changeTaskBusStatus(int newTaskBusStatus, ITaskBusCallback cb) {
        int oldTaskBusStatus;
        synchronized (mMutexForMembers) {
            oldTaskBusStatus = mTaskBusStatus;
            mTaskBusStatus = newTaskBusStatus;
        }

        if (null != cb) {
            cb.changeTaskBusStatus(oldTaskBusStatus, newTaskBusStatus);
        }
    }

    /**
     * 通知暂停
     * @param millis 非负数，暂停指定时长后自动resume。(若为0，表示永远暂停，直到resumePause())
     */
    public void notifyPause(long millis) {
        synchronized (mMutexForMembers) {
            if (null == mTaskThread) {
                return;
            }
        }

        mTaskCtrl.notifyPause(millis);
    }

    /**
     * 从暂停状态恢复
     */
    public void resumePause() {
        synchronized (mMutexForMembers) {
            if (null == mTaskThread) {
                return;
            }
        }

        mTaskCtrl.resumePause();
    }
}

package com.clean.spaceplus.cleansdk.boost.engine.clean;

import android.content.Context;

import com.clean.spaceplus.cleansdk.boost.engine.BoostEngine;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessCleanSetting;
import com.clean.spaceplus.cleansdk.boost.engine.data.BoostResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: boost数据管理
 * @date 2016/4/5 20:28
 * @copyright TCL-MIG
 */
public class BoostCleanEngine {
    private List<BoostCleanTask> mTasks = new ArrayList<BoostCleanTask>();
    private CleanEngineCallback mCallback = null;
    private Context mContext;

    public BoostCleanEngine(Context ctx, BoostCleanSetting setting) {
        mContext = ctx;
        prepareTasks(setting);
    }

    public void clean(CleanEngineCallback callback) {
        mCallback = callback;
        CleanThread cleanThread = new CleanThread();
        cleanThread.setName("BoostCleanEngine clean");
        cleanThread.start();

    }

    public BoostResult cleanSync(int type) {
        BoostCleanTask task = null;
        for (BoostCleanTask t : mTasks) {
            if (t != null && t.getType() == type) {
                task = t;
                break;
            }
        }

        if (task != null) {
            return task.cleanSync();
        } else {
            return null;
        }
    }

    public void stopClean(int types) {
        for (BoostCleanTask t : mTasks) {
            if (t != null && (t.getType() | types) != 0) {
                t.stop();
            }
        }
    }

    private void prepareTasks(BoostCleanSetting setting) {
        if ((setting.mTaskType & BoostEngine.BOOST_TASK_MEM) != 0) {
            Object obj = setting.mSettings.get(BoostEngine.BOOST_TASK_MEM);
            ProcessCleanSetting procSetting;
            if (obj != null && obj instanceof ProcessCleanSetting) {
                procSetting = (ProcessCleanSetting)obj;
            } else {
                procSetting = new ProcessCleanSetting();
            }
            ProcessCleanTask task = new ProcessCleanTask(mContext, procSetting);
            mTasks.add(task);
        }

    }

    private class CleanThread extends Thread {
        @Override
        public void run() {
            for (final BoostCleanTask task : mTasks) {
                task.clean(new BoostCleanTask.ICleanTaskCallback() {
                    @Override
                    public void onCleanStart() {
                        mCallback.onCleanStart(task.getType());
                    }

                    @Override
                    public void onCleanProgress(Object data) {
                        mCallback.onCleanProgress(task.getType(), data);
                    }

                    @Override
                    public void onCleanFinish(Object result) {
                        mCallback.onCleanFinish(task.getType(), result);

                    }
                });
            }
        }
    }

    /*
     * Scan engine callback interface
     */
    public interface CleanEngineCallback {
        void onCleanStart(int type);
        void onCleanProgress(int type, Object data);
        void onCleanFinish(int type, Object result);
    }

    public static class DefaultCleanEngineCallbackImpl implements CleanEngineCallback {
        @Override
        public void onCleanFinish(int type, Object result) {

        }

        @Override
        public void onCleanStart(int type) {

        }

        @Override
        public void onCleanProgress(int type, Object data) {

        }
    }
}

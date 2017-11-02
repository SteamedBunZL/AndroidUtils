package com.clean.spaceplus.cleansdk.boost.engine.clean;

import android.content.Context;

import com.clean.spaceplus.cleansdk.boost.engine.BoostEngine;
import com.clean.spaceplus.cleansdk.boost.engine.data.BoostDataManager;
import com.clean.spaceplus.cleansdk.boost.engine.data.BoostResult;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessCleanSetting;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessHelper;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessResult;
import com.clean.spaceplus.cleansdk.boost.util.MemoryInfoHelper;
import com.clean.spaceplus.cleansdk.boost.util.ProcessUtils;
import com.clean.spaceplus.cleansdk.util.BackgroundThread;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: 进程清理任务
 * @date 2016/4/6 10:26
 * @copyright TCL-MIG
 */
public class ProcessCleanTask extends BoostCleanTask<ProcessCleanSetting> {
    public ProcessCleanTask(Context ctx, ProcessCleanSetting setting) {
        super(ctx, setting);
    }

    @Override
    public int getType() {
        return BoostEngine.BOOST_TASK_MEM;
    }

    @Override
    public void clean(BoostCleanTask.ICleanTaskCallback callback) {
        if (callback != null) {
            callback.onCleanStart();
        }

        // prepare data to clean
        boolean isSpecificClean = false;
        ProcessResult syncData = (ProcessResult) BoostDataManager.getInstance().getResult(getType());
        List<ProcessModel> cleanData = new ArrayList<ProcessModel>();

        if (mSetting.mCleanData != null && mSetting.mCleanData.size() > 0) {
            cleanData.addAll(mSetting.mCleanData);
            isSpecificClean = true;
        } else {
            if (syncData != null) {
                cleanData.addAll(syncData.getData());
            }
        }
        if (cleanData.size() <= 0) {
            if (callback != null) {
                callback.onCleanFinish(null);
            }
            return;
        }

        // for debug
        if (syncData != null) {
            long totalMem = MemoryInfoHelper.getTotalMemoryByte() / 1024;
            long freeMem = MemoryInfoHelper.getAvailableMemoryByte() / 1024;
            long appUsed = syncData.mTotalUsedMem;
            // TODO: add white list mem usage and native mem uesage
            NLog.d("KillTask", "[Start] system total:" + totalMem +
                    " free:" + freeMem + " app:" + appUsed);
        }

        // dump uncheck data
        for (ProcessModel m : cleanData) {
            if (!m.isChecked()) {
                NLog.d("KillTask", "Unchecked:" + m.getPkgName() + " mOOM:" + m.getOOMADJ() +
                        " mUID:" + m.getUid() + " mem:" + m.getMemory() / 1024 +
                        " services:" + m.getServicesCount());
            }
        }

        // clean checked and not white list data
        if (syncData != null) {
            syncData.mTotalCleanMem = 0;
        }
        int cleanCount = 0;
        for (ProcessModel m : cleanData) {
            if (isSpecificClean || (m.isChecked() && !m.mIsHide)) {
                ProcessUtils.killAsync(m);
                if (syncData != null) {
                    syncData.removeData(m.getPkgName());
                }

                m.setResult(ProcessModel.RESULT_CLEAN, ProcessModel.RESULT_FROM_ONE_KEY);
                cleanCount++;

                if (callback != null) {
                    callback.onCleanProgress(m);
                }
            }
        }

        // for debug
        if (cleanCount > 0) {
            BackgroundThread.getHandler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    long afterCleanFreeMem = MemoryInfoHelper.getAvailableMemoryByte() / 1024;
                    NLog.d("KillTask", "[Stop] after clean free:" + afterCleanFreeMem);
                }

            }, 3000);
        }

        if (callback != null) {
            callback.onCleanFinish(syncData);
        }

        //记录上次清理时间
        if (!isSpecificClean && syncData != null) {
            ProcessHelper.postCleanHandler(syncData);
        }
    }

    @Override
    public BoostResult cleanSync() {
        return null;
    }
}

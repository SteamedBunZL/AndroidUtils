package com.clean.spaceplus.cleansdk.boost.engine.process;

import com.clean.spaceplus.cleansdk.boost.engine.data.BoostResult;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.boost.util.MemoryInfoHelper;

/**
 * @author zengtao.kuang
 * @Description: 进程结果结果
 * @date 2016/4/6 10:26
 * @copyright TCL-MIG
 */
public class ProcessResult extends BoostResult<ProcessModel> {
    public long mTotalUsedMem = 0;
    public long mTotalCleanMem = 0;
    public long mTotalAvailMem = 0;

    public ProcessResult() {
        mTotalAvailMem = MemoryInfoHelper.getAvailableMemoryByte();
    }

    @Override
    protected void updatePrivateDataLocked() {
        mTotalUsedMem = 0;
        mTotalCleanMem = 0;
        for (ProcessModel model : mData) {
            mTotalUsedMem += model.getMemory();
        }
    }

    @Override
    protected void removeDataLocked(Object key) {
        if (mData == null) {
            return;
        }

        if (key instanceof String) {
            String pkgName = (String)key;
            ProcessModel removeModel = null;
            for (ProcessModel pm : mData) {
                if (pm.getPkgName().equals(pkgName)) {
                    removeModel = pm;
                    break;
                }
            }

            if (removeModel != null) {
                mTotalCleanMem += removeModel.getMemory();
                mTotalUsedMem -= removeModel.getMemory();
                mTotalAvailMem += removeModel.getMemory();
                mData.remove(removeModel);
            }
        }
    }

    @Override
    protected boolean isDataValidLocked() {
        return ProcessHelper.isScanDataVaild();
    }

}

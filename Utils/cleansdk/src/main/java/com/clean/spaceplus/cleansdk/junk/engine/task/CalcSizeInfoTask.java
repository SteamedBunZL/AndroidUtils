package com.clean.spaceplus.cleansdk.junk.engine.task;

import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/22 16:04
 * @copyright TCL-MIG
 */
public class CalcSizeInfoTask extends ScanTask.BaseStub {

    public static class SizeUpdateInfo {
        public String mPathName;
        public long mFileCompute[];

        public SizeUpdateInfo(String pathName, long fileCompute[]) {
            mPathName = pathName;
            mFileCompute = fileCompute;
        }
    }

    @Override
    public boolean scan(ScanTaskController ctrl) {
        return false;
    }

    @Override
    public String getTaskDesc() {
        return null;
    }
}

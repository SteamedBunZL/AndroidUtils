package com.clean.spaceplus.cleansdk.junk.cleancloud.residual.cloud;

import com.clean.spaceplus.cleansdk.junk.engine.task.RubbishFileScanTask;

/**
 * @author Jerry
 * @Description:
 * @date 2016/7/1 10:32
 * @copyright TCL-MIG
 */
public class ResidualNetQuery extends RubbishFileScanTask {

    public ResidualNetQuery() {
        super();
        cfgRubbishFileTask(this);
    }




    public void cfgRubbishFileTask(RubbishFileScanTask rubbishScanTask) {
        if (null == rubbishScanTask) {
            return;
        }
        int mask = RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_CALC_SIZE | RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_TEMP_FILE |
                RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE | RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_CALC_CHECKED_SIZE |
                RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_CALC_UNCHECKED_SIZE | RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_REMAIN_INFO |
                RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_DCIM_THUMBNAIL_FOLDER;

        mask |= RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_SCAN_DALVIK_CACHE | RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_QUERY_WITHOUT_ALERTINFO |
                RubbishFileScanTask.RES_FILE_SCAN_CFG_MASK_NOT_COUNT_REMAIN_TARGET_MEDIA_FILE_NUM;
        rubbishScanTask.setScanConfigMask(mask);
    }
}

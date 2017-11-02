package com.clean.spaceplus.cleansdk.base.scan;

import space.network.cleancloud.MultiTaskTimeCalculator;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/22 16:06
 * @copyright TCL-MIG
 */
public interface ScanCommonStatus {
     boolean getIsForegroundScan();
     MultiTaskTimeCalculator getNetQueryTimeController();
     boolean getIsFirstCleanedJunkStandard();
     boolean getIsFirstCleanedJunkAdvanced();
}

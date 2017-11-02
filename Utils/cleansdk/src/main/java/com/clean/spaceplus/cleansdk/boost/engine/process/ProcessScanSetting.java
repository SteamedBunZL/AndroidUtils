package com.clean.spaceplus.cleansdk.boost.engine.process;

import com.clean.spaceplus.cleansdk.boost.engine.scan.BaseScanSetting;

import java.util.ArrayList;

/**
 * @author zengtao.kuang
 * @Description: 进程扫描配置
 * @date 2016/4/5 20:57
 * @copyright TCL-MIG
 */
public class ProcessScanSetting extends BaseScanSetting {
    public static final int SCANTYPE_QUICK = 0;		// quick scan for fast response UI
    public static final int SCANTYPE_NORMAL = 1;	// normal full scan

    /*
     * The scan type, SCANTYPE_QUICK or SCANTYPE_NORMAL
     */
    public int mScanType = SCANTYPE_NORMAL;

    /*
     * The process info count for SCANTYPE_QUICK only
     */
    public int mQuickCount = 0;

    /*
     * Check last running app status
     */
    public boolean mCheckLastApp = false;

    /*
     * Check abnormal memory process
     */
    public boolean mCheckAbnormalMemory = true;

    /*
     * Check cloud control process
     */
    public boolean mCheckCloudControl = true;

    /*
     * Get process memory
     */
    public boolean mGetMemory = true;

    /*
     * Need check uid dependency
     */
    public boolean isCheckUidDependency = true;

    /*
     * The extension scan for future
     */
    public ArrayList<String> mScanExtensionList = new ArrayList<String>();
}


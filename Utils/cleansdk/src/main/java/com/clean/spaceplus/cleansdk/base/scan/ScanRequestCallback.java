package com.clean.spaceplus.cleansdk.base.scan;

import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkResult;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/27 20:35
 * @copyright TCL-MIG
 */
public class ScanRequestCallback implements JunkRequest.RequestCallback {

    protected boolean mUseCache = true;

    @Override
    public void onScanBegin(JunkRequest request) {

    }

    @Override
    public void onScanEnd(JunkRequest request, JunkResult result) {

    }

    @Override
    public void onScanningItem(String strItemName) {

    }

    @Override
    public void onFoundItemSize(long nSize, boolean bChecked) {
        mUseCache = false;
    }



}
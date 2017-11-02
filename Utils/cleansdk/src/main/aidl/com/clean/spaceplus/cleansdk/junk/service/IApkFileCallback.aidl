
package com.clean.spaceplus.cleansdk.junk.service;

import java.lang.Object;

interface IApkFileCallback {

    void onStartScan(int nTotalScanItem);

    boolean onScanItem(String desc, int nProgressIndex);

    void onApkFileScanFinish(long scanSize);
}

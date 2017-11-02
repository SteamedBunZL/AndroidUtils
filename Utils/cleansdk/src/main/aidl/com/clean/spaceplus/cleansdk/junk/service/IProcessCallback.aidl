package com.clean.spaceplus.cleansdk.junk.service;

interface IProcessCallback {

    void onStartScan(int nTotalScanItem);

    boolean onScanItem(String desc, int nProgressIndex);

    void onProcessScanFinish(long scanSize);
}

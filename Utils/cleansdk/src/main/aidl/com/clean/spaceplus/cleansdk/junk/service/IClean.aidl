package com.clean.spaceplus.cleansdk.junk.service;

import com.clean.spaceplus.cleansdk.junk.service.ICacheCallback;
import com.clean.spaceplus.cleansdk.junk.service.IAdDirCallback;
import com.clean.spaceplus.cleansdk.junk.service.ISystemCacheCallback;
import com.clean.spaceplus.cleansdk.junk.service.IApkFileCallback;
import com.clean.spaceplus.cleansdk.junk.service.IResidualCallback;
import com.clean.spaceplus.cleansdk.junk.service.IScanCallback;
import com.clean.spaceplus.cleansdk.junk.service.ICleanCallback;
import com.clean.spaceplus.cleansdk.junk.service.IProcessCallback;

interface IClean {

    void startScan();

    void setScanCacheCallback(in ICacheCallback observer);

    void setScanAdDirCallback(in IAdDirCallback observer);

    void setScanSystemCacheCallback(in ISystemCacheCallback observer);

    void setScanApkFileCallback(in IApkFileCallback observer);

    void setResidualCallback(in IResidualCallback observer);

    void setScanCallback(in IScanCallback observer);

    void cleanScanCallback();

    void startClean();

    void setCleanCallback(in ICleanCallback observer);

    void setScanProcessCallback(in IProcessCallback observer);
}

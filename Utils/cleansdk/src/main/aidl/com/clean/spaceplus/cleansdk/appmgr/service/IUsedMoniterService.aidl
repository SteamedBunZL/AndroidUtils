// IUsedMoniterAidl.aidl
package com.clean.spaceplus.cleansdk.appmgr.service;
import com.clean.spaceplus.cleansdk.appmgr.service.AppUsedInfoRecord;
import com.clean.spaceplus.cleansdk.appmgr.service.AppUsedFreqInfo;
// Declare any non-default types here with import statements

interface IUsedMoniterService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    int getEldestRecordDaysToNow();
    void getReFreqList(out List<AppUsedInfoRecord> records);
    void getLastAppOpenTime(out List<AppUsedFreqInfo> infos);
}

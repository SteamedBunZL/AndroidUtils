package com.clean.spaceplus.cleansdk.junk.service;


import java.lang.Object;

/**
 * 
 * Callback function interface for system cache scan.
 * Caller should implement the interface.  SDK will invoke the callback functions to return the scan progress and result.
 * It's much like [ICacheCallback](@ref ICacheCallback)  interface, For samples, see the [Scan junk files](@ref sec32) section.
 */
interface ISystemCacheCallback {

    /**
     * Callback when scan is started
     * 
     * @param nTotalScanItem
     *            Total number of scan items in the current scan process which can be used to calculate the progress.
     */
    void onStartScan(int nTotalScanItem);

    /**
     * Callback for scan progress
     * 
     * @param desc
     *            User-readable string which represents the current scanning App package name.
     * @param nProgressIndex
     *            Current progress. This value can be used to calculate the current progress by nProgressIndex/nTotalScanItem(@ref onStartScan)
     * @return Flag to stop or continue current scan process:
     *      - true Stop scan
     *      - false Continue scan
     */
    boolean onScanItem(String desc, int nProgressIndex);

    /**
     * Callback when a SD cache item is found
     * 
     * @param pkgName
     *            App package name which generated the junk item.
     * @param descx
     *            Simple junk description of the item.
     * @param size
     *            Size of the system cache files.
     * @remarks
     *			  Developers can use method IPackageManager::deleteApplicationCacheFiles to delete system cache files of an application
     *
     **/
    void onFindCacheItem(String pkgName, String descx, long size);
    

    /**
     * Callback when scan is finished
     */
    void onCacheScanFinish(long scanSize);
}
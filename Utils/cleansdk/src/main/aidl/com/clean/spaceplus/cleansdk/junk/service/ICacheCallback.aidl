package com.clean.spaceplus.cleansdk.junk.service;

import java.lang.Object;

/**
 * 
 * Callback function interface for SD cache scan.
 * Caller should implement the interface.  SDK will invoke the callback functions to return the scan progress and result.
 * For samples, see the [Scan junk files](@ref sec32) section.
 */
interface ICacheCallback {

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
	 * @param cacheType
	 *            Localized cache type. Ex. "Image Cache","Local Logs","Icon Cache." The string will be localized according to the input language and country parameters in @ref IKSCleaner.init
	 * @param dirPath
	 *            Absolute path of the cache file.
	 * @param pkgName
	 *            App package name which generated the cache item.
	 * @param bAdviseDel
	 *            Flag to indicate if we suggest user to delete this item. If CMCleanConst.MASK_SCAN_COMMON is used for scan type, this flag will always be true.
	 *            - true Recommend user to delete the item and will not have side-effect.
	 *            - false Recommend user to keep the item. Folder might contain essential files that could impact user.
	 * @param alertInfo Localized string that contains advice for user. The string will be localized according to the input language and country parameters in @ref IKSCleaner.init
	 *            - The advice is to help users decide what action they should take.
	 *            - null if bAdviseDel is true.
	 * @param descx
	 *            Localized description of the junk item. Ex. "Game Downloaded cache from WeChat.Cleaning is completely safe."
	 *            The string will be localized according to the input language and country parameters in @ref IKSCleaner.init
	 **/
	void onFindCacheItem(String cacheType, String dirPath, String pkgName, boolean bAdviseDel, String alertInfo, String descx);
	

	/**
	 * Callback when scan is finished
	 */
	void onCacheScanFinish(long scanSize);
}
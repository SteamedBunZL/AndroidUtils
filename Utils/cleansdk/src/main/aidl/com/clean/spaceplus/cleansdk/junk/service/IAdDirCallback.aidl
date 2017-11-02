package com.clean.spaceplus.cleansdk.junk.service;

import java.lang.Object;

/**
 * 
 * Callback function interface for advertisement junks.
 * Caller should implement the interface. SDK will invoke the callback functions to return the scan progress and result.
 * It's much like [ICacheCallback](@ref ICacheCallback)  interface, For samples, see the [Scan junk files](@ref sec32) section.
 */
interface IAdDirCallback {

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
	 *            User-readable string which represents the current scanning app package name.
	 * @param nProgressIndex
	 *            Current progress. This value can be used to calculate the current progress by nProgressIndex/nTotalScanItem(@ref onStartScan)
     * @return Flag to stop or continue current scan process:
     *      - true Stop scan
     *      - false Continue scan
	 */
	boolean onScanItem(String desc, int nProgressIndex);

	/**
	 * Callback when an advertisement junk is found
	 * 
	 * @param name
	 *            Localized string that contains the advertisement name. The string will be localized according to the input language and country parameters in @ref IKSCleaner.init
	 * @param dirPath
	 *            Absolute path of the advertisement file.
	 **/
	void onFindAdDir(String name, String dirPath);

	/**
	 * Callback when scan is finished
	 */
	void onAdDirScanFinish(long scanSize);
}
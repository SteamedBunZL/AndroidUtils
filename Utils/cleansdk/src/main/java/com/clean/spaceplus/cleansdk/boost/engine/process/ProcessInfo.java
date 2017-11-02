package com.clean.spaceplus.cleansdk.boost.engine.process;

import java.util.ArrayList;

/**
 * @author zengtao.kuang
 * @Description: 进过filter转化而成
 * @date 2016/4/6 10:26
 * @copyright TCL-MIG
 */
public class ProcessInfo {
	/*
	 * Process name
	 */
	public String mProcessName = null;
	
	/*
	 * Process package name list
	 * All packages that have been loaded into the process.
	 */
	public ArrayList<String> mPkgList = new ArrayList<String>();
	
	/*
	 * Process pid
	 */
	public int mPid = 0;
	
	/*
	 * Process mOOM adj
	 */
	public int mOOM = 20;
	
	/*
	 * Process UID
	 */
	public int mUID = 0;

	public int mImportance;
	
	/*
	 * Advance information: support "account", "abnormal memory", "service info",
	 *  "white list service", "importance flag" currently
	 */
	public ArrayList<ProcessAdvInfo> mAdvanceInfo = new ArrayList<ProcessAdvInfo>();
	
	/*
	 * Clean suggestion
	 *     CLEAN: kill it won't hurt anything
	 *     ADVICE_KEEP: kill it will affect some application behavior
	 *     DONT_CLEAN: kill it will let device abnormal
	 */
	public static final int PROC_SUGGEST_CLEAN = 0;
	public static final int PROC_SUGGEST_ADVICE_KEEP = 1;
	public static final int PROC_SUGGEST_DONT_CLEAN = 2;
	
	public int mCleanSuggest = PROC_SUGGEST_CLEAN;
	
	/*
	 * Clean strategy
	 *     NORMAL: kill process without root, force stop with root
	 *     KILL: kill process even with root
	 *     FORECESTOP: force stop even without root
	 */
	public static final int PROC_STRATEGY_NORMAL = 0;
	public static final int PROC_STRATEGY_KILL = 1;
	public static final int PROC_STRATEGY_FORCESTOP = 2;
	
	public int mCleanStrategy = PROC_STRATEGY_NORMAL;
}

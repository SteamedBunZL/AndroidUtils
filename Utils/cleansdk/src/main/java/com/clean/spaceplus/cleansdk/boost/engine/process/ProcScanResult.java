package com.clean.spaceplus.cleansdk.boost.engine.process;

/**
 * @author zengtao.kuang
 * @Description: 进程扫描完后所需的一些常量
 * @date 2016/4/6 10:26
 * @copyright TCL-MIG
 */
public class ProcScanResult {

	public static final int RESULT_UNKNOWN = 0;
	public static final int RESULT_CHECKED = 1;
	public static final int RESULT_UNCHECKED = 2;
	public static final int RESULT_WHITELIST = 3;
	public static final int RESULT_NODISPLAy = 4;
	
	public int mResult = RESULT_UNKNOWN;
	
	public static final int STRATEGY_NORMAL = 0;	// kill when no root, force stop when root
	public static final int STRATEGY_KILL = 1;		// always kill
	public static final int STRATEGY_FORCESTOP = 2;	// always force stop
	public static final int STRATEGY_DISABLE = 3;	// always disable
	
	public int mKillStrategy = STRATEGY_NORMAL;
	
	public static final int PRIORITY_HIGH = 0;
	public static final int PRIORITY_NORMAL = 1;
	public static final int PRIORITY_LOW = 2;
	
	public int mPriority = PRIORITY_NORMAL;
}

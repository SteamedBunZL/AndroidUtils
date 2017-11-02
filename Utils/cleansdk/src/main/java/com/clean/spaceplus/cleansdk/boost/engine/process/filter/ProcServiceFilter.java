package com.clean.spaceplus.cleansdk.boost.engine.process.filter;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.os.Build;

import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessAdvInfo;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessInfo;
import com.clean.spaceplus.cleansdk.boost.util.IniManager;
import com.clean.spaceplus.cleansdk.boost.util.ProcessOOMHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: Service Filter
 * Protect ini value = 4 and there is no service running on it
 * e.g. service count = 0 and mOOM >= CACHED_APP_MIN_ADJ
 * @date 2016/4/18 20:10
 * @copyright TCL-MIG
 */
public class ProcServiceFilter extends ProcBaseFilter {

	private static final int MAX_SCAN_SERVICE = 256;
	
	private List<RunningServiceInfo> mServiceList = null;

	public ProcServiceFilter(Context ctx) {
		super(ctx);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
			mServiceList = am.getRunningServices(MAX_SCAN_SERVICE);
		}

	}

	@Override
	public boolean doFilter(ProcessInfo processInfo) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return false;
		}
		if (processInfo.mPkgList == null || mServiceList == null || mServiceList.size() == 0) {
			return false;
		}
		
		ArrayList<String> checkList = new ArrayList<String>();
		for (String pkgName : processInfo.mPkgList) {
			if (IniManager.getInstance().getFlexibleValue(pkgName) == IniManager.PROCESS_FLEXIBLE_WHITE_LIST) {
				checkList.add(pkgName);
			}
		}
		if (checkList.size() == 0) {
			return false;
		}
		
		int serviceCount = 0;
		for (RunningServiceInfo serviceInfo : mServiceList) {
			for (String pkgName : checkList) {
				if (pkgName.equalsIgnoreCase(serviceInfo.service.getPackageName())) {
					serviceCount++;
					break;
				}
			}
		}
		
		if (serviceCount == 0 && ProcessOOMHelper.getProcessOOM(processInfo.mPid) >= ProcessOOMHelper.CACHED_APP_MIN_ADJ) {
			processInfo.mCleanSuggest = ProcessInfo.PROC_SUGGEST_CLEAN;
			processInfo.mCleanStrategy = ProcessInfo.PROC_STRATEGY_KILL;
			ProcessAdvInfo processAdvInfo = new ProcessAdvInfo();
			processAdvInfo.mDescription = ProcessAdvInfo.UNUESD_SERVICE;
			processAdvInfo.mStatus = 1;
			processInfo.mAdvanceInfo.add(processAdvInfo);
			return false;
		}

		return false;
	}

}

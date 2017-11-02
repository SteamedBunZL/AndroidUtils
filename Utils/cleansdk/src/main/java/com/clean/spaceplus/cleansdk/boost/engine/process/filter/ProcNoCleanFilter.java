package com.clean.spaceplus.cleansdk.boost.engine.process.filter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessAdvInfo;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessInfo;
import com.clean.spaceplus.cleansdk.boost.util.ProcessOOMHelper;
import com.clean.spaceplus.cleansdk.boost.util.IniManager;
import com.clean.spaceplus.cleansdk.util.PackageUtils;

/**
 * @author zengtao.kuang
 * @Description: No Clean Filter
 * Protect ini value = 2, Launcher, Live-wallpaper,
 * Input Method, Some System App runtime mOOM
 * @date 2016/4/18 20:10
 * @copyright TCL-MIG
 */
public class ProcNoCleanFilter extends ProcBaseFilter {
	private static final int NOT_WHITE_PKG = 0;
	private static final int WL_INI_MANAGER = 1;
	private static final int WL_SYS_KEYWORD_RUNNING = 2;
	private static final int WL_SYS_PERSIST = 3;
	private static final int WL_LAUNCHER = 4;
	private static final int WL_LIVEWALLPAPER = 5;
	private static final int WL_INPUTMETHOD = 6;
	private static final int WL_PARTNER = 7;
	
	private final String[] mSysKeywords = { ":service", ":remote", ":push", ":FriendService",
			":BackgroundFriendService", ":LocationFriendService", ":provider"
	};

	//FIXME 这个由产品提供
	private final String[] mPartnerPkgs = {};
	
	private LauncherProcFilter mLauncherFilter = null;
	private String mCurrentLiveWallpaper = null;
	private InputProcFilter mInputerFilter = null;
	
	public ProcNoCleanFilter(Context ctx) {
		super(ctx);
		
		mLauncherFilter = new LauncherProcFilter(ctx);
		mInputerFilter = new InputProcFilter(ctx);
		mCurrentLiveWallpaper = PackageUtils.getPackageNameOfCurrentLiveWallpaper(ctx);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	@Override
	public boolean doFilter(ProcessInfo processInfo) {

		int status = checkIfWhiteListPackage(processInfo);
		if (status != NOT_WHITE_PKG) {
			processInfo.mCleanSuggest = ProcessInfo.PROC_SUGGEST_DONT_CLEAN;
			ProcessAdvInfo processAdvInfo = new ProcessAdvInfo();
			processAdvInfo.mDescription = ProcessAdvInfo.DEFAULT_NOT_CLEAN;
			processAdvInfo.mStatus = status;
			processInfo.mAdvanceInfo.add(processAdvInfo);
			return true;
		}

		return false;
	}

	private int checkIfWhiteListPackage(ProcessInfo processInfo) {
		// check system app
		if (processInfo.mPkgList != null && isSystemApp(mContext, processInfo.mPkgList.get(0))) {
			int oom = ProcessOOMHelper.getProcessOOM(processInfo.mPid);
			if (oom < 0) {
				return WL_SYS_PERSIST;
			}
			if (oom < ProcessOOMHelper.CACHED_APP_MIN_ADJ &&
				!TextUtils.isEmpty(processInfo.mProcessName)) {
				for (String keyword: mSysKeywords) {
					if (processInfo.mProcessName.contains(keyword)) {
						return WL_SYS_KEYWORD_RUNNING;
					}
				}
			}
		}
		
		if (processInfo.mPkgList != null) {
			for (String pkgName : processInfo.mPkgList) {
				// check from ini manager
				if (IniManager.getInstance().getIniMark(pkgName) == IniManager.PROCESS_FILTERED) {
					return WL_INI_MANAGER;
				}
				
				// check launcher
				if (mLauncherFilter.isLauncherPkg(pkgName)) {
					return WL_LAUNCHER;
				}
				
				// check live wallpaper
				if (pkgName.equals(mCurrentLiveWallpaper)) {
					return WL_LIVEWALLPAPER;
				}
				
				// check input method
				if (mInputerFilter.isInputerPkg(pkgName)) {
					return WL_INPUTMETHOD;
				}
				
				// check partner
				for (String partner: mPartnerPkgs) {
					if (pkgName.startsWith(partner)) {
						return WL_PARTNER;
					}
				}
			}
		}
		
		return NOT_WHITE_PKG;
	}
	
	private static boolean isSystemApp(Context ctx, String pkgName) {
		ApplicationInfo ai = getApplicationInfo(ctx, pkgName);
		if (ai != null) {
			return (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0; 
		} else {
			return false;
		}
	}
	
	private static ApplicationInfo getApplicationInfo(Context ctx, String pkgName) {

		try {
			PackageInfo pkgInfo = ctx.getPackageManager().getPackageInfo(pkgName, 0);
			if (pkgInfo != null) {
				ApplicationInfo appInfo = pkgInfo.applicationInfo;
				return appInfo;
			}
		} catch (Exception e) {
		}
		
		return null;
	}

}

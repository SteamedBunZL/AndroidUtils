package com.clean.spaceplus.cleansdk.boost.engine.process.filter;

import android.content.Context;

import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessInfo;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessAdvInfo;

/**
 * @author zengtao.kuang
 * @Description: Account Filter
 * Check the process account status
 * @date 2016/4/18 20:10
 * @copyright TCL-MIG
 */
public class ProcAccountFilter extends ProcBaseFilter {

	private AccountScanner mAccountScanner = null;
	
	public ProcAccountFilter(Context ctx) {
		super(ctx);
		mAccountScanner = new AccountScanner(ctx);
	}

	@Override
	public boolean doFilter(ProcessInfo processInfo) {
		if (processInfo.mPkgList == null||processInfo.mPkgList.size()==0 ) {
			return false;
		}

		for (String pkgName : processInfo.mPkgList) {
			if (mAccountScanner.getPackageAccountStatus(pkgName) == AccountScanner.ACCOUNT_LOGOUT) {
				processInfo.mCleanSuggest = ProcessInfo.PROC_SUGGEST_CLEAN;
				processInfo.mCleanStrategy = ProcessInfo.PROC_STRATEGY_KILL;
				ProcessAdvInfo processAdvInfo = new ProcessAdvInfo();
				processAdvInfo.mDescription = ProcessAdvInfo.ACCOUT;
				processAdvInfo.mStatus = AccountScanner.ACCOUNT_LOGOUT;
				processInfo.mAdvanceInfo.add(processAdvInfo);

				break;
			}
		}

		return false;
	}

}

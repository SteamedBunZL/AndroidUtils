package com.clean.spaceplus.cleansdk.boost.engine.process.filter;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessAdvInfo;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessInfo;
import com.clean.spaceplus.cleansdk.boost.util.IniManager;
import com.clean.spaceplus.cleansdk.boost.util.ProcessOOMHelper;

/**
 * @author zengtao.kuang
 * @Description: Last Running App Filter
 * Protect ini value = 3 and checking "mOOM and mImportance"
 * Not default doFilter and will trigger by ProcessSetting
 * @date 2016/4/18 20:10
 * @copyright TCL-MIG
 */
public class ProcLastAppFilter extends ProcBaseFilter {

	public ProcLastAppFilter(Context ctx) {
		super(ctx);
	}

	@Override
	public boolean doFilter(ProcessInfo processInfo) {
		if ( processInfo.mPkgList == null||processInfo.mPkgList.size()==0 ) {
			return false;
		}
		
		boolean needKeep = false;
		for (String pkgName : processInfo.mPkgList) {
			if (IniManager.getInstance().getIniMark(pkgName) == IniManager.PROCESS_UNCHECKED_WHEN_SCREENOFF) {
				needKeep = true;
				break;
			}
		}
		
		if (processInfo.mImportance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
			needKeep = true;
		}
		
		if (isLastRunningProcess(ProcessOOMHelper.getProcessOOM(processInfo.mPid))) {
			needKeep = true;
		}
		
		if (needKeep) {
			if (processInfo.mCleanSuggest == ProcessInfo.PROC_SUGGEST_CLEAN) {
				processInfo.mCleanSuggest = ProcessInfo.PROC_SUGGEST_ADVICE_KEEP;
			}
			ProcessAdvInfo processAdvInfo = new ProcessAdvInfo();
			processAdvInfo.mDescription = ProcessAdvInfo.LAST_APP;
			processAdvInfo.mStatus = 1;
			processInfo.mAdvanceInfo.add(processAdvInfo);
			return false;
		}
		
		return false;
	}
	
	// This is a process only hosting components that are perceptible to the
    // user, and we really want to avoid killing them, but they are not
    // immediately visible. An example is background music playback.
    private final static int PERCEPTIBLE_APP_ADJ = 2;
    // This is the process of the previous application that the user was in.
    // This process is kept above other things, because it is very common to
    // switch back to the previous app.  This is important both for recent
    // task switch (toggling between the two top recent apps) as well as normal
    // UI flow such as clicking on a URI in the e-mail app to view in the browser,
    // and then pressing back to return to e-mail.
    private final static int PREVIOUS_APP_ADJ = 7;

	private boolean isLastRunningProcess(int oom) {
		return (oom == PREVIOUS_APP_ADJ ||
    			oom == PERCEPTIBLE_APP_ADJ);
	}

}

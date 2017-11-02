package com.clean.spaceplus.cleansdk.boost.engine.process.filter;

import android.content.Context;

import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessAdvInfo;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessInfo;
import com.clean.spaceplus.cleansdk.boost.util.IniManager;

import java.util.Locale;

/**
 * @author zengtao.kuang
 * @Description: Advice Keep Filter
 * Protect ini value = 1 and some keyword package = "contact", "clock", "weather"
 * @date 2016/4/18 20:10
 * @copyright TCL-MIG
 */
public class ProcAdviceKeepFilter extends ProcBaseFilter {
	
	private String[] mKeywordPkgs = new String[] {
			"contact", "clock", "weather"
	};

	public ProcAdviceKeepFilter(Context ctx) {
		super(ctx);
	}

	@Override
	public boolean doFilter(ProcessInfo processInfo) {
		if (processInfo.mPkgList == null||processInfo.mPkgList.size()==0 ) {
			return false;
		}
		
		for (String pkgName : processInfo.mPkgList) {

			for (int i = 0; i < mKeywordPkgs.length; i++) {
                String keyword = mKeywordPkgs[i];
				if (pkgName.toLowerCase(Locale.US).contains(keyword)) {
					if (processInfo.mCleanSuggest == ProcessInfo.PROC_SUGGEST_CLEAN) {
						processInfo.mCleanSuggest = ProcessInfo.PROC_SUGGEST_ADVICE_KEEP;
					}
					ProcessAdvInfo processAdvInfo = new ProcessAdvInfo();
					processAdvInfo.mDescription = ProcessAdvInfo.ADVICE_KEEP;
					processAdvInfo.mStatus = i+2;
					processInfo.mAdvanceInfo.add(processAdvInfo);
					return false;
				}
			}

			int value = IniManager.getInstance().getIniMark(pkgName);
            if ( value == IniManager.PROCESS_UNCHECKED
					||value == IniManager.PROCESS_NECESSARY_APP ) {
                if (processInfo.mCleanSuggest == ProcessInfo.PROC_SUGGEST_CLEAN) {
					processInfo.mCleanSuggest = ProcessInfo.PROC_SUGGEST_ADVICE_KEEP;
                }
                ProcessAdvInfo processAdvInfo = new ProcessAdvInfo();
				processAdvInfo.mDescription = ProcessAdvInfo.ADVICE_KEEP;
				processAdvInfo.mStatus = 1;
				processInfo.mAdvanceInfo.add(processAdvInfo);
                return false;
            }

		}

		return false;
	}

}

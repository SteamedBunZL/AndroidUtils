package com.clean.spaceplus.cleansdk.boost.engine.process;

import com.clean.spaceplus.cleansdk.R;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.boost.engine.process.filter.AccountScanner;

/**
 * @author zengtao.kuang
 * @Description: scan advance information
 * @date 2016/4/6 10:26
 * @copyright TCL-MIG
 */
public class ProcessAdvInfo {
	public String mDescription;
	public int mStatus = 0;
	
	public static final String DEFAULT_NOT_CLEAN = "DefNotClean";
	public static final String ADVICE_KEEP = "AdviceKeep";
	public static final String ACCOUT = "Accout";
	public static final String CLOUD_CONTROL = "CloudCtrl";
	public static final String ABNORMAL_MEMORY = "AbnormalMem";
	public static final String SOCIAL_PROCESS = "SocialProc";
	public static final String LAST_APP = "LastApp";
	public static final String UNUESD_SERVICE = "UnuesdSvc";
    public static final String DEPEND_UID = "DependUid";

    // For ADVICE_KEEP
    public static final int ADVICE_KEEP_INI = 1;
    public static final int ADVICE_KEEP_CONTACT = 2;
    public static final int ADVICE_KEEP_CLOCK = 3;
    public static final int ADVICE_KEEP_WEATHER = 4;


	public static int getBoostKeepReason(ProcessModel model) {
		if (null == model) {
			return R.string.junk_suggest_clean;
		}

		int nRes;
		int nKeepReason = model.getKeepReason();
		if (ProcessAdvInfo.ADVICE_KEEP_INI == nKeepReason) {
			///< 库里加不勾选
			nRes = R.string.boost_keep_advice_lib;
		} else if (ProcessAdvInfo.ADVICE_KEEP_CONTACT == nKeepReason) {
			///< 通讯录类软件
			nRes = R.string.boost_keep_advice_contact;
		} else if (ProcessAdvInfo.ADVICE_KEEP_WEATHER == nKeepReason) {
			///< 天气类软件
			nRes = R.string.boost_keep_advice_weather;
		} else if (ProcessAdvInfo.ADVICE_KEEP_CLOCK == nKeepReason) {
			///< 闹钟类软件
			nRes = R.string.boost_keep_advice_clock;
		} else if (model.getDependUid()) {
			///< 进程名/uid加白的
			nRes = R.string.boost_keep_advice_process_white;
		} else {
			int nAccountStatus = model.getAccoutStatus();
			if (AccountScanner.ACCOUNT_LOGIN == nAccountStatus) {
				///< 登录中
				nRes = R.string.boost_keep_advice_login;
			} else if (AccountScanner.ACCOUNT_LOGOUT == nAccountStatus) {
				///< 已登出
				nRes = R.string.boost_keep_advice;
			} else {
				///< 其它...
				nRes = R.string.boost_keep_advice;
			}
		}
		return nRes;
	}
}

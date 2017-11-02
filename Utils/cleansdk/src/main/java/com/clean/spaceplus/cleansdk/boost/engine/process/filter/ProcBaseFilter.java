package com.clean.spaceplus.cleansdk.boost.engine.process.filter;

import android.content.Context;

import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessInfo;

/**
 * @author zengtao.kuang
 * @Description: 抽象的进程过滤器
 * @date 2016/4/18 20:10
 * @copyright TCL-MIG
 */
public abstract class ProcBaseFilter {
	Context mContext = null;

	public ProcBaseFilter(Context ctx) {
		mContext = ctx;
	}
	
	public abstract boolean doFilter(ProcessInfo processInfo);

}

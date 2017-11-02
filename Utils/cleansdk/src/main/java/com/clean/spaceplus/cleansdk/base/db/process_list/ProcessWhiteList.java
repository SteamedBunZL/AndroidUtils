package com.clean.spaceplus.cleansdk.base.db.process_list;

/**
 * @author zengtao.kuang
 * @Description: 进程白名单实体
 * @date 2016/4/19 17:11
 * @copyright TCL-MIG
 */
public  class ProcessWhiteList {

	public int mMark;
	public String mPkgname;
	public String mTitle;

	public ProcessWhiteList(){

	}

	public ProcessWhiteList(int mark, String pkgname, String title) {
		super();
		this.mMark = mark;
		this.mPkgname = pkgname;
		this.mTitle = title;
	}


	public String getPkgname() {
		return mPkgname;
	}

	public String getTitle() {
		return mTitle;
	}

	public int getMark() {
		return mMark;
	}

	@Override
	public String toString() {
		return "ProcessWhiteList [mark=" + mMark + ", pkgname=" + mPkgname + ", title=" + mTitle
				+ "]";
	}
	
}


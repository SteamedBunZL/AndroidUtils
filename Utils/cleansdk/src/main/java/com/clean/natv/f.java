package com.clean.natv;


public class f extends e {

	public f(int a) {
		super(a);
	}

	/**
	 * getFileNameList()
	 * 注意
	 *  1. 本函数返回的对象也要release释放。
	 *  2. 调用过当前对象的set或shrink操作后，不可再用本函数。
	 * @return 所有子项中的文件名列表
	 */
	public native e c();
	
	/**
	 * getFolderNameList()
	 * 注意
	 *  1. 本函数返回的对象也要release释放。
	 *  2. 调用过当前对象的set或shrink操作后，不可再用本函数。
	 * @return 所有子项中的文件夹名列表
	 */
	public native e d();
}

package com.clean.natv;


public class e {
	protected final int a;	///< native对象地址
	
	public e(int a) {
		this.a = a;
	}
	
	/**
	 * int size();
	 * 
	 * @return 元素总个数
	 */
	public native int a();
	
	/**
	 * String get(int idx);
	 * 
	 * 取指定元素
	 * @param idx 元素索引
	 * @return 索引对应元素对象
	 */
	public native String a(int idx);
	
	/**
	 * void set(int idx, String s);
	 * 
	 * 设定指定元素
	 * @param idx 元素索引
	 * @param s 索引处的新元素对象，可以为null。
	 */
	public native void a(int idx, String s);
	
	/**
	 * void shrink(int size);
	 * 
	 * 收缩到指定大小。只保留最前面size个元素，后面的元素全部放弃。
	 */
	public native void b(int size);
	
	/**
	 * void release();
	 * 
	 * 释放资源。本操作后，本对象不再可用。
	 */
	public native void b();
}
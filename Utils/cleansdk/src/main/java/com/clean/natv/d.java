package com.clean.natv;

public interface d {
	/**
	 * 文件名回调
	 * @param a 文件名全路径
	 * @param s 该文件大小
	 * @param at time of last access (Some file system not support this.) 此数据作为有符号数使用，还能用20多年不溢出。
	 * @param mt time of last modification 此数据作为有符号数使用，还能用20多年不溢出。
	 * @param ct time of last status change 此数据作为有符号数使用，还能用20多年不溢出。
	 */
	public void a(String a, long s, int at, int mt, int ct);
	
	public void b(String a, String b, long s);
	
	public void c(String strRootDir);
	public void e(String strRootDir, String strSubFile);
	public void f(String strRootDir, String strSubFolder);
	public void g(String strRootDir);

    public void h(String a, boolean b, boolean c, int d);

    public boolean z(String filePath, long fileModifyTime);
}

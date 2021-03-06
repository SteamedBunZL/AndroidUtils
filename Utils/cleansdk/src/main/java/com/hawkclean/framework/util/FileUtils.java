/*
 * Copyright (c) 1998-2012 TENCENT Inc. All Rights Reserved.
 * 
 * FileName: FileUtils.java
 * 
 * Description:文件操作工具类文件。
 * 
 * History:
 * 1.0	devilxie	2012-09-05	Create
 */

package com.hawkclean.framework.util;

/**
 * 文件操作工具类，提供目录创建、文件创建、大空文件创建、文件复制等功能
 * 
 * @author devilxie
 * @version 1.0
 * 
 */
public class FileUtils
{

//	            /**
//     * 创建空文件
//     *
//     * @param path
//     *            待创建的文件路径
//     * @param size
//     *            空文件大小
//     * @return 创建是否成功
//     * @throws IOException
//     */
//	public static boolean createEmptyFile(String path, long size)
//			throws IOException
//	{
//		File file = new File(path);
//		File parent = file.getParentFile();
//		parent.mkdirs();
//		RandomAccessFile raf = null;
//		raf = new RandomAccessFile(file, "rw");
//		raf.setLength(size);
//		raf.close();
//		return true;
//	}
//
//	public static boolean isExist(String path) {
//		if (TextUtils.isEmpty(path))
//			throw new IllegalArgumentException("path is empty!");
//
//		File file = new File(path);
//		return file.exists();
//	}
//
//	            /**
//     * 删除文件或者目录
//     *
//     * @param path
//     *            指定路径的文件或目录
//     * @return 返回操作结果
//     */
//	public static boolean deleteFile(String path)
//	{
//		File file = new File(path);
//		if (!file.exists())
//			return true;
//
//		if (file.isDirectory())
//		{
//			String[] subPaths = file.list();
//			for (int i = 0; i < subPaths.length; i++)
//			{
//				if (!deleteFile(path))
//				{
//					return false;
//				}
//			}
//		}
//
//		return file.delete();
//	}

//	            /**
//     * 创建目录，包括必要的父目录的创建，如果未创建
//     *
//     * @param path
//     *            待创建的目录路径
//     * @return 返回操作结果
//     */
//	public static boolean mkdir(String path)
//	{
//		File file = new File(path);
//		if (file.exists() && file.isDirectory())
//		{
//			return true;
//		}
//
//		file.mkdirs();
//		return true;
//	}
//
//	            /**
//     * 创建文件，包括必要的父目录的创建，如果未创建
//     *
//     * @param file
//     *            待创建的文件
//     * @return 返回操作结果
//     * @throws IOException
//     *             创建失败，将抛出该异常
//     */
//	public static boolean create(File file) throws IOException
//	{
//		if (file.exists())
//		{
//			return true;
//		}
//
//		File parent = file.getParentFile();
//		parent.mkdirs();
//		return file.createNewFile();
//	}
//
//	            /**
//     * 获取指定文件的xml Document 对象
//     *
//     * @param filePath
//     *            文件路径
//     * @return Document对象
//     */
//	public static Document getXmlDocument(String filePath) throws Exception
//	{
//		return getXmlDocument(new File(filePath));
//	}

//	            /**
//     * 获取指定文件的xml Document 对象
//     *
//     * @param file
//     *            文件句柄
//     * @return Document对象
//     * @throws ParserConfigurationException
//     */
//	public static Document getXmlDocument(File file) throws Exception
//	{
//		DocumentBuilderFactory docBuilderFactory = null;
//		DocumentBuilder db = null;
//		Document document = null;
//		docBuilderFactory = DocumentBuilderFactory.newInstance();
//		db = docBuilderFactory.newDocumentBuilder();
//
//		if (file.exists())
//		{
//			FileInputStream in = new FileInputStream(file);
//			document = db.parse(in);
//			in.close();
//		}
//		db = null;
//		docBuilderFactory = null;
//		return document;
//	}

//    /**
//     * 检查当前sdcard剩余空间大小
//     */
//	public static long getAvailableExternalMemorySize()
//	{
//		String state = Environment.getExternalStorageState();
//		if (Environment.MEDIA_MOUNTED.equals(state))
//		{
//			File path = Environment.getExternalStorageDirectory();
//			StatFs stat = new StatFs(path.getPath());
//            long blockSize = AndroidNewApi.getBlockSizeLong(stat);
//			long availableBlocks = AndroidNewApi.getAvailableBlocks(stat);;
//			long availableSize = availableBlocks * blockSize;
//            return availableSize - 5 * 1024 * 1024;// 预留5M的空间
//		}
//		return -1;
//	}

//	            /**
//     * 复制文件
//     *
//     * @param srcPath
//     *            源文件路径
//     * @param destPath
//     *            目标文件路径
//     * @return 返回操作结果
//     */
//	public static boolean copyFile(String srcPath, String destPath)
//	{
//		return copyFile(new File(srcPath), new File(destPath));
//	}

//	            /**
//     * 复制文件
//     *
//     * @param src
//     *            源文件
//     * @param dest
//     *            目标文件
//     * @return 返回操作结果
//     * @throws FileNotFoundException
//     */
//	public static boolean copyFile(File src, File dest)
//	{
//		if (!src.exists())
//			return false;
//
//		FileInputStream fis = null;
//		FileOutputStream fos = null;
//		try
//		{
//			fis = new FileInputStream(src);
//			fos = new FileOutputStream(dest);
//
//			byte[] buffer = new byte[2048];
//			int bytesread = 0;
//			while ((bytesread = fis.read(buffer)) != -1)
//			{
//				if (bytesread > 0)
//					fos.write(buffer, 0, bytesread);
//			}
//
//			return true;
//
//		}
//		catch (FileNotFoundException e)
//		{
//			return false;
//		}
//		catch (IOException e)
//		{
//			return false;
//		}
//		finally
//		{
//			if (fis != null)
//			{
//				try
//				{
//					fis.close();
//				}
//				catch (Exception e2)
//				{
//				}
//			}
//
//			if (fos != null)
//			{
//				try
//				{
//					fos.close();
//				}
//				catch (Exception e2)
//				{
//				}
//			}
//		}
//	}

//
//	            /**
//     * 移动文件
//     *
//     * @param srcPath
//     *            源文件路径
//     * @param destPath
//     *            目标文件路径
//     * @return 返回操作结果
//     */
//	public static boolean moveFile(String srcPath, String destPath)
//	{
//		File src = new File(srcPath);
//		File dest = new File(destPath);
//
//		boolean ret = copyFile(src, dest);
//		if (ret)
//		{
//			deleteFile(srcPath);
//		}
//
//		return ret;
//	}
//
//	            /**
//     * 移动文件
//     *
//     * @param src
//     *            源文件
//     * @param dest
//     *            目标文件
//     * @return 返回操作结果
//     */
//	public static boolean moveFile(File src, File dest)
//	{
//		boolean ret = copyFile(src, dest);
//		if (ret)
//		{
//			ret = deleteFile(src.getAbsolutePath());
//		}
//
//		return ret;
//	}
//
//	            /**
//     * 获取文件扩展名
//     *
//     * @param path
//     *            文件路径
//     * @return 返回扩展名，如果没有，将返回null
//     */
//	public static String getFileExtension(String path)
//	{
//		if (TextUtils.isEmpty(path))
//		{
//			return null;
//		}
//		int index = path.lastIndexOf(".");
//		if (index == -1 || index == path.length() -1)
//		{
//			return null;
//		}
//
//		return path.substring(index + 1);
//	}
	
//    /**
//     * 读取文件内容
//     *
//     * @param file
//     *            文件句柄对象
//     * @return 返回读取的字节数组
//     * @throws IOException
//     *             读取失败将抛出该异常
//     */
//	public static byte[] readFile(File file) throws IOException
//	{
//		int len = (int)file.length();
//		if (len == 0) {
//			return new byte[0];
//		}
//
//		byte[] data = null;
//		BufferedInputStream bis = null;
//		try
//		{
//			FileInputStream  fis = new FileInputStream(file);
//			bis = new BufferedInputStream(fis);
//			data = new byte[len];
//			bis.read(data);
//		}finally {
//			if (bis != null) {
//				try
//				{
//					bis.close();
//				}
//				catch (IOException e)
//				{
//				}
//			}
//		}
//
//		return data;
//	}
	
//	            /**
//     * 对文件进行MD5签名
//     *
//     * @param file
//     *            文件句柄对象
//     * @return 返回签名数组
//     * @throws IOException
//     *             读取文件错误将抛出该异常
//     */
//	public static byte[] digest(File file) throws IOException
//	{
//		BufferedInputStream bis = null;
//		byte[] digest = null;
//		try
//		{
//			bis = new BufferedInputStream(new FileInputStream(file));
//			digest = MD5.encode(bis);
//		}
//		finally
//		{
//			if (bis != null) {
//				bis.close();
//			}
//		}
//
//		return digest;
//	}

}

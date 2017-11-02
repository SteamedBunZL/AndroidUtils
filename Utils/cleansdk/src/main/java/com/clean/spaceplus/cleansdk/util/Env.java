package com.clean.spaceplus.cleansdk.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Environment;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.setting.update.mgmt.UpdateManager;

import java.io.File;

public class Env {
	public static final int VERSION_CODE = 1;
	public static final String VERSION_NAME = "1.0";
	public static final String DB_NAME_COMMON = "cleanmaster_process_list.db";

	public static final String PROCESS_SECTIONNAME = "process";
	public static final String ENCODING = "utf-8";

	public static final String FILE_PATH = "file_path";
	public static final String FLAG = "flag";
	public static final String ID = "id";
	public static final String _ID = "_id";

	public static final String FLEXIBLE_SECTIONNAME = "flexible";
	public static final String CPU_SECTIONNAME = "cpu";



	public static String getPkgName(Context context) {
		ComponentName cn = new ComponentName(context, context.getClass());
		return cn.getPackageName();
	}
	
	public static String getPkgName() {
		Context context = SpaceApplication.getInstance().getContext();
		ComponentName cn = new ComponentName(context, context.getClass());
		return cn.getPackageName();
	}

	public static boolean isMultiProc = IsMultiProcessor();
	private static boolean IsMultiProcessor(){
		File cpu = new File("/sys/devices/system/cpu/cpu1");

		if (cpu.isDirectory())
			return true;

		return false;
	}

	public static String getExternalStorageDirectoryx() {
		File apkCacheFile = null;
		try {
			apkCacheFile = SpaceApplication.getInstance().getContext().getExternalFilesDir(null);
		} catch(Exception ignore) {
		}

		String apkCacheDir = null;
		if (apkCacheFile != null && apkCacheFile.exists()) {
			apkCacheDir = apkCacheFile.getAbsolutePath();
		}

		try {
			if ( apkCacheDir == null && Environment.getExternalStorageDirectory() != null ){
				apkCacheDir = new File(Environment.getExternalStorageDirectory(), UpdateManager.getInstance().getSdCardExternalPath()).getAbsolutePath();
			}
			new File(apkCacheDir + "/").mkdirs(); // 如果没有路径，创建之
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return apkCacheDir;
	}

	public static int getVersionCode(Context context) {
		ComponentName cn = new ComponentName(context, context.getClass());
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					cn.getPackageName(), 0);
			return info.versionCode;
		} catch (/*NameNotFoundException*/Exception e) {
			return -1;
		}
	}

	public static String getVersionName(Context context) {
		ComponentName cn = new ComponentName(context, context.getClass());
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					cn.getPackageName(), 0);
			return info.versionName;
		} catch (/*NameNotFoundException*/Exception e) {
			return "";
		}
	}

	public static int getRealVersionCode() {
		return VERSION_CODE;
	}


}

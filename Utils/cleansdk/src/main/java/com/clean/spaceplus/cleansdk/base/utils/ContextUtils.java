package com.clean.spaceplus.cleansdk.base.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.hawkclean.framework.log.NLog;

/**
 * @author shunyou.huang
 * @Description: Context实用类
 * @date 2016/5/25 10:30
 * @copyright TCL-MIG
 */

public class ContextUtils {

//    private static final String TAG = ContextUtils.class.getSimpleName();

//	/**
//	 * 是否在主线程
//	 * @return
//     */
//    public static boolean isMainThread() {
//		long id = Thread.currentThread().getId();
//		return id == Looper.getMainLooper().getThread().getId();
//	}
//
//	public static void detectUINetwork(Context context) {
//
//		int appFlags = context.getApplicationInfo().flags;
//        if ((appFlags & ApplicationInfo.FLAG_DEBUGGABLE) == 0) {
//            return;
//        }
//
//        if (Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
//        	  ThreadPolicy.Builder threadPolicyBuilder = new ThreadPolicy.Builder();
//        	  threadPolicyBuilder.detectDiskReads()
//        	  					.detectDiskWrites()
//        	  					.detectAll();
//
//        	  StrictMode.setThreadPolicy(threadPolicyBuilder.build());
//        	  VmPolicy.Builder vmPolicyBuilder = new VmPolicy.Builder();
//        	  vmPolicyBuilder.detectLeakedSqlLiteObjects();
//
//        	  if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
//        	      //vmPolicyBuilder.detectLeakedClosableObjects();
//        	  }
//        	  vmPolicyBuilder.penaltyLog();
//        	  StrictMode.setVmPolicy(vmPolicyBuilder.penaltyDeath().build());
//        }
//	}

	/**
	 * 获取MetaData
	 * @param context
	 * @param name
     * @return
     */
	public static String getMetaData(Context context, String name) {
		PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo;
		Object value = null;
		try {

			applicationInfo = packageManager.getApplicationInfo(
					context.getPackageName(), packageManager.GET_META_DATA);
			if (applicationInfo != null && applicationInfo.metaData != null) {
				value = applicationInfo.metaData.get(name);
			}

		} catch (Exception e) {
			NLog.printStackTrace(e);
			NLog.w("ContextUtils",
					"Could not read the name(%s) in the manifest file.", name);
			return null;
		}

		return value == null ? null : value.toString();
	}

//	/**
//	 * 获取版本名称
//	 * @param context
//	 * @return
//     */
//	public static String getVersionName(Context context) {
//		PackageManager packageManager = context.getPackageManager();
//		try {
//
//			PackageInfo pi = packageManager.getPackageInfo(context.getPackageName(), 0);
//			if (pi != null ) {
//				return pi.versionName;
//			}
//
//		} catch (Exception e) {
//			NLog.printStackTrace(e);
//
//		}
//
//		return null;
//	}

	/**
	 * 获取版本号
	 * @param context
	 * @return
     */
	public static int getVersionCode(Context context) {
		PackageManager packageManager = context.getPackageManager();
		try {

			PackageInfo pi = packageManager.getPackageInfo(context.getPackageName(), 0);
			if (pi != null ) {
				return pi.versionCode;
			}

		} catch (Exception e) {
			NLog.printStackTrace(e);
		}
		
		return 0;
	}

//	/**
//	 * Open the activity to let user allow wifi feature in Settings app.
//	 *
//	 * @param context
//	 *            from which invoke this method
//	 */
//	public static void openWIFISettings(Context context) {
//		Intent intent = new Intent();
//		intent.setAction(Settings.ACTION_WIFI_SETTINGS);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(intent);
//	}

//	/**
//	 * 获取当前进程名
//	 * @param context
//	 * @return
//     */
//	public static String getCurrentProcessName(Context context) {
//		int pid = android.os.Process.myPid();
//		ActivityManager mActivityManager = (ActivityManager) context
//				.getSystemService(Context.ACTIVITY_SERVICE);
//
//		List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager
//				.getRunningAppProcesses();
//
//		for (ActivityManager.RunningAppProcessInfo appProcess : processInfos) {
//			if (appProcess.pid == pid) {
//				return appProcess.processName;
//			}
//		}
//
//		return null;
//	}

//	/**
//	 * 是否在子进程
//	 * @param context
//	 * @return
//     */
//	public static boolean isChildProcess(Context context) {
//		String process = getCurrentProcessName(context);
//		String pkName = context.getPackageName();
//
//		if (!pkName.equals(process))
//			return true;
//
//		return false;
//	}
//
//	/**
//	 * APK安装
//	 * @param context
//	 * @param apkPath
//     * @return
//     */
//	public static boolean install(Context context, String apkPath) {
//		if (TextUtils.isEmpty(apkPath)) {
//			NLog.w("ContextUtils", "download complete intent has no path param");
//			return false;
//		}
//
//		File file = new File(apkPath);
//		if (!file.exists()) {
//			NLog.w("ContextUtils", "file %s not exists", apkPath);
//			return false;
//		}
//
//		if(isSystemApp(context)){
//			return systemInstall(apkPath);
//		}else{
//			File apkFile = new File(apkPath);
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//	        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
//	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	        context.startActivity(intent);
//	        return true;
//		}
//	}
	
//	/**
//	 * 是否为系统应用
//	 *
//	 * @param context
//	 * @return
//	 */
//	public static boolean isSystemApp(Context context) {
//		return ((context.getApplicationInfo()).flags & ApplicationInfo.FLAG_SYSTEM) > 0;
//	}
//
//	/**
//	 * 系统安装
//	 * @param apkPath
//	 * @return
//     */
//	public static boolean systemInstall(String apkPath) {
//		String result = sysInstall(apkPath).trim();
//		int lastIndex = result.lastIndexOf("/n");
//		if (lastIndex == -1) {
//			return false;
//		}
//		result = result.substring(lastIndex + 2);
//		return "success".equalsIgnoreCase(result);
//	}
	
//	/**
//	 * 系统级自动安装
//	 *
//	 * @param apkPath
//	 * @return
//	 */
//	public static String sysInstall(String apkPath) {
//		String[] args = { "pm", "install", "-r", apkPath };
//		String result = "";
//		ProcessBuilder processBuilder = new ProcessBuilder(args);
//		Process process = null;
//		InputStream errIs = null;
//		InputStream inIs = null;
//		try {
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			int read = -1;
//			process = processBuilder.start();
//			errIs = process.getErrorStream();
//			while ((read = errIs.read()) != -1) {
//				baos.write(read);
//			}
//			baos.write("\n".getBytes("utf-8"));
//			inIs = process.getInputStream();
//			while ((read = inIs.read()) != -1) {
//				baos.write(read);
//			}
//			byte[] data = baos.toByteArray();
//			result = new String(data);
//		} catch (Exception e) {
//			NLog.printStackTrace(e);
//		} finally {
//			try {
//				if (errIs != null) {
//					errIs.close();
//				}
//				if (inIs != null) {
//					inIs.close();
//				}
//			} catch (Exception e) {
//
//			}
//			if (process != null) {
//				process.destroy();
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * 静默安装APK， 需要ROOT权限
//	 *
//	 * @param apkPath APK的文件路径
//	 * @return
//	 */
//	public static boolean installSilent(String apkPath) {
//		int result = -1;
//		DataOutputStream dos = null;
//		String cmd = "pm install -r " + apkPath;
//		try {
//			Process p = Runtime.getRuntime().exec("su");
//			dos = new DataOutputStream(p.getOutputStream());
//			dos.writeBytes(cmd + "\n");
//			dos.flush();
//			dos.writeBytes("exit\n");
//			dos.flush();
//			p.waitFor();
//			result = p.exitValue();
//		} catch (Exception e) {
//			NLog.printStackTrace(e);
//		} finally {
//			if (dos != null) {
//				try {
//					dos.close();
//				} catch (Exception e) {
//					NLog.printStackTrace(e);
//				}
//			}
//		}
//		return result == 0;
//	}
//
//	/**
//	 * 在主页面
//	 * @param context
//	 * @return
//     */
//	public static boolean isHome(Context context) {
//		ActivityManager mActivityManager = (ActivityManager) context
//				.getSystemService(Context.ACTIVITY_SERVICE);
//		@SuppressWarnings("deprecation")
//		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
//		List<String> homePackageNames = getHomes(context);
//		return homePackageNames.contains(rti.get(0).topActivity
//				.getPackageName());
//	}

//	/**
//	 * HOMES查询
//	 * @param context
//	 * @return
//     */
//	private static List<String> getHomes(Context context) {
//		List<String> names = new ArrayList<String>();
//		PackageManager packageManager = context.getPackageManager();
//		// 属性
//		Intent intent = new Intent(Intent.ACTION_MAIN);
//		intent.addCategory(Intent.CATEGORY_HOME);
//		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
//				intent, PackageManager.MATCH_DEFAULT_ONLY);
//		for (ResolveInfo ri : resolveInfo) {
//			names.add(ri.activityInfo.packageName);
//		}
//		return names;
//	}
//
//	/**
//	 * 顶部Activity
//	 * @param context
//	 * @return
//     */
//	public static ComponentName topActivity(Context context) {
//		ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE) ;
//		try {
//			@SuppressWarnings("deprecation")
//			List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1) ;
//			 if(runningTaskInfos != null && runningTaskInfos.size() > 0) {
//				 ComponentName component = runningTaskInfos.get(0).topActivity;
//				 return component;
//			}
//		} catch (Exception e) {
//			NLog.printStackTrace(e);
//		}
//
//		return null;
//	}

//	private final static int kSystemRootStateUnknow = -1;
//	private final static int kSystemRootStateDisable = 0;
//	private final static int kSystemRootStateEnable = 1;
//	private static int sRootState = kSystemRootStateUnknow;

//	/**
//	 * 判断系统是否已经ROOT
//	 * @return
//	 */
//	public static boolean hasSystemRooted() {
//
//		if (sRootState == kSystemRootStateEnable) {
//			return true;
//		} else if (sRootState == kSystemRootStateDisable) {
//			return false;
//		}
//
//		File f = null;
//		final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/",
//				"/system/sbin/", "/sbin/", "/vendor/bin/" };
//		try {
//			for (int i = 0; i < kSuSearchPaths.length; i++) {
//				f = new File(kSuSearchPaths[i] + "su");
//				if (f != null && f.exists()) {
//					sRootState = kSystemRootStateEnable;
//					return true;
//				}
//			}
//		} catch (Exception e) {
//			NLog.e("ContextUtil", e);
//		}
//
//		sRootState = kSystemRootStateDisable;
//		return false;
//	}
//
//	static final String DOMAIN_GLOBAL = "GLOBAL";
//	static final String DOMAIN_CHINA = "CHINA";
//	/**
//	 * 判断是否为国内版本
//	 *
//	 * @param
//	 * @return
//	 */
//	public static boolean isGlobalVersion(Context context){
//		Context appContext = context.getApplicationContext();
//		String currentDomain = ContextUtils.getMetaData(appContext, "DOMAIN_VERSION");
//		if (TextUtils.isEmpty(currentDomain) || !DOMAIN_CHINA.equalsIgnoreCase(currentDomain)){
//			return true;
//		}
//		return false;
//	}

//	/**
//	 * 获取当前有效时间
//	 * 如果可以获取网络时间  网络获取
//	 *
//	 * @return long
//	 */
//	public static long getLatestActualTime() {
//        /*  获取本地时间 用户因为手动设置时间  会导致误差   所以  在网络时间获取 满足的 情况下  获取网络时间  */
//		long specTime = System.currentTimeMillis();
//		if (NetworkHelper.sharedHelper().isNetworkAvailable()) { //获取网络时间
//			try {
//				URL timeUrl = new URL("http://www.time.ac.cn");//国家授权时间中心
//				URLConnection timeConn = timeUrl.openConnection();
//				timeConn.connect();
//				long ld = timeConn.getDate();
//				return ld;
//			} catch (Exception e) {
//				NLog.printStackTrace(e);
//				return specTime;
//			}
//		}
//		return specTime;
//	}
	
	
}

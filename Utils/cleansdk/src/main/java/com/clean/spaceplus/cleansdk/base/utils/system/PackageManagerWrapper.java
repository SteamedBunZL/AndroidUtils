package com.clean.spaceplus.cleansdk.base.utils.system;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.utils.monitor.MonitorManager;
import com.clean.spaceplus.cleansdk.junk.cleancloud.residual.cloud.ResidualCloudQueryImpl;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import space.network.util.RuntimeCheck;

/**
 * @author liangni
 * @Description:获取已安装列表相关信息
 * @date 2016/4/11 19:25
 * @copyright TCL-MIG
 */
public class PackageManagerWrapper {
	private static final String TAG = PackageManagerWrapper.class.getSimpleName();
	private static PackageManagerWrapper instanceManagerWrapper = new PackageManagerWrapper();
	Context mCtxContext 	= SpaceApplication.getInstance().getContext();
	private PMWrapper mWrapper	= null;
		
	private PackageManagerWrapper() {
		if (!RuntimeCheck.IsServiceProcess()) {
			mWrapper = new PMCacheableWrapper();
			NLog.d(TAG, " not service process: new PMCacheableWrapper() ");
		} else {
			NLog.d(TAG, "  service process: new PMWrapper() ");
			mWrapper = new PMWrapper();
		}
	}
	
	public static PackageManagerWrapper getInstance() {
		return instanceManagerWrapper;
	}
	
	/*
	 *  get all PackageInfo
	 *  (non-service process will cache results)
	 */
	public List<PackageInfo> getPkgInfoList() {
		return mWrapper.getPkgInfoList();
	}
	
	/*
	 *  get the PackageInfo of system app
	 *  (non-service process will cache results)
	 */
	public List<PackageInfo> getSystemPkgInfoList() {
		return mWrapper.getSystemPkgInfoList();
	}
	
	/*
	 *  get the PackageInfo of system app <packagename, PackageInfo>
	 *  (non-service process will cache results)
	 */
	public Map<String, PackageInfo> getUserPkgInfoMap() {
		List<PackageInfo> list = getUserPkgInfoList();
		if (list == null || list.isEmpty()) {
			return null;
		}
		
		Map<String, PackageInfo> map = new HashMap<String, PackageInfo>(list.size());
		for (PackageInfo info : list) {
			if (info == null) {
				continue ;
			}
			
			String pkgName = info.packageName;
			if (TextUtils.isEmpty(pkgName)) {
				continue ;
			}
			
			map.put(pkgName, info);
		}
		
		return map;
	}
	
	/*
	 *  get the PackageInfo of user installed app
	 *  (non-service process will cache results)
	 */
	public List<PackageInfo> getUserPkgInfoList() {
		return mWrapper.getUserPkgInfoList();
	}
	
	/**
	 * package name only
	 * */
	public List<String> getUserPkgNameList() {
		return mWrapper.getUserPkgNameList();
	}
	
		
/*	public List<PackageInfo> getUserPkgInfoListWithPermisson() {
		return mWrapper.getUserPkgInfoListWithPermisson();
	}*/
	
	/*
	 *  pre-generate all PackageInfo and cache them
	 *  (only non-service process will work)
	 */
	public void init() {
		mWrapper.init();
	}
	
	/*
	 *  remove PackageInfo by package name
	 *  (only non-service process will work)
	 */
	public void removePkg(String sPkgName) {
		mWrapper.removePkg(sPkgName);
	}

    public int getAppVersionCode(String sPkgName) {
        return mWrapper.getAppVersionCode(sPkgName);
    }

    public boolean isSystemApp(String sPkgName) {
		return mWrapper.isSystemApp(sPkgName);
	}

    public ProviderInfo[] getProviderInfo(Context context, String packagename) {
        return mWrapper.getProviderInfo(context,packagename);
    }
	
	/*
	 * Base wrapper class, get PackageInfo from PMS directly 
	 */
	private class PMWrapper {
		PackageManager mPM = mCtxContext.getPackageManager();

        public ProviderInfo[] getProviderInfo(Context context, String packagename) {
            PackageInfo pi;
            ProviderInfo[] providers = null;
            try {
                pi = mPM.getPackageInfo(packagename, PackageManager.GET_PROVIDERS);
                providers = pi.providers;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return providers;
        }

		public List<PackageInfo> getPkgInfoList() {
			return getInstalledPackagesNoThrow(mPM, 0);
		}

		public boolean isSystemApp(String sPkgName) {
			if (TextUtils.isEmpty(sPkgName)) {
				return false;
			}
			PackageInfo packageInfo = null;
			try {
				packageInfo = mPM.getPackageInfo(sPkgName, 0);
				return PackageManagerWrapper.isSystemApp(packageInfo.applicationInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

        public int getAppVersionCode(String sPkgName) {
			if (TextUtils.isEmpty(sPkgName)) {
				return -1;
			}
			PackageInfo packageInfo = null;
			try {
				packageInfo = mPM.getPackageInfo(sPkgName, 0);
				return packageInfo.versionCode;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return -1;
		}

		public List<PackageInfo> getSystemPkgInfoList() {
			List<PackageInfo> allPkgs = getInstalledPackagesNoThrow(mPM, 0);
			if (allPkgs == null) {
				return null;
			}
			List<PackageInfo> list = new ArrayList<PackageInfo>();
			for (PackageInfo packageInfo : allPkgs) {
				if (PackageManagerWrapper.isSystemApp(packageInfo.applicationInfo)) {
					list.add(packageInfo);
				}
			}
			return list;
		}
		
		public List<PackageInfo> getUserPkgInfoList() {
			List<PackageInfo> allPkgs = getInstalledPackagesNoThrow(mPM, 0);
			if (allPkgs == null) {
				return null;
			}
			List<PackageInfo> list = new ArrayList<PackageInfo>();
			for (PackageInfo packageInfo : allPkgs) {
				if (isUserApp(packageInfo.applicationInfo)) {
					list.add(packageInfo);
				}
			}
			return list;
		}

		public List<String> getUserPkgNameList() {
			List<PackageInfo> allPkgs = getInstalledPackagesNoThrow(mPM, 0);
			if (allPkgs == null || allPkgs.isEmpty()) {
				return null;
			}

			List<String> list = new ArrayList<String>();
			for (PackageInfo packageInfo : allPkgs) {
				if (packageInfo == null) {
					continue;
				}
				if (isUserApp(packageInfo.applicationInfo)) {
					list.add(packageInfo.packageName);
				}
			}
			
			return list;
		}
		
/*		public List<PackageInfo> getUserPkgInfoListWithPermisson() {
			List<PackageInfo> allPkgs = getInstalledPackagesNoThrow(mPM, PackageManager.GET_PERMISSIONS);
			if (allPkgs == null) {
				return null;
			}

			List<PackageInfo> list = new ArrayList<PackageInfo>();
			for (PackageInfo packageInfo : allPkgs) {
				if (Commons.isUserApp(packageInfo.applicationInfo)) {
					list.add(packageInfo);
				}
			}
			return list;
		}*/
		
		public void init() {
			// do nothing
		}
		
		public void removePkg(String sPkgName) {
			// do nothing
		}
	}

	public static boolean isUserApp(ApplicationInfo info) {
		if (info == null) {
			return false;
		}
		return !((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}

	public static boolean isSystemApp(ApplicationInfo info) {
		if (info == null) {
			return false;
		}
		return ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}
	/*
	 * Cacheable wrapper class, will cache all PackageInfo after the first query
	 */
	private class PMCacheableWrapper extends PMWrapper implements MonitorManager.Monitor {
		private int    UN_INIT		= 0;
		private int    INIT_ING		= 1;
		private int    INIT_END		= 2;
		private int    nInit_Status = UN_INIT;
		private List<PackageInfo> mPkgList = null;

		@Override
		public List<PackageInfo> getPkgInfoList() {
			synchronized (this) {
				//fix bug http://jira.lab.tclclouds.com/browse/SC-99
				//modify by xiangxiang.liu 2016/6/1 15:25
				//if (mPkgList == null) {
				if (mPkgList == null) {
					mPkgList = getInstalledPackagesNoThrow(mPM, 0);
					if (mPkgList != null){
						NLog.d(TAG, "PMCacheableWrapper getInstalledPackagesNoThrow mPkgList size = "+mPkgList.size());
					}
					nInit_Status = INIT_END;
				}

				List<PackageInfo> list = null;
				if (mPkgList != null) {
					list = new ArrayList<PackageInfo>();
					list.addAll(mPkgList);
				}
				return list;
			}
		}
		
		@Override
		public List<PackageInfo> getSystemPkgInfoList() {
			synchronized (this) {
				if (mPkgList == null) {
					mPkgList = getInstalledPackagesNoThrow(mPM, 0);
					nInit_Status = INIT_END;
				}
				if (mPkgList == null) {
					return null;
				}
				List<PackageInfo> list = new ArrayList<PackageInfo>();
				for (PackageInfo packageInfo : mPkgList) {
					if (PackageManagerWrapper.isSystemApp(packageInfo.applicationInfo)) {
						list.add(packageInfo);
					}
				}
				return list;
			}
		}

        public int getAppVersionCode(String sPkgName) {
            if (TextUtils.isEmpty(sPkgName)) {
                return -1;
            }
            synchronized (this) {
                if (mPkgList == null) {
                    mPkgList = getInstalledPackagesNoThrow(mPM, 0);
                    nInit_Status = INIT_END;
                }
                if (mPkgList == null) {
                    return -1;
                }
                for (PackageInfo packageInfo : mPkgList) {
                    if (sPkgName.equalsIgnoreCase(packageInfo.packageName)) {
                       return packageInfo.versionCode;
                    }
                }
            }
            return super.getAppVersionCode(sPkgName);
        }

		@Override
		public List<PackageInfo> getUserPkgInfoList() {
			synchronized (this) {
				if (mPkgList == null) {
					mPkgList = getInstalledPackagesNoThrow(mPM, 0);
					nInit_Status = INIT_END;
				}
				if (mPkgList == null) {
					return null;
				}
				List<PackageInfo> list = new ArrayList<PackageInfo>();
				for (PackageInfo packageInfo : mPkgList) {
					if (PackageManagerWrapper.isUserApp(packageInfo.applicationInfo)) {
						list.add(packageInfo);
					}
				}
				return list;
			}
		}
		
		@Override
		public void init() {
			synchronized (this) {
				if ((mPkgList != null && mPkgList.size() > 0 ) || nInit_Status != UN_INIT) {
					return;
				}

				MonitorManager.getInstance().addMonitor(
							MonitorManager.TYPE_PACKAGE_ADD, this,
							MonitorManager.PRIORITY_NORMAL);
				MonitorManager.getInstance().addMonitor(
							MonitorManager.TYPE_PACKAGE_REMOVE, this,
							MonitorManager.PRIORITY_NORMAL);

				nInit_Status = INIT_ING;
				new initThread().start();
			}
		}

		@Override
		public void removePkg(String sPkgName) {
			synchronized (this) {
				if (sPkgName != null && mPkgList != null) {
					PackageInfo infoTemPackageInfo = null;
					for (PackageInfo info: mPkgList) {
						if (info != null && sPkgName.equals(info.packageName)) {
							infoTemPackageInfo = info;
							break;
						}
					}
					if (infoTemPackageInfo != null) {
						mPkgList.remove(infoTemPackageInfo);
					}
				}
			}
		}
		
		@Override
		public int monitorNotify(int type, Object param1, Object param2) {
			if (type == MonitorManager.TYPE_PACKAGE_ADD) {
				Intent intent = (Intent)param2;
				String packageName = intent.getData().getSchemeSpecificPart();
				boolean isReplace = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
				if (isReplace) {
					updatePkg(packageName);
				} else {
					addPkg(packageName);
				}
			} else if (type == MonitorManager.TYPE_PACKAGE_REMOVE) {
				Intent intent = (Intent)param2;
				boolean isReplace = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
				String packageName = intent.getData().getSchemeSpecificPart();
				if (!isReplace) {
					removePkg(packageName);
				}
			}
			
			return 0;
		}
		
		private void addPkg(PackageInfo pkgInfo) {
			synchronized (this) {
				if (mPkgList != null  && pkgInfo != null) {
					mPkgList.remove(pkgInfo);
					mPkgList.add(pkgInfo);
				}
			}
		}
		
		private void addPkg(String sPkgname) {
			synchronized (this) {
				if (sPkgname != null  && mPkgList != null) {
					PackageInfo info = null;
					try {
						info = mPM.getPackageInfo(sPkgname, 0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (info != null) {
						addPkg(info);
					}
				}
			}
		}
		
		private void updatePkg(String sPkgName) {
			removePkg(sPkgName);
			addPkg(sPkgName);
		}
	}
	
	private class initThread extends Thread {
		@Override
		public void run() {
			PackageManagerWrapper.getInstance().getPkgInfoList();
		}
	}
	
	private synchronized List<PackageInfo> getInstalledPackagesNoThrow(PackageManager pm, int flags) {
		NLog.d(ResidualCloudQueryImpl.TAG, "getInstalledPackagesNoThrow");
		List<PackageInfo> pkgList = null;
		try {
			pkgList = pm.getInstalledPackages(flags);
		} catch (Exception e) {
			// error handling for dumpkey: 3592114665
			NLog.e(ResidualCloudQueryImpl.TAG, "getInstalledPackagesNoThrow Exception: "+e);
		}
		if (pkgList != null){
			NLog.d(ResidualCloudQueryImpl.TAG, "低层获取到的包名列表size = "+ pkgList.size());
		}
		return pkgList;
	}
}


package com.clean.spaceplus.cleansdk.junk.engine.task;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.SystemClock;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.base.utils.system.SystemCacheManager;
import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.util.PackageUtils;
import com.clean.spaceplus.cleansdk.util.SizeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import space.network.cleancloud.KCacheCloudQuery;

/**
 * @author liangni
 * @Description:系统垃圾扫描任务
 * @date 2016/4/22 15:51
 * @copyright TCL-MIG
 */

public class SysCacheScanTask extends ScanTask.BaseStub{

	public static final String TAG = SysCacheScanTask.class.getSimpleName();

	// TODO 待云端代码完善后修改这里的描述-596
	final String strDesc = "";
	final String strAlertInfo = "<font color=#d64438 >Be careful!</font><br/>If you clean this cache you may lost some important files.";
	final int SRSID = 271;

	public static final int SYS_CACHE_SCAN_FINISH			 = 1;	///< 扫描结束，若因超时结束，则arg1值为1；若用户过滤，则值为2；否则为0。
	public static final int SYS_CACHE_SCAN_STATUS			 = 2;
	public static final int SYS_CACHE_SCAN_FOUND_ITEM		 = 3;
	public static final int SYS_CACHE_SCAN_PROGRESS_START	 = 4;	///< 开始进度计算，arg1值为总步数
	public static final int SYS_CACHE_SCAN_PROGRESS_STEP	 = 5;	///< 进度计算，加一步。有可能多个线程同时回调
	public static final int SYS_CACHE_SCAN_START	         = 6;
	public static final int SYS_CACHE_SCAN_PACKAGESTATS_ITEM = 7;

	//SD_CACHE_SCAN_CFG_MASK_NOT_RETURN_IGNORE 默认配置是不在SD_CACHE_SCAN_FOUND_ITEM的消息回调中返回ignore item
	//如果将该配置取非，那么白名单中的项也会被作为结果返回，可以通过JunkInfoBase的isIgnore()查询是否是ignore item
	public static final int SYS_CACHE_SCAN_CFG_MASK_NOT_RETURN_IGNORE				= 0x00000001;
	public static final int SYS_CACHE_SCAN_CFG_MASK_NOT_ADJUST_ASYNC_THREAD_NUM	    = 0x00000002;	///< 不要根据CPU数量来控制同步扫描线程个数
	public static final int SYS_CACHE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS	        = 0x00000004;   ///< 是否 检查扫描结果 用户设置的锁定状态    默认 不检查
	public static final int SYS_CACHE_SCAN_CFG_MASK_LOAD_LABEL					    = 0x00000008;	///< 要加载app名字
	public static final int SYS_CACHE_SCAN_CFG_MASK_NOT_RETURN_PKGSTATS		        = 0x00000010;	///< 是否通过SYS_CACHE_SCAN_PACKAGESTATS_ITEM返回packagestats,默认不返回
	//小于20K不显示出来
	public static final long MIN_SIZE_LIMIT = 25 * 1024;

	private class SysCacheData {
		public String strDesc;
		public String strAlertInfo;
		public int nSrsID;
		public int nType;
	}

	public static class SysCacheOnCardInfo {
		public long nTotalSize;
		public List<String> strAbsPathList;
		public String strPackageName;
	}

	private List<String> mSdCardPathList = null;
	private TreeMap<String, KCacheCloudQuery.SysCacheFlagQueryData> mSysCacheFilter =
			new TreeMap<String, KCacheCloudQuery.SysCacheFlagQueryData>();

	public void setScanConfigMask(int mask) {
		mScanCfgMask = mask;
	}

	public int getScanConfigMask() {
		return mScanCfgMask;
	}

	/**
	 * @param caller 取值定义为cm_task_time.CM_TASK_TIME_USER_*
	 */
	public void setCaller(byte caller) {
	}

	public void setFirstScanFlag() {
	}

	@Override
	public boolean scan(ScanTaskController ctrl) {
		mStartTime = SystemClock.uptimeMillis();
		return doScan(ctrl);
	}

	/**
	 * 安装包列表处理
	 *
	 * @param pkgList
	 */
	public void setInstalledPkgList(List<PackageInfo> pkgList) {
		final String PKG_NAME = SpaceApplication.getInstance().getContext().getPackageName();
		List<PackageInfo> filterPkgList = new ArrayList<>();
		if(pkgList==null){
			mPkgList=new ArrayList<PackageInfo>();
			return;
		}
		List<PackageInfo> packageList = new ArrayList<>(pkgList);
		for (PackageInfo pi : packageList) {
			//不要扫描自己
			if (PKG_NAME.equals(pi.packageName)) {
				continue;
			}
			filterPkgList.add(pi);
		}
		mPkgList = filterPkgList;
	}

	public void setPkgManager(PackageManager pm) {
		mPackageManager = pm;
	}

	public static long calcCacheSize(PackageStats pStats) {
		return pStats.cacheSize;
	}

	private void reportPCScanTime() {
	}

	private boolean doScan(final ScanTaskController ctrl) {
		if (null != mCB) {
			mCB.callbackMessage(SYS_CACHE_SCAN_START, 0, 0, null);
		}
		if (null == mPackageManager) {
			if (null != mCB) {
				mCB.callbackMessage(SYS_CACHE_SCAN_PROGRESS_START, 0, 0, null);
				mCB.callbackMessage(SYS_CACHE_SCAN_FINISH, 0, 0, null);
				reportPCScanTime();
			}
			return false;
		}

		if (null == mPkgList || mPkgList.isEmpty()) {
			if (null != mCB) {
				mCB.callbackMessage(SYS_CACHE_SCAN_PROGRESS_START, 0, 0, null);
				mCB.callbackMessage(SYS_CACHE_SCAN_FINISH, 0, 0, null);
				reportPCScanTime();
			}
			return true;
		}

		mCtx = SpaceApplication.getInstance().getContext();

		if (null != mCB) {
			mCB.callbackMessage(SYS_CACHE_SCAN_PROGRESS_START, mPkgList.size() * 2, 0, null);
		}
		//如果已经预加载完成，则直接读取json文件，获取缓存数据，推送到状态机
		boolean isFinish = true;
		if (isFinish) {
			List<CacheInfo> sysCacheInfos = new SystemCacheManager(mCtx).getSystemCache();
			if (sysCacheInfos != null && sysCacheInfos.size() > 0) {
				for (CacheInfo info : sysCacheInfos) {
					if (mCB != null) {
						mCB.callbackMessage(SYS_CACHE_SCAN_STATUS, 0, 0, info.mPkgName);
						String size = SizeUtil.formatSizeForJunkHeader(info.getSize());
						PackageInfo pkgInfo = PackageUtils.getPackageInfo(mCtx, info.mPkgName);
						info.setPackageInfo(pkgInfo);
						mCB.callbackMessage(SYS_CACHE_SCAN_FOUND_ITEM, 0, 0, info);
						//NLog.i(TAG, "预先加载: %s %s的垃圾 大小为 %s ", info.getPackageName(), info.getAppName(), size);
						if (null != mCB) {
							mCB.callbackMessage(SYS_CACHE_SCAN_PROGRESS_STEP, 0, 0, null);
						}
					}
				}
			}

			if (mCB != null) {
				//NLog.i(TAG, "预先加载扫描任务完成");
				mCB.callbackMessage(SYS_CACHE_SCAN_FINISH, 0, 0, null);
				//NLog.i(TAG, "preload run time %d ", (SystemClock.uptimeMillis() - mStartTime));
			}
		}

		return true;
	}

	private List<PackageInfo> mPkgList = null;
	private PackageManager mPackageManager = null;
	private int mScanCfgMask = -1;
	private long mStartTime = 0L;

	private Context mCtx = null;

	@Override
	public String getTaskDesc() {
		return "SysCacheScanTask";
	}
}

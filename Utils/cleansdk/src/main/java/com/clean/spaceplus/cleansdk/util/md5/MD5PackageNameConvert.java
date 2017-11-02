package com.clean.spaceplus.cleansdk.util.md5;

import android.content.pm.PackageInfo;

import com.clean.spaceplus.cleansdk.base.utils.system.PackageManagerWrapper;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/29 20:35
 * @copyright TCL-MIG
 */
public class MD5PackageNameConvert {
    //private Map<String, String> mMD5PackageMap = new HashMap<String, String>();
    private List<String> mAllPkgNames = new ArrayList<String>();

    // 得到设备上所有已安装应用的包名
    public synchronized void getInstallApplication() {
        List<PackageInfo> packageInfoList = null;
        try {
            packageInfoList = PackageManagerWrapper.getInstance().getPkgInfoList();
        } catch (Exception e) {
            e.printStackTrace();
            if (PublishVersionManager.isTest()) {
                throw new RuntimeException("Warning! No packages found!", e);
            }
        }
        if (null == packageInfoList) {
            return;
        }
        int size = packageInfoList.size();

        //mMD5PackageMap.clear();
        for (int i = 0; i < size; ++i) {
            String packageName = packageInfoList.get(i).packageName;
            String md5 = Md5Util.getPackageNameMd5(packageName);
            //mMD5PackageMap.put(md5, packageName);
            mAllPkgNames.add(packageName);
        }
    }

//	public synchronized String getPackageNameByMD5(String packageNameMD5) {
//		if (mMD5PackageMap.size() == 0) {
//			getInstallApplication();
//		}
//		return mMD5PackageMap.get(packageNameMD5);
//	}

//	public synchronized String getPackageNameByMD5ForCloudQuery(String packageNameMD5) {
//		if (mMD5PackageMapForCloudQuery.size() == 0) {
//			getInstallApplication();
//		}
//		return mMD5PackageMapForCloudQuery.get(packageNameMD5);
//	}

    /**
     * 当前系统中是否已经安装此包
     */
//	public synchronized boolean isInstalled(String packageNameMD5) {
//		if (mMD5PackageMap.size() == 0) {
//			getInstallApplication();
//
//			if (mMD5PackageMap.isEmpty()) {
//				// 防止无权限读取App列表的时候，我们不要误报残留。
//				return true;
//			}
//		}
//		return mMD5PackageMap.containsKey(packageNameMD5);
//	}


    /**
     * 将数据库中加密packagenames字符串枚举解密。 不能枚举出来的，仍用md5字符串。
     */
//	public synchronized String[] getPackageNames(String md5Str) {
//		if (mMD5PackageMap.size() == 0) {
//			getInstallApplication();
//		}
//
//		if (md5Str == null) {
//			return null;
//		}
//		String md5Names[] = md5Str.split("\\+");
//		String[] packageNames = new String[md5Names.length];
//		for (int i = 0; i < md5Names.length; i++) {
//			String name = getPackageNameByMD5(md5Names[i]);
//			if (name != null) {
//				packageNames[i] = name;
//			} else {
//				packageNames[i] = md5Names[i];
//			}
//		}
//		return packageNames;
//	}

    public synchronized List<String> getAllPackageNames() {
        if (mAllPkgNames.isEmpty()) {
            getInstallApplication();
        }

        List<String> rst = new ArrayList<String>();
        rst.addAll(mAllPkgNames);

        return rst;
    }

}

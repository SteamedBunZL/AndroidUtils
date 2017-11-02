package com.clean.spaceplus.cleansdk.util;

import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;

import com.clean.spaceplus.cleansdk.main.bean.StorageInfo;

import java.io.File;
import java.util.ArrayList;

/**
 * @author liangni
 * @Description:
 * @date 2016/5/18 19:25
 * @copyright TCL-MIG
 */
public class StorageInfoUtils {

    /**
     * 取手机内部/data挂载点的存储信息
     *
     * @return 取到的存储信息(如果为空，或内部取到size值为0，则表明没有挂载。)
     */
    public static StorageInfo getDeviceStorageInfo() {
        return getStorageInfo(Environment.getDataDirectory());
    }

    public static StorageInfo getDeviceStorageInfoEx() {
        StorageInfo phoneInfo = getDeviceStorageInfo();
        if (null != phoneInfo && 0 != phoneInfo.allSize) {
            long low = SystemProperties.getLong("sys.memory.threshold.low", 0L);
            low = Math.min(phoneInfo.freeSize, low);
            phoneInfo.freeSize = phoneInfo.freeSize - low;
        }
        //System.out.println("系统空间大小  getDeviceFreeSize()="+(freeSize));
        return phoneInfo;

    }

//	public static int getDeviceFreePercent(){
//		int freePercent = 0;
//    	StorageInfo deviceStorageInfo = StorageInfoUtils.getDeviceStorageInfoEx();
//		if(null == deviceStorageInfo || deviceStorageInfo.allSize <= 0L){
//			return freePercent;
//		}
//		try{
//			freePercent = Commons.calcPercentage(deviceStorageInfo.freeSize, deviceStorageInfo.allSize);
//		}catch(IllegalArgumentException e){
//		}
//		return freePercent;
//    }

    public static StorageInfo getStorageInfo(ArrayList<String> mountedVolumePaths) {
        if (null == mountedVolumePaths) {
            return null;
        }

        StorageInfo allResult = null;
        StorageInfo tempResult = null;

        for ( String volpath : mountedVolumePaths ) {
            tempResult = getStorageInfo(new File(volpath));

            if (null == tempResult) {
                continue;
            }

            if (null != allResult) {
                allResult.allSize += tempResult.allSize;
                allResult.freeSize += tempResult.freeSize;
            } else {
                allResult = tempResult;
            }
        }

        return allResult;
    }

    public static StorageInfo getStorageInfo(File storagePathFile) {
        if (null == storagePathFile) {
            return null;
        }

        StatFs fsStat = null;

        try {
            fsStat = new StatFs(storagePathFile.getPath());
        } catch (Exception e) {
//			KInfocClientAssist.getInstance().reportData("cm_bp_sf", "p=" + storagePathFile.getPath());
            return null;
        }

        long sdAvailableBlocks = BlockUtils.getAvailableBlocks(fsStat);// 可用存储块的数量
        long sdBlockcount = BlockUtils.getBlockCount(fsStat);// 总存储块的数量
        long sdSize = BlockUtils.getBlockSize(fsStat);// 每块存储块的大小

        StorageInfo info = new StorageInfo();
        info.allSize = sdBlockcount * sdSize;
        info.freeSize = sdAvailableBlocks * sdSize;

        if (info.allSize < info.freeSize) {
            info.freeSize = info.allSize;
        }

        return info;
    }
}

package com.clean.spaceplus.cleansdk.junk.engine.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;

import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/23 19:11
 * @copyright TCL-MIG
 */
public class EmulateSdCardUtils {
    public static boolean s_bSdcardDataSame = IsSdCardDataSamePart();

    /*
     * 判断设备是否有虚拟的外部存储空间，例如米2，n7等都会返回true
     * 本函数判断所有SD卡中是否存在虚拟卡
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("rawtypes")
    public static boolean isAnyExtStorageEmulated() {
        if (Build.VERSION.SDK_INT >= 21) {
            return isAnyExtStorageEmulatedFor21();
        } else if (Build.VERSION.SDK_INT >= 14) {
            return isAnyExtStorageEmulatedFor14();
        } else if (Build.VERSION.SDK_INT >= 11) {
            return Environment.isExternalStorageEmulated();
        }
        return false;
    }

    /*
    * 判断设备是否有虚拟的外部存储空间，例如米2，n7等都会返回true
    * 本函数只判断主卡是否虚拟卡
    */
    @SuppressLint("NewApi")
    @SuppressWarnings("rawtypes")
    public static boolean isExtStorageEmulated() {
        if (Build.VERSION.SDK_INT >= 11) {
            return Environment.isExternalStorageEmulated();
        }
        return false;
    }

    // 大约运行时间需要100ms
    @SuppressLint("NewApi")
    private static boolean IsSdCardDataSamePart(){
        //long nTick = System.currentTimeMillis();
//		if ( Build.VERSION.SDK_INT < 11 ){
//			return false;
//		}

        //ro.crypto.fuse_sdcard

//		if ( !SystemProperties.get("ro.fuse_sdcard","false").equals("true") &&
//			 !SystemProperties.get("ro.crypto.fuse_sdcard","false").equals("true") )
//			return false;

        if ( !isExtStorageEmulated() ){
            return false;
        }

        boolean bRes = CheckSdcardDataPartSame2();

        //Log.e("CheckSdcardDataPartSame", "" + ( System.currentTimeMillis() - nTick));
        return bRes;
    }

    @SuppressLint("NewApi")
    private static boolean isAnyExtStorageEmulatedFor21() {

        assert (Build.VERSION.SDK_INT >= 21);

        ArrayList<String> pathList = new StorageList().getMountedPhoneVolumePaths();
        if (null == pathList || pathList.isEmpty()) {
            return false;
        }

        Iterator<String> iterNow = pathList.iterator();
        if (null == iterNow) {
            return false;
        }

        while (iterNow.hasNext()) {
            if (Environment.isExternalStorageEmulated(new File(iterNow.next()))) {
                return true;
            }
        }

        return false;
    }

    private static boolean isAnyExtStorageEmulatedFor14() {
        assert (Build.VERSION.SDK_INT >= 14);

        ArrayList<String> pathList = new StorageList().getMountedEmulatedVolumePathsFor14();
        if (null == pathList || pathList.isEmpty()) {
            return false;
        }

        return true;
    }

    private static boolean CheckSdcardDataPartSame2(){

        boolean bRes = true;
        File mountsInfo = new File("/proc/self/mounts");

        if ( mountsInfo.exists() && !mountsInfo.isDirectory() ){
            BufferedReader reader = null;
            try{
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(mountsInfo)));
                while( true ){
                    String line = reader.readLine();
                    if ( line != null ){
                        String[] xs = line.split(" ");
                        if ( xs != null && xs.length > 3 ){
                            String mp = xs[1];
                            String fsFormat = xs[2];

                            if ( fsFormat.startsWith("ext") ){
                                if ( mp.startsWith("/storage_int")  ){
                                    if ( xs[0].startsWith("/dev/block") ){
                                        bRes = false;
                                        break;
                                    }
                                }
                            }

                            if ( fsFormat.startsWith("ext") ){
                                if ( mp.startsWith("/data/media")  ){
                                    if ( xs[0].startsWith("/dev/block") ){
                                        bRes = false;
                                        break;
                                    }
                                }
                            }

                        }
                    }
                    else{
                        break;
                    }
                }
            }catch(Exception e){
                //	e.printStackTrace();
            }finally{
                if(reader != null){
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bRes;
    }

//	private static boolean CheckSdcardDataPartSame() {
//		File fproc 		= new File("/proc/");
//
//		File[] fSubFoolders = PathOperFunc.listFiles(fproc.getPath());
//		if (fSubFoolders != null && fSubFoolders.length > 0) {
//			for (File f : fSubFoolders) {
//				try {
//					int nPidx = Integer.parseInt(f.getName());
//					if (nPidx > 0) {
//						String str = getCmdLine(nPidx);
//						if (!TextUtils.isEmpty(str)) {
//							if ( str.startsWith("/system/bin/sdcard") ){
//								if (str.indexOf("/data/") != -1 ) {
//									return true;
//								}
//							}
//
//						}
//					}
//				} catch (NumberFormatException e) {
//					continue;
//				}
//			}
//		}
//
//		return false;
//	}

//	private static String getCmdLine(int nPid){
//		return ProcessUtils.getCmdLine(nPid);
//	}
}

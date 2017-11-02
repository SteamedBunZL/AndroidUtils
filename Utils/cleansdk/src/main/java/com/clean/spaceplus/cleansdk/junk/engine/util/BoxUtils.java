package com.clean.spaceplus.cleansdk.junk.engine.util;

import android.os.Build;
import android.os.Environment;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.clean.spaceplus.cleansdk.base.bean.BoxEntry;
import com.clean.spaceplus.cleansdk.base.utils.root.SuExec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/23 16:55
 * @copyright TCL-MIG
 */
public class BoxUtils {
    private static BoxUtils sInstance = new BoxUtils();
    private static final boolean DEBUG = true;
    private File CMBOX_HOME = null;
    private File CMBOX_HOME_Android_data = null;
    private File mExtSdcardMountPoint = null; // 外置物理Sdcard
    public static ArrayMap<String, String[]> BIG_DATA_ON_SDCARD = new ArrayMap<String, String[]>();
    private final static String CM_MOVE_PREFERENCE = "cm_move_preference";

    public static boolean sIsMoveSystem = false;

    static{
        BIG_DATA_ON_SDCARD.put("com.baidu.BaiduMap", new String[] { "BaiduMap"});
        BIG_DATA_ON_SDCARD.put("com.youku.phone", new String[] { "youku" });
        BIG_DATA_ON_SDCARD.put("com.jb.gosms", new String[] { "GOSMS" });
        BIG_DATA_ON_SDCARD.put("com.ogqcorp.bgh", new String[] { "/OGQ/BackgroundsHD" });
    }

    protected static void log(String message) {
        if(DEBUG) Log.e("app2sd", message);
    }

    private BoxUtils() {
        mExtSdcardMountPoint = getPhysicalExternalSdcardFile();
        log("mExtSdcardMountPoint="+mExtSdcardMountPoint);
        if(mExtSdcardMountPoint!=null) {
            CMBOX_HOME = new File(getPhysicalExternalSdcardFile(),".cmbox");
            CMBOX_HOME_Android_data = new File(CMBOX_HOME ,"Android/data");
        }
    }

    public static BoxUtils getInstance() {
        return sInstance;
    }

    /**
     * 通过分析/etc/vold.fstab文件找到外置sdcard physical
     * @return
     */
    public static File getPhysicalExternalSdcardFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/etc/vold.fstab"));
            String line = br.readLine();
            while((line = br.readLine()) !=  null) {
                if(line.startsWith("#")) {
                    continue;
                }

                if(line.contains("dev_mount") && line.contains("sdcard")) {
                    String[] xs = line.split("[\t ]");
                    String mountPoint = xs[2];
                    if(Environment.getExternalStorageDirectory().getAbsolutePath().equals(mountPoint)) {
                        continue;
                    } else {
                        br.close();
                        return new File(mountPoint);
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    /**
     * Cmbox是否可用
     * @return
     */
    public boolean isActive() {

        return SuExec.getInstance().checkRoot()
                && EmulateSdCardUtils.isExtStorageEmulated()
                && BoxUtils.getPhysicalExternalSdcardFile() != null
                && Build.VERSION.SDK_INT < 17;
    }

    /**
     *  该应用是否有Android/data目录
     * @return
     */
    public static File getAndroidDataFile(String packageName) {
        String internalsd = Environment.getExternalStorageDirectory().getAbsolutePath(); //getInternalSdcardPath();
        return (TextUtils.isEmpty(internalsd) || TextUtils.isEmpty(packageName))
                ? null
                : new File(internalsd,"Android/data/"+packageName);
    }

    /**
     *  获取所有可能需要挂载的目录
     * @return
     */
    public ArrayList<BoxEntry> getAllMountPoints() {
        ArrayList<BoxEntry> mps = new ArrayList<BoxEntry>();

        if(!isActive()) {
            return mps;
        }

        // 1. Android/data
        File[] xs = PathOperFunc.listFiles(CMBOX_HOME_Android_data.getPath());

        if (xs != null) {
            for (File x : xs) {
                mps.add(new BoxEntry(getAndroidDataFile(x.getName()), x , x.getName()));
            }
        }

        // 2. DataExt
        Iterator<Map.Entry<String, String[]>> iter = BIG_DATA_ON_SDCARD.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) iter.next();
            //String   pkgName = (String) entry.getKey();
            String[] folders = (String[]) entry.getValue();
            if(folders != null ) {
                File file = new File(CMBOX_HOME,folders[0]);
                if(file.exists()){
                    mps.add(new BoxEntry(new File(Environment.getExternalStorageDirectory(), folders[0]), new File(CMBOX_HOME, folders[0]),getPkgNameByBigDataOnSdcard(folders[0])));
                }
            }
        }
        return mps;
    }

    private String getPkgNameByBigDataOnSdcard(String value){
        Iterator<Map.Entry<String, String[]>> iter = BIG_DATA_ON_SDCARD.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) iter.next();
            String v[] = (String[]) entry.getValue();
            for(String t : v){
                if(t.contains(value)){
                    return (String) entry.getKey();
                }
            }
        }
        return null;
    }
}

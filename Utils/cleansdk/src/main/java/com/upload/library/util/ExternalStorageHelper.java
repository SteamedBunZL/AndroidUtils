package com.upload.library.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zeming_liu
 * @Description:
 * @date 2016/9/7.
 * @copyright TCL-MIG
 */
public class ExternalStorageHelper {
    public static final String TAG = ExternalStorageHelper.class.getSimpleName();

    public static boolean isSDAvailable() {
        //SD卡已挂载，且能进行读写操作
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSdcardExists() {
        File file = new File(getExtSDCardPath());
        long totalSize = file.getTotalSpace(); //当文件路径不存在时返回0
        if (totalSize != 0){  //通过
            return true;

        }
        else {
            return false;
        }
//        if (getExtFileList() != null && !getExtFileList().isEmpty()){
//            return true;
//        } else {
//            return false;
//        }
    }


    /**
     * 获取内置SD卡路径
     */
    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取外置SD卡路径
     */
    public static String getExtSDCardPath() {
        String lResult = null;
        try {
            Runtime rt = Runtime.getRuntime();
            String cmd = "cat system/etc/vold.fstab";
            Process proc = rt.exec(cmd);
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("dev_mount")) {
                    String[] arr = line.split(" ");
                    String path = arr[2];
                    File file = new File(path);
                    if (file.isDirectory()) {
                        lResult = path;
                        break;
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
        }
        if (lResult == null) {
            lResult = "/storage/sdcard1";
        }
        return lResult;
    }



    public static String getExtSDCardPathByReveiver(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString("SDcardPath", "null");
    }

    /**
     * 获取内部存储卡的目录文件列表
     *
     * @return
     */
    public static List<File> getInnerlDirList() {
        if (!isSDAvailable()) {
//            Log.d(TAG, "Storage is unmounted");
            return null;
        }
        File[] files = Environment.getExternalStorageDirectory().listFiles();

        return Arrays.asList(files);
    }

    /**
     * 获取外部存储卡的目录文件列表
     *
     * @return
     */
    public static List<File> getExtFileList() {
        File file = new File(getExtSDCardPath());
        if (!file.exists()) {
//            Log.d(TAG, "access sd_card error,file is not exists");
            return null;
        }
        File[] files = file.listFiles();
//        Log.d(TAG,"total:"+file.getTotalSpace()+"usable:"+file.getUsableSpace()+"free:"+file.getFreeSpace());

        if (files == null ) {
            return null;
        }

        return Arrays.asList(files);
    }
    public static List<File> getFilesExcludeHideFiles(File sourceFile){
        List<File> fileList = new ArrayList<>();
       if (sourceFile.isFile() ){
           return  null;
       }
       else  {
           for (File file :sourceFile.listFiles()){
                    if (!file.isHidden()) {
                        fileList.add(file);
                    }
           }
           return  fileList;
       }

    }
}

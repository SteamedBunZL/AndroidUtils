package com.clean.spaceplus.cleansdk.junk.engine.util;

import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;

import java.util.ArrayList;

/**
 * @author zengtao.kuang
 * @Description: ApkBackup过滤
 * @date 2016/5/11 11:12
 * @copyright TCL-MIG
 */
public class ApkBackupFilter {

//    private boolean isFilterDirDeleteName = true;  //是否开启 名字规则匹配直接过滤功能
//    private boolean isFilterAdvTable = true ;//是否开启 广告库匹配直接过滤功能
//    private boolean isFilterSoftDetailTable = true;//是否开启 残留库直接过滤功能
//
//
//    public static final int STAT_FILTER = 0;
//    public static final int STAT_NOT_CLEAN = 1;
//
//    public static final int STAT_CLEAN = 2;

    private StorageList storageList  ;
    private ArrayList<String> mountedVolumePaths  ;
    private static ApkBackupFilter apkBackupFilter = null;

    private ApkBackupFilter() {
        // TODO Auto-generated constructor stub
    }

    public static ApkBackupFilter getInstance() {
        if (apkBackupFilter == null) {
            apkBackupFilter = new ApkBackupFilter();
        }
        return apkBackupFilter;
    }

    /***
     * 备份文件过滤，只要父路径包含backup、back-up、备份就过滤掉
     * true   过滤 （不删除）    false 不过滤（删除）
     * @return 是否过滤
     */
    public boolean filter(String filePath) {
        filePath = getPathWithoutSdcarPath(filePath);

        if (!filePath.contains("/")) { // 在根目录下面
            return false;
        }

        String parentFolderPath = filePath.substring(0, filePath.lastIndexOf('/'));
        return parentFolderPath.contains("backup") || parentFolderPath.contains("back-up") || parentFolderPath.contains("备份");
    }


    private String getPathWithoutSdcarPath(String filePath,String sdCardPath){


        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        // sd卡路径 为空
        if (TextUtils.isEmpty(sdCardPath)) {
            return filePath;
        }

        if (filePath.startsWith(sdCardPath)) {
            // 返回剔除sd卡路径 后的结果
            return filePath.substring(sdCardPath.length()+1);
        }

        return null;
    }


    private String getPathWithoutSdcarPath(String filePath) {


        if(storageList == null)
        {
            storageList = new StorageList();
        }

        if(mountedVolumePaths==null)
        {
            mountedVolumePaths = storageList.getMountedVolumePaths();
        }
        String path;
        for (String  sdCardPath : mountedVolumePaths) {

            path =  getPathWithoutSdcarPath(filePath,sdCardPath);
            if(path!=null)
            {
                return path;
            }
        }
        return filePath;
    }
}

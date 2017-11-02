package com.clean.spaceplus.cleansdk.util.md5;

import android.os.Environment;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;
import com.clean.spaceplus.cleansdk.junk.engine.task.AdvFolderScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.clean.spaceplus.cleansdk.util.EnableCacheListDir;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author liangni
 * @Description:文件路径与MD5加密转换
 * @date 2016/4/23 15:56
 * @copyright TCL-MIG
 */
public class MD5PathConvert {
    private static final String TAG = AdvFolderScanTask.TAG;
    //key:5bb4d0f5a1e8146e5e9898d0c97d27a2(/storage/sdcard/spacepllusijinsan的md5值) value:/360Download
    private SoftReference<HashMap<String, String>> mRefMd5PathMap = null;
    private SoftReference<HashSet<String>> mRefKeyList = null;

    ArrayList<String> mExternalStoragePaths = null;

    public void clearSubDirMap() {
        synchronized (this) {
            if (mRefMd5PathMap != null && mRefMd5PathMap.get() != null) {
                mRefMd5PathMap.get().clear();
            }
            if (mRefKeyList != null && mRefKeyList.get() != null) {
                mRefKeyList.get().clear();
            }
        }
    }

    protected MD5PathConvert() {
        mRefMd5PathMap = new SoftReference<>(new HashMap<String, String>());
        mRefKeyList = new SoftReference<>(new HashSet<String>());

        // 外部设备路径
        mExternalStoragePaths = (new StorageList())
                .getMountedVolumePaths();
        if (mExternalStoragePaths == null || mExternalStoragePaths.isEmpty()) {
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                return;
            }
            mExternalStoragePaths = new ArrayList<>();
            mExternalStoragePaths.add( Environment.getExternalStorageDirectory().getPath() );
        }
    }
    static private MD5PathConvert mInstance = new MD5PathConvert();
    public static MD5PathConvert getInstance(){
        return mInstance;
    }

    /**
     * 将sd卡目录下的数据存放在map中
     * @param filePath
     * @param filePathMd5
     * @param md5PathMap
     * @param keyList
     * @return
     */
    private  HashMap<String, String> getSubDirMap(String filePath, String filePathMd5, HashMap<String, String> md5PathMap, HashSet<String> keyList ) {
        synchronized (this) {
            if ( keyList.contains(filePath) ) {
                return md5PathMap;
            }
            keyList.add( filePath );
            md5PathMap.put(filePathMd5, filePath);
        }

        for ( String strRootPath : mExternalStoragePaths ) {
            String strAbsPath = strRootPath + filePath;
            PathOperFunc.FilesAndFoldersStringList subFileStringList = EnableCacheListDir.listDir(strAbsPath);
            if (subFileStringList == null) {
                continue;
            }
            PathOperFunc.StringList subfolderNames = subFileStringList.getFolderNameList();
            if (subfolderNames == null) {
                subFileStringList.release();
                continue;
            }
            int folderNum = subfolderNames.size();

            for (int i = 0; i < folderNum; ++i) {
                File subFile = new File(strAbsPath, subfolderNames.get(i));
                String dirPath = subFile.getAbsolutePath();
                String subDirPathMd5 = filePathMd5;
                if (filePathMd5.length() > 0) {
                    subDirPathMd5 += "+";
                }
                subDirPathMd5 += Md5Util.getFilePathMd5(subFile.getName());
                dirPath = dirPath.substring(strRootPath.length(), dirPath.length());
                synchronized (this) {
                    md5PathMap.put(subDirPathMd5, dirPath);
                }
            }
            subfolderNames.release();
            subFileStringList.release();
        }
        return md5PathMap;
    }

    /***
     * 根据MD5值获取当前路径
     *
     * @return null 代表SD卡不存在该路径（无法枚举出来），正常路径已'/'开头
     */
    public  String getFilePathByMd5(String pathMd5Str) {
        if (pathMd5Str == null || pathMd5Str.trim().length() == 0 || mExternalStoragePaths == null ) {
            return null;
        }
        String dir;
        if ( mRefMd5PathMap.get() == null || mRefKeyList.get() == null ) {
            mRefMd5PathMap = new SoftReference<>(new HashMap<String, String>());
            mRefKeyList = new SoftReference<>(new HashSet<String>());
        }
        HashMap<String, String> md5PathMap = mRefMd5PathMap.get();
        HashSet<String> keyList = mRefKeyList.get();
        if (md5PathMap == null){
            md5PathMap = new HashMap<>();
        }
        if (keyList == null){
            keyList = new HashSet<>();
        }

        synchronized (this) {
            if (md5PathMap != null){
                dir = md5PathMap.get(pathMd5Str);
                if ( !TextUtils.isEmpty(dir) ) {
                    NLog.d(TAG, "直接从内存中取路径: "+ dir);
                    return dir;
                }
            }
        }
        String[] pathMd5Array = pathMd5Str.split("\\+");
        dir = "/";
        String dirMd5 = "";
        int length = pathMd5Array.length - 1;

        String findPathMd5 = "";
        for (int i = 0; i <= length; i++) {
            if (!TextUtils.isEmpty(pathMd5Array[i])) {
                if (i == 0) {
                    findPathMd5 = pathMd5Array[i];
                } else {
                    findPathMd5 = findPathMd5 + "+" + pathMd5Array[i];
                }
                dir = getSubDirMap(dir, dirMd5,md5PathMap,keyList).get(findPathMd5);
                dirMd5 = findPathMd5;
                // 不存在 直接跳出循环
                if (TextUtils.isEmpty(dir)) {
                    return null;
                } else // 存在表示有戏 判断是否是完整目录匹配
                {
                    if (i == length) {
                        break;
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(dir)){
            NLog.d(TAG, "遍历sd卡查找到了: "+ dir);
        }
        return dir;
    }
}

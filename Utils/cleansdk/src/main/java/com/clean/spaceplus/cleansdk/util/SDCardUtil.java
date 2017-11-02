package com.clean.spaceplus.cleansdk.util;

import android.os.Environment;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/5 14:22
 * @copyright TCL-MIG
 */
public class SDCardUtil {
    public static boolean isHaveSDCard(){
        String sdState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(sdState);
    }


    public static File getMountedThe2ndSdCardRootDir() {

        List<String> removalbeSdCardList = new StorageList().getMountedVolumePaths();
        if (null == removalbeSdCardList || removalbeSdCardList.isEmpty()) {
            return null;
        }

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return null;
        }

        File defaultSdCard = Environment.getExternalStorageDirectory();
        if (null == defaultSdCard) {
            return null;
        }

        String defaultSdCardPath = FileUtils.addSlash(defaultSdCard.getPath());
        for (String path : removalbeSdCardList) {
            path = FileUtils.addSlash(path);
            if (isTheSameCard(defaultSdCardPath, path)) {
                continue;
            }
            return new File(path);
        }
        return null;
    }


    private static boolean isTheSameCard(String defaultSdCardPath, String path) {
        assert(!TextUtils.isEmpty(defaultSdCardPath));
        assert(!TextUtils.isEmpty(path));
        if (path.equals(defaultSdCardPath)) {
            return true;
        }

        String[] defSubNames = getDefSubNames(defaultSdCardPath);
        if (null == defSubNames || defSubNames.length == 0) {
            return true;
        }

        PathOperFunc.StringList filesList = EnableCacheListDir.listDir(path);
        if ( filesList == null ) {
            return true;
        }

        if ( filesList.size() == defSubNames.length ) {
            String[] rst = new String[filesList.size()];
            for (int i = 0; i < filesList.size(); ++i) {
                rst[i] = filesList.get(i);
            }
            if ( Arrays.equals(rst, defSubNames) ) {
                filesList.release();
                filesList = null;
                return true;
            }
        }
        filesList.release();
        filesList = null;
        return false;
    }

    private static SoftReference<String[]> mDefSubNames = null;
    private static String[] getDefSubNames(String defaultSdCardPath) {

        String[] rst = null;
        if (null != mDefSubNames) {
            rst = mDefSubNames.get();
        }

        if (null != rst) {
            return rst;
        }

        PathOperFunc.StringList defSubs = EnableCacheListDir.listDir(defaultSdCardPath);
        if (null == defSubs) {
            return null;
        }

        try {
            int size = defSubs.size();
            if (size == 0) {
                return null;
            }

            rst = new String[size];
            for (int i = 0; i < size; ++i) {
                rst[i] = defSubs.get(i);
            }
        } finally {
            defSubs.release();
        }

        mDefSubNames = new SoftReference<>(rst);
        return rst;
    }


    public static File[] GetAllFolderOnSdcard( String sdcardRootDir) {
        // 得到设备的SDcard目录
        File[] arrayOfFile = null;
        PathOperFunc.FilesAndFoldersStringList fileAndFolderList = EnableCacheListDir.listDir( sdcardRootDir );
        if (fileAndFolderList == null) {
            return null;
        }
        PathOperFunc.StringList folderList = fileAndFolderList.getFolderNameList();
        if ( folderList != null ) {
            arrayOfFile = new File[folderList.size()];
            for (int i = 0; i < folderList.size(); i++) {
                arrayOfFile[i] = new File(sdcardRootDir, folderList.get(i));
            }
            folderList.release();
            folderList = null;
        }
        fileAndFolderList.release();
        fileAndFolderList = null;
        return arrayOfFile;
    }
}

package com.clean.spaceplus.cleansdk.appmgr.util;

import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.io.IOException;

/**
 * @author wangtianbao
 * @Description: 文件计算工具类
 * @date 2016/4/23 11:17
 * @copyright TCL-MIG
 */
public class FileComputeUtils {

    /**
     * 计算目录大小
     * @param directory
     * @return
     */
    public static long sizeOfDirectory(File directory) {
            checkDirectory(directory);
        File[] files = directory.listFiles();
        if(files == null) {
            return 0L;
        } else {
            long size = 0L;
            File[] arr$ = files;
            int len$ = files.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                File file = arr$[i$];

                try {
                    if(!isSymlink(file)) {
                        size += sizeOf(file);
                        if(size < 0L) {
                            break;
                        }
                    }
                } catch (Exception var9) {
                }
            }

            return size;
        }
    }

    /**
     * 检查目录存在
     * @param directory
     */
    private static void checkDirectory(File directory) throws IllegalArgumentException{
        if(!directory.exists()) {
            NLog.e(TAG,"错误："+directory + " 不存在");
        } else if(!directory.isDirectory()) {
            NLog.e(TAG,"错误："+directory + " 不是一个目录");
        }
    }

    private static final String TAG=FileComputeUtils.class.getSimpleName();
    /**
     * 文件大小
     * @param file
     * @return
     */
    public static long sizeOf(File file) {
        if(!file.exists()) {
            String message = file + " does not exist";
            NLog.e(TAG,message);
            return 0;
        } else {
            return file.isDirectory()?sizeOfDirectory(file):file.length();
        }
    }

    /**
     * 是否为符号链接
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean isSymlink(File file) throws IOException {
        if (isSystemWindows()) {
            return false;
        } else {
            File fileInCanonicalDir = null;
            if(file.getParent() == null) {
                fileInCanonicalDir = file;
            } else {
                File canonicalDir = file.getParentFile().getCanonicalFile();
                fileInCanonicalDir = new File(canonicalDir, file.getName());
            }

            return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile());
        }
    }

    /**
     * 是否为windows系统
     * @return
     */
    private static boolean isSystemWindows(){
        return File.separatorChar== 92;
    }
}

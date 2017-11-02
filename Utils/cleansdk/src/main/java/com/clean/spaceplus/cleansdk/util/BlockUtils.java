package com.clean.spaceplus.cleansdk.util;

import android.os.Build;
import android.os.StatFs;

import java.lang.reflect.Method;

/**
 * @author liangni
 * @Description:
 * @date 2016/5/18 19:26
 * @copyright TCL-MIG
 */
public class BlockUtils {

    public static long getAvailableBlocks(StatFs fsStat) {
        assert(null != fsStat);

        if (Build.VERSION.SDK_INT < 18) {
            return fsStat.getAvailableBlocks();
        }

        Method getAvailableBlocksLongMethod = null;
        try {
            getAvailableBlocksLongMethod = fsStat.getClass().getMethod("getAvailableBlocksLong");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (null == getAvailableBlocksLongMethod) {
            return fsStat.getAvailableBlocks();
        }

        try {
            return (Long)getAvailableBlocksLongMethod.invoke(fsStat);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fsStat.getAvailableBlocks();
    }

    public static long getBlockCount(StatFs fsStat) {
        assert(null != fsStat);

        if (Build.VERSION.SDK_INT < 18) {
            return fsStat.getBlockCount();
        }

        Method getBlockCountLongMethod = null;
        try {
            getBlockCountLongMethod = fsStat.getClass().getMethod("getBlockCountLong");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (null == getBlockCountLongMethod) {
            return fsStat.getBlockCount();
        }

        try {
            return (Long)getBlockCountLongMethod.invoke(fsStat);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fsStat.getBlockCount();
    }

    public static long getBlockSize(StatFs fsStat) {
        assert(null != fsStat);

        if (Build.VERSION.SDK_INT < 18) {
            return fsStat.getBlockSize();
        }

        Method getBlockSizeLongMethod = null;
        try {
            getBlockSizeLongMethod = fsStat.getClass().getMethod("getBlockSizeLong");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (null == getBlockSizeLongMethod) {
            return fsStat.getBlockSize();
        }

        try {
            return (Long)getBlockSizeLongMethod.invoke(fsStat);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fsStat.getBlockSize();
    }
}

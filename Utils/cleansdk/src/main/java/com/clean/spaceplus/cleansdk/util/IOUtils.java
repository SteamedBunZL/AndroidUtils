package com.clean.spaceplus.cleansdk.util;

import android.database.Cursor;

import com.hawkclean.framework.log.NLog;

import java.io.Closeable;
import java.util.zip.ZipFile;

/**
 * @author zengtao.kuang
 * @Description: IO工具类
 * @date 2016/4/6 13:49
 * @copyright TCL-MIG
 */
public class IOUtils {
    public static void closeSilently(Closeable closeable) {
        if (closeable == null)
            return;
        try {
            closeable.close();
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
    }

    public static void closeSilently(ZipFile zipFile) {
        if (zipFile == null)
            return;
        try {
            zipFile.close();
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
    }

    public static void closeSilently(Cursor cursor) {
        if (cursor == null)
            return;
        try {
            cursor.close();
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
    }
}

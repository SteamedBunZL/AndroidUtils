package com.tcl.security.virusengine.utils;

import android.os.Environment;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Steve on 16/10/21.
 */

public class FileUtil {

    public static void cloaseQuietly(Closeable closeable){
        if (closeable!=null){
            try {
                closeable.close();
            }catch (RuntimeException rethrown){
                throw rethrown;
            }catch (IOException e) {
                VirusLog.printException(e,"FileUtil.closeQuietly");
            }catch (Exception e){
                VirusLog.printException(e,"FileUtil.closeQuietly");
            }
        }
    }

    // 判断SD卡是否被挂载
    public static boolean isSDCardMounted() {
        // return Environment.getExternalStorageState().equals("mounted");
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    // 获取SD卡的根目录
    public static String getSDCardBaseDir() {
        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }
}

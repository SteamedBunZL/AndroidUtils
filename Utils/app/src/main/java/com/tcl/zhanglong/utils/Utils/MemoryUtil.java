package com.tcl.zhanglong.utils.Utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;

/**
 * Created by Steve on 16/10/27.
 */

public class MemoryUtil {

    @SuppressWarnings("unchecked")
    static <T> T getService(Context context, String service) {
        return (T) context.getSystemService(service);
    }


    public static long getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue();// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return initial_memory;// Byte转换为KB或者MB，内存大小规格化
    }

    public static long getAvailMemory(Context context){
        // 获取android当前可用内存大小
        long availMem = 0;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            availMem = mi.availMem/1024;
            Log.e("VirusLog","availMem is " + availMem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return availMem;
    }

    /**
     * 获取内存大小 如果使用了largeHeap属性获取largeMemory 单位为bytes
     * @param context
     * @return
     */
    public static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = getService(context, ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();
        if (largeHeap && SDK_INT >= HONEYCOMB) {
            memoryClass = getLargeMemoryClass(am);
        }
        return (int)(1024L * 1024L * memoryClass);
    }

    /**
     * 获取LargeMemory
     * @param activityManager
     * @return
     */
    @TargetApi(HONEYCOMB)
    public static int getLargeMemoryClass(ActivityManager activityManager) {
            return activityManager.getLargeMemoryClass();
        }

}

package com.clean.spaceplus.cleansdk.base.utils.system;

import android.text.TextUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * @author dongdong.huang
 * @Description: 获取设备参数
 * @date 2016/5/6 14:47
 * @copyright TCL-MIG
 */
public class DeviceUtil {
    private static int mCpuNum = 0;

    public static int getCpuNum() {

        if (mCpuNum > 0) {
            return mCpuNum;
        }

        File cpuDev = new File("/sys/devices/system/cpu");
        if (!cpuDev.exists() || !cpuDev.isDirectory()) {
            mCpuNum = 1;
            return mCpuNum;
        }

        String[] cpuInfo = cpuDev.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {

                if (TextUtils.isEmpty(filename)) {
                    return false;
                }

                return (Pattern.matches("cpu\\d+", filename));
            }
        });

        if (null == cpuInfo || 0 == cpuInfo.length) {
            mCpuNum = 1;
            return mCpuNum;
        }

        mCpuNum = cpuInfo.length;
        return mCpuNum;
    }
}

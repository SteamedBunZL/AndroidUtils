package com.clean.spaceplus.cleansdk.junk.cleancloud.util;

import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.utils.system.PackageManagerWrapper;
import com.clean.spaceplus.cleansdk.util.md5.Md5Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import space.network.util.compress.EnDeCodeUtils;
import space.network.util.hash.KQueryMd5Util;

/**
 * @author Jerry
 * @Description:
 * @date 2016/7/19 14:59
 * @copyright TCL-MIG
 */

public class DecryptHelper {

    private Map<Long, String> mDict = new HashMap<Long, String>();
    private static DecryptHelper sInstance = new DecryptHelper();

    public static DecryptHelper getInstance() {
        return sInstance;
    }

    private DecryptHelper() {

    }

    /**
     * 有些电话可能会禁止读取包信息
     * @return
     */
    public boolean hasReadPackageListPermission() {
        guard();
        return !mDict.isEmpty();
    }

    public void sync() {
        synchronized (this) {
            List<PackageInfo> xs = PackageManagerWrapper.getInstance().getPkgInfoList();
            if(xs != null) {
                for(PackageInfo x : xs) {
                    addPackage(x.packageName);
                }
            }
        }
    }

    public void clear() {
        synchronized (this) {
            mDict.clear();
        }
    }

    public boolean hasPackageInstalled(long packageNameMd5Half) {
        guard();
        return contains(packageNameMd5Half);
    }

    private boolean contains(long key) {
        return getPackageName(key) != null;
    }
    private void guard() {
        synchronized (this) {
            if (mDict.isEmpty()) {
                sync();
            }
        }
    }

    private void addPackage(String packageName) {
        if(TextUtils.isEmpty(packageName)) {
            return;
        }

        String md5HexStr = Md5Util.getStringMd5(packageName);
        byte[] md5byteArr = EnDeCodeUtils.hexStringtoBytes(md5HexStr);
        long md5Half = KQueryMd5Util.getMD5High64BitFromMD5(md5byteArr);
        if(md5Half != 0) {
            synchronized (this) {
                mDict.put(md5Half, packageName);
            }
        }
    }

    public String getPackageName(long packageNameMd5Half) {
        String value = null;
        synchronized (this) {
            value = mDict.get(packageNameMd5Half);
        }
        return value;
    }
}

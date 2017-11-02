package com.clean.spaceplus.cleansdk.util;

import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

import space.network.cleancloud.KCacheCloudQuery;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/26 15:14
 * @copyright TCL-MIG
 */
public class CachePathUtil {
    public static final class CachePathData {
        /**1、2部分*/
        public String[] mMd5Ids;
        /**path或者正则，可能为空*/
        public List<String> mMd5Ids2;
        /**正则片段，可能为空*/
        public List<String> mMd5Ids3;
        /**mMd5Ids2+mMd5Ids3*/
        public String mRemain;
    }

    public final static int PATH_PARSE_MAX_ID_COUNT = 2;

    public static CachePathData parseHighFreqDbDirString(String str, int pathType) {
        if (TextUtils.isEmpty(str))
            return null;

        CachePathData result = new CachePathData();
        String[] ids = new String[PATH_PARSE_MAX_ID_COUNT];
        result.mMd5Ids = ids;
        int columnCount = 0;
        StringBuilder sb = new StringBuilder();
        String remain = null;
        char c;
        int len = str.length();
        boolean bContinue = true;
        for (int i = 0; i < len && bContinue; ++i) {
            c = str.charAt(i);
            switch (c) {
                case '+':
                    ids[columnCount] = sb.toString();
                    sb.delete(0, sb.length());
                    ++columnCount;
                    if (columnCount >= PATH_PARSE_MAX_ID_COUNT) {
                        bContinue = false;
                        remain = str.substring(i);
                    }
                    break;
                case ',':
                    if (pathType != KCacheCloudQuery.CachePathType.FILE_2 && pathType != KCacheCloudQuery.CachePathType.FILE_REG_2) {
                        sb.append(c);
                        break;
                    }
                case '/':
                    // 遇到正则式
                    bContinue = false;
                    remain = str.substring(i);
                    ids[columnCount] = sb.toString();
                    sb.delete(0, sb.length());
                    ++columnCount;
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        if (bContinue) {
            ids[columnCount] = sb.toString();
            sb.delete(0, sb.length());
            ++columnCount;
        }

        result.mRemain = remain;
        return result;
    }

    public static CachePathData getOtherMD5IDs(CachePathData cachePathData) {
        if (cachePathData == null || TextUtils.isEmpty(cachePathData.mRemain)) {
            return cachePathData;
        }
        char c;
        final String str = cachePathData.mRemain;
        int len = str.length();
        boolean third = false;
        StringBuilder sb = new StringBuilder();
        cachePathData.mMd5Ids2 = new LinkedList<>();
        cachePathData.mMd5Ids2.add(str.substring(0, 1));
        for (int i = 1; i < len; ++i) {
            c = str.charAt(i);
            switch (c) {
                case '+':
                    if (third) {
                        cachePathData.mMd5Ids3.add(sb.toString());
                    } else {
                        cachePathData.mMd5Ids2.add(sb.toString());
                    }
                    sb.delete(0, sb.length());
                    break;
                case ',':
                    cachePathData.mMd5Ids2.add(sb.toString());
                    sb.delete(0, sb.length());
                    third = true;
                    if (cachePathData.mMd5Ids3 == null) {
                        cachePathData.mMd5Ids3 = new LinkedList<String>();
                    }
                    cachePathData.mMd5Ids3.add(",");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        if (sb.length() > 0) {
            if (third) {
                cachePathData.mMd5Ids3.add(sb.toString());
            } else {
                cachePathData.mMd5Ids2.add(sb.toString());
            }
            sb.delete(0, sb.length());
        }
        cachePathData.mRemain = null;
        return cachePathData;
    }
}

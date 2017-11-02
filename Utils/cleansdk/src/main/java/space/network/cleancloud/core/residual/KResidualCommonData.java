//=============================================================================
/**
 * @file KResidualCommonData.java
 * @brief
 */
//=============================================================================
package space.network.cleancloud.core.residual;

import java.util.ArrayList;

import space.network.cleancloud.KResidualCloudQuery;

public class KResidualCommonData {

    public static class DirQueryInnerData {
        public String mOriginalKey;  // 查询关键字原文;
        public String mLocalQueryKey;
        public String mDirNameMd5;
        public boolean misDetect;///< 是否检出,由于这个版本有部分特征不开放给上层,只用于测试特征,所以内部也加个检出标记
        public String mSuffixInfo;  // 从云端获取到的原始后缀名过滤信息

        public ArrayList<String> mOriFilterSubDirs;
        public ArrayList<KResidualCloudQuery.FilterDirData> mFilterSubDirDatas;

        @Override
        public String toString() {
            return "DirQueryInnerData{" +
                    "mOriginalKey='" + mOriginalKey + '\'' +
                    ", mLocalQueryKey='" + mLocalQueryKey + '\'' +
                    ", mDirNameMd5='" + mDirNameMd5 + '\'' +
                    ", misDetect=" + misDetect +
                    ", mSuffixInfo='" + mSuffixInfo + '\'' +
                    ", mOriFilterSubDirs=" + mOriFilterSubDirs +
                    ", mFilterSubDirDatas=" + mFilterSubDirDatas +
                    '}';
        }
    }

    public static class PkgQueryInnerData {
        public String mPkgNameMd5;         // pkg name的MD5.  暂时保留，因为暂时缓存库的格式还没有与高频库保持一致。
        public long mPkgNameMd5High64Bit;  // pkg name的MD5高64位。
    }
}
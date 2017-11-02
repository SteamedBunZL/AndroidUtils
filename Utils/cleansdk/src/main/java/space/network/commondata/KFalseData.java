//=============================================================================
/**
 * @file KSignFalseData.java
 * @brief
 */
//=============================================================================
package space.network.commondata;

import java.util.Arrays;

public class KFalseData {

    public static class SignIdData {
        public int mVersion;
        public int mCacheLifeTime;
        public int[] mFalseIds;


        @Override
        public String toString() {
            return "SignIdData{" +
                    "mVersion=" + mVersion +
                    ", mCacheLifeTime=" + mCacheLifeTime +
                    ", mFalseIds=" + Arrays.toString(mFalseIds) +
                    '}';
        }
    }

    public static class PkgQueryFalseData {
        public SignIdData mFalseDirIdData;///< 目录特征的误报id
        public SignIdData mFalsePkgIdData;///< 普通包特征误报id
        public SignIdData mFalseRegexPkgIdData;///< 正则包特征误报id
    }
    public static class CachePkgQueryFalseData {
        public SignIdData mFalsePkgIdData;///< 普通包特征误报id
    }
}
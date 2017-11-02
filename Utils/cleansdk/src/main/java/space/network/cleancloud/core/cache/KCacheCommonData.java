package space.network.cleancloud.core.cache;


import java.util.Collection;

import space.network.cleancloud.KCacheCloudQuery;

public class KCacheCommonData {

    public static class CachePkgQueryInnerData {

        public String mPkgNameMd5;
        public long mPkgNameMd5High64Bit;


        // temp data set
        public Collection<KCacheCloudQuery.PkgQueryPathItem> mPkgQueryPathItems;
        // end temp data set
        public boolean mIsCallback = false;




        @Override
        public String toString() {
            return "CachePkgQueryInnerData{" +
                    "mPkgNameMd5='" + mPkgNameMd5 + '\'' +
                    ", mPkgQueryPathItems=" + mPkgQueryPathItems +
                    ", mIsCallback=" + mIsCallback +
                    ", mPkgNameMd5High64Bit=" + mPkgNameMd5High64Bit +
                    '}';
        }
    }

    public static class SysCacheFlagQueryInnerData {
        public String mPkgNameMd5;
        public long mPkgNameMd5High64Bit;
    }
}

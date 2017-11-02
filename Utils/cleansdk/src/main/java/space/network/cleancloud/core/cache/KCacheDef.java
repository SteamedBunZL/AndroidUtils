/**
 *
 */
package space.network.cleancloud.core.cache;


import space.network.commondata.KCleanCloudEnv;

public class KCacheDef {
    //public static final String PKGCACHE_HIGH_FREQ_DBNAME = "pkgcache_hf.db";
    //public static final String PKGCACHE_SHOWINFO_HIGH_FREQ_DBNAME = "pkgcache_showinfo_hf.db";
    //public static final String PKGCACHE_CACHE_DBNAME = "pkgcache_cache.db";

    private static final String PKGCACHE_HIGH_FREQ_DBNAME = "cache_hf_cn_1.0.0.db";
    private static final String PKGCACHE_HIGH_FREQ_DBNAME_ABROAD = "cache_hf_en_1.0.0.db";

    //有一次把缓存库的表格式改出问题,后面用清表的方式修复,担心还是这个方式不能完全修复
    //所以把缓存库改名用新库,原来的库名是pkgcache_cache.
    public static final String PKGCACHE_CACHE_DBNAME = "cache.db";

    public static final String PKG_DATA_VER_NAME 	 = "pkgquery";


    //private static final String CACHE_NET_SERVICE_NAME = "/cps";
    private static final String CACHE_NET_SERVICE_NAME = "/cpsn";
    private static final String SHOWINFO_NET_SERVICE_NAME = "/cpd";

    public static final String[] CACHE_QUERY_URLS = {
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_HOST + CACHE_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_1 + CACHE_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_2 + CACHE_NET_SERVICE_NAME
    };

    public static final String[] CACHE_QUERY_URLS_TEST = {
            "http://cleanportal-test.tclclouds.com/packageRefer/cache",
            "http://cleanportal-test.tclclouds.com/packageRefer/cache",
            "http://cleanportal-test.tclclouds.com/packageRefer/cache"
    };

  /*  public static final String[] CACHE_QUERY_URLS = {
            "http://10.128.208.199:8080/cleanportal-server/packageRefer/cache",
            "http://10.128.208.199:8080/cleanportal-server/packageRefer/cache",
            "http://10.128.208.199:8080/cleanportal-server/packageRefer/cache"
    };*/


    public static final String[] CACHE_ABROAD_QUERY_URLS = {
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_HOST_ABROAD + CACHE_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_ABROAD_1 + CACHE_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_ABROAD_2 + CACHE_NET_SERVICE_NAME,
    };

    public static final String[] SHOWINFO_QUERY_URLS = {
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_HOST + SHOWINFO_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_1 + SHOWINFO_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_2 + SHOWINFO_NET_SERVICE_NAME
    };

    public static final String[] SHOWINFO_ABROAD_QUERY_URLS = {
            //KCleanCloudEnv.URL_HEAD + "54.183.152.88" + SHOWINFO_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_HOST_ABROAD + SHOWINFO_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_ABROAD_1 + SHOWINFO_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_ABROAD_2 + SHOWINFO_NET_SERVICE_NAME,
    };

    public static final String CLEAN_CACHE_ID_FILTER_NAME = "cc_c";

    public static String getHighFregDbName() {
        return PKGCACHE_HIGH_FREQ_DBNAME_ABROAD ;
    }

    public static final String H = "ucan't";
}

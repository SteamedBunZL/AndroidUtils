//=============================================================================
/**
 * @file KResidualDef.java
 * @brief
 */
//=============================================================================
package space.network.cleancloud.core.residual;


import space.network.commondata.KCleanCloudEnv;

public class KResidualDef {

    ///////////////////////////////////////////////////////////////////////
    //about db
    //public static final String RESIDUAL_CACHE_DBNAME = "residual_cache.db";
    //public static final String RESIDUAL_HIGH_FREQUENT_DBNAME = "residual_hfreq.db";


    public static final String RESIDUAL_TBL_NAME = "dirquery";
    public static final String RESIDUAL_LANG_TBL_NAME = "langquery";

    public static final String RESIDUAL_VER_TBL_NAME = "version";
    public static final String RESIDUAL_DATA_VER_TBL_NAME = "data_versions";

    public static final String DIR_DATA_VER_NAME 		= "dirquery";
    public static final String PKG_DATA_VER_NAME 		= "pkgquery";
    public static final String REGEX_PKG_DATA_VER_NAME 	= "repkgquery";

    private static final String PKG_QUERY_HIGH_FREQ_DBNAME = "query_hf_cn_1.0.0.db";
    private static final String PKG_QUERY_HIGH_FREQ_DBNAME_ABROAD = "query_hf_en_1.0.0.db";

    //有一次把缓存库的表格式改出问题,后面用清表的方式修复,担心还是这个方式不能完全修复
    //所以把缓存库改名用新库,原来的库名是residual_dircache.db residual_pkgcache.db
    public static final String RESIDUAL_DIR_CACHE_DBNAME = "leftover_cache.db";
    public static final String RESIDUAL_PKG_CACHE_DBNAME = "leftover_pkg2_cache.db";

    private static final String PKG_NET_SERVICE_NAME = "/aps";
    private static final String DIR_NET_SERVICE_NAME = "/adsn";

    public static final String W = "beinga";

    public static final String[] PKG_QUERY_URLS = {
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_HOST + PKG_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_1 + PKG_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_2 + PKG_NET_SERVICE_NAME
    };


    public static final String[] PKG_ABROAD_QUERY_URLS = {
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_HOST_ABROAD + PKG_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_ABROAD_1 + PKG_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_ABROAD_2 + PKG_NET_SERVICE_NAME
    };

    public static final String[] DIR_QUERY_URLS = {
            //KCleanCloudEnv.URL_HEAD + "54.183.152.88" + DIR_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_HOST + DIR_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_1 + DIR_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_2 + DIR_NET_SERVICE_NAME
    };
    public static final String[] DIR_ABROAD_QUERY_URLS = {
            //KCleanCloudEnv.URL_HEAD + "54.183.152.88" + DIR_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_HOST_ABROAD + DIR_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_ABROAD_1 + DIR_NET_SERVICE_NAME,
            KCleanCloudEnv.URL_HEAD + KCleanCloudEnv.CLEAN_CLOUD_IP_ABROAD_2 + DIR_NET_SERVICE_NAME
    };

}
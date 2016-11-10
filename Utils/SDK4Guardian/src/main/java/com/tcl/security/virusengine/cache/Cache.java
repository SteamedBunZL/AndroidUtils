package com.tcl.security.virusengine.cache;

import com.tcl.security.virusengine.Constants;

/**
 * Created by Steve on 2016/4/29.
 */
public interface Cache {

    Cache.CacheEntry get(String key);

    Object put(String key, Cache.CacheEntry entry);

    Object remove(String key);

//    public void init();

    /**
     * Cache entry
     */
    class CacheEntry{

        /**Pacakgename of app*/
        public String packageName;

        /**Cachekey*/
        public String cacheKey;

        /**Appname of the app*/
        public String appName;

        /**Version of the application*/
        public String applicationVersion;

        /**Version of the viruslib*/
        public String virusLibVersion;

        /**ScanState {@link com.intel.security.vsm.ScanResult}*/
        public int scanState;

        /**Time to last update*/
        public String ttl;

        /**Virusname*/
        public String virusName;

        /**Description of Virus*/
        public String virusDescription;

        /**Default 0 present from local  1 present from McafeeCloud  2 present from TCLCloud*/
        public int from;

        public int typeInt = Constants.ScanInfo.DEFAULT_VIRUS_TYPE;

        public String type;

        public int risk_level = Constants.ScanInfo.NO_RISK;

        public String tclHash;

        public String suggest;

        public String description_ids;

        public int versionCode;

        public int tcl_cloud_result = Constants.CLOUD_RESULT_ERROR;

        public int avengine_cloud_result = Constants.CLOUD_RESULT_ERROR;

        /**云端下发的cache时间*/
        public String cloud_cache_time = Constants.DEFAULT_SCAN_RESULT_CACHE_TIME;

        //APK路径  2016.10.13 数据上报新增字段 临时缓存 不存数据库
        public String publicSourceDir;


    }

}

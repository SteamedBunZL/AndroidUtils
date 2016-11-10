package com.tcl.security.virusengine.cache;

/**
 * The Interface to handle cache hit.
 * Created by Steve on 2016/4/29.
 */
public interface CacheHandle {

    void init();
    //obj[0] 当前应用版本号  obj[1] 当前病毒库版本号  obj[2] 缓存有效时间 obj[3] tcl云查结果  obj[4] avengine 云查结果
    Cache.CacheEntry performHandleCache(String key, Object... args);

    void put(String key, Cache.CacheEntry entry);

    void clearMemoryCache();

}

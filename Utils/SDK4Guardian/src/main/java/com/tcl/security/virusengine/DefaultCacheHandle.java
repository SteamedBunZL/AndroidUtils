package com.tcl.security.virusengine;

import com.tcl.security.virusengine.cache.Cache;
import com.tcl.security.virusengine.cache.CacheHandle;
import com.tcl.security.virusengine.cache.database.DatabaseCache;
import com.tcl.security.virusengine.cache.filter.CacheFilter;
import com.tcl.security.virusengine.cache.filter.MultipleFilters;
import com.tcl.security.virusengine.cache.memory.BaseMemoryCache;

import java.util.List;

/**
 * Created by Steve on 2016/5/2.
 */
public class DefaultCacheHandle implements CacheHandle{

    private final DatabaseCache mDatabaseCache;

    private final BaseMemoryCache mBaseMemoryCache;

    private final CacheFilter mCacheFilters;

    public DefaultCacheHandle(BaseMemoryCache baseMemoryCache,DatabaseCache databaseCache){
        this.mDatabaseCache = databaseCache;
        this.mBaseMemoryCache = baseMemoryCache;
        mCacheFilters = new MultipleFilters();
    }

    @Override
    public void init() {
        //缓存拉取
        dragCache();
    }

    @Override
    public Cache.CacheEntry performHandleCache(String key, Object... args) {
        Cache.CacheEntry entry;
        entry = mBaseMemoryCache.get(key);

        if (entry==null){
            entry = mDatabaseCache.get(key);

            if (entry!=null&&!mCacheFilters.apply(entry,args)) {
                mBaseMemoryCache.put(key, entry);
                return entry;
            }
        }


        if (entry!=null&&mCacheFilters.apply(entry,args))
            entry = null;

        return entry;
    }

    @Override
    public void put(String key, Cache.CacheEntry entry) {
        mBaseMemoryCache.put(key,entry);
        mDatabaseCache.put(key,entry);
    }

    @Override
    public void clearMemoryCache(){
        mBaseMemoryCache.clear();
    }

    private void dragCache(){
        //获取数据库中的数据
        List<Cache.CacheEntry> list = mDatabaseCache.getAll();

        if ((list != null && !list.isEmpty())) {
            for(Cache.CacheEntry entry:list){
                mBaseMemoryCache.put(entry.packageName,entry);
            }
        }
    }

}

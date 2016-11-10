package com.tcl.security.virusengine.cache.memory.impl;


import android.support.v4.util.LruCache;

import com.tcl.security.virusengine.cache.memory.BaseMemoryCache;

/**
 * MemeryCache by {@link LruCache}
 *
 * Created by Steve on 2016/5/2.
 */
public class ScanMemoryCacheImpl implements BaseMemoryCache {

    private LruCache<String,CacheEntry> mLruCache;

    public ScanMemoryCacheImpl(){
        init();
    }

    private void init() {

        int maxMemory = (int)(Runtime.getRuntime().maxMemory()/ 1024);
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String,CacheEntry>(cacheSize){
            @Override
            protected int sizeOf(String key, CacheEntry value) {
                return 1;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, CacheEntry oldValue, CacheEntry newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (evicted)
                    oldValue = null;
            }
        };
    }

    @Override
    public CacheEntry get(String key) {
        return mLruCache.get(key);
    }

    @Override
    public CacheEntry put(String key, CacheEntry entry) {
        return mLruCache.put(key,entry);
    }

    @Override
    public CacheEntry remove(String key) {
        return mLruCache.remove(key);
    }

    @Override
    public void clear() {
        mLruCache.evictAll();
    }
}

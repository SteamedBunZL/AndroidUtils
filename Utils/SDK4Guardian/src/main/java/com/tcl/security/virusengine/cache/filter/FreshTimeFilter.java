package com.tcl.security.virusengine.cache.filter;

import android.support.annotation.NonNull;

import com.tcl.security.virusengine.cache.Cache;

/**
 * Created by Steve on 2016/5/10.
 */
public class FreshTimeFilter implements CacheFilter{
    @Override
    public boolean apply(@NonNull Cache.CacheEntry entry, Object... args) {
        if (entry==null)
            return true;
        long ttl = Long.valueOf(entry.ttl);
        long time = Long.valueOf(entry.cloud_cache_time);
        return (ttl + time) < System.currentTimeMillis();
    }
}

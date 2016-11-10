package com.tcl.security.virusengine.cache.filter;

import android.support.annotation.NonNull;

import com.tcl.security.virusengine.cache.Cache;

/**
 * Created by Steve on 2016/5/10.
 */
public interface CacheFilter {
    /**
     *  args[0] appVersion args[1] virsuVersion args[2] freshTime
     */
    boolean apply(@NonNull Cache.CacheEntry entry, Object... args);
}

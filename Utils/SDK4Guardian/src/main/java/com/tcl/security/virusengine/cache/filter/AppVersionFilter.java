package com.tcl.security.virusengine.cache.filter;

import android.support.annotation.NonNull;

import com.tcl.security.virusengine.cache.Cache;

/**
 * Created by Steve on 2016/5/10.
 */
public class AppVersionFilter implements CacheFilter{

    @Override
    public boolean apply(@NonNull Cache.CacheEntry entry, Object... args) {
        if (args[0]==null)
            return true;
        String oldVersion = entry.applicationVersion;
        String newVersion = (String) args[0];
        return oldVersion == null || !oldVersion.equals(newVersion);
    }
}

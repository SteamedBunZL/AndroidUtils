package com.tcl.security.virusengine.cache.filter;

import android.support.annotation.NonNull;

import com.tcl.security.virusengine.cache.Cache;
import com.tcl.security.virusengine.utils.VirusLog;

/**
 * Created by Steve on 2016/5/10.
 */
public class VirusLibVersionFilter implements CacheFilter{
    @Override
    public boolean apply(@NonNull Cache.CacheEntry entry, Object... args) {
        if (entry==null)
            return true;
        if (args[1]==null)
            return true;
        String oldVersion = entry.virusLibVersion;
        String newVersion = (String) args[1];
        VirusLog.w("oldVersion %s,newVersion %s",oldVersion,newVersion);
        return !oldVersion.equals(newVersion);
    }
}

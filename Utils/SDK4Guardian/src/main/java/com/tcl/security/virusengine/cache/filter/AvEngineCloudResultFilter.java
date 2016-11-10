package com.tcl.security.virusengine.cache.filter;

import android.support.annotation.NonNull;

import com.tcl.security.virusengine.Constants;
import com.tcl.security.virusengine.cache.Cache;

/**
 * Created by Steve on 2016/7/14.
 */
public class AvEngineCloudResultFilter implements CacheFilter{
    @Override
    public boolean apply(@NonNull Cache.CacheEntry entry, Object... args) {
        return entry.avengine_cloud_result == Constants.CLOUD_RESULT_ERROR;
    }
}

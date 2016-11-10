package com.tcl.security.virusengine.cache.filter;

import android.support.annotation.NonNull;

import com.tcl.security.virusengine.Constants;
import com.tcl.security.virusengine.cache.Cache;

/**
 * Created by Steve on 2016/7/14.
 */
public class TclCloudResultFilter implements CacheFilter{
    @Override
    public boolean apply(@NonNull Cache.CacheEntry entry, Object... args) {
        //这里tcl_cloud_result得是扫描完才有的数据，直接通过entry获取上次缓存,这个字段是防止缓存是无网络情况下扫描结果，不准确
        if (entry==null)
            return true;
        return entry.tcl_cloud_result == Constants.CLOUD_RESULT_ERROR || entry.tcl_cloud_result == Constants.CLOUD_RESULT_UNSPECIFIED;
    }
}

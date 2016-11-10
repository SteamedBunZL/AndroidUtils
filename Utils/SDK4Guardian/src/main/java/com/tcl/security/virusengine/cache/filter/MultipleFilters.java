package com.tcl.security.virusengine.cache.filter;

import android.support.annotation.NonNull;

import com.tcl.security.virusengine.cache.Cache;

/**
 * Created by Steve on 2016/7/14.
 */
public class MultipleFilters implements CacheFilter{

    private AppVersionFilter appVersionFilter;

    private FreshTimeFilter freshTimeFilter;

    private VirusLibVersionFilter virusLibVersionFilter;

    private AvEngineCloudResultFilter avEngineCloudResultFilter;

    private TclCloudResultFilter tclCloudResultFilter;


    public MultipleFilters(){
        appVersionFilter = new AppVersionFilter();
        freshTimeFilter = new FreshTimeFilter();
        virusLibVersionFilter = new VirusLibVersionFilter();
        avEngineCloudResultFilter = new AvEngineCloudResultFilter();
        tclCloudResultFilter = new TclCloudResultFilter();
    }

    @Override
    public boolean apply(@NonNull Cache.CacheEntry entry, Object... args) {
        if (appVersionFilter.apply(entry,args))
            return true;

        if (freshTimeFilter.apply(entry,args))
            return true;

        if (virusLibVersionFilter.apply(entry,args))
            return true;

        //1.我们的云是无效的，mcafee的云是无效的，缓存无效的
        //2.我们的云是无效的，mcafee的云是有效的，缓存有效
        //3.我们的云是有效的，缓存是有效的
        if (tclCloudResultFilter.apply(entry)){
            return avEngineCloudResultFilter.apply(entry);
        }else{
            return  false;
        }
    }
}

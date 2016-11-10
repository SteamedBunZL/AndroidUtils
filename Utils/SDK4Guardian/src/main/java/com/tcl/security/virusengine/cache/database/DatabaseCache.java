package com.tcl.security.virusengine.cache.database;

import com.tcl.security.virusengine.cache.Cache;

import java.util.List;

/**
 * Created by Steve on 2016/4/29.
 */
public interface DatabaseCache extends Cache{

    List<CacheEntry> getAll();
}

package com.tcl.security.virusengine.engine;

import android.content.Context;

import com.intel.security.vsm.ScanResult;
import com.intel.security.vsm.content.ScanSource;
import com.tcl.security.virusengine.Constants;
import com.tcl.security.virusengine.VirusScanQueue;
import com.tcl.security.virusengine.cache.Cache;
import com.tcl.security.virusengine.cache.CacheHandle;
import com.tcl.security.virusengine.entry.ScanEntity;
import com.tcl.security.virusengine.entry.ScanEntry;
import com.tcl.security.virusengine.entry.ScanInfo;
import com.tcl.security.virusengine.func_interface.ScanMessage;
import com.tcl.security.virusengine.func_interface.ScanResultDelivery;
import com.tcl.security.virusengine.utils.RiskUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Steve on 16/11/2.
 */

public class SpecialHandleForSpace {

    public void handleSpace(List<ScanEntity> entityList, final Map<String, ScanEntry> entryMap, ScanResultDelivery delivery, ScanMessage scanMessage, CacheHandle cacheHandle,boolean isTclCloudError){
        final List<ScanSource> sourceList = new LinkedList<>();
        for(ScanEntity entity:entityList){
            ScanSource scanSource = entity.scanSource;
            ScanEntry entry = entryMap.remove(scanSource.toString());
            Context context = VirusScanQueue.getInstance().getContext();
            String type = RiskUtils.threatTypeToString(Constants.ScanInfo.DEFAULT_VIRUS_TYPE);
            String suggest = RiskUtils.obtainSuggestByType(context,type);
            ScanInfo cleanInfo =  new ScanInfo(Constants.ScanInfo.FILE_TYPE_APK,scanSource.toString(), null, null, ScanResult.CATEGORY_CLEAN, entry.appName, type, Constants.ScanInfo.NO_RISK,suggest);
            delivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_ENTRY, cleanInfo, entry);
            cacheEntry(context,cacheHandle,scanMessage,scanSource,entry,null,isTclCloudError);
        }

    }


    public void cacheEntry(Context context, CacheHandle cacheHandle, ScanMessage scanMessage, ScanSource scanSource, ScanEntry entry,String description, boolean isTclCloudError) {
        if (cacheHandle != null){
            Cache.CacheEntry cache = CloudProcessHelper.getCacheEnttry(context,scanMessage,entry,description,isTclCloudError);
            cacheHandle.put(scanSource.toString(), cache);
        }
    }
}

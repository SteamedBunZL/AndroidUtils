package com.tcl.security.virusengine.engine;

import android.content.Context;
import android.text.TextUtils;

import com.intel.security.vsm.ScanResult;
import com.intel.security.vsm.Threat;
import com.intel.security.vsm.content.ScanSource;
import com.tcl.security.cloudengine.CloudResponse;
import com.tcl.security.virusengine.Constants;
import com.tcl.security.virusengine.VirusScanQueue;
import com.tcl.security.virusengine.cache.Cache;
import com.tcl.security.virusengine.cache.CacheHandle;
import com.tcl.security.virusengine.entry.ScanEntity;
import com.tcl.security.virusengine.entry.ScanEntry;
import com.tcl.security.virusengine.entry.ScanInfo;
import com.tcl.security.virusengine.func_interface.ScanMessage;
import com.tcl.security.virusengine.func_interface.ScanResultDelivery;
import com.tcl.security.virusengine.modle.ResponseCloudCacheModle;
import com.tcl.security.virusengine.modle.ResponseMcAfeeModle;
import com.tcl.security.virusengine.modle.ResponseResultModle;
import com.tcl.security.virusengine.modle.ResponseVersionModle;
import com.tcl.security.virusengine.utils.DescriptionUtil;
import com.tcl.security.virusengine.utils.JSON;
import com.tcl.security.virusengine.utils.RiskUtils;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.List;
import java.util.Map;

/**
 * Created by Steve on 2016/8/9.
 */
public class CloudProcessHelper {


    /**
     * 判断EV是否过期
     * @param EV1
     * @param EV2
     * @param EV3
     * @param ev1
     * @param ev2
     * @param ev3
     * @return
     */
    public static boolean isEvOutofDate(int EV1,int EV2,int EV3,int ev1,int ev2,int ev3){
        return EV1 < ev1 || EV2 < ev2 || EV3 < ev3;
    }


    /**
     * 判断VLV是否过期
     * @param VLV1
     * @param VLV2
     * @param vlv1
     * @param vlv2
     * @return
     */
    public static boolean isVlOutofDate(int VLV1,int VLV2,int vlv1,int vlv2){
        return VLV1 < vlv1 || VLV2 < vlv2;
    }


    /**
     * TCL云返回的Mcafee扫描结果 和本地引擎版本比对，是否过期
     * @param EV1
     * @param EV2
     * @param EV3
     * @param VLV1
     * @param VLV2
     * @return
     */
    public static boolean isLibOutOfDate(int EV1,int EV2,int EV3,int VLV1,int VLV2){
        String mcs = VirusScanQueue.getInstance().getScanMessage().getMcafeeEngineVersion();
        String dat = VirusScanQueue.getInstance().getScanMessage().getLibVersion();

        try{
            if (!mcs.contains("."))
                return true;

            if (!dat.contains("."))
                return true;

            if (mcs.split("\\.").length!=3)
                return true;

            if (dat.split("\\.").length!=2)
                return true;
            String[] evs = mcs.split("\\.");
            int ev1 = Integer.parseInt(evs[0]);
            int ev2 = Integer.parseInt(evs[1]);
            int ev3 = Integer.parseInt(evs[2]);
            //VirusLog.e("EV1 %d,EV2 %d,EV3 %d   ev1 %d,ev2 %d,ev3 %d",EV1,EV2,EV3,ev1,ev2,ev3);
            if (isEvOutofDate(EV1,EV2,EV3,ev1,ev2,ev3))
                return true;

            String[] vlvs = dat.split("\\.");
            int vlv1 = Integer.parseInt(vlvs[0]);
            int vlv2 = Integer.parseInt(vlvs[1]);
            //VirusLog.e("VLV1 %d,VLV2 %d   vlv1 %d,vlv2 %d",VLV1,VLV2,vlv1,vlv2);
            if (isVlOutofDate(VLV1,VLV2,vlv1,vlv2))
                return true;
        }catch (Exception e){
            VirusLog.printException(e,"isLibOutOfDate");
            return true;
        }
        return false;
    }

    /**
     * 由Mcafee云返回结果得到CacheEntry
     * @param entry
     * @param result
     * @param description
     * @return
     */
    public static Cache.CacheEntry getCacheEnttry(Context context, ScanMessage scanMessage, ScanEntry entry, ScanResult result, String description, boolean isTclCloudError) {
        Cache.CacheEntry cache = new Cache.CacheEntry();
        cache.applicationVersion = entry.appVersion;
        cache.versionCode = entry.appVersionCode;
        cache.cacheKey = entry.packageName;
        cache.packageName = entry.packageName;
        cache.appName = entry.appName;
        cache.ttl = String.valueOf(scanMessage.getThisScanTime());
        cache.virusLibVersion = scanMessage.getLibVersion();
        cache.from = Constants.QUERY_FROM_MCAFEE_CLOUD_ENGINE;
        cache.scanState = result.getCategory();
        //如果tcl云异常，那么就是异常，否则经过了macfee云扫，这个一定是tcl云未知的
        cache.tcl_cloud_result = isTclCloudError? Constants.CLOUD_RESULT_ERROR: Constants.CLOUD_RESULT_UNSPECIFIED;
        cache.avengine_cloud_result = result.getCategory();
        cache.cloud_cache_time = String.valueOf(entry.cacheTime);
        Threat threat = result.getThreat();
        if (description!=null)
            cache.virusDescription = description;
        if (threat != null) {
            cache.virusName = threat.getName();
            cache.typeInt = threat.getType();
            cache.description_ids = getDescriptionIDs(context,entry.packageName);
            cache.type = RiskUtils.threatTypeToString(threat.getType());
            cache.risk_level = threat.getRiskLevel();
            cache.suggest = RiskUtils.obtainSuggestByType(context,cache.type);
        }
        return cache;
    }

    /**
     * 针对Space+ SDK 云返回结果得到CacheEntry 未知情况按安全算
     * @param entry
     * @param description
     * @return
     */
    public static Cache.CacheEntry getCacheEnttry(Context context, ScanMessage scanMessage, ScanEntry entry, String description, boolean isTclCloudError) {
        Cache.CacheEntry cache = new Cache.CacheEntry();
        cache.applicationVersion = entry.appVersion;
        cache.versionCode = entry.appVersionCode;
        cache.cacheKey = entry.packageName;
        cache.packageName = entry.packageName;
        cache.appName = entry.appName;
        cache.ttl = String.valueOf(scanMessage.getThisScanTime());
        cache.virusLibVersion = scanMessage.getLibVersion();
        cache.from = Constants.QUERY_FROM_MCAFEE_CLOUD_ENGINE;
        cache.scanState = Constants.ScanInfo.CATEGORY_CLEAN;
        //如果tcl云异常，那么就是异常，否则经过了macfee云扫，这个一定是tcl云未知的
        cache.tcl_cloud_result = isTclCloudError? Constants.CLOUD_RESULT_ERROR: Constants.CLOUD_RESULT_UNSPECIFIED;
        cache.avengine_cloud_result = Constants.ScanInfo.CATEGORY_CLEAN;
        cache.cloud_cache_time = String.valueOf(entry.cacheTime);
        cache.risk_level = Constants.ScanInfo.NO_RISK;
        return cache;
    }


    /**
     * 由TCL云返回结果得到CacheEntry
     * @param entry
     * @param virusName
     * @param virusDescription
     * @param type
     * @param riskLevel
     * @param tclHash
     * @return
     */
    public static Cache.CacheEntry getCacheEnttry(ScanMessage scanMessage, ScanEntry entry, String virusName, String virusDescription, String type, int riskLevel, String tclHash, String suggest, int tcl_cloud_result) {
        Cache.CacheEntry cache = new Cache.CacheEntry();
        cache.applicationVersion = entry.appVersion;
        cache.versionCode = entry.appVersionCode;
        cache.cacheKey = entry.packageName;
        cache.packageName = entry.packageName;
        cache.appName = entry.appName;
        cache.ttl = String.valueOf(scanMessage.getThisScanTime());
        cache.virusLibVersion = scanMessage.getLibVersion();
        cache.from = Constants.QUERY_FROM_TCL_CLOUD_ENGINE;
        cache.scanState = 0;
        cache.virusName = virusName;
        cache.virusDescription = virusDescription;
        cache.type = type;
        cache.risk_level = riskLevel;
        cache.tclHash = tclHash;
        cache.suggest = suggest;
        cache.tcl_cloud_result = tcl_cloud_result;
        cache.avengine_cloud_result = Constants.CLOUD_RESULT_UNSPECIFIED;
        cache.cloud_cache_time = String.valueOf(entry.cacheTime);
        //TCL云查有结果,AvEngine云查的结果是未知的，因为没有经过其他云检测

        //VirusLog.w("=== risky entry %s",cache);

        return cache;
    }

    /**
     * 获取病毒描述
     * @param packagename
     * @return
     */
    public static String getDescription(Context context,String packagename){
        String descrpiton = DescriptionUtil.getDescriptionByPermission(context,packagename);
        VirusLog.i("=============packagename %s,descrption %s",packagename,descrpiton);
        return descrpiton;
    }

    /**
     * Mcafee云扫 通过描述获取description ids
     * @param packageName
     * @return
     */
    public static String getDescriptionIDs(Context context,String packageName){
        return DescriptionUtil.getDescrptionIdsByPackageName(context,packageName);
    }

    /**
     * TCL云返回结果，存入缓存
     * @param packageName
     * @param entry
     * @param virusName
     * @param virusDescription
     * @param type
     * @param riskLevel
     * @param tclHash
     */
    public static void cacheEntry(CacheHandle cacheHandle, ScanMessage scanMessage, List<Cache.CacheEntry> reportList, String packageName, ScanEntry entry, String virusName, String virusDescription, String type, int riskLevel, String tclHash, String suggest, int tcl_cloud_result) {
        if (cacheHandle!=null){
            Cache.CacheEntry cache = getCacheEnttry(scanMessage,entry,virusName,virusDescription,type,riskLevel,tclHash,suggest,tcl_cloud_result);
            cacheHandle.put(packageName,cache);
            reportList.add(cache);
        }
    }

    /**
     * Mcafee云返回结果后，存入缓存
     * @param scanSource
     * @param entry
     * @param scanResult
     * @param description
     */
    public static void cacheEntry(Context context, CacheHandle cacheHandle, ScanMessage scanMessage, List<Cache.CacheEntry> reportList, ScanSource scanSource, ScanEntry entry, ScanResult scanResult, String description, boolean isTclCloudError) {
        if (cacheHandle != null){
            Cache.CacheEntry cache = CloudProcessHelper.getCacheEnttry(context,scanMessage,entry,scanResult,description,isTclCloudError);
            cacheHandle.put(scanSource.toString(), cache);
            reportList.add(cache);
        }
    }


    /**
     * TCL云端正式库Clean处理
     * @param context
     * @param delivery
     * @param entry
     * @param entryMap
     * @param reportList
     * @param response
     */
    public static void dealTclResponseClean(Context context, CacheHandle cacheHandle, ScanMessage scanMessage, ScanResultDelivery delivery, ScanEntry entry, Map<String, ScanEntry> entryMap, List<Cache.CacheEntry> reportList, CloudResponse response) {
        String packageName = response.key;
        entry = entryMap.remove(response.key);
        String type = RiskUtils.threatTypeToString(Constants.ScanInfo.DEFAULT_VIRUS_TYPE);
        String suggest = RiskUtils.obtainSuggestByType(context,type);
        delivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_ENTRY, new ScanInfo(Constants.ScanInfo.FILE_TYPE_APK,packageName, null, null, ScanResult.CATEGORY_CLEAN, entry.appName,type, Constants.ScanInfo.NO_RISK, suggest), entry);
        cacheEntry(cacheHandle,scanMessage,reportList,packageName,entry,response.virusName,response.virusDescription,type, Constants.ScanInfo.NO_RISK,response.metaInfo.apkHash,suggest, Constants.CLOUD_RESULT_CLEAN);
    }

    /**
     * TCL云端正式库Virus处理
     * @param context
     * @param cacheHandle
     * @param scanMessage
     * @param delivery
     * @param entry
     * @param entryMap
     * @param reportList
     * @param response
     */
    public static void dealTclResponseVirus(Context context, CacheHandle cacheHandle, ScanMessage scanMessage, ScanResultDelivery delivery, ScanEntry entry, Map<String, ScanEntry> entryMap, List<Cache.CacheEntry> reportList, CloudResponse response) {
        String packageName = response.key;
        entry = entryMap.remove(response.key);
        String virusName = response.virusName;
        String virusDescription = response.virusDescription;
        String type = RiskUtils.obtainRiskTypeByVirusName(virusName);
        String real_description = dealTclDescription(context,virusDescription);
        String suggest = RiskUtils.obtainSuggestByType(context,type);
        delivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_ENTRY, new ScanInfo(Constants.ScanInfo.FILE_TYPE_APK,packageName, virusName, real_description, Constants.ScanInfo.CATEGORY_RISKY, entry.appName, type, Constants.ScanInfo.RISK_HIGH,suggest), entry);
        cacheEntry(cacheHandle,scanMessage,reportList,packageName,entry,response.virusName,real_description,type, Threat.RISK_HIGH,response.metaInfo.apkHash,suggest, Constants.CLOUD_RESULT_RISK);
    }

    /**
     * TCL云端正式库Risk处理
     * @param context
     * @param cacheHandle
     * @param scanMessage
     * @param delivery
     * @param entry
     * @param entryMap
     * @param reportList
     * @param response
     */
    public static void dealTclResponseRisk(Context context, CacheHandle cacheHandle, ScanMessage scanMessage, ScanResultDelivery delivery, ScanEntry entry, Map<String, ScanEntry> entryMap, List<Cache.CacheEntry> reportList, CloudResponse response) {
        String packageName = response.key;
        entry = entryMap.remove(response.key);
        String virusName = response.virusName;
        String virusDescription = response.virusDescription;
        String real_description = dealTclDescription(context,virusDescription);
        String type = RiskUtils.obtainRiskTypeByVirusName(virusName);
        String suggest = RiskUtils.obtainSuggestByType(context,type);
        delivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_ENTRY, new ScanInfo(Constants.ScanInfo.FILE_TYPE_APK,packageName, virusName, real_description, ScanResult.CATEGORY_RISKY, entry.appName,type, Threat.RISK_LOW, suggest), entry);
        cacheEntry(cacheHandle,scanMessage,reportList,packageName,entry,response.virusName,real_description,type, Threat.RISK_LOW,response.metaInfo.apkHash,suggest, Constants.CLOUD_RESULT_RISK);
    }

    /**
     * 处理TCL云返回的description
     * @param description
     */
    public static String dealTclDescription(Context context,String description){
        String descriptionStr = DescriptionUtil.findDescriptionByIds(context,description);
        VirusLog.d("id %s",descriptionStr);
        return descriptionStr;
    }

    public static String dealTclDescription(Context context,List<Integer> descriptionIds){
        return DescriptionUtil.findDescriptionByIds(context,descriptionIds);
    }

    /**
     * 判断ScanEntry是否是RealTimeEntry
     * @param list
     * @return
     */
    public static boolean isRealTimeEntry(List<ScanEntry> list){
        if (list!=null&&list.size()==1) {
            ScanEntry entry = list.get(0);
            if (entry.priority== ScanEntry.REAL_TIME)
                return true;
        }
        return false;
    }

    /**
     * Mcafee云返回后的处理
     * @param unspecifiedList
     * @param category
     * @param scanSource
     * @param entry
     * @param scanResult
     */
    public static void mcafeeSuccessDeal(Context context, ScanResultDelivery delivery, CacheHandle cacheHandle, ScanMessage scanMessage, List<Cache.CacheEntry> reportList, List<ScanEntity> unspecifiedList, int category, ScanSource scanSource, ScanEntry entry, ScanResult scanResult, boolean isTclCloudError){
        switch (category) {
            case ScanResult.CATEGORY_CLEAN:
                VirusLog.i("=== mcafee cloud scan is clean %s", scanSource.toString());
                String type = RiskUtils.threatTypeToString(Constants.ScanInfo.DEFAULT_VIRUS_TYPE);
                String suggest = RiskUtils.obtainSuggestByType(context,type);
                ScanInfo cleanInfo =  new ScanInfo(Constants.ScanInfo.FILE_TYPE_APK,scanSource.toString(), null, null, ScanResult.CATEGORY_CLEAN, entry.appName, type, Constants.ScanInfo.NO_RISK,suggest);
                delivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_ENTRY, cleanInfo, entry);
                cacheEntry(context,cacheHandle,scanMessage,reportList,scanSource, entry, scanResult,null,isTclCloudError);
                break;
            case ScanResult.CATEGORY_RISKY:
                VirusLog.e("=== mcafee cloud scan is risky %s", scanSource.toString());
                Threat threat = scanResult.getThreat();
                String description = CloudProcessHelper.getDescription(context,scanSource.toString());
                String typeRisky =RiskUtils.threatTypeToString(threat.getType());
                String suggestRisky = RiskUtils.obtainSuggestByType(context,typeRisky);
                ScanInfo riskInfo = new ScanInfo(Constants.ScanInfo.FILE_TYPE_APK,scanSource.toString(), threat.getName(), description, ScanResult.CATEGORY_RISKY, entry.appName, typeRisky, threat.getRiskLevel(), suggestRisky);
                delivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_ENTRY, riskInfo, entry);
                cacheEntry(context,cacheHandle,scanMessage,reportList,scanSource, entry, scanResult,description,isTclCloudError);
                break;
            case ScanResult.CATEGORY_UNSPECIFIED://未知的加入未知列表
                VirusLog.w("=== mcafee cloud scan is unspecified %s and add it to mcafeescanqueue.", scanSource.toString());
                if (isTclCloudError){
                    unspecifiedList.add(new ScanEntity(scanSource, Constants.CLOUD_RESULT_ERROR, Constants.CLOUD_RESULT_UNSPECIFIED));
                }else {
                    unspecifiedList.add(new ScanEntity(scanSource, Constants.CLOUD_RESULT_UNSPECIFIED, Constants.CLOUD_RESULT_UNSPECIFIED));
                }
                break;
        }
    }

    /**
     * 处理TCL云返回数据中有Mcafee临时缓存数据流程
     * @param entry
     * @param entryMap
     * @param reportList
     * @param response
     * @return true 表示TCL云处理数据正常，这条数据不用再处理，false 表示异常，这条数据需要继续处理
     */
    public static boolean dealTclResponseFromMacfee(Context context, ScanResultDelivery delivery, CacheHandle cacheHandle, ScanMessage scanMessage, ScanEntry entry, Map<String, ScanEntry> entryMap, List<Cache.CacheEntry> reportList, CloudResponse response){
        String cloudCache = response.cloudCache;
        String packageName = response.key;
        entry = entryMap.get(response.key);
        if (TextUtils.isEmpty(cloudCache))
            return false;
        ResponseCloudCacheModle cloudCacheModle = JSON.getObject(cloudCache,ResponseCloudCacheModle.class);
        //得到cacheTime

        if (cloudCacheModle==null)
            return false;

        long cacheTime = ((long)cloudCacheModle.CacheTime)*1000;
        ResponseMcAfeeModle mcAfeeModle = cloudCacheModle.McAfee;
        if(mcAfeeModle==null){
            entry.cacheTime = cacheTime;
            return false;
        }
        ResponseVersionModle versionModle = mcAfeeModle.Version;
        //这里要比较引擎版本和病毒库版本
        if (CloudProcessHelper.isLibOutOfDate(versionModle.EV1,versionModle.EV2,versionModle.EV3,versionModle.VLV1,versionModle.VLV2)){
            entry.cacheTime = cacheTime;
            return false;
        }
        //这里要比较上报时间和当前时间策略
        long updateDateTime = ((long)mcAfeeModle.UpdateDateTime)*1000;
        //VirusLog.e("====UpdateDateTime %d,packageName %s,tclHash %s",updateDateTime,packageName,response.metaInfo.apkHash);
        long nowUTCTime = System.currentTimeMillis();
        //VirusLog.w("====CUR TIME %d",nowUTCTime);

        if (isTemporyDataOutofDate(nowUTCTime,updateDateTime,cacheTime)){
            entry.cacheTime = cacheTime;
            return false;
        }

        entry.cacheTime = calculateRemainCacheTime(nowUTCTime,updateDateTime,cacheTime);
        ResponseResultModle resultModle = mcAfeeModle.Result;
        if (resultModle.Category== ScanResult.CATEGORY_CLEAN){//结果是clean的处理
            String type = RiskUtils.threatTypeToString(Constants.ScanInfo.DEFAULT_VIRUS_TYPE);
            String suggest = RiskUtils.obtainSuggestByType(context,type);
            ScanInfo cleanInfo =  new ScanInfo(Constants.ScanInfo.FILE_TYPE_APK,response.key, null, null, ScanResult.CATEGORY_CLEAN, entry.appName, type, Constants.ScanInfo.NO_RISK, suggest);
            delivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_ENTRY, cleanInfo, entry);
            CloudProcessHelper.cacheEntry(cacheHandle,scanMessage,reportList,packageName,entry,response.virusName,response.virusDescription,type, Constants.ScanInfo.NO_RISK,response.metaInfo.apkHash,suggest, Constants.CLOUD_RESULT_CLEAN);
        }else if(resultModle.Category== ScanResult.CATEGORY_RISKY){//结果是risky的处理
            String virusName = resultModle.VirusName;
            String real_description = CloudProcessHelper.dealTclDescription(context,resultModle.VirusDesc);
            String type = RiskUtils.threatTypeToString(resultModle.Type);
            String suggest = RiskUtils.obtainSuggestByType(context,type);
            delivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_ENTRY, new ScanInfo(Constants.ScanInfo.FILE_TYPE_APK,packageName, virusName, real_description, ScanResult.CATEGORY_RISKY, entry.appName, type, Threat.RISK_HIGH,suggest), entry);
            CloudProcessHelper.cacheEntry(cacheHandle,scanMessage,reportList,packageName,entry,response.virusName,real_description,type, Threat.RISK_HIGH,response.metaInfo.apkHash,suggest, Constants.CLOUD_RESULT_RISK);
        }else if(resultModle.Category== ScanResult.CATEGORY_UNSPECIFIED){
            VirusLog.e("这里就异常了！！！！");
            String type = RiskUtils.threatTypeToString(Constants.ScanInfo.DEFAULT_VIRUS_TYPE);
            String suggest = RiskUtils.obtainSuggestByType(context,type);
            ScanInfo cleanInfo =  new ScanInfo(Constants.ScanInfo.FILE_TYPE_APK,response.key, null, null, ScanResult.CATEGORY_CLEAN, entry.appName, type, Constants.ScanInfo.NO_RISK, suggest);
            delivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_ENTRY, cleanInfo, entry);
            CloudProcessHelper.cacheEntry(cacheHandle,scanMessage,reportList,packageName,entry,response.virusName,response.virusDescription,type, Constants.ScanInfo.NO_RISK,response.metaInfo.apkHash,suggest, Constants.CLOUD_RESULT_CLEAN);
        }

        if (entryMap.containsKey(response.key)){
            entryMap.remove(response.key);
        }
        return true;

    }

    /**
     * 判断临时表中数据对比上报时间是否已经超过了缓存时间
     * @param nowUTCTime
     * @param updateDateTime
     * @param cacheTime
     * @return true 过期了  false 没过期
     */
    public static boolean isTemporyDataOutofDate(long nowUTCTime,long updateDateTime,long cacheTime){
        return (nowUTCTime - updateDateTime) > cacheTime;
    }

    /**
     * 在临时表中数据没有过期的情况下，计算剩余的缓存时间
     * @param nowUTCTime
     * @param updateDateTime
     * @param cacheTime
     * @return
     */
    public static long calculateRemainCacheTime(long nowUTCTime,long updateDateTime,long cacheTime){
        return cacheTime - (nowUTCTime - updateDateTime);
    }


}

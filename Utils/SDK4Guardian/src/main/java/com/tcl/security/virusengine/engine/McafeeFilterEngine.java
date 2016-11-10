package com.tcl.security.virusengine.engine;

import android.content.Context;

import com.intel.security.SecurityContext;
import com.intel.security.vsm.ScanObserver;
import com.intel.security.vsm.ScanResult;
import com.intel.security.vsm.ScanStrategy;
import com.intel.security.vsm.ScanTask;
import com.intel.security.vsm.Threat;
import com.intel.security.vsm.VirusScan;
import com.intel.security.vsm.content.ScanApplication;
import com.intel.security.vsm.content.ScanCombination;
import com.intel.security.vsm.content.ScanSource;
import com.tcl.security.virusengine.Constants;
import com.tcl.security.virusengine.DefaultMcAfeeScanTimerImpl;
import com.tcl.security.virusengine.VirusScanQueue;
import com.tcl.security.virusengine.cache.Cache;
import com.tcl.security.virusengine.cache.CacheHandle;
import com.tcl.security.virusengine.entry.CloudTask;
import com.tcl.security.virusengine.entry.PackagedScanTask;
import com.tcl.security.virusengine.entry.ScanEntity;
import com.tcl.security.virusengine.entry.ScanEntry;
import com.tcl.security.virusengine.entry.ScanInfo;
import com.tcl.security.virusengine.func_interface.IMcAfeeScanListener;
import com.tcl.security.virusengine.func_interface.McAfeeScanTimer;
import com.tcl.security.virusengine.func_interface.ScanMessage;
import com.tcl.security.virusengine.func_interface.ScanResultDelivery;
import com.tcl.security.virusengine.utils.DescriptionUtil;
import com.tcl.security.virusengine.utils.RiskUtils;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import tlogsdk.HiLogManager;

/**
 * Created by Steve on 2016/4/29.
 */
public class McafeeFilterEngine {

    private volatile boolean mCanceled = false;

    private final CacheHandle mCacheHandle;

    private final ScanResultDelivery mDelivery;

    private final Context mContext;

    private final ScanStrategy mStrategy;

    private final CopyOnWriteArrayList<ScanTask> mTaskList = new CopyOnWriteArrayList<>();

    private final CopyOnWriteArrayList<PackagedScanTask> mPcakgeTaskList = new CopyOnWriteArrayList<>();

    private final ScanMessage mScanMessage;

    private final long SINGLE_SCAN_ITEM_TIMEOUT = 500;

    public void cancel() {
        mCanceled = true;
        for (ScanTask task : mTaskList) {
            if (task != null)
                task.cancel();
        }
        mTaskList.clear();
        mPcakgeTaskList.clear();
    }

    public McafeeFilterEngine(Context context, CacheHandle cacheHandle, ScanResultDelivery delivery, ScanMessage message) {
        mContext = context;
        mCacheHandle = cacheHandle;
        mDelivery = delivery;
        mStrategy = new SignatureLocalStrategy();
        mScanMessage = message;
    }

    public void prepare() {
        mCanceled = false;
    }

    public void isVirus(final ScanEntry entry) throws Exception {

        if (mCanceled && entry.priority != ScanEntry.REAL_TIME) {
            return;
        }

        //TODO 走Macfee本地查杀流程
        boolean isEngineInitialized = SecurityContext.isInitialized(mContext);

        //引擎未初始化
        if (!isEngineInitialized) {
            throw new IllegalStateException("Macfee Engine is not initialized");
        }

        VirusScan virusScan = VirusScanQueue.getInstance().getVirusScan();

        if (virusScan == null) {
            virusScan = (VirusScan) SecurityContext.getService(mContext, SecurityContext.VIRUS_SCAN);
        }


        ConcurrentHashMap<String,ScanEntry> entryMap = new ConcurrentHashMap<>();
        CopyOnWriteArrayList<Cache.CacheEntry> list = new CopyOnWriteArrayList<>();

        entryMap.put(entry.packageName, entry);
        List<ScanEntity> entityList = new ArrayList<>();
        ScanSource scanSource = new ScanApplication(entry.packageName);
        entityList.add(new ScanEntity(scanSource, Constants.CLOUD_RESULT_UNSPECIFIED, Constants.CLOUD_RESULT_UNSPECIFIED));
        VirusLog.i("=== Mcafee filter engine is scaning %s", entry.packageName);
        ScanTask scan = virusScan.scan(new ScanApplication(entry.packageName), mStrategy, new LocalObserver(entityList,entryMap,list,null,0));
        mTaskList.add(scan);
    }

    private boolean isRealTimeEntry(List<ScanEntry> list){
        if (list!=null&&list.size()==1) {
            ScanEntry entry = list.get(0);
            if (entry.priority== ScanEntry.REAL_TIME)
                return true;
        }
        return false;
    }

    public void scan(List<ScanEntry> entryList, final List<ScanEntity> list, final CopyOnWriteArrayList<Cache.CacheEntry> uploadList) throws Exception {

        if (mCanceled&&!isRealTimeEntry(entryList)) {
            return;
        }

        boolean isEngineInitialized = SecurityContext.isInitialized(mContext);

        //引擎未初始化
        if (!isEngineInitialized) {
            VirusLog.e("McAfee本地引擎未初始化？？？？？？？？？");
            throw new IllegalStateException("Macfee Engine is not initialized");
        }

        VirusScan virusScan = VirusScanQueue.getInstance().getVirusScan();

        if (virusScan == null) {
            VirusLog.e("mVirusScan是null ？？？？？？？？");
            virusScan = (VirusScan) SecurityContext.getService(mContext, SecurityContext.VIRUS_SCAN);
        }


        ConcurrentHashMap<String,ScanEntry> entryMap = new ConcurrentHashMap<>();

        for (ScanEntry entry : entryList) {
            entryMap.put(entry.packageName, entry);
        }
        VirusLog.d("开启本次Mcafee本地扫描");

        final List<ScanSource> sourceList = new LinkedList<>();
        for(ScanEntity entity:list){
            sourceList.add(entity.scanSource);
        }
        final List<PackagedScanTask> taskList = new ArrayList<>();
        long start = System.currentTimeMillis();
        ScanTask task = virusScan.scan(new ScanCombination() {
            @Override
            public Collection<ScanSource> getSources() {
                return sourceList;
            }
        }, mStrategy, new LocalObserver(list,entryMap,uploadList,taskList,start));
        final PackagedScanTask packagedScanTask = new PackagedScanTask(task,false,false);
        McAfeeScanTimer scanTimer = new DefaultMcAfeeScanTimerImpl();
        scanTimer.startTiming(packagedScanTask, sourceList.size() * SINGLE_SCAN_ITEM_TIMEOUT, new IMcAfeeScanListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd() {

            }
        });
        taskList.add(packagedScanTask);
        VirusLog.d("Scantask %s",task.toString());
        mTaskList.add(task);
        mPcakgeTaskList.add(packagedScanTask);

    }

    private class SignatureLocalStrategy implements ScanStrategy {

        @Override
        public int getTechnology(ScanSource scanSource) {
            return TECHNOLOGY_SIGNATURES;
        }
    }

    private class LocalObserver implements ScanObserver {

        Map<String,ScanEntry> entryMap;

        CopyOnWriteArrayList<Cache.CacheEntry> reportList;

        List<ScanEntity> entityList;

        List<PackagedScanTask> taskList;

        PackagedScanTask task;

        long start;

        public LocalObserver(List<ScanEntity> entityList, Map<String,ScanEntry> map, CopyOnWriteArrayList<Cache.CacheEntry> reportList, List<PackagedScanTask> taskList, long start){
            this.entryMap = map;
            this.reportList = reportList;
            this.entityList = entityList;
            this.taskList = taskList;
            this.start = start;
        }

        @Override
        public void onStarted() {
            if (taskList!=null&&!taskList.isEmpty())
                task = taskList.get(0);
        }

        @Override
        public void onCompleted(int i) {
            VirusLog.w("i %d",i);
            if (task!=null)
                task.isCompleted = true;
            HiLogManager.sendEvent("local_mcafee_time","local_mcafee_time_num",entityList.size());
            long end = System.currentTimeMillis();
            float time = (end - start)/1000.0f;
            VirusLog.w("本次McAfee本地扫描用时 %f",time);
            HashMap<String,String> real_params = new HashMap<>();
            real_params.put("local_mcafee_time_real",Float.toString(time));
            HiLogManager.sendEvent("local_mcafee_time",real_params);
            if (taskList!=null&&!taskList.isEmpty()){
                ScanTask task = taskList.get(0).scanTask;
                if (task!=null){
                    HashMap<String,String> response_params = new HashMap<>();
                    float response_time = task.getState().getElapsedTime()/1000.0f;
                    VirusLog.e("==== local_time_response %f",response_time);
                    response_params.put("local_mcafee_time_response",Float.toString(response_time));
                    HiLogManager.sendEvent("local_mcafee_time",response_params);
                }
            }
            //走上报流程
            mDelivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_UPLOAD,null,reportList);
        }

        @Override
        public void onScanned(ScanSource scanSource, ScanResult scanResult) {
            VirusLog.i("McafeeFilterEngine is returned");
            int category = scanResult.getCategory();
            ScanEntry entry = entryMap.remove(scanSource.toString());
            if (mCanceled&&entry.priority!= ScanEntry.REAL_TIME)
                return;
            String description = null;
            String type = null;
            String suggest = null;
            ScanInfo info = null;
            int timeOut = 0;
            if (task!=null){
                timeOut = task.isTimeout ?1:0;
            }
            switch (category) {
                case ScanResult.CATEGORY_CLEAN:
                    type =  RiskUtils.threatTypeToString(Constants.ScanInfo.DEFAULT_VIRUS_TYPE);
                    suggest = RiskUtils.obtainSuggestByType(mContext,type);
                    info = new ScanInfo(Constants.ScanInfo.FILE_TYPE_APK,scanSource.toString(), null, null, Constants.ScanInfo.CATEGORY_CLEAN, entry.appName, type, Constants.ScanInfo.NO_RISK, suggest);
                    mDelivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_ENTRY,info, entry);
                    break;
                case ScanResult.CATEGORY_RISKY:
                    Threat threat = scanResult.getThreat();
                    description = getDescription(scanSource.toString());
                    type = RiskUtils.threatTypeToString(threat.getType());
                    suggest = RiskUtils.obtainSuggestByType(mContext,type);
                    info = new ScanInfo(Constants.ScanInfo.FILE_TYPE_APK,scanSource.toString(), threat.getName(), description, ScanResult.CATEGORY_RISKY, entry.appName,type, threat.getRiskLevel(), suggest);
                    mDelivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_ENTRY,info, entry);
                    break;
                case ScanResult.CATEGORY_UNSPECIFIED:
                    type = RiskUtils.threatTypeToString(Constants.ScanInfo.DEFAULT_VIRUS_TYPE);
                    suggest = RiskUtils.obtainSuggestByType(mContext,type);
                    mDelivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_ENTRY, new ScanInfo(Constants.ScanInfo.FILE_TYPE_APK,scanSource.toString(), null, null, ScanResult.CATEGORY_CLEAN, entry.appName, type, Constants.ScanInfo.NO_RISK, suggest), entry);
                    break;
            }

            if (mCacheHandle != null){
                for(ScanEntity entity:entityList){
                    if (entity.scanSource.toString().equals(scanSource.toString())){
                        Cache.CacheEntry cache= getCacheEnttry(entity,entry, scanSource, scanResult, description,suggest);
                        reportList.add(cache);
                        mCacheHandle.put(scanSource.toString(), cache);
                    }
                }

            }

        }

    }

    private String getDescription(String packagename) {
        String descrpiton = DescriptionUtil.getDescriptionByPermission(mContext, packagename);
        VirusLog.i("=============packagename %s,descrption %s", packagename, descrpiton);
        return DescriptionUtil.getDescriptionByPermission(mContext, packagename);
    }

    private String getDescriptionIDs(String packagename){
        return DescriptionUtil.getDescrptionIdsByPackageName(mContext,packagename);
    }

    private Cache.CacheEntry getCacheEnttry(ScanEntity entity, ScanEntry entry, ScanSource source, ScanResult result, String description, String suggest) {
        Cache.CacheEntry cache = new Cache.CacheEntry();
        cache.applicationVersion = entry.appVersion;
        cache.versionCode = entry.appVersionCode;
        cache.cacheKey = entry.packageName;
        cache.packageName = entry.packageName;
        cache.appName = entry.appName;
        cache.ttl = String.valueOf(mScanMessage.getThisScanTime());
        cache.virusLibVersion = mScanMessage.getLibVersion();
        cache.from = Constants.QUERY_FROM_LOCAL_ENGINE;
        cache.scanState = result.getCategory();
        cache.tcl_cloud_result = entity.tcl_cloud_result;
        cache.avengine_cloud_result = entity.avenine_cloud_result;
        cache.cloud_cache_time = String.valueOf(entry.cacheTime);
        cache.publicSourceDir = entry.publicSourceDir;
        Threat threat = result.getThreat();
        if (description != null)
            cache.virusDescription = description;
        if (threat != null) {
            cache.virusName = threat.getName();
            cache.typeInt = threat.getType();
            cache.description_ids = getDescriptionIDs(entry.packageName);
            cache.type = RiskUtils.threatTypeToString(threat.getType());
            cache.risk_level = threat.getRiskLevel();
            cache.suggest = RiskUtils.obtainSuggestByType(mContext,cache.type);
        }
        return cache;
    }


}

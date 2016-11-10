package com.tcl.security.virusengine.engine;

import android.content.Context;

import com.intel.security.vsm.ScanObserver;
import com.intel.security.vsm.ScanResult;
import com.intel.security.vsm.ScanTask;
import com.intel.security.vsm.content.ScanApplication;
import com.intel.security.vsm.content.ScanSource;
import com.tcl.security.cloudengine.CloudResponse;
import com.tcl.security.virusengine.Constants;
import com.tcl.security.virusengine.VirusEngine;
import com.tcl.security.virusengine.cache.Cache;
import com.tcl.security.virusengine.cache.CacheHandle;
import com.tcl.security.virusengine.entry.CloudTask;
import com.tcl.security.virusengine.entry.ScanEntity;
import com.tcl.security.virusengine.entry.ScanEntry;
import com.tcl.security.virusengine.func_interface.CloudScheduling;
import com.tcl.security.virusengine.func_interface.ScanMessage;
import com.tcl.security.virusengine.func_interface.ScanResultDelivery;
import com.tcl.security.virusengine.network.BaseNetwork;
import com.tcl.security.virusengine.network.RequestCallback;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static android.R.attr.start;

/**
 * Created by Steve on 2016/8/12.
 */
public class CloudSchedulingImpl implements CloudScheduling {

    private Context mContext;

    private McafeeFilterEngine mMcafeeFilterEngine;

    private SpecialHandleForSpace mSpecialHandleForSpace;

    private McafeeCloudEngine mMcafeeCloudEngine;

    private volatile boolean mCanceled = false;

    private volatile boolean useMacfeeCloud = true;

    private volatile boolean useTclCloud = true;

    private volatile boolean useBatchLocal = true;

    private CacheHandle mCacheHandle;

    private ScanResultDelivery mDelivery;

    private ScanMessage mScanMessage;


    public CloudSchedulingImpl(Context mContext, McafeeFilterEngine mMcafeeFilterEngine, McafeeCloudEngine mMcafeeCloudEngine, CacheHandle mCacheHandle, ScanResultDelivery mDelivery, ScanMessage mScanMessage) {
        this.mContext = mContext;
        this.mMcafeeFilterEngine = mMcafeeFilterEngine;
        this.mMcafeeCloudEngine = mMcafeeCloudEngine;
        this.mCacheHandle = mCacheHandle;
        this.mDelivery = mDelivery;
        this.mScanMessage = mScanMessage;
        mSpecialHandleForSpace = new SpecialHandleForSpace();
    }

    public boolean ismCanceled() {
        return mCanceled;
    }

    public void setCanceled(boolean mCanceled) {
        this.mCanceled = mCanceled;
    }

    @Override
    public void uploading(final List<ScanEntry> list) throws Exception {
        VirusLog.d("List");

        if (mCanceled&&!CloudProcessHelper.isRealTimeEntry(list))
            return;


        VirusLog.w("===upload list size %d", list.size());
        final CloudTask cloudTask = new CloudTask();
        if (!CloudProcessHelper.isRealTimeEntry(list))
            CloudFilterEngine.cloudTaskList.add(cloudTask);
        List<ScanEntity> entityList = new LinkedList<>();
        final List<String> packages = new LinkedList<>();
        final ConcurrentHashMap<String,ScanEntry> tempMap = new ConcurrentHashMap<>();
        for (ScanEntry entry : list) {
            entityList.add(new ScanEntity(new ScanApplication(entry.packageName), Constants.CLOUD_RESULT_UNSPECIFIED, Constants.CLOUD_RESULT_UNSPECIFIED));
            packages.add(entry.packageName);
            tempMap.put(entry.packageName, entry);
        }
        if (useTclCloud) {//是否使用TCL云
            BaseNetwork.getInstance().invokeQuery("queryPackage", packages, null, new RequestCallback() {
                @Override
                public void onSuccess(Object obj) {
                    List<CloudResponse> cloudList = (List<CloudResponse>) obj;
                    parseNeedQueryByVSMToScanSource(cloudTask,cloudList,list,tempMap);
                }

                @Override
                public void onFail(int code,String message) {
                    VirusLog.e("request fail");
                    parseListToScanSource(cloudTask,packages,list,tempMap);
                }
            });


        }else{
            if (useMacfeeCloud){//不使用TCL云，是否使用McafeeCloud
                uploadMcafeeCloud(cloudTask,entityList,list,tempMap,false);
            }else{
                //仅使用Mcafee本地引擎
                CloudFilterEngine.cloudTaskList.remove(cloudTask);
                CopyOnWriteArrayList<Cache.CacheEntry> reportList = new CopyOnWriteArrayList<>();
                mMcafeeFilterEngine.scan(list,entityList,reportList);
            }
        }
    }

    @Override
    public void uploading(ScanEntry entry) {
            VirusLog.d("ScanEntry");
            List<ScanEntry> list = new LinkedList<>();
            list.add(entry);

            try {
                uploading(list);
            } catch (Exception engineError) {
                engineError.printStackTrace();
            }
    }

    @Override
    public void parseListToScanSource(CloudTask cloudTask, List<String> packages, List<ScanEntry> entryList, Map<String, ScanEntry> entryMap) {
        List<ScanEntity> entityList = new ArrayList<>();
        for (String str : packages) {
            entityList.add(new ScanEntity(new ScanApplication(str), Constants.CLOUD_RESULT_ERROR, Constants.CLOUD_RESULT_UNSPECIFIED));
        }
        try {
            uploadMcafeeCloud(cloudTask,entityList,entryList,entryMap,true);
        } catch (Exception engineError) {
            engineError.printStackTrace();
        }
    }

    @Override
    public void parseNeedQueryByVSMToScanSource(CloudTask cloudTask, List<CloudResponse> list, List<ScanEntry> entryList, Map<String, ScanEntry> entryMap) {
        try {
            List<ScanEntity> entytyList = new ArrayList<>();
            CopyOnWriteArrayList<Cache.CacheEntry> reportList = new CopyOnWriteArrayList<>();
            ScanEntry entry = null;
            if (mCanceled&&!CloudProcessHelper.isRealTimeEntry(entryList))
                return;
            for (CloudResponse response : list) {
                //TODO 这里还要预留策略
                if (mCanceled&&!CloudProcessHelper.isRealTimeEntry(entryList))
                    return;
                if (response.result== Constants.RESPONSE_TCL_FROM_AVENGINE&&response.from == Constants.RESPONSE_TCL_FROM_AVENGINE){//表示数据来自McAfee引擎
                    if (CloudProcessHelper.dealTclResponseFromMacfee(mContext,mDelivery,mCacheHandle,mScanMessage,entry,entryMap,reportList,response)){//如果TCL云能处理这条数据,继续循环
                        continue;
                    }else{//如查TCL云不能处理这条数据，加入到entityList中让后续继续处理
                        //这时如果为了适配清理的SDK,理论上不会走到这里,会在上面的处理中都处理掉
                        entytyList.add(new ScanEntity(new ScanApplication(response.key), Constants.CLOUD_RESULT_UNSPECIFIED, Constants.CLOUD_RESULT_UNSPECIFIED));
                    }
                }else{
                    if (response.result == -1) {//未知
                        if (response.from== Constants.RESPONSE_TCL_FROM_AVENGINE){//result = - 1; from = 101 说明服务器正式库为未知，但是临时表中有数据,这里取临时表中的数据
                            VirusLog.i("TCL云正式库未知，临时表数据 packageName %s,result %d,from %d,TCLHash %s",response.key,response.result,response.from,response.metaInfo.apkHash);
                            if(CloudProcessHelper.dealTclResponseFromMacfee(mContext,mDelivery,mCacheHandle,mScanMessage,entry,entryMap,reportList,response)) {//如果TCL云能处理这条数据，继续循环
                                VirusLog.d("TCL云正式库未知，临时表数据 并且成功处理packageName %s,result %d,from %d,TCLHash %s",response.key,response.result,response.from,response.metaInfo.apkHash);
                                continue;
                            }
                        }
                        VirusLog.w("TCL云未知数据 packageName %s,result %d,from %d,TCLHash %s",response.key,response.result,response.from,response.metaInfo.apkHash);
                        //如果TCL云不能处理这条数据，或者response.from 不是101(没有临时表数据)，加入McAfee后续扫描流程
                        //加入清理SDK的逻辑后这里理论上不会走到这
                        entytyList.add(new ScanEntity(new ScanApplication(response.key), Constants.CLOUD_RESULT_UNSPECIFIED, Constants.CLOUD_RESULT_UNSPECIFIED));
                    } else if (response.result == 0) {//安全
                        VirusLog.i("=== tcl cloud scan is clean %s", response.key);
                        CloudProcessHelper.dealTclResponseClean(mContext,mCacheHandle,mScanMessage,mDelivery,entry,entryMap, reportList, response);
                    } else if (response.result == 1) {//病毒
                        VirusLog.e("=== tcl cloud scan is virus %s", response.key);
                        CloudProcessHelper.dealTclResponseVirus(mContext,mCacheHandle,mScanMessage,mDelivery,entry,entryMap, reportList, response);
                    } else if (response.result == 2) {//风险
                        VirusLog.w("=== tcl cloud scan is risk %s", response.key);
                        CloudProcessHelper.dealTclResponseRisk(mContext,mCacheHandle,mScanMessage,mDelivery,entry,entryMap, reportList, response);
                    }
                }
            }
            if (!entytyList.isEmpty()){
                VirusLog.w("mcafeecloud need query is %d",entytyList.size());
                if (mCanceled&&!CloudProcessHelper.isRealTimeEntry(entryList))
                    return;
                if (useMacfeeCloud)
                    uploadMcafeeCloud(cloudTask,entytyList,entryList,entryMap,false);
                else{
                    cloudTask.isCloudFinished = true;
                    mMcafeeFilterEngine.scan(entryList,entytyList,reportList);
                }

            }else{
                cloudTask.isCloudFinished = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadMcafeeCloud(final CloudTask cloudTask, List<ScanEntity> entityList, final List<ScanEntry> entryList, final Map<String, ScanEntry> entryMap, final boolean isTclCloudError) throws Exception {
        if (VirusEngine.engineForClean){
            mSpecialHandleForSpace.handleSpace(entityList,entryMap,mDelivery,mScanMessage,mCacheHandle,isTclCloudError);
            return;
        }
        VirusLog.e("uploadMcafeeCloud()");
        final List<ScanEntity> unspecifiedList = new ArrayList<>(20);
        final CopyOnWriteArrayList<Cache.CacheEntry> reportList = new CopyOnWriteArrayList<>();
        final List<ScanSource> sourceList = new LinkedList<>();
        for(ScanEntity entity:entityList){
            sourceList.add(entity.scanSource);
        }
        final long start = System.currentTimeMillis();
        final List<ScanTask> taskList = new ArrayList<>();
        final ScanTask task = mMcafeeCloudEngine.scan(sourceList, new ScanObserver() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onCompleted(int i) {
                cloudTask.isCloudFinished = true;
                try {
                    CloudProcessFlurryHelper.fluryMcAfeeCloudDataReport(sourceList, start, taskList);
                    //未知走Mcafee本地扫描流程
                    mMcafeeFilterEngine.scan(entryList,unspecifiedList,reportList);
                } catch (Exception engineError) {
                    engineError.printStackTrace();
                }
            }

            @Override
            public void onScanned(ScanSource scanSource, ScanResult scanResult) {
                int category = scanResult.getCategory();
                //这里是有可能多个线程回调这里的,所以如果cancel情况，不让它移除
                ScanEntry entry = entryMap.remove(scanSource.toString());
                if (entry == null) {
                    VirusLog.e("=== entry is null package is %s", scanSource.toString());
                    return;
                }
                if (mCanceled&&entry.priority!= ScanEntry.REAL_TIME)
                    return;
                Object cloudErrCode = scanResult.getMeta(ScanResult.CLOUD_SCAN_ERROR);
                if (cloudErrCode != null && (cloudErrCode instanceof Integer)) {
                    VirusLog.e("Cloud scan error code is %d, apk is %s", (Integer) cloudErrCode, scanSource.toString());
                    switch ((Integer) cloudErrCode) {
                        case ScanResult.CLOUD_SCAN_ERROR_COLLECT_SIGNATURE_FAILED:
                        case ScanResult.CLOUD_SCAN_ERROR_CONNECT_TIMEOUT:
                        case ScanResult.CLOUD_SCAN_ERROR_NO_NETWORK:
                        case ScanResult.CLOUD_SCAN_ERROR_NO_VALID_RESPONSE:
                        case ScanResult.CLOUD_SCAN_ERROR_PARSE_RESPONSE_FAILED:
                            if (isTclCloudError){
                                unspecifiedList.add(new ScanEntity(scanSource, Constants.CLOUD_RESULT_ERROR, Constants.CLOUD_RESULT_ERROR));//Mcafee云返回异常，添加到未知list
                            }else{
                                unspecifiedList.add(new ScanEntity(scanSource, Constants.CLOUD_RESULT_UNSPECIFIED, Constants.CLOUD_RESULT_ERROR));
                            }
                            break;
                        case ScanResult.CLOUD_SCAN_SUCCEED:
                            CloudProcessHelper.mcafeeSuccessDeal(mContext,mDelivery,mCacheHandle,mScanMessage,reportList,unspecifiedList,category,scanSource,entry,scanResult,isTclCloudError);
                            break;
                    }
                }

            }
        });
        taskList.add(task);
    }
}

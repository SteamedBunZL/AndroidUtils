package com.tcl.security.virusengine;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.intel.security.vsm.UpdateTask;
import com.intel.security.vsm.VirusScan;
import com.tcl.security.virusengine.cache.CacheHandle;
import com.tcl.security.virusengine.cache.database.impl.DatabaseCacheImpl;
import com.tcl.security.virusengine.cache.memory.impl.ScanMemoryCacheImpl;
import com.tcl.security.virusengine.engine.CloudFilterEngine;
import com.tcl.security.virusengine.engine.CloudSchedulingImpl;
import com.tcl.security.virusengine.engine.McafeeCloudEngine;
import com.tcl.security.virusengine.engine.McafeeFilterEngine;
import com.tcl.security.virusengine.entry.ScanEntry;
import com.tcl.security.virusengine.func_interface.CloudScheduling;
import com.tcl.security.virusengine.func_interface.IRealTimeScanListener;
import com.tcl.security.virusengine.func_interface.IScanListener;
import com.tcl.security.virusengine.func_interface.IScanScheduleCallback;
import com.tcl.security.virusengine.func_interface.ScanMessage;
import com.tcl.security.virusengine.func_interface.ScanResultDelivery;
import com.tcl.security.virusengine.network.BaseNetwork;
import com.tcl.security.virusengine.network.NetStateChangedReciver;
import com.tcl.security.virusengine.scan_strategy.AllScan;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Singleton
 * Created by Steve on 2016/4/29.
 */
public class VirusScanQueue{

    private final PriorityBlockingQueue<ScanEntry> mCacheQueue = new PriorityBlockingQueue<>();

    private final PriorityBlockingQueue<ScanEntry> mCloudScanQueue = new PriorityBlockingQueue<>();

    private final PriorityBlockingQueue<ScanEntry> mMacfeeScanQueue = new PriorityBlockingQueue<>();

    private CacheScanDispatcher mCacheScanDispatcher;

    private CloudScanDispatcher mCloudScanDispatcher;

    private CacheHandle mCacheHandle;

    private ScanResultDelivery mDelivery;

    private McafeeFilterEngine mMcafeeFilterEngine;

    private CloudFilterEngine mCloudFilterEngine;

    private ExecutorService mPool = Executors.newCachedThreadPool();

    private ScanMessage mScanMessage;

    private NetStateChangedReciver mNetChangeReciver;

    private VirusScan mVirusScan;

    private Context mContext;

    private volatile boolean isInitailized = false;

    private List<UpdateTask> updateList = new ArrayList<>();

    private volatile boolean isQuerying = false;

    private IScanScheduleCallback mVirusScanScheduleCallback;


    public void intialize(Context context){
        //如果已经初始化，不再初始化
        if (isInitailized){
            VirusLog.d("VirusScanQueue已经初始化，不再初始化");
            return;
        }
        this.mContext = context.getApplicationContext();
        //云平台SDK初始化,外部需要提供tlog.aar否则会崩溃
        this.mCacheHandle = new DefaultCacheHandle(new ScanMemoryCacheImpl(),new DatabaseCacheImpl(mContext));
        this.mDelivery = new DefaultVirusScanDeliveryImpl(new DefaultDataReport());
        this.mScanMessage = new VirusScanMessage();
        this.mMcafeeFilterEngine = new McafeeFilterEngine(context,mCacheHandle,mDelivery,mScanMessage);
        McafeeCloudEngine cloudEngine = new McafeeCloudEngine(context);
        CloudScheduling cloudScheduling = new CloudSchedulingImpl(mContext,mMcafeeFilterEngine,cloudEngine,mCacheHandle,mDelivery,mScanMessage);
        this.mCloudFilterEngine = new CloudFilterEngine(context,mMacfeeScanQueue,mCloudScanQueue,mDelivery,mCacheHandle,cloudEngine, mMcafeeFilterEngine,mScanMessage,cloudScheduling);
        this.mCacheScanDispatcher =  new CacheScanDispatcher(mCacheQueue,mCloudScanQueue,mCloudFilterEngine,mCacheHandle,mDelivery,mScanMessage);
        BaseNetwork.getInstance().init(mContext,null);
        //只有进程第一次启动时，进行缓存拉取
        mPool.execute(new CacheDrag());
        //TCL云引擎初始化
        start();

    }

    /**
     * 获取扫描信息
     * @return
     */
    public ScanMessage getScanMessage(){
        return mScanMessage;
    }

    public boolean isVirusQuerying(){
       return ((DefaultVirusScanDeliveryImpl)getDelivery()).isVirusQuerying;
    }

    public void setRealTimeListener(String engine,IRealTimeScanListener listener){
        ((DefaultVirusScanDeliveryImpl)getDelivery()).setRealTimeListener(engine,listener);
    }

    public void setScanListener(IScanListener listener){
        ((DefaultVirusScanDeliveryImpl)getDelivery()).setScanListener(listener);
    }

    /**
     * 获取VirusScan
     * @return
     */
    public VirusScan getVirusScan() {
        return mVirusScan;
    }

    /**
     * 初始化VirusScan
     * @param mVirusScan
     */
    public void setVirusScan(VirusScan mVirusScan) {
        this.mVirusScan = mVirusScan;
    }


    public boolean isQuerying() {
        return isQuerying;
    }

    public void setQuerying(boolean querying) {
        isQuerying = querying;
    }

    /**
     * 获取线程池
     * @return
     */
    public ExecutorService getThreadPool() {
        return mPool;
    }


    public List<UpdateTask> getUpdateList() {
        return updateList;
    }

    private void start(){
        stop();
        registerReceiver();

        VirusLog.d("invoked");

        mCacheScanDispatcher = new CacheScanDispatcher(mCacheQueue,mCloudScanQueue,mCloudFilterEngine,mCacheHandle,mDelivery,mScanMessage);
        mCacheScanDispatcher.start();

        mCloudScanDispatcher = new CloudScanDispatcher(mCloudScanQueue,mCloudFilterEngine);
        mCloudScanDispatcher.start();

        isInitailized = true;
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetChangeReciver = new NetStateChangedReciver();
        mContext.registerReceiver(mNetChangeReciver, filter);
    }

    private void unregisterReceiver() {
        if (mNetChangeReciver != null) {
            mContext.unregisterReceiver(mNetChangeReciver);
        }
    }

    public void launch(){
        //取消上次扫描
        cancel();
        //设置本次扫描时间
        mScanMessage.setThisScanTime(System.currentTimeMillis());
        VirusLog.w("===本次扫描开始时间UTC %d",System.currentTimeMillis());
        mCloudFilterEngine.prepare();
        mMcafeeFilterEngine.prepare();
    }

    private void checkForRealtimeScan(){
        if (mCacheScanDispatcher==null||!mCacheScanDispatcher.isAlive()){
            mCacheScanDispatcher = new CacheScanDispatcher(mCacheQueue,mCloudScanQueue,mCloudFilterEngine,mCacheHandle,mDelivery,mScanMessage);
            mCacheScanDispatcher.start();
        }

        if (mCloudScanDispatcher==null||!mCloudScanDispatcher.isAlive()){
            //调度线程，单线程
            mCloudScanDispatcher = new CloudScanDispatcher(mCloudScanQueue,mCloudFilterEngine);
            mCloudScanDispatcher.start();
        }

    }

    private void stop(){

        unregisterReceiver();

       if (mCacheScanDispatcher!=null)
           mCacheScanDispatcher.quit();

        if (mCloudScanDispatcher!=null)
            mCloudScanDispatcher.quit();

    }

    /**
     * 取消扫描
     *
     */
    public void cancel(){
        //云引擎取消扫描
        if (mCloudFilterEngine!=null)
            mCloudFilterEngine.cancel();
        //本地引擎取消扫描
        if (mMcafeeFilterEngine !=null)
            mMcafeeFilterEngine.cancel();
        //缓存队列清空
        if (mCacheQueue!=null)
            mCacheQueue.clear();
        //云扫描队列清空
        if (mCloudScanQueue!=null)
            mCloudScanQueue.clear();
        //TODO mcafee队列清空？？？
        if (mMacfeeScanQueue !=null)
            mMacfeeScanQueue.clear();
        //把取消状态告诉delivery，供delivery使用
        mDelivery.postScanInfo(ScanResultDelivery.DELIVERY_EVENT_CANCEL,null);

    }

    public void add(Collection<ScanEntry> collection){
        mCacheQueue.addAll(collection);
    }

    public void add(ScanEntry entry){
        //Realtime 为新安装应用
        if (entry.priority== ScanEntry.REAL_TIME){
            checkForRealtimeScan();
            mCacheQueue.add(entry);
            return;
        }

        mCacheQueue.add(entry);
    }

    /**
     * 获取ScanResultDelivery
     * @return
     */
    public ScanResultDelivery getDelivery(){
        return mDelivery;
    }


    private class CacheDrag implements Runnable{

        @Override
        public void run() {
            if (mCacheHandle!=null)
                mCacheHandle.init();
        }
    }


    private VirusScanQueue(){}

    public static VirusScanQueue getInstance(){
        return SingleHolder.sInstance;
    }

    private static class SingleHolder{
        private static final VirusScanQueue sInstance = new VirusScanQueue();
    }

    public IScanScheduleCallback getVirusScanScheduleCallback() {
        if (mVirusScanScheduleCallback==null)
            return new AllScan.VirusScanScheduleImpl();
        return mVirusScanScheduleCallback;
    }

    public void setVirusScanScheduleCallback(IScanScheduleCallback virusScanScheduleCallback) {
        this.mVirusScanScheduleCallback = virusScanScheduleCallback;
    }

    public Context getContext() {
        return mContext;
    }
}

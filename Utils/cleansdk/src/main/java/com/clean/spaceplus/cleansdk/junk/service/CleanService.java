package com.clean.spaceplus.cleansdk.junk.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.clean.spaceplus.cleansdk.base.utils.system.SystemCacheManager;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkModel;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkEngine;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkEngineMsg;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkEngineWrapper;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkEngineWrapperMsg;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkEngineWrapperUpdateInfo;
import com.clean.spaceplus.cleansdk.setting.authorization.AuthorizationMgr;
import com.clean.spaceplus.cleansdk.util.SDCardUtil;

import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bruceliu
 * @Description: sdk 外部调用垃圾清理服务
 * @date 2016/8/16 15:45
 * @copyright TCL-MIG
 */
public class CleanService extends Service {

    private JunkEngineWrapper mStdJunkEngWrapper;
    private ICacheCallback mCacheCallback;
    private IAdDirCallback mAdDirCallback;
    private ISystemCacheCallback mSysCacheCallback;
    private IApkFileCallback mApkFileCallback;
    private IResidualCallback mResidualCallback;
    private IScanCallback mScanCallback;
    private ICleanCallback mCleanCallback;
    private IProcessCallback mProcessCallback;
    private long mTotalSizeShow = 0l;
    private long mTotalCheckScanSize=0l;
    private List<JunkModel> mJunkList;
    private int mAdTmpLeftCompleted = 0;
    private int mSysCacheCompleted = 0;
    private JunkEngineWrapperUpdateInfo mScanSizeInfo = null;

    IClean.Stub binder=new IClean.Stub(){
        @Override
        public void startScan() throws RemoteException {
            //开始扫描,会根据设置的ScanCallback来确定扫描的类型
            //startscan();
        }

        @Override
        public void setScanCacheCallback(ICacheCallback observer) throws RemoteException {
            //设置扫描应用缓存
            if(observer!=null){
                mStdJunkEngWrapper.mbScanSdCache=true;
                mCacheCallback=observer;
            }
        }

        @Override
        public void setScanAdDirCallback(IAdDirCallback observer) throws RemoteException {
            //设置扫描广告垃圾
            if(observer!=null){
                mAdTmpLeftCompleted=0;
                mStdJunkEngWrapper.mbScanAdDirCache=true;
                mAdDirCallback=observer;
            }
        }

        @Override
        public void setScanApkFileCallback(IApkFileCallback observer) throws RemoteException {
            //设置扫描无用安装包
            if(observer!=null){
                mStdJunkEngWrapper.mbScanApkFile=true;
                mApkFileCallback=observer;
            }
        }

        @Override
        public void setResidualCallback(IResidualCallback observer) throws RemoteException {
            //设置扫描卸载残留
            if(observer!=null){
                mStdJunkEngWrapper.mbScanRubbish=true;
                mResidualCallback=observer;
            }
        }

        @Override
        public void cleanScanCallback() throws RemoteException {
            mStdJunkEngWrapper.mbScanSdCache=false;
            mCacheCallback=null;
            mAdTmpLeftCompleted=0;
            mStdJunkEngWrapper.mbScanAdDirCache=false;
            mAdDirCallback=null;
            mSysCacheCompleted = 0;
            mStdJunkEngWrapper.mbScanSysCache=false;
            mSysCacheCallback=null;
            mStdJunkEngWrapper.mbScanApkFile=false;
            mApkFileCallback=null;
            mStdJunkEngWrapper.mbScanRubbish=false;
            mResidualCallback=null;
            mStdJunkEngWrapper.mbCallerScanProcess=false;
            mProcessCallback=null;
            mScanCallback=null;
        }

        @Override
        public void setScanSystemCacheCallback(ISystemCacheCallback observer) throws RemoteException {
            //设置扫描系统缓存
            if(observer!=null){
                mStdJunkEngWrapper.mbScanSysCache=true;
                mSysCacheCallback=observer;
            }
        }

        @Override
        public void setScanCallback(IScanCallback observer) throws RemoteException {
            if(observer!=null){
                mScanCallback=observer;
            }
        }

        @Override
        public void startClean() throws RemoteException {
            //开始清理
            startclean();
        }

        @Override
        public void setCleanCallback(ICleanCallback observer) throws RemoteException {
            if(observer!=null){
                mCleanCallback=observer;
            }
        }

        @Override
        public void setScanProcessCallback(IProcessCallback observer) throws RemoteException {
            if(observer!=null){
                mStdJunkEngWrapper.mbCallerScanProcess=true;
                mProcessCallback=observer;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //创建时候初始化垃圾清理引擎
        mStdJunkEngWrapper= JunkEngineWrapper.createNewEngine();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void startscan(){
        // 如果没有进行功能授权
        if (!AuthorizationMgr.getInstance().isAuthorized()) {
            endscan();
            return;
        }
        if(mStdJunkEngWrapper!=null){
            mStdJunkEngWrapper.notifyStop();

        }
        if(mStdJunkEngWrapper.getmObserverArray()!=null){
            mStdJunkEngWrapper.getmObserverArray().clear();
        }
        mStdJunkEngWrapper.setEngineStatus(JunkEngine.EM_ENGINE_STATUS.IDLE);
        mStdJunkEngWrapper.setObserver(mJunkEvent);
        mStdJunkEngWrapper.startScan(JunkEngineWrapper.JUNK_WRAPPER_SCAN_TYPE_STD, true);
    }

    private void endscan(){
        //扫出来的垃圾项
        mJunkList = mStdJunkEngWrapper.getStdJunkModelList(true, -1, true);
        //总的大小
        mTotalSizeShow = mStdJunkEngWrapper.getTotalScanSize();
        mTotalCheckScanSize=mStdJunkEngWrapper.getTotalCheckScanSize();
    }

    private void startclean(){
        List<JunkModel> list = new ArrayList<>();
        list.addAll(mJunkList);
        SystemCacheManager.cleanCheckCache(list);

        mStdJunkEngWrapper.setCleanItemList(list);
        mStdJunkEngWrapper.setCleanType(JunkEngineWrapper.JUNK_WRAPPER_SCAN_TYPE_STD);

        mStdJunkEngWrapper.startClean(false);
        mStdJunkEngWrapper.addObserver(mJunkEvent);
    }

    private boolean isOnlyScanProcess(){
        if(mProcessCallback!=null && mCacheCallback==null && mAdDirCallback==null
                && mSysCacheCallback==null && mApkFileCallback==null && mResidualCallback==null){
            return true;
        }
        return false;
    }

    private JunkEngine.JunkEventCommandInterface mJunkEvent = new JunkEngine.JunkEventCommandInterface() {

        @Override
        public void callbackMessage(int what, int arg1, int arg2, final Object obj) {
//            NLog.i(TAG,"CommandInterface:::Event"+what);
            switch (what) {
                case JunkEngineWrapperMsg.MSG_HANDLER_UPDATE_INFO:
                    if (null != obj) {
                        mScanSizeInfo = ((JunkEngineWrapperUpdateInfo) obj).copyValue(mScanSizeInfo);
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_UPDATE_COMING_SOON_SIZE:

                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_SCAN:
                    //所有的垃圾都扫描结束
                    endscan();
                    if(mScanCallback!=null){
                        try{
                            mScanCallback.scanFinish(mTotalSizeShow,mTotalCheckScanSize);
                        }catch (RemoteException e){

                        }

                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_SDCACHE_INFO:
                    //应用缓存扫描每一项的内容
                    if(mCacheCallback!=null){
                        try {
                            mCacheCallback.onScanItem((String) obj,0);
                        }
                        catch (Exception e){

                        }
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_ADV_INFO:
                    //广告垃圾扫描每一项的内容
                    if(mAdDirCallback!=null){
                        try {
                            mAdDirCallback.onScanItem((String) obj,0);
                        }
                        catch (Exception e){

                        }
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_SYSCACHE_INFO:
                    //系统缓存扫描每一项的内容
                    if(mSysCacheCallback!=null){
                        try {
                            mSysCacheCallback.onScanItem((String) obj,0);
                        }
                        catch (Exception e){

                        }
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_APKFILE_INFO:
                    //无用安装包扫描每一项的内容
                    if(mApkFileCallback!=null){
                        try {
                            mApkFileCallback.onScanItem((String) obj,0);
                        }
                        catch (Exception e){

                        }
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_RUBBLISH_INFO:
                    //卸载残留扫描每一项的内容
                    if(mResidualCallback!=null){
                        try {
                            mResidualCallback.onScanItem((String) obj,0);
                        }
                        catch (Exception e){

                        }
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_PROCESS_INFO:
                    //内存扫描每一项的内容
                    if(mProcessCallback!=null){
                        try {
                            mProcessCallback.onScanItem((String) obj,0);
                        }
                        catch (Exception e){

                        }
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_SCAN_STATUS_INFO:

                    break;
                case JunkEngineMsg.MSG_HANDLER_CLEAN_STATUS_INFO:

                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_PROCESS_SCAN: {
                    //内存扫描结束
                    if(mProcessCallback!=null){
                        if(isOnlyScanProcess()){
                            endscan();
                            if(mScanCallback!=null){
                                try{
                                    mScanCallback.scanFinish(mTotalSizeShow,mTotalCheckScanSize);
                                }catch (RemoteException e){

                                }
                            }
                            //如果是只是扫内存，扫完后需要把引擎状态设置为活动状态,并且结束扫描
                            mStdJunkEngWrapper.setEngineStatus(JunkEngine.EM_ENGINE_STATUS.IDLE);
                        }
                        try {
                            mProcessCallback.onProcessScanFinish(mScanSizeInfo.mProcessScanSize);
                        }catch (RemoteException e){

                        }
                    }
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_FINISH_TMP_FILES_SCAN:
                case JunkEngineMsg.MSG_HANDLER_FINISH_ADV_SCAN:
                    //广告垃圾扫描结束
                    mAdTmpLeftCompleted++;
                    if ( mAdTmpLeftCompleted >= 2) {
                        if(mAdDirCallback!=null){
                            try{
                                mAdDirCallback.onAdDirScanFinish(mStdJunkEngWrapper.getAdLeftTmpSize());
                            }
                            catch (Exception e){

                            }
                        }
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_LEFT_OVER_SCAN: {
                    //卸载残留扫描结束
                    if(mResidualCallback!=null){
                        try{
                            mResidualCallback.onResidualScanFinish(mStdJunkEngWrapper.getLeftSize());
                        }
                        catch (Exception e){

                        }
                    }
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_FINISH_SYS_SCAN:
                case JunkEngineMsg.MSG_HANDLER_FINISH_SYS_FIXED_SCAN:
                    //如果是没有SD卡的
                    if(!SDCardUtil.isHaveSDCard()){
                        if(mSysCacheCallback!=null){
                            try{
                                mSysCacheCallback.onCacheScanFinish(mStdJunkEngWrapper.getSystemCacheSize());
                            }
                            catch (Exception e){

                            }
                        }
                    }
                    else{
                        mSysCacheCompleted++;
                        if( mSysCacheCompleted >= 2) {
                            if(mSysCacheCallback!=null){
                                try{
                                    mSysCacheCallback.onCacheScanFinish(mStdJunkEngWrapper.getSystemCacheSize());
                                }
                                catch (Exception e){

                                }
                            }
                        }
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_SD_SCAN:
                    //SD卡扫描结束
                    if(mCacheCallback!=null){
                        try{
                            mCacheCallback.onCacheScanFinish(mStdJunkEngWrapper.getAppCacheSize());
                        }
                        catch (Exception e){

                        }
                    }

                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_APK_SCAN: {
                    //无用安装包扫描结束
                    if(mApkFileCallback!=null){
                        try{
                            mApkFileCallback.onApkFileScanFinish(mStdJunkEngWrapper.getApkSize());
                        }
                        catch (Exception e){

                        }
                    }
                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_UPDATE_RECENT_JUNK_SIZE:

                    break;
                case JunkEngineMsg.MSG_HANDLER_ADD_PROGRESS: {

                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_SD_CLEAN_FINISH://sd clean finish

                    break;
                case JunkEngineMsg.MSG_HANDLER_RUB_CLEAN_FINISH://rubbish clean finish
                    break;
                case JunkEngineMsg.MSG_HANDLER_APK_CLEAN_FINISH://无用安装包

                    break;
                case JunkEngineMsg.MSG_HANDLER_SYS_CLEAN_FINISH://system clean finish

                    break;
                case JunkEngineMsg.MSG_HANDLER_ROOT_CACHE_CLEAN_FINISH://root clean finish

                    break;
                case JunkEngineMsg.MSG_HANDLER_SYS_FIXED_CLEAN_FINISH://fixed finish

                    break;
                case JunkEngineMsg.MSG_HANDLER_MEDIA_CLEAN_FINISH://media file
                    //暂不统计媒体文件
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_COMPRESS_SCRSHOTS://screenshots compress finish
                    //暂不统计截图压缩
                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_CLEAN:
                    //所有的清理项都清理完成
                    if(mCleanCallback!=null){
                        try{
                            mCleanCallback.cleanFinish();
                        }catch (RemoteException e){

                        }
                    }
                    break;
                case JunkEngineMsg.MSG_HANDLER_UPDATE_CLEAN_BUTTON: {

                    break;
                }
                case JunkEngineMsg.MSG_HANDLER_REMOVE_DATA_ITEM:

                    break;
                case JunkEngineMsg.MSG_HANDLER_FINISH_CLEAN_FOR_CACHE:
                    break;
            }
        }
    };
}

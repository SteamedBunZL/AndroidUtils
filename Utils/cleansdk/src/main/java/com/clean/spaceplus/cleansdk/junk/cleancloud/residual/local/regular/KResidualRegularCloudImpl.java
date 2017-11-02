package com.clean.spaceplus.cleansdk.junk.cleancloud.residual.local.regular;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.db.SqlUtil;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.PkgQueryHfRegDirQueryTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.ResidualDirHfProvider;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudGlue;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudPathConverter;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudQueryExecutor;
import com.clean.spaceplus.cleansdk.junk.cleancloud.KCleanCloudQueryLogic;
import com.clean.spaceplus.cleansdk.junk.cleancloud.KSimpleGlobalTask;
import com.clean.spaceplus.cleansdk.junk.cleancloud.residual.ResidualLocalQuery;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.clean.spaceplus.cleansdk.util.StringUtils;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import space.network.cleancloud.CleanCloudDef;
import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.KResidualCloudQuery.DirQueryData;
import space.network.util.hash.KQueryMd5Util;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/21 13:41
 * @copyright TCL-MIG
 */
public class KResidualRegularCloudImpl implements KResidualRegularCloudQuery {

    private static final String TAG = KResidualRegularCloudImpl.class.getSimpleName();

    //////////////////////////////////////////////////////////////////////
    //自动释放db和线程的相关配置
    //10分钟内没有使用,db和线程就可以被释放
    private static final long CAN_FREE_RESOURCE_TIME = 10 * 60 * 1000;

    //先预测当前任务两分钟内可以完成，并且后面没有新的任务,如果预测准确,那么下次检查的时候就可以释放库和线程
    private static final long PREDICTION_WORKING_TIME = 2 * 60 * 1000;

    //////////////////////////////////////////////////////////////////////
    //上报相关配置，尽可能在空闲时上报
    //假定3分钟内没有新请求，就是空闲了,就上报
    private static final long REPORT_RESULT_INTERVAL_TIME = 3 * 60 * 1000;


    private boolean mIsInited = false;
    private String mLanguage = "en";
    private AtomicInteger mQueryIdSeed = new AtomicInteger();

    private KResidualCloudQuery.PackageChecker mPackageChecker = null;
    private CleanCloudQueryExecutor mQueryExecutor;
    private KResidualCloudRegularDirQueryLogic mResidualCloudDirQueryLogic;
    private KResudialRegularLocalQuery mResidualRegLocalQuery;
    private CleanCloudPathConverter mCleanCloudPathConverter = new CleanCloudPathConverter();

    private PackageDirFilter   mPackageFilter = new PackageDirFilter();

    private volatile boolean mIsReportTaskStarted   = false;
    private volatile boolean mIsMaintainTaskStarted = false;
    private volatile long mLastAccessTime = 0;

    private HashMap<Integer, Long> mQueryTimeStart = new HashMap<>();

    private static Object mAvailableRegularPathLock = new Object();
    private static ArrayList<String> mAvailableRegularPathHead = null;

    private ResidualDirHfProvider mPkgQueryHfProvider;





    public KResidualRegularCloudImpl(Context context, CleanCloudGlue cleanCloudGlue) {
        mQueryExecutor = new CleanCloudQueryExecutor();
        mResidualRegLocalQuery = new KResudialRegularLocalQuery(context, cleanCloudGlue);

        mResidualCloudDirQueryLogic = new KResidualCloudRegularDirQueryLogic(context);
        mPkgQueryHfProvider = ResidualDirHfProvider.getInstance();


    }

    /*
     * 初始化,目前没有判断是否重复初始化
     */
    @Override
    public boolean initialize(){
        synchronized (this) {
            if (!mIsInited) {
                mResidualCloudDirQueryLogic.initialize(mQueryExecutor);
                mIsInited = true;
            }
        }
        return true;
    }

    /*
     * 反初始化,调用反初始化让内部维护的线程退出，如果有没有完成的查询也会放弃
     */
    @Override
    public void unInitialize(){
        synchronized (this) {
            if (!mIsInited)
                return;

            mPackageChecker = null;
            mIsInited = false;
            mQueryExecutor.quit();

         /*   mHighFreqDbHolder.unInitDb();
            mResidualCloudDirQueryLogic.unInitialize();
            mResidualRegLocalQuery.unInitDb();*/

            mCleanCloudPathConverter.cleanPathEnumCache();//省内存
            mPackageFilter.unInit();//省内存


        }
    }

    @Override
    public boolean setLanguage(String language) {
        if (TextUtils.isEmpty(language))
            return false;

        mLanguage = language;
        mResidualRegLocalQuery.setLanguage(language);
        return true;
    }

    @Override
    public String getLanguage() {
        return mResidualRegLocalQuery.getLanguage();
    }

    /*
 * 设置包信息获取接口
 */
    public boolean setPackageChecker(KResidualCloudQuery.PackageChecker packageChecker){
        if (null == packageChecker) {
            return false;
        }

        mPackageChecker = packageChecker;

        return true;
    }

    /*
     * 设置sd卡根路径
     * @param path sd卡根路径
     */
    @Override
    public boolean setSdCardRootPath(String path){
        return mCleanCloudPathConverter.setSdCardRootPath(path);
    }

    /*
     * 获取设置进去的sd卡根路径
     * @return 返回sd卡根路径
     */
    @Override
    public String getSdCardRootPath(){
        return mCleanCloudPathConverter.getSdCardRootPath();
    }

    /*
     * 清除枚举目录的缓存
     */
    @Override
    public void cleanPathEnumCache(){
        mCleanCloudPathConverter.cleanPathEnumCache();
    }

    /*
     * 查询正则路径特征；
     * 同步接口：当函数退出时，查询任务也已经结束。
     * 纯本地查询：不会有网络请求。
     * 提示：请严格控制目录查询个数，因为会枚举输入目录的子目录，然后再进行正则匹配，会比较耗时。
     * @param dirnames 目录查询数据列表(要求为sdcard下的一级路径)
     * @param callback 回调接口，详细说明见IDirQueryCallback
     * @author 
     * @date 2014.12.03
     */
    @Override
    public boolean queryByDirName(
            int scanType,
            Collection<String> dirnames,
            KResidualCloudQuery.DirQueryCallback callback,
            boolean pureAsync,
            boolean asyncCallback){
        if (!mIsInited)
            return false;

        if (null == dirnames || null == callback || dirnames.isEmpty())
            return false;

        int queryId = getQueryId();
        long systime = System.currentTimeMillis();
        RegularDirQueryCallbackData callbackData = new RegularDirQueryCallbackData();
        callbackData.mCallback = callback;
        callbackData.mDirScanType = scanType;
        callbackData.mQueryIdForUser = queryId;
        callback.onGetQueryId(queryId);
        mQueryTimeStart.put(queryId, systime);
        return _queryByDirName(queryId, dirnames, callbackData, pureAsync, asyncCallback);
    }

    /*
     * 如果有未完成的查询，那么丢弃
     */
    @Override
    public void discardAllQuery(){
        mQueryExecutor.discardAllQuery();
        if (mIsMaintainTaskStarted) {
        }
        if (mIsReportTaskStarted) {
        }
    }

    /*
     * 等待扫描结束，注意不要在回调线程中调用
     * @param timeout 等待的超时时间
     * @param discardQueryIfTimeout 等待如果超时是否丢弃所有未完成的查询
     * @return 等待结果，结果含义见CleanCloudDef.WaitResultType中的详细说明
     */
    @Override
    public int waitForComplete(long timeout, boolean discardQueryIfTimeout, CleanCloudDef.ScanTaskCtrl ctrl){
        return mQueryExecutor.waitForComplete(timeout, discardQueryIfTimeout, ctrl);
    }

    /**
     * 获得cm能够处理的正则路径
     * @author 
     * @date 2014.12.24
     * */
    private ArrayList<String> getAvailableRegularPath(SQLiteDatabase mDb){
        if(mAvailableRegularPathHead == null){
            synchronized (mAvailableRegularPathLock){
                mAvailableRegularPathHead = new ArrayList<>();
                Cursor cursorSign = null;
                try{
                    if (mDb != null) {
                        //String sql = "select regdir from regdirquery";

                        StringBuffer buffer = new StringBuffer();
                        String[] selection = new String[]{PkgQueryHfRegDirQueryTable.REGDIR};
                        String sql = SqlUtil.appendSqlString(PkgQueryHfRegDirQueryTable.TABLE_NAME,selection);
                        NLog.d(TAG, "KResidualRegularCloudImpl getAvailableRegularPath sql = %s", sql);
                        cursorSign = mDb.rawQuery(sql, null);
                        if (cursorSign != null && cursorSign.getCount() > 0) {
                            while (cursorSign.moveToNext()) {
                                String path = cursorSign.getString(0);
                                int end = path.indexOf("(");
                                if(end > 0){
                                    path = path.substring(0, end);
                                    path = path.replaceAll("\\\\", "");
                                    if(path.endsWith("/")){
                                        path = path.substring(0, path.length()-1);
                                    }

                                    mAvailableRegularPathHead.add(path);
                                }else if(end == 0){
                                    mAvailableRegularPathHead.add(path);
                                }
                            }
                        }
                    }
                }catch(Exception e){
                    mAvailableRegularPathHead = null;
                }finally {
                    if (cursorSign != null) {
                        cursorSign.close();
                    }

                }
            }
        }
        return mAvailableRegularPathHead;
    }

    /*private class FreeDbAndThreadProcessor implements IdelMaintainTask.Processor {
        public boolean doWork() {
            return doFreeDbAndThreadWork();
        }
        public boolean scheduleTask(Runnable task, long delayTime) {
            return scheduleMaintainTask(task, delayTime);
        }

        public long getMaxIdelTime() {
            return CAN_FREE_RESOURCE_TIME;
        }
        public long getLastBusyTime() {
            return getLastAccessTime();
        }
        public long getPredictionWorkingTime() {
            return PREDICTION_WORKING_TIME;
        }
    }

    private class IdleReportTaskProcessor implements IdelMaintainTask.Processor {
        public boolean doWork() {
            return doReportWork();
        }
        public boolean scheduleTask(Runnable task, long delayTime) {
            return scheduleMaintainTask(task, delayTime);
        }
        public long getMaxIdelTime() {
            return REPORT_RESULT_INTERVAL_TIME;
        }
        public long getLastBusyTime() {
            return getLastAccessTime();
        }
        public long getPredictionWorkingTime() {
            return PREDICTION_WORKING_TIME;
        }
    }*/

    /**
     * 正则包名过滤器；通过此类的接口，可以方便的匹配正则包名，以及检测其是否为安装状态。
     * @author 
     * @date 2014.12.08
     * */
    private class PackageDirFilter implements KResidualRegularCloudQueryHelper.IPkgDirFilter{
        private volatile Collection<String> mAllPkgs = null;  // 所有的安装包列表
        private volatile HashSet<String> mPkgSet = null;      // 安装包列表的小写形式集合
        private volatile HashSet<String> mPkgMd5Set = null;
        private volatile HashSet<Long> mPkgMd5High64Set = null;

        /**
         * 初始化
         * */
        public void init(KResidualCloudQuery.PackageChecker packageChecker) {
            synchronized(this) {
                if (mPkgSet != null)
                    return;

                if (null == packageChecker)
                    return;

                HashSet<String> pkgSet = null;
                HashSet<String> pkgMd5Set = null;
                HashSet<Long> pkgMd5High64Set = null;
                Collection<String> pkgs = packageChecker.getAllPackageNames();
                if (pkgs != null && !pkgs.isEmpty()) {
                    MessageDigest md5 = KQueryMd5Util.getMd5Digest();
                    pkgSet = new HashSet<String>();
                    pkgMd5Set = new HashSet<String>();
                    pkgMd5High64Set = new HashSet<Long>();
                    for (String pkg : pkgs) {
                        if (TextUtils.isEmpty(pkg))
                            continue;

                        pkgSet.add(StringUtils.toLowerCase(pkg));
                        pkgMd5Set.add(KQueryMd5Util.getPkgQueryMd5(md5, pkg));
                        pkgMd5High64Set.add(KQueryMd5Util.getMD5High64BitFromString(md5, pkg));
                    }
                }
                mPkgSet      = pkgSet;
                mPkgMd5Set   = pkgMd5Set;
                mAllPkgs     = pkgs;
                mPkgMd5High64Set = pkgMd5High64Set;
            }
        }

        public void unInit() {
            synchronized(this) {
                if (mPkgSet != null) {
                    mPkgSet = null;
                }
            }
        }

        /**
         * 基于MD5高64位的检测接口;
         * @author 
         * @date 2014.11.06
         * */
///<DEAD CODE>///         public boolean isApkInstalledCheckByMD5High64(Collection<Long> pkgMD5High64){
//            HashSet<Long> pkgMd5Hihg64Set = null;
//            synchronized(this) {
//                pkgMd5Hihg64Set = mPkgMd5High64Set;
//            }
//
//            if (pkgMd5Hihg64Set == null || pkgMd5Hihg64Set.isEmpty()) {
//                // 防止无权限读取App列表的时候，我们不要误报残留。
//                return true;
//            }
//
//            boolean isAllEmpty = true;
//            for (Long md5 : pkgMD5High64) {
//                if (isAllEmpty)
//                    isAllEmpty = false;
//
//                if (pkgMd5Hihg64Set.contains(md5)) {
//                    return true;
//                }
//            }
//            //防止因数据异常发生误删
//            return isAllEmpty ? true : false;
//        }

        /**
         * 基于MD5的检测接口;
         * @author 
         * @date 2014.11.06
         * */
///<DEAD CODE>///         public boolean isApkInstalledCheckByMD5(Collection<String>  allPkgNameMD5) {
//            HashSet<String> pkgMd5Set = null;
//            synchronized(this) {
//                pkgMd5Set = mPkgMd5Set;
//            }
//
//            if (pkgMd5Set == null || pkgMd5Set.isEmpty()) {
//                // 防止无权限读取App列表的时候，我们不要误报残留。
//                return true;
//            }
//
//            boolean isAllEmpty = true;
//            for (String md5 : allPkgNameMD5) {
//                if (TextUtils.isEmpty(md5))
//                    continue;
//
//                if (isAllEmpty)
//                    isAllEmpty = false;
//
//                if (pkgMd5Set.contains(md5)) {
//                    return true;
//                }
//            }
//            //防止因数据异常发生误删
//            return isAllEmpty ? true : false;
//        }

        /**
         * 根据正则路径中的正则匹配部分，匹配基于正则的pkg{?}检测接口;
         * @param dirRegularGroup : 明文路径与高频库正则路径的正则匹配部分;
         * @param pkgRegexs : 正则路径对应的正则pkg列表，即pkg{?};
         * @author 
         * @date 2014.12.08
         * */
        public boolean isApkInstalledCheckByRegex(String dirRegularGroup, Collection<String> pkgRegexs) {
            /**
             * 获取特定的pkg.
             * */
            String pkg = getRegularMatchPackage(dirRegularGroup, pkgRegexs);
            if(TextUtils.isEmpty(pkg)){
                return false;
            }

            // check pkg is installed.
            if (isPackageInstalled(pkg)) {
                return true;
            }
            //防止因数据异常发生误删
            return false;
        }

        /**
         * 根据正则路径中的正则匹配部分，匹配基于正则的pkg{?}检测接口;
         * PS:目前只处理pkgRegexs的第一个元素，即只支持正则路径对应一个正则包名;
         * @param dirRegularGroup : 明文路径与高频库正则路径的正则匹配部分;
         * @param pkgRegexs : 正则路径对应的正则pkg列表，即pkg{?};
         * @author 
         * @date 2014.12.08
         * */
        private String getRegularMatchPackage(String dirRegularGroup, Collection<String> pkgRegexs){
            Collection<String> allpkgs = null;
            String pkg = null;

            if(dirRegularGroup == null || pkgRegexs == null || pkgRegexs.isEmpty()){
                return null;
            }

            synchronized(this) {
                allpkgs = mPkgSet;
            }
            if (allpkgs == null || allpkgs.isEmpty()) {
                // 防止无权限读取App列表的时候，我们不要误报残留。
                return null;
            }

            // convert regular packages to normal packages.
            for(String regPkg : pkgRegexs){
                pkg = convertRegularPackage(dirRegularGroup, regPkg);
                break;
            }

            return pkg;
        }

        /**
         * 将pkg命中{?}部分，替换为正则匹配部分(dirRegularGroup);
         * @author 
         * @date 2014.12.08
         * */
        private String convertRegularPackage(String dirRegularGroup, String pkgRegular){
            if(dirRegularGroup == null || TextUtils.isEmpty(pkgRegular)){
                return null;
            }

            String pkg = null;
            int len = pkgRegular.length();
            int idx1 = -1;
            int idx2 = -1;
            for(int i = 0; i < len; i++){
                char str = pkgRegular.charAt(i);
                if(idx1 >= 0){
                    if(str == '}'){
                        idx2 = i;
                    }
                }else{
                    if(str == '{'){
                        if(i+1 < len){
                            char c = pkgRegular.charAt(i+1);
                            if(c == '0'){
                                idx1 = i;
                            }
                        }
                    }
                }
            }

            if(idx1 >= 0 && idx2 > idx1 && idx2 < len){
                String str1 = pkgRegular.substring(0, idx1);
                String str2 = "";
                if(idx2+1 < len){
                    str2 = pkgRegular.substring(idx2+1, len);
                }

                if(!TextUtils.isEmpty(str1)){
                    if(!TextUtils.isEmpty(str2)){
                        pkg = str1 + dirRegularGroup + str2;
                    }else{
                        pkg = str1 + dirRegularGroup;
                    }
                }else{
                    if(!TextUtils.isEmpty(str2)){
                        pkg = dirRegularGroup + str2;
                    }else{
                        pkg = dirRegularGroup;
                    }
                }
            }

            return pkg;
        }

        /**
         * 检测一个具体的pkg，是否安装在手机设备之上;
         * @author 
         * @date 2014.12.08
         * */
        private boolean isPackageInstalled(String strPkg) {
            if (TextUtils.isEmpty(strPkg))
                return false;

            HashSet<String> pkgSet = mPkgSet;
            if (pkgSet == null)
                return false;

            return pkgSet.contains(strPkg);
        }

        private final static String ANDROID_DATA_DIR = "android/data/";
        private final static String ANDROID_OBB_DIR = "android/obb/";

        private String getNeedFilterSubDirName(final String prefix, String path) {
            if (!path.startsWith(prefix))
                return null;

            String dirname = null;
            int pos = path.indexOf('/', prefix.length());
            if (pos != -1) {
                dirname = path.substring(prefix.length(), pos);
            } else {
                dirname = path.substring(prefix.length());
            }
            return dirname;
        }

        @Override
        public boolean isInFilter(String addPath) {
            String addPathLowerCase = StringUtils.toLowerCase(addPath);
            String dirname = getNeedFilterSubDirName(ANDROID_DATA_DIR, addPathLowerCase);
            if (TextUtils.isEmpty(dirname)) {
                dirname = getNeedFilterSubDirName(ANDROID_OBB_DIR, addPathLowerCase);
                if (TextUtils.isEmpty(dirname))
                    return false;
            }

            return isPackageInstalled(dirname);
        }
    }

    /**
     * 查询任务的回调信息类；
     * @author 
     * @date 2014.12.04
     * */
    private static class RegularDirQueryCallbackData {
        public KResidualCloudQuery.DirQueryCallback mCallback;
        public int mDirScanType;
        public int mCurrentQueryId;
        public int mQueryIdForUser;
        public boolean mRealComplete;
        public TreeMap<String, KResidualCloudQuery.DirQueryData> mLangMissmatchResult;
    }

    /**
     * 查询任务派生类；
     * @author 
     * @date 2014.12.04
     * */
    private class KResidualCloudRegularDirQueryLogic extends KCleanCloudQueryLogic<KResidualCloudQuery.DirQueryData, RegularDirQueryCallbackData> {
        public KResidualCloudRegularDirQueryLogic(Context context) {
            super(context);
        }
        //@Override
        //protected boolean localQuery(DirQueryData data, IDirQueryCallback callback) {
        //	return KResidualCloudQueryImpl.this.localDirQuery(data, callback);
        //}

        @Override
        protected boolean localQuery(final int queryId, Collection<KResidualCloudQuery.DirQueryData> datas, RegularDirQueryCallbackData callback) {
            return KResidualRegularCloudImpl.this.localDirQuery(datas, callback);
        }

        @Override
        protected boolean netQuery(final int queryId, Collection<KResidualCloudQuery.DirQueryData> datas, RegularDirQueryCallbackData callback) {
            return true;
        }

        @Override
        protected boolean isNeedNetQuery(KResidualCloudQuery.DirQueryData data, RegularDirQueryCallbackData callback) {
            return false;
        }

        @Override
        protected void onGetQueryResult(
                final Collection<KResidualCloudQuery.DirQueryData> datas,
                final RegularDirQueryCallbackData callback,
                final boolean queryComplete,
                final int queryId,
                final int dataTotalCount,
                final int dataCurrentCount) {
            KResidualRegularCloudImpl.this.onGetDirQueryResult(
                    datas,
                    callback,
                    queryComplete,
                    queryId,
                    dataTotalCount,
                    dataCurrentCount);
        }

        @Override
        protected boolean checkStop(RegularDirQueryCallbackData callback) {
            return callback.mCallback.checkStop();
        }
    }

    /**
     * 获取查询任务的ID
     * @author 
     * @date 2014.12.04
     * */
    private int getQueryId() {
        return mQueryIdSeed.incrementAndGet();
    }

    /**
     * 标示查询任务处于工作状态
     * @author 
     * @date 2014.12.04
     * */
    private void markWorking() {
        setLastAccessTime();
        startMaintainTask();
        startReportTask();
    }

    private void startMaintainTask() {
        if (mIsMaintainTaskStarted)
            return;


    }

    private void startReportTask() {
        if (mIsReportTaskStarted)
            return;

    }

    public void setLastAccessTime() {
        mLastAccessTime = System.currentTimeMillis();
    }

    public long getLastAccessTime() {
        return mLastAccessTime;
    }

    private boolean scheduleMaintainTask(Runnable task, long delayMillis) {
        if (!mIsInited)
            return false;

        KSimpleGlobalTask.getInstance().removeCallbacks(task);
        return KSimpleGlobalTask.getInstance().postDelayed(task, delayMillis);
    }

    /**
     * 释放工作线程与database;
     * @author 
     * @date 2014.12.04
     * */
    private boolean doFreeDbAndThreadWork() {
       /* boolean freeSuccess = mResidualRegLocalQuery.tryUnInitDb();
        if (freeSuccess) {
            mCleanCloudPathConverter.cleanPathEnumCache();//省内存
            mPackageFilter.unInit();//省内存
            mQueryExecutor.safeQuit();//线程也退出
            mIsMaintainTaskStarted = false;
        }
        return freeSuccess;*/
        return true;
    }

    /**
     * 进行扫描检出统计上报工作
     * @author 
     * @date 2014.12.04
     * */
    private boolean doReportWork() {

        return true;
    }

    /**
     * 根据查询关键字，创建查询数据队列；
     * 只遍历查询关键字对应目录下面一级的子目录;
     * @param dirnames : : sdcard下的一级目录;
     * @author 
     * @date 2014.12.04
     * */
    private Collection<DirQueryData> getDirQueryDatas(Collection<String> dirnames) {
        ArrayList<DirQueryData> result = new ArrayList<>(dirnames.size());
        String rootPath = getSdCardRootPath() + File.separator;

        for (String dirname : dirnames) {
            if(!isPossibleRegularPath(dirname)){
                continue;
            }

            DirQueryData data = KResidualRegularCloudQueryHelper.getDirQueryDatas(dirname, mLanguage);
            if(data != null){
                result.add(data);
            }

            String fullPath = rootPath + dirname;
            PathOperFunc.StringList subPaths = KRegularPathHelper.enumPath(fullPath);
            if(subPaths == null){
                continue;
            }

            Iterator<String> it =  subPaths.iterator();
            while(it.hasNext()){
                String subdir = it.next();
                fullPath = dirname + File.separator + subdir;
                if(!isPossibleRegularPath(fullPath)){
                    continue;
                }
                data = KResidualRegularCloudQueryHelper.getDirQueryDatas(fullPath, mLanguage);
                if(data != null){
                    result.add(data);
                }
            }
        }
        return result;
    }

    /**
     * 检测是否是可能的检出路径
     * @author 
     * @date 2014.12.24
     * */
    private boolean isPossibleRegularPath(String path){
        if(!TextUtils.isEmpty(path)){
            getAvailableRegularPath(mPkgQueryHfProvider.getDatabase());
            synchronized (mAvailableRegularPathLock){
                if(mAvailableRegularPathHead == null || mAvailableRegularPathHead.isEmpty()){
                    return false;
                }

                for(String regPath : mAvailableRegularPathHead){
                    if(!TextUtils.isEmpty(regPath)){
                        if(path.startsWith(regPath)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 执行查询任务
     * @param dirnames : sdcard的一级目录;
     * @author 
     * @date 2014.12.04
     * */
    private boolean _queryByDirName(int queryId, Collection<String> dirnames, RegularDirQueryCallbackData callback, boolean pureAsync, boolean asyncCallback) {
        markWorking();
        callback.mCurrentQueryId = queryId;
        Collection<KResidualCloudQuery.DirQueryData> querydatas = getDirQueryDatas(dirnames);
        if(querydatas == null || querydatas.isEmpty()){
            long end = System.currentTimeMillis();
            long start = mQueryTimeStart.get(queryId);
            return false;
        }
        NLog.d(ResidualLocalQuery.TAG, "KResidualRegularCloudImpl _queryByDirName ");
        return mResidualCloudDirQueryLogic.query(querydatas, callback, pureAsync, false, asyncCallback, queryId);
    }

    /**
     * query dir in regular dir table of high frequency db.
     * @author 
     * @date 2014.12.03
     * */
    private boolean localDirQuery(Collection<KResidualCloudQuery.DirQueryData> datas, RegularDirQueryCallbackData callback){
        mResidualRegLocalQuery.queryByDir(callback.mCurrentQueryId, datas);
        return true;
    }

    /**
     * handle the result query from high frequency db.
     * @author 
     * @date 2014.12.03
     * */
    private void onGetDirQueryResult(
            final Collection<KResidualCloudQuery.DirQueryData> datas,
            final RegularDirQueryCallbackData callback,
            final boolean queryComplete,
            final int queryId,
            final int dataTotalCount,
            final int dataCurrentCount){
        int hitCountTotal = 0;
        int hitCountHF    = 0;
        mPackageFilter.init(mPackageChecker);
        ArrayList<KResidualCloudQuery.DirQueryData> newResultDatas = null;
        boolean isNeedNewResultDatas = false;
        int currentCount = 0;
        for (DirQueryData data : datas) {

            if (data.mErrorCode == 0 && data.mResult.mQueryResult != KResidualCloudQuery.PkgResultType.UNKNOWN) {
                ++hitCountTotal;
                switch(data.mResultSource) {
                    case KResidualCloudQuery.ResultSourceType.HFREQ :
                        ++hitCountHF;
                        break;
                    default:
                }
            }

            KResidualRegularCloudQuery.RegularDirQueryInnerData innerData = (KResidualRegularCloudQuery.RegularDirQueryInnerData)data.mInnerData;
            if (!KResidualCloudQuery.TestFlagUtil.isTestSign(data.mResult.mTestFlag) && innerData.mFilterSubDirDatas != null && !innerData.mFilterSubDirDatas.isEmpty()) {
                data.mResult.mFilterSubDirs = new ArrayList<>(innerData.mFilterSubDirDatas.size());
                for (KResidualCloudQuery.FilterDirData filterData : innerData.mFilterSubDirDatas) {
                    if(filterData.mCleanType == KResidualCloudQuery.DirCleanType.CAREFUL || filterData.mCleanType == KResidualCloudQuery.DirCleanType.PURE_WHITE_FILTER){
                        String path = mCleanCloudPathConverter.getDirPath(filterData.mPath);
                        if (!TextUtils.isEmpty(path)) {
                            String fillPath = mCleanCloudPathConverter.getFullPathFromRelativePath(path);
                            KResidualCloudQuery.FilterDirData vaildFilterData = new KResidualCloudQuery.FilterDirData();
                            vaildFilterData.mPath = fillPath;
                            vaildFilterData.mSingId = filterData.mSingId;
                            vaildFilterData.mCleanType = filterData.mCleanType;
                            data.mResult.mFilterSubDirs.add(vaildFilterData);
                        }
                    }
                }
            }

            boolean isDetected = fillDetectResult(data, callback);

            //////////////////////////////////////////////////////////
            boolean isDataCanCallback = true;
            if (!isDetected) {
                isDataCanCallback = false;
                if (!isNeedNewResultDatas) {
                    isNeedNewResultDatas = true;
                }
            }
            if (isNeedNewResultDatas && newResultDatas == null) {
                newResultDatas = new ArrayList<>(datas.size());
                int i = 0;
                for (DirQueryData data2 : datas) {
                    if (i < currentCount) {
                        newResultDatas.add(data2);
                    }
                }
            }

            if (newResultDatas != null) {
                if (isDataCanCallback) {
                    newResultDatas.add(data);
                }
            }

            ++currentCount;
            ///////////////////////////////////////
        }

        /**
         * 云端上报
         * @author 
         * @date 2014.12.09
         * */

        if (callback != null) {
            if (queryId == callback.mCurrentQueryId && queryComplete) {
                callback.mRealComplete = true;
            }
            Collection<DirQueryData> resultDatas = (newResultDatas != null) ? newResultDatas : datas;
            /**
             * infoc上报
             * @author 
             * @date 2014.12.10
             * */

            postDirQueryResult(mQueryExecutor, callback, resultDatas);
        }
    }

    /**
     * 检测能够匹配正则路径对应的pkg{?}是否卸载;
     * doing.
     * @author 
     * @date 2014.12.05
     * */
    private boolean fillDetectResult(DirQueryData data, RegularDirQueryCallbackData callbackData) {

        boolean isTestSign = KResidualCloudQuery.TestFlagUtil.isTestSign(data.mResult.mTestFlag);

        if (data.mErrorCode != 0){
            return false;
        }

        if (!KResidualCloudQuery.DirQueryResultUtil.isHavePackageList(data.mResult)){
            return false;
        }

        if (!isNeedScan(data.mResult.mCleanType, callbackData.mDirScanType)) {
            return false;
        }

        if (data.mResult.mPackageRegexs != null && !data.mResult.mPackageRegexs.isEmpty() && ((RegularDirQueryInnerData)(data.mInnerData)).mRegularGroup != null) {
            if(mPackageFilter.isApkInstalledCheckByRegex(((RegularDirQueryInnerData)(data.mInnerData)).mRegularGroup, data.mResult.mPackageRegexs)){
                return false;
            }
        }else{
            return false;
        }

        ((KResidualRegularCloudQuery.RegularDirQueryInnerData)data.mInnerData).misDetect = true;
        if (isTestSign) {
            //todo 测试特征
            data.mIsDetected = false;

            String path = this.mCleanCloudPathConverter.getFullPathFromRelativePath(data.mDirName);

        } else{
            data.mIsDetected = true;
        }
        return true;
    }

    /**
     * 检查检出特征的清理类型与扫描类型是否匹配;
     * */
    private boolean isNeedScan(int cleanType, int scanType) {
        boolean result = false;
        switch (scanType) {
            case KResidualCloudQuery.DirScanType.DIR_STANDARD_SCAN :
                if (KResidualCloudQuery.DirCleanType.SUGGESTED == cleanType || KResidualCloudQuery.DirCleanType.SUGGESTED_WITH_FILTER == cleanType) {
                    result = true;
                }
                break;
            case KResidualCloudQuery.DirScanType.DIR_ADVANVCED_SCAN :
                if (KResidualCloudQuery.DirCleanType.CAREFUL == cleanType || KResidualCloudQuery.DirCleanType.CAREFUL_WITH_FILTER == cleanType) {
                    result = true;
                }
                break;
            case KResidualCloudQuery.DirScanType.DIR_ALL_SCAN :
                if (KResidualCloudQuery.DirCleanType.SUGGESTED == cleanType
                        || KResidualCloudQuery.DirCleanType.SUGGESTED_WITH_FILTER == cleanType
                        || KResidualCloudQuery.DirCleanType.CAREFUL == cleanType
                        || KResidualCloudQuery.DirCleanType.CAREFUL_WITH_FILTER == cleanType) {
                    result = true;
                }
                break;
            default:
        }
        return result;
    }

    /**
     * 将查询结果回调给调用方;
     * @author 
     * @date 2014.12.05
     * */
    private void postDirQueryResult(CleanCloudQueryExecutor queryExecutor,
                                    final RegularDirQueryCallbackData callback,
                                    final Collection<DirQueryData> directCallbackData) {
        if (callback.mRealComplete) {
        }
        queryExecutor.post(CleanCloudQueryExecutor.CALLBACK_RUNNER_2, new Runnable() {
            @Override
            public void run() {
//                for (DirQueryData data : directCallbackData) {
//                    data.mResult.mShowInfo.mAlertInfo = null;
//                }
                callback.mCallback.onGetQueryResult(callback.mQueryIdForUser, directCallbackData, callback.mRealComplete);
            }
        });
    }

    /**
     * 上报检出结果，以及检出耗时;
     * @author 
     * @date 2014.12.10
     * */
    private void reportQueryInfoc(int queryID, Collection<KResidualCloudQuery.DirQueryData> datas){
        long end = System.currentTimeMillis();

        /**
         * 概率上报：10%
         * */
        int random = (int)(Math.random()*10000);
        if(random > 1000){
            return;
        }

        if(queryID >= 0){
            /**
             * report scan time.
             * */
            String sTableName = "cm_cleancloud_residualreg_timeusage";
            int timecost = 0;
            try {
                Long start = mQueryTimeStart.get(queryID);
                if(start != null && start.longValue() <= end){
                    timecost = (int)(end - start);
                }
                String sParam = "costtime=" + timecost;
                mQueryTimeStart.remove(queryID);
            } catch (Exception e) {
            }

            if(datas != null && !datas.isEmpty()){
                /**
                 * report scan result.
                 * */
                sTableName = "cm_cleancloud_residualreg_find_id";
                String ids = "";
                int count = 0;
                for (KResidualCloudQuery.DirQueryData data : datas){
                    if(count >= 25){
                        break;
                    }
                    if(data != null && data.mResult != null){
                        ids += data.mResult.mSignId+",";
                        count++;
                    }
                }
                String sParam = "ids=" + ids + "&ccount=" + count;
            }
        }
    }

}

package com.clean.spaceplus.cleansdk.junk.cleancloud.residual.cloud;

import android.content.Context;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudGlue;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudPathConverter;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudPref;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudQueryExecutor;
import com.clean.spaceplus.cleansdk.junk.cleancloud.KCleanCloudQueryLogic;
import com.clean.spaceplus.cleansdk.junk.cleancloud.KSimpleGlobalTask;
import com.clean.spaceplus.cleansdk.junk.cleancloud.residual.ResidualLocalQuery;
import com.clean.spaceplus.cleansdk.junk.cleancloud.residual.local.regular.KResidualRegularCloudImpl;
import com.clean.spaceplus.cleansdk.junk.cleancloud.residual.local.rule.KResidualLocalRuleImpl;
import com.clean.spaceplus.cleansdk.junk.mgmt.JunkCloudByDirMgmt;
import com.clean.spaceplus.cleansdk.junk.mgmt.JunkCloudByPkgMgmt;
import com.clean.spaceplus.cleansdk.util.StringUtils;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import space.network.cleancloud.CleanCloudDef;
import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.MultiTaskTimeCalculator;
import space.network.cleancloud.core.residual.KResidualCommonData;
import space.network.cleancloud.core.residual.KResidualCommonData.DirQueryInnerData;
import space.network.cleancloud.core.residual.KResidualCommonData.PkgQueryInnerData;
import space.network.util.compress.EnDeCodeUtils;
import space.network.util.hash.KQueryMd5Util;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/25 16:10
 * @copyright TCL-MIG
 */
public class ResidualCloudQueryImpl implements KResidualCloudQuery {
    public  static final String TAG = ResidualCloudQueryImpl.class.getSimpleName();
    //自动释放db和线程的相关配置
    //10分钟内没有使用,db和线程就可以被释放
    private static final long CAN_FREE_RESOURCE_TIME = 10 * 60 * 1000;
    //for test
    //public static final long CAN_FREE_RESOURCE_TIME = 10 * 1000;//30 * 60 * 1000;

    //先预测当前任务两分钟内可以完成，并且后面没有新的任务,如果预测准确,那么下次检查的时候就可以释放库和线程
    private static final long PREDICTION_WORKING_TIME = 2 * 60 * 1000;

    //上报相关配置，尽可能在空闲时上报  假定3分钟内没有新请求，就是空闲了,就上报
    private static final long REPORT_RESULT_INTERVAL_TIME = 3 * 60 * 1000;

    private Context mConetxt;
    private boolean mIsInited = false;
    private String mLanguage = "en";
    private PackageChecker mPackageChecker = null;
    private PackageDirFilter   mPackageFilter = new PackageDirFilter();
    private CleanCloudQueryExecutor mQueryExecutor;
    private KResidualCloudDirQueryLogic mResidualCloudDirQueryLogic;
    private KResidualCloudPkgQueryLogic mResidualCloudPkgQueryLogic;
    private ResidualLocalQuery mResidualLocalQuery;
    private CleanCloudPathConverter mCleanCloudPathConverter = new CleanCloudPathConverter();
    private AtomicInteger mQueryIdSeed = new AtomicInteger();

    private volatile boolean mIsReportTaskStarted   = false;
    private volatile boolean mIsMaintainTaskStarted = false;
    private volatile long mLastAccessTime = 0;

    private CleanCloudGlue mCleanCloudGlue;

    private volatile boolean mNeedNetQuery = false;
    /**
     * regular dir query
     * */
    private KResidualRegularCloudImpl mRegularQueryImpl = null;
    private KResidualLocalRuleImpl mResudialLocalRuleImpl = null;
    private JunkCloudByDirMgmt dirMgmt;
    private JunkCloudByPkgMgmt pkgMgmt;

    /**
     * 构造函数
     * @date 2014.12.08
     * */
    public ResidualCloudQueryImpl(Context context, CleanCloudGlue cleanCloudGlue, boolean needNetQuery) {
        mConetxt = context;
        mCleanCloudGlue = cleanCloudGlue;
        mQueryExecutor = new CleanCloudQueryExecutor();
        mResidualLocalQuery = new ResidualLocalQuery();
        mResidualLocalQuery.setCacheLifeTime(CleanCloudPref.getInstanse().getCacheLifetime());

        mNeedNetQuery = needNetQuery;
        mResidualCloudDirQueryLogic = new KResidualCloudDirQueryLogic(context);
        mResidualCloudPkgQueryLogic = new KResidualCloudPkgQueryLogic(context);

        // regular dir query.
        mRegularQueryImpl = new KResidualRegularCloudImpl(mConetxt, mCleanCloudGlue);
        mResudialLocalRuleImpl = new KResidualLocalRuleImpl();
        dirMgmt = JunkCloudByDirMgmt.newInstance();
        pkgMgmt = JunkCloudByPkgMgmt.newInstance();
    }


    /**
     * 查询任务的属性;
     * */
    private static class DirQueryCallbackData {
        public DirQueryCallback mCallback;
        public int mDirScanType;
        public int mCurrentQueryId;
        public int mQueryIdForUser;
        public boolean mRealComplete;
        public TreeMap<String, DirQueryData> mLangMissmatchResult;
        public boolean mPureAsync;
        public boolean mForceNetQuery;
    }
    private class KResidualCloudDirQueryLogic extends KCleanCloudQueryLogic<DirQueryData, DirQueryCallbackData> {
        public KResidualCloudDirQueryLogic(Context context) {
            //super(context);
            super(context, mNeedNetQuery);
        }

        @Override
        protected boolean localQuery(final int queryId, Collection<DirQueryData> datas, DirQueryCallbackData callback) {
            return ResidualCloudQueryImpl.this.localDirQuery(datas, callback);
        }

        @Override
        protected boolean netQuery(final int queryId, Collection<DirQueryData> datas, DirQueryCallbackData callback) {
            return ResidualCloudQueryImpl.this.netDirQuery(datas, callback);
        }

        @Override
        protected boolean isNeedNetQuery(DirQueryData data, DirQueryCallbackData callback) {
            return ResidualCloudQueryImpl.this.isNeedNetDirQuery(data, callback);
        }

        @Override
        protected void onGetQueryResult(
                final Collection<DirQueryData> datas,
                final DirQueryCallbackData callback,
                final boolean queryComplete,
                final int queryId,
                final int dataTotalCount,
                final int dataCurrentCount) {
            ResidualCloudQueryImpl.this.onGetDirQueryResult(
                    datas,
                    callback,
                    queryComplete,
                    queryId,
                    dataTotalCount,
                    dataCurrentCount);
        }

        @Override
        protected boolean checkStop(DirQueryCallbackData callback) {
            return callback.mCallback.checkStop();
        }
    }

    private class KResidualCloudPkgQueryLogic extends KCleanCloudQueryLogic<PkgQueryData, PkgQueryCallback> {
        public KResidualCloudPkgQueryLogic(Context context) {
            //super(context);
            super(context, mNeedNetQuery);
        }

        @Override
        protected boolean localQuery(final int queryId, Collection<PkgQueryData> datas, PkgQueryCallback callback) {
            return ResidualCloudQueryImpl.this.localPkgQuery(datas, callback);
        }

        @Override
        protected boolean netQuery(final int queryId, Collection<PkgQueryData> datas, PkgQueryCallback callback) {
            long nStartTime = System.currentTimeMillis();
            boolean bRet = ResidualCloudQueryImpl.this.netPkgQuery(datas, callback);
            return bRet;
        }

        @Override
        protected boolean isNeedNetQuery(PkgQueryData data, PkgQueryCallback callback) {
            return ResidualCloudQueryImpl.this.isNeedNetPkgQuery(data, callback);
        }

        @Override
        protected void onGetQueryResult(
                final Collection<PkgQueryData> datas,
                final PkgQueryCallback callback,
                final boolean queryComplete,
                final int queryId,
                final int dataTotalCount,
                final int dataCurrentCount) {
            ResidualCloudQueryImpl.this.onGetPkgQueryResult(
                    datas,
                    callback,
                    queryComplete,
                    queryId,
                    dataTotalCount,
                    dataCurrentCount);
        }

        @Override
        protected boolean checkStop(PkgQueryCallback callback) {
            return ResidualCloudQueryImpl.this.pkgQueryCheckStop(callback);
        }
    }

    private class PackageDirFilter implements KResidualCloudQueryHelper.IPkgDirFilter{
        private volatile Collection<String> mAllPkgs = null;
        private volatile HashSet<String> mPkgSet = null;
        private volatile HashSet<String> mPkgMd5Set = null;
        private volatile HashSet<Long> mPkgMd5High64Set = null;

        /**
         * 基于MD5高64位的检测接口;
         * @author 
         * @date 2014.11.06
         * */
        public boolean isApkInstalledCheckByMD5High64(Collection<Long> pkgMD5High64){
            HashSet<Long> pkgMd5Hihg64Set = null;
            synchronized(this) {
                pkgMd5Hihg64Set = mPkgMd5High64Set;
            }

            if (pkgMd5Hihg64Set == null || pkgMd5Hihg64Set.isEmpty()) {
                // 防止无权限读取App列表的时候，我们不要误报残留。
                return true;
            }

            boolean isAllEmpty = true;
            for (Long md5 : pkgMD5High64) {
                if (isAllEmpty)
                    isAllEmpty = false;

                if (pkgMd5Hihg64Set.contains(md5)) {
                    return true;
                }
            }
            //防止因数据异常发生误删
            return isAllEmpty ? true : false;
        }

        public boolean isApkInstalledCheckByMD5(Collection<String>  allPkgNameMD5) {
            HashSet<String> pkgMd5Set = null;
            synchronized(this) {
                pkgMd5Set = mPkgMd5Set;
            }

            if (pkgMd5Set == null || pkgMd5Set.isEmpty()) {
                // 防止无权限读取App列表的时候，我们不要误报残留。
                return true;
            }

            boolean isAllEmpty = true;
            for (String md5 : allPkgNameMD5) {
                if (TextUtils.isEmpty(md5))
                    continue;

                if (isAllEmpty)
                    isAllEmpty = false;

                if (pkgMd5Set.contains(md5)) {
                    return true;
                }
            }
            //防止因数据异常发生误删
            return isAllEmpty ? true : false;
        }

        public boolean isApkInstalledCheckByRegex(Collection<String> pkgRegexs) {
            Collection<String> pkgs = null;
            synchronized(this) {
                pkgs = mAllPkgs;
            }
            if (pkgs == null || pkgs.isEmpty()) {
                // 防止无权限读取App列表的时候，我们不要误报残留。
                return true;
            }
            boolean isAllEmpty = true;
            for (String regex : pkgRegexs) {
                if (TextUtils.isEmpty(regex))
                    continue;

                if (isAllEmpty)
                    isAllEmpty = false;

                if (isApkInstalledRegPattern(regex, pkgs)) {
                    return true;
                }
            }
            //防止因数据异常发生误删
            return isAllEmpty ? true : false;
        }

        private boolean isApkInstalledRegPattern(String regex, Collection<String> pkgs) {
            Pattern pkgNameRegPattern = null;
            try {
                pkgNameRegPattern = Pattern.compile(regex);
            }
            catch (Exception e) {
                pkgNameRegPattern = null;
            }
            if (null == pkgNameRegPattern) {
                //到这个逻辑就是正则表达式错误了,所以这整个特征要废弃,所以就返回真,认为包安装了,就跳过这个特征的检出
                return true;
            }

            Matcher matcher = null;
            for (String pkg : pkgs) {
                matcher = pkgNameRegPattern.matcher(pkg);
                if (null != matcher && matcher.matches()) {
                    return true;
                }
            }
            return false;
        }

        public void init(PackageChecker packageChecker) {
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
                    pkgSet = new HashSet<>();
                    pkgMd5Set = new HashSet<>();
                    pkgMd5High64Set = new HashSet<>();
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


    @Override
    public boolean initialize() {
        synchronized (this) {
            if (!mIsInited) {
                mResidualCloudDirQueryLogic.initialize(mQueryExecutor);
                mResidualCloudPkgQueryLogic.initialize(mQueryExecutor);

//                mEmergencyFalseDirSignFilter = KEmergencyFalseSignManager.getInstance().createEmergencyFalseSignFilterAndNotifyDownLoad(
//                        IKEmergencyFalseSignFilter.FilterType.RESIDUAL_DIR);

                if (mRegularQueryImpl != null) {
                    mRegularQueryImpl.initialize();
                }

                if(mResudialLocalRuleImpl != null){
                    mResudialLocalRuleImpl.initialize(mConetxt);
                }

                mIsInited = true;
            }
        }
        return true;
    }

    @Override
    public void unInitialize() {
        synchronized (this) {
            if (!mIsInited)
                return;

            //退出前能上报的就上报
            doReportWork();

            mPackageChecker = null;
            //mPackageFilter.unInit();
            mIsInited = false;
            mQueryExecutor.quit();
            mResidualCloudDirQueryLogic.unInitialize();
            mResidualLocalQuery.unInitDb();
            clearInnerStatistics();

            mCleanCloudPathConverter.cleanPathEnumCache();//省内存
            mPackageFilter.unInit();//省内存

//            KSimpleGlobalTask.getInstance().removeCallbacks(mIdleReportTask);
//            KSimpleGlobalTask.getInstance().removeCallbacks(mFreeDbAndThreadTask);
//            KTestSignReportMgr.getInstance().notifyPutResidualTestSignInfoComplete();

            if(mRegularQueryImpl != null){
                mRegularQueryImpl.unInitialize();
            }
        }
    }

    @Override
    public boolean setLanguage(String language) {
        if (TextUtils.isEmpty(language))
            return false;

        mLanguage = language;
        mResidualLocalQuery.setLanguage(language);
        dirMgmt.setLanguage(language);
        pkgMgmt.setLanguage(language);
        if(mRegularQueryImpl != null){
            mRegularQueryImpl.setLanguage(language);
        }
        return true;
    }

    @Override
    public String getLanguage() {
        return mResidualLocalQuery.getLanguage();
    }

    @Override
    public boolean setSdCardRootPath(String path) {
        if(mRegularQueryImpl != null){
            mRegularQueryImpl.setSdCardRootPath(path);
        }
        if(mResudialLocalRuleImpl != null){
            mResudialLocalRuleImpl.setSdCardRootPath(path);
        }
        return mCleanCloudPathConverter.setSdCardRootPath(path);
    }

    @Override
    public String getSdCardRootPath() {
        return mCleanCloudPathConverter.getSdCardRootPath();
    }

    @Override
    public boolean setPackageChecker(PackageChecker packageChecker) {
        if (null == packageChecker) {
            return false;
        }

        mPackageChecker = packageChecker;

        if(mRegularQueryImpl != null){
            mRegularQueryImpl.setPackageChecker(packageChecker);
        }

        if(mResudialLocalRuleImpl != null){
            mResudialLocalRuleImpl.setPackageChecker(packageChecker);
        }

        return true;
    }

    @Override
    public void cleanPathEnumCache() {
        mCleanCloudPathConverter.cleanPathEnumCache();
        if(mRegularQueryImpl != null){
            mRegularQueryImpl.cleanPathEnumCache();
        }
    }

    /**
     * 获取文件名检测对象
     * @return  文件名检测对象
     */
    @Override
    public FileChecker getFileChecker()
    {
        return new FileChecker() {
            @Override
            public boolean removable(String fileName, FileCheckerData data)
            {
                // 大部分特征是没有后缀过滤规则的，提早结束
                if (null == data || ((null == data.globalSuffixCatIds || 0 == data.globalSuffixCatIds.length) &&
                        (null == data.whiteSuffixFilter || data.whiteSuffixFilter.isEmpty())) || TextUtils.isEmpty(fileName)) {
                    return true;
                }

                // 没有后缀名的文件，不判定
                int lastDot = fileName.lastIndexOf('.');
                if (lastDot <= 0 || lastDot >= fileName.length() - 1) {
                    return true;
                }

                // 后缀在黑名单中的文件可删除
                String ext = fileName.substring(lastDot + 1);
                if (null != data.blackSuffixFilter && !data.blackSuffixFilter.isEmpty()
                        && data.blackSuffixFilter.contains(ext)) {
                    return true;
                }

                // 后缀在白名单的文件不可删除
                if (null != data.whiteSuffixFilter && !data.whiteSuffixFilter.isEmpty()
                        && data.whiteSuffixFilter.contains(ext)) {
                    return false;
                }

                // 后缀在指定的全局配置中的文件不可删除
                Map<Integer, Set<String>> globalSuffixConfig = mResidualLocalQuery.getGlobalSuffixConfig();
                if (null != globalSuffixConfig && globalSuffixConfig.size() > 0 &&
                        null != data.globalSuffixCatIds && data.globalSuffixCatIds.length > 0) {
                    for (int catId : data.globalSuffixCatIds) {
                        Set<String> catSuffix = globalSuffixConfig.get(catId);
                        if (null != catSuffix && catSuffix.contains(ext)) {
                            return false;
                        }
                    }
                }

                return true;
            }
        };
    }

    /**
     * 设置目录网络查询的最长持续时间控制器,网络查询超出时间限制后将不再进行网络查询
     * @param timeCalculator 控制器接口
     */
    @Override
    public void setDirNetQueryTimeController(MultiTaskTimeCalculator timeCalculator) {
        mResidualCloudDirQueryLogic.setNetQueryTimeController(timeCalculator);
    }

    @Override
    public void discardAllQuery() {
        //Log.e(TAG, "discardAllQuery");
        mQueryExecutor.discardAllQuery();
        if (mIsMaintainTaskStarted) {
           // mFreeDbAndThreadTask.scheduleTask();
        }
        if (mIsReportTaskStarted) {
           // mIdleReportTask.scheduleTask();
        }
        if(mRegularQueryImpl != null){
            mRegularQueryImpl.discardAllQuery();
        }
    }

    @Override
    public int waitForComplete(long timeout, boolean discardQueryIfTimeout, CleanCloudDef.ScanTaskCtrl ctrl) {
        int result = mQueryExecutor.waitForComplete(timeout, discardQueryIfTimeout, ctrl);
        if(mRegularQueryImpl != null){
            mRegularQueryImpl.waitForComplete(timeout, discardQueryIfTimeout, ctrl);
        }
        return result;
    }

    public boolean setOthers(String uuid, int appVersion) {
        return false;
    }
	
/*	public void setHignFrequentDbPath(String path) {
		//mLocalQuery.setHignFrequentDbPath(path);
	}*/

    @Override
    public boolean queryByDirName(
            int scanType,
            Collection<String> dirnames,
            DirQueryCallback callback,
            boolean pureAsync,
            boolean forceNetQuery) {
        if (!mIsInited)
            return false;

        if (null == dirnames || null == callback || dirnames.isEmpty())
            return false;

        int queryId = getQueryId();
        DirQueryCallbackData callbackData = new DirQueryCallbackData();
        callbackData.mCallback = callback;
        callbackData.mDirScanType = scanType;
        callbackData.mQueryIdForUser = queryId;
        callbackData.mPureAsync = pureAsync;
        callbackData.mForceNetQuery = forceNetQuery;
        callback.onGetQueryId(queryId);
        return _queryByDirName(queryId, dirnames, callbackData, pureAsync, forceNetQuery);
    }

    private boolean _queryByDirName(
            int queryId,
            Collection<String> dirnames,
            DirQueryCallbackData callback,
            boolean pureAsync,
            boolean forceNetQuery) {
        markWorking();
        /**
         * 检查重复查询dir问题
         * @author
         * */
//        ArrayList<String> dirMd5Arr = new ArrayList<String>();
//        for (String dir : dirnames){
//            dirMd5Arr.add(dir);
//        }
//        KCleanCloudManager.checkRepeatKey(dirMd5Arr, 1);

        callback.mCurrentQueryId = queryId;
        Collection<DirQueryData> querydatas = getDirQueryDatas(dirnames);
        NLog.d(TAG, "ResidualCloudQueryImpl _queryByDirName query ");
        return mResidualCloudDirQueryLogic.query(querydatas, callback, pureAsync, forceNetQuery, queryId);
    }

    @Override
    public boolean queryByPkgName(
            Collection<String> pkgnames,
            PkgQueryCallback callback,
            boolean pureAsync,
            boolean forceNetQuery) {
        if (!mIsInited)
            return false;

        if (null == pkgnames || null == callback || pkgnames.isEmpty())
            return false;

        markWorking();
        int queryId = getQueryId();
        callback.onGetQueryId(queryId);
        Collection<PkgQueryData> querydatas = getPkgQueryDatas(pkgnames);
        NLog.d(ResidualLocalQuery.TAG, "ResidualCloudQueryImpl queryByPkgName query ");
        return mResidualCloudPkgQueryLogic.query(querydatas, callback, pureAsync, forceNetQuery, queryId);
    }

    private static class PkgSyncQueryCallback implements PkgQueryCallback {
        private volatile boolean mIsNotify 	= false;
        private volatile boolean mCheckStopValue = false;
        Collection<PkgQueryData> mDatas = null;

        PkgSyncQueryCallback(Collection<PkgQueryData> datas) {
            mDatas = datas;
        }

        @Override
        public void onGetQueryId(int queryId) {
        }

        @Override
        public void onGetQueryResult(int queryId, Collection<PkgQueryData> results, boolean queryComplete) {

            if (queryComplete) {
                myNotify();
            }
        }

        @Override
        public boolean checkStop() {
            return mCheckStopValue;
        }

//		public void setCheckStop(boolean value) {
//			mCheckStopValue = value;
//		}

        void myNotify() {
            synchronized (this) {
                notifyAll();
                mIsNotify = true;
            }
        }

        //		void reset() {
//			mIsNotify = false;
//		}
//		
//		boolean isNotify() {
//			return mIsNotify;
//		}
//		
        boolean waitForNotify(long timeout) {
            boolean result = true;
            synchronized (this) {
                if (mIsNotify)
                    return true;

                try {
                    wait(timeout);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    result = false;
                }
            }
            return result;
        }

        void fixResults() {
            ////for (PkgSeQueryData data : mDatas) {			
            //}
        }
    }

    @Override
    public Collection<PkgQueryData> syncQueryByPkgName(Collection<String> pkgnames, boolean forceNetQuery, long timeout) {
        if (!mIsInited)
            return null;

        if (null == pkgnames || pkgnames.isEmpty())
            return null;

        Collection<PkgQueryData> querydatas = getPkgQueryDatas(pkgnames);
        if (null == querydatas)
            return null;

        markWorking();
        PkgSyncQueryCallback callback = new PkgSyncQueryCallback(querydatas);
        int queryId = getQueryId();
        callback.onGetQueryId(queryId);
        NLog.d(ResidualLocalQuery.TAG, "ResidualCloudQueryImpl syncQueryByPkgName query ");
        boolean ret = mResidualCloudPkgQueryLogic.query(querydatas, callback, false, forceNetQuery, queryId);
        boolean bTimeout = callback.waitForNotify(timeout);
        //callback.setCheckStop(true);
        callback.fixResults();
        return querydatas;
    }

    @Override
    public PkgQueryData syncQueryByPkgName(String pkgname, boolean forceNetQuery, long timeout) {
        PkgQueryData result = null;
        ArrayList<String> pkgnames = new ArrayList<String>(1);
        pkgnames.add(pkgname);
        Collection<PkgQueryData> results = syncQueryByPkgName(pkgnames, forceNetQuery, timeout);
        if (results != null && !results.isEmpty()) {
            result = results.iterator().next();
        }
        return result;
    }
	/*
	@Override
	public QueryInnerStatistics getInnerStatistics() {
		QueryInnerStatistics result = new QueryInnerStatistics();
		
		KNetQueryStatistics netQueryStatistics = mResidualNetWorkQuery.getQueryStatistics();
		result.mNetQueryUseTime     = netQueryStatistics.mQueryUseTime;
		result.mNetQueryCount       = netQueryStatistics.mQueryCount;
		result.mNetQueryFailedCount = netQueryStatistics.mQueryFailedCount;
		result.mTotalPostSize		= netQueryStatistics.mTotalPostSize;
		result.mTotalResponseSize	= netQueryStatistics.mTotalResponseSize;
		
		KQueryLogicStatistics statistics = mResidualCloudDirQueryLogic.getQueryStatistics();
		result.mLastQueryCompleteTime = statistics.mLastQueryCompleteTime;
		result.mLocalQueryUseTime   = statistics.mLocalQueryUseTime;
		result.mTotalDirQueryCount  = statistics.mTotalQueryCount;	///< 目录查询总数
		result.mDirNetQueryCount    = statistics.mNetQueryCount;	///< 需要联网查询的目录总数	
		result.mIsUserBreakQuery    = statistics.mUserBreakQuery;
		
		return result;
	}
	*/

    @Override
    public String getDefaultLanguage() {
        return mResidualLocalQuery.getDefaultLanguage();
    }

    public void clearInnerStatistics() {
        //mResidualNetWorkQuery.clearQueryStatistics();
        //mResidualCloudDirQueryLogic.clearQueryStatistics();
    }

    @Override
    public DirQueryData[] localQueryDirInfo(String dirname, boolean queryParentDir, boolean isGetShowInfo, String language) {
        if (dirname == null || dirname.length() == 0) {
            return null;
        }
        while (dirname.startsWith(File.separator)) {
            dirname = dirname.substring(1);
        }
        while (dirname.endsWith(File.separator)) {
            dirname = dirname.substring(0, dirname.length() - 1);
        }
        if (dirname.length() == 0) {
            return null;
        }
        if (TextUtils.isEmpty(language)) {
            language = mLanguage;
        }
        markWorking();
        ArrayList<String> dirnames = null;
        StringBuilder sb = new StringBuilder(dirname.length());
        if (queryParentDir) {
            String[] segs = dirname.split("/");
            dirnames = new ArrayList<String>(segs.length);
            for ( String seg : segs ) {
                sb.append(seg);
                dirnames.add(sb.toString());
                sb.append(File.separator);
            }
        } else {
            dirnames = new ArrayList<String>(1);
            dirnames.add(dirname);
        }
        DirQueryData[] result = null;
        int resultCount = 0;
        Collection<DirQueryData> querydatas = getDirQueryDatas(dirnames);
        mResidualLocalQuery.queryByDir(querydatas, isGetShowInfo, language);
        for (DirQueryData data : querydatas) {
            if (data.mErrorCode == 0 && data.mResult.mQueryResult == DirResultType.PKG_LIST) {
                ++resultCount;
            }
        }
        if (resultCount > 0) {
            int i = 0;
            result = new DirQueryData[resultCount];
            for (DirQueryData data : querydatas) {
                if (data.mErrorCode == 0 && data.mResult.mQueryResult == DirResultType.PKG_LIST) {
                    if (i >= resultCount)
                        break;

                    result[i] = data;
                    ++i;
                }
            }
        }
        return result;
    }

    @Override
    public DirQueryData[] localQueryDirAndSubDirInfo(String dirname, boolean isGetShowInfo, String language) {
        NLog.d(TAG, "localQueryDirAndSubDirInfo dirname = %s", dirname);
        if (dirname == null || dirname.length() == 0) {
            return null;
        }
        while (dirname.startsWith(File.separator)) {
            dirname = dirname.substring(1);
        }
        while (dirname.endsWith(File.separator)) {
            dirname = dirname.substring(0, dirname.length() - 1);
        }
        if (dirname.length() == 0) {
            return null;
        }

        markWorking();

        MessageDigest md = KQueryMd5Util.getMd5Digest();
        if (null == md)
            return null;

        KResidualCommonData.DirQueryInnerData innerData = KResidualCloudQueryHelper.getDirQueryInnerData(md, dirname);

        return mResidualLocalQuery.localQueryDirAndSubDirInfo(innerData.mLocalQueryKey, isGetShowInfo, language);

    }

    public ShowInfo localQueryDirShowInfo(int signId, String language) {
        markWorking();
        return mResidualLocalQuery.getShowInfoByDirId(signId, language);
    }

    private int getQueryId() {
        return mQueryIdSeed.incrementAndGet();
    }

    private Collection<DirQueryData> getDirQueryDatas(Collection<String> dirnames) {
        MessageDigest md = KQueryMd5Util.getMd5Digest();
        if (null == md)
            return null;

        ArrayList<DirQueryData> result = new ArrayList<>(dirnames.size());
        for (String dirname : dirnames) {
            DirQueryData data = KResidualCloudQueryHelper.getDirQueryDatas(md, dirname, mLanguage);
            result.add(data);
        }
        return result;
    }

    private Collection<DirQueryData> getDirQueryDatasForPkgQuery(Collection<PkgQueryDirItem> pkgQueryDirItems) {
        MessageDigest md = KQueryMd5Util.getMd5Digest();
        if (null == md)
            return null;

        ArrayList<DirQueryData> result = new ArrayList<>(pkgQueryDirItems.size());
        for (PkgQueryDirItem diritem : pkgQueryDirItems) {
            DirQueryData data = KResidualCloudQueryHelper.getDirQueryDatas(md, diritem.mDir, mLanguage);
            diritem.mDirQueryData = data;
            result.add(data);
        }
        return result;
    }

    private Collection<PkgQueryData> getPkgQueryDatas(Collection<String> pkgnames) {
        MessageDigest md = KQueryMd5Util.getMd5Digest();
        if (null == md)
            return null;

        ArrayList<PkgQueryData> result = new ArrayList<>(pkgnames.size());
        for (String pkgname : pkgnames) {
            KResidualCommonData.PkgQueryInnerData innerData = getPkgQueryInnerData(md, pkgname);
            PkgQueryData data = new PkgQueryData();
            data.mResult 	= new PkgQueryResult();
            data.mLanguage 	= mLanguage;
            data.mInnerData = innerData;
            data.mPkgName   = pkgname;
            result.add(data);
        }
        return result;
    }

    private PkgQueryInnerData getPkgQueryInnerData(MessageDigest md, String pkgname) {
        PkgQueryInnerData result = new PkgQueryInnerData();
        byte[] md5Bytes = KQueryMd5Util.getPkgQueryMd5Bytes(md, pkgname);
        result.mPkgNameMd5 = EnDeCodeUtils.byteToHexString(md5Bytes);
        result.mPkgNameMd5High64Bit = KQueryMd5Util.getMD5High64BitFromMD5(md5Bytes);
        return result;
    }
    /////////////////////////////////////////////////
    //private boolean localDirQuery(DirQueryData data, DirQueryCallback callback) {
    //	return mResidualLocalQuery.queryByDir(data);	
    //}

    private boolean localDirQuery(Collection<DirQueryData> datas, DirQueryCallbackData callback) {
        NLog.d(ResidualLocalQuery.TAG, "ResidualCloudQueryImpl localDirQuery");
        boolean query = mResidualLocalQuery.queryByDir(datas);

        /*************************************************************/
        for (DirQueryData dqData : datas) {
            if ("mytest".equals(dqData.mDirName) && null != dqData.mResult.mFileCheckerData) {
                FileChecker fc = this.getFileChecker();
                File file = new File("/sdcard/mytest");
                File[] subFiles = file.listFiles();
                for (File f : subFiles) {
                    String fName = f.getName();
                    boolean r = fc.removable(fName, dqData.mResult.mFileCheckerData);
                }
            }
        }
        /*************************************************************/

        /**
         * regular dir query could call at here, also could call at scan engine.
         * @author 
         * @date 2014.12.08
         * */
        if(callback != null){
            if(mRegularQueryImpl != null){
                ArrayList<String> regdirs = getNeedRegularDirQueryKey(datas);
                if(regdirs != null && !regdirs.isEmpty()){
                    //KCleanCloudManager.reportCacheScan(51, (byte)1, (byte)1);
                    mRegularQueryImpl.queryByDirName(callback.mDirScanType, regdirs, callback.mCallback, true, true);
                }else{
                    //KCleanCloudManager.reportCacheScan(52, (byte)1, (byte)1);
                }
            }else{
                //KCleanCloudManager.reportCacheScan(53, (byte)1, (byte)1);
            }
        }

        /**
         * 检查重复查询dir问题
         * PS : just for 5.9.2 beta testing
         * @author 
         * @date 2014.12.25
         * */

//        ArrayList<String> dirMd5Arr = new ArrayList<String>();
//        for (DirQueryData data : datas){
//            dirMd5Arr.add(((DirQueryInnerData)(data.mInnerData)).mLocalQueryKey);
//        }
//        KCleanCloudManager.checkRepeatKey(dirMd5Arr, 2);
        return query;
    }

    private boolean netDirQuery(final Collection<DirQueryData> datas, final DirQueryCallbackData callback) {
        //boolean query = mResidualNetWorkQuery.query(datas, callback.mCallback);

        boolean query =false;
        //改用spacelib里面的代码
        try {
            NLog.d(TAG, "netQuery----->");
            query = dirMgmt.getResidualByDirName(datas,callback.mCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 检查重复查询dir问题
         * PS : just for 5.9.2 beta testing
         * @author 
         * @date 2014.12.25
         * */
//         ArrayList<String> dirMd5Arr = new ArrayList<String>();
//         for (DirQueryData data : datas){
//             dirMd5Arr.add(((DirQueryInnerData)(data.mInnerData)).mDirNameMd5);
//         }
//        KCleanCloudManager.checkRepeatKey(dirMd5Arr, 3);
        return query;
    }

    private boolean isNeedNetDirQuery(DirQueryData data, DirQueryCallbackData callback) {
        boolean result = false;
        //去除语言的判断条件,如果语言不匹配会在最后检出时进行再次云端查询
        result = (data.mResult.mQueryResult == DirResultType.UNKNOWN || data.mResultExpired);
        return result;
    }

    private boolean isNeedScan(DirQueryResult dqResult, int scanType) {
        boolean result = false;
        switch (scanType) {
            case DirScanType.DIR_STANDARD_SCAN :
                if (DirCleanType.SUGGESTED == dqResult.mCleanType || DirCleanType.SUGGESTED_WITH_FILTER == dqResult.mCleanType) {
                    result = true;
                }
                // 建议扫描过程的深度特征如果带有时间线或者具有后缀名过滤信息，则继续
                int cleanTime = dqResult.mCleanTime & 0xffff;
                if ((DirCleanType.CAREFUL == dqResult.mCleanType || DirCleanType.CAREFUL_WITH_FILTER == dqResult.mCleanType) &&
                        (cleanTime > 0 && cleanTime < 65535 || null != dqResult.mFileCheckerData &&
                                (null != dqResult.mFileCheckerData.globalSuffixCatIds && dqResult.mFileCheckerData.globalSuffixCatIds.length > 0 ||
                                        null != dqResult.mFileCheckerData.blackSuffixFilter && dqResult.mFileCheckerData.blackSuffixFilter.size() > 0 ||
                                        null != dqResult.mFileCheckerData.whiteSuffixFilter && dqResult.mFileCheckerData.whiteSuffixFilter.size() > 0))) {
                    result = true;
                }
                break;
            case DirScanType.DIR_ADVANVCED_SCAN :
                if (DirCleanType.CAREFUL == dqResult.mCleanType || DirCleanType.CAREFUL_WITH_FILTER == dqResult.mCleanType) {
                    result = true;
                }
                break;
            case DirScanType.DIR_ALL_SCAN :
                if (DirCleanType.SUGGESTED == dqResult.mCleanType
                        || DirCleanType.SUGGESTED_WITH_FILTER == dqResult.mCleanType
                        || DirCleanType.CAREFUL == dqResult.mCleanType
                        || DirCleanType.CAREFUL_WITH_FILTER == dqResult.mCleanType) {
                    result = true;
                }
                break;
            default:
        }
        return result;
    }

    /**
     * 判断碰撞Dir特征是否是应该是检出垃圾;
     * @return boolean: true-检出; false-不检出;
     * @date 2014.01.13
     * */
    private boolean fillDetectResult(DirQueryData data, DirQueryCallbackData callbackData) {
        boolean isTestSign = TestFlagUtil.isTestSign(data.mResult.mTestFlag);

        if (data.mErrorCode != 0)
            return false;

        if (!DirQueryResultUtil.isHavePackageList(data.mResult))
            return false;

        if (!isNeedScan(data.mResult, callbackData.mDirScanType)) {
            return false;
        }

       /* if (mEmergencyFalseDirSignSwitch.isEnable()
                && mEmergencyFalseDirSignFilter != null
                && mEmergencyFalseDirSignFilter.filter(data.mResult.mSignId)) {
            return false;
        }*/

        if(data.mResult.mPkgsMD5High64 != null
                && !data.mResult.mPkgsMD5High64.isEmpty()){
            //高频库检出的时候 data.mResult.mPkgsMD5High64的值为pkgs的值 根据包名判断如果该录该应用的包名还存在 说明该app还未卸载 则不检出
            if(mPackageFilter.isApkInstalledCheckByMD5High64(data.mResult.mPkgsMD5High64)){
                return false;
            }
        }else if(data.mResult.mPkgsMD5HexString != null
                && !data.mResult.mPkgsMD5HexString.isEmpty()){
            //缓存库检出的时候 data.mResult.mPkgsMD5HexString的值为pkgs的值
            if (mPackageFilter.isApkInstalledCheckByMD5(data.mResult.mPkgsMD5HexString)) {
                return false;
            }
        }

        if (data.mResult.mPackageRegexs != null
                && !data.mResult.mPackageRegexs.isEmpty()
                && mPackageFilter.isApkInstalledCheckByRegex(data.mResult.mPackageRegexs)) {
            return false;
        }

        ((DirQueryInnerData)data.mInnerData).misDetect = true;

        if (isTestSign) {
            //todo 测试特征
            data.mIsDetected = false;
        } else{
            data.mIsDetected = true;
        }

        return true;
    }

    private void onGetDirQueryResult(
            final Collection<DirQueryData> datas,
            final DirQueryCallbackData callback,
            final boolean queryComplete,
            final int queryId,
            final int dataTotalCount,
            final int dataCurreonGetDirQueryResultntCount) {

        int hitCountTotal = 0;
        int hitCountHF    = 0;
        int hitCountCache = 0;
        int hitCountCloud = 0;
        NLog.d(TAG, "ResidualCloudQueryImpl onGetDirQueryResult开始检测是否需要为残留垃圾");
        mPackageFilter.init(mPackageChecker);
        LinkedList<DirQueryData> updateCacheResults = null;
        ArrayList<String> langMissMatchDir = null;
        ArrayList<DirQueryData> newResultDatas = null;
        ArrayList<DirQueryData> androidDataLocalRuleResult = null;
        ArrayList<DirQueryData> androidObbLocalRuleResult = null;
        boolean isNeedNewResultDatas = false;
        int currentCount = 0;
        for (DirQueryData data : datas) {
            if (data.mErrorCode == 0
                    && data.mResultSource == ResultSourceType.CLOUD
                    && data.mResult != null
                    && data.mResult.mQueryResult != DirResultType.UNKNOWN) {
                if (null == updateCacheResults) {
                    updateCacheResults = new LinkedList<>();
                }
                updateCacheResults.add(data);
            }

            //网络查询可能失败,如果原来有本地过期结果,那么修正一下结果
            if (data.mErrorCode != 0
                    && data.mResultExpired
                    && data.mResult != null
                    && data.mResult.mQueryResult != DirResultType.UNKNOWN) {
                data.mErrorCode = 0;
                data.mResultSource = ResultSourceType.CACHE;
            }

            if (data.mErrorCode == 0 && data.mResult.mQueryResult != PkgResultType.UNKNOWN) {
                ++hitCountTotal;
                switch(data.mResultSource) {
                    case ResultSourceType.HFREQ :
                        ++hitCountHF;
                        break;
                    case ResultSourceType.CACHE :
                        ++hitCountCache;
                        break;
                    case ResultSourceType.CLOUD :
                        ++hitCountCloud;
                        break;
                    default:
                }
            }

            DirQueryInnerData innerData = (DirQueryInnerData)data.mInnerData;
            if (!TestFlagUtil.isTestSign(data.mResult.mTestFlag) && innerData.mFilterSubDirDatas != null && !innerData.mFilterSubDirDatas.isEmpty()) {
                data.mResult.mFilterSubDirs = new ArrayList<>(innerData.mFilterSubDirDatas.size());
                for (KResidualCloudQuery.FilterDirData filterData : innerData.mFilterSubDirDatas) {
                    if(filterData.mCleanType == DirCleanType.CAREFUL || filterData.mCleanType == DirCleanType.PURE_WHITE_FILTER){
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
            if (isDetected){
                NLog.e(TAG, "isDetected = " + isDetected+", data ="+data );
            }else {
                NLog.e(TAG, "isDetected = " + isDetected+", dirname ="+data.mDirName );
            }


            // local rule.
            if(mResudialLocalRuleImpl != null && data.mResult.mQueryResult == DirResultType.NOT_FOUND){
                if(!isDetected){
                    isDetected = mResudialLocalRuleImpl.processAndroidDataRule(data);
                    if(isDetected){
                        if(androidDataLocalRuleResult == null){
                            androidDataLocalRuleResult = new ArrayList<>();
                        }
                        androidDataLocalRuleResult.add(data);
                    }
                }

                if(!isDetected){
                    isDetected = mResudialLocalRuleImpl.processAndroidObbRule(data);
                    if(isDetected){
                        if(androidObbLocalRuleResult == null){
                            androidObbLocalRuleResult = new ArrayList<>();
                        }
                        androidObbLocalRuleResult.add(data);
                    }
                }
            }

            //////////////////////////////////////////////////////////
            //以下逻辑都是为了最大限度利用缓存,并正确处理当语言不匹配的情况
            boolean isDataCanCallback = true;
            DirQueryData oldResultData = null;

            if (isDetected) {
                // 如果检出了但是描诉的语言不对或者没有描述(异常情况)，那么强制网络查询一次以获取正确的描诉
                if (data.mResultSource == ResultSourceType.CACHE
                        && data.mResult.mShowInfo != null
                        && (data.mResult.mShowInfo.mResultLangMissmatch || TextUtils.isEmpty(data.mResult.mShowInfo.mName))) {

                    if (callback.mLangMissmatchResult == null) {
                        callback.mLangMissmatchResult = new TreeMap<String, DirQueryData>();
                    }
                    if (!callback.mLangMissmatchResult.containsKey(data.mDirName)) {
                        callback.mLangMissmatchResult.put(data.mDirName, data);

                        if (langMissMatchDir == null) {
                            langMissMatchDir = new ArrayList<String>();
                        }
                        langMissMatchDir.add(data.mDirName);
                        isDataCanCallback = false;
                        if (!isNeedNewResultDatas) {
                            isNeedNewResultDatas = true;
                        }
                    }
                }
            }else{
                if (callback.mLangMissmatchResult != null) {
                    //如果当前没有检出但是老的结果有检出
                    oldResultData = callback.mLangMissmatchResult.get(data.mDirName);
                    if (oldResultData != null) {
                        isDataCanCallback = false;
                        if (!isNeedNewResultDatas) {
                            isNeedNewResultDatas = true;
                        }
                    }
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
                if (oldResultData != null) {
                    newResultDatas.add(oldResultData);
                }
            }

            ++currentCount;
            ///////////////////////////////////////
        }

       // mResidualDirQueryStatistics.addHitCountData(hitCountHF, hitCountCache, hitCountCloud, hitCountTotal);
        mResidualLocalQuery.updateDirCache(updateCacheResults);
        NLog.d(TAG, "ResidualCloudQueryImpl onGetDirQueryResult  androidDataLocalRuleResult = "+androidDataLocalRuleResult);
        // make android-data rule query result.
        if(androidDataLocalRuleResult != null && mResudialLocalRuleImpl != null){
            for (DirQueryData localRuleData : androidDataLocalRuleResult){
                mResudialLocalRuleImpl.makeAndroidDataResult(localRuleData, LocalRuleType.ANDROID_DATA);
            }
        }
        NLog.d(TAG, "ResidualCloudQueryImpl onGetDirQueryResult  androidObbLocalRuleResult = "+androidObbLocalRuleResult);
        // make android-obb rule query result.
        if(androidObbLocalRuleResult != null && mResudialLocalRuleImpl != null){
            for (DirQueryData localRuleData : androidObbLocalRuleResult){
                mResudialLocalRuleImpl.makeAndroidDataResult(localRuleData, LocalRuleType.ANDROID_OBB);
            }
        }

        NLog.d(TAG, "ResidualCloudQueryImpl datas = "+datas);
        Collection<String> secondQueryDirs = KResidualCloudQueryHelper.getSecondaryQueryDirs(
                datas, mCleanCloudPathConverter, mPackageFilter);
        NLog.d(TAG, "ResidualCloudQueryImpl callback = "+callback +",secondQueryDirs = "+ secondQueryDirs);
        if (callback != null) {
            if (secondQueryDirs != null && !secondQueryDirs.isEmpty()) {
                int newQueryId = getQueryId();
                callback.mCallback.onGetQueryDirs(callback.mQueryIdForUser, secondQueryDirs);
                _queryByDirName(newQueryId, secondQueryDirs, callback, false, false);
            }

            // 如果检出了但是描诉的语言不对或者没有描述(异常情况)，那么强制网络查询一次以获取正确的描诉
            if (langMissMatchDir != null && !langMissMatchDir.isEmpty()) {
                int newQueryId = getQueryId();
                callback.mCallback.onGetQueryDirs(callback.mQueryIdForUser, langMissMatchDir);
                _queryByDirName(newQueryId, langMissMatchDir, callback, false, true);
            }

            if (queryId == callback.mCurrentQueryId && queryComplete) {
                callback.mRealComplete = true;
            }
            Collection<DirQueryData> resultDatas = (newResultDatas != null) ? newResultDatas : datas;
            postDirQueryResult(mQueryExecutor, callback, resultDatas);

            // report local rule result.
            if(androidDataLocalRuleResult != null){
                postLocalRuleReportData(androidDataLocalRuleResult, getSdCardRootPath());
            }
            if(androidObbLocalRuleResult != null){
                postLocalRuleReportData(androidObbLocalRuleResult, getSdCardRootPath());
            }
        }
    }

    /**
     * 返回经过本地查询之后，没有命中的key；
     * PS:只处理是一级路径的key；
     * @author 
     * @date 2014.12.08
     * */
    private ArrayList<String> getNeedRegularDirQueryKey(Collection<DirQueryData> datas){
        if(datas == null || datas.isEmpty()){
            return null;
        }

        ArrayList<String> keys = null;
        for(DirQueryData data : datas){
            if(data.mResult == null
                    || data.mResult.mQueryResult == DirResultType.UNKNOWN
                    || data.mResult.mQueryResult == DirResultType.NOT_FOUND
                    || data.mResult.mQueryResult == DirResultType.DIR_LIST){
                String dir = ((DirQueryInnerData)data.mInnerData).mOriginalKey;
                if(!TextUtils.isEmpty(dir) && dir.indexOf("/") < 0){
                    if(keys == null){
                        keys = new ArrayList<>();
                    }
                    keys.add(dir);
                }
            }
        }

        return keys;
    }

    ///////////////////////////////////////////////////////
    //private boolean localPkgQuery(PkgQueryData data, PkgQueryCallback callback) {
    //	return mResidualLocalQuery.queryByPkg(data);
    //}

    private boolean localPkgQuery(Collection<PkgQueryData> datas, PkgQueryCallback callback) {
        return mResidualLocalQuery.queryByPkg(datas);
    }

    private boolean netPkgQuery(final Collection<PkgQueryData> datas, final PkgQueryCallback callback) {
        boolean query =false;
        //改用spacelib里面的代码
        try {
            NLog.d(TAG, "netPkgQuery----->");
            query = pkgMgmt.getResidualByPkgName(datas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return query;
    }

    private boolean isNeedNetPkgQuery(PkgQueryData data, PkgQueryCallback callback) {
        boolean result = false;
        result = (data.mResult.mQueryResult == PkgResultType.UNKNOWN || data.mResultExpired);
        return result;
    }

    private static class DirQueryCallbackForPkgQuery implements DirQueryCallback {
        int mPkgQueryId;
        boolean mPkgFirstStepQueryComplete;
        PkgQueryCallback mPkgQueryCallback;
        LinkedList<PkgQueryData> mQueryData = null;
        private CleanCloudQueryExecutor mQueryExecutor;
        DirQueryCallbackForPkgQuery(
                int pkgQueryId,
                boolean pkgFirstStepQueryComplete,
                PkgQueryCallback pkgQueryCallback,
                LinkedList<PkgQueryData> queryData,
                CleanCloudQueryExecutor queryExecutor) {
            mPkgQueryId       = pkgQueryId;
            mPkgQueryCallback = pkgQueryCallback;
            mQueryData        = queryData;
            mPkgFirstStepQueryComplete = pkgFirstStepQueryComplete;
            mQueryExecutor = queryExecutor;
        }

        @Override
        public void onGetQueryDirs(int queryId, final Collection<String> dirs) {
        }

        @Override
        public void onGetQueryId(int queryId) {
        }

        @Override
        public void onGetQueryResult(int queryId, Collection<DirQueryData> results, boolean queryComplete) {
            if (queryComplete) {
                if (mPkgQueryCallback != null) {
                    postPkgQueryResult(mQueryExecutor, mPkgQueryCallback, mQueryData, mPkgFirstStepQueryComplete, mPkgQueryId);
                }
            }
        }

        @Override
        public boolean checkStop() {
            return (mPkgQueryCallback != null) ? mPkgQueryCallback.checkStop() : false;
        }
    }

    private void onGetPkgQueryResult(
            final Collection<PkgQueryData> datas,
            final PkgQueryCallback callback,
            final boolean queryComplete,
            final int queryId,
            final int dataTotalCount,
            final int dataCurrentCount) {
        NLog.d(TAG, "onGetPkgQueryResult----->");
        LinkedList<PkgQueryData> updateCacheResults = null;
        LinkedList<PkgQueryData> directCallbackData  = null;
        LinkedList<PkgQueryData> needDirQueryData    = null;
        LinkedList<PkgQueryDirItem> pkgQueryDirItems = null;

        int hitCountTotal = 0;
        int hitCountHF    = 0;
        int hitCountCache = 0;
        int hitCountCloud = 0;

        for (PkgQueryData data : datas) {

            if (data.mErrorCode == 0
                    && data.mResultSource == ResultSourceType.CLOUD
                    && data.mResult != null
                    && data.mResult.mQueryResult != PkgResultType.UNKNOWN) {
                if (null == updateCacheResults) {
                    updateCacheResults = new LinkedList<>();
                }
                updateCacheResults.add(data);
            }

            //网络查询可能失败,如果原来有本地过期结果,那么修正一下结果
            if (data.mErrorCode != 0
                    && data.mResultExpired
                    && data.mResult != null
                    && data.mResult.mQueryResult != PkgResultType.UNKNOWN) {
                data.mErrorCode = 0;
                data.mResultSource = ResultSourceType.CACHE;
            }
            if (data.mResult != null
                    && data.mResult.mPkgQueryDirItems != null
                    && !data.mResult.mPkgQueryDirItems.isEmpty()
                    && data.mResult.mQueryResult != PkgResultType.DIR_LIST) {
                //如果只有正则匹配成功,data.mResult.mQueryResult是没有设置的,现在补设置一下
                data.mResult.mQueryResult = PkgResultType.DIR_LIST;
                if (data.mErrorCode != 0) {
                    data.mErrorCode = 0;
                }
                if (data.mResultMatchRegex && data.mResultSource != ResultSourceType.HFREQ) {
                    data.mResultSource = ResultSourceType.HFREQ;
                }
            }

            if (data.mErrorCode != 0 || data.mResult == null || data.mResult.mPkgQueryDirItems == null
                    || data.mResult.mPkgQueryDirItems.isEmpty()) {
                if (null == directCallbackData) {
                    directCallbackData = new LinkedList<>();
                }
                directCallbackData.add(data);
            } else {
                boolean needDirQuery = false;
                for (PkgQueryDirItem item : data.mResult.mPkgQueryDirItems) {
                    if (TextUtils.isEmpty(item.mDirString))
                        continue;

                    String strDir = mCleanCloudPathConverter.getDirPath(item.mDirString);
                    if (TextUtils.isEmpty(strDir))
                        continue;

                    item.mDir = strDir;
                    String strFullPath = mCleanCloudPathConverter.getFullPathFromRelativePath(strDir);
                    File testFile = new File(strFullPath);
                    item.mIsDirStringExist = testFile.exists();
                    if (!item.mIsDirStringExist)
                        continue;

                    if (null == pkgQueryDirItems) {
                        pkgQueryDirItems = new LinkedList<>();
                    }

                    needDirQuery = true;
                    pkgQueryDirItems.add(item);
                }

                if (needDirQuery) {
                    if (needDirQueryData == null) {
                        needDirQueryData = new LinkedList<>();
                    }
                    needDirQueryData.add(data);
                } else {
                    if (null == directCallbackData) {
                        directCallbackData = new LinkedList<>();
                    }
                    directCallbackData.add(data);
                }
            }

            if (data.mErrorCode == 0 && data.mResult.mQueryResult != PkgResultType.UNKNOWN) {
                ++hitCountTotal;
                switch(data.mResultSource) {
                    case ResultSourceType.HFREQ :
                        ++hitCountHF;
                        break;
                    case ResultSourceType.CACHE :
                        ++hitCountCache;
                        break;
                    case ResultSourceType.CLOUD :
                        ++hitCountCloud;
                        break;
                    default:
                }
            }
        }

        //mResidualPkgQueryStatistics.addHitCountData(hitCountHF, hitCountCache, hitCountCloud, hitCountTotal);
        mResidualLocalQuery.updatePkgCache(updateCacheResults);

        if (needDirQueryData != null && pkgQueryDirItems != null) {
            int dirQueryId = getQueryId();
            Collection<DirQueryData> dirQuerydatas = getDirQueryDatasForPkgQuery(pkgQueryDirItems);

            DirQueryCallbackForPkgQuery dirQueryCallback = new DirQueryCallbackForPkgQuery(
                    queryId, queryComplete, callback, needDirQueryData, mQueryExecutor);

            DirQueryCallbackData callbackData = new DirQueryCallbackData();
            callbackData.mCallback = dirQueryCallback;
            callbackData.mDirScanType = DirScanType.DIR_ALL_SCAN;
            callbackData.mCurrentQueryId = dirQueryId;
            NLog.d(TAG, "ResidualCloudQueryImpl _queryByDirName ");
            mResidualCloudDirQueryLogic.query(dirQuerydatas, callbackData, false, false, true, dirQueryId);
        }

        if (directCallbackData != null && callback != null) {
            boolean finalQueryComplete = (queryComplete && null == needDirQueryData);
            postPkgQueryResult(mQueryExecutor, callback, directCallbackData, finalQueryComplete, queryId);
        }
    }

    private boolean pkgQueryCheckStop(PkgQueryCallback callback) {
        return callback.checkStop();
    }

    private boolean doFreeDbAndThreadWork() {
        boolean freeSuccess = mResidualLocalQuery.tryUnInitDb();
        if (freeSuccess) {
            //doReportBlackResultWork();//退出前先把上报给报了
            //doReportStatisticsWork();
            mCleanCloudPathConverter.cleanPathEnumCache();//省内存
            mPackageFilter.unInit();//省内存
            mQueryExecutor.safeQuit();//线程也退出
            mIsMaintainTaskStarted = false;
        }
        return freeSuccess;
    }

    private boolean scheduleMaintainTask(Runnable task, long delayMillis) {
        if (!mIsInited)
            return false;

        KSimpleGlobalTask.getInstance().removeCallbacks(task);
        return KSimpleGlobalTask.getInstance().postDelayed(task, delayMillis);
    }

    private void markWorking() {
        long currentTime = System.currentTimeMillis();
        setLastAccessTime(currentTime);
        startMaintainTask();
        startReportTask();
    }

    private void startMaintainTask() {
        if (mIsMaintainTaskStarted)
            return;

       /* synchronized(mFreeDbAndThreadTask) {
            if (!mIsMaintainTaskStarted) {
                mFreeDbAndThreadTask.scheduleTask();
                mIsMaintainTaskStarted = true;
            }
        }*/
    }

    public long getLastAccessTime() {
        return mLastAccessTime;
    }

    public void setLastAccessTime(long time) {
        mLastAccessTime = time;
    }

    private void startReportTask() {
        if (mIsReportTaskStarted)
            return;

    }

    private boolean doReportWork() {


        return true;
    }

    private static void postPkgQueryResult(CleanCloudQueryExecutor queryExecutor, final PkgQueryCallback callback,
                                           final Collection<PkgQueryData> directCallbackData,
                                           final boolean finalQueryComplete, final int mQueryId) {
        NLog.d(TAG, "postPkgQueryResult----->");
        queryExecutor.post(CleanCloudQueryExecutor.CALLBACK_RUNNER_2, new Runnable() {
            @Override
            public void run() {
                callback.onGetQueryResult(mQueryId, directCallbackData, finalQueryComplete);
            }
        });
    }

    private static void postDirQueryResult(CleanCloudQueryExecutor queryExecutor,
                                           final DirQueryCallbackData callback,
                                           final Collection<DirQueryData> directCallbackData) {
        NLog.d(TAG, "ResidualCloudQueryImpl postDirQueryResult");
        if (callback.mRealComplete) {
            //KTestSignReportMgr.getInstance().notifyPutResidualTestSignInfoComplete();
        }
        queryExecutor.post(CleanCloudQueryExecutor.CALLBACK_RUNNER_2, new Runnable() {
            @Override
            public void run() {
                callback.mCallback.onGetQueryResult(callback.mQueryIdForUser, directCallbackData, callback.mRealComplete);
            }
        });
    }

    /**
     * 异步上报本地规则检出结果;
     * @author 
     * @date 2015.1.15
     * */
    private void postLocalRuleReportData(final ArrayList<DirQueryData> androidDataResult, final String rootPath){

    }
    
}

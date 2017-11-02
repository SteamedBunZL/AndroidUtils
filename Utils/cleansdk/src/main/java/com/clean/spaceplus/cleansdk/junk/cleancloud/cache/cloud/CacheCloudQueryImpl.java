package com.clean.spaceplus.cleansdk.junk.cleancloud.cache.cloud;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.junk.cleancloud.CacheQueryStatistics;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudGlue;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudPathConverter;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudPref;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudQueryExecutor;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CloudCfgDataWrapper;
import com.clean.spaceplus.cleansdk.junk.cleancloud.KCleanCloudQueryLogic;
import com.clean.spaceplus.cleansdk.junk.cleancloud.cache.local.CacheLocalQuery;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.CloudCfgKey;
import com.clean.spaceplus.cleansdk.junk.engine.FalseFilterManager;
import com.clean.spaceplus.cleansdk.junk.engine.PatternCache;
import com.clean.spaceplus.cleansdk.junk.engine.util.NameFilter;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.clean.spaceplus.cleansdk.junk.engine.CacheFindRegSubPaths;
import com.clean.spaceplus.cleansdk.junk.mgmt.CacheCloudMgmt;
import com.clean.spaceplus.cleansdk.util.AesUtil;
import com.clean.spaceplus.cleansdk.util.CachePathUtil;
import com.clean.spaceplus.cleansdk.util.StringUtils;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import space.network.cleancloud.CleanCloudDef;
import space.network.cleancloud.KCacheCloudQuery;
import space.network.cleancloud.MultiTaskTimeCalculator;
import space.network.cleancloud.core.cache.KCacheCommonData;
import space.network.cleancloud.core.cache.KCacheDef;
import space.network.cleancloud.core.residual.KResidualDef;
import space.network.commondata.KCleanCloudEnv;
import space.network.util.CleanTypeUtil;
import space.network.util.compress.EnDeCodeUtils;
import space.network.util.hash.KQueryMd5Util;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/23 13:56
 * @copyright TCL-MIG
 */
public class CacheCloudQueryImpl implements KCacheCloudQuery {
    private static final String TAG = CacheCloudQueryImpl.class.getSimpleName();
    private String mLanguage = "en";
    PatternCache mPatternCache = new PatternCache();
    private Context mContext;
    private CleanCloudGlue mCleanCloudGlue;
    private CacheCloudPkgQueryLogic mCacheCloudPkgQueryLogic;
    private CacheQueryShowInfoLogic mCacheQueryShowInfoLogic;
    private CacheQueryStatistics mCachePkgQueryStatistics;
    private CacheQueryStatistics mCacheShowInfoQueryStatistics;
    private volatile boolean mEmergencyFalseDirSignSwitch;
    private volatile boolean mFileSignSwitch;
    private volatile CustomCleanCarefulPathGetter mCustomCleanCarefulPathGetter;
    private CleanCloudQueryExecutor mQueryExecutor;
    private CleanCloudPathConverter[] mCleanCloudPathConverters = null;
    private String[] mSdCardRootPath = null;
    private CleanCloudPathConverter.Md5Cache mPathConvertMd5Cache = new CleanCloudPathConverter.Md5Cache();
    private CacheLocalQuery mCacheLocalQuery;
    private AtomicInteger mQueryIdSeed = new AtomicInteger();
    private volatile int mCurrentScanType = ScanType.INVALID;
    private volatile boolean mHaveNotCleaned;
    private boolean mIsInited;

    private CacheCloudMgmt cacheCloudMgmt;
    private boolean mNeedNetQuery = false;

    public CacheCloudQueryImpl(Context context, CleanCloudGlue glue,boolean needNetQuery){
        mContext = context;
        mCleanCloudGlue = glue;
        mCacheLocalQuery = new CacheLocalQuery(context, glue);
        mCacheLocalQuery.setCacheLifeTime(CleanCloudPref.getInstanse().getCacheLifetime());
        mCachePkgQueryStatistics = new CacheQueryStatistics(glue, KCleanCloudEnv.CloudQueryType.CACHE_PKG_QUERY);

        mNeedNetQuery = needNetQuery;
        mCacheCloudPkgQueryLogic = new CacheCloudPkgQueryLogic(context);
        mCacheQueryShowInfoLogic = new CacheQueryShowInfoLogic(context);
        mCacheShowInfoQueryStatistics = new CacheQueryStatistics(glue, KCleanCloudEnv.CloudQueryType.CACHE_SHOW_QUERY);
        mQueryExecutor = new CleanCloudQueryExecutor();
        mCacheCloudPkgQueryLogic.initialize(mQueryExecutor);
        cacheCloudMgmt = CacheCloudMgmt.newInstance();
    }

    @Override
    public boolean initialize(boolean first) {
        synchronized (this) {
            if (!mIsInited) {
                mCacheCloudPkgQueryLogic.initialize(mQueryExecutor);
                mCacheQueryShowInfoLogic.initialize(mQueryExecutor);
                mIsInited = true;
                mFileSignSwitch =  CloudCfgDataWrapper.getCloudCfgBooleanValue(CloudCfgKey.CLOUD_SWITCH_KEY,
                        CloudCfgKey.CLEAN_CLOUD_CACHE_FILE_SWITCH, true);
                mHaveNotCleaned = first;
                mEmergencyFalseDirSignSwitch =  CloudCfgDataWrapper.getCloudCfgBooleanValue(CloudCfgKey.CLOUD_SWITCH_KEY,
                        CloudCfgKey.CLEAN_CLOUD_CACHE_DIR_EMERGENCY_FALSE_SIGN, true);
            }
            return mIsInited;
        }
    }

    @Override
    public void unInitialize() {
        synchronized (this) {
            if (mIsInited) {
                mQueryExecutor.quit();
//                mCacheLocalQuery.unInitialize();
                mCacheQueryShowInfoLogic.unInitialize();
                mCacheCloudPkgQueryLogic.unInitialize();
                cleanPathEnumCache();
                mIsInited = false;
                mCustomCleanCarefulPathGetter = null;
            }
        }
    }

    @Override
    public boolean setLanguage(String language) {
        mLanguage = language;
        mCacheLocalQuery.setLanguage(language);
        cacheCloudMgmt.setLanguage(language);
        return false;
    }

    @Override
    public String getLanguage() {
        return mLanguage;
    }

    @Override
    public boolean setCustomCleanCarefulPathGetter(CustomCleanCarefulPathGetter pathGetter) {
        mCustomCleanCarefulPathGetter = pathGetter;
        return true;
    }

    @Override
    public boolean queryByPkgName(Collection<PkgQueryParam> pkgnames, PkgQueryCallback callback, boolean pureAsync, boolean forceNetQuery) {
        if (!mIsInited)
            return false;

        if (null == pkgnames || null == callback || pkgnames.isEmpty()){
            return false;
        }

        int queryId = getQueryId();
        callback.onGetQueryId(queryId);

        Collection<PkgQueryData> querydatas = new ArrayList<>(pkgnames.size());
        MessageDigest md5 = KQueryMd5Util.getMd5Digest();
        for (PkgQueryParam param : pkgnames) {
            PkgQueryData data = new PkgQueryData();
            data.mQueryParam = param;
            data.mLanguage = getLanguage();
            data.mResult = new PkgQueryResult();
            KCacheCommonData.CachePkgQueryInnerData pkgQueryInnerData = new KCacheCommonData.CachePkgQueryInnerData();

            byte[] md5Bytes = KQueryMd5Util.getPkgQueryMd5Bytes(md5, param.mPkgName);
            pkgQueryInnerData.mPkgNameMd5 = EnDeCodeUtils.byteToHexString(md5Bytes);
            pkgQueryInnerData.mPkgNameMd5High64Bit = KQueryMd5Util.getMD5High64BitFromMD5(md5Bytes);

            data.mInnerData = pkgQueryInnerData;
            querydatas.add(data);
        }

        mCurrentScanType = pkgnames.iterator().next().mCleanType;
        return mCacheCloudPkgQueryLogic.query(querydatas, callback, pureAsync, forceNetQuery, queryId);
    }

    @Override
    public ArrayList<SysCacheFlagQueryData> queryCleanFlagByPkgName(Collection<String> pkgNames) {
        if (null == pkgNames || pkgNames.isEmpty()) {
            return null;
        }
        MessageDigest md5 = KQueryMd5Util.getMd5Digest();
        ArrayList<SysCacheFlagQueryData> result = new ArrayList<SysCacheFlagQueryData>(pkgNames.size());
        for (String pkgName : pkgNames) {
            SysCacheFlagQueryData data = new SysCacheFlagQueryData();
            KCacheCommonData.SysCacheFlagQueryInnerData innerData = new KCacheCommonData.SysCacheFlagQueryInnerData();
            byte[] md5Bytes = KQueryMd5Util.getPkgQueryMd5Bytes(md5, pkgName);
            innerData.mPkgNameMd5 = EnDeCodeUtils.byteToHexString(md5Bytes);
            innerData.mPkgNameMd5High64Bit = KQueryMd5Util.getMD5High64BitFromMD5(md5Bytes);

            data.mPkgName = pkgName;
            data.mInnerData = innerData;
            result.add(data);
        }
        mCacheLocalQuery.queryCleanFlagForCache(result);
        return result;
    }

    @Override
    public void setPkgNetQueryTimeController(MultiTaskTimeCalculator timeCalculator) {

    }

    @Override
    public boolean setSdCardRootPath(String[] paths) {
        if (null == paths || 0 == paths.length)
            return false;

        int cnt = 0;
        for (String path : paths) {
            if (TextUtils.isEmpty(path))
                continue;

            ++cnt;
        }
        if (0 == cnt)
            return false;

        synchronized (this) {
            mCleanCloudPathConverters = new CleanCloudPathConverter[cnt];
            mSdCardRootPath = new String[cnt];
            CleanCloudPathConverter converter;
            int i = 0;
            for (String path : paths) {
                if (TextUtils.isEmpty(path))
                    continue;

                converter = new CleanCloudPathConverter();
                //一定要在设置setSdCardRootPath前调用setMd5Cache才能共享cache
                converter.setMd5Cache(mPathConvertMd5Cache);
                converter.setSdCardRootPath(path);

                mCleanCloudPathConverters[i] = converter;
                mSdCardRootPath[i] = converter.getSdCardRootPath();
                ++i;
            }
        }
        return true;
    }

    @Override
    public String[] getSdCardRootPath() {
        return new String[0];
    }

    @Override
    public void cleanPathEnumCache() {
        synchronized (this) {
            if (mCleanCloudPathConverters != null) {
                for (CleanCloudPathConverter converter : mCleanCloudPathConverters) {
                    converter.cleanPathEnumCache();
                }
            }
            mPathConvertMd5Cache.clear();
        }
    }

    @Override
    public ArrayList<PkgQueryPathItem> localGetDirPathByContentType(int[] contentTypes) {
        return null;
    }

    @Override
    public ArrayList<PkgQueryData> localGetNormalDirPathByPkgName(Collection<PkgQueryParam> pkgnames, boolean isGetShowInfo) {
        return null;
    }

    @Override
    public int waitForComplete(long timeout, boolean discardQueryIfTimeout, CleanCloudDef.ScanTaskCtrl ctrl) {
        return mQueryExecutor.waitForComplete(timeout, discardQueryIfTimeout, ctrl);
    }

    /**
     * 设置网络查询相关参数
     * @param uuid
     * @param appVersion
     * @return
     */
    public boolean setParams(String uuid, int appVersion) {
//        mCacheShowInfoNetQuery.setOthers(uuid, appVersion);
//        return mCacheNetQuery.setOthers(uuid, appVersion);
        return false;
    }

    /**
     * 获取查询id
     * @return
     */
    private int getQueryId(){
        return mQueryIdSeed.incrementAndGet();
    }

    private class CacheCloudPkgQueryLogic extends KCleanCloudQueryLogic<PkgQueryData, PkgQueryCallback> {
        public CacheCloudPkgQueryLogic(Context context) {
            //super(context);
            super(context,mNeedNetQuery);
        }

        @Override
        protected boolean localQuery(final int queryId, Collection<PkgQueryData> data, PkgQueryCallback callback) {
            return CacheCloudQueryImpl.this.localQuery(queryId, data, callback);
        }

        @Override
        protected boolean netQuery(final int queryId, Collection<PkgQueryData> datas, PkgQueryCallback callback) {
            NLog.d("CCQ", "NQuery S");
            long nStartTime = System.currentTimeMillis();
            return CacheCloudQueryImpl.this.netQuery(datas, callback);
        }

        @Override
        protected boolean isNeedNetQuery(PkgQueryData data, PkgQueryCallback callback) {
            return CacheCloudQueryImpl.this.isNeedNetQuery(data, callback);
        }

        @Override
        protected void onGetQueryResult(Collection<PkgQueryData> datas, PkgQueryCallback callback, boolean queryComplete, int queryId, int dataTotalCount, int dataCurrentCount) {
            CacheCloudQueryImpl.this.onGetQueryResult(datas, callback, queryComplete, queryId, dataTotalCount, dataCurrentCount);
        }

        @Override
        protected boolean checkStop(PkgQueryCallback callback) {
            return CacheCloudQueryImpl.this.checkStop(callback);
        }
    }

    private class LocalPkgQueryCallbackImpl implements CacheLocalQuery.LocalPkgQueryCallback {
        int mTotalCount = 0;
        int mCallbackCount = 0;

        LocalPkgQueryCallbackImpl(int totalCount) {
            mTotalCount = totalCount;
        }
        public void onGetQueryResult(int queryId, PkgQueryData result, PkgQueryCallback outCallback) {

            processPathItem(result, outCallback);

            if (result.mResult.mPkgQueryPathItems != null) {
                boolean isGetShowInfoComplete = true;
                mCacheLocalQuery.queryShowInfo(result.mResult.mPkgQueryPathItems);
                for (PkgQueryPathItem item : result.mResult.mPkgQueryPathItems) {
                    if (checkIsShowInfoNeedNetQuery(item)) {
                        isGetShowInfoComplete = false;
                        break;
                    }
                }

                if (isGetShowInfoComplete) {
                    ArrayList<PkgQueryData> datas = new ArrayList<>(1);
                    datas.add(result);
                    ((KCacheCommonData.CachePkgQueryInnerData)result.mInnerData).mIsCallback = true;
                    ++mCallbackCount;
                    boolean finalQueryComplete = (mCallbackCount >= mTotalCount);
                    postQueryResult(mQueryExecutor, outCallback, datas,
                            finalQueryComplete, queryId);
                }
            }
        }
    }

    static class CacheQueryShowInfoCallback {

        private boolean queryComplete;
        private LinkedList<PkgQueryData> data;
        private PkgQueryCallback callback;
        private int queryId;
        private final CleanCloudQueryExecutor mQueryExecutor;

        public CacheQueryShowInfoCallback(CleanCloudQueryExecutor queryExecutor, PkgQueryCallback callback, int queryId, LinkedList<PkgQueryData> data, boolean queryComplete) {
            this.mQueryExecutor = queryExecutor;
            this.callback = callback;
            this.queryId = queryId;
            this.data = data;
            this.queryComplete = queryComplete;
        }

        public boolean checkStop() {
            return callback.checkStop();
        }

        public void onGetQueryResult(int queryId2, LinkedList<PkgQueryData> directCallbackData, boolean finalQueryComplete) {
            if (finalQueryComplete) {
                postQueryResult(mQueryExecutor, callback, data, queryComplete, queryId);
            }

        }
    }

    private class CacheQueryShowInfoLogic extends KCleanCloudQueryLogic<PkgQueryPathItem, CacheQueryShowInfoCallback> {


        /**
         * @param context
         */
        public CacheQueryShowInfoLogic(Context context) {
            //super(context);
            super(context,mNeedNetQuery);
        }

        @Override
        protected boolean localQuery(final int queryId, Collection<PkgQueryPathItem> data, CacheQueryShowInfoCallback callback) {
            return CacheCloudQueryImpl.this.localQueryShowInfo(data, callback);
        }

        @Override
        protected boolean netQuery(final int queryId, Collection<PkgQueryPathItem> data, CacheQueryShowInfoCallback callback) {
            boolean bRet = CacheCloudQueryImpl.this.netQueryShowInfo(data, callback);
            return bRet;
        }

        @Override
        protected boolean isNeedNetQuery(PkgQueryPathItem item,
                                         CacheQueryShowInfoCallback callback) {
            boolean needNetQuery = item.mShowInfoResultType == ShowInfoResultType.UNKNOWN
                    || item.mShowInfoResultSource == ResultSourceType.INVAILD
                    || item.mShowInfo == null
                    || item.mShowInfo.mResultLangMissmatch
                    || item.mShowInfo.mResultExpired;
            if (needNetQuery) {
                return true;
            }
            return false;
        }

        @Override
        protected void onGetQueryResult(Collection<PkgQueryPathItem> datas, CacheQueryShowInfoCallback callback, boolean queryComplete, int queryId, int dataTotalCount, int dataCurrentCount) {
            CacheCloudQueryImpl.this.onGetShowInfoQueryResult(datas, callback, queryComplete, queryId, dataTotalCount, dataCurrentCount);

        }

        @Override
        protected boolean checkStop(CacheQueryShowInfoCallback callback) {
            return callback == null ? false : callback.checkStop();
        }

    }

    /**
     * 本地扫描
     * @param queryId
     * @param data
     * @param callback
     * @return
     */
    private boolean localQuery(int queryId, Collection<PkgQueryData> data, PkgQueryCallback callback) {
        return mCacheLocalQuery.query(data, queryId, new LocalPkgQueryCallbackImpl(data.size()), callback);
    }

    /**
     * 检查任务是否停止
     * @param iCallback
     * @return
     */
    private boolean checkStop(PkgQueryCallback iCallback) {
        return iCallback != null && iCallback.checkStop();
    }

    /**
     * 是否需要网络查询
     * @param data
     * @param callback
     * @return
     */
    private boolean isNeedNetQuery(PkgQueryData data, PkgQueryCallback callback) {
        boolean result = false;
        // 去除语言的判断条件,如果语言不匹配会在最后检出时进行再次云端查询
        result = (data.mResult.mQueryResult == PkgResultType.UNKNOWN || data.mResultExpired
                || (!data.mResultIntegrity && (!mHaveNotCleaned ||data.mResultIntegrityNeedNetQuery)) );
        return result;
    }

    /**
     * 执行网络查询
     * @param datas
     * @param callback
     * @return
     */
    private boolean netQuery(Collection<PkgQueryData> datas, PkgQueryCallback callback) {
//        boolean query = mCacheNetQuery.query(datas, callback);
//        if(!query){
//            for (PkgQueryData pkgQueryData : datas) {
//                if(pkgQueryData.mErrorCode == 0){
//                    pkgQueryData.mErrorCode = -1;
//                }
//            }
//        }
//        return query;
        boolean query = false;
        try {
            query = cacheCloudMgmt.getCacheByPkgName(datas, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return query;
    }

    private void onGetQueryResult(
            Collection<PkgQueryData> datas,
            final PkgQueryCallback callback,
            boolean queryComplete,
            int queryId,
            int dataTotalCount,
            int dataCurrentCount) {
        final LinkedList<PkgQueryData> directCallbackData = new LinkedList<>();
        if (datas == null || datas.size() == 0) {
            return;
        }
        LinkedList<PkgQueryData> updateCacheResults = new LinkedList<>();
        LinkedList<PkgQueryData> repairedResult = new LinkedList<>();

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
        }

        //将云端查询的结果更新到数据库
        mCacheLocalQuery.updatePkgCache(updateCacheResults);

        for (PkgQueryData data : datas) {
            // 网络查询可能失败,如果原来有本地过期结果或者不完整数据,那么修正一下结果，依然检出
            if (data.mErrorCode != 0
                    && (data.mResultExpired || !data.mResultIntegrity)
                    && data.mResult != null
                    && data.mResult.mQueryResult != PkgResultType.UNKNOWN) {
                data.mErrorCode = 0;
                data.mResultSource = ResultSourceType.CACHE;
            }
            boolean isCallback = ((KCacheCommonData.CachePkgQueryInnerData)data.mInnerData).mIsCallback;
            if (!isCallback) {
                if (data.mErrorCode != 0
                        || data.mResult == null
                        || data.mResult.mQueryResult == PkgResultType.UNKNOWN
                        || data.mResultSource == ResultSourceType.INVAILD) {
                    directCallbackData.add(data);
                } else if (data.mErrorCode == 0 && data.mResult.mQueryResult != PkgResultType.UNKNOWN) {
                    processPathItem(data, callback);
                    if (data.mResult.mPkgQueryPathItems != null && data.mResult.mPkgQueryPathItems.size() > 0) {
                        repairedResult.add(data);
                    } else {
                        directCallbackData.add(data);
                    }
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
        mCachePkgQueryStatistics.addHitCountData(hitCountHF, hitCountCache, hitCountCloud, hitCountTotal);

        if (repairedResult != null && repairedResult.size() > 0) {
            // data.mResult.mQueryResult == PkgResultType.DIR_LIST
            LinkedList<PkgQueryPathItem> queryDatas = new LinkedList<>();
            for (PkgQueryData data : repairedResult) {
                if (data.mErrorCode == 0
                        && data.mResult != null
                        && data.mResult.mQueryResult != PkgResultType.UNKNOWN
                        && data.mResult.mPkgQueryPathItems != null) {
                    mCachePkgQueryStatistics.addError3Count(data.mResult.mPkgQueryPathItems.size());
                }
                for (PkgQueryPathItem item : data.mResult.mPkgQueryPathItems) {
                    if (checkIsShowInfoNeedNetQuery(item)) {
                        queryDatas.add(item);
                    }
                }
            }
            CacheQueryShowInfoCallback cacheQueryShowInfoCallback = new CacheQueryShowInfoCallback(mQueryExecutor, callback, queryId, repairedResult, queryComplete);
            mCacheQueryShowInfoLogic.query(queryDatas, cacheQueryShowInfoCallback, true, false, getQueryId());
        }
        if (directCallbackData.size() > 0) {
            final boolean finalQueryComplete = (queryComplete && (repairedResult == null || repairedResult.size() == 0));
            final int myQueryId = queryId;
            postQueryResult(mQueryExecutor, callback, directCallbackData,
                    finalQueryComplete, myQueryId);
        }
    }

    boolean isNeedCheckCustomCleanCarefulPath(PkgQueryData data,
                                              PkgQueryPathItem item,
                                              ArrayList<String> sortedCarefulPathList) {
        //把深度扫描中的用户主动勾选的目录转移到建议扫描中
        //对于其他入口(空间不足入口,隐私扫描入口),以及文件级检出,不处理这个逻辑,按普通逻辑走
        return (null != sortedCarefulPathList
                && (item.mPathType == CachePathType.DIR || item.mPathType == CachePathType.DIR_REG)
                && (data.mQueryParam.mCleanType == ScanType.CAREFUL
                || data.mQueryParam.mCleanType == ScanType.SUGGESTED
                || data.mQueryParam.mCleanType == ScanType.SUGGESTED_WITH_CLEANTIME));
    }

    boolean addPathResultForCustomCleanCarefulPath(PkgQueryData data,
                                                   PkgQueryPathItem item,
                                                   ArrayList<String> sortedCarefulPathList) {
        boolean result = false;
        if (!isNeedCheckCustomCleanCarefulPath(data, item, sortedCarefulPathList)) {
            data.mResult.mPkgQueryPathItems.add(item);
            result = true;
        } else {
            String strPath = KQueryMd5Util.toLowerCase(item.mPath);
            int index = Collections.binarySearch(sortedCarefulPathList, strPath);
            if (index >= 0) {
                //如果是建议扫描,那就加进去
                if (data.mQueryParam.mCleanType == ScanType.SUGGESTED
                        || data.mQueryParam.mCleanType == ScanType.SUGGESTED_WITH_CLEANTIME) {
                    item.isCustomCleanPath = true;
                    data.mResult.mPkgQueryPathItems.add(item);
                    result = true;
                    NLog.i(TAG, "add to items");
                }//else... 是深度扫描,不加到结果集合中
                else{
                    NLog.i(TAG, "not add to items");
                }
            } else {
                if (CleanTypeUtil.accept(data.mQueryParam.mCleanType, item)) {
                    data.mResult.mPkgQueryPathItems.add(item);
                    result = true;
                }
            }
        }
        return result;
    }

    private void processPathItem(PkgQueryData data, PkgQueryCallback callback) {
        processPathItem(data, callback, true);
    }

    private void processPathItem(PkgQueryData data, PkgQueryCallback callback, boolean processAllPath) {
        if (null == data.mResult.mPkgQueryPathItems) {
            data.mResult.mPkgQueryPathItems = new LinkedList<KCacheCloudQuery.PkgQueryPathItem>();
        }
        KCacheCommonData.CachePkgQueryInnerData innerData = (KCacheCommonData.CachePkgQueryInnerData) data.mInnerData;
        if (innerData.mPkgQueryPathItems == null || innerData.mPkgQueryPathItems.isEmpty())
            return;

        FalseFilterManager.FalseSignFilter dirSignFilter = getNormalDirSignFilter();

        CustomCleanCarefulPathGetter customPathGetter = mCustomCleanCarefulPathGetter;

        ArrayList<String> carefulPathList = null;
        ArrayList<String> sortedCarefulPathList = null;
        if (processAllPath && customPathGetter != null) {
            carefulPathList = customPathGetter.getCustomCleanPath(data.mQueryParam.mPkgName);
            if (carefulPathList != null) {
                if (!carefulPathList.isEmpty()) {
                    sortedCarefulPathList = new  ArrayList<>(carefulPathList.size());
                    for (String str : carefulPathList) {
                        sortedCarefulPathList.add(KQueryMd5Util.toLowerCase(str));
                    }
                    Collections.sort(sortedCarefulPathList);
                }
            }
        }

        Collection<PkgQueryPathItem> innerPkgQueryPathItems = innerData.mPkgQueryPathItems;
        //数据处理过后就赋空值,避免重复处理
        innerData.mPkgQueryPathItems = null;

        for (PkgQueryPathItem item : innerPkgQueryPathItems) {
            if (item == null || TextUtils.isEmpty(item.mPathString)) {
                continue;
            }

            //把深度扫描中的用户主动勾选的目录转移到建议扫描中
            //对于其他入口(空间不足入口,隐私扫描入口),以及文件级检出,不处理这个逻辑,按普通逻辑走
            if (!isNeedCheckCustomCleanCarefulPath(data, item, sortedCarefulPathList)) {
                if (!CleanTypeUtil.accept(data.mQueryParam.mCleanType, item)) {
                    continue;
                }
            }

            if (mCleanCloudGlue != null
                    && mCleanCloudGlue.isInCloudFilter(
                    KCacheDef.CLEAN_CACHE_ID_FILTER_NAME,
                    item.mSignId)) {
                continue;
            }

            int dirid = 0;
            try {
                dirid = Integer.valueOf(item.mSignId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mEmergencyFalseDirSignSwitch) {
//                if (mEmergencyFalseDirSignFilter != null && mEmergencyFalseDirSignFilter.filter(dirid)) {
//                    continue;
//                }

                if (dirSignFilter != null && dirSignFilter.filter(dirid)) {
                    continue;
                }
            }

            switch (item.mPathType) {
                case CachePathType.DIR:
                    repairNetworkQueryResult(item, callback, data, true, sortedCarefulPathList);
                    break;
                case CachePathType.DIR_REG://b9c77f89ecb05f50f2592d02219c1bc4//[0-9a-za-z]{32}/image
                    if (processAllPath) {
                        convertNetworkQueryRegResult(item, callback, data, true, sortedCarefulPathList);
                    }
                    break;
                case CachePathType.ROOT_DIR:
                case CachePathType.ROOT_DIR_REG:
                    if (processAllPath) {
                        if (data.mResult.mSystemDataCleanItems == null) {
                            data.mResult.mSystemDataCleanItems = new LinkedList<PkgQueryPathItem>();
                        }
                        data.mResult.mSystemDataCleanItems.add(item);
                    }
                    break;
                case CachePathType.FILE:
                case CachePathType.FILE_2:
                    if (processAllPath) {
                        if (mFileSignSwitch) {
                            repairNetworkQueryResult(item, callback, data, false, null);
                        }
                    }
                    break;
                case CachePathType.FILE_REG:
                case CachePathType.FILE_REG_2:
                    if (processAllPath) {
                        if (mFileSignSwitch) {
                            convertNetworkQueryRegResult(item, callback, data, false, null);
                        }
                    }
                    break;
                default:
            }
        }
    }

    private boolean checkIsShowInfoNeedNetQuery(PkgQueryPathItem item) {
        boolean result = false;
        if (item.mShowInfoResultType == ShowInfoResultType.UNKNOWN
                || item.mShowInfoResultSource == ResultSourceType.INVAILD
                || item.mShowInfo == null
                || item.mShowInfo.mResultLangMissmatch
                || item.mShowInfo.mResultExpired) {
            result = true;
        }
        return result;
    }

    private static void postQueryResult(CleanCloudQueryExecutor queryExecutor, final PkgQueryCallback callback,
                                        final Collection<PkgQueryData> directCallbackData,
                                        final boolean finalQueryComplete, final int mQueryId) {
        queryExecutor.post(CleanCloudQueryExecutor.CALLBACK_RUNNER_2, new Runnable() {
            @Override
            public void run() {
                callback.onGetQueryResult(mQueryId, directCallbackData, finalQueryComplete);
            }
        });
    }

    private boolean localQueryShowInfo(Collection<PkgQueryPathItem> data, CacheQueryShowInfoCallback callback) {
        return mCacheLocalQuery.queryShowInfo(data);
    }

    private boolean netQueryShowInfo(Collection<PkgQueryPathItem> data, CacheQueryShowInfoCallback callback) {
        boolean query = false;
//        query = mCacheShowInfoNetQuery.query(data, callback);
        return query;
    }

    private void onGetShowInfoQueryResult(Collection<PkgQueryPathItem> datas, CacheQueryShowInfoCallback callback, boolean queryComplete, int queryId, int dataTotalCount, int dataCurrentCount) {

        if (datas == null || datas.size() == 0) {
            return;
        }

        LinkedList<PkgQueryPathItem> updateCacheResults = new LinkedList<PkgQueryPathItem>();
        int hitCountTotal = 0;
        int hitCountHF    = 0;
        int hitCountCache = 0;
        int hitCountCloud = 0;

        for (PkgQueryPathItem item : datas) {
            if (item.mShowInfoResultType == ShowInfoResultType.SUCCESS
                    && item.mShowInfoResultSource == ResultSourceType.CLOUD) {
                updateCacheResults.add(item);
            }
            if (item.mShowInfoResultType != ShowInfoResultType.UNKNOWN) {
                ++hitCountTotal;
                switch (item.mShowInfoResultSource) {
                    case ResultSourceType.HFREQ:
                        ++hitCountHF;
                        break;
                    case ResultSourceType.CACHE:
                        ++hitCountCache;
                        break;
                    case ResultSourceType.CLOUD:
                        ++hitCountCloud;
                        break;
                    default:
                }
            }
        }
        mCacheShowInfoQueryStatistics.addHitCountData(hitCountHF, hitCountCache, hitCountCloud, hitCountTotal);
        mCacheLocalQuery.updateShowInfoCache(updateCacheResults);
        callback.onGetQueryResult(queryId, null, queryComplete);
    }

    private FalseFilterManager.FalseSignFilter getNormalDirSignFilter() {
        return null;
    }

    private void convertNetworkQueryRegResult(
            PkgQueryPathItem pathItem,
            PkgQueryCallback callback,
            PkgQueryData data,
            boolean isDir,
            ArrayList<String> sortedCarefulPathList) {
        if (null == mCleanCloudPathConverters)
            return;

        long timeBegin = 0;
        int finum = 0;
        timeBegin = System.currentTimeMillis();

        if (pathItem.mPathType == CachePathType.FILE_REG_2) {
            if (!getMultiFileSigns(pathItem)) {
                return;
            }
        }
        String[] dirSegs = pathItem.mPathString.split("//");//b9c77f89ecb05f50f2592d02219c1bc4//[0-9a-za-z]{32}/image
        CacheFindRegSubPaths.Counting counting = new CacheFindRegSubPaths.Counting();
        if (dirSegs.length == 2) {
            for (CleanCloudPathConverter converter : mCleanCloudPathConverters) {
                String strParentPath = converter.getDirPath(dirSegs[0]);
                if (TextUtils.isEmpty(strParentPath)) {
                    continue;
                }

                String strFullPath = converter.getFullPathFromRelativePath(strParentPath);
                File testFile = new File(strFullPath);
                if (!testFile.exists()) {
                    continue;
                }
                ArrayList<File> matchedPath = null;
                boolean isScanByMediaStore = false;
                if (CleanMediaFlagUtil.IsScanByMediaStore(pathItem.mCleanMediaFlag)) {
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
                        isScanByMediaStore = true;
                        matchedPath = matchSubPathByMediaStore(testFile,
                                dirSegs[1], callback, isDir, counting);
                    }
                }
                if ( !isScanByMediaStore ) {
                    matchedPath = matchSubPath(testFile,
                            dirSegs[1], callback, isDir, counting);
                }
                if (matchedPath == null || matchedPath.size() == 0) {
                    continue;
                }
                pathItem.mIsPathStringExist = true;
                if (isDir) {
                    for (File file : matchedPath) {
                        PkgQueryPathItem newItem = (PkgQueryPathItem) pathItem.clone();
                        newItem.mPath = file.getPath();
                        if (TestFlagUtil.isTestSign(pathItem.mTestFlag)) {
                            //put cache test sign info
                        } else {
                            addPathResultForCustomCleanCarefulPath(data, newItem, sortedCarefulPathList);
                        }
                    }
                } else {
                    ArrayList<String> files = new ArrayList<String>(matchedPath.size());
                    for (File file : matchedPath) {
                        files.add(file.getPath());
                        ++finum;
                    }

                    if (!files.isEmpty()) {
                        PkgQueryPathItem newItem = (PkgQueryPathItem) pathItem.clone();
                        newItem.mPath = strFullPath;
                        newItem.mFiles = new String[files.size()];
                        newItem.mFiles = files.toArray(newItem.mFiles);

                        if (TestFlagUtil.isTestSign(pathItem.mTestFlag)) {
                            //put cache test sign info
//                            for (String path : files) {
//                                KTestSignReportMgr.getInstance().putCacheTestSignInfo(
//                                        path,
//                                        isDir,
//                                        Integer.parseInt(newItem.mSignId),
//                                        newItem.mCleanType,
//                                        pathItem.mTestFlag,
//                                        data.mResultSource);
//                            }
                        } else {
                            data.mResult.mPkgQueryPathItems.add(newItem);
                        }
                    }
                }
            }
        }

        long timeEnd = System.currentTimeMillis();
        int stime = (int)(timeEnd - timeBegin);
        //report to server
    }

    private void repairNetworkQueryResult(
            PkgQueryPathItem pathItem,
            PkgQueryCallback callback,
            PkgQueryData data,
            boolean isDir,
            ArrayList<String> sortedCarefulPathList) {
        if (null == mCleanCloudPathConverters)
            return;

        boolean[] isDirectoryResult = new boolean[1];
        if (pathItem.mPathType == CachePathType.FILE_2) {
            if (!getMultiFileSigns(pathItem)) {
                return;
            }
        }

        for (CleanCloudPathConverter converter : mCleanCloudPathConverters) {
            String strPath = converter.getPath(pathItem.mPathString, isDir, isDirectoryResult);
            if (TextUtils.isEmpty(strPath)) {
                continue;
            }

            String strFullPath = converter.getFullPathFromRelativePath(strPath);
            if (TextUtils.isEmpty(strFullPath)) {
                continue;
            }

            File testFile = new File(strFullPath);
            if (!testFile.exists()) {
                continue;
            }

            if (isDir != isDirectoryResult[0]) {
                continue;
            }

            pathItem.mIsPathStringExist = true;
            PkgQueryPathItem resultItem = (PkgQueryPathItem) pathItem.clone();
            String path = testFile.getPath();
            if (isDir) {
                resultItem.mPath = path;
            } else {
                resultItem.mPath = testFile.getParent();
                resultItem.mFiles = new String[1];
                resultItem.mFiles[0] = path;
            }

            if (TestFlagUtil.isTestSign(resultItem.mTestFlag)) {
                NLog.d(TAG, "---is test sign---");

            } else {
                if (isDir) {
                    addPathResultForCustomCleanCarefulPath(data, resultItem, sortedCarefulPathList);
                } else {
                    data.mResult.mPkgQueryPathItems.add(resultItem);
                }
            }

        }

    }

    private static boolean getMultiFileSigns(PkgQueryPathItem pkgQueryPathItem) {
        if (pkgQueryPathItem == null || TextUtils.isEmpty(pkgQueryPathItem.mPathString)
                || (pkgQueryPathItem.mPathType != CachePathType.FILE_2 && pkgQueryPathItem.mPathType != CachePathType.FILE_REG_2)) {
            return false;
        }
        CachePathUtil.CachePathData dirData = CachePathUtil.parseHighFreqDbDirString(pkgQueryPathItem.mPathString, pkgQueryPathItem.mPathType);
        CachePathUtil.getOtherMD5IDs(dirData);
        List<String> mMd5Ids2 = dirData.mMd5Ids2;
        List<String> mMd5Ids3 = dirData.mMd5Ids3;
        StringBuilder pathStringBuilder = new StringBuilder();
        int appendCnt = 0;
        for (String id : dirData.mMd5Ids) {
            if (TextUtils.isEmpty(id)) {
                continue;
            }
            if (appendCnt > 0) {
                pathStringBuilder.append('+');
            }
            pathStringBuilder.append(id);
            ++appendCnt;
        }
        AesUtil aes = null;
        StringBuilder sb = new StringBuilder();
        if (mMd5Ids2 != null && mMd5Ids2.size() > 0) {
            if (aes == null) {
                final String Q = "stop";
                String key = KCacheDef.H + Q + KResidualDef.W;
                aes = new AesUtil(key.getBytes());
            }
            int i = 0;
            for (String md5Seg : mMd5Ids2) {
                if (i == 0) {
                    i++;
                    continue;
                } else {
                    i++;
                }
                if (!TextUtils.isEmpty(md5Seg)) {
                    sb.append(md5Seg);
                }
            }
            byte[] decrypt = aes.decrypt(EnDeCodeUtils.hexStringtoBytes(sb.toString()));
            try {
                if (decrypt != null) {
                    String separator = mMd5Ids2.get(0);
                    if (separator.equalsIgnoreCase(",") && !(mMd5Ids3 != null && mMd5Ids3.size() > 0)) {
                        separator = "//";
                    }
                    pathStringBuilder.append(separator + new String(decrypt, "utf-8"));
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            sb.delete(0, sb.length());
        }
        if (mMd5Ids3 != null && mMd5Ids3.size() > 0) {
            int i = 0;
            for (String md5Seg : mMd5Ids3) {
                if (i == 0) {
                    i++;
                    continue;
                } else {
                    i++;
                }
                if (!TextUtils.isEmpty(md5Seg)) {
                    sb.append(md5Seg);
                }
            }
            byte[] decrypt = aes.decrypt(EnDeCodeUtils.hexStringtoBytes(sb.toString()));
            try {
                if (decrypt != null) {
                    pathStringBuilder.append("//");
                    pathStringBuilder.append(new String(decrypt, "utf-8"));
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            sb.delete(0, sb.length());
        }
        pkgQueryPathItem.mPathString = pathStringBuilder.toString();
        return true;
    }

    private ArrayList<File> matchSubPathByMediaStore (File parentPath, String subPath, PkgQueryCallback callback, boolean isDirectory,CacheFindRegSubPaths.Counting counting){
        final String strRootPath = FileUtils.addSlash(parentPath.getPath());
        final Pattern pattern = Pattern.compile(subPath);
        ArrayList<String> results = new ArrayList<String>();
        FileUtils.listFilesInDirandSubDirByMediaStore(isDirectory ? null : results, isDirectory ? results : null, strRootPath, new NameFilter() {
            @Override
            public boolean accept(String parent, String sub, boolean bFolder) {
                String strFileFullPath = FileUtils.addSlash(parent) + sub;
                String strSubPath = strFileFullPath.substring(strRootPath.length());
                if (strSubPath != null && pattern.matcher(StringUtils.toLowerCase(strSubPath)).matches()) {
                    return true;
                }
                return false;
            }
        });
        ArrayList<File> fileResults = new ArrayList<File>();
        for ( String path : results ) {
            fileResults.add( new File(path) );
        }
        return fileResults;
    }

    private ArrayList<File> matchSubPath (File parentPath, String subPath, PkgQueryCallback callback, boolean isDirectory, CacheFindRegSubPaths.Counting counting){
        return CacheFindRegSubPaths.matchSubPath(parentPath, subPath, callback, isDirectory, counting, mPatternCache);
    }
}

package com.clean.spaceplus.cleansdk.junk.cleancloud.cache.local;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.clean.spaceplus.cleansdk.base.bean.ValueType;
import com.clean.spaceplus.cleansdk.base.db.DatabaseProvider;
import com.clean.spaceplus.cleansdk.base.db.MySQLiteDB;
import com.clean.spaceplus.cleansdk.base.db.SqlUtil;
import com.clean.spaceplus.cleansdk.base.db.pkgcache.CacheLangQueryTable;
import com.clean.spaceplus.cleansdk.base.db.pkgcache.CachePathQueryTable;
import com.clean.spaceplus.cleansdk.base.db.pkgcache.CachePkgQueryTable;
import com.clean.spaceplus.cleansdk.base.db.pkgcache.PkgCacheProvider;
import com.clean.spaceplus.cleansdk.base.db.pkgcache_hf.CacheHfLangQueryDescParamTable;
import com.clean.spaceplus.cleansdk.base.db.pkgcache_hf.CacheHfLangQueryDescTable;
import com.clean.spaceplus.cleansdk.base.db.pkgcache_hf.CacheHfLangQueryFormatDescTable;
import com.clean.spaceplus.cleansdk.base.db.pkgcache_hf.CacheHfLangQueryNameTable;
import com.clean.spaceplus.cleansdk.base.db.pkgcache_hf.CacheHfPathQueryDirMd5Table;
import com.clean.spaceplus.cleansdk.base.db.pkgcache_hf.CacheHfPathQueryDirTable;
import com.clean.spaceplus.cleansdk.base.db.pkgcache_hf.CacheHfPathQueryTable;
import com.clean.spaceplus.cleansdk.base.db.pkgcache_hf.CacheHfPkgQueryTable;
import com.clean.spaceplus.cleansdk.base.db.pkgcache_hf.PkgCacheHfProvider;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudGlue;
import com.clean.spaceplus.cleansdk.junk.cleancloud.cache.CacheProviderUpdate;
import com.clean.spaceplus.cleansdk.junk.engine.FalseFilterManager;
import com.clean.spaceplus.cleansdk.util.CachePathUtil;
import com.clean.spaceplus.cleansdk.util.IOUtils;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import space.network.cleancloud.KCacheCloudQuery;
import space.network.cleancloud.KCacheCloudQuery.PkgQueryData;
import space.network.cleancloud.KCacheCloudQuery.PkgQueryPathItem;
import space.network.cleancloud.KCacheCloudQuery.PkgResultType;
import space.network.cleancloud.KCacheCloudQuery.ResultSourceType;
import space.network.cleancloud.KCacheCloudQuery.ShowInfo;
import space.network.cleancloud.KCacheCloudQuery.ShowInfoResultType;
import space.network.cleancloud.core.cache.KCacheCommonData;
import space.network.commondata.KCleanCloudEnv;
import space.network.util.KMiscUtils;
import space.network.util.net.KJsonUtils;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/25 16:06
 * @copyright TCL-MIG
 */
public class CacheLocalQuery {
    private static final String TAG = CacheLocalQuery.class.getSimpleName();
    private static final long TIME_WAIT_FOR_HF_DB_QUERY = 20000l;
    public static final long ONE_DAY_TIMEMILLIS = 24 * 60 * 60 * 1000;// 一天的毫秒数
    private long mCacheLifeTime = 7 * ONE_DAY_TIMEMILLIS;// 默认值是7天
    private long mNotFoundCacheLifeTime = 2 * ONE_DAY_TIMEMILLIS;// 灰缓存暂定是2天
    private long mCurrentTime;
    private CacheProviderUpdate mCacheProviderUpdate;
    private final ReentrantLock mRWLock = new ReentrantLock();
    private volatile FalseFilterManager.FalseSignFilter mSignFilter;
    private PkgCacheHfProvider mPkgCacheHfProvider;
    private PkgCacheProvider mPkgCacheProvider;
    private String mLanguage = "en";

    public CacheLocalQuery(Context context, CleanCloudGlue cleanCloudGlue) {
        mCurrentTime = System.currentTimeMillis();
        mPkgCacheHfProvider = PkgCacheHfProvider.getInstance();
        mPkgCacheProvider = PkgCacheProvider.getInstance();
//        mPkgCacheDb = new KCacheQueryCacheDB(
//                context, cleanCloudGlue, KCacheDef.PKGCACHE_CACHE_DBNAME);
//        mHighFrequentDb = new CleanCloudReadOnlyHighFreqDB(
//                context, cleanCloudGlue, KCacheDef.getHighFregDbName(cleanCloudGlue));
        mCacheProviderUpdate = new CacheProviderUpdate();
//
//        mPkgCacheDb.AutoFreeSwitch(true);
//        mHighFrequentDb.AutoFreeSwitch(true);
    }

    //系统缓存任务
    /**
     * @param pkgParams 待查询的数据结构体，包含包名及MD5
     * @return 查询成功返回true，反之false
     */
    public boolean queryCleanFlagForCache(ArrayList<KCacheCloudQuery.SysCacheFlagQueryData> pkgParams) {
        if (pkgParams.isEmpty()) {
            return false;
        }

        HashMap<String, KCacheCloudQuery.SysCacheFlagQueryData> keyPkgNameMap = new HashMap<String, KCacheCloudQuery.SysCacheFlagQueryData>(pkgParams.size());
        HashMap<Long, KCacheCloudQuery.SysCacheFlagQueryData> keyPkgValueMap = new HashMap<Long, KCacheCloudQuery.SysCacheFlagQueryData>(pkgParams.size());

        for (KCacheCloudQuery.SysCacheFlagQueryData param : pkgParams) {
            KCacheCommonData.SysCacheFlagQueryInnerData innerData =
                    (KCacheCommonData.SysCacheFlagQueryInnerData)param.mInnerData;
            if (!TextUtils.isEmpty(innerData.mPkgNameMd5) && param.mSysFlag == -1) {
                keyPkgNameMap.put(innerData.mPkgNameMd5, param);
            }

            if (innerData.mPkgNameMd5High64Bit != 0 && param.mSysFlag == -1) {
                keyPkgValueMap.put(innerData.mPkgNameMd5High64Bit, param);
            }
        }

        queryCacheSysFlagByCacheDB(keyPkgNameMap);
        return queryCacheSysFlagByHFDB(keyPkgValueMap);
    }

    private boolean queryCacheSysFlagByCacheDB(HashMap<String, KCacheCloudQuery.SysCacheFlagQueryData> keyPkgNameMap) {
        //MyDBData myCacheData = null;
        boolean result = false;
        try {
                queryCacheSysFlagFromCacheDB(mPkgCacheProvider, keyPkgNameMap);
                result = true;
        } catch (Exception e) {
            NLog.e(TAG, " IllegalStateException %s" , e);
        }

        return result;
    }

    private boolean queryCacheSysFlagByHFDB(HashMap<Long, KCacheCloudQuery.SysCacheFlagQueryData> keyPkgValueMap) {
        boolean result = false;
        try {
                queryCacheSysFlagFromHFDB(mPkgCacheHfProvider, keyPkgValueMap);
                result = true;
        } catch (Exception e) {
            NLog.e(TAG, " IllegalStateException %s", e);
        }

        return result;
    }

    private void queryCacheSysFlagFromHFDB(DatabaseProvider db,
                                           HashMap<Long, KCacheCloudQuery.SysCacheFlagQueryData> keyPkgValueMap) {

        int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        int pageNum = 0;
        String r;
        int sysFlag = -1;
        Cursor cursor = null;

        Set<Long> keys = keyPkgValueMap.keySet();
        long[] md5High64Bit = new long[keys.size()];
        Iterator<Long> it =  keys.iterator();
        int i = 0;
        while(it.hasNext()){
            md5High64Bit[i] = it.next().longValue();
            i++;
        }

        while ((r = SqlUtil.arrayLongToSQLInStringIncreasing(
                md5High64Bit, pageSize, pageNum++)) != null) {
            String sql = null;
            //sql = "select lower(pkg),sysflag " + "from pkgquery " + "where pkg in " + r + " ;";
            sql = String.format("select lower(%s),%s from %s where %s in " + r,
                    CacheHfPkgQueryTable.PKG, CacheHfPkgQueryTable.SYS_FLAG, CacheHfPkgQueryTable.TABLE_NAME, CacheHfPkgQueryTable.PKG);

            try {
                NLog.d(TAG, "queryCacheSysFlagFromHFDB exec sql = %s", sql);
                cursor = db.rawQuery(sql, null);
                if (cursor != null){
                    int count = cursor.getCount();
                    NLog.i(TAG, " HFDB count %d", count );
                }
                if (cursor == null || cursor.getCount() <= 0) {
                    if (cursor != null) {
                        cursor.close();
                        cursor = null;
                    }
                    continue;
                }

                while (cursor.moveToNext()) {
                    sysFlag = cursor.getInt(1);
                    KCacheCloudQuery.SysCacheFlagQueryData param = keyPkgValueMap.get(cursor.getLong(0));
                    if (param != null && param.mSysFlag == -1) {
                        param.mSysFlag = sysFlag;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    private void queryCacheSysFlagFromCacheDB(DatabaseProvider dataBase,
                                              HashMap<String, KCacheCloudQuery.SysCacheFlagQueryData> keyPkgNameMap) {

        int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        int pageNum = 0;
        String r;
        int sysFlag = -1;
        Cursor cursor = null;
        while ((r = SqlUtil.collectionToSQLInStringIncreasing(
                keyPkgNameMap.keySet(), pageSize, pageNum++, true)) != null) {
            String sql = null;
            //sql = "select lower(hex(pkg)),sysflag " + "from pkgquery " + "where pkg in " + r + " ;"
            sql = String.format("select lower(hex(%s)),%s from %s where %s in %s",
                    CachePkgQueryTable.PKG, CachePkgQueryTable.SYS_FLAG, CachePkgQueryTable.TABLE_NAME, CachePkgQueryTable.PKG, r);
                        try {
                            NLog.d(TAG, "queryCacheSysFlagFromCacheDB exec sql = %s", sql);
                            cursor = dataBase.rawQuery(sql, null);
                            if (cursor != null){
                                int count = cursor.getCount();
                                NLog.i(TAG, " CacheDB count %d", count );
                            }
                            if (cursor == null || cursor.getCount() <= 0) {
                                if (cursor != null) {
                                    cursor.close();
                                    cursor = null;
                    }
                    continue;
                }

                while (cursor.moveToNext()) {
                    sysFlag = cursor.getInt(1);
                    KCacheCloudQuery.SysCacheFlagQueryData param = keyPkgNameMap.get(cursor.getString(0));
                    if (param != null && param.mSysFlag == -1) {
                        param.mSysFlag = sysFlag;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    /**
     * 本地pkg查询回调
     */
    public  interface LocalPkgQueryCallback {
         void onGetQueryResult(int queryId, KCacheCloudQuery.PkgQueryData result, KCacheCloudQuery.PkgQueryCallback outCallback);
    }

    static class QueryPkgMiddleResult {
        PkgQueryData result;
        String dirs;
    }

    private static class MiddleResult {
        public ShowInfo mShowInfo;
        public String mLangnamedesc;
    }

    /**
     * 设置缓存有效时间
     *
     * @param days
     */
    public void setCacheLifeTime(int days) {
        if (days != 0) {
            mCacheLifeTime = ONE_DAY_TIMEMILLIS * days;
        }
    }

    /**
     * 查询库数据
     *
     * @param data
     * @param queryId
     * @param callback
     * @param outCallback
     * @return
     */
    public boolean query(
            Collection<KCacheCloudQuery.PkgQueryData> data,
            int queryId,
            LocalPkgQueryCallback callback,
            KCacheCloudQuery.PkgQueryCallback outCallback) {
        int queryByPkg = queryByPkg(data, queryId, callback, outCallback);
        return data == null || queryByPkg == data.size();
    }

    /**
     * 更新本地缓存数据
     *
     * @param results
     * @return
     */
    public boolean updatePkgCache(Collection<KCacheCloudQuery.PkgQueryData> results) {
        NLog.d(TAG, "CacheLocalQuery  updatePkgCache results = "+results);
        if (results == null || results.size() == 0) {
            NLog.d(TAG, "CacheLocalQuery 需要更新数据库 size 为 0");
            return false;
        }
        /*boolean updatePkgCache  =  mCacheProviderUpdate.updatePkgCache(results, mPkgCacheProvider);
        boolean updatePathCache =  mCacheProviderUpdate.updatePathCache(results, mPkgCacheProvider);
        return updatePkgCache && updatePathCache;*/
        return true;
    }

    public void updateShowInfoCache(
            LinkedList<KCacheCloudQuery.PkgQueryPathItem> updateCacheResults) {
        if (updateCacheResults == null || updateCacheResults.size() == 0) {
            return;
        }
//        mCacheProviderUpdate.updateShowInfoCache(updateCacheResults,
//                mLanguage);
    }

    public boolean queryShowInfo(Collection<KCacheCloudQuery.PkgQueryPathItem> datas) {
        int size = 0;
        try {
                size = queryShowInfoByHFDb(mPkgCacheHfProvider, datas);
        } catch (IllegalStateException e) {
            handleSQLiteDatabaseIllegalStateException(e);
        }

        try {
                size += queryShowInfoByCacheDb(mPkgCacheProvider, datas);
        } catch (Exception e) {
            handleSQLiteDatabaseIllegalStateException(e);
        }

        return size > 0;
    }

    private int queryByPkg(
            Collection<KCacheCloudQuery.PkgQueryData> datas,
            int queryId,
            LocalPkgQueryCallback callback,
            KCacheCloudQuery.PkgQueryCallback outCallback) {
        int size = 0;

        long currentTimeMillis = System.currentTimeMillis();
        setCurrentTime(currentTimeMillis);

        try {
            initFalseFilterData();
            size = queryPkgByHFDb(mPkgCacheHfProvider, datas, mSignFilter, queryId, callback, outCallback);
        } catch (Exception e) {
            handleSQLiteDatabaseIllegalStateException(e);
        }


        // 可能有结果，但只是正则匹配的结果，所以要去缓存库再查
        int size2 = 0;
        try {
            size2 = queryPkgByCacheDb(mPkgCacheProvider,
                    datas, mSignFilter, queryId, callback, outCallback);
            NLog.e(TAG, "queryPkgByCacheDb size = %s", size2);
        } catch (Exception e) {
            handleSQLiteDatabaseIllegalStateException(e);
        }

        if (size > 0 && size2 > 0) {
            size += size2;
        } else if (size2 > 0) {
            size = size2;
        }

        return size;
    }

    private void setCurrentTime(long time) {
        mCurrentTime = time;
    }

    private long getCurrentTime() {
        return mCurrentTime;
    }

    /**
     * 设置错误过滤
     */
    private void initFalseFilterData() {
        mRWLock.lock();
        try {
//            if (mSignFilter == null) {
//                FalseFilterManager falseFilterManager = FalseFilterFactory.getFalseFilterManagerInstance();
//                mSignFilter = falseFilterManager.getFalseDataByCategory(CategoryKey.KEY_CACHE);
//            }
//            if (mSignFilter != null) {
//                mSignFilter.acquireReference();
//            }
        } finally {
            mRWLock.unlock();
        }
    }

    /**
     * db 异常处理
     * @param e
     * @param dbHolder
     * @param dbData
     * @param isHFDb
     * @param type
     * @param cleanCloudGlue
     */
    private void handleSQLiteDatabaseIllegalStateException(IllegalStateException e,
                                                   MySQLiteDB dbHolder,
                                                   MySQLiteDB.MyDBData dbData,
                                                   boolean isHFDb,
                                                   short type,
                                                   CleanCloudGlue cleanCloudGlue) {
        String msg = e.getMessage();
        if (msg.contains("database not open")
                || (msg.contains("re-open") && msg.contains("SQLiteDatabase"))
                || (msg.contains("database") && msg.contains("already closed"))) {

            if (dbData != null) {
                dbHolder.releaseReference(dbData);
                dbHolder.unInitDb();
            }
        } else {
            throw e;
        }
    }

    /**
     * 查询高频库
     * @param datas
     * @param filter
     * @param queryId
     * @param callback
     * @param outCallback
     * @return
     */
    private int queryPkgByHFDb(DatabaseProvider db,
            Collection<KCacheCloudQuery.PkgQueryData> datas,
            FalseFilterManager.FalseSignFilter filter,
            int queryId,
            LocalPkgQueryCallback callback,
            KCacheCloudQuery.PkgQueryCallback outCallback) {
        ValueType resultCount = new ValueType(0);
        if (datas == null || datas.size() == 0) {
            return -1;
        }

        HashMap<Long, KCacheCloudQuery.PkgQueryData> keyPkgNameMap = new HashMap<>(
                datas.size());
        for (PkgQueryData data : datas) {
            if (data.mErrorCode != 0
                    || (data.mResult != null && data.mResult.mQueryResult == PkgResultType.UNKNOWN)) {
                keyPkgNameMap.put(((KCacheCommonData.CachePkgQueryInnerData) data.mInnerData).mPkgNameMd5High64Bit, data);
            }
        }

        Set<Long> keys = keyPkgNameMap.keySet();
        long[] md5High64Bit = new long[keys.size()];
        Iterator<Long> it = keys.iterator();
        int i = 0;
        while (it.hasNext()) {
            md5High64Bit[i] = it.next().longValue();
            i++;
        }

        HashMap<String, String> pathSegMD5s = new HashMap<>(keyPkgNameMap.size() / 2);
        int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        int pageNum = 0;
        String r;
        int queryCount = md5High64Bit.length % pageSize == 0 ? md5High64Bit.length / pageSize : md5High64Bit.length / pageSize + 1;
        CountDownLatch queryLatch = new CountDownLatch(queryCount);
        try {
            while ((r = SqlUtil.arrayLongToSQLInStringIncreasing(md5High64Bit, pageSize, pageNum++)) != null) {
               doHfPkgQuery(db, queryLatch, r, keyPkgNameMap, filter, callback, pathSegMD5s, queryId, outCallback, resultCount);
            }

            queryLatch.await(TIME_WAIT_FOR_HF_DB_QUERY, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }

        pathSegMD5s.clear();
        return resultCount.intValue();
    }

    private void doHfPkgQuery(final DatabaseProvider db, final CountDownLatch queryLatch, final String r
    , final HashMap<Long, KCacheCloudQuery.PkgQueryData> keyPkgNameMap
    , final FalseFilterManager.FalseSignFilter filter, final LocalPkgQueryCallback callback, final HashMap<String, String> pathSegMD5s
    , final int queryId, final KCacheCloudQuery.PkgQueryCallback outCallback, final ValueType resultCount){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = null;
                StringBuilder sql = new StringBuilder();
                int pkgid = -1;
                //sql = "select pkgid,dirs,lower(pkg),is_integrity from pkgquery where pkg in " + r + " ;";
                sql.append(SQL_QUERY_PKG_PREFIX).append(r);

                try {
                    cursor = db.rawQuery(sql.toString(), null);
                    if (cursor == null) {
                        return;
                    }

                    if (cursor.getCount() <= 0) {
                        cursor.close();
                        cursor = null;
                        return;
                    }


                    while (cursor.moveToNext()) {
                        pkgid = cursor.getInt(0);
					/*如果第一bit位值等于1 则认为是完整的数据*/
                        int integrity = cursor.getInt(3);
                        boolean is_integrity = (integrity & 0x1) == 1;
                        long pkgMd5Value = cursor.getLong(2);
                        PkgQueryData result = keyPkgNameMap.get(pkgMd5Value);
                        resultCount.increment();
                        result.mResultIntegrity = is_integrity;
                    /*如果第二bit位值等于1 则认为不完整数据首扫下需要联网查询*/
                        result.mResultIntegrityNeedNetQuery = (integrity & 0x2) != 0;
                        result.mResult.mPkgId = pkgid;
                        if (pkgid == 0) {
                            //不支持去误报
                            result.mResult.mQueryResult = PkgResultType.NOT_FOUND;
                            result.mErrorCode = 0;
                            result.mResultSource = ResultSourceType.HFREQ;
                            continue;
                        }
                        //如果数据被去误报,只是把过期标记打上,让重新查询,没有联网或者缓存没有,则还会使用这个数据,以确保检出
                        if ((filter != null && filter.filter(pkgid))) {
                            result.mResultExpired = true;
                        } else {
                            result.mResultExpired = false;
                        }
                        String dirs = null;
                        if (!cursor.isNull(1)) {
                            dirs = cursor.getString(1);
                        }

                        QueryPkgMiddleResult middleResult = new QueryPkgMiddleResult();
                        middleResult.result = result;
                        middleResult.dirs = dirs;
                        /////////////////////////////////////////////////////////////////////
                        //为了性能,先恢复原来的查询方式,让结果可以尽早的回调出去
                        //middleResults.add(middleResult);
                        int count = 0;
                        KCacheCommonData.CachePkgQueryInnerData innerData = (KCacheCommonData.CachePkgQueryInnerData) middleResult.result.mInnerData;
                        count = getPkgQueryDirResultByHFDb(db, innerData, middleResult.dirs, pathSegMD5s);
                        int queryResult = PkgResultType.UNKNOWN;
                        if (count < 0) {
                            queryResult = PkgResultType.UNKNOWN;
                        } else if (count == 0) {
                            queryResult = PkgResultType.NOT_FOUND;
                        } else if (count > 0) {
                            queryResult = PkgResultType.DIR_LIST;
                        }

                        middleResult.result.mResult.mQueryResult = queryResult;
                        middleResult.result.mErrorCode = 0;
                        middleResult.result.mResultSource = ResultSourceType.HFREQ;

                        //注释原有代码
//                    if (middleResult.result.mResult.mQueryResult == PkgResultType.DIR_LIST
//                            && !middleResult.result.mResultExpired && middleResult.result.mResultIntegrity) {
//                        if (callback != null) {
//                            callback.onGetQueryResult(queryId, middleResult.result, outCallback);
//                        }
//                    }
                        //注释原有代码

                        if (callback != null) {
                            callback.onGetQueryResult(queryId, middleResult.result, outCallback);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }

                    if(queryLatch != null){
                        queryLatch.countDown();
                    }
                }
            }
        });

        thread.setName("hf-cache-thread");
        thread.start();
    }

    /**
     * @param db
     * @param innerData
     * @param dirs
     * @param pathSegMD5s
     * @return 成功获取的数据个数,如果为负数说明数据完整性有问题,比如说库数据有问题
     */
    private int getPkgQueryDirResultByHFDb(DatabaseProvider db,
                                           KCacheCommonData.CachePkgQueryInnerData innerData, String dirs, HashMap<String, String> pathSegMD5s) {
        int count = 0;
        if (innerData.mPkgQueryPathItems != null) {
            innerData.mPkgQueryPathItems.clear();
        }
        boolean[] integrityResult = new boolean[1];
        Collection<KCacheCloudQuery.PkgQueryPathItem> pkgQueryDirItems = getPathDataFromHighFreqDb(
                db, dirs, pathSegMD5s, integrityResult);
        if (integrityResult[0]) {
            if (pkgQueryDirItems != null && pkgQueryDirItems.size() > 0) {
                innerData.mPkgQueryPathItems = pkgQueryDirItems;
                count = pkgQueryDirItems.size();
            }
        } else {
            count = -1;
        }
        return count;
    }

    private Collection<KCacheCloudQuery.PkgQueryPathItem> getPathDataFromHighFreqDb(
            DatabaseProvider db, String dirs, HashMap<String, String> pathSegMD5s, boolean[] integrityResult) {
        boolean integrityOK = true;
        Collection<KCacheCloudQuery.PkgQueryPathItem> result = null;
        Collection<String> signIds;
        if (!TextUtils.isEmpty(dirs)) {
            signIds = KJsonUtils.getStringArrayFromArrayString(dirs);
            if (signIds != null && signIds.size() > 0) {
                result = getPkgQueryPathItemDatasFromHighFreqDb(db, signIds, pathSegMD5s);
                if (result == null || result.isEmpty()) {
                    integrityOK = false;
                }
            }
        }
        if (integrityResult != null) {
            integrityResult[0] = integrityOK;
        }
        return result;
    }

    private Collection<KCacheCloudQuery.PkgQueryPathItem> getPkgQueryPathItemDatasFromHighFreqDb(
            DatabaseProvider db, Collection<String> signIds, HashMap<String, String> pathSegMD5s) {
        if (signIds == null || signIds.size() == 0) {
            return null;
        }
        ArrayList<KCacheCloudQuery.PkgQueryPathItem> items = new ArrayList<>(
                signIds.size());

        Cursor cursor = null;
        StringBuilder builder = new StringBuilder();
     /*   builder.append("select pathquery.pathtype,pathquerydir.dir,pathquery.cleantype,pathquery.cleantime,pathquery.cleanop,"
                + "pathquery.contenttype,pathquery.privacytype,pathquery.pathid,pathquery.isneedcheck,pathquery.cmtype,pathquery.test "
                + "from pathquery,pathquerydir "
                + "where "
                + "pathquery.pathid in "
                + KDBUtils.collectionSToSQLInString(signIds)
                + " and pathquery.path = pathquerydir._id");*/
        builder.append(SQL_QUERY_PATHQUERY_AND_PATHQUERYDIR_PREFIX).append(SqlUtil.collectionSToSQLInString(signIds));

        try {
            cursor = db.rawQuery(builder.toString(), null);
            if (cursor != null) {
                KCacheCloudQuery.PkgQueryPathItem item = null;
                while (cursor.moveToNext()) {
                    item = new KCacheCloudQuery.PkgQueryPathItem();
                    item.mPathType = cursor.getInt(0);
                    item.mPathString = cursor.getString(1);
                    item.mCleanType = cursor.getInt(2);
                    item.mCleanTime = cursor.getInt(3);
                    item.mCleanOperation = cursor.getInt(4);
                    item.mContentType = cursor.getInt(5);
                    item.mPrivacyType = cursor.getInt(6);
                    item.mSignId = "" + cursor.getInt(7);
                    item.mNeedCheck = cursor.getInt(8);
                    item.mCleanMediaFlag = cursor.getInt(9);
                    item.mTestFlag = cursor.getInt(10);
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        for (KCacheCloudQuery.PkgQueryPathItem item : items) {
            queryForMD5Path(db, item, pathSegMD5s);
        }

        return items;
    }

    /**
     * 查询md5路径
     * @param db
     * @param item
     * @param pathSegMD5s
     */
    private void queryForMD5Path(DatabaseProvider db, KCacheCloudQuery.PkgQueryPathItem item, HashMap<String, String> pathSegMD5s) {
        if (!TextUtils.isEmpty(item.mPathString)) {
            CachePathUtil.CachePathData dirData = CachePathUtil.parseHighFreqDbDirString(item.mPathString, item.mPathType);
            if (dirData == null || null == dirData.mMd5Ids[0])
                return;
            if (!TextUtils.isEmpty(dirData.mRemain)
                    && (item.mPathType == KCacheCloudQuery.CachePathType.FILE_2 || item.mPathType == KCacheCloudQuery.CachePathType.FILE_REG_2)) {
                CachePathUtil.getOtherMD5IDs(dirData);
            }

            int needQueryCnt = 0;
            String[] needQueryIds;
            int len2 = 0;
            int len3 = 0;
            List<String> mMd5Ids2 = dirData.mMd5Ids2;
            List<String> mMd5Ids3 = dirData.mMd5Ids3;
            if (mMd5Ids2 != null && mMd5Ids2.size() > 0) {
                len2 = mMd5Ids2.size() -1;
            }

            if (mMd5Ids3 != null && mMd5Ids3.size() > 0) {
                len3 = mMd5Ids3.size() -1;
            }
            needQueryIds = new String[CachePathUtil.PATH_PARSE_MAX_ID_COUNT + len2 + len3];

            for (String id : dirData.mMd5Ids) {
                if (TextUtils.isEmpty(id)){
                    continue;
                }

                if (pathSegMD5s.containsKey(id)) {
                    continue;
                }
                needQueryIds[needQueryCnt++] = id;
            }

            if (mMd5Ids2 != null) {
                int i =0;
                for (String id : mMd5Ids2) {
                    if (i == 0) {
                        i++;
                        continue;
                    }
                    i++;

                    if (TextUtils.isEmpty(id)){
                        continue;
                    }

                    if (pathSegMD5s.containsKey(id)) {
                        continue;
                    }
                    needQueryIds[needQueryCnt++] = id;
                }

            }
            if (mMd5Ids3 != null) {
                int i =0;
                for (String id : mMd5Ids3) {
                    if (i == 0) {
                        i++;
                        continue;
                    }
                    i++;
                    if (TextUtils.isEmpty(id)) {
                        continue;
                    }

                    if (pathSegMD5s.containsKey(id)) {
                        continue;
                    }
                    needQueryIds[needQueryCnt++] = id;
                }
            }

            boolean bQueryOk = true;
            if (needQueryCnt > 0) {
                StringBuilder builder = new StringBuilder();
                if (needQueryCnt == 1) {
                    //builder.append("select lower(hex(dirmd5)),_id from pathquerydirmd5 where _id = ");
                    builder.append(SQL_QUERY_MD5_DIR_PREFIX).append(needQueryIds[0]);
                } else {
                    //builder.append("select lower(hex(dirmd5)),_id from pathquerydirmd5 where _id in (");
                    builder.append(SQL_QUERY_MD5_DIR_PREFIX_2);

                    int appendCnt = 0;
                    for( String strQueryId : needQueryIds ) {
                        builder.append(strQueryId).append(',');
                        ++appendCnt;
                    }

                    if(appendCnt > 0){
                        builder.deleteCharAt(builder.length() - 1);
                    }

                    builder.append(")");
                }
                String strSqlString = builder.toString();
                Cursor cursorSeg = null;
                try {
                    bQueryOk = false;
                    cursorSeg = db.rawQuery(strSqlString, null);
                    if (cursorSeg != null && cursorSeg.getCount() > 0) {
                        bQueryOk = true;
                        while (cursorSeg.moveToNext()) {
                            pathSegMD5s.put("" + cursorSeg.getInt(1), cursorSeg.getString(0));
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursorSeg != null) {
                        cursorSeg.close();
                    }
                }

            }
            if (bQueryOk) {
                boolean bTransOk = true;
                StringBuilder pathStringbuilder = new StringBuilder();
                int appendCnt = 0;
                String strDir = null;
                for (String id : dirData.mMd5Ids) {
                    if (TextUtils.isEmpty(id)){
                        continue;
                    }

                    strDir = pathSegMD5s.get(id);
                    if (TextUtils.isEmpty(strDir)){
                        bTransOk = false;
                        break;
                    }

                    pathStringbuilder.append(strDir).append('+');
                    ++appendCnt;
                }

                if(appendCnt > 0){
                    pathStringbuilder.deleteCharAt(pathStringbuilder.length() - 1);
                }

                if (!TextUtils.isEmpty(dirData.mRemain)) {
                    pathStringbuilder.append(dirData.mRemain);
                } else {
                    StringBuilder sb = new StringBuilder();
                    if(mMd5Ids2 != null && mMd5Ids2.size() > 0) {
                        int i = 0;
                        for (String md5Seg: mMd5Ids2) {
                            if (i == 0) {
                                i++;
                                continue;
                            }
                            if( i != 1) {
                                sb.append('+');
                            }
                            i++;
                            String str = pathSegMD5s.get(md5Seg);
                            if (!TextUtils.isEmpty(str)) {
                                sb.append(str);
                            }
                        }
                        pathStringbuilder.append(mMd5Ids2.get(0)).append(sb);
                        sb.delete(0, sb.length());
                    }
                    if(mMd5Ids3 != null && mMd5Ids3.size() > 0) {
                        int i = 0;
                        for (String md5Seg: mMd5Ids3) {
                            if (i == 0) {
                                i++;
                                continue;
                            }
                            if(i != 1) {
                                sb.append('+');
                            }
                            i++;
                            String str = pathSegMD5s.get(md5Seg);
                            if (!TextUtils.isEmpty(str)) {
                                sb.append(str);
                            }
                        }
                        pathStringbuilder.append(mMd5Ids3.get(0)).append(sb);
                        sb.delete(0, sb.length());
                    }
                }
                if (bTransOk) {
                    item.mPathString = pathStringbuilder.toString();
                }
            }
        }
    }

    /**
     * 查询本地缓存库
     * @param datas
     * @param filter
     * @param queryId
     * @param callback
     * @param outCallback
     * @return
     */
    private int queryPkgByCacheDb(
            DatabaseProvider db,
            Collection<PkgQueryData> datas,
            FalseFilterManager.FalseSignFilter filter,
            int queryId,
            LocalPkgQueryCallback callback,
            KCacheCloudQuery.PkgQueryCallback outCallback) {
        int resultCount = 0;
        if (db == null || datas == null
                || datas.size() == 0) {
            return -1;
        }

        Cursor cursor = null;
        long time = 0L;
        int pkgid = -1;
        LinkedList<QueryPkgMiddleResult> middleResults = new LinkedList<>();
        HashMap<String, PkgQueryData> keyPkgNameMap = new HashMap<>(
                datas.size());
        for (PkgQueryData data : datas) {
            if (data.mErrorCode != 0
                    || (data.mResult != null && data.mResult.mQueryResult == PkgResultType.UNKNOWN)
                    || data.mResultExpired || !data.mResultIntegrity ) { //现在是先查高频库,高频库的数据过期是因为被去误报
                String queryKey = ((KCacheCommonData.CachePkgQueryInnerData) data.mInnerData).mPkgNameMd5;
                keyPkgNameMap.put(queryKey, data);
            }
        }
        int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        int pageNum = 0;
        String r;

        //分页查询，遍历全部pkg
        while ((r = SqlUtil.collectionToSQLInStringIncreasing(
                keyPkgNameMap.keySet(), pageSize, pageNum++, false)) != null) {
            String sql = null;
//            sql = "select pkgid,dirs,pkg,time from pkgquery where pkg in " + r ;
            sql = String.format("select %s,%s,%s,%s from %s where %s in " + r,
                    CachePkgQueryTable.PKG_ID, CachePkgQueryTable.DIRS, CachePkgQueryTable.PKG,
                    CachePkgQueryTable.TIME,CachePkgQueryTable.TABLE_NAME,CachePkgQueryTable.PKG);
            try {
                NLog.d(TAG, "queryPkgByCacheDb exec sql = %s", sql);
                cursor = db.rawQuery(sql, null);//db-->cache.db
                if (cursor == null) {
                    continue;
                }

                if (cursor.getCount() <= 0) {
                    cursor.close();
                    cursor = null;
                    continue;
                }
                while (cursor.moveToNext()) {
                    pkgid = cursor.getInt(0);
                    String pkgMd5 = cursor.getString(2);
                    time = cursor.getLong(3);
                    PkgQueryData result = keyPkgNameMap.get(pkgMd5);
                    resultCount++;
                    result.mResult.mPkgId = pkgid;
                    result.mResultIntegrity = true;//是否完整
                    result.mResultIntegrityNeedNetQuery = false;
                    if (pkgid == 0) {
                        //不支持去误报
                        result.mResult.mQueryResult = PkgResultType.NOT_FOUND;
                        result.mErrorCode = 0;
                        result.mResultSource = ResultSourceType.CACHE;
                        continue;
                    }
                    //如果数据被去误报,只是把过期标记打上,让重新查询,没有联网或者缓存没有,则还会使用这个数据,以确保检出
                    if (filter != null && filter.filter(pkgid)) {
                        result.mResultExpired = true;
                    } else {
                        result.mResultExpired = isResultExpired(getCurrentTime(),
                                time, result.mResult.mQueryResult);
                    }
                    String dirs = null;
                    if (!cursor.isNull(1)) {
                        dirs = cursor.getString(1);
                    }

                    QueryPkgMiddleResult middleResult = new QueryPkgMiddleResult();
                    middleResult.result = result;
                    middleResult.dirs   = dirs;//查询的路径id
                    middleResults.add(middleResult);
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        for (QueryPkgMiddleResult middleResult : middleResults) {
            int count = 0;
            KCacheCommonData.CachePkgQueryInnerData innerData =
                    (KCacheCommonData.CachePkgQueryInnerData) middleResult.result.mInnerData;
            count += getPkgQueryDirResultByCacheDb(db, innerData, middleResult.dirs, null);//进行路径匹配
            int queryResult = PkgResultType.UNKNOWN;
            if (count < 0) {
                queryResult = PkgResultType.UNKNOWN;
            } else if (count == 0){
                queryResult = PkgResultType.NOT_FOUND;
            } else if (count > 0){
                queryResult = PkgResultType.DIR_LIST;//获取路径md5
            }

            middleResult.result.mResult.mQueryResult = queryResult;
            middleResult.result.mErrorCode = 0;
            middleResult.result.mResultSource = ResultSourceType.CACHE;
            if (middleResult.result.mResult.mQueryResult == PkgResultType.DIR_LIST
                    && !middleResult.result.mResultExpired) {
                if (callback != null) {
                    callback.onGetQueryResult(queryId, middleResult.result, outCallback);
                }
            }
        }
        return resultCount;
    }

    private boolean isResultExpired(long currentTime, long resultSaveTime,
                                    int queryResult) {
        // currentTime不应该为0，异常情况，先当成不过期
        if (0 == currentTime)
            return false;

        // 下发的本地高频库时间是0,就认为是永久生效的
        if (0 == resultSaveTime)
            return false;

        boolean result = false;
        if (currentTime > resultSaveTime) {
            long diff = (currentTime - resultSaveTime);
            if (PkgResultType.NOT_FOUND == queryResult) {
                if (diff < mNotFoundCacheLifeTime)
                    result = false;
                else
                    result = true;
            } else {
                if (diff < mCacheLifeTime)
                    result = false;
                else
                    result = true;
            }
        } else {
            // 系统的时间发生错误了,当成过期了
            result = true;
        }
        return result;
    }

    private int getPkgQueryDirResultByCacheDb(DatabaseProvider db,
                                              KCacheCommonData.CachePkgQueryInnerData innerData,
                                              String dirs,
                                              HashMap<String, String> pathSegMD5s) {//sd cache pathSegMD5s=null
        int count = 0;
        if (innerData.mPkgQueryPathItems != null) {
            innerData.mPkgQueryPathItems.clear();
        }
        boolean[] integrityResult = new boolean[1];
        Collection<KCacheCloudQuery.PkgQueryPathItem> pkgQueryDirItems = getPathDataFromCacheDb(
                db, dirs, pathSegMD5s, integrityResult);
        if (integrityResult[0]) {
            if (pkgQueryDirItems != null && pkgQueryDirItems.size() > 0) {
                innerData.mPkgQueryPathItems = pkgQueryDirItems;
                count = pkgQueryDirItems.size();
            }
        } else {
            count = -1;
        }
        return count;
    }

    /**
     * 获取缓存子路径
     * @param db
     * @param dirs
     * @param pathSegMD5s
     * @param integrityResult
     * @return
     */
    private Collection<KCacheCloudQuery.PkgQueryPathItem> getPathDataFromCacheDb(
            DatabaseProvider db, String dirs, HashMap<String, String> pathSegMD5s, boolean[] integrityResult) {
        boolean integrityOK = true;
        Collection<KCacheCloudQuery.PkgQueryPathItem> result = null;
        Collection<String> signIds;
        if (!TextUtils.isEmpty(dirs)) {
            signIds = KJsonUtils.getStringArrayFromJsonArrayString(dirs);
            if (signIds != null && signIds.size() > 0) {
                result = getPkgQueryPathItemDatasFromCacheDb(db, signIds, pathSegMD5s);
                if (result == null || result.isEmpty()) {
                    integrityOK = false;
                }
            }
        }
        if (integrityResult != null) {
            integrityResult[0] = integrityOK;
        }
        return result;
    }

    /**
     * 根据pathid获取到path md5路径
     * @param db
     * @param signIds
     * @param pathSegMD5s
     * @return
     */
    private Collection<PkgQueryPathItem> getPkgQueryPathItemDatasFromCacheDb(
            DatabaseProvider db, Collection<String> signIds, HashMap<String, String> pathSegMD5s) {
        if (db == null || signIds == null || signIds.size() == 0) {
            return null;
        }
        ArrayList<PkgQueryPathItem> items = new ArrayList<>(
                signIds.size());

        Cursor cursor = null;

        StringBuilder builder = new StringBuilder();
        //builder.append("select pathType,path,cleantype,cleantime,cleanop,contenttype,privacytype,pathid,isneedcheck,cmtype,test from pathquery where pathid in ");

        String tempSql = String.format("select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s from %s where %s in ",
                CachePathQueryTable.PATH_TYPE,CachePathQueryTable.PATH,CachePathQueryTable.CLEAN_TYPE,
                CachePathQueryTable.CLEAN_TIME,CachePathQueryTable.CLEANOP,CachePathQueryTable.CONTENT_TYPE,
                CachePathQueryTable.PRIVACY_TYPE,CachePathQueryTable.PATH_ID,CachePathQueryTable.IS_NEED_CHECK,
                CachePathQueryTable.CMTYPE,CachePathQueryTable.TEST,CachePathQueryTable.TABLE_NAME,
                CachePathQueryTable.PATH_ID);
        builder.append(tempSql);
        builder.append(SqlUtil.collectionSToSQLInString(signIds));

        try {
            NLog.d(TAG, "getPkgQueryPathItemDatasFromCacheDb exec sql = %s", builder.toString());
            cursor = db.rawQuery(builder.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    KCacheCloudQuery.PkgQueryPathItem item = new KCacheCloudQuery.PkgQueryPathItem();
                    item.mCleanOperation = cursor.getInt(4);
                    item.mCleanTime = cursor.getInt(3);
                    item.mCleanType = cursor.getInt(2);
                    item.mPathString = cursor.getString(1);
                    item.mPathType = cursor.getInt(0);
                    item.mContentType = cursor.getInt(5);
                    item.mPrivacyType = cursor.getInt(6);
                    item.mSignId = "" + cursor.getInt(7);
                    item.mNeedCheck = cursor.getInt(8);
                    item.mCleanMediaFlag = cursor.getInt(9);
                    item.mTestFlag = cursor.getInt(10);
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return items;
    }

    private int queryShowInfoByHFDb(DatabaseProvider db,
                                    Collection<PkgQueryPathItem> datas) {
        if (datas == null
                || datas.size() == 0) {
            return 0;
        }

        //结果临时集合
        HashMap<String, KCacheCloudQuery.ShowInfo> idMap = new HashMap<String, ShowInfo>();
        LinkedList<PkgQueryPathItem> infos = new LinkedList<PkgQueryPathItem>();
        HashSet<String> ids = new HashSet<String>();
        for (PkgQueryPathItem item : datas) {
            if (item.mShowInfoResultType == ShowInfoResultType.UNKNOWN
                    || item.mShowInfoResultSource == ResultSourceType.INVAILD
                    || item.mShowInfo == null) {
                infos.add(item);
                ids.add(item.mSignId);// 多个PkgQueryPathItem可能共用一个signId
            }
        }

        boolean isTwLang = false;
        String language = mLanguage;
        String[] langSetting = null;
        if (language.equalsIgnoreCase(KMiscUtils.LANG_EN)) {
            langSetting = new String[1];
            langSetting[0] = KMiscUtils.LANG_EN;
        } else if (language.equalsIgnoreCase(KMiscUtils.LANG_TW)) {
            langSetting = new String[2];
            langSetting[0] = KMiscUtils.LANG_TW;
            langSetting[1] = KMiscUtils.LANG_CN;
            isTwLang = true;
        } else {
            langSetting = new String[2];
            langSetting[0] = mLanguage;
            langSetting[1] = KMiscUtils.LANG_EN;
        }

        String[] args = new String[langSetting.length];
        StringBuilder builder = new StringBuilder(8);
        for (int i = 0; i < langSetting.length; ++i) {
            builder.append('%');
            builder.append(langSetting[i]);
            builder.append('%');
            args[i] = builder.toString();
            builder.delete(0, builder.length());
        }

        int result = 0;
        String pathids = null;
        final int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        int pageNum = 0;
        Cursor cursorLang = null;
        LinkedList<MiddleResult> middleQueryResult = new LinkedList<MiddleResult>();
        while ((pathids = SqlUtil.collectionToSQLInStringIncreasing(ids,
                pageSize, pageNum++, false)) != null) {

            try {
                //String sql = "select pathid,langnamedesc from pathquery where pathid in "+ pathids;
                String sql = String.format("select %s,%s from %s where %s in %s",
                        CacheHfPathQueryTable.PATH_ID,CacheHfPathQueryTable.LANG_NAME_DESC,CacheHfPathQueryTable.TABLE_NAME,
                        CacheHfPathQueryTable.PATH_ID,pathids);
                cursorLang = db.rawQuery(sql, null);

                if (cursorLang == null || cursorLang.getCount() <= 0) {
                    if (cursorLang != null) {
                        cursorLang.close();
                        cursorLang = null;
                    }
                    continue;
                }

                // 如果是本地高频库并且语言是tw,那么最后强制设置mResultLangMissmatch为false,以减少联网
                cursorToHFShowInfoResult(idMap, cursorLang, true,
                        db, langSetting, middleQueryResult);
            } catch(Exception e){
                e.printStackTrace();
            } finally {
                if (cursorLang != null) {
                    cursorLang.close();
                }
            }
        }

        if (!middleQueryResult.isEmpty()) {
            for (MiddleResult middleResult : middleQueryResult) {
                if (!TextUtils.isEmpty(middleResult.mLangnamedesc)) {
                    getShowInfoFromHFDb(middleResult.mLangnamedesc, langSetting, db, middleResult.mShowInfo);
                }
            }
        }

        // 下面是赋值阶段
        for (PkgQueryPathItem item : infos) {
            ShowInfo showInfo = idMap.get(item.mSignId);
            if (showInfo != null) {
                item.mShowInfo = showInfo;
                result++;
            } else {
                continue;
            }
            item.mShowInfoResultType = ShowInfoResultType.SUCCESS;
            item.mShowInfoResultSource = ResultSourceType.HFREQ;
        }

        return result;
    }

    /**
     * 设置语言
     * @param language
     * @return
     */
    public boolean setLanguage(String language) {
        if (TextUtils.isEmpty(language))
            return false;

        mLanguage = KMiscUtils.toSupportedLanguage(language);
        return true;
    }

    private void cursorToHFShowInfoResult(HashMap<String, ShowInfo> idMap,
                                          Cursor cursor, boolean forceSetLangMatch, DatabaseProvider db,
                                          String[] langArray, List<MiddleResult> middleQueryResult) {
        String strLang;
        ArrayList<ShowInfo> langMissmatchFixList = null;
        if (forceSetLangMatch) {
            langMissmatchFixList = new ArrayList<ShowInfo>(idMap.size());
        }
        while (cursor.moveToNext()) {
            String pathid = cursor.getString(0);
            strLang = cursor.getString(1);
            boolean isMatch = false;
            if (strLang.contains(mLanguage)) {
                isMatch = true;
            }
            ShowInfo oldShowInfo = idMap.get(pathid);
            if (oldShowInfo != null && !oldShowInfo.mResultLangMissmatch) {// 已有好结果
                continue;
            }
            if (oldShowInfo != null && oldShowInfo.mResultLangMissmatch
                    && !isMatch) {// 这个是坏结果，以前的也是坏结果，干嘛还要继续更新坏结果
                continue;
            }
            KCacheCloudQuery.ShowInfo showInfo = new ShowInfo();
            showInfo.mResultLangMissmatch = !isMatch;
            idMap.put(pathid, showInfo);
            MiddleResult middle = new MiddleResult();
            middle.mLangnamedesc = strLang;
            middle.mShowInfo = showInfo;
            middleQueryResult.add(middle);
            // 如果是本地高频库并且语言是tw,那么最后强制设置mResultLangMissmatch为false,以减少联网
            if (langMissmatchFixList != null) {
                langMissmatchFixList.add(showInfo);
            }
        }
        if (langMissmatchFixList != null) {
            for (ShowInfo info : langMissmatchFixList) {
                info.mResultLangMissmatch = false;
            }
        }
    }

    private void getShowInfoFromHFDb(String langnamedesc, final String[] langArray, final DatabaseProvider db,
                                     KCacheCloudQuery.ShowInfo showInfo) {
        Cursor cursor = null;
        String[] langnamedescArray = null;
        langnamedescArray = getLangNameDescArray(db, langnamedesc,
                langArray);
        if (langnamedescArray == null || langnamedescArray.length == 0) {
            Log.e("KCacheLocalQuery",
                    "KCacheLocalQuery.getShowInfoFromHFDb(langnamedescArray.length == 0)");
            return;
        }
        try {
            String nameId = langnamedescArray[0];
            if (!TextUtils.isEmpty(nameId) && Integer.valueOf(nameId.trim()) > 0 ) {

                //String sql = "select name " + "from langqueryname " + "where _id = ? ;";

                String sql = String.format("select %s from %s where %s = ? ",
                        CacheHfLangQueryNameTable.NAME, CacheHfLangQueryNameTable.TABLE_NAME, CacheHfLangQueryNameTable._ID);
                NLog.d(TAG, "getShowInfoFromHFDb exec sql = %s", sql);
                cursor = db.rawQuery(sql, new String[]{nameId});
                if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                    showInfo.mName = cursor.getString(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        String params = null;

        try {
            String descId = langnamedescArray[1];
            if (!TextUtils.isEmpty(descId) && (Integer.valueOf(descId.trim())) > 0) {
                //String sql = "select langqueryformatdesc.formatdesc,langquerydesc.params from langquerydesc left join langqueryformatdesc on langquerydesc.desc = langqueryformatdesc._id where langquerydesc._id = ?";
                String sql = String.format("select %s,%s from %s left join %s on %s = %s where %s = ?",
                        getLangQueryFormatDescColumn(CacheHfLangQueryFormatDescTable.FORMAT_DESC), getLangQueryDescColumn(CacheHfLangQueryDescTable.PARAMS),
                        CacheHfLangQueryDescTable.TABLE_NAME, CacheHfLangQueryFormatDescTable.TABLE_NAME,getLangQueryDescColumn(CacheHfLangQueryDescTable.DESC),
                        getLangQueryFormatDescColumn(CacheHfLangQueryFormatDescTable._ID), getLangQueryDescColumn(CacheHfLangQueryDescTable._ID));

                NLog.d(TAG, "getShowInfoFromHFDb exec sql = %s",sql );
                cursor = db.rawQuery(sql, new String[]{descId});
                if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                    showInfo.mDescription = cursor.getString(0);
                    if (!cursor.isNull(1)) {
                        params = cursor.getString(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeSilently(cursor);
        }

        if (TextUtils.isEmpty(params)) {
            return;
        }
        Cursor paramsCursor = null;

        int[] paramArray = null;
        try {
            //String sql = "select param,_id from langquerydescparam where _id in ( " + params + " )";
            String sql = String.format("select %s,%s from %s where %s in ( " + params + " )",
                    CacheHfLangQueryDescParamTable.PARAMS, CacheHfLangQueryDescParamTable._ID, CacheHfLangQueryDescParamTable.TABLE_NAME,
                    CacheHfLangQueryDescParamTable._ID);
            NLog.d(TAG, "getShowInfoFromHFDb query langquerydescparam exec sql = %s", sql);
            paramsCursor = db.rawQuery(sql, null);
            int count = 0;
            if (paramsCursor != null
                    && (count = paramsCursor.getCount()) > 0) {
                HashMap<String, String> result = new HashMap<>(count);
                while (paramsCursor.moveToNext()) {
                    int id = paramsCursor.getInt(1);
                    result.put("" + id,
                            paramsCursor.getString(0));
                }

                int i = 0;
                String[] idArr = params.split(",");
                if(idArr != null && idArr.length > 0){
                    paramArray = new int[idArr.length];
                    for(String id : idArr){
                        try{
                            paramArray[i] = Integer.parseInt(id);
                        }catch(Exception e){
                            paramArray[i] = -1;
                        }
                        i++;
                    }
                }

                if(paramArray != null){
                    ArrayList<String> resultFinal = new ArrayList<String>();
                    for ( int id : paramArray ) {
                        resultFinal.add(result.get("" + id));
                    }

                    try {
                        showInfo.mDescription = String.format(
                                showInfo.mDescription,
                                resultFinal.toArray());
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (paramsCursor != null) {
                paramsCursor.close();
            }
        }
    }

    private static String[] getLangNameDescArray(DatabaseProvider db, String langnamedesc,
                                                 String[] langArray) {
        if (langArray == null || langArray.length == 0) {
            return null;
        }
        if (TextUtils.isEmpty(langnamedesc)) {
            return null;
        }
        if (!langnamedesc.contains(":")) {//hard coding here!
            Cursor cursor = null;
            try {
                String args[] = new String[1];
                args[0] = langnamedesc;
                cursor = db.rawQuery("select langnamedesc from langquerycontext where _id = ?;", args);
                if (cursor != null && cursor.moveToFirst()) {
                    langnamedesc = cursor.getString(0);
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (TextUtils.isEmpty(langnamedesc)) {
            return null;
        }

        String[] result = null;
        for (String lang : langArray) {
            if (TextUtils.isEmpty(lang))
                continue;

            int pos = langnamedesc.indexOf(lang);
            if (pos == -1)
                continue;

            int pos2 = langnamedesc.indexOf(':', pos);
            if (pos2 == -1)
                continue;

            result = parseShowInfoContent(langnamedesc, pos2+1, 2);

            if (result != null)
                break;

        }
        return result;
    }

    private static String[] parseShowInfoContent(String str, int start, int targetCount) {
        String[] result = new String[targetCount];
        int len = str.length();
        int cnt = 0;
        char c;
        StringBuilder sb = new StringBuilder(10);
        boolean isStop = false;
        for (int i = start;
             i < len && cnt < targetCount && !isStop;
             ++i) {
            c = str.charAt(i);
            switch (c) {
                case ',':
                case '|':
                    if (sb.length() == 0) {
                        result[cnt++] = "0";
                    } else {
                        result[cnt++] = sb.toString();
                        sb.delete(0, sb.length());
                    }
                    if (c == '|') {
                        isStop = true;
                    }
                    break;
                default:
                    sb.append(c);
            }
        }
        if (!isStop && cnt < targetCount){
            if (sb.length() == 0) {
                result[cnt] = "0";
            } else {
                result[cnt] = sb.toString();
            }
        }
        sb.delete(0, sb.length());
        return result;
    }

    private int queryShowInfoByCacheDb(DatabaseProvider db,
                                       Collection<PkgQueryPathItem> datas) {
        if (datas == null
                || datas.size() == 0) {
            return 0;
        }

        //结果临时集合
        HashMap<String, ShowInfo> idMap = new HashMap<String, ShowInfo>();
        LinkedList<PkgQueryPathItem> infos = new LinkedList<PkgQueryPathItem>();
        HashSet<String> ids = new HashSet<String>();
        for (PkgQueryPathItem item : datas) {
            if (item.mShowInfoResultType == ShowInfoResultType.UNKNOWN
                    || item.mShowInfoResultSource == ResultSourceType.INVAILD
                    || item.mShowInfo == null) {
                infos.add(item);
                ids.add(item.mSignId);// 多个PkgQueryPathItem可能共用一个signId
            }
        }

        boolean isTwLang = false;
        String language = mLanguage;
        String[] langSetting = null;
        if (language.equalsIgnoreCase(KMiscUtils.LANG_EN)) {
            langSetting = new String[1];
            langSetting[0] = KMiscUtils.LANG_EN;
        } else if (language.equalsIgnoreCase(KMiscUtils.LANG_TW)) {
            langSetting = new String[2];
            langSetting[0] = KMiscUtils.LANG_TW;
            langSetting[1] = KMiscUtils.LANG_CN;
            isTwLang = true;
        } else {
            langSetting = new String[2];
            langSetting[0] = mLanguage;
            langSetting[1] = KMiscUtils.LANG_EN;
        }

        String[] args = new String[langSetting.length];
        StringBuilder builder = new StringBuilder(8);
        for (int i = 0; i < langSetting.length; ++i) {
            builder.append('%');
            builder.append(langSetting[i]);
            builder.append('%');
            args[i] = builder.toString();
            builder.delete(0, builder.length());
        }

        int result = 0;
        String pathids = null;
        final int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        int pageNum = 0;
        Cursor cursorLang = null;
        while ((pathids = SqlUtil.collectionToSQLInStringIncreasing(ids,
                pageSize, pageNum++, false)) != null) {

            try {
                if (args.length == 1) {
                    //String sql = "select pathid,lang,name,desc,time from langquery where pathid in " + pathids + " and lang like ?";
                    String sql = String.format("select %s,%s,%s,%s,%s from %s where %s in "+ pathids +" and %s like ? ",
                            CacheLangQueryTable.PATH_ID,CacheLangQueryTable.LANG,CacheLangQueryTable.NAME,
                            CacheLangQueryTable.DESC,CacheLangQueryTable.TIME,CacheLangQueryTable.TABLE_NAME,
                            CacheLangQueryTable.PATH_ID,CacheLangQueryTable.LANG);
                    NLog.d(TAG, "queryShowInfoByCacheDb langquery args.length == 1 exec sql = %s", sql);
                    cursorLang = db.rawQuery(sql, args);
                } else if (args.length == 2){
                    //String sql = "select pathid,lang,name,desc,time from langquery where pathid in \" + pathids + \" and (lang like ? or lang like ?)";
                    String sql = String.format("select %s,%s,%s,%s,%s from %s where %s in "+ pathids +" and (%s like ? or %s like ?) ",
                            CacheLangQueryTable.PATH_ID,CacheLangQueryTable.LANG,CacheLangQueryTable.NAME,
                            CacheLangQueryTable.DESC,CacheLangQueryTable.TIME,CacheLangQueryTable.TABLE_NAME,
                            CacheLangQueryTable.PATH_ID,CacheLangQueryTable.LANG,CacheLangQueryTable.LANG);
                    NLog.d(TAG, "queryShowInfoByCacheDb langquery args.length == 2 exec sql = %s", sql);
                    cursorLang = db.rawQuery(sql, args);
                }

                if (cursorLang == null || cursorLang.getCount() <= 0) {
                    if (cursorLang != null) {
                        cursorLang.close();
                        cursorLang = null;
                    }
                    continue;
                }

                cursorToShowInfoResult(idMap, cursorLang, false, db,
                        langSetting);
            } catch(Exception e){
                e.printStackTrace();
            } finally {
                if (cursorLang != null) {
                    cursorLang.close();
                }
            }
        }
        // 下面是赋值阶段
        for (PkgQueryPathItem item : infos) {
            ShowInfo showInfo = idMap.get(item.mSignId);
            if (showInfo != null) {
                item.mShowInfo = showInfo;
                result++;
            } else {
                continue;
            }
            item.mShowInfoResultType = ShowInfoResultType.SUCCESS;
            item.mShowInfoResultSource = ResultSourceType.CACHE;
        }

        return result;
    }

    private void cursorToShowInfoResult(HashMap<String, ShowInfo> idMap,
                                        Cursor cursor, boolean forceSetLangMatch, DatabaseProvider db,
                                        String[] langArray) {
        long currentTime = System.currentTimeMillis();
        String strLang;
        ArrayList<ShowInfo> langMissmatchFixList = null;
        if (forceSetLangMatch) {
            langMissmatchFixList = new ArrayList<ShowInfo>(idMap.size());
        }
        while (cursor.moveToNext()) {
            String pathid = cursor.getString(0);
            strLang = cursor.getString(1);
            boolean isMatch = false;
            if (strLang.contains(mLanguage)) {
                isMatch = true;
            }
            ShowInfo oldShowInfo = idMap.get(pathid);
            if (oldShowInfo != null && !oldShowInfo.mResultLangMissmatch) {// 已有好结果
                continue;
            }
            if (oldShowInfo != null && oldShowInfo.mResultLangMissmatch
                    && !isMatch) {// 这个是坏结果，以前的也是坏结果，干嘛还要继续更新坏结果
                continue;
            }

            KCacheCloudQuery.ShowInfo showInfo = new ShowInfo();
            if (strLang != null) {
                getShowInfoFromCacheDb(cursor, db, currentTime, showInfo);
            }

            showInfo.mResultLangMissmatch = !isMatch;
            idMap.put(pathid, showInfo);
            // 如果是本地高频库并且语言是tw,那么最后强制设置mResultLangMissmatch为false,以减少联网
            if (langMissmatchFixList != null) {
                langMissmatchFixList.add(showInfo);
            }
        }
        if (langMissmatchFixList != null) {
            for (ShowInfo info : langMissmatchFixList) {
                info.mResultLangMissmatch = false;
            }
        }
    }

    private void getShowInfoFromCacheDb(Cursor cursor, DatabaseProvider db,
                                        long currentTime, KCacheCloudQuery.ShowInfo showInfo) {
        if (!cursor.isNull(2)) {
            showInfo.mName = cursor.getString(2);
        }

        if (!cursor.isNull(3)) {
            showInfo.mDescription = cursor.getString(3);
        }
        if (!cursor.isNull(4)) {
            long tSave = cursor.getLong(4);
            if (tSave > 0) {
                showInfo.mResultExpired = isResultExpired(currentTime, tSave,
                        ShowInfoResultType.SUCCESS);
            }
        }
    }

    private void handleSQLiteDatabaseIllegalStateException(Exception e){
        NLog.w(TAG, "push--Sd" + e.getMessage());
    }


    /**
     * 连表查询时 select pathquery.pathtype,pathquerydir.dir 用实体类属性来替代硬编码
     * @return
     */
    public static String getPathQueryColumn(String columnName){
        return CacheHfPathQueryTable.TABLE_NAME +"."+columnName;
    }

    /**
     * 连表查询时 select pathquery.pathtype,pathquerydir.dir 用实体类属性来替代硬编码
     * @return
     */
    public static String getPathQueryDirColumn(String columnName){
        return CacheHfPathQueryDirTable.TABLE_NAME +"."+columnName;
    }


    public static String getLangQueryDescColumn(String columnName){
        return CacheHfLangQueryDescTable.TABLE_NAME +"."+columnName;
    }

    public static String getLangQueryFormatDescColumn(String columnName){
        return CacheHfLangQueryFormatDescTable.TABLE_NAME +"."+columnName;
    }

    private static final String SQL_QUERY_PATHQUERY_AND_PATHQUERYDIR_PREFIX
            = String.format("select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s from %s inner join %s on %s = %s where %s in ",
                getPathQueryColumn(CacheHfPathQueryTable.PATH_TYPE),getPathQueryDirColumn(CacheHfPathQueryDirTable.DIR),getPathQueryColumn(CacheHfPathQueryTable.CLEAN_TYPE),
                getPathQueryColumn(CacheHfPathQueryTable.CLEAN_TIME),getPathQueryColumn(CacheHfPathQueryTable.CLEANOP),getPathQueryColumn(CacheHfPathQueryTable.CONTENT_TYPE),
                getPathQueryColumn(CacheHfPathQueryTable.PRIVACY_TYPE),getPathQueryColumn(CacheHfPathQueryTable.PATH_ID),getPathQueryColumn(CacheHfPathQueryTable.IS_NEED_CHECK),
                getPathQueryColumn(CacheHfPathQueryTable.CMTYPE),getPathQueryColumn(CacheHfPathQueryTable.TEST)
                ,CacheHfPathQueryTable.TABLE_NAME, CacheHfPathQueryDirTable.TABLE_NAME,
                getPathQueryColumn(CacheHfPathQueryTable.PATH), getPathQueryDirColumn(CacheHfPathQueryDirTable._ID)
                , getPathQueryColumn(CacheHfPathQueryTable.PATH_ID)
                );
    private static final String SQL_QUERY_MD5_DIR_PREFIX
            = String.format("select lower(hex(%s)),%s from %s where %s = ",
            CacheHfPathQueryDirMd5Table.DIR_MD5, CacheHfPathQueryDirMd5Table._ID, CacheHfPathQueryDirMd5Table.TABLE_NAME, CacheHfPathQueryDirMd5Table._ID);
    private static final String SQL_QUERY_MD5_DIR_PREFIX_2
            = String.format("select lower(hex(%s)),%s from %s where %s in ( ",
            CacheHfPathQueryDirMd5Table.DIR_MD5, CacheHfPathQueryDirMd5Table._ID, CacheHfPathQueryDirMd5Table.TABLE_NAME, CacheHfPathQueryDirMd5Table._ID);
    private static final String SQL_QUERY_PKG_PREFIX
            = String.format("select %s,%s,lower(%s),%s from %s where %s in ",
            CacheHfPkgQueryTable.PKG_ID, CacheHfPkgQueryTable.DIRS,CacheHfPkgQueryTable.PKG,
            CacheHfPkgQueryTable.IS_INTEGRITY, CacheHfPkgQueryTable.TABLE_NAME,CacheHfPkgQueryTable.PKG) ;
}

package com.clean.spaceplus.cleansdk.junk.cleancloud.residual;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.bean.ValueType;
import com.clean.spaceplus.cleansdk.base.db.SqlUtil;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_cache.ResidualDirCacheDirQueryTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_cache.ResidualDirCacheLangQueryTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_cache.ResidualDirCacheProvider;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.GlobalsuffixconfigTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.LangqueryalertTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.PkgQueryHfDirQuery2Table;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.PkgQueryHfDirQueryTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.PkgQueryHfLangQueryNameTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.PkgQueryHfPkgQueryTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.PkgQueryHfRePkgQueryTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_hf.ResidualDirHfProvider;
import com.clean.spaceplus.cleansdk.base.db.residual_pkg_cache.ResidualPkgCachePkgQueryTable;
import com.clean.spaceplus.cleansdk.base.db.residual_pkg_cache.ResidualPkgCacheProvider;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.locks.ReentrantLock;

import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.KResidualCloudQuery.DirQueryData;
import space.network.cleancloud.KResidualCloudQuery.DirQueryResult;
import space.network.cleancloud.KResidualCloudQuery.DirResultType;
import space.network.cleancloud.KResidualCloudQuery.PkgQueryData;
import space.network.cleancloud.KResidualCloudQuery.PkgQueryDirItem;
import space.network.cleancloud.KResidualCloudQuery.PkgResultType;
import space.network.cleancloud.KResidualCloudQuery.ResultSourceType;
import space.network.cleancloud.core.residual.KResidualCommonData;
import space.network.cleancloud.core.residual.dir.KDirQueryDataEnDeCode;
import space.network.commondata.KCleanCloudEnv;
import space.network.util.KMiscUtils;
import space.network.util.hash.KQueryMd5Util;
import space.network.util.net.KJsonUtils;

;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/21 15:13
 * @copyright TCL-MIG
 */
public class ResidualLocalQuery {
    public static final String TAG = ResidualLocalQuery.class.getSimpleName();

    /////////////////////////////////////////////////////////////////
    public static final long ONE_DAY_TIMEMILLIS = 24 * 60 * 60 * 1000;//一天的毫秒数
    private static final long TIME_WAIT_FOR_HIGH_FQ_QUERY = 20000;
    private long mCacheLifeTime = 7 * ONE_DAY_TIMEMILLIS;//默认值是7天
    private long mNotFoundCacheLifeTime = 2 * ONE_DAY_TIMEMILLIS;//灰缓存暂定是2天
    private volatile long mCurrentTime;
    private String mDefaultLanguage = "en";
    private String mLanguage = "en";

    //init db 前增加计数，不使用db时减少计数
    //结合最近访问db的时间进行智能释放db
    private AtomicInteger mAccessCount = new AtomicInteger();

    private KResidualProviderUpdate mResidualProviderUpdate;

//    private ReentrantLock mPkgFilterLock = new ReentrantLock();
//    private ReentrantLock mDirFilterLock = new ReentrantLock();
//    private ReentrantLock mRegexFilterLock = new ReentrantLock();


    private TreeMap<DirQueryData, DirQueryData> mDirDataMemCache =
            new TreeMap<>(new DirQueryDataComparator());
    private TreeMap<PkgQueryData, PkgQueryData> mPkgDataMemCache =
            new TreeMap<>(new PkgQueryDataComparator());

    private KPkgRegexQuery mKPkgRegexQuery = new KPkgRegexQuery();
    private ResidualDirHfProvider mResidualDirHfProvider;
    private ResidualDirCacheProvider mResidualDirCacheProvider;
    private ResidualPkgCacheProvider mResidualPkgCacheProvider;


    public ResidualLocalQuery() {
        mCurrentTime = System.currentTimeMillis();
        mResidualDirHfProvider = ResidualDirHfProvider.getInstance();
        mResidualDirCacheProvider = ResidualDirCacheProvider.getInstance();
        mResidualPkgCacheProvider = ResidualPkgCacheProvider.getInstance();

        mResidualProviderUpdate = new KResidualProviderUpdate();
    }



    private static class DirQueryDataComparator implements Comparator<KResidualCloudQuery.DirQueryData> {
        @Override
        public int compare(DirQueryData left, DirQueryData right) {
            int result = 0;
            int tmp = 0;
            if (left.equals(right) ){
                result = 0;
            } else if ((tmp = left.mDirName.compareTo(right.mDirName)) != 0) {
                result = tmp;
            } else if ((tmp = left.mLanguage.compareTo(right.mLanguage)) != 0) {
                result = tmp;
            } else {
                result = 0;
            }
            return result;
        }
    }

    private static class PkgQueryDataComparator implements Comparator<KResidualCloudQuery.PkgQueryData> {
        @Override
        public int compare(PkgQueryData left, PkgQueryData right) {
            int result = 0;
            int tmp = 0;
            if (left.equals(right) ){
                result = 0;
            } else if ((tmp = left.mPkgName.compareTo(right.mPkgName)) != 0) {
                result = tmp;
            } else if ((tmp = left.mLanguage.compareTo(right.mLanguage)) != 0) {
                result = tmp;
            } else {
                result = 0;
            }
            return result;
        }
    }

    private void initDirFalseFilter() {
        /*if (null != mDirFilter) {
            return;
        }
        mDirFilterLock.lock();
        try {
            if (null == mDirFilter) {
                mDirFilter = KFalseFilterFactory
                        .getFalseFilterManagerInstance()
                        .getFalseDataByCategory(CategoryKey.KEY_RESIDUAL_DIR);
            }
            if (null != mDirFilter) {
                mDirFilter.acquireReference();
            }
        } finally {
            mDirFilterLock.unlock();
        }*/
    }

    private void initPkgFalseFilter() {
      /*  if (null != mPkgFilter) {
            return;
        }
        mPkgFilterLock.lock();
        try{
            if (null == mPkgFilter) {
                mPkgFilter = KFalseFilterFactory.getFalseFilterManagerInstance().getFalseDataByCategory(CategoryKey.KEY_RESIDUAL);
            }
            if (null != mPkgFilter) {
                mPkgFilter.acquireReference();
            }
        } finally {
            mPkgFilterLock.unlock();
        }*/
    }

    private void initRegexFalseFilter() {
      /*  if (null != mRegexFilter) {
            return;
        }
        mRegexFilterLock.lock();
        try {
            if (null == mRegexFilter) {
                mRegexFilter =KFalseFilterFactory.getFalseFilterManagerInstance().getFalseDataByCategory(CategoryKey.KEY_RESIDUAL_REGEX);
            }
            if (null != mRegexFilter) {
                mRegexFilter.acquireReference();
            }
        } finally {
            mRegexFilterLock.unlock();
        }*/
    }




    public boolean setLanguage(String language) {
        if (TextUtils.isEmpty(language))
            return false;
        mLanguage = KMiscUtils.toSupportedLanguage(language);

        return true;
    }

    public boolean queryByDir(Collection<DirQueryData> results) {
        return queryByDir(results, true, mLanguage);
    }

  /*  void handleSQLiteDatabaseIllegalStateException(IllegalStateException e,
                                                   MySQLiteDB dbHolder,
                                                   MyDBData dbData,
                                                   boolean isHFDb,
                                                   KCleanCloudGlue cleanCloudGlue) {
        String msg = e.getMessage();
        if (msg.contains("database not open")
                || (msg.contains("re-open") && msg.contains("SQLiteDatabase"))
                || (msg.contains("database") && msg.contains("already closed"))) {

            KCleanCloudCommonError err = new KCleanCloudCommonError();
            err.mytype = KCleanCloudCommonError.MyType_Residual_Sqlite_IllegalStateException;
            if (isHFDb) {
                err.sub_type = KCleanCloudCommonError.SubType_Residual_Sqlite_IllegalStateException_HFDB;
            } else {
                err.sub_type = KCleanCloudCommonError.SubType_Residual_Sqlite_IllegalStateException_CACHEDB;
            }
            err.setExceptionMsgToDetailMsg(e);
            err.reportToServer(cleanCloudGlue);

            if (dbData != null) {
                dbHolder.releaseReference(dbData);
                dbHolder.unInitDb();
            }
        } else {
            throw e;
        }
    }*/

    public boolean queryByDir(Collection<DirQueryData> results, boolean getShowInfo, String language) {
        if (null == results || results.isEmpty())
            return false;

        for (DirQueryData result : results) {
            result.mErrorCode = 0;
            result.mResultSource = ResultSourceType.CACHE;
            result.mResult.mQueryResult = KResidualCloudQuery.DirResultType.UNKNOWN;
            queryByDirInMemCache(result);
        }

        mAccessCount.incrementAndGet();

        initDirFalseFilter();

        setCurrentTime(System.currentTimeMillis());

        //从pkgquery_hf_en_1.0.0.db查询数据
        try {
//            long highFreqStartTime = System.currentTimeMillis();
            queryDirByHighFreqDb(mResidualDirHfProvider.getDatabase(),results, getShowInfo, language);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        }

        //从residual_dir_cache库读取数据
        try {
//            long cacheStartTime = System.currentTimeMillis();
            queryDirByCacheDb(mResidualDirCacheProvider.getDatabase(),results, getShowInfo, language);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        }
        mAccessCount.decrementAndGet();
        return true;
    }

    public DirQueryData[] localQueryDirAndSubDirInfo(String dirnameEncodeString, boolean isGetShowInfo, String language) {
        if (TextUtils.isEmpty(dirnameEncodeString))
            return null;

        if (TextUtils.isEmpty(language)) {
            language = mLanguage;
        }

        DirQueryData[] result = null;
        ArrayList<DirQueryData> resultArray = null;
        mAccessCount.incrementAndGet();

        setCurrentTime(System.currentTimeMillis());
        /*MyDBData highFrequentDb = mHighFreqDbHolder.getDatabaseAndAcquireReference();

        if (highFrequentDb != null) {
            resultArray = localQueryDirAndSubDirInfoByHighFreqDb(
                    highFrequentDb.mDb,
                    dirnameEncodeString,
                    isGetShowInfo,
                    language);
            mHighFreqDbHolder.releaseReference(highFrequentDb);
        }

        if (resultArray == null) {
            MyDBData dirCacheDb = mDirCacheDbHolder.getDatabaseAndAcquireReference();
            if (dirCacheDb != null) {
                resultArray = localQueryDirAndSubDirInfoByCacheDb(
                        dirCacheDb.mDb,
                        dirnameEncodeString,
                        isGetShowInfo,
                        language);
                mDirCacheDbHolder.releaseReference(dirCacheDb);
            }
        }*/

        resultArray = localQueryDirAndSubDirInfoByHighFreqDb(mResidualDirHfProvider.getDatabase(),dirnameEncodeString,
                isGetShowInfo,language);
        if (resultArray == null){
            localQueryDirAndSubDirInfoByCacheDb(mResidualDirCacheProvider.getDatabase(), dirnameEncodeString,
                    isGetShowInfo,language);
        }

        mAccessCount.decrementAndGet();
        if (resultArray != null && !resultArray.isEmpty()) {
            result = new DirQueryData[resultArray.size()];
            resultArray.toArray(result);
        }
        return result;
    }

    private ArrayList<DirQueryData> localQueryDirAndSubDirInfoByHighFreqDb(
            SQLiteDatabase db,
            String dirnameEncodeString,
            boolean isGetShowInfo,
            String language) {

        ArrayList<DirQueryData> result = null;
        String key = KQueryMd5Util.getHexPathString(dirnameEncodeString).toUpperCase(Locale.ENGLISH);
        //String sqlHead = "select dirid,queryresult,cleantype,dirs,pkgs,repkgs,dir,contenttype,cmtype,test from dirquery where hex(dir) like ";

        String[] selection = new String[]{
                PkgQueryHfDirQueryTable.DIRID, PkgQueryHfDirQueryTable.QUERYRESULT, PkgQueryHfDirQueryTable.CLEANTYPE,
                PkgQueryHfDirQueryTable.DIRS, PkgQueryHfDirQueryTable.PKGS, PkgQueryHfDirQueryTable.REPKGS,
                PkgQueryHfDirQueryTable.DIR, PkgQueryHfDirQueryTable.CONTENTTYPE, PkgQueryHfDirQueryTable.CMTYPE,
                PkgQueryHfDirQueryTable.TEST
        };
        String sqlHead = SqlUtil.appendSqlString(PkgQueryHfDirQueryTable.TABLE_NAME,selection) +" where hex(" +PkgQueryHfDirQueryTable.DIR +") like ";
        String sql = sqlHead + "'" + key + "%'";
        NLog.d(TAG, "localQueryDirAndSubDirInfoByHighFreqDb exec sql = %s", sql);
        Cursor cursorSign = null;
        try {
            cursorSign = db.rawQuery(sql, null);
            if (cursorSign != null && cursorSign.getCount() > 0) {
                while (cursorSign.moveToNext()) {
                    String strKey = KQueryMd5Util.getPathStringFromBytes(cursorSign.getBlob(6));
                    int queryResult = cursorSign.getInt(1);
                    if (queryResult != DirResultType.NOT_FOUND
                            && queryResult != DirResultType.UNKNOWN
                            && queryResult != DirResultType.SIGN_IGNORE) {
                        DirQueryData data = new DirQueryData();
                        data.mResult 	= new DirQueryResult();
                        data.mDirName 	= strKey;
                        data.mLanguage 	= language;
                        data.mResultSource = ResultSourceType.HFREQ;
                        data.mResult.mSignId      = cursorSign.getInt(0);;
                        data.mResult.mQueryResult = queryResult;
                        data.mResult.mCleanType   = cursorSign.getInt(2);
                        data.mResultExpired       = false;
                        data.mResult.mContentType = cursorSign.getInt(7);
                        data.mResult.mCleanMediaFlag= cursorSign.getInt(8);
                        data.mResult.mTestFlag = cursorSign.getInt(9);
                        if (result == null) {
                            result = new ArrayList<>(cursorSign.getCount());
                        }
                        result.add(data);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursorSign != null) {
                cursorSign.close();
                cursorSign = null;
            }
        }
        if (isGetShowInfo && result != null) {
            getShowInfoFromHighFreqDb(db, result, language);
        }
        return result;
    }

    private ArrayList<DirQueryData> localQueryDirAndSubDirInfoByCacheDb(
            SQLiteDatabase db,
            String dirnameEncodeString,
            boolean isGetShowInfo,
            String language) {

        ArrayList<DirQueryData> result = null;
        String key = dirnameEncodeString;
       // String sqlHead = "select dirid,queryresult,cleantype,time,dirs,pkgs,repkgs,dir,contenttype,cmtype,test from dirquery where dir like ";
        String[] selection = new String[]{
                ResidualDirCacheDirQueryTable.DIRID, ResidualDirCacheDirQueryTable.QUERYRESULT, ResidualDirCacheDirQueryTable.CLEANTYPE,
                ResidualDirCacheDirQueryTable.TIME, ResidualDirCacheDirQueryTable.DIRS, ResidualDirCacheDirQueryTable.PKGS,
                ResidualDirCacheDirQueryTable.REPKGS, ResidualDirCacheDirQueryTable.DIR, ResidualDirCacheDirQueryTable.CONTENTTYPE,
                ResidualDirCacheDirQueryTable.CMTYPE, ResidualDirCacheDirQueryTable.TEST
        };
        String sqlHead = SqlUtil.appendSqlString(ResidualDirCacheDirQueryTable.TABLE_NAME,selection) +" where "+ ResidualDirCacheDirQueryTable.DIR  +" like ";
        String sql = sqlHead + "'" + key + "%'";
        NLog.d(TAG, "localQueryDirAndSubDirInfoByCacheDb sql = %s", sql);
        Cursor cursorSign = null;
        try {
            cursorSign = db.rawQuery(sql, null);
            if (cursorSign != null && cursorSign.getCount() > 0) {
                while (cursorSign.moveToNext()) {
                    String strKey = cursorSign.getString(7);
                    int queryResult = cursorSign.getInt(1);
                    if (queryResult != DirResultType.NOT_FOUND
                            && queryResult != DirResultType.UNKNOWN
                            && queryResult != DirResultType.SIGN_IGNORE) {
                        DirQueryData data = new DirQueryData();
                        data.mResult 	= new KResidualCloudQuery.DirQueryResult();
                        data.mDirName 	= strKey;
                        data.mLanguage 	= language;
                        data.mResultSource = ResultSourceType.CACHE;
                        data.mResult.mSignId      = cursorSign.getInt(0);
                        data.mResult.mQueryResult = queryResult;
                        data.mResult.mCleanType   = cursorSign.getInt(2);
                        long time        = cursorSign.getLong(3);
                        //由于目前服务器没有数据  所以先强制本地数据为不过期
                        //data.mResultExpired       = isResultExpired(getCurrentTime(), time, queryResult);
                        data.mResultExpired       = false;
                        data.mResult.mContentType = cursorSign.getInt(8);
                        data.mResult.mCleanMediaFlag= cursorSign.getInt(9);
                        data.mResult.mTestFlag = cursorSign.getInt(10);
                        if (isGetShowInfo) {
                            data.mResult.mShowInfo = getShowInfoFromCacheDb(db, data.mResult.mSignId, language);
                        }
                        if (result == null) {
                            result = new ArrayList<DirQueryData>(cursorSign.getCount());
                        }
                        result.add(data);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursorSign != null) {
                cursorSign.close();
                cursorSign = null;
            }
        }
        return result;
    }

    public boolean queryByPkg(Collection<PkgQueryData> results) {
        if (null == results || results.isEmpty())
            return false;

        for (PkgQueryData result : results) {
            result.mErrorCode = 0;
            result.mResultSource = ResultSourceType.CACHE;
            result.mResult.mQueryResult = PkgResultType.UNKNOWN;
            queryByPkgInMemCache(result);
        }
        initPkgFalseFilter();
        initRegexFalseFilter();
        //initDb();
     /*   MyDBData pkgCacheDb = null;
        MyDBData highFrequentDb = null;
        mAccessCount.incrementAndGet();

        pkgCacheDb = mPkgCacheDbHolder.getDatabaseAndAcquireReference();
        highFrequentDb = mHighFreqDbHolder.getDatabaseAndAcquireReference();

        if (null == pkgCacheDb && null == highFrequentDb) {
            mAccessCount.decrementAndGet();
            return false;
        }*/
        setCurrentTime(System.currentTimeMillis());
        /*if (highFrequentDb != null) {
            queryPkgByHighFreqDb(highFrequentDb.mDb, results);
        }

        if (pkgCacheDb != null) {
            queryPkgByCacheDb(pkgCacheDb.mDb, results);
        }
        mPkgCacheDbHolder.releaseReference(pkgCacheDb);
        mHighFreqDbHolder.releaseReference(highFrequentDb);*/
        long startTime = System.currentTimeMillis();
        if (mResidualDirHfProvider != null){
            queryPkgByHighFreqDb(mResidualDirHfProvider.getDatabase(), results);
        }
        NLog.d(TAG, "queryPkgByHighFreqDb cost time = %d", (System.currentTimeMillis() - startTime));
        startTime = System.currentTimeMillis();
        if (mResidualDirCacheProvider != null){
            queryPkgByCacheDb(mResidualPkgCacheProvider.getDatabase(), results);
        }
        NLog.d(TAG, "queryPkgByCacheDb cost time = %d", (System.currentTimeMillis() - startTime));
        mAccessCount.decrementAndGet();
        return true;
    }

    public void unInitDb() {

      /*  mDirCacheDbHolder.unInitDb();
        mPkgCacheDbHolder.unInitDb();
        mHighFreqDbHolder.unInitDb();
        clearMemCache();
        mPkgFilterLock.lock();
        try {
            if (mPkgFilter != null) {
                mPkgFilter.releaseReference();
                mPkgFilter = null;
            }
        } finally {
            mPkgFilterLock.unlock();
        }
        mDirFilterLock.lock();
        try {
            if (mDirFilter != null) {
                mDirFilter.releaseReference();
                mDirFilter = null;
            }
        } finally {
            mDirFilterLock.unlock();
        }
        mRegexFilterLock.lock();
        try {
            if (mRegexFilter != null) {
                mRegexFilter.releaseReference();
                mRegexFilter = null;
            }
        } finally {
            mRegexFilterLock.unlock();
        }*/
    }

    public boolean tryUnInitDb() {
        boolean result = false;
        int accessCount = mAccessCount.get();
        if (accessCount > 0) {
            return result;
        }

        unInitDb();
        result = true;
        return result;
    }

    public boolean updateDirCache(final Collection<DirQueryData> results) {
        NLog.d(TAG, "ResidualLocalQuery updateDirCache = "+results);
        if (null == results || results.isEmpty()){
            NLog.d(TAG, "ResidualLocalQuery 需要更新数据库Dir表 size 为 0");
            return false;
        }
        NLog.d(TAG, "ResidualLocalQuery 需要更新数据库Dir表 size = %d",results.size());
        putDirDataToMemCache(results);
        mResidualProviderUpdate.updateDirCache(results);
        return true;
    }




    public boolean updatePkgCache(final Collection<PkgQueryData> results) {
        NLog.d(TAG, "ResidualLocalQuery updatePkgCache = "+results);
        if (null == results || results.isEmpty()){
            NLog.d(TAG, "ResidualLocalQuery 需要更新数据库Pkg表 size 为 0");
            return false;
        }
        NLog.d(TAG, "ResidualLocalQuery 需要更新数据库Pkg表 size = %d",results.size());
        putPkgDataToMemCache(results);
        mResidualProviderUpdate.updatePkgCache(results);

        return true;
    }

    public void setCacheLifeTime(int days) {
        if (days != 0) {
            mCacheLifeTime = ONE_DAY_TIMEMILLIS * days;
        }
    }

    public String getLanguage(){
        return mLanguage;
    }
    /**
     * @return
     */
    public String getDefaultLanguage() {
        return mDefaultLanguage;
    }

    private long getCurrentTime() {
        return mCurrentTime;
    }

    private void setCurrentTime(long time) {
        mCurrentTime = time;
    }

    private void clearMemCache() {
        clearDirDataMemCache();
        clearPkgDataMemCache();

    }
    private boolean queryByDirInMemCache(DirQueryData data) {
        boolean result = false;
        DirQueryData cache = getDirDataFromMemCache(data);
        if (cache != null) {
            copyDirQueryDataFromMemCache(cache, data);
            result = true;
        }else {
        }
        return result;
    }

    private void copyDirQueryDataFromMemCache(DirQueryData src, DirQueryData dst) {
        dst.mErrorCode = src.mErrorCode;
        dst.mResult    = src.mResult;
        dst.mResultSource = ResultSourceType.CACHE;
        dst.mResultExpired = src.mResultExpired;
    }

    private void putDirDataToMemCache(Collection<DirQueryData> datas) {
        synchronized(mDirDataMemCache) {
            for (DirQueryData data : datas) {
                NLog.d(TAG , "放置数据到内存缓存中");
                mDirDataMemCache.put(data, data);
            }
        }

        Iterator<Map.Entry<DirQueryData, DirQueryData>> entries = mDirDataMemCache.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry<DirQueryData, DirQueryData> entry = entries.next();

            System.out.println("Value = " + entry.getValue());

        }
    }
	/*
	private void putDirDataToMemCache(DirQueryData data) {
		synchronized(mMemDirDataCache) {
			mMemDirDataCache.put(data, data);
		}
	}
	*/

    private DirQueryData getDirDataFromMemCache(DirQueryData data) {
        DirQueryData result = null;
        synchronized(mDirDataMemCache) {
            result = mDirDataMemCache.get(data);
        }
        return result;
    }

    private void clearDirDataMemCache() {
        synchronized(mDirDataMemCache) {
            mDirDataMemCache.clear();
        }
    }

    private boolean queryByPkgInMemCache(PkgQueryData data) {
        boolean result = false;
        PkgQueryData cache = getPkgDataFromMemCache(data);
        if (cache != null) {
            copyPkgQueryDataFromMemCache(cache, data);
            result = true;
        }
        return result;
    }

    private void copyPkgQueryDataFromMemCache(PkgQueryData src, PkgQueryData dst) {
        dst.mErrorCode = src.mErrorCode;
        dst.mResult    = src.mResult;
        dst.mResultSource = ResultSourceType.CACHE;
        dst.mResultExpired = src.mResultExpired;
    }

    private void putPkgDataToMemCache(Collection<PkgQueryData> datas) {
        synchronized(mPkgDataMemCache) {
            for (PkgQueryData data : datas) {
                mPkgDataMemCache.put(data, data);
            }
        }
    }
	/*
	private void putPkgDataToMemCache(PkgQueryData data) {
		synchronized(mMemPkgDataCache) {
			mMemPkgDataCache.put(data, data);
		}
	}
	*/

    private PkgQueryData getPkgDataFromMemCache(PkgQueryData data) {
        PkgQueryData result = null;
        synchronized(mPkgDataMemCache) {
            result = mPkgDataMemCache.get(data);
        }
        return result;
    }

    private void clearPkgDataMemCache() {
        synchronized(mPkgDataMemCache) {
            mPkgDataMemCache.clear();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////
    private int queryPkgByCacheDb(SQLiteDatabase db, Collection<PkgQueryData> results) {
        int ret = 0;
        ret = queryPkgByPkgMd5FromCacheDB(db, results, ResultSourceType.CACHE);
        return ret;
    }

    private int queryPkgByHighFreqDb(SQLiteDatabase db, Collection<PkgQueryData> results) {
        int ret = 0;
        ret = queryPkgByPkgMd5InHighFreqDb(db, results);
        //高频库才有正则表达式包名
        //不管包名精准匹配是否成功都进行正则式匹配,但是不修改result.mResult.mQueryResult的状态
        //避免由于正则匹配的结果影响是否需要网络查询
        for (PkgQueryData result : results) {
            queryPkgByRegex(db, result);
        }

        return ret;
    }

    private static class QueryPkgMiddleResult {
        PkgQueryData 	result;
        int 			pkgid;
        String			dirs;
        long			time;
    }

    private int queryPkgByPkgMd5InHighFreqDb(SQLiteDatabase db, Collection<PkgQueryData> results) {
        int sizeResult = 0;
        HashMap<Long, PkgQueryData> keyPkgNameMap = new HashMap<Long, PkgQueryData>(
                results.size());
        for (PkgQueryData result : results) {
            if (result.mResult.mQueryResult == PkgResultType.NOT_FOUND
                    || result.mResult.mQueryResult == PkgResultType.UNKNOWN) {
                long queryKey = ((KResidualCommonData.PkgQueryInnerData)result.mInnerData).mPkgNameMd5High64Bit;
                keyPkgNameMap.put(queryKey, result);
            }
        }

        Set<Long> keys = keyPkgNameMap.keySet();
        long[] md5High64Bit = new long[keys.size()];
        Iterator<Long> it =  keys.iterator();
        int i = 0;
        while(it.hasNext()){
            md5High64Bit[i] = it.next().longValue();
            i++;
        }

        LinkedList<QueryPkgMiddleResult> middleResults = new LinkedList<QueryPkgMiddleResult>();
        int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        int pageNum = 0;
        //String sql = "select pkgid,dirs,lower(pkg) from pkgquery where pkg in ";

        String sql = String.format("select %s,%s,lower(%s) from %s where %s in ",
                PkgQueryHfPkgQueryTable.PKG_ID,PkgQueryHfPkgQueryTable.DIRS,PkgQueryHfPkgQueryTable.PKG,
                PkgQueryHfPkgQueryTable.TABLE_NAME,PkgQueryHfPkgQueryTable.PKG);

        String r;
        Cursor cursorSign = null;
        while ((r = SqlUtil.arrayLongToSQLInStringIncreasing(
                md5High64Bit, pageSize, pageNum++)) != null) {
            try {

                //r += " AND dirs <> '^'";//加^说明这个数据是不完整的,只用于目录查询,不用于包查询,包查询的时候就无视这个数据
                r += String.format(" AND %s <> '^'", PkgQueryHfPkgQueryTable.DIRS);
                NLog.d(TAG, "queryPkgByPkgMd5InHighFreqDb exec sql = %s", sql + r);

                cursorSign = db.rawQuery(sql + r, null);
                if (cursorSign == null || cursorSign.getCount() == 0) {
                    continue;
                }

                while (cursorSign.moveToNext()) {
                    long strKey = cursorSign.getLong(2);
                    PkgQueryData result = keyPkgNameMap.get(strKey);
                    if (result != null) {
                        int pkgid     = cursorSign.getInt(0);
                        String strDirs= null;
                        long time     = 0;
                        if (!cursorSign.isNull(1)) {
                            strDirs = cursorSign.getString(1);
                        }

                        QueryPkgMiddleResult middleResult = new QueryPkgMiddleResult();
                        middleResult.result = result;
                        middleResult.pkgid  = pkgid;
                        middleResult.dirs	= strDirs;
                        middleResult.time	= time;
                        middleResults.add(middleResult);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursorSign != null) {
                    cursorSign.close();
                    cursorSign = null;
                }
            }
        }

        for(QueryPkgMiddleResult middleResult : middleResults) {
            if (fillPkgQueryDataByHighFreqDb(db, middleResult)) {
                ++sizeResult;
            }
        }
        return sizeResult;
    }

    private int queryPkgByPkgMd5FromCacheDB(SQLiteDatabase db, Collection<PkgQueryData> results, int resultSource) {
        int sizeResult = 0;
        HashMap<String, PkgQueryData> keyPkgNameMap = new HashMap<>(
                results.size());
        for (PkgQueryData result : results) {
            if (result.mResult.mQueryResult == PkgResultType.NOT_FOUND
                    || result.mResult.mQueryResult == PkgResultType.UNKNOWN) {
                String queryKey = ((KResidualCommonData.PkgQueryInnerData)result.mInnerData).mPkgNameMd5;
                keyPkgNameMap.put(queryKey, result);
            }
        }

        LinkedList<QueryPkgMiddleResult> middleResults = new LinkedList<>();
        int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        int pageNum = 0;
        //String sql = "select pkgid,time,dirs,pkg from pkgquery where pkg in ";

        String[] selection = new String[]{ResidualPkgCachePkgQueryTable.PKG_ID,ResidualPkgCachePkgQueryTable.TIME,
                ResidualPkgCachePkgQueryTable.DIRS, ResidualPkgCachePkgQueryTable.PKG};
        String sql =SqlUtil.appendSqlString(ResidualPkgCachePkgQueryTable.TABLE_NAME,selection) +" where " + ResidualPkgCachePkgQueryTable.PKG +" in ";


        String r;
        Cursor cursorSign = null;
        Set<String> pkgids = keyPkgNameMap.keySet();
        while ((r = SqlUtil.collectionToSQLInStringIncreasing(
                pkgids, pageSize, pageNum++)) != null) {
            try {
                NLog.d(TAG, "queryPkgByPkgMd5FromCacheDB exec sql = %s", sql + r);
                cursorSign = db.rawQuery(sql + r, null);
                if (cursorSign == null || cursorSign.getCount() == 0) {
                    continue;
                }

                while (cursorSign.moveToNext()) {
                    String strKey = cursorSign.getString(3);
                    PkgQueryData result = keyPkgNameMap.get(strKey);
                    if (result != null) {
                        int pkgid     = cursorSign.getInt(0);
                        String strDirs= null;
                        long time     = cursorSign.getLong(1);
                        if (!cursorSign.isNull(2)) {
                            strDirs = cursorSign.getString(2);
                        }
                        QueryPkgMiddleResult middleResult = new QueryPkgMiddleResult();
                        middleResult.result = result;
                        middleResult.pkgid  = pkgid;
                        middleResult.dirs	= strDirs;
                        middleResult.time	= time;
                        middleResults.add(middleResult);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursorSign != null) {
                    cursorSign.close();
                    cursorSign = null;
                }
            }
        }
        for(QueryPkgMiddleResult middleResult : middleResults) {
            if (fillPkgQueryDataFromCacheDB(db, middleResult, resultSource)) {
                ++sizeResult;
            }
        }
        return sizeResult;
    }


    private boolean fillPkgQueryDataByHighFreqDb(SQLiteDatabase db, QueryPkgMiddleResult middleResult) {
        boolean ret = false;
        long time = 0;
        int  pkgid = 0;
        int  queryResult = PkgResultType.UNKNOWN;
        boolean integrityOk = false;
        PkgQueryData result = middleResult.result;
        ArrayList<String> oridirs   = null;
        Collection<String> dirs      = null;
        integrityOk = true;
        pkgid       = middleResult.pkgid;
        time        = middleResult.time;
      /*  IFalseSignFilter filter = this.mPkgFilter;
        if ((filter != null && filter.filter(pkgid))) {
            return false;
        }*/
        if (!TextUtils.isEmpty(middleResult.dirs)) {
            queryResult = PkgResultType.DIR_LIST;
            try {
                oridirs = KJsonUtils.getStringArrayFromArrayString(middleResult.dirs);
            } catch (Exception e) {
                integrityOk = false;
                e.printStackTrace();
            }
        } else {
            queryResult = PkgResultType.NOT_FOUND;
        }

        if (queryResult == PkgResultType.DIR_LIST) {
            if (oridirs != null && !oridirs.isEmpty()) {
                dirs = getDirArrayFromDirString(db, oridirs);
            }
            if (null == dirs || dirs.isEmpty()) {
                integrityOk = false;
            }
        } else {
            dirs = oridirs;
        }

        if (integrityOk) {
            result.mResultSource        = ResultSourceType.HFREQ;
            result.mResult.mSignId      = pkgid;
            result.mResult.mQueryResult = queryResult;
            result.mResultExpired       = isResultExpired(getCurrentTime(), time, queryResult);
            if(dirs != null){
                if (result.mResult.mPkgQueryDirItems == null) {
                    result.mResult.mPkgQueryDirItems = new ArrayList<>(dirs.size());
                }
                for (String dir : dirs) {
                    PkgQueryDirItem item = new PkgQueryDirItem();
                    item.mDirString = dir;
                    result.mResult.mPkgQueryDirItems.add(item);
                }
            }
            ret = true;
        }
        return ret;
    }

    private boolean fillPkgQueryDataFromCacheDB(SQLiteDatabase db, QueryPkgMiddleResult middleResult, int resultSource) {
        boolean ret = false;
        long time = 0;
        int  pkgid = 0;
        int  queryResult = PkgResultType.UNKNOWN;
        boolean integrityOk = false;
        PkgQueryData result = middleResult.result;
        Collection<String> oridirs   = null;
        Collection<String> dirs      = null;
        integrityOk = true;
        pkgid       = middleResult.pkgid;
        time        = middleResult.time;
       /* IFalseSignFilter filter = this.mPkgFilter;
        if ((filter != null && filter.filter(pkgid))) {
            return false;
        }*/
        if (!TextUtils.isEmpty(middleResult.dirs)) {
            queryResult = KResidualCloudQuery.PkgResultType.DIR_LIST;
            try {
                oridirs = KJsonUtils.getStringArrayFromJsonArrayString(middleResult.dirs);
            } catch (Exception e) {
                integrityOk = false;
                e.printStackTrace();
            }
        } else {
            queryResult = PkgResultType.NOT_FOUND;
        }

        if (queryResult == PkgResultType.DIR_LIST) {
            if (oridirs != null && !oridirs.isEmpty()) {
                if (isNumberList(oridirs)) {
                    dirs = getDirListFromDirIds(db, oridirs, false);
                } else {
                    dirs = oridirs;
                }
            }
            if (null == dirs || dirs.isEmpty()) {
                integrityOk = false;
            }
        } else {
            dirs = oridirs;
        }

        if (integrityOk) {
            result.mResultSource        = resultSource;
            result.mResult.mSignId      = pkgid;
            result.mResult.mQueryResult = queryResult;
            result.mResultExpired       = isResultExpired(getCurrentTime(), time, queryResult);
            if(dirs != null){
                if (result.mResult.mPkgQueryDirItems == null) {
                    result.mResult.mPkgQueryDirItems = new ArrayList<>(dirs.size());
                }
                for (String dir : dirs) {
                    PkgQueryDirItem item = new PkgQueryDirItem();
                    item.mDirString = dir;
                    result.mResult.mPkgQueryDirItems.add(item);
                }
            }
            ret = true;
        }
        return ret;
    }

    private boolean queryPkgByRegex(SQLiteDatabase db, PkgQueryData result) {
        if (null == db || null == result)
            return false;

        initPkgRegexQuery(db);

        LinkedList<KPkgRegexQuery.PkgRegxData> queryResults = mKPkgRegexQuery.query(result.mPkgName);
        if (null == queryResults)
            return true;

        result.mErrorCode = 0;
        result.mResultMatchRegex = true;
        result.mResultSource = ResultSourceType.HFREQ;
        //为了可以让包的非正则匹配(按包名精准匹配)不命中的情况下依旧可以进行网络查询
        //先不设置状态为PkgResultType.DIR_LIST,最后进行修正
        //if (result.mResult.mQueryResult != PkgResultType.DIR_LIST) {
        //	result.mResult.mQueryResult = PkgResultType.DIR_LIST;
        //}
        if (result.mResult.mPkgQueryDirItems == null) {
            result.mResult.mPkgQueryDirItems = new ArrayList<>();
        }
        //IFalseSignFilter filter = this.mRegexFilter;
        for (KPkgRegexQuery.PkgRegxData queryResult : queryResults) {
            /*if (filter!= null && filter.filter(queryResult.mPkgId)) {
                continue;
            }*/
            for (String dir : queryResult.mDirs) {
                KResidualCloudQuery.PkgQueryDirItem item = new PkgQueryDirItem();
                item.mDirString = dir;
                item.mRegexSignId = queryResult.mPkgId;
                result.mResult.mPkgQueryDirItems.add(item);
            }
        }
        return true;
    }

    ArrayList<String> getDirArrayFromDirString(SQLiteDatabase db, ArrayList<String> oridirs) {
        ArrayList<String> result = null;
        ArrayList<String> dirs1  = null;
        ArrayList<String> dirs2  = null;
        ArrayList<String> dir2Arr= new ArrayList<String>();
        ArrayList<String> dir1Arr= new ArrayList<String>();
        for(String dir : oridirs){
            if(TextUtils.isEmpty(dir)){
                continue;
            }
            if(dir.charAt(0) == '#'){
                String fixdir = dir.substring(1);
                if (!TextUtils.isEmpty(fixdir)) {
                    dir2Arr.add(fixdir);
                }
            }else{
                dir1Arr.add(dir);
            }
        }

        if (dir1Arr.size() > 0) {
            dirs1 = getDirListFromDirIds(db, dir1Arr, true);
        }

        if (dir2Arr.size() > 0) {
            dirs2 = getDirListFromDirIds2(db, dir2Arr);
        }
        if (dirs1 != null && !dirs1.isEmpty()) {
            result = dirs1;
        }
        if (dirs2 != null && !dirs2.isEmpty()) {
            if (result != null) {
                result.addAll(dirs2);
            } else {
                result = dirs2;
            }
        }
        return result;
    }

    static class RegexQueryDataMiddleResult {
        KPkgRegexQuery.PkgRegxData data;
        ArrayList<String> oridirs;
    }
    private void initPkgRegexQuery(SQLiteDatabase db) {
        if (mKPkgRegexQuery.isInitialized())
            return;

        synchronized(mKPkgRegexQuery) {
            if (mKPkgRegexQuery.isInitialized())
                return;

            if (!db.isOpen()) {
                return;
            }

            Cursor cursorSign = null;
            LinkedList<RegexQueryDataMiddleResult> middleResults = new LinkedList<RegexQueryDataMiddleResult>();
            boolean integrityOk = false;

            try {
                //String sql = "select pkgid,pkg,dirs from repkgquery";
                String[] selection = new String[]{PkgQueryHfRePkgQueryTable.PKG_ID, PkgQueryHfRePkgQueryTable.PKG, PkgQueryHfRePkgQueryTable.DIRS};
                String sql = SqlUtil.appendSqlString(PkgQueryHfRePkgQueryTable.TABLE_NAME,selection);
                NLog.d(TAG, "initPkgRegexQuery sql = %s", sql);
                cursorSign = db.rawQuery(sql, null);
                if (cursorSign != null) {
                    int cnt = cursorSign.getCount();
                    if (cnt > 0) {
                        while (cursorSign.moveToNext()) {
                            KPkgRegexQuery.PkgRegxData data = new KPkgRegexQuery.PkgRegxData();
                            data.mPkgId    = cursorSign.getInt(0);
                            data.mPkgRegex = cursorSign.getString(1);
                            String strDirs = cursorSign.getString(2);
                            ArrayList<String> oridirs = KJsonUtils.getStringArrayFromArrayString(strDirs);
                            RegexQueryDataMiddleResult middleResult = new RegexQueryDataMiddleResult();
                            middleResult.data = data;
                            middleResult.oridirs = oridirs;
                            middleResults.add(middleResult);
                        }
                    }
                    integrityOk = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursorSign != null) {
                    cursorSign.close();
                }
            }

            if (integrityOk) {
                ArrayList<KPkgRegexQuery.PkgRegxData> regxDatas = new ArrayList<KPkgRegexQuery.PkgRegxData>();
                regxDatas.ensureCapacity(middleResults.size());
                for (RegexQueryDataMiddleResult middleResult : middleResults) {
                    if (middleResult.oridirs != null && !middleResult.oridirs.isEmpty()) {
                        ArrayList<String> dirs = getDirArrayFromDirString(db, middleResult.oridirs);

                        if (dirs != null) {
                            middleResult.data.mDirs = new String[dirs.size()];
                            dirs.toArray(middleResult.data.mDirs);
                            regxDatas.add(middleResult.data);
                        }
                    }
                }
                middleResults.clear();
                mKPkgRegexQuery.initialize(regxDatas);
            }
        }
    }

    private boolean isResultExpired(long currentTime, long resultSaveTime, int queryResult) {
        //特殊情况处理,正常情况只有高频库才有保存时间为0，并且高频库不放置NOT_FOUND结果
        //防止高频库数据异常，如果出现这种异常情况就当过期
        if (0 == resultSaveTime && DirResultType.NOT_FOUND == queryResult)
            return true;

        //currentTime不应该为0，异常情况，先当成不过期
        if (0 == currentTime)
            return false;

        //下发的本地高频库时间是0,就认为是永久生效的
        if (0 == resultSaveTime)
            return false;

        boolean result = false;
        if (currentTime > resultSaveTime) {
            long diff = (currentTime - resultSaveTime);
            if (DirResultType.NOT_FOUND == queryResult) {
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
            //系统的时间发生错误了,当成过期了
            result = true;
        }
        return result;
    }

    private boolean isNumberList(Collection<String> list) {
        //在dirs里面可以能是id列表或者md5字符串列表
        //在pkgs里面可以是id列表或者md5字符串列表
        //在repkgs里面可以是id列表或者正则式列表
        boolean result = false;
        if (list != null && !list.isEmpty()) {
            String testvalue = list.iterator().next();
            if (testvalue.length() < 32) {
                if (testvalue.contains(".")) {
                    result = false;//可能是正则式列表
                } else {
                    result = true;
                }
            } else {
                //用md5转换后的字符串列表
                result = false;
            }
        }
        return result;
    }

    private int queryDirByHighFreqDb(final SQLiteDatabase db, Collection<DirQueryData> results,  final boolean isGetShowInfo, String language) {
        final HashMap<String, DirQueryData> keyPkgNameMap = new HashMap<>(
                results.size());

        for (DirQueryData result : results) {
            if (result.mResult.mQueryResult == DirResultType.NOT_FOUND
                    || result.mResult.mQueryResult == DirResultType.UNKNOWN
                    || (result.mResult.mShowInfo != null && result.mResult.mShowInfo.mResultLangMissmatch)) {

                String queryKey = ((KResidualCommonData.DirQueryInnerData) result.mInnerData).mLocalQueryKey;
                keyPkgNameMap.put(queryKey, result);
                NLog.d(TAG, "queryDirByHighFreqDb query key = %s, dirname = %s", queryKey, result.mDirName);
            }
        }
        //NLog.d(TAG, "queryDirByHighFreqDb queryKeyList size = %d", keyPkgNameMap.size());
        ArrayList<String> queryKeyList = new ArrayList<>(keyPkgNameMap.size());
        for (String str : keyPkgNameMap.keySet()) {
            queryKeyList.add(KQueryMd5Util.getHexPathString(str));
        }

        final ArrayList<DirQueryData> toGetShowInfoResults = new ArrayList<DirQueryData>();
        int size  = queryKeyList.size();
        int sz1 = size / 2;
        int sz2 = size - sz1;
        final ArrayList<String> queryKeyList1 = new ArrayList<String>(sz1);
        final ArrayList<String> queryKeyList2 = new ArrayList<String>(sz2);
        for(int i = 0; i < sz1; i++){
            queryKeyList1.add(queryKeyList.get(i));
            queryKeyList2.add(queryKeyList.get(i + sz1));
        }

        if(size % 2 != 0){
            queryKeyList2.add(queryKeyList.get(size - 1));
        }

        final CountDownLatch countLatch = new CountDownLatch(2);
        final ValueType resultCount = new ValueType(0);
        try {
            Thread thread1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doHighFqQuery(keyPkgNameMap, db, queryKeyList1, countLatch, isGetShowInfo, toGetShowInfoResults, resultCount);
                    }
                });
            thread1.setName("Residual_hf_thread");
            thread1.start();
            Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    doHighFqQuery(keyPkgNameMap, db, queryKeyList2, countLatch, isGetShowInfo, toGetShowInfoResults, resultCount);
                }
            });
            thread2.setName("Residual_hf_thread");
            thread2.start();
            countLatch.await(TIME_WAIT_FOR_HIGH_FQ_QUERY, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isGetShowInfo && toGetShowInfoResults != null) {
            getShowInfoFromHighFreqDb(db, toGetShowInfoResults, language);
        }
        return resultCount.intValue();
    }

    private void doHighFqQuery(HashMap<String, DirQueryData> keyPkgNameMap, SQLiteDatabase db, List<String> queryKeyList
            , CountDownLatch countLatch, boolean isGetShowInfo, List<DirQueryData> toGetShowInfoResults, ValueType resultCount){
        int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        Cursor cursorSign = null;
        String r = null;
        int pageNum = 0;

        try {
            while ((r = SqlUtil.collectionToSQLInStringIncreasing(queryKeyList, pageSize, pageNum++, true)) != null) {
                try {
                    // String sql = "select dirid,queryresult,cleantype,dirs,pkgs,repkgs,dir,contenttype,cmtype,langnamealert,test,subdirs,cleantime,suffixinfo from dirquery where dir in ";
                    String sqlStr = SQL_QUERY_DIR_HF + r;
                    NLog.d(TAG, "doHighFqQuery exec sql = %s", sqlStr);
                    cursorSign = db.rawQuery(sqlStr, null);
                    if (cursorSign == null || cursorSign.getCount() == 0) {
                        continue;
                    }

                    while (cursorSign.moveToNext()) {
                        String strKey = KQueryMd5Util.getPathStringFromBytes(cursorSign.getBlob(6));
                        DirQueryData result = keyPkgNameMap.get(strKey);
                        if (result != null && fillDirQueryDataByHighFreqDb(db, cursorSign, result)) {
                            if (KResidualCloudQuery.DirQueryResultUtil.isHavePackageList(result.mResult) && isGetShowInfo) {
                                toGetShowInfoResults.add(result);
                            }
                            resultCount.increment();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursorSign != null) {
                        cursorSign.close();
                        cursorSign = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(countLatch != null){
                countLatch.countDown();
            }
        }

    }

    private boolean fillDirQueryDataByHighFreqDb(SQLiteDatabase db, Cursor cursorSign, DirQueryData result) {
        boolean ret = false;
        long time = 0;
        int  dirid = 0;
        int  queryResult = 0;
        int  cleanType = 0;
        int  contenttype = 0;
        int  cmtype = 0;
        int testFlag = 0;
        int cleanTime = 0;
        String suffixInfo = "";
        boolean integrityOk = false;
        Collection<String> oridirs   = null;
        Collection<String> dirs      = null;
        Collection<String> oripkgs   = null;
        Collection<Long> pkgs      = null;
        Collection<String> orirepkgs = null;
        Collection<String> repkgs    = null;
        ArrayList<String> filterDirIds = null;
        ArrayList<KResidualCloudQuery.FilterDirData> filterSubDirDatas = null;
        String namealert;

        integrityOk = true;
        dirid       = cursorSign.getInt(0);
        queryResult = cursorSign.getInt(1);
        cleanType   = cursorSign.getInt(2);
        time        = 0;
        contenttype = cursorSign.getInt(7);
        cmtype      = cursorSign.getInt(8);
        namealert = cursorSign.getString(9);
        testFlag	= cursorSign.getInt(10);
        if (!cursorSign.isNull(12)) {
            cleanTime = cursorSign.getInt(12);
        }
        if (!cursorSign.isNull(13)) {
            suffixInfo = cursorSign.getString(13);
        }

       /* IFalseSignFilter filter = this.mDirFilter;
        if (filter!= null && filter.filter(dirid)) {
            return false;
        }*/
        //String sql = "select dirid,queryresult,cleantype,dirs,pkgs,repkgs,dir,contenttype,cmtype,langnamealert,test,subdirs,cleantime,suffixinfo from dirquery where dir in ";
        if (!cursorSign.isNull(3)) {
            String strDirs = cursorSign.getString(3);
            try {
                if (queryResult == DirResultType.DIR_QUERY_LIST) {
                    oridirs = KJsonUtils.getStringArrayFromNoBracketJsonArrayString(strDirs);
                } else {
                    oridirs = KJsonUtils.getStringArrayFromArrayString(strDirs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (!cursorSign.isNull(4)) {
            String strPkgsID = cursorSign.getString(4);
            try {
                /**
                 * 获得pkg的pkgid列表
                 * */
                oripkgs = KJsonUtils.getStringArrayFromArrayString(strPkgsID);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!cursorSign.isNull(5)) {
            String strRePkgs = cursorSign.getString(5);
            try {
                orirepkgs = KJsonUtils.getStringArrayFromNoBracketJsonArrayString(strRePkgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (queryResult == DirResultType.DIR_LIST) {
            if (oridirs != null && !oridirs.isEmpty()) {
                dirs = getSubDirListFromDirIds(db, oridirs, true);
            }
            //数据完整性检查
            if (null == dirs || dirs.isEmpty()) {
                integrityOk = false;
            }
        } else {
            dirs = oridirs;
        }


        if (oripkgs != null && !oripkgs.isEmpty()) {
            pkgs = getGeneralPkgListFromPkgIdsByHighFreqDb(db, oripkgs);
            NLog.d(TAG, "fillDirQueryDataByHighFreqDb oripkgs = "+oripkgs +", pkgs = "+pkgs);
            //数据完整性检查
            if (null == pkgs || pkgs.isEmpty()) {
                integrityOk = false;
            }
        }
        if (orirepkgs != null && !orirepkgs.isEmpty()) {
            boolean isNumberList = isNumberList(orirepkgs);
            //NLog.d(TAG, "fillDirQueryDataByHighFreqDb isNumberList = "+isNumberList );
            if (isNumberList) {
                repkgs = getRegexPkgListFromPkgIds(db, orirepkgs);
            } else {
                repkgs = orirepkgs;
            }
            NLog.d(TAG, "fillDirQueryDataByHighFreqDb orirepkgs = "+orirepkgs +", repkgs = "+repkgs);
            //数据完整性检查
            if (null == repkgs || repkgs.isEmpty()) {
                integrityOk = false;
            }
        }
        //数据完整性检查
        if (queryResult == DirResultType.PKG_LIST
                && (null == pkgs || pkgs.isEmpty())
                && (null == repkgs || repkgs.isEmpty())) {
            integrityOk = false;
        }


        if (!cursorSign.isNull(11)) {
            String strDirIs = cursorSign.getString(11);
            filterDirIds = KJsonUtils.getStringArrayFromNoBracketJsonArrayString(strDirIs);
            if (filterDirIds != null) {
                filterSubDirDatas = getFilterDirDataFromDirIds(db, filterDirIds);

                //数据完整性检查
                if (filterSubDirDatas == null || filterSubDirDatas.size() != filterDirIds.size()) {
                    integrityOk = false;
                }
            }
        }
        if (integrityOk) {
            result.mResultSource        = ResultSourceType.HFREQ;
            result.mResult.mSignId      = dirid;
            result.mResult.mQueryResult = queryResult;
            result.mResult.mCleanType   = cleanType;
            result.mResult.mNameAlert   = namealert;
            //由于目前服务器没有数据  所以先强制本地数据为不过期
            //result.mResultExpired       = isResultExpired(getCurrentTime(), time, queryResult);
            result.mResultExpired       = false;
            result.mResult.mDirs = dirs;
            result.mResult.mPkgsMD5High64 = pkgs;
            result.mResult.mPackageRegexs = repkgs;
            result.mResult.mContentType = contenttype;
            result.mResult.mCleanMediaFlag = cmtype;
            result.mResult.mTestFlag = testFlag;
            result.mResult.mCleanTime = cleanTime;
            result.mResult.mFileCheckerData = KResidualCloudQuery.FileCheckerData.parseFromJsonString(suffixInfo);
            ((KResidualCommonData.DirQueryInnerData)result.mInnerData).mFilterSubDirDatas = filterSubDirDatas;
            ret = true;
        }
        return ret;
    }

    static class ParseLangData {
        public int	  dirid;
        public String langnamealert;
        public String nameid;
        public String alertinfoid;
        public DirQueryData result;
    }

    private boolean getShowInfoFromHighFreqDb(SQLiteDatabase db, ArrayList<DirQueryData> toGetShowInfoResults, String language) {
        boolean ret = false;
        ArrayList<ParseLangData> parseDatas = new ArrayList<>(toGetShowInfoResults.size());

        for(DirQueryData result : toGetShowInfoResults){
            int key = result.mResult.mSignId;
            ParseLangData data = new ParseLangData();
            data.result = result;
            data.dirid  =  key;

            if (!ret) {
                ret = true;
            }
            if (null == result.mResult.mShowInfo) {
                result.mResult.mShowInfo = new KResidualCloudQuery.ShowInfo();
            }

            if(!TextUtils.isEmpty(result.mResult.mNameAlert)){
                data.langnamealert = result.mResult.mNameAlert;
            }

            parseDatas.add(data);
            parseLangNameAlertString(data, language);
        }

        fillShowInfoDatas(
                parseDatas,
                getShowInfoNames(db, parseDatas),
                getShowInfoAlertInfos(db, parseDatas));

        return ret;
    }

    private boolean parseLangNameAlertString(ParseLangData data, String language) {
        if (TextUtils.isEmpty(data.langnamealert))
            return false;

        String[] langSetting = null;
        if (language.equalsIgnoreCase(KMiscUtils.LANG_EN)) {
            langSetting = new String[1];
            langSetting[0] = KMiscUtils.LANG_EN;
        } else if (language.equalsIgnoreCase(KMiscUtils.LANG_TW)) {
            langSetting = new String[2];
            langSetting[0] = KMiscUtils.LANG_TW;
            langSetting[1] = KMiscUtils.LANG_CN;
        } else {
            langSetting = new String[2];
            langSetting[0] = mLanguage;
            langSetting[1] = KMiscUtils.LANG_EN;
        }

        String[] parseResult = null;
        for (String lang : langSetting) {
            if (TextUtils.isEmpty(lang))
                continue;

            int pos = data.langnamealert.indexOf(lang);
            if (pos == -1)
                continue;

            int pos2 = data.langnamealert.indexOf(':', pos);
            if (pos2 == -1)
                continue;

            parseResult = parseShowInfoContent(data.langnamealert, pos2+1, 2);

            if (parseResult != null)
                break;
        }

        if (parseResult != null) {
            data.nameid      = parseResult[0];
            data.alertinfoid = parseResult[1];
        }
        return true;
    }

    private String[] parseShowInfoContent(String str, int start, int targetCount) {
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
                    result[cnt++] = sb.toString();
                    sb.delete(0, sb.length());
                    if (c == '|') {
                        isStop = true;
                    }
                    break;
                default:
                    sb.append(c);
            }
        }
        if (cnt < targetCount){
            result[cnt++] = sb.toString();
        }
        return result;
    }
    private HashMap<String, String> getShowInfoNames(SQLiteDatabase db, ArrayList<ParseLangData> parseDatas) {
        HashMap<String, String> result = new HashMap<String, String>();
        HashSet<String> ids = new HashSet<>();
        for(ParseLangData data : parseDatas) {
            if (!TextUtils.isEmpty(data.nameid)) {
                ids.add(data.nameid);
            }
        }

        if (ids.isEmpty())
            return result;

        int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        int pageNum  = 0;
        //String sql = "select _id,name from langqueryname where _id in ";

        StringBuffer buffer = new StringBuffer();
        String[] selection = new String[]{PkgQueryHfLangQueryNameTable._ID, PkgQueryHfLangQueryNameTable.NAME};
        buffer.append(SqlUtil.appendSqlString(PkgQueryHfLangQueryNameTable.TABLE_NAME,selection) +" where " + PkgQueryHfLangQueryNameTable._ID +" in ");
        String sql = buffer.toString();

        String r;
        Cursor cursorSign = null;
        while ((r = SqlUtil.collectionToSQLInStringIncreasing(
                ids, pageSize, pageNum++)) != null) {
            try {
                String strSql = sql + r;
                NLog.d(TAG, "getShowInfoNames exec sql = %s", strSql);
                cursorSign = db.rawQuery(strSql, null);
                if (cursorSign == null || cursorSign.getCount() == 0) {
                    continue;
                }

                while (cursorSign.moveToNext()) {
                    int key = cursorSign.getInt(0);
                    String value = cursorSign.getString(1);
                    result.put(String.valueOf(key), value);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursorSign != null) {
                    cursorSign.close();
                    cursorSign = null;
                }
            }
        }
        return result;
    }

    private HashMap<String, String> getShowInfoAlertInfos(SQLiteDatabase db, ArrayList<ParseLangData> parseDatas) {
        HashMap<String, String> result = new HashMap<>();
        HashSet<String> ids = new HashSet<>();
        for(ParseLangData data : parseDatas) {
            if (!TextUtils.isEmpty(data.alertinfoid)) {
                ids.add(data.alertinfoid);
            }
        }

        if (ids.isEmpty())
            return result;

        int pageSize = KCleanCloudEnv.SQL_IN_MAGIC_NUMBER;
        int pageNum  = 0;
        //String sql = "select _id,alert from langqueryalert where _id in ";

        String[] selection = new String[]{LangqueryalertTable._ID, LangqueryalertTable.ALERT};
        String sql = SqlUtil.appendSqlString(LangqueryalertTable.TABLE_NAME,selection) +" where " + LangqueryalertTable._ID +" in ";

        String r;
        Cursor cursorSign = null;
        while ((r = SqlUtil.collectionToSQLInStringIncreasing(
                ids, pageSize, pageNum++)) != null) {
            try {
                String strSql = sql + r;
                NLog.d(TAG, "ResidualLocalQuery getShowInfoAlertInfos exec sql = %s", strSql);
                cursorSign = db.rawQuery(strSql, null);
                if (cursorSign == null || cursorSign.getCount() == 0) {
                    continue;
                }

                while (cursorSign.moveToNext()) {
                    int key = cursorSign.getInt(0);
                    String value = cursorSign.getString(1);
                    result.put(String.valueOf(key), value);
                }

            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (cursorSign != null) {
                    cursorSign.close();
                    cursorSign = null;
                }
            }
        }
        return result;
    }

    private void fillShowInfoDatas(ArrayList<ParseLangData> parseDatas, HashMap<String, String> names, HashMap<String, String> alertInfos) {
        for (ParseLangData data : parseDatas) {
            if (names != null && !TextUtils.isEmpty(data.nameid)) {
                data.result.mResult.mShowInfo.mName = names.get(data.nameid);
            } else {
                data.result.mResult.mShowInfo.mName = "";
            }
            if (alertInfos != null && !TextUtils.isEmpty(data.alertinfoid)) {
                data.result.mResult.mShowInfo.mAlertInfo = alertInfos.get(data.alertinfoid);
            } else {
                data.result.mResult.mShowInfo.mAlertInfo = "";
            }
        }
    }

    private int queryDirByCacheDb(SQLiteDatabase db, Collection<DirQueryData> results, boolean isGetShowInfo, String language) {
        int sizeResult = 0;
        HashMap<String, DirQueryData> keyPkgNameMap = new HashMap<>(
                results.size());
        for (DirQueryData result : results) {
            if (result.mResult.mQueryResult == DirResultType.NOT_FOUND
                    || result.mResult.mQueryResult == DirResultType.UNKNOWN
                    || (result.mResult.mShowInfo != null && result.mResult.mShowInfo.mResultLangMissmatch)) {

                String queryKey = ((KResidualCommonData.DirQueryInnerData) result.mInnerData).mLocalQueryKey;
                keyPkgNameMap.put(queryKey, result);
            }
        }

        Set<String> keySet = keyPkgNameMap.keySet();//size > 0

        ArrayList<DirQueryData> toGetShowInfoResults = null;
        if (isGetShowInfo) {
            toGetShowInfoResults = new ArrayList<>(keyPkgNameMap.size());
        }
        //SqlUtil.collectionToSQLInStringIncreasing有出现过几次拼装字符串OOM
        //残留的查询key也比较长,所以先把pageSize由KCleanCloudEnv.SQL_IN_MAGIC_NUMBER改为KCleanCloudEnv.SQL_IN_MAGIC_NUMBER/2
        int pageSize = (KCleanCloudEnv.SQL_IN_MAGIC_NUMBER / 2);
        int pageNum = 0;
        //String sql = "select dirid,queryresult,cleantype,time,dirs,pkgs,repkgs,dir,contenttype,cmtype,test,subdirs,cleantime,suffixinfo from dirquery where dir in ";
        String[] selection = new String[]{
                ResidualDirCacheDirQueryTable.DIRID, ResidualDirCacheDirQueryTable.QUERYRESULT, ResidualDirCacheDirQueryTable.CLEANTYPE,
                ResidualDirCacheDirQueryTable.TIME, ResidualDirCacheDirQueryTable.DIRS, ResidualDirCacheDirQueryTable.PKGS,
                ResidualDirCacheDirQueryTable.REPKGS, ResidualDirCacheDirQueryTable.DIR, ResidualDirCacheDirQueryTable.CONTENTTYPE,
                ResidualDirCacheDirQueryTable.CMTYPE, ResidualDirCacheDirQueryTable.TEST, ResidualDirCacheDirQueryTable.SUBDIRS,
                ResidualDirCacheDirQueryTable.CLEANTIME, ResidualDirCacheDirQueryTable.SUFFIXINFO
        };
        String sql = SqlUtil.appendSqlString(ResidualDirCacheDirQueryTable.TABLE_NAME,selection) +" where "+ ResidualDirCacheDirQueryTable.DIR  +" in ";
        String r;
        Cursor cursorSign = null;
        while ((r = SqlUtil.collectionToSQLInStringIncreasing(
                keySet, pageSize, pageNum++)) != null) {
            try {
                NLog.d(TAG, String.format("queryDirByCacheDb exec sqlStr = %s", sql + r));
                cursorSign = db.rawQuery(sql + r, null);
                if (cursorSign == null || cursorSign.getCount() == 0) {
                    continue;
                }

                while (cursorSign.moveToNext()) {
                    String strKey = cursorSign.getString(7);
                    DirQueryData result = keyPkgNameMap.get(strKey);
                    if (result != null && fillDirQueryDataByCacheDb(db, cursorSign, result)) {
                        if (KResidualCloudQuery.DirQueryResultUtil.isHavePackageList(result.mResult) && isGetShowInfo) {
                            toGetShowInfoResults.add(result);
                        }
                        ++sizeResult;
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (cursorSign != null) {
                    cursorSign.close();
                    cursorSign = null;
                }
            }
        }

        if (isGetShowInfo && toGetShowInfoResults != null) {
            for (DirQueryData result : toGetShowInfoResults) {
                result.mResult.mShowInfo = getShowInfoFromCacheDb(db, result.mResult.mSignId, language);
            }
        }

        return sizeResult;
    }

    private boolean fillDirQueryDataByCacheDb(SQLiteDatabase db, Cursor cursorSign, DirQueryData result) {
        boolean ret = false;
        long time = 0;
        int  dirid = 0;
        int  queryResult = 0;
        int  cleanType = 0;
        int  contenttype = 0;
        int  cmtype = 0;
        int  testFlag = 0;
        int  cleanTime = 0;
        String suffixInfo = "";

        Collection<String> oridirs   = null;
        Collection<String> dirs      = null;
        Collection<String> oripkgs   = null;
        Collection<String> pkgs      = null;
        Collection<String> orirepkgs = null;
        Collection<String> repkgs    = null;
        ArrayList<String>  oriFilterSubDirs = null;

        boolean integrityOk = true;
        dirid       = cursorSign.getInt(0);
        queryResult = cursorSign.getInt(1);
        cleanType   = cursorSign.getInt(2);
        time        = cursorSign.getLong(3);
        contenttype = cursorSign.getInt(8);
        cmtype      = cursorSign.getInt(9);
        testFlag	= cursorSign.getInt(10);
        if (!cursorSign.isNull(12)) {
            cleanTime = cursorSign.getInt(12);
        }
        if (!cursorSign.isNull(13)) {
            suffixInfo = cursorSign.getString(13);
        }
      /*  IFalseSignFilter filter = this.mDirFilter;
        if (filter != null && filter.filter(dirid)) {
            return false;
        }*/
        //String sql = "select dirid,queryresult,cleantype,time,dirs,pkgs,repkgs,dir,contenttype,cmtype,test,subdirs,cleantime,suffixinfo from dirquery where dir in ";
        if (!cursorSign.isNull(4)) {
            String strDirs = cursorSign.getString(4);
            try {
                oridirs = KJsonUtils.getStringArrayFromJsonArrayString(strDirs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!cursorSign.isNull(5)) {
            String strPkgs = cursorSign.getString(5);
            try {
                oripkgs = KJsonUtils.getStringArrayFromJsonArrayString(strPkgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!cursorSign.isNull(6)) {
            String strRePkgs = cursorSign.getString(6);
            try {
                orirepkgs = KJsonUtils.getStringArrayFromJsonArrayString(strRePkgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (queryResult == DirResultType.DIR_LIST) {
            if (oridirs != null && !oridirs.isEmpty()) {
                boolean isNumberList = isNumberList(oridirs);
                NLog.d(TAG, "fillDirQueryDataByCacheDb isNumberList = "+ isNumberList +", oridirs = "+oridirs );
                if (isNumberList) {
                    dirs = getSubDirListFromDirIds(db, oridirs, false);
                } else {
                    dirs = oridirs;
                }
                NLog.d(TAG, "fillDirQueryDataByCacheDb dirs = "+dirs);
            }
            if (null == dirs || dirs.isEmpty()) {
                integrityOk = false;
            }
        } else {
            dirs = oridirs;
        }

        if (oripkgs != null && !oripkgs.isEmpty()) {
            pkgs = oripkgs;
        }
        if (orirepkgs != null && !orirepkgs.isEmpty()) {
            repkgs = orirepkgs;
        }

        //数据完整性检查
        if (queryResult == DirResultType.PKG_LIST
                && (null == pkgs || pkgs.isEmpty())
                && (null == repkgs || repkgs.isEmpty())) {
            integrityOk = false;
        }

        if (!cursorSign.isNull(11)) {
            String subDirs = cursorSign.getString(11);
            try {
                oriFilterSubDirs = KJsonUtils.getStringArrayFromJsonArrayString(subDirs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (integrityOk) {
            result.mResultSource        = ResultSourceType.CACHE;
            result.mResult.mSignId      = dirid;
            result.mResult.mQueryResult = queryResult;
            result.mResult.mCleanType   = cleanType;
            //由于目前服务器没有数据  所以先强制本地数据为不过期
            //result.mResultExpired       = isResultExpired(getCurrentTime(), time, queryResult);
            result.mResultExpired = false;
            result.mResult.mDirs = dirs;
            result.mResult.mPkgsMD5HexString = pkgs;
            result.mResult.mPkgsMD5High64 = null;
            result.mResult.mPackageRegexs = repkgs;
            result.mResult.mContentType = contenttype;
            result.mResult.mCleanMediaFlag = cmtype;
            result.mResult.mTestFlag = testFlag;
            result.mResult.mCleanTime = cleanTime;
            result.mResult.mFileCheckerData
                    = KResidualCloudQuery.FileCheckerData.parseFromJsonString(suffixInfo);

            ((KResidualCommonData.DirQueryInnerData)result.mInnerData).mOriFilterSubDirs = oriFilterSubDirs;

            ((KResidualCommonData.DirQueryInnerData)result.mInnerData).mFilterSubDirDatas =
                    KDirQueryDataEnDeCode.getFilterSubDirDatasFromStrings(oriFilterSubDirs);

            ret = true;
        }
        return ret;
    }

    private void appendSqlInExpString(StringBuilder builder, Collection<String> strs) {
        if (strs == null || strs.isEmpty())
            return ;

        int i = 0;
        builder.append("(");
        for (String str : strs) {
            if (i != 0) {
                builder.append(",");
            }
            builder.append(str);
            ++i;
        }
        builder.append(")");
    }

    /**
     * 组装sql语句中 where ** in (**)部分的String
     * */
///<DEAD CODE>/// 	private void appendSqlInExpLong(StringBuilder builder, Collection<Long> numbers) {
//		if (numbers == null || numbers.isEmpty())
//			return ;
//
//		int i = 0;
//		builder.append("(");
//		for (Long num : numbers) {
//			if (i != 0) {
//				builder.append(",");
//			}
//			builder.append(num);
//			++i;
//		}
//		builder.append(")");
//	}

    private ArrayList<String> getDirsFromDirIds(SQLiteDatabase db, Collection<String> dirids, boolean isBlobData) {
        ArrayList<String> dirs = new ArrayList<String>(dirids.size());
        StringBuilder builder = new StringBuilder();
        //builder.append("select dir from dirquery where dirid in ");
        String[] selection = new String[]{PkgQueryHfDirQueryTable.DIR};
        builder.append(SqlUtil.appendSqlString(PkgQueryHfDirQueryTable.TABLE_NAME,selection))
               .append(" where ")
               .append(PkgQueryHfDirQueryTable.DIRID).append(" in ");
        appendSqlInExpString(builder, dirids);
        String sql = builder.toString();
        String dir = null;
        Cursor cursorDir = null;
        try {
            cursorDir = db.rawQuery(sql, null);
            if (cursorDir != null && cursorDir.getCount() > 0) {
                while (cursorDir.moveToNext()) {
                    if (isBlobData) {
                        dir = KQueryMd5Util.getPathStringFromBytes(cursorDir.getBlob(0));
                    } else {
                        dir = cursorDir.getString(0);
                    }

                    if (!TextUtils.isEmpty(dir)) {
                        dirs.add(dir);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursorDir != null) {
                cursorDir.close();
            }
        }
        return dirs;
    }

    private ArrayList<KResidualCloudQuery.FilterDirData> getFilterDirDataFromDirIds(SQLiteDatabase db, Collection<String> dirids) {
        ArrayList<KResidualCloudQuery.FilterDirData> datas
                = new ArrayList<KResidualCloudQuery.FilterDirData>(dirids.size());
        StringBuilder builder = new StringBuilder();
        //builder.append("select dir,dirid,cleantype from dirquery where dirid in ");

        String[] selection = new String[]{PkgQueryHfDirQueryTable.DIR,PkgQueryHfDirQueryTable.DIRID,PkgQueryHfDirQueryTable.CLEANTYPE};
        builder.append(SqlUtil.appendSqlString(PkgQueryHfDirQueryTable.TABLE_NAME,selection) +" where " + PkgQueryHfDirQueryTable.DIRID +" in ");
        appendSqlInExpString(builder, dirids);
        String sql = builder.toString();
        NLog.d(TAG, "getFilterDirDataFromDirIds sql = %s",sql );
        String dir = null;
        int dirId = 0;
        int cleanType = 0;
        Cursor cursorDir = null;
        try {
            cursorDir = db.rawQuery(sql, null);
            if (cursorDir != null && cursorDir.getCount() > 0) {
                while (cursorDir.moveToNext()) {
                    dir = KQueryMd5Util.getPathStringFromBytes(cursorDir.getBlob(0));
                    dirId = cursorDir.getInt(1);
                    cleanType = cursorDir.getInt(2);
                    if (!TextUtils.isEmpty(dir)) {
                        KResidualCloudQuery.FilterDirData data = new KResidualCloudQuery.FilterDirData();
                        data.mSingId = dirId;
                        data.mPath = dir;
                        data.mCleanType = cleanType;
                        datas.add(data);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursorDir != null) {
                cursorDir.close();
            }
        }
        return datas;
    }

    private ArrayList<String> getDirsFromDirIds2(SQLiteDatabase db, Collection<String> dirids) {
        ArrayList<String> dirs = new ArrayList<String>(dirids.size());
        StringBuilder builder = new StringBuilder();
        //builder.append("select dir from dirquery2 where _id in ");

        String[] selection = new String[]{PkgQueryHfDirQuery2Table.DIR};
        builder.append(SqlUtil.appendSqlString(PkgQueryHfDirQuery2Table.TABLE_NAME,selection) +" where " + PkgQueryHfDirQueryTable._ID +" in ");

        appendSqlInExpString(builder, dirids);
        String sql = builder.toString();
        String dir = null;
        Cursor cursorDir = null;
        try {
            cursorDir = db.rawQuery(sql, null);
            if (cursorDir != null && cursorDir.getCount() > 0) {
                while (cursorDir.moveToNext()) {
                    dir = KQueryMd5Util.getPathStringFromBytes(cursorDir.getBlob(0));
                    if (!TextUtils.isEmpty(dir)) {
                        dirs.add(dir);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursorDir != null) {
                cursorDir.close();
            }
        }
        return dirs;
    }

    private Collection<String> getSubDirListFromDirIds(SQLiteDatabase db, Collection<String> dirids, boolean isBlobData) {
        Collection<String> dirs = getDirsFromDirIds(db, dirids, isBlobData);
        if (null == dirs || dirs.isEmpty())
            return null;

        ArrayList<String> resultDirs = new ArrayList<>(dirs.size());
        int pos = -1;
        for (String dir : dirs) {
            pos = dir.indexOf('+');
            //不应该出现没有加号的
            if (pos == -1)
                continue;

            //把子目录取出来
            resultDirs.add(dir.substring(pos+1));
        }
        return resultDirs;
    }

    private ArrayList<String> getDirListFromDirIds(SQLiteDatabase db, Collection<String> dirids, boolean isBlobData) {
        ArrayList<String> dirs = getDirsFromDirIds(db, dirids, isBlobData);
        if (dirs.size() != dirids.size()) {
            //如果目录获取不到，说明数据不完整,那么就丢弃这个数据，联网查
            //如果出现这个情况，高频库数据就是有问题
            dirs = null;
        }
        return dirs;
    }

    private ArrayList<String> getDirListFromDirIds2(SQLiteDatabase db, Collection<String> dirids) {
        ArrayList<String> dirs = getDirsFromDirIds2(db, dirids);
        if (dirs.size() != dirids.size()) {
            //如果目录获取不到，说明数据不完整,那么就丢弃这个数据，联网查
            //如果出现这个情况，高频库数据就是有问题
            dirs = null;
        }
        return dirs;
    }


    private Collection<Long> getGeneralPkgListFromPkgIdsByHighFreqDb(SQLiteDatabase db, Collection<String> pkgids) {
        ArrayList<Long> pkgs = new ArrayList<Long>(pkgids.size());
        StringBuilder builder = new StringBuilder();
        //builder.append("select pkg from pkgquery where pkgid in ");
        String[] selection = new String[]{ResidualPkgCachePkgQueryTable.PKG};
        builder.append(SqlUtil.appendSqlString(ResidualPkgCachePkgQueryTable.TABLE_NAME,selection) +" where " + ResidualPkgCachePkgQueryTable.PKG_ID +" in ");
        appendSqlInExpString(builder, pkgids);
        String sql = builder.toString();
        NLog.d(TAG, "getGeneralPkgListFromPkgIdsByHighFreqDb sql = "+ sql);
        long pkg;
        Cursor cursorPkg = null;
        try {
            cursorPkg =db.rawQuery(sql, null);
            if (cursorPkg != null) {
                while (cursorPkg.moveToNext()) {
                    pkg = cursorPkg.getLong(0);
                    pkgs.add(pkg);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (cursorPkg != null) {
                cursorPkg.close();
            }
        }
        if (pkgs.size() != pkgids.size()) {
            //如果包列表不全，可能会产生误报,所以全部废弃
            pkgs = null;
        }
        return pkgs;
    }

///<DEAD CODE>/// 	private Collection<String> getGeneralPkgListFromPkgIds(SQLiteDatabase db, Collection<String> pkgids) {
//		return getPkgListFromPkgIds(db, "pkgquery", pkgids);
//	}

    private Collection<String> getRegexPkgListFromPkgIds(SQLiteDatabase db, Collection<String> pkgids) {
        //return getPkgListFromPkgIds(db, "repkgquery", pkgids);
        return getPkgListFromPkgIds(db, PkgQueryHfRePkgQueryTable.TABLE_NAME, pkgids);
    }

    private static final String[] PKG_QUERY_COLUMNS =  {PkgQueryHfRePkgQueryTable.PKG};
    private Collection<String> getPkgListFromPkgIds(SQLiteDatabase db, String tableName, Collection<String> pkgids) {
        ArrayList<String> pkgs = new ArrayList<String>(pkgids.size());
        StringBuilder builder = new StringBuilder();
        //builder.append("pkgid in ");
        builder.append(PkgQueryHfRePkgQueryTable.PKG_ID +" in ");
        appendSqlInExpString(builder, pkgids);
        String sql = builder.toString();
        NLog.e(TAG, "getRegexPkgListFromPkgIds sql = "+ sql);
        String pkg;
        Cursor cursorPkg = null;
        try {
            cursorPkg = db.query(tableName, PKG_QUERY_COLUMNS, sql,
                    null, null, null, null);
            if (cursorPkg != null) {
                while (cursorPkg.moveToNext()) {
                    pkg = cursorPkg.getString(0);
                    if (!TextUtils.isEmpty(pkg)) {
                        pkgs.add(pkg);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursorPkg != null) {
                cursorPkg.close();
            }
        }
        if (pkgs.size() != pkgids.size()) {
            //如果包列表不全，可能会产生误报,所以全部废弃
            pkgs = null;
        }
        return pkgs;
    }


    private KResidualCloudQuery.ShowInfo getShowInfoFromCacheDb(SQLiteDatabase db, int dirid, String language) {
        KResidualCloudQuery.ShowInfo showInfo = new KResidualCloudQuery.ShowInfo();
        Cursor cursorLang = null;
        String strDirid = String.valueOf(dirid);
        //boolean isTwLang = false;
        try {
            StringBuilder builder = new StringBuilder(mDefaultLanguage.length()+3);
            builder.append("%");
            //如果语言是台湾的，那么如果没有tw对应的信息就用cn的
            if(language.equalsIgnoreCase(KMiscUtils.LANG_TW)) {
                builder.append(KMiscUtils.LANG_CN);
                //isTwLang = true;
            } else{
                builder.append(mDefaultLanguage);
            }
            builder.append("%");
            if (0 == language.compareTo(mDefaultLanguage)) {
                String[] args = {strDirid, builder.toString()};
                //String sql = "select lang,name,alert,desc from langquery where dirid=? and lang like ?";
                String[] selection = new String[]{
                        ResidualDirCacheLangQueryTable.LANG, ResidualDirCacheLangQueryTable.NAME,
                        ResidualDirCacheLangQueryTable.ALERT, ResidualDirCacheLangQueryTable.DESC};
                String sql = SqlUtil.appendSqlString(ResidualDirCacheLangQueryTable.TABLE_NAME,selection)
                        +" where " + ResidualDirCacheLangQueryTable.DIRID +" = ? and "+ ResidualDirCacheLangQueryTable.LANG +" like ?";
                NLog.d(TAG, "getShowInfoFromCacheDb sql1 = %s", sql);
                cursorLang = db.rawQuery(sql, args);
            } else {
                StringBuilder builder2 = new StringBuilder(language.length()+3);
                builder2.append("%");
                builder2.append(language);
                builder2.append("%");
                String[] args = {strDirid, builder.toString(), builder2.toString()};
                //String sql = "select lang,name,alert,desc from langquery where dirid=? and (lang like ? or lang like ?)";

                String[] selection = new String[]{
                        ResidualDirCacheLangQueryTable.LANG, ResidualDirCacheLangQueryTable.NAME,
                        ResidualDirCacheLangQueryTable.ALERT, ResidualDirCacheLangQueryTable.DESC};
                String sql = SqlUtil.appendSqlString(ResidualDirCacheLangQueryTable.TABLE_NAME,selection)
                        +" where " + ResidualDirCacheLangQueryTable.DIRID +" = ? and ("+ ResidualDirCacheLangQueryTable.LANG +" like ? or "+ ResidualDirCacheLangQueryTable.LANG +" like ?)";
                NLog.d(TAG, "getShowInfoFromCacheDb sq2 = %s", sql);
                cursorLang = db.rawQuery(sql, args);
            }

            if (cursorLang != null) {
                String strLang = null;
                boolean isMatch = false;
                int cnt = cursorLang.getCount();
                int i = 0;
                while (cursorLang.moveToNext()) {
                    ++i;
                    strLang = cursorLang.getString(0);
                    if (strLang.contains(language)) {
                        isMatch = true;
                        break;
                    }
                    if (i >= cnt) {
                        break;
                    }
                }
                if (strLang != null) {
                    if (!cursorLang.isNull(1)) {
                        showInfo.mName = cursorLang.getString(1);
                    }
                    if (!cursorLang.isNull(2)) {
                        showInfo.mAlertInfo = cursorLang.getString(2);
                    }
                    if (!cursorLang.isNull(3)) {
                        showInfo.mDescription = cursorLang.getString(3);
                    }
                }
                //if (isTwLang && resultSource == ResultSourceType.HFREQ) {
                //	showInfo.mResultLangMissmatch = false;
                //} else {
                showInfo.mResultLangMissmatch = !isMatch;
                //}
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (cursorLang != null) {
                cursorLang.close();
            }
        }
        return showInfo;
    }
    /**
     * @param signId
     * @param language
     * @return
     */

    public KResidualCloudQuery.ShowInfo getShowInfoByDirId(int signId, String language) {
        if (TextUtils.isEmpty(language)) {
            language = mLanguage;
        }


        KResidualCloudQuery.ShowInfo showInfo = null;

      /*  MyDBData dirCacheDb = null;
        MyDBData highFrequentDb = null;
        mAccessCount.incrementAndGet();

        highFrequentDb 	= mHighFreqDbHolder.getDatabaseAndAcquireReference();
        dirCacheDb 		= mDirCacheDbHolder.getDatabaseAndAcquireReference();

        if (null == dirCacheDb && null == highFrequentDb) {
            mAccessCount.decrementAndGet();
            return new KResidualCloudQuery.ShowInfo();
        }



        if (highFrequentDb != null) {
            ArrayList<DirQueryData> toGetShowInfoResults = new ArrayList<>();
            DirQueryData queryData = new DirQueryData();
            queryData.mLanguage = language;
            queryData.mErrorCode = 0;
            queryData.mResultSource = ResultSourceType.HFREQ;
            queryData.mResult = new KResidualCloudQuery.DirQueryResult();
            queryData.mResult.mSignId = signId;
            toGetShowInfoResults.add(queryData);
            if (getShowInfoFromHighFreqDb(highFrequentDb.mDb, toGetShowInfoResults, language)) {
                if (queryData.mResult.mShowInfo != null) {
                    showInfo = queryData.mResult.mShowInfo;
                }
            }
        }

        if (null == showInfo || TextUtils.isEmpty(showInfo.mName)) {
            if (dirCacheDb != null) {
                showInfo = getShowInfoFromCacheDb(dirCacheDb.mDb, signId, language);
            }
        }
        mDirCacheDbHolder.releaseReference(dirCacheDb);
        mHighFreqDbHolder.releaseReference(highFrequentDb);*/

        ArrayList<DirQueryData> toGetShowInfoResults = new ArrayList<>();
        DirQueryData queryData = new DirQueryData();
        queryData.mLanguage = language;
        queryData.mErrorCode = 0;
        queryData.mResultSource = ResultSourceType.HFREQ;
        queryData.mResult = new KResidualCloudQuery.DirQueryResult();
        queryData.mResult.mSignId = signId;
        toGetShowInfoResults.add(queryData);
        if (getShowInfoFromHighFreqDb(mResidualDirHfProvider.getDatabase(), toGetShowInfoResults, language)) {
            if (queryData.mResult.mShowInfo != null) {
                showInfo = queryData.mResult.mShowInfo;
            }
        }
        if (null == showInfo || TextUtils.isEmpty(showInfo.mName)) {
            if (mResidualDirCacheProvider != null) {
                showInfo = getShowInfoFromCacheDb(mResidualDirCacheProvider.getDatabase(), signId, language);
            }
        }


        mAccessCount.decrementAndGet();
        return showInfo;
    }

    // 全局文件名后缀分类配置信息
    private Map<Integer, Set<String>> globalSuffixConfig = null;
    private Object globalSuffixConfigLocker = new Object();

    /**
     * 获取全局文件名后缀分类配置信息
     * @return      全局文件名后缀分类配置信息
     */
    public Map<Integer, Set<String>> getGlobalSuffixConfig() {
        if (null == globalSuffixConfig) {
            synchronized (globalSuffixConfigLocker) {
                if (null == globalSuffixConfig) {
                    globalSuffixConfig = loadGlobalSuffixConfig(mResidualDirHfProvider.getDatabase());
                }

                return globalSuffixConfig;
            }
        }
        return globalSuffixConfig;
    }

    private Map<Integer, Set<String>> loadGlobalSuffixConfig(SQLiteDatabase db) {
        mAccessCount.incrementAndGet();
        setCurrentTime(System.currentTimeMillis());

        Map<Integer, Set<String>> result = new HashMap<Integer, Set<String>>();

        try {
            if (db != null) {

                //String sql = "select typeid,suffix from globalsuffixconfig";
                StringBuilder builder = new StringBuilder();
                String[] selection = new String[]{GlobalsuffixconfigTable.TYPEID, GlobalsuffixconfigTable.SUFFIX};
                builder.append(SqlUtil.appendSqlString(GlobalsuffixconfigTable.TABLE_NAME,selection) );
                String sql = builder.toString();
                NLog.d(TAG, "loadGlobalSuffixConfig sql = %s", sql);
                Cursor cursorSign = null;
                try {
                    cursorSign = db.rawQuery(sql, null);
                    if (null != cursorSign && cursorSign.getCount() > 0) {
                        while (cursorSign.moveToNext()) {
                            int id = cursorSign.getInt(0);
                            if (!cursorSign.isNull(1)) {
                                String suffixStr = cursorSign.getString(1);
                                if (TextUtils.isEmpty(suffixStr)) {
                                    continue;
                                }

                                String[] sfxArr = suffixStr.split("\\|");
                                Set<String> sfxSet = new HashSet<String>();
                                for (int i = 0; i < sfxArr.length; i++) {
                                    sfxSet.add(sfxArr[i]);
                                }
                                result.put(id, sfxSet);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursorSign != null) {
                        cursorSign.close();
                        cursorSign = null;
                    }
                }

               /* mHighFreqDbHolder.releaseReference(highFrequentDb);
                highFrequentDb = null;*/
            }
        } catch (Exception e) {
            //handleSQLiteDatabaseIllegalStateException(e, mHighFreqDbHolder, highFrequentDb, true, mHighFreqDbHolder.getCleanCloudGlue());
        }

        mAccessCount.decrementAndGet();

        return result;
    }


    private static final String SQL_QUERY_DIR_HF
        = String.format("select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s from %s where %s in "
        , PkgQueryHfDirQueryTable.DIRID, PkgQueryHfDirQueryTable.QUERYRESULT, PkgQueryHfDirQueryTable.CLEANTYPE
        , PkgQueryHfDirQueryTable.DIRS, PkgQueryHfDirQueryTable.PKGS, PkgQueryHfDirQueryTable.REPKGS
        , PkgQueryHfDirQueryTable.DIR, PkgQueryHfDirQueryTable.CONTENTTYPE, PkgQueryHfDirQueryTable.CMTYPE
        , PkgQueryHfDirQueryTable.LANGNAMEALERT,PkgQueryHfDirQueryTable.TEST,PkgQueryHfDirQueryTable.SUBDIRS
        , PkgQueryHfDirQueryTable.CLEANTIME,PkgQueryHfDirQueryTable.SUFFIXINFO
        , PkgQueryHfDirQueryTable.TABLE_NAME
        , PkgQueryHfDirQueryTable.DIR);
}

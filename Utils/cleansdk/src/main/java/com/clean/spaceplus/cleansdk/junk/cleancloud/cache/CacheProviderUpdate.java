package com.clean.spaceplus.cleansdk.junk.cleancloud.cache;

import android.content.ContentValues;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.db.pkgcache.CachePathQueryTable;
import com.clean.spaceplus.cleansdk.base.db.pkgcache.CacheLangQueryTable;
import com.clean.spaceplus.cleansdk.base.db.pkgcache.CachePkgQueryTable;
import com.clean.spaceplus.cleansdk.base.db.pkgcache.PkgCacheProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import space.network.cleancloud.KCacheCloudQuery;
import space.network.cleancloud.KCacheCloudQuery.PkgQueryData;
import space.network.cleancloud.KCacheCloudQuery.PkgQueryPathItem;
import space.network.cleancloud.core.cache.KCacheCommonData;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/25 17:30
 * @copyright TCL-MIG
 */
public class CacheProviderUpdate {


    public CacheProviderUpdate() {
    }

    // pkgid, pkg, dirs, redirs, files, refiles, time
    public synchronized boolean updatePathCache(final Collection<PkgQueryData> results,PkgCacheProvider pkgCacheProvider) {
        if (results == null || results.size() == 0) {
            return false;
        }
        ArrayList<ContentValues> bulkValues = new ArrayList<>();

        for (PkgQueryData result : results) {// result在上级调用中已经确保有效性
            KCacheCommonData.CachePkgQueryInnerData mInnerData = (KCacheCommonData.CachePkgQueryInnerData) result.mInnerData;
            bindPathItemInfos(bulkValues, mInnerData.mPkgQueryPathItems, KCacheCloudQuery.CachePathType.DIR);
        }

        if (bulkValues.isEmpty())
            return true;


        //int ret = mCacheDb.bulkInsert("pathquery", bulkValues.toArray(new ContentValues[bulkValues.size()]));
        //更新到pkgcache2_cache库
        long ret = pkgCacheProvider.insert(CachePathQueryTable.TABLE_NAME, null, bulkValues.toArray(new ContentValues[bulkValues.size()]));
        if(ret < 0){
//            CleanCloudLogger.reportUpdateCacheDBFail((byte) CleanCloudDef.CloudLogicType.CLOUD_CACHE, CacheTable.Table_PathQuery);
        }

        return true;
    }

    private void bindPathItemInfos(ArrayList<ContentValues> bulkValues,
                                   Collection<PkgQueryPathItem> items, int pathType) {
        if (items == null || items.size() == 0) {
            return;
        }
        for (PkgQueryPathItem item : items) {
            ContentValues cv = new ContentValues();
            //cv.put("pathid", item.mSignId);
            cv.put(CachePathQueryTable.PATH_ID, item.mSignId);

            //cv.put("path", item.mPathString == null ? "" : item.mPathString);
            cv.put(CachePathQueryTable.PATH, item.mPathString == null ? "" : item.mPathString);

            //cv.put("pathtype", item.mPathType);
            cv.put(CachePathQueryTable.PATH_TYPE, item.mPathType);

            //cv.put("cleantype", item.mCleanType);
            cv.put(CachePathQueryTable.CLEAN_TYPE, item.mCleanType);

            //cv.put("cleantime", item.mCleanTime);
            cv.put(CachePathQueryTable.CLEAN_TIME, item.mCleanTime);

            //cv.put("cleanop", item.mCleanOperation);
            cv.put(CachePathQueryTable.CLEANOP, item.mCleanOperation);

            //cv.put("contenttype", item.mContentType);
            cv.put(CachePathQueryTable.CONTENT_TYPE, item.mContentType);

            //cv.put("cmtype", item.mCleanMediaFlag);
            cv.put(CachePathQueryTable.CMTYPE, item.mCleanMediaFlag);

            //cv.put("privacytype", item.mPrivacyType);
            cv.put(CachePathQueryTable.PRIVACY_TYPE, item.mPrivacyType);

            //cv.put("isneedcheck", item.mNeedCheck);
            cv.put(CachePathQueryTable.IS_NEED_CHECK, item.mNeedCheck);

            //cv.put("test", item.mTestFlag);
            cv.put(CachePathQueryTable.TEST, item.mTestFlag);
            bulkValues.add(cv);
        }
    }

    /**
     * <pre>
     * PkgQueryResult pkgQueryResult = result.mResult;
     * replaceCachePkgSql.bindLong(1, pkgQueryResult.mPkgId);
     * CachePkgQueryInnerData mInnerData = (CachePkgQueryInnerData) result.mInnerData;
     * replaceCachePkgSql.bindString(2, mInnerData.mPkgNameMd5);
     * binddfdrfr(replaceCachePkgSql, 3, mInnerData.mPkgQueryDirItems);
     * binddfdrfr(replaceCachePkgSql, 4, mInnerData.mPkgQueryDirRegItems);
     * binddfdrfr(replaceCachePkgSql, 5, mInnerData.mPkgQueryFileItems);
     * binddfdrfr(replaceCachePkgSql, 6, mInnerData.mPkgQueryFileRegItems);
     * replaceCachePkgSql.bindLong(7, currentTime);
     * replaceCachePkgSql.execute();
     * </pre>
     *
     * @param results
     * @return
     */
    public synchronized boolean updatePkgCache(final Collection<PkgQueryData> results,PkgCacheProvider pkgCacheProvider) {
        if (results == null || results.size() == 0) {
            return false;
        }
        ContentValues[] bulkValues = new ContentValues[results.size()];
        long currentTime = System.currentTimeMillis();
        int i = 0;
        for (PkgQueryData result : results) {
            KCacheCommonData.CachePkgQueryInnerData mInnerData = (KCacheCommonData.CachePkgQueryInnerData) result.mInnerData;
            String queryKey = mInnerData.mPkgNameMd5;
            ContentValues values = new ContentValues();
            //values.put("pkgid", result.mResult.mPkgId);
            values.put(CachePkgQueryTable.PKG_ID, result.mResult.mPkgId);
            //values.put("pkg", queryKey);
            values.put(CachePkgQueryTable.PKG, queryKey);
            //values.put("time", currentTime);
            values.put(CachePkgQueryTable.TIME, currentTime);
            //values.put("dirs", getDirStringFromPkgQueryPathItems(mInnerData.mPkgQueryPathItems));
            values.put(CachePkgQueryTable.DIRS, getDirStringFromPkgQueryPathItems(mInnerData.mPkgQueryPathItems));
            //values.put("sysflag", result.mResult.mSysFlag);
            values.put(CachePkgQueryTable.SYS_FLAG, result.mResult.mSysFlag);
            bulkValues[i] = values;
            ++i;
        }

        //int ret = mCacheDb.bulkInsert("pkgquery", bulkValues);
        long ret = pkgCacheProvider.insert(CachePkgQueryTable.TABLE_NAME, null, bulkValues);
        if(ret < 0){
//            CleanCloudLogger.reportUpdateCacheDBFail((byte)CleanCloudDef.CloudLogicType.CLOUD_CACHE, CacheTable.Table_PKGQuery);
        }

        return true;
    }
    private String getDirStringFromPkgQueryPathItems(
            Collection<PkgQueryPathItem> datas) {
        String result = null;
        if (datas != null && datas.size() > 0) {
            String[] dirArrays = new String[datas.size()];
            int i = 0;
            for (PkgQueryPathItem item : datas) {
                dirArrays[i] = item.mSignId;
                i++;
            }
            result = Arrays.toString(dirArrays);// dirArrays
            // !=
            // null
        } else {
            result = "[]";
        }
        return result;
    }

    public void updateShowInfoCache(LinkedList<PkgQueryPathItem> updateCacheResults, String language,PkgCacheProvider pkgCacheProvider) {
        if (updateCacheResults == null || updateCacheResults.size() == 0) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        ArrayList<ContentValues> cvs = new ArrayList<ContentValues>(
                updateCacheResults.size());
        for (PkgQueryPathItem item : updateCacheResults) {
            // result在上级调用中已经确保有效性 showinfo!=null and showinfo没有过期！
            if (item.mShowInfo == null || item.mShowInfo.mResultExpired) {
                continue;
            }
            ContentValues cv = new ContentValues();
            //cv.put("pathid", item.mSignId);
            cv.put(CacheLangQueryTable.PATH_ID, item.mSignId);

            //cv.put("lang", language);
            cv.put(CacheLangQueryTable.LANG, language);

            //cv.put("time", currentTime);
            cv.put(CacheLangQueryTable.TIME, currentTime);

            String showinfoName = item.mShowInfo.mName;

            //cv.put("name", TextUtils.isEmpty(showinfoName) ? "" : showinfoName);
            cv.put(CacheLangQueryTable.NAME, TextUtils.isEmpty(showinfoName) ? "" : showinfoName);

            String mDescription = item.mShowInfo.mDescription;
            //cv.put("desc", TextUtils.isEmpty(mDescription) ? "" : mDescription);
            cv.put(CacheLangQueryTable.DESC, TextUtils.isEmpty(mDescription) ? "" : mDescription);
            cvs.add(cv);
        }
        if (!cvs.isEmpty()) {
            ContentValues[] bulkLangValues = cvs.toArray(new ContentValues[cvs.size()]);

            pkgCacheProvider.insert(CacheLangQueryTable.TABLE_NAME, null, bulkLangValues);
        }
    }
}

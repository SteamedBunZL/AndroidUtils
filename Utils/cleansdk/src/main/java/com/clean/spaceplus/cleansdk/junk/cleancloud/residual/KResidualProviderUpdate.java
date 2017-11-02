package com.clean.spaceplus.cleansdk.junk.cleancloud.residual;

import android.content.ContentValues;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.db.residual_dir_cache.ResidualDirCacheProvider;
import com.clean.spaceplus.cleansdk.base.db.residual_pkg_cache.ResidualPkgCacheProvider;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_cache.ResidualDirCacheDirQueryTable;
import com.clean.spaceplus.cleansdk.base.db.residual_dir_cache.ResidualDirCacheLangQueryTable;
import com.clean.spaceplus.cleansdk.base.db.residual_pkg_cache.ResidualPkgCachePkgQueryTable;

import java.util.ArrayList;
import java.util.Collection;

import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.KResidualCloudQuery.DirQueryData;
import space.network.cleancloud.KResidualCloudQuery.PkgQueryData;
import space.network.cleancloud.KResidualCloudQuery.PkgQueryDirItem;
import space.network.cleancloud.core.residual.KResidualCommonData;
import space.network.util.net.KJsonUtils;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/14 14:07
 * @copyright TCL-MIG
 */
public class KResidualProviderUpdate {
    public KResidualProviderUpdate() {
    }


    public boolean updateDirCache(final Collection<DirQueryData> results) {
        ContentValues[] bulkValues = new ContentValues[results.size()];
        ArrayList<ContentValues> langValuesArray = new ArrayList<>(results.size());
        long currentTime = System.currentTimeMillis();
        int i = 0;
        for (DirQueryData result : results) {
            String queryKey = ((KResidualCommonData.DirQueryInnerData)result.mInnerData).mLocalQueryKey;

            ContentValues values = new ContentValues();
            //values.put("dir", queryKey);
            values.put(ResidualDirCacheDirQueryTable.DIR, queryKey);

            //values.put("dirid", result.mResult.mSignId);
            values.put(ResidualDirCacheDirQueryTable.DIRID, result.mResult.mSignId);

            //values.put("queryresult", result.mResult.mQueryResult);
            values.put(ResidualDirCacheDirQueryTable.QUERYRESULT, result.mResult.mQueryResult);

            //values.put("cleantype", result.mResult.mCleanType);
            values.put(ResidualDirCacheDirQueryTable.CLEANTYPE, result.mResult.mCleanType);

            //values.put("contenttype", result.mResult.mContentType);
            values.put(ResidualDirCacheDirQueryTable.CONTENTTYPE, result.mResult.mContentType);

            //values.put("cmtype", result.mResult.mCleanMediaFlag);
            values.put(ResidualDirCacheDirQueryTable.CMTYPE, result.mResult.mCleanMediaFlag);

            //values.put("test", result.mResult.mTestFlag);
            values.put(ResidualDirCacheDirQueryTable.TEST, result.mResult.mTestFlag);

            //values.put("time", currentTime);
            values.put(ResidualDirCacheDirQueryTable.TIME, currentTime);

            if (result.mResult.mDirs != null && !result.mResult.mDirs.isEmpty()) {
                String dirs = KJsonUtils.getJsonArrayStringFromStringArray(result.mResult.mDirs);
                //values.put("dirs", dirs);
                values.put(ResidualDirCacheDirQueryTable.DIRS, dirs);
            }
            ArrayList<String> oriFilterSubDirs = ((KResidualCommonData.DirQueryInnerData)result.mInnerData).mOriFilterSubDirs;
            if (oriFilterSubDirs != null && !oriFilterSubDirs.isEmpty()) {
                String subdirs = KJsonUtils.getJsonArrayStringFromStringArray(oriFilterSubDirs);
                //values.put("subdirs", subdirs);
                values.put(ResidualDirCacheDirQueryTable.SUBDIRS, subdirs);
            }
            if (result.mResult.mPkgsMD5HexString != null && !result.mResult.mPkgsMD5HexString.isEmpty()) {
                String pkgs = KJsonUtils.getJsonArrayStringFromStringArray(result.mResult.mPkgsMD5HexString);
                //values.put("pkgs", pkgs);
                values.put(ResidualDirCacheDirQueryTable.PKGS, pkgs);
            }

            if (result.mResult.mPackageRegexs != null && !result.mResult.mPackageRegexs.isEmpty()) {
                String pkgs = KJsonUtils.getJsonArrayStringFromStringArray(result.mResult.mPackageRegexs);
                //values.put("repkgs", pkgs);
                values.put(ResidualDirCacheDirQueryTable.REPKGS, pkgs);
            }
            //values.put("cleantime", result.mResult.mCleanTime);
            values.put(ResidualDirCacheDirQueryTable.CLEANTIME, result.mResult.mCleanTime);

            String suffixInfo = ((KResidualCommonData.DirQueryInnerData)result.mInnerData).mSuffixInfo;
            //values.put("suffixinfo", null != suffixInfo ? suffixInfo : "");
            values.put(ResidualDirCacheDirQueryTable.SUFFIXINFO, null != suffixInfo ? suffixInfo : "");

            bulkValues[i] = values;
            ++i;


            if (result.mResult.mShowInfo != null
                    && !TextUtils.isEmpty(result.mResult.mShowInfo.mName)) {
                ContentValues langValues = new ContentValues();
                String strDirid = String.valueOf(result.mResult.mSignId);
                //langValues.put("dirid", strDirid);
                langValues.put(ResidualDirCacheLangQueryTable.DIRID, strDirid);

                //langValues.put("lang", result.mLanguage);
                langValues.put(ResidualDirCacheLangQueryTable.LANG, result.mLanguage);

                //langValues.put("name", result.mResult.mShowInfo.mName);]
                langValues.put(ResidualDirCacheLangQueryTable.NAME, result.mResult.mShowInfo.mName);

                if (result.mResult.mShowInfo.mAlertInfo != null) {
                    //langValues.put("alert", result.mResult.mShowInfo.mAlertInfo);
                    langValues.put(ResidualDirCacheLangQueryTable.ALERT, result.mResult.mShowInfo.mAlertInfo);
                }

                if (result.mResult.mShowInfo.mDescription != null) {
                    //langValues.put("desc", result.mResult.mShowInfo.mDescription);
                    langValues.put(ResidualDirCacheLangQueryTable.DESC, result.mResult.mShowInfo.mDescription);
                }
                langValuesArray.add(langValues);
            }

        }

        if (bulkValues.length == 0 && langValuesArray.isEmpty())
            return true;


        if (bulkValues.length != 0) {
            ResidualDirCacheProvider provider = ResidualDirCacheProvider.getInstance();
            provider.insert(ResidualDirCacheDirQueryTable.TABLE_NAME, null,bulkValues);
        }

        if (!langValuesArray.isEmpty()) {
            ContentValues[] bulkLangValues = langValuesArray.toArray(new ContentValues[langValuesArray.size()]);
            //int ret = mDirCacheDb.bulkInsert("langquery", bulkLangValues);
            ResidualDirCacheProvider provider = ResidualDirCacheProvider.getInstance();
            provider.insert(ResidualDirCacheLangQueryTable.TABLE_NAME, null,bulkLangValues);
        }

        return true;
    }



    public boolean updatePkgCache(final Collection<KResidualCloudQuery.PkgQueryData> results) {
        ContentValues[] bulkValues = new ContentValues[results.size()];
        long currentTime = System.currentTimeMillis();
        int i = 0;
        for (PkgQueryData result : results) {
            Collection<String> dirs = null;
            String strDirs = null;
            String queryKey = ((KResidualCommonData.PkgQueryInnerData)result.mInnerData).mPkgNameMd5;
            ContentValues values = new ContentValues();
            //values.put("pkgid", result.mResult.mSignId);
            values.put(ResidualPkgCachePkgQueryTable.PKG_ID, result.mResult.mSignId);

            //values.put("pkg", queryKey);
            values.put(ResidualPkgCachePkgQueryTable.PKG, queryKey);

            //values.put("time", currentTime);
            values.put(ResidualPkgCachePkgQueryTable.TIME, currentTime);

            if (result.mResult.mPkgQueryDirItems != null && !result.mResult.mPkgQueryDirItems.isEmpty()) {
                dirs = getDirStringFromPkgQueryDirItems(result.mResult.mPkgQueryDirItems);
                strDirs = KJsonUtils.getJsonArrayStringFromStringArray(dirs);
            }
            if (strDirs != null) {
                //values.put("dirs", strDirs);
                values.put(ResidualPkgCachePkgQueryTable.DIRS, strDirs);
            }
            bulkValues[i] = values;
            ++i;
        }
        if (0 == bulkValues.length)
            return true;

        //int ret = mPkgCacheDb.bulkInsert("pkgquery", bulkValues);

        ResidualPkgCacheProvider provider = ResidualPkgCacheProvider.getInstance();
        long ret = provider.insert(ResidualPkgCachePkgQueryTable.TABLE_NAME, null,bulkValues);

        if(ret < 0){
            //CleanCloudLogger.reportUpdateCacheDBFail((byte) CleanCloudDef.CloudLogicType.CLOUD_RESIDUAL, ResidualTable.Table_PKGQuery);
        }

        return true;
    }


    Collection<String> getDirStringFromPkgQueryDirItems(Collection<KResidualCloudQuery.PkgQueryDirItem> pkgQueryDirItems) {
        Collection<String> result = null;
        if (null == pkgQueryDirItems || pkgQueryDirItems.isEmpty())
            return result;

        result = new ArrayList<>(pkgQueryDirItems.size());
        for (PkgQueryDirItem item : pkgQueryDirItems) {
            if (0 == item.mRegexSignId) {
                result.add(item.mDirString);
            }
        }
        return result;
    }
}

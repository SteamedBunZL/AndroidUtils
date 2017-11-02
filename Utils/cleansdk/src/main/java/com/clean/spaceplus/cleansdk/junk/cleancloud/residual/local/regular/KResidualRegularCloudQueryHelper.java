package com.clean.spaceplus.cleansdk.junk.cleancloud.residual.local.regular;

import android.text.TextUtils;

import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.KResidualCloudQuery.DirQueryData;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/21 13:50
 * @copyright TCL-MIG
 */
public class KResidualRegularCloudQueryHelper {

    /**
     * pkg filter.
     * */
    public  interface IPkgDirFilter {
        boolean isInFilter(String strPkg);
    }

    public static KResidualCloudQuery.DirQueryData getDirQueryDatas(String dirname, String lang) {
        DirQueryData data = new DirQueryData();
        KResidualRegularCloudQuery.RegularDirQueryInnerData innerData = KResidualRegularCloudQueryHelper.getDirQueryInnerData(dirname);
        data.mResult 	= new KResidualCloudQuery.DirQueryResult();
        data.mLanguage 	= lang;
        data.mInnerData = innerData;
        data.mDirName   = dirname;

        return data;
    }

    private static KResidualRegularCloudQuery.RegularDirQueryInnerData getDirQueryInnerData(String dirname) {
        if(TextUtils.isEmpty(dirname)){
            return null;
        }
        KResidualRegularCloudQuery.RegularDirQueryInnerData result = new KResidualRegularCloudQuery.RegularDirQueryInnerData();
        result.mDirName = dirname;
        return result;
    }
}

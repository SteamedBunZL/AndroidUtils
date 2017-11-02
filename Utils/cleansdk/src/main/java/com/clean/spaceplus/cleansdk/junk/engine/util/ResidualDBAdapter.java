package com.clean.spaceplus.cleansdk.junk.engine.util;

import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudFactory;
import com.clean.spaceplus.cleansdk.junk.engine.DBColumnFilterManager;
import com.clean.spaceplus.cleansdk.junk.engine.task.RubbishFileScanTask;
import com.clean.spaceplus.cleansdk.util.StringUtils;

import java.security.MessageDigest;

import space.network.cleancloud.KPKGDefaultRegMatchPolicy;
import space.network.cleancloud.KResidualCloudQuery;
import space.network.util.hash.KQueryMd5Util;

/**
 * @author zengtao.kuang
 * @Description: ResidualDB适配器
 * @date 2016/5/12 17:29
 * @copyright TCL-MIG
 */
public class ResidualDBAdapter {

    private final KResidualCloudQuery mIkResidualCloudQuery;
    private ResidualDBAdapter() {
        mIkResidualCloudQuery = CleanCloudFactory.createResidualCloudQuery(false);
    }

    /**
     * pkg明文，dirname无sdcard前缀，有‘/’
     *
     * @param pkg
     * @param dirName
     * @return
     */
    public boolean isDirPkgExistInDetailDb(String pkg, String dirName) {
        if (TextUtils.isEmpty(pkg) || TextUtils.isEmpty(dirName)) {
            return false;
        }
        KResidualCloudQuery.DirQueryData[] result = mIkResidualCloudQuery.localQueryDirInfo(
                dirName, true, false, null);
        if (result == null || result.length == 0) {
            return false;
        }
        KPKGDefaultRegMatchPolicy policy = new KPKGDefaultRegMatchPolicy(pkg);
        MessageDigest md5 = KQueryMd5Util.getMd5Digest();
        for (KResidualCloudQuery.DirQueryData dirQueryData : result ) {
            if (dirQueryData != null && dirQueryData.mErrorCode == 0) {
                if (!isFilter(dirQueryData) && policy.match(md5, dirQueryData)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 云端过滤了吗?
     *
     * @param dirQueryData
     * @return
     */
    private boolean isFilter(KResidualCloudQuery.DirQueryData dirQueryData) {
        return DBColumnFilterManager.getInstance().isFilter(
                RubbishFileScanTask.CLEAN_CLOUD_RESIDUAL_ID_FILTER_NAME,
                String.valueOf(dirQueryData.mResult.mSignId));
    }

    private static final class Holder {
        private static final ResidualDBAdapter INST = new ResidualDBAdapter();
    }
    /**
     * @return
     */
    public static ResidualDBAdapter getInstance() {
        return Holder.INST;
    }

    public boolean queryTableByFilePathForBackups(String dirName) {
        KResidualCloudQuery.DirQueryData[] result = mIkResidualCloudQuery.localQueryDirInfo(
                dirName, true, true, mIkResidualCloudQuery.getDefaultLanguage());
        if (result == null || result.length == 0) {
            return false;
        }
        for (KResidualCloudQuery.DirQueryData dirQueryData:result) {
            if (isDirQueryDataValid(dirQueryData)) {
                KResidualCloudQuery.ShowInfo showInfo = dirQueryData.mResult.mShowInfo;

                if (showInfo == null
                        || TextUtils.isEmpty(showInfo.mAlertInfo)
                        || !StringUtils.toLowerCase(showInfo.mAlertInfo).contains("backup")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param dirQueryData
     * @return
     */
    private boolean isDirQueryDataValid(KResidualCloudQuery.DirQueryData dirQueryData) {
        return dirQueryData != null && dirQueryData.mErrorCode == 0
                && dirQueryData.mResult != null
                && dirQueryData.mResult.mQueryResult == KResidualCloudQuery.DirResultType.PKG_LIST;
    }

    /**
     * false代表云端打开旧库的使用许可,true代表关闭
     * @return
     */
    public boolean isOldSoftdetailDBDisabled() {
        boolean result = true;
        return result;
    }

}

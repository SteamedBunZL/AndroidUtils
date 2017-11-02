package com.clean.spaceplus.cleansdk.junk.cleancloud.residual;

import android.os.Environment;

import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudManager;
import com.clean.spaceplus.cleansdk.util.CleanCloudScanHelper;
import com.hawkclean.framework.log.NLog;

import space.network.cleancloud.KResidualCloudQuery;

/**
 * @author Jerry
 * @Description:
 * @date 2016/7/19 14:53
 * @copyright TCL-MIG
 */

public class KResidualCloudQueryHolder {
    public static final String TAG = KResidualCloudQueryHolder.class.getSimpleName();
    private static final KResidualCloudQueryHolder HOLDER= new KResidualCloudQueryHolder();
    private KResidualCloudQuery mIkResidualCloudQuery;

    private KResidualCloudQueryHolder() {
        NLog.d(TAG, "KResidualCloudQueryHolder private KResidualCloudQueryHolder()");
        mIkResidualCloudQuery = createIKResidualCloudQuery();

    }

    public static KResidualCloudQueryHolder getInstance(){
        return HOLDER;
    }

    public static KResidualCloudQuery createIKResidualCloudQuery() {
        NLog.d(TAG, "KResidualCloudQueryHolder createIKResidualCloudQuery");
        KResidualCloudQuery ikQuery = CleanCloudManager.createResidualCloudQuery(false);
        String lang = CleanCloudScanHelper.getCurrentLanguage();
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        ikQuery.initialize();
        ikQuery.setLanguage(lang);
        ikQuery.setSdCardRootPath(sdcardPath);
        return ikQuery;
    }



    public KResidualCloudQuery getIkResidualCloudQuery() {
        return mIkResidualCloudQuery;
    }

}

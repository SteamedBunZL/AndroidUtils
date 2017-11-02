package com.clean.spaceplus.cleansdk.junk.cleancloud.cleanhelper;

import android.os.Environment;

import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudFactory;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.ServiceConfigManager;
import com.clean.spaceplus.cleansdk.util.CleanCloudScanHelper;
import com.clean.spaceplus.cleansdk.util.bean.LanguageCountry;

import space.network.cleancloud.KResidualCloudQuery;

/**
 * @author Jerry
 * @Description:
 * @date 2016/7/4 13:54
 * @copyright TCL-MIG
 */
public class ResidualCloudQueryHolder implements ServiceConfigManager.OnLanguageCountryChangeListener {

    public static KResidualCloudQuery createIKResidualCloudQuery(boolean netQuery) {
        KResidualCloudQuery ikQuery = CleanCloudFactory.createResidualCloudQuery(netQuery);
        String lang = CleanCloudScanHelper.getCurrentLanguage();
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        ikQuery.initialize();
        ikQuery.setLanguage(lang);
        ikQuery.setSdCardRootPath(sdcardPath);
        return ikQuery;
    }


    @Override
    public void onLanguageChanged(LanguageCountry languageCountry) {
        String lang = CleanCloudScanHelper.getCurrentLanguage();
    }
}

package com.hawkclean.mig.commonframework.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.hawkclean.framework.log.NLog;

/**
 * @author Jerry
 * @Description:发版配置信息
 * @date 2016/7/7 13:46
 * @copyright TCL-MIG
 */
public class PublishVersionManager {

    //配置是否是测试环境
    private static final String DOMAIN_TEST_VALUE= "ISTEST_VALUE";
    private static final String DOMAIN_TEST = "TEST";
    private static final String DOMAIN_PRD = "PRD";


    public static final String CHANNEL_GOOGLE_PLAY_ID = "1001";
    public static final String CHANNEL_PRELOADE_ID = "";
    public static final String CHANNEL_LITEONE_CN_QQ_ID = "1009";
    public static final String CHANNEL_HAWK_ID = "1010";


    //配置国内版还是国外版 配置国内版时 需要将PRODUCT_ID的值设置为PRODUCT_ID_CN，国外版设置为PRODUCT_ID_OU
    public static final int PRODUCT_ID_CN 		= 1;	///< 国内版
    public static final int PRODUCT_ID_OU 		= 2;	///< 国际版
    public static final int PRODUCT_ID = PRODUCT_ID_CN;

    // 渠道相关配置信息
    private static String sChannel;
    private static boolean sIsTest;
    private static boolean sIsCNVersion;

    // 应用相关配置信息
    private static String sVersionName;
    private static int sVersionCode;

    //鹰眼sdk相关配置
    private static String statisticsAppKey;

    static {
        initChannel();
        initIsTest();
        initIsCNVersion();
        initVersionInfo();
        initStatisticsInfo();
    }

    private static void initChannel() {
        String channel = getMetaData(SpaceApplication.getInstance().getContext(), "SPACESDK_APP_KEY");
        sChannel = channel == null ? "" : channel;
    }

    private static void initIsTest() {
        String isTest = getMetaData(SpaceApplication.getInstance().getContext(), "ISTEST");
//        sIsTest = DOMAIN_TEST.equalsIgnoreCase(isTest) || DOMAIN_TEST_VALUE.equalsIgnoreCase(isTest);
        sIsTest = false;  // SDK 默认不是测试环境。。
    }


    private static void initIsCNVersion() {
        String internal = getMetaData(SpaceApplication.getInstance().getContext(), "SPACESDK_SERVER_DOMAIN");
        //0代表国内市场,1代表国外市场
        if("0".equalsIgnoreCase(internal)){
            sIsCNVersion=true;
        }
        else{
            sIsCNVersion=false;
        }
    }

    private static void initStatisticsInfo(){
        String appkey = getMetaData(SpaceApplication.getInstance().getContext(), "APP_KEY");
        statisticsAppKey = appkey == null ? "" : appkey;
    }

    private static void initVersionInfo() {
        Context context = SpaceApplication.getInstance().getContext();
        try {
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            sVersionName = pkgInfo.versionName;
            sVersionCode = pkgInfo.versionCode;
        } catch (Exception e) {
            sVersionName = "";
            sVersionCode = 0;
        }
    }

    /**
     * @return 是否是测试版本
     */
    public static boolean isTest() {
        return sIsTest;
    }

    /**
     * 获取渠道号
     * @return
     */
    public static String getChannelId() {
        return sChannel;
    }

    /**
     * 获取鹰眼sdk的appkey
     * @return
     */
    public static String getStatisticsKey(){
        return statisticsAppKey;
    }

    /**
     * 只区分国内 和 国际， 不区分平台
     * 判断是否是国内版
     * @return
     */
    public static boolean isCNVersion(){
        return sIsCNVersion;
    }

    /**
     * @return 返回VersionName
     */
    public static String getVersionName() {
        return sVersionName;
    }

    /**
     * 获取MetaData
     * @param context
     * @param name
     * @return
     */
    public static String getMetaData(Context context, String name) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        Object value = null;
        try {

            applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), packageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                value = applicationInfo.metaData.get(name);
            }

        } catch (Exception e) {
            NLog.printStackTrace(e);
            NLog.w("ContextUtils",
                    "Could not read the name(%s) in the manifest file.", name);
            return null;
        }

        return value == null ? "" : value.toString();
    }

}

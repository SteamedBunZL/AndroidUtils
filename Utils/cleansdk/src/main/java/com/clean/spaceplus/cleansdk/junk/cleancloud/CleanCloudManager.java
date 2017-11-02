package com.clean.spaceplus.cleansdk.junk.cleancloud;

import android.content.Context;

import com.clean.spaceplus.cleansdk.junk.engine.EmergencyFalseSignFilter;
import com.clean.spaceplus.cleansdk.junk.engine.EmergencyFalseSignManager;

import space.network.cleancloud.KCacheCloudQuery;
import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.MultiTaskTimeCalculator;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/22 16:52
 * @copyright TCL-MIG
 */
public class CleanCloudManager {
    private static Context mContext;
    public static Context getApplicationContext() {
        return mContext;
    }

    public static MultiTaskTimeCalculator createMultiTaskTimeCalculator() {
        return CleanCloudFactory.createMultiTaskTimeCalculator();
    }

    public static void setApplicationContext(Context context) {
        mContext = context;
    }

    public static EmergencyFalseSignFilter createEmergencyFalseSignFilter(int type) {
        return EmergencyFalseSignManager.getInstance().createEmergencyFalseSignFilter(type);
    }

    public static KResidualCloudQuery createResidualCloudQuery(boolean needNetQuery) {
        return CleanCloudFactory.createResidualCloudQuery(needNetQuery);
    }

///<DEAD CODE>///     public static KResidualRegularCloudQuery createRegularResidualCloudQuery(){
//        return KCleanCloudFactroy.createRegularResidualCloudQuery();
//    }

    //public static IKDescCloudQuery createDescCloudQuery() {
    //	return KCleanCloudFactroy.createDescCloudQuery();
    //}

    /**
     * 创建缓存查询实例
     * @return
     */
    public static KCacheCloudQuery createCacheCloudQuery(boolean netQuery) {
        return CleanCloudFactory.createCacheCloudQuery(netQuery);
    }

//
//    public static KPreInstalledCloudQuery createPreInstalledCloudQuery() {
//        return KCleanCloudFactroy.createPreInstalledCloudQuery();
//    }
//
//    //public static IKAppInfoCloudQuery createAppInfoCloudQuery() {
//    //	return KCleanCloudFactroy.createAppInfoCloudQuery();
//    //}
//
//    public static void startLocalSignFilePrepare() {
//        KCleanCloudSignManager.getInstance().startPrepare();
//    }
//
    public static void waitLocalSignFilePrepareComplete() {
        CleanCloudSignManager.getInstance().waitForComplete();
    }
//
//    public static void startBackgroundUpdate() {
//        KCleanCloudUpdateManager.getInstance().start();
//    }

    /*	public static void stopBackgroundUpdate() {
            KCleanCloudUpdateManager.getAppContext().stop();
        }
        */
	/*
	 * Fixes for the output of the default PRNG having low entropy.<p>
	 *
	 * http://android-developers.blogspot.hk/2013/08/some-securerandom-thoughts.html
	 *
	 * <p>The fixes need to be applied via {@link #apply()} before any use of Java
	 * Cryptography Architecture primitives. A good place to invoke them is in the
	 * application's {@code onCreate}.
	 */
/*	public static void prngFix() {
		try {
			PRNGFixes.apply();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}
		*/
//    public static void updateSignFile(Collection<String> updateFiles) {
//        KFalseFilterFactory.getFalseFilterManagerInstance().onDBUpdate(updateFiles);
//    }
//
//    public static boolean handleFalseProcPushMsg(String pushContent, String pushVersion) {
//        return KFalseFilterFactory.getFalseFilterManagerInstance().handleFalseProcPushMsg(pushContent, pushVersion);
//    }
//
//    public static CleanCloudResultReporter createResidualResultReporter() {
//        return KCleanCloudFactroy.createCleanCloudResultReporter(IKCleanCloudResultReporter.FunctionType.RESIDUAL_SCAN);
//    }
//
//    public static CleanCloudResultReporter createCacheResultReporter() {
//        return CleanCloudFactroy.createCleanCloudResultReporter(IKCleanCloudResultReporter.FunctionType.CACHE_SCAN);
//    }
//
/////<DEAD CODE>///     public static IKCleanCloudResultReporter createCleanCloudResultReporter(int type) {
////        return KCleanCloudFactroy.createCleanCloudResultReporter(type);
////    }
//
//    public static IKEmergencyFalseSignFilter createEmergencyFalseSignFilter(int type) {
//        return KEmergencyFalseSignManager.getInstance().createEmergencyFalseSignFilter(type);
//    }
//
//    public static MultiTaskTimeCalculator createMultiTaskTimeCalculator() {
//        return KCleanCloudFactroy.createMultiTaskTimeCalculator();
//    }
//
//    public static void syncUpdateEmergencyFalseSign(IKEmergencyFalseSignFilter.NotifySignUpdateData[] notifyDatas) {
//        KEmergencyFalseSignManager.getInstance().syncUpdateEmergencyFalseSign(notifyDatas);
//    }

    /**
     * 主界面打开的时候触发,智能判断是否需要联网下拉新的误报数据,
     * 并且先预先创建去误报对象(用软引用的方式持有),因为下拉数据后,消耗最大的行为已经完成
     * 可以顺便用误报数据生成一个去误报对象,方便后面扫描时获取
     *
     * 智能判断的逻辑如下
     *
     *
     * 获取去误报对象-->获取失败-->如果最近(暂定为2分钟,避免断网情况下频繁下拉)没有联网下拉过则联网下拉,否则放弃
     *                    | (获取成功)                                /|\
     *                   \|/                                          |
     *           查看误报对象加载的时间                                  |
     *                    |                                           |
     *                   \|/                                          |
     *          如果加载时间间隔太长(暂定10分钟),则重新从磁盘加载           |
     *                    |                                           |
     *                   \|/                                          |
     *         检查数据保存时间,如果数据保存时间间隔太长(暂定30分钟),则重新联网下拉
     */
    public static void notifySmartGetEmergencyFalseSign() {
//        KEmergencyFalseSignManager.getInstance().notifySmartGetEmergencyFalseSign();
    }

    /**
     * 上报扫描
     * @param nStep
     * @param isSuccess
     * @param isBackground
     */
    public static void reportCacheScan(int nStep, byte isSuccess, byte isBackground){

    }

    /**
     * Monitor the cm run state and detail.
     * section : 1: residual
     *           2: residual regular
     *           3: cache
     * */
    public static void reportState(int section, int state, String detail){

    }

}

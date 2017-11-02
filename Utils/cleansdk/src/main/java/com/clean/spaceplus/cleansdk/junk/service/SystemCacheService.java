package com.clean.spaceplus.cleansdk.junk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.base.strategy.BaseStrategy;
import com.clean.spaceplus.cleansdk.base.utils.system.SystemCacheManager;
import com.hawkclean.mig.commonframework.common.SystemUtility;

/**
 * @author shunyou.huang
 * @Description:系统缓存预加载服务
 * @date 2016/7/7 19:41
 * @copyright TCL-MIG
 */

public class SystemCacheService extends IntentService{

    private static final String TAG = SystemCacheService.class.getSimpleName();
    public static final String ACTION_PRELOAD_SYSTEM_CACHE = "action_preload_system_cache";
    private long mStartTime = 0L;
    private static final String SERVICE_NAME = "com.clean.spaceplus.cleansdk.junk.service.SystemCacheService";
    private static BaseStrategy sStrategy = null;

    public SystemCacheService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null){
            return;
        }

        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)){
            try {
                handleAction(action);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     * action分类处理
     * @param action
     */
    private void handleAction(String action){
        if (ACTION_PRELOAD_SYSTEM_CACHE.equals(action)){
            mStartTime = SystemClock.uptimeMillis();
            try {
                new SystemCacheManager().preLoadSysCacheInfo(sStrategy);
            } catch (Throwable e) {
            }
        }
    }

    /**
     * 启动系统缓存预加载服务
     * @param context
     */
    public static void startPreloadSysCache(Context context, BaseStrategy bs){
        if (SystemUtility.isServiceRunning(context, SERVICE_NAME)){
            return;
        }

        sStrategy = bs;
        Intent intent = new Intent(context, SystemCacheService.class);
        intent.setAction(ACTION_PRELOAD_SYSTEM_CACHE);
        intent.setPackage(context.getPackageName());
        try {
            context.startService(intent);
        } catch (Exception e) {
        }
    }
}

package com.clean.spaceplus.cleansdk.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.clean.spaceplus.cleansdk.base.strategy.SecularService;
import com.clean.spaceplus.cleansdk.base.utils.TUncaughtExceptionHandler;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudManager;
import com.clean.spaceplus.cleansdk.setting.authorization.AuthorizationMgr;
import com.hawkclean.framework.log.Logger;
import com.hawkclean.framework.log.NLog;
import com.hawkclean.framework.network.NetworkHelper;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.io.File;

/**
 * @author zeming_liu
 * @Description:
 * @date 2016/9/7.
 * @copyright TCL-MIG
 */
public  class SpaceApplication  {

    private long   mTimeStartApp = 0;
    private static final String SPACESDK_APP_KEY="SPACESDK_APP_KEY";
    private String spaceKey="";

    private Context mContext;

    private static class SingletonHolder {
        private static final SpaceApplication INSTANCE = new SpaceApplication();
    }

    private SpaceApplication (){}
    public static final SpaceApplication getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Context getContext()
    {
        return mContext;
    }

    public void sdkInit(Application app)
    {

        mContext=app.getApplicationContext();

        //获取接入SDK的渠道
        try {
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            Object obj=appInfo.metaData.get(SpaceApplication.SPACESDK_APP_KEY);
            if(obj!=null){
                spaceKey=String.valueOf(obj);
            }
        }catch (Exception e){
        }

        SecularService.launch(-1);
        try{
            init(app);
        }catch (Exception e){
        }
    }
    /**
     * 初始化处理
     */
    private void init(Context context) {
        //保存application实例
        CleanCloudManager.setApplicationContext(context);
        //Log输出打印
        initLog();
        //初始化NetworkHelper
        NetworkHelper.sharedHelper().registerNetworkSensor(context);
        // 功能授权管理初始化
        AuthorizationMgr.getInstance().init();
        //异常捕获初始化
        String path = mContext.getExternalFilesDir(null) + File.separator + "crash"; // Android/data/包名/files/crash
//        String path = Environment.getExternalStorageDirectory() + File.separator + "SpacePlus" + File.separator + "crash";
        TUncaughtExceptionHandler.catchUncaughtException(mContext.getApplicationContext(), path);
    }

    /**
     * LOG输出控制
     */
    private void initLog(){
        if (PublishVersionManager.isTest()) {
            NLog.setDebug(true, Logger.VERBOSE);
        }
    }


    /**
     * 获取app启动时间
     * @return
     */
    public long getAppStartTime(){
        return mTimeStartApp;
    }
}

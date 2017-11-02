package com.clean.spaceplus.cleansdk.base.strategy;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.appmgr.service.AppUsedFreqInfo;
import com.clean.spaceplus.cleansdk.appmgr.service.AppUsedInfoRecord;
import com.clean.spaceplus.cleansdk.appmgr.service.IUsedMoniterService;
import com.clean.spaceplus.cleansdk.base.bean.CheckType;
import com.clean.spaceplus.cleansdk.base.db.DatabaseHelper;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.bean.DataReportPublicBean;
import com.clean.spaceplus.cleansdk.base.utils.analytics.DataReport;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;
import com.clean.spaceplus.cleansdk.base.utils.analytics.bean.AppEvent;
import com.clean.spaceplus.cleansdk.base.utils.system.PackageManagerWrapper;
import com.clean.spaceplus.cleansdk.setting.authorization.AuthorizationMgr;
import com.clean.spaceplus.cleansdk.util.BackgroundThread;
import com.hawkclean.framework.log.NLog;
import com.hawkclean.mig.commonframework.util.ThreadMgr;
import com.upload.library.util.UploadUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author liangni
 * @Description:常驻后台服务
 * @date 2016/6/27 15:43
 * @copyright TCL-MIG
 */

public class SecularService extends Service{

    private final  static  String TAG = SecularService.class.getSimpleName();
    private BroadcastReceiver  mObserver;
    public static final String SERVICE_CHECK_TYPE = "service_check_type";


    IUsedMoniterService.Stub binder=new IUsedMoniterService.Stub(){

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public int getEldestRecordDaysToNow() throws RemoteException {

            //return UsedStatsMoniter.getInstance(SecularService.this).getEldestRecordDaysToNow();
            return 0;
        }

        @Override
        public void getReFreqList(List<AppUsedInfoRecord> records) throws RemoteException {
//            List<AppUsedInfoRecord> freqs=UsedStatsMoniter.getInstance(SecularService.this).getReFreqList();
//            if (freqs != null) {
//                records.addAll(freqs);
//            }
        }

        @Override
        public void getLastAppOpenTime(List<AppUsedFreqInfo> infos) throws RemoteException {
//            List<AppUsedFreqInfo> timeInfos = UsedStatsMoniter.getInstance(SpaceApplication.getInstance().getContext()).getLastAppOpenTime();
//            infos.addAll(timeInfos);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            dealAction(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    private void dealAction(Intent intent) throws Exception{
        if (intent == null){
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null){
            return ;
        }
        int type = bundle.getInt(SERVICE_CHECK_TYPE, 0);
        try {
            if (type == CheckType.CHECKTYPE_DIRANDAPPNAME){
                //检查上传目录和应用名
                uploadDirAndAppname();
            }
            // 都执行的内容
            // 执行功能验证
            AuthorizationMgr.getInstance().verify();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {

        unRegisterObserverReceiver();
        restartService();
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StrategyExecutor.getInstance().execItem(StrategyExecutor.StrategyFlag.SysCache);
        BackgroundThread.getHandler().post(new Runnable() {
            @Override
            public void run() {
                boolean ret = false;
                try {
                    ret = DatabaseHelper.initDBFile(SpaceApplication.getInstance().getContext());
                } catch (Exception ignore) {
                }
                if (ret) {
                    try{
                        DataReport.getInstance().init(SpaceApplication.getInstance().getContext());
                    }catch (Exception e){
                        NLog.printStackTrace(e);
                    }
                    reportInstall();
                    reportActivity();
                    reportInstallData();
                }
            }
        });
        //注册广播
        registerObserverReceiver();

    }

    private void restartService(){
        NLog.i(TAG,"SecularService restartService");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SecularService.this, SecularService.class);
                try {
                    SpaceApplication.getInstance().getContext().startService(intent);
                } catch (Exception e) {
                }
            }
        }, 500);
    }

    //后台上报安装
    private void reportInstall(){
        Runnable reportTask = new Runnable() {
            @Override
            public void run() {
                try {
                    //安装情况上报
                    DataReportFactory.DataReportInstall();
                }catch (Exception e){
                }
            }
        };
        ThreadMgr.executeLocalTask(reportTask);
    }

    //后台报活
    private void reportActivity(){
        Runnable reportTask = new Runnable() {
            @Override
            public void run() {
                try {
                    //后台报活，判断下当天是否已经上报过，没有进行上报
                    DataReportFactory.DataReportActivity();
                }catch (Exception e){
                }
            }
        };
        ThreadMgr.executeLocalTask(reportTask);
    }

    //上报安装的应用
    private void reportInstallData(){
        Runnable reportTask = new Runnable() {
            @Override
            public void run() {
                try {
                    SharedPreferences sf = SpaceApplication.getInstance().getContext().getSharedPreferences(DataReport.SHARED_NAME, Context.MODE_PRIVATE);
                    long lastTime = sf.getLong(DataReport.SHARED_INSTALL, 0L);
                    long now = System.currentTimeMillis();
                    if (now - lastTime < DataReport.INSTALL_SPACE) {
                        return;
                    }
                    List<PackageInfo> apps = PackageManagerWrapper.getInstance().getUserPkgInfoList();
                    List<Event> eventList=new LinkedList<>();
                    PackageInfo packageInfo;
                    AppEvent event;
                    for (int i = 0; i < apps.size(); i++) {
                        packageInfo = apps.get(i);

                        String type=DataReportPublicBean.APP_USER;
                        if (PackageManagerWrapper.getInstance().isSystemApp(packageInfo.packageName)) {
                            type= DataReportPublicBean.APP_SYSTEM;
                        }
                        event=new AppEvent(packageInfo.packageName,type);
                        eventList.add(event);
                    }
                    DataReportFactory.getDefaultDataReport().putEvents(eventList,true);

                    SharedPreferences.Editor editor = sf.edit();
                    editor.putLong(DataReport.SHARED_INSTALL, now);
                    editor.apply();
                }catch (Exception e){
                    NLog.printStackTrace(e);
                }
            }
        };
        ThreadMgr.executeLocalTask(reportTask);
    }

    //注册广播
    private void registerObserverReceiver() {
        mObserver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null) {
                    return;
                }
                if (Intent.ACTION_SCREEN_ON.equals(action) || Intent.ACTION_POWER_CONNECTED.equals(action)
                        || ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                    SecularService.launch(CheckType.CHECKTYPE_DIRANDAPPNAME);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mObserver, filter);
    }

    private void unRegisterObserverReceiver() {
        if (mObserver != null) {
            try {
                unregisterReceiver(mObserver);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 启动服务
     */
    public static void startService(){
        Intent intent = new Intent();
        intent.putExtra(SERVICE_CHECK_TYPE, -1);
        intent.setClass(SpaceApplication.getInstance().getContext(), SecularService.class);
        try {
            SpaceApplication.getInstance().getContext().startService(intent);
        } catch (Exception e) {
        }
    }

    /**
     * 停止服务
     */
    public static void stopService(){
        Intent intent = new Intent();
        intent.setClass(SpaceApplication.getInstance().getContext(), SecularService.class);
        SpaceApplication.getInstance().getContext().stopService(intent);
    }


    public static void launch(int checkType){
        Intent intent = new Intent();
        intent.putExtra(SERVICE_CHECK_TYPE, checkType);
        intent.setClass(SpaceApplication.getInstance().getContext(), SecularService.class);
        try {
            SpaceApplication.getInstance().getContext().startService(intent);
        } catch (Exception e) {
        }
    }

    //上传一二级目录或者应用包名
    private void uploadDirAndAppname(){
        BackgroundThread.getHandler().post(new Runnable() {
            @Override
            public void run() {
                //上传一二级目录,一周上传一次
                long uploadDirTime=SecularConfigManage.getInstance().getLastReportDirLongTime();
                if(System.currentTimeMillis()-uploadDirTime>=SecularConfigManage.CLEANSDK_SEND_DIR_TIME){
                    UploadUtil.postDir(SpaceApplication.getInstance().getContext(),getDefaultBuilder().build());
                }
            }
        });
        BackgroundThread.getHandler().post(new Runnable() {
            @Override
            public void run() {
                //上传应用包名,一周上传一次
                long uploadAppTime=SecularConfigManage.getInstance().getLastReportAppNameLongTime();
                if(System.currentTimeMillis()-uploadAppTime>=SecularConfigManage.CLEANSDK_SEND_APPNAME_TIME){
                    UploadUtil.postPackage(SpaceApplication.getInstance().getContext(),getDefaultBuilder().build());
                }
            }
        });
    }

    private OkHttpClient.Builder getDefaultBuilder(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.writeTimeout(60, TimeUnit.SECONDS);
        return builder;
    }

}

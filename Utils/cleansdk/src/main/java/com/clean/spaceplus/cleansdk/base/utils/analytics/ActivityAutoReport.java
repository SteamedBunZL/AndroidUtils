package com.clean.spaceplus.cleansdk.base.utils.analytics;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.clean.spaceplus.cleansdk.util.TimingUtil;
import com.hawkclean.framework.log.NLog;

/**
 * @author haiyang.tan
 * @Description: 自动化统计activity和app访问时长
 * @date 2016/7/5 16:21
 * @copyright TCL-MIG
 */
class ActivityAutoReport implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = ActivityAutoReport.class.getSimpleName();
    private int activityCount = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        NLog.d(TAG, activity.getClass().getSimpleName() + ": onCreate");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        NLog.d(TAG, activity.getClass().getSimpleName() + ": onStart");
        if (activityCount == 0){
            TimingUtil.start("appTime");
        }
        activityCount ++;


    }

    @Override
    public void onActivityResumed(Activity activity) {
        NLog.d(TAG, activity.getClass().getSimpleName() + ": onResume");
        TimingUtil.start("activityPage");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        NLog.d(TAG, activity.getClass().getSimpleName() + ": onPause");
        String type = getActivityType(activity);
        if (type.equals("")){
            return;
        }
        long time = TimingUtil.end("activityPage");
        //DataReport.getInstance().send(new ButtonEvent(type,String.valueOf(time)));
    }

    @Override
    public void onActivityStopped(Activity activity) {
        NLog.d(TAG, activity.getClass().getSimpleName() + ": onStop");
        activityCount--;
        checkActivity();

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        NLog.d(TAG, activity.getClass().getSimpleName() + ": onSaveInstanceState");

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        NLog.d(TAG, activity.getClass().getSimpleName() + ": onDestroy");
    }

    private void checkActivity(){
        if (activityCount > 0){
            return;
        }
        long time = TimingUtil.end("appTime");
        if (time == -1L){
            return;
        }
        //DataReport.getInstance().send(new ButtonEvent("all time",String.valueOf(time)));
    }

    private String getActivityType(Activity activity){
//        if (activity instanceof SplashActivity){
//            return "1,,-1";
//        }else if (activity instanceof MainActivity){
//            return "2,,-1";
//        }else if (activity instanceof JunkActivity){
//            return "3,,-1";
//        }else if (activity instanceof BoostActivity){
//            return "4,,-1";
//        }else if (activity instanceof AppMgrActivity){
//            return "5,,-1";
//        }else if (activity instanceof HistoryActivity){
//            return "7,2,-1";
//        }else if (activity instanceof FeedbackActivity){
//            return "6,,-1";
//        }else if (activity instanceof PWLActivity){
//            return "";
//        }else if (activity instanceof BoostAddWhiteListActivity){
//            return "";
//        }else {
//            return "";
//        }
        return "";
    }
}

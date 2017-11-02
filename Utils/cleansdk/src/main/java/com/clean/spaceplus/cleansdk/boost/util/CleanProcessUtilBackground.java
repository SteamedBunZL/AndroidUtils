package com.clean.spaceplus.cleansdk.boost.util;

import android.app.ActivityManager;
import android.content.Context;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.appmgr.appmanager.AppUsedFreqDao;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: it will kill process in background
 * @date 2016/4/6 18:46
 * @copyright TCL-MIG
 */
public class CleanProcessUtilBackground {

    private static class ProcessInformation{

        public ProcessInformation(String pkgName, long shutDownTime, boolean isKill) {
            msPackageName = pkgName;
            mnShutDownTime = shutDownTime;
            mbNeedKill = isKill;
        }
        String 		msPackageName;
        long 		mnShutDownTime;
        boolean		mbNeedKill = true;
    }

    private static CleanProcessUtilBackground sInstance = new CleanProcessUtilBackground();
    private List<ProcessInformation> mProcessInfoList = new ArrayList<ProcessInformation>();
    private Object mSyncObjForThread = new Object();
    private static ProcessCleanThread mCleanThread = null;
    private Boolean mNewListFlag = false;//true: 标示数据是新的，第一次扫描到
    private long mLastCleanTime = 0;
    private int mRunCount = 0;		// 记录执行杀的次数
    private List<Integer> mProcessIndexList  = new ArrayList<Integer>();
    //FIXME
    private boolean mBackgroundKillingSwitch = true;


    public static CleanProcessUtilBackground getInstance(){
        return sInstance;
    }

    public void addProcessModel(ProcessModel m){

        if (!mBackgroundKillingSwitch)
            return;

        if (true/*!SuExec.getInstance().checkRoot()*/) {
            synchronized (mSyncObjForThread){
                addProcessModel2List(m);
            }
        }
    }

    public Boolean isInLimitedMinutes() {

        if (!mBackgroundKillingSwitch)
            return false;

        boolean isValide = false;
        long currentTime = System.currentTimeMillis();
        if(currentTime - mLastCleanTime < getConfiguredMinutes() * 1000 * 60){
            isValide = true;
        }
        return isValide;
    }

    /**
     * 开始监控
     *
     * @param context
     */
    public static void startAppOpenWatcher(Context context) {
        //FIXME
//        Intent serviceIntent = new Intent(context, PermanentService.class);
//        context.startService(serviceIntent);
    }

    private void addProcessModel2List(ProcessModel Model){

        if (true/*!SuExec.getInstance().checkRoot()*/) {
            //add duba kind of application in my list, but wont' kill them permantly.
            if( true /*Model.isKillInBackground()*/){

                mNewListFlag = true;
                initVars();

                if (mCleanThread == null) {
                    mCleanThread = new ProcessCleanThread(mSyncObjForThread);
                    mCleanThread.start();

                    if(sInstance.mProcessInfoList.size() == 0){

                        startAppOpenWatcher(SpaceApplication.getInstance().getContext().getApplicationContext());
                    }
                }

                addPackageName(Model.getPkgName(), Model.isKillInBackground());
                mSyncObjForThread.notify();

            }
        }
    }

    private void addPackageName(String packName, boolean isNeedKillInBackground){

        String s = packName;
        Boolean bFound = false;

        bFound = false;
        for(ProcessInformation name: mProcessInfoList){

            if(name.msPackageName.equals(s)){
                bFound = true;
                name.mnShutDownTime = mLastCleanTime;
                break;
            }
        }
        if(!bFound){

            mProcessInfoList.add(new ProcessInformation(s, mLastCleanTime, isNeedKillInBackground));
        }
    }

    private long getConfiguredMinutes() {
        //FIXME
//        return CloudCfgDataWrapper.getCloudCfgLongValue(CloudCfgKey.CLOUD_SWITCH_KEY, CloudCfgKey.CLOUD_KEY, 3);
        return 1;
    }

    private void initVars() {

        mLastCleanTime = System.currentTimeMillis();
        mRunCount		= 0;
    }

    private void killOneTime() {
        String mpkgName = null;
        for(int index = 0; !mNewListFlag&&index < mProcessInfoList.size(); index++ ){

            if (mProcessInfoList.size() > index) {
                mpkgName = mProcessInfoList.get(index).msPackageName;
            }

            AppUsedFreqDao appUsedFreqDao = new AppUsedFreqDao();
            long lLastOpenTime = appUsedFreqDao.getLastLauchedTime(mpkgName);
            if(mProcessInfoList.get(index).mnShutDownTime > lLastOpenTime ){
                if( mProcessInfoList.get(index).mbNeedKill)
                    cleanProcess(mpkgName);
            }else {
                //process started by user manually, can't stopped.
//				log("not clean, cause not started by system mLastCleanTime=" + mLastCleanTime + " LauchedTime=" + getLastLauchedTime(mpkgName));
                //should remove from this list.
                mProcessIndexList.add(index);
            }
        }
        for(int k = mProcessIndexList.size()-1; k>=0; k--){
            int i = mProcessIndexList.get(k);
            mProcessInfoList.remove(i);
        }
        mProcessIndexList.clear();
    }



    private void cleanProcess(String pkgName){

        try {
            ((ActivityManager) SpaceApplication.getInstance().getContext().getSystemService(Context.ACTIVITY_SERVICE)).restartPackage(pkgName);
        } catch (Exception e) {
            // TODO: handle exception
            NLog.printStackTrace(e);
        }
    }

    private class ProcessCleanThread extends Thread{

        private Object mSyncObj = null;

        public ProcessCleanThread(Object syncObj){
            mSyncObj = syncObj;
        }

        @Override
        public void run(){

            try{
                while( isInLimitedMinutes() ){

                    synchronized (mSyncObj) {

                        killOneTime();
                        mNewListFlag = false;
                        sleepOnTime();
                    }

                }

            }catch(Exception e){
                NLog.printStackTrace(e);
            }finally{
                synchronized (mSyncObj) {
                    mCleanThread = null;
                    mProcessIndexList.clear();
                    mProcessInfoList.clear();
                }
            }
        }

        private void sleepOnTime(){

            ++mRunCount;

            try {
                int waiting = 57;
                if (mRunCount == 1) {
                    waiting = 7;
                } else if (mRunCount == 2) {
                    waiting = 27;
                }

                mSyncObj.wait(waiting * 1000);

            } catch (Exception e) {
                NLog.printStackTrace(e);
            }
        }
    }

}

package com.tcl.security.virusengine.engine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Steve on 16/9/19.
 */
public class FakeProgressEngine {


    private static final float NORMAL_SPEED = 1.0f/16000;
    private static final float HIGH_SPEED = 1.0f/10000;

    private int mTotalApkNum;

    private float mCurrentSpeed;

    private ScheduledExecutorService mPool;

    private float mCurrentPercent;

    private float mVirusRealPercent;

    private boolean isCloudScanFinished = false;

    private IFakeListener mFakeListener;



    public void setTotalApkNum(int mTotalApkNum) {
        this.mTotalApkNum = mTotalApkNum;
    }

    public void setLocalScanNum(int mLocalScanNum) {
        this.mVirusRealPercent = (float)mLocalScanNum / mTotalApkNum;
    }

    public void setCloudScanFinished(boolean cloudScanFinished) {
        isCloudScanFinished = cloudScanFinished;
    }

    public void setFakeListener(IFakeListener mFakeListener) {
        this.mFakeListener = mFakeListener;
    }

    public void startFakeProgress(){
        resetValue();

        if (mPool==null)
            mPool = Executors.newScheduledThreadPool(1);

        mPool.scheduleWithFixedDelay(new FakeTask(),0,16, TimeUnit.MILLISECONDS);
    }

    private void resetValue(){
        mCurrentPercent = 0;
        mCurrentSpeed = 0;
        mVirusRealPercent = 0;
        isCloudScanFinished = false;
    }

    public void stopFakeProgress(){
        if (mPool!=null)
            mPool.shutdownNow();
        mPool = null;
    }


    private class FakeTask implements Runnable{

        @Override
        public void run() {
            movePercent();
        }
    }


    private void movePercent(){
        float step = 16.0f * mCurrentSpeed;
        mCurrentPercent += step;

        if(mCurrentSpeed<=0.8){
            mCurrentSpeed = NORMAL_SPEED;
        }

        if (mCurrentPercent>=0.75){
            if (mVirusRealPercent <1&&!isCloudScanFinished){
                mCurrentPercent = 0.75f + 0.01f*(mVirusRealPercent *100/20);
            }else{
                mCurrentSpeed = HIGH_SPEED;
            }
        }

        if (mFakeListener!=null){
            mFakeListener.onScanOneSecond(mCurrentPercent>1.0f?1.0f:mCurrentPercent);
        }


    }


    public interface IFakeListener{

        void onScanOneSecond(float percent);

        void onScanFinish();
    }





}

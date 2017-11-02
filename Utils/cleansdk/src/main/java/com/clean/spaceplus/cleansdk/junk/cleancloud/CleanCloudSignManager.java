package com.clean.spaceplus.cleansdk.junk.cleancloud;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/23 14:39
 * @copyright TCL-MIG
 */
public class CleanCloudSignManager {
    private static CleanCloudSignManager mSignManager = null;

    public static CleanCloudSignManager getInstance(){
        if(mSignManager == null){
            synchronized (CleanCloudSignManager.class){
                if(mSignManager == null){
                    mSignManager = new CleanCloudSignManager();
                }
            }
        }

        return mSignManager;
    }

    /**
     * 开始准备
     */
    public void startPrepare(){

    }

    /**
     * 等待解析各种db文件task完成
     */
    public void waitForComplete(){

    }
}

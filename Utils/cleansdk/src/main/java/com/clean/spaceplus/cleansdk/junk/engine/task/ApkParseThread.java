package com.clean.spaceplus.cleansdk.junk.engine.task;

import com.clean.spaceplus.cleansdk.base.scan.ScanTaskCallback;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskControllerObserver;
import com.clean.spaceplus.cleansdk.junk.engine.ApkModelAssemblage;
import com.clean.spaceplus.cleansdk.junk.engine.ProgressControl;
import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.ApkParseData;
import com.clean.spaceplus.cleansdk.junk.engine.bean.GenericWhiteInfo;
import com.clean.spaceplus.cleansdk.junk.engine.util.ApkParser;
import com.clean.spaceplus.cleansdk.util.ArraySet;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author wangtianbao
 * @Description:从ApkScanTask独立出来的解析apk的线程
 * @date 2016/8/26 10:49
 * @copyright TCL-MIG
 */

public class ApkParseThread extends Thread  {
    public ApkParseThread(ApkModelAssemblage apkModelAssemblage, ScanTaskController ctrl, ScanTaskCallback cb ) {
        mApkModelAssemblage = apkModelAssemblage;
        this.mCtrl=ctrl;
        this.mCB=cb;
        mDupFilter = new ArraySet<>();
        mApkParser = new ApkParser(SpaceApplication.getInstance().getContext());
        mProgressControl = new ProgressControl(mCB, ApkScanTask.HANDLER_ADD_PROGRESS);
    }
    private ArraySet<String> mDupFilter;//重复过滤
    public ApkParser mApkParser;
    private ApkModelAssemblage mApkModelAssemblage;
    private ProgressControl mProgressControl;
    private ScanTaskController mCtrl = null;
    ScanTaskCallback mCB;
    LinkedBlockingQueue<ApkParseData> waitForParse=new LinkedBlockingQueue<>();
    ApkParseData FINISH_POISION=new ApkParseData();
    public boolean putOneApkFile(File apkFile, GenericWhiteInfo info) {
        if (!mDupFilter.contains(apkFile.getAbsolutePath())) {
            mDupFilter.add(apkFile.getAbsolutePath());
            ApkParseData data = new ApkParseData();
            data.setApkParseDataFile(apkFile);
            data.setApkParseDataWhiteInfo(info);
            waitForParse.offer(data);
        }
        return true;
    }

    /**
     * 所有的apk提供完了
     */
    public void allApkOffered(){
        waitForParse.offer(FINISH_POISION);
    }

    @Override
    public void run() {
        mApkParser.initApkParser();
        int obsIdx = -1;
        if (null != mCtrl) {
            obsIdx = mCtrl.addObserver(new ScanTaskControllerObserver() {

                @Override
                public void timeout() {
                }

                @Override
                public void stop() {
                    ApkParseThread.this.interrupt();
                }

                @Override
                public void resume() {
                }

                @Override
                public void reset() {
                }

                @Override
                public void pause(long millis) {
                }
            });
        }
        mProgressControl.setStepNum(waitForParse.size());
//        mProgressControl.startControl(mProgressBarTotal, ApkScanTask.PROG_BAR_PARSER_FILE, false);
        while (true) {
            if (null != mCtrl && mCtrl.checkStop()) {
                break;
            }
            try {
                ApkParseData apkFile= waitForParse.take();
                if(apkFile==FINISH_POISION){
                    break;
                }else{
                    if (null != mCB) {
                        mCB.callbackMessage(ApkScanTask.HANDLER_APK_DIR_SHOW, 0, 0, apkFile.getApkParseDataFile()
                                .getName());
                    }

                    APKModel apkModel = mApkParser.parseApkFile(apkFile);
                    if (null != mApkModelAssemblage) {
                        mApkModelAssemblage.putOneApkModel(apkFile.getApkParseDataFile(), apkModel);
                    }
                    mProgressControl.addStep();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
        mProgressControl.stopControl();

        if (null != mCtrl && obsIdx >= 0) {
            mCtrl.removeObserver(obsIdx);
        }
    }
}

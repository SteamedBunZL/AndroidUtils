package com.clean.spaceplus.cleansdk.boost.engine.scan;

import android.content.Context;

import com.clean.spaceplus.cleansdk.BuildConfig;
import com.clean.spaceplus.cleansdk.boost.engine.BoostEngine;
import com.clean.spaceplus.cleansdk.boost.engine.data.BoostDataManager;
import com.clean.spaceplus.cleansdk.boost.engine.data.BoostResult;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessHelper;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessResult;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessScanSetting;
import com.hawkclean.framework.log.NLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: boost扫描引擎
 * @date 2016/4/5 20:25
 * @copyright TCL-MIG
 */
public class BoostScanEngine {
    private static final boolean IS_DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "BoostScanEngine";
    private List<BoostScanTask> mTasks = new ArrayList<BoostScanTask>();
    private ScanEngineCallback mCallback = null;
    private Context mContext = null;
    private BoostDataManager mDataManager;


    public BoostScanEngine(Context ctx, BoostScanSetting setting) {
        mContext = ctx;
        prepareTasks(setting);
        mDataManager = BoostDataManager.getInstance();
    }

    public void scan(ScanEngineCallback callback) {
        mCallback = callback;
        ScanThread scanThread = new ScanThread();
        scanThread.setName("BoostScanEngine scan");
        scanThread.start();

    }

    public void setScanEngineCallback(ScanEngineCallback callback){
        mCallback = callback;
    }

    public BoostResult scanSync(int type) {
        BoostScanTask task = null;
        for (BoostScanTask t : mTasks) {
            if (t != null && t.getType() == type) {
                task = t;
                break;
            }
        }

        if (task != null) {
            return task.scanSync();
        } else {
            return null;
        }
    }

    private void prepareTasks(BoostScanSetting setting) {
        if ((setting.mTaskType & BoostEngine.BOOST_TASK_MEM) != 0) {
            Object obj = setting.mSettings.get(BoostEngine.BOOST_TASK_MEM);
            ProcessScanSetting procSetting;
            if (obj != null && obj instanceof ProcessScanSetting) {
                procSetting = (ProcessScanSetting)obj;
            } else {
                procSetting = new ProcessScanSetting();
            }
            ProcessScanTask task = new ProcessScanTask(mContext, procSetting);
            mTasks.add(task);
        }

    }

    private class ScanThread extends Thread {
        @Override
        public void run() {
            for (final BoostScanTask task : mTasks) {
                if (task.isUseDataManager()) {
                    if (mDataManager.isScanning(task.getType())) {

                        if (IS_DEBUG) {
                            NLog.d(TAG, "scantype:" + task.getType() + " is scanning");
                        }

                        // task in scanning, we register data change callback here
                        mDataManager.registerCallback(task.getType(), new BoostDataManager.DataUpdateCallback() {
                            @Override
                            public void onDataUpdate(Object data) {
                                final ScanEngineCallback callback = mCallback;
                                if(callback!=null){
                                    callback.onScanStart(task.getType());
                                    callback.onScanPreFinish(task.getType(), data);
                                    callback.onScanFinish(task.getType(), data);
                                }
                            }
                        });
                        continue;
                    }else if(ProcessHelper.isCleanProtect()){
                        final ScanEngineCallback callback = mCallback;
                        if(callback!=null){
                            callback.onScanStart(task.getType());
                            callback.onScanPreFinish(task.getType(), null);
                            callback.onScanFinish(task.getType(), null);
                        }
                        continue;
                    }
                    else if (mDataManager.isDataValid(task.getType())) {

                        if (IS_DEBUG) {
                            NLog.d(TAG, "scantype:" + task.getType() + " data valid");
                        }
                        try{
                            if (ProcessHelper.isCleanProtect()) {
                                ProcessResult processResult = (ProcessResult) mDataManager.getResult(task.getType());
                                boolean change = false;
                                if(processResult!=null){
                                    List<ProcessModel> processModelList =processResult.getData();
                                    if(processModelList!=null){
                                        for (int i = processModelList.size() - 1; i >=0; i--) {
                                            ProcessModel processModel = processModelList.get(i);
                                            if(processModel.isChecked()){
                                                processModelList.remove(i);
                                                change = true;
                                            }
                                        }
                                        if(change){
                                            mDataManager.updateResult(task.getType(),processResult);
                                        }
                                    }
                                }
                            }
                        }catch (Exception e){

                        }

                        // task result is valid, callback result here
                        final ScanEngineCallback callback = mCallback;
                        if(callback!=null){
                            callback.onScanStart(task.getType());
                            callback.onScanPreFinish(task.getType(), mDataManager.getResult(task.getType()));
                            callback.onScanFinish(task.getType(), mDataManager.getResult(task.getType()));
                        }

                        continue;
                    } else {
                        // otherwise, we need to scan this task
                        mDataManager.setScan(task.getType(), true);
                    }
                }

                if (IS_DEBUG) {
                    NLog.d(TAG, "scantype:" + task.getType() + " need scan!!");
                }

                task.scan(new BoostScanTask.IScanTaskCallback() {
                    @Override
                    public void onScanStart() {
                        final ScanEngineCallback callback = mCallback;
                        if(callback!=null){
                            callback.onScanStart(task.getType());
                        }
                    }

                    @Override
                    public void onScanProgress(Object data) {
                        final ScanEngineCallback callback = mCallback;
                        if(callback!=null){
                            callback.onScanProgress(task.getType(), data);
                        }
                    }

                    @Override
                    public void onScanPreFinish(Object results) {
                        final ScanEngineCallback callback = mCallback;
                        if(callback!=null){
                            callback.onScanPreFinish(task.getType(), results);
                        }
                    }

                    @Override
                    public void onScanFinish(Object results) {
                        final ScanEngineCallback callback = mCallback;
                        if(callback!=null){
                            callback.onScanFinish(task.getType(), results);
                        }
                    }
                });
            }
        }
    }

    /*
     * Scan engine callback interface
     */
    public interface ScanEngineCallback {
        void onScanStart(int type);
        void onScanProgress(int type, Object data);
        void onScanPreFinish(int type, Object results);
        void onScanFinish(int type, Object results);
    }


    public static class DefaultScanEngineCallbackImpl implements ScanEngineCallback{
        @Override
        public void onScanFinish(int type, Object results) {

        }

        @Override
        public void onScanStart(int type) {

        }

        @Override
        public void onScanProgress(int type, Object data) {

        }

        @Override
        public void onScanPreFinish(int type, Object results) {

        }
    }
}

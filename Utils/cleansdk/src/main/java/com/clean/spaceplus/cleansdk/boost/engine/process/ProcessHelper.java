package com.clean.spaceplus.cleansdk.boost.engine.process;

import android.content.Context;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.boost.BoostConfigManager;
import com.clean.spaceplus.cleansdk.boost.engine.BoostEngine;
import com.clean.spaceplus.cleansdk.boost.engine.data.BoostDataManager;
import com.clean.spaceplus.cleansdk.boost.engine.data.BoostResult;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.hawkclean.framework.log.NLog;

import java.util.List;

/**
 * @author zengtao.kuang
 * @Description:进程相关辅助类
 * @date 2016/4/6 10:26
 * @copyright TCL-MIG
 */
public class ProcessHelper {
    public static final String TAG = ProcessHelper.class.getSimpleName();
    private static final String BOOST_LAST_CLEAN_TIME = "boost_last_clean_time";
    private static final String BOOST_LAST_SCAN_TIME = "boost_last_scan_time";
    private static final String BOOST_CLEAN_ALL = "boost_clean_all";
    private static final int CLEAN_PROTECT_DURATION = 80 * 1000;    //
    private static final int RESCAN_DURATION = 80 * 1000;           //

    public static void postCleanHandler(ProcessResult result) {
        Context context = SpaceApplication.getInstance().getContext();

        long curTime = System.currentTimeMillis();
        BoostConfigManager.getInstanse(context)
                .setLongValue(BOOST_LAST_CLEAN_TIME, curTime);

        if (result != null) {
//            MemoryInfo mem = MemoryInfo.newInstance(mResult.mTotalAvailMem);
//
//            long endTime = curTime + CLEAN_PROTECT_DURATION;
//            ProcessNotifierUtil.notifyWidgetFlush(mem.usedSize, endTime);
//            ProcessNotifierUtil.notifyFloatFlush(mem.getPercent(), endTime);
//            ProcessNotifierUtil.notifyNotificationMemoryFlush();
        }

        NLog.d(TAG, "postCleanHandler:" + curTime);
    }

    public static void resetLastScanCleanTime() {
        Context context = SpaceApplication.getInstance().getContext();
        BoostConfigManager.getInstanse(context)
                .setLongValue(BOOST_LAST_CLEAN_TIME, 0);
        BoostConfigManager.getInstanse(context)
                .setLongValue(BOOST_LAST_SCAN_TIME, 0);

        NLog.d(TAG, "resetLastScanCleanTime");
    }

    public static boolean isCleanProtect() {
        Context context = SpaceApplication.getInstance().getContext();

        long curTime = System.currentTimeMillis();
        long lastCleanTime = BoostConfigManager.getInstanse(context)
                .getLongValue(BOOST_LAST_CLEAN_TIME, 0);

        NLog.d(TAG, "isCleanProtect:" + ((curTime - lastCleanTime) < CLEAN_PROTECT_DURATION));

        long dur = curTime - lastCleanTime;
        return ((dur<CLEAN_PROTECT_DURATION)&&dur>0);
    }

    public static void updateLastScanTime() {
        Context context = SpaceApplication.getInstance().getContext();

        long curTime = System.currentTimeMillis();
        BoostConfigManager.getInstanse(context)
                .setLongValue(BOOST_LAST_SCAN_TIME, curTime);

        NLog.d(TAG, "updateLastScanTime:" + curTime);
    }

    public static void updateLastCleanAllFlag(boolean boostAll) {
        Context context = SpaceApplication.getInstance().getContext();

        BoostConfigManager.getInstanse(context)
                .setBooleanValue(BOOST_CLEAN_ALL, boostAll);

        NLog.d(TAG, "updateLastCleanAllFlag:" + boostAll);
    }

    public static boolean isLastCleanAllFlag(){
        Context context = SpaceApplication.getInstance().getContext();
        return BoostConfigManager.getInstanse(context).getBooleanValue(BOOST_CLEAN_ALL,false);
    }

    public static boolean isScanDataVaild() {
        Context context = SpaceApplication.getInstance().getContext();

        long curTime = System.currentTimeMillis();
        long lastScanTime = BoostConfigManager.getInstanse(context)
                .getLongValue(BOOST_LAST_SCAN_TIME, 0);

        long duration = curTime - lastScanTime;
        boolean bScanDataValid = ((curTime - lastScanTime) < RESCAN_DURATION)&&duration>0;
        if (bScanDataValid) {
            ///< 缓存有效，继续判断清理时间和扫描时间的先后
            long lastCleanTime = BoostConfigManager.getInstanse(context).getLongValue(
                    BOOST_LAST_CLEAN_TIME, 0);
            if (lastCleanTime != 0 && lastCleanTime > lastScanTime) {
                bScanDataValid = false;
            }
        }

        NLog.d(TAG, "isScanDataVaild:" + bScanDataValid);

        return bScanDataValid;
    }

    public static boolean isProcessInCache() {
        Context context = SpaceApplication.getInstance().getContext();
        long curTime = System.currentTimeMillis();
        long lastScanTime = BoostConfigManager.getInstanse(context)
                .getLongValue(BOOST_LAST_SCAN_TIME, 0);
        long lastCleanTime = BoostConfigManager.getInstanse(context)
                .getLongValue(BOOST_LAST_CLEAN_TIME, 0);

        boolean isInScanTime = ((curTime - lastScanTime) < RESCAN_DURATION);
        boolean isInCleanTime = ((curTime - lastCleanTime) < CLEAN_PROTECT_DURATION);

        return (isInScanTime || isInCleanTime);
    }

    /**
     * 判断当前内存是否有可清理项
     * @return
     */
    public static boolean isHasProcessToClean(){

        if(isScanDataVaild()){
            //当前处于缓存期限内，不需要扫描内存信息，可以直接去缓存中的数据
            BoostResult result = BoostDataManager.getInstance().getResult(BoostEngine.BOOST_TASK_MEM);
            if(result != null && result instanceof  ProcessResult){
                ProcessResult pResult = (ProcessResult)result;
                List<ProcessModel> pList =  pResult.getData();
                if(pList != null && pList.size() > 0){
                    for (ProcessModel model:pList){
                        if(model != null && model.isChecked()){
                            //只有有一个勾选项，即认为有可清理的信息，立即范围结果
                            return true;
                        }
                    }
                }
            }
        }else{
            //如果不在缓存期间内，则默认为有可清理的内存信息
            return true;
        }
        return false;
    }
}

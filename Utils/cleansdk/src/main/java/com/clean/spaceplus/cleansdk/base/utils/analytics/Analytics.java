package com.clean.spaceplus.cleansdk.base.utils.analytics;

import com.clean.spaceplus.cleansdk.boost.engine.scan.BoostScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.AdvFolderScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.ApkScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.RubbishFileScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.SdCardCacheScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.SysCacheScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.SysFixedFileScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.ThumbnailScanTask;
import com.clean.spaceplus.cleansdk.util.TimingUtil;
import com.hawkclean.framework.log.NLog;
import java.text.DecimalFormatSymbols;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/6/20 17:10
 * @copyright TCL-MIG
 */
public class Analytics {

    private static final String TAG = Analytics.class.getSimpleName();


    public static final String[] TASKLIST = {"RubbishFileScanTask",
            "AdvFolderScanTask", "ThumbnailScanTask", "ApkScanTask",
            "SysFixedFileScanTask", "SdCardCacheScanTask",
            "SysCacheScanTask", "BoostScanTask"};

    public static final String ANALYTICS_TASK = "引擎任务耗时";
    public static final String ANALYTICS_DB = "任务数据库耗时";

    private List<AtomicLong> dbTimeList;
    private List<AtomicLong> taskTimeList;

    private static volatile Analytics _instance;
    static DecimalFormat mSizeFormat;
    static {
        mSizeFormat= new DecimalFormat("#.00",new DecimalFormatSymbols(Locale.ENGLISH));
    }

    private Analytics(){
        dbTimeList = Collections.synchronizedList(new ArrayList<>(Collections.nCopies(TASKLIST.length,new AtomicLong(0L))));
        taskTimeList = Collections.synchronizedList(new ArrayList<>(Collections.nCopies(TASKLIST.length,new AtomicLong(0L))));
    }

    public static Analytics getInstance() {
        if (_instance == null) {
            synchronized (Analytics.class) {
                if (_instance == null) {
                    _instance = new Analytics();
                }
            }
        }
        return _instance;
    }

    public void addDBTime(long num, int type){
        AtomicLong dbTime = dbTimeList.get(type);
        long temp = dbTime.get();
        dbTimeList.set(type, new AtomicLong(temp + num));
    }

    public long getDBTime(int type){
        AtomicLong result = dbTimeList.get(type);
        return result.get();
    }

    public void setTaskTime(long num, int type) {
        AtomicLong dbTime = taskTimeList.get(type);
        long temp = dbTime.get();
        taskTimeList.set(type, new AtomicLong(temp + num));
    }

    public long getTaskTime(int type) {
        AtomicLong result = taskTimeList.get(type);
        return result.get();
    }

    /**
     * 获取扫描任务时间
     * @param mark
     * @return
     */
    public long getTaskTime(Class mark){
        String name = mark.getName();
        return Analytics.getInstance().getTaskTime(Analytics.getTask(name));
    }

    public void reset(){
        dbTimeList = Collections.synchronizedList(new ArrayList<>(Collections.nCopies(TASKLIST.length,new AtomicLong(0L))));
        taskTimeList = Collections.synchronizedList(new ArrayList<>(Collections.nCopies(TASKLIST.length,new AtomicLong(0L))));
    }

    /**
     * 获得任务的编号
     * @return
     */
    public int getDBTask(){
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if(stackElements != null) {
            for (int i = 0; i < stackElements.length; i++){
                String name = stackElements[i].getClassName();
                int taskNum = getTask(name);
                if ( taskNum != -1){
                    return taskNum;
                }
                NLog.d(TAG, stackElements[i].getClassName());
            }
        }
        return -1;
    }

    public static int getTask(String name){
        if (name.equals(RubbishFileScanTask.class.getName())){
            return 0;
        } else if (name.equals(AdvFolderScanTask.class.getName())){
            return 1;
        }else if (name.equals(ThumbnailScanTask.class.getName())){
            return 2;
        }else if (name.equals(ApkScanTask.class.getName())){
            return 3;
        }else if (name.equals(SysFixedFileScanTask.class.getName())){
            return 4;
        }else if (name.equals(SdCardCacheScanTask.class.getName())){
            return 5;
        }else if (name.equals(SysCacheScanTask.class.getName())){
            return 6;
        }else if (name.equals(BoostScanTask.class.getName())){
            return 7;
        }else {
            return -1;
        }
    }

    public static void endTask(Class mark){
        String name = mark.getName();
        Analytics.getInstance().setTaskTime(TimingUtil.end(name), Analytics.getTask(name));
    }

    /**
     * 返回毫秒计时
     * @param time
     * @return
     */
    public static String formatTimeSize(long time){
        String ret = "";
        try {
            if(time<=0) return "0";
            ret = mSizeFormat.format((double) time / 1000000);
        }catch (Exception e){
            NLog.printStackTrace(e);
        }
        return ret;
    }

    public static void endDBUtil(long start){
        long end = System.nanoTime();
        Analytics analytics = Analytics.getInstance();
        int type = analytics.getDBTask();
        do{
            if (type == -1){
                break;
            }
            analytics.addDBTime(end - start, type);
        }while (false);
    }
}

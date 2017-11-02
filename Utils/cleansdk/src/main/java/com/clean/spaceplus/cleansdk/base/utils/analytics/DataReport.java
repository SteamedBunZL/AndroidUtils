package com.clean.spaceplus.cleansdk.base.utils.analytics;

import android.app.Application;
import android.content.Context;

import com.hawkclean.framework.network.NetworkHelper;

import java.util.List;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/7/4 17:14
 * @copyright TCL-MIG
 */
public class DataReport {

    public static final String SHARED_NAME = "DATAREOPRT";
    public static final String SHARED_INSTALL = "INSTALL";
    public static final long INSTALL_SPACE = 7 * 24 * 60 * 60 * 1000; //暂定1周

    private static final String TAG = DataReport.class.getSimpleName();

    private ReportExecutor executor;

    //本地化接口
    private EventDao mEventDao;

    private static final long REPORT_SPACE = 5000L;
    //策略
//    private List<DataStrategy> mStrategies;
    private static volatile DataReport _instance;

    /**
     * 在Application中初始化
     * @param context
     */
    public void init(Context context){
        try{
            Application application = (Application) context;
            application.registerActivityLifecycleCallbacks(new ActivityAutoReport());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private DataReport(){
        mEventDao = new DefaultEventDaoImpl();
        executor = new ReportExecutor(mEventDao, new TimeStrategy() {
            @Override
            public long getReportSpace() {
                return REPORT_SPACE;
            }
        });

    }

    public static DataReport getInstance(){
        if (_instance == null) {
            synchronized (DataReport.class) {
                if (_instance == null) {
                    _instance = new DataReport();
                }
            }
        }
        return _instance;
    }

    public synchronized void send(Event event){
        executor.add(event);
    }

    public synchronized void batchSend(List<Event> event,boolean isAllReport){
        executor.addBatch(event,isAllReport);
    }

    public void destory(){
        executor.destory();
    }

    public synchronized void setEventDao(EventDao eventDao){
        mEventDao = eventDao;
    }

    private boolean isNetWork(){
        return NetworkHelper.sharedHelper().isNetworkAvailable();
    }
}

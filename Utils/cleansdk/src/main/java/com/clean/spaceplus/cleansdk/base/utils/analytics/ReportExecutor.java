package com.clean.spaceplus.cleansdk.base.utils.analytics;

import android.os.Handler;
import android.os.Message;

import com.hawkclean.framework.log.NLog;
import com.hawkclean.framework.network.NetworkHelper;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;
import com.hawkclean.mig.commonframework.util.ThreadMgr;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/7/5 10:28
 * @copyright TCL-MIG
 */
public class ReportExecutor {

    private static final String TAG = ReportExecutor.class.getSimpleName();
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>();
    private static final int MAX_REPORT_NUM = 20;

    //本地化接口
    private EventDao mEventDao;

    private EventStrategy mEventStrategy = new EventStrategy() {
        @Override
        public int getMaxReport() {
            if (PublishVersionManager.isTest()){
                return 10;
            }
            return MAX_REPORT_NUM;
        }
    };

    //上报时间间隔
    private TimeStrategy mStrategy;
    //网络监听
    private NetworkHelper.NetworkInductor mInductor = new NetworkHelper.NetworkInductor() {
        @Override
        public void onNetworkChanged(final NetworkHelper.NetworkStatus networkStatus) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    NLog.d(TAG, networkStatus.toString());
                    if (networkStatus == NetworkHelper.NetworkStatus.NetworkNotReachable){
                        List<EventWrap> events = null;
                        try {
                            events = (List<EventWrap>) drainQueue();
                        }catch (Exception e){
                            NLog.printStackTrace(e);
                        }
                        if (events == null){
                            return;
                        }
                        saveTolocal(events);
                    }else if (networkStatus == NetworkHelper.NetworkStatus.NetworkReachableViaWiFi){
                        EventWrap local = getToLocal();
                        if (local != null) {
                            execute(local);
                        }
                    }else if (networkStatus == NetworkHelper.NetworkStatus.NetworkReachableViaWWAN){
                        EventWrap local = getToLocal();
                        if (local != null) {
                            execute(local);
                        }
                    }
                }
            };
            ThreadMgr.executeLocalTask(task);
        }
    };


    public ReportExecutor(EventDao eventDao){
        NetworkHelper.sharedHelper().addNetworkInductor(mInductor);
        this.mEventDao = eventDao;
    }

    public ReportExecutor(EventDao eventDao, TimeStrategy strategy){
        this(eventDao);
        this.mStrategy = strategy;
    }

    /**
     * 实时上报
     * @param event
     */
    public void add(final Event event){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (hasNetwork()) {
                    EventWrapImpl eventWrapImpl = new EventWrapImpl(event.parseToJSONObject());
                    execute(eventWrapImpl);
                }else {
                    mEventDao.putEvent(event.parseToJSONObject());
                }
            }
        };
        ThreadMgr.executeLocalTask(task);
    }

    /**
     * 几个事件放一起上报
     * @param event
     */
    public void addBatch(final List<Event> event,final boolean isAllReport){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try{
                    if(event==null){
                        return;
                    }
                    if (hasNetwork()) {
                        List<JSONObject> jsonList=new LinkedList<>();
                        for(Event e:event){
                            jsonList.add(e.parseToJSONObject());
                        }
                        EventWrapImpl eventWrapImpl = new EventWrapImpl(jsonList);
                        //判断是否需要一起上报
                        if(isAllReport){
                            ThreadMgr.executeNetworkTask(eventWrapImpl);
                        }
                        else{
                            execute(eventWrapImpl);
                        }
                    }else {
                        for(Event e:event){
                            mEventDao.putEvent(e.parseToJSONObject());
                        }
                    }
                }catch (Exception e){

                }

            }
        };
        ThreadMgr.executeLocalTask(task);
    }

    private volatile boolean mTimeTaskStatus = false;

    private final Timer mTimer = new Timer();

    //根据时间策略上报
    public void push(final Event event){
        if (mStrategy == null) {
            NLog.e(TAG, "DateStrategy is null !!!!");
            return;
        }

        Runnable task = new Runnable() {
            @Override
            public void run() {
                mEventDao.putEvent(event.parseToJSONObject());
                //先判断有没有网络，如果没有网络，不开启定时任务
                if (hasNetwork()) {
                    if (!mTimeTaskStatus) {
                        mTimeTaskStatus = true;
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            }
                        }, mStrategy.getReportSpace());
                    }
                }
            }
        };
        ThreadMgr.executeLocalTask(task);
    }

    private static class MyHandler extends Handler{

        private WeakReference<ReportExecutor> mRef;

        public MyHandler(ReportExecutor executor){
            mRef = new WeakReference<>(executor);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final ReportExecutor executor = mRef.get();
            if (executor != null) {
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        NLog.d(TAG, "run Task");
                        EventWrap local = executor.getToLocal();
                        if (local != null) {
                            executor.execute(local);
                        }
                        executor.mTimeTaskStatus = false;
                    }
                };
                ThreadMgr.executeLocalTask(task);
            }
        }
    }

    private MyHandler handler = new MyHandler(this);

    void destory(){
        NetworkHelper.sharedHelper().removeNetworkInductor(mInductor);
//        List<EventWrap> events = null;
//        try {
//            events = (List)executors.shutdownNow();
//        }catch (Exception e){
//            NLog.printStackTrace(e);
//        }
//        saveTolocal(events);
    }

    private List drainQueue() {
        BlockingQueue<Runnable> q = sPoolWorkQueue;
        ArrayList<Runnable> taskList = new ArrayList<Runnable>();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            for (Runnable r : q.toArray(new Runnable[0])) {
                if (q.remove(r))
                    taskList.add(r);
            }
        }
        return taskList;
    }

    private void saveTolocal(List<EventWrap> eventWraps){
        if (mEventDao == null){
            NLog.e(TAG,"EventDao is not Setting");
            return;
        }
        if (eventWraps == null || eventWraps.isEmpty()){
            return;
        }
        List<JSONObject> events = new LinkedList<>();
        List<JSONObject> temps = null;
        for (int i = 0; i < eventWraps.size(); i++){
            temps = eventWraps.get(i).mEvents;
            if(temps != null && !temps.isEmpty()){
                events.addAll(temps);
            }
        }
        mEventDao.putEvents(events);
    }

    private EventWrap getToLocal(){
        if (mEventDao == null){
            NLog.e(TAG,"EventDao is not Setting");
            return null;
        }
        List<JSONObject> ret = mEventDao.getEvents();
        return new EventWrapImpl(ret);
    }

    /**
     * 分条上传统计
     * @param eventWrap
     */
    private void execute(EventWrap eventWrap){
        List<JSONObject> events = eventWrap.mEvents;
        if (events == null || events.isEmpty()){
            return;
        }
        int size = events.size();
        int max = mEventStrategy.getMaxReport();
        if (size > mEventStrategy.getMaxReport()){
            int temp = size / max;
            for (int i = 0; i < temp + 1; i++ ){
                List<JSONObject> tempEvents = new LinkedList<>();
                for (int j = max * i; j < max * (i + 1) && j < size; j++){
                    tempEvents.add(events.get(j));
                }
                EventWrap tempEW = new EventWrapImpl(tempEvents);
                ThreadMgr.executeNetworkTask(tempEW);
            }
        }else {
            ThreadMgr.executeNetworkTask(eventWrap);
        }
    }

    private boolean hasNetwork(){
        return NetworkHelper.sharedHelper().isNetworkAvailable();
    }
}

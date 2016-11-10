package tlog.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import tlog.IReportCallback;
import tlog.TLogError;
import tlog.TLogStrategy;
import tlog.nlog.NLog;


/**
 * Created by hui.zhu on 2016/4/27.
 */
public class TLogManager {
    private static final String APP_SEQ_ID = "APP_INFO";
    private static final String TAG = "TLogManager";
    public final static String COMPRESS_DATA = "true";
    private final static int MAX_BATCH_LOG = 100;
    private static TLogManager _instance = null;
    private final CacheControl mCacheControl;
    private TLogStrategy mTLogStrategy = null;
    private RuntimeTask mRuntimeTask;
    private Handler mHandler = null;
    private ArrayList<TLogInfo> mWaitingQueue;
    private final String mAppKey;
    private final String mDefaultStr;
    private final JSONObject mUuid;

    private TLogManager(Context context, String offlinePath, String appKey, String defaultStr,JSONObject uuid) {
        mAppKey = appKey;
        mDefaultStr = defaultStr;
        mUuid = uuid;
        mCacheControl = new CacheControl(context, offlinePath, mAppKey,defaultStr);
        mTLogStrategy = TLogStrategy.getDefoultTLogStrategy();
        mWaitingQueue = new ArrayList<>();
        this.mHandler = new TLogHandler(this, Looper.getMainLooper());
    }

    /**
     * 初始化 （使用默认配置path）
     * @param context
     * @return
     */
    public static TLogManager initInstance(Context context, String appKey,String defaultStr, JSONObject uuid) {
        return initInstance(context, "", appKey, defaultStr,uuid);
    }

    /**
     * 初始化： 配置离线地址
     * @param context
     * @param offlinePath
     * @return
     */
    public static TLogManager initInstance(Context context, String offlinePath, String appKey, String defaultStr,JSONObject uuid) {
        if (_instance == null) {
            _instance = new TLogManager(context, offlinePath, appKey,defaultStr,uuid);
        }

        _instance.reportLog(uuid.toString(), _instance.getTLogStrategy());
        return _instance;
    }

    public static TLogManager defaultManager() {
        return _instance;
    }

    public void setStrategy(TLogStrategy tLogStrategy){
        this.mTLogStrategy = tLogStrategy;
    }

    private TLogStrategy getTLogStrategy(){
        return mTLogStrategy;
    }

    /**
     * 添加离线日志
     * @param jolog
     */
    public void addToCache(JSONObject jolog) {
        try{
            if (!jolog.has(APP_SEQ_ID)) {
                jolog.put(APP_SEQ_ID, mUuid);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        String log = jolog.toString();
        NLog.v(TAG, "TLog addToCache log: %s ", log);
        // TODO 这里是debug模式的话就不写入离线文件
//        if(Constant.DEBUG || TextUtils.isEmpty(log)){
//            return;
//        }
        mCacheControl.println(log);
        reportCache();
    }

    void addToCache(String log) {
        NLog.v(TAG, "TLog addToCache log: %s ", log);
        // TODO 这里是debug模式的话就不写入离线文件
//        if(Constant.DEBUG || TextUtils.isEmpty(log)){
//            return;
//        }
        mCacheControl.println(log);
    }

    /**
     * 启动离线上报
     */
    public void reportCache(){
        // TODO 没有网络就不进行离线上报
//        if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
//            NLog.w(TAG, "TLog network unavailable");
//            return;
//        }

        if (!mCacheControl.needReport()) {
            NLog.w(TAG, "TLog Report too frequently");
            return;
        }

        if (mCacheControl.isReporting()) {
            NLog.w(TAG, "TLog mReporting");
            return;
        }

        if(!mCacheControl.creatTmpFile()){
            return;
        }

        CacheTask cacheTask = mCacheControl.createCacheLogTask();
        if (cacheTask !=null) {
            mCacheControl.setReporting(true);
            cacheTask.execute();
        }
    }


    /**
     *  report log
     * @param jolog
     */
    public void reportLog(JSONObject jolog){
        //try{
        //    if (!jolog.has(APP_SEQ_ID)) {
        //        jolog.put(APP_SEQ_ID, mUuid);
        //    }
        //}catch (JSONException e){
        //    e.printStackTrace();
        //}
        reportLog(jolog.toString(), getTLogStrategy());
        reportCache();
    }

    public void reportLog(String log, TLogStrategy tLogStrategy){
        enqueue(new TLogInfo(log, tLogStrategy));
    }

    private boolean isCurrentIoThread() {
        return (Thread.currentThread() == mHandler.getLooper().getThread());
    }

    void enqueue(TLogInfo tLogInfo) {

        if (!isCurrentIoThread()) {
            Message msg = mHandler.obtainMessage(MSG_QUEUE, tLogInfo);
            msg.sendToTarget();
            return;
        }

        boolean running = false;
        final RuntimeTask task = mRuntimeTask;
        if (task != null) {
            running = true;
        }

        mWaitingQueue.add(tLogInfo);

        if (!running) {
            start();
        }
    }


    private boolean mAllowSchedule = true;

    void start() {
        mAllowSchedule = true;
        postSchedule();
    }

    private void postSchedule() {
        if (!mAllowSchedule)
            return;

        mHandler.removeMessages(MSG_SCHEDULE);
        mHandler.sendEmptyMessageDelayed(MSG_SCHEDULE, 100);
    }

    private List<TLogInfo> nextBundle() {
        int size = Math.min(MAX_BATCH_LOG, mWaitingQueue.size());
        List<TLogInfo> results = mWaitingQueue.subList(0, size);
        List<TLogInfo> r = new ArrayList<>(results);
        results.clear();
        return r;
    }

    private void schedule() {
        NLog.v(TAG, "runtime schedule");
        if (mRuntimeTask != null || mWaitingQueue.size() == 0)
            return;
        NLog.v(TAG, "Tlog schedule mWaitingQueue  size: %s ", mWaitingQueue.size());
        List<TLogInfo> runningList = nextBundle();
        if (runningList == null || runningList.isEmpty()) {
            NLog.w(TAG, "Tlog schedule runningList is null ");
            return;
        }
        NLog.v(TAG, "Tlog schedule nextBundle ,mWaitingQueue size: %s ", mWaitingQueue.size());
        RuntimeTask task = new RuntimeTask(mCallback, runningList, mAppKey,mDefaultStr);
        mRuntimeTask = task;
        task.execute();
    }

    void stopAll(int cause) {
        NLog.v(TAG, "Tlog  stopAll cause= %d", cause);
        mWaitingQueue.clear();
        if (mRuntimeTask != null) {
//            mRuntimeTask.stop();
            mRuntimeTask = null;
        }
    }

    void onComplete( int err) {

        if (isCurrentIoThread()) {

            if (mRuntimeTask != null ) {
                if(err != TLogError.SUCCESS){
                    addToCache(mRuntimeTask.getReportLog());
                }
                mRuntimeTask = null;
            }

            // 进入下一次调度
            postSchedule();
        } else {
            Message m = mHandler.obtainMessage(MSG_COMPLETE, err, 0);
            m.sendToTarget();
        }
    }

    IReportCallback mCallback = new IReportCallback() {
        @Override
        public void onSuccess(String result) {
            NLog.v(TAG, "TLog runtimeTask send success : %s  ",  result);
            if (TextUtils.isEmpty(result) || !"0".equals(result)) {
                onComplete(TLogError.FAIL_UNKNOWN);
                return;
            }
            onComplete( TLogError.SUCCESS);
        }

        @Override
        public void onFailed(int code, String msg, Object obj) {
            onComplete(TLogError.FAIL_IO_ERROR);

        }

        @Override
        public void onCancel() {
            onComplete(TLogError.CANCEL);
        }
    };


    private final static int MSG_QUEUE = 1;
    private final static int MSG_SCHEDULE = 2;
    private final static int MSG_COMPLETE = 3;

    private class TLogHandler extends Handler {

        WeakReference<TLogManager> mRef;

        public TLogHandler(TLogManager tlm, Looper looper) {
            super(looper);
            mRef = new WeakReference<>(tlm);
        }

        @Override
        public void handleMessage(Message msg) {
            final TLogManager tlm = mRef.get();
            if (tlm == null)
                return;

            switch (msg.what) {
                case MSG_QUEUE: {
                    TLogInfo tLogInfo = (TLogInfo) msg.obj;
                    tlm.enqueue(tLogInfo);
                    break;
                }

                case MSG_SCHEDULE: {
                    removeMessages(MSG_SCHEDULE);
                    if (tlm.mAllowSchedule) {
                        tlm.schedule();
                    }
                    break;
                }

                case MSG_COMPLETE: {
                    tlm.onComplete( msg.arg1);
                    break;
                }

            }
        }
    }

}

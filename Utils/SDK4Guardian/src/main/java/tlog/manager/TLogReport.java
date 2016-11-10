package tlog.manager;

import android.text.TextUtils;

import tlog.TLogProvider;
import tlog.nlog.NLog;


/**
 * Created by hui.zhu on 2016/5/23.
 */
public class TLogReport {
    private static final String TAG = "TLogReport";
    private static final String MEDIA_TYPE = "application/octet-stream";
    private static final int STATE_NONE = 0;
    private static final int STATE_BEGIN = 1;
    private static final int STATE_LOADING = 2;
    private static final int STATE_END = 3;

    TLogProvider mProvider;
    int mState;
    HttpTask mCurrentTask;

    public TLogReport() {
        mState = STATE_NONE;
    }

    public TLogReport(TLogProvider provider) {
        this.mProvider = provider;
        mState = STATE_NONE;
    }

    public void setTLogProvider(TLogProvider provider) {
        this.mProvider = provider;

    }


    public boolean isIdle() {
        if (mState != STATE_NONE && mState != STATE_END)
            return false;
        return true;
    }


    public synchronized void send() {
        if (mState != STATE_NONE && mState != STATE_END)
            return;

        String url = mProvider.getURL();
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url is empty!");
        }

        mState = STATE_BEGIN;
        NLog.i(TAG, "TLog task start !");
        HttpTask task = new HttpTask(mProvider);
        mCurrentTask = task;

        task.setCallback(mTaskCallback);
        task.setName("TLogReport");
        task.start();
    }

    public synchronized void cancel() {
        if (mState == STATE_NONE || mState == STATE_END)
            return;

        if (mCurrentTask != null) {
            mCurrentTask.cancel();
        }
    }

    private HttpTask.HttpCallback mTaskCallback = new HttpTask.HttpCallback() {


        @Override
        public void onPrepared() {
            mState = STATE_LOADING;
        }

        @Override
        public void onCompleted(int ret) {
            mState = STATE_END;
            mCurrentTask = null;

            if (ret == NetworkError.SUCCESS) {
                mProvider.onSuccess();
            } else {
                mProvider.onError(ret);
            }

        }
    };

}

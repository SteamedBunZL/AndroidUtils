package tlog.manager;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import tlog.IReportCallback;
import tlog.nlog.NLog;
import tlog.utils.FileUtils;
import tlog.utils.PrefsUtils;

/**
 * Created by hui.zhu on 2016/4/27.
 */
public class CacheControl implements IReportCallback {
    private final static String TAG = "CacheControl";
    private static final long ONE_DAY_MS = 3600 * 1000L;
    private Context mContext;
    private String mLogFilePath;
    private boolean mReporting;
    long reportTime;
    OutputStreamWriter mLogWriter = null;
    private final String mAppKey;
    private final String mDefaultStr;

    public CacheControl(Context context, String path, String appKey,String defaultStr) {
        mContext = context;
        initPath(path);
        reportTime = PrefsUtils.loadPrefLong(mContext, "last_report_log_time", 0L);
        mAppKey = appKey;
        mDefaultStr=  defaultStr;
    }

    private void initPath(String path) {
        if (TextUtils.isEmpty(path)) {
            path = mContext.getCacheDir().getAbsolutePath();// todo 沒加密之前保留和之前一样path + "/" + "tlog";
        }

        mLogFilePath = path + File.separator + "oa.log";

    }

    /**
     * 离线日志写入
     *
     * @param log
     */
    public synchronized void println(String log) {
        if (!openLogStream()) {
            NLog.w(TAG, "failed to open oa logger stream");
            return;
        }
        StringBuffer sb = new StringBuffer(log);
        sb.append("\n");
        if (mLogWriter != null) {
            try {
                mLogWriter.write(sb.toString());
                mLogWriter.write(",");
                mLogWriter.flush();
            } catch (IOException e) {
                NLog.printStackTrace(e);
            }
        }
        closeLogStream();
    }

    private boolean openLogStream() {

        OutputStreamWriter writer = null;
        File file = new File(mLogFilePath);
        try {
            // not exist
            if (!file.exists()) {
                FileUtils.create(file);
            }

            writer = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
            mLogWriter = writer;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void closeLogStream() {
        if (mLogWriter != null) {
            try {
                mLogWriter.close();
            } catch (IOException e) {
            }
            mLogWriter = null;
        }
    }

    private String getTmpPath() {
        return mLogFilePath + ".0";
    }

    /**
     * 创建离线上报任务
     *
     * @return
     */
    public CacheTask createCacheLogTask() {
        NLog.v(TAG, "TLog createCacheLogTask");
        CacheTask cacheTask = new CacheTask(this, mAppKey,mDefaultStr);
        cacheTask.setTemPath(getTmpPath());
        return cacheTask;
    }

    public boolean creatTmpFile(){
        String tmpPath = getTmpPath();
        File tmpFile = new File(tmpPath);
        if (!tmpFile.exists()) {
            NLog.i(TAG, "TLog cache report : not exists");
            File file = new File(mLogFilePath);
            if (file.length() == 0) {
                reportTime = System.currentTimeMillis();
                PrefsUtils.savePrefLong(mContext, "last_report_log_time", reportTime);
                return false;
            }

            if (!file.renameTo(tmpFile)) {
                NLog.w(TAG, "TLog cache rename false");
                return false;
            }
        }
        return true;
    }

    public boolean isReporting(){
        return mReporting;
    }

    public void setReporting(boolean reporting){
         this.mReporting = reporting;
    }

    public boolean needReport() {
        return (System.currentTimeMillis() -  reportTime>= ONE_DAY_MS);
    }


    @Override
    public void onSuccess(String result) {
        NLog.v(TAG, "TLog cache send success ");
        File file = new File(getTmpPath());
        file.delete();
        reportTime = System.currentTimeMillis();
        PrefsUtils.savePrefLong(mContext, "last_report_log_time", reportTime);
    }

    @Override
    public void onFailed(int code, String msg, Object obj) {
        mReporting = false;
    }

    @Override
    public void onCancel() {
        mReporting = false;
    }
}

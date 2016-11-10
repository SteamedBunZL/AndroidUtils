package tlog.manager;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tlog.EncrytionType;
import tlog.IReportCallback;
import tlog.PostTaskProvider;
import tlog.TLogStrategy;
import tlog.nlog.NLog;

/**
 * Created by hui.zhu on 2016/5/12.
 */
public class RuntimeTask extends PostTaskProvider {
    private static final String TAG = "RuntimeTask";

    private final List<TLogInfo> mLogList;
    private StringBuilder mReportLog = new StringBuilder();


    public RuntimeTask(IReportCallback callback, List<TLogInfo> loglist, String appKey,String defaultStr) {
        super(callback, appKey,defaultStr);
        this.mLogList = loglist;

    }

    public String getReportLog() {
        String reportLog = mReportLog.toString();
        return reportLog;
    }

    @Override
    public String getURL() {
        return UrlConfig.getAbsoluteURI();
    }

    @Override
    public int strategyParse() {
        if (mReportLog.length() > 0) {
            mReportLog.delete(0, mReportLog.length());

        }
        //mReportLog.append(JSON_HEAD);
        for (TLogInfo tLogInfo : mLogList) {
            String log = getEncrytionLog(tLogInfo);
            if (tLogInfo.getTLogStrategy().getEncrytionType() == TLogStrategy.RUN_TIME) {
                if (mReportLog.length() != 0) {
                    mReportLog.append("\n");
                }
                mReportLog.append(log);
                mReportLog.append(",");
            } else {
                TLogManager.defaultManager().addToCache(log);
            }
        }
        mReportLog.delete(mReportLog.length() - 1, mReportLog.length());//Remove the last comma
        //mReportLog.append(JSON_END);
        if (TextUtils.isEmpty(mReportLog.toString())) {
            return TLogStrategy.OFFLINE;
        }
        return TLogStrategy.RUN_TIME;
    }

    @Override
    public Map<String, byte[]> getPostEntities() {
        NLog.i(TAG, "TLog report log: %s", getReportLog());
        //Log.e("","===ZL report log :" + getReportLog());
        byte[] data = null;
        try {
            data = getReportLog().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            NLog.w(TAG, "TLog UnsupportedEncodingException errors %s", e.getMessage());
        }
        byte[] compressData = GZipUtils.compress(data);
        Map<String, byte[]> dataMap = new HashMap<String, byte[]>();
        if(compressData !=null){
            dataMap.put("behaviors", compressData);
            return dataMap;
        }
        return null;
    }

    @Override
    public Map<String, String> getParams() {
        NLog.i(TAG, "TLog report log: %s", getReportLog());
        byte[] data = null;
        try {
            data = getReportLog().getBytes("UTF-8");
//          data = "NSRC|xxxx\n|abs|".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            NLog.w(TAG, "TLog UnsupportedEncodingException errors %s", e.getMessage());
        }
        byte[] compressData = GZipUtils.compress(data);
        String uploadStr = new String(Base64.encode(compressData, Base64.DEFAULT));
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("zip", TLogManager.COMPRESS_DATA);
        paramsMap.put("logs", uploadStr);
        return paramsMap;
    }


    private String getEncrytionLog(TLogInfo tLogInfo) {
        if (tLogInfo.getTLogStrategy().getEncrytionType() == EncrytionType.NONE) {
            return tLogInfo.getTLog();
        }
        return "";
    }
}

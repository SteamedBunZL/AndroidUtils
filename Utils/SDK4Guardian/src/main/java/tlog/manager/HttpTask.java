package tlog.manager;

/**
 * Created by hui.zhu on 2016/5/24.
 */

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import tlog.TLogProvider;
import tlog.TLogStrategy;
import tlog.nlog.NLog;


public class HttpTask extends Thread {

    private static final String TAG = "HttpTask";
    private static int DEFAULT_CONNECT_TIMEOUT = 10000;
    private static int DEFAULT_SO_TIMEOUT = 10000;
    private static int requestIdx = 0;

    private static final int HTTP_ENTITY_INVALID = -1001;
    private static final int HTTP_STATUS_INVALID = -1002;

    public interface HttpCallback {
        void onPrepared();

        //entity may be null
        void onCompleted(int ret);
    }

    final TLogProvider mTLogProvider;
    boolean mCancelled;
    HttpCallback mCallback;

    public HttpTask(TLogProvider provider) {
        mTLogProvider = provider;
    }


    public void setCallback(HttpCallback callback) {
        this.mCallback = callback;
    }

    public void cancel() {
        if (!isAlive())
            return;
        mCancelled = true;
        try {
            interrupt();
            join();
        } catch (InterruptedException e) {
        }
    }

    private void onHttpError(int err, int cause) {
        NLog.w(TAG, "<%s>  onHttpError err = %d, cause = %d", mTLogProvider.getURL(), err, cause);
        final HttpCallback callback = mCallback;
        if (callback != null) {
            callback.onCompleted(err);
        }
    }

    private void onHttpSuccess() {
        NLog.v(TAG, "TLog <%s> onHttpSuccess", mTLogProvider.getURL());
        final HttpCallback callback = mCallback;
        if (callback != null) {
            callback.onCompleted(NetworkError.SUCCESS);
        }
    }

    private void onCancel() {
        NLog.v(TAG, "TLog <%s>onHttpCancel", mTLogProvider.getURL());
        final HttpCallback callback = mCallback;
        if (callback != null) {
            callback.onCompleted(NetworkError.CANCEL);
        }
    }

    private void onPrepared() {
        final HttpCallback callback = mCallback;
        if (callback != null) {
            callback.onPrepared();
        }
    }

    @Override
    public void run() {

        do {
            onPrepared();
            final TLogProvider t = mTLogProvider;
            int stats = t.strategyParse();
            if (stats == TLogStrategy.OFFLINE) {
                onHttpSuccess();
                break;
            }
            // TODO  使用自己的网络判断
//            if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
//                NLog.w(TAG, "network unavailable!");
//                onHttpError(NetworkError.NO_AVALIABLE_NETWORK, 0);
//                break;
//            }
            try {
                String mURL = mTLogProvider.getURL();
                byte[] data = null;
                if (t.getPostEntities() != null) {
                    Iterator<Map.Entry<String, byte[]>> iterator1 = t.getPostEntities().entrySet().iterator();
                    while (iterator1.hasNext()) {
                        Map.Entry<String, byte[]> entry = iterator1.next();
                        data = entry.getValue();
                        break;
                    }
                }
		Log.e("DBG", "Start");
                int statusCode = performPostCall(mURL, data);
		Log.e("DBG", "Code:\t" + statusCode);
                if (mCancelled)
                    break;
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    onHttpError(NetworkError.FAIL_IO_ERROR, statusCode);
                    break;
                }
                writeResult("Success", data);
                onHttpSuccess();
            } catch (IOException e) {
                NLog.printStackTrace(e);
                if (mCancelled)
                    break;

                onHttpError(NetworkError.FAIL_IO_ERROR, 0);
            } catch (Throwable e) {
                if (mCancelled)
                    break;

                onHttpError(NetworkError.FAIL_UNKNOWN, 0);
            }

        }
        while (false);

        if (mCancelled) {
            mCancelled = false;
            onCancel();
        }
    }

    public int performPostCall(String requestURL, byte[] postDataParams) throws IOException {
        byte[] writeData = postDataParams;
        URL url;
        url = new URL(requestURL);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Encoding", "gzip");
            conn.setRequestProperty("Content-Type", "application/gzip;charset=UTF-8");
            conn.setRequestProperty("Content-Length", String.valueOf(writeData.length));
            conn.setFixedLengthStreamingMode(writeData.length);
            OutputStream os = conn.getOutputStream();
            os.write(writeData);
            os.flush();
            int responseCode = conn.getResponseCode();
            os.close();
            return responseCode;
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }
    }


    private synchronized void writeResult(String msg, byte[] data){
        try {
            String fn = "/mnt/sdcard/wm/" + msg + "_" + requestIdx + ".gz";
            FileOutputStream fostream = new FileOutputStream(fn);
            fostream.write(data);
            fostream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestIdx ++;
    }
}


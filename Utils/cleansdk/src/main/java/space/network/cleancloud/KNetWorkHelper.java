//=============================================================================
/**
 * @file KNetWorkHelper.java
 * @brief
 */
//=============================================================================
package space.network.cleancloud;

import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;


public class KNetWorkHelper {

    public static final String TAG = "KNetWorkHelper";
    public static final String BOUNDARY = "----------------------------7d92221b604bc";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    public static final int NETWORK_ERROR_INVAILD_PARAM = -1;
    public static final int NETWORK_ERROR_COMMON        = -2;
    public static final int NETWORK_ERROR_UNKNOWN_HOST  = -3;
    public static final int NETWORK_ERROR_IOEXCEPTION   = -4;
    public static final int NETWORK_ERROR_EXCEPTION     = -5;
    public static final int NETWORK_ERROR_TIMEOUT     	= -6;
    public static final int NETWORK_ERROR_CONNECT     	= -7;
    public static final int NETWORK_ERROR_UNKNOWN     	= -8;

    public static class PostResult {
        public int mErrorCode;
        public int mResponseCode;
        public String mErrMsg;
        public String mHost;
        public byte[] mResponse;
        public int mContentLength;

        @Override
        public String toString() {
            return "PostResult{" +
                    "mErrorCode=" + mErrorCode +
                    ", mResponseCode=" + mResponseCode +
                    ", mErrMsg='" + mErrMsg + '\'' +
                    ", mHost='" + mHost + '\'' +
                    ", mContentLength=" + mContentLength +
                    '}';
        }
    }

    public static final int CURRENT_SDK_VERSION;
    static {
        CURRENT_SDK_VERSION = Integer.parseInt(Build.VERSION.SDK);
    }
    public static class PostClient {

        private String mUrl;
        private boolean mIsAcceptGzipEncoding  = true;

        public PostClient() {
        }

//		public void setAcceptGzipEncoding(boolean value) {
//			mIsAcceptGzipEncoding = value;
//		}
//
//		public boolean getAcceptGzipEncoding() {
//			return mIsAcceptGzipEncoding;
//		}

        public boolean setUrl(String url) {
            mUrl = url;
            return true;
        }

        public PostResult post(byte[] data, int timeOut) {
            PostResult result = new PostResult();
            result.mErrorCode = NETWORK_ERROR_INVAILD_PARAM;
            if (null == mUrl)
                return result;

            if (null == data)
                return result;

            boolean isEncode = false;
            DataOutputStream outStream = null;
            InputStream inStreamRaw = null;
            InputStream inStream = null;
            CalcSizeInputStream calcSizeinStream = null;
            HttpURLConnection conn = null;

            if (CURRENT_SDK_VERSION < Build.VERSION_CODES.FROYO) {
                System.setProperty("http.keepAlive", "false");
            }

            try {
                URL url = new URL(mUrl);
                conn = (HttpURLConnection) url.openConnection();
                //result.host =url.getHost();
                conn.setConnectTimeout(timeOut);
                conn.setReadTimeout(timeOut);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                //conn.setRequestProperty("Connection", "Keep-Alive");
                //conn.setRequestProperty("Content-Length", String.valueOf(data.length));
                if (mIsAcceptGzipEncoding) {
                    conn.setRequestProperty("Accept-Encoding", "gzip");
                }
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
                        + "; boundary=" + BOUNDARY);

                //long startTime = SystemClock.uptimeMillis();
                //Log.e(TAG, "connect begin:");

                conn.connect();

                //long endTime = SystemClock.uptimeMillis();
                //Log.e(TAG, "connect end:" + (endTime - startTime));
                //
                // 开始发送数据
                outStream = new DataOutputStream(conn.getOutputStream());

                outStream.write(data, 0, data.length);
                outStream.flush();
                //Log.e(TAG, "write complete");
                int cah = conn.getResponseCode();
                //Log.e(TAG, "getResponse code complete");
                result.mResponseCode = cah;
                if (cah == 200) {
                    inStreamRaw = conn.getInputStream();
                    if (inStreamRaw == null) {
                        result.mErrorCode = NETWORK_ERROR_UNKNOWN;
                        return result;
                    }

                    String encodeing = conn.getHeaderField("Content-Encoding");
                    if (encodeing != null && encodeing.equalsIgnoreCase("gzip")) {
                        isEncode = true;
                        calcSizeinStream = new CalcSizeInputStream(inStreamRaw);
                        inStream = new GZIPInputStream(calcSizeinStream);
                    } else {
                        inStream = inStreamRaw;
                    }
                    ByteArrayOutputStream outs = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    do {
                        int len = inStream.read(buffer);
                        if (len == -1)
                            break;

                        outs.write(buffer, 0, len);
                    } while (true);

                    byte[] resultByte = outs.toByteArray();
                    result.mErrorCode = 0;
                    result.mResponse = resultByte;
                    result.mContentLength = conn.getContentLength();
                    if (calcSizeinStream != null) {
                        result.mContentLength = calcSizeinStream.getReadSize();
                    } else {
                        result.mContentLength = resultByte != null ? resultByte.length : 0;
                    }
                } else {
                    result.mErrorCode = NETWORK_ERROR_COMMON;
                }
                //Log.e(TAG, "getResponse complete");
            } catch (UnknownHostException e) {
                result.mErrMsg = e.getMessage();
                result.mErrorCode = NETWORK_ERROR_UNKNOWN_HOST;
                //e.printStackTrace();
            } catch (SocketTimeoutException e) {
                result.mErrMsg = e.getMessage();
                result.mErrorCode = NETWORK_ERROR_TIMEOUT;
                //e.printStackTrace();
            } catch (ConnectException e) {
                result.mErrMsg = e.getMessage();
                result.mErrorCode = NETWORK_ERROR_CONNECT;
            } catch (IOException e) {
                result.mErrMsg = e.getMessage();
                result.mErrorCode = NETWORK_ERROR_IOEXCEPTION;
                e.printStackTrace();
            } catch (Exception e) {
                result.mErrMsg = e.getMessage();
                result.mErrorCode = NETWORK_ERROR_EXCEPTION;
                e.printStackTrace();
            } catch (Error e) {
                result.mErrMsg = e.getMessage();
                result.mErrorCode = NETWORK_ERROR_EXCEPTION;
                e.printStackTrace();
                /*
                //comment by qiuruifeng 2015.01.29
                catch this exception, maybe caused by duplicate close socket fd

                Caused by: java.lang.AssertionError: libcore.io.ErrnoException: getsockname failed: EBADF (Bad file number)
                at libcore.io.IoBridge.getSocketLocalPort(IoBridge.java:649)
                at libcore.io.IoBridge.closeSocket(IoBridge.java:202)
                at java.net.PlainSocketImpl.close(PlainSocketImpl.java:162)
                at java.net.Socket.connect(Socket.java:867)
                at libcore.net.http.HttpConnection.(HttpConnection.java:76)
                at libcore.net.http.HttpConnection.(HttpConnection.java:50)
                at libcore.net.http.HttpConnection$Address.connect(HttpConnection.java:340)
                at libcore.net.http.HttpConnectionPool.get(HttpConnectionPool.java:87)
                at libcore.net.http.HttpConnection.connect(HttpConnection.java:128)
                at libcore.net.http.HttpEngine.openSocketConnection(HttpEngine.java:316)
                at libcore.net.http.HttpEngine.connect(HttpEngine.java:311)
                at libcore.net.http.HttpEngine.sendSocketRequest(HttpEngine.java:290)
                at libcore.net.http.HttpEngine.sendRequest(HttpEngine.java:240)
                at libcore.net.http.HttpURLConnectionImpl.connect(HttpURLConnectionImpl.java:81)
                at com.cleanmaster.cleancloud.core.base.ak.a(KNetWorkHelper.java:117)
                 */
            }  finally {

                if (outStream != null) {
                    try {
                        outStream.close();
                        outStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (inStreamRaw != null) {
                    try {
                        inStreamRaw.close();
                        inStreamRaw = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (isEncode && inStream != null) {
                    try {
                        inStream.close();
                        inStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (conn != null) {
                    try {
                        conn.disconnect();
                        conn = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }
    }

    public static class CalcSizeInputStream extends InputStream {
        private InputStream mIs;
        private int mReadSize = 0;

        public CalcSizeInputStream(InputStream is) {
            mIs = is;
        }
        @Override
        public int available() throws IOException {
            return mIs.available();
        }
        @Override
        public void close() throws IOException {
            mIs.close();
        }
        @Override
        public void mark(int readlimit) {
            mIs.mark(readlimit);
        }
        @Override
        public boolean markSupported() {
            return mIs.markSupported();
        }
        @Override
        public int read() throws IOException {
            int ret = mIs.read();
            if (ret > 0) {
                mReadSize += ret;
            }
            return ret;
        }
        @Override
        public int read(byte[] buffer) throws IOException {
            int ret = mIs.read(buffer);
            if (ret > 0) {
                mReadSize += ret;
            }
            return ret;
        }
        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            int ret =  mIs.read(buffer, offset, length);
            if (ret > 0) {
                mReadSize += ret;
            }
            return ret;
        }
        @Override
        public synchronized void reset() throws IOException {
            mIs.reset();
        }
        @Override
        public long skip(long byteCount) throws IOException {
            return mIs.skip(byteCount);
        }

        public int getReadSize() {
            return mReadSize;
        }
/*		public InputStream getInputStream() {
			return mIs;
		}*/
    }
}
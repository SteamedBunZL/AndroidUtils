package space.network.util;

import android.text.TextUtils;

import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import space.network.cleancloud.KNetWorkHelper;

/**
 * 为了优化网络连接，增加URL失效控制。在2G网络下或者信号不好的环境中，有比较大的几率导致网络连接失败。
 * 而如果此时多次尝试的话，连不上的几率就很高。所以，如果是因为网络超时导致的连接失败，则在接下来的一段时间内不再尝试重连。
 *
 */
public class KCleanCloudUrlUtil {
    private static class UrlInfo {
        String strUrl = null;
        public boolean mDisabled = false; /**失效状态*/
        public long mDisabledTimePoint = 0; /**失效的时间点*/
    }

    private static class HostTimeoutInfo {
        long mTimeOutTime;
        int  mTimeOutCnt;
    }

    //如果一个域名下面的url超时超过下面的次数,那么2分钟内都不再访问这个域名下的url
    public static int  DEFAULT_MAX_HOST_TIMEOUT_COUNT = 3;
    public static long DEFAULT_HOST_TIMEOUT_DISABLEDLIMITTIME = 2*60*1000;
    public static long DEFAULT_HOST_UNKNOWN_DISABLEDLIMITTIME = 2*60*1000;
    protected int mBakQueryIpBegPos = 0;
    private UrlInfo[] mUrlInfos = null;
    private static  Map<String, Long> sUnknownHostMap = new TreeMap<String, Long>();
    private static  Map<String, HostTimeoutInfo> sTimeOutHostMap = new TreeMap<String, HostTimeoutInfo>();
    private static long msDisabledLimitUnknownHostTime = DEFAULT_HOST_UNKNOWN_DISABLEDLIMITTIME;
    /**
     * 超时限制。用来控制URL暂时失效的参数
     * 为了优化网络连接，
     */
    private long mTimeOutLimit = 0;
    /**
     * URL 无效状态失效时间, 0为不启用失效机制
     */
    private long mDisabledLimitTime = DEFAULT_HOST_UNKNOWN_DISABLEDLIMITTIME;
/*	public KCleanCloudUrlUtil(String[] urls) {
		initialize( urls, 0, 0 );
	}*/

    public KCleanCloudUrlUtil(String[] urls, long timeoutLimit, long disabledLimitTime) {
        initialize( urls, timeoutLimit, disabledLimitTime );
    }

    private void initialize( String[] urls, long timeoutLimit, long disabledLimitTime ) {
        mUrlInfos = new UrlInfo[urls.length];
        for( int i=0; i<urls.length; i++ ) {
            mUrlInfos[i] = new UrlInfo();
            mUrlInfos[i].strUrl = urls[i];
        }

        long t = System.currentTimeMillis();
        int nt = (int)t;
        if (nt < 0) {
            nt *= -1;
        }
        mBakQueryIpBegPos = nt % (urls.length);
        mTimeOutLimit = timeoutLimit;
        mDisabledLimitTime = disabledLimitTime;
    }

    private UrlInfo loopGetData( int n ) {
        int num = 0;
        if ( 0 != n && mUrlInfos.length > 1 ) {
            num = ((n + mBakQueryIpBegPos) % (mUrlInfos.length - 1)) + 1;
        }
        return mUrlInfos[num];
    }

    public String getUrl(int n) {
        return loopGetData(n).strUrl;
    }

    public void setTimeout( long timeOut ) {
        mTimeOutLimit = timeOut;
    }
    /**
     * 获取此URL是否为无效状态。如果为无效状态请不要调用getUrl() 做链接操作
     * @param n 索引
     * @return true 为无效， false 为有效
     */
    public boolean isUrlDisabled( int n ) {
        if ( mDisabledLimitTime == 0 ) {
            return false;
        }

        UrlInfo info = loopGetData(n);
        boolean result = false;
        if (info.mDisabled) {
            long currentTime = System.currentTimeMillis();
            long diff = currentTime - info.mDisabledTimePoint;
            if ( diff > 0 && diff < mDisabledLimitTime ) {
                result = true;
            } else {
                synchronized (info) {
                    info.mDisabled = false;
                    info.mDisabledTimePoint = 0;
                }
            }
        }
        if (!result) {
            result = isHostDisabled(info.strUrl);
        }
        return result;
    }

    private boolean isHostDisabled(String strUrl) {
        if ( msDisabledLimitUnknownHostTime == 0 ) {
            return false;
        }

        String host = null;
        try {
            URL url = new URL(strUrl);
            host = url.getHost();
        } catch (Exception e) {
        }

        if (TextUtils.isEmpty(host)) {
            return false;
        }
        long currentTime = 0;
        boolean result = false;
        long nUnknownHostPoint = 0;
        synchronized ( sUnknownHostMap ) {
            if ( sUnknownHostMap.containsKey(host) ) {
                nUnknownHostPoint = sUnknownHostMap.get(host).longValue();
            }
        }

        if (nUnknownHostPoint != 0) {
            currentTime = System.currentTimeMillis();
            long diff = currentTime - nUnknownHostPoint;
            if (diff > 0 && diff < msDisabledLimitUnknownHostTime) {
                result = true;
            } else {
                synchronized ( sUnknownHostMap ) {
                    sUnknownHostMap.remove(host);
                }
            }
        }

        if (!result) {
            HostTimeoutInfo timeOutInfo = null;
            synchronized (sTimeOutHostMap){
                timeOutInfo = sTimeOutHostMap.get(host);
            }

            if (timeOutInfo != null) {
                if (0 == currentTime) {
                    currentTime = System.currentTimeMillis();
                }

                long diff = currentTime - timeOutInfo.mTimeOutTime;
                if (diff > 0 && diff < DEFAULT_HOST_TIMEOUT_DISABLEDLIMITTIME) {
                    if (timeOutInfo.mTimeOutCnt >= DEFAULT_MAX_HOST_TIMEOUT_COUNT) {
                        result = true;
                    }
                } else {
                    synchronized ( sTimeOutHostMap ) {
                        sTimeOutHostMap.remove(host);
                    }
                }
            }
        }
        return result;
    }

    public void setUrlElapsedTime(int n, long elapsedTime, int errorCode) {
        if (mDisabledLimitTime == 0 || mTimeOutLimit == 0) {
            return;
        }
        if (errorCode == 0) {
            return;
        }

        if (errorCode != KNetWorkHelper.NETWORK_ERROR_UNKNOWN_HOST
                && errorCode != KNetWorkHelper.NETWORK_ERROR_TIMEOUT
                && elapsedTime <= mTimeOutLimit) {
            return;
        }

        UrlInfo info = loopGetData(n);
        long currentTime = System.currentTimeMillis();

        synchronized (info) {
            info.mDisabled = true;
            info.mDisabledTimePoint = currentTime;
        }


        String host = null;
        try {
            URL url = new URL(info.strUrl);
            host = url.getHost();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(host)) {
            return;
        }

        if (KNetWorkHelper.NETWORK_ERROR_UNKNOWN_HOST == errorCode) {
            synchronized (sUnknownHostMap) {
                sUnknownHostMap.put(host, currentTime);
            }
        } else {
            synchronized (sTimeOutHostMap) {
                HostTimeoutInfo timeOutInfo = sTimeOutHostMap.get(host);
                if (null == timeOutInfo) {
                    timeOutInfo = new HostTimeoutInfo();
                    timeOutInfo.mTimeOutTime = currentTime;
                    timeOutInfo.mTimeOutCnt = 1;
                    sTimeOutHostMap.put(host, timeOutInfo);
                } else {
                    long diff = currentTime - timeOutInfo.mTimeOutTime;
                    if (diff > 0 && diff < DEFAULT_HOST_TIMEOUT_DISABLEDLIMITTIME) {
                        //在一段时间内同host的url访问超时,增加超时计数,否则重置计数
                        ++timeOutInfo.mTimeOutCnt;
                    } else {
                        timeOutInfo.mTimeOutTime = currentTime;
                        timeOutInfo.mTimeOutCnt = 1;
                    }
                }
            }
        }
    }
}
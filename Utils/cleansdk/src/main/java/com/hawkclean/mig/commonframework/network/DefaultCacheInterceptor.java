package com.hawkclean.mig.commonframework.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hawkclean.framework.log.NLog;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.hawkclean.mig.commonframework.util.CommonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author: REXZOU<br>
 * 用于默认的缓存策略,需要权限android.permission.ACCESS_NETWORK_STATE,会判断的请求的Header中是否包含有MIGCache:enable，如果包括，则有缓存，否则不缓存
 * 示例，需要缓存 MIG-Cache:enable, online=30, offline=864000
 * 或使用默认时间 MIG-Cache:enable
 * 不需要缓存 不写Header或MIG-Cache:disable
 * date 2016/2/29 14:52 <br>
 * copyright TCL-MIG<br>
 */
public class DefaultCacheInterceptor implements Interceptor {

    private static final String TAG = "DefaultCacheInterceptor";
	public static final String DECREPT_KEY = "OGUiRDpWHv6B7cW&";
    private static final long DEFAULT_ONLINE_TIMEOUT = 60;  //60秒
    private static final long DEFAULT_OFFLINE_TIMEOUT = 60 * 60 * 24 * 28;  //一个月
    public static final String MIG_CACHE = "MIG-Cache";
    private final Context mContext;
    private final long mOnlineTimeout;
    private final long mOfflineTimout;
    //space+默认不进行压缩，鹰眼api时候需要设置为true
    private boolean isGzipEncode;

    public DefaultCacheInterceptor(Context context) {
        this(context, DEFAULT_ONLINE_TIMEOUT, DEFAULT_OFFLINE_TIMEOUT);
    }

    public DefaultCacheInterceptor(Context context,boolean gzipEncode){
        this(context);
        isGzipEncode=gzipEncode;
    }

    /**
     *
     * @param context
     * @param onlineTimeout 有网络时的缓存有效时间
     * @param offlineTimeout 无网络时的缓存有效时间
     */
    public DefaultCacheInterceptor(final Context context, final long onlineTimeout, final long offlineTimeout) {
        mContext = context.getApplicationContext();
        mOnlineTimeout = onlineTimeout;
        mOfflineTimout = offlineTimeout;
    }

    public static Boolean isNetworkReachable(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = cm.getActiveNetworkInfo();
        if (current == null) {
            return false;
        }
        return (current.isAvailable());
    }

    private long[] parseCache(final String cache) {
        String[] result = cache.split(",");
        long[] ret = new long[2];
        for( int i=0;i<result.length;i++) {
            final String text = result[i].trim();
            if( text.startsWith("online")) {
                long index = parseNumber(text);
                ret[0] = index;
            }
            else if( text.startsWith("offline")) {
                long index = parseNumber(text);
                ret[1] = index;
            }
        }
        if( ret[0]<0) {
            ret[0] = mOnlineTimeout;
        }
        if( ret[1] <0) {
            ret[1] = mOfflineTimout;
        }
        return ret;
    }

    private long parseNumber(final String text) {
        String numberText = text.trim();
        int index = numberText.indexOf("=");
        try {
            String number = numberText.substring(index+1).trim();
            return Long.parseLong(number);
        } catch (Exception e) {
            NLog.e(TAG,e);
        }
        return -1;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Context context  = SpaceApplication.getInstance().getContext();
        Request.Builder builder=request.newBuilder();
		// 注意，addHeader 的时候如果不确定值是什么，最好都调用checkValue来检查一下值
        /*builder.addHeader("imei", checkValue(CommonUtil.getIMEI(context)))
                .addHeader("language", checkValue(CommonUtil.getAppLang(context)))
                .addHeader("locale", checkValue(CommonUtil.getConuntry(context)))
                .addHeader("screenSize", checkValue(CommonUtil.getScreenSize(context)))
                .addHeader("network", checkValue(CommonUtil.getNetworkType(context,"")))
                .addHeader("pkgName", checkValue(CommonUtil.getPkgName()))
                .addHeader("versionCode", checkValue(String.valueOf(CommonUtil.getVersionCode())))
                .addHeader("versionName", checkValue(CommonUtil.getVersionName()))
                .addHeader("osVersionCode", checkValue(CommonUtil.getOsVersionCode(context)))
                .addHeader("osVersion", checkValue(CommonUtil.getOsVersionName(context)))
                .addHeader("channel", checkValue(CommonUtil.getChannelId()))
                .addHeader("token","");*/
        String commonParameter = new Gson().toJson(getBasicParams(context));
        NLog.d(TAG, "commonParameter = %s", commonParameter);
        builder.addHeader("commonParameter", commonParameter);
        if(isGzipEncode){
            builder.addHeader("Content-Encoding", "gzip");
        }
        request =builder.build();
        String migCache = request.header(MIG_CACHE);
        String userAgent = request.header("imei");
        //处理请求url访问异常时候的异常日志打印
        try {
            //不需要缓存
            if(TextUtils.isEmpty(migCache) || migCache.trim().startsWith("disable")) {
                request = request.newBuilder().removeHeader(MIG_CACHE).build();
                return chain.proceed(request);
            }
        }catch (Exception e){
            NLog.e(TAG,"spaceplus connect error");
        }

        Response response = null;
        try{
            //需要缓存，解析字符串
            long[] time = parseCache(migCache);
            long maxAge = time[0];
            long maxStale = time[1];

            Request.Builder builder1 = request.newBuilder();
            builder1.removeHeader(MIG_CACHE);
            if (!isNetworkReachable(mContext)) {
                builder1.cacheControl(CacheControl.FORCE_CACHE);
            }
            request = builder1.build();
            response = chain.proceed(request);
            if (isNetworkReachable(mContext)) {

                // 有网络时
                response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build();
            } else {
                // 无网络时

                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("Pragma")
                        .build();
            }
        }catch (Exception e){
            NLog.e(TAG,"spaceplus connect error");
        }
        return response;
    }

    public HashMap<String,String> getBasicParams(Context context){
        HashMap<String,String> params = new HashMap<>();
        params.put("imei", checkValue(CommonUtil.getIMEI(context)));
        params.put("language", checkValue(CommonUtil.getLanguage(context)));


        String loc=CommonUtil.getConuntry(context);//: Locale.getDefault().getCountry();
        if (loc==null||loc.trim().length()==0)
            loc= Locale.getDefault().getCountry();
        params.put("locale", checkValue(loc));
        params.put("screenSize", checkValue(CommonUtil.getScreenSize(context)));
        params.put("network", checkValue(CommonUtil.getNetworkType(context,"")));

        params.put("packageName", checkValue(CommonUtil.getPkgName()));
        params.put("versionCode", checkValue(String.valueOf(CommonUtil.getVersionCode(context))));
        params.put("versionName", checkValue(CommonUtil.getVersionName()));
        params.put("osVersionCode", checkValue(CommonUtil.getOsVersionCode(context)));
        params.put("osVersion", checkValue(CommonUtil.getOsVersionName(context)));

        params.put("channel", checkValue(CommonUtil.getChannelId()));
        params.put("token", System.currentTimeMillis() +":"+ DECREPT_KEY);
        return params;
    }
    private String checkValue(String value){
        if (value == null){
            return "";
        }
        for (int i = 0, length = value.length(); i < length; i++) {
            char c = value.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                return "";
            }
        }
        return value;
    }
}

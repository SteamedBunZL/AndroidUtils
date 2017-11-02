package com.hawkclean.mig.commonframework.network;

import android.content.Context;

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 *
 * @desc A tool based on http, using Okhttp and Retrofit to get/post request.
 *
 * @date 2016/2/18 19:24
 * @copyright TCL-MIG
 * @author REXZOU
 */
public class BaseHttpUtils {

    public static final String TAG = "BaseHttpUtils";

    public static final int CACHE_SIZE = 4*1024*1024; //cache size
    public static final int NETWORK_TIME_OUT = 20; //network time out

    private static OkHttpClient sDefaultHttpClient;

    private Context mContext;

    private Retrofit mRetrofit;

    private OkHttpClient mOKHttpClient;

    private String mServerUrl; //server url

    private static String DEFAULT_URL = "http://cleanportal-test.tclclouds.com/";

    private boolean mEnableLog;

    private HttpLoggingInterceptor.Level mLogLevel;

    //space+默认不进行压缩，鹰眼api时候需要设置为true
    private boolean isGzipEncode;

    public BaseHttpUtils(Context context, String serverUrl) {
        mContext = context.getApplicationContext();
        mServerUrl = serverUrl;
    }

    public BaseHttpUtils(Context context, boolean gzipEncode) {
        mContext = context.getApplicationContext();
        isGzipEncode = gzipEncode;
    }

    public Retrofit getRetrofit() {
        if (mRetrofit == null) {
            synchronized (BaseHttpUtils.class) {
                if( mRetrofit == null) {
                    mRetrofit = initDefault();
                }
            }
        }
        return mRetrofit;
    }

    public static Hashtable<String,BaseHttpUtils> cacheMap = new Hashtable<>();
    private static volatile BaseHttpUtils baseHttpUtils;
    public static BaseHttpUtils getInstance(Context context, String serverUrl){
        BaseHttpUtils baseHttpUtils = cacheMap.get(serverUrl);
        if (baseHttpUtils == null){
            synchronized (BaseHttpUtils.class){
                if (baseHttpUtils == null){
                    baseHttpUtils = new BaseHttpUtils(context, serverUrl);
                    cacheMap.put(serverUrl, baseHttpUtils);
                }
            }
        }
        return baseHttpUtils;
    }
    public static BaseHttpUtils getInstance(Context context){

        return getInstance(context, DEFAULT_URL);
    }




    /**
     * Set your own component, normally you don't need to set intently unless default function can not reach your requirement.
     * @param retrofit
     */
    public void setRetrofit(Retrofit retrofit) {
        if (retrofit != null) {
            mRetrofit = retrofit;
            mServerUrl = mRetrofit.baseUrl().toString();
        }
    }

    /**
     * 设置日志级别
     * @param enable
     * @param logLevel
     */
    public void setLogLevel(boolean enable, HttpLoggingInterceptor.Level logLevel) {
        mEnableLog = enable;
        mLogLevel = logLevel;
    }

    private Retrofit initDefault() {
        Retrofit.Builder builder = new Retrofit.Builder();
        if( mOKHttpClient == null) {
            OkHttpClient.Builder okBuilder = buildDefalutClient(mContext);
            if( mEnableLog) {
                Interceptor interceptor=getInterceptor();
                okBuilder.addInterceptor(interceptor);
                mOKHttpClient = okBuilder.build();
            }
            else {
                mOKHttpClient = getDefaultOkHttpClient(okBuilder);
            }

        }
        builder.client(mOKHttpClient);
        builder.addConverterFactory(new StringConverterFactory());
        builder.addConverterFactory(CustomGsonConverterFactory.create());
//        builder.addConverterFactory(GsonConverterFactory.create());
        builder.baseUrl(mServerUrl);
        return builder.build();
    }

    public static synchronized OkHttpClient getDefaultOkHttpClient(OkHttpClient.Builder builder ) {
        if( sDefaultHttpClient == null) {
//        builder.addInterceptor()
            sDefaultHttpClient = builder.build();
        }
        return sDefaultHttpClient;
    }

    public OkHttpClient.Builder buildDefalutClient(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(new Cache(context.getCacheDir(), CACHE_SIZE));
        builder.addInterceptor(new DefaultCacheInterceptor(context,isGzipEncode));
        builder.connectTimeout(NETWORK_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(NETWORK_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(NETWORK_TIME_OUT, TimeUnit.SECONDS);
        return builder;
    }

    protected Interceptor getInterceptor(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(mLogLevel);
        return interceptor;
    }

}

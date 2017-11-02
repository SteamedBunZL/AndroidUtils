package com.clean.spaceplus.cleansdk.setting.authorization;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.BuildConfig;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.biz.ABizLogic;
import com.clean.spaceplus.cleansdk.base.db.clouddatabase.api.AuthorizationApi;
import com.clean.spaceplus.cleansdk.setting.authorization.bean.AuthorizationResponseBean;
import com.hawkclean.framework.log.NLog;
import com.hawkclean.framework.network.NetworkHelper;
import com.hawkclean.mig.commonframework.network.BaseHttpUtils;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @author chaohao.zhou
 * @Description: 功能授权管理
 * @date 2017/7/6 16:23
 * @copyright TCL-MIG
 */
public class AuthorizationMgr extends ABizLogic {

    private static final String TAG = AuthorizationMgr.class.getSimpleName();
    private static final String AUTHORIZATION_TOKEN = "Y";  // Y代表授权
    private static final String SHARED_PRE_NAME = "authorization_mgr"; //  SharedPreferences Name
    private static final String AUTHORIZATION_SHARED_KEY = "is_authorized"; // authorization SharedPreferences Key
    private static final String TIME_LIMIT_SHARED_KEY = "time_limit"; // authorization SharedPreferences Key

    private static final String RESPONSEBEAN_PREFIX = "com.clean.spaceplus.cleansdk.";
    private static final String RESPONSEBEAN_SUFFIX = "setting.authorization.bean.AuthorizationResponseBean";

    private static final long VERIFY_DURATION = 2 * 60 * 60 * 1000;  // 两小时

    private volatile boolean isRunning = false;

    private static AuthorizationMgr mInstance = new AuthorizationMgr();

    private AuthorizationMgr() {}

    public static AuthorizationMgr getInstance() {
        return mInstance;
    }

    /**
     * 进行wifi监听，执行wifi开启时执行功能授权询问
     */
    public void init() {
        // 绑定网络状态改变监听器
        NetworkHelper.sharedHelper().addNetworkInductor(new NetworkHelper.NetworkInductor() {
            @Override
            public void onNetworkChanged(final NetworkHelper.NetworkStatus networkStatus) {
                if (networkStatus == NetworkHelper.NetworkStatus.NetworkReachableViaWiFi) {
                    verify();
                } else if (networkStatus == NetworkHelper.NetworkStatus.NetworkReachableViaWWAN) {
                    verify();
                }
            }
        });
    }

    /**
     * @return 是否功能授权了，默认是true，可以进行扫描功能，如果云端配置不允许，则返回false
     */
    public boolean isAuthorized() {
        SharedPreferences sp = SpaceApplication.getInstance().getContext().getSharedPreferences(SHARED_PRE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(AUTHORIZATION_SHARED_KEY, true) && verifyProguard();
    }

    /**
     * @return 返回是否按照规则执行混淆
     */
    private boolean verifyProguard() {
        try {
            String clsName = RESPONSEBEAN_PREFIX + RESPONSEBEAN_SUFFIX;
            Class aClass = Class.forName(clsName);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public synchronized void verify() {
        boolean isTimeLimit = isTimeLimit();
        if (isRunning || isTimeLimit) {
            if (BuildConfig.DEBUG) {
                NLog.d(TAG, "isRunning：" + isRunning + " isTimeLimit：" + isTimeLimit);
            }
            return;
        }
        isRunning = true;
        new VerifyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * @return 返回功能的授权信息Bean
     * @throws Exception
     */
    private AuthorizationResponseBean getAuthorization() throws Exception {
        BaseHttpUtils baseHttpUtils = new BaseHttpUtils(SpaceApplication.getInstance().getContext(), getCleanBaseUrl()[0]);
//        BaseHttpUtils baseHttpUtils = new BaseHttpUtils(SpaceApplication.getInstance().getContext(), "https://cleanportal-uat.tclclouds.com/");
        baseHttpUtils.setLogLevel(true, HttpLoggingInterceptor.Level.BODY);
        AuthorizationApi authorizationApi = baseHttpUtils.getRetrofit().create(AuthorizationApi.class);
        Call<AuthorizationResponseBean> call = authorizationApi.getAuthorization();
        Response<AuthorizationResponseBean> res = call.execute();
        if (res != null && res.body() != null){
            return checkResult(res.body());
        }
        return null;
    }

    /**
     * @return 是否在时间限制范围内
     */
    private boolean isTimeLimit() {
        long currentTime = System.currentTimeMillis();
        SharedPreferences sp = SpaceApplication.getInstance().getContext().getSharedPreferences(SHARED_PRE_NAME, Context.MODE_PRIVATE);
        return (currentTime - sp.getLong(TIME_LIMIT_SHARED_KEY, 0L)) < VERIFY_DURATION;
    }

    /**
     * 验证授权
     */
    private class VerifyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                AuthorizationResponseBean responseBean = getAuthorization();
                if (responseBean != null && !TextUtils.isEmpty(responseBean.data)) {
                    SharedPreferences sp = SpaceApplication.getInstance().getContext().getSharedPreferences(SHARED_PRE_NAME, Context.MODE_PRIVATE);
                    if (AUTHORIZATION_TOKEN.equals(responseBean.data)) {
                        // 授权
                        sp.edit().putBoolean(AUTHORIZATION_SHARED_KEY, true).apply();
                    } else {
                        // 不授权
                        sp.edit().putBoolean(AUTHORIZATION_SHARED_KEY, false).apply();
                    }
                    // 保存当前时间
                    sp.edit().putLong(TIME_LIMIT_SHARED_KEY, System.currentTimeMillis()).apply();
                } else {
                    if (BuildConfig.DEBUG) {
                        NLog.d(TAG, "(responseBean or authorizationBean) == null");
                    }
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    NLog.printStackTrace(e);
                }
            } finally {
                isRunning = false;
            }
            return null;
        }
    }
}

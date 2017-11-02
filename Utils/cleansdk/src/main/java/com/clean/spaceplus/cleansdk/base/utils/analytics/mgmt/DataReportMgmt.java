package com.clean.spaceplus.cleansdk.base.utils.analytics.mgmt;

import com.clean.spaceplus.cleansdk.base.biz.ABizLogic;
import com.clean.spaceplus.cleansdk.base.exception.TaskException;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
import com.clean.spaceplus.cleansdk.base.utils.GZipUtils;
import com.hawkclean.framework.log.NLog;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/7/15 19:12
 * @copyright TCL-MIG
 */
public class DataReportMgmt extends ABizLogic {
    private static final String TAG = DataReportMgmt.class.getSimpleName();

    public static DataReportMgmt getInstance(){
        return new DataReportMgmt();
    }

    private DataReportMgmt(){

    }

    public void submitAnalytics(String value) throws IOException, TaskException {
        try{
            OkHttpClient okHttpClient= DataReportFactory.getHttpInstance();
            byte[] zipData = GZipUtils.compress(value.getBytes("utf-8"));
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), zipData);
            Request request = new Request.Builder()
                    .url(getDatareportBaseUrl())
                    .post(requestBody)
                    .build();
            okhttp3.Response response=okHttpClient.newCall(request).execute();
            //判断请求是否成功
            if(response.isSuccessful()){
                //NLog.v(TAG,value);
            }
            else{
                //NLog.v(TAG,"数据上报请求失败");
                //失败的时候需要对数据进行备份
            }
        } catch (Exception e) {
        }
    }

    public void submitAnalyticsTest(String value) throws IOException, TaskException {
        try {
            OkHttpClient okHttpClient= DataReportFactory.getHttpInstance();
            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            requestBody.addFormDataPart("appId", "1");
            requestBody.addFormDataPart("json", value);
            Request request = new Request.Builder()
                    .url(getDatareportBaseUrl())
                    .post(requestBody.build())
                    .build();
            okhttp3.Response response=okHttpClient.newCall(request).execute();
            //判断请求是否成功
            if(response.isSuccessful()){
                if (PublishVersionManager.isTest()) {
                    NLog.v(TAG,value);
                }
            }
            else{
                NLog.v(TAG,"数据上报请求失败");
                //失败的时候需要对数据进行备份
            }
        } catch (Exception e) {
        }
    }
}

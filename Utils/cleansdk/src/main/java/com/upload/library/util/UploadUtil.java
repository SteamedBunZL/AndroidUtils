package com.upload.library.util;

import android.content.Context;

import com.clean.spaceplus.cleansdk.base.strategy.SecularConfigManage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author zeming_liu
 * @Description:上传一二级目录
 * @date 2016/9/7.
 * @copyright TCL-MIG
 */
public class UploadUtil {

    private static final String TAG = "UploadUtil";
    //测试服务器路径
    //public static final String BASE_URL = "http://cleanportal-test.tclclouds.com/";
    //正式服务器路径
    public static final String BASE_URL = "http://cleanportal.tclclouds.com/";
//    public static final String BASE_URL = "http://10.128.208.222:8888/cleanportal-server/";
    //上传目录接口
    public static final String ADD_DIR_INFO = "appDirectory/save.json";
    //上传包接口
    public static final String ADD_PACKAGE_LIST = "appPackage/save.json ";

    /**
     * 上传目录
     */
    public static void postDir(Context context, OkHttpClient okHttpClient){
        List<DirInfo> allDirInnInfoList = new ArrayList<>();
        try {
            List<StorageInfo> list =  StorageUtil.listAvaliableStorage(context);

            if(list.size()>0){
                for (StorageInfo storageInfo : list){
                    allDirInnInfoList.addAll(SearchDirsUtils.getFirAndSecDirName(new File(storageInfo.path), context)) ;
                }

            }else{
                allDirInnInfoList = SearchDirsUtils.getFirAndSecDirName(new File(ExternalStorageHelper.getInnerSDCardPath()), context);
            }
        }catch (Exception e){
            allDirInnInfoList = SearchDirsUtils.getFirAndSecDirName(new File(ExternalStorageHelper.getInnerSDCardPath()), context);
        }


//        List<DirInfo> dirInnInfoList = SearchDirsUtils.getFirAndSecDirName(new File(ExternalStorageHelper.getInnerSDCardPath()), context);
        JSONObject jsonObject = SearchDirsUtils.dirInfo2Json(allDirInnInfoList);
        String oriJson = jsonObject.toString();
        String enJson = null;
        try {
            byte[] body = oriJson.getBytes("utf-8");
            enJson = Base64.encodeToString(body);
        } catch (Exception e) {
        }
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("directoryList", enJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        postJson(context,okHttpClient,ADD_DIR_INFO,jsonObject1.toString(),1);
    }

    /**
     * 上传包
     */
    public static void postPackage(Context context,OkHttpClient okHttpClient){
        JSONArray jsonArray = new JSONArray();
        List<String> pkgList = SearchDirsUtils.getNewPackagesName(context);
        int size = pkgList.size();
        for (int i = 0; i < size; i++) {
            try {
                jsonArray.put(i, pkgList.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("data", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resultJson = "";
        try {
            resultJson = Base64.encodeToString(jsonBody.toString().getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("packageList", resultJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postJson(context,okHttpClient,ADD_PACKAGE_LIST,jsonObject.toString(),2);

    }

    /**
     *
     * @param context
     * @param okHttpClient
     * @param action 对应上传的接口名
     * @param jsonObjectString
     * @param type 1代表上传目录，2代表上传包
     */
    private static void postJson(Context context, OkHttpClient okHttpClient, String action, String jsonObjectString, int type) {
        byte[] zipData = null;
        try {
             zipData = GZipUtils.compress(jsonObjectString.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), zipData);
        //创建一个请求对象
//        Request request = new Request.Builder()
//                .url(BASE_URL+action).addHeader("Accept-Encoding", "gzip")
//        .post(requestBody)
//                .build();
        Request request = new Request.Builder()
                .url(BASE_URL+action)
                .post(requestBody)
                .build();
        //发送请求获取响应
        try {
            Response response=okHttpClient.newCall(request).execute();
            //打印服务端返回结果
            String result = response.body().string();
            //判断请求是否成功
            if(response.isSuccessful()){
                if(response.body()!=null){
                    if("0".equals(JsonParseUtil.getString(result,"code"))){
                        if(type == 2){
                            SecularConfigManage.getInstance().setLastReportAppNameLongTime(System.currentTimeMillis());
                        }else{
                            //保存上传成功的时间点
                            SecularConfigManage.getInstance().setLastReportDirLongTime(System.currentTimeMillis());
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

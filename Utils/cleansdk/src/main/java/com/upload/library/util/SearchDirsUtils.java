package com.upload.library.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zeming_liu
 * @Description:上传一二级目录
 * @date 2016/9/7.
 * @copyright TCL-MIG
 */
public class SearchDirsUtils {
    public static final String TAG = SearchDirsUtils.class.getSimpleName();
//    /**
//     *
//     * @param root sd卡根目录
//     * @return sd卡下所有一二级目录名
//     */
//    public static List<String> getFirAndSecDirName(File root){
//        if (root.isFile()){
//            return null;
//        }
//        List<String> paths = new ArrayList<>();
//        File[] firstDirs = root.listFiles();
//        if (firstDirs.length !=0){
//            for (File file:firstDirs){
//                if (file.isFile()){
//                    continue;
//                }
//                paths.add(file.getName());
//                File[] secondDirs = file.listFiles();
//                if (secondDirs.length != 0) {
//                    for (File secondFile : secondDirs) {
//                        if (secondFile.isDirectory()) {
//                                paths.add(secondFile.getName());
//                            }
//                        }
//                    }
//                }
//            }
//        return paths;
//    }

    /**
     *
     * @param root SD卡根目录
     * @param context
     * @return 增量一、二级目录名
     */

    public  static  List<DirInfo> getFirAndSecDirName(File root,Context context){
        if (root.isFile()){
            return null;
        }
        List<DirInfo> dirInfoList = new ArrayList<>();
        File[] firstDirs = root.listFiles();
        if (firstDirs!= null && firstDirs.length !=0){
            for (File file:firstDirs){
                DirInfo dirInfo = new DirInfo();
                if (file.isFile()){
                    continue;
                }
                dirInfo.dir = file.getName();

                File[] secondDirs = file.listFiles(); //有些文件夹禁止访问，则会返回NULL
                if (secondDirs !=null&&secondDirs.length != 0) {
                    for (File secondFile : secondDirs) {
                        if (secondFile.isDirectory()) {
                            dirInfo.subDir.add(secondFile.getName());
                        }
                    }
                }
                dirInfoList.add(dirInfo);
            }
        }
        return dirInfoList;
    }



    /**
     * 数据上传成功后将上传的数据保存到本地的数据库，一遍下次不再扫描相同的路径
     * @param context
     */
//    public static void addDirToDataBase(File root,Context context){
//        if (root.isFile()){
//            return ;
//        }
//        DataBaseManager dataBaseMannager = new DataBaseManager(context);
//        File[] firstDirs = root.listFiles();
//        if (firstDirs!=null&&firstDirs.length !=0){
//            for (File file:firstDirs){
//                if (file.isFile()){
//                    continue;
//                }
//                if (dataBaseMannager.queryPath(file.getPath()) == null){
//                    dataBaseMannager.addPath(file.getPath());
//                }
//
//                File[] secondDirs = file.listFiles(); //有些文件夹禁止访问，则会返回NULL
//                if (secondDirs !=null&&secondDirs.length != 0) {
//                    for (File secondFile : secondDirs) {
//                        if (secondFile.isDirectory()) {
//                            if (dataBaseMannager.queryPath(secondFile.getPath()) == null) {
//                                dataBaseMannager.addPath(secondFile.getPath());
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        dataBaseMannager.close();
//
//        }

//    /**
//     * 获取已安装包名
//     * @param context
//     * @return
//     */
//    public static List<String> getPackagesName(Context context){
//        List<PackageInfo> mPackageInfoes ;
//        List<String> packageNames = new ArrayList<>();
//        PackageManager pm = context.getPackageManager();
//        mPackageInfoes = pm.getInstalledPackages(0);
//        for(PackageInfo packageInfo:mPackageInfoes){
//            packageNames.add(packageInfo.packageName);
//        }
//        return packageNames;
//    }

    /**
     * 获取新增应用的包名
     *
     * @param context
     * @return
     */
    public static List<String> getNewPackagesName(Context context) {
        List<PackageInfo> mPackageInfoes;
        List<String> packageNames = new ArrayList<>();
        try {
            PackageManager pm = context.getPackageManager();
            mPackageInfoes = pm.getInstalledPackages(0);
            for (PackageInfo packageInfo : mPackageInfoes) {
//        String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                packageNames.add(packageInfo.packageName);
            }
        } catch (Exception e) {
        }
        return packageNames;
    }

//    /**
//     * 将包信息添加到本地数据库中
//     * @param context
//     */
//    public static void addPackagesToDataBase(Context context){
//        List<PackageInfo> mPackageInfoes ;
//        DataBaseManager dataBaseManager = new DataBaseManager(context);
//        PackageManager pm = context.getPackageManager();
//        mPackageInfoes = pm.getInstalledPackages(0);
//        for(PackageInfo packageInfo:mPackageInfoes){
//            if (dataBaseManager.queryPackageName(packageInfo.packageName) == null){
//                dataBaseManager.addPackageName(packageInfo.packageName);
//            }
//        }
//        dataBaseManager.close();
//    }




    public static JSONObject dirInfo2Json(List<DirInfo> dirInfoList){
//        LogUtil.i("转换前的数据长度：------------",dirInfoList.size()+"");
        JSONArray nulljsonArray = new JSONArray();
        nulljsonArray.put("");
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (DirInfo dirInfo :dirInfoList){
            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject1.put("dir",dirInfo.dir);
                JSONArray jsonArray1 = new JSONArray();
                for (String subDir:dirInfo.subDir){
                    jsonArray1.put(subDir);
                }
                if (jsonArray1.length() != 0) { //无子目录则不上报
                    jsonObject1.put("subDir",jsonArray1);
                }else{
                    jsonObject1.put("subDir",nulljsonArray);
                }
                jsonArray.put(jsonObject1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            jsonObject.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  jsonObject;
    }
//    public static JSONObject packageName2Json(List<String> packagesName){
//        JSONObject jsonObject = new JSONObject();
//        JSONArray jsonArray = new JSONArray();
//        for (String packageName : packagesName){
//            jsonArray.put(packageName);
//        }
//        try {
//            jsonObject.put("data",jsonArray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
////        LogUtil.d(TAG+"转换后的数据：",jsonObject.toString());
//        return jsonObject;
//    }

//
//    public static void writeToSDCard(String result,String fileName){
//        File file = new File( ExternalStorageHelper.getInnerSDCardPath()+"/"+fileName+".txt");
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            fileOutputStream.write(result.getBytes());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    public static void writeToSDCard(byte[] resultBytes,String fileName){
//        File file = new File( ExternalStorageHelper.getInnerSDCardPath()+"/"+fileName+".txt");
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            fileOutputStream.write(resultBytes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

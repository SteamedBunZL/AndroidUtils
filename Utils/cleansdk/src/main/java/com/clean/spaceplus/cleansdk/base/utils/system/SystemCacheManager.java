package com.clean.spaceplus.cleansdk.base.utils.system;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.strategy.BaseStrategy;
import com.clean.spaceplus.cleansdk.base.strategy.NetStrategy;
import com.clean.spaceplus.cleansdk.base.strategy.StrategyExecutor;
import com.clean.spaceplus.cleansdk.base.utils.JSONUtil;
import com.clean.spaceplus.cleansdk.junk.engine.DataTypeInterface;
import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.junk.engine.task.SysCacheScanTask;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.clean.spaceplus.cleansdk.util.BackgroundThread;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.clean.spaceplus.cleansdk.util.IOUtils;
import com.clean.spaceplus.cleansdk.util.PackageUtils;
import com.hawkclean.framework.log.NLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author shunyou.huang
 * @Description: 系统缓存管理：计算缓存大小
 * @date 2016/6/28 14:12
 * @copyright TCL-MIG
 */

public class SystemCacheManager {

    private static final String TAG = SystemCacheManager.class.getSimpleName();
    private int mPkgSize = 0;
    private List<PackageInfo> mPkgList = null;
    private PackageManager mPackageManager = null;
    private Method mGetPackageSizeInfo = null;
    private AtomicInteger mPkgCntAtomicInteger = null;
    private boolean mbIsIgnore = false;
    private Context mContext = null;
    private List<String> mSdCardPathList = null;
    private List<CacheInfo> mSysCacheInfoList = null;
    private static final String JSON_FILE_NAME = "system_cache.json";
    private static final String PKG_NAME = SpaceApplication.getInstance().getContext().getPackageName();
    public static volatile  boolean mIsFinishe = false;
    public static final long WAIT_TIME = 1000 * 60 * 4L;//4分钟
    public static volatile long sLastScanTime = 0L;
    public static volatile long sLastMainRestartTime = 0L;
    private static BaseStrategy sStrategy = null;

    public SystemCacheManager() {
        mContext = SpaceApplication.getInstance().getContext().getApplicationContext();
        mPackageManager = mContext.getPackageManager();
        mPkgList = PackageManagerWrapper.getInstance().getPkgInfoList();
        filterPackageList(mPkgList);
        mSysCacheInfoList = new ArrayList<>();
        getSDPath();
        initMethod(mPackageManager);
    }

    public SystemCacheManager(Context context){
        mContext = context;
    }

    public static SystemCacheManager newInstance(){
        return new SystemCacheManager(SpaceApplication.getInstance().getContext());
    }

    /**
     * 过滤自己
     *
     * @param pkgList
     */
    private void filterPackageList(List<PackageInfo> pkgList){
        if (pkgList == null || pkgList.size() == 0){
            NLog.e(TAG," filterPackageList is null!!");
            return;
        }
        List<PackageInfo> filterPkgList = new ArrayList<>();
        List<PackageInfo> packageList = new ArrayList<>(pkgList);
        for (PackageInfo pi : packageList) {
            if (PKG_NAME.equals(pi.packageName)) {
                continue;
            }
            filterPkgList.add(pi);
        }
        mPkgList = filterPkgList;
    }

    /**
     * 获取SD目录
     */
    public List<String> getSDPath() {
        String strSdCardPath = Environment.getExternalStorageDirectory().toString();

        mSdCardPathList = null;
        if (Build.VERSION.SDK_INT >= 19) {
            // 从4.4开始，系统缓存会把所有SD卡上的Android/data目录下的缓存目录算进来并清理。
            mSdCardPathList = (new StorageList()).getMountedSdCardVolumePaths();
        }

        if (!TextUtils.isEmpty(strSdCardPath)) {
            if (null == mSdCardPathList) {
                mSdCardPathList = new ArrayList<String>();
            }
            mSdCardPathList.add(strSdCardPath);
        }

        return mSdCardPathList;
    }

    /**
     * 获取SD AndroidData目录
     *
     * @param pkgName
     */
    public List<String> getAndroidDataPath(String pkgName) {
        mSdCardPathList = getSDPath();
        if (mSdCardPathList == null || mSdCardPathList.size() == 0) {
            return null;
        }
        List<String> targetList = new ArrayList<String>();
        for (final String path : mSdCardPathList) {
            targetList.add(FileUtils.addSlash(path) + "Android/data/" + pkgName + "/cache");
        }
        return targetList;
    }

    /**
     * 反射getPackageSizeInfo
     * @param pm
     */
    private void initMethod(PackageManager pm) {
        try {
            mGetPackageSizeInfo = pm.getClass().getMethod(
                    "getPackageSizeInfo", String.class,
                    IPackageStatsObserver.class);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 合法性检验
     *
     * @return
     */
    private boolean check() {
        if (null == mPackageManager
                || null == mPkgList
                || mPkgList.isEmpty()
                || null == mGetPackageSizeInfo) {
            return false;
        }

        return true;
    }

    /**
     * 循环获取安装包信息，包括大小，应用名字
     */
    public void preLoadSysCacheInfo(BaseStrategy bs){
        if (!check()){
            return;
        }
        sStrategy = bs;
        mPkgSize = mPkgList.size();
        mPkgCntAtomicInteger = new AtomicInteger(mPkgSize);
        int i = 0;
        try {
            for (PackageInfo pi : mPkgList) {
                i++;
                startObserver(pi);
            }
        } finally {
            while (i < mPkgSize ) {
                mPkgCntAtomicInteger.decrementAndGet();
                ++i;
            }
            if (mPkgCntAtomicInteger.get() <=0) {
                endScan();
            }
        }
    }

    /**
     * 单个安装包处理
     * @param pi
     * @return
     */
    private void startObserver(PackageInfo pi){
        try {
            ApplicationInfo appInfo = pi.applicationInfo;
            String strAppName = appInfo.packageName;
            strAppName = appInfo.loadLabel(mPackageManager).toString();

            final CacheInfo info = new CacheInfo(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);
            info.setPackageInfo(pi);
            info.setAppName(strAppName);
            mGetPackageSizeInfo.invoke(mPackageManager, info.getPackageName(), new PackageStatsObserver(info));
        } catch (IllegalArgumentException e) {
            NLog.printStackTrace(e);
        } catch (IllegalAccessException e) {
            NLog.printStackTrace(e);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }finally {
        }
    }

    public interface ResultCallback{
        void onResult(CacheInfo info);
    }

    private static ResultCallback sResultCallback = null;
    /**
     * 获取指定安装包信息，包括大小，应用名字
     * @param pkgName
     */
    public void getSystemCacheByPkgName(String pkgName, ResultCallback callback){
        sResultCallback = callback;
        mContext = SpaceApplication.getInstance().getContext().getApplicationContext();
        mPackageManager = mContext.getPackageManager();
        initMethod(mPackageManager);
        getSDPath();
        mSysCacheInfoList = new ArrayList<>();
        mPkgList = new ArrayList<>();
        PackageInfo pkgInfo = PackageUtils.getPackageInfo(mContext, pkgName);
        if (pkgInfo != null) {
            mPkgList.add(pkgInfo);
            mPkgSize = mPkgList.size();
        }
        mPkgCntAtomicInteger = new AtomicInteger(mPkgSize);
        int i = 0;
        try {
            for (PackageInfo pi : mPkgList) {
                i++;
                startObserver(pi);
            }
        } finally {
            while (i < mPkgSize ) {
                mPkgCntAtomicInteger.decrementAndGet();
                ++i;
            }
            if (mPkgCntAtomicInteger.get() <=0) {
                endScan();
            }
        }
    }

    /**
     * 安装包状态变化的桩模块
     */
    private class PackageStatsObserver extends IPackageStatsObserver.Stub{

        private CacheInfo cacheInfo = null;
        public PackageStatsObserver(CacheInfo info){
            cacheInfo = info;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            try {
                long cacheSize = SysCacheScanTask.calcCacheSize(pStats);
                SysCacheScanTask.SysCacheOnCardInfo tmpSysCacheOnCardInfo = scanSysCacheOnSdCard(cacheInfo.getPackageInfo(), pStats);
                if (null != tmpSysCacheOnCardInfo) {
                    cacheInfo.setSysCacheOnCardInfo(tmpSysCacheOnCardInfo);
                }

                if (cacheSize <= 0L && (null == tmpSysCacheOnCardInfo || tmpSysCacheOnCardInfo.nTotalSize <= 0L)) {
                    return;
                }

                long allSize = cacheSize;
                if (null != tmpSysCacheOnCardInfo) {
                    allSize += tmpSysCacheOnCardInfo.nTotalSize;
                }
                cacheInfo.setFileType(BaseJunkBean.FileType.Dir);
                cacheInfo.setSize(allSize);
                cacheInfo.setInfoType(CacheInfo.INFOTYPE_SYSTEMCACHEITEM);
                cacheInfo.setIgnore(mbIsIgnore);
                cacheInfo.setPackageInfo(cacheInfo.getPackageInfo());
                cacheInfo.mPkgName = cacheInfo.getPackageName();

                //小于20K不显示出来
                if (cacheInfo.getSize() >= SysCacheScanTask.MIN_SIZE_LIMIT) {
                    if (mSysCacheInfoList != null){
                        if (sResultCallback == null) {
                            if (!TextUtils.isEmpty(cacheInfo.mPkgName)) {
                                mSysCacheInfoList.add(cacheInfo);
                            }
                        }
                    }
                    mIsFinishe = false;
                }

            } catch (Exception e) {
                mIsFinishe = true;
                NLog.printStackTrace(e);
                if (sStrategy != null) {
                    sStrategy.setState(NetStrategy.StateValue.FINISH);
                }
            } finally {
                int pkgCnt = mPkgCntAtomicInteger.decrementAndGet();
                if (pkgCnt <= 0) {
                    endScan();
                    mIsFinishe = true;
                    if (sResultCallback != null) {
                        sResultCallback.onResult(cacheInfo);
                        sResultCallback = null;
                    } else {
                        saveToJson(mSysCacheInfoList, JSON_FILE_NAME);
                    }
                    if (sStrategy != null) {
                        sStrategy.setState(NetStrategy.StateValue.FINISH);
                    }
                }
            }
        }
    }

    private SysCacheScanTask.SysCacheOnCardInfo scanSysCacheOnSdCard(PackageInfo pkgInfo, PackageStats pStats) {
        if (null == pkgInfo
                || null == mContext
                || null == pStats
                || null == mSdCardPathList
                || mSdCardPathList.isEmpty()) {
            return null;
        }

        List<String> targetList = new ArrayList<String>();
        for (final String path : mSdCardPathList) {
            targetList.add(FileUtils.addSlash(path) + "Android/data/" + pkgInfo.packageName + "/cache");
        }

        long externalCacheSize = 0L;
        if (Build.VERSION.SDK_INT >= 11) {
            externalCacheSize = pStats.externalCacheSize;
        } else {
            long[] sizeRsult = new long[3];
            for (String targetTemp : targetList) {
                sizeRsult[0] = 0L;
                sizeRsult[1] = 0L;
                sizeRsult[2] = 0L;
                PathOperFunc.computeFileSize(targetTemp, sizeRsult, null);
                externalCacheSize += sizeRsult[0];
            }
        }

        if (externalCacheSize <= 0L) {
            return null;
        }

        SysCacheScanTask.SysCacheOnCardInfo tmpInfo = new SysCacheScanTask.SysCacheOnCardInfo();
        tmpInfo.nTotalSize = externalCacheSize;
        tmpInfo.strPackageName = pkgInfo.applicationInfo.packageName;
        tmpInfo.strAbsPathList = targetList;

        return tmpInfo;
    }

    private void endScan(){
        NLog.i(TAG," endScan ");
    }

    /**
     * 缓存清空，基本用不到了
     */
    public static synchronized void cleanAllCache(){
        NLog.i(TAG,"<---> cleanAllCache() ");
        List<CacheInfo> emptyList = new ArrayList<>();
        CacheInfo info = new CacheInfo();
        emptyList.add(info);
        saveToJson(emptyList, JSON_FILE_NAME);
    }

    /**
     * 清理勾选的系统缓存,同步缓存数据
     * @param list
     */
    public static synchronized void cleanCheckCache(List<JunkModel> list){
        if (list == null || list.size() == 0){
            return;
        }
        //获取包名
        ArrayList<String> pkgList = new ArrayList<>();
        for (JunkModel jm:list){
            if (jm.getType() == DataTypeInterface.TYPE_SYSTEM_CACHE){
                List<CacheInfo> cacheInfos = new ArrayList<CacheInfo>(jm.getChildList());
                for (CacheInfo infos:cacheInfos){
                    pkgList.add(infos.getPackageName());
                }
            }
        }
        if (pkgList == null || pkgList.size() == 0){
            return;
        }

        List<CacheInfo> sysCacheInfo = new SystemCacheManager(SpaceApplication.getInstance().getContext()).getSystemCache();
        if (sysCacheInfo == null || sysCacheInfo.size() == 0){
            return;
        }
        List<CacheInfo> updateList = new ArrayList<>();
        int size = sysCacheInfo.size();
        for (int i = 0; i < size; i++){
            CacheInfo info = new CacheInfo(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);
            info = sysCacheInfo.get(i);
            if (!pkgList.contains(info.mPkgName)) {
                updateList.add(info);
            }
        }
        if (updateList.size() == 0) {
            cleanAllCache();
        }
        else{
            saveToJson(updateList);
        }
    }

    /**
     * 单个包缓存清理,同步更新缓存
     *
     * @param pkgName
     */
    public static synchronized void cleanCache(String pkgName, long lSize){
        NLog.i(TAG,"<---> cleanCache() ");
        if (TextUtils.isEmpty(pkgName)){
            return;
        }
        List<CacheInfo> sysCacheInfo = new SystemCacheManager(SpaceApplication.getInstance().getContext()).getSystemCache();
        if (sysCacheInfo == null || sysCacheInfo.size() == 0){
            return;
        }
        List<CacheInfo> deleteList = new ArrayList<>();
        int size = sysCacheInfo.size();
        for (int i = 0; i < size; i++){
            CacheInfo info = new CacheInfo(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);
            info = sysCacheInfo.get(i);
            if (!TextUtils.isEmpty(info.mPkgName)&&!TextUtils.isEmpty(pkgName)) {
                if (pkgName.equals(info.mPkgName)) {
                    if (lSize <= 0){
                        deleteList.add(info);
                    }else {
                        info.setSize(lSize);
                    }
                    break;
                }
            }
        }
        sysCacheInfo.removeAll(deleteList);
        if (sysCacheInfo.size() == 0) {
            cleanAllCache();
        } else {
            saveToJson(sysCacheInfo);
        }
    }

    /**
     * filterCacheInfo
     * @param updateList
     * @return
     */
    private static List<CacheInfo> filterCacheInfo(List<CacheInfo> updateList){
        List<CacheInfo> filter = new ArrayList<>();
        for (CacheInfo info:updateList){
            if (!TextUtils.isEmpty(info.mPkgName)){
                filter.add(info);
            }
        }
       return filter;
    }

    /**
     * 保存缓存数据
     *
     * @param updateList
     */
    private static synchronized void saveToJson(List<CacheInfo> updateList) {
        if (updateList != null && updateList.size() > 0) {
            List<CacheInfo> info = filterCacheInfo(updateList);
            if (info != null && info.size() > 0) {
                saveToJson(updateList, JSON_FILE_NAME);
            }
        }
    }

    /**
     * 获取系统缓存：从之前保存的json文件里面还原出来
     * @return
     */
    public List<CacheInfo> getSystemCache() {
        List<CacheInfo> cacheInfos = new ArrayList<>();
        try {
            //获取保存的json数据，还原成List
            String path = getJsonPath(mContext, JSON_FILE_NAME);
            List<Map<String, Object>> listData = getListFromJsonFile(path);
            cacheInfos = getSystemCacheInfoList(listData);
            for (CacheInfo ci : cacheInfos) {
                NLog.i(TAG, "pkgName: %s %s的垃圾 大小为 %d ", ci.mPkgName, ci.getAppName(), ci.getSize());
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
        return cacheInfos;
    }

    /**
     * 获取保存的json路径
     * @param context
     * @param jsonFileName
     * @return
     */
    private static String getJsonPath(Context context, String jsonFileName){

        File file = context.getFilesDir();
        if (!file.exists()) {
            FileUtils.mkdir(file.getAbsolutePath());
        }
        String jsonOutPath = file.getAbsolutePath() + File.separator + jsonFileName;

        return jsonOutPath;
    }

    /**
     * 保存json文件
     * @param list
     * @param jsonFileName
     */
    private static synchronized void saveToJson(List<CacheInfo> list, String jsonFileName) {
        if (list == null || list.size() == 0) {
            NLog.w(TAG, "SysCache List is error!");
            return;
        }

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            JSONArray array = toJsonArray(list);
            NLog.d(TAG, "CacheInfo array is %s", array.toString());
            String jsonOutPath = getJsonPath(SpaceApplication.getInstance().getContext(), jsonFileName);
            NLog.d(TAG, "Json  path is %s", jsonOutPath);
            File outFile = new File(jsonOutPath);
            fos = new FileOutputStream(outFile);
            bos = new BufferedOutputStream(fos);
            JSONUtil.jsonArrayOutput(array, bos);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }finally {
            IOUtils.closeSilently(bos);
            IOUtils.closeSilently(fos);
        }

    }

    private static JSONArray toJsonArray(List<CacheInfo> list) {
        int size = list.size();
        JSONArray array = new JSONArray();
        for (int i = 0; i < size; ++i) {
            array.put(list.get(i).getJSONObject());
        }
        return array;
    }

    /**
     *  获取json列表数据
     *
     *  @param path
     */
    private List<Map<String, Object>> getListFromJsonFile(String path) {

        File file = new File(path);
        InputStream inputStream;
        String string = null;
        int size = 0;
        try {
            inputStream = new FileInputStream(file);
            try {
                size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                string = new String(buffer);
                NLog.i(TAG, " read json string : %s", string);
            } catch (IOException e) {
                NLog.printStackTrace(e);
            }finally {
                IOUtils.closeSilently(inputStream);
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }

        List<Map<String, Object>> lmData = getList(string);

        return lmData;
    }

    /**
     * 把json转换为ArrayList
     *
     * @param jsonString
     */
    private static List<Map<String, Object>> getList(String jsonString) {
        List<Map<String, Object>> list = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject jsonObject;
            list = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list.add(getMap(jsonObject.toString()));
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
        return list;
    }

    /**
     * 将json 数组转换为Map 对象
     *
     * @param jsonString
     */
    private static Map<String, Object> getMap(String jsonString) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            @SuppressWarnings("unchecked")
            Iterator<String> keyIter = jsonObject.keys();
            String key;
            Object value;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            while (keyIter.hasNext()) {
                key = (String) keyIter.next();
                value = jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
        return null;
    }

    /**
     * Json还原处理
     *
     * @param lmData
     */
    private List<CacheInfo> getSystemCacheInfoList(List<Map<String, Object>> lmData) {

        if (lmData == null || lmData.size() == 0){
            NLog.w(TAG, "SysCacheInfo List is error!");
            return null;
        }
        List<CacheInfo> cacheInfos = new ArrayList<>();
        int need = 0;
        for (int i = 0; i < lmData.size(); i++) {
            CacheInfo info = new CacheInfo(JunkRequest.EM_JUNK_DATA_TYPE.SYSCACHE);
            JSONObject json = new JSONObject(lmData.get(i));
            info.setFilePath(json.optString("mFilePath"));
            info.setCheck(json.optBoolean("mbCheck"));
            info.setCleanTime(json.optInt("mCleanTime"));
            info.mPkgName = json.optString("mPkgName");
            NLog.i(TAG,"info.mPkgName %S ",info.mPkgName);
            info.setAppName(json.optString("mAppName"));
            info.setInfoType(json.optInt("mInfoType"));
            if ("Dir".equals(json.optString("mFileType"))){
               info.setFileType(BaseJunkBean.FileType.Dir);
            }
            info.setSize(json.optInt("mSize"));
            cacheInfos.add(need++, info);
//
//            pkgName.put("mFilePath", mFilePath);
//            pkgName.put("mbCheck", mbCheck);
//            pkgName.put("mCleanTime", mCleanTime);
//            pkgName.put("mPkgName", getPackageName());
//            pkgName.put("mAppName", mAppName);
//            pkgName.put("mInfoType", mInfoType);
//            pkgName.put("mFileType", getFileType());
//            pkgName.put("mJunkInfoType", getJunkDataType());//"SYSCACHE"
//            pkgName.put("mSize", mSize);
//            pkgName.put("mJunkType",getJunkType());

        }
        return cacheInfos;

    }

    /**
     * 预加载延迟4分钟处理
     * @param delay
     */
    public static void postPreLoad(long delay){
        NLog.i(TAG," start 清理完4分钟后启动系统缓存预加载 ");
        //清理完4分钟后启动系统缓存预加载
        BackgroundThread.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                NLog.i(TAG," 清理完4分钟后启动系统缓存预加载 ");
                NLog.i("timeToPreload","timeToPreload postPreLoad isFinish %s", mIsFinishe);
                //SystemCacheService.startPreloadSysCache(SpaceApplication.getInstance().getContext());
                StrategyExecutor.getInstance().execItem(StrategyExecutor.StrategyFlag.SysCache);
            }
        }, delay);
    }

    /**
     * 超过WAIT_TIME时间预加载
     */
    public static void timeToPreLoad(){
        if (getLastScanTime() == 0){
            NLog.i("timeToPreLoad"," can not preLoad!");
            return;
        }
        NLog.i("timeToPreLoad","getLastScanTime() %d", getLastScanTime());
        NLog.i("timeToPreLoad","getLastMainRestartTime() %d", getLastMainRestartTime());
        final long timeDistance = getLastMainRestartTime() - getLastScanTime();
        NLog.i("timeToPreLoad","timeDistance %d WAIT_TIME %d %s", timeDistance, WAIT_TIME, mIsFinishe);
        if (timeDistance > WAIT_TIME){
            NLog.i("timeToPreLoad","timeToPreLoad finish %s", mIsFinishe);
            postPreLoad(0);
            sLastScanTime = 0L;
            NLog.i("timeToPreLoad","timeToPreLoad ");
        }else{
            NLog.i("timeToPreLoad","time notToPreLoad ");
        }
    }

    /**
     * 获取最后一次进入垃圾扫描页面的时间
     * @return
     */
    private static long getLastScanTime(){
        NLog.i(TAG," sLastScanTime %d", sLastScanTime);
        return sLastScanTime;
    }

    /**
     * 获取最近一次主页面onRestart的时间
     * @return
     */
    private static long getLastMainRestartTime(){
        NLog.i(TAG," sLastMainRestartTime %d", sLastMainRestartTime);
        return sLastMainRestartTime;
    }
}

package com.clean.spaceplus.cleansdk.junk.engine.task;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.scan.ExtraAndroidFileScanner;
import com.clean.spaceplus.cleansdk.base.scan.IScanFilter;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskCallback;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.ServiceConfigManager;
import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.SDcardRubbishResult;
import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.clean.spaceplus.cleansdk.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/28 20:02
 * @copyright TCL-MIG
 */
public class BigFileScanTask extends ScanTask.BaseStub implements IScanFilter {


//	public static final String TAG = "BigFileScanTask";
//	public static final boolean DEBUG = true;

    private int mScanCfgMask = -1;
    private int mActiveTaskMask = 0;

    public static final int TASK_TYPE_SD_CACHE = 1;
    public static final int TASK_TYPE_LEFT_OVER = 2;
    public static final int TASK_TYPE_BIG_FILE = 4;
    public static final int SCAN_FINISH = 0x0000002;


    private static boolean scanBigfileSwitch = false;
    private static boolean scanMusicSwitch = false;
    private static boolean scanVideoSwitch = false;
    private static boolean scanPictureSwitch = false;
    private static boolean scanApkSwitch = false;

    private ScanTaskCallback mergeCallback = null;
    private LinkedBlockingQueue<BaseJunkBean> junkInfoList = new LinkedBlockingQueue<BaseJunkBean>();
    private HashMap<String, SDcardRubbishResult> leftoverUpdateMap = new HashMap<String, SDcardRubbishResult>();
    private ExtraAndroidFileScanner mEAFScanner = new ExtraAndroidFileScanner();

    public void addLeftoverUpdateMap(SDcardRubbishResult dcardRubbishResult) {
        leftoverUpdateMap.put(dcardRubbishResult.getStrDirPath(), dcardRubbishResult);
    }


    public void setMergeCallback(ScanTaskCallback mergeCallback) {
        this.mergeCallback = mergeCallback;
    }

    public BigFileScanTask() {
        Context mCtxContext = SpaceApplication.getInstance().getContext();
        ServiceConfigManager mConfig = ServiceConfigManager.getInstanse(mCtxContext);
        scanBigfileSwitch = mConfig.getScanBigFileFlag();
        scanMusicSwitch = mConfig.isFilterBigFileType(ExtraAndroidFileScanner.EF_TYPE_AUDIO);
        scanVideoSwitch = mConfig.isFilterBigFileType(ExtraAndroidFileScanner.EF_TYPE_VIDEO);
        scanPictureSwitch = mConfig.isFilterBigFileType(ExtraAndroidFileScanner.EF_TYPE_PICTURE);
        scanApkSwitch = mConfig.isFilterBigFileType(ExtraAndroidFileScanner.EF_TYPE_APK);
    }

    private static List<String> mExternalStoragePaths = null;
    /**
     * 缓存是否需要移动到大文件
     * @param path
     * @return
     */
    public static boolean isIgnoreItem(String path){
        if(path == null){
            return true;
        }
        if(mExternalStoragePaths == null){
            mExternalStoragePaths = (new StorageList()).getMountedVolumePaths();//外存卡路径列表（可能多个）
        }
        if (null != mExternalStoragePaths && !mExternalStoragePaths.isEmpty()) {
            for (String sd : mExternalStoragePaths) {
                if(path.equalsIgnoreCase(sd + "/baidumap/vmp")
                        ||(!scanVideoSwitch && ( path.equalsIgnoreCase(sd + "/baofeng/.download")
                        || path.equalsIgnoreCase(sd + "/qvod")
                        || path.equalsIgnoreCase(sd + "/p2pcache"))))
                    return true;

            }
        }
        return false;
    }
    public static boolean isScanBigfile(){
        return scanBigfileSwitch;
    }

    public static boolean isFilterCombineRubbish(CacheInfo cacheInfo){
        if(!isScanBigfile()){
            return true;
        }else if(scanApkSwitch && cacheInfo.isApkType()){
            return true;
        }else if(scanMusicSwitch && cacheInfo.isMusicType()){
            return true;
        }else if(scanPictureSwitch && cacheInfo.isImageType()){
            return true;
        }else if(scanVideoSwitch && cacheInfo.isVideoType()){
            return true;
        }else if(isIgnoreItem(cacheInfo.getFilePath())){
            return true;
        }
        return false;
    }

    /**
     * @param caller 取值定义为cm_task_time.CM_TASK_TIME_USER_*
     */
    public void setCaller(byte caller) {
        mEAFScanner.setCaller(caller);
    }
    /**
     * 第一次使用
     * mTimeRpt mFirstScan均为true
     */
    public void setFirstScanFlag() {
        mEAFScanner.setFirstScanFlag();
    }

    private class CombineThread extends Thread{

        /**
         * 大文件应该去重的文件
         */
        private HashSet<String> sdCardDeleteBigFile = new HashSet<String>();
        /**
         * 缓存或者残留应该去重的文件
         */
        private HashSet<String> cacheInfoOrLeftoverDeleteBigFile = new HashSet<String>();
        /**
         * key:缓存或者残留的路径
         * value:是否抛出过size
         */
        private TreeMap<String, Boolean> cacheInfoOrLeftoverMap = new TreeMap<String, Boolean>();
        /**
         * key: 大文件的路径
         * value:是否抛出过size
         */
        private TreeMap<String, Boolean> sdCardMap = new TreeMap<String, Boolean>();
        /**
         * 所有的大文件（包含扩展）
         */
        private List<BaseJunkBean> allbigFile = new ArrayList<BaseJunkBean>();
        private final ScanTaskController controller;

        public CombineThread(ScanTaskController controller) {
            this.controller = controller;
        }
        @Override
        public void run() {

            try{

                //抛出size，先进入队列的先抛，后面发现重复的或者包含（被包含）的都不抛
                while(true){
                    if(controller != null && controller.checkStop()){
                        break;
                    }else if(junkInfoList.size() == 0){
                        if(mActiveTaskMask == 0){
                            break;
                        }else{
                            try {
                                sleep(128);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                    }

                    BaseJunkBean base = junkInfoList.poll();
                    if(base == null)
                        continue;

                    if(base instanceof CacheInfo){
                        //缓存去重
                        CacheInfo cacheInfo = (CacheInfo) base;
                        String path = cacheInfo.getFilePath();
                        if(path == null)
                            continue;
                        path = StringUtils.toLowerCase(path);

                        if(cacheInfoOrLeftoverMap.containsKey(path)){
                            continue;
                        }
                        deleteDuplicateBigfile(path,base);
                    }else  if(base instanceof SDcardRubbishResult){
                        SDcardRubbishResult dcardRubbishResult = ((SDcardRubbishResult) base);
                        String path = dcardRubbishResult.getStrDirPath();
                        if(path == null)
                            continue;
                        path = StringUtils.toLowerCase(path);
                        //残留
                        if(dcardRubbishResult.getType() == SDcardRubbishResult.RF_APP_LEFTOVERS){
                            //大文件去重
                            deleteDuplicateBigfile(path,base);
                        }else if(sdCardMap.containsKey( path) || cacheInfoOrLeftoverMap.containsKey(path)){
                            continue;
                        }else{
                            //ExtraAndroidFileScanner中的大文件
                            String backPath = null;
                            if(Build.VERSION.SDK_INT > 8){
                                backPath = getDir(path);
                            }else{
                                backPath = getDirPath(path);
                            }

                            if(backPath != null && path.startsWith(FileUtils.addSlash(backPath))){
                                if(!cacheInfoOrLeftoverMap.get(backPath)){
                                    //抛出size
                                    if (null != mCB) {
                                        mCB.callbackMessage(ExtraAndroidFileScanner.ADD_SIZE_DATA_ITEM_TO_ADAPTER,0, 0, base);
                                    }
                                }
                                continue;
                            }else{
                                sdCardMap.put(path, true);

                                //抛出size
                                if (null != mCB) {
                                    mCB.callbackMessage(ExtraAndroidFileScanner.ADD_SIZE_DATA_ITEM_TO_ADAPTER,0, 0, base);
                                }
                            }
                        }
                    }
                    allbigFile.add(base);
                }

            } finally {

                //最后抛出所有的item
                if (null != mCB) {
                    for(BaseJunkBean base : allbigFile){
                        if(base instanceof CacheInfo){
                            CacheInfo cacheInfo = (CacheInfo) base;
                            if(cacheInfoOrLeftoverDeleteBigFile.contains(StringUtils.toLowerCase(cacheInfo.getFilePath()))){
                                continue;
                            }
                            //抛出单个缓存
                            cacheInfo.setExtendType(CacheInfo.TYPE_BIG_FILE_EXTEND_CACHE);
                            mCB.callbackMessage(ExtraAndroidFileScanner.ADD_CACHEINFO_DATA_ITEM_TO_ADAPTER,ExtraAndroidFileScanner.RF_CACHE_INFO , 0, base);
                        }else{
                            SDcardRubbishResult dcardRubbishResult = ((SDcardRubbishResult)base);

                            if(dcardRubbishResult.getType() == SDcardRubbishResult.RF_BIG_FILES){
                                if(sdCardDeleteBigFile.contains(StringUtils.toLowerCase(dcardRubbishResult.getStrDirPath()))){
                                    continue;
                                }
                                //抛出单个大文件
                                mCB.callbackMessage(ExtraAndroidFileScanner.ADD_BIGFILE_DATA_ITEM_TO_ADAPTER, ExtraAndroidFileScanner.RF_BIG_FILES, 0, base);
                            }else{
                                if(cacheInfoOrLeftoverDeleteBigFile.contains(StringUtils.toLowerCase(dcardRubbishResult.getStrDirPath()))){
                                    continue;
                                }
                                //只有残留才会update
                                if(leftoverUpdateMap.containsKey(dcardRubbishResult.getStrDirPath())){
                                    dcardRubbishResult = leftoverUpdateMap.get(dcardRubbishResult.getStrDirPath());
                                }
                                //抛出单个残留
                                dcardRubbishResult.setExtendType(SDcardRubbishResult.TYPE_BIG_FILE_EXTEND_LEFTOVER);
                                mCB.callbackMessage(ExtraAndroidFileScanner.ADD_LEFTOVER_DATA_ITEM_TO_ADAPTER, ExtraAndroidFileScanner.RF_APP_LEFTOVERS, 0, dcardRubbishResult);
                            }
                        }
                    }
                }

                cacheInfoOrLeftoverDeleteBigFile.clear();
                sdCardDeleteBigFile.clear();
                sdCardMap.clear();
                cacheInfoOrLeftoverMap.clear();
                leftoverUpdateMap.clear();

                //nilo 延后
//                mEAFScanner.reportEndScan();

                if (null != mCB) {
                    //回调JunkAdvancedScan 中的大文件SCAN_FINISH
                    mCB.callbackMessage(SCAN_FINISH,
                            (null != controller && ScanTaskController.TASK_CTRL_TIME_OUT == controller.getStatus()) ? 1 : 0, 0, null);
                }
            }
        }

        private void deleteDuplicateBigfile(String path,BaseJunkBean base){
            if(sdCardMap.containsKey(path)){
                sdCardMap.remove(path);
                sdCardDeleteBigFile.add(path);
            }
            if(Build.VERSION.SDK_INT > 8){
                deleteSdcardRubbishPath(path,base);
            }else{
                enumDeleteSdcardRubbishPath(path,base);
            }
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        private String getDir(String path){
            String tempPath = cacheInfoOrLeftoverMap.lowerKey(path);
            return tempPath == null?tempPath:tempPath;
        }

        private String getDirPath(String path){
            Set<String> set = cacheInfoOrLeftoverMap.keySet();
            if(set != null){
                Iterator<String> iterator =  set.iterator();
                String tempPath = null;
                while(iterator.hasNext()){
                    tempPath = iterator.next();
                    if(tempPath != null && path.startsWith(FileUtils.addSlash(tempPath))){
                        return tempPath;
                    }
                }
            }
            return null;
        }

        private void enumDeleteSdcardRubbishPath(String path,BaseJunkBean base){

            boolean hasContain = false;
            Set<Map.Entry<String, Boolean>> set = sdCardMap.entrySet();
            if(set != null){
                Iterator<Map.Entry<String, Boolean>> iterator = set.iterator();
                String backPath = null;
                while(iterator.hasNext()){
                    backPath = iterator.next().getKey();
                    if(backPath != null && backPath.startsWith(FileUtils.addSlash(path))){
                        sdCardDeleteBigFile.add(backPath);
                        hasContain = true;
                        iterator.remove();
                    }
                }
            }

            //缓存去重
            Set<Map.Entry<String, Boolean>> cacheSet = cacheInfoOrLeftoverMap.entrySet();
            if(cacheSet != null){
                Iterator<Map.Entry<String, Boolean>> iterator =  cacheSet.iterator();
                String tempPath = null;
                while(iterator.hasNext()){
                    tempPath = iterator.next().getKey();
                    if(path.startsWith(FileUtils.addSlash(tempPath))){
                        cacheInfoOrLeftoverDeleteBigFile.add(path);
                        return;
                    } else if(tempPath.startsWith(FileUtils.addSlash(path))){
                        hasContain = true;
                        iterator.remove();
                        cacheInfoOrLeftoverDeleteBigFile.add(tempPath);
                        break;
                    }
                }
            }

            if(hasContain){
                cacheInfoOrLeftoverMap.put(path, false);
            }else{
                //抛出size
                if (null != mCB) {
                    mCB.callbackMessage(ExtraAndroidFileScanner.ADD_SIZE_DATA_ITEM_TO_ADAPTER,0, 0, base);
                }
                cacheInfoOrLeftoverMap.put(path, true);
            }
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        private void deleteSdcardRubbishPath(String path,BaseJunkBean base){
            //大文件去重
            String backPath = path;
            boolean hasContain = false;
            do{
                backPath = sdCardMap.higherKey(backPath);
                if(backPath != null && backPath.startsWith(FileUtils.addSlash(path))){
                    sdCardMap.remove(backPath);
                    sdCardDeleteBigFile.add(backPath);
                    hasContain = true;
                }else{
                    backPath = null;
                }
            }while(backPath != null);

            //缓存去重
            String cachePath = cacheInfoOrLeftoverMap.lowerKey(path);
            if(cachePath != null  &&  path.startsWith(FileUtils.addSlash(cachePath))){
                cacheInfoOrLeftoverDeleteBigFile.add(path);
                return;
            }else{
                cachePath = cacheInfoOrLeftoverMap.higherKey(path);
                if(cachePath != null  &&  cachePath.startsWith(FileUtils.addSlash(path))){
                    cacheInfoOrLeftoverDeleteBigFile.add(cachePath);
                    hasContain = true;
                    cacheInfoOrLeftoverMap.remove(cachePath);
                }
            }

            if(hasContain){
                cacheInfoOrLeftoverMap.put(path, false);
            }else{
                //抛出size
                if (null != mCB) {
                    mCB.callbackMessage(ExtraAndroidFileScanner.ADD_SIZE_DATA_ITEM_TO_ADAPTER,0, 0, base);
                }
                cacheInfoOrLeftoverMap.put(path, true);
            }
        }

    }

    @Override
    public boolean scan(ScanTaskController ctrl) {
//        Commons.WriteVerInfoLog("bigFile_scan");
        if (null == ctrl || !ctrl.checkStop()){
            setTaskActive(TASK_TYPE_BIG_FILE);

            Thread ct = new CombineThread(ctrl);
            ct.start();

            mEAFScanner.scanInternBigFile(mergeCallback,mCB,ctrl,mScanCfgMask);

            finishScanTask(TASK_TYPE_BIG_FILE);

            try {
                ct.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }



    public void setScanConfigMask(int mask) {
        mScanCfgMask = mask;
    }

    public int getScanConfigMask() {
        return mScanCfgMask;
    }

    // 对于3.0以上的系统，此接口无效，默认使用mediaScanner全盘扫
/*	public void setBigFileScanFolderLevel(int nLevel){
		mEAFScanner.setBigFileScanFolderLevel(nLevel);
	}*/

/*	public int getBigFileScanFolderLevel(){
		return mEAFScanner.getBigFileScanFolderLevel();
	}*/


    @Override
    public String getTaskDesc() {
        return "BigFileScanTask";
    }

    @Override
    public boolean isFilter(String name) {
        return false;
    }

    public void finishScanTask(int taskType) {
        mActiveTaskMask &= (~taskType);
    }

    public void setTaskActive(int type) {
        mActiveTaskMask |= type;
    }

    public void putBaseJunkBean(BaseJunkBean base){
        if(base == null)
            return;
        junkInfoList.offer(base);
    }

}
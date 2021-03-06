package com.clean.spaceplus.cleansdk.junk.engine.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.junk.engine.PathCallback;
import com.clean.spaceplus.cleansdk.junk.engine.PathListCallback;
import com.clean.spaceplus.cleansdk.junk.engine.ProgressCtrl;
import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.clean.spaceplus.cleansdk.util.StringUtils;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/28 20:20
 * @copyright TCL-MIG
 */
public class CalculateFolderSizeUtil {
    static ArrayList<String> msdRootList = null;
    public static final int MEDIA_STORE_CHECK_COUNT_LIMIT = 10;
    public static final int MEDIA_STORE_CALC_ALL_COUNT_LIMIT = 5000;

    private static String getNoRootPath(String strPathString) {
        if (msdRootList == null) {
            return null;
        }
        for (String rootString : msdRootList) {
            if (strPathString.startsWith(rootString)) {
                return strPathString.substring(rootString.length());
            }
        }
        return null;
    }

    public CalculateFolderSizeUtil() {
        msdRootList = (new StorageList()).getMountedVolumePaths();
        if (msdRootList != null) {
            for (int i = 0; i < msdRootList.size(); i++) {
                msdRootList.set(i, FileUtils.addSlash(msdRootList.get(i)));
            }
        }
        initUseMediaStoreCalcSLDFolder();
        initUseMediaStoreCalcOldFileFolder();
    }

    private class MSCalcItem {
        public MSCalcItem(String pathString, long nModifiedTime_s,
                          long nFilesCount) {
            this.pathString = pathString;
            this.nModifiedTime_s = nModifiedTime_s;
            this.nFilesCount = nFilesCount;
        }

        public String pathString;
        public long nModifiedTime_s;
        public long nFilesCount;
    }


    /// 抽查里面的nFileCountLimit个文件，确认MediaStore与SD卡文件一致
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static boolean checkMediaStoreFileSDConstiancy(ProgressCtrl progCtrl, String strFilePath, int nFileCountLimit) {
        String strSelection = "format != 12289 and _data > ? and _data < ? ) limit (" + nFileCountLimit;
        Cursor cursor = null;
        final String[] projection = {
                MediaStore.Files.FileColumns.DATA};

        boolean bRet = false;
        try {
            Context context = SpaceApplication.getInstance().getContext();
            cursor = context
                    .getContentResolver()
                    .query(MediaStore.Files.getContentUri("external"),
                            projection, strSelection,
                            new String[]{FileUtils.addSlash(strFilePath), FileUtils.replaceEndSlashBy0(strFilePath)}, null);
            if (cursor != null && cursor.moveToFirst()) {
                bRet = true;
                do {
                    if (progCtrl.isStop()) {
                        bRet = false;
                        break;
                    }
                    String strFileName = cursor.getString(0);
                    File file = new File(strFileName);
                    if (!file.isFile()) {
                        bRet = false;
                        break;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            bRet = false;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return bRet;
    }

    public boolean getAllFilesByMediaStore(String path, long[] result, ProgressCtrl progCtrl, PathCallback pathCallback,
                                           PathListCallback pathListCallback, List<String> allfileList) {
        result[0] = 0;
        result[1] = 0;
        result[2] = 0;
        allfileList.clear();
        NLog.d("sdCardCacheScanTask:CalculateFolderSizeUtil:getAllFilesByMediaStore1",path);
        long nLimitUnit = MEDIA_STORE_CALC_ALL_COUNT_LIMIT;
        long nLimitOffset = 0;
        AtomicBoolean bCallNext = new AtomicBoolean(true);
        try{
            for (int i = 0; i < 3; i++) {

                if (!getAllFilesByMediaStore(path, result,progCtrl, pathCallback, pathListCallback, allfileList, nLimitUnit, nLimitOffset, bCallNext)) {
                    return false;
                }
                nLimitOffset += nLimitUnit;
                if (!bCallNext.get()) {
                    break;
                }
            }
        }catch (Error e) {
            int nSize = allfileList.size();
            allfileList.clear();
            OutOfMemoryError outOfMemoryError = new OutOfMemoryError("getAllFilesByMediaStore path:" + path + " size:" + nSize);
            outOfMemoryError.initCause(e);
            throw outOfMemoryError;
        }

        result[1] = 1;
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean getAllFilesByMediaStore(String path, long[] result, ProgressCtrl progCtrl, PathCallback pathCallback,
                                           PathListCallback pathListCallback, List<String> allfileList, long nLimit, long nLimitOffset, AtomicBoolean bCallNext) {
        boolean bRet = true;
        NLog.d("sdCardCacheScanTask:CalculateFolderSizeUtil:getAllFilesByMediaStore",path);
        String selection = "format != 12289 and _data > ? and _data < ?";
        Cursor cursor = null;
        final String[] projection = {
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_MODIFIED};
        bCallNext.set(false);
        long nTotalSize = 0;
        long nCount = 0;
        int nBasePathLen = FileUtils.removeSlash(path).length();
        try {
            Context context = SpaceApplication.getInstance().getContext();
            cursor = context
                    .getContentResolver()
                    .query(MediaStore.Files.getContentUri("external"),
                            projection, selection,
                            new String[] {FileUtils.addSlash(path),FileUtils.replaceEndSlashBy0(path)},  MediaStore.Files.FileColumns._ID +" limit "+nLimit+" offset "+nLimitOffset);
            if (cursor != null && cursor.moveToFirst()) {
                if (cursor.getCount() == nLimit) {
                    bCallNext.set(true);
                }
                do {
                    if (progCtrl.isStop()) {
                        bRet = false;
                        break;
                    }
                    nCount++;
                    final long nSize = cursor.getLong(0);
                    final String subPathString = cursor.getString(1);
                    final long fileModifyTime = cursor.getLong(2);

                    if (pathCallback == null || (pathCallback != null && pathCallback.OnFilter(subPathString, fileModifyTime))) {
                        result[0] += nSize;
                        result[2] += 1;
                        nTotalSize += nSize;
                        allfileList.add(subPathString.substring(nBasePathLen));

                        if (pathCallback != null) {
                            pathCallback.onFile(subPathString, nSize, 0, 0, 0);
                        }
                    }
                    if (nCount % 100 == 0) {
                        if (pathListCallback != null) {
                            pathListCallback.onFile((int)nTotalSize);
                        }
                        nTotalSize = 0;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            bRet = false;
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }

        if (pathListCallback != null) {
            pathListCallback.onFile((int)nTotalSize);
        }
        return bRet;
    }

    public void useMediaStoreCalcSLDFolder(String path, long[] result,
                                           ProgressCtrl progCtrl, PathCallback pathCallback,
                                           PathListCallback pathListCallback, final boolean [] msInfo ) {
        long nStartTime = SystemClock.uptimeMillis();
        NLog.d("sdCardCacheScanTask:CalculateFolderSizeUtil:useMediaStoreCalcSLDFolder",path);
        final ArrayList<MSCalcItem> list = new ArrayList<MSCalcItem>();
        PathOperFunc.NeedMSCalcCallback msCalcCallback = new PathOperFunc.NeedMSCalcCallback() {
            @Override
            public void onFolder(String strPath, long nModifiedTime_s,
                                 long FilesCount) {
                //Log.d("sytest", "****" + strPath + "**** nModifiedTime_s:"
                //		+ nModifiedTime_s + " FilesCount:" + FilesCount);
                msInfo[0] = true;
                msInfo[1] = false;
                list.add(new MSCalcItem(strPath, nModifiedTime_s, FilesCount));
            }
        };

        PathOperFunc.computeFileSizeNeedMSCalc(path, result, progCtrl,
                pathCallback, pathListCallback, false, 100, 3 * 60 * 60,
                msCalcCallback);
        for (MSCalcItem msCalcItem : list) {
            long[] result2 = new long[] { 0, 0, 0 };
            if (computeFileSizeByMediaStoreSLD(msCalcItem.pathString,
                    msCalcItem.nModifiedTime_s, msCalcItem.nFilesCount, result2)) {
                result[0] += result2[0];
            } else {
                msInfo[0] = false;
                msInfo[1] = true;
                PathOperFunc.computeFileSizeNeedMSCalc(msCalcItem.pathString,
                        1, progCtrl, result2, null, null, pathListCallback,
                        false, 0, 0, null);
                result[0] += result2[0];
            }
        }

        long nEndTime = SystemClock.uptimeMillis();
        //Log.v("sytest", "path:" + path + " result[0]:" + result[0] + "[1]:"
        //		+ result[1] + "[2]:" + result[2] + " usedTime:"
        //		+ (nEndTime - nStartTime));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean computeFileSizeOnlyByMediaStore(String path, long[] result, ProgressCtrl progCtrl, long[] MediaInfo, PathCallback pathCallback) {
        long nOneDay_s = 24 * 60 * 60;
        long nCurTime_s = System.currentTimeMillis() / 1000;
        long nStartTime = SystemClock.uptimeMillis();
        NLog.d("sdCardCacheScanTask:CalculateFolderSizeUtil:computeFileSizeOnlyByMediaStore",path);
        String selection = "format != 12289 and _data > ? and _data < ? ";
        Cursor cursor = null;
        final String[] projection = {
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.DATA};
        if (MediaInfo != null) {
            Arrays.fill(MediaInfo, 0);
        }
        boolean bRetResult = false;
        try {
            Context context = SpaceApplication.getInstance().getContext();
            cursor = context
                    .getContentResolver()
                    .query(MediaStore.Files.getContentUri("external"),
                            projection, selection,
                            new String[] {FileUtils.addSlash(path),FileUtils.replaceEndSlashBy0(path)}, null);
            //Log.d("sytest", "UMSCFS. computeFileSizeOnlyByMediaStore path:"+path +" query OK. usedTime:"+(SystemClock.uptimeMillis()-nStartTime));
            if (cursor != null && cursor.moveToFirst()) {
                //Log.d("sytest", "UMSCFS. computeFileSizeOnlyByMediaStore path:"+path +" moveToFirst OK. usedTime:"+(SystemClock.uptimeMillis()-nStartTime));
                long nTotalSize = 0;
                long nLastAddTime_s = 0;
                long nLastModifiedTime_s = 0;
                long nTotalCount = 0;
                do {
                    if (progCtrl.isStop()) {
                        break;
                    }

                    final String filePath = cursor.getString(4);
                    final long fileModifyTime = cursor.getLong(2);
                    if (pathCallback == null || (pathCallback != null && pathCallback.OnFilter(filePath, fileModifyTime))) {
                        nTotalSize += cursor.getLong(0);
                        nTotalCount += 1;
                    }

                    if (cursor.getLong(1) > nLastAddTime_s) {
                        nLastAddTime_s = cursor.getLong(1);
                    }
                    if (fileModifyTime > nLastModifiedTime_s) {
                        nLastModifiedTime_s = fileModifyTime;
                    }

                    if (MediaInfo != null && cursor.getString(3) != null) {
                        String strMimeType = cursor.getString(3);
                        if (strMimeType.startsWith("image/")) {
                            MediaInfo[1] += 1;
                        } else if(strMimeType.startsWith("audio/")) {
                            MediaInfo[2] += 1;
                        } else if (strMimeType.startsWith("video/")) {
                            MediaInfo[0] += 1;
                        }
                    }

                } while (cursor.moveToNext());

                //Log.d("sytest", "UMSCFS. computeFileSizeOnlyByMediaStore path:"+path +" getTSize:"+nTotalSize+" lastAddTime:"+
                //		dateFm.format(new Date(nLastAddTime_s * 1000))+" lastModifiedTime:"+dateFm.format(new Date(nLastModifiedTime_s * 1000))+" CurrentTime:"+dateFm.format(new Date(nCurTime_s * 1000))+" totalCount:"+nTotalCount);
                if ((nLastAddTime_s + nOneDay_s >= nCurTime_s)
                        || (nLastModifiedTime_s + nOneDay_s >= nCurTime_s)) {
                    result[0] = nTotalSize;
                    result[1] = 1;
                    result[2] = nTotalCount;
                    if (nTotalCount >= MEDIA_STORE_CALC_ALL_COUNT_LIMIT) {
                        bRetResult = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        if ( !bRetResult ) {
            if ( MediaInfo != null ) {
                Arrays.fill( MediaInfo, 0 );
            }
        }
        //Log.d("sytest", "UMSCFS. computeFileSizeOnlyByMediaStore path:"+path +" ret:"+bRetResult);
        return bRetResult;
    }

    private boolean useMediaStoreCalcOldFileFolder( String path, long[] result,
                                                    ProgressCtrl progCtrl, PathCallback pathCallback,
                                                    PathListCallback pathListCallback, long [] mediaInfo, boolean isPureUserMSFolder ) {
        Arrays.fill(result, 0);
        NLog.d("sdCardCacheScanTask:CalculateFolderSizeUtil:useMediaStoreCalcOldFileFolder",path);
        if (!checkMediaStoreFileSDConstiancy(progCtrl, path, MEDIA_STORE_CHECK_COUNT_LIMIT)) {
            return false;
        }
        boolean bSuccessCalc = false;

        if (isPureUserMSFolder) {
            bSuccessCalc = computeFileSizeOnlyByMediaStore(path, result, progCtrl, mediaInfo, pathCallback);
        }

        if (!bSuccessCalc && result[2] < MEDIA_STORE_CALC_ALL_COUNT_LIMIT) {  // 针对使用纯MediaStore计算，总数小于MEDIA_STORE_CALC_ALL_COUNT_LIMIT，则用其他方式计算
            long[] result2 = new long[] { 0, 0, 0 };
            LinkedList<String> allFilesList = new LinkedList<String>();
            bSuccessCalc = getAllFilesByMediaStore(path, result2, progCtrl, pathCallback, pathListCallback, allFilesList);
            if (bSuccessCalc) {
                PathOperFunc.computeNewFileSize(path, progCtrl, result, pathCallback, pathListCallback, false, allFilesList);
                //Log.d("sytest", "UMSCFS. computeNewFileSize path:"+path + " all OK.["+result[0]+","+result[1]+","+result[2]+"] usedTime:"+(SystemClock.uptimeMillis()-nStartTime));
                //特殊目录大小计算重复相加
                //result[0] += result2[0];
                //result[2] += result2[2];
            }
        }
        return bSuccessCalc;
    }

    /**
     * @param path
     * @param result
     * @param progCtrl
     * @param pathCallback
     * @param pathListCallback
     * @param mediaInfo
     * @param msInfo
     * msInfo[0] 是否是使用MediaStore扫描的.true为是使用MediaStore扫描。false是未使用MediaStore扫描
     * msInfo[1] MediaStore是否失效标志.true为失效。false是未失效
     * @return
     */
    private boolean computeFileSizeUseMS(String path, long[] result,
                                         ProgressCtrl progCtrl, PathCallback pathCallback,
                                         PathListCallback pathListCallback, long [] mediaInfo, boolean [] msInfo ) {
        msInfo[0] = false;
        msInfo[1] = false;
        NLog.d("sdCardCacheScanTask:CalculateFolderSizeUtil:computeFileSizeUseMS",path);
        if (!isCanUseMediaStore()) {
            return false;
        }
        String pathNoRoot = getNoRootPath(path);
        if (pathNoRoot == null) {
            return false;
        }

        pathNoRoot = StringUtils.toLowerCase(pathNoRoot);
        pathNoRoot = FileUtils.addSlash(pathNoRoot);
        if (pathNoRoot.startsWith("android/data/")) {
            return false;
        }

        if(isUseMediaStoreCalcOldFileFolder(pathNoRoot)) {
            boolean bRet = useMediaStoreCalcOldFileFolder(path, result, progCtrl, pathCallback, pathListCallback, mediaInfo, isPureUseMediaStoreCalcFolder(pathNoRoot));
            if ( !bRet ) {
                msInfo[1] = true; //使用MediaStore失效
            }else {
                msInfo[0] = true; //使用MediaStore查询
            }
            return bRet;
        }

        if (pathCallback == null && isUseMediaStoreCalcSLDFolder(pathNoRoot)) {
            useMediaStoreCalcSLDFolder(path, result, progCtrl, pathCallback,
                    pathListCallback, msInfo );
            return true;
        }
        return false;
    }

    /**
     * @param path 待检查大小的文件路径
     * @param result 结果
     * @param progCtrl
     * @param pathCallback 计算过程中的回调
     * @param MediaInfo
     * @param filterSubDirList
     * @param msInfo
     * msInfo[0] 是否是使用MediaStore扫描的.true为是使用MediaStore扫描。false是未使用MediaStore扫描
     * msInfo[1] MediaStore是否失效标志.true为失效。false是未失效
     */
    public void computeFileSize(String path,
                                long[] result,
                                ProgressCtrl progCtrl,
                                PathCallback pathCallback,
                                long[] MediaInfo,
                                List<String> filterSubDirList,
                                boolean[] msInfo) {
        msInfo[0] = false;
        msInfo[1] = false;
        NLog.d("sdCardCacheScanTask:CalculateFolderSizeUtil:computeFileSize",path);
        if (!computeFileSizeUseMS(path, result, progCtrl, pathCallback, null, MediaInfo, msInfo)) {
            PathOperFunc.computeFileSize(path, result, progCtrl, pathCallback, filterSubDirList);
        }
    }

    public boolean computePatchFileSize(List<String> pathList, CacheInfo.FileType fileType, boolean isFilterNoMedia,
                                        boolean isTimeLine, int timeLine, long[] result,
                                        long[] resultCleanTime, ProgressCtrl progCtrl,
                                        List<String> targetFiles, final PathListCallback pathCallback, long[] MediaInfo, boolean [] msInfo ) {

        msInfo[0] = false; //是否是使用MediaStore扫描的.true为是使用MediaStore扫描。false是未使用MediaStore扫描
        msInfo[1] = false; //MediaStore是否失效标志.true为失效。false是未失效
        if (null == pathList || pathList.isEmpty()) {
            return false;
        }
        NLog.d("sdCardCacheScanTask:CalculateFolderSizeUtil:computePatchFileSize",pathList.toString());
        long nStartTime = SystemClock.uptimeMillis();
        if (!isTimeLine || !(fileType == CacheInfo.FileType.File)) {
            //TODO: only use first Dir right now
            String path = pathList.get(0);
            if (computeFileSizeUseMS(path, result, progCtrl, null, pathCallback, MediaInfo,msInfo)) {
                long nEndTime = SystemClock.uptimeMillis();
                //Log.i( "syCalcSize", "5.8.5.CPFS.path:"+path+" result:["+result[0]+","+result[1]+","+result[2]+"] usedTime:"+(nEndTime-nStartTime) );
                return true;
            }
        }

        boolean bRet = PathOperFunc.computePatchFileSize(pathList, isFilterNoMedia,
                isTimeLine, timeLine, result, resultCleanTime, progCtrl,
                targetFiles, pathCallback,null);
        long nEndTime = SystemClock.uptimeMillis();
        //Log.i( "syCalcSize", "5.8.5.CPFS.path:"+path+"result:["+result[0]+","+result[1]+","+result[2]+"] usedTime:"+(nEndTime-nStartTime) );
        return bRet;
    }

    private boolean isCanUseMediaStore() {
        if (android.os.Build.VERSION.SDK_INT < 11) {
            return false;
        }
        return true;
    }

    private TreeSet<String> museMS_SLD_folders = new TreeSet<String>();

	/* Single-Level Directory : SLD */
    /**
     * 针对单层目录下文件个数很多，又不经常增加，删除，修改 的目录
     */
    private void initUseMediaStoreCalcSLDFolder() {
        TreeSet<String> foldersSet = museMS_SLD_folders;
        foldersSet.add(newStringByBase64("dGVuY2VudC9jb20vdGVuY2VudC9tb2JpbGVxcQ==")); // "tencent/com/tencent/mobileqq"
        foldersSet.add(newStringByBase64("Z2FtZWxvZnQvZ2FtZXMvR2xvZnRBOENO")); // "gameloft/games/GloftA8CN"
    }

    private static String newStringByBase64(String base64String) {
        String pathString = new String(Base64.decode(base64String,Base64.DEFAULT));
        return StringUtils.toLowerCase(FileUtils.addSlash(pathString));
    }

    private boolean isUseMediaStoreCalcSLDFolder(String pathString) {
        return museMS_SLD_folders.contains(pathString);
    }

    private TreeSet<String> museMS_CalcOldFile_folders = new TreeSet<String>();
    private TreeSet<String> museMS_CalcOldFile_folders_regex = new TreeSet<String>(); //针对正则表达式的
    /**
     * 针对子目录很多或者文件也很多，但是经常被添加删除文件（文件创建后就不再修改）的目录
     */
    private void initUseMediaStoreCalcOldFileFolder() {
        TreeSet<String> foldersSet = museMS_CalcOldFile_folders;
        foldersSet
                .add(newStringByBase64("c2luYS93ZWliby8uaW50ZXJlc3Q=")); // "sina/weibo/.interest"
        foldersSet
                .add(newStringByBase64("c2luYS93ZWliby8ucG9ydHJhaXRuZXc=")); // "sina/weibo/.portraitnew"
        foldersSet.add(newStringByBase64("c2luYS93ZWliby8ucHJlbmV3")); // "sina/weibo/.prenew"
        foldersSet
                .add(newStringByBase64("c2luYS93ZWliby8ud2VpYm9fcGljX25ldw==")); // "sina/weibo/.weibo_pic_new"
        foldersSet
                .add(newStringByBase64("c2luYS93ZWliby9zbWFsbF9wYWdl")); // "sina/weibo/small_page"
        foldersSet
                .add(newStringByBase64("dGVuY2VudC9Nb2JpbGVRUS9kaXNrY2FjaGU=")); // "tencent/MobileQQ/diskcache"
        foldersSet.add(newStringByBase64("amluZ2RvbmcvaW1hZ2U=")); // "jingdong/image"
        foldersSet.add(newStringByBase64("amluZ2RvbmcvanNvbg==")); // "jingdong/json"
        foldersSet
                .add(newStringByBase64("dGVuY2VudC9Nb2JpbGVRUS9oZWFkL19oZA==")); // "tencent/MobileQQ/head/_hd"
        foldersSet.add(newStringByBase64("VUNEb3dubG9hZHMvY2FjaGU=")); // "UCDownloads/cache"
        foldersSet.add(newStringByBase64("a3Vnb3UvLmltYWdlcw==")); // "kugou/.images"
        foldersSet.add(newStringByBase64("a3Vnb3UvLmZzc2luZ2VycmVz")); // "kugou/.fssingerres"
        foldersSet.add(newStringByBase64("c25vd2JhbGwvaW1hZ2VfY2FjaGU=")); // "snowball/image_cache"
        foldersSet.add( newStringByBase64("dGVuY2VudC9UZW5jZW50bmV3cy9kYXRhL25ld3NfaW1hZ2U=")); // "tencent/Tencentnews/data/news_image"
        foldersSet.add(newStringByBase64("VGVuY2VudC9tb2JpbGVRUS9xYml6L2h0bWw1")); // "Tencent/mobileQQ/qbiz/html5"
        foldersSet.add(newStringByBase64("YXV0b25hdmkvbWluaV9tYXB2Mw==")); //"autonavi/mini_mapv3"


        TreeSet<String> foldersSet_reg = museMS_CalcOldFile_folders_regex;
        foldersSet_reg.add(newStringByBase64("dGVuY2VudC9taWNyb21zZy9bMC05YS16YS16XXszMn0vaW1hZ2Uy")); // "tencent/micromsg/[0-9a-za-z]{32}/image2"
        foldersSet_reg.add(newStringByBase64("dGVuY2VudC9taWNyb21zZy9bMC05YS16YS16XXszMn0vdm9pY2Uy")); // "tencent/micromsg/[0-9a-za-z]{32}/voice2"
        foldersSet_reg.add(newStringByBase64("dGVuY2VudC9taWNyb21zZy9bMC05YS16YS16XXszMn0vZW1vamk=")); // "tencent/micromsg/[0-9a-za-z]{32}/emoji"
        foldersSet_reg.add(newStringByBase64("dGVuY2VudC9taWNyb21zZy9bMC05YS16YS16XXszMn0vc25z")); // "tencent/micromsg/[0-9a-za-z]{32}/sns"
        foldersSet_reg.add(newStringByBase64("dGVuY2VudC9taWNyb21zZy9bMC05YS16YS16XXszMn0vYXZhdGFy")); // "tencent/micromsg/[0-9a-za-z]{32}/avatar"
        foldersSet_reg.add(newStringByBase64("dGVuY2VudC9taWNyb21zZy9bMC05YS16YS16XXszMn10ZW1wWzAtOV17MTN9")); // "tencent/micromsg/[0-9a-za-z]{32}temp[0-9]{13}"
    }

    private boolean isUseMediaStoreCalcOldFileFolder(String pathString) {
        if( museMS_CalcOldFile_folders.contains(pathString) ) {
            return true;
        }
        //下面正则表达式都是腾讯的，先判断一下第一个字符，如果不满足，就不做正则表达式匹配了。
        if ( pathString.charAt(0) != 't' ) {
            return false;
        }
        for ( String regString : museMS_CalcOldFile_folders_regex ) {
            if ( pathString.matches(regString) ) {
                return true;
            }
        }
        return false;
    }

    private TreeSet<String> mpureUseMediaStore_folders = new TreeSet<String>();
    private boolean isPureUseMediaStoreCalcFolder( String pathString ) {
        TreeSet<String> foldersSet = mpureUseMediaStore_folders;
        if ( foldersSet.isEmpty() ) {
            foldersSet.add(newStringByBase64("dGVuY2VudC9taWNyb21zZy9bMC05YS16YS16XXszMn0vaW1hZ2Uy")); // "tencent/micromsg/[0-9a-za-z]{32}/image2"
            foldersSet.add(newStringByBase64("dGVuY2VudC9taWNyb21zZy9bMC05YS16YS16XXszMn0vdm9pY2Uy")); // "tencent/micromsg/[0-9a-za-z]{32}/voice2"
        }
        //下面正则表达式都是腾讯的，先判断一下第一个字符，如果不满足，就不做正则表达式匹配了。
        if ( pathString.charAt(0) != 't' ) {
            return false;
        }
        for ( String regString : foldersSet ) {
            if ( pathString.matches(regString) ) {
                return true;
            }
        }
        return false;
    }

    //SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 格式化当前系统日期

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean computeFileSizeByMediaStoreSLD(String path,
                                                  long nIoModifiedTime_s, long nIoFilesCount, long[] result) {
        NLog.d("sdCardCacheScanTask:CalculateFolderSizeUtil:computeFileSizeByMediaStoreSLD",path);
        String selection = "format != 12289 and parent = (select _id from files where _data = ?)";
        path = FileUtils.removeSlash(path);
        Cursor cursor = null;
        final String[] projection = {
                "sum(" + MediaStore.Files.FileColumns.SIZE + ")",
                "max(" + MediaStore.Files.FileColumns.DATE_MODIFIED + ")",
                "count(*)" };
        result[0] = 0;
        result[1] = 0;
        result[2] = 0;
        long nSumSize = 0;
        long nLastAddTime_s = 0;
        long nCount = 0;
        try {
            cursor = SpaceApplication.getInstance().getContext()
                    .getContentResolver()
                    .query(MediaStore.Files.getContentUri("external"),
                            projection, selection, new String[] { path }, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    nSumSize = cursor.getLong(0);
                    nLastAddTime_s = cursor.getLong(1);
                    nCount = cursor.getLong(2);
                } while (false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }

        if (nLastAddTime_s < nIoModifiedTime_s) {
            return false;
        }

        result[0] = nSumSize;
        result[1] = 1;
        result[2] = nCount;
        return true;
    }

}

package com.clean.spaceplus.cleansdk.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.db.LocalStringDbUtil;
import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;
import com.clean.spaceplus.cleansdk.main.bean.StorageInfo;

import java.util.ArrayList;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/28 14:31
 * @copyright TCL-MIG
 */
public class Commons {
    public static final int PRODUCT_ID_CN 		= 1;	///< 国内版
    public static final int PRODUCT_ID_OU 		= 2;	///< 国际版
    public static final int PRODUCT_ID = PRODUCT_ID_CN;

//    public static final int PACKAGE_ENABLE_STATUS  = 1;
//    public static final int PACKAGE_DISABLE_STATUS = 0;
//    public static final int PACKAGE_ERROR_STATUS   = -1;

    /**
     * 查询一个数据库字符串列的本地化字符串资源，如果查不到，则返回defaultStringData。
     * 注意，本查询包含云端数据查询，所以不要用本函数来查缓存库以外的数据。(只有缓存库数据有云端数据)
     *
     * @param tableName
     *            当前要查询的字符串所在表名，不能为空
     * @param columnName
     *            当前要查询的字符串所在列名，不能为空
     * @param pkgNameMd5
     *            包名md5，不能为空
     * @param stringResourceId
     *            当前要查询的字符串资源ID，非负数
     * @param defaultStringData
     *            默认串
     * @return   如果本地缓存查到结果，则返回结果；
     *            如果本地缓存未查到，且不需要连网查，则返回defaultStringData；
     *            如果本地缓存未查到，且需要连网查，本函数返回时还未查到网络结果，则返回null；
     */
    public static String getLocalStringResourceOfDatabaseStringData(
            String tableName, String columnName, String pkgNameMd5, int stringResourceId,
            String defaultStringData) {

        LocalStringDbUtil.SrsidCheckCallback.Stub srsidCheckCallback =
                new LocalStringDbUtil.SrsidCheckCallback.Stub();
        String string = LocalStringDbUtil.getInstance()
                .getLocalStringResourceOfDatabaseStringDataWithCacheDB(
                        tableName, columnName, stringResourceId, null,
                        srsidCheckCallback);
        if (null != string) {
            return string;
        }

        if (srsidCheckCallback.exists()) {
            return defaultStringData;
        }

        // 本地串没有，查云端。
        LocalStringDbUtil.LocalStringCheckCloudCallback.Base rstCB =
                new LocalStringDbUtil.LocalStringCheckCloudCallback.Base(null);
        return rstCB.getResultString();
    }

    /**
     * 查询一个数据库字符串列的本地化字符串资源，如果查不到，则返回defaultStringData。
     * 注意，本查询不包含云端数据，所以不要用本函数来查缓存库数据。
     * @param tableName
     *            当前要查询的字符串所在表名，不能为空
     * @param columnName
     *            当前要查询的字符串所在列名，不能为空
     * @param stringResourceId
     *            当前要查询的字符串资源ID，非负数
     * @param defaultStringData
     *            默认串
     */
    public static String getLocalStringResourceOfDatabaseStringData(String tableName, String columnName, int stringResourceId,
                                                                    String defaultStringData) {
        return LocalStringDbUtil.getInstance()
                .getLocalStringResourceOfDatabaseStringDataWithCacheDB(
                        tableName, columnName, stringResourceId, defaultStringData, null);
    }

//    public static String getFileSavePath() {
//        String fileSavePath;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            fileSavePath = Environment.getExternalStorageDirectory().getPath() + "/space_plus_cn/";
//
//            if (Build.VERSION.SDK_INT >= 8) {
//                File externFilesDir = getExternalFilesRootDir();
//                if (null != externFilesDir) {
//                    if (!externFilesDir.exists()) {
//                        externFilesDir.mkdirs();
//                    }
//
//                    fileSavePath = FileUtils.addSlash(externFilesDir.getPath());
//                }
//            }
//
//            File fileSdDir = new File(fileSavePath);
//            fileSdDir.mkdir();
//            if (!fileSdDir.exists()) {
//                fileSavePath = null;
//            }
//        } else {
//            fileSavePath = null;
//        }
//
//        if (null == fileSavePath) {
//            fileSavePath = FileUtils.addSlash(SpaceApplication.getInstance().getContext().getApplicationInfo().dataDir);
//        }
//        return fileSavePath;
//    }

//    /**
//     * 注意，这个函数有时候会返回null。建议使用Env.getExternalStorageDirectoryx()
//     */
//    public static File getExternalFilesRootDir() {
//        try{
//            String rootDir = Env.getExternalStorageDirectoryx();
//            if(!TextUtils.isEmpty(rootDir)){
//                return new File(rootDir);
//            }
//        }
//        catch(NullPointerException e){
//        }catch (SecurityException e) {
//            //fix http://trace.cm.ijinshan.com/index/dump?version=&date=20140708&thever=0&dumpkey=2769283760&field=%E6%97%A0&field_content=2769283760
//        }catch(Exception e){
//        }
//
//        return null;
//    }

    /**
     * 只区分国内 和 国际， 不区分平台
     * @return
     */
    public static boolean isCNVersion(){
        return PRODUCT_ID == PRODUCT_ID_CN;
    }

//    /**
//     * getPkgAndroidDataSize
//     * @param packageName
//     * @return
//     */
//    public static long getPkgAndroidDataSize(String packageName) {
//        File file = Environment.getExternalStorageDirectory();
//        File data = new File(file, "Android/data/" + packageName +"/cache");
//        if (data.exists()) {
//            long result[] = new long[3];
//            PathOperFunc.computeFileSize(data.getPath(), result, null);
//            return result[0];
//        }
//        return 0;
//    }

    /**
     * 取sd卡存储信息(此函数只取外接SD卡信息，如果有多张外接SD卡，则取得叠加信息)
     *
     * @return 取到的存储信息(如果为空，或内部取到size值为0，则表明没有sd卡，或者没有挂载。)
     */
    public static StorageInfo getRemovableSdCardsStorageInfo() {
        StorageList storageList = new StorageList();
        ArrayList<String> mountedVolumePaths = storageList.getMountedSdCardVolumePaths();
        if (null == mountedVolumePaths) {
            return null;
        }

        return StorageInfoUtils.getStorageInfo(mountedVolumePaths);
    }

    /**
     * 取内置sd卡存储信息(此函数只取内置SD卡信息，如果有多张内置SD卡，则取得叠加信息)
     *
     * @return 取到的存储信息(如果为空，或内部取到size值为0，则表明没有sd卡，或者没有挂载。)
     */
    public static StorageInfo getInternalSdCardsStorageInfo() {
        StorageList storageList = new StorageList();
        ArrayList<String> mountedVolumePaths = storageList.getMountedPhoneVolumePaths();
        if (null == mountedVolumePaths) {
            return null;
        }

        return StorageInfoUtils.getStorageInfo(mountedVolumePaths);
    }

//    public static int calcPercentage(long n, long all) {
//
//        if (n < 0L || all <= 0L) {
//            if (PublishVersionManager.isTest()) {
//                throw new IllegalArgumentException("n:" + n + " all:" + all);
//            } else {
//                android.util.Log.w("CP", "n:" + n + " all:" + all);
//            }
//            return 0;
//        }
//
//        if (0L == n) {
//            return 0;
//        }
//
//        double rst = (((double)(n * 100L)) / ((double)all));
//        return (int)Math.round(rst);
//    }
//
//    public  static  int stringToInt(String str)
//    {   int a=0;
//        try {
//            a = Integer.parseInt(str);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return  a;
//    }

//    public static int getPackageEnableStat(Context context, String packageName){
//
//        PackageInfo packageInfo = PackageUtils.getPackageInfo(context, packageName);
//        if(packageInfo == null){
//            return -1;
//        }
//
//        Boolean enable = (Boolean) ReflectionUtil.getField(packageInfo.applicationInfo, "enabled");
//        Integer EnableSetting = (Integer) ReflectionUtil.getField(packageInfo.applicationInfo, "enabledSetting");
//        if(enable == null){
//            return -1;
//        }
//        if(enable == true){
//            return 1;
//        }
//        if(EnableSetting == null){
//            return -1;
//        }
//        if(EnableSetting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER){
//            return -1;
//        }
//        return 0;
//    }


    public static boolean isUserApp(ApplicationInfo info) {
        if (info == null) {
            return false;
        }
        return !((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

//    public static boolean isUserApp(final int flags) {
//        return !((flags & ApplicationInfo.FLAG_SYSTEM) != 0 || (flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
//    }
//
//    public static String removeFirstUnPrintableString(String string) {
//        if (TextUtils.isEmpty(string)) {
//            return "";
//        }
//        char[] nameArray = string.toCharArray();
//        StringBuilder builder = new StringBuilder();
//        boolean isPrintble = false;
//        for ( char c : nameArray ) {
//            if (!isPrintble && TextUtils.isGraphic(c)) {
//                isPrintble = true;
//                builder.append(c);
//            }else if(isPrintble){
//                builder.append(c);
//            }
//        }
//        return builder.toString();
//    }
//
//
//    /**
//     * 删除文件或文件夹
//     *
//     * @param file
//     *            要删除的文件或文件夹
//     * @param IDelFileN
//     *            文件删除回调对象
//     */
//    public static void DeleteFolder(File file, DeleteFileNotify IDelFileN ) {
//        DeleteFolder( file, IDelFileN, 0 );
//    }

//    /**
//     * 删除文件或文件夹
//     * @param file 要删除的文件或文件夹
//     * @param IDelFileN 文件删除回调对象
//     * @param nCleanTime_d 时间线, 单位: 天
//     */
//    public static void DeleteFolder(File file, DeleteFileNotify IDelFileN, int nCleanTime_d ) {
//        if (null == IDelFileN || (IDelFileN.getEnableFlags() & DeleteFileNotify.DISABLE_WRITE_LOG) == 0) {
//            if (null != file) {
//            }
//        }
//        DeleteFolder(file, IDelFileN, true, false, true, nCleanTime_d );
//    }
//    private static final int MAX_RECURSION_LEVEL = 128;
//    private static void DeleteFolder(File file, DeleteFileNotify IDelFileN, boolean topLevel, boolean keepTopFolder, boolean delFiles, int nCleanTime_d ) {
//        if (null == file) {
//            return;
//        }
//
//        if ((delFiles || (!keepTopFolder) || (!topLevel)) && null == IDelFileN) {
//            if (directDeleteFile(file, null, true, 0 )) {
//                return;
//            }
//        }
//
//        if (file.isFile()) {
//            if (delFiles) {
//                directDeleteFile(file, IDelFileN, true, 0);
//            }
//        } else {
//            try {
//                directDeleteFolder(file, false, IDelFileN, delFiles, MAX_RECURSION_LEVEL, nCleanTime_d );
//            } catch (Error e) {
//                //File lastFile = mLastFile;
//                //OpLog.d("DeleteFileError", file.getPath() + " The last path is: " + ((null != lastFile) ? lastFile.getPath() : "UNKNOWN"));
//            } finally {
//                mLastFile = null;
//            }
//
//            if ((!keepTopFolder) || (!topLevel)) {
//                file.delete();
//                if (Build.VERSION.SDK_INT >= 19) {
//                    if (file.exists()) {
//                        try {
//                            (new MyMediaFile(getContext().getContentResolver(), file)).delete();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//            if (null != IDelFileN) {
//                IDelFileN.afterFolderDel(file.getPath());
//            }
//        }
//    }
    private static Context mCtx = null;
    public static Context getContext() {
        if (null == mCtx) {
            mCtx = SpaceApplication.getInstance().getContext();
        }
        return mCtx;
    }

//    private static volatile File mLastFile = null;
//    private static void directDeleteFolder(File file, boolean deleteNowFolder, DeleteFileNotify IDelFileN, boolean delFiles, int maxLevel, int nCleanTime_d) {
//
//        if ((maxLevel--) <= 0) {
//            return;
//        }
//
//        mLastFile = file;
//        if (null != IDelFileN) {
//            if ( (IDelFileN.getEnableFlags() & DeleteFileNotify.ENABLE_NOTIFY_FOLDER_DEL) == DeleteFileNotify.ENABLE_NOTIFY_FOLDER_DEL) {
//                IDelFileN.notifyFolderDel(file.getPath());
//            }
//        }
//
//        PathOperFunc.FilesAndFoldersStringList fileArray = PathOperFunc.listDir(file.getPath());
//        PathOperFunc.StringList fileArrayTemp = null;
//        if (null != fileArray) {
//            try {
//                if (delFiles) {
//                    fileArrayTemp = fileArray.getFileNameList();
//                    if (null != fileArrayTemp) {
//                        int nTmpCleanTime_d = 0;
//                        if ( fileArrayTemp.size() > 0 ) {
//                            if ( nCleanTime_d > 0  && nCleanTime_d != CleanTypeUtils.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE ) {
//                                if ( System.currentTimeMillis() - file.lastModified()  < nCleanTime_d * 24*60*60*1000) {
//                                    nTmpCleanTime_d = nCleanTime_d;
//                                }
//                            }
//                        }
//                        for ( String subName : fileArrayTemp ) {
//                            File sub = new File(FileUtils.addSlash(file.getPath()) + subName);
//                            directDeleteFile(sub, IDelFileN, true, nTmpCleanTime_d );
//                        }
//                        fileArrayTemp.release();
//                        fileArrayTemp = null;
//                    }
//                }
//
//                fileArrayTemp = fileArray.getFolderNameList();
//
//                if (null != fileArrayTemp) {
//                    for ( String subName : fileArrayTemp ) {
//                        File sub = new File(FileUtils.addSlash(file.getPath()) + subName);
//                        directDeleteFolder(sub, true, IDelFileN, delFiles, maxLevel, nCleanTime_d);
//                    }
//                }
//            } finally {
//                if (null != fileArrayTemp) {
//                    fileArrayTemp.release();
//                    fileArrayTemp = null;
//                }
//
//                fileArray.release();
//            }
//        }
//
//        if (deleteNowFolder) {
//            file.delete();
//            if (Build.VERSION.SDK_INT >= 19) {
//                if (file.exists()) {
//                    try {
//                        (new MyMediaFile(
//                                getContext().getContentResolver(), file)).delete();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

//
//    private static boolean directDeleteFile(File file, DeleteFileNotify IDelFileN, boolean delByRoot, int nCleanTime_d ) {
//
//        if (null != IDelFileN) {
//            if ( (IDelFileN.getEnableFlags() & DeleteFileNotify.ENABLE_BEFORE_FILE_DEL) == DeleteFileNotify.ENABLE_BEFORE_FILE_DEL ) {
//                if (!IDelFileN.beforeFileDel(file.getPath())) {
//                    return false;
//                }
//            }
//            if ( nCleanTime_d > 0 && nCleanTime_d != CleanTypeUtils.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE ) {
//                if ( System.currentTimeMillis() - file.lastModified()  < nCleanTime_d * 24*60*60*1000) {
//                    return false;
//                }
//            }
//            if ( (IDelFileN.getEnableFlags() & DeleteFileNotify.ENABLE_NOTIFY_FILE_DEL) == DeleteFileNotify.ENABLE_NOTIFY_FILE_DEL ) {
//                IDelFileN.notifyFileDel(file.getPath());
//            }
//            if ( (IDelFileN.getEnableFlags() & DeleteFileNotify.ENABLE_NOTIFY_DELETED_FILE_SIZE) == DeleteFileNotify.ENABLE_NOTIFY_DELETED_FILE_SIZE ) {
//                IDelFileN.notifyDeletedFileSize(file.length());
//            }
//        }else{
//            if ( nCleanTime_d > 0 && nCleanTime_d != CleanTypeUtils.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE ) {
//                if ( System.currentTimeMillis() - file.lastModified()  < nCleanTime_d * 24*60*60*1000) {
//                    return false;
//                }
//            }
//        }
//
//        boolean bExists = !file.delete();
//        if (Build.VERSION.SDK_INT >= 19) {
//            if (bExists) {
//                try {
//                    bExists = !(new MyMediaFile(getContext().getContentResolver(), file)).delete();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        if (delByRoot) {
//            if (bExists) {
//                if (SuExec.getInstance().checkRoot()) {
//                    bExists = !SuExec.getInstance().deleteFile(file.getPath());
//                }
//            }
//        }
//
//        if (null != IDelFileN) {
//            if ( (IDelFileN.getEnableFlags() & DeleteFileNotify.ENABLE_AFTER_FILE_DEL) == DeleteFileNotify.ENABLE_AFTER_FILE_DEL ) {
//                IDelFileN.afterFileDel(file.getPath());
//            }
//        }
//
//        return !bExists;
//    }


//    /**
//     * 删除文件或文件夹下的文件(不删文件夹)
//     *
//     * @param file
//     *            要删除的文件或要删除下属文件的文件夹路径
//     * @param IDelFileN
//     *            文件删除回调对象
//     */
//    public static void DeleteFile(File file, DeleteFileNotify IDelFileN ) {
//        DeleteFile( file, IDelFileN, 0 );
//    }

//    /**
//     * 删除文件或文件夹下的文件(不删文件夹)
//     * @param file 要删除的文件或要删除下属文件的文件夹路径
//     * @param IDelFileN 文件删除回调对象
//     * @param nCleanTime_d 时间线 单位: 天
//     */
//    public static void DeleteFile(File file, DeleteFileNotify IDelFileN, int nCleanTime_d ) {
//        if (null == IDelFileN || (IDelFileN.getEnableFlags() & DeleteFileNotify.DISABLE_WRITE_LOG) == 0) {
//            if (null != file) {
//            }
//        }
//        DeleteFile(file, IDelFileN, true, nCleanTime_d );
//    }

//    private static void DeleteFile(File file, DeleteFileNotify IDelFileN, boolean topLevel, int nCleanTime_d ) {
//        if (null == file) {
//            return;
//        }
//
//        if (file.isFile()) {
//            directDeleteFile(file, IDelFileN, false, 0 );
//        } else {
//            try {
//                directDeleteFilesInFolder(file, IDelFileN, MAX_RECURSION_LEVEL, nCleanTime_d);
//            } catch (Error e) {
//                //File lastFile = mLastFile;
//                //OpLog.d("DeleteFileError", file.getPath() + " The last path is: " + ((null != lastFile) ? lastFile.getPath() : "UNKNOWN"));
//            } finally {
//                mLastFile = null;
//            }
//        }
//    }


//    private static void directDeleteFilesInFolder(File file, DeleteFileNotify IDelFileN, int maxLevel, int nCleanTime_d ) {
//
//        if ((maxLevel--) <= 0) {
//            return;
//        }
//
//        mLastFile = file;
//        PathOperFunc.FilesAndFoldersStringList fileArray = PathOperFunc.listDir(file.getPath());
//        PathOperFunc.StringList fileArrayTemp = null;
//        if (null != fileArray) {
//            try {
//                fileArrayTemp = fileArray.getFileNameList();
//                if (null != fileArrayTemp) {
//                    int nTmpCleanTime_d = 0;
//                    if ( fileArrayTemp.size() > 0 ) {
//                        if ( nCleanTime_d > 0  && nCleanTime_d != CleanTypeUtils.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE ) {
//                            if ( System.currentTimeMillis() - file.lastModified()  < nCleanTime_d * 24*60*60*1000) {
//                                nTmpCleanTime_d = nCleanTime_d;
//                            }
//                        }
//                    }
//                    for ( String subName : fileArrayTemp ) {
//                        File sub = new File(FileUtils.addSlash(file.getPath()) + subName);
//                        directDeleteFile(sub, IDelFileN, false, nTmpCleanTime_d );
//                    }
//                    fileArrayTemp.release();
//                    fileArrayTemp = null;
//                }
//
//                fileArrayTemp = fileArray.getFolderNameList();
//                if (null != fileArrayTemp) {
//                    for ( String subName : fileArrayTemp ) {
//                        File sub = new File(FileUtils.addSlash(file.getPath()) + subName );
//                        directDeleteFilesInFolder(sub, IDelFileN, maxLevel, nCleanTime_d );
//                    }
//                }
//            } finally {
//                if (null != fileArrayTemp) {
//                    fileArrayTemp.release();
//                    fileArrayTemp = null;
//                }
//
//                fileArray.release();
//            }
//        }
//    }
}

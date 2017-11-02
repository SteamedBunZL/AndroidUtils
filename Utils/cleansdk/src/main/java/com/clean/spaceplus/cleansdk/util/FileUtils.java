package com.clean.spaceplus.cleansdk.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.appmgr.util.FileComputeUtils;
import com.clean.spaceplus.cleansdk.base.db.DatabaseHelper;
import com.clean.spaceplus.cleansdk.base.utils.root.SuExec;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.ServiceConfigManager;
import com.clean.spaceplus.cleansdk.junk.engine.DelCallback;
import com.clean.spaceplus.cleansdk.junk.engine.DeleteFileNotify;
import com.clean.spaceplus.cleansdk.junk.engine.DeleteFilesByMediaStore;
import com.clean.spaceplus.cleansdk.junk.engine.FileDeletedCounter;
import com.clean.spaceplus.cleansdk.junk.engine.PathCallback;
import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFileHelper;
import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;
import com.clean.spaceplus.cleansdk.junk.engine.junk.EngineConfig;
import com.clean.spaceplus.cleansdk.junk.engine.util.NameFilter;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import space.network.util.CleanTypeUtil;

/**
 * @author zengtao.kuang
 * @Description: 文件工具
 * @date 2016/4/6 11:25
 * @copyright TCL-MIG
 */
public class FileUtils {
    public static final String TAG = "FileUtils";
    private static final int MAX_RECURSION_LEVEL = 128;
    private static final String HEXES = "0123456789abcdef";

    public static final String ID_CONFIG = "config";
    public static final String ID_DATA = "data";
    public static final String ID_APK = "cleanmaster.apk";
    public static final String ID_ANTIVIRUS = "antivirus.db";
    public static final String ID_ANTIVIRUS_LW = "antivirus_lw.db";
    public static final String ID_EDB_SOFTDETAIL 	= "clearpath5_softdetail.db";
    public static final String ID_EDB_CACHE 		= "clearpath_cache_5.9.1.db";
    public static final String ID_EDB_OTHER 		= "clearpath5_other.db";
    public static final String ID_EDB_JUNKWHITE 	= "junkwhite.db"; //系统缓存忽略包数据库，通用白名单

    public static final String ID_STRINGS_CACHE_DB 		= "advdesc_cache.db";
    public static final String ID_STRINGS_SOFTDETAIL_DB = "strings2_softdetail.db";
    public static final String ID_STRINGS_OTHER_DB 		= "strings2_other.db";
    public static final String ID_PROCESS_TIPS = "process_tips2.db";//进程tips文件
    public static final String ID_PRIVACY_CACHE_DB 	= "privacy_cache.db";// 隐私垃圾对应库
    public static final String ID_COVER_CACHE_DB = "tapp.dat";	//Cover Cache

    public static final String MELIB = "melib.dat"; // 内存异常库
    public static final String CLEARPROCESS_FILTER_CN = "clearprocess_cn_5.9.2.filter";
    public static final String CLEARPROCESS_FILTER_EN = "clearprocess_en_5.9.2.filter";

    public static final String ATS_WL_CN = "ats2_wl_cn.dat";  //国内版本自启白名单库
    public static final String ATS_SR_CN = "ats2_sr_cn.dat";  //国内版本自启自修复库
    public static final String ATS_WL_EN = "ats2_wl_en.dat";  //国外版本自启白名单库

    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_APK = 1;
    public static final int TYPE_BINARY = 2;
    public static final int TYPE_SQLITE = 3;
    public static final int TYPE_ENCRYPT_SQLITE = 4;

    private static volatile File mLastFile = null;
    private static Boolean mSecondSdCardCanWriteable = null;

    private FileUtils(){

    }
    // 尽量提高数据库使用正确版本的机率
    public static void ControlWait(){
        int i = 64;

    }
    public static File checkPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        File file = new File(path);
        if (file == null || !file.exists()) {
            return null;
        }
        return file;
    }

    public static String encodeHex(byte[] data)
    {
        if(data == null)
        {
            return null;
        }

        int len = data.length;
        StringBuilder hex = new StringBuilder(len * 2);

        for(int i = 0; i < len; ++i)
        {
            hex.append(HEXES.charAt((data[i] & 0xF0) >>> 4));
            hex.append(HEXES.charAt((data[i] & 0x0F)));
        }

        return hex.toString();
    }

    /**
     * 添加斜杠
     * @param path
     * @return
     */
    public static String addSlash(final String path) {
        if (Miscellaneous.isEmpty(path)) {
            return File.separator;
        }

        if (path.charAt(path.length() - 1) != File.separatorChar) {
            return path + File.separatorChar;
        }

        return path;
    }

    /**
     * 最后的斜杠换成0，如果没有斜杠则补0
     * @param path
     * @return
     */
    public static String replaceEndSlashBy0(String path) {

        if (Miscellaneous.isEmpty(path)) {
            return "0";
        }

        if (path.charAt(path.length() - 1) != File.separatorChar) {
            return path + "0";
        }

        return path.substring(0, path.length() - 1) + "0";
    }

    /**
     * 删除文件夹
     * @param folderPath
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
    }

    /**
     * 删除文件夹中的所有文件
     * @param path
     * @return true:成功 false:失败
     */
    public static boolean delAllFile(String path) {
        if(path == null || path.length() <= 0) {
            return false;
        }

        boolean bReturn = false;
        File file = new File(path);
        if (!file.exists()) {
            return bReturn;
        }
        if (!file.isDirectory()) {
            return bReturn;
        }
        String[] tempList = file.list();
        if ( tempList != null ){
            File temp = null;
            for (int i = 0; i < tempList.length; i++) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isFile()) {
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    delAllFile(path + File.separatorChar + tempList[i]);
                    delFolder(path + File.separatorChar + tempList[i]);
                    bReturn = true;
                }
            }
        }

        return bReturn;
    }

//    /**
//     * 拷贝Assert下的文件到指定目录
//     *
//     * @param astMgr
//     * @param srcFile
//     * @param dstFile
//     */
//    public static void copyAssertFile(AssetManager astMgr, String srcFile, String dstFile) {
//        long startTime = System.currentTimeMillis();
//        InputStream in = null;
//        OutputStream out = null;
//        try {
//            in = astMgr.open(srcFile);
//            File outFile = new File(dstFile);
//            File parent = new File(outFile.getParent());
//            if (!parent.exists()){
//                parent.mkdirs();
//            }
//            out = new FileOutputStream(outFile);
//            FileUtils.copyFile(in, out);
//            in.close();
//            in = null;
//            out.flush();
//            out.close();
//            out = null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            NLog.e(TAG, "Failed to copy asset file: " + srcFile, e);
//        }
//        NLog.d(TAG, "copy asset file from asset to data/data/database, cost time = "+ (System.currentTimeMillis() - startTime));
//    }

//    /**
//     * 文件拷贝
//     *
//     * @param in
//     * @param out
//     */
//    public static void copyFile(InputStream in, OutputStream out) throws IOException {
//        byte[] buffer = new byte[1024];
//        int read;
//        while ((read = in.read(buffer)) != -1) {
//            out.write(buffer, 0, read);
//        }
//    }
//
//    public static void copyFile(String oldPath, String newPath) {
//        InputStream inStream = null;
//        FileOutputStream fs = null;
//        try {
//            int bytesum = 0;
//            int byteread = 0;
//            File oldfile = new File(oldPath);
//            if (oldfile.exists()) { // 文件存在时
//                inStream = new FileInputStream(oldPath); // 读入原文件
//                fs = new FileOutputStream(newPath);
//                byte[] buffer = new byte[1444];
//                while ((byteread = inStream.read(buffer)) != -1) {
//                    bytesum += byteread; // 字节数 文件大小
//                    fs.write(buffer, 0, byteread);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try{
//                if(inStream != null){
//                    inStream.close();
//                }
//            } catch(Exception e){
//
//            }
//
//            try{
//                if(fs != null){
//                    fs.close();
//                }
//            } catch(Exception e){
//
//            }
//        }
//    }

    /**
     * 去掉斜杠
     * @param path
     * @return
     */
    public static String removeSlash(String path) {
        if (Miscellaneous.isEmpty(path) || path.length() == 1) {
            return path;
        }

        if (path.charAt(path.length() - 1) == File.separatorChar) {
            return path.substring(0, path.length() - 1);
        }

        return path;
    }

    /**
     * 获取文件版本
     * @param type
     * @param path
     * @return
     */
    public static String getFileVersion(int type, String path) {
        if (type == TYPE_BINARY) {
            return getBinaryVersion(path);
        } else if (type == TYPE_SQLITE) {
            return getSqliteVersion(path);
        } else if ( type == TYPE_ENCRYPT_SQLITE ){
            return "";
        }

        return getOtherTypeFileVersion(path);
    }

    // 二进制文件版本获取
    private static String getBinaryVersion(String path) {
        String version = null;
        FileInputStream is = null;
        try {
            is = new FileInputStream(path);

            byte[] buffer = new byte[4];
            int bytes = is.read(buffer);
            if (bytes >= 4) {
                version = String.format(Locale.US, "%d.%d.%d.%d", buffer[0], buffer[1],
                        buffer[2], buffer[3]);
            }
        } catch (Exception e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
        return version;
    }

    private static String getOtherTypeFileVersion(String path) {
        if (null == path) {
            return null;
        }

        String version = ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).getFileVersion(path);
        if (null != version && 0 == version.compareTo("")) {
            version = null;
        }

        return version;
    }

    /**
     * 数据库文件版本获取
     * @param path
     * @return
     */
    private static String getSqliteVersion(String path) {
        String version = null;
        Cursor cursor = null;
        SQLiteDatabase db = null;

        if (!new File(path).exists())
            return version;

        try {
            db = DatabaseHelper.OpenDatabaseProperly(path);

            cursor = db.rawQuery(
                    "SELECT major, minor, build, subcnt FROM version", null);
            if (cursor != null && cursor.moveToFirst()) {
                version = String.format(Locale.US, "%d.%d.%d.%d", cursor.getInt(0),
                        cursor.getInt(1), cursor.getInt(2), cursor.getInt(3));
            }
        } catch (Exception e) {
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }

                if (db != null) {
                    db.close();
                }
            } catch (Exception e) {
            } catch (Error e) {
            }
        }
        return version;
    }

    /**
     * 外部存储是否已挂载
     * @return
     */
    public static boolean isValidExternalStorage() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取外部存储设备路径
     * @return
     */
    public static String getExternalStoragePath() {
        if (!FileUtils.isValidExternalStorage()) {
            return null;
        }

        return FileUtils.addSlash(Env.getExternalStorageDirectoryx());
    }

//    /**
//     * 删除dstPath文件 并将srcPath文件重命名为dstPath
//     * @param srcPath
//     * @param dstPath
//     * @return
//     */
//    public static boolean replaceFile(String srcPath, String dstPath) {
//        File dstFile = new File(dstPath);
//        if (dstFile.exists()) {
//            int retry = 3;
//            do {
//                if (dstFile.delete()) {
//                    return (new File(srcPath)).renameTo(dstFile);
//                }
//
//                try {
//                    Thread.sleep(100);
//                } catch (Exception e) {
//                }
//            } while (--retry > 0);
//            return false;
//        }
//
//        return (new File(srcPath)).renameTo(dstFile);
//    }

    public static File get(File file) throws IOException{
        try{
            return file.getCanonicalFile();
        }
        catch (Exception e) {
            if ( Build.VERSION.SDK_INT < 8 ){

                try{
                    Class<?> cls = Class.forName("org.apache.harmony.luni.internal.io.FileCanonPathCache");
                    Method mtd = cls.getMethod("clear");
                    mtd.invoke(null);
                }
                catch (Exception e1) {
                }

            }
            return file.getCanonicalFile();
        }
    }



    /**
     * 封装对Context.getFilesDir()的调用
     */
    public static File getFilesDir(Context ctx) {

        if (null == ctx) {
            return null;
        }

        File result = null;
        for (int i = 0; i < 3; ++i) {
            // 因为有时候getFilesDir()在无法创建目录时会返回失败，所以我们在此等待并于半秒内尝试三次。
            result = ctx.getFilesDir();
            if (null != result) {
                break;
            } else {
                try {
                    Thread.sleep(166);
                } catch (Exception e) {
                }
            }
        }

        return result;
    }

    static public String getFilePathInFilesDir(Context context, String fileName) {
        String strPath = null;
        File filesDir = context.getFilesDir();
        if (null == filesDir) {
            File FileDummyDb = context.getDatabasePath("dummyfile");
            if (FileDummyDb != null) {
                String databaseDir = FileDummyDb.getParent();
                File fileDbDir = new File(databaseDir);
                String strDataPath = fileDbDir.getParent();
                if (!TextUtils.isEmpty(strDataPath)) {
                    strPath = strDataPath;
                    strPath += File.separator;
                    strPath += "files";
                }
            }
        } else {
            strPath = filesDir.getAbsolutePath();
        }

        if (TextUtils.isEmpty(strPath))
            return null;

        strPath += File.separator;
        strPath += fileName;

        return strPath;
    }
	
//	 /**
//     * 获取指定目录文件列表
//     * @param dir
//     * @return
//     */
//    public static List<File> getDirectoryFileList(String dir) {
//        File file = new File(dir);
//        NLog.d(TAG, "getDirectoryFileList dir = "+dir +", file exist = " +file.exists() );
//        if (!file.exists()) {
//            return null;
//        }
//        File[] files = file.listFiles();
//        NLog.d(TAG, "getDirectoryFileList listFiles = "+files );
//        if (files == null ) {
//            return null;
//        }
//        return Arrays.asList(files);
//    }



    // 尽量提高数据库使用正确版本的机率
    public static void controlWait(){

    }

    /**
     * 删除文件或文件夹
     * @param file 要删除的文件或文件夹
     * @param delFileN 文件删除回调对象
     * @param nCleanTime_d 时间线, 单位: 天
     */
    public static void deleteFolder(List<String> folderWhiteList, File file, DeleteFileNotify delFileN, int nCleanTime_d, FileDeletedCounter counter) {
        if (null == delFileN || (delFileN.getEnableFlags() & DeleteFileNotify.DISABLE_WRITE_LOG) == 0) {
            if (null != file) {
                NLog.d("DFo", file.getPath());
            }
        }

        //不删除白名单中数据
        if(folderWhiteList.contains(file.getAbsolutePath())){
            return;
        }

        deleteFolder(file, delFileN, true, false, true, nCleanTime_d, counter);
    }

    private static void deleteFolder(File file, DeleteFileNotify delFileN, boolean topLevel, boolean keepTopFolder, boolean delFiles, int nCleanTime_d, FileDeletedCounter counter) {
        if (null == file) {
            return;
        }

        if ((delFiles || (!keepTopFolder) || (!topLevel)) && null == delFileN) {
            if (directDeleteFile(file, null, true, 0, counter)) {
                NLog.d(TAG, "directDeleteFile: true"+file.getAbsolutePath());
                return;
            }
        }

        NLog.d(TAG, "directDeleteFile: false"+file.getAbsolutePath());
        if (file.isFile()) {
            if (delFiles) {
                directDeleteFile(file, delFileN, true, 0, counter);
            }
        } else {
            try {
                directDeleteFolder(file, false, delFileN, delFiles, MAX_RECURSION_LEVEL, nCleanTime_d, counter);
            } catch (StackOverflowError e) {
            } finally {
                mLastFile = null;
            }

            if ((!keepTopFolder) || (!topLevel)) {
                file.delete();
                if (Build.VERSION.SDK_INT >= 19) {
                    if (file.exists()) {
                        try {
                            Context context = SpaceApplication.getInstance().getContext();
                            new MediaFileHelper(context.getContentResolver(), file).delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (null != delFileN) {
                delFileN.afterFolderDel(file.getPath());
            }
        }
    }

    private static boolean directDeleteFile(File file, DeleteFileNotify delFileN, boolean delByRoot, int nCleanTime_d, FileDeletedCounter counter) {
        NLog.d(TAG, "directDeleteFile--"+file.getAbsolutePath());
        if (null != delFileN) {
            if ( (delFileN.getEnableFlags() & DeleteFileNotify.ENABLE_BEFORE_FILE_DEL) == DeleteFileNotify.ENABLE_BEFORE_FILE_DEL ) {
                if (!delFileN.beforeFileDel(file.getPath())) {
                    return false;
                }
            }
            if ( nCleanTime_d > 0 && nCleanTime_d != CleanTypeUtil.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE ) {
                if ( System.currentTimeMillis() - file.lastModified()  < nCleanTime_d * 24*60*60*1000) {
                    return false;
                }
            }
            if ( (delFileN.getEnableFlags() & DeleteFileNotify.ENABLE_NOTIFY_FILE_DEL) == DeleteFileNotify.ENABLE_NOTIFY_FILE_DEL ) {
                delFileN.notifyFileDel(file.getPath());
            }
            if ( (delFileN.getEnableFlags() & DeleteFileNotify.ENABLE_NOTIFY_DELETED_FILE_SIZE) == DeleteFileNotify.ENABLE_NOTIFY_DELETED_FILE_SIZE ) {
                delFileN.notifyDeletedFileSize(file.length());
            }
        }else{
            if ( nCleanTime_d > 0 && nCleanTime_d != CleanTypeUtil.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE ) {
                if ( System.currentTimeMillis() - file.lastModified()  < nCleanTime_d * 24*60*60*1000) {
                    return false;
                }
            }
        }
        long fileSize = file.exists() ? FileComputeUtils.sizeOf(file) : -1;
        boolean bExists = !file.delete();
        if (Build.VERSION.SDK_INT >= 19) {
            if (bExists) {
                try {
                    Context context = SpaceApplication.getInstance().getContext();
                    bExists = !new MediaFileHelper(
                            context.getContentResolver(), file).delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (delByRoot) {
            if (bExists) {
                if (SuExec.getInstance().checkRoot()) {
                    bExists = !SuExec.getInstance().deleteFile(file.getPath());
                }
            }
        }
        if (!bExists && counter != null) { // 删除了
            counter.addFileDeleted(file, fileSize);
            NLog.d(TAG, "删除了" + file.getAbsolutePath() + " size " + fileSize);
            if (fileSize > 0) { // 当大于0的时候才加入
                counter.mFileDeletedSize += fileSize;
            }
        }

        if (null != delFileN) {
            if ( (delFileN.getEnableFlags() & DeleteFileNotify.ENABLE_AFTER_FILE_DEL) == DeleteFileNotify.ENABLE_AFTER_FILE_DEL ) {
                delFileN.afterFileDel(file.getPath());
            }
        }

        return !bExists;
    }

    private static void directDeleteFolder(File file, boolean deleteNowFolder, DeleteFileNotify delFileN, boolean delFiles, int maxLevel, int nCleanTime_d, FileDeletedCounter counter) {

        if ((maxLevel--) <= 0) {
            return;
        }

        mLastFile = file;
        if (null != delFileN) {
            if ( (delFileN.getEnableFlags() & DeleteFileNotify.ENABLE_NOTIFY_FOLDER_DEL) == DeleteFileNotify.ENABLE_NOTIFY_FOLDER_DEL) {
                delFileN.notifyFolderDel(file.getPath());
            }
        }

        PathOperFunc.FilesAndFoldersStringList fileArray = PathOperFunc.listDir(file.getPath());
        PathOperFunc.StringList fileArrayTemp = null;
        if (null != fileArray) {
            try {
                if (delFiles) {
                    fileArrayTemp = fileArray.getFileNameList();
                    if (null != fileArrayTemp) {
                        int nTmpCleanTime_d = 0;
                        if ( fileArrayTemp.size() > 0 ) {
                            if ( nCleanTime_d > 0  && nCleanTime_d != CleanTypeUtil.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE ) {
                                if ( System.currentTimeMillis() - file.lastModified()  < nCleanTime_d * 24*60*60*1000) {
                                    nTmpCleanTime_d = nCleanTime_d;
                                }
                            }
                        }
                        for ( String subName : fileArrayTemp ) {
                            File sub = new File(FileUtils.addSlash(file.getPath()) + subName);
                            directDeleteFile(sub, delFileN, true, nTmpCleanTime_d, counter);
                        }
                        fileArrayTemp.release();
                        fileArrayTemp = null;
                    }
                }

                fileArrayTemp = fileArray.getFolderNameList();

                if (null != fileArrayTemp) {
                    for ( String subName : fileArrayTemp ) {
                        File sub = new File(FileUtils.addSlash(file.getPath()) + subName);
                        directDeleteFolder(sub, true, delFileN, delFiles, maxLevel, nCleanTime_d, counter);
                    }
                }
            } finally {
                if (null != fileArrayTemp) {
                    fileArrayTemp.release();
                    fileArrayTemp = null;
                }

                fileArray.release();
            }
        }

        if (deleteNowFolder) {
            file.delete();
            if (Build.VERSION.SDK_INT >= 19) {
                if (file.exists()) {
                    try {
                        Context context = SpaceApplication.getInstance().getContext();
                        new MediaFileHelper(
                                context.getContentResolver(), file).delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /*
	 * 是否为空的文件夹
	 */
    public static boolean isEmptyFolder(File Folder) {
        boolean isEmpty = PathOperFunc.isEmptyFolder(Folder.getPath(), MAX_RECURSION_LEVEL, null, null, null);
        return isEmpty;

    }

    /**
     * 删除文件或文件夹下的文件(不删文件夹)
     * @param file 要删除的文件或要删除下属文件的文件夹路径
     * @param delFileN 文件删除回调对象
     * @param nCleanTime_d 时间线 单位: 天
     */
    public static void deleteFile(List<String> fileWhiteList, File file, DeleteFileNotify delFileN, int nCleanTime_d, FileDeletedCounter counter) {
        if (null == delFileN || (delFileN.getEnableFlags() & DeleteFileNotify.DISABLE_WRITE_LOG) == 0) {
            if (null != file) {
                NLog.d("DFi", file.getPath());
            }
        }

        //不删除白名单中数据
        if(fileWhiteList.contains(file.getAbsolutePath())){
            return;
        }

        DeleteFile(file, delFileN, true, nCleanTime_d, counter);
    }

    private static void DeleteFile(File file, DeleteFileNotify IDelFileN, boolean topLevel, int nCleanTime_d, FileDeletedCounter counter) {
        if (null == file) {
            return;
        }

        if (file.isFile()) {
            directDeleteFile(file, IDelFileN, false, 0, counter);
        } else {
            try {
                directDeleteFilesInFolder(file, IDelFileN, MAX_RECURSION_LEVEL, nCleanTime_d, counter);
            } catch (Error e) {
            } finally {
                mLastFile = null;
            }
        }
    }

    private static void directDeleteFilesInFolder(File file, DeleteFileNotify IDelFileN, int maxLevel, int nCleanTime_d, FileDeletedCounter counter) {
        if ((maxLevel--) <= 0) {
            return;
        }

        mLastFile = file;
        PathOperFunc.FilesAndFoldersStringList fileArray = PathOperFunc.listDir(file.getPath());
        PathOperFunc.StringList fileArrayTemp = null;
        if (null != fileArray) {
            try {
                fileArrayTemp = fileArray.getFileNameList();
                if (null != fileArrayTemp) {
                    int nTmpCleanTime_d = 0;
                    if ( fileArrayTemp.size() > 0 ) {
                        if ( nCleanTime_d > 0  && nCleanTime_d != CleanTypeUtil.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE ) {
                            if ( System.currentTimeMillis() - file.lastModified()  < nCleanTime_d * 24*60*60*1000) {
                                nTmpCleanTime_d = nCleanTime_d;
                            }
                        }
                    }
                    for ( String subName : fileArrayTemp ) {
                        File sub = new File(FileUtils.addSlash(file.getPath()) + subName);
                        directDeleteFile(sub, IDelFileN, false, nTmpCleanTime_d, counter);
                    }
                    fileArrayTemp.release();
                    fileArrayTemp = null;
                }

                fileArrayTemp = fileArray.getFolderNameList();
                if (null != fileArrayTemp) {
                    for ( String subName : fileArrayTemp ) {
                        File sub = new File(FileUtils.addSlash(file.getPath()) + subName );
                        directDeleteFilesInFolder(sub, IDelFileN, maxLevel, nCleanTime_d, counter);
                    }
                }
            } finally {
                if (null != fileArrayTemp) {
                    fileArrayTemp.release();
                    fileArrayTemp = null;
                }

                fileArray.release();
            }
        }
    }

    /**
     * 删除文件或目录
     * @param fileList
     * @param IDelFileCB
     * @param recycleConfig 回收站数据回调
     * @return
     */
    public static boolean deleteFileOrFolderWithConfig(List<String> fileList, final DelCallback IDelFileCB, final EngineConfig recycleConfig, final boolean needRecycle) {
        if (null == IDelFileCB || (IDelFileCB.getEnableFlags() & DelCallback.DISABLE_WRITE_LOG) == 0) {
            if (null != fileList) {
                if (!fileList.isEmpty()) {
                    NLog.d("NDFo", fileList.get(0));
                }
            }
        }
        int result[] = new int[6];
        Arrays.fill(result, 0);
        int delFlags = IDelFileCB.getDelFlags();
        int delFileTimeLimit = IDelFileCB.getDelFileTimeLimit();
        if ( delFileTimeLimit == CleanTypeUtil.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE ) {
            delFileTimeLimit = 0;
        }

        List<String> fileWhiteList = IDelFileCB.getFileWhiteList();
        List<String> folderWhiteList = IDelFileCB.getFolderWhiteList();
        List<String> feedbackFileList = IDelFileCB.getFeedbackFileList();
        List<String> feedbackFolderList = IDelFileCB.getFeedbackFolderList();
        List<String> ExternalStoragePaths = (new StorageList()).getMountedVolumePaths();
        boolean success = PathOperFunc.deleteFileOrFolderWithConfig(result, fileList, delFlags, delFileTimeLimit,
                fileWhiteList, folderWhiteList,
                feedbackFileList, feedbackFolderList,
                new PathCallback() {
                    @Override
                    public void onFile(String filePath, long size, int atime, int mtime, int ctime) {
                        // type callback from size is temp solution for
                        // print log
                        if ( IDelFileCB != null ) {
                            IDelFileCB.onDeleteFile(filePath, size);
                        }
                    }

                    @Override
                    public void onFeedback(String filePath, String fileName, long size) {
                        if ( IDelFileCB != null ) {
                            IDelFileCB.onFeedbackFile(filePath, fileName, size);
                        }
                    }

                    HashMap<String, DeleteFilesByMediaStore> mMap = new HashMap<String, DeleteFilesByMediaStore>();

                    @Override
                    public void onStart(String strRootDir) {
                        if (strRootDir != null) {
                            mMap.put(strRootDir, new DeleteFilesByMediaStore(strRootDir, IDelFileCB));
                        }
                    }

                    @Override
                    public void onFile(String strRootDir, String strSubFile) {
                        if (strRootDir == null) {
                            try {
                                Context context = SpaceApplication.getInstance().getContext();
                                (new MediaFileHelper(context.getContentResolver(), new File(strSubFile))).delete();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } else {
                            mMap.get(strRootDir).delFile(strSubFile);
                        }
                    }

                    @Override
                    public void onFolder(String strRootDir, String strSubFolder) {
                        if (strRootDir != null) {
                            mMap.get(strRootDir).delFolder(strSubFolder);
                        }
                    }

                    @Override
                    public void onDone(String strRootDir) {
                        if (strRootDir != null) {
                            mMap.get(strRootDir).finish(needRecycle);
                            mMap.remove(strRootDir);
                        }
                    }

                    @Override
                    public void onError(String strPath, boolean bRmDir,
                                        boolean bRoot, int nErrorCode) {
                        if ( IDelFileCB != null ) {
                            IDelFileCB.onError(strPath, bRmDir, bRoot, nErrorCode);
                        }
                    }

                    @Override
                    public boolean OnFilter(String filePath, long fileModifyTime) {
                        if (IDelFileCB != null) {
                            return IDelFileCB.onFilter(filePath, fileModifyTime);
                        }

                        return true;
                    }
                }, checkSecondSdCardCanWriteable(), Environment.getExternalStorageDirectory().getAbsolutePath(), recycleConfig, ExternalStoragePaths, needRecycle);

        if (null != IDelFileCB && (IDelFileCB.getEnableFlags() & DelCallback.ENABLE_AFTER_DELETE) == DelCallback.ENABLE_AFTER_DELETE) {
            IDelFileCB.afterDel(result[1],result[2], result[3], result[4], result[5]);
        }

        return success;
    }

    /**
     * 检测第二张SD卡是否可写
     * 4.4+版本的第二张SD卡不让写操作了，增加此判断逻辑可以提高删除效率
     * 听说5.0版本取消了对第二张卡限制？现在还不确定
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static boolean checkSecondSdCardCanWriteable() {
        if ( mSecondSdCardCanWriteable != null ) {
            return mSecondSdCardCanWriteable.booleanValue();
        }
        boolean bCanWriteable = true;
        do {
            //需要4.4及以上手机
            if ( Build.VERSION.SDK_INT < 19 ) {
                break;
            }
            // 外部设备路径
            ArrayList<String> mExternalStoragePaths = (new StorageList())
                    .getMountedVolumePaths();
            if (mExternalStoragePaths == null || mExternalStoragePaths.isEmpty()) {
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    break;
                }
                mExternalStoragePaths = new ArrayList<String>();
                mExternalStoragePaths.add( Environment.getExternalStorageDirectory().getPath() );
            }
            //需要存在两张SD卡
            if ( mExternalStoragePaths.size() < 2 ) {
                break;
            }

            //判断第二张卡能否写入
            for ( String root : mExternalStoragePaths ) {
                if ( !root.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath()) ) {
                    File file = new File(root,".CleanMaster"+System.currentTimeMillis()+".tmp");
                    boolean bCreateOK = false;
                    try {
                        bCreateOK = file.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if ( bCreateOK ) {
                        file.delete();
                    } else {
                        bCanWriteable = false;
                        NLog.d( "Del2SD", "SDK:"+Build.VERSION.SDK_INT+" SecondSdCard not write! SecondSdCardPath:"+root );
                    }
                    break;
                }
            }

        }while ( false );
        mSecondSdCardCanWriteable = new Boolean( bCanWriteable );
        return bCanWriteable;
    }

    /**
     * 删除文件或目录
     * @param fileList
     * @return
     */
    public static boolean deleteFileOrFolder(List<String> fileList, final DelCallback delFileCB ) {
        return deleteFileOrFolderWithConfig( fileList, delFileCB, null, false );
    }

    public static void directDeleteSubFoldersAndFiles(File file,  DeleteFileNotify IDelFileN) {
        internalDirectDeleteFolder(file, false, IDelFileN, true);
    }

    private static void internalDirectDeleteFolder(File file, boolean deleteNowFolder, DeleteFileNotify delFileN, boolean delFiles) {
        try {
            directDeleteFolder(file, deleteNowFolder, delFileN, delFiles, MAX_RECURSION_LEVEL, 0, null);
        } catch (Error e) {
        } finally {
            mLastFile = null;
        }
        if (null != delFileN) {
            if ( (delFileN.getEnableFlags() & DeleteFileNotify.ENABLE_AFTER_FOLDER_DEL) == DeleteFileNotify.ENABLE_AFTER_FOLDER_DEL ) {
                delFileN.afterFolderDel(file.getPath());
            }
        }
    }

//    public static void deleteAllFile(File file) {
//        if (null == file || !file.exists()) {
//            return;
//        }
//
//        if (file.isFile()) {
//            file.delete();
//        } else {
//            PathOperFunc.FilesAndFoldersStringList fileArray = PathOperFunc.listDir(file.getPath());
//            PathOperFunc.StringList fileArrayTemp = null;
//            if (null != fileArray) {
//                try {
//                    fileArrayTemp = fileArray.getFileNameList();
//                    if (null != fileArrayTemp) {
//                        for ( String subName : fileArrayTemp ) {
//                            File sub = new File(FileUtils.addSlash(file.getPath()) + subName);
//                            sub.delete();
//                        }
//                        fileArrayTemp.release();
//                        fileArrayTemp = null;
//                    }
//
//                    fileArrayTemp = fileArray.getFolderNameList();
//
//                    if (null != fileArrayTemp) {
//                        for ( String subName : fileArrayTemp ) {
//                            File sub = new File(FileUtils.addSlash(file.getPath()) + subName );
//                            deleteAllFile(sub);
//                        }
//                        fileArrayTemp.release();
//                        fileArrayTemp = null;
//                    }
//
//                    fileArrayTemp = PathOperFunc.listDir(file.getPath());
//                    if (null == fileArrayTemp || fileArrayTemp.size() == 0) {
//                        file.delete();
//                    }
//                } finally {
//                    if (null != fileArrayTemp) {
//                        fileArrayTemp.release();
//                        fileArrayTemp = null;
//                    }
//
//                    fileArray.release();
//                }
//            }
//        }
//    }
//
//    public static void saveFile(String str) {
//        String filePath = null;
//        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//        if (hasSDCard) {
//            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "hello.txt";
//        } else
//            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "hello.txt";
//
//        try {
//            File file = new File(filePath);
//            if (!file.exists()) {
//                File dir = new File(file.getParent());
//                dir.mkdirs();
//                file.createNewFile();
//            }
//            FileOutputStream outStream = new FileOutputStream(file);
//            outStream.write(str.getBytes());
//            outStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void listFilesInDirandSubDirByMediaStore( ArrayList<String> listFiles, ArrayList<String> listFolders, String filePath, NameFilter filter ) {
        directListFilesInDirandSubDirByMediaStore( listFiles, listFolders, filePath, filter );
    }
    private static void directListFilesInDirandSubDirByMediaStore( ArrayList<String> listFiles, ArrayList<String> listFolders, String filePath, NameFilter filter ) {

        long nLimitUnit = 5000;
        long nLimitOffset = 0;
        AtomicBoolean bCallNext = new AtomicBoolean(true);
        try{
            for ( int i=0; i<10; i++) {
                directListFilesInDirandSubDirByMediaStore(listFiles, listFolders, filePath, filter,nLimitUnit, nLimitOffset, bCallNext );
                nLimitOffset += nLimitUnit;
                if ( !bCallNext.get() ){
                    break;
                }
            }
        }catch (Error e ) {
            OutOfMemoryError outOfMemoryError = new OutOfMemoryError( "directListFilesInDirandSubDirByMediaStore path:"+filePath+" nLimitOffset:"+nLimitOffset );
            outOfMemoryError.initCause(e);
            throw outOfMemoryError;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void directListFilesInDirandSubDirByMediaStore(ArrayList<String> listFiles, ArrayList<String> listFolders, String filePath, NameFilter filter, long nLimit, long nLimitOffset, AtomicBoolean bCallNext) {
        String selection = "_data > ? AND _data < ? and title!='.nomedia'";
        if ( listFiles != null && listFolders != null ) {

        }else if( listFiles != null ) {
            selection += " AND format != 12289"; //只检出文件
        }else {
            selection += " AND format = 12289"; //只检出目录
        }
        bCallNext.set(false);
        Cursor cursor = null;
        final String[] projection = {"_data", "format"};
        try {
            Context context = SpaceApplication.getInstance().getContext();
            cursor = context.getContentResolver()
                    .query(MediaStore.Files.getContentUri("external"),
                            projection, selection, new String[]{ FileUtils.addSlash(filePath), FileUtils.replaceEndSlashBy0(filePath)}, MediaStore.Files.FileColumns._ID +" limit "+nLimit+" offset "+nLimitOffset);
            if (cursor != null && cursor.moveToFirst()) {
                if ( cursor.getCount() == nLimit ) {
                    bCallNext.set(true);
                }
                do {
                    String subPathString =  cursor.getString(0);
                    long nFormat = cursor.getLong(1) ;
                    boolean bFolder = nFormat == 12289;
                    boolean bAccept = true;
                    if ( filter != null ) {
                        int nidx = subPathString.lastIndexOf('/');
                        if ( nidx != -1 ) {
                            bAccept = filter.accept( subPathString.substring(0, nidx), subPathString.substring(nidx+1), bFolder);
                        }
                    }
                    if ( bAccept ) {
                        if ( bFolder ) {
                            if(listFolders != null) listFolders.add( subPathString );
                        }else {
                            if(listFiles != null) listFiles.add( subPathString );
                        }
                    }
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
    }

//    public static Boolean fileExist(String path){
//        try {
//            File file = new File(path);
//            if (!file.exists()){
//                return false;
//            }
//        }catch (Exception e){
//            NLog.printStackTrace(e);
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     *获取SD卡后部分的相对路径
//     * @param path
//     * @return
//     */
//    public static String getFileRelativePath(String path){
//        if (!FileUtils.isValidExternalStorage()) {
//            return null;
//        }
//        String sdPath=Environment.getExternalStorageDirectory().toString();
//        if(sdPath==null) return path;
//        if(path.startsWith(sdPath)){
//            path=path.substring(sdPath.length());
//        }
//        return path;
//    }

    /**
     * 创建目录，包括必要的父目录的创建，如果未创建
     *
     * @param path
     *            待创建的目录路径
     * @return 返回操作结果
     */
    public static boolean mkdir(String path)
    {
        File file = new File(path);
        if (file.exists() && file.isDirectory())
        {
            return true;
        }

        file.mkdirs();
        return true;
    }

}

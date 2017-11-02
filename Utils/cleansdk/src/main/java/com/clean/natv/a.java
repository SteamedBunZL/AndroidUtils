package com.clean.natv;

import android.os.Environment;

import com.clean.spaceplus.cleansdk.base.bean.SpecialFolder;
import com.clean.spaceplus.cleansdk.base.exception.FailException;
import com.clean.spaceplus.cleansdk.base.utils.root.SuExec;
import com.clean.spaceplus.cleansdk.junk.engine.PathScanCallback;
import com.clean.spaceplus.cleansdk.junk.engine.ProgressCtrl;
import com.clean.spaceplus.cleansdk.junk.engine.util.NameFilter;
import com.clean.spaceplus.cleansdk.util.Env;
import com.hawkclean.framework.log.NLog;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author dongdong.huang
 * @Description:native方法调用
 * @date 2016/5/3 10:30
 * @copyright TCL-MIG
 */
public class a {
    /**
     * PathOperFunc.isEmptyFolder()的native实现
     * 判断是否空文件夹。
     * @param folder                 要判断的文件(夹)路径
     * @param maxLevel               最多检查子文件夹层数，如果存在超出层数的子文件夹，就判为非空文件夹。
     * @param progCtrl               流程控制对象
     * @param subEmptyFolderList     本参数若不为null，则本函数会将folder及其之下的空文件夹记录在本对象内。
     * @param uncheckedSubFolderList 本参数若不为null，则本函数会将folder之下的未完全检查的文件夹记录在本对象内。
     * @return 空文件夹返回true，否则返回false。
     * @throws NullPointerException 初始化状态异常时可能抛出
     * @throws FailException    执行判定和结果数据转换失败时抛出
     * @throws OutOfMemoryError     内存不足时抛出
     */
    public static native boolean a(
            String folder,
            int maxLevel,
            ProgressCtrl progCtrl,
            List<String> subEmptyFolderList,
            List<String> uncheckedSubFolderList);

    /**
     * PathOperFunc.computeRealSize()的native实现
     * 计算文件及文件夹大小
     * @param path      要计算的文件或文件夹路径
     * @param maxLevel  最大深度
     * @param progCtrl  流程控制对象
     * @param result    数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     * @param uncheckedSubFolderList 本参数若不为null，则本函数会将path之下因深
     *                                度限制而未深入枚举的文件夹路径记录在本对象内。
     *                                注意，此对象中记录的文件夹本身的size已计算在内。
     * @throws IllegalArgumentException result.length小于3时抛出
     * @throws NullPointerException     初始化状态异常时可能抛出
     * @throws com.clean.spaceplus.base.exception.FailException        执行计算和结果数据转换失败时抛出
     * @throws OutOfMemoryError         内存不足时抛出
     */
    public static native void a(
            String path,
            int maxLevel,
            ProgressCtrl progCtrl,
            long[] result,
            List<String> uncheckedSubFolderList);

    /**
     * native函数异常处理回调
     */
    public static void a() {
//        MyCrashHandler.getInstance().nativeCrashedHandler();
    }

    /**
     * 提供给native层调用infoc上报数据
     * @param tableName  表名
     * @param dataString 数据
     */
    public static void a(String tableName, String dataString) {
//        KInfocClientAssist.getInstance().reportData(tableName, dataString);
    }

    /**
     * 取当前CM的VersionCode
     */
    public static int b() {
        return Env.getRealVersionCode();
    }

    /**
     * 获取minidump存放路径，并确保此路径存在。
     */
    public static String c() {
//        File minidumpDir = new File(MyCrashHandler.getMiniDumpPath());
//        if (!minidumpDir.exists()) {
//            minidumpDir.mkdir();
//        }
//
//        if (!minidumpDir.exists()) {
//            return null;
//        }
//
//        return minidumpDir.getPath();
        return "";
    }

    /**
     * PathOperFunc.computeFileSize()的native实现
     * 计算文件大小
     * @param path		要计算的文件或文件夹路径
     * @param maxLevel	最大深度
     * @param progCtrl	流程控制对象
     * @param result	数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     * @param uncheckedSubFolderList	本参数若不为null，则本函数会将path之下因深
     *                               	度限制而未深入枚举的文件夹路径记录在本对象内。
     *                               	注意，此对象中记录的文件夹个数已计算在result[1]内。
     * @param pathCallback	文件全路径回调。可以为null
     * @throws IllegalArgumentException result.length小于3时抛出
     * @throws NullPointerException     初始化状态异常时可能抛出
     * @throws com.clean.spaceplus.base.exception.FailException        执行计算和结果数据转换失败时抛出
     * @throws OutOfMemoryError         内存不足时抛出
     */
    public static native void b(
            String path,
            int maxLevel,
            ProgressCtrl progCtrl,
            long[] result,
            List<String> uncheckedSubFolderList,
            d pathCallback);

    /**
     * PathOperFunc.computeFileSize()的native实现
     * 计算文件大小
     * @param path		要计算的文件或文件夹路径
     * @param maxLevel	最大深度
     * @param progCtrl	流程控制对象
     * @param result	数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     * @param uncheckedSubFolderList	本参数若不为null，则本函数会将path之下因深
     *                               	度限制而未深入枚举的文件夹路径记录在本对象内。
     *                               	注意，此对象中记录的文件夹个数已计算在result[1]内。
     * @param pathCallback	文件全路径回调。可以为null
     * @param bCalcSparseFileBlkSize	计算稀疏文件占用块大小
     * @param filterSubDirList	        子目录白名单
     * @throws IllegalArgumentException result.length小于3时抛出
     * @throws NullPointerException     初始化状态异常时可能抛出
     * @throws com.clean.spaceplus.base.exception.FailException        执行计算和结果数据转换失败时抛出
     * @throws OutOfMemoryError         内存不足时抛出
     */
    public static native void b(
            String path,
            int maxLevel,
            ProgressCtrl progCtrl,
            long[] result,
            List<String> uncheckedSubFolderList,
            d pathCallback,
            boolean bCalcSparseFileBlkSize,
            List<String> filterSubDirList);


    /**
     * PathOperFunc.computePatchFileSize()的native实现 (批量回调)
     * 计算文件大小
     * @param path 要计算的文件或文件夹路径
     * @param maxLevel 最大深度
     * @param isFilterNoMedia 是否过滤.nomedia文件，并且当存在.nomedia文件(夹)时，本函数返回值为true
     * @param isTimeLine 是否记录按时间线清理的数据
     * @param timeLine 时间线(单位：天)
     * @param progCtrl 流程控制对象
     * @param result 数组大小为3，result[0]:文件大小，result[1]：文件夹数，result[2]：文件数
     * @param resultCleanTime 数组大小为2, resultCleanTime[0]:时间线前的文件大小，resultCleanTime[1]：时间线前的文件数
     * @param uncheckedSubFolderList    本参数若不为null，则本函数会将path之下因深
     *                                  度限制而未深入枚举的文件夹路径记录在本对象内。
     *                                  注意，此对象中记录的文件夹个数已计算在result[1]内。
     * @param targetFiles 满足要求的文件列表，由native函数添加内容
     * @param pathCallback 文件全路径回调。可以为null
     * @throws IllegalArgumentException result.length小于3时抛出
     * @throws NullPointerException     初始化状态异常时可能抛出
     * @throws com.clean.spaceplus.base.exception.FailException        执行计算和结果数据转换失败时抛出
     * @throws OutOfMemoryError         内存不足时抛出
     * @return 当isFilterNoMedia为false时，返回值无意义；当isFilterNoMedia为true时，返回值表示当前文件夹下是否存在.nomedia文件(夹)名字
     */
    public static native boolean c(
            List<String> path,
            int maxLevel,
            boolean isFilterNoMedia,
            boolean isTimeLine,
            int timeLine,
            ProgressCtrl progCtrl,
            long[] result,
            long[] resultCleanTime,
            List<String> uncheckedSubFolderList,
            List<String> targetFiles,
            g pathCallback,
            boolean bCalcSparseFileBlkSize);

    /**
     * PathOperFunc.listDir()的native实现
     * 枚举dirPath文件夹下满足过滤条件的子项名字
     * @param dirPath 文件夹全路径
     * @param filter  过滤条件
     */
    public static native f a(String dirPath, NameFilter filter);

    /**
     * 传入UTF-8编码，构造一个String对象返回。
     * @param data
     * @return
     */
    public static String a(byte[] data) {
        String result = null;
        try {
            result = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    /**
     * 传入APK路径
     * 返回是否是正常的zip文件
     */
    public static native boolean a(String path);

    /**
     * 取得文件大小，如果为稀疏文件，则返回占用的块大小。
     * @param filePath 文件路径
     * @return 文件大小，如果为稀疏文件，则返回占用的块大小
     */
    public static native long b(String filePath);

    /**
     * 遍历目录获取 log,tmp,apk,大文件列表
     * @param strPath 要遍历的目录
     * @param progCtrl 流程控制对象
     * @param nMaxLevel 遍历的最大深度
     * @param nNotFindFileNumberLimit 某级目录中扫描文件和文件夹超过限制仍未找到nScanTypes标识的文件，则退出此目录的遍历. 为0时不限制
     * @param nPureFolderNumberLimit 纯目录个数限制。为0时无限制。
     * @param pignoreFolders 忽略的目录列表。可以为null
     * @param specialFolders 特殊目录列表。可以为null
     * @param nScanTypes 需要获取的文件类型。IPachScanCallback。TYPE_*的组合
     * @param scanCallback 找到文件后的回调接口
     */
    public static native void b( String strPath,
                                 ProgressCtrl progCtrl,
                                 int nMaxLevel,
                                 int nNotFindFileNumberLimit,
                                 int nPureFolderNumberLimit,
                                 List<String> pignoreFolders,
                                 List<SpecialFolder> specialFolders,
                                 int nScanTypes,
                                 PathScanCallback scanCallback );

    /**
     * PathOperFunc.deleteFileOrFolder()的native实现 删除文件或文件夹
     * @param result		     数组大小为3, result[0]:删除成功或失败  0:失败 1:成功  ，result[1]：文件夹数，result[2]：文件数
     * @param path			     要删除的文件或文件夹路径
     * @param delFlags        删除的控制参数
     * @param delFileTimeLimit    删除的时间控制参数
     * @param fileWhiteList   文件白名单
     * @param folderWhiteList 文件夹白名单
     * @param feedbackFileList
     * @param feedbackFolderList
     * @param pathCallback	      删除回调
     * @param bSecondSdCardCanWriteable 第二张SD卡是否可写标记。如果不可写，将使用策略删除（root用户直接使用root删除，非root用户使用MediaStore批量删除）
     * @param strFirstSdCardRootPath 第一张SD卡根路径
     * @param configCallBack 配置数据回调
     * @param ExternalStoragePaths 存储区路径
     * @param needRecycle
     * @param bRoot root标记。root用户为true, 否则为false
     * @throws IllegalArgumentException path为空时抛出
     * @throws NullPointerException     delCallback or path 初始化状态异常时可能抛出
     * @throws com.clean.spaceplus.base.exception.FailException        执行计算和结果数据转换失败时抛出
     * @throws OutOfMemoryError         内存不足时抛出
     */
    public static native void c(
            int[] result,
            List<String> path,
            int delFlags,
            int delFileTimeLimit,
            List<String> fileWhiteList,
            List<String> folderWhiteList,
            List<String> feedbackFileList,
            List<String> feedbackFolderList,
            d pathCallback,
            boolean bSecondSdCardCanWriteable,
            String strFirstSdCardRootPath,
            z configCallBack,
            List<String> ExternalStoragePaths,
            boolean needRecycle,
            boolean bRoot );

    public static void c(String tag, String message) {
        NLog.d(tag, message);
//		KInfocClientAssist.getInstance().reportData("cm_standard_photodetail",
//				"deletedetail=" + path);
    }

    private static String defaultSdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static boolean c(String path) {
//        if (Build.VERSION.SDK_INT >= 19 && !(path.startsWith(defaultSdCardPath))) {
//            File file = new File(path);
//            NLog.d("(M)deleteFile", path);
//            try {
//                return (new MyMediaFile(SpaceApplication.getInstance().getContext().getContentResolver(), file)).delete();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        }
        return false;
    }

    public static boolean d(String path) {
        if (SuExec.getInstance().checkRoot() /*&& !(path.startsWith(defaultSdCardPath))*/) {
            return SuExec.getInstance().deleteFilesLeftFoder(path);
        }
        return false;
    }

    /**
     * countFileInFolder
     * @param result [0] : 目录数  [1] : 文件数
     * @param pathList : 要扫的目录路径
     */
    public static native void b(
            int [] result,
            List<String> pathList);

    /**
     * PathOperFunc.IsSubDirNumMoreThan()的native实现
     * 判断一个文件夹下子文件/子文件夹个数是否>=给定的num
     * @param dirPath
     * @param num
     * @param filter
     * @return >=num true;<num false
     */
    public static native boolean b(String dirPath,int num);

    /**
     * 回调符合条件的目录
     */
    public static interface ea {
        /**
         * 回调符合条件的目录
         * @param strPath 目录路径
         * @param nModifiedTime_s 最后修改时间，单位：s
         * @param FilesCount 单层目录文件个数
         */
        public void a(String strPath, long nModifiedTime_s, long FilesCount);
    }

    /**
     * 结合MediaStore计算文件大小的接口
     * 由nNeedMSCalcFilesLimit限制哪些子目录可以使用MediaStore方式计算文件大小，则不用NDK计算直接通过msCalcCallback回调由MediaStore方式算目录大小
     * @param path 路径
     * @param maxLevel 扫描层级
     * @param progCtrl 控制接口
     * @param result 返回结果 ， 如果有目录通过msCalcCallback回调传出，则不会记录此目录的大小，但是会记录此目录的文件和目录个数。
     * @param uncheckedSubFolderList 为检查的子目录列表
     * @param pathCallback 路径回调
     * @param pathListCallback 路径回调2
     * @param bCalcSparseFileBlkSize 是否按块大小来计算文件大小的标志
     * @param nNeedMSCalcFilesLimit 需要使用MediaStore计算单层目录文件个数限制。只有等于或超过此限制，才能使用MediaStore计算大小
     * @param msCalcCallback 符合nNeedMSCalcFilesLimit的目录回调接口
     */
    public static native void b(
            String path,
            int maxLevel,
            ProgressCtrl progCtrl,
            long[] result,
            List<String> uncheckedSubFolderList,
            d pathCallback,
            g pathListCallback,
            boolean bCalcSparseFileBlkSize,
            int nNeedMSCalcFilesLimit,
            long nNeedMSCalcMTimeLimit_s,
            ea msCalcCallback );

    public static native void b(
            String path,
            ProgressCtrl progCtrl,
            long[] result,
            final d pathCallback,
            final g pathListCallback,
            boolean bCalcSparseFileBlkSize,
            List<String> knownFileList);

    /**
     * 设置检查稀疏文件的界限大小。单位：字节
     * 计算文件大小的时候会根据这个值来开启检查此文件是否为稀疏文件
     * @param nChkSparseFileLimitSize 单位：字节。如果为0则表示不检查稀疏文件
     */
    public static native void b( long nChkSparseFileLimitSize );
}

//package com.clean.spaceplus.cleansdk.util;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Environment;
//
//import SpaceApplication;
//import NLog;
//import CommonUtil;
//
//import java.io.File;
//
//import static android.os.Environment.MEDIA_MOUNTED;
//
///**
// * @author dongdong.huang
// * @Description:
// * @date 2016/5/16 16:52
// * @copyright TCL-MIG
// */
//public class StorageUtil {
//    private static final String TAG = StorageUtil.class.getSimpleName();
//    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
//    private static final String INDIVIDUAL_DIR_NAME = "uil-images";
//
//    private StorageUtil() {
//    }
//
//    /**
//     * Returns application cache directory. Cache directory will be created on SD card
//     * <i>("/Android/data/[app_package_name]/cache")</i> if card is mounted and app has appropriate permission. Else -
//     * Android defines cache directory on device's file system.
//     *
//     * @param context Application context
//     * @return Cache {@link File directory}.<br />
//     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
//     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
//     */
//    public static File getCacheDirectory(Context context) {
//        return getCacheDirectory(context, true);
//    }
//
//    /**
//     * Returns application cache directory. Cache directory will be created on SD card
//     * <i>("/Android/data/[app_package_name]/cache")</i> (if card is mounted and app has appropriate permission) or
//     * on device's file system depending incoming parameters.
//     *
//     * @param context        Application context
//     * @param preferExternal Whether prefer external location for cache
//     * @return Cache {@link File directory}.<br />
//     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
//     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
//     */
//    public static File getCacheDirectory(Context context, boolean preferExternal) {
//        File appCacheDir = null;
//        if (preferExternal && MEDIA_MOUNTED
//                .equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
//            appCacheDir = getExternalCacheDir(context);
//        }
//        if (appCacheDir == null) {
//            appCacheDir = context.getCacheDir();
//        }
//        if (appCacheDir == null) {
//            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
//            NLog.w("Can't define system cache directory! '%s' will be used.", cacheDirPath);
//            appCacheDir = new File(cacheDirPath);
//        }
//        return appCacheDir;
//    }
//
//    /**
//     * Returns individual application cache directory (for only image caching from ImageLoader). Cache directory will be
//     * created on SD card <i>("/Android/data/[app_package_name]/cache/uil-images")</i> if card is mounted and app has
//     * appropriate permission. Else - Android defines cache directory on device's file system.
//     *
//     * @param context Application context
//     * @return Cache {@link File directory}
//     */
//    public static File getIndividualCacheDirectory(Context context) {
//        File cacheDir = getCacheDirectory(context);
//        File individualCacheDir = new File(cacheDir, INDIVIDUAL_DIR_NAME);
//        if (!individualCacheDir.exists()) {
//            if (!individualCacheDir.mkdir()) {
//                individualCacheDir = cacheDir;
//            }
//        }
//        return individualCacheDir;
//    }
//
//    private static File getExternalCacheDir(Context context) {
//        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
//        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
//        if (!appCacheDir.exists()) {
//            if (!appCacheDir.mkdirs()) {
//                NLog.w(TAG, "Unable to create external cache directory");
//                return null;
//            }
//            try {
//                new File(appCacheDir, ".nomedia").createNewFile();
//            } catch (Exception e) {
//                NLog.i(TAG, "Can't create \".nomedia\" file in application external cache directory");
//            }
//        }
//        return appCacheDir;
//    }
//
//    private static boolean hasExternalStoragePermission(Context context) {
//        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
//        return perm == PackageManager.PERMISSION_GRANTED;
//    }
//
//    public static boolean hasReadExternalStoragePermission() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
//            return true;
//        }
//        PackageManager manager = SpaceApplication.getInstance().getContext().getPackageManager();
//        return (PackageManager.PERMISSION_GRANTED ==
//                manager.checkPermission( Manifest.permission.READ_EXTERNAL_STORAGE, CommonUtil.getPkgName()));
//    }
//}

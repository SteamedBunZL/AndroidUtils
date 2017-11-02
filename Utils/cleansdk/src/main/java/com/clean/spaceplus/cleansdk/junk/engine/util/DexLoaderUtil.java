//package com.clean.spaceplus.cleansdk.junk.engine.util;
//
//import android.content.Context;
//
//import com.clean.spaceplus.cleansdk.util.FileUtils;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//import dalvik.system.DexClassLoader;
//import space.network.util.RuntimeCheck;
//
///**
// * @author Jerry
// * @Description:
// * @date 2016/5/5 15:46
// * @copyright TCL-MIG
// */
//public class DexLoaderUtil {
//    private static final String REPORTLIB_NAME = "cmrlib.jar";
//    private static final String JUNKLIB_NAME = "junkext.jar";
//    private static final String PROCLIB_NAME = "pext.jar";
//    private static final String CPULIB_NAME = "cext.jar";
//    private static final String CPULIB_VERSION_PRE = "cextver";
//    private static final String CPULIB_DEX_PRE = "cext";
//    private static final String JUNKLIB_VERSION_PRE = "extver";
//    private static final String JUNKLIB_DEX_PRE = "ext";
//    private static final String DEX_EXT = ".dex";
//
//    public static final String CLS_APP_LISTENER = "com.cleanmaster.libs.watcher.AppListener";
//    public static final String CLS_ENV_HELPER = "com.cleanmaster.libs.envcollect.EnvHelper";
//    public static final String CLS_JUNK_FACTORY = "com.junkext.factory.ExtCleanerFactory";
//    public static final String CLS_SNPROC_FILTER = "com.cleanmaster.procext.SocialNetworkProcFilter";
//    public static final String CLS_CPUTEMP_WATCHER = "com.cleanmaster.cpu.temp.utils.TempWatcher";
//
//    private static DexClassLoader sReportDexLoader = null;
//    private static DexClassLoader sJunkDexLoader = null;
//    private static DexClassLoader sProcDexLoader = null;
//    private static DexClassLoader sCpuDexLoader = null;
//    private static int currentVersion = -1;
//
//    public static final int VERSION_OF_JAR = 19;
//    public static synchronized Object createInstance(Context context, String clazzName) {
//        if (context == null || clazzName == null) {
//            return null;
//        }
//
//        if (RuntimeCheck.IsServiceProcess()) {
//           /* JarMonitor monitor = new JarMonitor(context, clazzName);
//            MonitorManager.getInstance().addMonitor(
//                    MonitorManager.TYPE_DOWN_JAR_NOTIFY_SERVICE,
//                    monitor,
//                    MonitorManager.PRIORITY_NORMAL);*/
//        }
//
//        return createInstanceImpl(context, clazzName);
//    }
//
//
//
//
//
//
//    private static synchronized Object createInstanceImpl(Context context, String clazzName) {
//        DexClassLoader loader = getDexLoader(clazzName);
//        if (loader != null) {
//            // cached loader exist, we can use it to create class directly
//            return loadClass(loader, clazzName);
//        }
//
//        File filesDir = FileUtils.getFilesDir(context);
//        if (filesDir == null) {
//            return null;
//        }
//        String libName = getLibName(clazzName);
//        String libPath = FileUtils.addSlash(filesDir.getAbsolutePath()) + libName;
//        File cloudJar = getCloudJar(clazzName);
//        if (cloudJar != null) {
//            // cloud has new update, copy to file-dir
//            copyLibrary(cloudJar, libPath);
//            cloudJar.delete();
//            if (getLibName(clazzName).equals(CPULIB_NAME)) { // only for cpulib
//                deleteOldDexFile(filesDir, CPULIB_DEX_PRE);
//            } else if (getLibName(clazzName).equals(JUNKLIB_NAME)) { // only for junkext
//                deleteOldDexFile(filesDir, JUNKLIB_DEX_PRE);
//            }
//        }
//
//        boolean isLibExist = false;
//        File libFile = new File(libPath);
//        if (getLibName(clazzName).equals(CPULIB_NAME)) { // only for cpulib
//            isLibExist = handleJarVersion(context, clazzName, libFile, filesDir,
//                    VERSION_OF_JAR, CPULIB_VERSION_PRE, CPULIB_DEX_PRE);
//        } else if (getLibName(clazzName).equals(JUNKLIB_NAME)) { // only for junkext
//            isLibExist = handleJarVersion(context, clazzName, libFile, filesDir,
//                    VERSION_OF_JAR, JUNKLIB_VERSION_PRE, JUNKLIB_DEX_PRE);
//        } else {
//            if (!libFile.exists()) {
//                // no jar from cloud, we use jar from res
//                int resId = getResourceId(clazzName);
//                if (resId != -1) {
//                    isLibExist = copyFileFromRaw(resId, libFile, context);
//                }
//            } else if (libFile.length() > 0) {
//                // already copy from cloud or copy from res
//                isLibExist = true;
//            }
//        }
//
//        Object obj = null;
//        if (isLibExist) {
//            // jar is ready, we can do dex-opt
//            String dexFolder = filesDir.getAbsolutePath();
//            try {
//                loader = new DexClassLoader(libPath, dexFolder, null, context.getClass().getClassLoader());
//            } catch (Exception e) {
//                // Not sure why this happened, workaround here
//                // Looks like only root user will encounter this issue
//                // Dumpkey id: 1203201404
//                e.printStackTrace();
//            }
//
//            if (loader != null) {
//                cacheDexLoader(loader, clazzName);
//                obj = loadClass(loader, clazzName);
//            }
//        }
//
//
//        return obj;
//    }
//
//
//    private static DexClassLoader getDexLoader(String clazz) {
//        if (CLS_APP_LISTENER.equals(clazz) ||
//                CLS_ENV_HELPER.equals(clazz)) {
//            return sReportDexLoader;
//        } else if (CLS_JUNK_FACTORY.equals(clazz)) {
//            return sJunkDexLoader;
//        } else if (CLS_SNPROC_FILTER.equals(clazz)) {
//            return sProcDexLoader;
//        } else if (CLS_CPUTEMP_WATCHER.equals(clazz)) {
//            return sCpuDexLoader;
//        } else {
//            return null;
//        }
//    }
//
//    private static Object loadClass(DexClassLoader dexLoader, String clazzName) {
//        Object obj = null;
//        try {
//            Class<?> clazz = ((ClassLoader)dexLoader).loadClass(clazzName);
//            obj = clazz.newInstance();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return obj;
//    }
//
//
//    private static File getCloudJar(String clazz) {
//       /* String downloadPath = PushUtil.loadPushService().getDownloadPath(MoSecurityApplication.getAppContext().getApplicationContext(),
//                String.valueOf(MessageChannel.CHANNEL_COMMON_JAR.value()), getJarAction(clazz));*/
//        String downloadPath = "";
//        if (downloadPath != null) {
//            File downloadFile = new File(downloadPath);
//            if (downloadFile.exists()) {
//                return downloadFile;
//            }
//        }
//        return null;
//    }
//
//
//    private static boolean copyLibrary(File downloadFile, String outFile) {
//        FileOutputStream out = null;
//        FileInputStream in = null;
//        try {
//            in = new FileInputStream(downloadFile);
//            out = new FileOutputStream(outFile);
//
//            byte buf[] = new byte[1024];
//            int len;
//            while ((len = in.read(buf)) > 0) {
//                out.write(buf, 0, len);
//            }
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            try {
//                if (out != null)
//                    out.close();
//                if (in != null)
//                    in.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static String getLibName(String clazz) {
//        if (CLS_APP_LISTENER.equals(clazz) ||
//                CLS_ENV_HELPER.equals(clazz)) {
//            return REPORTLIB_NAME;
//        } else if(CLS_JUNK_FACTORY.equals(clazz)) {
//            return JUNKLIB_NAME;
//        } else if (CLS_SNPROC_FILTER.equals(clazz)) {
//            return PROCLIB_NAME;
//        } else if (CLS_CPUTEMP_WATCHER.equals(clazz)) {
//            return CPULIB_NAME;
//        } else {
//            return null;
//        }
//    }
//
//    private static void cacheDexLoader(DexClassLoader dexLoader, String clazz) {
//        if (CLS_APP_LISTENER.equals(clazz) ||
//                CLS_ENV_HELPER.equals(clazz)) {
//            sReportDexLoader = dexLoader;
//        } else if (CLS_JUNK_FACTORY.equals(clazz)) {
//            sJunkDexLoader = dexLoader;
//        } else if (CLS_SNPROC_FILTER.equals(clazz)) {
//            sProcDexLoader = dexLoader;
//        } else if (CLS_CPUTEMP_WATCHER.equals(clazz)) {
//            sCpuDexLoader = dexLoader;
//        }
//    }
//
//
//    private static void createNewVersionFile(File folder, final String pre, final int version) {
//        if (folder == null) {
//            return;
//        }
//        String filePath = FileUtils.addSlash(folder.getAbsolutePath()) + pre + "." + version;
//        File verFile = new File(filePath);
//        if (!verFile.exists()) {
//            try {
//                verFile.createNewFile();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static void deleteOldVersionFile(File folder, final String pre, final int version) {
//        if (folder == null) {
//            return;
//        }
//        String filePath = FileUtils.addSlash(folder.getAbsolutePath()) + pre + "." + version;
//        File verFile = new File(filePath);
//        if (verFile.exists()) {
//            verFile.delete();
//        }
//    }
//
//    private static void deleteOldDexFile(File folder, final String pre) {
//        if (folder == null) {
//            return;
//        }
//        String filePath = FileUtils.addSlash(folder.getAbsolutePath()) + pre + DEX_EXT;
//        File dexFile = new File(filePath);
//        if (dexFile.exists()) {
//            dexFile.delete();
//        }
//    }
//
//
//    private static boolean handleJarVersion(Context context, String clazzName,
//                                            File libFile, File filesDir, int newVersion, String verPre, String dexPre) {
//
//        boolean isLibExist = false;
//        currentVersion  = getCurrentVersion(filesDir, verPre);
//        boolean isDiffVersion = (newVersion != currentVersion)? true:false;
//        if (isDiffVersion || !libFile.exists()) {
//            // delete old version file and dex file
//            deleteOldVersionFile(filesDir, verPre, currentVersion);
//            deleteOldDexFile(filesDir, dexPre);
//            // no jar from cloud, we use jar from res
//            int resId = getResourceId(clazzName);
//            if (resId != -1) {
//                isLibExist = copyFileFromRaw(resId, libFile, context);
//                createNewVersionFile(filesDir, verPre, newVersion);
//            }
//        } else if (libFile.length() > 0) {
//            // already copy from cloud or copy from res
//            isLibExist = true;
//        }
//        return isLibExist;
//    }
//
//
//    private static int getCurrentVersion(File folder, final String pre) {
//        if (folder == null) {
//            return -1;
//        }
//        if (!folder.exists()) {
//            return -1;
//        }
//        String[] allFile = folder.list();
//        if (allFile == null) {
//            return -1;
//        }
//        for (String f : allFile) {
//            if (f.startsWith(pre)) {
//                if (f.length() > pre.length()) {
//                    String verString = f.substring(pre.length()+1, f.length());
//                    try {
//                        int ver = Integer.valueOf(verString);
//                        return ver;
//                    } catch (Exception e) {
//                        return -1;
//                    }
//                }
//            }
//        }
//        return -1;
//    }
//
//
//    private static boolean copyFileFromRaw(int resId, File file, Context context) {
//
//        FileOutputStream out = null;
//        InputStream is = null;
//        try {
//            out = new FileOutputStream(file, false);
//            is = context.getResources().openRawResource(resId);
//            byte buf[] = new byte[1024];
//            int len;
//            int totalLen = 0;
//            while ((len = is.read(buf)) > 0) {
//                out.write(buf, 0, len);
//                totalLen += len;
//            }
//            return (totalLen > 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            try {
//                if (out != null)
//                    out.close();
//                if (is != null)
//                    is.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    private static int getResourceId(String clazz) {
//        return -1;
//    }
//
//
//}

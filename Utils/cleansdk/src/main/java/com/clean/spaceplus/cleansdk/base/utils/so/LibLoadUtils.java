//package com.clean.spaceplus.cleansdk.base.utils.so;
//
//import android.app.Application;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.text.TextUtils;
//import android.util.Log;
//
//import SpaceApplication;
//import com.clean.spaceplus.cleansdk.base.config.CommonConfigSharedPref;
//import com.clean.spaceplus.cleansdk.util.FileUtils;
//import com.clean.spaceplus.cleansdk.util.md5.Md5Util;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipException;
//import java.util.zip.ZipFile;
//
//import space.network.util.RuntimeCheck;
//
///**
// * @author liangni
// * @Description:
// * @date 2016/4/23 17:13
// * @copyright TCL-MIG
// */
//public class LibLoadUtils {
//
//    private static boolean 				DEBUG = false;
//
//    private static String 				ZIP_LIB_PATH = "lib/armeabi/";
//
//    private static Object mMutexForSoFileSync = new Object();
//
//
//    private String 						mLibFullPath = null;
//    private String 						mLibPath ;
//    private String 						mApkPath ;
//    private String [] 					mSoPathArray = {"libkcmutil.so", "libkcmsyscore.so", "libkcmlzma.so"};
//    private Application mAppInstance;
//    private String 						mNowVersion;
//    private String 						mSystemLibPath  ;
//    private String 						mSoName ;
//    private String 						mLibName;
//    private ZipFile mZipFile = null;
//
//    public LibLoadUtils(String libName){
//        mLibName = libName;
//        mAppInstance = SpaceApplication.getInstance();
//        mApkPath = mAppInstance.getPackageResourcePath();
//        // 例如：mApkPath: /system/app/com.cleanmaster.mguard_cn-1.apk
//        mLibPath = FileUtils.addSlash(mAppInstance.getApplicationInfo().dataDir) + "files/lib/";
//        // 例如：mLibPath: /data/data/com.cleanmaster.mguard_cn/files/lib/
//        mSoName = System.mapLibraryName(libName);
//        // 例如：mSoName: libkcmutil.so
//        mSystemLibPath = FileUtils.addSlash(mAppInstance.getApplicationInfo().dataDir) + "lib/"  + mSoName;
//        // 例如：mSystemLibPath: /data/data/com.cleanmaster.mguard_cn/lib/libkcmutil.so
//        File file = new File(mLibPath);
//        file.mkdirs();
//    }
//
//    public String GetOwnCopySoPath(){
//        return getLibPath(mSoName);
//    }
//
//    public String GetSystemCopySoPath(){
//        return mSystemLibPath;
//    }
//
//
//    /**
//     * 加载动态库
//     * @return
//     */
//    public boolean load(){
//        try {
//            int rc = syncSoFiles();
//            switch (rc) {
//                case 1:
//                    return loadFromSysCopyLibPath();
//
//                case 0:
//                default:
//                    return false;
//                case 2:
//                    break;
//            }
//
//
//
//            File soFile = new File(getLibPath(mSoName));
//            if(soFile.exists()){
//                System.load(getLibPath(mSoName));
//                mLibFullPath = soFile.getPath();
//            }else{
//                return false;
//            }
//            if(DEBUG){
//                Log.e("", "load sucess" + mLibPath + mSoName);
//            }
//            return true;
//        } catch (ZipException e) {
//            return false;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public boolean isLibraryOk() {
//        return ((!TextUtils.isEmpty(mLibFullPath)) && (new File(mLibFullPath).exists()));
//    }
//
//    public String getLibFullPath() {
//        return mLibFullPath;
//    }
//    public String getHeadMd5String(File file) {
//
//        if (null == file) {
//            return null;
//        }
//
//        InputStream inputStream = null;
//
//        try {
//            inputStream = new FileInputStream(file);
//            byte[] b = new byte[4 * 1024];
//
//            inputStream.read(b);
//            return Md5Util.getByteArrayMD5(b);
//        } catch (ZipException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }finally {
//            if (null != inputStream) {
//                try {
//                    inputStream.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                inputStream = null;
//            }
//        }
//
//        return null;
//    }
//
//    /**
//     * 判断是否需要复制
//     * @return
//     */
//    public boolean isNeedCopy(String soName, String soPath){
//        CommonConfigSharedPref scm = CommonConfigSharedPref.getInstanse(mAppInstance);
//        long zipSize = scm.getLibSoSize2(soName);
//        String md5_in_apk = scm.getLibSoHeadMd5(soName);
//        File file = new File(soPath);
//        if(file.exists() && file.length() == zipSize) {
//            String md5 = getHeadMd5String(file);
//
//            if (null == md5 && null == md5_in_apk) {
//                return false;
//            }
//
//            if (null == md5 || null == md5_in_apk) {
//                return true;
//            }
//
//            return !(md5.equals(md5_in_apk));
//        }
//        return true;
//    }
//
//
//    /**
//     * @return 0:加载系统默认路径 1:加载mSystemLibPath路径 2:加载自拷贝路径
//     * @throws ZipException
//     * @throws IOException
//     */
//    public int syncSoFiles() throws ZipException, IOException {
//
//        if (RuntimeCheck.IsServiceProcess()) {
//            synchronized (mMutexForSoFileSync) {
//                boolean isUpdate = false;
//                if(!checkVersion()){
//                    syncCopySo();
//                    isUpdate = true;
//                }
//                if(!isNeedCopy(mSoName, mSystemLibPath)){
//                    return 1;
//                }
//                if (isUpdate || isNeedCopy(mSoName, getLibPath(mSoName))) {
//                    startCopy();
//                }
//            }
//            return 2;
//        } else {
//            //nilo ipc 延后开发
//            /*try {
//                return SyncIpcCtrl.getIns().getIPCClient().syncSoFile(mLibName);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }*/
//            return 0;
//        }
//    }
//
//    private boolean loadFromSysCopyLibPath() {
//        System.load(mSystemLibPath);
//        mLibFullPath = mSystemLibPath;
//        return true;
//    }
//
//    /**
//     * 获取在zip中的路径
//     * @param libName
//     * @return
//     */
//    private String getZipPath(String libName){
//        return ZIP_LIB_PATH + libName;
//    }
//
//    /**
//     * 获取在data/data 下的路径
//     * @param libName
//     * @return
//     */
//    private String getLibPath (String libName){
//        return mLibPath + libName;
//    }
//
//    /**
//     * 把size 存起来
//     * @throws ZipException
//     * @throws IOException
//     */
//    private void syncCopySo() throws ZipException, IOException{
//        ZipFile zip = new ZipFile(new File(mApkPath));
//        CommonConfigSharedPref scm = CommonConfigSharedPref.getInstanse(mAppInstance);
//        for(String soPath : mSoPathArray){
//            ZipEntry entry = zip.getEntry(getZipPath(soPath));
//            if (null == entry)
//                continue;
//
//            scm.setLibSoSize2(soPath, entry.getSize());
//            byte[] b = new byte[4 * 1024];
//            InputStream inputStream = null;
//            String md5 = null;
//
//            try {
//                inputStream = zip.getInputStream(entry);
//
//                inputStream.read(b);
//                md5 = Md5Util.getByteArrayMD5(b);
//
//                inputStream.close();
//                inputStream = null;
//            } catch (ZipException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (null != inputStream) {
//                    try {
//                        inputStream.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    inputStream = null;
//                }
//            }
//
//            scm.setLibSoHeadMd5(soPath, md5);
//        }
//        close();
//        mZipFile = zip;
//        scm.setSoVersion(mNowVersion);
//    }
//
//    /**
//     * 开始复制
//     * @throws ZipException
//     * @throws IOException
//     */
//    private void startCopy() throws ZipException, IOException{
//        if(mZipFile == null){
//            mZipFile = new ZipFile(new File(mApkPath));
//        }
//        File file = new File(getLibPath(mSoName));
//        if(file.exists())
//            file.delete();
//        byte[] b = new byte[4 * 1024];
//        int length = 0 ;
//        ZipEntry entry = mZipFile.getEntry(getZipPath(mSoName));
//        if (entry == null)
//            return;
//
//        OutputStream outputStream = new FileOutputStream(file);
//        InputStream inputStream = mZipFile.getInputStream(entry);
//        while ((length = inputStream.read(b)) > 0) {
//            outputStream.write(b, 0, length);
//        }
//        inputStream.close();
//        outputStream.close();
//        if(DEBUG){
//            Log.e("", "copy_so" + file.getPath());
//        }
//    }
//
//    /**
//     * 检测版本
//     * @return
//     */
//    private  boolean checkVersion(){
//        PackageManager packageManager = mAppInstance.getPackageManager();
//        PackageInfo packInfo;
//        try {
//            packInfo = packageManager.getPackageInfo(mAppInstance.getPackageName(),0);
//            String version = packInfo.versionCode+"";
//            mNowVersion = version;
//            String perVersion = CommonConfigSharedPref.getInstanse(mAppInstance).getSoVersion();
//            if(DEBUG){
//                Log.e("", "check_version" + version.equals(perVersion));
//            }
//            return version.equals(perVersion);
//        } catch (/*NameNotFoundException*/Exception e) {
//            return false;
//        }
//    }
//
//    public void close(){
//        if(mZipFile != null)
//            try {
//                mZipFile.close();
//                mZipFile =null;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//    }
//}

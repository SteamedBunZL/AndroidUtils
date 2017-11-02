package com.clean.spaceplus.cleansdk.util;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.hawkclean.framework.log.NLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liangni
 * @Description:获取手机相关信息Util
 * @date 2016/4/11 16:54
 * @copyright TCL-MIG
 */
public class PhoneUtil {

//    private static final String TAG = PhoneUtil.class.getName();
//    public static final String SHARED_NAME = "phone_util";
//    public static final String IMEI_SHARED_NAME = "imei";
//    public static final String USER_ID_FILE_NAME = "userId";
//    private static final String MODEL_SAMSUNG_N9006 = "SM-N9006";

    /**
     * 获取手机机型信息
     * 如果pixi android5手机获取model的api为反射ro.build.product方式
     * @return
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

//    /**
//     * 部分手机不能正常跳转到频率设置页
//     * @return
//     */
//    public static boolean isUsageFreqExcludeModel(){
//        String model = getPhoneModel();
//        if(MODEL_SAMSUNG_N9006.equals(model)){
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 获取手机的Android_ID
//     * @return
//     */
//    public static String getAndroidId(Context context){
//        return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//    }
//
//    /**
//     * 获取手机MAC地址
//     * 这个方法有问题，在6.0以上版本没有权限的情况下，拿到的地址是一样的
//     * @return
//     */
//    public static String getMacAddress() {
//
//        String macAddress = null;
//
//        WifiManager wifiManager =
//                (WifiManager) SpaceApplication.getInstance().getContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = (null == wifiManager ? null : wifiManager.getConnectionInfo());
//        if (!wifiManager.isWifiEnabled())
//        {
//            //必须先打开，才能获取到MAC地址
//            wifiManager.setWifiEnabled(true);
//            wifiManager.setWifiEnabled(false);
//        }
//        if (info != null) {
//            macAddress = info.getMacAddress();
//        }
//        if (macAddress == null){
//            macAddress = "spacs";
//        }
//        return macAddress;
//    }
//
//    /**
//     * 获取通过InstanceID和GUID地址生成的UserID；
//     * @param context
//     * @return 进行MD5加密后的UUID
//     */
//    public static String getUserID(Context context){
////        String uuid = getIMEI(context);
////        if ("".equals(uuid)){
////            uuid = getMacAddress();
////        }
////        return  Md5Util.getStringMd5(uuid);
//        FileInputStream fi = null;
//        String userId = null;
//        try {
//            fi = context.openFileInput(USER_ID_FILE_NAME);
//            byte[] b=new byte[fi.available()];
//            fi.read(b);
//            fi.close();
//            userId = Arrays.toString(b);
//        } catch (Exception e){
//            NLog.i(TAG, "file is not exist");
//        }
//        if (userId == null || userId.equals("") ) {
////            context.getFileStreamPath(USER_ID_FILE_NAME);
////            userId = getInstanceId(context) + getGUID();
//            userId =  getGUID();
//            FileOutputStream fo = null;
//            try {
//                fo = context.openFileOutput(USER_ID_FILE_NAME, Context.MODE_PRIVATE);
//                fo.write(userId.getBytes());
//                fo.flush();
//                fo.close();
//            } catch (Exception e) {
//                NLog.printStackTrace(e);
//            }
//        }
//        return Md5Util.getStringMd5(userId);
//    }

//    /**
//     *获取InstanceId
//     * @param context
//     * @return
//     */
//    public static String getInstanceId(Context context){
//        String instanceId = "";
//        try{
//            if (!FirebaseApp.getApps(SpaceApplication.getInstance().getContext()).isEmpty()) {
//                InstanceID instanceID = InstanceID.getInstance(SpaceApplication.getInstance().getContext());
//                if (instanceID != null) {
//                    instanceId = instanceID.getId();
//                }
//            }
//
//        }catch (Exception e){
//            //todo google InstanceId 在有个别情况下拿不到，会抛出异常，后面再来解决
//            NLog.printStackTrace(e);
//            instanceId = "SpacePlus";
//        }
//        return instanceId;
//    }

//    /**
//     * 获取UUID
//     * @return
//     */
//    public static String getGUID() {
//        String guid = UUID.randomUUID().toString();
//        return guid.replace("-","");
//    }
//    /**
//     * 获取imei
//     * @param context
//     * @return
//     */
//    public static String getIMEI(Context context) {
//        if (context == null)
//            return "";
//        SharedPreferences sp = context.getSharedPreferences(SHARED_NAME, Context.MODE_APPEND);
//        String imei = sp.getString(IMEI_SHARED_NAME, "");
//        if (!imei.equals("")){
//            return imei;
//        }
//        //检查权限
//        int permissionCheck = ContextCompat.checkSelfPermission(context,
//                Manifest.permission.READ_PHONE_STATE);
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
//            return "";
//        }
//
//        final TelephonyManager tm = (TelephonyManager)context
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        imei = tm.getDeviceId();
//        if (imei == null){
//            imei = "";
//        }
//        //存储imei号
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString(IMEI_SHARED_NAME, imei);
//        SharePreferenceUtil.applyToEditor(editor);
//        return imei;
//    }

//    /**
//     * 检查剩余空间
//     * @return 以B为单位
//     */
//    public static long residueUsedSpace() {
//        long mTodaySize;
//        long mTotalSize;
//        long mSize;
//        long mSizeAll;
//        long mSystemSize = 0l;
//        long mSystemUsedSize = 0l;
//        long mSDcardSize = 0l;
//        long mSDcardUsedSize = 0l;
//        long mPhoneSize = 0l;
//        long mPhoneUsedSize = 0l;
//        int mSystemPercent = 0;
//        int mInternalPercent = 0;
//        int mSDCardPercent = 0;
//
//        mTodaySize = CleanedInfo.getInstance().getTodayCleanedSize();
//        mTotalSize = CleanedInfo.getInstance().getTotalCleanedSize();
//        mSize = 0l;
//        mSizeAll = 0l;
//
//
//        StorageInfo systemInfo = StorageInfoUtils.getDeviceStorageInfo();
//        StorageInfo removeSdcardInfo = Commons.getRemovableSdCardsStorageInfo();
//        StorageInfo internalSdInfo = null;
//        if (!EmulateSdCardUtils.s_bSdcardDataSame) {
//            internalSdInfo = Commons.getInternalSdCardsStorageInfo();
//        }
//
//        if (null != systemInfo && 0 != systemInfo.allSize) {
//
//            long low = SystemProperties.getLong("sys.memory.threshold.low", 0L);
//
//            low = Math.min(systemInfo.freeSize, low);
//            systemInfo.freeSize = systemInfo.freeSize - low;
//            mSystemSize = systemInfo.allSize;
//            mSystemUsedSize = systemInfo.allSize - systemInfo.freeSize;
//            mSize += mSystemUsedSize;
//            mSizeAll += mSystemSize;
//
//            if (mSystemSize != 0) {
//                try {
//                    mSystemPercent = Commons.calcPercentage(mSystemUsedSize,
//                            mSystemSize);
//                } catch (IllegalArgumentException e) {
//                    String log = String
//                            .format("systemInfo.allSize = %s, systemInfo.freeSize = %s",
//                                    systemInfo.allSize, systemInfo.freeSize);
//                    if (PublishVersionManager.isTest()) {
//                        throw new RuntimeException(log);
//                    }
//                }
//            }
//
//        }
//
//        if (null != removeSdcardInfo && 0 != removeSdcardInfo.allSize) {
//
//            mSDcardSize = removeSdcardInfo.allSize;
//            mSDcardUsedSize = removeSdcardInfo.allSize
//                    - removeSdcardInfo.freeSize;
//
//            mSize += mSDcardUsedSize;
//            mSizeAll += mSDcardSize;
//            if (mSDcardSize != 0) {
//                try {
//                    mSDCardPercent = Commons.calcPercentage(mSDcardUsedSize,
//                            mSDcardSize);
//                } catch (IllegalArgumentException e) {
//                    String log = String
//                            .format("removeSdcardInfo.allSize = %s, removeSdcardInfo.freeSize = %s",
//                                    removeSdcardInfo.allSize,
//                                    removeSdcardInfo.freeSize);
//                    if (PublishVersionManager.isTest()) {
//                        throw new RuntimeException(log);
//                    }
//                }
//            }
//
//        }
//
//        if (null != internalSdInfo && 0 != internalSdInfo.allSize) {
//
//            mPhoneSize = internalSdInfo.allSize;
//            mPhoneUsedSize = internalSdInfo.allSize - internalSdInfo.freeSize;
//            mSize += mPhoneUsedSize;
//            mSizeAll += mPhoneSize;
//            if (mPhoneSize != 0) {
//                try {
//                    mInternalPercent = Commons.calcPercentage(mPhoneUsedSize,
//                            mPhoneSize);
//                } catch (Exception e) {
//                    String log = String
//                            .format("internalSdInfo.allSize = %s, internalSdInfo.freeSize = %s",
//                                    internalSdInfo.allSize,
//                                    internalSdInfo.freeSize);
//                    if (PublishVersionManager.isTest()) {
//                        throw new RuntimeException(log);
//                    }
//                }
//            }
//
//        }
//        return mSizeAll - mSize;
//    }

    public static String getCpuType() {
        String[] cpuStrArr = getCpuInfo();
        return cpuStrArr != null ? ((!cpuStrArr[0].trim().isEmpty()) ? cpuStrArr[0] : cpuStrArr[1]) : null; // 获取不到Processor就获取Hardware
    }

    public static String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                arrayOfString = str2.split("\\s+");
                if (("Processor").equals(arrayOfString[0])) {
                    for (int i = 2; i < arrayOfString.length; i++) {
                        cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
                    }
                }
                if ((("Hardware").equals(arrayOfString[0]))) {
                    for (int i = 2; i < arrayOfString.length; i++) {
                        cpuInfo[1] = cpuInfo[1] + arrayOfString[i] + " ";
                    }
                }
            }
            localBufferedReader.close();
        } catch (Exception e) {
            NLog.printStackTrace(e);
            return cpuInfo;
        }
        return cpuInfo;
    }

    public static String getPhoneBrand(){
        return Build.BRAND;
    }

    public static String getOsVersionName(){
        return Build.VERSION.RELEASE;
    }

//    public static long getTotalStorageSizeByte(){
//        long mSizeAll = 0l;
//        long mSystemSize = 0l;
//        long mSDcardSize = 0l;
//        long mPhoneSize = 0l;
//        StorageInfo systemInfo = StorageInfoUtils.getDeviceStorageInfo();
//        StorageInfo removeSdcardInfo = Commons.getRemovableSdCardsStorageInfo();
//        StorageInfo internalSdInfo = null;
//        if (!EmulateSdCardUtils.s_bSdcardDataSame) {
//            internalSdInfo = Commons.getInternalSdCardsStorageInfo();
//        }
//
//        if (null != systemInfo && 0 != systemInfo.allSize) {
//
//            long low = SystemProperties.getLong("sys.memory.threshold.low", 0L);
//
//            low = Math.min(systemInfo.freeSize, low);
//            systemInfo.freeSize = systemInfo.freeSize - low;
//            mSystemSize = systemInfo.allSize;
//            mSizeAll += mSystemSize;
//
//            if (mSystemSize != 0) {
//                try {
//
//                } catch (Exception e) {
//                    String log = String
//                            .format("systemInfo.allSize = %s, systemInfo.freeSize = %s",
//                                    systemInfo.allSize, systemInfo.freeSize);
//                }
//            }
//
//        }
//
//        if (null != removeSdcardInfo && 0 != removeSdcardInfo.allSize) {
//
//            mSDcardSize = removeSdcardInfo.allSize;
//            mSizeAll += mSDcardSize;
//        }
//
//        if (null != internalSdInfo && 0 != internalSdInfo.allSize) {
//
//            mPhoneSize = internalSdInfo.allSize;
//            mSizeAll += mPhoneSize;
//
//        }
//        return mSizeAll;
//    }

//    public static boolean externalMemoryAvailable() {
//        return Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED);
//    }

    public static long getTotalInternalStorageSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

//    public static long getTotalExternalStorageSize() {
//        if (externalMemoryAvailable()) {
//            File path = Environment.getExternalStorageDirectory();
//            StatFs stat = new StatFs(path.getPath());
//            long blockSize = stat.getBlockSize();
//            long totalBlocks = stat.getBlockCount();
//            return totalBlocks * blockSize;
//        } else {
//            return 0;
//        }
//    }

    /**
     * 获取外置SD卡路径
     * @return	应该就一条记录或空
     */
    public static List<String> getExtSDCardPath()
    {
        List<String>  lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard"))
                {
                    String [] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory())
                    {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }

    public static long getTotalExternalStorageSize(String path) {
        try{
            if(TextUtils.isEmpty(path))return 0;
            File pathFile = new File(path);
            StatFs stat = new StatFs(pathFile.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        }catch (Exception e){
            return 0;
        }
    }


}

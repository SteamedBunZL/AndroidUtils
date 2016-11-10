package tlogsdk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Steve on 16/11/3.
 */

public class HiLogCommonUtil {

    /**
     * 获取UID
     * @param context
     * @return
     */
    public static String getUid(Context context){
        String androidId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        String mac = getMacAddress(context);
        if (TextUtils.isEmpty(mac)) mac = "0";
        return androidId + mac;
    }

    /**
     * 获取Mac地址
     * @param context
     * @return
     */
    public static String getMacAddress(Context context)
    {
        String mac = "";
        // 获取wifi管理器
        WifiManager wifiMng = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfor = wifiMng.getConnectionInfo();
        mac = wifiInfor.getMacAddress();
        return mac;
    }


    /**
     * 获取程序版本名
     * @param context
     * @return
     */
    public static String getVersionName(Context context){
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(),0);
            String ver = info.versionName;
            return ver;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return HiLogConstants.NOT_FOUND;
        }
    }

    /**
     * 获取程序版本号
     * @return
     */
    public static int getVer(Context context){
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(),0);
            int ver = info.versionCode;
            return ver;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return HiLogConstants.NOT_FOUND_INT;
        }
    }

    /**
     * 获取当前系统语言
     * @return
     */
    public static String getLang(){
        try {
            Locale l = Locale.getDefault();
            return l.getLanguage();
        } catch (Exception e) {
            e.printStackTrace();
            return HiLogConstants.NOT_FOUND;
        }
    }

    /**
     * 获取渠道号
     * @return
     */
    public static int getSource(){
        return 100;
    }

    /**
     * 获取系统版本号
     * @return
     */
    public static String getOsVer(){
        try {
            return Build.VERSION.RELEASE;
        } catch (Exception e) {
            e.printStackTrace();
            return HiLogConstants.NOT_FOUND;
        }
    }

    /**
     * 获取国家
     * @return
     */
    public static String getArea(){
        try {
            Locale l = Locale.getDefault();
            return l.getCountry();
        } catch (Exception e) {
            e.printStackTrace();
            return HiLogConstants.NOT_FOUND;
        }
    }

    /**
     * 获取手机型号
     * @return
     */
    public static String getModel(){
        try {
            return Build.MODEL;
        } catch (Exception e) {
            e.printStackTrace();
            return HiLogConstants.NOT_FOUND;
        }
    }

    /**
     * 获取手机品牌
     * @return
     */
    public static String getBrand(){
        try {
            return Build.BRAND;
        } catch (Exception e) {
            e.printStackTrace();
            return HiLogConstants.NOT_FOUND;
        }
    }

    /**
     * 获取日志生成时间
     * @return
     */
    public static String getUTCTime(){
        return getUTCStringDate();
    }

    /**
     * 获取APPID
     * @param context
     * @return
     */
    public static String getAppId(Context context){
        try {
            return context.getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
            return HiLogConstants.NOT_FOUND;
        }
    }

    /**
     * 获取SDK的版本号
     * @return
     */
    public static String getSdkVer(){
        return "20161107";
    }

    /**
     * 获取网络模式
     * @param context
     * @return
     */
    public static String getNetwork(Context context){
        try {
            int network = NetworkUtils.getConnectionType(context);
            return String.valueOf(network);
        } catch (Exception e) {
            e.printStackTrace();
            return HiLogConstants.NOT_FOUND;
        }
    }


    /**
     * 获取屏幕宽高
     * @param context
     * @return
     */
    public static String getHeightWidth(Context context){
        try {
            String width = String.valueOf(getScreenSize(context)[0]);
            String height = String.valueOf(getScreenSize(context)[1]);
            return "W:" + width + ",H:" + height;
        } catch (Exception e) {
            e.printStackTrace();
            return HiLogConstants.NOT_FOUND;
        }
    }

    public static int[] getScreenSize(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        // ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int[] array = new int[2];
        array[0] = dm.widthPixels;
        array[1] = dm.heightPixels;
        return array;
    }

    /**
     * 获取UTC时间
     * @return
     */
    public static String getUTCStringDate(){
        DateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone pst = TimeZone.getTimeZone("Etc/GMT+0");

        Date curDate = new Date();
        dateFormatter.setTimeZone(pst);
        String str=dateFormatter.format(curDate);
        return str;
    }




}

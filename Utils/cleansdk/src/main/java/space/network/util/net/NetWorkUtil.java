package space.network.util.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;


/**
 * @author dongdong.huang
 * @Description: 网络状态工具类
 * @date 2016/4/25 15:02
 * @copyright TCL-MIG
 */
public class NetWorkUtil {
//    private static final String TAG = NetWorkUtil.class.getSimpleName();

//    public static boolean IsMobileNetworkAvailable(Context context) {
//        if (IsNetworkAvailable(context)) {
//            if (IsWifiNetworkAvailable(context))
//                return false;
//            return true;
//        }
//        return false;
//    }

//    public static boolean IsWifiNetworkAvailable(Context context) {
//        // Monitor network connections (Wi-Fi, GPRS, UMTS, etc.)
//        // mobile 3G Data Network
//        ConnectivityManager conmgr = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        if(conmgr == null) {
//            return false;
//        }
//
//        NetworkInfo info = null;
//        try {
//            info = conmgr
//                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//            if (info == null) {
//                return false;
//            }
//        } catch (Exception e) {
//            return false;
//        }
//
//        NetworkInfo.State wifi = info.getState(); // 显示wifi连接状态
//        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
//            return true;
//
//        return false;
//    }
//
//    public static boolean IsNetworkAvailable(Context context) {
//
//        ConnectivityManager conmgr = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        if ( conmgr == null ) {
//            return false;
//        }
//
//        // 修改解决判断网络时的崩溃
//        // mobile 3G Data Network
//        try {
//            NetworkInfo net3g = conmgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//            if ( net3g != null){
//                NetworkInfo.State mobile = net3g.getState();// 显示3G网络连接状态
//                if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING)
//                    return true;
//            }
//        } catch (Exception e) {
//        }
//
//        try {
//            NetworkInfo netwifi = conmgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//            if (netwifi != null){
//                NetworkInfo.State wifi = netwifi.getState(); // wifi
//                // 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
//                if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
//                    return true;
//            }
//        } catch (Exception e) {
//        }
//
//        return false;
//    }

//    /**
//     * Return the ssid of wifi is it's connected, otherwise return null.
//     */
//    public static String getWifiSSID(Context context) {
////        if (IsWifiNetworkAvailable(context)) {
////            try {
////                WifiManager wifiManager = (WifiManager) context.getSystemService(Service.WIFI_SERVICE);
////                WifiInfo info = wifiManager.getConnectionInfo();
////                String ssid = info.getSSID();
////                return ssid;
////            } catch (Exception e) {
////            }
////        }
//        return "";
//    }
//
//    /**
//     * 获取cell_id, area_code in GSM, 获取 bs_id, network_id in CDMA
//     * @return
//     */
//    public static HashMap<String, String> getCellIdByType(Context context) {
//        HashMap<String, String> cellTowerMap = null;
//        try {
//            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            if (tm != null) {
//                switch (tm.getPhoneType()) {
//                    case TelephonyManager.PHONE_TYPE_GSM:
//                        GsmCellLocation gcl = (GsmCellLocation) tm.getCellLocation();
//                        /*if (CMLog.LOG_SWITCH_ON) {
//                            CMLog.d(TAG, "getCellIdByType() gcl = " + gcl);
//                        }*/
//                        if (gcl != null) {
//                            cellTowerMap = new HashMap<String, String>();
//                            cellTowerMap.put("cid", String.valueOf(gcl.getCid()));
//                            cellTowerMap.put("lac", String.valueOf(gcl.getLac()));
//                        }
//                        break;
//                    case TelephonyManager.PHONE_TYPE_CDMA:
//                        CdmaCellLocation ccl = (CdmaCellLocation) tm.getCellLocation();
//                        /*if (CMLog.LOG_SWITCH_ON) {
//                            CMLog.d(TAG, "getCellIdByType() ccl = " + ccl);
//                        }*/
//                        if (ccl != null) {
//                            cellTowerMap = new HashMap<String, String>();
//                            cellTowerMap.put("cid", String.valueOf(ccl.getBaseStationId()));
//                            cellTowerMap.put("lac", String.valueOf(ccl.getNetworkId()));
//                        }
//                        break;
//                    default:
//                        break;
//                }
//            }
//        } catch (Exception e) {
//        }
//
//        return cellTowerMap;
//    }
//
//    public static String getNetworkType(Context context, String defaultType) {
//        String networkType = "";
//        try {
//            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            if (cm != null) {
//                NetworkInfo info = cm.getActiveNetworkInfo();
//                if (info != null) {
//                    int type = info.getType();
//                    if (type == ConnectivityManager.TYPE_WIFI) {
//                        networkType = "wifi";
//                    } else {
//                        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//                        if (tm != null) {
//                            switch (tm.getNetworkType()) {
//                                case TelephonyManager.NETWORK_TYPE_1xRTT:
//                                case TelephonyManager.NETWORK_TYPE_EDGE:
//                                case TelephonyManager.NETWORK_TYPE_GPRS:
//                                case TelephonyManager.NETWORK_TYPE_CDMA:
//                                case TelephonyManager.NETWORK_TYPE_IDEN:
//                                    networkType = "2g";
//                                    break;
//                                case TelephonyManager.NETWORK_TYPE_EHRPD:
//                                case TelephonyManager.NETWORK_TYPE_EVDO_0:
//                                case TelephonyManager.NETWORK_TYPE_EVDO_A:
//                                case TelephonyManager.NETWORK_TYPE_EVDO_B:
//                                case TelephonyManager.NETWORK_TYPE_HSPAP:
//                                case TelephonyManager.NETWORK_TYPE_HSPA:
//                                case TelephonyManager.NETWORK_TYPE_UMTS:
//                                    networkType = "3g";
//                                    break;
//                                case TelephonyManager.NETWORK_TYPE_HSDPA:
//                                case TelephonyManager.NETWORK_TYPE_HSUPA:
//                                    networkType = "3.5g";
//                                    break;
//                                case TelephonyManager.NETWORK_TYPE_LTE:
//                                    networkType = "4g";
//                                    break;
//                                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
//                                    break;
//                                default:
//                                    break;
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//        }
//
//        return TextUtils.isEmpty(networkType) ? defaultType : networkType;
//    }
//
//
//    public static boolean isNetWorkingEnable(Context context){
//        try {
//            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
//            if (null != networkInfos) {
//                for (NetworkInfo networkInfo : networkInfos) {
//                    if(networkInfo.isAvailable()) return true;
//                }
//            }
//        } catch (Exception e) {
//        }
//        return false;
//    }
//
//    /**
//     * 是否有网络可用
//     *
//     * @param context
//     * @return
//     */
//    public static boolean isNetworkUp(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
//        for (NetworkInfo ni : netInfo) {
//            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
//                if (ni.isConnected()) {
//                    return true;
//                }
//            } else if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
//                if (ni.isConnected()) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 是否有wifi可用
//     *
//     * @param context
//     * @return
//     */
//    public static boolean isWifiNetworkUp(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
//        for (NetworkInfo ni : netInfo) {
//            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
//                if (ni.isConnected()) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 是否有移动网络可用
//     *
//     * @param context
//     * @return
//     */
//    public static boolean isMobileNetworkUp(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
//        for (NetworkInfo ni : netInfo) {
//            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
//                if (ni.isConnected()) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    public static String getIpAddress(final String strUrl)
//    {
//        if (TextUtils.isEmpty(strUrl))
//        {
//            return null;
//        }
//
//        try
//        {
//            URL url  = new URL(strUrl);
//            InetAddress addr = InetAddress.getByName(url.getHost());
//            return addr.getHostAddress();
//        }
//        catch (Throwable e)
//        {
//            return null;
//        }
//    }

    //判断网络是否存在
    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                try{
                    if (cm.getActiveNetworkInfo() != null) {
                        if (cm.getActiveNetworkInfo().isAvailable()) {
                            return true;
                        }
                    }
                }catch (Exception e) {
                    return false;
                }

            }
            return false;
        }
        return false ;
    }

//    /**
//     * 判断网络是否可用
//     * @return true:可用 false:不可用
//     */
//    public static boolean isNetworkActive(Context context) {
//        if (context == null)
//            return false;
//        boolean bReturn = false;
//        ConnectivityManager conManager = (ConnectivityManager)
//                context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo netInfo = null;
//        try {
//            netInfo = conManager.getActiveNetworkInfo();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        if(netInfo != null && netInfo.isConnected()) {
//            bReturn = true;
//        }
//        return bReturn;
//    }
//
//
//    public static boolean isGooglePlayUrl(String url) {
//        if (TextUtils.isEmpty(url))
//            return false;
//        if (url.startsWith("https://play.google.com") || url.startsWith("http://play.google.com") || url.startsWith("market:")) {
//            return true;
//        }
//        return false;
//    }
//
//    public interface onOpenUrlListener {
//        void onOpen(String url, boolean isGooglePlayUrl);
//    }

//    public static final int NETWORK_TYPE_UNKNOW					= 100;	//网络类型未知
//    public static final int NETWORK_TYPE_3G						= 101;	//
//    public static final int NETWORK_TYPE_WIFI					= 102;	//
//    public static final int NETWORK_TYPE_NO						= 103;  //

//    public static short getNetWorkType(Context context) {
//        short nType = NETWORK_TYPE_UNKNOW;
//        if(NetWorkUtil.IsNetworkAvailable(context)){
//            if(NetWorkUtil.IsWifiNetworkAvailable(context)){
//                nType = NETWORK_TYPE_WIFI;
//            }else if(NetWorkUtil.IsMobileNetworkAvailable(context)){
//                nType = NETWORK_TYPE_3G;
//            }else{
//                nType = NETWORK_TYPE_UNKNOW;
//            }
//        }else {
//            nType = NETWORK_TYPE_NO;
//        }
//
//        return nType;
//    }

//    public static final int TYPE_NO_NETWROK = -1;
//    public static final int TYPE_WIFI = ConnectivityManager.TYPE_WIFI;
//    public static final int TYPE_MOBILE = ConnectivityManager.TYPE_MOBILE;
//    public static final int TYPE_2G = 2;
//    public static final int TYPE_3G = 3;
//    public static final int TYPE_4G = 4;

//    public static int[] getNetWorkInfoStatus(Context context) {
//        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = connectMgr.getActiveNetworkInfo();
//
//        int[] type = {TYPE_NO_NETWROK, 0};
//        if (info != null) {
//            if (info.getState() != NetworkInfo.State.CONNECTED) return type;
//            switch (info.getType()) {
//                case ConnectivityManager.TYPE_WIFI:
//                    type[0] = ConnectivityManager.TYPE_WIFI;
//                    break;
//                case ConnectivityManager.TYPE_MOBILE:
//                    type[0] = ConnectivityManager.TYPE_MOBILE;
//                    switch (info.getSubtype()) {
//                        case TelephonyManager.NETWORK_TYPE_GPRS:
//                        case TelephonyManager.NETWORK_TYPE_EDGE:
//                        case TelephonyManager.NETWORK_TYPE_CDMA:
//                        case TelephonyManager.NETWORK_TYPE_1xRTT:
//                        case TelephonyManager.NETWORK_TYPE_IDEN:
//                            type[1] = TYPE_2G;
//                            break;
//                        case TelephonyManager.NETWORK_TYPE_UMTS:
//                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
//                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
//                        case TelephonyManager.NETWORK_TYPE_HSDPA:
//                        case TelephonyManager.NETWORK_TYPE_HSUPA:
//                        case TelephonyManager.NETWORK_TYPE_HSPA:
//                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
//                        case TelephonyManager.NETWORK_TYPE_EHRPD:
//                        case TelephonyManager.NETWORK_TYPE_HSPAP:
//                            type[1] = TYPE_3G;
//                            break;
//                        case TelephonyManager.NETWORK_TYPE_LTE:
//                            type[1] = TYPE_4G;
//                            break;
//                        default:
//                            type[1] = 0;
//                    }
//                    break;
//                default:
//                    type[0] = TYPE_NO_NETWROK;
//            }
//        }
//
//        return type;
//    }


//    public static final int 	UUID_LENGTH 	= 32;
//    public static final int 	IMSI_LENGTH 	= 20;
//    /**
//     * 取IMEI
//     *
//     * @param context
//     * @return
//     */
//    public static String getIMEI(Context context) {
//        if (context == null)
//            return null;
//        final TelephonyManager tm = (TelephonyManager)context
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        return tm.getDeviceId();
//    }

//    /**
//     * 根据IMEI获取UUID
//     *
//     * @param context
//     * @return UUID
//     */
//    public static String getUUID(Context context) {
//        if (context == null){
//            //InfocLog.getLogInstance().log(" imei, the context is null ");
//            return null;
//        }
//
//        String phoneIMEI = null;
//        try {
//            phoneIMEI = getIMEI(context);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //InfocLog.getLogInstance().log("the imei=" + phoneIMEI);
//        int imeiLength = 0;
//        if (phoneIMEI != null)
//            imeiLength = phoneIMEI.length();
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < UUID_LENGTH - imeiLength; i++) {
//            builder.append('0');
//        }
//        if (phoneIMEI != null)
//            builder.append(phoneIMEI);
//        return builder.toString();
//    }
//
//    public static String getMCC(Context context) {
//        if (context == null)
//            return null;
//        final TelephonyManager tm = (TelephonyManager)context
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        String mcc_mnc = tm.getSimOperator();
//        StringBuilder mcc = null;
//        if (null != mcc_mnc && mcc_mnc.length() >= 3) {
//            mcc = new StringBuilder();
//            mcc.append(mcc_mnc, 0, 3);
//            return mcc.toString();
//        }
//        return null;
//    }
//
//    public static String getMNC(Context context) {
//        if (context == null)
//            return null;
//        final TelephonyManager tm = (TelephonyManager)context
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        String mcc_mnc = tm.getSimOperator();
//        StringBuilder mnc = null;
//        if (null != mcc_mnc && mcc_mnc.length() >= 5) {
//            mnc = new StringBuilder();
//            mnc.append(mcc_mnc, 3, 5);
//            return mnc.toString();
//        }
//        return null;
//    }
}

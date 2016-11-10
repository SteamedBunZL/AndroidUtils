package com.tcl.security.virusengine.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtils {
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_2G = 2;
    public static final int TYPE_3G = 3;
    public static final int TYPE_4G = 4;
    public static final int TYPE_DISCONNECT = 99;

    /** Network type is unknown */
    private static final int NETWORK_TYPE_UNKNOWN = 0;
    /** Current network is GPRS */
    private static final int NETWORK_TYPE_GPRS = 1;
    /** Current network is EDGE */
    private static final int NETWORK_TYPE_EDGE = 2;
    /** Current network is UMTS */
    private static final int NETWORK_TYPE_UMTS = 3;
    /** Current network is CDMA: Either IS95A or IS95B*/
    private static final int NETWORK_TYPE_CDMA = 4;
    /** Current network is EVDO revision 0*/
    private static final int NETWORK_TYPE_EVDO_0 = 5;
    /** Current network is EVDO revision A*/
    private static final int NETWORK_TYPE_EVDO_A = 6;
    /** Current network is 1xRTT*/
    private static final int NETWORK_TYPE_1xRTT = 7;
    /** Current network is HSDPA */
    private static final int NETWORK_TYPE_HSDPA = 8;
    /** Current network is HSUPA */
    private static final int NETWORK_TYPE_HSUPA = 9;
    /** Current network is HSPA */
    private static final int NETWORK_TYPE_HSPA = 10;
    /** Current network is iDen */
    private static final int NETWORK_TYPE_IDEN = 11;
    /** Current network is EVDO revision B*/
    private static final int NETWORK_TYPE_EVDO_B = 12;
    /** Current network is LTE */
    private static final int NETWORK_TYPE_LTE = 13;
    /** Current network is eHRPD */
    private static final int NETWORK_TYPE_EHRPD = 14;
    /** Current network is HSPA+ */
    private static final int NETWORK_TYPE_HSPAP = 15;
    /** Current network is GSM  */
    private static final int NETWORK_TYPE_GSM = 16;

    public static boolean isConnected(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo[] infos = cm.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if (ni.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context c) {
        ConnectivityManager connecManager = (ConnectivityManager)c.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        try {
            networkInfo = connecManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        } catch(Exception ex) {
            //java.lang.NullPointerException
            //   at android.os.Parcel.readException(Parcel.java:1333)
            //   at android.os.Parcel.readException(Parcel.java:1281)
            //   at android.net.IConnectivityManager$Stub$Proxy.getNetworkInfo(IConnectivityManager.java:830)
            //   at android.net.ConnectivityManager.getNetworkInfo(ConnectivityManager.java:387)
        }
        if (networkInfo != null) {
            return networkInfo.isConnected();
        } else {
            return false;
        }
    }

    public static int getConnectionType(Context c) {
        if (!isConnected(c))
            return TYPE_DISCONNECT;

        if (isWifiConnected(c))
            return TYPE_WIFI;

        TelephonyManager telephonyManager = (TelephonyManager)c.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case NETWORK_TYPE_GPRS:
                case NETWORK_TYPE_GSM:
                case NETWORK_TYPE_EDGE:
                case NETWORK_TYPE_CDMA:
                case NETWORK_TYPE_1xRTT:
                case NETWORK_TYPE_IDEN:
                    return TYPE_2G;
                case NETWORK_TYPE_UMTS:
                case NETWORK_TYPE_EVDO_0:
                case NETWORK_TYPE_EVDO_A:
                case NETWORK_TYPE_HSDPA:
                case NETWORK_TYPE_HSUPA:
                case NETWORK_TYPE_HSPA:
                case NETWORK_TYPE_EVDO_B:
                case NETWORK_TYPE_EHRPD:
                case NETWORK_TYPE_HSPAP:
                    return TYPE_3G;
                case NETWORK_TYPE_LTE:
                    return TYPE_4G;
                default:
                    return TYPE_UNKNOWN;
            }
        }
        return TYPE_UNKNOWN;
    }


}

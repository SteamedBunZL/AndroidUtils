package com.tcl.security.virusengine.network;

/**
 * Created by Steve on 2016/6/15.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tcl.security.virusengine.CacheScanDispatcher;
import com.tcl.security.virusengine.utils.VirusLog;


public class NetStateChangedReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo==null) {
            CacheScanDispatcher.mCurrentNetState = NetworkChange.NETWORK_UNAVAILABLE;
            VirusLog.i("netchange : unavailable");
            return;
        }
        int nettype = networkInfo.getType();
        if (nettype == ConnectivityManager.TYPE_MOBILE) {
            CacheScanDispatcher.mCurrentNetState = NetworkChange.NETWORK_MOBILE;
            VirusLog.i("netchange : mobile");
        }else if (nettype == ConnectivityManager.TYPE_WIFI) {
            CacheScanDispatcher.mCurrentNetState = NetworkChange.NETWORK_WIFI;
            VirusLog.i("netchange : wifi");
        }
    }

}

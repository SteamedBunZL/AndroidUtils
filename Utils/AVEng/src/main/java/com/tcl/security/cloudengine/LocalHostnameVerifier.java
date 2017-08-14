package com.tcl.security.cloudengine;


import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class LocalHostnameVerifier implements HostnameVerifier {
    private static final String TAG = ProjectEnv.bDebug ? "LocalHostnameVerifier" : LocalHostnameVerifier.class.getSimpleName();

    @Override
    public boolean verify(String arg0, SSLSession arg1) {
        if (ProjectEnv.hosts.size() > 0) {
            for (String host : ProjectEnv.hosts) {
                if (host.indexOf(arg0) != -1) {
                    return true;
                }
            }
            if (ProjectEnv.bDebug) {
                Log.w(TAG, "distrust:" + arg0);
            }
            return false;
        }
        return true;
    }
}

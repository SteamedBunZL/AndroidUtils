package com.tcl.security.cloudengine;


import android.content.Context;

public class App {
    static Context c;
    static String accessKey;
    static String pkgName;

    static void init(Context context, String key) {
        c = context;
        accessKey = key;
        pkgName = context.getPackageName();
    }
}

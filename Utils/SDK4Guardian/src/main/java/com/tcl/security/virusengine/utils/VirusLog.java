package com.tcl.security.virusengine.utils;

import android.util.Log;

import java.util.Locale;

/**
 * Log-master
 *
 * Created by Steve on 2016/4/29.
 */
public class VirusLog {

    public static String TAG = "VirusLog";

    public static boolean DEBUG = false;

    public static void setTag(String tag) {
        d("Changing log tag to %s", tag);
        TAG = tag;

        // Reinitialize the DEBUG "constant"
//        DEBUG = Log.isLoggable(TAG, Log.VERBOSE);
    }

    public static void v(String format, Object... args) {
        if (DEBUG) {
            Log.v(TAG, buildMessage(format, args));
        }
    }

    public static void i(String format,Object... args){
        if (DEBUG)
            Log.i(TAG,buildMessage(format,args));
    }

    public static void d(String format, Object... args) {
        if (DEBUG)
            Log.d(TAG, buildMessage(format, args));
    }

    public static void e(String format, Object... args) {
        if (DEBUG)
            Log.e(TAG, buildMessage(format, args));
    }


    public static void e(Throwable tr, String format, Object... args) {
        if (DEBUG)
            Log.e(TAG, buildMessage(format, args), tr);
    }

    /**
     * 专为异常而打印
     * @param e
     */
    public static void printException(Exception e,String attach){
        Log.e(TAG,"---------------------Caught Error+++"+attach+"+++--------------------------");
        e.printStackTrace();
    }

    public static void w(String format, Object... args) {
        if (DEBUG)
            Log.w(TAG, buildMessage(format, args));
    }

    public static void w(Throwable tr, String format, Object... args) {
        if (DEBUG)
            Log.w(TAG, buildMessage(format, args), tr);
    }

    private static String buildMessage(String format, Object... args) {
        String msg = (args == null) ? format : String.format(Locale.US, format, args);
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        String caller = "<unknown>";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(VirusLog.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);

                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%s] %s: %s", Thread.currentThread().getName() + "-" + Thread.currentThread().getId(), caller, msg);
    }


}

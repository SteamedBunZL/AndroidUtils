package com.steve.commonlib;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.Locale;

/**
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　>      <　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　⌒　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃  护码神兽
 * 　　　　┃　　　┃
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * <p>
 * Created by Steve on 17/8/10.
 * <p>
 * ━━━━━━感觉萌萌哒━━━━━━
 */

public class Printer {

    public static String TAG = "DebugLog";
    public static boolean DEBUG = true;

    /**
     * Provides one-time used tag for the log message
     */
    ThreadLocal<String> localTag = new ThreadLocal<>();

    public Printer t(String tag){
        if (tag!=null)
            localTag.set(tag);
        return this;
    }

    private String getTag(){
        String tag = localTag.get();
        if (tag!=null){
            localTag.remove();
            return tag;
        }
        return TAG;
    }

    public void v(String format, Object... args) {
        if (DEBUG) {
            Log.v(getTag(), buildMessage(format, args));
        }
    }

    public void i(String format,Object... args){
        if (DEBUG)
            Log.i(getTag(),buildMessage(format,args));
    }

    public void d(String format, Object... args) {
        if (DEBUG)
            Log.d(getTag(), buildMessage(format, args));
    }

    public void e(String format, Object... args) {
        if (DEBUG)
            Log.e(getTag(), buildMessage(format, args));
    }

    public void e(Throwable tr, String format, Object... args) {
        if (DEBUG)
            Log.e(getTag(), buildMessage(format, args), tr);
    }

    public void w(String format, Object... args) {
        if (DEBUG)
            Log.w(getTag(), buildMessage(format, args));
    }

    public void w(Throwable tr, String format, Object... args) {
        if (DEBUG)
            Log.w(getTag(), buildMessage(format, args), tr);
    }

    /**
     * 专为异常而打印
     * @param e
     */
    public void printException(Throwable e){
        DebugLog.e("---------------------Caught Error--------------------------");
        e("%s",getStackTraceString(e));
    }




    private String buildMessage(String format, Object... args) {
        String msg = (args == null) ? format : String.format(Locale.US, format, args);
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        String caller = "<unknown>";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals( DebugLog.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);

                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%s] %s: %s", Thread.currentThread().getName() + "-" + Thread.currentThread().getId(), caller, msg);
    }

    /**
     * 把throwable信息转化为string
     * @param tr
     * @return
     */
    public String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }


}

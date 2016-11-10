package tlog.utils;


import java.io.Closeable;
import java.io.IOException;

import tlog.nlog.NLog;

/**
 * description:
 * author hui.zhu
 * date 2016/10/24
 * copyright TCL-MIG
 */


public class Streams {

    public static <T extends Closeable> void safeClose(T is) {
        if (is == null)
            return;

        try {
            is.close();
        }catch (IOException e) {
            NLog.printStackTrace(e);
        }
    }

    public static void closeSilently(Object o) {
        if (o == null)
            return;

        if (o instanceof Closeable) {
            safeClose((Closeable)o);
        }

        else {
            try {
                InvokeUtil.invokeMethod(o, "close");
            } catch (Exception e) {
                NLog.printStackTrace(e);
            }
        }
    }

}

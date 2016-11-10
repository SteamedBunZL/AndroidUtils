package tlog.nlog;

import android.text.TextUtils;

import java.io.File;

/**
 * description:
 * author hui.zhu
 * date 2016/10/24
 * copyright TCL-MIG
 */


public final class NLog {
    private static final String LOG_FILENAME = "tcl_logcat.log";
    private static boolean debug = false;
    private static Logger logger = null;
    private static final String LOGGING_PROPERTIES = "logging.properties";

    public NLog() {
    }

    public static Logger getLogger() {
        return logger;
    }

    public static synchronized boolean init(String filePath) {
        return false;
    }

    public static synchronized void setDebug(boolean d, int level) {
        boolean old = debug;
        if(old != d) {
            if(old) {
                trace(1, (String)null);
            }

            debug = d;
            if(d) {
                if(logger == null) {
                    logger = Logger.getLogger((String)null);
                }

                logger.setLevel(level);
            }

        }
    }

    public static boolean isDebug() {
        return debug;
    }

    public static synchronized boolean trace(int level, String path) {
        if(!debug) {
            throw new IllegalStateException("you should enable log before modifing trace mode");
        } else {
            if(logger == null) {
                logger = Logger.getLogger((String)null);
            }

            if(level == 3 || level == 2) {
                if(TextUtils.isEmpty(path)) {
                    throw new IllegalArgumentException("path should not be null for offline and all mode");
                }

                File dir = new File(path);
                if(!dir.exists() || !dir.isDirectory()) {
                    boolean sb = dir.mkdirs();
                    if(!sb) {
                        return false;
                    }
                }

                StringBuffer sb1 = new StringBuffer(path);
                sb1.append(File.separator);
                sb1.append("tcl_logcat.log");
                path = sb1.toString();
            }

            return logger.trace(level, path);
        }
    }

    private static String buildWholeMessage(String format, Object... args) {
        if(args != null && args.length != 0) {
            String msg = String.format(format, args);
            return msg;
        } else {
            return format;
        }
    }

    public static void d(String tag, String format, Object... args) {
        if(debug) {
            logger.d(tag, buildWholeMessage(format, args));
        }

    }

    public static void i(String tag, String format, Object... args) {
        if(debug) {
            logger.i(tag, buildWholeMessage(format, args));
        }

    }

    public static void e(String tag, String format, Object... args) {
        if(debug) {
            logger.e(tag, buildWholeMessage(format, args));
        }

    }

    public static void e(String tag, Throwable e) {
        if(debug) {
            logger.e(tag, e);
        }

    }

    public static void v(String tag, String format, Object... args) {
        if(debug) {
            logger.v(tag, buildWholeMessage(format, args));
        }

    }

    public static void w(String tag, String format, Object... args) {
        if(debug) {
            logger.w(tag, buildWholeMessage(format, args));
        }

    }

    public static void printStackTrace(Throwable e) {
        if(debug) {
            logger.e("TCLException", e);
        }

    }
}

package com.clean.spaceplus.cleansdk.base.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TUncaughtExceptionHandler implements UncaughtExceptionHandler {

    private static final String TAG = "ExceptionHandler";

    private static TUncaughtExceptionHandler instance = null;
    private UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    // 用来存储设备信息和异常信息
    private String mDeviceInfos;
    private String mCrashDirPath;
    private boolean mRegistered;

    public static synchronized void catchUncaughtException(Context context, String path) {
        if (instance == null) {
            instance = new TUncaughtExceptionHandler(context, path);
        }

        instance.registerForExceptionHandler();
    }

    private TUncaughtExceptionHandler(Context context, String path) {
        this.mContext = context;
        this.mCrashDirPath = path;
        collectDeviceInfo();
    }

    public void registerForExceptionHandler() {
        if (mRegistered) {
            return;
        }
        mRegistered = true;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    protected boolean handleException(Thread thread, Throwable ex) {
        if (ex == null) {
            return false;
        }
        ex.printStackTrace();
//        collectMemInfo();
        // 保存日志文件
        saveCrashInfo2File(thread, ex);
        /*flurry错误日志上传，google analytic则自带错误信息分析*/
//		FlurryAgent.onError(FlurryStatConst.CATEGORY.HANDLE_EXCEPTION_UPLOAD, "handle exception:",ex);
        return true;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(thread, ex);
        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * 收集内存使用情况
     */
    private String collectMemInfo() {
        StringWriter meminfo = new StringWriter();
        PrintWriter writer = new PrintWriter(meminfo);
        writer.append("*********************************************************************************\n");
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        writer.append("memoryInfo.availMem=").append(String.valueOf(memoryInfo.availMem)).append("\n");
        writer.append("memoryInfo.lowMemory=").append(String.valueOf(memoryInfo.lowMemory)).append("\n");
        writer.append("memoryInfo.threshold=").append(String.valueOf(memoryInfo.threshold)).append("\n");


        getSelfMem(activityManager, writer);
        writer.close();
        return meminfo.toString();
    }

    /**
     * 获取进程内存信息
     *
     * @param am
     *
     * @param writer
     */
    public boolean getSelfMem(ActivityManager am, PrintWriter writer) {

        List<RunningAppProcessInfo> procInfo = am.getRunningAppProcesses();
        for (RunningAppProcessInfo runningAppProcessInfo : procInfo) {
            if (runningAppProcessInfo.processName.indexOf(mContext.getPackageName()) != -1) {
                int pids[] = {runningAppProcessInfo.pid};
                Debug.MemoryInfo self_mi[] = am.getProcessMemoryInfo(pids);

                writer.append("dalvikPrivateDirty=").append(String.valueOf(self_mi[0].dalvikPrivateDirty)).append("\n");
                writer.append("dalvikPss=").append(String.valueOf(self_mi[0].dalvikPss)).append("\n");
                writer.append("nativePrivateDirty=").append(String.valueOf(self_mi[0].nativePrivateDirty)).append("\n");
                writer.append("nativePss=").append(String.valueOf(self_mi[0].nativePss)).append("\n");
                writer.append("nativeSharedDirty=").append(String.valueOf(self_mi[0].nativeSharedDirty)).append("\n");
                writer.append("otherPrivateDirty=").append(String.valueOf(self_mi[0].otherPrivateDirty)).append("\n");
                writer.append("otherPss=").append(String.valueOf(self_mi[0].otherPss)).append("\n");
                writer.append("otherSharedDirty=").append(String.valueOf(self_mi[0].otherSharedDirty)).append("\n");
                writer.append("TotalPrivateDirty=").append(String.valueOf(self_mi[0].getTotalPrivateDirty())).append("\n");
                writer.append("TotalPss=").append(String.valueOf(self_mi[0].getTotalPss())).append("\n");
                writer.append("TotalSharedDirty=").append(String.valueOf(self_mi[0].getTotalSharedDirty())).append("\n");

                return true;
            }
        }

        return false;
    }

    /**
     * 收集设备参数信息
     */
    private void collectDeviceInfo() {
        StringWriter deviceInfos = new StringWriter();
        try {
            deviceInfos.append("=================================================================================\n");
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;

                deviceInfos.append("versionName=").append(versionName).append("\n");
                deviceInfos.append("versionCode=").append(pi.versionCode + "").append("\n");
            }
        } catch (Exception e) {
        }

        deviceInfos.append("androidVersion=").append(Build.VERSION.RELEASE).append("\n");

        deviceInfos.append("=================================================================================\n");
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                deviceInfos.append(field.getName()).append("=").append(field.get(null).toString()).append("\n"); // TODO
            } catch (Exception e) {
                Log.e(TAG, " collectDeviceInfo Exception" + e);
            }
        }

        mDeviceInfos = deviceInfos.toString();
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     *
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private boolean saveCrashInfo2File(Thread thread, Throwable ex) {

        // SD卡未挂载，不保存
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return false;
        }

        String dirForCrashFile = mCrashDirPath;
        if (TextUtils.isEmpty(mCrashDirPath)) {
            try {
                dirForCrashFile = mContext.getExternalFilesDir(null) + File.separator + "crash";
            } catch (Exception ignore) {
            }
        }
        if (TextUtils.isEmpty(dirForCrashFile)) {
            return false;
        }

        // 检查目录是否存在，不存在则创建
        File dir = new File(dirForCrashFile);
        if (!dir.exists() && !dir.mkdirs()) {
            return false;
        }

        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        printWriter.append(mDeviceInfos);

        String meminfo = collectMemInfo();
        printWriter.append(meminfo);

        printWriter.append("*********************************************************************************\n");
        if (thread != null) {
            String name = String.format("%s(%d)", thread.getName(), thread.getId());
            printWriter.write("the crashed thread: ");
            printWriter.write(name);
            printWriter.write("\n");
        }

        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        printWriter.close();

        // 用于格式化日期,作为日志文件名的一部分
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String time = formatter.format(new Date());
        String fileName = "crash-" + time + ".txt";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dirForCrashFile + File.separator + fileName);
            fos.write(writer.toString().getBytes("utf-8"));
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }

    }
}

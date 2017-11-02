package com.clean.spaceplus.cleansdk.base.exception;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.os.Build;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.WindowManager;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.util.Env;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

import space.network.util.RuntimeCheck;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/3 11:03
 * @copyright TCL-MIG
 */
public class ProbeCrash {
    private static final String VERSION_INI = "version.ini";

    public static void Probe(Context context){

        int nSysVerCode = Env.getVersionCode(context);

        // 覆盖安装的过程中版本号不一致，拒绝执行，以免发生意外
//        if ( Env.VERSION_CODE != nSysVerCode ){
//            Log.e("CM", "Env.VERSION_CODE != nSysVerCode, exit....");
//            System.exit(-1);
//            return;
//        }

        // 如果资源为空，并且确定是在覆盖安装的过程中，那么自己退出自己
        // 其余情况继续崩溃
        if ( context.getResources() == null ){
            try {
                String srcDir = context.getApplicationInfo().sourceDir;
                File curFile = new File(srcDir);

                if ( !curFile.exists() ){
                    String nextSrc = getNextSourcePath(srcDir);
                    if ( new File(nextSrc).exists() ){
                        System.exit(-1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ProbeAsset(context);
    }


    private static String getNextSourcePath(String src){
        if ( src.contains("-1.") ){
            return src.replace("-1.", "-2.");
        }
        else{
            return src.replace("-2.", "-1.");
        }
    }

    public static void ProbeAsset(Context context){
        AssetManager assertManager = null;
        try {
            InputStream verIStream = null;
            assertManager = context.getAssets();
            try {
                verIStream = assertManager.open(VERSION_INI);
            } finally {
                if (verIStream != null) {
                    verIStream.close();
                }
            }
        }
        catch (Exception e) {
            boolean bOpenFdSuccess = false;
            try{
                AssetFileDescriptor afd = assertManager.openFd(VERSION_INI);

                if ( afd != null ){
                    afd.close();
                    afd = null;
                    bOpenFdSuccess = true;
                }
            }
            catch (Exception e1) {
            }
            if (PublishVersionManager.isTest()) {
                throw new RuntimeException("AssertManager Open VERSION_INI Failed! try openFd result: " + bOpenFdSuccess, e);
            }
        }
    }


    private static boolean bUpLoad = false;
    public static void ProbeCheckSqliteCrash(int nSubType){
        if ( bUpLoad )
            return;

        bUpLoad = true;

        boolean bDBExist 		= false;
        long	nDBSize			= 0;
        long	diskTotalSpace	= 0;
        long	diskFreeSpace	= 0;
        String 	canonPath		= "";
        Context ctx = SpaceApplication.getInstance().getContext();
        File dbPath = ctx.getDatabasePath(Env.DB_NAME_COMMON);

        if ( dbPath.exists() ){
            bDBExist = true;
            nDBSize = dbPath.length();

            try {
                canonPath = dbPath.getCanonicalPath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        StatFs fs = new StatFs("/data/");
        diskFreeSpace = fs.getAvailableBlocks();
        diskTotalSpace= fs.getBlockCount();

        String reportInfo = "";
        reportInfo += "File: " + dbPath.getAbsolutePath() + "\n";
        reportInfo += "Exist: " + bDBExist + "\n";
        reportInfo += "nDBSize: " + nDBSize + "\n";
        reportInfo += "diskTotalSpace: " + diskTotalSpace + "\n";
        reportInfo += "diskFreeSpace: " + diskFreeSpace + "\n";
        reportInfo += "canonPath: " + canonPath + "\n";

        //report carsh...
    }

    public static boolean ShouldPrintLogcat(Throwable ex){
        if ( ex == null )
            return false;

        if ( ex instanceof OutOfMemoryError )
            return true;

        if ( ex instanceof SQLiteDatabaseCorruptException)
            return true;

        if ( ex instanceof WindowManager.BadTokenException)
            return true;

        if ( ex instanceof VerifyError)
            return true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//            if (ex instanceof CursorWindowAllocationException){
//                return true;
//            }
            return true;
        }

        String msg = ex.getMessage();

        if ( ex instanceof SecurityException ){
            if ( msg != null ){
                if ( msg.contains("Not allowed to start service Intent") &&
                        msg.contains("Service process is bad") ){
                    return true;
                }
                if (msg.contains("when starting service") && msg.contains("com.cleanmaster.service.FloatService")) {
                    return true;
                }
            }
        }

        if (ex instanceof IllegalArgumentException) {
            if (msg != null) {
                if (msg.contains("Unknown URL")) {
                    return true;
                }
            }
        }

        if (msg != null) {
            if (ex.getMessage().contains("Couldn't expand RemoteViews for")) {
                return true;
            }

            if (msg.contains("Bad notification posted from")) {
                return true;
            }

            if (msg.contains("CRITICAL ERROR")) {
                return true;
            }

            if (msg.contains("infoc data format error")) {
                return true;
            }

        }

        return false;
    }

    public static boolean IsGcFinalizyCrash(Throwable throwable){

        if ( throwable instanceof TimeoutException && Build.VERSION.SDK_INT >= 17 ){
            if ( !TextUtils.isEmpty( throwable.getMessage() ) ){

                if ( throwable.getMessage().contains("timed out after 10 seconds") ){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean IsClassNotFoundCrash(Throwable throwable){
        if ( throwable instanceof NoClassDefFoundError )
            return true;

        if ( throwable instanceof ClassNotFoundException )
            return true;

        Throwable cause = throwable.getCause();
        if(cause != null)    {
            if ( cause instanceof NoClassDefFoundError )
                return true;

            if ( cause instanceof ClassNotFoundException )
                return true;
        }
        return false;
    }

    private static long s_last_up_time = 0;
    public static final void SetLastUpdateDbTime(){
        s_last_up_time = System.currentTimeMillis();
    }

    public static final long GetLastUpdateDbTimeInService(){
        if ( s_last_up_time == 0 )
            return s_last_up_time;

        return s_last_up_time - SpaceApplication.getInstance().getAppStartTime();
    }

    public static final long GetLastUpdateDbTime(){
        if ( RuntimeCheck.IsServiceProcess() )
            return GetLastUpdateDbTimeInService();

//        try{
//            return SyncIpcCtrl.getIns().getIPCClient().GetLastUpdateDbTime();
//        }
//        catch(Exception e){
//            return 0;
//        }
        return 0;
    }
}

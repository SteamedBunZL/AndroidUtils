package space.network.util;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/23 17:54
 * @copyright TCL-MIG
 */
public class RuntimeCheck {
    public static String CLEANMASTER_CRASH_FEEDBACK_PROCESSNAME = ":crash.feedback";
    public static String CLEANMASTER_SERVICE_PROCESSNAME = ":service";
    public static String CLEANMASTER_FLOAT_PROCESSNAME = ":float";
    public static String CLEANMASTER_BG_SCAN_PROCESSNAME = ":bg.scan";
    public final static String LOGIN_PROCESS_SUBNAME = ":ssologin";
    public final static String PHOTOTRIM_PROCESS_SUBNAME = ":phototrim";
    public static String CLEANMASTER_GAMEBOX_WEB_PROCESSNAME = ":gamebox.web";
    public static String CLEANMASTER_STORAGE_PROCESSNAME = ":storage";
    public static String CLEANMASTER_PERMS_PROCESSNAME = ":perms";
    public static String CLEANMASTER_SDSCANSERVICE_PROCESSNAME = ":sdscanservice";
    public static String CLEANMASTER_APPLOCK_HOST_PROCESSNAME = ":AppLockHost";

    private static Thread  s_mainThread			    = null;
    private static boolean s_bIsSerivceProcess      = false;
    private static boolean s_bIsFloatProcess        = false;
    private static boolean s_bIsUiProcess		    = false;
    private static boolean s_bIsCrashProcess	    = false;
    private static boolean s_bIsBgScanProcess	    = false;
    private static boolean s_bIsSSOLoginProcess	    = false;
    private static boolean s_bIsPhotoTrimProcess    = false;
    private static boolean s_bIsGameBoxWebProcess	= false;
    private static boolean s_bIsStroageProcess	    = false;
    private static boolean s_bIsPermsProcess	    = false;
    private static boolean s_bIsSdScanServiceProcess= false;
    private static boolean s_bIsAppLockHostProcesss = false;

    public static void Init(String procName){
        s_mainThread = Thread.currentThread();
        if ( procName.contains(CLEANMASTER_SERVICE_PROCESSNAME) ){
            s_bIsSerivceProcess = true;
        }
        else if ( procName.contains(CLEANMASTER_FLOAT_PROCESSNAME) ){
            s_bIsFloatProcess = true;
        }
        else if ( procName.contains(CLEANMASTER_CRASH_FEEDBACK_PROCESSNAME) ){
            s_bIsCrashProcess = true;
        }
        else if ( procName.contains(CLEANMASTER_BG_SCAN_PROCESSNAME) ){
            s_bIsBgScanProcess = true;
        }
        else if ( procName.contains(LOGIN_PROCESS_SUBNAME)) {
            s_bIsSSOLoginProcess = true;
        }
        else if (procName.contains(PHOTOTRIM_PROCESS_SUBNAME)) {
            s_bIsPhotoTrimProcess = true;
        }
        else if ( procName.contains(CLEANMASTER_GAMEBOX_WEB_PROCESSNAME) ){
            s_bIsGameBoxWebProcess = true;
        }
        else if ( procName.contains(CLEANMASTER_STORAGE_PROCESSNAME) ){
            s_bIsStroageProcess = true;
        }
        else if ( procName.contains(CLEANMASTER_PERMS_PROCESSNAME)) {
            s_bIsPermsProcess = true;
        }
        else if (procName.contains(CLEANMASTER_SDSCANSERVICE_PROCESSNAME)) {
            s_bIsSdScanServiceProcess = true;
        }
        else if (procName.contains(CLEANMASTER_APPLOCK_HOST_PROCESSNAME)) {
            s_bIsAppLockHostProcesss = true;
        }
        else {
            s_bIsUiProcess = true;
        }

    }

    public static void SetServiceProcess(){
        s_bIsUiProcess = false;
        s_bIsSerivceProcess = true;
        s_bIsCrashProcess = false;
        s_bIsBgScanProcess = false;
        s_bIsSSOLoginProcess = false;
        s_bIsPhotoTrimProcess = false;
        s_bIsGameBoxWebProcess = false;
        s_bIsStroageProcess = false;
        s_bIsPermsProcess = false;
        s_bIsSdScanServiceProcess = false;
        s_bIsAppLockHostProcesss = false;
    }

    public static void CheckUiProcess(){
        if ( !s_bIsUiProcess ){
            throw new RuntimeException("Must run in UI Process");
        }
    }

    public static void CheckServiceProcess(){
        if ( !s_bIsSerivceProcess ){
            throw new RuntimeException("Must run in Service Process");
        }
    }

    public static void CheckMainUIThread(){
		/*if ( Thread.currentThread() != s_mainThread ){
			throw new RuntimeException("Must run in UI Thread");
		}*/
    }

    public static void CheckNoUIThread(){
        if ( Thread.currentThread() == s_mainThread ){
            throw new RuntimeException("Must not run in UI Thread");
        }
    }


    public static boolean IsCrashProcess(){
        return s_bIsCrashProcess;
    }

    public static boolean IsUIProcess(){
        return s_bIsUiProcess;
    }

    public static boolean IsServiceProcess(){
        return s_bIsSerivceProcess;
    }

    public static boolean IsFloatProcess() {
        return s_bIsFloatProcess;
    }

    public static boolean IsBgScanProcess() {
        return s_bIsBgScanProcess;
    }

    public static boolean IsSSOLoginProcess() {
        return s_bIsSSOLoginProcess;
    }

    public static boolean IsPhotoTrimProcess() {
        return s_bIsPhotoTrimProcess;
    }

    public static boolean IsGameBoxWebProcess() {
        return s_bIsGameBoxWebProcess;
    }

    public static boolean IsStroageProcess() {
        return s_bIsStroageProcess;
    }

    public static boolean IsPermsProcess() {
        return s_bIsPermsProcess;
    }

    public static boolean IsSdScanServiceProcess() {
        return s_bIsSdScanServiceProcess;
    }

    public static boolean isAppLockServiceProcess() {
        return s_bIsAppLockHostProcesss;
    }
}

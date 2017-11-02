package com.clean.spaceplus.cleansdk.util.system;

import android.os.SystemProperties;
import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * @author Jerry
 * @Description:
 * @date 2016/7/19 17:35
 * @copyright TCL-MIG
 */

public class PhoneModelUtils {
//    private static final int FLAG_SHOW_FLOATING_WINDOW = 1 << 27;
//    private static final int FLAG_SHOW_FLOATING_WINDOW_GT_V19 = 1 << 25;
    private static final int OP_SYSTEM_ALERT_WINDOW = 24;
    private static final String AddWindowManagerVer = "3.3.29";
//    private static boolean m_bHasDetected = false;
//    private static boolean m_bIsPad = false;
//
//
//    private static boolean invokeCheckOpMethod(Context context, int uid, String pkgName) {
//        boolean isClosedByMiuiV6 = false;
//
//        try {
//            Class clz = Class.forName("android.content.Context");
//            Field fd = clz.getDeclaredField("APP_OPS_SERVICE");
//            fd.setAccessible(true);
//            Object obj = fd.get(clz);
//            String ops = "";
//            if (obj instanceof String) {
//                ops = (String) obj;
//                Method method1 = clz.getMethod("getSystemService", String.class);
//                Object appOpsManager = method1.invoke(context, ops);
//                Class<?> cls = Class.forName("android.app.AppOpsManager");
//                fd = cls.getDeclaredField("MODE_ALLOWED");
//                fd.setAccessible(true);
//                int allowMode = fd.getInt(cls);
//
//                Method method = cls.getMethod("checkOp", int.class, int.class, String.class);
//                int opMode =  (Integer) method.invoke(appOpsManager, OP_SYSTEM_ALERT_WINDOW, uid, pkgName);
//                isClosedByMiuiV6 = opMode != allowMode;
//            }
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//        return isClosedByMiuiV6;
//    }

//    private static boolean checkOp(Context context, int uid, String pkgName) {
//        boolean isClosedByMiuiV6 = false;
//        try {
//            AppOpsManager aom = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//            Method method = AppOpsManager.class.getMethod("checkOp", int.class, int.class, String.class);
//            int opMode =  (Integer) method.invoke(aom, OP_SYSTEM_ALERT_WINDOW, uid, pkgName);
//            isClosedByMiuiV6 = opMode != AppOpsManager.MODE_ALLOWED;
//        } catch (Exception e) {
//            android.util.Log.d("ny", "checkOp() e = " + e.getMessage());
//        }
//        android.util.Log.d("ny", "checkOp() isClosedByMiuiV6 = " + isClosedByMiuiV6);
//        return isClosedByMiuiV6;
//    }

//    /**
//     * 判断是不是弹出 WINDOW 被干掉了
//     *
//     * @return
//     */
//    public static boolean isWindowAlterCloseByMIUIV5(Context context) {
//        if (/* DeviceUtils.isMiui() || */PhoneModelUtils.isMiuiV5() || isEMUI()) {
//            String pkgName = context.getPackageName();
//            ApplicationInfo info;
//            try {
//                info = context.getPackageManager().getPackageInfo(pkgName, 0).applicationInfo;
//
//                if (isEMUI() || isSingleMiuiV6()) {
//                    return invokeCheckOpMethod(context, info.uid, pkgName);
//                } else {
//                    if (android.os.Build.VERSION.SDK_INT >= 19) {
//                        return (FLAG_SHOW_FLOATING_WINDOW_GT_V19 & info.flags) == 0;
//                    } else {
//                        return (FLAG_SHOW_FLOATING_WINDOW & info.flags) == 0;
//                    }
//                }
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }catch ( Exception e)
//            {
//
//            }
//        }
//        return false;
//    }

    //hua wei EMUI2.3 以上
//    public static boolean isEMUI(){
//
//        String emui = SystemProperties.get("ro.build.version.emui","unkonw");
//        if(null == emui) return false;
//
//        String emotion[] = emui.split("_");
//        if(emotion.length > 1){
//            if(emotion[0].equalsIgnoreCase("EmotionUI")){
//                String nemui[] = emotion[1].split("\\.");
//                String aemui[] = "2.3".split("\\.");
//                int length = nemui.length > aemui.length ? aemui.length : nemui.length;
//                for (int i = 0 ; i < length ; i++ ){
//                    int n = Integer.valueOf(nemui[i]);
//                    int a = Integer.valueOf(aemui[i]);
//                    if(n == a){
//                        continue;
//                    }else{
//                        return n > a;
//                    }
//                }
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static boolean isMiuiV5() {
//        String ver = SystemProperties.get("ro.miui.ui.version.name", "unkonw");
//        if (ver.equalsIgnoreCase("V5") || ver.equalsIgnoreCase("V6")) {
//            return isAddWindowManagerOnMIUIV5();
//        }
//        return false;
//    }

    /**
     * @note 这个接口可能不准，在一些非V6机器上竟然也返回V6
     * @return
     */
    public static boolean isSingleMiuiV6() {
        String ver = SystemProperties.get("ro.miui.ui.version.name", "unkonw");
        if (ver.equalsIgnoreCase("V6")) {
            return isAddWindowManagerOnMIUIV5();
        }
        return false;
    }

//    public static boolean isSingleMiuiV5() {
//        String ver = SystemProperties.get("ro.miui.ui.version.name", "unkonw");
//        if (ver.equalsIgnoreCase("V5")) {
//            return isAddWindowManagerOnMIUIV5();
//        }
//        return false;
//    }
    /**
     * 是否添加了浮动框管理
     *
     * @return
     */
    private static boolean isAddWindowManagerOnMIUIV5() {
        boolean hasAddWindowManager = true;
        try {
            String ver = SystemProperties.get("ro.build.version.incremental", "unkonw");
            if (TextUtils.isEmpty(ver)) {
                return false;
            }
            // 检测是否为稳定版MUIV5的VERSION   使用字母前缀标示
            boolean needCheckPrefix = Pattern.compile("(?i)[a-z]").matcher(ver).find();
            if (needCheckPrefix) {
                //对M2手机判断
                if (ver.startsWith("JLB")) {
                    int length = ver.length();
                    ver = ver.substring(3, length);
                    float nver = Float.valueOf(ver);
                    if (nver < 22.0f) {
                        hasAddWindowManager = false;
                    }
                }
            } else {
                //对(开发版)数字版本号判断
                String[] nVer = ver.split("\\.");
                String[] aVer = AddWindowManagerVer.split("\\.");
                int length = nVer.length > aVer.length ? aVer.length : nVer.length;
                for (int i = 0; i < length; i++) {
                    int n = Integer.valueOf(nVer[i]);
                    int a = Integer.valueOf(aVer[i]);
                    if (n == a) {
                        continue;
                    } else {
                        hasAddWindowManager = n > a;
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        return hasAddWindowManager;
    }



//    /**
//     * 判断是否为pad以及是否支持横竖屏切换
//     * @return <li>true:是pad且支持横竖屏切换</li><li>false:不是pad且禁止横竖屏切换</li>
//     */
//    public static boolean isPad()
//    {
//        if(!m_bHasDetected)
//        {
//
//            int deviceSizeMask = SpaceApplication.getInstance().getContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
//            if (deviceSizeMask == Configuration.SCREENLAYOUT_SIZE_XLARGE ||
//                    (deviceSizeMask == Configuration.SCREENLAYOUT_SIZE_LARGE ))
//            {
//                if (ConflictCommons.isCNVersion()) {
//                    m_bIsPad = true;
//                } else {
//                    if(DimenUtils.getDiagonalInch(SpaceApplication.getInstance().getContext()) >= 9)
//                    {
//                        m_bIsPad = true;
//                    }
//                    else{
//                        m_bIsPad = false;
//                    }
//                }
//            }
//            else
//            {
//                m_bIsPad = false;
//            }
//            m_bHasDetected = true;
//        }
//        return m_bIsPad;
//    }
}

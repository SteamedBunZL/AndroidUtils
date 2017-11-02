//=============================================================================
/**
 * @file KCleanCloudMiscHelper.java
 */
//=============================================================================
package space.network.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


public class KCleanCloudMiscHelper {

    private static String sAndroidID = null;
    private static Object sAndroidIDLock = new Object();
    public static String GetAndroidID(Context context) {
        String androidid = "";
        if (sAndroidID != null) {
            androidid = sAndroidID;
        } else {
            synchronized(sAndroidIDLock) {
                if (sAndroidID == null) {
                    androidid = directGetAndroidID(context);
                    if (!TextUtils.isEmpty(androidid)) {
                        sAndroidID = androidid;
                    }
                }
            }
        }
        return androidid;
    }

    static public String directGetAndroidID(Context context){
        try{
            ContentResolver cr = context.getContentResolver();
            return Settings.System.getString(cr, Settings.System.ANDROID_ID);
        }
        catch (Exception e) {
            return "";
        }
    }

    public static String GetUuid(Context context) {
        return GetAndroidID(context);
    }

    private static int sVersion = 0;
    public static int getCurrentVersion(Context context) {
        int version = 0;
        if (sVersion != 0) {
            version = sVersion;
        } else {
            PackageManager packManager = context.getPackageManager();
            try {
                PackageInfo packInfo = packManager.getPackageInfo(context.getPackageName(), 0);
                version = packInfo.versionCode;
                sVersion = version;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return version;
    }



    private static String sMCC = null;
    public static String getMCC(Context context) {
        if (context == null)
            return null;

        String myMCC = "";
        if (sMCC != null) {
            myMCC = sMCC;
        } else {
            synchronized(KCleanCloudMiscHelper.class) {
                if (sMCC == null) {
                    String tmpMCC = getRealMCC(context);
                    if (null != tmpMCC
                            && !TextUtils.isEmpty(tmpMCC)) {
                        sMCC = myMCC = tmpMCC;
                    }
                }
            }
        }
        return myMCC;
    }

    private static String getRealMCC(Context context) {
        if (context == null)
            return null;

        final TelephonyManager tm = (TelephonyManager)context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String mcc_mnc = tm.getSimOperator();
        StringBuilder mcc = null;
        int firstDigitIdx=-1;
        if (null != mcc_mnc && mcc_mnc.length() >= 3) {
            //find first digit
            int nSize = mcc_mnc.length();
            for (int i=0; i<nSize; ++i) {
                if (Character.isDigit(mcc_mnc.charAt(i))) {
                    firstDigitIdx = i;
                    break;
                }
            }
            if (firstDigitIdx != -1
                    && (mcc_mnc.length()-firstDigitIdx >= 3)) {
                mcc = new StringBuilder();
                mcc.append(mcc_mnc, firstDigitIdx, firstDigitIdx+3);
                return mcc.toString();
            }
        }
        return null;
    }
}
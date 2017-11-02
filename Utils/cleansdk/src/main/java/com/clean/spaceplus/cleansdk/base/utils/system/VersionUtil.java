package com.clean.spaceplus.cleansdk.base.utils.system;

import android.os.Build;
import android.text.TextUtils;

import java.util.Locale;

/**
 * @author dongdong.huang
 * @Description:版本号格式转换及版本比较
 * @date 2016/5/3 11:25
 * @copyright TCL-MIG
 */
public class VersionUtil {
    /**
     * 比较两个版本号
     * @param ver1
     * @param ver2
     * @return
     */
    public static int compare(String ver1, String ver2) {
        if(ver1 == null && ver2 == null)
        {
            return 0;
        }
        else if(ver1 == null)
        {
            return -1;
        }
        else if(ver2 == null)
        {
            return 1;
        }
        else
        {
            String[] subVers1 = ver1.split("\\.");
            String[] subVers2 = ver2.split("\\.");
            if(subVers1 == null && subVers2 == null)
            {
                return 0;
            }
            else if(subVers1 == null)
            {
                return -1;
            }
            else if(subVers2 == null)
            {
                return 1;
            }
            else
            {
                int idx = 0;
                for(; idx < subVers1.length && idx < subVers2.length; ++idx)
                {
                    long iSubVer1 = 0;
                    long iSubVer2 = 0;

                    try{
                        iSubVer1 = Long.parseLong(subVers1[idx]);
                    }catch(Exception e){
                        return -1;
                    }

                    try{
                        iSubVer2 = Long.parseLong(subVers2[idx]);
                    }catch(Exception e){
                        return 1;
                    }

                    if(iSubVer1 > iSubVer2)
                    {
                        return 1;
                    }
                    else if(iSubVer1 < iSubVer2)
                    {
                        return -1;
                    }
                }

                return subVers1.length - subVers2.length;
            }
        }
    }

    /**
     * 将形如30110001(3-01-1-0001)版本号格式化为3.1.1.1
     * @param version
     * @return
     */
    public static String translateDecimal(int version) {
        return String.format(Locale.US, "%d.%d.%d.%d",
                version / 10000000,
                version / 100000 % 100,
                version / 10000 % 10,
                version % 10000
        );
    }


//    /**
//     * 获取3位个数的系统版本数值，如 230, 401, 400
//     * */
//    public static short initVersionString() {
//        short r = 0;
//        try {
//            String version = Build.VERSION.RELEASE.replace(".", "");
//            if (TextUtils.isEmpty(version)) {
//                return 0;
//            }
//
//            String[] specTags = new String[] {
//                    "r", "b", "a"
//            };
//
//            for (String idx : specTags) {
//                int pos = version.indexOf(idx);
//                if (-1 < pos) {
//                    version = version.substring(0, pos);
//                }
//            }
//
//            version = version.trim();
//
//            if (version.length() == 2) {
//                version += "0";
//            }
//
//            r = Short.parseShort(version);
//        } catch (Exception e) {
//            r = 0;
//        }
//
//        return r;
//    }
}

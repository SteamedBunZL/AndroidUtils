package com.clean.spaceplus.cleansdk.junk.cleancloud;

import space.network.util.compress.Base64;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/23 19:24
 * @copyright TCL-MIG
 */
public class CloudCfgDataWrapper {
    public static String getCloudCfgStringValue(String key, String subKey, String defValue){
//        if(RuntimeCheck.IsServiceProcess()) {
//            return CloudCfgData.getInstance().getStringValue(key, subKey, defValue);
//        }else {
//            try {
//                return SyncIpcCtrl.getIns().getIPCClient().getCloudCfgStringValue(key, subKey, defValue);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//                return defValue;
//            }
//        }
        return "";
    }

    /* 对于云端多语言的文字，该方法根据本地语言取云端对应语言的文字
	 * @param defValue 必须是没有被encode的
	 */

///<DEAD CODE>///     public static String getCloudStringOfCustomLanguage(String key, String subKey, final String defValue, boolean bBase64Decode, String lang, String country, Object... args){
//        String strReturn = "";
//        StringBuilder sb = new StringBuilder(subKey);
//        if(TextUtils.isEmpty(lang) || TextUtils.isEmpty(country)) {
//            LanguageCountry language = ServiceConfigManager.getInstanse(MoSecurityApplication.getAppContext().getApplicationContext()).getLanguageSelected(MoSecurityApplication.getAppContext().getApplicationContext());
//            if (null != language) {
//                sb.append("_").append(language.getLanguage());
//                if (!TextUtils.isEmpty(language.getCountry())) {
//                    sb.append("_").append(language.getCountry());
//                }
//            }
//        }else{
//            sb.append("_").append(lang).append("_").append(country);
//        }
//
//        String value = getCloudCfgStringValue(key, sb.toString(), defValue);
//        if(value == null || value.length() <= 0) {
//            value = defValue;
//        }
//
//        if(bBase64Decode && !value.equalsIgnoreCase(defValue)) {
//            try{
//                strReturn = new String(Base64.decode(value));
//            }catch(Exception e) {
//                e.printStackTrace();
//                strReturn = defValue;
//            }
//        }else{
//            strReturn = value;
//        }
//
//        if(args != null && args.length > 0){
//            try{
//                strReturn = String.format(strReturn, args);
//            }catch(Exception e){
//                e.printStackTrace();
//                try {
//                    strReturn = String.format(defValue, args);
//                } catch (Exception e1) {}
//            }
//        }
//        return strReturn;
//    }

	/* 对于云端多语言的文字，该方法根据本地语言取云端对应语言的文字
	 * @param defValue 必须是没有被encode的
	 */

    public static String getCloudStringOfLocalLanguage(String key, String subKey, final String defValue, boolean bBase64Decode, Object... args){
        String strReturn = "";
//        LanguageCountry language = ServiceConfigManager.getInstanse(CleanCloudManager.getApplicationContext()).getLanguageSelected(MoSecurityApplication.getAppContext().getApplicationContext());
//        if(null != language){
//            subKey += ("_" + language.getLanguage());
//            if(!TextUtils.isEmpty(language.getCountry())){
//                subKey += ("_" + language.getCountry());
//            }
//        }

        String value = getCloudCfgStringValue(key, subKey, defValue);
        if(value == null || value.length() <= 0) {
            value = defValue;
        }

        if(bBase64Decode && !value.equalsIgnoreCase(defValue)) {
            try{
                strReturn = new String(Base64.decode(value));
            }catch(Exception e) {
                e.printStackTrace();
                strReturn = defValue;
            }
        }else{
            strReturn = value;
        }

        if(args != null && args.length > 0){
            try{
                strReturn = String.format(strReturn, args);
            }catch(Exception e){
                e.printStackTrace();
                try {
                    strReturn = String.format(defValue, args);
                } catch (Exception e1) {}
            }
        }
        return strReturn;
    }

    //获取的文字版本目前仅针对getCloudStringOfLocalLanguage对应的云端文字
    public static int getCloudStrigVersion(){
//        return getCloudCfgIntValue(CloudCfgKey.CLOUD_JUNK_STRING_KEY, CloudCfgKey.STRING_VERSION, 0);
        return 0;
    }

    // -- 云端文案版本号
///<DEAD CODE>///     public static String getCloudVersion() {
//        String version = getCloudCfgStringValue(CloudCfgKey.CLOUD_CFG_INFO,
//                CloudCfgKey.CLOUD_CFG_VERSION,
//                Env.getVersionNameL(MoSecurityApplication.getAppContext()));
//        return version;
//    }

    public static boolean getCloudCfgBooleanValue(String key, String subKey, boolean defValue){
        String dValue = defValue ? "1" : "0";
        String value = getCloudCfgStringValue(key, subKey, dValue);
        if(value == null || value.length() <= 0) {
            return defValue;
        }
        return !value.equalsIgnoreCase("0");
    }

    public static int getCloudCfgIntValue(String key, String subKey, int defValue){
        String dValue = Integer.toString(defValue);
        String value = getCloudCfgStringValue(key, subKey, dValue);
        if(value == null || value.length() <= 0) {
            return defValue;
        }

        try{
            return Integer.parseInt(value);
        }catch(Exception e) {
            e.printStackTrace();
//            MyCrashHandler.getInstance().throwOne(e, false);
            return defValue;
        }
    }

    public static long getCloudCfgLongValue(String key, String subKey, long defValue){
        String dValue = Long.toString(defValue);
        String value = getCloudCfgStringValue(key, subKey, dValue);
        if(value == null || value.length() <= 0) {
            return defValue;
        }

        try{
            return Long.parseLong(value);
        }catch(Exception e) {
            e.printStackTrace();
//            MyCrashHandler.getInstance().throwOne(e, false);
            return defValue;
        }
    }
}

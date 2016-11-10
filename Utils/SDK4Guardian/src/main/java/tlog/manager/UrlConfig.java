package tlog.manager;

import android.text.TextUtils;

/**
 * description:
 * author hui.zhu
 * date 2016/10/24
 * copyright TCL-MIG
 */


public class UrlConfig {
    private static final String SERVICE_URL = "http://gw.rtdp.tclclouds.com/";//BuildConfig.LOG_URL;
    //private static final String SERVICE_URL = "http://54.165.152.107/";//BuildConfig.LOG_URL;

    private static final String ROOT_API = "api/log/";

    public static String getAbsoluteURI() {
        StringBuilder sb = new StringBuilder();
        String configUrl = "";
//        if (Constant.DEBUG) {
//            configUrl = FileManager.readConfigParams("configLogUrl.txt");
//        }
        if (!TextUtils.isEmpty(configUrl)) {
            sb.append(configUrl);
        } else {
            sb.append(SERVICE_URL);
        }
        sb.append(ROOT_API);
        return sb.toString();
    }

}

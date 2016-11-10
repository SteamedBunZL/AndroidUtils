package tlogsdk;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Steve on 16/11/3.
 */

public class HiLogUtil {

    /**
     * 获取UUID JO
     * @param context
     * @return
     */
    public static JSONObject getUidJo(Context context){
        JSONObject jo = null;
        try {
            jo = new JSONObject();
            jo.put(HiLogConstants.UUID,HiLogCommonUtil.getUid(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    /**
     * 获取公共参数
     * @param context
     * @return
     */
    public static JSONObject getDefaultJo(Context context){
        JSONObject defautObject = null;
        try {
            defautObject = new JSONObject();
            defautObject.put(HiLogConstants.VER,HiLogCommonUtil.getVer(context));
            defautObject.put(HiLogConstants.VERSION_NAME,HiLogCommonUtil.getVersionName(context));
            defautObject.put(HiLogConstants.LANG,HiLogCommonUtil.getLang());
            defautObject.put(HiLogConstants.SOURCE,HiLogCommonUtil.getSource());
            defautObject.put(HiLogConstants.OSVER,HiLogCommonUtil.getOsVer());
            defautObject.put(HiLogConstants.AREA,HiLogCommonUtil.getArea());
            defautObject.put(HiLogConstants.MODEL,HiLogCommonUtil.getModel());
            defautObject.put(HiLogConstants.BRAND,HiLogCommonUtil.getBrand());
            defautObject.put(HiLogConstants.TIME,HiLogCommonUtil.getUTCTime());
            defautObject.put(HiLogConstants.APPID,HiLogCommonUtil.getAppId(context));
            defautObject.put(HiLogConstants.SDK_VER,HiLogCommonUtil.getSdkVer());
            defautObject.put(HiLogConstants.NETWORK,HiLogCommonUtil.getNetwork(context));
            defautObject.put(HiLogConstants.HEIGHT_WIDTH,HiLogCommonUtil.getHeightWidth(context));
            defautObject.put(HiLogConstants.UUID,HiLogCommonUtil.getUid(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defautObject;
    }



}

package tlogsdk;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tlog.manager.TLogManager;

/**
 * Created by Steve on 16/11/3.
 */

public class HiLogManager {

    private static Context mContext;

    /**
     * 初始化HiLogManager
     * @param context
     */
    public static void initialize(Context context){
        mContext = context.getApplicationContext();
        JSONObject joDefault = HiLogUtil.getDefaultJo(mContext);
        TLogManager.initInstance(context,context.getPackageName(),joDefault.toString(),HiLogUtil.getUidJo(context.getApplicationContext()));
    }


    public static void sendEvent(String eventId, Map<String , String> params){
        if (mContext==null)
            return;
        if (params==null||params.isEmpty())
            return;
        try {
            JSONObject jo = new JSONObject();
            JSONObject joDefault = HiLogUtil.getDefaultJo(mContext);
            JSONObject joEvent = new JSONObject();
            JSONObject joParams = getMapJo(params);
            jo.put("appkey",mContext.getPackageName());
            jo.put("default",joDefault);
            joEvent.put(eventId,joParams);
            jo.put("events",joEvent);
            TLogManager.defaultManager().reportLog(jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void sendEvent(String eventId){

    }

    public static void sendEvent(String eventId,boolean param){

    }

    public static void sendEvent(String eventId,String key,Integer value){
        if (eventId == null)
            return;
        Map<String , String> params = new HashMap<>();
        params.put(key , value.toString());
        sendEvent(eventId, params);
    }


    public static JSONObject getMapJo(Map<String,String> params){
        try {
            JSONObject jo = new JSONObject();
            Iterator it = params.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String,String> entry = (Map.Entry<String, String>) it.next();
                String key = entry.getKey();
                String value = entry.getValue();
                jo.put(key,value);
            }
            return jo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }







}

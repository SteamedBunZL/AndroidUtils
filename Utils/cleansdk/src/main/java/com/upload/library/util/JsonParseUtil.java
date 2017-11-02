package com.upload.library.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zeming_liu
 * @Description:
 * @date 2016/9/7.
 * @copyright TCL-MIG
 */
public class JsonParseUtil {


    private static JSONObject generateJSONObject(String json) {
        try {

            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getString(String json, String key) {
        try {
            return generateJSONObject(json).optString(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public static String getString(JSONObject jsonObj, String key) {
        try {
            return jsonObj.optString(key);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }


    }

    public static int getInt(String json, String key) {

        try {
            return generateJSONObject(json).optInt(key);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }


    }



    public static String getJSONObject(String json, String key) {
        try {
            return generateJSONObject(json).optJSONObject(key).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONArray getJSONArray(String json, String key) {
        try {

            return generateJSONObject(json).getJSONArray(key);
        } catch (Exception e) {
            return null;
        }
    }



}

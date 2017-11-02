package com.clean.spaceplus.cleansdk.base.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author shunyou.huang
 * @Description:JSON实用类
 * @date 2016/6/29 18:44
 * @copyright TCL-MIG
 */

public class JSONUtil {

    public static int jsonArrayOutput(JSONArray array, OutputStream os) throws IOException, JSONException {

        int count = 0;
        if (array == null || array.length() == 0) {
            final byte[] EMPTY = "[]".getBytes("utf-8");
            os.write(EMPTY);
            count = EMPTY.length;
        }

        final byte[] prefix = "[".getBytes("utf-8");
        final byte[] suffix = "]".getBytes("utf-8");
        final byte[] SPLIT = ",".getBytes("utf-8");

        count += prefix.length;
        os.write(prefix);
        byte[] bytes = null;

        for (int i = 0; i< array.length(); i++) {
            Object o = array.get(i);
            String json = o.toString();
            bytes = json.getBytes("utf-8");
            os.write(bytes);
            count += bytes.length;

            if (i != array.length() -1) {
                os.write(SPLIT);
                count += SPLIT.length;
            }
        }

        os.write(suffix);
        count += suffix.length;
        return count;
    }

//    public static int jsonOutput(JSONObject json, OutputStream os) throws  IOException {
//        int count = 0;
//        String jsonStr = json.toString();
//        byte[] bytes = jsonStr.getBytes("utf-8");
//        os.write(bytes);
//        count += bytes.length;
//
//        return count;
//    }
}

package com.tcl.security.virusengine.utils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * json工具类
 * Created by Steve on 2016/7/12.
 */
public class JSON {




    /**
     * 将对象转换为Json String
     * @author steve
     * @param t
     * @return
     */
    public static <T> String toJson(T t) {
        Gson gson = new Gson();
        return gson.toJson(t);
    }


    /**
     * 将json解析为(clasz)Object
     * @author steve
     * @param json
     * @param clasz
     * @return
     */
    public static <T> T getObject(String json, Class<T> clasz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, clasz);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json中的list解析为ArrayList<itemClass>
     * @author steve
     * @param json
     * @param itemClass
     * @return
     */
    public static <T> ArrayList<T> getList(String json, Class<T> itemClass) {
        JSONArray jsonArray;
        ArrayList<T> list = new ArrayList<T>();
        String jsonItem;
        T obj;
        Gson gson = new Gson();
        try {
            jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonItem = jsonArray.getString(i);
                obj = gson.fromJson(jsonItem, itemClass);
                list.add(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}

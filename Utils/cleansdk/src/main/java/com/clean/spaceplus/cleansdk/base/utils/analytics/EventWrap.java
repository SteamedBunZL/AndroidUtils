package com.clean.spaceplus.cleansdk.base.utils.analytics;

import com.google.gson.annotations.Expose;

import org.json.JSONObject;

import java.util.List;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/7/7 20:04
 * @copyright TCL-MIG
 */
public abstract class EventWrap implements Runnable{

    @Expose(deserialize = false,serialize = false)
    List<JSONObject> mEvents;

}

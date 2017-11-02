package com.clean.spaceplus.cleansdk.base.utils.analytics;

import org.json.JSONObject;

import java.util.List;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/7/6 11:29
 * @copyright TCL-MIG
 */
public interface EventDao {

    List<JSONObject> getEvents();

    void putEvent(JSONObject t);

    void putEvents(List<JSONObject> t);
}

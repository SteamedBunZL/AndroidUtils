package com.clean.spaceplus.cleansdk.base.utils.analytics;

import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;

import org.json.JSONObject;

/**
 * @author haiyang.tan
 * @Description:
 * @date 2016/7/4 19:13
 * @copyright TCL-MIG
 */
public abstract class Event {

    public String toJson(){
        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
    }


    public JSONObject parseToJSONObject(){
        JSONObject json=null;
        try{
            json=new JSONObject(toJson());
        }catch (Exception e){
            json=new JSONObject();
        }
        return json;
    }
}

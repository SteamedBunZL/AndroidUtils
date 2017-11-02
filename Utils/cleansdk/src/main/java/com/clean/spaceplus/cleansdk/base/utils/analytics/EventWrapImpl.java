package com.clean.spaceplus.cleansdk.base.utils.analytics;

import com.clean.spaceplus.cleansdk.base.utils.CommonUtils;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;
import com.clean.spaceplus.cleansdk.base.utils.analytics.bean.AnalyticsBaseBean;
import com.clean.spaceplus.cleansdk.base.utils.analytics.mgmt.DataReportMgmt;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hawkclean.framework.log.NLog;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


/**
 * @author haiyang.tan
 * @Description: 用来包装Event，加入一些公共参数
 * @date 2016/7/5 15:06
 * @copyright TCL-MIG
 */
public class EventWrapImpl extends EventWrap  {

    public static final String TAG = EventWrapImpl.class.getSimpleName();

    @Expose
    @SerializedName("basep")
    public AnalyticsBaseBean baseBean = new AnalyticsBaseBean();

    @Expose
    @SerializedName(DataReprotStringKey.APPKEY)
    public String appkey;

    public EventWrapImpl(JSONObject event){
        appkey= CommonUtils.getStatisticsKey();
        if (event == null){
            return;
        }
        mEvents = new LinkedList<>();
        mEvents.add(event);
    }

    public EventWrapImpl(List<JSONObject> events){
        appkey=CommonUtils.getStatisticsKey();
        if (events == null){
            return;
        }
        if (events.isEmpty()){
            return;
        }
        int size = events.size();
        mEvents = new LinkedList<>();
        JSONObject event = null;
        for (int i = 0; i < size; i++){
            event = events.get(i);
            mEvents.add(event);
        }
    }

    @Override
    public void run() {
        DataReportMgmt mgmt = DataReportMgmt.getInstance();
        try {
            JSONObject json=new JSONObject(DataReportFactory.getDefaultGson().toJson(this,this.getClass()));
            JSONArray jsonArray=new JSONArray();
            json.put("operationp",jsonArray);
            for (JSONObject item:mEvents) {
                jsonArray.put(item);
            }
            if(PublishVersionManager.isTest()){
                mgmt.submitAnalyticsTest(json.toString());
            }
            else{
                mgmt.submitAnalytics(json.toString());
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
    }
}

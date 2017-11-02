package com.clean.spaceplus.cleansdk.base.utils.analytics.bean;

import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.bean.DataReportPublicBean;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author zeming_liu
 * @Description: 活跃用户事件
 * @date 2016/9/20.
 * @copyright TCL-MIG
 */
public class StartEvent  extends Event {

    @Expose
    @SerializedName(DataReprotStringKey.EVENTNAME)
    public String mName;
    @Expose
    @SerializedName(DataReprotStringKey.OPENMODE)
    public String mOpenModel;

    public StartEvent(String openmodel){
        mName= DataReportPublicBean.EVENT_SPACE_START;
        mOpenModel=openmodel;
    }

    @Override
    public String toJson() {
        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
    }
}

package com.clean.spaceplus.cleansdk.base.utils.analytics.bean;

import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.bean.DataReportPublicBean;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author zeming_liu
 * @Description: 安装事件
 * @date 2016/9/20.
 * @copyright TCL-MIG
 */
public class InstallEvent extends Event {

    @Expose
    @SerializedName(DataReprotStringKey.EVENTNAME)
    public String mName;
    @Expose
    @SerializedName(DataReprotStringKey.ACTIVETIME)
    public String mActivetime;

    public InstallEvent(String activetime){
        mName= DataReportPublicBean.EVENT_SPACE_INSTALL;
        mActivetime=activetime;

    }

    @Override
    public String toJson() {
        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
    }
}

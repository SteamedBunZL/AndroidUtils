package com.clean.spaceplus.cleansdk.base.utils.analytics.bean;

import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportFactory;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.bean.DataReportPublicBean;
import com.clean.spaceplus.cleansdk.base.utils.analytics.Event;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author zeming_liu
 * @Description: 手机用户app事件
 * @date 2016/9/20.
 * @copyright TCL-MIG
 */
public class AppEvent extends Event {

    @Expose
    @SerializedName(DataReprotStringKey.EVENTNAME)
    public String mName;
    @Expose
    @SerializedName(DataReprotStringKey.PKGNAME)
    public String pkgname;
    @Expose
    @SerializedName(DataReprotStringKey.TYPE)
    public String type;

    public AppEvent(String pkgname, String type){
        mName= DataReportPublicBean.EVENT_SPACE_APP;
        this.pkgname = pkgname;
        this.type = type;
    }

    @Override
    public String toJson() {
        return DataReportFactory.getDefaultGson().toJson(this, this.getClass());
    }

}

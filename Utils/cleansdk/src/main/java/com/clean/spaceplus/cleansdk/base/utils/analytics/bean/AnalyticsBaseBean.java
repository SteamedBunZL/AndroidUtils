package com.clean.spaceplus.cleansdk.base.utils.analytics.bean;

import android.content.Context;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.utils.CommonUtils;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReportConfigManage;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.DataReprotStringKey;
import com.clean.spaceplus.cleansdk.util.LanguageUtil;
import com.clean.spaceplus.cleansdk.util.PhoneUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hawkclean.mig.commonframework.util.CommonUtil;

/**
 * @author haiyang.tan
 * @Description: 公共参数bean
 * @date 2016/7/7 20:11
 * @copyright TCL-MIG
 */
public class AnalyticsBaseBean {
    @Expose
    @SerializedName(DataReprotStringKey.UUID)
    public String UUID;
    @Expose
    @SerializedName(DataReprotStringKey.VER)
    public String ver;
    @Expose
    @SerializedName(DataReprotStringKey.MCC)
    public String mcc;
    @Expose
    @SerializedName(DataReprotStringKey.SOURCE)
    public String source;
    @Expose
    @SerializedName(DataReprotStringKey.BRAND)
    public String brand;
    @Expose
    @SerializedName(DataReprotStringKey.MODEL)
    public String model;
    @Expose
    @SerializedName(DataReprotStringKey.LANGUAGE)
    public String language;
    @Expose
    @SerializedName(DataReprotStringKey.OSVER)
    public String osver;
    @Expose
    @SerializedName(DataReprotStringKey.AREA)
    public String area;
    @Expose
    @SerializedName(DataReprotStringKey.TIME)
    public String time;

    public AnalyticsBaseBean(){
        Context context  = SpaceApplication.getInstance().getContext();
        UUID = DataReportConfigManage.getInstance().getInstanceId();
        //移动国家号码
        mcc= CommonUtils.getIMSI(context);
        //渠道号
        source = CommonUtils.getChannelId();
        //手机品牌
        brand = PhoneUtil.getPhoneBrand();
        //手机型号
        model = PhoneUtil.getPhoneModel();
        language = LanguageUtil.getLanguage(context);
        //程序版本号
        ver = String.valueOf(CommonUtil.getVersionName());
        //memoryc = String.valueOf(MemoryInfoHelper.getTotalMemoryByte());//获取当前总内存值，单位byte
        //storagec = String.valueOf(PhoneUtil.getTotalStorageSizeByte());
        osver = PhoneUtil.getOsVersionName();
        //ifr = SuExec.getInstance().getIsRootMark();
        //androidID = PhoneUtil.getAndroidId(SpaceApplication.getInstance().getContext());
        //国家
        area = LanguageUtil.getCountry(context);
        //cpu = PhoneUtil.getCpuType();
        time=String.valueOf(System.currentTimeMillis());
    }
}

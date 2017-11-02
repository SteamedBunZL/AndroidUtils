package com.clean.spaceplus.cleansdk.base.utils.DataReport;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.config.DBVersionConfigManager;
import com.clean.spaceplus.cleansdk.base.utils.CommonUtils;
import com.clean.spaceplus.cleansdk.base.utils.DataReport.bean.DataReportPublicBean;
import com.clean.spaceplus.cleansdk.base.utils.NetUtils;
import com.clean.spaceplus.cleansdk.base.utils.analytics.bean.ActivityEvent;
import com.clean.spaceplus.cleansdk.base.utils.analytics.bean.InstallEvent;
import com.clean.spaceplus.cleansdk.base.utils.analytics.bean.StartEvent;
import com.clean.spaceplus.cleansdk.base.utils.root.SuExec;
import com.clean.spaceplus.cleansdk.boost.util.MemoryInfoHelper;
import com.clean.spaceplus.cleansdk.util.DateUtils;
import com.clean.spaceplus.cleansdk.util.PackageUtils;
import com.clean.spaceplus.cleansdk.util.PhoneUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hawkclean.mig.commonframework.network.BaseHttpUtils;
import com.hawkclean.mig.commonframework.util.CommonUtil;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * @author zeming_liu
 * @Description: 数据上报工厂类
 * @date 2016/9/14.
 * @copyright TCL-MIG
 */
public class DataReportFactory {

    //鹰眼sdk接口
    private static volatile IDataReportTarget _instance;
    private static volatile OkHttpClient _httpInstance;
    private static volatile Gson _gson;

    public static Gson getDefaultGson(){
        if (_gson == null) {
            synchronized (DataReportFactory.class) {
                if (_gson == null) {
                    _gson = new GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation()
                            .create();
                }
            }
        }
        return _gson;
    }

    /**
     * 获取默认的统计接口
     * @return
     */
    public static IDataReportTarget getDefaultDataReport(){
        if (_instance == null) {
            synchronized (DataReportFactory.class) {
                if (_instance == null) {
                    _instance = new StatisticsAgentAdapter();
                }
            }
        }
        return _instance;
    }

    public static OkHttpClient getHttpInstance(){
        if(_httpInstance==null){
            synchronized (DataReportFactory.class) {
                if (_httpInstance == null) {
                    if (PublishVersionManager.isTest()) {
                        _httpInstance = dataReportHttpClientTest();
                    }
                    else{
                        _httpInstance = dataReportHttpClient();
                    }
                }
            }
        }
        return _httpInstance;
    }

    private static OkHttpClient dataReportHttpClient(){
        BaseHttpUtils baseHttpUtils = new BaseHttpUtils(SpaceApplication.getInstance().getContext(), true);
        OkHttpClient.Builder builder=baseHttpUtils.buildDefalutClient(SpaceApplication.getInstance().getContext());
        try {
            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] x509Certificates = new X509Certificate[0];
                    return x509Certificates;
                }
            };
            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());
            HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            builder.sslSocketFactory(sslContext.getSocketFactory());
            builder.hostnameVerifier(DO_NOT_VERIFY);
            return builder.build();
        } catch (Exception e) {
        }
        return null;
    }

    private static OkHttpClient dataReportHttpClientTest(){
        BaseHttpUtils baseHttpUtils = new BaseHttpUtils(SpaceApplication.getInstance().getContext(), true);
        OkHttpClient.Builder builder=baseHttpUtils.buildDefalutClient(SpaceApplication.getInstance().getContext());
        return builder.build();
    }

    /**
     * 上报安装情况
     */
    public static void DataReportInstall(){
        //当前版本号
        String currVersion= CommonUtil.getVersionName();
        String reportVersion=DataReportConfigManage.getInstance().getReportVersion();
        if(reportVersion==null || reportVersion.isEmpty()){
            //全新安装
            InstallEvent event=new InstallEvent(DataReportPublicBean.INSTALL_NEW);
            getDefaultDataReport().putEvent(event);
            DataReportConfigManage.getInstance().setReportVersion(currVersion);
        }
        else if(!currVersion.equalsIgnoreCase(reportVersion)){
            //覆盖安装
            InstallEvent event=new InstallEvent(DataReportPublicBean.INSTALL_OVER);
            getDefaultDataReport().putEvent(event);
            DataReportConfigManage.getInstance().setReportVersion(currVersion);
        }
    }

    /**
     * 上报启动应用活跃用户
     */
    public static void DataReportStart(String mode){
        if(!DataReportConfigManage.isAppStart){
            //应用启动时候上报
            StartEvent event=new StartEvent(mode);
            getDefaultDataReport().putEvent(event);
            DataReportConfigManage.isAppStart=true;
        }

    }

    /**
     * 后台报活，目前设置的是一天上报一次
     */
    public static void DataReportActivity(){
        String reportActivityTime=DataReportConfigManage.getInstance().getLastReportActivityTime();
        String currDate= DateUtils.simpleFormat(System.currentTimeMillis());
        if(!currDate.equalsIgnoreCase(reportActivityTime)){
            String width=String.valueOf(CommonUtils.getDisplayMetricsWidth(SpaceApplication.getInstance().getContext()));
            String height=String.valueOf(CommonUtils.getDisplayMetricsHeight(SpaceApplication.getInstance().getContext()));
            String dpi= width+","+height;
            String imsi=CommonUtils.getIMSI(SpaceApplication.getInstance().getContext());
            String ram= String.valueOf(MemoryInfoHelper.getTotalMemoryByte());
            long inStro=PhoneUtil.getTotalInternalStorageSize();
            long extStro=0;
            String sdcardin= String.valueOf(inStro);
            String sdcardout=String.valueOf(extStro);
            String sdcardbluk=String.valueOf(inStro+extStro);
            String sdcard = DataReportPublicBean.SDCARD_NO;
            List<String> list=PhoneUtil.getExtSDCardPath();
            if(list==null || list.size()>0){
                //有外置sd卡
                try{
                    sdcard = DataReportPublicBean.SDCARD_YES;
                    extStro=PhoneUtil.getTotalExternalStorageSize(list.get(0));
                    sdcardout=String.valueOf(extStro);
                    sdcardbluk=String.valueOf(inStro+extStro);
                }catch (Exception e){

                }
            }
            String isRoot=DataReportPublicBean.ROOT_NO;
            if(SuExec.getInstance().checkRoot()){
                isRoot=DataReportPublicBean.ROOT_YES;
            }
            ActivityEvent event=new ActivityEvent(dpi,imsi,ram,sdcard,sdcardin,sdcardout,sdcardbluk,
                    findCompeting(), NetUtils.getConnectType(SpaceApplication.getInstance().getContext()),isRoot,
                    DBVersionConfigManager.getInstance().getCurrentAllDBVersion());
            getDefaultDataReport().putEvent(event);
            DataReportConfigManage.getInstance().setLastReportActivityTime(currDate);
        }
    }

    //查找竞品
    private static String findCompeting(){
        StringBuilder sb=new StringBuilder();
        //360 手机卫士 gp版
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.qihoo360.mobilesafe.gpe")!=null)sb.append(" 1");
        //DU Speed Booster
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.dianxinos.optimizer.duplay")!=null)sb.append(" 3");
        //360手机卫士（极客版）
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.qihoo.antivirus")!=null)sb.append(" 4");
        //腾讯手机管家
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.tencent.qqpimsecure")!=null)sb.append(" 5");
        //DU Clean Master
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.duapps.cleanmaster")!=null)sb.append(" 6");
        //360 security
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.qihoo.security")!=null)sb.append(" 7");
        //360清理大师
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.qihoo.cleandroid_cn")!=null)sb.append(" 8");
        //Power Clean(Booster & Cleaner)
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.lionmobi.powerclean")!=null)sb.append(" 9");
        //UC Cleaner: Clean Memory
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"mobi.uclean.boost")!=null)sb.append(" 10");
        //360 Security Lite
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.qihoo.security.lite")!=null)sb.append(" 11");
        //SuperB Cleaner
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.hermes.superb.booster")!=null)sb.append(" 12");
        //Go speed
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.gto.zero.zboost")!=null)sb.append(" 13");
        //Antivirus, Booster & Cleaner
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.psafe.msuite")!=null)sb.append(" 14");
        //Antivirus, Booster & Cleaner
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.cleanmaster.mguard")!=null)sb.append(" 15");
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.lbe.security")!=null)sb.append(" 16");
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"com.turboc.cleaner")!=null)sb.append(" 17");
        if(PackageUtils.getApplicationInfo(SpaceApplication.getInstance().getContext(),"mobi.yellow.booster")!=null)sb.append(" 18");
        String value=sb.toString();
        if(value==null || value.length()<=0){
            value="0";
        }
        return value;
    }


}

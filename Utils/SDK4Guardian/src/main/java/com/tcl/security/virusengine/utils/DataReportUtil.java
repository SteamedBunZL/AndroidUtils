package com.tcl.security.virusengine.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tcl.security.virusengine.VirusEngine;
import com.tcl.security.virusengine.VirusScanQueue;
import com.tcl.security.virusengine.modle.ASModle;
import com.tcl.security.virusengine.modle.AVEngineModle;
import com.tcl.security.virusengine.modle.DeviceModle;
import com.tcl.security.virusengine.modle.HiSecuritySDKModle;

import java.util.Map;

/**
 * Created by Steve on 16/9/26.
 */

public class DataReportUtil {


    public static Map getDeviceMap(Context context){
        return DeviceInfo.get(context);
    }

    /**
     * 获取AsModle
     * @param deviceMap
     * @return
     */
    @NonNull
    public static ASModle getAsModle(Map<String, String> deviceMap) {
        ASModle asModle = new ASModle();
        asModle.ProductName = deviceMap.get(DeviceInfo.appNameTag);
        if (deviceMap.get(DeviceInfo.versionCodeTag)!=null)
            asModle.VersionCode = Integer.parseInt(deviceMap.get(DeviceInfo.versionCodeTag));
        asModle.VDLibVersion = "2016071301";
        asModle.VersionName = deviceMap.get(DeviceInfo.versionNameTag);
        return asModle;
    }

    /**
     * 获取AVEngineModle
     * @param deviceMap
     * @return
     */
    @NonNull
    public static AVEngineModle getAvEngineModle(Map<String, String> deviceMap) {
        AVEngineModle avEngineModle = new AVEngineModle();
        avEngineModle.Name = deviceMap.get(DeviceInfo.avengineNameTag);
        avEngineModle.Version = VirusScanQueue.getInstance().getScanMessage().getMcafeeEngineVersion();
        avEngineModle.VirusLibVersion = VirusScanQueue.getInstance().getScanMessage().getLibVersion();
        return avEngineModle;
    }

    /**
     * 获取DeviceModle
     * @param deviceMap
     * @return
     */
    @NonNull
    public static DeviceModle getDeviceModle(Map<String, String> deviceMap) {
        DeviceModle deviceModle = new DeviceModle();
        deviceModle.Brand = deviceMap.get(DeviceInfo.brandTag);
        deviceModle.Model = deviceMap.get(DeviceInfo.modelTag);
        if(deviceMap.get(DeviceInfo.sdkTag)!=null)
            deviceModle.AndroidSDK = Integer.parseInt(deviceMap.get(DeviceInfo.sdkTag));
        deviceModle.IMEI = deviceMap.get(DeviceInfo.imeiTag);
        deviceModle.AndroidID = deviceMap.get(DeviceInfo.androidIdTag);
        deviceModle.Language = deviceMap.get(DeviceInfo.langTag);
        deviceModle.Country = deviceMap.get(DeviceInfo.countryTag);
        if (deviceMap.get(DeviceInfo.networkTag)!=null)
            deviceModle.Network = Integer.parseInt(deviceMap.get(DeviceInfo.networkTag));
        return deviceModle;
    }

    public static HiSecuritySDKModle getHiSecuritySDK(){
        HiSecuritySDKModle sdkModle = new HiSecuritySDKModle();
        if (VirusEngine.engineForClean){
            sdkModle.ProductName = "Spase";
        }else{
            sdkModle.ProductName = "PhoneGuard";
        }
        sdkModle.VersionCode = 1;
        sdkModle.VersionName = "1.0";
        sdkModle.VDLibVersion = "20161107";
        return sdkModle;
    }
}

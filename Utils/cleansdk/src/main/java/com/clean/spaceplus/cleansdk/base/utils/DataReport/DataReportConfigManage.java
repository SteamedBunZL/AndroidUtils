package com.clean.spaceplus.cleansdk.base.utils.DataReport;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.util.SharePreferenceUtil;
import com.hawkclean.mig.commonframework.util.CommonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;

/**
 * @author zeming_liu
 * @Description:数据上报控制配置信息
 * @date 2016/9/22.
 * @copyright TCL-MIG
 */
public class DataReportConfigManage {
    public static final String SPACE_DATAREPORT_CONFIG_SP_NAME = "space_datareport_config_sp_name";

    //如果应用启动上报了一次，后面就不需要上报，除非再次启动
    public static boolean isAppStart;
    public static boolean isAppRestart=true;

    //记录上报后台活跃的时间
    private static final String SPACE_DATAREPORT_ACTIVITY_TIME = "space_datareport_activity_time";
    //需要上报后台活跃的周期时间，现在1天报一次
    public static final long SPACE_DATAREPORT_ACTIVITY_TIME_CYCLE=24*3600*1000;

    //记录应用版本号，安装判断需要
    public static final String SPACE_DATAREPORT_VERSION="space_datareport_version";
    //记录客户端当天安装的时间，主要是页面埋点的用户类型
    private static final String SPACE_DATAREPORT_PAGE_TIME = "space_datareport_page_time";
    //需要上报后台活跃的周期时间，现在1天报一次
    public static final long SPACE_DATAREPORT_ACTIVITY_PAGE_CYCLE=24*3600*1000;
    //记录应用是否首次扫描垃圾
    public static final String SPACE_DATAREPORT_SCANFIRST="space_datareport_scanfirst";
    public static final String SPACE_HOME="space_home";
    //记录随机生成的uuid，旧版本的uuid
    public static final String SPACE_DATAREPORT_UUID="space_datareport_uuid";
    //该变量用来存上报后的uuid，不用每次都去判断查找，影响性能
    public static String Report_UUID;
    //存第一次数据上报过的唯一标识
    public static final String SPACE_REAL_UUID="space_real_uuid";

    private static final String emptyImei="000000000000000";


    private static volatile DataReportConfigManage dataReportConfigManager;
    private SharedPreferences sharedPreference;

    public static DataReportConfigManage getInstance(){
        if (dataReportConfigManager == null){
            synchronized (DataReportConfigManage.class){
                if (dataReportConfigManager == null){
                    dataReportConfigManager = new DataReportConfigManage();
                }
            }
        }
        return dataReportConfigManager;
    }

    public SharedPreferences getSharedPreference(){
        if (sharedPreference == null){
            sharedPreference = SpaceApplication.getInstance().getContext().getSharedPreferences(SPACE_DATAREPORT_CONFIG_SP_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreference;
    }

    /**
     * 获取上一次上报后台活跃的时间
     * @return
     */
    public String getLastReportActivityTime(){
        return getSharedPreference().getString(SPACE_DATAREPORT_ACTIVITY_TIME, "");
    }

    /**
     * 保存最后一次上报后台活跃的时间
     * @param time
     */
    public void setLastReportActivityTime(String time) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putString(SPACE_DATAREPORT_ACTIVITY_TIME, time);
        SharePreferenceUtil.applyToEditor(editor);
    }

    /**
     * 获取上次上报安装的版本号
     * @return
     */
    public String getReportVersion(){
        return getSharedPreference().getString(SPACE_DATAREPORT_VERSION,"");
    }

    /**
     * 设置上次上报安装的版本号
     * @param version
     */
    public void setReportVersion(String version){
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putString(SPACE_DATAREPORT_VERSION,version);
        SharePreferenceUtil.applyToEditor(editor);
    }

    /**
     * 获取上一次安装的时间
     * @return
     */
    public long getLastReportPageTime(){
        return getSharedPreference().getLong(SPACE_DATAREPORT_PAGE_TIME, 0);
    }

    /**
     * 保存最后一次安装的时间
     * @param time
     */
    public void setLastReportPageTime(long time) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putLong(SPACE_DATAREPORT_PAGE_TIME, time);
        SharePreferenceUtil.applyToEditor(editor);
    }

    public String getFirstScan(){
        int scan=getSharedPreference().getInt(SPACE_DATAREPORT_SCANFIRST,0);
        if(scan==0){
            return "1";
        }
        return "2";
    }

    public void setFirstScan(){
        int scan=getSharedPreference().getInt(SPACE_DATAREPORT_SCANFIRST,0);
        scan++;
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putInt(SPACE_DATAREPORT_SCANFIRST,scan);
        SharePreferenceUtil.applyToEditor(editor);
    }

    public boolean getSpaceHome(){
        boolean isHome=getSharedPreference().getBoolean(SPACE_HOME,false);
        return isHome;
    }

    public boolean resetSpaceHome(boolean isHome){
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putBoolean(SPACE_HOME,isHome);
        SharePreferenceUtil.applyToEditor(editor);
        return isHome;
    }

    public String getDataUUID(){
        try{
            //sp和sd卡中的值都为空了，取IMEI号
            String imei=CommonUtil.getIMEI(SpaceApplication.getInstance().getContext());
            if(TextUtils.isEmpty(imei)){
                //如果没有获取imei号的权限，值为空，
                imei=emptyImei;
            }
            //在IMEI号取不到的时候，获取AndroidID
            String androidid= Settings.Secure.getString(SpaceApplication.getInstance().getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            if(TextUtils.isEmpty(androidid) || "9774d56d682e549c".equalsIgnoreCase(androidid)){
                //AndroidID为空或者为"9774d56d682e549c",因为有些主流设备会有重复该字符串，此时为随机生成的uuid
                String uuid = getMac();
                if(TextUtils.isEmpty(uuid)){
//                    uuid = UUID.randomUUID().toString();
//                    uuid=uuid.replaceAll("-","");
//                    //取前面16位数字，以前老的32位数据就不要了
//                    if(uuid.length()>16) uuid=uuid.substring(0,16);
                    uuid="";
                }
                androidid=uuid;
            }
            if(TextUtils.isEmpty(androidid) && emptyImei.equalsIgnoreCase(imei)){
                Report_UUID="";
            }
            else if(TextUtils.isEmpty(androidid) && !emptyImei.equalsIgnoreCase(imei)){
                Report_UUID=imei;
            }
            else{
                Report_UUID=imei+"_"+androidid;
            }
            setRealUUID(Report_UUID);
            saveSDUuid();
        }catch (Exception e){
        }
        return Report_UUID;
    }

    //检查下权限
    private boolean checkPhoneState(){
        boolean hasPermission=true;
        try {
            //检查权限
            int permissionCheck = ContextCompat.checkSelfPermission(SpaceApplication.getInstance().getContext(),
                    Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED){
                hasPermission=false;
            }
        }catch (Exception e){
            hasPermission=false;
        }
        return hasPermission;
    }

    /**
     * 这里以sd卡有效值为准，同步到sp
     * @return
     */
    public String resetUUIDFromSD(){
        try{
            //先判断下sd卡中有没值
            String sduuid=getSDUuid();
            if(!TextUtils.isEmpty(sduuid)){
                int l=sduuid.indexOf("_");
                if(l>0){
                    String imeiTemp=sduuid.substring(0,l);
                    //如果等于原来的空
                    if(imeiTemp.equalsIgnoreCase(emptyImei)){
                        getDataUUID();
                    }
                    else{
                        //以sd卡的值为主，因为之前有存过一次imei号
                        Report_UUID=sduuid;
                        setRealUUID(Report_UUID);
                    }
                }
            }
            else{
                getDataUUID();
            }
        }catch (Exception e){
        }
        return Report_UUID;
    }

    //以此sp为唯一标识
    private String getRealUUID(){
        String realuuid=getSharedPreference().getString(SPACE_REAL_UUID,"");
        return realuuid;
    }

    //保存到sp中
    private void setRealUUID(String uuid){
        if(TextUtils.isEmpty(uuid)) return;
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putString(SPACE_REAL_UUID,uuid);
        SharePreferenceUtil.applyToEditor(editor);
    }

    /**
     * 数据上报的uuid，确保唯一
     * @return
     */
    public String getInstanceId(){
        try{
            //如果变量为空
            if(TextUtils.isEmpty(Report_UUID)){
                //判断sp中是否有值
                Report_UUID=getRealUUID();
                if(TextUtils.isEmpty(Report_UUID)){
                    //判断sd卡中有没值
                    Report_UUID=getSDUuid();
                    if(TextUtils.isEmpty(Report_UUID)){
                        Report_UUID=getDataUUID();
                    }
                    else{
                        //保存数据到sp中
                        setRealUUID(Report_UUID);
                    }
                }
                else{
                    //将sp中的值备份到sd卡中
                    saveSDUuid();
                }
                //这里考虑到本来没权限，但是手动去应用打开权限
                if(!TextUtils.isEmpty(Report_UUID) && Report_UUID.startsWith(emptyImei)&& checkPhoneState()){
                    Report_UUID=getDataUUID();
                }
            }
        }catch (Exception e){
        }
        return Report_UUID;
    }

    //获取mac地址
    private String getMac() {
        String macSerial = "";
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
        }
        if(!TextUtils.isEmpty(macSerial)){
            macSerial=macSerial.replaceAll(":","");
            macSerial+="0000000000000000";
            macSerial=macSerial.substring(0,16);
        }
        return macSerial;
    }

    private void saveSDUuid(){
        try{
            File uuidFile=getDataCacheFile();
            if(uuidFile!=null && uuidFile.exists()){
                writeInstallationFile(uuidFile,Report_UUID);
            }
        }catch (Exception e){
        }
    }

    private String getSDUuid(){
        try{
            File uuidFile=getDataCacheFile();
            //如果文件存在
            if(uuidFile!= null && uuidFile.exists()){
                return readInstallationFile(uuidFile);
            }
        }catch (Exception e){
        }
        return "";
    }

    private String readInstallationFile(File uuidFile){
        //读sd卡中的文件的值
        try{
            RandomAccessFile f = new RandomAccessFile(uuidFile, "r");
            byte[] bytes = new byte[(int) f.length()];
            f.readFully(bytes);
            f.close();
            return new String(bytes);
        }catch (Exception e){
        }
        return "";
    }

    private void writeInstallationFile(File uuidFile,String uuid){
        //向sd卡中写入字符串
        try{
            if(TextUtils.isEmpty(uuid)) return;
            if(uuidFile.exists()){
                uuidFile.delete();
                uuidFile.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(uuidFile);
            out.write(uuid.getBytes());
            out.close();
        }catch (Exception e){
        }
    }

    private File getDataCacheFile(){
        String filePath = null;
        //检查权限
        int permissionCheck = ContextCompat.checkSelfPermission(SpaceApplication.getInstance().getContext(),Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            return null;
        }
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator +"CleanerSDK"+ File.separator + "uuid.txt";
        } else
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator +"CleanerSDK"+ File.separator + "uuid.txt";
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

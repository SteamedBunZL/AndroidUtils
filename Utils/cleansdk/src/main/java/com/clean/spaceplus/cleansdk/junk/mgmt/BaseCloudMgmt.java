package com.clean.spaceplus.cleansdk.junk.mgmt;

import android.content.Context;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.biz.ABizLogic;

import space.network.commondata.KCleanCloudEnv;
import space.network.commondata.KPostConfigData;
import space.network.util.KCleanCloudMiscHelper;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/25 15:06
 * @copyright TCL-MIG
 */
public abstract class BaseCloudMgmt extends ABizLogic implements CloudMgmtControl {
    private String language = "en";
    public static boolean useCleanMasterServer = false;//是否用猎豹服务器测试  目前只是方便测试 打包时要改为false

    /**
     * 目前代码发送给服务器的语言跟服务器不兼容,为了不影响前期的代码,在发送参数给服务器时,对语言做统一处理
     *
     * @return
     */
    public String getServerSupportedLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    /**
     * 获取国内服务器的接口完整url
     * @return
     */
    public String[] getQueryUrls() {
        String[] baseUrlArray = getCleanBaseUrl();
        if (baseUrlArray != null && baseUrlArray.length > 0) {
            int len = baseUrlArray.length;
            String[] result = new String[len];
            for (int i = 0; i < len; i++) {
                result[i] = baseUrlArray[i] + getAction();
            }
            return result;
        }
        return null;
    }



    /**
     * 配置一些请求的公共输入参数
     * @return
     */
    protected KPostConfigData getConfigData(){
        Context context = SpaceApplication.getInstance().getContext();
        KPostConfigData configData = new KPostConfigData();
        configData.mChannelId = KCleanCloudEnv.DEFAULT_CHANNEL_ID;
        configData.mPostDataEnCodeKey = KCleanCloudEnv.DEFAULT_CHANNEL_KEY.getBytes();
        configData.mResponseDecodeKey = KCleanCloudEnv.DEFAULT_RESPONSE_KEY.getBytes();
        configData.setLanguage(getServerSupportedLanguage());
        configData.setOthers(KCleanCloudMiscHelper.GetUuid(context),
                KCleanCloudMiscHelper.getCurrentVersion(context));
        configData.setMCC(KCleanCloudMiscHelper.getMCC(context));
        return configData;
    }
}

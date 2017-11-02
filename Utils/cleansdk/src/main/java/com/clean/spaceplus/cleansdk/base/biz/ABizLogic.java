package com.clean.spaceplus.cleansdk.base.biz;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.bean.BaseBean;
import com.clean.spaceplus.cleansdk.base.exception.TaskException;
import com.clean.spaceplus.cleansdk.setting.update.bean.UpdateResponseBean;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

/**
 * @author JerryLiu
 * @Description:
 * @date 2016/4/19 15:40
 * @copyright TCL-MIG
 */
public abstract class ABizLogic {
    private static final String TAG = ABizLogic.class.getSimpleName();
    /**
     * 清理的BaseUrl
     * 测试：http://cleanportal-test.tclclouds.com/
     * 正式：服务器反馈说待定...
     */
    private static final String CLEAN_BASE_URL_TEST = "http://cleanportal-test.tclclouds.com/";
    private static final String CLEAN_BASE_URL_TEST1 = "http://cleanportal-test.tclclouds.com/";
    private static final String CLEAN_BASE_URL_TEST2 = "http://cleanportal-test.tclclouds.com/";
    private static final String CLEAN_BASE_URL_TEST_ARRAY[] = {
            CLEAN_BASE_URL_TEST, CLEAN_BASE_URL_TEST1, CLEAN_BASE_URL_TEST2
    };

    /**
     * 国外测试地址
     */
    private static final String CLEAN_BASE_URL_TEST_ABROAD = "http://cleanportal-test.tclclouds.com/";
    private static final String CLEAN_BASE_URL_TEST1_ABROAD = "http://cleanportal-test.tclclouds.com/";
    private static final String CLEAN_BASE_URL_TEST2_ABROAD = "http://cleanportal-test.tclclouds.com/";
    private static final String CLEAN_BASE_URL_TEST_ARRAY_ABROAD[] = {
            CLEAN_BASE_URL_TEST_ABROAD, CLEAN_BASE_URL_TEST1_ABROAD, CLEAN_BASE_URL_TEST2_ABROAD
    };


    //数据获取流程：先取PRD,取不到再取PRD1,取不到数据最后再取PRD2，减少数据取不到的几率
    private static final String CLEAN_BASE_URL_PRD = "https://cleanportal.tclclouds.com/";
    private static final String CLEAN_BASE_URL_PRD1 = "https://cleanportal.tclclouds.com/";
    private static final String CLEAN_BASE_URL_PRD2 = "https://cleanportal.tclclouds.com/";
    private static final String CLEAN_BASE_URL_PRD_ARRAY[] = {
            CLEAN_BASE_URL_PRD, CLEAN_BASE_URL_PRD1, CLEAN_BASE_URL_PRD2
    };

    private static final String CLEAN_BASE_URL_PRD_ABROAD = "https://cleanportal.tclclouds.com/";
    private static final String CLEAN_BASE_URL_PRD1_ABROAD = "https://cleanportal.tclclouds.com/";
    private static final String CLEAN_BASE_URL_PRD2_ABROAD = "https://cleanportal.tclclouds.com/";
    private static final String CLEAN_BASE_URL_PRD_ARRAY_ABROAD[] = {
            CLEAN_BASE_URL_PRD_ABROAD, CLEAN_BASE_URL_PRD1_ABROAD, CLEAN_BASE_URL_PRD2_ABROAD
    };


    /**
     * 反馈的BaseUrl
     * 测试：http://feedback-test.tclclouds.com/api/
     * 正式：http://feedback.tclclouds.com/api/
     */
    private static final String FEEDBACK_BASE_URL_TEST = "http://feedback-test.tclclouds.com/api/";

    private static final String FEEDBACK_BASE_URL_PRD = "http://feedback.tclclouds.com/api/";


    /**
     * 数据上报URL
     * 不能去掉端口号
     * 20160919切换为鹰眼api
     */
    private static final String DATAREPORT_URL_OVERSEA = "https://gwrtdp.tclclouds.com/api/log HTTP/1.1";

    private static final String DATAREPORT_URL_TEST = "http://feedback.tclclouds.com/api/buriedPointUpload";

    private static final String DATAREPORT_URL_CN = "https://gwrtdp.tclclouds.com/api/log HTTP/1.1";

    /**
     * 如果子类的baseURL不一样，需要重写该方法
     * @return 返回国内的BaseUrl
     */
    public String[] getCleanBaseUrl() {
        boolean isTest = PublishVersionManager.isTest();
        boolean isCNVersion = PublishVersionManager.isCNVersion();
        //测试环境
        if ("com.clean.spaceplus".equals(SpaceApplication.getInstance().getContext().getPackageName())) { // 暂时弄成这样
            return CLEAN_BASE_URL_TEST_ARRAY;
        } else {
            if (isTest) {
                return CLEAN_BASE_URL_TEST_ARRAY;
            }
            //线上环境
            else {
                //国内版本
                if (isCNVersion) {
                    return CLEAN_BASE_URL_PRD_ARRAY;
                } else {
                    return CLEAN_BASE_URL_PRD_ARRAY_ABROAD;
                }
            }
        }
    }




    /**
     * 如果子类的baseURL不一样，需要重写该方法
     * @return 返回BaseUrl
     */
    public String getFeedbackBaseUrl() {
        if (PublishVersionManager.isTest()) {
            return FEEDBACK_BASE_URL_TEST;
        } else {
            return FEEDBACK_BASE_URL_PRD;
        }
    }

    public String getDatareportBaseUrl(){
        if (PublishVersionManager.isTest()) {
            return DATAREPORT_URL_TEST;
        }else if (PublishVersionManager.isCNVersion()){
            return DATAREPORT_URL_CN;
        } else {
            return DATAREPORT_URL_OVERSEA;
        }
    }

    /**
     * 对服务器返回的数据做一些业务异常处理
     *
     * @param t
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T checkResult(T t) throws TaskException {
        if (t instanceof BaseBean) {
            BaseBean resultBean = (BaseBean) t;
            //error
            if (!"0".equals(resultBean.code)) {
                //对某些接口服务器的msg做一些处理的时候
                if (resultBean instanceof UpdateResponseBean) {
                    throw TaskException.getnerataException(String.valueOf(resultBean.code));
                }
                //不需要做处理直接返回服务器的code和msg
                throw new TaskException(resultBean.code, resultBean.message);
            }
        }
        return t;
    }

}

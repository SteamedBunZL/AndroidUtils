package com.clean.spaceplus.cleansdk.setting.authorization.bean;

import com.clean.spaceplus.cleansdk.base.bean.BaseBean;

/**
 * @author chaohao.zhou
 * @Description: 功能授权响应Bean
 * @date 2017/7/11 15:43
 * @copyright TCL-MIG
 */
public class AuthorizationResponseBean extends BaseBean {

    public String data; // "Y" 代表授权，"N" 代表不授权

    @Override
    public String toString() {
        return "UpdateResponseBean{" +
                "data=" + data +
                "} " + super.toString();
    }
}

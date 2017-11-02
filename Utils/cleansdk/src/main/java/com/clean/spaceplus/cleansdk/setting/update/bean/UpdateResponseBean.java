package com.clean.spaceplus.cleansdk.setting.update.bean;

import com.clean.spaceplus.cleansdk.base.bean.BaseBean;

/**
 * @author Jerry
 * @Description:
 * @date 2016/4/19 16:41
 * @copyright TCL-MIG
 */
public class UpdateResponseBean extends BaseBean{
    public UpdateBean data;

    @Override
    public String toString() {
        return "UpdateResponseBean{" +
                "data=" + data +
                "} " + super.toString();
    }
}

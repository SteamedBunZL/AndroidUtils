package com.clean.spaceplus.cleansdk.base.bean;

import java.io.Serializable;

/**
 * @author Jerry
 * @Description:
 * @date 2016/4/19 14:31
 * @copyright TCL-MIG
 * 服务器接口请求返回结果的基类， 每个接口返回结果至少都有code和message这两个字段
 * 所有服务器返回的结果数据都必须继承这个BaseBean
 *
 *
 */
public class BaseBean implements Serializable {
    public String code; //结果码
    public String message; //结果码描述


    @Override
    public String toString() {
        return "BaseBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}

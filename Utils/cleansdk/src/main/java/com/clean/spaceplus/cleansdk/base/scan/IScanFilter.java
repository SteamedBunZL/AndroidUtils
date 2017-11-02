package com.clean.spaceplus.cleansdk.base.scan;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/28 20:06
 * @copyright TCL-MIG
 */
public interface IScanFilter {
    /**
     * 是否过滤
     * @param name 过滤项名字
     * @return 要过滤返回true，不过滤返回false。
     */
    public boolean isFilter(String name);
}

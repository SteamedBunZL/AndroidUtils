package com.clean.spaceplus.cleansdk.base.scan;

/**
 * @author liangni
 * @Description:扫描任务结果回调
 * @date 2016/4/22 13:43
 * @copyright TCL-MIG
 */
public interface ScanTaskCallback {
    /**
     * 回调扫描相关信息
     * @param what  IScanTask实现方定义信息类别值
     * @param arg1  参数1
     * @param arg2  参数2
     * @param obj   数据对象
     */
     void callbackMessage(int what, int arg1, int arg2, Object obj);
}

package com.clean.spaceplus.cleansdk.junk.engine.junk;

import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;

import java.util.List;

/**
 * @author liangni
 * @Description:扫描结果
 * @date 2016/4/22 11:13
 * @copyright TCL-MIG
 */
public interface JunkResult {

    /**
     * 取出指定类别的垃圾项集合
     * @return 请求类别的数据项目集合
     */
     List<BaseJunkBean> getDataList();

    /**
     * @return 取当前请求类别
     */
     JunkRequest.EM_JUNK_DATA_TYPE getDataType();

    /**
     * @return 扫描错误码
     */
     int getErrorCode();
}

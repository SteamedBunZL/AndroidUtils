package com.clean.spaceplus.cleansdk.base.clean;

import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.junk.engine.junk.RequestConfig;

import java.util.List;
import java.util.Map;

/**
 * @author liangni
 * @Description:清理请求接口
 * @date 2016/4/22 16:02
 * @copyright TCL-MIG
 */
public interface CleanRequest {
    // cleantype 0未清理 1详情中清理 2批量清理
    public static final int CLEAN_TYPE_NONE = 0;
    public static final int CLEAN_TYPE_DETAIL = 1;
    public static final int CLEAN_TYPE_ONETAP = 2;
    public static final int CLEAN_TYPE_SWIPE = 4;

    /**
     * 要清理的数据
     *
     * @return
     */
    public Map<JunkRequest.EM_JUNK_DATA_TYPE, List<BaseJunkBean>> getCleanJunkInfoList();

    /**
     * @return 请求清理回调对象
     */
    public ICleanCallback getCleanCallback();

    /**
     *
     * @return 请求清理类型
     */
    public int getCleanType();

    /**
     * 配置管理
     */
    public void setRequestConfig(RequestConfig cfg);

    /**
     * @return 获取配置
     */
    public RequestConfig getRequestConfig();

    public static interface ICleanCallback {
        /**
         * 开始清理当前对象
         *
         * @param request
         *            request对象自身
         */
        public void onCleanBegin(CleanRequest request);

        /**
         * 回传当下清理路径
         *
         * @param strPathName
         *            路径名
         */
        public void onCleaningPath(String strPathName);

        /**
         * 回传当下清理大小
         *
         * @param nSize
         *            清理大小
         */
        public void onCleanItemSize(JunkRequest.EM_JUNK_DATA_TYPE type, long nSize);

        /**
         * 清理结束
         *
         * @param obj
         *            清理结果
         */
        public void onCleanItem(BaseJunkBean obj);

        /**
         * 单个清理任务完成
         * @param msg
         */
        public void onSubCleanTaskFinish(int msg);
    }
}

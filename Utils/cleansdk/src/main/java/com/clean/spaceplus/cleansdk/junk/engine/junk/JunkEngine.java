package com.clean.spaceplus.cleansdk.junk.engine.junk;

import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;

/**
 * @author liangni
 * @Description:垃圾扫描引擎接口
 * @date 2016/4/22 10:28
 * @copyright TCL-MIG
 */
public interface JunkEngine {

     interface JunkEventCommandInterface {

         void callbackMessage(int what, int arg1, int arg2, Object obj);
    }

    /**
     * 引擎状态
     */
     enum EM_ENGINE_STATUS {
        IDLE, SCANNING, CLEANING;
    }

    /**
     * 开始异步扫描
     */
     void startScan();

    /**
     * 开始异步清理(多项)
     */
     void startClean();

    /**
     * 从数据中移除指定垃圾项(用于外部处理流程后清理数据，例如非root下系统缓存单项清理)
     * @param item 待移除项目
     */
     void removeDataItem(BaseJunkBean item);

    /**
     * 通知引擎暂停
     */
     void notifyPause();

    /**
     * 通知引擎从暂停状态恢复
     */
     void notifyResume();

    /**
     * 通知引擎取消扫描或清理工作
     */
     void notifyStop();

    /**
     * 添加扫描类别
     * @param request 扫描请求
     */
     void addScanRequest(JunkRequest request);

    /**
     * 取得引擎当前的工作状态
     * @return 引擎工作状态
     */
     EM_ENGINE_STATUS getEngineStatus();

    /**
     * 调用交互回调
     * @param cb 回调对象
     */
     void setCallback(EngineCallback cb);


    /**
     * 回调接口
     */
      interface EngineCallback {

        /**
         * 成功完成扫描或清理流程
         */
         void onSuccess();

        /**
         * 引擎出错
         * @param errorCode 错误码
         */
         void onError(int errorCode);

        /**
         * 进度条
         * @param nStep current progress step
         * @param nMaxStep max progress step
         */
         void onProgress(int nStep, int nMaxStep);
    }

    /**
     * 配置管理
     */
     void setEngineConfig(EngineConfig cfg);
}

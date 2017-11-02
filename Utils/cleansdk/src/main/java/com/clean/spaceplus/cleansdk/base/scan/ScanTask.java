package com.clean.spaceplus.cleansdk.base.scan;

import com.clean.spaceplus.cleansdk.junk.engine.junk.EngineConfig;

/**
 * @author liangni
 * @Description:扫描任务基类
 * @date 2016/4/22 13:39
 * @copyright TCL-MIG
 */
public interface ScanTask {

    /**
     * 绑定一个数据回调对象
     */
     void bindCallbackObj(ScanTaskCallback cb);

    /**
     * 扫描
     * @param ctrl 扫描流程控制对象
     * @return 成功扫描(包括控制对象通知停止)返回为true，失败返回false。
     */
     boolean scan(ScanTaskController ctrl);

    /**
     * 获取任务的字符串描述
     */
     String getTaskDesc();

    /**
     * 为方便写IScanTask
     */
    abstract class BaseStub implements ScanTask {
        protected ScanTaskCallback mCB = null;
        protected EngineConfig mEngineConfig = null;

        @Override
        public void bindCallbackObj(ScanTaskCallback cb) {
            mCB = cb;
        }

        public void setEngineConfigForCleanTasks(EngineConfig engineCfg) {
            mEngineConfig = engineCfg;
        }
    }
}

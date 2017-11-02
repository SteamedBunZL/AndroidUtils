package com.clean.spaceplus.cleansdk.base.scan;

/**
 * @author liangni
 * @Description:扫描任务控制器观察者
 * @date 2016/4/22 13:51
 * @copyright TCL-MIG
 */
public interface ScanTaskControllerObserver {
    /**
     * stop事件触发
     */
    public void stop();

    /**
     * reset事件触发
     */
    public void reset();

    /**
     * timeout事件触发
     */
    public void timeout();

    /**
     * 暂停事件触发
     * @param millis 非负数，暂停指定时长后自动resume。(若为0，表示永远暂停，直到resume()事件触发)
     */
    public void pause(long millis);

    /**
     * 恢复暂停事件触发
     */
    public void resume();
}

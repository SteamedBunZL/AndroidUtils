package com.clean.spaceplus.cleansdk.junk.view;

import java.util.List;

/**
 * @author wangtianbao
 * @Description:关联选择的父组件
 * @date 2016/6/2 19:41
 * @copyright TCL-MIG
 */
public interface LinkedCheckableGroup<T extends LinkedCheckableChild> {

    /**
     * 刷新当前选择状态
     */
    void refreshCheckStatus();

    /**
     * 全选状态
     * @return
     */
    boolean isAllChecked();

    /**
     * 部分选择状态
     * @return
     */
    boolean isPartChecked();

    /**
     * 没有一个是选中的
     * @return
     */
    boolean isNoneChecked();

    /**
     * 切换选择状态
     */
    void toggleCheck();

    List<T> getChildren();

    void addChild(T child);

    boolean hasChild();
}

package com.clean.spaceplus.cleansdk.junk.view;

/**
 * @author wangtianbao
 * @Description:关联选择的子组件
 * @date 2016/6/2 19:41
 * @copyright TCL-MIG
 */
public abstract class LinkedCheckableChild {
    LinkedCheckableGroup mParent;
    public boolean isChildChecked;

    public LinkedCheckableChild(LinkedCheckableGroup parent) {
        this.mParent = parent;
    }

    public LinkedCheckableGroup getParent() {
        return mParent;
    }

    @Deprecated
    public void toggleChildChecked() {
        this.isChildChecked = !isChildChecked;
        getParent().refreshCheckStatus();
    }
}

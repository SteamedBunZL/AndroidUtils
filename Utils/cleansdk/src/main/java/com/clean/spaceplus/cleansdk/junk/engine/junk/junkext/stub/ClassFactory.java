package com.clean.spaceplus.cleansdk.junk.engine.junk.junkext.stub;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/6 15:10
 * @copyright TCL-MIG
 */
//警告：只准添加接口，不能删除和修改参数名字
public interface ClassFactory {
    int VERSION = 1;
    int getVersion();
    JunkScanner createJunkScanner();
}

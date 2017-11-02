package com.clean.spaceplus.cleansdk.junk.engine.junk.junkext.stub;

import android.content.Context;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/6 15:08
 * @copyright TCL-MIG
 */
//警告：只准添加接口，不能删除和修改参数名字
public interface JunkScanner {
    int TYPE_STARND 	= 1;
    int TYPE_ADVANCE 	= 2;
    int VERSION_OF_JAR = 1;	// jarq包更新后update此版本号。
    void startScan(Context contex, int type, JunkScanCallback callback);
    void endScan();
}

package com.tcl.zhanglong.utils;

import com.steve.commonlib.DebugLog;

/**
 * Test 测试类
 * Created by Steve on 16/10/19.
 */

public class Test {


    /**
     * 方法可以锁传递,方法内调用方法
     */
    public void sss(){
        synchronized (this){
            ss();
        }
        DebugLog.e("=========ZL sss");
    }

    public void ss(){
        DebugLog.e("==========ZL 之前");
        synchronized (this){
            DebugLog.e("==========ZL ss");
        }
    }
}

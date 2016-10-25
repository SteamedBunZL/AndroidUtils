package com.tcl.zhanglong.utils;

import com.tcl.zhanglong.utils.Utils.DebugLog;

import java.awt.font.TextAttribute;

/**
 * Created by Steve on 16/10/19.
 */

public class Test {



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

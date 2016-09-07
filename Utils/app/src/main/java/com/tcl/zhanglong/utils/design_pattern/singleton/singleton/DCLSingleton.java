package com.tcl.zhanglong.utils.design_pattern.singleton.singleton;

/**
 * Double Check Lock Singleton
 * Created by Steve on 16/9/8.
 */
public class DCLSingleton {

    private static volatile DCLSingleton sIntance = null;

    private DCLSingleton(){}

    public static DCLSingleton getsIntance(){
        if (sIntance==null){
            synchronized (DCLSingleton.class){
                if (sIntance==null){
                    sIntance = new DCLSingleton();
                }
            }
        }
        return sIntance;
    }

}

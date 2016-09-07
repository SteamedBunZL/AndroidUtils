package com.tcl.zhanglong.utils.design_pattern.singleton.singleton;

/**
 * DCL检测有失效情况，这个是最优单例写法
 * Created by Steve on 16/9/8.
 */
public class StaticSingleton {

    private StaticSingleton(){}

    public static StaticSingleton getInstance(){
        return SingletonHolder.sIntance;
    }


    /**
     * 静态内部类
     */
    private static class SingletonHolder{
        private static final StaticSingleton sIntance = new StaticSingleton();
    }
}

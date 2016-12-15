package com.tcl.zhanglong.utils.eventbus_study;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Builder的设计模式
 * Created by Steve on 16/12/13.
 */


public class MyEventBusBuilder {

    private final static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    boolean logSubscriberExceptions = true;
    ExecutorService executorService = DEFAULT_EXECUTOR_SERVICE;

    MyEventBusBuilder(){

    }

    public MyEventBusBuilder logSubscriberExceptions(boolean logSubsriberException){
        this.logSubscriberExceptions = logSubsriberException;
        return this;
    }

    public MyEventBus build(){
        return new MyEventBus(this);
    }
}

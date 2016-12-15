package com.tcl.zhanglong.utils.eventbus_study;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Steve on 16/12/13.
 */

public class MyEventBus {


    static volatile MyEventBus defaultInstancce;

    private static final MyEventBusBuilder DEFAULT_BUILDER = new MyEventBusBuilder();

    private final ExecutorService executorService;

    private final boolean logSubscriberExceptions;


    private final MySubscriberMethodFInder subscriberMethodFinder;

    //1.单例
    public static MyEventBus getDefault(){
        if (defaultInstancce ==null){
            synchronized (MyEventBus.class){
                if (defaultInstancce ==null)
                    defaultInstancce = new MyEventBus();
            }
        }
        return defaultInstancce;
    }


    public MyEventBus(){
        this(DEFAULT_BUILDER);
    }

    //builder模式
    public static MyEventBusBuilder builder(){
        return new MyEventBusBuilder();
    }

    //2.使用builder模式
    MyEventBus(MyEventBusBuilder builder){
        executorService = builder.executorService;
        logSubscriberExceptions = builder.logSubscriberExceptions;
        subscriberMethodFinder = new MySubscriberMethodFInder();
    }



    public void register(Object subscriber){
        Class<?> subscriberClass = subscriber.getClass();
        //3.获取到订阅者方法封装,这里面向对象思想
        List<MySubscriberMethod> subscriberMethods = subscriberMethodFinder.findSubscriberMethod(subscriberClass);

        synchronized (this){
            for(MySubscriberMethod subscriberMethod:subscriberMethods){
                subscribe(subscriber,subscriberMethod);
            }
        }
    }

    private void subscribe(Object subscriber,MySubscriberMethod subscriberMethod){

    }



    public void post(){

    }



    public void unregister(Class<?> clazz){

    }


}

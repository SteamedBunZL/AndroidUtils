package com.tcl.zhanglong.utils.eventbus_study;

/**
 * Created by Steve on 16/12/13.
 */

public class MyEventBusException extends RuntimeException{

    private static final long serialVersionUID = -28123813918391313L;

    public MyEventBusException(String detailMessage){
        super(detailMessage);
    }

    public MyEventBusException(Throwable throwable){
        super(throwable);
    }

    public MyEventBusException(String detailMessage,Throwable throwable){
        super(detailMessage,throwable);
    }
}

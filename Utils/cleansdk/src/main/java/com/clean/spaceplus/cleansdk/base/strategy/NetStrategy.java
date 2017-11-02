package com.clean.spaceplus.cleansdk.base.strategy;

/**
 * @author liangni
 * @Description:
 * @date 2016/6/24 11:02
 * @copyright TCL-MIG
 */
public interface NetStrategy extends Runnable{
     void netquery(BaseStrategy bs);
     void success();
     void fail();
     public static enum StateValue {RUNNING,FINISH,START};
     StateValue getState();
     void setState(StateValue v);
}

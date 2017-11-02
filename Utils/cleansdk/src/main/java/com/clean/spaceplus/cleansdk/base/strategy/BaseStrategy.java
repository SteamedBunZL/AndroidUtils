package com.clean.spaceplus.cleansdk.base.strategy;

/**
 * @author liangni
 * @Description:
 * @date 2016/7/11 17:19
 * @copyright TCL-MIG
 */

public abstract class BaseStrategy implements NetStrategy{
    public volatile StateValue mState=StateValue.START;

    @Override
    public StateValue getState() {
        return mState;
    }

    @Override
    public void setState(StateValue v) {
        mState=v;
    }

    @Override
    public void run() {
        setState(StateValue.RUNNING);
        netquery(this);
    }
}

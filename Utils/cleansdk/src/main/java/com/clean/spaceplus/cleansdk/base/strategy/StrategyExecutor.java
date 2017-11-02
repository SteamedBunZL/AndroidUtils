package com.clean.spaceplus.cleansdk.base.strategy;


import com.hawkclean.mig.commonframework.util.ThreadMgr;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author liangni
 * @date 2016/6/24 11:14
 * @copyright TCL-MIG
 */

public class StrategyExecutor {

    private final  static String TAG=StrategyExecutor.class.getSimpleName();
    private Queue<NetStrategy> mNetStrategy=new LinkedList();
    public static enum StrategyFlag {SysCache};
    SystemCacheStrategyImpl systemCacheStrategy=new SystemCacheStrategyImpl();
    private volatile static  StrategyExecutor strategyExecutor=new StrategyExecutor();

    public static StrategyExecutor getInstance(){
        return strategyExecutor;
    }

    private StrategyExecutor() {
        init();
    }

    public void init(){
        mNetStrategy.offer(systemCacheStrategy);
    }


    public  void execAll()
    {
        for (NetStrategy netstrategy = mNetStrategy.poll(); null != netstrategy; netstrategy = mNetStrategy.poll()) {
            if (netstrategy.getState()!= NetStrategy.StateValue.RUNNING)
            {
                ThreadMgr.executeNetworkTask(netstrategy);
            }
        }
    }

    public  void execItem(StrategyFlag number)
    {
        switch (number)
        {
            case SysCache:
                if (systemCacheStrategy.getState()!=NetStrategy.StateValue.RUNNING)
                {
                    ThreadMgr.executeNetworkTask(systemCacheStrategy);
                }
                break;
        }

    }

}

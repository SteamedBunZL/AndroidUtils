package tlog.manager;


import tlog.TLogStrategy;

/**
 * Created by hui.zhu on 2016/5/13.
 */
public class TLogInfo {
    String tLog;
    TLogStrategy tLogStrategy;

    public TLogInfo(String log, TLogStrategy strategy) {
        this.tLog = log;
        this.tLogStrategy = strategy;
    }


    public String getTLog() {
        return tLog;
    }

    public void setTLog(String mTLog) {
        this.tLog = mTLog;
    }

    public TLogStrategy getTLogStrategy() {
        return tLogStrategy;
    }

    public void setTLogStrategy(TLogStrategy tLogStrategy) {
        this.tLogStrategy = tLogStrategy;
    }
}

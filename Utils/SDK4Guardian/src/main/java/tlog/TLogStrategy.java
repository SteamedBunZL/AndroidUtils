package tlog;


/**
 * 上报策略
 * Created by hui.zhu on 2016/4/27.
 */
public class TLogStrategy {
    public static final int RUN_TIME = 0;
    public static final int WIFI = 1;
    public static final int OFFLINE = 2;

    public static final int DEFAULT = RUN_TIME;


    private int mReportType;
    private int mEncrytionType = DEFAULT;


    public TLogStrategy(int reportType, int encrytionType){
            this.mReportType = reportType;
            this.mEncrytionType = encrytionType;
    }

    public int getEncrytionType() {
        return mEncrytionType;
    }

    public void setEncrytionType(int mEncrytionType) {
        this.mEncrytionType = mEncrytionType;
    }

    public int getReportType() {
        return mReportType;
    }

    public void setReportType(int mReportType) {
        this.mReportType = mReportType;
    }

    public static TLogStrategy getDefoultTLogStrategy(){
        return TlogStrategyHolder.instance;
    }

    private static class TlogStrategyHolder{
        public static final TLogStrategy instance = new TLogStrategy(RUN_TIME, EncrytionType.NONE);
    }
}

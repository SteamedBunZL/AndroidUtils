package com.clean.spaceplus.cleansdk.junk.cleancloud;

import android.content.Context;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import space.network.cleancloud.MultiTaskTimeCalculator;
import space.network.util.net.NetWorkUtil;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/12 20:13
 * @copyright TCL-MIG
 */
public abstract class KCleanCloudQueryLogic<DATA_TYPE, CALLBACK_TYPE> {
    private static final String TAG = CleanCloudQueryLogic.class.getSimpleName();

    //单次网络查询目录最大限制，防止异常情况下查询次数过多
    private static final int MAX_ONCE_NET_QUERY_COUNT = 1024;
    private static final int ONCE_NET_QUERY_COUNT = 32;
    private static final int ONCE_LOCAL_RESUL_CALLBACK_COUNT = 16;

    //////////////////////////////////////////////////////////////////////
    private Context mContext;
    private boolean mIsInited = false;

    private int mMaxOnceNetQueryCount 			= MAX_ONCE_NET_QUERY_COUNT;
    private int mOnceNetQueryCount 				= ONCE_NET_QUERY_COUNT;
    private int mOnceLocalResultCallbackCount 	= ONCE_LOCAL_RESUL_CALLBACK_COUNT;

    private CleanCloudQueryExecutor mCleanCloudQueryExecutor;
    private MultiTaskTimeCalculator mNetQueryTimeCalculator;

    //private RequestCount mPendingUserCallbackRequestCount 	= new RequestCount();
    //private RequestCount mPendingNetQueryRequestCount 		= new RequestCount();

    //////////////////////////////////////////////////////////////////////
    private long mLastQueryCompleteTime;///< 最后一次查询的完成时间
    private AtomicInteger  mNetQueryUseTime = new AtomicInteger();    ///< 网络查询总耗时

    private AtomicInteger  mTotalQueryCount = new AtomicInteger();	///< 目录查询总数
    private AtomicInteger  mNetQueryCount = new AtomicInteger();	///< 需要联网查询的目录总数
    /**
     * 网络查询批次失败数
     */
    public AtomicInteger mNetQueryFailCount = new AtomicInteger(0);
    /**在线情况下网络查询批次失败数*/
    public AtomicInteger mNetAvailQueryFailCount =new AtomicInteger(0);
    private boolean mUserBreakQuery;    ///< 查询是否被中断
    //////////////////////////////////////////////////////////////////////
    private boolean mNeedNetQuery = false;


    //需要调用方改写
    protected abstract boolean localQuery(final int queryId, Collection<DATA_TYPE> querydatas, CALLBACK_TYPE callback);

    //需要调用方改写
    protected abstract boolean netQuery(final int queryId, final Collection<DATA_TYPE> datas, final CALLBACK_TYPE callback);

    //需要调用方改写
    protected abstract boolean isNeedNetQuery(DATA_TYPE data, CALLBACK_TYPE callback);

    //需要调用方改写
    protected abstract void onGetQueryResult(
            final Collection<DATA_TYPE> datas,
            final CALLBACK_TYPE callback,
            final boolean queryComplete,
            final int queryId,
            final int dataTotalCount,
            final int dataCurrentCount);

    //需要调用方改写
    protected abstract boolean checkStop(CALLBACK_TYPE callback);

    public KCleanCloudQueryLogic(Context context) {
        mContext = context;
    }
    public KCleanCloudQueryLogic(Context context, boolean needNetQuery) {
        mContext = context;
        this.mNeedNetQuery = needNetQuery;
    }


    public boolean initialize(CleanCloudQueryExecutor cleanCloudQueryExecutor) {
        if (null == cleanCloudQueryExecutor)
            return false;

        synchronized (this) {
            if (!mIsInited) {
                mCleanCloudQueryExecutor = cleanCloudQueryExecutor;
                mIsInited = true;
            }
        }
        return true;
    }

    public void setNetQueryTimeController(MultiTaskTimeCalculator timeCalculator) {
        mNetQueryTimeCalculator = timeCalculator;
    }


    private boolean isNetQueryTimeOverThreshold() {
        boolean result = false;
        if (mNetQueryTimeCalculator != null) {
            result = mNetQueryTimeCalculator.isDurationOverThreshold();
        }
        return result;
    }

    public void unInitialize() {
        synchronized (this) {
            clearQueryStatistics();
            if (!mIsInited)
                return;

            mIsInited = false;
        }
    }

    public boolean query(
            final Collection<DATA_TYPE> querydatas,
            final CALLBACK_TYPE callback,
            final boolean pureAsync,
            final boolean forceNetQuery,
            final int queryId) {
        return query(querydatas, callback, pureAsync, forceNetQuery, false, queryId);
    }

    public boolean query(
            final Collection<DATA_TYPE> querydatas,
            final CALLBACK_TYPE callback,
            final boolean pureAsync,
            final boolean forceNetQuery,
            final boolean syncCallback,
            final int queryId) {
        boolean result = false;

        if (forceNetQuery || !pureAsync) {
            //网络查询本来就是异步的，如果forceNetQuery为真也没有必要再post一下
            result = realQuery(querydatas, callback, pureAsync, forceNetQuery, syncCallback, queryId);
        } else {
            result = mCleanCloudQueryExecutor.post(CleanCloudQueryExecutor.CALLBACK_RUNNER, new Runnable() {
                @Override
                public void run() {
                    realQuery(querydatas, callback, pureAsync, forceNetQuery, syncCallback, queryId);
                }
            });
        }
        return result;
    }

    public void clearQueryStatistics() {
        mNetQueryUseTime.set(0);
        mTotalQueryCount.set(0);
        mNetQueryCount.set(0);
        mNetQueryFailCount.set(0);
        mNetAvailQueryFailCount.set(0);
        mUserBreakQuery = false;
        mLastQueryCompleteTime = 0;
    }

    private boolean realQuery(
            final Collection<DATA_TYPE> querydatas,
            final CALLBACK_TYPE callback,
            final boolean pureAsync,
            final boolean forceNetQuery,
            final boolean syncCallback,
            final int queryId) {
        if (checkStop(callback)) {
            mUserBreakQuery = true;
            return false;
        }
        long beginTime = System.currentTimeMillis();
        int netQueryCount = 0;
        mTotalQueryCount.addAndGet(querydatas.size());
        int dataTotalCount = querydatas.size();
        AtomicInteger dataCounter = new AtomicInteger();
        LinkedList<DATA_TYPE> localDatas = null;
        LinkedList<DATA_TYPE> netQueryDatas = null;

        if (!forceNetQuery) {
            localQuery(queryId, querydatas, callback);
        }
        for (DATA_TYPE data : querydatas) {
            if (checkStop(callback)) {
                mUserBreakQuery = true;
                break;
            }
            if (!forceNetQuery && !isNeedNetQuery(data, callback)) {
                if (null == localDatas) {
                    localDatas = new LinkedList<>();
                }
                localDatas.add(data);
                //pureAsync为真也就是查询和回调是同一个线程,就没有必要分批次回调了
                //本地查询是满了16个就回调结果
                if (!pureAsync && localDatas.size() >= mOnceLocalResultCallbackCount) {
                    postQueryResult(localDatas, callback, syncCallback, queryId, dataTotalCount, dataCounter);
                    localDatas = null;
                }
            } else {
                if (null == netQueryDatas) {
                    netQueryDatas = new LinkedList<>();
                }
                /**
                 * 单次网络查询目录最大限制，防止异常情况下查询次数过多
                 * 万一真出现这个情况，就把超过限制的数量的本地扫描结果直接回调
                 * 限制网络查询的最长时间,如果超出设置的最长时间,也不进行网络查询
                 *
                 */
                if (netQueryCount < mMaxOnceNetQueryCount
                        && !isNetQueryTimeOverThreshold()) {
                    netQueryDatas.add(data);
                    ++netQueryCount;
                }else {
                    if (null == localDatas) {
                        localDatas = new LinkedList<>();
                    }
                    localDatas.add(data);
                }
                //网络查询是满了32个就回调结果
                if (netQueryDatas.size() >= mOnceNetQueryCount) {
                    /**
                     * 不在扫描的时候去执行网络查询 而是在类似锁屏 wifi打开的时候去执行网络查询 start
                     * add by xiangxiang.liu 2016/06/30 16:20
                     */
                    if (!mNeedNetQuery){
                        if (netQueryDatas != null && netQueryDatas.size() > 0){
                            netQueryDatas.clear();
                        }
                    }
                    postNetQuery(netQueryDatas, callback, queryId, dataTotalCount, dataCounter);
                    netQueryDatas = null;
                }
            }
        }

        if (checkStop(callback)) {
            if (!mUserBreakQuery) {
                mUserBreakQuery = true;
            }
            //mCleanCloudQueryExecutor.discardAllQuery();

        } else {

            /**
             * 不在扫描的时候去执行网络查询 而是在类似锁屏 wifi打开的时候去执行网络查询 start
             * add by xiangxiang.liu 2016/06/30 16:20
             */
            if (!mNeedNetQuery){
                if (netQueryDatas != null && netQueryDatas.size() > 0){
                    netQueryDatas.clear();
                }
            }

            if (netQueryDatas != null  && netQueryDatas.size() > 0) {
                postNetQuery(netQueryDatas, callback, queryId, dataTotalCount, dataCounter);
                netQueryDatas = null;
            }

            if (localDatas != null) {
                postQueryResult(localDatas, callback, syncCallback, queryId, dataTotalCount, dataCounter);
                localDatas = null;
            }
        }

        return true;
    }

    private boolean postNetQuery(
            final Collection<DATA_TYPE> datas,
            final CALLBACK_TYPE callback,
            final int queryId,
            final int dataTotalCount,
            final AtomicInteger dataCounter) {
        mNetQueryCount.addAndGet(datas.size());
        mCleanCloudQueryExecutor.post(CleanCloudQueryExecutor.NETWORK_RUNNER, new Runnable() {
            @Override
            public void run() {
                long beginTime = System.currentTimeMillis();
                boolean r = false;
                MultiTaskTimeCalculator.TimeData timeData = null;
                try {
                    if (mNetQueryTimeCalculator != null) {
                        timeData = mNetQueryTimeCalculator.taskStart();
                    }
                    r = netQuery(queryId, datas, callback);
                } finally {
                    if (mNetQueryTimeCalculator != null) {
                        mNetQueryTimeCalculator.taskEnd(timeData);
                    }
                }
                long endTime = System.currentTimeMillis();
                if (!r) {
                    boolean netAvailable = NetWorkUtil.isNetworkAvailable(mContext);
                    mNetQueryFailCount.addAndGet(datas.size());
                    if (netAvailable) {
                        mNetAvailQueryFailCount.addAndGet(datas.size());
                    }
                }
                postQueryResult(datas, callback, false, queryId, dataTotalCount, dataCounter);
                mNetQueryUseTime.addAndGet((int) (endTime - beginTime));
            }
        });
        return true;
    }




    private void postQueryResult(
            final Collection<DATA_TYPE> datas,
            final CALLBACK_TYPE callback,
            final boolean syncCallback,
            final int queryId,
            final int dataTotalCount,
            final AtomicInteger dataCounter) {
        if (checkStop(callback)) {
            //mCleanCloudQueryExecutor.discardAllQuery();
            return;
        }
        //if(datas != null){
        //   CacheLog.log("KCleanCloudQueryLogic::postQueryResult queryId=" + queryId + "  datas=" + datas.size() );
        //}

        //if (true || syncCallback) {
        if (syncCallback) {
            int dataCurrentCount = dataCounter.addAndGet(datas.size());
            boolean queryComplete = (dataCurrentCount >= dataTotalCount);
            onGetQueryResult(datas, callback, queryComplete, queryId, dataTotalCount, dataCurrentCount);
            mLastQueryCompleteTime = System.currentTimeMillis();
        } else {
            mCleanCloudQueryExecutor.post(CleanCloudQueryExecutor.CALLBACK_RUNNER, new Runnable() {
                @Override
                public void run() {
                    if (checkStop(callback)) {
                        if (!mUserBreakQuery) {
                            mUserBreakQuery = true;
                        }
                        //mCleanCloudQueryExecutor.discardAllQuery();
                    } else {
                        int dataCurrentCount = dataCounter.addAndGet(datas.size());
                        boolean queryComplete = (dataCurrentCount >= dataTotalCount);
                        onGetQueryResult(datas, callback, queryComplete, queryId, dataTotalCount, dataCurrentCount);
                    }
                    mLastQueryCompleteTime = System.currentTimeMillis();
                }
            });
        }
    }



}

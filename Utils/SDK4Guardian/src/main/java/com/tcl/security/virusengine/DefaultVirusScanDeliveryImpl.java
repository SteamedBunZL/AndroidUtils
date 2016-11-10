package com.tcl.security.virusengine;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.intel.security.vsm.ScanResult;
import com.tcl.security.virusengine.engine.FakeProgressEngine;
import com.tcl.security.virusengine.entry.ScanEntry;
import com.tcl.security.virusengine.entry.ScanInfo;
import com.tcl.security.virusengine.func_interface.DataReport;
import com.tcl.security.virusengine.func_interface.IRealTimeScanListener;
import com.tcl.security.virusengine.func_interface.IScanListener;
import com.tcl.security.virusengine.func_interface.IScanScheduleCallback;
import com.tcl.security.virusengine.func_interface.ScanResultDelivery;
import com.tcl.security.virusengine.utils.HandlerUtil;
import com.tcl.security.virusengine.utils.VirusLog;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.intel.security.vsm.sdk.internal.dt.a.i;

/**
 * Created by Steve on 2016/5/3.
 */
public class DefaultVirusScanDeliveryImpl implements ScanResultDelivery {

    private static final int MESSAGE_NUMERIC = 0x100;

    private static final int MESSAGE_SCANENTRY = 0x200;

    private static final int MESSAGE_FINISHED = 0x400;

    private static final int MESSAGE_CANCELED = 0x800;

    private static final int MESSAGE_IMMEDIATE = 0x1600;


    private static final int MSG_SCAN_REMAIN_TIME = 0X0021;

    private static final int MSG_SCAN_ANIM_ONE_SECOND = 0X0022;

    private final PostHandler mPostHandler = new PostHandler(Looper.getMainLooper());

    private int mScanningCount = 0;

    private int mTotalCount = -1;

    private int mPercent;

    private volatile boolean mCanceled = false;

    private volatile boolean mFinished = false;

    private IScanListener mListener;

    private volatile boolean mIsDeliveredCanceled = false;

    private volatile boolean mIsDeliveredFinished = false;

    private final ConcurrentHashMap<String,IRealTimeScanListener> mRealMap = new ConcurrentHashMap<>();

    public volatile boolean isVirusQuerying = false;

    private CopyOnWriteArrayList<ScanInfo> mResultList = new CopyOnWriteArrayList<>();

    private final DataReport mDataReport;

    @IntDef({
            MESSAGE_NUMERIC,
            MESSAGE_CANCELED,
            MESSAGE_FINISHED,
            MESSAGE_IMMEDIATE,
            MSG_SCAN_ANIM_ONE_SECOND,
            MSG_SCAN_REMAIN_TIME
    })
    public @interface MessageType{}

    public DefaultVirusScanDeliveryImpl(DataReport dataReport){
        mDataReport = dataReport;
    }


    public void setRealTimeListener(String virusEngine,IRealTimeScanListener listener){
        mRealMap.put(virusEngine,listener);
    }

    public void setScanListener(IScanListener listener){
        mListener = listener;
    }

    private long start;

    @Override
    public void postScanInfo(@DeliveryEvent int type, final ScanInfo info, Object... args) {
        switch (type) {
            case DELIVERY_EVENT_NUMERIC:
                synchronized (this) {
                    mTotalCount = (int) args[0];
                    VirusLog.w("This scan is start and Total num is %d", mTotalCount);
                    //通知上层扫描总数
                    mPercent = 0;
                    mPostHandler.sendMessage(HandlerUtil.obtainMessage(mPostHandler,MESSAGE_NUMERIC,mTotalCount));
                    if (mTotalCount==0){
                        mPostHandler.sendMessage(HandlerUtil.obtainMessage(mPostHandler,MESSAGE_FINISHED));
                        mFinished = true;
                    }
                }
                break;
            case DELIVERY_EVENT_ENTRY:
                synchronized (this) {
                    final ScanEntry source = (ScanEntry) args[0];
                    //记数，但只有当前tag相同才记数，即时任务不参与记数
                    if ((mCanceled)&&source.priority!= ScanEntry.REAL_TIME){
                        //这里要统计，取消扫描，已经扫描出来的结果
                        if (mIsDeliveredCanceled)
                            return;
                        //mPostHandler.obtainMessage(MESSAGE_CANCELED).sendToTarget();
                        mPostHandler.sendMessage(HandlerUtil.obtainMessage(mPostHandler,MESSAGE_CANCELED));
                        mIsDeliveredCanceled = true;
                        return;
                    }
                    //已经完成不再传输结果
                    if (mFinished&&source.priority!= ScanEntry.REAL_TIME){
                        return;
                    }
                    //处理立即任务
                    if (source.priority== ScanEntry.REAL_TIME){
                        VirusLog.e("Immediate Entry %s is scan finished",source.packageName);
                        //通知UI线程 扫描单项结束
                        mPostHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(source.virusEngine))
                                    return;
                                IRealTimeScanListener realListener = mRealMap.remove(source.virusEngine);
                                if (realListener!=null)
                                    realListener.onScanRealTimeComplete(info);
                            }
                        });
                        return;
                    }
                    if (mTotalCount == -1 || mScanningCount < mTotalCount) {
                        VirusLog.i("current app %s and current num is %d", source.packageName, mScanningCount);
                        //通知UI线程 扫描项，和当前扫描到第几项
                        mPostHandler.sendMessage(HandlerUtil.obtainMessage(mPostHandler,MESSAGE_SCANENTRY,mScanningCount,0,info));
                        mResultList.add(info);
                        mScanningCount++;
                        VirusLog.w("scan finish %s and the ScanCount:%d,mTotalCount:%d",source.packageName,mScanningCount,mTotalCount);
                        if (mTotalCount != -1 && mScanningCount == mTotalCount) {
                            mScanningCount = 0;
                            //这里扫描结束,通知页面扫描已经结束，重置标记位
                            mPostHandler.sendMessage(HandlerUtil.obtainMessage(mPostHandler,MESSAGE_FINISHED,mTotalCount));
                            mFinished = true;
                            VirusLog.e("This scan is finished and Total num is %d", mTotalCount);
                        }
                    } else {
                        throw new IllegalStateException("Unknow State. And the mTotalCount :" + mTotalCount + ",mScanningCount :" + mScanningCount);
                    }

                }
                break;

            case DELIVERY_EVENT_CANCEL:
                VirusLog.d("取消扫描");
                mCanceled = true;
                break;
            case DELIVERY_EVENT_PRE:
                synchronized (this){
                    mResultList.clear();
                    mCanceled = false;
                    mFinished = false;
                    mIsDeliveredCanceled = false;
                    mIsDeliveredFinished = false;
                    mScanningCount = 0;
                    mTotalCount = -1;
                }
                break;
            case DELIVERY_EVENT_UPLOAD:
                //去数据上报
                mDataReport.reportData(args[0]);
                break;
        }

    }


    public class PostHandler extends Handler {

        public PostHandler(Looper looper) {
            super(looper);
        }


        @Override
        public void handleMessage(Message msg) {

            @MessageType int msgType = msg.what;
            switch (msgType){
                case MESSAGE_NUMERIC://扫描总数
                    if (mListener!=null) {
                        mListener.onScanStart((int) msg.obj);
                        start = System.currentTimeMillis();
                        isVirusQuerying = true;
                    }
                    VirusLog.w("=== Total num is %d",(int) msg.obj);
                    break;

                case MESSAGE_SCANENTRY://当前扫描项
                    ScanInfo info = (ScanInfo) msg.obj;
                    if (info.state== ScanResult.CATEGORY_RISKY){
                        VirusLog.w("Find risky info %s",info);
                    }
                    int num = msg.arg1 +1;
                    int percentage = (int) (num*100/(float)mTotalCount);
                    if (mListener!=null){
                        mListener.onScanOneComplete(num,info);
                        if (percentage!=100)
                            mListener.onScanProgress(percentage);
                        VirusLog.d("scaninfo %s",info);
                    }
                    break;

                case MESSAGE_CANCELED://取消扫描
                    VirusLog.e("=== onScanCancel()");
                    //if (mListener!=null)
                    //mListener.onScanCancel();
                    isVirusQuerying = false;
                    break;

                case MESSAGE_FINISHED://扫描完成
                    if (mIsDeliveredFinished)
                        return;
                    mIsDeliveredFinished =true;
                    VirusScanQueue.getInstance().setQuerying(false);
                    IScanScheduleCallback scanScheduleCallback = VirusScanQueue.getInstance().getVirusScanScheduleCallback();
                    if (scanScheduleCallback!=null)
                        scanScheduleCallback.onScanFinished();
                    if (mListener!=null) {
                        mListener.onScanProgress(100);
                        mListener.onScanFinish(mResultList);

                    }
                    long finish = System.currentTimeMillis();
                    VirusLog.w("本次扫描用时 %f 秒",(finish - start) / 1000.0f);
                    break;

            }
        }
    }

    private final FakeProgressEngine.IFakeListener mFakeListener = new FakeProgressEngine.IFakeListener() {
        @Override
        public void onScanOneSecond(float percent) {
            //mPostHandler.obtainMessage(MSG_SCAN_ANIM_ONE_SECOND,0,0,percent).sendToTarget();
            mPostHandler.sendMessage(HandlerUtil.obtainMessage(mPostHandler,MSG_SCAN_ANIM_ONE_SECOND,0,0,percent));
        }

        @Override
        public void onScanFinish() {

        }
    };













}

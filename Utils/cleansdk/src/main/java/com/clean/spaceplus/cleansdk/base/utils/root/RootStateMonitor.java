package com.clean.spaceplus.cleansdk.base.utils.root;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.utils.monitor.MonitorManager;
import com.hawkclean.framework.log.NLog;

import java.util.HashSet;
import java.util.Set;

/**
 * @author shunyou.huang
 * @Description:Root状态监控
 * @date 2016/5/5 20:09
 * @copyright TCL-MIG
 */

public class RootStateMonitor {

    private static  final String TAG = RootStateMonitor.class.getSimpleName();

    // 状态
    public static final int STATE_QUERYING = 1;
    public static final int STATE_RESULT_DENY = 2;

    /**@note: IOverallRootStateObserver 不返回本值*/
    public static final int STATE_RESULT_ALLOW = 3;

    /**
     * 自动root成功 - IOverallRootStateObserver有效
     * */
    public static final int STATE_RESULT_ALLOW_AUTO = 4;

    /**
     * 三方授权root成功 - IOverallRootStateObserver有效
     * */
    public static final int STATE_RESULT_ALLOW_MANUAL = 5;

    static final int OBSERVER_TYPE_MANUAL = 1;
    static final int OBSERVER_TYPE_AUTO = 2;
    static final int OBSERVER_TYPE_OVERALL = 3;

    public static int TYPE_SENTRY = 0;

    /**
     * root状态变更monitor
     * */
    public static final int TYPE_ROOT_STATE_MONITOR = TYPE_SENTRY++;

    // -----------------总体状态-------------------
    /**
     * 整体看root申请状态
     * */
    public interface IOverallRootStateObserver {
        /**
         * 返回当前root申请状态，正在申请中(1)/结果拒绝(2)/自动成功(4)/三方成功(5)
         *
         * 	// 状态</p>
         public static final int STATE_QUERYING = 1; </p>
         public static final int STATE_RESULT_DENY = 2; </p>
         public static final int <strong>STATE_RESULT_ALLOW_AUTO = 4; </strong></p>
         public static final int <strong>STATE_RESULT_ALLOW_MANUAL = 5; </strong></p>

         可以用 isStateSuccess 判断
         * */
        void onStateChange(int state);
    }

    /**
     * IOverallRootStateObserver 的状态判断是否成功
     * */
    public static boolean isStateSuccess(int state) {
        if (state == STATE_RESULT_ALLOW
                || state == STATE_RESULT_ALLOW_AUTO
                || state == STATE_RESULT_ALLOW_MANUAL) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取当前整体root申请进度状态
     * */
    public int getOverallRootState() {
        int state = 0;
        synchronized (mOverallRootState) {
            state = mOverallRootState;
        }

        return state;
    }

    private void updateOverallState() {
        int pre = mOverallRootState;
        calcOervallState();

        if (pre != mOverallRootState) {
            mMonitorMgr.triggerMonitor(
                    RootStateMonitor.TYPE_ROOT_STATE_MONITOR, OBSERVER_TYPE_OVERALL, null);
        }
    }


    private void calcOervallState() {

        int curManualState = getMaunalApplyRootState();
	/* BUILD_CTRL:IF:CNVERSION */
        int curAutoState = getAutoRootState();
	/* BUILD_CTRL:ENDIF:CNVERSION */

        if (curManualState == STATE_QUERYING) {
            mOverallRootState = STATE_QUERYING;
            return ;
        }

	/* BUILD_CTRL:IF:CNVERSION */
        if (curAutoState == STATE_RESULT_ALLOW) {
            mOverallRootState = STATE_RESULT_ALLOW_AUTO;
            return ;
        }
	/* BUILD_CTRL:ENDIF:CNVERSION */

        if (curManualState == STATE_RESULT_ALLOW) {
            mOverallRootState = STATE_RESULT_ALLOW_MANUAL;
            return ;
        }

	/* BUILD_CTRL:IF:CNVERSION */
        if (curAutoState == STATE_QUERYING) {
            mOverallRootState = STATE_QUERYING;
            return ;
        }
	/* BUILD_CTRL:ENDIF:CNVERSION */

        mOverallRootState = STATE_RESULT_DENY;
    }

    public void register(IOverallRootStateObserver observer) {
	/* BUILD_CTRL:IF:CNVERSION */
        registerRTreceiver();
	/* BUILD_CTRL:ENDIF:CNVERSION */

        synchronized (mLock) {
            mOverallRootObserverSet.add(observer);
            if (observer != null) {
                observer.onStateChange(getOverallRootState());
            }
        }
    }

    public void unregister(IOverallRootStateObserver observer) {
        synchronized (mLock) {
            mOverallRootObserverSet.remove(observer);
        }

	/* BUILD_CTRL:IF:CNVERSION */
        unregisterRTreceiver();
	/* BUILD_CTRL:ENDIF:CNVERSION */
    }

    private void notifyOverallRootStateMonitor() {
        synchronized (mLock) {
            for (IOverallRootStateObserver observer : mOverallRootObserverSet) {
                if (observer != null) {
                    observer.onStateChange(getOverallRootState());
                }
            }
        }
    }

    // ------------------Manual Root ----------------

    /**
     * 手机有root，走三方授权方式状态监控
     * */
    public interface IManualApplyRootStateObserver {

        /**
         * 返回当前root申请状态，正在申请中/结果拒绝/结果允许
         *
         * 		 * 	// 状态</p>
         public static final int STATE_QUERYING = 1; </p>
         public static final int STATE_RESULT_DENY = 2; </p>
         public static final int STATE_RESULT_ALLOW = 3; </p>
         * */
        void onStateChange(int state);
    }

    public int getMaunalApplyRootState() {
        int state = 0;
        synchronized (mManualApplyRootState) {
            state = mManualApplyRootState;
        }

        return state;
    }

    /**
     * 由申请方设置状态
     * */
    public void updateManualApplyRootState(int mstate) {
        if (checkParam(mstate)) {
            mManualApplyRootState = mstate;
        }

        mMonitorMgr.triggerMonitor(
                RootStateMonitor.TYPE_ROOT_STATE_MONITOR, OBSERVER_TYPE_MANUAL, null);

        updateOverallState();
    }

    public void register(IManualApplyRootStateObserver observer) {
        synchronized (mLock) {
            mManualObserverSet.add(observer);
            if (observer != null) {
                observer.onStateChange(getMaunalApplyRootState());
            }
        }
    }

    public void unregister(IManualApplyRootStateObserver observer) {
        synchronized (mLock) {
            mManualObserverSet.remove(observer);
        }
    }

    public static RootStateMonitor getInst() {
        return ms_inst;
    }

    // -----------------------------------Auto Root ---------------

    /**
     * 自动获取root状态监控
     * */

    public interface IAutoRootStateObserver {
        /**
         *
         * 		 * 	// 状态</p>
         public static final int STATE_QUERYING = 1; </p>
         public static final int STATE_RESULT_DENY = 2; </p>
         public static final int STATE_RESULT_ALLOW = 3; </p>
         * */
        void onStateChange(int state);
    }

    /* BUILD_CTRL:IF:CNVERSION */
    public void register(IAutoRootStateObserver observer) {

        registerRTreceiver();

        synchronized (mLock) {
            mAutoRootObserverSet.add(observer);
            if (observer != null) {
                observer.onStateChange(getAutoRootState());
            }
        }
    }

    public void unregister(IAutoRootStateObserver observer) {
        synchronized (mLock) {
            mAutoRootObserverSet.remove(observer);
        }

        unregisterRTreceiver();
    }
	/* BUILD_CTRL:ENDIF:CNVERSION */

    /**
     * 更新自动root状态
     * */
	/* BUILD_CTRL:IF:CNVERSION */
    public void updateAutoRootState(int astate) {
        if (checkParam(astate)) {
            mAutoRootState = astate;
        }

        mMonitorMgr.triggerMonitor(
                RootStateMonitor.TYPE_ROOT_STATE_MONITOR, OBSERVER_TYPE_AUTO, null);

        updateOverallState();
    }

    public int getAutoRootState() {
        int state = 0;
        synchronized (mAutoRootState) {
            state = mAutoRootState;
        }

        return state;
    }

    private void notifyAutoRootStateMonitor() {
        synchronized (mLock) {
            for (IAutoRootStateObserver observer : mAutoRootObserverSet) {
                if (observer != null) {
                    observer.onStateChange(getAutoRootState());
                }
            }
        }
    }

    private void registerRTreceiver() {

        synchronized (mregisterRTreceiverCountLock) {
            ++mregisterRTreceiverCount;

            if (mregisterRTreceiver == null) {
                mregisterRTreceiver = new AutoRootReceiver();

                IntentFilter inf = new IntentFilter();
                inf.addAction(RootReceiver.ACTION_ROOT_RECEIVER);

                try {
                    SpaceApplication.getInstance().getContext().registerReceiver(mregisterRTreceiver, inf);
                } catch(Exception e) {

                }
            }
        }
    }

    private void unregisterRTreceiver() {

        synchronized (mregisterRTreceiverCountLock) {
            --mregisterRTreceiverCount;

            if (mregisterRTreceiverCount <= 0) {
                if (mregisterRTreceiver != null) {
                    try {
                        SpaceApplication.getInstance().getContext().unregisterReceiver(mregisterRTreceiver);
                    } catch (Exception e) {
                        // http://trace.cm.ijinshan.com/index/lists?thever=16&field=dumpkey&field_content=691783475&date=20141012&version=1.0.0%2810000256%29
                    }
                    mregisterRTreceiver = null;
                }
            }
        }
    }

    class AutoRootReceiver extends RootReceiver {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if (intent.getAction().equals(ACTION_ROOT_RECEIVER)) {

//    public static final int STEP_UNKNOWN = 0;
//    public static final int STEP_QUERYING = 1;
//    public static final int STEP_SUCCESS = 2;
//    public static final int STEP_FAILED = 3;

                int state = intent.getIntExtra(STATE, 0);
                NLog.d(TAG, "autoRootState = %d", state);

                int toShow = 0;
                switch (state) {
                    case 1:
                        toShow = STATE_QUERYING;
                        break;
                    case 2:
                        toShow = STATE_RESULT_ALLOW;
                        break;
                    case 3:
                        toShow = STATE_RESULT_DENY;
                        break;
                    default:
                        toShow = 0;
                        break;
                }

                if (0 < toShow) {
                    updateAutoRootState(toShow);
                }
            }
        }
    }

	/* BUILD_CTRL:ENDIF:CNVERSION */


    private boolean checkParam(int state) {
        if (STATE_QUERYING <= state
                && state <= STATE_RESULT_ALLOW) {
            return true;
        } else {
            return false;
        }
    }



    private RootStateMonitor() {

        boolean checkroot = SuExec.getInstance().checkRoot();
        checkroot = true;
        if (checkroot){
            NLog.i(TAG, " already root");
        }else {
            NLog.i(TAG, "  un root");
        }

        mManualApplyRootState = (checkroot ? STATE_RESULT_ALLOW : STATE_RESULT_DENY);
		/* BUILD_CTRL:IF:CNVERSION */
        mAutoRootState = RootReceiver.getState();
		/* BUILD_CTRL:ENDIF:CNVERSION */
        calcOervallState();

        mMonitorMgr = MonitorManager.getInstance();
        mManualObserverSet = new HashSet<IManualApplyRootStateObserver>();
        mAutoRootObserverSet = new HashSet<IAutoRootStateObserver>();
        mOverallRootObserverSet = new HashSet<IOverallRootStateObserver>();

        mMonitorMgr.addMonitor(
                RootStateMonitor.TYPE_ROOT_STATE_MONITOR,
                new MonitorManager.Monitor() {

                    @Override
                    public int monitorNotify(int type, Object param1, Object param2) {

                        if (type == RootStateMonitor.TYPE_ROOT_STATE_MONITOR) {
                            if (param1 != null) {
                                Integer t = (Integer) param1;
                                if (t == OBSERVER_TYPE_MANUAL) {
                                    notifyManualApplyRootStateMonitor();
                                } else if (t == OBSERVER_TYPE_AUTO) {
									/* BUILD_CTRL:IF:CNVERSION */
                                    notifyAutoRootStateMonitor();
									/* BUILD_CTRL:ENDIF:CNVERSION */

                                } else if (t == OBSERVER_TYPE_OVERALL) {
                                    notifyOverallRootStateMonitor();
                                }
                            }
                        }

                        return 0;
                    }

                },
                MonitorManager.PRIORITY_NORMAL);
    }



    private void notifyManualApplyRootStateMonitor() {
        synchronized (mLock) {
            for (IManualApplyRootStateObserver observer : mManualObserverSet) {
                if (observer != null) {
                    observer.onStateChange(getMaunalApplyRootState());
                }
            }
        }
    }


    private Integer mManualApplyRootState = 0;
    private Integer mAutoRootState = 0;
    private Integer mOverallRootState = 0;

    private byte[] mLock = new byte[0];
    private MonitorManager mMonitorMgr;
    private Set<IManualApplyRootStateObserver> mManualObserverSet;
    private Set<IAutoRootStateObserver> mAutoRootObserverSet;
    private Set<IOverallRootStateObserver> mOverallRootObserverSet;

    /* BUILD_CTRL:IF:CNVERSION */
    private AutoRootReceiver mregisterRTreceiver = null;
    /* BUILD_CTRL:ENDIF:CNVERSION */
    private Integer mregisterRTreceiverCount = 0;
    private Object mregisterRTreceiverCountLock = new Object();


    private static RootStateMonitor ms_inst = new RootStateMonitor();
}

package com.clean.spaceplus.cleansdk.junk.engine;

import com.clean.spaceplus.cleansdk.base.scan.ScanTaskCallback;
import com.hawkclean.framework.log.NLog;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/27 15:12
 * @copyright TCL-MIG
 */
public class ProgressControl {

    private ScanTaskCallback mCB = null;
    private final int mAddProgressMsgId;
    private int mMaxAll = 0;
    private int mControlLength = 0;
    private int mNowStepLength = 1;
    private String TAG=ProgressControl.class.getSimpleName();
    public ProgressControl(ScanTaskCallback callback, final int addProgressMsgId) {
        mCB = callback;
        mAddProgressMsgId = addProgressMsgId;
    }

//	public ProgressControl(ScanTaskCallback callback, final int addProgressMsgId, int defaultStepLegnth) {
//		mCB = callback;
//		mAddProgressMsgId = addProgressMsgId;
//		mNowStepLength = defaultStepLegnth;
//	}

    public void startControl(int maxAll, int maxControl, boolean smoothProgress) {
        NLog.i(TAG,"startControlstartControlstartControl %d",mControlLength);
        mMaxAll = maxAll;
        if (maxAll >= maxControl) {
            mControlLength = maxControl;
        }
        else {
            mControlLength = maxAll;
        }
        NLog.i(TAG,"startControl %d",mControlLength);
    }

    public void stopControl() {
        NLog.i(TAG,"stopControl %d",mControlLength);
        synchronized (this) {
            if (mControlLength > 0) {
                if (null != mCB) {
                    mCB.callbackMessage(mAddProgressMsgId, mControlLength, mMaxAll, null);
                }
            }
            mControlLength = 0;
        }
    }

    public void setStepNum(int stepNum) {
        NLog.i(TAG,"ProgressControl setStepNum %d,%d",mControlLength,stepNum);
        synchronized (this) {
            if (0 != stepNum) {
                mNowStepLength = mControlLength / stepNum;
            }
            else {
                mNowStepLength = mControlLength;
            }
        }
    }

    public void addStep() {
        synchronized (this) {
            NLog.i(TAG,"ProgressControl addStep %d",mControlLength);
            if (mControlLength > 0) {
                mControlLength -= mNowStepLength;
                if (null != mCB) {
                    mCB.callbackMessage(mAddProgressMsgId, mNowStepLength, mMaxAll, null);
                }
            }
        }
    }
}

package com.clean.spaceplus.cleansdk.boost.engine.data;

import android.content.ComponentName;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.boost.engine.process.ProcScanResult;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessInfo;
import com.clean.spaceplus.cleansdk.boost.engine.process.filter.AccountScanner;
import com.clean.spaceplus.cleansdk.boost.util.ProcessOOMHelper;
import com.clean.spaceplus.cleansdk.boost.util.ProcessWhiteListMarkHelper;

import java.util.ArrayList;

/**
 * @author zengtao.kuang
 * @Description: 与包对应的实体类
 * @date 2016/4/6 10:15
 * @copyright TCL-MIG
 */
public class ProcessModel {

    public static final int SUGGEST_UNKNOWN = 0;
    public static final int SUGGEST_UNCHECKED_DEFAULT = 1;
    public static final int SUGGEST_UNCHECKED_USER	= 2;
    public static final int SUGGEST_CHECKED_DEFAULT = 3;
    public static final int SUGGEST_CHECKED_USER = 4;

    public static final int PROCESS_USER = 2;
    public static final int PROCESS_SYS = 4;

    private long mId;
    private String mPkgName;

    private String mTitle;
    private ArrayList<Integer> mPidList = null;

    private ArrayList<Integer> mOomList = new ArrayList<Integer>();

    private boolean mNeedCheckFlexibleWhiteList = false;

    public int mType = -1; // 记住Model类型
    private int mRunningServicesCount = 0;// 正在运行的后台service的数量
    private long mRecentestlastActivityTime = 0; // 最近服务使用时间

    public int mMark = 0; // task清理时的忽略等级

    private ArrayList<ComponentName> mServiceComponentList = null;

    private KILL_LEVEL mKillLevel = KILL_LEVEL.WITHOUT_ROOT;
    private int mUid;

    final static int DEFAULT_OOM_ADJ = 20; // 纯属占坑

    private int mAppFlags;
    private long mElapsedMillsFromLastFront = 0; // 距离上次用户可见的小时数


    private String mCertMd5 = null;
    private int mVersionCode = 0;
    //是否内存异常
    private boolean isAbnormal = false;

    private boolean mIsMemoryCheckEx = false;

    // Check Account Status
    private int mAccountStatus = AccountScanner.NO_ACCOUNT_PKG;

    // only for cm_task_scan2 report
    public static final int RESULT_UNKNOWN = 0;
    public static final int RESULT_NO_CLEAN = 1;
    public static final int RESULT_CLEAN = 2;
    public static final int RESULT_ADD_WHITE = 3;
    public static final int RESULT_UNINSTALL = 4;
    public static final int RESULT_STOPPED = 5;
    public static final int RESULT_ALWAYS_HIDE = 6;

    private int mResult = RESULT_UNKNOWN;

    public static final int RESULT_FROM_ONE_KEY = 1;
    public static final int RESULT_FROM_SINGLE = 2;
    private int mResult_from = RESULT_FROM_ONE_KEY;

    private int mExtKillStrategy = ProcScanResult.STRATEGY_NORMAL;

    private int mCleanStrategy = ProcessInfo.PROC_STRATEGY_NORMAL;


    private Object mInnerObject = null;

    public boolean mIsHide = false;
    public boolean mHasMemory = false;

    private boolean mbCheck = false;
    private long mSize = 0L;

    private int mKeepReason = 0;
    private boolean mDependUid = false;
    private boolean mHasLabel = true;

    public void setHasLabel(boolean hasLabel){
        this.mHasLabel = hasLabel;
    }

    public boolean isHasLabel(){
        return mHasLabel;
    }

    public void setResult(int result, int result_from) {
        mResult = result;
        mResult_from = result_from;
    }

    public int getResult() {
        return mResult;
    }


    /**
     * @return
     * AccountScanner.ACCOUNT_LOGOUT
     * AccountScanner.ACCOUNT_LOGIN
     * AccountScanner.NO_ACCOUNT_PKG
     * */
    public int getAccoutStatus() {
        return mAccountStatus;
    }


    /**
     * 是否处在满足clearprocess.filter中值为4的名单中，且状态符合(mOOM>=9&&servicecount==0)
     * */
    public boolean isInFlexibleWhiteListState() {

        if (!mNeedCheckFlexibleWhiteList) {
            return false;
        }

        if (mAccountStatus == AccountScanner.ACCOUNT_LOGOUT) {
            return true;
        }

        if (getServicesCount() > 0) {
            return false;
        }

        int oom = getOOMADJ();
        return !(oom == DEFAULT_OOM_ADJ || oom < ProcessOOMHelper.CACHED_APP_MIN_ADJ);
    }

    public int getSuggest() {
        int suggest = SUGGEST_UNKNOWN;

        if (mbCheck) {
            if (ProcessWhiteListMarkHelper.isUserModified(mMark)) {
                suggest = SUGGEST_CHECKED_USER;
            } else {
                suggest = SUGGEST_CHECKED_DEFAULT;
            }
        } else {
            if (ProcessWhiteListMarkHelper.isUserModified(mMark)) {
                suggest = SUGGEST_UNCHECKED_USER;
            } else {
                suggest = SUGGEST_UNCHECKED_DEFAULT;
            }
        }

        return suggest;
    }


    public enum KILL_LEVEL {
        WITHOUT_ROOT, // 可以在非root的情况下kill
        WITH_ROOT, // 可以在root的情况下kill
        UNABLE // 系统常驻进程，无法kill
    }

    public ProcessModel() {
        mbCheck = false;	// 兼容旧代码
    }
    /**
     *
     * @param level
     */
    public void setKillLevel(KILL_LEVEL level) {
        mKillLevel = level;
    }
    /**
     * 很危险暴露了细节
     * @return
     */
    public KILL_LEVEL getKillLevel() {
        return mKillLevel;
    }

    public void setServicesCount(int runningServicesCount) {
        this.mRunningServicesCount = runningServicesCount;
    }

    public int getServicesCount() {
        return mRunningServicesCount;
    }

    public void setRecentestlastActivityTime(long time) {
        mRecentestlastActivityTime = time;
    }

    public long getRecentestlastActivityTime() {
        return mRecentestlastActivityTime;
    }


    public int getUid() {
        return mUid;
    }
    public void setUid(int uid) {
        this.mUid = uid;
    }
    /**
     * 是否周期杀
     * @return
     */
    public boolean isKillInBackground() {
        // 非ROOT下能杀的才会后台杀
        if (mKillLevel != KILL_LEVEL.WITHOUT_ROOT) {
            return false;
        }

        return mType != ProcessModel.PROCESS_SYS;

    }

    public void setChecked(boolean checked) {
        mbCheck = checked;
    }

    public boolean isChecked() {
        return mbCheck && !mIsHide;
    }

    public void setIgnoreMark(int mark) {
        this.mMark = mark;
    }

    /**
     * 取值见 MarkHelper.*
     * */
    public int getIgnoreMark() {
        return mMark;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getPkgName() {
        return mPkgName;
    }

    public void setPkgName(String processName) {
        this.mPkgName = processName;
    }

    public String getTitle() {
        if (TextUtils.isEmpty(mTitle)) {
            return mPkgName;
        }
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * 单位byte
     * */
    public long getMemory() {
        return mSize;
    }

    public void setMemory(long memory) {
        mSize = memory;
        this.mHasMemory = true;
    }

    public ArrayList<ComponentName> getServComponentList() {
        return mServiceComponentList;
    }

    public void addServComponent(ComponentName component) {
        if (null == mServiceComponentList) {
            mServiceComponentList = new ArrayList<ComponentName>();
        }

        if (!mServiceComponentList.contains(component))
            mServiceComponentList.add(component);
    }

    public void setNeedCheckFlexibleWhiteList(boolean state, int accountStatus) {
        mNeedCheckFlexibleWhiteList = state;
        mAccountStatus = accountStatus;
    }

    public void setNeedCheckFlexibleWhiteList(boolean state) {
        mNeedCheckFlexibleWhiteList = state;
    }

    /**
     * 要么是默认值DEFAULT_OOM_ADJ， 要么是oomadj最小值
     * */
    public int getOOMADJ() {
        synchronized (mOomList) {
            if (mOomList == null || mOomList.size() == 0) {
                return DEFAULT_OOM_ADJ;
            }

            int r = mOomList.get(0);
            for (Integer idx : mOomList) {
                if (idx < r) {
                    r = idx;
                }
            }

            return r;
        }
    }

    public void addOOM(int oom) {
        synchronized (mOomList) {
            mOomList.add(oom);
        }
    }

    public void setAppFlags(int flag) {
        mAppFlags = flag;
    }

    public int getAppFlags() {
        return mAppFlags;
    }

    public ArrayList<Integer> getPidList() {
        return mPidList;
    }

    public int getProcessCount() {
        if (mPidList != null) {
            return mPidList.size();
        } else {
            return 0;
        }
    }


    public void addPid(int pid) {
        if (null == mPidList) {
            mPidList = new ArrayList<Integer>();
        }

        if (!mPidList.contains(pid))
            mPidList.add(pid);
    }


    public String getCertMd5() {
        return mCertMd5;
    }

    public void setCertMd5(String certMd5) {
        mCertMd5 = certMd5;
    }

    public void setVersionCode(int verCode) {
        mVersionCode = verCode;
    }

    public int getVersionCode() {
        return mVersionCode;
    }

    public boolean isAbnormal() {
        return isAbnormal;
    }

    public void setAbnormal(boolean isAbnormal) {
        this.isAbnormal = isAbnormal;
    }

    /**
     * 已经跟isAbnormal做过规避
     * */
    public boolean isInMemoryCheckEx() {
        return mIsMemoryCheckEx;
    }

    /**
     * 需要跟isAbnormal做过规避：如果是内存异常状态，这个别为true
     * */
    public void setMemoryCheckEx(boolean isNeed) {
        mIsMemoryCheckEx = isNeed;
    }

    public void setExtKillStrategy(int strategy) {
        mExtKillStrategy = strategy;
    }

    public int getExtKillStrategy() {
        return mExtKillStrategy;
    }

    public void setCleanStrategy(int cleanStrategy) {
        mCleanStrategy = cleanStrategy;
    }

    public int getCleanStrategy() {
        return mCleanStrategy;
    }

    public void setKeepReasion(int reason) {
        mKeepReason = reason;
    }

    public int getKeepReason() {
        return mKeepReason;
    }

    public void setDependUid(boolean dependUid) {
        mDependUid = dependUid;
    }

    public boolean getDependUid() {
        return mDependUid;
    }

    public boolean isShowKeepReason(){
        boolean showKeepReason = false;
        if(!isInFlexibleWhiteListState() && ProcessWhiteListMarkHelper.isDefaultIgnore(getIgnoreMark())){
            showKeepReason = true;
        } else{
            showKeepReason = false;
            if (isInMemoryCheckEx()) {
                showKeepReason = true;
            } else if (getExtKillStrategy() != ProcScanResult.STRATEGY_NORMAL){
                showKeepReason = true;
            }
        }

        return showKeepReason;
    }

    @Override
    public String toString() {
        return "title:->"+getTitle()+" mark:->"+mMark+" isHide:->"+mIsHide+" hasLabel:->"+mHasLabel +" pkgName:->"+mPkgName;
    }
}

package com.clean.spaceplus.cleansdk.boost.engine.scan;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.boost.dao.ProcessWhiteListDAOHelper;
import com.clean.spaceplus.cleansdk.boost.engine.BoostEngine;
import com.clean.spaceplus.cleansdk.boost.engine.data.BoostDataManager;
import com.clean.spaceplus.cleansdk.boost.engine.data.BoostResult;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcScanResult;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessAdvInfo;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessHelper;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessInfo;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessResult;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessScanSetting;
import com.clean.spaceplus.cleansdk.boost.engine.process.filter.AccountScanner;
import com.clean.spaceplus.cleansdk.boost.engine.process.filter.ProcAdviceKeepFilter;
import com.clean.spaceplus.cleansdk.boost.engine.process.filter.ProcBaseFilter;
import com.clean.spaceplus.cleansdk.boost.engine.process.filter.ProcLastAppFilter;
import com.clean.spaceplus.cleansdk.boost.engine.process.filter.ProcNoCleanFilter;
import com.clean.spaceplus.cleansdk.boost.engine.process.filter.ProcServiceFilter;
import com.clean.spaceplus.cleansdk.boost.util.MemoryInfoHelper;
import com.clean.spaceplus.cleansdk.boost.util.ProcessManager;
import com.clean.spaceplus.cleansdk.boost.util.ProcessOOMHelper;
import com.clean.spaceplus.cleansdk.boost.util.ProcessWhiteListMarkHelper;
import com.clean.spaceplus.cleansdk.util.PackageUtils;
import com.hawkclean.framework.log.NLog;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: 进程扫描任务
 * @date 2016/4/6 10:26
 * @copyright TCL-MIG
 */
public class ProcessScanTask extends BoostScanTask<ProcessScanSetting> {
    static final int MAX_SCAN_SERVICE = 256;

	private ArrayList<ProcBaseFilter> mFilters = new ArrayList<ProcBaseFilter>();
	private int mMaxScanCount = 0;

    private boolean mGetMemory = false;

    private BoostDataManager mDataManager;

	public ProcessScanTask(Context ctx, ProcessScanSetting setting) {
		super(ctx, setting);

		ProcNoCleanFilter noCleanFilter = new ProcNoCleanFilter(ctx);
		mFilters.add(noCleanFilter);

		if (setting.mScanType != ProcessScanSetting.SCANTYPE_QUICK) {
			// for non-quick scan, we need to doFilter many cases
			ProcAdviceKeepFilter adviceKeepFilter = new ProcAdviceKeepFilter(ctx);
			ProcServiceFilter serviceFilter = new ProcServiceFilter(ctx);
//			ProcAccountFilter accountFilter = new ProcAccountFilter(ctx);//新版猎豹大师貌似没有用这个
	
			mFilters.add(adviceKeepFilter);
			mFilters.add(serviceFilter);
//			mFilters.add(accountFilter);
//			mFilters.add(socialFilter);
	
			/* control by setting*/
			if (setting.mCheckLastApp) {
				ProcLastAppFilter lastAppFilter = new ProcLastAppFilter(ctx);
				mFilters.add(lastAppFilter);
			}

		} else {
			// for quick scan
			mMaxScanCount = setting.mQuickCount;
		}


        // check setting conflict
        if (setting.isUseDataManager && !setting.mGetMemory) {
            if (PublishVersionManager.isTest())
            {
                throw new IllegalArgumentException("Using DataManager must scan memory size!!");
            }
        }

        mGetMemory = setting.mGetMemory;

        if (setting.isUseDataManager) {
            mDataManager = BoostDataManager.getInstance();
        }
	}

    @Override
    public int getType() {
        return BoostEngine.BOOST_TASK_MEM;
    }

    @Override
    public void scan(IScanTaskCallback callback) {
        if(ProcessHelper.isCleanProtect()&&isUseDataManager()){
            if (callback != null) {
                callback.onScanStart();
                callback.onScanFinish(null);
                updateDataManager(null);
            }
            return;
        }

        try {
            scanInternal(callback);
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
    }

    @Override
    public BoostResult scanSync() {
        return null;
    }

    private void scanInternal(IScanTaskCallback callback) {

		ActivityManager	am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        long start = System.currentTimeMillis();
		List<RunningAppProcessInfo> processList = ProcessManager.getCurrent().getRunningAppProcessInfo(mContext);


		if (processList == null) {
			// error handling
			if (callback != null) {
                callback.onScanStart();
                callback.onScanFinish(null);
                updateDataManager(null);
			}
            return;
		}

		if (callback != null) {
            callback.onScanStart();
		}

        ArrayList<ProcessModel> results = new ArrayList<ProcessModel>();
        ArrayList<ProcessInfo> procInfoList = createProcInfoList(processList);
        ArrayMap<String, ProcessModel> procMap = createProcModelMap(procInfoList);
        if (procMap != null && procMap.size() > 0) {
            List<ActivityManager.RunningServiceInfo> svcList = am.getRunningServices(MAX_SCAN_SERVICE);

            Collection<ProcessModel> procList = procMap.values();
            for (ProcessModel pm : procList) {
                if (!pm.mIsHide) {
                    addServiceInfo(svcList, pm);

                    if (callback != null) {
                        callback.onScanProgress(pm);
                    }
                }
            }

            results.addAll(procMap.values());
        }

        // remove all checked app
        if (ProcessHelper.isCleanProtect()&&isUseDataManager()) {
            for (int i = results.size() - 1; i >=0; i--) {
                ProcessModel pm = results.get(i);
                if(!ProcessHelper.isLastCleanAllFlag()){
                    if (pm.isChecked()) {
                        results.remove(i);
                    }
                }else{
                    results.remove(i);
                }
            }
        }

        ProcessResult procResult = new ProcessResult();
        procResult.updateData(results);

        // pre-finish callback for fast response
        if (callback != null) {
            callback.onScanPreFinish(procResult);
        }

        // heavy weight scanning
        for (ProcessModel pm : results) {
            if (!pm.mIsHide) {
                updateOptionInfo(am, pm);
            }
        }

        procResult.updatePrivateData();
        updateDataManager(procResult);

        // final callback for completed results
		if (callback != null) {
            callback.onScanFinish(procResult);
		}
        long end = System.currentTimeMillis();
        NLog.e("scanInternal",String.valueOf(end-start));
	}

    private ArrayList<ProcessInfo> createProcInfoList(List<RunningAppProcessInfo> processList) {
        ArrayList<ProcessInfo> procInfoList = new ArrayList<ProcessInfo>();
        int myUid = mContext.getApplicationInfo().uid;

        for (RunningAppProcessInfo rpi : processList) {
            if (rpi.uid == myUid) {
                continue;
            }
            ProcessInfo procInfo = new ProcessInfo();
            procInfo.mPid = rpi.pid;
            procInfo.mUID = rpi.uid;
            procInfo.mProcessName = rpi.processName;
            procInfo.mImportance = rpi.importance;
            procInfo.mOOM = ProcessOOMHelper.getProcessOOM(rpi.pid);
            if (rpi.pkgList != null) {
                procInfo.mPkgList.addAll(Arrays.asList(rpi.pkgList));
            }

            for (ProcBaseFilter filter : mFilters) {
                filter.doFilter(procInfo);
            }

            procInfoList.add(procInfo);

            if (mMaxScanCount != 0 && procInfoList.size() >= mMaxScanCount) {
                break;
            }

        }

        updateDependency(procInfoList);

        return procInfoList;
    }
	
	private void updateDependency(ArrayList<ProcessInfo> procInfoList) {
		ArrayList<ProcessInfo> checkList = new ArrayList<ProcessInfo>(procInfoList);

		for (ProcessInfo procInfo : procInfoList) {
			unionByUidAndPackage(procInfo, checkList); 
		}
	}
	
	private void unionByUidAndPackage(ProcessInfo procInfo, ArrayList<ProcessInfo> checkList) {
		boolean needCheckUid = Build.VERSION.SDK_INT < 17;
		boolean needCheckPkg = procInfo.mPkgList.size() > 0;
		
		ArrayList<ProcessInfo> unionList = new ArrayList<ProcessInfo>();
		unionList.add(procInfo);

		int count = checkList.size();
		for (int i = count - 1; i >= 0; i--) {
			ProcessInfo checkInfo = checkList.get(i);
			
			if (procInfo.mPid == checkInfo.mPid) {
				checkList.remove(i);
				continue;
			}
			
			boolean needUnion = false;

			if (needCheckUid && checkInfo.mUID == procInfo.mUID) {
				needUnion = true;
			}
			
			if (!needUnion && needCheckPkg) {
				for (String pkgName : procInfo.mPkgList) {
					for (String checkPkg : checkInfo.mPkgList) {
						if (pkgName.equals(checkPkg)) {
							needUnion = true;
							break;
						}
					}
				}
			}
			
			if (needUnion) {
				unionList.add(checkInfo);
				checkList.remove(i);
			}
		}
		
		unionCleanParameter(unionList);
	}
	
	private void unionCleanParameter(ArrayList<ProcessInfo> unionList) {
		int maxSuggest = 0;
		int maxStrategy = 0;
		
		// find max suggest and strategy
		for (ProcessInfo info : unionList) {			
			if (info.mCleanSuggest > maxSuggest) {
				maxSuggest = info.mCleanSuggest;
			}

			if (info.mCleanStrategy > maxStrategy) {
				maxStrategy = info.mCleanStrategy;
			}
		}

        if (mSetting.isCheckUidDependency) {
            for (ProcessInfo info : unionList) {
                if (info.mCleanSuggest < maxSuggest) {
                    ProcessAdvInfo adv = new ProcessAdvInfo();
                    adv.mDescription = ProcessAdvInfo.DEPEND_UID;
                    adv.mStatus = 1;
                    info.mAdvanceInfo.add(adv);
                }
                info.mCleanSuggest = maxSuggest;
                info.mCleanStrategy = maxStrategy;
            }
        } else if (maxSuggest > ProcessInfo.PROC_SUGGEST_CLEAN) {
            for (ProcessInfo info : unionList) {
                if (info.mCleanSuggest == ProcessInfo.PROC_SUGGEST_CLEAN) {
                    info.mCleanStrategy = ProcessInfo.PROC_STRATEGY_KILL;
                }
            }
        }
	}

    private ArrayMap<String, ProcessModel> createProcModelMap(final List<ProcessInfo> src) {
        if (src == null || src.size() == 0) {
            return null;
        }

        ArrayMap<String, ProcessModel> procMap = new ArrayMap<String, ProcessModel>();
        ProcessWhiteListDAOHelper processWhiteListDAOHelper = ProcessWhiteListDAOHelper.getInstance();
        processWhiteListDAOHelper.loadAllProcessWhiteList();
        for (ProcessInfo pi : src) {
            ProcessModel pm = getProcessModel(procMap, pi);
            if (pm == null) {
                continue;
            }

            int storeState = processWhiteListDAOHelper.getProcessWhiteListIgnoreLevel(pm.getPkgName());

            // White list
            if (ProcessWhiteListMarkHelper.isUserModified(storeState)) {
                // Apply user modified
                if (ProcessWhiteListMarkHelper.isUserWhiteList(storeState)) {
                    pm.mIsHide = true;
                } else {
                    pm.setCleanStrategy(pi.mCleanStrategy);
                    if (ProcessWhiteListMarkHelper.isUserUnchecked(storeState)) {
                        pm.setChecked(false);
                    } else if (ProcessWhiteListMarkHelper.isUserChecked(storeState) ||
                            ProcessWhiteListMarkHelper.isUserUnwhite(storeState)) {
                        pm.setChecked(true);
                    } else {
                        if (pi.mCleanSuggest == ProcessInfo.PROC_SUGGEST_DONT_CLEAN) {
                            pm.mIsHide = true;
                        } else {
                            pm.setChecked(pi.mCleanSuggest == ProcessInfo.PROC_SUGGEST_CLEAN);
                        }
                    }
                }
            } else {
                // Apply auto rule
                if (pi.mCleanSuggest == ProcessInfo.PROC_SUGGEST_DONT_CLEAN) {
                    pm.mIsHide = true;
                } else {
                    pm.setChecked(pi.mCleanSuggest == ProcessInfo.PROC_SUGGEST_CLEAN);
                    pm.setCleanStrategy(pi.mCleanStrategy);
                }
            }

            // Advance info
            if (pi.mAdvanceInfo != null && pi.mAdvanceInfo.size() > 0) {
                for (ProcessAdvInfo advInfo : pi.mAdvanceInfo) {
                    if (TextUtils.isEmpty(advInfo.mDescription)) {
                        continue;
                    }

                    if (advInfo.mDescription.equals(ProcessAdvInfo.ACCOUT)) {
                        if (advInfo.mStatus == AccountScanner.ACCOUNT_LOGOUT) {
                            pm.setNeedCheckFlexibleWhiteList(true, advInfo.mStatus);
                        }
                    } else if (advInfo.mDescription.equals(ProcessAdvInfo.UNUESD_SERVICE)) {
                        if (advInfo.mStatus == 1) {
                            pm.setNeedCheckFlexibleWhiteList(true);
                        }
                    } else if (advInfo.mDescription.equals(ProcessAdvInfo.ABNORMAL_MEMORY)) {
                        pm.setAbnormal(true);
                    } else if (advInfo.mDescription.equals(ProcessAdvInfo.CLOUD_CONTROL)) {
                        pm.setMemoryCheckEx(true);
                    } else if (advInfo.mDescription.equals(ProcessAdvInfo.DEFAULT_NOT_CLEAN) ||
                            advInfo.mDescription.equals(ProcessAdvInfo.ADVICE_KEEP)) {
                        storeState |= ProcessWhiteListMarkHelper.FLAG_WHITELIST;
                        if (advInfo.mDescription.equals(ProcessAdvInfo.ADVICE_KEEP)) {
                            pm.setKeepReasion(advInfo.mStatus);
                        }
                    } else if (advInfo.mDescription.equals(ProcessAdvInfo.SOCIAL_PROCESS)) {
                        pm.setExtKillStrategy(ProcScanResult.STRATEGY_KILL);
                    } else if (advInfo.mDescription.equals(ProcessAdvInfo.DEPEND_UID)) {
                        pm.setDependUid(advInfo.mStatus == 1);
                    }
                }
            }

            pm.setIgnoreMark(storeState);

        }

        return procMap;
    }

    private ProcessModel getProcessModel(ArrayMap<String, ProcessModel> currentMap, ProcessInfo pi) {
        // check parameters first
        String key = null;
        ProcessModel pm = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            if (currentMap == null ||
                    pi == null || pi.mPkgList == null || pi.mPkgList.size() <= 0) {
                return null;
            }

        }
        key = pi.mPkgList.get(0);
        pm = currentMap.get(key);

        if (pm == null) {
            // This pm is not in procMap, so we creata a new one
            pm = new ProcessModel();
            pm.setPkgName(key);
            pm.setUid(pi.mUID);
            try{
                PackageManager pmgr = mContext.getPackageManager();
                PackageInfo info = mContext.getPackageManager().getPackageInfo(key,0);

                if (info != null) {
                    ApplicationInfo appInfo = info.applicationInfo;
                    try {
                        if (appInfo != null) {
                            CharSequence labelCS = appInfo.loadLabel(pmgr);
                            if(labelCS!=null){
                                String labelStr = labelCS.toString();
                                pm.setTitle(labelStr);
                                if(labelStr.startsWith("com.")){
                                    pm.setHasLabel(false);
                                }

                            }

                        }
                        if(appInfo.labelRes==0){
                            pm.setHasLabel(false);
                        }
                    } catch (Resources.NotFoundException ex) {

                    }
                }
            }catch (PackageManager.NameNotFoundException nnfe){
                pm.setHasLabel(false);
                return null;//说明这是一个进程，不是一个应用
            }
            catch (Exception e){
               NLog.printStackTrace(e);
            }

//            pm.setTitle(LabelNameUtil.getInstance().getLabelNameOut(key, null));

            ApplicationInfo ai = PackageUtils.getApplicationInfo(mContext, key);
            if (ai != null) {
                pm.setAppFlags(ai.flags);
                pm.mType = ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? ProcessModel.PROCESS_SYS : ProcessModel.PROCESS_USER;
            }

            currentMap.put(key, pm);
        }

        // update mPid, mOOM and inner object
        pm.addPid(pi.mPid);
        pm.addOOM(pi.mOOM);

        return pm;
    }

    private void addServiceInfo(List<ActivityManager.RunningServiceInfo> svcList, ProcessModel pm) {
        if (svcList != null && pm != null) {
            int svcCount = 0;
            long lastActivityTime = 0;

            for (ActivityManager.RunningServiceInfo svc : svcList) {
                if (svc.service.getPackageName().equals(pm.getPkgName())) {
                    svcCount++;
                    lastActivityTime = svc.lastActivityTime;

                    ServiceInfo svcInfo = null;
                    try {
                        svcInfo = mContext.getPackageManager().getServiceInfo(svc.service, 0);
                    } catch (Exception e) {
                        NLog.printStackTrace(e);
                    }

                    // serviceInfo.service.
                    if (svc.foreground && svcInfo != null) {
                        if (svcInfo.exported && TextUtils.isEmpty(svcInfo.permission)) {
                            pm.addServComponent(svc.service);
                        } else {
                            pm.setKillLevel(ProcessModel.KILL_LEVEL.WITH_ROOT);
                        }
                    }

                }
            }

            pm.setServicesCount(svcCount);
            pm.setRecentestlastActivityTime(lastActivityTime);
        }
    }

    private void updateOptionInfo(ActivityManager am, ProcessModel pm) {
        if (mGetMemory) {
            long memory = MemoryInfoHelper.getProcessMemory(am, pm.getPidList());
            pm.setMemory(memory);
        }
    }

    private void updateDataManager(ProcessResult result) {
        if (mSetting.isUseDataManager) {
            mDataManager.updateResult(getType(), result);
            if(result!=null){
                ProcessHelper.updateLastScanTime();
            }
        }
    }
}

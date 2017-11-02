package com.clean.spaceplus.cleansdk.junk.engine.task;

import android.content.Context;

import com.clean.spaceplus.cleansdk.R;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskCallback;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CloudCfgDataWrapper;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.CloudCfgKey;
import com.clean.spaceplus.cleansdk.util.ResUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author shunyou.huang
 * @Description:系统固定缓存扫描任务
 * @date 2016/5/5 19:01
 * @copyright TCL-MIG
 */

public class SysFixedFileScanTask extends ScanTask.BaseStub{

    private static final String TAG = SysFixedFileScanTask.class.getSimpleName();

    public static final int SYS_FIXED_FILE_CACHE_SCAN_FINISH			= 1;	///< 扫描结束，若因超时结束，则arg1值为1；若用户过滤，则值为2；否则为0。
    public static final int SYS_FIXED_FILE_SCAN_STATUS			= 2;
    public static final int SYS_FIXED_FILE_SCAN_FOUND_ITEM		= 3;
    public static final int SYS_FIXED_FILE_SCAN_PROGRESS_START	= 4;	///< 开始进度计算，arg1值为总步数
    public static final int SYS_FIXED_FILE_SCAN_PROGRESS_STEP	= 5;	///< 进度计算，加一步。有可能多个线程同时回调
    public static final int SYS_FIXED_FILE_SCAN_START	= 6;
    public static final int SYS_FIXED_FILE_SCAN_PACKAGESTATS_ITEM = 7;

    private Context mContext;
    public static final String 	LIB_NAME 	= "kcmutil";
    private ArrayList<String> mDirNameList = new ArrayList<String>();
    private HashMap<String,String> mDirNameMap = new HashMap<String,String>();
    private long timeLine = 0;
    private boolean mFeedbackScanResultOnly = true;
//	public static final long DEFAULT_TIME_LINE = 7 * 24 * 60 * 60 * 1000;

/*	public void setTimeLine(long timeLine){
		this.timeLine = timeLine;
	}*/

    public long getTimeLine() {
        return this.timeLine;
    }
    @Override
    public boolean scan(ScanTaskController ctrl) {
        mContext = SpaceApplication.getInstance().getContext();

        mFeedbackScanResultOnly = CloudCfgDataWrapper.getCloudCfgBooleanValue(
                CloudCfgKey.JUNK_SCAN_FLAG_KEY,
                CloudCfgKey.JUNK_SCAN_SYSFIXEDFILE_FEEDBACK_ONLY_FLAG,
                false);

        boolean bRes = doScan(ctrl);
        return bRes;
    }

    @Override
    public String getTaskDesc() {
        return "SysFixedFileScanTask";
    }

    private void initData(){
        mDirNameList.add("log");
        mDirNameList.add("tombstone");
        mDirNameList.add("tombstones");
        mDirNameList.add("anr");

        mDirNameMap.put("log", ResUtil.getString(R.string.junk_tag_system_fixed_cache_item_data_log_title));
        mDirNameMap.put("tombstone", ResUtil.getString(R.string.junk_tag_system_fixed_cache_item_data_tombstone_title));
        mDirNameMap.put("tombstones", ResUtil.getString(R.string.junk_tag_system_fixed_cache_item_data_tombstones_title));
        mDirNameMap.put("anr", ResUtil.getString(R.string.junk_tag_system_fixed_cache_item_data_anr_title));

    }

    private boolean doScan(final ScanTaskController ctrl) {
        ScanTaskCallback scanCallBack = this.mCB;
        if (null != scanCallBack) {
            scanCallBack.callbackMessage(SYS_FIXED_FILE_SCAN_START, 0, 0, null);
        }

        initData();
//        boolean alreadyRoot = SuExec.getInstance().checkRoot();
//        boolean alreadyRoot = false;
//        if(alreadyRoot){
//            String libPath = null;
//            try {
//                libPath = LibLoader.getInstance().loadLibrary(LIB_NAME);
//            } catch(Exception e) {
//                NLog.e(TAG, " loadLibrary exception %s", e);
//            } catch (Error e) {
//                NLog.e(TAG, " loadLibrary error %s", e);
//            }
//            //JunkTimeTracer.ScanTracer scanTracer = new JunkTimeTracer.ScanTracer();
//            for(String name : mDirNameList){
//                if (null != ctrl && ctrl.checkStop()) {
//                    break;
//                }
//
//                String path = Environment.getDataDirectory() + "/" + name;
//
//                if (WhiteListsWrapper.isCacheWhiteListItem(":" + path)) {
//                    continue;
//                }
//
//                //scanTracer.startTracer().setUser(cm_task_time.CM_TASK_TIME_USER_JUNKSTD).setSType(cm_task_time.CM_TASK_TIME_STYPE_APK).setTag("SysFixed").setPath(path);
//                //String test_path = "data/data/com.clean.spaceplus.cleansdk/databases";
//                //path = test_path;
//                File file = new File(path);
//                if(file.exists()){
//                    int size = 0;
//                    List<File> listFile = FileUtils.getDirectoryFileList(path);
//                    if (listFile != null) {
//                        size = listFile.size();
//                    }
//                    JunkFileInfoNew junkFileInfos = new JunkFileInfoNew();
//                    for (int i = 0; i < size; i++){
//                        junkFileInfos.pathList.add(listFile.get(i).getPath());
//                    }
//                    junkFileInfos.size = FileSizeUtil.getFileSizes(new File(path));
//
//                    //JunkFileInfoNew junkFileInfos = SuExec.getInstance().enumJunkFiles(path, libPath);
//                    if(junkFileInfos != null && junkFileInfos.pathList.size() > 0){
//                        CacheInfo info = new CacheInfo(JunkRequest.EM_JUNK_DATA_TYPE.SYSFIXEDCACHE);
//                        info.setCheck(true);
//                        info.appendAllCleanTimeFileList(junkFileInfos.pathList);
//                        info.setSize(junkFileInfos.size);
//                        info.setAppName(mDirNameMap.get(name));
//                        info.setFilePath(path);
//                        info.setInfoType(CacheInfo.INFOTYPE_SYSFIXEDFIELITEM);
//                        //scanTracer.addFileCount(junkFileInfos.pathList.size()).addSize(junkFileInfos.size);
//                        if (mFeedbackScanResultOnly) {
//                            long nStartTime = SystemClock.uptimeMillis();
//                            for (String strFilePath : junkFileInfos.pathList) {
//                                JunkFileInfoNew tmp = SuExec.getInstance().enumJunkFiles(strFilePath,libPath);
//
//                                int pos = strFilePath.lastIndexOf("/");
//                                if (pos != -1) {
//                                    pos++;
//                                } else {
//                                    pos = 0;
//                                }
//
//                                long nSize = (null != tmp)?(tmp.size/1024):0;
//                                String strFileName = strFilePath.substring(pos);
//                                String strInfo = new StringBuilder("datapath=").append(strFilePath)
//                                        .append("&name=").append(strFileName)
//                                        .append("&datasize=").append(nSize)
//                                        .toString();
//                                //OpLog.d("cm_root_clean", strInfo);
//                                NLog.d(TAG, strInfo);
//                                //KInfocClientAssist.getInstance().reportData("cm_root_clean", strInfo);
//                            }
//                            //scanTracer.setCustom( "feed:"+(SystemClock.uptimeMillis()-nStartTime) );
//                        } else if (null != scanCallBack) {
//                            NLog.i(TAG, "Found Item: %s的垃圾 %s %s", info.getAppName(), FileSizeUtil.FormatFileSize(info.getSize()), info.getFilePath());
//                            scanCallBack.callbackMessage(SYS_FIXED_FILE_SCAN_FOUND_ITEM, 0, 0, info);
//                        }
//                    }
//                }
//                //scanTracer.stopTracer().report();
//            }
//        }

        if (null != scanCallBack) {
            int arg1 = (null != ctrl && ScanTaskController.TASK_CTRL_TIME_OUT == ctrl.getStatus()) ? 1 : 0;
            scanCallBack.callbackMessage(SYS_FIXED_FILE_CACHE_SCAN_FINISH, arg1, 0, null);
        }

        return true;
    }

    public void callbackMessage(int what, int arg1, int arg2, Object obj) {
        if ( null != mCB ) {
            mCB.callbackMessage(what, arg1, arg2, obj);
        }
    }
}

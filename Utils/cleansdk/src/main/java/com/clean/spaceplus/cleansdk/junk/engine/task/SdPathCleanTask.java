package com.clean.spaceplus.cleansdk.junk.engine.task;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.R;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.base.utils.system.DeviceUtil;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudFactory;
import com.clean.spaceplus.cleansdk.junk.engine.FileDeletedCounter;
import com.clean.spaceplus.cleansdk.util.Commons;
import com.clean.spaceplus.cleansdk.junk.engine.DelCallback;
import com.clean.spaceplus.cleansdk.junk.engine.RubbishFileFilter;
import com.clean.spaceplus.cleansdk.junk.engine.RubbishFileFilterImpl;
import com.clean.spaceplus.cleansdk.junk.engine.bean.APKModel;
import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.bean.CacheInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.JunkCleanItemInfo;
import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFile;
import com.clean.spaceplus.cleansdk.junk.engine.bean.SDcardRubbishResult;
import com.clean.spaceplus.cleansdk.junk.engine.junk.EngineConfig;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.junk.engine.junk.RecycleConfig;
import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean.FileType;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.clean.spaceplus.cleansdk.util.ResUtil;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import space.network.cleancloud.KResidualCloudQuery;

/**
 * @author dongdong.huang
 * @Description: sd卡清理task
 * @date 2016/5/6 14:43
 * @copyright TCL-MIG
 */
public class SdPathCleanTask extends ScanTask.BaseStub {
    public static final String TAG = SdPathCleanTask.class.getSimpleName();

    public static final int CLEAN_FINISH = 1;
    public static final int CLEAN_ITEM = 2;
    public static final int CLEAN_STATUS = 3;
    public static final int CLEAN_INFO = 4;
    public static final int RECYCLE_ITEM = 5;

    public static final int CTRL_MASK_CALC_SIZE = 0x00000001; // /< 是否在删除前计算大小
    public static final int CTRL_MASK_CLEAN_FOLDER = 0x00000002; // /<
    // 是否删除空文件夹(若否，则只删文件，留下空文件夹)
    public static final int CTRL_MASK_CLEAN_TOP_FOLDER = 0x00000004; // /<
    // 是否删除顶层文件夹(仅当CTRL_MASK_CLEAN_FOLDER被置位时有效)
    public static final int CTRL_MASK_CLEAN_FILES = 0x00000008; // /<
    // 是否删除文件(仅当CTRL_MASK_CLEAN_FOLDER被置位时有效。若本掩码为0，则跳过文件，只删指定范围内的空文件夹)

    public static final int CTRL_MASK_CLEAN_RECYCLE_FILES = 0x00000010; //是否备份文件

    public static final String ClEAN_MASTER_PATH = "cleanmaster";
    public static final String ClEAN_MASTER_CN_PATH = "cleanmaster_cn";
    public static final String RECYCLE_PATH = "recycle";
    public static final String DELETE_PATH = "delete";

    public static final int CLEAN_FLAG_VIDEO_MASK = 0x00000001;  //是否备份视频
    public static final int CLEAN_FLAG_AUDIO_MASK = 0x00000002;  //是否备份音频
    public static final int CLEAN_FLAG_IMAGE_MASK = 0x00000004;  //是否备份图片


    private static final int CLEAN_THREAD_NUM = DeviceUtil.getCpuNum();

    private int mCtrlMask = 0xFFFFFFEF;
    private CleanDataSrc mDataMgr = null;
    public static String mCMPath = (Commons.isCNVersion() ? ClEAN_MASTER_CN_PATH : ClEAN_MASTER_PATH);
    private static String defaultSdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private HashMap<String, String> mRecycleCreateFolder = new HashMap<String, String>();  //删除任务 已经建立的备份资料天
    private Map<String, Boolean> mVisibleFolderMap = new HashMap<String, Boolean>();   //缓存已找过的目录
    private Context mCtx = SpaceApplication.getInstance().getContext();

    private String mTaskName = null;
    private boolean mIsLogDisable = false;
    private RecycleConfig mRecycleConfig = null;

    private List<String> mFolderWhiteList = new ArrayList<String>();
    private List<String> mFileWhiteList = new ArrayList<String>();

    private List<String> mOnCleanFeedbackListFolder = new ArrayList<String>();
    private List<String> mOnCleanFeedbackListFile = new ArrayList<String>();

    public SdPathCleanTask(String taskName) {
        mTaskName = taskName;
    }

    public SdPathCleanTask(String taskName, boolean isLogDisable) {
        mTaskName = taskName;
        mIsLogDisable = isLogDisable;
    }

    /**
     * @param taskName
     * @param visibleFolderMap 用来做备份文件时，缓存查找过的目录使用，提升效率
     */
    public SdPathCleanTask(String taskName, Map<String, Boolean> visibleFolderMap, boolean isLogDisable) {
        mTaskName = taskName;
        mIsLogDisable = isLogDisable;
        if (visibleFolderMap != null) {
            mVisibleFolderMap = visibleFolderMap;
        }
    }

    @Override
    public boolean scan(ScanTaskController ctrl) {
        ExecutorService deleteThreadPool = null;
        CleanPathMgr cleanPathMgr = new CleanPathMgr(ctrl);
        DeleteTask task = null;

        try {
            if (null == mDataMgr) {
                return true;
            }

            deleteThreadPool = Executors.newFixedThreadPool(CLEAN_THREAD_NUM);

            assert (null != deleteThreadPool);

            while (true) {
                task = cleanPathMgr.getNext();
                if (null == task) {
                    break;
                }

                if (null != ctrl && ctrl.checkStop()) {
                    break;
                }

                deleteThreadPool.execute(task);

            }
        } finally {

            deleteThreadPool.shutdown();

            try {
                deleteThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (null != mCB) {
                mCB.callbackMessage(CLEAN_FINISH, 0, 0, null);
            }
        }

        return true;
    }

    @Override
    public String getTaskDesc() {
        return null;
    }

    public void setWhiteList(List<String> fileWhiteList, List<String> folderWhiteList) {
        mFileWhiteList = fileWhiteList;
        mFolderWhiteList = folderWhiteList;
    }

    public void setFeedbackList(List<String> feedbackFileList, List<String> feedbackFolderList) {
        mOnCleanFeedbackListFile.clear();
        mOnCleanFeedbackListFolder.clear();

        mOnCleanFeedbackListFile.addAll(feedbackFileList);
        mOnCleanFeedbackListFolder.addAll(feedbackFolderList);
    }

    public static interface CleanDataSrc {
        public String getNextCleanPath();
    }

    public int getCtrlMask() {
        return mCtrlMask;
    }

    public static abstract class CleanDataSrcBase implements CleanDataSrc {
        public abstract DelPathInfo getNextCleanPathInfo();

        @Override
        public String getNextCleanPath() {
            DelPathInfo item = getNextCleanPathInfo();
            if (null == item) {
                return null;
            }

            if (TextUtils.isEmpty(item.getFirstPath())) {
                return null;
            }

            return item.getFirstPath();
        }

    }

    public void bindCleanDataSrc(CleanDataSrc srcCallback) {
        mDataMgr = srcCallback;
    }

    public void setCtrlMask(int ctrlMask) {
        mCtrlMask = ctrlMask;
    }

    public static class DelPathInfo {
        public Integer mNewCtrlMask = null;
        public int mCleanFileFlag = 0;
        public Object mAttachInfo = null;
        public long mNeedDelSize = 0L;
        public BaseJunkBean.FileType mFileType = FileType.Unknown;
        public List<String> mPathList = new ArrayList<String>();

        public String getFirstPath() {
            if (!mPathList.isEmpty()) {
                return mPathList.get(0);
            }
            return null;
        }

        public DelPathInfo(String path, Object attachInfo) {
            mPathList.add(path);
            mAttachInfo = attachInfo;
        }

        public DelPathInfo(String path, Object attachInfo, int cleanFileFlag) {
            mPathList.add(path);
            mAttachInfo = attachInfo;
            mCleanFileFlag = cleanFileFlag;
        }

        public DelPathInfo(String path, Object attachInfo, int cleanFileFlag, int mask) {
            mPathList.add(path);
            mAttachInfo = attachInfo;
            mCleanFileFlag = cleanFileFlag;
            mNewCtrlMask = mask;
        }

        public DelPathInfo(int mask, String path, Object attachInfo) {
            mPathList.add(path);
            mNewCtrlMask = mask;
            mAttachInfo = attachInfo;
        }

        public DelPathInfo(int mask, String path, Object attachInfo, int cleanFileFlag) {
            mPathList.add(path);
            mNewCtrlMask = mask;
            mAttachInfo = attachInfo;
            mCleanFileFlag = cleanFileFlag;
        }

        public DelPathInfo(String path, Object attachInfo, int cleanFileFlag, FileType fileType, long needDelSize) {
            mPathList.add(path);
            mAttachInfo = attachInfo;
            mCleanFileFlag = cleanFileFlag;
            mFileType = fileType;
            mNeedDelSize = needDelSize;
        }

        public DelPathInfo(List<String> pathList, Object attachInfo, int cleanFileFlag, FileType fileType, long
                needDelSize) {
            mPathList = pathList;
            mAttachInfo = attachInfo;
            mCleanFileFlag = cleanFileFlag;
            mFileType = fileType;
            mNeedDelSize = needDelSize;
        }

        public DelPathInfo(int mask, String path, Object attachInfo, int cleanFileFlag, FileType fileType, long
                needDelSize) {
            mPathList.add(path);
            mAttachInfo = attachInfo;
            mCleanFileFlag = cleanFileFlag;
            mNewCtrlMask = mask;
            mFileType = fileType;
            mNeedDelSize = needDelSize;
        }

        public DelPathInfo(int mask, List<String> pathList, Object attachInfo, int cleanFileFlag, FileType fileType,
                           long needDelSize) {
            mPathList = pathList;
            mAttachInfo = attachInfo;
            mCleanFileFlag = cleanFileFlag;
            mNewCtrlMask = mask;
            mFileType = fileType;
            mNeedDelSize = needDelSize;
        }

        public DelPathInfo(int mask, String path) {
            mPathList.add(path);
            mNewCtrlMask = mask;
            mAttachInfo = null;
        }
    }

    private class CleanPathMgr {

        CleanDataSrcBase mNewDataMgr = (mDataMgr instanceof CleanDataSrcBase ? (CleanDataSrcBase) mDataMgr : null);

        private ScanTaskController mCtrl;
        Queue<List<DelPathInfo>> mSortDelPathInfo = new LinkedList<List<DelPathInfo>>();
//		HashMap<String, List> mPathInfoList = new HashMap<String, List>();

        public CleanPathMgr(ScanTaskController ctrl) {
            mCtrl = ctrl;
            sortDelPathInfo();
        }

        private void sortDelPathInfo() {
            ArrayList<DelPathInfo> tmpPaths = new ArrayList<DelPathInfo>();
            DelPathInfo delPathInfo;
            delPathInfo = getNextPathInfoFromCleanDataSrc();
            for (; delPathInfo != null; ) {
                tmpPaths.add(delPathInfo);
                delPathInfo = getNextPathInfoFromCleanDataSrc();
            }

            Collections.sort(tmpPaths, new Comparator<DelPathInfo>() {

                @Override
                public int compare(DelPathInfo lhs, DelPathInfo rhs) {
                    String lPath = lhs.getFirstPath();
                    String rPath = rhs.getFirstPath();
                    if (TextUtils.isEmpty(lPath)) {
                        return -1;
                    } else if (TextUtils.isEmpty(rPath)) {
                        return 1;
                    }
                    return lPath.compareTo(rPath);
                }
            });

            List<DelPathInfo> delPathInfoList = null;
            boolean shouldAdd;
            String parentFolder = null;
            String parentSlash = null;
            for (DelPathInfo pathInfo : tmpPaths) {
                shouldAdd = false;
                String mPath = pathInfo.getFirstPath();
                if (parentFolder == null) {
                    if (mPath != null) {
                        shouldAdd = true;
                    }
                } else {
                    String nPath = mPath;
                    if ((nPath.startsWith(parentSlash)) && ((delPathInfoList != null))) {
                        delPathInfoList.add(pathInfo);
                    } else {
                        shouldAdd = true;
                    }
                }

                if (shouldAdd) {
                    parentFolder = mPath;
                    parentFolder = FileUtils.removeSlash(parentFolder);
                    parentSlash = FileUtils.addSlash(parentFolder);
                    delPathInfoList = new ArrayList<DelPathInfo>();
                    delPathInfoList.add(pathInfo);
                    mSortDelPathInfo.add(delPathInfoList);
                }
            }
        }

        private DelPathInfo getNextPathInfoFromCleanDataSrc() {
            String delPath = null;
            DelPathInfo delPathInfo = null;
            if (null != mNewDataMgr) {
                delPathInfo = mNewDataMgr.getNextCleanPathInfo();
                if (null == delPathInfo) {
                    return null;
                }
                if (delPathInfo.mPathList.isEmpty()) {
                    return null;
                }
                if (null == delPathInfo.mNewCtrlMask) {
                    delPathInfo.mNewCtrlMask = mCtrlMask;
                }
                if (0 == (CTRL_MASK_CLEAN_RECYCLE_FILES & delPathInfo.mNewCtrlMask)) {
                    delPathInfo.mCleanFileFlag = 0;
                }
            } else {
                delPath = mDataMgr.getNextCleanPath();
                if (null == delPath) {
                    return null;
                }
                delPathInfo = new DelPathInfo(delPath, null, 0, mCtrlMask);
            }
            return delPathInfo;
        }

        public DeleteTask getNext() {

            List<DelPathInfo> deleteTaskPathInfo;
            if (!mSortDelPathInfo.isEmpty()) {
                deleteTaskPathInfo = mSortDelPathInfo.poll();
                if (null != deleteTaskPathInfo) {
                    return new DeleteTask(deleteTaskPathInfo, mCtrl);
                }
            }
            return null;
        }
    }

    private class DeleteTask implements Runnable {
        private List<String> mPathList;
        private int mMask;
        private Object mAttachInfo;
        private ScanTaskController mCtrl;
        private List<DelPathInfo> mDelPathInfoList;
        //		private int mCleanFileFlag;
        private JunkCleanItemInfo info;

        public DeleteTask(List<DelPathInfo> delPathInfoList, ScanTaskController ctrl) {
            mCtrl = ctrl;
            mDelPathInfoList = delPathInfoList;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void run() {

            KResidualCloudQuery.FileChecker fileChecker = CleanCloudFactory.createResidualCloudQuery(false)
                    .getFileChecker();
            RubbishFileFilterImpl fileDelFilter = new RubbishFileFilterImpl();
            if (fileChecker != null && fileDelFilter != null) {
                fileDelFilter.setFilterInterface(fileChecker);
            }
            for (DelPathInfo delPathInfo : mDelPathInfoList) {
                FileDeletedCounter fileDeletedCounter = new FileDeletedCounter();
                mPathList = delPathInfo.mPathList;
                if (mPathList.isEmpty()) {
                    continue;
                }

                mMask = delPathInfo.mNewCtrlMask;
                boolean success = false;
                mAttachInfo = delPathInfo.mAttachInfo;

                if (null != mCtrl && mCtrl.checkStop()) {
                    return;
                }

                int nCleanTime = 0;
                DelCallbackImpl delCallback = new DelCallbackImpl();
                List<String> folderWhiteList = new ArrayList<String>();
                List<String> fileWhiteList = new ArrayList<String>();
                folderWhiteList.addAll(mFolderWhiteList);
                fileWhiteList.addAll(mFileWhiteList);
                boolean isNeedRecycle = false;
                boolean isEmptyFolder = false;
                if (mAttachInfo instanceof JunkCleanItemInfo) {
                    nCleanTime = ((JunkCleanItemInfo) mAttachInfo).getCleanTime();
                    delCallback.setDelFileTimeLimit(nCleanTime);

                    BaseJunkBean info = ((JunkCleanItemInfo) mAttachInfo).getJunkItem();
                    if (null != info && info instanceof SDcardRubbishResult) {
                        SDcardRubbishResult tmpRubbish = (SDcardRubbishResult) info;
                        isEmptyFolder = ResUtil.getString(R.string.junk_tag_RF_EmptyFolders).equals(tmpRubbish
                                .getChineseName());
                        if (null != tmpRubbish.getFilterSubFolderList()) {
                            folderWhiteList.addAll(tmpRubbish.getFilterSubFolderList());
                            fileWhiteList.addAll(tmpRubbish.getFilterSubFolderList());
                        }
                        if (fileDelFilter.setFilterData(tmpRubbish.getRubbishFilterData())) {
                            fileDelFilter.setRubbishCleanTime(tmpRubbish.getRubbishCleanTime());
                            delCallback.setDelFileTimeLimit(tmpRubbish.getRubbishCleanTime());
                            delCallback.setRubbishDelFilterCallBack(fileDelFilter);
                        }
                    }

                    if (info != null && info instanceof CacheInfo) {
                        CacheInfo cacheInfo = (CacheInfo) info;
                        isNeedRecycle = cacheInfo.isPicRecycleType();
                        ///> 避免未被勾选的APK在SD缓存中被误删除
                        if (mEngineConfig != null && cacheInfo.getJunkDataType() == JunkRequest.EM_JUNK_DATA_TYPE
                                .SDCACHE) {
                            List<BaseJunkBean> list = mEngineConfig.getRestCleanList(EngineConfig
                                    .ENG_CFG_ID_REST_CLEAN_ITEM_LIST);
                            if (list != null && !list.isEmpty()) {
                                for (BaseJunkBean junkInfoBase : list) {
                                    APKModel apkModel = (APKModel) junkInfoBase;
                                    fileWhiteList.add(apkModel.getPath());
                                }
                            }
                        }
                    }
                }

                if (mAttachInfo instanceof MediaFile) {
                    MediaFile mediaFile = (MediaFile) mAttachInfo;
                    if (mediaFile.getMediaType() == MediaFile.MEDIA_TYPE_IMAGE) {
                        isNeedRecycle = true;
                    }
                }

                delCallback.setEnableFlags(DelCallback.DISABLE_WRITE_LOG, mIsLogDisable); //control from PathCleanTask
                delCallback.setEnableFlags(DelCallback.ENABLE_AFTER_DELETE, true); //control from PathCleanTask

                //TODO : change to PathCleanTask parameter
                delCallback.setFolderWhiteList(folderWhiteList);
                delCallback.setFileWhiteList(fileWhiteList);

                delCallback.setFeedbackFolderList(mOnCleanFeedbackListFolder);
                delCallback.setFeedbackFileList(mOnCleanFeedbackListFile);

                //Reduce callback times
                boolean isCallbackItem = true;
                if (null != mCB) {
                    if (mAttachInfo instanceof JunkCleanItemInfo) {
                        info = (JunkCleanItemInfo) mAttachInfo;
                        BaseJunkBean jib = info.getJunkItem();
                        delCallback.setType(jib.getJunkDataType());

                        if (jib instanceof SDcardRubbishResult) {
                            delCallback.setSign(((SDcardRubbishResult) jib).getSignId());
                        } else if (jib instanceof CacheInfo) {
                            delCallback.setSign(((CacheInfo) jib).getCacheId());
                        }
                        if (info.getIsSubItem()) {
                            isCallbackItem = false;
                        }
                    }
                    if (isCallbackItem) {
                        mCB.callbackMessage(CLEAN_ITEM, 0, 0, new DelPathResult(mMask, mPathList.get(0), mAttachInfo,
                                delPathInfo.mNeedDelSize, 0, 0, 0, 0, 0));
                    }
                }

                switch (mMask & (CTRL_MASK_CLEAN_FOLDER | CTRL_MASK_CLEAN_TOP_FOLDER | CTRL_MASK_CLEAN_FILES)) {
                    case (CTRL_MASK_CLEAN_FILES): //Only Clean File
                        delCallback.setDelFlags(DelCallback.DELETE_ONLY_FILE, true);
                        break;
                    case (CTRL_MASK_CLEAN_FOLDER | CTRL_MASK_CLEAN_TOP_FOLDER):  //EmptyFolder
                        delCallback.setDelFlags(DelCallback.DELETE_ONLY_EMPTY_FOLDER, true);
                        break;
                }

                //so加载失败，无法使用本地方法删除文件  // 注意，如果使用了so方式删除文件，请注意要记录删除文件的类型 by：chaohao.zhou 2016-7-19
//                success = FileUtils.deleteFileOrFolderWithConfig(mPathList, delCallback, mEngineConfig,
// isNeedRecycle);
                success = false;

                if (!success) { //Load so file fail using Java Function to replace the delete Function
                    for (String mPath : mPathList) {
                        File pathHander = new File(mPath);
                        NLog.d(TAG, "DeleteTask run deleteFolder path = %s", pathHander.getAbsolutePath());
                        switch (mMask & (CTRL_MASK_CLEAN_FOLDER | CTRL_MASK_CLEAN_TOP_FOLDER | CTRL_MASK_CLEAN_FILES)) {
                            case (CTRL_MASK_CLEAN_FOLDER | CTRL_MASK_CLEAN_TOP_FOLDER | CTRL_MASK_CLEAN_FILES):
                                //clear All Folders
                                FileUtils.deleteFolder(folderWhiteList, pathHander, null, nCleanTime,
                                        fileDeletedCounter);
                                break;
                            case (CTRL_MASK_CLEAN_FILES): //Only Clean File
                                FileUtils.deleteFile(fileWhiteList, pathHander, null, nCleanTime, fileDeletedCounter);
                                break;
                            case (CTRL_MASK_CLEAN_FOLDER | CTRL_MASK_CLEAN_TOP_FOLDER):  //EmptyFolder
                                boolean isEmpty = FileUtils.isEmptyFolder(pathHander);
                                if (isEmpty) {
                                    FileUtils.deleteFolder(folderWhiteList, pathHander, null, nCleanTime,
                                            fileDeletedCounter);
                                }
                                break;
                        }
                    }
                }

//                if (null != delCallback && null != mCB) {
//                    if (isCallbackItem) {
//                        mCB.callbackMessage(CLEAN_INFO, 0, 0,
//                                new DelPathResult(mMask,  mPathList.get(0), mAttachInfo, delPathInfo.mNeedDelSize,
//                                        delCallback.getDeletedFolderCount(),
//                                        delCallback.getDeletedFileCount(),
//                                        delCallback.getDeletedImageCount(),
//                                        delCallback.getDeletedVideoCount(),
//                                        delCallback.getDeletedAudioCount()
//                                ));
//                    }
//                }
                if (null != delCallback && null != mCB) {
                    if (!isEmptyFolder || !info.getIsSubItem()) {
                        mCB.callbackMessage(CLEAN_INFO, 0, 0, new DelPathResult(mMask, mPathList.get(0), mAttachInfo,
                                delPathInfo.mNeedDelSize, 0, 0, fileDeletedCounter.mImageCount, fileDeletedCounter
                                .mVideoCount, fileDeletedCounter.mAudioCount, fileDeletedCounter.mFileDeletedList,
                                fileDeletedCounter.mFileDeletedSize, mPathList));
                    }
                }
            }
        }
    }

    private class DelCallbackImpl implements DelCallback {
        private int mFolderCount = 0;
        private int mFileCount = 0;
        private int mImageCount = 0;
        private int mVideoCount = 0;
        private int mAudioCount = 0;
        private int mEnableFlags = 0;
        private int mFileTimeLimit = 0;
        private int mDelFlags = 0;
        private JunkRequest.EM_JUNK_DATA_TYPE mType = JunkRequest.EM_JUNK_DATA_TYPE.UNKNOWN;
        private int mSign = 0;
        private RubbishFileFilter mRubbishFilter = null;

        private List<String> mFolderWhiteList = new ArrayList<String>();
        private List<String> mFileWhiteList = new ArrayList<String>();

        private List<String> mOnCleanFeedbackListFolder = new ArrayList<String>();
        private List<String> mOnCleanFeedbackListFile = new ArrayList<String>();

        public void setEnableFlags(int enableType, boolean enableValue) {
            if (enableValue) {
                mEnableFlags |= (enableType);
            } else {
                mEnableFlags &= ~(enableType);
            }
        }

        public void setRubbishDelFilterCallBack(RubbishFileFilter iFilter) {
            mRubbishFilter = iFilter;
        }

        @Override
        public int getEnableFlags() {
            return mEnableFlags;
        }

        public void setDelFlags(int enableType, boolean enableValue) {
            if (enableValue) {
                mDelFlags |= (enableType);
            } else {
                mDelFlags &= ~(enableType);
            }
        }

        @Override
        public int getDelFlags() {
            return mDelFlags;
        }

        public int getDeletedFolderCount() {
            return mFolderCount;
        }

        public int getDeletedFileCount() {
            return mFileCount;
        }

        public int getDeletedImageCount() {
            return mImageCount;
        }

        public int getDeletedVideoCount() {
            return mVideoCount;
        }

        public int getDeletedAudioCount() {
            return mAudioCount;
        }

        public void setDelFileTimeLimit(int fileTimeLimit) {
            mFileTimeLimit = fileTimeLimit;
        }

        @Override
        public int getDelFileTimeLimit() {
            return mFileTimeLimit;
        }

        public void setFileWhiteList(List<String> whiteList) {
            mFileWhiteList = whiteList;
        }

        @Override
        public List<String> getFileWhiteList() {
            return mFileWhiteList;
        }

        public void setFolderWhiteList(List<String> whiteList) {
            mFolderWhiteList = whiteList;
        }

        @Override
        public List<String> getFolderWhiteList() {
            return mFolderWhiteList;
        }

        public void setFeedbackFolderList(List<String> feedbackList) {
            mOnCleanFeedbackListFolder.addAll(feedbackList);
        }

        public void setFeedbackFileList(List<String> feedbackList) {
            mOnCleanFeedbackListFile.addAll(feedbackList);
        }

        @Override
        public List<String> getFeedbackFolderList() {
            return mOnCleanFeedbackListFolder;
        }

        @Override
        public List<String> getFeedbackFileList() {
            return mOnCleanFeedbackListFile;
        }

        @Override
        public void onDeleteFile(String strFileName, long type) {
            if (null != mCB) {
                if (type == 0L) {
                    mCB.callbackMessage(CLEAN_STATUS, 0, 0, strFileName);
                    NLog.d("Delete File", strFileName);
                } else {
                    if (strFileName.contains("DCIM/.thumbnails")) {
                        return;
                    }
                    File file = new File(strFileName);
                    String dir = file.getParent().replace(defaultSdCardPath, "");
                    String fileName;
                    if (Commons.isCNVersion()) {
                        fileName = file.getName();
                    } else {
                        int idx = file.getName().lastIndexOf(".");
                        if (idx != -1) {
                            fileName = file.getName().substring(idx);
                        } else {
                            fileName = "";
                        }
                    }
                    String info = new StringBuilder("deletedetail=").append(dir).append("&name=").append(fileName)
                            .append("&t=").append(mType.ordinal()).append("&sign=").append(Integer.toString(mSign))
                            .toString();
                }
            }
        }

        @Override
        public void afterDel(int folderCount, int fileCount, int imageCount, int videoCount, int audioCount) {
            mFolderCount = folderCount;
            mFileCount = fileCount;
            mImageCount = imageCount;
            mVideoCount = videoCount;
            mAudioCount = audioCount;
        }

        public void setType(JunkRequest.EM_JUNK_DATA_TYPE type) {
            mType = type;
        }

        public void setSign(int sign) {
            mSign = sign;
        }

        @Override
        public void onFeedbackFile(String strFilePath, String strFileName, long size) {
            String info = new StringBuilder("datapath=").append(strFilePath).append("&name=").append(strFileName)
                    .append("&datasize=").append(size).toString();
            NLog.d("root_clean", info);
        }

        @Override
        public void onError(String strPath, boolean bRmDir, boolean bRoot, int nErrorCode) {
            String info = new StringBuilder("datapath=").append(strPath).append("&rmdir=").append(bRmDir ? "1" : "0")
                    .append("&root=").append(bRoot ? "1" : "0").append("&errcode=").append(nErrorCode).toString();
            NLog.d("junk_cleanerr", info);
        }

        @Override
        public boolean onFilter(String filePath, long fileModifyTime) {
            if (mRubbishFilter != null) {
                long fileTimeLimit = mRubbishFilter.getFileTimeLimit();
                if (fileTimeLimit != -1) {
                    if (Math.abs(System.currentTimeMillis() / 1000 - fileModifyTime) > fileTimeLimit) {
                        return mRubbishFilter.isNeedFilterByPath(filePath);
                    }
                    return false;
                }
            }

            return true;
        }

    }

    public static class DelPathResult extends DelPathInfo {
        public long mSize;
        public int mFolderCount;
        public int mFileCount;
        public int mImageCount = 0;
        public int mVideoCount = 0;
        public int mAudioCount = 0;
        public List<String> mFileDeletedList;
        public List<String> mActualScanList;
        public long mFileDeletedSize;

        public DelPathResult(int mask, String path, Object attachInfo, long size, int folderCount, int fileCount, int
                imageCount, int videoCount, int audioCount) {
            super(mask, path, attachInfo);
            mSize = size;
            mFolderCount = folderCount;
            mFileCount = fileCount;
            mImageCount = imageCount;
            mVideoCount = videoCount;
            mAudioCount = audioCount;
        }

        public DelPathResult(int mask, String path, Object attachInfo, long size, int folderCount, int fileCount, int
                imageCount, int videoCount, int audioCount, List<String> fileDeletedList, long fileDeletedSize, List<String> actualScanList) {
            super(mask, path, attachInfo);
            mSize = size;
            mFolderCount = folderCount;
            mFileCount = fileCount;
            mImageCount = imageCount;
            mVideoCount = videoCount;
            mAudioCount = audioCount;
            mFileDeletedList = fileDeletedList;
            mFileDeletedSize = fileDeletedSize;
            mActualScanList = actualScanList;
        }
    }
}

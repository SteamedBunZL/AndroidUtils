package com.clean.spaceplus.cleansdk.junk.engine.bean;

import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.junk.engine.DataTypeInterface;
import com.clean.spaceplus.cleansdk.junk.engine.task.CalcSizeInfoTask;

import java.util.Iterator;
import java.util.List;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/5 13:33
 * @copyright TCL-MIG
 */
public class JunkModel implements Comparable<JunkModel>, DataTypeInterface{
    public static final String EXTRA_JUNKMODEL_INDEX = "extra_junk_model_index";

    private int mType;
    private int mCategoryType;
    private int mCategoryCount;
    // 是否在界面中隐藏
    private boolean isHidden = false;
    private boolean isCategoryHidden = false;
    private CacheInfo mCacheInfo;
    private RootCacheInfo mRootCacheInfo;
    private SDcardRubbishResult mSdcardRubbishResult;
    private APKModel mApkModel;
    private ProcessModel mProcessModel;
    private VideoOfflineResult mVideoOfflineResult;

    public JunkModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(JunkModel categoryModel){
        this.categoryModel = categoryModel;
    }

    /* start 建议清理页面缓存数据使用 */
    private JunkModel categoryModel;
    private String categoryName = null;
    private String categorySize = null;
    private boolean isChecked = false;
    private boolean mProcessModelChecked = true;

    //缓存cacheInfo中的label
    private String appInfoLabel = null;
    /* end 建议清理页面缓存数据使用 */

    public String getAppInfoLabel() {
        return appInfoLabel;
    }

    public void setAppInfoLabel(String appInfoLabel) {
        this.appInfoLabel = appInfoLabel;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isProcessChecked() {
        return mProcessModelChecked;
    }

    public void setProcessChecked(boolean isChecked) {
        mProcessModelChecked = isChecked;
        if(mProcessModel != null){
            mProcessModel.setChecked(isChecked);
        }
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategorySize() {
        return categorySize;
    }

    public void setCategorySize(String categorySize) {
        this.categorySize = categorySize;
    }
    private String mAdvice = null;
    private String mAdviceContent = null;

    // 系统缓存和应用缓存会用到
    private List<CacheInfo> mChildList;
    private List<RootCacheInfo> mRootChildList;

    //	private ArrayList<MyMediaFile> mediaList;
    private MediaFileList mMediaFileList;
    private List<CalcSizeInfoTask.SizeUpdateInfo> mDownloads ;
    private List<CalcSizeInfoTask.SizeUpdateInfo>  mBluetooths;
//    public SimilarPhotoWrapper mSimilarPhotoWrapper;

    @Override
    public int compareTo(JunkModel another) {
        if (another == null) {
            return 0;
        }
        if (getFileSize() > another.getFileSize()) {
            return -1;
        } else if (getFileSize() < another.getFileSize()) {
            return 1;
        } else {
            return 0;
        }
    }

    public long getFileSize() {
        if (mType == TYPE_SYSTEM_CACHE || mType == TYPE_APP_CACHE || mType == TYPE_SYS_FIXED_CACHE) {
            long fileSize = 0;
            if (mChildList != null && mChildList.size() > 0) {
                for (CacheInfo cacheInfo : mChildList) {
                    fileSize = fileSize + cacheInfo.getSize();
                }
            }
            return fileSize;
        }else if(mType == TYPE_ROOT_CACHE){
            long fileSize = 0;
            if (mChildList != null && mChildList.size() > 0) {
                for (CacheInfo cacheInfo : mChildList) {
                    fileSize = fileSize + cacheInfo.getSize();
                }
            }
            if (mRootChildList != null && mRootChildList.size() > 0) {
                for (RootCacheInfo cacheInfo : mRootChildList) {
                    fileSize = fileSize + cacheInfo.getSize();
                }
            }
            return fileSize;
        } else if (mCacheInfo != null) {
            return mCacheInfo.getSize();
        }else if (mSdcardRubbishResult != null) {
            return mSdcardRubbishResult.getSize();
        } else if (mApkModel != null) {
            return mApkModel.getSize();
        } else if(mType==TYPE_PHOTO_GALLERY && null != mMediaFileList) {
            return mMediaFileList.getSize();
        } else if(mType==TYPE_DOWNLOAD_GALLERY){
            return getDownloadSize();
        }else if(mType==TYPE_BLUETOOTH_GALLERY){
            return getBluetoothSize();
        } else if (mProcessModel != null) {
            return mProcessModel.getMemory();
        } else if (mType == TYPE_AUDIO_MANAGER && null != mMediaFileList) {
            return mMediaFileList.getSize();
        } else if (mCategoryType == CATEGORY_TYPE_SCREEN_SHOTS_COMPRESS && null != mMediaFileList) {
            return mMediaFileList.getSize();
        }
        return 0;
    }

    public synchronized long getDownloadSize(){
        long fileSize = 0;

        if (mDownloads == null|| mDownloads.isEmpty() ) {
            return fileSize;
        }

        for ( CalcSizeInfoTask.SizeUpdateInfo sizeUpdateInfo : mDownloads ) {
            if ( sizeUpdateInfo != null && sizeUpdateInfo.mFileCompute != null ) {
                fileSize += sizeUpdateInfo.mFileCompute[0];
            }
        }
        return fileSize;
    }

    public synchronized long getBluetoothSize(){
        long fileSize = 0;

        if (mBluetooths == null|| mBluetooths.isEmpty() ) {
            return fileSize;
        }

        for ( CalcSizeInfoTask.SizeUpdateInfo sizeUpdateInfo : mBluetooths ) {
            if ( sizeUpdateInfo != null && sizeUpdateInfo.mFileCompute != null ) {
                fileSize += sizeUpdateInfo.mFileCompute[0];
            }
        }
        return fileSize;
    }

    public static JunkModel createCategoryModel(int categoryTypeCache, int categoryCount) {
        JunkModel junkModel = new JunkModel();
        junkModel.setType(TYPE_CATEGORY);
        junkModel.setCategoryType(categoryTypeCache);
        junkModel.setCategoryCount(categoryCount);
        return junkModel;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getCategoryType() {
        return mCategoryType;
    }

    public void setCategoryType(int categoryType) {
        mCategoryType = categoryType;
    }

    public void setCategoryCount(int categoryCount) {
        mCategoryCount = categoryCount;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public CacheInfo getCacheInfo() {
        return mCacheInfo;
    }

    public RootCacheInfo getRootCacheInfo() {
        return mRootCacheInfo;
    }

    public void setCacheInfo(CacheInfo cacheInfo) {
        mCacheInfo = cacheInfo;
    }

    public void setRootCacheInfo(RootCacheInfo cacheInfo) {
        mRootCacheInfo = cacheInfo;
    }

    public SDcardRubbishResult getSdcardRubbishResult() {
        return mSdcardRubbishResult;
    }

    public ProcessModel getProcessModel(){
        return mProcessModel;
    }

    public void setVideoOfflineResult(VideoOfflineResult vor) {
        mVideoOfflineResult = vor;
    }
    public VideoOfflineResult getVideoOfflineResult() {
        return  mVideoOfflineResult;
    }
    public void setProcessModel(ProcessModel processModel) {
        mProcessModel = processModel;
    }

    public void setSdcardRubbishResult(SDcardRubbishResult sdcardRubbishResult) {
        mSdcardRubbishResult = sdcardRubbishResult;
    }

    public void setCategoryHidden(boolean isCategoryHidden) {
        this.isCategoryHidden = isCategoryHidden;
    }

    public void setMediaFileList(MediaFileList mediaFileList) {
        mMediaFileList = mediaFileList;
    }

    public void setRootChildList(List<RootCacheInfo> childList) {
        mRootChildList = childList;
    }

    public List<CacheInfo> getChildList() {
        return mChildList;
    }

    public int getChildSize() {
        int childSize = 0;
        if (mChildList != null) {
            childSize =  mChildList.size();
        }
        if (mRootChildList != null && !mRootChildList.isEmpty()) {
            childSize += mRootChildList.size();
        }
        return childSize;
    }

    public void setChildList(List<CacheInfo> childList) {
        mChildList = childList;
    }

    public String getAdviceStr(){
        return mAdvice;
    }

    public void setAdviceStr(String advice) {
        mAdvice = advice;
    }

    public void setAdviceContentStr(String adviceContent){
        mAdviceContent = adviceContent;
    }

    public APKModel getApkModel() {
        return mApkModel;
    }

    public void setApkModel(APKModel apkModel) {
        mApkModel = apkModel;
    }

    public List<RootCacheInfo> getRootChildList(){
        return mRootChildList;
    }

    public MediaFileList getMediaFileList() {
        return mMediaFileList;
    }

    @Override
    public String toString() {
        return "JunkModel{" +
                "mType=" + mType +
                ", mCategoryType=" + mCategoryType +
                ", mCategoryCount=" + mCategoryCount +
                ", isHidden=" + isHidden +
                ", isCategoryHidden=" + isCategoryHidden +
                ", mCacheInfo=" + mCacheInfo +
                ", mRootCacheInfo=" + mRootCacheInfo +
                ", mSdcardRubbishResult=" + mSdcardRubbishResult +
                ", mApkModel=" + mApkModel +
                ", mProcessModel=" + mProcessModel +
                ", mVideoOfflineResult=" + mVideoOfflineResult +
                ", categoryModel=" + categoryModel +
                ", categoryName='" + categoryName + '\'' +
                ", categorySize='" + categorySize + '\'' +
                ", isChecked=" + isChecked +
                ", mProcessModelChecked=" + mProcessModelChecked +
                ", appInfoLabel='" + appInfoLabel + '\'' +
                ", mAdvice='" + mAdvice + '\'' +
                ", mAdviceContent='" + mAdviceContent + '\'' +
                ", mChildList=" + mChildList +
                ", mRootChildList=" + mRootChildList +
                ", mMediaFileList=" + mMediaFileList +
                ", mDownloads=" + mDownloads +
                ", mBluetooths=" + mBluetooths +
                '}';
    }

    /**
     * 卡片是否lock  同时 remove 没有勾选的不要的子项
     *
     * @return
     */
    public boolean isGroupLockandRemoveUnCheckChild() {
        if (mType == TYPE_SYSTEM_CACHE || mType == TYPE_APP_CACHE || mType == TYPE_SYS_FIXED_CACHE) {
            if (mChildList != null && mChildList.size() > 0) {
                CacheInfo cacheInfo;
                boolean isGroupLock = true;
                Iterator<CacheInfo> iter = mChildList.iterator();
                while (iter.hasNext()) {
                    cacheInfo = iter.next();
                    if (cacheInfo.isCheck()) {
                        if (isGroupLock) {
                            isGroupLock = false;
                        }
                    } else {
                        iter.remove();
                    }
                }
                return isGroupLock;
            }
        }else if (mType == TYPE_ROOT_CACHE) {
            boolean isGroupLock = true;
            if (mChildList != null && mChildList.size() > 0) {
                CacheInfo cacheInfo;
                Iterator<CacheInfo> iter = mChildList.iterator();
                while (iter.hasNext()) {
                    cacheInfo = iter.next();
                    if (cacheInfo.isCheck()) {
                        if (isGroupLock) {
                            isGroupLock = false;
                        }
                    } else {
                        iter.remove();
                    }
                }
            }

            if (mRootChildList != null && mRootChildList.size() > 0) {
                RootCacheInfo cacheInfo;
                Iterator<RootCacheInfo> iter = mRootChildList.iterator();
                while (iter.hasNext()) {
                    cacheInfo = iter.next();
                    if (cacheInfo.isCheck()) {
                        if (isGroupLock) {
                            isGroupLock = false;
                        }
                    } else {
                        iter.remove();
                    }
                }
            }
            return isGroupLock;
        } else if (mCacheInfo != null) {
            return !mCacheInfo.isCheck();
        } else if (mSdcardRubbishResult != null) {
            return !mSdcardRubbishResult.isCheck();
        } else if (mApkModel != null) {
            return !mApkModel.isChecked();
        } else if (mProcessModel != null) {
            return !isProcessChecked();
        } else if(mRootCacheInfo != null){
            return !mRootCacheInfo.isCheck();
        }else if(mCategoryType == CATEGORY_TYPE_SCREEN_SHOTS_COMPRESS){
            return !isChecked;
        }
        return true;
    }
}

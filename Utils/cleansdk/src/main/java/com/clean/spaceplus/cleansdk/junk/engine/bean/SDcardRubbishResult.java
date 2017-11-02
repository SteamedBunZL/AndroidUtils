package com.clean.spaceplus.cleansdk.junk.engine.bean;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;

import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.util.FileUtils;
import com.clean.spaceplus.cleansdk.base.scan.ExtraAndroidFileScanner;
import com.clean.spaceplus.cleansdk.util.OpenFileHelper;
import com.clean.spaceplus.cleansdk.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import space.network.cleancloud.KResidualCloudQuery;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/28 20:09
 * @copyright TCL-MIG
 */
public class SDcardRubbishResult extends BaseJunkBean{

    public static final int RF_APP_LEFTOVERS = 0;
    public static final int RF_TEMPFILES = 1;
    public static final int RF_ADV_FOLDERS = 2;
    public static final int RF_BIG_FILES = 3;

    public final static int ICON_CAT_OTHER = 0;
    public final static int ICON_CAT_ARCHIVE = 1;
    public final static int ICON_CAT_AUDIO = 2;
    public final static int ICON_CAT_PICTURE = 3;
    public final static int ICON_CAT_VIDEO = 4;
    public final static int ICON_CAT_BOOK = 5;
    public final static int ICON_CAT_GPK = 6;
    public final static int ICON_CAT_APK = 7;
    public final static int ICON_CAT_GAMEDATA = 8;
    public final static int ICON_CAT_BIGFILE_VIDEO_FOLDER = 9;
    public final static int ICON_CAT_BIGFILE = 10;
    public final static int ICON_CAT_BIGFILE_MERGEFILE = 11;
    public final static int ICON_CAT_BAIDU_MAPFILE = 12;
    public final static int CLEAN_FILE_FLAG_DEFAULT = 0x00000000;  //[2][1][0]={image,audio,vedio}

    /**
     * 18：扩展的大文件（残留）
     */
    public static final int TYPE_BIG_FILE_EXTEND_LEFTOVER = 18;


    private int iconCategory = ICON_CAT_OTHER;
    private int type;
    private int id = 0;
    private ImageView icon;
    private String strDirPath;
    private String chineseName = "unknown";
    private String apkName;
    private String appName = null;//added by liyao :For obb/baidumap/mergedFile
    private long nFilesCount = 0L;

    private long nFoldersCount = 0L;
    private String mAlertInfo = "";
    private String mFromName = null;
    private List<PathInfo> mPathInfoList = new ArrayList<>();
    private List<String> mPathList = new ArrayList<>();
    private List<String> mMSImageThumbIdList = new ArrayList<>();
    private List<String> mMSImageMediaIdList = new ArrayList<>();
    private boolean bIsStdItemInAdvPage = false;
    private boolean mbIs2ndSdCard = false;
    private String mWhiteListKey = null;
    private int mCleanFileFlag = 0;
    private int mCleanType = 0;
    private int mIsShow = 0;//用于上报，标识是否显示
    private int extendType = 1;
    private int mergeType = ICON_CAT_OTHER;

    private int mObjHash;
    private static AtomicInteger smNextObjHash = new AtomicInteger(1);

    private List<String> mFilterSubFolderList = null;
    private Object m_mtxFilterSubFolderList = new Object();
    private byte resultSource;

    ///> 用于垃圾清理残留时间线以及扩展名检测
    private int mCleanTime = 0;
    private KResidualCloudQuery.FileCheckerData mRubbishFilterData = new KResidualCloudQuery.FileCheckerData();

    private static int getNextObjHash() {
        return smNextObjHash.getAndIncrement();
    }

    public int getMergeType() {
        return mergeType;
    }

    public void setMergeType(int mergeType) {
        this.mergeType = mergeType;
    }

    @Deprecated
    public SDcardRubbishResult() {
        super(JUNK_SD_RUBBISH);
        mObjHash = getNextObjHash();
    }

    public SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE junkType) {
        super(junkType);
        mObjHash = getNextObjHash();
    }

    public SDcardRubbishResult(SDcardRubbishResult o) {
        super(o);
        mObjHash = o.mObjHash;
        iconCategory = o.iconCategory;
        type = o.type;
        id = o.id;
        icon = o.icon;
        strDirPath = o.strDirPath;
        chineseName = o.chineseName;
        apkName = o.apkName;
        mbHaveSetSize=o.mbHaveSetSize;
        mSize = o.mSize;
        mbCheck = o.mbCheck;
        mbIgnore = o.mbIgnore;
        nFilesCount = o.nFilesCount;
        nFoldersCount = o.nFoldersCount;
        mAlertInfo = o.mAlertInfo;
        mFromName = o.mFromName;
        mPathInfoList.addAll(o.mPathInfoList);
        mPathList.addAll(o.mPathList);
        bIsStdItemInAdvPage = o.bIsStdItemInAdvPage;
        mbIs2ndSdCard = o.mbIs2ndSdCard;
        mWhiteListKey = o.mWhiteListKey;
        mCleanFileFlag = o.mCleanFileFlag;
        mMSImageThumbIdList.addAll(o.mMSImageThumbIdList);
        mMSImageMediaIdList.addAll(o.mMSImageMediaIdList);
        setFileType(o.getFileType());
    }

    public int getCleanType() {
        return mCleanType ;
    }

    public void setCleanType(int cleanType) {
        this.mCleanType  = cleanType;
    }

    public void setExtendType(int extendType) {
        this.extendType = extendType;
    }

    public int getExtendType() {
        return extendType;
    }


    public void setIconCategory(int cat) {
        iconCategory = cat;
    }

    public int getIconCategory() {
        return iconCategory;
    }

    public void setType(int nType){
        type = nType;
    }

    public int getType(){
        return type;
    }

    public void addPathList(String path){
        if (null != path) {
            mPathInfoList.add(new PathInfo(path));
            mPathList.add(path);
        }
    }
//	public void addPathListAll(List<String> paths) {
//	    if((paths != null) && (paths.isEmpty())) {
//	        mPathList.addAll(paths);
//	    }
//	}

    public void addPathList(String path, int cleanFileFlag) {
        if (null != path) {
            mPathInfoList.add(new PathInfo(path, cleanFileFlag));
            mPathList.add(path);
        }
    }

    public List<String> getPathList(){
        return mPathList;
    }

    public void addPathInfo(PathInfo pathInfo) {
        if (null != pathInfo) {
            mPathInfoList.add(pathInfo);
            mPathList.add(pathInfo.getPath());
        }
    }

    public void setRubbishCleanTime(int cleanTime) {
        mCleanTime = cleanTime;
    }

    public int getRubbishCleanTime() {
        return mCleanTime;
    }

    public void setRubbishFilterData(KResidualCloudQuery.FileCheckerData filterData) {
        if (filterData != null) {
            mRubbishFilterData.globalSuffixCatIds = filterData.globalSuffixCatIds;
            if (filterData.whiteSuffixFilter != null) {
                mRubbishFilterData.whiteSuffixFilter = filterData.whiteSuffixFilter;
            }

            if (filterData.blackSuffixFilter != null) {
                mRubbishFilterData.blackSuffixFilter = filterData.blackSuffixFilter;
            }
        }
    }

    public KResidualCloudQuery.FileCheckerData getRubbishFilterData() {
        return mRubbishFilterData;
    }

    public List<PathInfo> getPathInfoList() {
        return mPathInfoList;
    }

//	public int getCleanFileFlag(String path) {
//		if (null != path) {
//			for (PathInfo pathInfo : mPathInfoList) {
//	        	if (path.equals(pathInfo.getPath())) {
//			    	return pathInfo.getCleanFileFlag();
//		    	}
//			}
//		}
//		return 0;  //return default
//	}

    public void setCleanFileFlag(int cleanFileFlag){
        mCleanFileFlag = cleanFileFlag;
    }

    public int getCleanFileFlag(){
        return mCleanFileFlag;
    }

    public String getChineseName() {
        return chineseName;
    }

    @Override
    public String getName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getAlertInfo() {
        return this.mAlertInfo;
    }

    public void setAlertInfo(String alertInfo) {
        if(null == alertInfo)
            alertInfo = "";
        this.mAlertInfo = alertInfo;
    }

    public String getFromName() {
        return this.mFromName;
    }

    public void setFromName(String fromName) {
        this.mFromName = fromName;
    }

    public String getApkName() {
        return apkName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String app) {
        appName = app;
    }

    public void setFilesCount(long nFiles){
        this.nFilesCount = nFiles;
    }

    public long getFilesCount(){
        return this.nFilesCount;
    }

    public void setFoldersCount(long nFiles){
        this.nFoldersCount = nFiles;
    }

    public long getFoldersCount(){
        return this.nFoldersCount;
    }

    public void addMSImageThumbIdList(int nThumbId) {
        mMSImageThumbIdList.add(""+nThumbId);
    }

    public void addMSImageMediaIdList(int nMediaId) {
        mMSImageMediaIdList.add(""+nMediaId);
    }

    public List<String> getMSImageThumbIdList() {
        return mMSImageThumbIdList;
    }

    public List<String> getMSImageMediaIdList() {
        return mMSImageMediaIdList;
    }

    @Override
    public int hashCode() {
        return mObjHash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SDcardRubbishResult other = (SDcardRubbishResult) obj;
        return mObjHash == other.mObjHash;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

//	public ImageView getIcon() {
//		return icon;
//	}

///<DEAD CODE>/// 	public long getFileSize() {
//		return mSize;
//	}

//	public void setIcon(ImageView appIcon) {
//		this.icon = appIcon;
//	}

    public String getStrDirPath() {
        return strDirPath;
    }

    public void setStrDirPath(String strDirPath) {
        this.strDirPath = strDirPath;
    }

    public void setSignId(int id) {
        this.id = id;
    }

    public int getSignId() {
        //dalvikcache设定id为1000000
        /*if(getChineseName().equals(SpaceApplication.getInstance().getContext().getString(R.string.RF_DalvikCacheLeftovers))){
            return 1000000;
        } else if(getChineseName().equals(SpaceApplication.getInstance().getContext().getString(R.string.remain_item_name_n7p))){
            return 1000001;
        } else if(getChineseName().equals(SpaceApplication.getInstance().getContext().getString(R.string.remain_item_name_pamp))){
            return 1000002;
        }*/
        return id;
    }

    @Override
    public int compareTo(BaseJunkBean another) {
        if (this.mSize > ((SDcardRubbishResult)another).mSize)
            return -1;
        else if (this.mSize < ((SDcardRubbishResult)another).mSize)
            return 1;
        else
            return 0;
    }

    /**
     * if can opened, return the File, else return null.
     */
    public File getOpenedFile() {
        File file = null;
        if (strDirPath != null) {
            file = FileUtils.checkPath(strDirPath);
            if (file != null
                    && (iconCategory != ICON_CAT_OTHER || iconCategory != ICON_CAT_ARCHIVE || iconCategory != ICON_CAT_GPK)) {
                return file;
            }
        }
        return null;
    }

    /**
     * if can opened, return the Intent, else return null.
     */
    public Intent getOpenIntent() {
        final File file = getOpenedFile();
        if (file == null)
            return null;
        String path = file.getAbsolutePath();
        Intent intent = null;
        switch (iconCategory) {
            case SDcardRubbishResult.ICON_CAT_AUDIO:
                intent = OpenFileHelper.getAudioFileIntent(file);
                break;
            case SDcardRubbishResult.ICON_CAT_PICTURE:
                intent = OpenFileHelper.getImageFileIntent(file);
                break;
            case SDcardRubbishResult.ICON_CAT_VIDEO:
                intent = OpenFileHelper.getVideoFileIntent(file);
                break;
            case SDcardRubbishResult.ICON_CAT_BOOK:
                if (path.endsWith("txt")) {
                    intent = OpenFileHelper.getTextFileIntent(file);
                } else if (path.endsWith("chm")) {
                    intent = OpenFileHelper.getChmFileIntent(file);
                } else if (path.endsWith("pdf")) {
                    intent = OpenFileHelper.getPdfFileIntent(file);
                }
                break;
        }
        return intent;
    }

    public boolean isMyVideo() {
        if (getType() == SDcardRubbishResult.RF_BIG_FILES && getIconCategory() == SDcardRubbishResult.ICON_CAT_VIDEO) {
            return getStrDirPath() != null && StringUtils.toLowerCase(getStrDirPath()).contains("dcim/camera");
        }
        return false;
    }

    public boolean isStdItemInAdvPage() {
        return bIsStdItemInAdvPage;
    }

//	public void setStdItemInAdvPageFlag() {
//		bIsStdItemInAdvPage = true;
//	}

    public boolean is2ndSdCardRubbish() {
        return mbIs2ndSdCard;
    }

    public void set2ndSdCardRubbishFlag(boolean the2ndSdCard) {
        mbIs2ndSdCard = the2ndSdCard;
    }

//	public String getWhiteListDesc(){
//		return getChineseName();
//	}

    public String getWhiteListKey(){
        if ( !TextUtils.isEmpty(mWhiteListKey) )
            return mWhiteListKey;

        return getStrDirPath();
    }

    public void SetWhiteListKey(String key){
        mWhiteListKey = key;
    }

    public int getIsShow(){
        //用于上报，标识是否显示
        if (getType() == SDcardRubbishResult.RF_BIG_FILES &&
                !TextUtils.isEmpty(getFromName())) {
            return 1;
        } else if (getType() == SDcardRubbishResult.RF_APP_LEFTOVERS &&
                getSize() > ExtraAndroidFileScanner.SIZE_BIG_FILE_MIN) {
            return 1;
        }
        return 0;
    }

    public void addFilterSubFolderList(List<String> subFolderList) {
        if (null == subFolderList || subFolderList.isEmpty()) {
            return;
        }

        synchronized (m_mtxFilterSubFolderList) {
            if (null == mFilterSubFolderList) {
                mFilterSubFolderList = new ArrayList<String>();
            }

            mFilterSubFolderList.addAll(subFolderList);
        }
    }

///<DEAD CODE>///     public void addFilterSubFolderList(String strSubFolder) {
//        if (TextUtils.isEmpty(strSubFolder)) {
//            return;
//        }
//
//        synchronized (m_mtxFilterSubFolderList) {
//            if (null == mFilterSubFolderList) {
//                mFilterSubFolderList = new ArrayList<String>();
//            }
//
//            mFilterSubFolderList.add(strSubFolder);
//        }
//    }

    public List<String> getFilterSubFolderList() {
        return mFilterSubFolderList;
    }

    public byte getResultSource() {
        return resultSource;
    }

    public void setResultSource(byte resultSource) {
        this.resultSource = resultSource;
    }

    public static class PathInfo {

        private String mPath = null;
        private int mCleanFileFlag = 0;

        public PathInfo(String path, int cleanFileFlag){
            mPath = path;
            mCleanFileFlag = cleanFileFlag;
        }

        public PathInfo(String path){
            mPath = path;
            mCleanFileFlag = CLEAN_FILE_FLAG_DEFAULT;
        }

        public String getPath(){
            return mPath;
        }

//		public void setCleanFileFlag(int cleanFileFlag){
//			mCleanFileFlag = cleanFileFlag;
//		}

        public int getCleanFileFlag(){
            return mCleanFileFlag;
        }

        @Override
        public String toString() {
            return "PathInfo{" +
                    "mPath='" + mPath + '\'' +
                    ", mCleanFileFlag=" + mCleanFileFlag +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SDcardRubbishResult{" +
                "iconCategory=" + iconCategory +
                ", type=" + type +
                ", id=" + id +
                ", icon=" + icon +
                ", strDirPath='" + strDirPath + '\'' +
                ", chineseName='" + chineseName + '\'' +
                ", apkName='" + apkName + '\'' +
                ", appName='" + appName + '\'' +
                ", nFilesCount=" + nFilesCount +
                ", nFoldersCount=" + nFoldersCount +
                ", mAlertInfo='" + mAlertInfo + '\'' +
                ", mFromName='" + mFromName + '\'' +
                ", mPathInfoList=" + mPathInfoList +
                ", mbCheck=" + mbCheck +
                ", mPathList=" + mPathList +
                ", mMSImageThumbIdList=" + mMSImageThumbIdList +
                ", mMSImageMediaIdList=" + mMSImageMediaIdList +
                ", bIsStdItemInAdvPage=" + bIsStdItemInAdvPage +
                ", mbIs2ndSdCard=" + mbIs2ndSdCard +
                ", mWhiteListKey='" + mWhiteListKey + '\'' +
                ", mCleanFileFlag=" + mCleanFileFlag +
                ", mCleanType=" + mCleanType +
                ", mIsShow=" + mIsShow +
                ", extendType=" + extendType +
                ", mergeType=" + mergeType +
                ", mObjHash=" + mObjHash +
                ", mFilterSubFolderList=" + mFilterSubFolderList +
                ", m_mtxFilterSubFolderList=" + m_mtxFilterSubFolderList +
                ", resultSource=" + resultSource +
                ", mCleanTime=" + mCleanTime +
                ", mRubbishFilterData=" + mRubbishFilterData +
                '}';
    }
}

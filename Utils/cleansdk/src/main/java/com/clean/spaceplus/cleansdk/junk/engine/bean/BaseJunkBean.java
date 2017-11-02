package com.clean.spaceplus.cleansdk.junk.engine.bean;

import android.support.v4.util.ArrayMap;

import com.clean.spaceplus.cleansdk.base.clean.CleanRequest;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.junk.engine.task.CalcSizeInfoTask;
import com.clean.spaceplus.cleansdk.junk.engine.task.SysCacheScanTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangni
 * @Description:扫描垃圾基础字段
 * @date 2016/4/22 11:08
 * @copyright TCL-MIG
 */
public abstract class BaseJunkBean implements Comparable<BaseJunkBean>{
    public static final int JUNK_SD_RUBBISH = 0;//SD卡垃圾文件
    public static final int JUNK_CACHE = 1;//缓存文件
    public static final int JUNK_APK = 2;//冗余安装包
    public static final int JUNK_APP = 3;//应用卸载
    public static final int MOVE_APP = 4;//应用搬家
    private int mJunkType;

    public static final int SCAN_TYPE_NONE = 0;
    public static final int SCAN_TYPE_STANDARD = 1;
    public static final int SCAN_TYPE_ADVANCED = 2;
    private int mScanType = SCAN_TYPE_NONE;

    protected boolean mbIgnore = false;
    protected boolean mbCheck = true;
    protected boolean mbHaveSetSize = false;
    protected long mSize = 0L;
    protected SysCacheScanTask.SysCacheOnCardInfo mSysCacheOnCardInfo = null;
    protected boolean mbRecycle=false;
    private JunkRequest.EM_JUNK_DATA_TYPE mJunkInfoType;
    private int mCleanType = CleanRequest.CLEAN_TYPE_NONE;

    private int mVideoNum = -1;
    private int mAudioNum = -1;
    private int mImageNum = -1;
    private List<MediaFile> mMediaList = null;

    private ArrayMap<Integer, List<CalcSizeInfoTask.SizeUpdateInfo>> mCalcSizeInfoMap = null;

    public BaseJunkBean(int type) {
        // TODO 过渡兼容
        mJunkType = type;
        mJunkInfoType = JunkRequest.EM_JUNK_DATA_TYPE.UNKNOWN;
    }

    public BaseJunkBean(JunkRequest.EM_JUNK_DATA_TYPE junkType){
        mJunkInfoType = junkType;

        // TODO 过渡兼容
        switch (junkType) {
            case SYSCACHE:
            case SDCACHE:
            case SYSFIXEDCACHE:
            case ROOTCACHE:
                mJunkType = JUNK_CACHE;
                break;

            case ADVERTISEMENT:
            case TEMPFOLDER:
            case BIGFILE:
            case APPLEFTOVER:
            case USELESSTHUMBNAIL:
                mJunkType = JUNK_SD_RUBBISH;
                break;

            case APKFILE:
                mJunkType = JUNK_APK;
                break;
        }
    }

    public BaseJunkBean(BaseJunkBean o) {
        mJunkType = o.mJunkType;
        mScanType = o.mScanType;
        mbIgnore = o.mbIgnore;
        mbCheck = o.mbCheck;
        mbHaveSetSize = o.mbHaveSetSize;
        mSize = o.mSize;
        mSysCacheOnCardInfo = o.mSysCacheOnCardInfo;
        mbRecycle = o.mbRecycle;
        mJunkInfoType = o.mJunkInfoType;

        mVideoNum = o.mVideoNum;
        mAudioNum = o.mAudioNum;
        mImageNum = o.mImageNum;
        mMediaList = (null != o.mMediaList ? new ArrayList(o.mMediaList) : null);

        mFileType = o.mFileType;
        mCleanType = o.mCleanType;
    }

    public void setScanType(int type) {
        mScanType = type;
    }

    public int getScanType() {
        return mScanType;
    }

    ///<DEAD CODE>/// 	public void setRecycle(boolean isRecycle){
    //		mbRecycle=isRecycle;
    //	}

    ///<DEAD CODE>/// 	public boolean isRecycle(){
    //		return mbRecycle;
    //	}

    public void setJunkInfoType(JunkRequest.EM_JUNK_DATA_TYPE type) {
        mJunkInfoType = type;
    }

    public JunkRequest.EM_JUNK_DATA_TYPE getJunkDataType() {
        return mJunkInfoType;
    }

    public void addMediaList(List<MediaFile> list){
        if(list == null || list.isEmpty()){
            return;
        }
        if(mMediaList == null){
            mMediaList = new ArrayList<MediaFile>();
        }
        mMediaList.addAll(list);
    }

    public List<MediaFile> getMediaList(){
        return mMediaList;
    }

    public boolean setCalcFolderInfo(int key, List<CalcSizeInfoTask.SizeUpdateInfo> val) {
        if (null == val) {
            return false;
        }

        if (null == mCalcSizeInfoMap) {
            mCalcSizeInfoMap = new ArrayMap<Integer, List<CalcSizeInfoTask.SizeUpdateInfo>>();
        }
        mCalcSizeInfoMap.put(Integer.valueOf(key), val);

        return true;
    }

    public List<CalcSizeInfoTask.SizeUpdateInfo> getCalcFolderInfo(int key) {
        if (null == mCalcSizeInfoMap) {
            return null;
        }

        return mCalcSizeInfoMap.get(key);
    }

    public void setVideoNum(int num) {
        mVideoNum = num;
    }

    public int getVideoNum() {
        return mVideoNum;
    }

    public void setAudioNum(int num) {
        mAudioNum = num;
    }

    public int getAudioNum() {
        return mAudioNum;
    }

    public void setImageNum(int num) {
        mImageNum = num;
    }

    public int getImageNum() {
        return mImageNum;
    }

    public int getJunkType(){
        return mJunkType;
    }

    public void setIgnore(boolean bIgnore){
        mbIgnore = bIgnore;
    }

    ///<DEAD CODE>/// 	public boolean isIgnore(){
    //		return mbIgnore;
    //	}

    public boolean isCheck(){
        return mbCheck;
    }

    public void setCheck(boolean bCheck){
        mbCheck = bCheck;
    }

    public long getSize(){
        return mSize;
    }

    public void setSize(long size){
        mbHaveSetSize = true;
        mSize = size;
    }

    public void setSysCacheOnCardInfo(SysCacheScanTask.SysCacheOnCardInfo sysCacheOnCardInfo)  {
        mSysCacheOnCardInfo = sysCacheOnCardInfo;
    }

    public SysCacheScanTask.SysCacheOnCardInfo getSysCacheOnCardInfo()  {
        return mSysCacheOnCardInfo;
    }

    public boolean hadSetSize(){
        return mbHaveSetSize;
    }

    public void setCleanType(int nType) {
        mCleanType = nType;
    }

    public int getCleanType() {
        return mCleanType;
    }

    public abstract String getName();

    public enum FileType{
        Unknown, Dir, File
    }
    private FileType mFileType = FileType.Unknown;
    public void setFileType(FileType fileType ){
        mFileType = fileType;
    }
    public FileType getFileType(){
        return mFileType;
    }

    @Override
    public abstract int compareTo(BaseJunkBean another);

    @Override
    public String toString() {
        return "BaseJunkBean{" +
                "mScanType=" + mScanType +
                ", mJunkType=" + mJunkType +
                ", mSize=" + mSize +
                '}';
    }
}

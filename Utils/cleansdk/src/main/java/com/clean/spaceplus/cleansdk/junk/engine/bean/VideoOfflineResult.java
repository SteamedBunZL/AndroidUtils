package com.clean.spaceplus.cleansdk.junk.engine.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;

import java.util.ArrayList;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/5 14:31
 * @copyright TCL-MIG
 */
public class VideoOfflineResult extends BaseJunkBean implements Parcelable{

    //减少传输资料 每次进程间传输只能有8KB
    private String mName = "";        //视频名字
    private String mPath = "";        //视频目录路径名
    private ArrayList<String> mFilePathList = new ArrayList<String>();  //文件列表 空的代表path本身是个文件
    private String mThumbnail = "";   //缩略图  会是空
    private String mApkName = "";     //app包
    private boolean mIsFinishWatch = false;   //是否观看结束
    private boolean mIsFinishDownload = false; //是否下载完成
    private long mDownloadTime = 0;       //下载完成时间
    private long mLastPlayTime = 0;       //上次观看时间
    private long mLastPlayLength = 0;     //上次观看到视频时间
    private boolean mIsHide;
    private int mType;//ui使用，分类or条目
    private boolean mIsSecSD = false;

    public VideoOfflineResult(Parcel p){
        super(JunkRequest.EM_JUNK_DATA_TYPE.VIDEO_OFF);
        mName = p.readString();
        mPath = p.readString();
        mFilePathList = p.readArrayList(VideoOfflineResult.class.getClassLoader());
        mThumbnail = p.readString();
        mApkName = p.readString();
        mSize = p.readLong();
        mbHaveSetSize = (p.readInt() == 1);
        mIsFinishWatch = (p.readInt() == 1);
        mIsFinishDownload = (p.readInt() == 1);
        mDownloadTime = p.readLong();
        mLastPlayTime = p.readLong();
        mLastPlayLength = p.readLong();
        mIsSecSD = (p.readInt() == 1);
    }

    public void setType(int type){
        mType = type;
    }
    public VideoOfflineResult(JunkRequest.EM_JUNK_DATA_TYPE dataType) {
        super(dataType);
    }
    public int getType(){
        return mType;
    }
    public void setHide(boolean hide){
        mIsHide = hide;
    }
    public boolean getHide(){
        return mIsHide;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public int compareTo(BaseJunkBean another) {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mPath);
        dest.writeList(mFilePathList);
        dest.writeString(mThumbnail);
        dest.writeString(mApkName);
        dest.writeLong(mSize);
        dest.writeInt(mbHaveSetSize ? 1 : 0);
        dest.writeInt((mIsFinishWatch ? 1 : 0));
        dest.writeInt((mIsFinishDownload ? 1 : 0));
        dest.writeLong(mDownloadTime);
        dest.writeLong(mLastPlayTime);
        dest.writeLong(mLastPlayLength);
        dest.writeInt(mIsSecSD ? 1 : 0);
    }

    public static final Parcelable.Creator<VideoOfflineResult> CREATOR = new Parcelable.Creator<VideoOfflineResult>() {
        public VideoOfflineResult createFromParcel(Parcel p) {
            return new VideoOfflineResult(p);
        }

        public VideoOfflineResult[] newArray(int size) {
            return new VideoOfflineResult[size];
        }
    };
}

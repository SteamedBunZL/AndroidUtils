package com.clean.spaceplus.cleansdk.junk.engine.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/22 15:54
 * @copyright TCL-MIG
 */
public class MediaFile extends BaseJunkBean implements Parcelable, Comparable<BaseJunkBean> {
    //start------------------------------------------以下属性,相似照片专用
    private int index;// 在list中的位置，供相似图片使用
    /**
     * 当前这是一张美化后的图片，且其对应App的美化后图片保存路径为mBeautify的值
     */
//    public String mBeautify = "";

//    /**
//     * 美化后的图片的原图路径，通过这个找到美化后图片的原图。
//     */
//    public String mDecopic = "";

    /**
     * 颜色分布算法指纹
     */
    public String mColorAlgoFinger = "";

    /**
     * 平均值算法指纹
     */
    public String mAveAlgoFinger = "";

    public static final String NoMediaFileName = ".nomedia";
    /**
     * 相同或类似的图片，分组第一个图片带上日期，作标记用
     */
    public static final int FLAG_FIRST_ITEM_IN_GROUP = 1 << 0;
    /**
     * 是否是填充数据
     */
    public static final int FLAG_FILLED = 1 << 1;
    /**
     * 是否是美化图片
     */
    public static final int FLAG_BEAUTIFY = 1 << 2;
    /**
     * 是否已经进行过验证
     */
    public static final int FLAG_CHECKED = 1 << 3;
    public int flag;

    public double mPicLevel = 0;

    /**
     * 是否已经进行过验证
     */
    public boolean hasCheck() {
        return (flag & FLAG_CHECKED) != 0;
    }

    /**
     * 相同或类似的图片，分组第一个图片带上日期，作标记用
     */
    public boolean isFirstItemInGroup() {
        return (flag & FLAG_FIRST_ITEM_IN_GROUP) != 0;
    }

    /**
     * 是否是填充数据
     */
    public boolean isFilled() {
        return (flag & FLAG_FILLED) != 0;
    }

    /**
     * 是否是美化图片
     */
    public boolean isBeautify() {
        return (flag & FLAG_BEAUTIFY) != 0;
    }
    /**
     * 在相似GRIDVIEW 界面  所在分组的第一个ITEM POSTION
     */
    public int junkSimilarFirstGroupPostion = -1;

    /**
     * 移动到回收站的时间，手动记录
     */
    public long lastMoved;

    public long lastMoved() {
        return lastMoved;
    }

    public void setLastMoved(long lastMoved) {
        this.lastMoved = lastMoved;
    }

    //end------------------------------------------类似照片专用
    //:start 云回收站专用
    public int cloudState;
    //:end

    public static final int MEDIA_TYPE_NONE = 0;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_AUDIO = 2;
    public static final int MEDIA_TYPE_VIDEO = 3;

    private String title;
    private String path;
    private long tick = 0;
    private long id = 0;
    /**
     * The media type (audio, video or image) of the file, or 0 for not a media
     * file
     * <P>
     * Type: TEXT
     * </P>
     */
    private int mediaType = MEDIA_TYPE_NONE;
    private String mimeType;
    private String artist;
    private String duration;
    private long lastModified;

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    private long dateTaken;

    public MediaFile(JunkRequest.EM_JUNK_DATA_TYPE type) {
        super(type);
        setCheck(false);
    }

    public MediaFile() {
        super(JunkRequest.EM_JUNK_DATA_TYPE.UNKNOWN);
        setCheck(false);
    }

    @Override
    public String toString() {
        return "MyMediaFile [path=" + path + ", size=" + getSize() + ", id=" + id
                + ", lastModified=" + lastModified + "]";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /** 图片上次修改时间 */
    public long lastModified() {
        return lastModified;
    }

    /** 图片上次修改时间 */
    public void setLastModified(long time) {
        this.lastModified = time;
    }

    /** 图片名称 */
    public String getTitle() {
        return title;
    }

    /** 图片名称 */
    public void setTitle(String title) {
        this.title = title;
    }

    /** 图片路径 */
    public String getPath() {
        return path;
    }

    /** 图片路径 */
    public void setPath(String path) {
        this.path = path;
    }

    /** 该图片是否被选中 */
    public boolean isSelect() {
        return isCheck();
    }

    /** 该图片是否被选中 */
    public void setSelect(boolean isSelect) {
        setCheck(isSelect);
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setTickCount(long tick) {
        this.tick = tick;
    }

    public void setArtist(String strArtist) {
        this.artist = strArtist;
    }

    public void setDuration(String strDuration) {
        this.duration = strDuration;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /** 在相似图片中的位置 */
    public int getIndex() {
        return index;
    }

    public String getDuration() {
        if (!TextUtils.isEmpty(this.duration)
                && TextUtils.isDigitsOnly(this.duration)) {
            return this.duration;
        }
        return "0";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(super.getJunkDataType());
        dest.writeString(title);
        dest.writeString(path);
        dest.writeLong(getSize());
        dest.writeInt(mediaType);
        dest.writeString(mimeType);
        dest.writeInt(isCheck() ? 1 : 0);
        dest.writeLong(tick);
        dest.writeString(artist);
        dest.writeString(duration);
        dest.writeLong(lastModified);
        dest.writeLong(id);
        dest.writeInt(index);
        dest.writeLong(dateTaken);
//		dest.writeLong(lastModified);
    }

    public static final Parcelable.Creator<MediaFile> CREATOR = new Creator<MediaFile>() {

        @Override
        public MediaFile[] newArray(int size) {
            return new MediaFile[size];
        }

        @Override
        public MediaFile createFromParcel(Parcel source) {

            MediaFile info = new MediaFile((JunkRequest.EM_JUNK_DATA_TYPE)source.readValue(JunkRequest.EM_JUNK_DATA_TYPE.class.getClassLoader()));
            info.title = source.readString();
            info.path = source.readString();
            info.setSize(source.readLong());
            info.mediaType = source.readInt();
            info.mimeType = source.readString();
            info.setCheck(source.readInt() == 1 ? true : false);
            info.tick = source.readLong();
            info.artist = source.readString();
            info.duration = source.readString();
            info.lastModified = source.readLong();
            info.id=source.readLong();
            info.index=source.readInt();
            info.dateTaken = source.readLong();
//			info.lastModified=source.readLong();
            return info;
        }
    };

    public boolean equals(Object o) {
        if (o == null || !(o instanceof MediaFile)) {
            return false;
        }
        if(this.path ==null){
            return false;
        }
        return this.path.equals(((MediaFile) o).path);
    }

    @Override
    public int compareTo(BaseJunkBean another) {
        MediaFile md = (MediaFile)another;
        int typeWeight1 = this.GetTypeWight(this.getMediaType());
        int typeWeight2 = md.GetTypeWight(md.getMediaType());

        if (typeWeight1 > typeWeight2) {
            return -1;
        } else if (typeWeight1 < typeWeight2)
            return 1;

        if (this.tick > md.tick) {
            return 1;
        } else if (this.tick < md.tick)
            return -1;
        else{
            if(this.path ==null){
                return -1;
            }else{
                return this.path.compareTo(md.path);
            }
        }
    };

    private int GetTypeWight(int type) {
        int val = 0;
        switch (type) {
            case MEDIA_TYPE_IMAGE:
            case MEDIA_TYPE_VIDEO:
                val = 2;
                break;
            case MEDIA_TYPE_AUDIO:
                val = 1;
                break;
            case MEDIA_TYPE_NONE:
                val = 0;
                break;
            default:
                break;
        }
        return val;
    }

    public void setCloudState(int cloudState) {
        this.cloudState = cloudState;
    }

    public int getCloudState() {
        return cloudState;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

}

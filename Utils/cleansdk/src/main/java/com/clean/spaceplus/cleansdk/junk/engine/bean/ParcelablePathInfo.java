package com.clean.spaceplus.cleansdk.junk.engine.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/23 16:24
 * @copyright TCL-MIG
 */
public class ParcelablePathInfo implements Parcelable{
    public String path = null;
    public long time = 0L;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeLong(time);
    }

    public static final Parcelable.Creator<ParcelablePathInfo> CREATOR = new Parcelable.Creator<ParcelablePathInfo>() {

        @Override
        public ParcelablePathInfo createFromParcel(Parcel source) {

            ParcelablePathInfo rst = new ParcelablePathInfo();
            rst.path = source.readString();
            rst.time = source.readLong();

            return rst;
        }

        @Override
        public ParcelablePathInfo[] newArray(int size) {
            return new ParcelablePathInfo[size];
        }
    };
}

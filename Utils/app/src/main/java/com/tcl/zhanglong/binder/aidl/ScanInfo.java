package com.tcl.zhanglong.binder.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Steve on 17/4/7.
 */

public class ScanInfo implements Parcelable{


    protected ScanInfo(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScanInfo> CREATOR = new Creator<ScanInfo>() {
        @Override
        public ScanInfo createFromParcel(Parcel in) {
            return new ScanInfo(in);
        }

        @Override
        public ScanInfo[] newArray(int size) {
            return new ScanInfo[size];
        }
    };
}

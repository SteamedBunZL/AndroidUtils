package com.clean.spaceplus.cleansdk.boost.engine.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zengtao.kuang
 * @Description: 手机内存信息
 * @date 2016/4/6 14:28
 * @copyright TCL-MIG
 */
public class PhoneMemoryInfo implements IPhoneMemoryInfo {

    public long mTotalMemoryByte;
    public long mAvailableMemoryByte;
    public int mUsedMemoryPercent;
    public int mState;
    public long mAvailableMemoryByteReal;
    public boolean mIsCache;

    PhoneMemoryInfo () {

    }

    public  PhoneMemoryInfo(long available, long total) {
        flush(available, total);
    }

    public void flush(long available, long total) {
        mAvailableMemoryByteReal = available;
        mTotalMemoryByte = total;

        mState = STATE_REAL_DATA;
        mAvailableMemoryByte = available;

        if (0 < mTotalMemoryByte && (mTotalMemoryByte > mAvailableMemoryByte)) {
            mUsedMemoryPercent = (int) (((mTotalMemoryByte - mAvailableMemoryByte) * 100f) / mTotalMemoryByte);
        } else {
            mUsedMemoryPercent = DEFAULT_USED_MEMORY_PERCENT; // default
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public long getTotalMemoryByte() {
        return mTotalMemoryByte;
    }

    @Override
    public long getAvailableMemoryByte() {
        return mAvailableMemoryByte;
    }


    @Override
    public int getUsedMemoryPercentage() {
        return mUsedMemoryPercent;
    }

    @Override
    public boolean isInCache() {
        return mIsCache;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTotalMemoryByte);
        dest.writeLong(mAvailableMemoryByte);
        dest.writeInt(mUsedMemoryPercent);
        dest.writeInt(mState);
        dest.writeLong(mAvailableMemoryByteReal);
        dest.writeBooleanArray(new boolean[]{mIsCache});
    }

    Parcelable.Creator<IPhoneMemoryInfo> CREATOR = new Parcelable.Creator<IPhoneMemoryInfo>() {
        @Override
        public IPhoneMemoryInfo createFromParcel(Parcel source) {
            PhoneMemoryInfo pmi = new PhoneMemoryInfo();
            pmi.mTotalMemoryByte = source.readLong();
            pmi.mAvailableMemoryByte = source.readLong();
            pmi.mUsedMemoryPercent = source.readInt();
            pmi.mState = source.readInt();
            pmi.mAvailableMemoryByteReal = source.readLong();

            boolean[] booleanArry = source.createBooleanArray();
            if(booleanArry != null && booleanArry.length == 1){
                pmi.mIsCache = booleanArry[0];
            }

            return pmi;
        }

        @Override
        public IPhoneMemoryInfo[] newArray(int size) {
            return new PhoneMemoryInfo[size];
        }
    };

}


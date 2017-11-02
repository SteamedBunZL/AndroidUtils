package com.clean.spaceplus.cleansdk.appmgr.service;

import android.os.Parcel;
import android.os.Parcelable;

import com.clean.spaceplus.cleansdk.util.DateUtils;
import com.clean.spaceplus.cleansdk.util.builder.HashCodeBuilder;

import java.util.Date;

/**
 * @author wangtianbao
 * @Description: App使用信息记录
 * @date 2016/5/11 11:21
 * @copyright TCL-MIG
 */
public class AppUsedInfoRecord implements Parcelable {

    public AppUsedFreqInfo mAppUsedFreqInfo;
    public String mRecordDate;
    public long mId;

    protected AppUsedInfoRecord(Parcel in) {
        mAppUsedFreqInfo=in.readParcelable(AppUsedFreqInfo.class.getClassLoader());
        mRecordDate = in.readString();
        mId = in.readLong();
    }

    public AppUsedInfoRecord(){

    }



    public static AppUsedInfoRecord createRecord(long id, String pkgName, long lastOpenTime, String recordDate, int totalOpenCount, long totalOpenTime) {
        AppUsedInfoRecord record = new AppUsedInfoRecord();
        record.mId = id;
        record.mRecordDate = recordDate;
        record.mAppUsedFreqInfo = new AppUsedFreqInfo(pkgName);
        record.mAppUsedFreqInfo.setLastOpenTime(lastOpenTime);
        record.mAppUsedFreqInfo.setTotalOpenCount(totalOpenCount);
        record.mAppUsedFreqInfo.setTotalOpenTime(totalOpenTime);
        return record;
    }

    /**
     * 创建一个空的记录
     *
     * @param pkgName
     * @return
     */
    public static AppUsedInfoRecord createRecord(String pkgName) {
        AppUsedInfoRecord record = new AppUsedInfoRecord();
        record.mRecordDate = DateUtils.dbFormat(new Date());
        record.mAppUsedFreqInfo = new AppUsedFreqInfo(pkgName);
        return record;
    }

    @Override
    public String toString() {
        return "recordDate:" + mRecordDate.toString() + "," + mAppUsedFreqInfo.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mRecordDate).append(mAppUsedFreqInfo).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AppUsedInfoRecord) {
            AppUsedInfoRecord record = (AppUsedInfoRecord) o;
            return mRecordDate.equals(record.mRecordDate) && mAppUsedFreqInfo.equals(((AppUsedInfoRecord) o).mAppUsedFreqInfo);
        }
        return super.equals(o);
    }

    /**
     * 日期是否过期
     *
     * @return
     */
    public boolean isExpire() {
        Date dbDate=DateUtils.dbParse(mRecordDate);
        return !DateUtils.isSameDay(dbDate,new Date());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mAppUsedFreqInfo,flags);
        dest.writeString(mRecordDate);
        dest.writeLong(mId);
    }

    public static final Creator<AppUsedInfoRecord> CREATOR = new Creator<AppUsedInfoRecord>() {
        @Override
        public AppUsedInfoRecord createFromParcel(Parcel in) {
            return new AppUsedInfoRecord(in);
        }

        @Override
        public AppUsedInfoRecord[] newArray(int size) {
            return new AppUsedInfoRecord[size];
        }
    };
}

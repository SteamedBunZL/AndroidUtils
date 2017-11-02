package com.clean.spaceplus.cleansdk.appmgr.service;

import android.os.Parcel;
import android.os.Parcelable;

import com.clean.spaceplus.cleansdk.util.DateUtils;
import com.clean.spaceplus.cleansdk.util.builder.HashCodeBuilder;

/**
 * @author wangtianbao
 * @Description:App使用model
 * @date 2016/4/20 15:31
 * @copyright TCL-MIG
 */
public class AppUsedFreqInfo implements Parcelable {
    private String packageName;
    private long lastOpenTime;//最后打开app时间
    private long totalOpenTime;//总共运行时长
    private int totalOpenCount;//总共打开app次数

    public AppUsedFreqInfo(String pkgName){
        this.packageName=pkgName;
    }

    protected AppUsedFreqInfo(Parcel in) {
        packageName = in.readString();
        lastOpenTime = in.readLong();
        totalOpenTime = in.readLong();
        totalOpenCount = in.readInt();
    }

    public static final Creator<AppUsedFreqInfo> CREATOR = new Creator<AppUsedFreqInfo>() {
        @Override
        public AppUsedFreqInfo createFromParcel(Parcel in) {
            return new AppUsedFreqInfo(in);
        }

        @Override
        public AppUsedFreqInfo[] newArray(int size) {
            return new AppUsedFreqInfo[size];
        }
    };

    /**最后打开时间 ms*/
    public long getLastOpenTime() {
        return lastOpenTime;
    }
    /**最后打开时间 ms*/
    public void setLastOpenTime(long lastOpenTime) {
        this.lastOpenTime = lastOpenTime;
    }


    public long getTotalOpenTime(){
        return totalOpenTime;
    }

    public int getTotalOpenCount(){
        return totalOpenCount;
    }

    public void setTotalOpenCount(int count){
        this.totalOpenCount=count;
    }

    public void setTotalOpenTime(long openTime){
        this.totalOpenTime=openTime;
    }

    /**总体打开时间ms*/
//    public long getTotalOpenTime() {
//        return totalOpenTime;
//    }
    /**总体打开时间ms*/
    public void addTime(long time) {
        this.totalOpenTime += time;
    }

    /**总体打开次数*/
//    public int getTotalOpenCount() {
//        return totalOpenCount;
//    }
    /**总体打开次数*/
    public void addOpenCount() {
        this.totalOpenCount ++;
    }
    /**app的包名*/
    public String getPackageName() {
        return packageName;
    }
    /**app的包名*/
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void reset(){
        this.totalOpenCount=0;
        this.totalOpenTime=0;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof AppUsedFreqInfo){
            return packageName.equals(((AppUsedFreqInfo)o).getPackageName());
        }
       return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(packageName).hashCode();
    }

    @Override
    public String toString() {
        return "name:"+packageName+",最后打开时间:"+ DateUtils.simpleFormatLong(lastOpenTime)+",总共打开:"+ (totalOpenTime/1000)+"秒,总共打开次数:"+totalOpenCount+"次";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
        dest.writeLong(lastOpenTime);
        dest.writeLong(totalOpenTime);
        dest.writeInt(totalOpenCount);
    }
}

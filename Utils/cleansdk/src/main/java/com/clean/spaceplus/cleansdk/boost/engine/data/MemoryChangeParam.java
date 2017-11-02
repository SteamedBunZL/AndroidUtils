package com.clean.spaceplus.cleansdk.boost.engine.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zengtao.kuang
 * @Description: 内存变化参数
 * @date 2016/4/6 15:33
 * @copyright TCL-MIG
 */
public class MemoryChangeParam implements Parcelable {

    /**让内存缓存失效*/
    public static final int OP_ON_CLEAN_CACHE_NORMAL = 1;

    /**如果当前是长超时，变更为短超时；短超时，不管
     * @note 当前进程为launcher时候调用
     * */
    public static final int OP_ON_CLEAN_CACHE_LONG = 2;

    /**其他入口执行内存清理，超时短点*/
    public static final int OP_ON_MEMORY_BOOST_NORMAL = 11;
    /**在界面执行内存清理，超时长点*/
    public static final int OP_ON_MEMORY_BOOST_LONG = 12;

    /**
     * @param operation 操作类型 OP_ON_CLEAN_CACHE/OP_ON_MEMORY_BOOST
     * */
    public MemoryChangeParam(int operation, long mem) {
        mOperation = operation;
        mCachedMemory = mem;
    }

    public MemoryChangeParam() {

    }

    public long getCachedMemory() {
        return mCachedMemory;
    }

    public int getOperation() {
        return mOperation;
    }

    private int mOperation;
    private long mCachedMemory;

    // -----------parcel -------

    public static final Creator<MemoryChangeParam> CREATOR = new Creator<MemoryChangeParam>() {
        @Override
        public MemoryChangeParam createFromParcel(Parcel source) {
            MemoryChangeParam mc = new MemoryChangeParam();
            mc.mOperation = source.readInt();
            mc.mCachedMemory = source.readLong();

            return mc;
        }

        @Override
        public MemoryChangeParam[] newArray(int size) {
            return new MemoryChangeParam[size];
        }
    };


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mOperation);
        dest.writeLong(mCachedMemory);
    }


    @Override
    public int describeContents() {
        return 0;
    }

}
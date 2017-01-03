package com.tcl.zhanglong.utils.IPC;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Steve on 16/12/17.
 */

public class User implements Parcelable{

    public int userId;
    public String userName;
    public boolean isMale;

    public Book book;

    public User(int userId, String userName, boolean isMale) {
        this.userId = userId;
        this.userName = userName;
        this.isMale = isMale;
    }

    /**
     * 从序列化后的对象上创建原始对象
     * @param in
     */
    protected User(Parcel in) {
        userId = in.readInt();
        userName = in.readString();
        isMale = in.readInt() ==1;
        //当前线程的类加载器
        book = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    /**
     * 将当前对象写入序列化结构中,其中flags标记有两种,1或者0
     * 为1时标识当前对象需要作为返回值返回,不能立即释放资源,几乎所有的情况都为0
     * @param out
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(userId);
        out.writeString(userName);
        out.writeInt(isMale?1:0);
        out.writeParcelable(book,0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //反序列化
    public static final Creator<User> CREATOR = new Creator<User>() {
        /**
         * 从序列化后的对象上创建原始对象
         * @param in
         * @return
         */
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        /**
         * 创建指定长度的原始对象数组
         * @param size
         * @return
         */
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


}

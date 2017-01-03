package com.tcl.zhanglong.utils.IPC;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Steve on 16/12/17.
 */

public class Book implements Parcelable{

    public int bookId;
    public String bookName;


    protected Book(Parcel in) {
        this.bookId = in.readInt();
        this.bookName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(bookId);
        out.writeString(bookName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}

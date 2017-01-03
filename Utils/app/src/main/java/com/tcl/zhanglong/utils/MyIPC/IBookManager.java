package com.tcl.zhanglong.utils.MyIPC;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import com.tcl.zhanglong.utils.IPC.Book;

import java.util.List;

/**
 * Created by Steve on 16/12/19.
 */

public interface IBookManager extends IInterface{

    static final String descriptor = "com.tcl.zhanglong.utils.MyIPC.IBookManager";

    static final int TRANSACTION_getBookList = IBinder.FIRST_CALL_TRANSACTION + 0;

    static final int TRANSACTION_addBook = IBinder.FIRST_CALL_TRANSACTION + 1;

    public List<Book> getBookList() throws RemoteException;

    public void addBook(Book book) throws RemoteException;
}

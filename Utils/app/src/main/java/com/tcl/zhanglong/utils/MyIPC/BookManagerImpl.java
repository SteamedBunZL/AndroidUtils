package com.tcl.zhanglong.utils.MyIPC;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import com.tcl.zhanglong.utils.IPC.Book;

import java.util.List;

/**
 * Created by Steve on 16/12/19.
 */

public class BookManagerImpl extends Binder implements IBookManager{


    public BookManagerImpl(){
        this.attachInterface(this,IBookManager.descriptor);
    }

    public static IBookManager asInterface(IBinder obj){
        if (obj==null)
            return null;

        IInterface iin = obj.queryLocalInterface(IBookManager.descriptor);
        if (iin!=null&&(iin instanceof IBookManager))
            return (IBookManager) iin;

        return new BookManagerImpl.Proxy(obj);
    }

    @Override
    public List<Book> getBookList() throws RemoteException {
        //TODO 待实现
        return null;
    }

    @Override
    public void addBook(Book book) throws RemoteException {
        //TODO 待实现
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code){
            case INTERFACE_TRANSACTION:
                reply.writeString(IBookManager.descriptor);
                return true;
            case IBookManager.TRANSACTION_getBookList:
                data.enforceInterface(IBookManager.descriptor);
                List<Book> result = this.getBookList();
                reply.writeNoException();
                reply.writeTypedList(result);
                return true;
            case IBookManager.TRANSACTION_addBook:
                data.enforceInterface(IBookManager.descriptor);
                Book arg;
                if (0!=data.readInt()){
                    arg = Book.CREATOR.createFromParcel(data);
                }else{
                    arg = null;
                }
                this.addBook(arg);
                reply.writeNoException();
                return true;

        }
        return super.onTransact(code, data, reply, flags);
    }

    private static class Proxy implements IBookManager{

        private IBinder mRemote;

        Proxy(IBinder remote){
            mRemote = remote;
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            List<Book> result;
            try{
                data.writeInterfaceToken(IBookManager.descriptor);
                mRemote.transact(IBookManager.TRANSACTION_getBookList,data,reply,0);
                reply.readException();
                result = reply.createTypedArrayList(Book.CREATOR);
            }finally {
                reply.recycle();
                data.recycle();
            }
            return result;
        }

        @Override
        public void addBook(Book book) throws RemoteException {

        }

        @Override
        public IBinder asBinder() {
            return mRemote;
        }
    }
}

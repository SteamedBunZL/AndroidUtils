/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/zhanglong/AndroidStudioProjects/github/AndroidUtils/Utils/app/src/main/aidl/com/tcl/zhanglong/util/IBookManager.aidl
 */
package com.tcl.zhanglong.utils.IPC;
// Declare any non-default types here with import statements

public interface IBookManager extends android.os.IInterface
{
    //这个Sub就是一个binder类，当客户端和服务端位于同一个进程时，方法调用不会走跨进程的transact过程；
    //而当两者位于不同进程时，方法调用需要走transact过程，这个逻辑由Stub内部代理类Proxy来完成
    public static abstract class Stub extends android.os.Binder implements com.tcl.zhanglong.util.IBookManager
    {
        //Binder的唯一标识，一般用当前Binder的类名表示
        private static final java.lang.String DESCRIPTOR = "com.tcl.zhanglong.util.IBookManager";
        /** Construct the stub at attach it to the interface. */
        public Stub()
        {
            this.attachInterface(this, DESCRIPTOR);
        }

        //用于将服务端的Binder对象换成客户端所需的AIDL接口类型的对象，这种转换过程是区分进程的，
        //如果客户端和服务端位于同一进程，那么此方法返回的就是服务端的Stub对象本身，否则返回的是系统封装后的Stub.proxy对象。
        public static com.tcl.zhanglong.util.IBookManager asInterface(android.os.IBinder obj)
        {
            if ((obj==null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin!=null)&&(iin instanceof com.tcl.zhanglong.util.IBookManager))) {
                return ((com.tcl.zhanglong.util.IBookManager)iin);
            }
            return new com.tcl.zhanglong.util.IBookManager.Stub.Proxy(obj);
        }
        //此方法用于返回当前Binder对象
        @Override public android.os.IBinder asBinder()
        {
            return this;
        }
        //这方法运行在服务端中的Binder线程池中，当客户端发起跨进程请求时，远程请求会通过系统底层封装后交由此方法来处理。
        //服务端通过code可以确定客户端所请求的目标方法是什么，接着从data中取出目标方法所需的参数（如果目标方法有参数的话），
        //然后执行目标方法。当目标方法执行完后，就向reply中写入返回值（如果目标方法有返回值的话），onTransact方法的执行
        //过程就是这样的。需要注意的是，如果此方法返回false那么客户端请求失败，因此我们可以利用这个特性来做权限验证，避免一个进程都能远程调用我们的服务
        @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
            switch (code)
            {
                case INTERFACE_TRANSACTION:
                {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_getBookList:
                {
                    data.enforceInterface(DESCRIPTOR);
                    java.util.List<com.tcl.zhanglong.util.Book> _result = this.getBookList();
                    reply.writeNoException();
                    reply.writeTypedList(_result);
                    return true;
                }
                case TRANSACTION_addBook:
                {
                    data.enforceInterface(DESCRIPTOR);
                    com.tcl.zhanglong.util.Book _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = com.tcl.zhanglong.util.Book.CREATOR.createFromParcel(data);
                    }
                    else {
                        _arg0 = null;
                    }
                    this.addBook(_arg0);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }
        private static class Proxy implements com.tcl.zhanglong.util.IBookManager
        {
            private android.os.IBinder mRemote;
            Proxy(android.os.IBinder remote)
            {
                mRemote = remote;
            }
            @Override public android.os.IBinder asBinder()
            {
                return mRemote;
            }
            public java.lang.String getInterfaceDescriptor()
            {
                return DESCRIPTOR;
            }
            //这个方法运行在客户端，当客户端调用此方法时，它的内部实现是这样的:首先创建该方法所需要的的输入型Parcel对象_data、
            //输出型对象Parcel对象_reply和返回值对象List;然后把该方法的参数写入_data,接着调用transact方法来发起RPC(远程调用请求)
            //同时当前线程挂起；然后服务端的onTranscat方法会被调用直到RPC过程返回，当前线程继续执行并从_reply中取出RPC过程返回结果；最后返回_reply上的数据
            @Override public java.util.List<com.tcl.zhanglong.util.Book> getBookList() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.util.List<com.tcl.zhanglong.util.Book> _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getBookList, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.createTypedArrayList(com.tcl.zhanglong.util.Book.CREATOR);
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
            @Override public void addBook(com.tcl.zhanglong.util.Book book) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((book!=null)) {
                        _data.writeInt(1);
                        book.writeToParcel(_data, 0);
                    }
                    else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
        static final int TRANSACTION_getBookList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_addBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    }
    public java.util.List<com.tcl.zhanglong.util.Book> getBookList() throws android.os.RemoteException;
    public void addBook(com.tcl.zhanglong.util.Book book) throws android.os.RemoteException;
}

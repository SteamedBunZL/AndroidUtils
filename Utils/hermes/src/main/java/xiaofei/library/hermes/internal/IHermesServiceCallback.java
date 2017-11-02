/**
 *
 * Copyright 2016 Xiaofei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package xiaofei.library.hermes.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import java.util.List;

public interface IHermesServiceCallback extends IInterface {

    abstract class Stub extends Binder implements IHermesServiceCallback {

        //Binder的唯一标识，一般用当前的Binder的类名表示。
        private static final String DESCRIPTOR = "xiaofei.library.hermes.internal.IHermesServiceCallback";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * 用于将服务端的Binder对象转换成客户端所需的AIDL接口类型的对象，这种转换过程是区分进程的，如果客户
         * 端和服务端位于同一进程，那么此方法返回的就是服务端的Stub对象本身，否则返回的是系统封装后的Stub.proxy对象
         * @param obj
         * @return
         */
        public static IHermesServiceCallback asInterface(IBinder obj) {
            if ((obj==null)) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin!=null)&&(iin instanceof IHermesServiceCallback))) {
                return ((IHermesServiceCallback)iin);
            }
            return new Proxy(obj);
        }

        /**
         * 此方法用于返回当前的Binder对象。
         * @return
         */
        @Override
        public IBinder asBinder() {
            return this;
        }

        /**
         * 这个方法运行在服务端中的Binder线程池中，当客户端发起跨进程请求时，远程请求会通过系统底层封装后交由此方法
         * 来处理。该方法的原型为public Boolean onTransact(int code,android.os.Parcel data,android.os.Parcel
         *  reply,int flags)。服务端通过code可以确定客户端所请求的目标方法是什么，接着从data中取出目标方法所需要的参数，
         *  然后执行目标方法。当目标方法执行完毕后，就向reply中写入返回值，onTransact方法的执行过程就是这样的。需要
         *  注意的是，如果此方法返回false，那么客户端的请求会失败，因此我们可以利用这个我来做权限验证，毕竟我们也不希望随便
         *  一个进程都能远程调用我们的服务。
         * @param code
         * @param data
         * @param reply
         * @param flags
         * @return
         * @throws RemoteException
         */
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION:
                    reply.writeString(DESCRIPTOR);
                    return true;
                case TRANSACTION_callback:
                    data.enforceInterface(DESCRIPTOR);
                    CallbackMail _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = CallbackMail.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    Reply _result = this.callback(_arg0);
                    reply.writeNoException();
                    if ((_result!=null)) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_gc:
                    data.enforceInterface(DESCRIPTOR);
                    List list1, list2;
                    ClassLoader cl = this.getClass().getClassLoader();
                    list1 = data.readArrayList(cl);
                    list2 = data.readArrayList(cl);
                    this.gc(list1, list2);
                    reply.writeNoException();
                    return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IHermesServiceCallback {

            private IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            /**
             * 这个方法运行在客户端，当客户端远程调用此方法时，这经的内部实现是这样的：首先创建该方法所需要的输入型Parcel
             * 对象_data、输出型Parcel对象_reply和返回值对象List；然后把该方法的参数信息写入_data中，接着调用transact
             * 方法来发起RPC请求，同时当前线程挂起；然后服务端的onTransact方法会被调用，直到RPC过程返回后，当前线程继续执行
             * 并从_reply中取RPC过程的返回结果 ；最后返回_reply中的数据。
             * @param mail
             * @return
             * @throws RemoteException
             */
            @Override
            public Reply callback(CallbackMail mail) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                Reply _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((mail!=null)) {
                        _data.writeInt(1);
                        mail.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_callback, _data, _reply, 0);
                    _reply.readException();
                    if ((0!=_reply.readInt())) {
                        _result = Reply.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void gc(List<Long> timeStamps, List<Integer> indexes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeList(timeStamps);
                    _data.writeList(indexes);
                    mRemote.transact(Stub.TRANSACTION_gc, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_callback = IBinder.FIRST_CALL_TRANSACTION;

        static final int TRANSACTION_gc = IBinder.FIRST_CALL_TRANSACTION + 1;
    }

    Reply callback(CallbackMail mail) throws RemoteException;

    /**
     * http://business.nasdaq.com/marketinsite/2016/Indexes-or-Indices-Whats-the-deal.html
     *
     * This article says something about the plural form of "index".
     */
    void gc(List<Long> timeStamps, List<Integer> indexes) throws RemoteException;

}
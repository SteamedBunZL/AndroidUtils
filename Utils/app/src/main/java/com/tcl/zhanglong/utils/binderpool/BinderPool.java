package com.tcl.zhanglong.utils.binderpool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.tcl.zhanglong.binder.aidl.IBinderPool;

/**
 * Created by Steve on 17/4/7.
 */

public class BinderPool {

    public static final int BINDER_SCAN_APK = 0;
    public static final int BINDER_SCAN_FILE = 1;

    private IBinderPool mBinderPool;
    private Context mContext;

    private static volatile BinderPool sInstance;

    private BinderPool(Context context){
        mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context){
        if (sInstance == null){
            synchronized (BinderPool.class){
                if (sInstance == null){
                    sInstance = new BinderPool(context);
                }
            }
        }
        return sInstance;
    }

    private synchronized void connectBinderPoolService(){
        Intent service = new Intent(mContext,ScanService.class);
        mContext.bindService(service,mBinderPoolConnection,Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public static class BinderPoolImpl extends IBinderPool.Stub {

        public BinderPoolImpl() {
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode){
                case BINDER_SCAN_APK:
                    binder = new ScanApkImpl();
                    break;
                case BINDER_SCAN_FILE:
                    binder = new ScanFileImpl();
                    break;
            }
            return binder;
        }
    }

    public IBinder queryBinder(int binderCode){
        IBinder binder = null;
        try {
            if (mBinderPool!=null){
                binder = mBinderPool.queryBinder(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }







}

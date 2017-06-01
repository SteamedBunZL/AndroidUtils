// IBinderPool.aidl
package com.tcl.zhanglong.binder.aidl;

// Declare any non-default types here with import statements

interface IBinderPool {

    IBinder queryBinder(int binderCode);
}

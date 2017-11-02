package com.clean.spaceplus.cleansdk.junk.engine.aidl;

/**
 * @author liangni
 * @Description:
 * @date 2016/5/3 17:23
 * @copyright TCL-MIG
 */
public interface JunkService {
    public long queryJunkSize(int type) throws android.os.RemoteException;
    public void notifyJunkSize(int type, long size) throws android.os.RemoteException;
    public long substractJunkSize(int type, long size) throws android.os.RemoteException;
}

package com.clean.spaceplus.cleansdk.boost.engine.data;

import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zengtao.kuang
 * @Description: boost数据管理
 * @date 2016/4/5 20:28
 * @copyright TCL-MIG
 */
public class BoostDataManager {

    private final Map<Integer, BoostResult> mDataMap = new ArrayMap<Integer, BoostResult>();
    private final Map<Integer, Boolean> mScanStatus = new ArrayMap<Integer, Boolean>();
    private final Map<Integer, List<DataUpdateCallback>> mCallbacks = new ArrayMap<Integer, List<DataUpdateCallback>>();

    public interface BoostDataManagerHolder{
        BoostDataManager INSTANCE = new BoostDataManager();
    }

    public static BoostDataManager getInstance() {
        return BoostDataManagerHolder.INSTANCE;
    }

    public BoostResult getResult(int type) {
        if (isScanning(type)) {
            return null;
        }

        BoostResult result;
        synchronized (mDataMap) {
            result = mDataMap.get(type);
        }

        if (result != null && result.isDataValid()) {
            return result;
        } else {
            return null;
        }
    }

    public void updateResult(int type, BoostResult data) {
//        if (data == null) {
//            return;
//        }

        synchronized (mDataMap) {
            if(data==null){
                mDataMap.remove(type);
            }else{
                mDataMap.put(type, data);
            }
        }
        setScan(type, false);

        List<DataUpdateCallback> callbacks;

        synchronized (mCallbacks) {
            callbacks = mCallbacks.get(type);
        }

        if (callbacks != null) {
            for (DataUpdateCallback cb : callbacks) {
                cb.onDataUpdate(data);
            }
            callbacks.clear();
        }
    }

    public void setScan(int type, boolean isScan) {
        synchronized (mScanStatus) {
            mScanStatus.put(type, isScan);
        }
    }

    public boolean isScanning(int type) {
        Boolean isScan;
        synchronized (mScanStatus) {
            isScan = mScanStatus.get(type);
        }
        return isScan != null && isScan;
    }

    public boolean isDataValid(int type) {
        BoostResult data;
        synchronized (mDataMap) {
            data = mDataMap.get(type);
        }
        return data != null && data.isDataValid();
    }

    public void registerCallback(int type, DataUpdateCallback callback) {
        List<DataUpdateCallback> callbacks;
        synchronized (mCallbacks) {
            if (mCallbacks.containsKey(type)) {
                callbacks = mCallbacks.get(type);
            } else {
                callbacks = new ArrayList<DataUpdateCallback>();
                mCallbacks.put(type, callbacks);
            }
        }

        if (callbacks != null) {
            callbacks.add(callback);
        }
    }

    public interface DataUpdateCallback {
        void onDataUpdate(Object data);
    }
}

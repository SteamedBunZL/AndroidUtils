package com.clean.spaceplus.cleansdk.boost.engine.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: 加速结果集抽象类
 * @date 2016/4/5 20:29
 * @copyright TCL-MIG
 */
public abstract class BoostResult<E> {
    protected List<E> mData = null;
    protected Object mLock = new Object();

    public List<E> getData() {
        synchronized (mLock) {
            if (null == mData) {
                return null;
            }
            return new ArrayList<E>(mData);
        }
    }

    public void updateData(List<E> data) {
        synchronized (mLock) {
            mData = data;
        }
    }

    public void updatePrivateData() {
        synchronized (mLock) {
            updatePrivateDataLocked();
        }
    }

    public void removeData(Object key) {
        synchronized (mLock) {
            removeDataLocked(key);
        }
    }

    public boolean isDataValid() {
        synchronized (mLock) {
            if (mData != null) {
                return isDataValidLocked();
            } else {
                return false;
            }
        }
    }

    protected abstract void updatePrivateDataLocked();
    protected abstract void removeDataLocked(Object key);
    protected abstract boolean isDataValidLocked();

}

package com.clean.spaceplus.cleansdk.junk.engine;

import android.support.v4.util.ArrayMap;

import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.lang.ref.SoftReference;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/27 21:00
 * @copyright TCL-MIG
 */
public class ObjPoolMgr {

    private static ObjPoolMgr mInstance = new ObjPoolMgr();

    public static ObjPoolMgr getInstance() {
        return mInstance;
    }

    private ObjPoolMgr() {
    }

    private ArrayMap<Class, SoftReference<Object>> mObjPoolMap = new ArrayMap<>();

    public <E extends KPoolObj>
    KObjPool<E> getObjPool(Class<E> cls, int nPoolSize, IKPoolObjCreator<E> creator) {

        if (null == cls || null == creator) {
            if(PublishVersionManager.isTest()){
                throw new NullPointerException();
            }
        }

        Object pool = null;
        synchronized (mObjPoolMap) {
            SoftReference<Object> refPool = mObjPoolMap.get(cls);
            if (null != refPool) {
                pool = refPool.get();
            }
            if (null == pool) {
                pool = new KObjPool<>(nPoolSize, creator);
                mObjPoolMap.put(cls, new SoftReference<>(pool));
            }
        }

        assert (null != pool);
        assert (pool instanceof KObjPool<?>);

        return (KObjPool<E>)pool;
    }


    public static abstract class KPoolObj {

        public abstract void reset();

        private KPoolObj mNext = null;

        KPoolObj getNext() {
            return mNext;
        }

        void setNext(KPoolObj next) {
            mNext = next;
        }
    }

    public  interface IKPoolObjCreator<E extends KPoolObj> {
         E create();
    }


    public static class KObjPool<E extends KPoolObj> {

        private int mPoolSize;
        private IKPoolObjCreator<E> mCreator;
        private Object mMutex = new Object();
        private E mPooledObj = null;

        public KObjPool(int nPoolSize, IKPoolObjCreator<E> creator) {

            if (nPoolSize <= 0 && PublishVersionManager.isTest()) {
                throw new IllegalArgumentException();
            }

            if (null == creator && PublishVersionManager.isTest()) {
                throw new NullPointerException();
            }

            mPoolSize = nPoolSize;
            mCreator = creator;
        }

        public E obtainObj() {
            if (null == mPooledObj) {
                return mCreator.create();
            }

            synchronized (mMutex) {
                if (null == mPooledObj) {
                    return mCreator.create();
                }

                E topObj = mPooledObj;
                mPooledObj = (E)mPooledObj.getNext();
                topObj.setNext(null);
                ++mPoolSize;
                return topObj;
            }
        }

        public void recycleObj(E o) {

            if (null == o) {
                return;
            }

            o.reset();

            if (mPoolSize <= 0) {
                return;
            }

            synchronized (mMutex) {
                if (mPoolSize <= 0) {
                    return;
                }

                o.setNext(mPooledObj);
                mPooledObj = o;
                --mPoolSize;
            }
        }
    }
}

//package com.clean.spaceplus.cleansdk.main.sync;
//
//import PublishVersionManager;
//
//import java.util.LinkedList;
//import java.util.Queue;
//
///**
// * @author liangni
// * @Description:
// * @date 2016/5/18 19:30
// * @copyright TCL-MIG
// */
//public class AsyncConsumer<E> {
//
//    public static interface ConsumerCallback<E> {
//        public void consumeProduct(E product);
//    }
//
//    public static class Builder<E> {
//        public AsyncConsumer<E> build() {
//            return new AsyncConsumer<E>(this);
//        }
//
//        //give the AsyncConsumer Thread a Name
//        public AsyncConsumer<E> build(String name) {
//            mAsyncConsumerName += name;
//            return new AsyncConsumer<E>(this);
//        }
//
//        public Builder<E> mWaitTime(int waitTime) {
//            if (waitTime <= 0) {
//                if (PublishVersionManager.isTest())
//                {
//                    throw new IllegalArgumentException("The wait time should be positive integer.");
//                }
//                mWaitTime=0;
//            }else {
//                mWaitTime = waitTime;
//            }
//            return this;
//        }
//
//        public Builder<E> mCallback(ConsumerCallback<E> callback) {
//            mCallback = callback;
//            return this;
//        }
//
//        private int mWaitTime = 1000 * 17;
//        private ConsumerCallback<E> mCallback= null;
//        private String mAsyncConsumerName = "AsyncConsumer";
//    }
//
//
//    public void addProduct(E item) {
//        if (null == item) {
//            return;
//        }
//
//        synchronized (mProductQueue) {
//            mProductQueue.offer(item);
//
//            if (null == mConsumerThread && !mIsSwitchEffect) {
//                createThread();
//            }
//            if (!mIsSwitchEffect) {
//                mProductQueue.notify();
//            }
//        }
//    }
//
//    /**
//     * 延时消费
//     * 触发createThread()
//     */
//    public void enableConsume(){
//        if (peekProductSize() <= 0) {
//            return;
//        }
//        if (mIsSwitchEffect && null == mConsumerThread) {//不允许上报，线程为空，否则允许上报
//            createThread();
//        }
//
//        if (mIsSwitchEffect) {
//            synchronized (mProductQueue) {
//                mProductQueue.notify();
//            }
//        }
//    }
//
//    public int peekProductSize() {
//        int size = 0;
//        synchronized (mProductQueue) {
//            size = mProductQueue.size();
//        }
//        return size;
//    }
//
//    public void setSwitchEffect(boolean isEffect){
//        mIsSwitchEffect = isEffect;
//    }
//
//    protected void createThread() {
//        mConsumerThread = new Thread() {
//            @Override
//            public void run() {
//
//                E item = null;
//                while (true) {
//                    item = null;
//                    synchronized(mProductQueue) {
//                        if (mProductQueue.isEmpty()) {
//                            try {
//                                mProductQueue.wait(mWaitTime);
//                                if (mProductQueue.isEmpty()) {
//                                    mConsumerThread = null;
//                                    break;
//                                }
//                            } catch (Exception e) {
//                                mConsumerThread = null;
//                                break;
//                            }
//                        }
//
//                        item = mProductQueue.poll();
//                    }
//
//                    if (null != mCallback) {
//                        mCallback.consumeProduct(item);
//                    }
//                }
//            }
//        };
//        mConsumerThread.setName(mAsyncConsumerName);
//
//        mConsumerThread.start();
//    }
//
//
//    protected AsyncConsumer(Builder<E> builder) {
//        mWaitTime = builder.mWaitTime;
//        mCallback = builder.mCallback;
//        mAsyncConsumerName = builder.mAsyncConsumerName;
//    }
//
//
//    protected Thread mConsumerThread = null;
//    protected final Queue<E> mProductQueue = new LinkedList<E>();
//    protected final int mWaitTime;
//    protected final String mAsyncConsumerName;
//    protected final ConsumerCallback<E> mCallback;
//    private boolean mIsSwitchEffect = false;//及时消费开关
//}

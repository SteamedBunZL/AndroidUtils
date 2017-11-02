package com.clean.spaceplus.cleansdk.main.bean;

/**
 * @author liangni
 * @Description:
 * @date 2016/5/18 19:28
 * @copyright TCL-MIG
 */
public class CleanedInfo {

    private static CleanedInfo smCleanedInfoSingleton = new CleanedInfo();
    private static long mTotalSize = 0L;

    public static CleanedInfo getInstance() {
        return smCleanedInfoSingleton;
    }

//    public long getTodayCleanedSize() {
//        return ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).getTodayCleanedSize();
//    }
//
//    public long getTotalCleanedSize() {
//        if(mTotalSize == 0L){
//            mTotalSize = ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).getTotalCleanedSize();
//        }
//        return mTotalSize;
//    }

//    public long getMaxCleanedSize(){
//        return  ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).getMaxCleanedSize();
//    }
//
//    public void addCleanedSize(long size) {
//        if (size <= 0) {
//            return;
//        }
//        synchronized (mMutexForAsyncConsumer) {
//            mTotalSize = getTotalCleanedSize() + size;
//            if (null == mAsyncConsumerForAddSize) {
//                mAsyncConsumerForAddSize =
//                        new AsyncConsumer.Builder<Long>().mCallback(
//                                new AsyncConsumer.ConsumerCallback<Long>() {
//
//                                    @Override
//                                    public void consumeProduct(Long product) {
//                                        ServiceConfigManager.getInstanse(
//                                                SpaceApplication.getInstance().getContext())
//                                                .addCleanedSize(product);
//                                    }
//                                }).build(this.getClass().getSimpleName());
//            }
//        }
//
//        mAsyncConsumerForAddSize.addProduct(size);
//    }

//    private AsyncConsumer<Long> mAsyncConsumerForAddSize = null;
//    private Object mMutexForAsyncConsumer = new Object();

}

//package com.clean.spaceplus.cleansdk.junk.engine.photo;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.os.Process;
//import android.support.annotation.Nullable;
//
///**
// * @author dongdong.huang
// * @Description:图片压缩service
// * @date 2016/5/16 15:58
// * @copyright TCL-MIG
// */
//public class PhotoCompressService extends Service{
//    private PhotoCompressEngineImpl mPhotoCompressEngine = null;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mPhotoCompressEngine = new PhotoCompressEngineImpl();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mPhotoCompressEngine.destory();
//        Process.killProcess(Process.myPid());
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mPhotoCompressEngine;
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        return super.onUnbind(intent);
//    }
//}

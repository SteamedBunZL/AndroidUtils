//package com.clean.spaceplus.cleansdk.junk.engine.photo;
//
//import android.os.RemoteException;
//
//import com.clean.spaceplus.cleansdk.PhotoCompressCallback;
//import com.clean.spaceplus.cleansdk.PhotoCompressEngine;
//import com.clean.spaceplus.cleansdk.base.scan.TaskControllerImpl;
//import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFile;
//import com.clean.spaceplus.cleansdk.util.FileUtils;
//import com.clean.spaceplus.cleansdk.util.ImageCompressUtil;
//import NLog;
//
//import java.io.File;
//import java.util.List;
//
///**
// * @author dongdong.huang
// * @Description:图片压缩处理
// * @date 2016/5/16 15:40
// * @copyright TCL-MIG
// */
//public class PhotoCompressEngineImpl extends PhotoCompressEngine.Stub{
//    private TaskControllerImpl mTaskCtroller = new TaskControllerImpl();
//    private String mTmpFilePath;
//
//    public interface CompressFailCallback{
//        /**
//         * @param srcFile
//         * @param tmpFile
//         * @param failReason 1失败，2压缩损坏
//         */
//        void compressFailDamagePicture(File srcFile, File tmpFile, int failReason);
//    }
//
//    @Override
//    public int getPhotoCompressPercentage(String path, String destPath, String mimeType) throws RemoteException {
//        return 0;
//    }
//
//    @Override
//    public boolean compressPhoto(final List<MediaFile> mediaFileList, final PhotoCompressCallback callback, final String failPath) throws RemoteException {
//        if(callback == null || mediaFileList == null) {
//            return false;
//        }
//
//        mTaskCtroller.notifyStop();
//        mTaskCtroller = new TaskControllerImpl();
//        final Long maxSize = getTotalMemory();
//        Thread compressThead = new Thread(){
//            @Override
//            public void run(){
//                long compressTotalSize = 0l;
//                try {
//
//                    for(MediaFile mediaFile : mediaFileList){
//                        //检查是否停止
//                        if(mTaskCtroller != null && mTaskCtroller.checkStop()){
//                            break;
//                        }
//
//                        callback.onCompressItem(mediaFile);
//                        long size = ImageCompressUtil.compressImage(mediaFile,mTaskCtroller,maxSize,failPath == null?null:new CompressFailCallback(){
//                            boolean isCopyDamagePicture = false;
//                            @Override
//                            public void compressFailDamagePicture(File srcFile, File tmpFile, int failReason) {
//                                if(!isCopyDamagePicture){
//                                    NLog.d("photo_compress", "photoCompressService#photo compress damage, path : " + srcFile + " tmp path " + tmpFile);
//                                    copyDamagePictureToCache(callback,failPath,srcFile,tmpFile,failReason);
//                                    isCopyDamagePicture = true;
//                                }
//                            }
//                        });
//
//                        if(size > 0){
//                            //压缩成功，改变原来的size并返回
//                            long srcSize = mediaFile.getSize();
//                            mediaFile.setSize(mediaFile.getSize() - size);
//                            callback.onCompressSuccess(mediaFile,srcSize);
//                            compressTotalSize += size;
//                        }else{
//                            callback.onCompressFail(mediaFile);
//                        }
//                    }
//
//                    callback.onCompressFinish(compressTotalSize);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }finally {
//                    mTaskCtroller.reset();
//                    System.gc();
//                }
//            }
//        };
//        compressThead.setName("PhotoCompressThread");
//        compressThead.start();
//
//        return true;
//    }
//
//    @Override
//    public void onStop() throws RemoteException {
//        mTaskCtroller.notifyStop();
//    }
//
//    @Override
//    public void onPause() throws RemoteException {
//        mTaskCtroller.notifyPause(0);
//    }
//
//    @Override
//    public void onResume() throws RemoteException {
//        mTaskCtroller.resumePause();
//    }
//
//    public void destory(){
//        mTaskCtroller.notifyStop();
//        deleteTmpFile();
//    }
//
//    public void deleteTmpFile(){
//        if(mTmpFilePath != null){
//            File file = new File(mTmpFilePath);
//            if(file.exists()){
//                file.delete();
//            }
//        }
//    }
//
//    private long getTotalMemory(){
//        Long maxSize = Runtime.getRuntime().maxMemory() - (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory());
//        if(maxSize < Runtime.getRuntime().maxMemory() / 2){
//            maxSize = Runtime.getRuntime().maxMemory() / 2;
//        }
//        return maxSize;
//    }
//
//    private void copyDamagePictureToCache(PhotoCompressCallback callback,String failDir,File srcFile,File tmpFile,int failReason){
//        String cacheSrcPath = failDir + "/damage_src";
//        String cacheTmpPath = failDir + "/damage_tmp";
//        File cacheSrcFile = new File(cacheSrcPath);
//        File cacheTmpFile = new File(cacheTmpPath);
//
//        if(cacheSrcFile.exists() && cacheSrcFile.isFile()){
//            cacheSrcFile.delete();
//        }
//
//        if(cacheTmpFile.exists() && cacheTmpFile.isFile()){
//            cacheTmpFile.delete();
//        }
//
//        FileUtils.copyFile(srcFile.getAbsolutePath(),cacheSrcPath);
//        FileUtils.copyFile(tmpFile.getAbsolutePath(),cacheTmpPath);
//
//        try {
//            callback.onCompressDamage(cacheSrcPath,cacheTmpPath,failReason);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//}

//package com.clean.spaceplus.cleansdk.junk.engine.photo;
//
//import android.annotation.TargetApi;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.database.Cursor;
//import android.os.Build;
//import android.os.Environment;
//import android.os.IBinder;
//import android.os.RemoteException;
//import android.provider.MediaStore;
//import android.text.TextUtils;
//
//import com.clean.spaceplus.cleansdk.BuildConfig;
//import com.clean.spaceplus.cleansdk.PhotoCompressEngine;
//import SpaceApplication;
//import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
//import com.clean.spaceplus.cleansdk.base.scan.TaskControllerImpl;
//import com.clean.spaceplus.cleansdk.junk.cleancloud.CloudCfgDataWrapper;
//import com.clean.spaceplus.cleansdk.util.Commons;
//import com.clean.spaceplus.cleansdk.junk.cleancloud.config.CloudCfgKey;
//import com.clean.spaceplus.cleansdk.junk.cleancloud.config.ServiceConfigManager;
//import com.clean.spaceplus.cleansdk.junk.engine.MediaFileOperator;
//import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFile;
//import com.clean.spaceplus.cleansdk.junk.engine.bean.StorageList;
//import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
//import com.clean.spaceplus.cleansdk.util.ImageCompressUtil;
//import com.clean.spaceplus.cleansdk.util.PhoneUtil;
//import com.clean.spaceplus.cleansdk.util.StorageUtil;
//import NLog;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicBoolean;
//
///**
// * @author dongdong.huang
// * @Description:
// * @date 2016/5/16 16:25
// * @copyright TCL-MIG
// */
//public class PhotoCompressManager {
//    /**
//     * 扫描回调接口
//     */
//    public interface PhotoScanCallback {
//
//        public void onServiceConnected();
//
//        public void onScanStart();
//        // public void onFoundItem(MyMediaFile mediaFile) ;
//        public void onScanFinished(List<MediaFile> mediaFileList, int compressPercentage);
//    }
//
//    /**
//     * 扫描回调接口
//     */
//    public interface getPhotoCompressPercentageCallback {
//
//        public void onGetCompressPercentage(String tmpPath,int compressPercentage);
//    }
//
//
//    public interface OnServiceConnectedCallback {
//
//        public void onServiceConnected();
//    }
//
//    /**
//     * 压缩回调接口
//     */
//    public interface PhotoCompressCallback {
//        public void onCompressStart();
//        public void onCompressItem(MediaFile mediaFile,int cur ,int total) ;
//        public void onCompressFinished(long compressSize) ;
//        public void onCompressSuccess(MediaFile mediaFile);
//        public void onCompressFail(MediaFile mediaFile);
//        public void onCompressDamage(String srcPath,String tmpPath,int reason);
//    }
//
//
//    private static class PhotoThread extends Thread {
//        private TaskControllerImpl mTaskCtrl = new TaskControllerImpl();
//        public void notifyPause() {
//            mTaskCtrl.notifyPause(0);
//        }
//
//        public void resumePause() {
//            mTaskCtrl.resumePause();
//        }
//
//        public void notifyStop() {
//            mTaskCtrl.notifyStop();
//        }
//
//        @Override
//        public void run() {
//            run(mTaskCtrl);
//        }
//
//        protected void run( TaskControllerImpl taskCtrl ) {
//
//        }
//    }
//
//    private PhotoScanCallback photoScanCallback;
//    private PhotoCompressCallback photoCompressCallback;
//    private ServiceConnection conn;
//    private PhotoThread mScanThread = null;
//    private String tmpImagePath = null;
//    private String  OriginalImagePath = null;
//    private int compressPercentage = 0;
//    private int minPhotoSize = 200;
//    private int minScreenshotSize = 30;
//    private static final int KB = 1024;
//    private Context mContext;
//    private boolean hasScreenshotPath = true;
//    private PhotoCompressEngine photoCompressEngine;
//    private AtomicBoolean isCompressing = new AtomicBoolean(false);
//
//    public boolean isCompressing() {
//        return isCompressing.get();
//    }
//
//    public void setPhotoScanCallback(PhotoScanCallback photoScanCallback) {
//        this.photoScanCallback = photoScanCallback;
//    }
//
//    public void setPhotoCompressCallback(PhotoCompressCallback photoCompressCallback) {
//        this.photoCompressCallback = photoCompressCallback;
//    }
//
//    /**
//     * 通知暂停
//     */
//    public void notifyPause() {
//        if(mScanThread!=null) {
//            mScanThread.notifyPause();
//        }
//
//        if(photoCompressEngine != null){
//            try {
//                photoCompressEngine.onPause();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    /**
//     * 通知继续
//     */
//    public void notifyResume() {
//        if(mScanThread!=null) {
//            mScanThread.resumePause();
//        }
//
//        if(photoCompressEngine != null){
//            try {
//                photoCompressEngine.onResume();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    /**
//     * 通知停止
//     */
//    public void notifyStop() {
//        stopScan();
//        stopCompress();
//    }
//
//    public void stopScan() {
//        if ( !isScanning() ) {
//            return;
//        }
//        if ( mScanThread != null ) {
//            mScanThread.notifyStop();
//        }
//    }
//
//    public void stopCompress() {
//        if(photoCompressEngine != null){
//            try {
//                photoCompressEngine.onStop();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    /**
//     * 对比图片中的原始图片和压缩片的比率
//     * 原图 * percentage = 压缩图
//     * @return
//     */
//    public int getCompressPercentage() {
//        return compressPercentage;
//    }
//
//    /**
//     * 对比图片中的原始图片路径
//     * @return
//     */
//    public String getOriginalImagePath() {
//        return OriginalImagePath;
//    }
//
//    /**
//     * 对比图片中的压缩图片路径
//     * @return
//     */
//    public String getTmpImagePath() {
//        return tmpImagePath;
//    }
//
/////<DEAD CODE>///     public void setTmpImagePath(String tmpImagePath) {
////        this.tmpImagePath = tmpImagePath;
////    }
//
//    /**
//     * 扫描引擎是否正在扫描
//     * @return true为正在扫描， false为扫描还未开始或者扫描结束
//     */
//    public boolean isScanning() {
//        return mScanThread != null && mScanThread.isAlive();
//    }
//
//    public static int getMinScreenshotSize(){
//        int size = CloudCfgDataWrapper.getCloudCfgIntValue(
//                CloudCfgKey.JUNK_SCAN_FLAG_KEY,
//                CloudCfgKey.JUNK_SCAN_SCREENSHOT_COMPRESS_MIN_SIZE,
//                30) * KB;
//        return size;
//    }
//
//    /**
//     * 扫描可以压缩的照片，拍照目录和截屏目录至少传入一个
//     * 其中拍照目录最小size限制为200KB，截屏目录限制为30KB
//     * @param ctx
//     * @param scanPathList
//     * @param screenShotPathList
//     * @return
//     */
//    public boolean startScan(final Context ctx ,final List<String> scanPathList,final List<String> screenShotPathList) {
//        if (((scanPathList == null || scanPathList.isEmpty())
//                && (screenShotPathList == null || screenShotPathList.isEmpty()))
//                || isScanning() ) {
//            return false;
//        }
//
//        minPhotoSize = CloudCfgDataWrapper.getCloudCfgIntValue(
//                CloudCfgKey.JUNK_SCAN_FLAG_KEY,
//                CloudCfgKey.JUNK_SCAN_PHOTO_COMPRESS_MIN_SIZE,
//                200) * KB;
//
//        minScreenshotSize = getMinScreenshotSize();
//
//        mScanThread = new PhotoThread() {
//            private final Object object = new Object();
//            private void initPathList(List<String> initCameraPathList,final List<String> initScreenshotPathList) {
//                // 外部设备路径
//                ArrayList<String> mExternalStoragePaths = new ArrayList<String>();
//                if (Environment.getExternalStorageState().equals(
//                        Environment.MEDIA_MOUNTED)) {
//                    mExternalStoragePaths.add(Environment.getExternalStorageDirectory().getPath());
//                }
//
//                if (android.os.Build.VERSION.SDK_INT < 19) {
//                    ArrayList<String>  mountedVolumePaths= (new StorageList()).getMountedVolumePaths();
//                    if (mountedVolumePaths != null && !mountedVolumePaths.isEmpty()) {
//                        mExternalStoragePaths.addAll(mountedVolumePaths);
//                    }
//                }
//
//                for(String dir : mExternalStoragePaths){
//                    for(String name : scanPathList){
//                        if(new File(dir + name).exists()){
//                            initCameraPathList.add(dir + name);
//                        }
//                    }
//
//                    for(String name : screenShotPathList){
//                        if(new File(dir + name).exists()){
//                            initScreenshotPathList.add(dir + name);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            protected void run(final TaskControllerImpl taskCtrl ) {
//
//                final List<MediaFile> mediaFileList = new ArrayList<MediaFile>();
//
//                final List<MediaFile> jpgMediaFileList = new ArrayList<MediaFile>();
//                final List<MediaFile> pngMediaFileList = new ArrayList<MediaFile>();
//
//                final List<String> initCameraPathList = new ArrayList<String>();
//                final List<String> initScreenshotPathList = new ArrayList<String>();
//
//                initPathList(initCameraPathList,initScreenshotPathList);
//
//                if(initCameraPathList.isEmpty() && initScreenshotPathList.isEmpty()){
//                    if(photoScanCallback != null){
//                        photoScanCallback.onScanFinished(mediaFileList,compressPercentage);
//                    }
//                    return;
//                }
//
//                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//                    if(photoScanCallback != null){
//                        photoScanCallback.onScanFinished(mediaFileList,compressPercentage);
//                    }
//                    return;
//                }
//
//                try {
//
//                    photoScanCallback.onScanStart();
//
//                    if(initScreenshotPathList.isEmpty()){
//                        hasScreenshotPath = false;
//                    }
//
//                    doScan(taskCtrl, initCameraPathList,initScreenshotPathList, jpgMediaFileList,pngMediaFileList);
//
//                    mediaFileList.addAll(jpgMediaFileList);
//                    mediaFileList.addAll(pngMediaFileList);
//
//                    if(mediaFileList.size() > 0){
//
//                        MediaFile tmpMedia =  jpgMediaFileList.size() > 0 ?
//                                (jpgMediaFileList.get(jpgMediaFileList.size() - 1))
//                                :(pngMediaFileList.get(pngMediaFileList.size() - 1));
//                        final String lastPath = tmpMedia.getPath();
//                        final String mimeType = tmpMedia.getMimeType();
//
//                        final int rate = ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).getPhotoCompressAverageSavedRate(lastPath);
//
//                        if(rate > 0){
//                            compressPercentage = rate;
//                            //OriginalImagePath = lastPath;
//                        }else{
//
//                            createPhotoCompressConnector(ctx,new OnServiceConnectedCallback() {
//                                @Override
//                                public void onServiceConnected() {
//
//                                    new Thread("getPhotoCompressPercentage"){
//
//                                        @Override
//                                        public void run() {
//
//                                            try{
//                                                if (null != taskCtrl && taskCtrl.checkStop()) {
//                                                    return;
//                                                }
//                                                if(photoCompressEngine != null) {
//                                                    compressPercentage = photoCompressEngine.getPhotoCompressPercentage(lastPath, null,mimeType);
//                                                }
//                                                if(compressPercentage > 0){
//                                                    //OriginalImagePath = lastPath;
//                                                    ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).setPhotoCompressAverageRate(100 - compressPercentage);
//                                                    ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).setPhotoCompressAverageSavedRate(lastPath, compressPercentage);
//                                                }
//                                            }catch (Exception e){
//                                                e.printStackTrace();
//                                            }finally {
//                                                synchronized(object) {
//                                                    object.notifyAll();
//                                                }
//                                            }
//                                        }
//                                    }.start();
//
//                                }
//                            });
//
//                            try{
//                                synchronized(object) {
//                                    object.wait();
//                                }
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//
//                        }
//
//                    }
//
//
//                } finally {
//
//                    if(photoScanCallback != null){
//                        NLog.d("photo_compress", "scan photo num " + mediaFileList.size());
//                        photoScanCallback.onScanFinished(mediaFileList,compressPercentage);
//                    }
//                }
//            }
//
//            public void notifyStop() {
//                synchronized(object) {
//                    object.notifyAll();
//                }
//                super.notifyStop();
//            }
//
//
//            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//            private boolean doScan(ScanTaskController ctrl, List<String> cameraPathList, List<String> screenShotPathList, List<MediaFile> jpgMediaFileList, List<MediaFile> pngMediaFileList) {
//
//                final String[] projection = {
//                        MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE,
//                        MediaStore.Video.Media.DATE_MODIFIED, MediaStore.Images.Media.MIME_TYPE,
//                        MediaStore.Files.FileColumns.MEDIA_TYPE,MediaStore.Images.Media.DATE_TAKEN};
//                final StringBuilder selection = new StringBuilder(" media_type = 1 and (");
//
//                if(cameraPathList.size() > 0){
//                    for(int i = 0;i < cameraPathList.size();i++){
//                        String  str = cameraPathList.get(i);
//                        if(i == 0){
//                            selection.append("(mime_type = 'image/jpeg' and _size > ");
//                            selection.append(minPhotoSize);
//                            selection.append(" and ( _data like '");
//                        }else{
//                            selection.append(" or _data like '");
//                        }
//                        selection.append(str);
//                        selection.append("/%'");
//                    }
//                    selection.append(" ))");
//                }
//
//                if(screenShotPathList.size() > 0) {
//                    for (int i = 0; i < screenShotPathList.size(); i++) {
//                        String str = screenShotPathList.get(i);
//                        if (i == 0) {
//                            if(cameraPathList.size() > 0){
//                                selection.append(" or ( _size >");
//                            }else{
//                                selection.append(" ( _size >");
//                            }
//                            selection.append(minScreenshotSize);
//                            selection.append(" and ( _data like '");
//                        } else {
//                            selection.append(" or _data like '");
//                        }
//                        selection.append(str);
//                        selection.append("/%'");
//                    }
//                    selection.append(" ))");
//                }
//
//                selection.append(" )");
//
//                Cursor cursor = null;
//                try {
//                    cursor = SpaceApplication.getInstance().getContext().getContentResolver()
//                            .query(MediaStore.Files.getContentUri("external"),
//                                    projection, selection.toString(), null, MediaStore.Video.Media.DATE_MODIFIED + " DESC");
//
//                    if (null != cursor && cursor.moveToFirst()) {
//                        String jpgMime = "image/jpeg";
//                        do {
//                            if (null != ctrl && ctrl.checkStop()) {
//                                break;
//                            }
//
//                            long id = cursor.getLong(0);
//                            String path = cursor.getString(1);
//                            long size = cursor.getLong(2);
//                            long lastModified = cursor.getLong(3);
//                            String mimeType = cursor.getString(4);
//                            long dateTaken = cursor.getLong(6);
//
//                            if (TextUtils.isEmpty(path)) {
//                                continue;
//                            }
//
//                            int index = path.lastIndexOf("/");
//                            if(index > 0){
//                                String temp = path.substring(0,index);
//                                if(!cameraPathList.contains(temp) && !screenShotPathList.contains(temp)){
//                                    continue;
//                                }
//                            }
//
//                            boolean isJPG = jpgMime.equals(mimeType);
//                            String userCommentSize = ImageCompressUtil.getUserComment(path);
//                            if(userCommentSize != null){
//                                continue;
//                            }
//
//
//                            MediaFile mediaFile = new MediaFile(JunkRequest.EM_JUNK_DATA_TYPE.MYPHOTO);
//                            mediaFile.setPath(path);
//                            mediaFile.setId(id);
//                            mediaFile.setLastModified(lastModified);
//                            mediaFile.setSize(size);
//                            mediaFile.setMimeType(mimeType);
//                            mediaFile.setMediaType(MediaFile.MEDIA_TYPE_IMAGE);
//                            mediaFile.setSelect(true);// 默认全选
//                            //这个很重要，保存拍照时间，压缩后还原这个时间，否则图库中的顺序会被打乱
//                            mediaFile.setDateTaken(dateTaken);
//
//                            if(isJPG){
//                                jpgMediaFileList.add(mediaFile);
//                            }else{
//                                pngMediaFileList.add(mediaFile);
//                            }
//
//                        } while (cursor.moveToNext());
//                    }
//                    cursor.close();
//                    cursor = null;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (cursor != null && !cursor.isClosed()) {
//                        cursor.close();
//                        cursor = null;
//                    }
//                }
//
//                return true;
//            }
//        };
//        mScanThread.setName( "PhotoCompressScanThread" );
//        mScanThread.start();
//        return true;
//    }
//
//    //解绑service
//    public void destory(){
//        if(mContext != null){
//            mContext.unbindService(conn);
//            mContext = null;
//        }
//    }
//
//    public void createPhotoCompressConnector(Context ctx,final OnServiceConnectedCallback onServiceConnectedCallback){
//
//        mContext = ctx;
//        conn = new ServiceConnection() {
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                photoCompressEngine = null;
//            }
//
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                if(service != null){
//                    photoCompressEngine = PhotoCompressEngine.Stub.asInterface(service);
//                    try{
//                        photoCompressEngine.onStop();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if(onServiceConnectedCallback != null){
//                        onServiceConnectedCallback.onServiceConnected();
//                    }
//                }
//            }
//        };
//
//        Intent intent = new Intent();
//        intent.setClass(ctx, PhotoCompressService.class);
//        ctx.bindService(intent, conn, Context.BIND_AUTO_CREATE);
//    }
//
//    public void getCompressPercentage(final Context ctx,final String file,final String mimeType,final getPhotoCompressPercentageCallback getPhotoCompressPercentageCallback) {
//        if (file == null) {
//            return ;
//        }
//
//        if (compressPercentage > 0 && tmpImagePath != null) {
//            getPhotoCompressPercentageCallback.onGetCompressPercentage(tmpImagePath, compressPercentage);
//            return ;
//        }
//
//        final String path = StorageUtil.getCacheDirectory(ctx, true).getAbsolutePath() + "/compress_tmp";
//        OnServiceConnectedCallback onServiceConnectedCallback = new OnServiceConnectedCallback() {
//            @Override
//            public void onServiceConnected() {
//
//                new Thread("getCompressPercentage"){
//                    @Override
//                    public void run() {
//                        try {
//                            compressPercentage = photoCompressEngine.getPhotoCompressPercentage(file, path,mimeType);
//                            tmpImagePath = path;
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }finally {
//                            String path = tmpImagePath;
//                            if(path == null){
//                                path = file;
//                            }
//                            getPhotoCompressPercentageCallback.onGetCompressPercentage(path,compressPercentage);
//                        }
//                    }
//                }.start();
//            }
//        };
//
//
//        if (photoCompressEngine == null) {
//            createPhotoCompressConnector(ctx, onServiceConnectedCallback);
//        } else {
//            onServiceConnectedCallback.onServiceConnected();
//        }
//    }
//
//    public boolean startCompress(Context ctx,final List<MediaFile> mediaFileList) {
//        if (mediaFileList == null || mediaFileList.isEmpty() || isCompressing.get()) {
//            return false;
//        }
//
//        isCompressing.set(true);
//        if(photoCompressCallback != null){
//            photoCompressCallback.onCompressStart();
//        }
//
//        final String failDir = StorageUtil.getCacheDirectory(ctx, true).getAbsolutePath();
//        boolean  isCollectDamageImage = false;
//        if(!ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).getPhotoCompressCollectDamage()){
//            isCollectDamageImage = CloudCfgDataWrapper.getCloudCfgBooleanValue(
//                    CloudCfgKey.JUNK_SCAN_FLAG_KEY,
//                    CloudCfgKey.JUNK_SCAN_COMPRESS_DAMAGE_COLLECT_SWITCH,
//                    false) ;
//        }
//        final boolean finalCollectDamageImage = isCollectDamageImage;
//
//        OnServiceConnectedCallback onServiceConnectedCallback = new OnServiceConnectedCallback() {
//            @Override
//            public void onServiceConnected() {
//                try {
//                    final int total = mediaFileList.size();
//                    photoCompressEngine.compressPhoto(mediaFileList,new com.clean.spaceplus.cleansdk.PhotoCompressCallback.Stub() {
//                        private int curItem = 0;
//                        long srcFileSize = 0l;
//                        private List<MediaFile> mCompressedList = new ArrayList<MediaFile>();
//                        @Override
//                        public void onCompressItem(MediaFile mediaFile) throws RemoteException {
//                            curItem++;
//                            if(photoCompressCallback != null){
//                                photoCompressCallback.onCompressItem(mediaFile, curItem, total);
//                            }
//                        }
//
//                        @Override
//                        public void onCompressDamage(String srcPath, String tmpPath, int reason) throws RemoteException {
//                            if(photoCompressCallback != null){
//                                photoCompressCallback.onCompressDamage(srcPath, tmpPath, reason);
//                            }
//                        }
//
//                        @Override
//                        public void onCompressSuccess(MediaFile mediaFile,long srcSize) throws RemoteException {
//                            if(photoCompressCallback != null){
//                                photoCompressCallback.onCompressSuccess(mediaFile);
//                            }
//
//                            if(mediaFile.getSize() != srcSize){
//                                mCompressedList.add(mediaFile);
//                                srcFileSize += srcSize;
//                            }
//                        }
//
//                        @Override
//                        public void onCompressFail(MediaFile mediaFile) throws RemoteException {
//                            if(photoCompressCallback != null){
//                                photoCompressCallback.onCompressFail(mediaFile);
//                            }
//                        }
//
//                        @Override
//                        public void onCompressFinish(long compressSize) throws RemoteException {
//                            isCompressing.set(false);
//                            if(photoCompressCallback != null){
//                                photoCompressCallback.onCompressFinished(compressSize);
//                            }
//                            if(0 == ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).getPhotoCompressAverageRate()
//                                    && srcFileSize > 0){
//                                int rate = (int)(compressSize * 100/srcFileSize);
//                                if(rate > 0 && rate < 100){
//                                    ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).setPhotoCompressAverageRate(rate);
//                                }
//                            }
//
//                            if(!mCompressedList.isEmpty()){
//                                new Thread("compressUpdateMediaStore") {
//                                    @Override
//                                    public void run() {
//                                        MediaFileOperator.updateImagesFromMediaStoreByMediaFiles(mCompressedList);
//                                    }
//                                }.start();
//                            }
//                            if (compressSize > 0) {
//                                if(hasScreenshotPath){
////                                    JunkManagerActivity.setRecentCachedDataInvalid(JunkRequest.EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS);
//                                }
//                                ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).addPhotoCompressHistorySize(compressSize);
//                            }
//
//                            destory();
//
//                        }
//
//                    },finalCollectDamageImage?failDir:null);
//                }catch (Exception e){
//                    e.printStackTrace();
//                    if(photoCompressCallback != null){
//                        photoCompressCallback.onCompressFinished(0);
//                    }
//                    isCompressing.set(false);
//                }
//            }
//        };
//
//        if(photoCompressEngine == null){
//            createPhotoCompressConnector(ctx,onServiceConnectedCallback);
//        }else{
//            onServiceConnectedCallback.onServiceConnected();
//        }
//
//        return true;
//    }
//
//    /**
//     * 云端开关
//     * @return
//     */
//    public static boolean isFunctionEnable() {
//        if (Build.VERSION.SDK_INT <= 10) {
//            return false;
//        }
//        long imei = 0L;
//        try {
//            imei = Math.abs(Long.parseLong(PhoneUtil.getIMEI(SpaceApplication.getInstance().getContext())));
//        } catch (Exception e) {
//            imei = 0L;
//        }
//        // 云端获取打开比例。0表示全部关闭，100表示全部打开。默认全部打开
//        if (!BuildConfig.DEBUG && ((imei % 100) >= CloudCfgDataWrapper.getCloudCfgIntValue(CloudCfgKey.CLOUD_PHOTO_COMPRESS, CloudCfgKey.PHOTO_COMPRESS_SWITCH, Commons.isCNVersion() ? 100 : 0))) {
//            return false;
//        }
//        return true;
//    }
//
//    public static final int limitShowCompressPercent(int compressPercent) {
//        if (compressPercent < 0) {
//            compressPercent = 0;
//        } else if (compressPercent > 100) {
//            compressPercent = 100;
//        }
//        return compressPercent;
//    }
//}

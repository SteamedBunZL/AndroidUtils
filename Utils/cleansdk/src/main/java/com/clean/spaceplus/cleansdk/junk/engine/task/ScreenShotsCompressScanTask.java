//package com.clean.spaceplus.cleansdk.junk.engine.task;
//
//import android.content.Context;
//import android.os.Environment;
//import android.os.Looper;
//import android.text.TextUtils;
//
//import com.clean.spaceplus.cleansdk.BuildConfig;
//import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
//import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
//import com.clean.spaceplus.cleansdk.junk.cleancloud.CloudCfgDataWrapper;
//import com.clean.spaceplus.cleansdk.junk.cleancloud.config.CloudCfgKey;
//import com.clean.spaceplus.cleansdk.junk.cleancloud.config.ServiceConfigManager;
//import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFile;
//import com.clean.spaceplus.cleansdk.junk.engine.bean.PhotoCompressDataModel;
//import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
//import com.clean.spaceplus.cleansdk.junk.engine.util.NameFilter;
//import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
//import com.clean.spaceplus.cleansdk.util.EnableCacheListDir;
//import com.clean.spaceplus.cleansdk.util.FileUtils;
//import com.clean.spaceplus.cleansdk.util.ImageCompressUtil;
//import com.clean.spaceplus.cleansdk.util.StringUtils;
//import com.clean.spaceplus.cleansdk.app.SpaceApplication;
//
//import java.io.File;
//
///**
// * @author zengtao.kuang
// * @Description: 扫描压缩截图Task
// * @date 2016/5/9 11:03
// * @copyright TCL-MIG
// */
//public class ScreenShotsCompressScanTask extends ScanTask.BaseStub {
//
//    public static final int SCREEN_SHOTS_COMPRESS_SCAN_START = 1;       ///< 开始扫描
//    public static final int SCREEN_SHOTS_COMPRESS_SCAN_FINISH = 2;      ///< 扫描结束
//    public static final int SCREEN_SHOTS_COMPRESS_SCAN_FOUND_ITEM = 3;  ///< 扫描结束
//
//
//
//    @Override
//    public boolean scan(ScanTaskController ctrl) {
//        try {
//            if (this.mCB != null) {
//                mCB.callbackMessage(SCREEN_SHOTS_COMPRESS_SCAN_START, 0, 0, null);
//            }
//            scanScreenShotsCompress(ctrl, SpaceApplication.getInstance().getContext().getApplicationContext());
//        } finally {
//            if (this.mCB != null) {
//                mCB.callbackMessage(SCREEN_SHOTS_COMPRESS_SCAN_FINISH, 0, 0, null);
//            }
//        }
//        return true;
//    }
//
//    private void scanScreenShotsCompress(ScanTaskController ctrl, Context ctx) {
//
//        if (!BuildConfig.DEBUG && !CloudCfgDataWrapper.getCloudCfgBooleanValue(
//                CloudCfgKey.JUNK_SCAN_FLAG_KEY,
//                CloudCfgKey.JUNK_STD_SCAN_SCREEN_SHOTS_COMPRESS,
//                false)) {
//            Looper.getMainLooper();
//            return;
//        }
//
//        final String[] folderArray = new String[] {
//                PhotoCompressDataModel.PATH_PIC_SCREENSHOT,
//                PhotoCompressDataModel.PATH_DCIM_SCREENSHOT,
//                PhotoCompressDataModel.PATH_SCREENSHOT,
//                PhotoCompressDataModel.PATH_PHOTO_SCREENSHOT
//        };
//
//        final int minScreenshotSize = 30*1024/*PhotoCompressManager.getMinScreenshotSize()*/;//暂时用默认值 FIXME BY Davis
//        int rate = ServiceConfigManager.getInstanse(SpaceApplication.getInstance().getContext()).getPhotoCompressAverageRate();
//        if(rate <= 0){
//            rate = 50;
//        }
//
//        MediaFile targetItem = null;
//        PathOperFunc.StringList targets = null;
//        File screenShotsFolder = null;
//        String screenShotsFolderStr = null;
//        for (String folder : folderArray) {
//
//            if (null != ctrl && ctrl.checkStop()) {
//                break;
//            }
//
//            screenShotsFolder = new File(Environment.getExternalStorageDirectory(), folder);
//            if (!screenShotsFolder.exists()) {
//                continue;
//            }
//
//            targets = EnableCacheListDir.listDir(screenShotsFolder.getPath(), new NameFilter() {
//
//                @Override
//                public boolean accept(String parent, String sub, boolean bFolder) {
//
//                    if (bFolder) {
//                        return false;
//                    }
//
//                    if (TextUtils.isEmpty(sub)) {
//                        return false;
//                    }
//
//                    sub = StringUtils.toLowerCase(sub);
//                    if (sub.endsWith(".png") || sub.endsWith(".jpg")) {
//                        return true;
//                    }
//
//                    return false;
//                }
//            });
//
//            if (null != ctrl && ctrl.checkStop()) {
//                break;
//            }
//
//            try {
//                if (null == targets || 0 == targets.size()) {
//                    continue;
//                }
//
//                screenShotsFolderStr = FileUtils.addSlash(screenShotsFolder.getPath());
//                for (String sub : targets) {
//                    if (TextUtils.isEmpty(sub)) {
//                        continue;
//                    }
//
//                    if (null != ctrl && ctrl.checkStop()) {
//                        break;
//                    }
//
//                    sub = screenShotsFolderStr + sub;
//                    if (null != ImageCompressUtil.getUserComment(sub)) {
//                        // 已经压缩过
//                        continue;
//                    }
//
//                    long size = new File(sub).length();
//                    if(size < minScreenshotSize){
//                        // size 太小
//                        continue;
//                    }
//                    targetItem = new MediaFile(JunkRequest.EM_JUNK_DATA_TYPE.SCRSHOTSCOMPRESS);
//                    targetItem.setCheck(true);
//                    targetItem.setPath(sub);
//                    targetItem.setMediaType(MediaFile.MEDIA_TYPE_IMAGE);
//                    targetItem.setSize(size*rate/100);
//
//                    if (null != mCB) {
//                        mCB.callbackMessage(SCREEN_SHOTS_COMPRESS_SCAN_FOUND_ITEM, 0, 0, targetItem);
//                    }
//                }
//
//            } finally {
//                if (null != targets) {
//                    targets.release();
//                    targets = null;
//                }
//            }
//        }
//    }
//
//    @Override
//    public String getTaskDesc() {
//        return "ScreenShotsCompressScanTask";
//    }
//
//}

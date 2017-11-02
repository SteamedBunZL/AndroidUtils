//package com.clean.spaceplus.cleansdk.junk.engine.task;
//
//import SpaceApplication;
//import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
//import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
//import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFile;
//import com.clean.spaceplus.cleansdk.junk.engine.photo.PhotoCompressManager;
//
//import java.util.ArrayList;
//
///**
// * @author dongdong.huang
// * @Description:
// * @date 2016/5/10 17:38
// * @copyright TCL-MIG
// */
//public class ScreenShotsCompressTask extends ScanTask.BaseStub{
//    public static final int COMPRESS_FINISH = 1;
//
//
//    private IScrShotsCompressDataSrc mDataMgr = null;
//
//    public static interface IScrShotsCompressDataSrc {
//        public MediaFile getNextCompressFile();
//    }
//
//    public void bindCompressDataSrc(IScrShotsCompressDataSrc dataMgr) {
//        mDataMgr = dataMgr;
//    }
//
//    @Override
//    public boolean scan(ScanTaskController ctrl) {
//
//        ArrayList<MediaFile> compressList = null;
//        try {
//            if (null == mDataMgr) {
//                return true;
//            }
//
//            int nItemNum = 0;
//            long lAllSize = 0L;
//            compressList = new ArrayList<MediaFile>();
//            for (MediaFile path = mDataMgr.getNextCompressFile(); null != path; path = mDataMgr.getNextCompressFile()) {
//                compressList.add(path);
//                ++nItemNum;
//                lAllSize += path.getSize();
//            }
//
//            if (null != ctrl && ctrl.checkStop()) {
//                return true;
//            }
//
//            final int fnItemNum = nItemNum;
//            final long flAllSize = lAllSize;
//
//            PhotoCompressManager pcm = new PhotoCompressManager();
//            pcm.setPhotoCompressCallback(new PhotoCompressManager.PhotoCompressCallback() {
//
//                @Override
//                public void onCompressStart() {
//                }
//
//                @Override
//                public void onCompressItem(MediaFile mediaFile, int cur, int total) {
//                }
//
//                @Override
//                public void onCompressFinished(long compressSize) {
//                    //report compress finish
//                }
//
//                @Override
//                public void onCompressSuccess(MediaFile mediaFile) {
//                }
//
//                @Override
//                public void onCompressFail(MediaFile mediaFile) {
//                }
//
//                @Override
//                public void onCompressDamage(String srcPath, String tmpPath, int reason) {
//                }
//            });
//            pcm.startCompress(SpaceApplication.getInstance().getContext(), compressList);
//        } finally {
//            if (null != mCB) {
//                mCB.callbackMessage(COMPRESS_FINISH, 0, 0,  compressList);
//            }
//        }
//
//        return true;
//    }
//
//    @Override
//    public String getTaskDesc() {
//        return ScreenShotsCompressTask.class.getSimpleName();
//    }
//}

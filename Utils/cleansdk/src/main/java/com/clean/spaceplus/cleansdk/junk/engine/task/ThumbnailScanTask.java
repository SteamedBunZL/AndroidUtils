package com.clean.spaceplus.cleansdk.junk.engine.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.clean.spaceplus.cleansdk.R;
import com.clean.spaceplus.cleansdk.base.scan.IScanFilter;
import com.clean.spaceplus.cleansdk.base.scan.ScanTask;
import com.clean.spaceplus.cleansdk.base.scan.ScanTaskController;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CleanCloudManager;
import com.clean.spaceplus.cleansdk.junk.cleancloud.CloudCfgDataWrapper;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.CloudCfgKey;
import com.clean.spaceplus.cleansdk.junk.engine.WhiteListsWrapper;
import com.clean.spaceplus.cleansdk.junk.engine.bean.BaseJunkBean;
import com.clean.spaceplus.cleansdk.junk.engine.bean.SDcardRubbishResult;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.clean.spaceplus.cleansdk.util.ResUtil;
import com.clean.spaceplus.cleansdk.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author chaohao.zhou
 * @Description: 无用缩略图扫描
 * @date 2016/5/7 10:52
 * @copyright TCL-MIG
 */
public class ThumbnailScanTask extends ScanTask.BaseStub implements IScanFilter {

    private static final String TAG = ThumbnailScanTask.class.getSimpleName();

    // RES_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE
    // 默认配置是不在ADD_CHILDREN_DATA_ITEM_TO_ADAPTER的消息回调中返回ignore item
    // 如果将该配置取非，那么白名单中的项也会被作为结果返回，可以通过JunkInfoBase的isIgnore()查询是否是ignore item
    public static final int THUMB_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE = 0x00000020;
    //是否 检查扫描结果 用户设置的锁定状态    默认不检查  // 应该是默认检查才对。。
    public static final int THUMB_FILE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS = 0x0000040;


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int SCAN_SDCARD_INFO = 0x0000001; // 貌似这个也没用
    public static final int SCAN_FINISH = 0x0000002; // 扫描结束，若因超时结束，则arg1值为1，否则为0。
    public static final int THUMB_FILE_SCAN_PROGRESS_START = 0x0000003; // 开始进度计算，arg1值为类别
    public static final int THUMB_FILE_SCAN_PROGRESS_STEP_NUM = 0x0000004; // 进度计算，arg1值为类别，arg2为此类别的总步数
    public static final int THUMB_FILE_SCAN_PROGRESS_ADD_STEP = 0x0000005; // 进度计算，arg1类别加一步
    public static final int ADD_CHILDREN_DATA_ITEM_TO_ADAPTER = 0x0000006; // 添加 // 貌似这个也没用
    public static final int UPDATE_CHILDREN_DATA_ITEM_TO_ADAPTER = 0x0000007; // 添加或更新
    public static final int THUMB_FILE_SCAN_IGNORE_ITEM = 0x0000008;
    public static final int THUMB_FILE_SCAN_TEMP_FILE_FINISHED = 0x0000009;    //掃描结束

    final static int MAX_BATCH_QUERY_COUNT = 50;  // 一次最多查询50条
    final static String[] projectionMedia = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID};
    final static String[] projectionThumbnails = new String[]{MediaStore.Images.Thumbnails.DATA, MediaStore.Images
            .Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.KIND};

    private int mRFWhiteListMapSize = 0;
    private ArrayMap<String, ProcessModel> mRFWhiteListMap = new ArrayMap<String, ProcessModel>(); // 云端白名单

    private int mScanCfgMask = -1;

    HashSet<String> mThumbActualFiles = new HashSet<String>();

    class ThumbInfo {
        public String strPath;
        public int nThumbId;
        public int nPhotoId;
        public boolean bCanDelete;
    }

    public ThumbnailScanTask() {

    }

    public void setScanConfigMask(int mask) {
        mScanCfgMask = mask;
    }

//    public void setCaller(byte caller) {
//        mTimeRpt.user(caller);
//    }
//
//    public void setFirstScanFlag() {
//        mTimeRpt.first(true);
//    }

//    private void reportEndScan() {
//        mTimeRpt.end();
//        mTimeRpt.report();
//    }

    @Override
    public boolean scan(ScanTaskController ctrl) {
        try {
            do {
                if (null != ctrl && ctrl.checkStop()) {
                    break;
                }
//                try {
//                    mTimeRpt.start(cm_task_time.CM_TASK_TIME_STYPE_OBSOLETE_THUMB, ctrl);
                scanThumbnailFiles(ctrl);
//                } finally {
//                    reportEndScan();
//                }
            } while (false);
        } finally {
            if (null != mCB) {
                mCB.callbackMessage(SCAN_FINISH, (null != ctrl && ScanTaskController.TASK_CTRL_TIME_OUT == ctrl
                        .getStatus()) ? SDcardRubbishResult.RF_TEMPFILES : 0, 0, null);
            }
            //long timeDistance = SystemClock.uptimeMillis() - startTime;
            //Log.d(TAG, "Total time: " + Long.toString(timeDistance));
        }
        return true;
    }

    @Override
    public boolean isFilter(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        //由于云端残留等查询获取的路径大小写可能和原始的路径大写不一致,所以对于路径，一律转成小写
        String strLowerName = name;
        if (name.contains("/")) {
            strLowerName = StringUtils.toLowerCase(name);
        }
        if (mRFWhiteListMapSize > 0 && null != mRFWhiteListMap.get(strLowerName)) {
            return true;
        }
        return false;
    }

    /**
     * 获取白名单列表
     */
    private void loadAllRFWhiteList() {
        if (!mRFWhiteListMap.isEmpty()) {
            return;
        }
        mRFWhiteListMapSize = 0;
        mRFWhiteListMap.clear();
        List<ProcessModel> tmpWhiteList = WhiteListsWrapper.getRFWhiteList();
        if (null != tmpWhiteList) {
            for (ProcessModel tmpModel : tmpWhiteList) {
                if (!TextUtils.isEmpty(tmpModel.getPkgName())) {
                    mRFWhiteListMap.put(tmpModel.getPkgName(), tmpModel);
                }
            }
            mRFWhiteListMapSize = mRFWhiteListMap.size();
        }
    }

    private void checkLocked(SDcardRubbishResult info, String filePath) {
//        JunkLockedDaoImp junkLockedDao = null ;
        // 默认会触发这方法 因为mScanCfgMask = ~THUMB_FILE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS
//        if(0 == (THUMB_FILE_SCAN_CFG_MASK_NOT_CHECK_LOCKED_STATUS & mScanCfgMask)) {
//            junkLockedDao  = DaoFactory.getJunkLockedDao(MoSecurityApplication.getAppContext());
//            if(junkLockedDao!=null) {
//                info.setCheck(!junkLockedDao.checkLocked(filePath,info.isCheck()));
//            }
//        }
    }

    /**
     * 重复的缩略图直接添加到结果中
     */
    private SDcardRubbishResult scanAllDuplicatedThumb(final ScanTaskController ctrl, final ContentResolver cr,
                                                       SDcardRubbishResult info) {
        ArrayList<ThumbInfo> multiExistFile = new ArrayList<ThumbInfo>();
        Cursor cursor = null;
        try {
            // 获取重复ImageId 的缩略图
            cursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images
                    .Thumbnails.IMAGE_ID}, "0 == 0) GROUP BY (" + MediaStore.Images.Thumbnails.IMAGE_ID + ") HAVING " +
                    "(COUNT(" + MediaStore.Images.Thumbnails.IMAGE_ID + ") > 1", null, null);
            while (null != cursor && cursor.moveToNext()) { // 如果有重复ImageId 的缩略图
                if (null != ctrl && ctrl.checkStop()) {
                    break;
                }
                int origId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                Cursor cursor2 = null;
                try {
                    // 根据上面获取到的重复ImageId获取对应图片的Uri，降序排序
                    cursor2 = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, new String[]{MediaStore
                            .Images.Thumbnails._ID, MediaStore.Images.Thumbnails.DATA}, MediaStore.Images.Thumbnails
                            .IMAGE_ID + "=?", new String[]{Integer.valueOf(origId).toString()}, MediaStore.Images
                            .Thumbnails._ID + " desc");
                    while (null != cursor2 && cursor2.moveToNext()) {
                        int thumbId = cursor.getInt(cursor2.getColumnIndex(MediaStore.Images.Thumbnails._ID));
                        String strPath = cursor2.getString(cursor2.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                        File f1 = new File(strPath);
                        if (f1.exists() && f1.isFile()) {
                            ThumbInfo tmpInfo = new ThumbInfo();
                            tmpInfo.strPath = strPath;
                            tmpInfo.nThumbId = thumbId;
                            multiExistFile.add(tmpInfo);
                        }
                    }
                    if (multiExistFile.size() > 1) {
                        Iterator<ThumbInfo> iter = multiExistFile.iterator();
                        if (null != iter && iter.hasNext()) {
                            iter.next();
                            iter.remove();
                        }
                        SDcardRubbishResult newInfo = new SDcardRubbishResult(info);
                        for (ThumbInfo tmpInfo : multiExistFile) {
                            long fileSize = new File(tmpInfo.strPath).length();
                            newInfo.addPathList(tmpInfo.strPath);
                            newInfo.setSize(newInfo.getSize() + fileSize);
                            newInfo.setFilesCount(newInfo.getFilesCount() + 1);
                            newInfo.addMSImageThumbIdList(tmpInfo.nThumbId);
//                            mTimeRpt.foundFirst();
//                            mTimeRpt.addSize(fileSize);
//                            mTimeRpt.addFinum(1);
                        }
                        mCB.callbackMessage(UPDATE_CHILDREN_DATA_ITEM_TO_ADAPTER, SDcardRubbishResult.RF_TEMPFILES,
                                0, new RubbishFileScanTask.UpdateChildrenData(info, newInfo));
                        info = newInfo;
                    }
                    multiExistFile.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != cursor2) {
                        cursor2.close();
                        cursor2 = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        return info;
    }

    /**
     * 获取/DCIM/.thumbnails/中的缩略图
     *
     * @return 没数据，返回false，文件夹不存在会返回true
     */
    private boolean getAllThumbnails() {
        mThumbActualFiles.clear();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/.thumbnails/";
        File thumbnailDir = new File(path);
        if (!thumbnailDir.exists() || !thumbnailDir.isDirectory()) {
            return true;
        }
        PathOperFunc.FilesAndFoldersStringList fileAndFoldersList = PathOperFunc.listDir(path);
        if (null == fileAndFoldersList) {
            return false;
        }
        PathOperFunc.StringList pathList = null;
        pathList = fileAndFoldersList.getFileNameList();
        if (pathList != null) {
            for (String strThumbPath : pathList) {
                if (StringUtils.toLowerCase(strThumbPath).startsWith(".thumbdata")) {
                    continue;
                }
//                if (StringUtils.toLowerCase(strThumbPath).startsWith(".thumbindex")) {
//                    continue;
//                }
                mThumbActualFiles.add(path + strThumbPath);
            }
            pathList.release();
        }
        fileAndFoldersList.release();
        return true;
    }

    private boolean checkIfOrigPhotoExist(ContentResolver cr, ArrayMap<Integer, ThumbInfo> mThumbInfoMap, String
            strSelection, SDcardRubbishResult info) {
        if (null == cr || null == mThumbInfoMap ||
                TextUtils.isEmpty(strSelection) ||
                null == info) {
            return false;
        }
        boolean newItem = false;
        if (!mThumbInfoMap.isEmpty()) {
            Cursor cursorOrig = null;
            try {
                // 获取原图片cursor
                cursorOrig = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projectionMedia, MediaStore
                        .Images.Media._ID + " IN " + strSelection, null, null);
                while (null != cursorOrig && cursorOrig.moveToNext()) {
                    int imageId = cursorOrig.getInt(cursorOrig.getColumnIndex(MediaStore.Images.Media._ID));
                    String strOrigData = cursorOrig.getString(cursorOrig.getColumnIndex(MediaStore.Images.Media.DATA));
                    File origFile = new File(strOrigData);
                    if (origFile.exists()) {
                        ThumbInfo tmpThumbInfo = mThumbInfoMap.get(imageId);
                        if (null != tmpThumbInfo) {
                            tmpThumbInfo.bCanDelete = false; // 如果原文件存在，则不删除
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursorOrig != null && !cursorOrig.isClosed()) {
                    cursorOrig.close();
                    cursorOrig = null;
                }
            }
            long fileSize = 0L;
            Collection<ThumbInfo> mThumbInfoList = mThumbInfoMap.values();
            for (ThumbInfo tmpinfo : mThumbInfoList) {
                if (tmpinfo.bCanDelete) {
//					Log.e(TAG, "found path:" + tmpinfo.strPath);
                    fileSize = new File(tmpinfo.strPath).length();
                    info.addPathList(tmpinfo.strPath);
                    info.setSize(info.getSize() + fileSize);
                    info.setFilesCount(info.getFilesCount() + 1);
                    info.addMSImageThumbIdList(tmpinfo.nThumbId);
                    info.addMSImageMediaIdList(tmpinfo.nPhotoId);
//                    mTimeRpt.foundFirst();
//                    mTimeRpt.addSize(fileSize);
//                    mTimeRpt.addFinum(1);
                    newItem = true;
                }
            }
        }

        return newItem;
    }

    /**
     * 这个扫描只会删除重复的缩略图和 /DCIM/.thumbnails/中无用（原图片已经不在的）缩略图
     * @param ctrl
     */
    private void scanThumbnailFiles(final ScanTaskController ctrl) {
        if (null != ctrl && ctrl.checkStop()) {
            return;
        }
        if (Build.VERSION.SDK_INT < 11) {
            return;
        }
//        if (Commons.isMeizu()) {
//            OpLog.x("cld", "[Meizu] thumbnail disabled.");
//            return;
//        }
        // 检查是否关闭缩略图扫描
        if (!CloudCfgDataWrapper.getCloudCfgBooleanValue(CloudCfgKey.JUNK_SCAN_FLAG_KEY, CloudCfgKey
                .JUNK_SCAN_OBSOLETE_THUMBNAIL_FLAG, true)) {
            return;
        }
        // 白名单
        loadAllRFWhiteList();
        boolean isIgnoreItem = false;
        if (isFilter(WhiteListsWrapper.FUNCTION_FILTER_NAME_OBSOLETE_THUMBNAIL_SCAN)) {
            isIgnoreItem = true;
            if (null != mCB) {
                mCB.callbackMessage(THUMB_FILE_SCAN_IGNORE_ITEM, 0, 0, WhiteListsWrapper
                        .FUNCTION_FILTER_NAME_OBSOLETE_THUMBNAIL_SCAN); // 这边也是做了空处理
            }
            if ((mScanCfgMask & THUMB_FILE_SCAN_CFG_MASK_NOT_RETURN_IGNORE) != 0) {
                return;
            }
        }
        Context ctx = CleanCloudManager.getApplicationContext();
        ContentResolver cr = ctx.getApplicationContext().getContentResolver();

        String strUIText = ResUtil.getString(R.string.junk_tag_RF_ObsoleteImageThumbnails);
        SDcardRubbishResult info = new SDcardRubbishResult(JunkRequest.EM_JUNK_DATA_TYPE.USELESSTHUMBNAIL);
        info.setApkName(strUIText);
        info.setChineseName(strUIText);
        info.setCheck(true);
        info.setScanType(BaseJunkBean.SCAN_TYPE_STANDARD);
        info.setType(SDcardRubbishResult.RF_TEMPFILES);
        info.setStrDirPath(WhiteListsWrapper.FUNCTION_FILTER_NAME_OBSOLETE_THUMBNAIL_SCAN);
        checkLocked(info, WhiteListsWrapper.FUNCTION_FILTER_NAME_OBSOLETE_THUMBNAIL_SCAN);
        if (isIgnoreItem) {
            info.setIgnore(true);  // 这个值貌似没用啊。。
        }
        //init scan progress total steps
        int scanStepNum = 1000 * 4; //default set to a constant instead of calculating directories on SD card
        if (null != mCB) {
            mCB.callbackMessage(THUMB_FILE_SCAN_PROGRESS_START, 0, 0, null); // callback对这两个message没用进行处理
            mCB.callbackMessage(THUMB_FILE_SCAN_PROGRESS_STEP_NUM, 0, scanStepNum, null);
        }
        // 获取重复缩略图
        info = scanAllDuplicatedThumb(ctrl, cr, info);
        // 从/DCIM/.thumbnails/中获取所有缩略图 主要赋值mThumbActualFiles变量
        boolean bRetGetAllThumb = getAllThumbnails();
        Cursor cursor = null;
        try {
            // 获取所有的缩略图
            cursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projectionThumbnails, null, null,
                    null);
            if (null == cursor || 0 == cursor.getCount()) {
                if (null != cursor) {
                    cursor.close();
                }
                return;
            }
            ArrayMap<Integer, ThumbInfo> thumbInfoMap = new ArrayMap<Integer, ThumbInfo>();
            String strSelection = "(";
            while (null != cursor && cursor.moveToNext()) {
                String strPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                int imageId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Thumbnails._ID));
                int origId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                if (bRetGetAllThumb) {
                    if (!mThumbActualFiles.contains(strPath)) {
                        //not an actual file, skip for now
                        continue;
                    }
                    mThumbActualFiles.remove(strPath);
                } else {
                    File thumbFile = new File(strPath);
                    if (!thumbFile.exists()) {
                        //not an actual file, skip for now
                        continue;
                    }
                }
                ThumbInfo tmpInfo = new ThumbInfo();
                tmpInfo.strPath = strPath;
                tmpInfo.nThumbId = imageId;
                tmpInfo.nPhotoId = origId;
                tmpInfo.bCanDelete = true;
//				Log.e(TAG, "[Thumbnail info] path:"+strPath+ ", nThumbId:" + imageId + ", nPhotoId:"+Integer.valueOf(origId).toString());
                if (strSelection.length() != 1) {
                    strSelection += ",";
                }
                strSelection += Long.toString(origId);
                thumbInfoMap.put(Integer.valueOf(origId), tmpInfo);

                if (null != ctrl && ctrl.checkStop()) {
                    break;
                }

                if (thumbInfoMap.size() >= MAX_BATCH_QUERY_COUNT) { // 一次最多查询50条
                    strSelection += ")";
                    SDcardRubbishResult newInfo = new SDcardRubbishResult(info);
                    boolean needUpdate = checkIfOrigPhotoExist(cr, thumbInfoMap, strSelection, newInfo);
                    thumbInfoMap.clear();
                    strSelection = "(";

                    if (needUpdate) {
                        if (null != mCB && 0 != newInfo.getFilesCount()) {
                            mCB.callbackMessage(UPDATE_CHILDREN_DATA_ITEM_TO_ADAPTER, SDcardRubbishResult
                                    .RF_TEMPFILES, 0, new RubbishFileScanTask.UpdateChildrenData(info, newInfo));
                        }
                        info = newInfo; // 如果需要更新，将新的Info替换原来的Info
                    }
                }
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
            if (null != ctrl && ctrl.checkStop()) {
                return;
            }
            if (!thumbInfoMap.isEmpty()) {
                strSelection += ")";
                SDcardRubbishResult newInfo = new SDcardRubbishResult(info);
                boolean needUpdate = checkIfOrigPhotoExist(cr, thumbInfoMap, strSelection, newInfo);
                if (needUpdate) {
                    if (null != mCB && 0 != newInfo.getFilesCount()) {
                        mCB.callbackMessage(UPDATE_CHILDREN_DATA_ITEM_TO_ADAPTER, SDcardRubbishResult.RF_TEMPFILES,
                                0, new RubbishFileScanTask.UpdateChildrenData(info, newInfo));
                    }
                    info = newInfo; // 如果需要更新，将新的Info替换原来的Info
                }
            }
            if (!mThumbActualFiles.isEmpty()) {
                SDcardRubbishResult newInfo = new SDcardRubbishResult(info);
                long fileSize = 0L;
                for (String strThumbPath : mThumbActualFiles) {
                    fileSize = new File(strThumbPath).length();
                    newInfo.addPathList(strThumbPath);
                    newInfo.setSize(newInfo.getSize() + fileSize);
                    newInfo.setFilesCount(newInfo.getFilesCount() + 1);
//                    mTimeRpt.foundFirst();
//                    mTimeRpt.addSize(fileSize);
//                    mTimeRpt.addFinum(1);
//					Log.e(TAG, "[Lost Thumbnail] path:"+strThumbPath+", size:"+fileSize);
                }
                if (null != mCB && 0 != newInfo.getFilesCount()) {
                    mCB.callbackMessage(UPDATE_CHILDREN_DATA_ITEM_TO_ADAPTER, SDcardRubbishResult.RF_TEMPFILES, 0,
                            new RubbishFileScanTask.UpdateChildrenData(info, newInfo));
                }
                info = newInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
    }

    @Override
    public String getTaskDesc() {
        return TAG;
    }
}

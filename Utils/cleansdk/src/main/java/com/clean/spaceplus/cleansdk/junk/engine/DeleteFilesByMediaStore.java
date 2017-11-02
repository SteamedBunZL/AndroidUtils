package com.clean.spaceplus.cleansdk.junk.engine;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;
import com.clean.spaceplus.cleansdk.util.FileUtils;

import java.util.ArrayList;

import space.network.util.CleanTypeUtil;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/7 16:12
 * @copyright TCL-MIG
 */
public class DeleteFilesByMediaStore {
    private static final int BATCH_DIR_FILES_COUNT = 200;
    private String mRootDirString;
    private boolean mbDelFile = false;
    private boolean mbDelFolder = false;
    private long mDeleteTime_s = 0;

    private ContentResolver contentResolver;
    private Uri filesUri;

    private int mDelMediaType;
    ArrayList<ContentValues> mFileContentValueList;
    int mBatchFilesCount = 0;
    DelCallback mDelCallback = null;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DeleteFilesByMediaStore( String rootDirString, DelCallback IDelFileCB ) {
        this( rootDirString, IDelFileCB, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE );
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private DeleteFilesByMediaStore( String rootDirString, DelCallback IDelFileCB, int nDelMediaType ) {
        mRootDirString = rootDirString;
        mDelCallback = IDelFileCB;
        int delFlags = IDelFileCB.getDelFlags();
        int delFileTimeLimit = IDelFileCB.getDelFileTimeLimit();
        if ( delFileTimeLimit == CleanTypeUtil.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE ) {
            delFileTimeLimit = 0;
        }
        mbDelFile = delFlags == DelCallback.DELETE_ONLY_FILE || delFlags == 0 ;
        mbDelFolder = delFlags == DelCallback.DELETE_ONLY_EMPTY_FOLDER || delFlags == 0 ;

        if ( delFileTimeLimit != 0 ) {
            long currentTime = System.currentTimeMillis();
            mDeleteTime_s = currentTime/1000 - delFileTimeLimit * (24*60*60);
        }
        filesUri = MediaStore.Files.getContentUri("external");
        Context context = SpaceApplication.getInstance().getContext();
        contentResolver = context.getContentResolver();
        mBatchFilesCount = BATCH_DIR_FILES_COUNT;
        mFileContentValueList = new ArrayList<ContentValues>(mBatchFilesCount);
        mDelMediaType = nDelMediaType;
    }

    public void delFile( String pathString ){
        if ( mbDelFile ) {
            add(pathString);
        }
    }

    public void delFolder( String pathString ){
        if (mbDelFolder) {
            add( FileUtils.removeSlash(pathString) );
        }
    }

    public boolean finish(boolean needRecycle) {
        if ( mFileContentValueList.isEmpty() ) {
            return true;
        }
        //使用 filesUri插入数据比imagesUri快好多倍！
        try {
            contentResolver.bulkInsert(filesUri, mFileContentValueList.toArray(new ContentValues[mFileContentValueList.size()] ) );
            toDeleteByDir(needRecycle);
        } catch ( Exception e ) {

        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void toDeleteByDir(final boolean needRecycle){
        StringBuffer whereBuffer = new StringBuffer();
        String where = " _data > ? AND _data < ? ";
        String[] selectionArgs = new String[] { FileUtils.addSlash(mRootDirString), FileUtils.replaceEndSlashBy0(mRootDirString) };
        String delRootDirWhere = "_data = '"+FileUtils.removeSlash(mRootDirString)+"'";
        String strTimeLimit = "";
        if ( mDeleteTime_s != 0 ) {
            strTimeLimit = " AND "+MediaStore.Files.FileColumns.DATE_MODIFIED +" < " + mDeleteTime_s;
        }

        if ( mbDelFile && mbDelFolder ) {
            whereBuffer.append( where ).append( strTimeLimit );
        }else if( mbDelFile ) {
            whereBuffer.append( where ).append( " AND format != 12289 " ).append( strTimeLimit );
        }else{
            whereBuffer.append( where ).append( " AND format = 12289 " ).append( strTimeLimit );
        }

        //更新media_type为 image_type，MEDIA_TYPE_FILE 、MEDIA_TYPE_AUDIO 类型无法删除文件，需要先修改类型。
	        /*
	         * 测试发现三星note4和htc 4.4的系统第二张SD无法使用MEDIA_TYPE_IMAGE删除文件和目录。需要使用MEDIA_TYPE_PLAYLIST删除。
	         * 但是MEDIA_TYPE_PLAYLIST只能删除文件，不能删除目录！
	         */

        ContentValues mediaTypeValues = new ContentValues();
        mediaTypeValues.put(MediaStore.Files.FileColumns.MEDIA_TYPE, Integer.valueOf(mDelMediaType) );
        contentResolver.update(filesUri, mediaTypeValues, whereBuffer.toString(), selectionArgs );
        if ( mbDelFolder ) {
            contentResolver.update(filesUri, mediaTypeValues, delRootDirWhere, null );
        }
        //删除数据库中已有的文件
        if (mbDelFolder) {
            try {
                //删除目录。删除目录时需要先排序
                //加入Order by后缀一定会抛异常。但是并不影响文件和文件夹从磁盘上移除。
                //抛出的异常为: android.database.sqlite.SQLiteException: near "ORDER": syntax error (code 1):。。。
                contentResolver.delete(filesUri, whereBuffer.toString()+ " ORDER BY _data DESC", selectionArgs );
            } catch (Exception e) {

            }
        }
        // 删除文件
        contentResolver.delete(filesUri, whereBuffer.toString(), selectionArgs );
        if ( mbDelFolder ) {
            contentResolver.delete(filesUri, delRootDirWhere, null );
        }

        //再次遍历此目录并把未删除的文件使用MEDIA_TYPE_PLAYLIST的方式再删除一遍
        if ( mDelMediaType != MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST ) {

            int result[] = new int[6];
            int delFileTimeLimit = mDelCallback.getDelFileTimeLimit();
            if ( delFileTimeLimit == CleanTypeUtil.CAREFUL_SCAN_CLEANTIME_DEFAULT_VALUE ) {
                delFileTimeLimit = 0;
            }
            ArrayList<String> fileList = new ArrayList<String>(1);
            fileList.add( mRootDirString );
            PathOperFunc.deleteFileOrFolderWithConfig(result,
                    fileList,
                    mDelCallback.getDelFlags(),
                    delFileTimeLimit,
                    mDelCallback.getFileWhiteList(),
                    mDelCallback.getFolderWhiteList(),
                    mDelCallback.getFeedbackFileList(),
                    mDelCallback.getFeedbackFolderList(),
                    new PathCallback() {
                        @Override
                        public void onFile( String filePath, long size, int atime, int mtime, int ctime) {

                        }

                        @Override
                        public void onFeedback(String filePath, String fileName, long size) {

                        }

                        DeleteFilesByMediaStore mDeleteFilesByMediaStore = null;

                        @Override
                        public void onStart(String strRootDir) {
                            if ( strRootDir != null ) {
                                mDeleteFilesByMediaStore = new DeleteFilesByMediaStore(strRootDir, mDelCallback, MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST );
                            }
                        }

                        @Override
                        public void onFile(String strRootDir, String strSubFile) {
                            if ( strRootDir != null && mDeleteFilesByMediaStore != null ) {
                                mDeleteFilesByMediaStore.delFile(strSubFile);
                            }
                        }

                        @Override
                        public void onFolder(String strRootDir, String strSubFolder) {
                            if ( strRootDir != null && mDeleteFilesByMediaStore != null ) {
                                mDeleteFilesByMediaStore.delFolder(strSubFolder);
                            }
                        }

                        @Override
                        public void onDone(String strRootDir) {
                            if ( strRootDir != null && mDeleteFilesByMediaStore != null ) {
                                mDeleteFilesByMediaStore.finish(needRecycle);
                                mDeleteFilesByMediaStore = null;
                            }
                        }

                        @Override
                        public void onError(String strPath, boolean bRmDir,
                                            boolean bRoot, int nErrorCode) {

                        }

                        @Override
                        public boolean OnFilter(String filePath, long fileModifyTime) {
                            if (mDelCallback != null) {
                                return mDelCallback.onFilter(filePath, fileModifyTime);
                            }

                            return true;
                        }
                    } , FileUtils.checkSecondSdCardCanWriteable(), Environment.getExternalStorageDirectory().getAbsolutePath(), null, null, needRecycle);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void add( String pathString ) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Files.FileColumns.DATA, pathString );
        mFileContentValueList.add(values);
        if ( mFileContentValueList.size() >= mBatchFilesCount ) {
            //使用 filesUri插入数据比imagesUri快好多倍！
            try {
                contentResolver.bulkInsert(filesUri, mFileContentValueList.toArray(new ContentValues[mFileContentValueList.size()] ) );
            } catch (Exception e) {
            }
            mFileContentValueList.clear();
        }
    }
}

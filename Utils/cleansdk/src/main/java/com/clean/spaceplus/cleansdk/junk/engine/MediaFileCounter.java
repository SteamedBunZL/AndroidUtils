package com.clean.spaceplus.cleansdk.junk.engine;

import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFile;
import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/28 19:49
 * @copyright TCL-MIG
 */
public class MediaFileCounter implements PathCallback{
    /**
     *
     * @param maxSize 最多保存的MediaFile数量，maxSize=-1时不限制数量
     */
    public void setMediaFileListMaxSize(int maxSize){
        mMediaListMaxSize = maxSize;
    }

    public int getVideoNum() {
        return mVideoNum;
    }

    public int getAudioNum() {
        return mAudioNum;
    }

    public int getImageNum() {
        return mImageNum;
    }

    public List<MediaFile> getMediaList(){
        return mMediaList;
    }
    @Override
    public void onFile(String filePath, long size, int atime, int mtime, int ctime) {

    }

    private void addMediaFile(String path, int type){
        if(mMediaList == null){
            mMediaList = new ArrayList<MediaFile>();
        }
        if(mMediaListMaxSize != -1 && mMediaList.size() >= mMediaListMaxSize){
            return;
        }
        MediaFile mediaFile = new MediaFile(JunkRequest.EM_JUNK_DATA_TYPE.MYPHOTO);
        mediaFile.setPath(path);
        mediaFile.setMediaType(type);
        mMediaList.add(mediaFile);
    }

    private int mMediaListMaxSize = 4;
    private List<MediaFile> mMediaList = null;
    private int mVideoNum = 0;
    private int mAudioNum = 0;
    private int mImageNum = 0;
    private long mVideoSize = 0l;
    private long mAudioSize = 0l;
    private long mImageSize = 0l;

    private RubbishFileFilter mRubbishFilter = null;
    public void setRubbishFilterInterface(RubbishFileFilter filter) {
        mRubbishFilter = filter;
    }

    @Override
    public void onFeedback(String filePath, String fileName, long size) {

    }

    @Override
    public void onStart(String strRootDir) {

    }

    @Override
    public void onFile(String strRootDir, String strSubFile) {

    }

    @Override
    public void onFolder(String strRootDir, String strSubFolder) {

    }

    @Override
    public void onDone(String strRootDir) {

    }

    @Override
    public void onError(String strPath, boolean bRmDir, boolean bRoot,
                        int nErrorCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean OnFilter(String filePath, long fileModifyTime) {
        return true;
    }
}

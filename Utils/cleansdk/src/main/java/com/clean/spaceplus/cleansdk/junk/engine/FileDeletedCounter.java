package com.clean.spaceplus.cleansdk.junk.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chaohao.zhou
 * @Description: 删除文件记录
 * @date 2016/7/15 16:37
 * @copyright TCL-MIG
 */
public class FileDeletedCounter {

    private final String[] mImageSufArgs = new String[]{"BMP", "JPG", "JPEG", "PNG", "GIF", "GFIF", "DIB", "GPE",
            "PIC", "TIF"};
    private final String[] mVideoSufArgs = new String[]{"MPEG", "AVI", "MOV", "ASF", "WMV", "NAVI", "3GP", "RMVB",
            "MP4", "RM", "MPG", "AIFF", "MPEG", "QT", "DAT", "SWF"};
    private final String[] mAudioSufArgs = new String[]{"MP3", "AAC", "WMA", "WAV", "FLAC", "APE", "AU", "RAM",
            "MMF", "AMR", "AAC"};

    public int mImageCount;
    public int mVideoCount;
    public int mAudioCount;
    public long mFileDeletedSize; // 删除的文件总大小

    public List<String> mFileDeletedList;

    public FileDeletedCounter() {
        mImageCount = 0;
        mVideoCount = 0;
        mAudioCount = 0;
        mFileDeletedSize = 0;
        mFileDeletedList = new ArrayList<>();
    }

    public void addFileDeleted(File fileDeleted, long fileSize) {
        String fileAbsolutePath = fileDeleted.getAbsolutePath();
        mFileDeletedList.add(fileAbsolutePath + "：size=" + fileSize);
        int index = fileAbsolutePath.lastIndexOf(".");
        if (index != -1) { // 如果找不到，证明是其他类型
            String suf = fileAbsolutePath.substring(index + 1);
            if (isImage(suf)) {
                mImageCount++;
            } else if (isVideo(suf)) {
                mVideoCount++;
            } else if (isAudio(suf)) {
                mAudioCount++;
            }
        }
    }

    private boolean isImage(String suf) {
        for (String imageSuf : mImageSufArgs) {
            if (imageSuf.equalsIgnoreCase(suf)) {
                return true;
            }
        }
        return false;
    }

    private boolean isVideo(String suf) {
        for (String videoSuf : mVideoSufArgs) {
            if (videoSuf.equalsIgnoreCase(suf)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAudio(String suf) {
        for (String audioSuf : mAudioSufArgs) {
            if (audioSuf.equalsIgnoreCase(suf)) {
                return true;
            }
        }
        return false;
    }
}

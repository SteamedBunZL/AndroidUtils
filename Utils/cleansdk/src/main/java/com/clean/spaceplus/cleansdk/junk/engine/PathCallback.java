package com.clean.spaceplus.cleansdk.junk.engine;

/**
 * @author dongdong.huang
 * @Description:文件路径回调
 * @date 2016/4/28 19:50
 * @copyright TCL-MIG
 */
public interface PathCallback {
    /**
     * 文件名回调
     * @param filePath 文件名全路径
     * @param size 该文件大小
     * @param atime time of last access (Some file system not support this.)
     * @param mtime time of last modification
     * @param ctime time of last status change
     */
    public void onFile(String filePath, long size, int atime, int mtime, int ctime);

    public void onFeedback(String filePath, String fileName, long size);
    public void onError(String strPath, boolean bRmDir, boolean bRoot, int nErrorCode);

    public boolean OnFilter(String filePath, long fileModifyTime);

    /**
     * 回调接口
     */
    public void onStart( String strRootDir );
    public void onFile( String strRootDir, String strSubFile );
    public void onFolder( String strRootDir, String strSubFolder );
    public void onDone( String strRootDir );
}

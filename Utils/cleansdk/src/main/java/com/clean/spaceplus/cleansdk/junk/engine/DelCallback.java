package com.clean.spaceplus.cleansdk.junk.engine;

import java.util.List;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/7 13:54
 * @copyright TCL-MIG
 */
public interface DelCallback {
    public final static int DISABLE_WRITE_LOG = 0x00000001;	// 不要记录删除日志
    public final static int ENABLE_AFTER_DELETE = 0x00000002;	//删除之後回报

    public final static int DELETE_ONLY_FILE = 0x00000001;  //启用notifyFolderDel()标志
    public final static int DELETE_ONLY_EMPTY_FOLDER = 0x00000002;//启用notifyFileDel()标志

    public int getEnableFlags();

    /*
     * #define DELETE_ALL               0  // 2^0, bit 0  清理文件和文件夹
     * #define DELETE_ONLY_FILE         1  // 2^1, bit 1  只清理文件
     * #define DELETE_ONLY_EMPTY_FOLDER 2  // 2^2, bit 2  只清理空文件夹
     *
     */
    public int getDelFlags();

    /*
     * TODO:for future time limit interface
     */
    public int getDelFileTimeLimit();

    public List<String> getFileWhiteList();
    public List<String> getFolderWhiteList();

    //type callback from size is temp solution for print log
    public void onDeleteFile(String strFileName, long type);

    public void onFeedbackFile(String strFilePath, String strFileName, long size);

    public void afterDel(int folderCount, int fileCount, int imageCount, int videoCount, int audioCount);

    public void onError(String strPath, boolean bRmDir, boolean bRoot, int nErrorCode);

    public boolean onFilter(String filePath, long fileModifyTime);

    List<String> getFeedbackFolderList();
    List<String> getFeedbackFileList();
}

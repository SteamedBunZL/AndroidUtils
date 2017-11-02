package com.clean.spaceplus.cleansdk.junk.engine;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/7 14:12
 * @copyright TCL-MIG
 */
 public interface DeleteFileNotify {
     int ENABLE_NOTIFY_FOLDER_DEL 						= 0x00000001;//启用notifyFolderDel()标志
     int ENABLE_NOTIFY_FILE_DEL 								= 0x00000002;//启用notifyFileDel()标志
     int ENABLE_NOTIFY_DELETED_FILE_SIZE 				= 0x00000004;//启用notifyDeletedFileSize()标志
     int ENABLE_BEFORE_FILE_DEL 								= 0x00000008;//启用beforeFileDel()标志
     int ENABLE_AFTER_FOLDER_DEL							= 0x00000010;//启用afterFolderDel()标志
     int ENABLE_AFTER_FILE_DEL 								= 0x00000020;//启用afterFileDel()标志
     int DISABLE_WRITE_LOG								= 0x00000040;	// 不要记录删除日志
     int ENABLE_BEFORE_FOLDER_DEL 						= 0x00000080;//启用beforeFileDel()标志
     int ENABLE_MEDIASTORE_DEL    						= 0x00000100;//启用MediaStore Del
     int DISABLE_ALL = 0x00000000;//禁用所有
     int ENABLE_ALL = 0xFFFFFFFF; //启用所有
     int FILE_TYPE_UNKNOWN = 0;
     int FILE_TYPE_FILE = 1;
     int FILE_TYPE_FOLDER = 2;
     void notifyFolderDel(String strFolderName);
     void notifyFileDel(String strFileName);
     void notifyDeletedFileSize(long size);
    /**
     * 文件删除前做一些判断和检查，根据返回值可以控制是否继续做删除操作
     * @param strFileName
     * @return 为true为需要继续做删除操作，为false为不需要继续做删除操作
     */
     boolean beforeFileDel(String strFileName);
     boolean beforeFolderDel(String strFolderName);

     void afterFolderDel(String strFolderName);
     void afterFileDel(String strFileName);
    /**
     * 获取启用功能的标志。所有功能必须要设置启用标志
     * @return 返回 ENABLE_*组合，或者DISABLE_ALL
     */
     int getEnableFlags();
}

package com.clean.spaceplus.cleansdk.junk.engine;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/3 14:08
 * @copyright TCL-MIG
 */
public interface PathScanCallback {
    /**
     * 未知类型
     */
    static public int TYPE_UNKNOWN  = 0x00000000;
    /**
     * log文件类型
     */
    static public int TYPE_LOG 		= 0x00000001;
    /**
     * tmp文件类型
     */
    static public int TYPE_TMP 		= 0x00000002;
    /**
     * apk文件类型
     */
    static public int TYPE_APK 		= 0x00000004;
    /**
     * 大于10M的文件类型
     */
    static public int TYPE_BIG_10M 	= 0x00000008;
    /**
     * 其他类型
     */
    static public int TYPE_OTHER 	= 0x00000010;
    /**
     * 文件信息回调
     * @param filePath 文件路径
     * @param size 文件大小
     * @param nType 文件类型 TYPE_*
     * @param createTime 创建时间 单位：秒
     * @param modifyTime 最后修改时间 单位：秒
     * @param accessTime 最后访问时间 单位：秒
     * @param mode 保护模式。文件的权限信息，如是否可读可写可执行等
     */
    public void onFile(String filePath, long size, int nType, long createTime, long modifyTime, long accessTime, long mode );
}

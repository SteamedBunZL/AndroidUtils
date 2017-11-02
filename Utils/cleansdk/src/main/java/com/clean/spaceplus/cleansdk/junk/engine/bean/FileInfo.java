package com.clean.spaceplus.cleansdk.junk.engine.bean;

/**
 * @author zeming_liu
 * @Description:应用缓存明细文件信息
 * @date 2016/7/21 14:54
 * @copyright TCL-MIG
 */
public class FileInfo implements Comparable{
    //文件名
    public String fileName;
    //文件路径
    public String filePath;
    //文件大小
    public long fileSize;
    //是否目录文件
    public boolean IsDir;
    //文件数
    public int Count;
    //修改日期
    public long ModifiedDate;
    //是否选中
    public boolean Selected;

    @Override
    public int compareTo(Object another) {
        FileInfo info=(FileInfo)another;
        if(this.IsDir && !info.IsDir){
            return -1;
        }
        else if(!this.IsDir && info.IsDir){
            return 1;
        }
        return 0;
    }
}

package com.clean.spaceplus.cleansdk.boost.engine.data;

import com.clean.spaceplus.cleansdk.boost.util.MemoryInfoHelper;
import com.hawkclean.framework.log.NLog;

/**
 * @author zengtao.kuang
 * @Description:
 * @date 2016/5/30 13:15
 * @copyright TCL-MIG
 */
public class MemoryInfo {
    public long totalSize; // 单位byte
    public long usedSize;
    public long freeSize;
    /**
     * 内存已用量
     * */
    private int percentage;
    public long totalCleaned;

    //	public long originalSize;
    public int originalPercentage;
    public int killedProcessCount;

    private MemoryInfo(){

    }

    public void update() {
        update(MemoryInfoHelper.getAvailableMemoryByte());
    }

    public void update(long manualAvailMem) {
        totalSize = MemoryInfoHelper.getTotalMemoryByte();
        freeSize = manualAvailMem;
        usedSize = totalSize - freeSize;
        if (totalSize == 0) {
            percentage = 0;
        }else {
            //percentage = ProcessInfoHelper.getUsedMemoryPercentage();
            percentage =  (int)((float) usedSize / (float)totalSize * 100);
            //3.8.1 上可能会出现这个bug
            if (percentage < 0) {
                percentage = - percentage;
            }
            if(usedSize < 0){
                usedSize = -usedSize;
            }

            if(freeSize < 0){
                freeSize = -freeSize;
            }
        }
        originalPercentage = percentage;

        //如果内存update以后，内存信息出现异常，则写入log
        if(percentage <= 0 || percentage >= 100 ||
                totalSize <= 0 ||
                freeSize <= 0 ||
                usedSize <= 0){
            writeMemoryErrorLog();
        }
    }

    public static MemoryInfo newInstance(){
        MemoryInfo r = new MemoryInfo();
        r.update();
        return r;
    }


    public static MemoryInfo newInstance(long availMem){
        MemoryInfo r = new MemoryInfo();
        r.update(availMem);
        return r;
    }

    /**
     * 返回当前内存, 数值(0 - 100)
     * */
    public int getPercent() {
        return percentage;
    }

    /**
     * 如果获取到的信息出现问题，则把用户当前的log信息写入log文件
     */
    private void writeMemoryErrorLog(){

    }

    /**
     * 返回 0-1
     * */
    public float getCleanedPercent(){
        if (totalSize == 0) {
            return 0;
        }
        float result =  (float) ((float)totalCleaned / (totalSize));
        return result;
    }

    public void cleaned(long size){
        usedSize -= size;
        freeSize += size;
        percentage =  (int)((float) usedSize / (float)totalSize * 100);

        //当内存信息出现异常的时候，写入日志
        if(percentage <= 0 || usedSize <= 0 || freeSize <= 0){
            NLog.d("MemoryCleaned", "usedSize:"+usedSize+";totalSize:"+totalSize+";percentage:"+percentage+";clean size:"+size);
        }
        if (percentage < 0) {
            percentage = - percentage;
        }

        if(usedSize < 0){
            usedSize = -usedSize;
        }

        if(freeSize < 0){
            freeSize = -freeSize;
        }
    }

    public void resetCleanedMemory(){
        totalCleaned = 0;
        killedProcessCount = 0;
    }

    /**
     * 记录清理的Memory大小，单位byte , 增量
     * @param mem
     */
    public void addCleanedMem(long mem) {
        totalCleaned += mem;
    }
    /**
     * 记录清理的进程数
     */
    public void addCleanedCount(){
        killedProcessCount++;
    }

    @Override
    public String toString() {
        return "MemoryInfo [totalSize=" + totalSize + ", usedSize=" + usedSize + ", freeSize=" + freeSize
                + ", percentage=" + percentage + "]";
    }
}

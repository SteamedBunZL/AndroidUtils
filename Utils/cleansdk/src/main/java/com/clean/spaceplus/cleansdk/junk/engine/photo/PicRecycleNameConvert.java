package com.clean.spaceplus.cleansdk.junk.engine.photo;

import android.text.TextUtils;

import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.io.File;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/16 16:46
 * @copyright TCL-MIG
 */
public class PicRecycleNameConvert {
    public static final int CONVERT_NORMAL_TO_RECYCLE = 1;
    public static final int CONVERT_RECYCLE_TO_RECOVERY = 2;
    public static final int CONVERT_RECOVERY_MEDIA_TITLE = 3;
    private static final String FILE_DAT_SUFFIX = ".dat";

    /**
     * @param file 待转换文件
     * @param mode 转换类型
     * @return 目标文件路径，包含文件类型(后缀)
     */
    public String convert(File file, int mode, long currentTimeMillis) {
        String filename = file.getName();
        String newFileName = filename;
        int pIndex;
        int cIndex;

        switch (mode) {
            case CONVERT_NORMAL_TO_RECYCLE:
                pIndex = filename.lastIndexOf(".");
                if (pIndex != -1) {
                    String ext = filename.substring(pIndex + 1);
                    newFileName = String.format("%s_%s_%s%s", file.lastModified(), currentTimeMillis, ext, FILE_DAT_SUFFIX);
                }
                break;

            case CONVERT_RECYCLE_TO_RECOVERY:
                pIndex = filename.lastIndexOf(".");
                if (pIndex != -1) {
                    cIndex = filename.lastIndexOf("_");
                    if (cIndex != -1) {
                        String orginName = filename.substring(0, cIndex);
                       /* String ext = filename.substring(cIndex + 1, pIndex);
                        newFileName = String.format("%s.%s", orginName, ext);*/
                        int fIndex = orginName.lastIndexOf("_"); //需要重置文件最后修改的时间
                        if(fIndex != -1){
                            String lastMoved = orginName.substring(fIndex + 1);
                            String ext = filename.substring(cIndex + 1, pIndex);
                            newFileName = String.format("%s_%s.%s", currentTimeMillis,lastMoved, ext);
                        }
                    }
                }
                break;

            case CONVERT_RECOVERY_MEDIA_TITLE:
                pIndex = filename.lastIndexOf(".");
                if (pIndex != -1) {
                    newFileName = filename.substring(0, pIndex);
                }
                break;
        }

        return newFileName;
    }

    public TimeDes parse(String path) {
        if (TextUtils.isEmpty(path) && PublishVersionManager.isTest()) {
            throw new NullPointerException("path can't be null");
        }
        File file = new File(path);
        return parse(file);
    }

    /**
     * @return recycle time by convert mode {@link #CONVERT_NORMAL_TO_RECYCLE }
     * <p> fg: 13600000000_13900000000_png.dat -> 13600000000 & 13900000000</p>
     */
    public TimeDes parse(File file) {
        TimeDes timedes = null;
        String filename = file.getName();

        int cIndex = filename.lastIndexOf("_");
        if (cIndex != -1) {
            String temp = filename.substring(0, cIndex);
            cIndex = temp.lastIndexOf("_");
            if (cIndex != -1) {
                String lastModifiedStr = temp.substring(0, cIndex);
                String lastMovedStr = temp.substring(cIndex + 1);
                try {
                    long lastModified = Long.parseLong(lastModifiedStr);
                    long lastMoved = Long.parseLong(lastMovedStr);
                    timedes = new TimeDes(lastModified, lastMoved);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return timedes;
    }

    /**
     * @return recycle time by convert mode {@link #CONVERT_NORMAL_TO_RECYCLE }
     * <p> fg: 13600000000_13900000000.png -> 13600000000 & 13900000000</p>
     */
    public TimeDes parseWithoutDatSuffix(File file) {
        TimeDes timedes = null;
        String filename = file.getName();

        int cIndex = filename.lastIndexOf(".");
        if (cIndex != -1) {
            String temp = filename.substring(0, cIndex);
            cIndex = temp.lastIndexOf("_");
            if (cIndex != -1) {
                String lastModifiedStr = temp.substring(0, cIndex);
                String lastMovedStr = temp.substring(cIndex + 1);
                try {
                    long lastModified = Long.parseLong(lastModifiedStr);
                    long lastMoved = Long.parseLong(lastMovedStr);
                    timedes = new TimeDes(lastModified, lastMoved);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return timedes;
    }

    public class TimeDes {

        long lastModified;
        long lastMoved;

        TimeDes(long lastModified, long lastMoved) {
            this.lastModified = lastModified;
            this.lastMoved = lastMoved;
        }

        public long getLastModified(){
            return lastModified;
        }
    }
}

package com.clean.spaceplus.cleansdk.junk.engine;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.CRC32;

import space.network.commondata.KFalseData;
import space.network.util.compress.EnDeCodeUtils;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/6 15:28
 * @copyright TCL-MIG
 */
public class FalseSignFile {
    /* 文件格式
     * --------------------------------------------------------------
     * 头部
     * int8_t  format_version;//数据格式版本号
     * int8_t  reserve1;      //保留字段1
     * int16_t reserve2;      //保留字段2
     * int32_t save_time;     //数据保存的时间,秒数(时间戳除1000)
     * int32_t body_data_size;//有效数据长度,偏移从version开始
     * int32_t body_data_crc; //数据crc ,偏移从version开始,数据大小为data_length
     * --------------------------------------------------------------
     * 数据体
     * int32_t version;       //数据版本
     * int32_t cache_lifetime;//缓存有效期(天数,暂时不用)
     * int32_t id_count;      //id个数,后面的数据为id列表
     * int32_t signid1;       //误报id,id列表是排好序的,加载后不用重新排序以提高加载速度
     * ......
     * int32_t signidn;
     */
    private static final int FORMAT_VERSION = 1;
    private static final int MAX_SAVE_DATA_LENGTH =  1024 * 64;

    private static final int FILE_DATA_HEADER_SIZE = 4 * 4;//(format_version ~ body_data_crc)
    private static final int FILE_DATA_BODY_HEADER_SIZE = 3 * 4;//(version ~ id_count)

    public static class SaveData {
        int mSaveTime;
        KFalseData.SignIdData mFalseData;
    }

    public static boolean save(String filePath, SaveData saveData) {
        if (TextUtils.isEmpty(filePath))
            return false;

        if (null == saveData || null == saveData.mFalseData) {
            return false;
        }

        byte[] data = encode(saveData);
        return saveBuffer(filePath, data);
    }

    private static byte[] encode(SaveData saveData) {
        if (null == saveData || null == saveData.mFalseData) {
            return null;
        }

        int time = saveData.mSaveTime;
        KFalseData.SignIdData falseData = saveData.mFalseData;
        int idCount    = falseData.mFalseIds != null ? falseData.mFalseIds.length : 0;
        int idBuffSize = idCount * 4;
        int dataBodySize = FILE_DATA_BODY_HEADER_SIZE + idBuffSize;
        int dataSize = FILE_DATA_HEADER_SIZE + dataBodySize;
        byte[] data = new byte[dataSize];

        int pos = 0;
        data[0] = FORMAT_VERSION;

        pos += 4;
        EnDeCodeUtils.copyIntToBytes(time, data, pos);
        pos += 4;

        EnDeCodeUtils.copyIntToBytes(dataBodySize, data, pos);
        pos += 4;

        /////////////////////////////
        // skip crc value
        pos += 4;
        /////////////////////////////

        EnDeCodeUtils.copyIntToBytes(falseData.mVersion, data, pos);
        pos += 4;

        EnDeCodeUtils.copyIntToBytes(falseData.mCacheLifeTime, data, pos);
        pos += 4;

        EnDeCodeUtils.copyIntToBytes(idCount, data, pos);
        pos += 4;

        for (int i = 0; i < idCount; ++i) {
            EnDeCodeUtils.copyIntToBytes(falseData.mFalseIds[i], data, pos);
            pos += 4;
        }

        CRC32 crc32 = new CRC32();
        crc32.update(
                data,
                FILE_DATA_HEADER_SIZE,
                dataBodySize);

        int crc = (int)crc32.getValue();

        pos = FILE_DATA_HEADER_SIZE - 4;
        EnDeCodeUtils.copyIntToBytes(crc, data, pos);
        return data;
    }

    private static SaveData decode(byte[] data, boolean isNeedIdData) {
        if (null == data || data.length == 0)
            return null;

        if (data.length < FILE_DATA_HEADER_SIZE)
            return null;

        SaveData result;

        int time;
        int dataBodySize;
        int saveCrc;

        int pos = 0;
        byte formatVersion = data[0];

        //先不考虑什么兼容处理,如果格式不一致就不解析
        if (formatVersion != FORMAT_VERSION) {
            return null;
        }

        pos += 4;
        time = EnDeCodeUtils.bytesToInt(data, pos);
        pos += 4;

        dataBodySize = EnDeCodeUtils.bytesToInt(data, pos);
        pos += 4;

        if (dataBodySize <= 0
                || dataBodySize > data.length - FILE_DATA_HEADER_SIZE
                || dataBodySize < FILE_DATA_BODY_HEADER_SIZE) {
            return null;
        }

        saveCrc = EnDeCodeUtils.bytesToInt(data, pos);
        pos +=4;

        CRC32 crc32 = new CRC32();
        crc32.update(
                data,
                FILE_DATA_HEADER_SIZE,
                dataBodySize);
        int crc = (int)crc32.getValue();

        if (crc != saveCrc) {
            return  null;
        }

        result = new SaveData();
        KFalseData.SignIdData falseData = new KFalseData.SignIdData();
        result.mSaveTime = time;
        result.mFalseData = falseData;

        falseData.mVersion = EnDeCodeUtils.bytesToInt(data, pos);
        pos += 4;

        falseData.mCacheLifeTime = EnDeCodeUtils.bytesToInt(data, pos);
        pos += 4;

        int idCount = EnDeCodeUtils.bytesToInt(data, pos);
        pos +=4;

        if (idCount > ((dataBodySize - FILE_DATA_BODY_HEADER_SIZE) / 4)) {
            return null;
        }

        if (isNeedIdData && idCount > 0) {
            falseData.mFalseIds = new int[idCount];
            for (int i = 0; i < idCount; ++i) {
                falseData.mFalseIds[i] = EnDeCodeUtils.bytesToInt(data, pos);
                pos += 4;
            }
        }
        return  result;
    }

    private static void prepareParentDir(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            try {
                if (parent.mkdirs()) {
//                    FileUtils.setPermissions(parent.getPath(),
//                            FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IXOTH,
//                            -1, -1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean saveBuffer(String filePath, byte[] buffer) {
        if (TextUtils.isEmpty(filePath) || null == buffer)
            return false;

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        } else {
            prepareParentDir(file);
        }

        boolean result = false;
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(buffer);
            result = true;
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static SaveData load(String filePath) {
        return load(filePath, true);
    }

    public static SaveData load(String filePath, boolean isNeedIdData) {
        File file = new File(filePath);
        if (!file.exists())
            return null;

        SaveData result = null;
        FileInputStream is = null;
        try {
            long fileSize = file.length();
            is = new FileInputStream(file);
            if (fileSize >= (FILE_DATA_HEADER_SIZE + FILE_DATA_BODY_HEADER_SIZE)
                    && fileSize < MAX_SAVE_DATA_LENGTH) {
                byte[] data = new byte[(int)fileSize];
                is.read(data);
                result = decode(data, isNeedIdData);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}

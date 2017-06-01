package com.tcl.zhanglong.utils.Utils;

import android.annotation.TargetApi;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;

/**
 * 文件工具类
 * Created by zhanglong on 16/8/30.
 */
public class FileUtil {

    private static final int BYTE_BUFFER_SIZE = 256;

    /**
     * 通用关闭流工具方法，优雅的关闭流
     * @param closeable
     */
    public static void cloaseQuietly(Closeable closeable){
        if (closeable!=null){
            try {
                closeable.close();
            }catch (RuntimeException rethrown){
                throw rethrown;
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从文件中一行行读取直到末尾 到内存中去
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String readFromFile(String fileName) throws IOException{
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"),8*1024);
        String str;
        while((str=reader.readLine())!=null){
            buffer.append(str);
        }
        cloaseQuietly(reader);
        return buffer.toString();
    }

    /**
     * 把String内容写入到文件中
     * @param file
     * @param content
     * @param append
     * @throws IOException
     */
    public static void writeToFile(File file,String content,boolean append) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(file,append),8*1024);
        writer.write(content);
        writer.flush();
        cloaseQuietly(writer);
    }


    /**
     * 生成指定目录的文件夹，如果不存在就创建
     * @param filePath
     * @return true 成功  false 失败
     */
    public static boolean createFileFolder(String filePath){
        boolean isFileExists = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File file = new File(filePath);
            if (isFileExists = !file.exists())
                isFileExists = file.mkdirs();
        }
        return isFileExists;
    }

    /**
     * 生成指定目录中的指定文件
     * @param fileFolder
     * @param subFile
     * @return
     * @throws IOException
     */
    public static boolean createFile(String fileFolder,String subFile) throws IOException{
        boolean isFileExits = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File folder = new File(fileFolder);
            if (!folder.exists())
                folder.mkdirs();
            File file = new File(folder+subFile);
            if (isFileExits = !file.exists())
                isFileExits = file.createNewFile();
        }
        return isFileExits;
    }

    /**
     * 把文件读到byte数组中
     * @param file
     * @return
     */
    public static byte[] readFile(String file) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream(BYTE_BUFFER_SIZE);
        byte[] buffer = new byte[BYTE_BUFFER_SIZE];
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            int len = -1;
            while ((len = is.read(buffer)) > 0) {
                byteBuffer.write(buffer, 0, len);
            }
            is.close();
        } catch (Exception e) {
            return null;
        }
        return byteBuffer.toByteArray();
    }

    /***
     * 把file读到String中,设置终止字符
     * @param file
     * @param endBit
     * @return
     */
    public static String readFile(String file, char endBit) {
        byte[] b = readFile(file);
        if (b == null){
            return null;
        }
        for (int i = 0; i < b.length; i++) {
            if (endBit == b[i]) {
                return new String(b, 0, i);
            }
        }
        return new String(b);
    }

    /**
     * 获取file 总空间大小 方法来自picasso
     * @param dir
     * @return
     */
    @TargetApi(JELLY_BEAN_MR2)
    public static long calculateDiskCacheSize(File dir) {
        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            //noinspection deprecation
            long blockCount =
                    SDK_INT < JELLY_BEAN_MR2 ? (long) statFs.getBlockCount() : statFs.getBlockCountLong();
            //noinspection deprecation
            long blockSize =
                    SDK_INT < JELLY_BEAN_MR2 ? (long) statFs.getBlockSize() : statFs.getBlockSizeLong();
            long available = blockCount * blockSize;
            // Target 2% of the total space.
            return available;
        } catch (IllegalArgumentException ignored) {
        }

        return 0L;
    }




}

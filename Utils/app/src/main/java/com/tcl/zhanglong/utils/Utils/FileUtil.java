package com.tcl.zhanglong.utils.Utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 文件工具类
 * Created by zhanglong on 16/8/30.
 */
public class FileUtil {

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




}

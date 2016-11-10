package tlog.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * description:
 * author hui.zhu
 * date 2016/10/24
 * copyright TCL-MIG
 */
public class FileUtils {


    public FileUtils() {
    }

    public static boolean createEmptyFile(String path, long size) throws IOException {
        File file = new File(path);
        File parent = file.getParentFile();
        parent.mkdirs();
        RandomAccessFile raf = null;
        raf = new RandomAccessFile(file, "rw");
        raf.setLength(size);
        raf.close();
        return true;
    }

    public static boolean isExist(String path) {
        if(TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException("path is empty!");
        } else {
            File file = new File(path);
            return file.exists();
        }
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if(!file.exists()) {
            return true;
        } else {
            if(file.isDirectory()) {
                String[] subPaths = file.list();

                for(int i = 0; i < subPaths.length; ++i) {
                    if(!deleteFile(path)) {
                        return false;
                    }
                }
            }

            return file.delete();
        }
    }

    public static boolean mkdir(String path) {
        File file = new File(path);
        if(file.exists() && file.isDirectory()) {
            return true;
        } else {
            file.mkdirs();
            return true;
        }
    }

    public static boolean create(File file) throws IOException {
        if(file.exists()) {
            return true;
        } else {
            File parent = file.getParentFile();
            parent.mkdirs();
            return file.createNewFile();
        }
    }



    public static boolean copyFile(File src, File dest) {
        if(!src.exists()) {
            return false;
        } else {
            FileInputStream fis = null;
            FileOutputStream fos = null;

            boolean bytesread;
            try {
                fis = new FileInputStream(src);
                fos = new FileOutputStream(dest);
                byte[] e = new byte[2048];
                bytesread = false;

                int bytesread1;
                while((bytesread1 = fis.read(e)) != -1) {
                    if(bytesread1 > 0) {
                        fos.write(e, 0, bytesread1);
                    }
                }

                boolean e2 = true;
                return e2;
            } catch (FileNotFoundException var23) {
                bytesread = false;
                return bytesread;
            } catch (IOException var24) {
                bytesread = false;
            } finally {
                if(fis != null) {
                    try {
                        fis.close();
                    } catch (Exception var22) {
                        ;
                    }
                }

                if(fos != null) {
                    try {
                        fos.close();
                    } catch (Exception var21) {
                        ;
                    }
                }

            }

            return bytesread;
        }
    }


    public static byte[] readContent(File file) throws IOException {
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException("file too big...");
        }

        FileInputStream fi = null;
        try {
            fi = new FileInputStream(file);
            return IOUtils.readLeftBytes(fi);
        } finally {
            Streams.safeClose(fi);
        }
    }

    public static String readString(File file) throws IOException {
        byte[] bytes = readContent(file);
        if (bytes == null || bytes.length == 0)
            return null;
        return new String(bytes, "utf-8");
    }
}

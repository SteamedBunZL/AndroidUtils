package com.clean.spaceplus.cleansdk.base.utils;

import android.content.Context;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.junk.cleancloud.config.ServiceConfigManager;
import com.clean.spaceplus.cleansdk.junk.engine.DBColumn;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/5 15:57
 * @copyright TCL-MIG
 */
public class FilterUtil {
    private final static String PHP_NAME = "filter_list.php";
    private final static String PHP_PATH = "filter_list/";
    private final static String LOCAL_FILE_NAME = "filter_list.json";
    private final static String LOCAL_TEMP_FILE_NAME = "filter_list_temp.json";
    private final static String JSON_FILE_FLAG = "\"cleanmaster\"";
    private final static int TIME_OUT = 10 * 1000;

//    /**
//     * 将临时文件命名为正式文件
//     */
//    private synchronized static void renameFilterListFile() {
//        Context appContext = SpaceApplication.getInstance().getContext();
//        File tempFile = new File(appContext.getFilesDir(), LOCAL_TEMP_FILE_NAME);
//        if(tempFile.exists()){
//            File newFile = new File(appContext.getFilesDir(), LOCAL_FILE_NAME);
//            tempFile.renameTo(newFile);
//        }
//    }

    public static ArrayList<DBColumn> readDBColumnFilterList() {
        ArrayList<DBColumn> columnList = new ArrayList<DBColumn>(5);
        String json = readFilterList();
        if (json == null || json.length() == 0) {
            return columnList;
        }
        try {
            JSONObject rootObject = new JSONObject(json);
            JSONArray dbColumnArray = rootObject.optJSONArray("db_column_filter");
            if (dbColumnArray == null || dbColumnArray.length() == 0) {
                return columnList;
            }

            for (int i = 0; i < dbColumnArray.length(); i++) {
                JSONObject dbColumnObj = dbColumnArray.optJSONObject(i);
                if (dbColumnObj == null) {
                    continue;
                }
                String tableName = dbColumnObj.optString("table_name");
                String columnIndex = dbColumnObj.optString("column_index");
                DBColumn dbColumn = new DBColumn(tableName, columnIndex);
                columnList.add(dbColumn);
            }
        } catch (Exception e) {
            rmFilterList();
        }
        return columnList;
    }

    public synchronized static void rmFilterList() {
        Context appContext = SpaceApplication.getInstance().getContext();
        ServiceConfigManager.getInstanse(appContext).removeFilterListVersion();
        File localFile = new File(appContext.getFilesDir(), LOCAL_FILE_NAME);
        if (localFile.exists()) {
            localFile.delete();
        }
    }

    /**
     * 从本地读取FilterList
     */
    public synchronized static String readFilterList() {
        Context appContext = SpaceApplication.getInstance().getContext();
        FileInputStream fis = null;
        ByteArrayOutputStream contents = null;
        try {
            byte[] buffer = new byte[1024];
            File localFile = new File(appContext.getFilesDir(), LOCAL_FILE_NAME);
            if (!localFile.exists()) {
                return null;
            }

            fis = new FileInputStream(localFile);
            contents = new ByteArrayOutputStream();
            int n = -1;
            while ((n = fis.read(buffer)) != -1) {
                contents.write(buffer, 0, n);
            }
            String json = contents.toString();
            if (json == null || !json.contains(JSON_FILE_FLAG)) {
                rmFilterList();
                return null;
            }
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                closeStream(fis);
            }
            if (contents != null) {
                closeStream(contents);
            }
        }
        return null;
    }

//    /**
//     * 存储FilterList
//     * @throws IOException
//     */
//    private synchronized static void saveFilterListToTempFile(BufferedInputStream bis, String newVersion) throws IOException {
//        String json = getStringFromZip(bis);
//
//        if (json == null || !json.contains(JSON_FILE_FLAG)) {
//            return;
//        }
//        Context appContext = SpaceApplication.getInstance().getContext();
//        BufferedWriter out = null;
//        try {
//            File tempFile = new File(appContext.getFilesDir(), LOCAL_TEMP_FILE_NAME);
//            OutputStream os = new FileOutputStream(tempFile);
//            out = new BufferedWriter(new OutputStreamWriter(os));
//            out.write(json);
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally {
//            if (out != null) {
//                closeStream(out);
//            }
//        }
//    }

//    private static String getStringFromZip(BufferedInputStream bis) {
//        ZipInputStream zis = new ZipInputStream(bis);
//        try {
//            if (zis.getNextEntry() != null) {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                byte[] buffer = new byte[1024];
//                int count;
//                while ((count = zis.read(buffer)) != -1) {
//                    baos.write(buffer, 0, count);
//                }
//                return baos.toString();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            closeStream(zis);
//        }
//        return null;
//    }

    private static void closeStream(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

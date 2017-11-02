package com.clean.spaceplus.cleansdk.junk.engine;

import android.os.Environment;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author chaohao.zhou
 * @Description: 误删记录
 * @date 2016/7/25 18:51
 * @copyright TCL-MIG
 */
public class FileDeletedRecorder {

    private final static String TAG = FileDeletedRecorder.class.getSimpleName();

    public static String FOLDER_PATH_FILE_DEL;

    public final static String FILE_NAME_FILE_DELETED = "fileDel.txt";

    static {
        //可挂载的时候才初始化该路径
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                FOLDER_PATH_FILE_DEL = SpaceApplication.getInstance().getContext().getExternalFilesDir(null) + File.separator + "fileDel";
            } catch (Exception ignore) {
            }
        }
    }

    public void record(String delRecord) {
        if (FOLDER_PATH_FILE_DEL == null || TextUtils.isEmpty(delRecord)) {
            return;
        }
        File fileRecord = new File(FOLDER_PATH_FILE_DEL, FILE_NAME_FILE_DELETED);
        if (!fileRecord.getParentFile().exists()) {
            fileRecord.getParentFile().mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileRecord, true);
            fos.write(delRecord.getBytes("utf-8"));
        } catch (Exception e) {
            NLog.e(TAG, "-->:: eee" + e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }
}

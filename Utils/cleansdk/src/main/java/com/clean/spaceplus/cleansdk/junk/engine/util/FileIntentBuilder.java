package com.clean.spaceplus.cleansdk.junk.engine.util;

import android.content.Intent;
import android.net.StaticIpConfiguration;
import android.net.Uri;

import com.clean.spaceplus.cleansdk.junk.engine.bean.FileInfo;

import java.io.File;

import libcore.net.MimeUtils;

/**
 * @author zeming_liu
 * @Description:查看文件的Intent
 * @date 2016/7/21 14:54
 * @copyright TCL-MIG
 */
public class FileIntentBuilder {
    public static final String ALL_MIMETYPE="*/*";

    /**
     * 获取对应文件类型的Intent
     * @param filePath
     * @param mimeType
     * @return
     */
    public static Intent buildSendFile(String filePath,String mimeType) {
        File fileIn = new File(filePath);
        Uri u = Uri.fromFile(fileIn);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //设置intent的data和Type属性。
        intent.setDataAndType(u, mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, u);

        return intent;
    }

    /**
     * 获取文件的MimeTyoe
     * @param filePath
     * @return
     */
    public static String getMimeType(String filePath) {
        int dotPosition = filePath.lastIndexOf('.');
        if (dotPosition == -1)
            return "*/*";

        String ext = filePath.substring(dotPosition + 1, filePath.length()).toLowerCase();
        String mimeType = MimeUtils.guessMimeTypeFromExtension(ext);
        if (ext.equals("mtz")) {
            mimeType = "application/miui-mtz";
        }
        return mimeType != null ? mimeType : "*/*";
    }
}

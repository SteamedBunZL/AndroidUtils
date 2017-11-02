package com.clean.spaceplus.cleansdk.util;

import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/28 20:12
 * @copyright TCL-MIG
 */
public class OpenFileHelper {
    // android获取一个用于打开HTML文件的intent
//	public static Intent getHtmlFileIntent(File file) {
//		if(null==file || !file.exists()){
//			return null;
//		}
//
//		Uri uri = Uri.parse(file.toString()).buildUpon()
//				.encodedAuthority("com.android.htmlfileprovider")
//				.scheme("content").encodedPath(file.toString()).build();
//		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.setDataAndType(uri, "text/html");
//		return intent;
//	}

    // android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(File file) {
        if(null==file || !file.exists()){
            return null;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(File file) {
        if(null==file || !file.exists()){
            return null;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    // android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(File file) {
        if(null==file || !file.exists()){
            return null;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

    // android获取一个用于打开音频文件的intent
    public static Intent getAudioFileIntent(File file) {
        if(null==file || !file.exists()){
            return null;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // android获取一个用于打开视频文件的intent
    public static Intent getVideoFileIntent(File file) {
        if(null==file || !file.exists()){
            return null;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(File file) {
        if(null==file || !file.exists()){
            return null;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // android获取一个用于打开Word文件的intent
//	public static Intent getWordFileIntent(File file) {
//		if(null==file || !file.exists()){
//			return null;
//		}
//
//		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.addCategory(Intent.CATEGORY_DEFAULT);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		Uri uri = Uri.fromFile(file);
//		intent.setDataAndType(uri, "application/msword");
//		return intent;
//	}

    // android获取一个用于打开Excel文件的intent
//	public static Intent getExcelFileIntent(File file) {
//		if(null==file || !file.exists()){
//			return null;
//		}
//
//		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.addCategory(Intent.CATEGORY_DEFAULT);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		Uri uri = Uri.fromFile(file);
//		intent.setDataAndType(uri, "application/vnd.ms-excel");
//		return intent;
//	}

    // android获取一个用于打开PPT文件的intent
//	public static Intent getPPTFileIntent(File file) {
//		if(null==file || !file.exists()){
//			return null;
//		}
//
//		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.addCategory(Intent.CATEGORY_DEFAULT);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		Uri uri = Uri.fromFile(file);
//		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
//		return intent;
//	}

    // android获取一个用于打开apk文件的intent
//	public static Intent getApkFileIntent(File file) {
//		if(null==file || !file.exists()){
//			return null;
//		}
//
//		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setAction(android.content.Intent.ACTION_VIEW);
//		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//		return intent;
//	}

//	public static Intent getFileBrowserIntent(File file){
//		if(null==file || !file.exists()){
//			return null;
//		}
//
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.addCategory(Intent.CATEGORY_DEFAULT);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setDataAndType(Uri.fromFile(file), "file/*");
//		return intent;
//	}
}

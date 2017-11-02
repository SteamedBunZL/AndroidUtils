//package com.clean.spaceplus.cleansdk.util;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//
//import com.clean.spaceplus.cleansdk.BuildConfig;
//import com.clean.spaceplus.cleansdk.base.scan.TaskControllerImpl;
//import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFile;
//import com.clean.spaceplus.cleansdk.junk.engine.photo.PhotoCompressEngineImpl;
//import NLog;
//import PublishVersionManager;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.RandomAccessFile;
//import java.nio.charset.Charset;
//import java.text.DecimalFormatSymbols;
//
//import space.photocompress.exif.ExifInterface;
//import space.photocompress.exif.ExifTag;
//
///**
// * @author zengtao.kuang
// * @Description:Image压缩工具
// * @date 2016/5/10 15:01
// * @copyright TCL-MIG
// */
//public class ImageCompressUtil {
//
//    public static final String TAG = ImageCompressUtil.class.getSimpleName();
//    public static final int COMPRESS_PERCENTAGE = 50;
//    private static final String US_ASCII = "ASCII\0\0\0";
//    private static final Charset US_ASCII_CHARSET = Charset.forName("US-ASCII");
//    public static final String USER_COMMENT_TAG = "NID:SIZE:";
//
//    public static String formatSizeForJunkHeader(long size) {
//        String suffix = null;
//        float fSize = 0;
//        if (size >= 1000) {
//            suffix = "K";
//            fSize = (float) (size / 1024.0);
//            if (fSize >= 1000) {
//                suffix = "M";
//                fSize /= 1024;
//            }
//            if (fSize >= 1000) {
//                suffix = "G";
//                fSize /= 1024;
//            }
//        } else {
//            fSize = (float) (size / 1024.0);
//            suffix = "K";
//        }
//
//        String formatString = null;
//        if(fSize > 100){
//            formatString = "#0";
//        }else if(fSize > 10){
//            formatString = "#0.0";
//        }else{
//            formatString = "#0.00";
//        }
//
//        java.text.DecimalFormat df = new java.text.DecimalFormat(formatString);
//        DecimalFormatSymbols symbols=df.getDecimalFormatSymbols();
//        symbols.setDecimalSeparator('.');
//        df.setDecimalFormatSymbols(symbols);
//        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
//        resultBuffer.append(suffix);
//        return resultBuffer.toString().replaceAll("-", ".");
//    }
//
//    /**
//     * 判断文件是不是以DDF8开头
//     * 判断宽高都大于0
//     * @param file
//     * @return
//     */
//    public static boolean isDamageJPGPicture(File file){
//        if(file == null){
//            return true;
//        }
//        RandomAccessFile raf = null;
//        try{
//            raf = new RandomAccessFile(file, "r");
//            byte b1 = raf.readByte();
//            byte b2 = raf.readByte();
//            if(b1 == -1 && b2 == -40){
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(file.getAbsolutePath(), options);
//                if(options.outWidth > 0 && options.outHeight > 0){
//                    return false;
//                }
//            }
//
//            //压缩生成的压缩图是坏的
//            NLog.d(TAG, "photoCompressService#photo compress damage, path : " + file.getAbsolutePath());
//        }catch (FileNotFoundException e){
//            e.printStackTrace();
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            if(raf != null){
//                try{
//                    raf.close();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return true;
//    }
//
//    public static ByteArrayOutputStream getCompressStream(String path,long maxSize){
//
//        ByteArrayOutputStream baos = null;
//        File file = new File(path);
//        if(!file.exists()){
//            return baos;
//        }
//        long size = file.length();
//        Bitmap image = null;
//        FileInputStream inputStream = null;
//
//        try {
//
//            baos = new ByteArrayOutputStream();
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(path, options);
//
//            int srcWidth = options.outWidth;
//            int srcHeight =  options.outHeight;
//
//            if(srcWidth * srcHeight * 4 > maxSize){
//                //照片太大
//                if(PublishVersionManager.isTest()){
//                    throw new RuntimeException("photoCompressService getCompressStream path:"+path+" size:"+ srcWidth * srcHeight * 4 +" maxSize" + maxSize);
//                }
//                return null;
//            }
//
//            options.inJustDecodeBounds = false;
//            options.inSampleSize = 1;
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            inputStream = new FileInputStream(file);
//            image = BitmapFactory.decodeFileDescriptor(inputStream.getFD(), null, options);
//            if (image == null) {
//                return null;
//            }
//
//            image.compress(Bitmap.CompressFormat.JPEG, COMPRESS_PERCENTAGE, baos);
//
//        }catch (Error error){
//            error.printStackTrace();
//            System.gc();
//            if(BuildConfig.DEBUG){
//                OutOfMemoryError outOfMemoryError = new OutOfMemoryError( "photoCompressService getCompressStream path:"+path+" size:"+ size +" maxSize" + maxSize);
//                outOfMemoryError.initCause(error);
//                throw outOfMemoryError;
//            }
//
//            NLog.d(TAG, "photoCompressService getCompressStream OOM, path:"+path+" size:"+ size +" maxSize" + maxSize);
//        }catch(Exception e){
//            e.printStackTrace();
//        }finally{
//
//            try{
//                if(inputStream != null){
//                    inputStream.close();
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//            if(image != null && !image.isRecycled()){
//                image.recycle();
//            }
//        }
//
//        return baos;
//    }
//
//    /**
//     * 获取图片的压缩比率
//     * @param path 压缩的图片
//     * @param destPath 压缩后的图片
//     * @return
//     */
//    public static int getCompressPercentage(String path,String destPath,long maxSize,String mimeType) {
//
//        int percentage = -1;
//        ByteArrayOutputStream baos  = getCompressStream(path,maxSize);
//        if(baos == null) {
//            return -1;
//        }
//
//        long fileSize = new File(path).length();
//        //不将压缩的图片写入文件，直接以流的大小计算压缩比
//        if(destPath == null){
//            percentage = (int)(100*baos.size()/fileSize);
//            if(percentage > 100){
//                percentage = 100;
//            }
//            return percentage;
//        }
//
//        //写入压缩文件
//        File tempFile = new File(destPath);
//        if(tempFile.exists()){
//            tempFile.delete();
//        }
//
//        try{
//
//            ExifInterface exifInterface = new ExifInterface();
//            if("image/jpeg".equals(mimeType)) {
//                exifInterface.readExif(path, false);
//            }
//            ExifTag exifTag = exifInterface.buildTag(ExifInterface.TAG_USER_COMMENT
//                    ,US_ASCII + USER_COMMENT_TAG + formatSizeForJunkHeader(fileSize));
//            exifInterface.setTag(exifTag);
//            exifInterface.writeExif(baos.toByteArray(), tempFile.getAbsolutePath());
//
//            baos.flush();
//            try{
//                baos.close();
//                baos = null;
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//            //计算压缩比
//            long tempSize = new File(tempFile.getAbsolutePath()).length();
//            percentage = (int)(100*tempSize/fileSize);
//            if(percentage > 100){
//                percentage = 100;
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            if(tempFile.exists()){
//                tempFile.delete();
//            }
//            percentage = -1;
//        }finally {
//
//            try{
//                if(baos != null){
//                    baos.close();
//                    baos = null;
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
//        return percentage;
//    }
//
//    /**
//     * 获取用户注释
//     * 和360兼容
//     * @param path
//     * @return
//     */
//	public static String getUserComment(String path) {
//        String s1 = null;
//		try {
//            ExifInterface exifInterface = new ExifInterface();
//            exifInterface.readExif(path,true);
//
//            Object mValue = exifInterface.getTagValue(ExifInterface.TAG_USER_COMMENT);
//            if (mValue == null) {
//                s1 =  null;
//            } else if (mValue instanceof String) {
//                s1 = (String) mValue;
//            } else if (mValue instanceof byte[]) {
//                s1 =  new String((byte[]) mValue, US_ASCII_CHARSET);
//            }
//            if(s1 != null && s1.startsWith(US_ASCII + USER_COMMENT_TAG)) {
//                s1 = s1.substring(US_ASCII.length() + USER_COMMENT_TAG.length(), s1.length());
//            }else{
//                s1 = null;
//            }
//		} catch (Exception exception) {
//            exception.printStackTrace();
//            s1 = null;
//		}
//		return s1;
//	}
//
//    /**
//     * 压缩图片
//     * @param mediaFile
//     * @param taskCtrl
//     * @return
//     */
//    public static long compressImage(MediaFile mediaFile, TaskControllerImpl taskCtrl, long maxSize, PhotoCompressEngineImpl.CompressFailCallback compressFailCallback) {
//        long size = 0l;
//        String path = mediaFile.getPath();
//        ByteArrayOutputStream baos = null;
//        File tempFile = null;
//        try {
//
//            baos = getCompressStream(path,maxSize);
//            if(baos == null){
//                return 0;
//            }
//            //检查是否停止
//            if(taskCtrl != null && taskCtrl.checkStop()){
//                return 0;
//            }
//
//            tempFile = new File(path+".tmp");
//            if(tempFile.exists()){
//                tempFile.delete();
//            }
//
//            File srcFile = new File(path);
//            long fileSize = srcFile.length();
//
//            boolean isJPG = "image/jpeg".equals(mediaFile.getMimeType());
//            ExifInterface exifInterface = new ExifInterface();
//            if(isJPG) {
//                exifInterface.readExif(path, false);
//            }
//            ExifTag exifTag = exifInterface.buildTag(ExifInterface.TAG_USER_COMMENT
//                    ,US_ASCII + USER_COMMENT_TAG + formatSizeForJunkHeader(fileSize));
//            exifInterface.setTag(exifTag);
//            exifInterface.writeExif(baos.toByteArray(), tempFile.getAbsolutePath());
//
//            long lastModified = System.currentTimeMillis() / 1000;
//
//            //第二次检查是否停止
//            if(taskCtrl != null && taskCtrl.checkStop()){
//                return 0;
//            }
//            baos.flush();
//            try{
//                baos.close();
//                baos = null;
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//            if(tempFile.exists()){
//                if(!isDamageJPGPicture(tempFile)){
//
//                    //先保存源文件
//                    File srcTempFile = new File(srcFile.getAbsolutePath() + "_" + System.currentTimeMillis());
//                    if(srcFile.renameTo(srcTempFile)){
//
//                        //临时文件rename到源文件
//                        if(tempFile.renameTo(srcFile)){
//                            size = fileSize - srcFile.length();
//                            if (size <= 0) {
//                                size = 0;
//                            }
//                            mediaFile.setMimeType("image/jpeg");
//                            // 改变最后一次修改时间
//                            mediaFile.setLastModified(lastModified);
//                            NLog.d("photo_compress", "photoCompressService#compress photo path : " + path + " success");
//
//                            //临时文件rename成功，删除保存的源文件
//                            srcTempFile.delete();
//                        }else{
//                            if(compressFailCallback != null){
//                                compressFailCallback.compressFailDamagePicture(srcFile,tempFile,1);
//                            }
//                            NLog.d("photo_compress", "photoCompressService#compress photo path : " + path + " renameTo fail");
//                            tempFile.delete();
//
//                            //临时文件rename失败，还原源文件
//                            srcTempFile.renameTo(srcFile);
//                        }
//                    }else{
//                        //源文件rename失败，那么本次压缩失败
//                        NLog.d("photo_compress", "photoCompressService#compress src photo path : " + path + " fail");
//                    }
//
//                }else{
//                    NLog.d("photo_compress", "photoCompressService#compress photo path : " + path + " compress fail");
//                    if(compressFailCallback != null){
//                        compressFailCallback.compressFailDamagePicture(srcFile,tempFile,2);
//                    }
//                    tempFile.delete();
//                }
//
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            if(tempFile != null && tempFile.exists()){
//                NLog.d("photo_compress", "photoCompressService#delete compress tmp photo path : " + tempFile.getAbsolutePath());
//                tempFile.delete();
//            }
//
//        }finally {
//
//            try{
//                if(baos != null){
//                    baos.close();
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
//
//        return size;
//    }
//}

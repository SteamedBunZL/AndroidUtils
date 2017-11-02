package com.hawkclean.mig.commonframework.network;

import android.text.TextUtils;
import android.util.Base64;

import com.hawkclean.framework.log.NLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author dongdong.huang
 * @Description:
 * @date 2017/3/3 15:16
 * @copyright TCL-MIG
 */

public class DataCryptor {
    private static final String TAG = "DataCryptor";

    /*******
     * AES key
     *********/
    private static String CRYPTOR_KEY = "zfOGUiRDpWHv6B7c";//"cqgf971sp394@!#0";
    //    private String IV  =  "1234567812345678";
    private static String ALGORITHM = "AES/CBC/NoPadding";
    private static String CHAR_SET = "utf-8";


//    /**
//     * 加密数据
//     * @param plainText
//     * @return
//     * 网络传输数据时需设置成Base64.URL_SAFE
//     */
//    public String encodeStringAndCompress(String plainText){
//        if(!TextUtils.isEmpty(plainText)){
//            try {
//                return Base64.encodeToString(encrypt(compressZip(plainText), CRYPTOR_KEY), Base64.URL_SAFE);
//            } catch (Exception e) {
//                if (DebugUtils.isDebug()){
//                    NLog.e(TAG, e.toString());
//                }
//            }
//        }
//
//        return "";
//    }

//    public String encodeString(String plainText){
//        if(!TextUtils.isEmpty(plainText)){
//            try {
//                return Base64.encodeToString(encrypt(plainText.getBytes(CHAR_SET), CRYPTOR_KEY), Base64.DEFAULT);
//            } catch (Exception e) {
//                if (DebugUtils.isDebug()){
//                    NLog.e(TAG, e.toString());
//                }
//            }
//        }
//
//        return "";
//    }

    /**
     * 解密数据
     *
     * @param encodedText
     * @return
     */
    public static String decodeStringAndDecompress(String encodedText) {
        if (!TextUtils.isEmpty(encodedText)) {
            try {
                return new String(decrypt(decompressZip(Base64.decode(encodedText, Base64.DEFAULT)), CRYPTOR_KEY));
            } catch (Exception e) {
                NLog.e(TAG, e.toString());
            }
        }

        return "";
    }

//    public String decodeString(String encodedText){
//        if(!TextUtils.isEmpty(encodedText)){
//            try {
//                return new String(decrypt(Base64.decode(encodedText, Base64.DEFAULT), CRYPTOR_KEY));
//            } catch (Exception e) {
//                if (DebugUtils.isDebug()){
//                    NLog.e(TAG, e.toString());
//                }
//            }
//        }
//
//        return "";
//    }


//    private byte[] encrypt(byte[] dataBytes , String key) throws Exception {
//        if (dataBytes == null || dataBytes.length == 0){
//            return null;
//        }
//
//        try {
//            Cipher cipher = Cipher.getInstance(ALGORITHM);
//            int blockSize = cipher.getBlockSize();
//
//            int plaintextLength = dataBytes.length;
//            if (plaintextLength % blockSize != 0) {
//                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
//            }
//
//            byte[] plaintext = new byte[plaintextLength];
//            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
//
//            byte[] keybytes = key.getBytes(CHAR_SET);
//            SecretKeySpec keyspec = new SecretKeySpec(keybytes, "AES");
//            IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes(CHAR_SET));
//
//            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
//            byte[] encrypted = cipher.doFinal(plaintext);
//            return encrypted;
//
//        } catch (Exception e) {
//            if (DebugUtils.isDebug()){
//                NLog.e(TAG, e.toString());
//            }
//            return null;
//        }
//    }

    private static byte[] decrypt(byte[] encrypted, String key) throws Exception {
        if (encrypted == null || encrypted.length == 0) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            byte[] keybytes = key.getBytes(CHAR_SET);
            SecretKeySpec keyspec = new SecretKeySpec(keybytes, "AES");
//            IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes(CHAR_SET));
            IvParameterSpec ivspec = new IvParameterSpec(keybytes);

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted);
            return original;
        } catch (Exception e) {
            NLog.e(TAG, e.toString());
            return null;
        }
    }

    /**
     * gzip 解压缩
     *
     * @param zipBytes
     * @return
     */
    private static byte[] decompressZip(byte[] zipBytes) {
        BufferedOutputStream bufferedOutputStream = null;
        byte[] plainBytes = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bufferedOutputStream = new BufferedOutputStream(bos);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new GZIPInputStream(new
                    ByteArrayInputStream(zipBytes)));
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = bufferedInputStream.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, len);
                bufferedOutputStream.flush();
            }

            bufferedOutputStream.flush();
            plainBytes = bos.toByteArray();
            bos.close();
        } catch (Exception e) {
            NLog.e(TAG, e.toString());
        } finally {
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e) {

                }
            }
        }

        return plainBytes;
    }

//    /**
//     * gzip压缩
//     * @param plainText
//     * @return
//     */
//    private static byte[] compressZip(String plainText){
//        BufferedWriter bufferedWriter = null;
//
//        try {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(bos), CHAR_SET));
//            bufferedWriter.write(plainText);
//            bufferedWriter.flush();
//            bufferedWriter.close();
//            return bos.toByteArray();
//        } catch (Exception e) {
//            if (DebugUtils.isDebug()){
//                NLog.e(TAG, e.toString());
//            }
//        } finally {
//            if(bufferedWriter != null){
//                try {
//                    bufferedWriter.close();
//                } catch (IOException e) {
//
//                }
//            }
//        }
//
//        return null;
//    }
}

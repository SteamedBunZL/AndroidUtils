package com.tcl.security.cloudengine;


import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Utils {
    private static final String TAG = ProjectEnv.bDebug ? "Utils" : Utils.class.getSimpleName();
    public static final String HASH_MD5 = "MD5";
    public static final String HASH_SHA1 = "SHA1";

    /**
     * 解压缩字节
     * @param data
     * @return
     */
    public static byte[] inflate0(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];

        int error = 0;
        while (!inflater.finished()) {
            int count = 0;
            try {
                count = inflater.inflate(buf);
            } catch (DataFormatException e) {
                Log.e(TAG, "", e);
                error = 1;
                break;
            }
            baos.write(buf, 0, count);
        }
        inflater.end();
        if (error > 0) {
            return null;
        }

        return baos.toByteArray();
    }

    public static byte[] deflate0(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buf);
            baos.write(buf, 0, count);
        }
        deflater.end();
        return baos.toByteArray();
    }

    /**
     * 没啥用
     * @param data
     * @return
     */
    private static boolean checkXdeInput(byte[] data) {
        return true;
    }

    /**
     * 通过64解码，解压字节，解密string
     * @param s
     * @return
     */
    public static String xde(String s) {
        byte[] data = null;
        try {
            data = s.getBytes("utf-8");//转byte[]
        } catch (Exception e) {
            return null;
        }
        if (!checkXdeInput(data)) {
            return null;
        }
        byte[] xdata = Base64.decode(data, Base64.DEFAULT);//base64解码
        int len = xdata.length;
        data = new byte[len];
        for (int i = 0; i < len; i++) {
            byte b = (byte)(xdata[i] ^ 0xFF);//按位异或 0^0=0，1^1=0 ，1^0 = 1，0^1=1
            data[i] = b;
        }
        data = inflate0(data);//解压缩字节码，生成string
        try {
            return new String(data, "utf-8");
        } catch (Exception e) {
            return null;
        }
    }




    private static MessageDigest getHashHelper0(InputStream in, String algorithm, boolean isClose) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "hash " + algorithm + " init error:");
                e.printStackTrace();
            }
            return null;
        }

        int len = 0;
        byte[] buffer = new byte[4096];
        BufferedInputStream bin = new BufferedInputStream(in);
        try {
            while (true) {
                len = bin.read(buffer);
                if (len < 0) {
                    break;
                }
                digest.update(buffer, 0, len);
            }
            return digest;
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "hash " + algorithm + " error:");
                e.printStackTrace();
            }
            return null;
        } finally {
            if (isClose) {
                try {
                    bin.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static String getHashHelper(InputStream in, String algorithm, boolean isClose) {
        MessageDigest digest = getHashHelper0(in, algorithm, isClose);
        if (digest != null) {
            return bytesToHex(digest.digest());
        }
        return null;
    }

    public static String getHashHelper(String s, String algorithm) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "hash " + algorithm + " init error:");
                e.printStackTrace();
            }
            return null;
        }

        try {
            return bytesToHex(digest.digest(s.getBytes("utf-8")));
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "hash " + algorithm + " error:");
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getHashHelper(byte[] bytes, String algorithm) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "hash " + algorithm + " init error:");
                e.printStackTrace();
            }
            return null;
        }

        try {
            return bytesToHex(digest.digest(bytes));
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "hash " + algorithm + " error:");
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}

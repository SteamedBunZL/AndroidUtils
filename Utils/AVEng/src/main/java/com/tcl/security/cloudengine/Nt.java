package com.tcl.security.cloudengine;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Nt {
    private static final String TAG = ProjectEnv.bDebug ? "Nt" : Nt.class.getSimpleName();
    private static String apkPath = null;

    private Nt() {}
    public static Nt getInstance() {
        return new Nt();
    }

    private static native String nt5(byte[] src, int len, int[] ret);
    public static String encode(String s) {
        int[] ret = new int[]{0};
        byte[] bytes = null;
        try {
            bytes = s.getBytes("utf-8");
        } catch (Exception e) {
            return null;
        }

        byte[] data = bytes;
        int pad = 0;
        int len = bytes.length;
        int r = len % 4;
        if (r != 0) {
            pad = 4 - r;
            data = new byte[len + pad];
            Arrays.fill(data, (byte)0);
            System.arraycopy(bytes, 0, data, 0, len);
        }

        return nt5(data, data.length, ret);
    }

    private static native String nt6(byte[] src, int len, int[] ret);
    public static String decode(String s) {
        int[] ret = new int[]{0};
        byte[] bytes = null;
        try {
            bytes = s.getBytes("utf-8");
        } catch (Exception e) {
            return null;
        }

        int len = bytes.length;
        return nt6(bytes, len, ret);
    }

    public static String get() {
        if (apkPath == null) {
            apkPath = App.c.getPackageCodePath();
        }
        return apkPath;
    }

    private static String getHelper(InputStream in, String tag) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(tag)) {
                    return reader.readLine();
                }
            }
        } catch (IOException e) {
        }
        return null;
    }

    public static String get(String path, String name, String tag) {
        ZipInputStream zin = null;
        File file = new File(path);
        try {
            zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                if (!ze.isDirectory() && (ze.getName().endsWith(name))) {
                    return getHelper(zin, tag);
                }
            }
        } catch (Exception e) {
        } finally {
            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}

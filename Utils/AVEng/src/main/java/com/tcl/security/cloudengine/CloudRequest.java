package com.tcl.security.cloudengine;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import com.steve.commonlib.DebugLog;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;


public class CloudRequest {
    private static final String TAG = ProjectEnv.bDebug ? "CloudRequest" : CloudRequest.class.getSimpleName();

    private static final String MTEXT_SF = ".SF";
    private static final String MTEXT_METAINF = "META-INF/";
    private static String x509 =  "h2N0L8zKT/v//FL+2g==";
    static String apkListTag = "h2O00zcJNtPR/v/01f0m";
    static String basicTag = "h2O0tdMxs/n/+f79/A==";
    static String extraTag = "h2O0UtfWtfv/+YD92g==";

    /**
     * 请求类初始化，也是解码一些key
     */
    static void init() {
        toTags();
    }

    /**
     apkListTag = apkList,
     basicTag = basic,
     extraTag = extra,
     x509 = X.509,
     */
    private static void toTags() {
        apkListTag = Utils.xde(apkListTag);
        basicTag = Utils.xde(basicTag);
        extraTag = Utils.xde(extraTag);
        x509 = Utils.xde(x509);
        DebugLog.w("------------CloudRequest 解码-------------");
        DebugLog.d("apkListTag = %s,\nbasicTag = %s,\nextraTag = %s,\nx509 = %s,\n",
                apkListTag,basicTag,extraTag,x509);
    }

    private static String getHashWithoutCheck(File file) {
        CustomUnzip zip = null;
        ArrayList<String> crcList = new ArrayList<String>();
        ArrayList<String> md5StreamList = new ArrayList<String>();
        ArrayList<String> md5NameList = new ArrayList<String>();

        try {
            zip = new CustomUnzip(file.getAbsolutePath());
            Enumeration<CustomUnzip.UnzipEntry> e = zip.entries();
            while (e.hasMoreElements()) {
                CustomUnzip.UnzipEntry entry = e.nextElement();
                if (entry.fileName.endsWith(MTEXT_SF) && entry.fileName.startsWith(MTEXT_METAINF) && !entry.isDirectory()) {
                    String md5 = Utils.getHashHelper(entry.fileNameBytes, Utils.HASH_MD5);
                    if (md5 == null) {
                        return null;
                    } else {
                        md5NameList.add(md5.toUpperCase());
                    }

                    long crc32 = entry.crc32;
                    if (crc32 > 0) {
                        crcList.add(String.format("%08X", crc32));
                        String m = Utils.getHashHelper(entry.getInputStream(), Utils.HASH_MD5, true);
                        if (m == null) {
                            return null;
                        } else {
                            md5StreamList.add(m.toUpperCase());
                        }
                    }
                } else if (entry.fileName.startsWith(MTEXT_METAINF)) {
                    String md5 = Utils.getHashHelper(entry.fileNameBytes, Utils.HASH_MD5);
                    if (md5 == null) {
                        return null;
                    } else {
                        md5NameList.add(md5.toUpperCase());
                    }

                    long crc32 = entry.crc32;
                    if (crc32 > 0) {
                        crcList.add(String.format("%08X", crc32));
                    }
                } else if (entry.isDirectory()) {
                    String md5 = Utils.getHashHelper(entry.fileNameBytes, Utils.HASH_MD5);
                    if (md5 == null) {
                        return null;
                    } else {
                        md5NameList.add(md5.toUpperCase());
                    }
                }
            }

            StringBuilder builder = new StringBuilder(512);
            String[] md5StreamArray = new String[md5StreamList.size()];
            md5StreamList.toArray(md5StreamArray);
            Arrays.sort(md5StreamArray);
            for (String s : md5StreamArray) {
                builder.append(s);
            }

            String[] crcArray = new String[crcList.size()];
            crcList.toArray(crcArray);
            Arrays.sort(crcArray);
            for (String s : crcArray) {
                builder.append(s);
            }

            String[] md5NameArray = new String[md5NameList.size()];
            md5NameList.toArray(md5NameArray);
            Arrays.sort(md5NameArray);
            for (String s : md5NameArray) {
                builder.append(s);
            }

            byte[] commentBytes = zip.commentBytes;
            byte[] bytes;
            byte[] data = builder.toString().getBytes("utf-8");
            if (commentBytes != null) {
                bytes = new byte[commentBytes.length + data.length];
                System.arraycopy(data, 0, bytes, 0, data.length);
                System.arraycopy(commentBytes, 0, bytes, data.length, commentBytes.length);
            } else {
                bytes = data;
            }
            return Utils.getHashHelper(bytes, Utils.HASH_SHA1);
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "custom unzip error:\n");
                e.printStackTrace();
            }
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (Exception e) {
                }
            }
        }

        return null;
    }

    public static class MetaInfo {
        public String key;
        public String pkgName;
        public int versionCode;
        public String versionName;
        public String sigName;
        public String sigHash;
        public String apkHash;
        public int apkSize;

        static String keyTag = "h2M0sVL7//14/rU=";
        static String pkgNameTag = "h2PUN7EItDOy+v/0yf07";
        static String versionCodeTag = "h2PUtNLVMTMwjDGwtvr/5DH7fQ==";
        static String versionNameTag = "h2PUtNLVMTMwDLQzsvr/5B37dw==";
        static String sigNameTag = "h2PUMbMItDOy+v/0wP06";
        static String sigHashTag = "h2PUMbMIt9Mx///0yf03";
        static String apkHashTag = "h2O00zcJt9Mx///1Cf0+";
        static String apkSizeTag = "h2O00zfxMVO1+v/0uv0n";

        static void init() {
            toTags();
        }

        /**
         keyTag = key,
         pkgNameTag = pkgName,
         versionCodeTag = versionCode,
         versionNameTag = versionName,
         sigNameTag = sigName,
         sighHashTag = sigHash,
         apkHashTag = apkHash,
         apkSizeTag = apkSize,
         */
        private static void toTags() {
            keyTag = Utils.xde(keyTag);
            pkgNameTag = Utils.xde(pkgNameTag);
            versionCodeTag = Utils.xde(versionCodeTag);
            versionNameTag = Utils.xde(versionNameTag);
            sigNameTag = Utils.xde(sigNameTag);
            sigHashTag = Utils.xde(sigHashTag);
            apkHashTag = Utils.xde(apkHashTag);
            apkSizeTag = Utils.xde(apkSizeTag);
            DebugLog.w("------------CloudRequest.MetaInfo 解码-------------");
            DebugLog.d("keyTag = %s,\npkgNameTag = %s,\nversionCodeTag = %s,\nversionNameTag = %s,\nsigNameTag = %s,\nsighHashTag = %s,\napkHashTag = %s,\napkSizeTag = %s,\n",
                    keyTag,pkgNameTag,versionCodeTag,versionNameTag,sigNameTag,sigHashTag,apkHashTag,apkSizeTag);
        }

        public MetaInfo() {

        }

        public MetaInfo(String key, String pkgName, int versionCode, String versionName, String sigName, String sigHash, String apkHash, int apkSize) {
            this.key = key;
            this.pkgName = pkgName;
            this.versionCode = versionCode;
            this.versionName = versionName;
            this.sigName = sigName;
            this.sigHash = sigHash;
            this.apkHash = apkHash;
            this.apkSize = apkSize;
        }

        @Override
        public String toString() {
            if (ProjectEnv.bDebug) {
                StringBuilder builder = new StringBuilder(128);
                builder.append("key:");
                builder.append(key);
                builder.append("|");
                builder.append("package name:");
                builder.append(pkgName);
                builder.append("|");
                builder.append("version code:");
                builder.append(versionCode);
                builder.append("|");
                builder.append("version name:");
                builder.append(versionName);
                builder.append("|");
                builder.append("sig name:");
                builder.append(sigName);
                builder.append("|");
                builder.append("sig hash:");
                builder.append(sigHash);
                builder.append("|");
                builder.append("apk hash:");
                builder.append(apkHash);
                builder.append("|");
                builder.append("apk size:");
                builder.append(apkSize);
                return builder.toString();
            } else {
                return super.toString();
            }
        }
    }

    private static String get509Text() {
        return x509;
    }

    private static String getCertSubject(byte[] sigs) {
        String text = get509Text();
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance(text);
            ByteArrayInputStream bais = new ByteArrayInputStream(sigs);
            X509Certificate cert = (X509Certificate)certFactory.generateCertificate(bais);
            return cert.getSubjectDN().toString();
        } catch (CertificateException e) {
            if (ProjectEnv.bDebug) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 由pkgName获取上传请求的MetaInfo
     * @param c
     * @param pkgName
     * @return
     */
    public static MetaInfo getMetaInfoForPkg(Context c, String pkgName) {
        try {
            PackageInfo pkg = c.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            String path = pkg.applicationInfo.publicSourceDir;
            File f = new File(path);
            // continue if apk hash not get for installed package.
            String apkHash = getHashWithoutCheck(f);
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance(Utils.HASH_MD5);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            byte[] bytes = pkg.signatures[0].toByteArray();
            digest.update(bytes);
            String sigHash = Utils.bytesToHex(digest.digest());
            String sigName = getCertSubject(bytes);
            int apkSize = (int)f.length();
            return new MetaInfo(pkgName, pkg.packageName, pkg.versionCode, pkg.versionName, sigName, sigHash, apkHash, apkSize);
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "get pkg info error:\n");
                e.printStackTrace();
            }
        }
        return null;
    }

    public static PackageManager pm =  null ;

    public static MetaInfo getMetaInfoForFile(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        // for uninstalled apk file, open always OK.
        String apkHash = getHashWithoutCheck(f);
        /*if (apkHash == null) {
            return null;
        }*/
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(Utils.HASH_MD5);
        } catch (Exception e) {
            return null;
        }

        if (pm == null){
            pm = App.c.getPackageManager() ;
        }

        if (pm == null){
            return  null;
        }

		PackageInfo pkg = null;
		try {
			pkg = pm.getPackageArchiveInfo(path, PackageManager.GET_SIGNATURES);
		} catch(Exception e) {
			return null;			
		}        
        if (pkg == null) {
            return null;
        }
        Signature[] sigs = pkg.signatures;
        if (sigs == null) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "sigs null.");
            }
            return null;
        }
        if (sigs[0] == null) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "sigs empty.");
            }
            return null;
        }
        byte[] bytes = sigs[0].toByteArray();
        digest.update(bytes);
        String sigHash = Utils.bytesToHex(digest.digest());
        String sigName = getCertSubject(bytes);
        int apkSize = (int)f.length();
        return new MetaInfo(path, pkg.packageName, pkg.versionCode, pkg.versionName, sigName, sigHash, apkHash, apkSize);
    }

}

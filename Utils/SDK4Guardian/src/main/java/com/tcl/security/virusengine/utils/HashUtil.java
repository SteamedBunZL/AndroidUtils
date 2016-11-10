package com.tcl.security.virusengine.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.tcl.security.cloudengine.Utils;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * hash工具类
 * Created by Steve on 2016/7/12.
 */
public class HashUtil {


    private static final String MTEXT_PKG =  "eJwrSCwqTg1ITM5OTE8FAB+FBMg=";
    private static final String MTEXT_CERTS=  "eJxLzs/JSU0ucU4tKslMy0xOLEktBgBM9ge9";
    private static final String MTEXT_509 =  "eJyL0DM1sAQAA60BJQ==";
    private static final String MTEXT_SF = ".SF";
    private static String plainPkg = null;
    private static String plainCerts = null;
    private static String plain509 = null;
    private static String x509 =  "h2N0L8zKT/v//FL+2g==";


    /**
     * 获签名取证书名称
     * @param c
     * @param pkgName
     * @return
     */
    public static String getSigName(Context c,String pkgName){
        try {
            PackageInfo pkg = c.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            String path = pkg.applicationInfo.sourceDir;
            // continue if apk hash not get for install package.
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            byte[] bytes = pkg.signatures[0].toByteArray();
            digest.update(bytes);
            return getCert(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取签名证书hash
     * @param c
     * @param pkgName
     * @return
     */
    public static String getSigHash(Context c,String pkgName){
        try {
            PackageInfo pkg = c.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            String path = pkg.applicationInfo.sourceDir;
            // continue if apk hash not get for install package.
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            byte[] bytes = pkg.signatures[0].toByteArray();
            digest.update(bytes);
            return digestToString(digest.digest());
        } catch (Exception e) {
                e.printStackTrace();
        }
        return null;
    }


    private static String digestToString(byte[] dig) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dig.length; i++) {
            int k = dig[i];
            if (k < 0) {
                k = 256+k;
            }
            builder.append(String.format("%02X", k));
        }
        return builder.toString();
    }

    private static String getCert(byte[] sigs) {
        String text = get509Text();
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance(text);
            ByteArrayInputStream bais = new ByteArrayInputStream(sigs);
            X509Certificate cert = (X509Certificate)certFactory.generateCertificate(bais);
            return cert.getSubjectDN().toString();
        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String get509Text() {
        return Utils.xde(x509);
    }

    public static String getRealSignName(Context context,String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return parseSignature(sign.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseSignature(byte[] signature) {
        try {

            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
//            String pubKey = cert.getPublicKey().toString();
//            String signNumber = cert.getSerialNumber().toString();
//            System.out.println("signName:" + cert.getSigAlgName());
//            System.out.println("pubKey:" + pubKey);
//            System.out.println("signNumber:" + signNumber);
//            System.out.println("subjectDN:"+cert.getSubjectDN().toString());
            return cert.getSigAlgName();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }



}

package com.tcl.security.cloudengine;


import com.steve.commonlib.DebugLog;

import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class LocalTrustManager implements X509TrustManager {
    private static final String TAG = ProjectEnv.bDebug ? "LocalTrustManager" : LocalTrustManager.class.getSimpleName();
    private X509TrustManager trustManager = null;
    private static final String[] keys0 = {"h2P6Pjj+/9/3+09qFzcMvyOA22xbcmNZCpWeiBF5H+J0rf10Fn8MEfhH1/cB"};
    private static ArrayList<String> keys = new ArrayList<String>();
    private MessageDigest digest;

    static {
        init();
    }

    static void init() {
        toKeys();
    }

    private static void toKeys() {
        for (String s : keys0) {
            try {
                String key = Utils.xde(s);
                DebugLog.w("\n-----------------------Trus");
                if (key != null) {
                    keys.add(key);
                }
            } catch (Exception e) {
                if (ProjectEnv.bDebug) {
                    e.printStackTrace();
                }
            }
        }
    }

    public LocalTrustManager(KeyStore ks) {
        try {
            if (ks != null) {
                String algorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
                tmf.init(ks);

                trustManager = findX509TrustManager(tmf);
                if (trustManager == null) {
                    throw new IllegalStateException("Couldn't find key.");
                }
            }
            digest = MessageDigest.getInstance("SHA1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (trustManager != null) {
            trustManager.checkClientTrusted(chain, authType);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (trustManager != null) {
            trustManager.checkServerTrusted(chain, authType);
        }
        if (!validateCertificatePins(chain)) {
            throw new CertificateException("invalid key.");
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        if (trustManager != null) {
            return trustManager.getAcceptedIssuers();
        } else {
            return new X509Certificate[]{};
        }
    }

    private X509TrustManager findX509TrustManager(TrustManagerFactory tmf) {
        TrustManager trustManagers[] = tmf.getTrustManagers();
        int len = trustManagers.length;
        for (int i = 0; i < len; i++) {
            TrustManager tm = trustManagers[i];
            if (tm instanceof X509TrustManager) {
                return (X509TrustManager)trustManagers[i];
            }
        }
        return null;
    }

    private boolean validateCertificatePins(X509Certificate[] chain) {
        for (X509Certificate certificate : chain) {
            if (!validateCertificatePin(certificate)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateCertificatePin(X509Certificate certificate) {
        final byte[] pubKeyInfo = certificate.getPublicKey().getEncoded();
        final byte[] pin = digest.digest(pubKeyInfo);
        final String pinAsHex = Utils.bytesToHex(pin);
        for (String validPin : keys) {
            if (validPin.equalsIgnoreCase(pinAsHex)) {
                return true;
            }
        }
        return false;
    }

}

package com.tcl.security.cloudengine;

import java.util.ArrayList;
import java.util.List;

public class ProjectEnv {
    public static final boolean bDebug = true;
    static final boolean bRttTrace = true;
    static final boolean bUseOkhttp = true;
    static final boolean bQueryOOB = true;
    static final boolean bUseGzip = true;
    static final boolean bUseAccessKey = true;
    static final int rttCountMax = 1;
    static List<String> hosts = new ArrayList<String>();
    public static void setHosts(List<String> hosts) {
        if (hosts != null) {
            for (String host : hosts) {
                ProjectEnv.hosts.add(host);
            }
        }
    }

    public enum EncType{
        HTTPS, PLAIN, CUSTOM, NONE,
    }
    static EncType encType = EncType.HTTPS;
    static EncType lastType = EncType.NONE;
    static boolean encTypeReseted = false;
    static final String LIBMZ = "mzip";

    public static void useHttp() {
        setEncType(EncType.PLAIN);
    }
    public static void useHttps() {
        setEncType(EncType.HTTPS);
    }
    public static void setEncType(EncType type) {
        encType = type;
        if (lastType != EncType.NONE) {
            if (lastType != encType) {
                encTypeReseted = true;
            } else {
                encTypeReseted = false;
            }
        }
        lastType = type;
    }

    public static EncType getEncType() {
        return encType;
    }
}
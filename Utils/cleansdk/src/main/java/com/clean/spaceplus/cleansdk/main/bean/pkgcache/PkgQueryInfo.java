package com.clean.spaceplus.cleansdk.main.bean.pkgcache;

import com.clean.spaceplus.cleansdk.main.bean.Bean;

/**
 * @author shunyou.huang
 * @Description:
 * @date 2016/4/22 14:57
 * @copyright TCL-MIG
 */

public class PkgQueryInfo extends Bean{

    public int _id;
    public int pkgid;
    public String pkg;
    public long time;
    public int src;
    public String dirs;
    public String redirs;
    public String  files;
    public String refiles;
    public int sysflag;
    public int is_integrity;
}

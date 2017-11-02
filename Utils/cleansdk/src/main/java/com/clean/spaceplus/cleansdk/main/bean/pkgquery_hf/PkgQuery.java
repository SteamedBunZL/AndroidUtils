package com.clean.spaceplus.cleansdk.main.bean.pkgquery_hf;

import com.clean.spaceplus.cleansdk.main.bean.Bean;

/**
 * @author shunyou.huang
 * @Description:
 * @date 2016/4/21 13:26
 * @copyright TCL-MIG
 */

public class PkgQuery extends Bean{
    public String pkgid;
    public String pkg;
    public String dirs;


    @Override
    public String toString() {
        return "PkgQuery{" +
                "pkgid='" + pkgid + '\'' +
                ", pkg='" + pkg + '\'' +
                ", dirs='" + dirs + '\'' +
                "} " + super.toString();
    }
}

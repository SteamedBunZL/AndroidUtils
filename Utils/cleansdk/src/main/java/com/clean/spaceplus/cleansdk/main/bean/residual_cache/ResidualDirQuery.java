package com.clean.spaceplus.cleansdk.main.bean.residual_cache;

import com.clean.spaceplus.cleansdk.main.bean.Bean;

import java.util.Arrays;

/**
 * residual_dir_cache的dirquery表
 */
public class ResidualDirQuery extends Bean{
    public int _id;
    public int dirid;
    public byte[] dir;
    public int queryresult;
    public int cleantype;
    public int contenttype;

    public int cmtype;
    public long time;
    public String dirs;
    public String pkgs;
    public String repkgs;

    public int test;
    public String subdirs;
    public int cleantime;
    public String suffixinfo;


    @Override
    public String toString() {
        return "ResidualDirQuery{" +
                "_id=" + _id +
                ", dirid=" + dirid +
                ", dir=" + Arrays.toString(dir) +
                ", queryresult=" + queryresult +
                ", cleantype=" + cleantype +
                ", contenttype=" + contenttype +
                ", cmtype=" + cmtype +
                ", time=" + time +
                ", dirs='" + dirs + '\'' +
                ", pkgs='" + pkgs + '\'' +
                ", repkgs='" + repkgs + '\'' +
                ", test=" + test +
                ", subdirs='" + subdirs + '\'' +
                ", cleantime=" + cleantime +
                ", suffixinfo='" + suffixinfo + '\'' +
                "} " + super.toString();
    }
}

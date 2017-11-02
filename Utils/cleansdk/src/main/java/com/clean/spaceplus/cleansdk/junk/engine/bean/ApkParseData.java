package com.clean.spaceplus.cleansdk.junk.engine.bean;

import java.io.File;

/**
 * @author zengtao.kuang
 * @Description:
 * @date 2016/5/10 20:50
 * @copyright TCL-MIG
 */
public class ApkParseData {
    private File mApkFile = null;
    private GenericWhiteInfo mWhiteInfo = new GenericWhiteInfo();

    public ApkParseData(final String filePath,
                        int type,
                        String strFixPath,
                        String strRegEx,
                        boolean bIsFile,
                        int checkType,
                        int displayType,
                        boolean isWhiteFile) {
        mApkFile = new File(filePath);
        mWhiteInfo.setType(type);
        mWhiteInfo.setFixPath(strFixPath);
        mWhiteInfo.setRegEx(strRegEx);
        mWhiteInfo.setIsFile(bIsFile);
        mWhiteInfo.setCheckType(checkType);
        mWhiteInfo.setDisplayType(displayType);
        mWhiteInfo.setIsWhiteFile(isWhiteFile);
    }

    public ApkParseData() {

    }

    public void setApkParseDataFile(final String filePath) {
        mApkFile = new File(filePath);
    }

    public void setApkParseDataFile(final File file) {
        mApkFile = file;
    }

    public void setApkParseDataWhiteInfo(GenericWhiteInfo info) {
        mWhiteInfo.setType(info.getType());
        mWhiteInfo.setFixPath(info.getFixPath());
        mWhiteInfo.setRegEx(info.getRegEx());
        mWhiteInfo.setIsFile(info.isFile());
        mWhiteInfo.setCheckType(info.getCheckType());
        mWhiteInfo.setDisplayType(info.getDisplayType());
        mWhiteInfo.setIsWhiteFile(info.getIsWhiteFile());
    }

    public File getApkParseDataFile() {
        return mApkFile;
    }

    public GenericWhiteInfo getApkParseDataWhiteInfo() {
        return mWhiteInfo;
    }
}

package com.clean.spaceplus.cleansdk.junk.engine.bean;

/**
 * @author zengtao.kuang
 * @Description:
 * @date 2016/5/10 20:49
 * @copyright TCL-MIG
 */
public class GenericWhiteInfo {

    private int mType;
    private String mFixPath;
    private String mRegEx;
    private boolean mIsFile;
    private int mCheckType;
    private int mDisplayType;
    private boolean mIsWhiteFile = false;

    public GenericWhiteInfo(int type,
                            String strFixPath,
                            String strRegEx,
                            boolean bIsFile,
                            int checkType,
                            int displayType,
                            boolean isWhiteFile) {
        mType = type;
        mFixPath = strFixPath;
        mRegEx = strRegEx;
        mIsFile = bIsFile;
        mCheckType = checkType;
        mDisplayType = displayType;
        mIsWhiteFile = isWhiteFile;
    }

    public GenericWhiteInfo() {

    }

    public int getType() {
        return mType;
    }

    public String getFixPath() {
        return mFixPath;
    }

    public String getRegEx() {
        return mRegEx;
    }

    public boolean isFile() {
        return mIsFile;
    }

    public int getCheckType() {
        return mCheckType;
    }

    public int getDisplayType() {
        return mDisplayType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public void setFixPath(String filePath) {
        this.mFixPath = filePath;
    }

    public void setRegEx(String reg) {
        this.mRegEx = reg;
    }

    public void setIsFile(boolean isFile) {
        this.mIsFile = isFile;
    }

    public void setCheckType(int type) {
        this.mCheckType = type;
    }

    public void setDisplayType(int displayType) {
        this.mDisplayType = displayType;
    }

    public void setIsWhiteFile(boolean isWhiteFile) {
        mIsWhiteFile = isWhiteFile;
    }

    public boolean getIsWhiteFile() {
        return mIsWhiteFile;
    }
}

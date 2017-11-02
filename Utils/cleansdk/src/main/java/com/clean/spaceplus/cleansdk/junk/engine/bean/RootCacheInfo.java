package com.clean.spaceplus.cleansdk.junk.engine.bean;

import com.clean.spaceplus.cleansdk.junk.engine.junk.JunkRequest;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/22 15:20
 * @copyright TCL-MIG
 */
public class RootCacheInfo extends BaseJunkBean{

    public RootCacheInfo() {
        super(JUNK_CACHE);
    }

    public RootCacheInfo(JunkRequest.EM_JUNK_DATA_TYPE junkType) {
        super(junkType);
    }

    private String pkgName;
    private String path;
    private int cleanType;
    private byte haveNotCleaned;
    private int scanType;
    private int resultSource;
    private int pathType;
    private int cleanOperation;
    private int mCleanFileFlag = 0;
    private int cacheId = 0;
    private String mSignId;

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCleanType() {
        return cleanType;
    }

    public void setCleanType(int cleanType) {
        this.cleanType = cleanType;
    }

    public int getPathType() {
        return pathType;
    }

    public void setPathType(int pathType) {
        this.pathType = pathType;
    }

    ///<DEAD CODE>/// 	public int getCleanOperation() {
    //		return cleanOperation;
    //	}

    public void setCleanOperation(int cleanOperation) {
        this.cleanOperation = cleanOperation;
    }

    public int getCleanFileFlag() {
        return mCleanFileFlag;
    }

    ///<DEAD CODE>/// 	public void setCleanFileFlag(int mCleanFileFlag) {
    //		this.mCleanFileFlag = mCleanFileFlag;
    //	}

    public int getCacheId() {
        return cacheId;
    }

    ///<DEAD CODE>/// 	public void setCacheId(int cacheId) {
    //		this.cacheId = cacheId;
    //	}

    public String getSignId() {
        return mSignId;
    }

    public void setSignId(String mSignId) {
        this.mSignId = mSignId;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int compareTo(BaseJunkBean another) {
        // TODO Auto-generated method stub
        return 0;
    }
    public int getScanType() {
        return scanType;
    }

    public void setScanType(int scanType) {
        this.scanType = scanType;
    }

    public int getResultSource() {
        return resultSource;
    }

    public void setResultSource(int resultSource) {
        this.resultSource = resultSource;
    }

    public byte getHaveNotCleaned() {
        return haveNotCleaned;
    }

    public void setHaveNotCleaned(byte haveNotCleaned) {
        this.haveNotCleaned = haveNotCleaned;
    }

}

package com.clean.spaceplus.cleansdk.junk.engine.bean;

import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/6 15:01
 * @copyright TCL-MIG
 */
public class JunkCleanItemInfo {
    private List<String> mPathList = new ArrayList<String>();
    private int mCleanFileFlag;
    private BaseJunkBean mItem;
    private boolean mIsSubItem = false; //is subItem not Item in the scan result
    private int mCleanTime = 0;

    public JunkCleanItemInfo(String path, BaseJunkBean item) {
        if(null == path || null == item){
            if(PublishVersionManager.isTest()){
                throw new NullPointerException();
            }
        }
        mPathList.add(path);
        mCleanFileFlag = SDcardRubbishResult.CLEAN_FILE_FLAG_DEFAULT;
        mItem = item;
    }

    public JunkCleanItemInfo(String path, int cleanFileFlag, BaseJunkBean item) {
        if(null == path || null == item){
            if(PublishVersionManager.isTest()){
                throw new NullPointerException();
            }
        }
        mPathList.add(path);
        mCleanFileFlag = cleanFileFlag;
        mItem = item;
    }

    public JunkCleanItemInfo(List<String> path, int cleanFileFlag, BaseJunkBean item) {
        if(null == path || null == item){
            if(PublishVersionManager.isTest()){
                throw new NullPointerException();
            }
        }
        mPathList = path;
        mCleanFileFlag = cleanFileFlag;
        mItem = item;
    }

    public JunkCleanItemInfo(List<String> path, int cleanFileFlag, BaseJunkBean item, int nCleanTime ) {
        if(null == path || null == item){
            if(PublishVersionManager.isTest()){
                throw new NullPointerException();
            }
        }
        mPathList = path;
        mCleanFileFlag = cleanFileFlag;
        mItem = item;
        mCleanTime = nCleanTime;
    }

    public String getPath() {
        if (!mPathList.isEmpty()) {
            return mPathList.get(0);
        }
        return null;
    }

    public List<String> getPathList() {
        return mPathList;
    }

    public int getCleanFileFlag() {
        return mCleanFileFlag;
    }

    public int getCleanTime() {
        return mCleanTime;
    }

    public BaseJunkBean getJunkItem() {
        return mItem;
    }

    public void setIsSubItem(boolean isSubItem) {
        mIsSubItem = isSubItem;
    }

    public boolean getIsSubItem() {
        return mIsSubItem;
    }
}
